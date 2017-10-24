package com.nunoribeiro;

import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.ClientInterceptor;
import io.grpc.Context;
import io.grpc.ForwardingClientCall;
import io.grpc.Grpc;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author admin
 */
public class Interceptor implements ClientInterceptor {
    
    //some examples: https://www.programcreek.com/java-api-examples/index.php?api=io.grpc.Metadata
    
    private static final Logger LOGGER = Logger.getLogger(MyGrpcClient.class.getName());
    public static final Context.Key<Object> USER_IDENTITY
      = Context.key("identity"); // "identity" is just for debugging
    
    
    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(
            final MethodDescriptor<ReqT, RespT> method,
            CallOptions callOptions,
            Channel next
    ) {
        
        
        ClientCall client = new ForwardingClientCall.SimpleForwardingClientCall<ReqT, RespT>(
                next.newCall(method, callOptions)
        ) {};
        /*
                .SimpleForwardingClientCall<ReqT, RespT>(
                next.newCall(method, callOptions)) {
            
            @Override
            public void sendMessage(ReqT message) {
                String msg =  method.getFullMethodName() + " --- request content: \n"
                        + message.toString();
                LOGGER.log(Level.INFO, msg);
                super.sendMessage(message);
            }

            @Override
            public void start(Listener<RespT> responseListener, Metadata headers) {
                
                System.out.println("\nNEW CALL");
                System.out.println("Call Remote address: "+this.getAttributes().get(Grpc.TRANSPORT_ATTR_REMOTE_ADDR));
                System.out.println("call headers: "+headers.toString());
                //System.out.println("Call Remote address: "+ call.getAttributes().get(Grpc.TRANSPORT_ATTR_REMOTE_ADDR));
                
                ClientCall.Listener<RespT> listener = new ClientCall.Listener<RespT>() {
                    @Override
                    public void onMessage(RespT message) {
                        //System.out.println("Call Remote address: "+ this.getAttributes().get(Grpc.TRANSPORT_ATTR_REMOTE_ADDR));
                        String msg =  method.getFullMethodName() + " --- response content: \n"
                                + message.toString();
                        LOGGER.log(Level.INFO, msg);
                        super.onMessage(message);
                    }
                };
                super.start(listener, headers);
            }
        };
        */
        
        System.out.println("Call Remote address2: "+ client.getAttributes().get(Grpc.TRANSPORT_ATTR_REMOTE_ADDR));
        return client;
    }
    
    /*
    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(
            ClientCall<ReqT, RespT> call,
            Metadata headers, CallOptions co, Channel chnl) {
        
        Object identity = validateIdentity();
        Context context = Context.current().withValue(USER_IDENTITY, identity);
      
        System.out.println("\nNEW CALL");
        System.out.println("call headers: "+headers.toString());
        System.out.println("Call Remote address: "+call.getAttributes().get(Grpc.TRANSPORT_ATTR_REMOTE_ADDR));
        
        
        
    }
    */
    
    private Object validateIdentity() {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        Object object;
        object = (Object) new Identity("Nuno", 32);
        return object;
    }
    /*
    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(
            MethodDescriptor<ReqT, RespT> md,
            CallOptions co,
            Channel chnl
    ) {
        Metadata headers = new Metadata();
        
        System.out.println("\nNEW CALL");
        System.out.println("call headers: "+headers.toString());
        
        //System.out.println("Call Remote address: "+ call.getAttributes().get(Grpc.TRANSPORT_ATTR_REMOTE_ADDR));
        return null;
    }
    */
    /*
    @Override
    public <ReqT,RespT>ClientCall<ReqT,RespT> interceptCall(MethodDescriptor<ReqT,RespT> method,CallOptions callOptions,Channel next){
        
        return null;
        
        return new CheckedForwardingClientCall<ReqT,RespT>(next.newCall(method,callOptions)){

            @Override
            protected void checkedStart(ClientCall.Listener<RespT> ll, Metadata mtdt) throws Exception {
                //mtdt.put("test", "ip?" );
                System.out.println("Call Remote address: "+ this.getAttributes().get(Grpc.TRANSPORT_ATTR_REMOTE_ADDR));
            }
        };
        
    }
    */
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
