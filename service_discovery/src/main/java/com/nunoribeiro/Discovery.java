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
import com.nunoribeiro.MyGuiDNSListner.MyGuiDNSListner;
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
import javax.swing.JOptionPane;
import javax.swing.JTextArea;


/**
 *
 * @author admin
 */
public class Discovery {
    public String newDomain;
    public String newIP;
    
    private InputStreamReader inputStream;
    private BufferedReader buffer;
    
    public String host;
    public JTextArea jTextArea = null;
    private JmDNS jmdns;
    private final MymDNSListner listener;
    
    private InetAddress myNewAddr;
    
    private void initialMessage(String _msg, Boolean _mode){
        this.appendText("\n");
        this.appendText("                        ::Init::");
        this.appendText("             Welcome to java jmDNS discovery." );
        this.appendText("          by Nuno Ribeiro <nmpribeiro@gmail.com>\n");
        this.appendText(_msg, _mode);
    }
    
    public Discovery() throws InterruptedException{
        initialMessage("Entering CLI mode", true);
        this.listener = new MymDNSListner(this);
        //host = initHostAddress();
        this.init();
    }
    
    public Discovery(JTextArea _jTextArea) throws InterruptedException{
        this.jTextArea = _jTextArea;
        initialMessage("Entering GUI mode", false);
        this.listener = new MymDNSListner(this);
        //host = initHostAddress();
        this.init();
    }
    
    public MymDNSListner getListener(){
        return this.listener;
    }
    
    public void setListener(MyGuiDNSListner _l){
        jmdns.removeServiceListener("_http._tcp.local.", listener);
        jmdns.addServiceListener("_http._tcp.local.", _l);
    }
    
    private void init() throws InterruptedException{
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

                    newDomain = myIP.get("domain").getAsString();
                    newIP = myIP.get("address").getAsString();
                }
            }
            
            String hostname = newDomain;
            //there could be non IP assignment
            if(newDomain.equals(newIP)){
                hostname = InetAddress.getLocalHost().getHostName();
            }
            
            myNewAddr = InetAddress.getByAddress(hostname, asBytes(newIP));
            this.appendText("\nRegistering HOST "+myNewAddr.getHostName());
            this.appendText("Changing InetAddress to: "+myNewAddr.getHostName()+".local/"+myNewAddr.getHostAddress() );
            
            
            jmdns = JmDNS.create(myNewAddr);
            
            this.appendText("\nJmDNS created on "+myNewAddr.getHostName());

            // Add a service listener
            jmdns.addServiceListener("_http._tcp.local.", listener);
            //jmdns.addServiceListener("local.", listener);
            this.appendText("\n\nService Listening\n");

            // Wait a bit
            //Thread.sleep(30000);
            
        } catch (UnknownHostException e) {
            appendText(e.getMessage());
            //System.out.println(e.getMessage());
        } catch (IOException e) {
            appendText(e.getMessage());
            //System.out.println(e.getMessage());
        }
    }
    
    public JmDNS renewJmDNS(MymDNSListner listener){
        try {
            jmdns.close();
            jmdns = JmDNS.create(myNewAddr);
            jmdns.addServiceListener("_http._tcp.local.", listener);
            return jmdns;
        } catch (IOException ex) {
            Logger.getLogger(Discovery.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    public JmDNS renewJmDNS(MyGuiDNSListner listener){
        try {
            jmdns.close();
            jmdns = JmDNS.create(myNewAddr);
            jmdns.addServiceListener("_http._tcp.local.", listener);
            return jmdns;
        } catch (IOException ex) {
            Logger.getLogger(Discovery.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
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
    
    private JsonObject askUserForInterface(QueryNetworkInterfaces query) {
        int id;
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
            
            if(choice == null){
                System.out.print("No Interface choosen! Exiting!");
                System.exit(0);
            }
            
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
    
    int getInt(String prompt) {
        System.out.print(prompt);
        while(true){
            try {
                try {
                    return Integer.parseInt(buffer.readLine());
                } catch (IOException ex) {
                    Logger.getLogger(Discovery.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch(NumberFormatException ne) {
                System.out.print("That's not a whole number.\n"+prompt);
            }
        }
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
        byte[] myAddressBytes = new byte[] {
                        (byte) (ipInt & 0xff),
                        (byte) (ipInt >> 8 & 0xff),
                        (byte) (ipInt >> 16 & 0xff),
                        (byte) (ipInt >> 24 & 0xff)
        };
         // Return the TCP/IP bytes
          return myAddressBytes;
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
    
    public void appendText(String _text){
        this.appendText(_text, true);
    }
    public void appendText(String _text, Boolean _mode){
        if(_mode){
            if(jTextArea != null ){
                jTextArea.append(_text+"\n");
                jTextArea.setCaretPosition(jTextArea.getDocument().getLength());
            }
            System.out.println(_text+"\n");
        }
        else {
            out.printf(_text+"\n");
        }
    }
    
    public void close(){
        appendText("Shutting down...");
        try {
            jmdns.close();
        } catch (IOException ex) {
            Logger.getLogger(Discovery.class.getName()).log(Level.SEVERE, null, ex);
        }
        appendText("Bye!");
    }
    
    public JmDNS getJmDNS(){
        return this.jmdns;
    }
    
}



