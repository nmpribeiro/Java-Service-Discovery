/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nunoribeiro.MyGuiDNSListner;

import com.nunoribeiro.gui.MainGui;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceInfo;
import javax.jmdns.ServiceListener;

/**
 *
 * @author admin
 */
public class MyGuiDNSListner implements ServiceListener {
        public MainGui app;
        
        public MyGuiDNSListner(MainGui app){
            System.out.println("MymDNSListner started!");
            this.app = app;
        }
        
        @Override
        public void serviceAdded(ServiceEvent event) {
            //JSONObject obj = event.getInfo();
            app.jmdns.requestServiceInfo(event.getType(), event.getName(), 1);
            append("+ " + event.getInfo());
        }

        @Override
        public void serviceRemoved(ServiceEvent event) {
            append("- " + event.getInfo());
            
            ServiceInfo info = event.getInfo();
            app.removeService(app.getServiceName(info));
        }

        @Override
        public void serviceResolved(ServiceEvent event ) {
            append("Resolved: " + event.getInfo());
            
            ServiceInfo info = event.getInfo();
            app.addService(app.getServiceName(info));
        }
        
        public void append(String text){
            app.appendText(text, true);
        }
        public void append(String text, Boolean mode){
            app.appendText(text, mode);
        }
}
