/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nunoribeiro.grpc;

import io.grpc.Context;
import io.grpc.Contexts;
import io.grpc.Grpc;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import io.grpc.Status;
import javax.swing.JTextArea;

/**
 *
 * @author admin
 */
public class Interceptor implements ServerInterceptor {
    public JTextArea console = null;
    
    public Interceptor(){
        this(null);
    }
    public Interceptor(JTextArea console){
        if(console != null) this.console = console;
    }
    
    public void setConsole(JTextArea console){
        this.console = console;
    }
    
    public static final Context.Key<Object> USER_IDENTITY
      = Context.key("identity"); // "identity" is just for debugging
    
    /*
    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> sc, Metadata mtdt, ServerCallHandler<ReqT, RespT> sch) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }*/
    
    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
        ServerCall<ReqT, RespT> call,
        Metadata headers,
        ServerCallHandler<ReqT, RespT> next) {
            // You need to implement validateIdentity
            Object identity = validateIdentity();
            if (identity == null) { // this is optional, depending on your needs
                // Assume user not authenticated
                call.close(Status.UNAUTHENTICATED.withDescription("some more info"),
                        new Metadata());
                return new ServerCall.Listener() {};
      }
      Context context = Context.current().withValue(USER_IDENTITY, identity);
      
      System.out.println("\nNEW CALL");
      System.out.println("call headers: "+headers.toString());
      System.out.println("Call Remote address: "+call.getAttributes().get(Grpc.TRANSPORT_ATTR_REMOTE_ADDR));
      
      if(this.console != null){
          this.console.append("\nInterceptor:: "+"NEW CALL camme in");
          this.console.append("\nInterceptor::"+"Call headers: "+headers.toString());
          this.console.append("\nInterceptor::"+"Call Remote address: "+call.getAttributes().get(Grpc.TRANSPORT_ATTR_REMOTE_ADDR));
      }
      
      return Contexts.interceptCall(context, call, headers, next);
    }

    private Object validateIdentity() {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        Object object;
        object = (Object) new Identity("Nuno", 32);
        return object;
    }
    
    private class Identity {
        String name;
        int age;
        Metadata headers;
        
        Identity(){
            this(null);
        }
        Identity(String name){
            this(name, 0);
        }
        Identity(String name, int age){
            this.name = name;
            this.age = age;
        }
    }
}
