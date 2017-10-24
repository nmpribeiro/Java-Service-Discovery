/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nunoribeiro.gui;

import com.nunoribeiro.Discovery;
import com.nunoribeiro.MyGrpcClient;
import com.nunoribeiro.MyGuiDNSListner.MyGuiDNSListner;
import com.nunoribeiro.MymDNSListner;
import com.nunoribeiro.gui.listners.ListListner;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import static java.lang.System.out;
import java.net.InetAddress;
import java.util.ArrayList;
import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.Timer;

/**
 *
 * @author admin
 */
public class MainGui extends javax.swing.JFrame {
    JScrollPane consoleScroll;
    public JTextArea console;
    public JmDNS jmdns;
    MymDNSListner listener;
    MyGuiDNSListner guiListener;
    DefaultListModel servicesListUi;
    public ArrayList<String> foundDevices = new ArrayList<>();
    Discovery app;
    JFrame initFrame;
    
    ListSelectionModel listSelectionModel;
    ServiceInfo selectedService;
    ServiceInfo[] availableServices;
    String type = "_http._tcp.local.";
    
    /**
     * Creates new form MainGui
     */
    public MainGui() {
        initComponents();
    }
    
    public MainGui(JFrame frame, JTextArea console, Discovery app){
        this.console = console;
        this.initFrame = frame;
        this.app = app;
        this.jmdns = app.getJmDNS();
        
        this.listener = app.getListener();
        this.servicesListUi = new DefaultListModel();
        setWindowAdapter();
    }
    
    // <editor-fold defaultstate="collapsed" desc="setWindowAdapter">
    private void setWindowAdapter(){
        JFrame frame = this;
        JTextArea _console = this.console;
        
        WindowAdapter windowAdapter = new WindowAdapter() {
            // WINDOW_CLOSING event handler
            @Override
            public void windowClosing(WindowEvent e) {
                Object options[] = {"Yes", "No"};

                int close = JOptionPane.showOptionDialog(e.getComponent(),
                        "Really want to close this application?\n", "Attention",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.INFORMATION_MESSAGE,
                        null,
                        options,
                        null);

                if(close == JOptionPane.YES_OPTION) {
                    ((JFrame)e.getSource()).setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
                    _console.append("\nShutting down Application!\nIt takes a while... please wait!\n");
                    
                    ActionListener task = new ActionListener() {
                        boolean alreadyDisposed = false;
                        @Override
                        @SuppressWarnings("empty-statement")
                        public void actionPerformed(ActionEvent e) {
                            if (frame.isDisplayable()) {
                                alreadyDisposed = true;
                                //frame.dispose();
                                
                                WindowEvent closingEvent = new WindowEvent(frame, WindowEvent.WINDOW_CLOSED);
                                Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(closingEvent);;
                            }
                        }
                    };
                    Timer timer = new Timer(500, task); //fire every half second
                    timer.setInitialDelay(2000);        //first delay 2 seconds
                    timer.setRepeats(false);
                    timer.start();

                } else {
                   ((JFrame)e.getSource()).setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
                }
            }
            
            // WINDOW_CLOSED event handler
            @Override
            public void windowClosed(WindowEvent e) {
                super.windowClosed(e);
                //frame.dispose();
                // Close application if you want to with System.exit(0)
                // but don't forget to dispose of all resources 
                // like child frames, threads, ...
                System.exit(0);
            }
        };
        
        this.addWindowListener(windowAdapter);
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        //this.addWindowListener(_w);
    }
    //</editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="utilities like appendText">
    public void appendText(String _text){
        appendText(_text, true);
    }
    public void appendText(String _text, Boolean mode){
        if(mode){
            if(this.console != null){
                System.out.println(_text+"\n");
                this.console.append(_text+"\n");
                this.console.setCaretPosition(this.console.getDocument().getLength());
            } else {
                out.printf(_text+"\n");
            }
        } else {
            out.printf(_text+"\n");
        }
    }
    // </editor-fold>
    
    public void init(){
        
        //now we need to populate that jmdns
        //jmdns.getServiceInfo(type, name);
        this.guiListener = new MyGuiDNSListner(this);
        this.app.setListener(this.guiListener);
        
        initComponents();
        
        refreshServices(); //already updates jmdns.list
        appendText("There are "+Integer.toString(this.availableServices.length)+" services.");
        
        initListSelection();
        
        initFrame.setVisible(false);
        this.setVisible(true);
        
    }
    
    public String getServiceName(ServiceInfo info){
        if(info == null) return null;
        return info.getQualifiedName();
    }
    public String getServiceIP(ServiceInfo info){
        if(info == null) return null;
        String result = null;
        InetAddress[] inetList = info.getInet4Addresses();
        for(InetAddress inet : inetList){
            result = inet.getHostAddress();
        }
        
        return result;
    }
    
    public String getServiceHost(ServiceInfo info){
        if(info == null) return null;
        return info.getServer();
    }
    
    public String getServicePort(ServiceInfo info){
        if(info == null) return null;
        return Integer.toString(info.getPort());
    }
    
    public ServiceInfo getServiceByName(String name){
        if(name == null) return null;
        if("".equals(name)) return null;
        for(ServiceInfo info : this.availableServices){
            if(name.equals(getServiceName(info))) return info;
        }
        return null;
    }
    
    public void setSelectedService(){
        setSelectedService(0);
    }
    public void setSelectedService(int i){
        this.selectedService = this.getServiceByName(this.guiList.getSelectedValue());
        populateFieldsWithService();
    }
    
    public void populateFieldsWithService(){
        selectedIPjTextField.setText(getServiceIP(selectedService));
        selectedHostjTextField.setText(getServiceHost(selectedService));
        selectedPortjTextField.setText(getServicePort(selectedService));
    }
    
    public void initListSelection(){
        listSelectionModel = guiList.getSelectionModel();
        listSelectionModel.addListSelectionListener(
                new ListListner(this)
        );
    }
    
    
    

    
    
    
    private void renewJmDNS(){
        //get all available services again
        this.jmdns = app.renewJmDNS(this.guiListener);
        refreshServices();
    }
    
    public void updateJmDNSServiceList(){
        this.availableServices = jmdns.list(this.type, 1000);
    }
    
    /**
     * Only use updateJmDNSServiceList to get a new 'availableServices' list HERE!
     */
    public void refreshServices(){
        if(guiList != null ) guiList.clearSelection();
        
        this.servicesListUi.removeAllElements();
        updateJmDNSServiceList();
        for(ServiceInfo info : this.availableServices){
            //no need to add! use Listner for that
            //servicesListUi.addElement(getServiceName(info));
            this.appendText("Added service "+getServiceName(info)+" to GUI List");
        }
    }
    
    public void addService(String name){
        servicesListUi.addElement(name);
        updateJmDNSServiceList();
    }
    public void removeService(String name){
        //check if selection is removed service
        if(this.getServiceName(this.selectedService) == null ? name == null : this.getServiceName(this.selectedService).equals(name)){
            guiList.clearSelection();
            this.selectedService = null;
        }
        servicesListUi.removeElement(name);
        updateJmDNSServiceList();
    }

    /**
     * This method is called from within the constructor to initialise the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        topPanel = new javax.swing.JPanel();
        activeDevicesList = new javax.swing.JScrollPane();
        guiList = new javax.swing.JList(servicesListUi);
        sayHello = new java.awt.Button();
        lastDeviceComScroll = new javax.swing.JScrollPane();
        lastDeviceCom = new javax.swing.JTextArea();
        comLabel = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        itemIPLabel = new javax.swing.JLabel();
        selectedIPjTextField = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        selectedHostjTextField = new javax.swing.JTextField();
        rediscoverBtn = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        selectedPortjTextField = new javax.swing.JTextField();
        jSeparator1 = new javax.swing.JSeparator();
        bottomPanel = new javax.swing.JPanel();
        scrollConsole = new javax.swing.JScrollPane(this.console);
        jLabel1 = new javax.swing.JLabel();
        statusBar = new javax.swing.JLabel();
        main_menu = new javax.swing.JMenuBar();
        file_menu = new javax.swing.JMenu();
        file_refresh_item = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        file_exit_item = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Communicate with Devices using gRPC");
        setFont(new java.awt.Font("Monospaced", 0, 10)); // NOI18N
        setForeground(java.awt.Color.darkGray);
        setLocationByPlatform(true);
        setMinimumSize(new java.awt.Dimension(300, 200));
        setName("mainFrame"); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        activeDevicesList.setViewportView(guiList);

        sayHello.setActionCommand("sayHello");
        sayHello.setFont(sayHello.getFont());
        sayHello.setLabel("Say Hello!");
        sayHello.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                sayHelloMouseClicked(evt);
            }
        });
        sayHello.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sayHelloActionPerformed(evt);
            }
        });

        lastDeviceCom.setColumns(20);
        lastDeviceCom.setFont(lastDeviceCom.getFont());
        lastDeviceCom.setRows(5);
        lastDeviceComScroll.setViewportView(lastDeviceCom);

        comLabel.setFont(comLabel.getFont());
        comLabel.setText("Last communication w/ devices");

        itemIPLabel.setText("Service IP");

        jLabel2.setText("Service Hostname");

        rediscoverBtn.setText("Re-Discover (found no other way)");
        rediscoverBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                rediscoverBtnMouseClicked(evt);
            }
        });
        rediscoverBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rediscoverBtnActionPerformed(evt);
            }
        });

        jLabel3.setText("Service Port");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(rediscoverBtn)
                    .addComponent(jLabel3)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(selectedPortjTextField, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(selectedIPjTextField, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(itemIPLabel, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(selectedHostjTextField, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE)))
                .addGap(0, 28, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(rediscoverBtn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(itemIPLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(selectedIPjTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(selectedHostjTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(selectedPortjTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout topPanelLayout = new javax.swing.GroupLayout(topPanel);
        topPanel.setLayout(topPanelLayout);
        topPanelLayout.setHorizontalGroup(
            topPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(topPanelLayout.createSequentialGroup()
                .addComponent(activeDevicesList, javax.swing.GroupLayout.PREFERRED_SIZE, 189, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(topPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lastDeviceComScroll, javax.swing.GroupLayout.DEFAULT_SIZE, 206, Short.MAX_VALUE)
                    .addGroup(topPanelLayout.createSequentialGroup()
                        .addGroup(topPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(topPanelLayout.createSequentialGroup()
                                .addComponent(sayHello, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(comLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addContainerGap())))
        );
        topPanelLayout.setVerticalGroup(
            topPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(activeDevicesList)
            .addGroup(topPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(sayHello, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(comLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lastDeviceComScroll, javax.swing.GroupLayout.DEFAULT_SIZE, 152, Short.MAX_VALUE))
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        scrollConsole.setFont(scrollConsole.getFont());

        jLabel1.setText("Console");

        javax.swing.GroupLayout bottomPanelLayout = new javax.swing.GroupLayout(bottomPanel);
        bottomPanel.setLayout(bottomPanelLayout);
        bottomPanelLayout.setHorizontalGroup(
            bottomPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrollConsole)
            .addGroup(bottomPanelLayout.createSequentialGroup()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        bottomPanelLayout.setVerticalGroup(
            bottomPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, bottomPanelLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrollConsole, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        statusBar.setFont(statusBar.getFont());
        statusBar.setText("Ready...");

        main_menu.setName("mainMenu"); // NOI18N

        file_menu.setText("File");
        file_menu.setFont(file_menu.getFont());

        file_refresh_item.setFont(file_refresh_item.getFont());
        file_refresh_item.setText("Refresh Devices");
        file_refresh_item.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                file_refresh_itemActionPerformed(evt);
            }
        });
        file_refresh_item.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                file_refresh_itemKeyPressed(evt);
            }
        });
        file_menu.add(file_refresh_item);
        file_menu.add(jSeparator2);

        file_exit_item.setFont(file_exit_item.getFont());
        file_exit_item.setText("Exit");
        file_exit_item.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                file_exit_itemActionPerformed(evt);
            }
        });
        file_exit_item.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                file_exit_itemKeyPressed(evt);
            }
        });
        file_menu.add(file_exit_item);

        main_menu.add(file_menu);

        setJMenuBar(main_menu);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(topPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(bottomPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(statusBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSeparator1)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(topPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 4, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(bottomPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(statusBar))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // <editor-fold defaultstate="collapsed" desc="Gui Actions">
    private void file_refresh_itemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_file_refresh_itemActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_file_refresh_itemActionPerformed

    private void file_refresh_itemKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_file_refresh_itemKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_file_refresh_itemKeyPressed

    private void file_exit_itemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_file_exit_itemActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_file_exit_itemActionPerformed

    private void file_exit_itemKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_file_exit_itemKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_file_exit_itemKeyPressed

    private void sayHelloActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sayHelloActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_sayHelloActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        // TODO add your handling code here:
        setExtendedState(JFrame.MAXIMIZED_BOTH);
    }//GEN-LAST:event_formWindowOpened

    private void rediscoverBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rediscoverBtnActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_rediscoverBtnActionPerformed

    private void rediscoverBtnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_rediscoverBtnMouseClicked
        // TODO add your handling code here:
        //((JmDNSImpl) jmdns).getCache().clear();
        //getServices();
        renewJmDNS();
    }//GEN-LAST:event_rediscoverBtnMouseClicked

    private void sayHelloMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_sayHelloMouseClicked
        // TODO add your handling code here:
        if(this.selectedService == null)
            msgDialogue("Please choose first a device on the List!");
        else
        {
            //msgDialogue("Starting a Dialogue with <ul><li><b>"+this.selectedService.getName()+"</b> service</li></ul>");
            
            //this.lastDeviceCom.setText("");
            this.lastDeviceCom.append("\n================\nStarting a Dialogue with ::"+this.selectedService.getName()+"::");
            this.lastDeviceCom.append("\nINIT\nHost: "+getServiceHost(selectedService)+ " "+getServiceIP(selectedService));
            
            MyGrpcClient client = new MyGrpcClient(getServiceHost(selectedService), getServicePort(selectedService));
            
            client.setConsole(this.lastDeviceCom);
            client.start();
        }
    }//GEN-LAST:event_sayHelloMouseClicked
    // </editor-fold>
    
    public void msgDialogue(String msg){
        JLabel label = new JLabel("<html>"+msg+"</html>");
        label.setFont(new Font("sans-serif", Font.PLAIN, 14));
        JOptionPane.showMessageDialog(null, label);
    }
    
    // <editor-fold defaultstate="collapsed" desc="main method (this won't fire up!)">
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainGui.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        // </editor-fold>
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            new MainGui().setVisible(true);
        });
    }
    // </editor-fold>

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane activeDevicesList;
    private javax.swing.JPanel bottomPanel;
    private javax.swing.JLabel comLabel;
    private javax.swing.JMenuItem file_exit_item;
    private javax.swing.JMenu file_menu;
    private javax.swing.JMenuItem file_refresh_item;
    private javax.swing.JList<String> guiList;
    private javax.swing.JLabel itemIPLabel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JTextArea lastDeviceCom;
    private javax.swing.JScrollPane lastDeviceComScroll;
    private javax.swing.JMenuBar main_menu;
    private javax.swing.JButton rediscoverBtn;
    private java.awt.Button sayHello;
    private javax.swing.JScrollPane scrollConsole;
    private javax.swing.JTextField selectedHostjTextField;
    private javax.swing.JTextField selectedIPjTextField;
    private javax.swing.JTextField selectedPortjTextField;
    private javax.swing.JLabel statusBar;
    private javax.swing.JPanel topPanel;
    // End of variables declaration//GEN-END:variables


    // <editor-fold defaultstate="collapsed" desc="class WindowConfirmedCloseAdapter">
    public class WindowConfirmedCloseAdapter extends WindowAdapter {

        @Override
        public void windowClosing(WindowEvent e) {

            Object options[] = {"Yes", "No"};

            int close = JOptionPane.showOptionDialog(e.getComponent(),
                    "Really want to close this application?\n", "Attention",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    options,
                    null);

            if(close == JOptionPane.YES_OPTION) {
               ((JFrame)e.getSource()).setDefaultCloseOperation(
                       JFrame.EXIT_ON_CLOSE);
            } else {
               ((JFrame)e.getSource()).setDefaultCloseOperation(
                       JFrame.DO_NOTHING_ON_CLOSE);
            }
        }
    }
    // </editor-fold>

}



