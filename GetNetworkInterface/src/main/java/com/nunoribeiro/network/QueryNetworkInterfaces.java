package com.nunoribeiro.network;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.IOException;
import static java.lang.System.out;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTextArea;

/**
 *
 * @author admin
 */
public class QueryNetworkInterfaces {
    private JTextArea jTextArea;
    private Enumeration<NetworkInterface> interfaces;
    public JsonArray my_interfaces = new JsonArray();
    
    public QueryNetworkInterfaces(){
        this(null);
    }
    
    public QueryNetworkInterfaces(JTextArea jTextArea){
        this.jTextArea = jTextArea;
        init();
    }
    
    private void init() {
        
        
        try {
            interfaces = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException ex) {
            Logger.getLogger(QueryNetworkInterfaces.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        
        try {
            
            appendText( "DUMP interfaces: "+getDump(interfaces, true) );
            
            OUTER : for (NetworkInterface interface_ : Collections.list(interfaces)) {
                /*
                try{
                    appendText( "\tDUMP: "+getDump(interface_, false) );
                } catch (Exception e) {
                    appendText("\terror: "+e.getMessage());
                }
                */
                
                appendText("============================================");
                appendText("           NEW INTERNET INTERFACE           ");
                appendText("    Display name: "+interface_.getDisplayName());
                appendText("    Name: "+interface_.getName());
                //out.printf("Display name: %s\n", netIf.getDisplayName());
                //out.printf("Name: %s\n", netIf.getName());
                displaySubInterfaces(interface_);
                //out.printf("\n");
                appendText("============================================");
                
                
                // we shouldn't care about loopback addresses
                if (interface_.isLoopback())
                    continue;

                // if you don't expect the interface to be up you can skip this
                // though it would question the usability of the rest of the code
                if (!interface_.isUp())
                    continue;
                
                JsonObject element = new JsonObject();
                element.addProperty("name", interface_.getDisplayName());
                
                // iterate over the addresses associated with the interface
                JsonArray ipaddresses = new JsonArray();
                Enumeration<InetAddress> addresses = interface_.getInetAddresses();
                for (InetAddress address : Collections.list(addresses)) {
                    // look only for ipv4 addresses
                    if (address instanceof Inet6Address)
                        continue;
                    
                    try {
                        // use a timeout big enough for your needs
                        if (!address.isReachable(3000))
                            continue;
                    } catch (IOException ex) {
                        Logger.getLogger(QueryNetworkInterfaces.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
                    JsonObject ipaddress = new JsonObject();
                    
                    ipaddress.addProperty("address", address.getHostAddress());
                    ipaddress.addProperty("domain", address.getHostName());
                    ipaddress.addProperty("domain2", address.getCanonicalHostName());
                    ipaddresses.add(ipaddress);
                    
                    
                    //System.out.format("ni: %s, ia: %s\n", interface_, address);
                    
                    // stops at the first *working* solution
                    //break OUTER;
                }
                element.add("ip_addresses", ipaddresses);
                my_interfaces.add(element);
            }
            
        } catch (SocketException ex) {
            Logger.getLogger(QueryNetworkInterfaces.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //System.out.format("json dump: %s\n", getDump(my_interfaces, true));
    }
    
    public static String getDump(Object o, Boolean m) {
        String result;
        if(m){
            result = new GsonBuilder().setPrettyPrinting().create().toJson(o);
        }
        else{
            result = new GsonBuilder().create().toJson(o);
        }
        //System.out.format("result: %s\n", result);
        return result;
    }
    
    private void displaySubInterfaces(NetworkInterface netIf) throws SocketException {
        Enumeration<NetworkInterface> subIfs = netIf.getSubInterfaces();
        
        Collections.list(subIfs).stream().map((subIf) -> {
            //out.printf("\tSub Interface Display name: %s\n", subIf.getDisplayName());
            //out.printf("\tSub Interface Name: %s\n", subIf.getName());
            appendText("    Sub Interface Display name: "+subIf.getDisplayName());
            return subIf;
        }).forEachOrdered((subIf) -> {
            appendText("    Sub Interface Name: "+subIf.getName());
        });
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
}
