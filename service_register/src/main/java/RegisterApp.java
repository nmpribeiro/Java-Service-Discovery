/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import com.nunoribeiro.Register;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.AdjustmentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.JTextComponent;
import javax.swing.text.TextAction;

/**
 *
 * @author admin
 */
public class RegisterApp {
    
    public static void main(String[] args){
        Register app;
        Boolean guiMode = false;
        
        if ( RegisterApp.class
                .getProtectionDomain()
                .getCodeSource()
                .getLocation()
                .getFile()
                .endsWith(".jar") ) {
            
            if (args.length > 0) {
                //java -Dgui=false -jar target/file.jar -gui
                String guiArg = System.getProperty("gui");
                if(guiArg != null){
                    if("true".equals(guiArg)) {
                        guiMode = true;
                    }
                }
            }
        } else {
            guiMode = true;
        }
        
        if(!guiMode){
            
            try {
                app = new Register();
                
                Runtime.getRuntime().addShutdownHook(new Thread() {
                    @Override
                    public void run() {
                        System.out.println(" >> Shutdown Hook Issued...\n\nTerminating by user request.");
                        app.close();
                    }
                });
                
            } catch (InterruptedException ex) {
                Logger.getLogger(RegisterApp.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        } else {
            
            JFrame frame = new JFrame();
            frame.setTitle("Register Service with JmDNS");
            
            JTextArea jTextArea = new JTextArea();
            jTextArea.append( "             ::Init::\n  Welcome to java jmDNS register.\n\n" );

            //fix font!
            Font font = new Font("monospaced", Font.PLAIN, 12);
            jTextArea.setFont(font);
            
            //add mouse menu (copy and selet all)
            JPopupMenu menu = new JPopupMenu();
            
            //copy
            Action copy = new DefaultEditorKit.CopyAction();
            copy.putValue(Action.NAME, "Copy");
            copy.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control C"));
            menu.add( copy );
            
            //select all
            Action selectAll = new SelectAll();
            menu.add( selectAll );
            
            //assign mouse menu
            jTextArea.setComponentPopupMenu( menu );
            
            
            JScrollPane scrollPane = new JScrollPane(jTextArea);

            scrollPane.getVerticalScrollBar().addAdjustmentListener((AdjustmentEvent e) -> {
                e.getAdjustable().setValue(e.getAdjustable().getMaximum());
            });
            
            
            scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
            frame.getContentPane().add(scrollPane);

            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            

            frame.setPreferredSize(new Dimension(600, 300));
            //frame.add( jTextArea );
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            
            
            try {
                
                app = new Register(jTextArea);
                
                //do window fancy stuff :)
                frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
                
                Runtime.getRuntime().addShutdownHook(new Thread() {
                    @Override
                    public void run() {
                        app.close();

                        WindowEvent closingEvent = new WindowEvent(frame, WindowEvent.WINDOW_CLOSING);
                        Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(closingEvent);
                    }
                });

                WindowAdapter windowAdapter = new WindowAdapter() {
                    // WINDOW_CLOSING event handler
                    @Override
                    public void windowClosing(WindowEvent e) {
                        super.windowClosing(e);
                        // You can still stop closing if you want to
                        int res = JOptionPane.showConfirmDialog(frame, "Are you sure you want to close?", "Close?", JOptionPane.YES_NO_OPTION);
                        if ( res == 0 ) {
                            // dispose method issues the WINDOW_CLOSED event
                            //frame.dispose();
                            frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSED));
                        }
                    }

                    // WINDOW_CLOSED event handler
                    @Override
                    public void windowClosed(WindowEvent e) {
                        super.windowClosed(e);
                        // Close application if you want to with System.exit(0)
                        // but don't forget to dispose of all resources 
                        // like child frames, threads, ...
                        System.exit(0);
                    }
                };

                // don't forget this
                frame.addWindowListener(windowAdapter);
                
            } catch (InterruptedException ex) {
                Logger.getLogger(RegisterApp.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
    }
    
    static class SelectAll extends TextAction
    {
        public SelectAll()
        {
            super("Select All");
            putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control S"));
        }

        @Override
        public void actionPerformed(ActionEvent e)
        {
            JTextComponent component = getFocusedComponent();
            component.selectAll();
            component.requestFocusInWindow();
        }
    }
}
