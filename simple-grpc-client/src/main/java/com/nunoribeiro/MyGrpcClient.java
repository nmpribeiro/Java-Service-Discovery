
package com.nunoribeiro;

//import io.grpc.Server;
//import io.grpc.ServerBuilder;
import com.example.server.GreetingServiceGrpc;
import com.example.server.GreetingServiceGrpc.GreetingServiceBlockingStub;
import com.example.server.HelloRequest;
import com.example.server.HelloResponse;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
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
public class MyGrpcClient {
    public String port;
    public int portInt;
    public String domain;
    private InputStreamReader inputStream;
    private BufferedReader buffer;
    public JTextArea console;
    
    private static final Logger LOGGER = Logger.getLogger(MyGrpcClient.class.getName());
    
    public MyGrpcClient() {
        this(null);
    }
    public MyGrpcClient(String domain) {
        this(domain, null);
    }
    public MyGrpcClient(String domain, String port) {
        this.inputStream = new InputStreamReader(System.in);
        this.buffer = new BufferedReader(this.inputStream);
        
        if(domain == null || "".equals(domain)){
            this.domain = this.getDomain("Enter server name/IP:");
        } else {
            this.domain = domain;
        }
        
        if(port == null || "".equals(port)){
            this.portInt = this.getInt("Enter server port:");
        }
        else {
            this.portInt = Integer.parseInt(port);
        }
        this.console = null;
    }
    
    public void setConsole(JTextArea console){
        this.console = console;
    }
    
    public void start() {
        System.out.println("\nAddress: "+this.domain+" Port: "+portInt );
        
        this.console.append("\nAddress: "+this.domain+" Port: "+portInt );
        
        ManagedChannel channel = ManagedChannelBuilder.forAddress(domain, portInt)
                .usePlaintext(true)
                .build();
        
        Interceptor interceptor = new Interceptor();
        GreetingServiceBlockingStub stub = GreetingServiceGrpc.newBlockingStub(channel).withInterceptors(interceptor);
        
        HelloResponse response = stub.greeting(
                HelloRequest.newBuilder()
                        .setName("Nuno")
                        //.addHobbies("Photography")
                        .build()
        );
        
        //String msg = "Client started, listening on " + port;
        //LOGGER.log(Level.INFO, msg);
        
        System.out.println("Response. \n"+response);
        this.console.append("\nResponse: "+response );
        
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
              // Use stderr here since the logger may have been reset by its JVM shutdown hook.
              String msg = "\n*** shutting down gRPC client since JVM is shutting down";
              System.err.println(msg);
              LOGGER.log(Level.INFO, msg);
              MyGrpcClient.this.stop();
              msg = "*** client shut down";
              System.err.println(msg);
              LOGGER.log(Level.INFO, msg);
            }
        });
        
    }
    
    
    private void stop() {
        /*
        if (client != null) {
            client.shutdown();
        }
        */
    }
    
    
    /**
     * Await termination on the main thread since the grpc library uses daemon threads.
     *//*
    private void blockUntilShutdown() throws InterruptedException {
      if (client != null) {
        client.awaitTermination();
      }
    }
    */
    
    
    /**
     *      UTILS
     * 
     */
    private String getDomain(String msg) {
        
        /*
        if(this.jTextArea == null){
            System.out.println("Enter a Service name: ");
            try {
                name = buffer.readLine();
            } catch (IOException ex) {
                Logger.getLogger(Register.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            name = doVisualServiceQuestion();
        }
        */
        System.out.println(msg);
        try {
            domain = buffer.readLine();
        } catch (IOException ex) {
            Logger.getLogger(MyGrpcClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        if(domain == null){
            domain = "localhost";
        }
        if(domain.equals("")){
            domain = "localhost";
        }
        return domain;
    }
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
                    Logger.getLogger(MyGrpcClient.class.getName()).log(Level.SEVERE, null, ex);
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
        
        final MyGrpcClient client;
        
        if (args.length > 0) {
            //java -Dgui=false -jar target/file.jar -gui
            String domain = System.getProperty("domain");
            String port = System.getProperty("port");
            
            Boolean hasDomain = false;
            Boolean hasPort = false;
            
            if(domain != null){
                if(!"".equals(domain)) {
                    hasDomain = true;
                }
            }
            if(port != null){
                if(!"".equals(port)) {
                    hasPort = true;
                }
            }
            if(hasDomain && hasPort){
                System.out.println(domain+" "+port);
                client = new MyGrpcClient(domain, port);
            }
            else if(hasDomain && !hasPort){
                client = new MyGrpcClient(domain);
            }
            else if (!hasDomain && hasPort){
                client = new MyGrpcClient(null, port);
            }
            else {
                client = new MyGrpcClient();
            }
        } else {
            client = new MyGrpcClient();
        }
        
        //client = new MyGrpcClient();
        client.start();
        //client.blockUntilShutdown();
    }

    
}
