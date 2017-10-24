/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nunoribeiro.gui.listners;

import com.nunoribeiro.gui.MainGui;
import javax.swing.JTextArea;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 *
 * @author admin
 */
public class ListListner implements ListSelectionListener {
    JTextArea console;
    MainGui app;
    
    public ListListner(MainGui app){
        this.console = app.console;
        this.app = app;
    }
    
    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()){
            app.setSelectedService();
        }
    }
}
