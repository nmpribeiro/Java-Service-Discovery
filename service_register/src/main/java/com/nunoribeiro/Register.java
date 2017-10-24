/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nunoribeiro;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.nunoribeiro.network.QueryNetworkInterfaces;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import static java.lang.System.out;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;

/**
 *
 * @author admin
 */
public class Register {
    public String newAddress;
    public String newDomain;
    public String newIP;
    public JTextArea jTextArea = null;
    private JmDNS jmdns;
    
    private InputStreamReader inputStream;
    private BufferedReader buffer;
    
    public Register() throws InterruptedException{
        this.appendText( "             ::Init::\n  Welcome to java jmDNS register.\n\n" );
        this.appendText("Entering CLI mode");
        
        //host = initHostAddress();
        this.init();
    }
    
    public Register(JTextArea _jTextArea) throws InterruptedException{
        this.appendText("Entering GUI mode");
        
        jTextArea = _jTextArea;
        //host = initHostAddress();
        
        this.init();
    }
    
    private void init() throws InterruptedException {
        
        this.inputStream = new InputStreamReader(System.in);
        this.buffer = new BufferedReader(this.inputStream);
        
        
        
        //Get the interface
        QueryNetworkInterfaces query = new QueryNetworkInterfaces(jTextArea);
        
        //getDump(query.my_interfaces, true);
        //we can now ask for the right interface to assign an InetAdress
        
        for(JsonElement interface_ : query.my_interfaces){
            //getDump(interface_, true);
            
            JsonObject my_interface = interface_.getAsJsonObject();
            JsonArray my_addresses = my_interface.getAsJsonArray("ip_addresses");
            
            for(JsonElement my_ip : my_addresses){
                JsonObject myIP = my_ip.getAsJsonObject();
                
                newDomain = myIP.get("domain").getAsString();
                newIP = myIP.get("address").getAsString();
            }
        }
        
        //ask user for interface!
        JsonObject interface_ = askUserForInterface(query);
        //getDump(interface_, true);
        
        
        try {
            
            
            // Get choosen!
            if(interface_ != null){
                JsonArray my_addresses = interface_.getAsJsonArray("ip_addresses");
                
                //we can later ask for IP's... but there's one per interface...
                
                for(JsonElement my_ip : my_addresses){
                    JsonObject myIP = my_ip.getAsJsonObject();

                    String _domain = myIP.get("domain").getAsString();
                    if(_domain != null){
                        newDomain = _domain;
                    }
                    String _ip = myIP.get("address").getAsString();
                    if(_ip != null){
                        newIP = _ip;
                    }
                    
                    getDump(interface_, true);
                    getDump(myIP, true);
                }
            }
            
            String hostname = newDomain;
            //there could be non IP assignment
            if(newDomain.equals(newIP)){
                hostname = InetAddress.getLocalHost().getHostName();
            }
            
            
            
            InetAddress myNewAddr = InetAddress.getByAddress(hostname, asBytes(newIP));
            //InetAddress myNewAddr = InetAddress.getByName(hostname);
            this.appendText("Original HOST "+InetAddress.getLocalHost());
            this.appendText("Changing InetAddress to: "+myNewAddr.getHostName()+" IP: "+myNewAddr.getHostAddress() );
            
            
            jmdns = JmDNS.create(myNewAddr);
            
            //jmdns = JmDNS.create(InetAddress.getLocalHost());
            
            //ask user the service name!
            String name = askServiceName();
            int port = askPort();
            
            this.appendText("Registering service "+name+" on port "+port+" from host "+myNewAddr.getHostName());
            
            //String path = "UnitLabsDevice";
            String path = "path=index.html";
            
            // Register a serviceString
            ServiceInfo serviceInfo = ServiceInfo.create("_http._tcp.local.", name, port, path);
            jmdns.registerService(serviceInfo);
            
            this.appendText("Service "+name+" Registered!");
            this.appendText("Access it through port "+port+" on host "+myNewAddr.getHostName()+" IP: "+myNewAddr.getHostAddress()
            );
            
            // Wait a bit
            //Thread.sleep(30000);
            
            MyGrpcServer gRPCserver = new MyGrpcServer(myNewAddr.getHostName(), port);
            gRPCserver.setConsole(jTextArea);
            gRPCserver.start();
            
        } catch (UnknownHostException e) {
            setAppendText(e.getMessage());
            //System.out.println(e.getMessage());
        } catch (IOException e) {
            setAppendText(e.getMessage());
            //System.out.println(e.getMessage());
        }
    }
    
    
    public static String getDump(Object o, Boolean m) {
        String result;
        if(m){
            result = new GsonBuilder().setPrettyPrinting().create().toJson(o);
        }
        else{
            result = new GsonBuilder().create().toJson(o);
        }
        System.out.format("result: %s\n", result);
        System.out.format("\n");
        return result;
    }
    
    public void setAppendText(String _text){
        this.appendText(_text);
    }
    private void appendText(String _text){
        if(jTextArea != null){
            System.out.println(_text+"\n");
            jTextArea.append(_text+"\n");
        } else {
            out.printf(_text+"\n");
        }
    }
    
    
    /**
     * Convert a TCP/IP address string into a byte array
     * 
     * @param addr String
     * @return byte[]
     */
    public final static byte[] asBytes(String addr) {
      
      // Convert the TCP/IP address string to an integer value
      
      int ipInt = parseNumericAddress(addr);
      if ( ipInt == 0)
        return null;
      
      // Convert to bytes
      
      byte[] ipByts = new byte[4];
      
      ipByts[3] = (byte) (ipInt & 0xFF);
      ipByts[2] = (byte) ((ipInt >> 8) & 0xFF);
      ipByts[1] = (byte) ((ipInt >> 16) & 0xFF);
      ipByts[0] = (byte) ((ipInt >> 24) & 0xFF);
      
      // Return the TCP/IP bytes
      
      return ipByts;
    }
    /**
     * Check if the specified address is a valid numeric TCP/IP address and return as an integer value
     * 
     * @param ipaddr String
     * @return Integer
     */
    public final static int parseNumericAddress(String ipaddr) {

      //  Check if the string is valid

      if ( ipaddr == null || ipaddr.length() < 7 || ipaddr.length() > 15)
        return 0;

      //  Check the address string, should be n.n.n.n format

      StringTokenizer token = new StringTokenizer(ipaddr,".");
      if ( token.countTokens() != 4)
        return 0;

      int ipInt = 0;

      while ( token.hasMoreTokens()) {

        //  Get the current token and convert to an integer value

        String ipNum = token.nextToken();

        try {

          //  Validate the current address part

          int ipVal = Integer.valueOf(ipNum);
          if ( ipVal < 0 || ipVal > 255)
            return 0;

          //  Add to the integer address

          ipInt = (ipInt << 8) + ipVal;
        }
        catch (NumberFormatException ex) {
          return 0;
        }
      }

      //  Return the integer address

      return ipInt;
    }
    
    public void close(){
        setAppendText("Shutting down...");
        try {
            
            jmdns.close();
            
        } catch (IOException ex) {
            Logger.getLogger(Register.class.getName()).log(Level.SEVERE, null, ex);
        }
        setAppendText("Bye!");
    }

    private String askServiceName() {
        String name = null;
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
        
        if(name == null){
            name = "UnitLabs default";
        }
        if(name.equals("")){
            name = "UnitLabs default";
        }
        return name.replaceAll(" ", "_");
    }

    private String doVisualServiceQuestion() {
        String result = "UnitLabs_default";
        String input;
        input = JOptionPane.showInputDialog("What's the service name? ");
        if(input == null){
            
        } else switch (input) {
            case "":
                break;
            default:
                result = input;
                break;
        }
        return result;
    }
    
    int getInt(String prompt) {
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
                    Logger.getLogger(Register.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch(NumberFormatException ne) {
                System.out.print("That's not a whole number.\n"+prompt);
            }
        }
    }

    private JsonObject askUserForInterface(QueryNetworkInterfaces query) {
        int id = 0;
        ArrayList if_ = new ArrayList();
        int i = 0;
        for(JsonElement interface_ : query.my_interfaces){
            //getDump(interface_, true);
            JsonObject my_if = interface_.getAsJsonObject();
            //getDump(my_if, true);
            System.out.printf("interface: "+my_if.get("name").getAsString()+"\n");
            String if_name = my_if.get("name").getAsString();
            if_.add(if_name);
            i++;
        }
        
        if(this.jTextArea == null){
            //do question
            System.out.printf("Please, choose your interface\n");
            
            for( int j = 0; j < if_.size(); j++ )
            {
                String s = if_.get(j).toString();
                if(s!=null) System.out.printf("\t %o) %s\n", (int)j ,s);
            }
            
            id = this.getInt("Enter desired interface ID:");
            
            
        } else {
            
            //get it visually!
            String[] strArray = new String[ if_.size() ];

            for( int j = 0; j < strArray.length; j++ )
                strArray[ j ] = if_.get( j ).toString();
            
            final Object choice = JOptionPane.showInputDialog(null, "Select an Interface", "Interface List",
            JOptionPane.QUESTION_MESSAGE, null, strArray, strArray[0]);
            id = getChoiceIndex(choice, strArray) +1 ;
        }
        
        JsonObject choosen_if = null;
        
        i = 0;
        for(JsonElement interface_ : query.my_interfaces){
            if(i == id-1){
                choosen_if = interface_.getAsJsonObject();
            }
            i++;
        }
        
        return choosen_if;
    }
    
    public static int getChoiceIndex(final Object choice, final Object[] choices) {
        if (choice != null) {
         for (int i = 0; i < choices.length; i++) {
          if (choice.equals(choices[i])) {
           return i;
          }
         }
        }
        return -1;
    }

    private int askPort() {
        int result;
        
        if(this.jTextArea == null){
            
            result = this.getInt("Enter a Service port:");
            
        } else {
            result = doVisualPortQuestion();
        }
        return result;
    }

    private int doVisualPortQuestion() {
        String input;
        int result;
        input = JOptionPane.showInputDialog("What's the service port? ");
        if(null == input){
            result = 1234;
        }
        else switch (input) {
            case "":
                result = 1234;
                break;
            default:
                result = Integer.parseInt(input);
                break;
        }
        return result;
    }
    
    
}
