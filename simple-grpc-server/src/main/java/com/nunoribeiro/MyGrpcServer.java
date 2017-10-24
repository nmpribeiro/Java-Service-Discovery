package com.nunoribeiro;

import com.nunoribeiro.grpc.Interceptor;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.ServerInterceptors;
import io.grpc.ServerServiceDefinition;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTextArea;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author admin
 */
public class MyGrpcServer {
    
    final private InputStreamReader inputStream = new InputStreamReader(System.in);;
    final private BufferedReader buffer = new BufferedReader(this.inputStream);
    
    private static final Logger LOGGER = Logger.getLogger(MyGrpcServer.class.getName());
    private Server server;
    
    private String domain;
    private int port;
    public JTextArea console;
    
    MyGrpcServer(){
        this("localhost", 0);
    }
    MyGrpcServer(String domain){
        this(domain, 0);
    }
    MyGrpcServer(String domain, int port){
        if(port == 0){
            port = this.getInt("Enter desired port for server:");
        }
        this.domain = domain;
        this.port = port;
        this.console = null;
    }
    
    public void setConsole(JTextArea console){
        this.console = console;
    }
    
    public void start() throws IOException {
        
        ServerServiceDefinition interceptor = ServerInterceptors.intercept(
                new GreetingServiceImpl(this.console),
                new Interceptor(this.console));
        
        server = ServerBuilder.forPort(port)
                /*
                // enable tls
                .useTransportSecurity(
                    new File(serverCert),
                    new File(serverKey)
                )
                */
                //.addService( new GreetingServiceImpl() )
                .addService( interceptor )
                .build();
        server.start();
        
        String msg = "Client Server started, listening on " + port;
        LOGGER.log(Level.INFO, msg);
        
        if(this.console != null) this.console.append(msg);
        //System.out.println(msg);
        
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
              // Use stderr here since the logger may have been reset by its JVM shutdown hook.
              String msg = "\n*** shutting down gRPC client server since JVM is shutting down";
              System.err.println(msg);
              console.append(msg);
              LOGGER.log(Level.INFO, msg);
              MyGrpcServer.this.stop();
              msg = "*** gRPC client server shut down";
              console.append(msg);
              System.err.println(msg);
              LOGGER.log(Level.INFO, msg);
            }
        });
    }

    private void stop() {
        if (server != null) {
            server.shutdown();
        }
    }
    
    
    /**
     * Await termination on the main thread since the grpc library uses daemon threads.
     */
    private void blockUntilShutdown() throws InterruptedException {
      if (server != null) {
        server.awaitTermination();
      }
    }
    
    
    /**
     *      UTILS
     * 
     */
    private int getInt(String prompt) {
        System.out.print(prompt);
        while(true){
            try {
                try {
                    String input = buffer.readLine();
                    if(input == null) {
                        return 0;
                    }
                    if(input.equals("")){
                        return 1234;
                    }
                    else return Integer.parseInt(input);
                } catch (IOException ex) {
                    Logger.getLogger(MyGrpcServer.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch(NumberFormatException ne) {
                System.out.print("That's not a whole number.\n"+prompt);
            }
        }
    }
    
    
    /**
     * Main launches the server from the command line.
     * @param args
     * @throws java.io.IOException
     * @throws java.lang.InterruptedException
     */
    public static void main(String[] args) throws IOException, InterruptedException {
        final MyGrpcServer server = new MyGrpcServer();
        server.start();
        server.blockUntilShutdown();
    }
    
}
