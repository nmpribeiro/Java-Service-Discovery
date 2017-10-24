/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nunoribeiro;

import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceListener;

/**
 *
 * @author admin
 */
public class MymDNSListner implements ServiceListener {
        public Discovery discover;
        
        MymDNSListner(Discovery discovery){
            System.out.println("MymDNSListner started!");
            this.discover = discovery;
        }
        
        @Override
        public void serviceAdded(ServiceEvent event) {
            append("+ " + event.getInfo());
            //Discovery.getDump(event.getInfo(), true);
            //System.out.println("Service added: " + event.getInfo());
        }

        @Override
        public void serviceRemoved(ServiceEvent event) {
            append("- " + event.getInfo());
            //Discovery.getDump(event.getInfo(), true);
            //System.out.println("Service removed: " + event.getInfo());
        }

        @Override
        public void serviceResolved(ServiceEvent event ) {
            append("Resolved: " + event.getInfo());
            //Discovery.getDump(event.getInfo(), true);
            //Discovery.this.appendText();
            //System.out.println("Service resolved: " + event.getInfo());
        }
        
        public void append(String text){
            discover.appendText(text, true);
        }
        public void append(String text, Boolean mode){
            discover.appendText(text, mode);
        }
    }
