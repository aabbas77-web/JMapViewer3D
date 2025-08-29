/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package oghab.mapviewer;

import com.sun.jna.Library;
import com.sun.jna.Native;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import static oghab.mapviewer.MainFrame.CURR_LANG;
import static oghab.mapviewer.MainFrame.Settings.bDebug;
import static oghab.mapviewer.MainFrame.jvm_arch;
import static oghab.mapviewer.MainFrame.lib_core32;
import static oghab.mapviewer.MainFrame.lib_core64;
import static oghab.mapviewer.MainFrame.lib_core_path;
import static oghab.mapviewer.MainFrame.strAppPath;

/**
 *
 * @author AZUS
 */
public class AboutFrame extends javax.swing.JFrame {
    static public AboutFrame frame = null;

    boolean bRegistered = false;
    public void set_language(String lang)
    {
        ResourceBundle bundle = MainFrame.getLanguageBundle(this, lang);
        if(bRegistered)
            this.setTitle(bundle.getString("F_MapViewer")+" "+bundle.getString("F_Registered_Version"));
        else
            this.setTitle(bundle.getString("F_MapViewer")+" "+bundle.getString("F_Demo_Version"));

        L_SystemID.setText(bundle.getString("L_SystemID"));
        L_SerialNumber.setText(bundle.getString("L_SerialNumber"));
        B_Register.setText(bundle.getString("B_Register"));
    }
    
    /**
     * Creates new form AboutFrame
     */
    public AboutFrame() {
        frame = this;
        initComponents();
        check_version();
        this.set_language(CURR_LANG);

        ImageIcon icon = new ImageIcon(getClass().getResource("/res/mapviewer_icon.png"));  
        this.setIconImage(icon.getImage());  

        if(MainFrame.Settings.bCooperation){
            ImageIcon icon_oti = new ImageIcon(getClass().getResource("/res/OTI.png"));  
            jLabel2.setIcon(icon_oti);        
        }
        
        this.pack();

        MainFrame.system_update();
    }
    
    /**
     * Creates new form AboutFrame
     */
    public void check_version()
    {
        jvm_arch = System.getProperty("sun.arch.data.model");
        if(bDebug)
        {
            if(jvm_arch.contains("64"))
                lib_core_path = "D:\\Ali\\java\\Tools\\Protection\\DLL\\Win64\\Release\\Core.dll";
            else
                lib_core_path = "D:\\Ali\\java\\Tools\\Protection\\DLL\\Win32\\Release\\Core.dll";
        }
        else
        {
            if(jvm_arch.contains("64"))
                lib_core_path = Paths.get(strAppPath, "Core64.dll").toString();
            else
                lib_core_path = Paths.get(strAppPath, "Core32.dll").toString();
        }

        if(jvm_arch.contains("64"))
        {
            lib_core64 = (MainFrame.myCore64) Native.load(lib_core_path, MainFrame.myCore64.class);
            if(lib_core64 == null)
            {
                System.out.println("MapViewer [Demo Version]");
                System.exit(0);
            }
            System.out.println("System ID: "+lib_core64.System_ID());
            TF_SystemID.setText(Long.toString(lib_core64.System_ID()));
        }
        else
        {
            lib_core32 = (MainFrame.myCore32) Native.load(lib_core_path, MainFrame.myCore32.class);
            if(lib_core32 == null)
            {
                System.out.println("MapViewer [Demo Version]");
                System.exit(0);
            }
            System.out.println("System ID: "+lib_core32._System_ID());
            TF_SystemID.setText(Long.toString(lib_core32._System_ID()));
        }

        bRegistered = false;
        if(jvm_arch.contains("64"))
        {
            int g_SerialNumber1 = 0;
            int c_nMapViewer_Key = 55555555;
            g_SerialNumber1 = lib_core64.LoadSerialNumber();
            if(g_SerialNumber1 != lib_core64.EncodeSystem_ID(lib_core64.System_ID(),c_nMapViewer_Key))
            {
                System.out.println("MapViewer [Demo Version]");
                System.exit(0);
            }
            else
            {
                System.out.println("MapViewer [Registered Version]");
                bRegistered = true;
                L_SerialNumber.setVisible(false);
                TF_SerialNumber.setVisible(false);
                B_Register.setVisible(false);
            }
        }
        else
        {
            long g_SerialNumber1 = 0;
            long c_nMapViewer_Key = 55555555;
            g_SerialNumber1 = lib_core32._LoadSerialNumber();
            System.out.println("g_SerialNumber1: "+g_SerialNumber1);
            System.out.println("EncodeSystem_ID: "+lib_core32._EncodeSystem_ID(lib_core32._System_ID(),c_nMapViewer_Key));
            if(g_SerialNumber1 != lib_core32._EncodeSystem_ID(lib_core32._System_ID(),c_nMapViewer_Key))
            {
                System.out.println("MapViewer [Demo Version]");
                System.exit(0);
            }
            else
            {
                System.out.println("MapViewer [Registered Version]");
                bRegistered = true;
                L_SerialNumber.setVisible(false);
                TF_SerialNumber.setVisible(false);
                B_Register.setVisible(false);
            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        L_SystemID = new javax.swing.JLabel();
        TF_SystemID = new javax.swing.JTextField();
        L_SerialNumber = new javax.swing.JLabel();
        TF_SerialNumber = new javax.swing.JTextField();
        B_Register = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/Splash.png"))); // NOI18N
        jLabel1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel1MouseClicked(evt);
            }
        });

        jLabel2.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel2MouseClicked(evt);
            }
        });

        L_SystemID.setText("System ID");

        L_SerialNumber.setText("Serial Number");

        B_Register.setText("Register");
        B_Register.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                B_RegisterActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(L_SerialNumber)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(TF_SerialNumber, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(B_Register)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(L_SystemID)
                        .addGap(21, 21, 21)
                        .addComponent(TF_SystemID, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(171, 171, 171))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(L_SystemID)
                    .addComponent(TF_SystemID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(B_Register)
                    .addComponent(L_SerialNumber)
                    .addComponent(TF_SerialNumber, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jLabel1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel1MouseClicked
//        final URI uri;
//        try {
//            uri = new URI("https://sites.google.com/view/oghab-mapviewer/home");
//            open(uri);
//        } catch (URISyntaxException ex) {
//            Logger.getLogger(AboutFrame.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        this.setVisible(false);
    }//GEN-LAST:event_jLabel1MouseClicked

    private void jLabel2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel2MouseClicked
//        final URI uri;
//        try {
//            uri = new URI("http://www.oti.sy/");
//            open(uri);
//        } catch (URISyntaxException ex) {
//            Logger.getLogger(AboutFrame.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        this.setVisible(false);
    }//GEN-LAST:event_jLabel2MouseClicked

    private void B_RegisterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_B_RegisterActionPerformed
        if(jvm_arch.contains("64"))
        {
            int sn = Integer.parseInt(TF_SerialNumber.getText());
            lib_core64.SaveSerialNumber(sn);
        }
        else
        {
            long sn = Long.parseLong(TF_SerialNumber.getText());
            lib_core32._SaveSerialNumber(sn);
        }
    }//GEN-LAST:event_B_RegisterActionPerformed

    private static void open(URI uri) {
      if (Desktop.isDesktopSupported()) {
        try {
          Desktop.getDesktop().browse(uri);
        } catch (IOException e) { /* TODO: error handling */ }
      } else { /* TODO: error handling */ }
    }

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
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(AboutFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(AboutFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(AboutFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(AboutFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                AboutFrame aboutFrame = new AboutFrame();

                // Center the application on the screen.
                Dimension prefSize = aboutFrame.getPreferredSize();
                Dimension parentSize;
                java.awt.Point parentLocation = new java.awt.Point(0, 0);
                parentSize = Toolkit.getDefaultToolkit().getScreenSize();
                int x = parentLocation.x + (parentSize.width - prefSize.width) / 2;
                int y = parentLocation.y + (parentSize.height - prefSize.height) / 2;
                aboutFrame.setLocation(x, y);

                aboutFrame.setVisible(true);
                aboutFrame.pack();
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton B_Register;
    private javax.swing.JLabel L_SerialNumber;
    private javax.swing.JLabel L_SystemID;
    private javax.swing.JTextField TF_SerialNumber;
    private javax.swing.JTextField TF_SystemID;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables
}
