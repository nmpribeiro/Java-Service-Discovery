
package com.nunoribeiro;

import com.example.server.GreetingServiceGrpc;
import com.example.server.HelloRequest;
import com.example.server.HelloResponse;
import io.grpc.stub.StreamObserver;
import javax.swing.JTextArea;


/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


/*
public class GreetingServiceImpl extends GreetingServiceGrpc.GreetingServiceImplBase {
    @Override
    public void greeting(HelloRequest request, StreamObserver<HelloResponse> responseObserver){
        String name = request.getName();
        HelloResponse response = HelloResponse.newBuilder().setGreeting("Hello "+name).build();
        
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
*/

public class GreetingServiceImpl extends GreetingServiceGrpc.GreetingServiceImplBase {
    public JTextArea console;
    
    public GreetingServiceImpl(){
        this(null);
    }
    public GreetingServiceImpl(JTextArea console){
        this.console = console;
    }
    
    @Override
    public void greeting(HelloRequest request, StreamObserver<HelloResponse> responseObserver){
        String name = request.getName();
        HelloResponse response = HelloResponse.newBuilder().setGreeting("Hello "+name).build();
        
        
        System.out.println("\nRequest from :");
        System.out.println("Request: \n"+request);
        System.out.println("Response: "+response);
        
        if(this.console != null) this.console.append("\nRESPONSE: "+response);
        
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
