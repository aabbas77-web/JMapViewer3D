/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package oghab.mapviewer;

import gov.nasa.worldwind.util.WWIO;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import static oghab.mapviewer.MainFrame.CURR_LANG;
import static oghab.mapviewer.MainFrame.strDataPath;
import org.apache.commons.io.FilenameUtils;

/**
 *
 * @author AZUS
 */
public class TacticalSymbolsFrame extends javax.swing.JFrame {
    public boolean is_ok = false;
    DefaultListModel<ListItem> model = new DefaultListModel<>();
    static public TacticalSymbolsFrame frame = null;

    public void set_language(String lang)
    {
        ResourceBundle bundle = MainFrame.getLanguageBundle(this, lang);
        MainFrame.bUpdateEdit = false;
        this.setTitle(bundle.getString("F_Icons"));

        B_Ok.setText(bundle.getString("B_Ok"));
        B_Cancel.setText(bundle.getString("B_Cancel"));
        
        ((TitledBorder)P_Icon.getBorder()).setTitle(bundle.getString("P_Icon"));
        L_IconColor.setText(bundle.getString("L_Color"));
        L_IconScale.setText(bundle.getString("L_Scale"));
        L_IconOpacity.setText(bundle.getString("L_Opacity"));
        L_SymbolID.setText(bundle.getString("L_SymbolID"));
        B_AddCustomIcon.setText(bundle.getString("B_AddCustomIcon"));
        B_NoIcon.setText(bundle.getString("B_NoIcon"));

        MainFrame.bUpdateEdit = true;
    }

    /**
     * Creates new form IconsFrame
     */
    public TacticalSymbolsFrame() {
        frame = this;
        initComponents();
        this.set_language(CURR_LANG);
        is_ok = false;

        ImageIcon icon = new ImageIcon(getClass().getResource("/res/mapviewer_icon.png"));  
        this.setIconImage(icon.getImage());  
        
        load_icons();
        L_Icons.setModel(model);
        
        L_Icons.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        L_Icons.setVisibleRowCount(0);
        L_Icons.setCellRenderer(new ListRenderer());
        L_Icons.addListSelectionListener(new SelectionHandler());

        this.pack();
    }

    class ListItem {
      public final ImageIcon icon;
      public final String path;

      protected ListItem(String iconfile) {
        this.icon = new ImageIcon(iconfile);
        this.path = iconfile;
      }
    }

    private void load_icons()
    {
        try {
            listPngFiles(Paths.get(strDataPath, "symbols", "milstd2525-symbols", "icons", "war").toString());
        } catch (IOException ex) {
            Logger.getLogger(TacticalSymbolsFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void listPngFiles(String startDir) throws IOException
    {
        File dir = new File(startDir);
        File[] files = dir.listFiles();
        Arrays.sort(files, new Comparator<File>(){
            public int compare(File f1, File f2)
            {
                return Long.valueOf(f1.lastModified()).compareTo(f2.lastModified());
            } });        
        if (files != null && files.length > 0)
        {
            for (File file : files)
            {
                if(file.getName().toLowerCase().startsWith("!"))    continue;
                // Check if the file is a directory
                if (file.isDirectory())
                {
                    // We will not print the directory name, just use it as a new
                    // starting point to list files from
                    listPngFiles(file.getAbsolutePath());
                }
                else
                {
                    if(file.getName().toLowerCase().endsWith(".png"))
                    {
                        String strPngFile = startDir + "\\"+ file.getName();
//                        String strExt = "."+file.getParentFile().getName();
//                        System.out.println(strPngFile + " (size in bytes: " + file.length()+"), ext["+strExt+"]");
//                        ImageIcon icon = new ImageIcon(strPngFile);
                        ListItem item = new ListItem(strPngFile);
                        model.addElement(item);
                    }
                }
            }
        }
    }

    private class SelectionHandler implements ListSelectionListener {

        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (!e.getValueIsAdjusting()) {
                ListItem item = L_Icons.getSelectedValue();
                L_Icon.setIcon(item.icon);
                TF_SymbolID.setText(FilenameUtils.removeExtension(WWIO.getFilename(item.path)));
                PropertiesFrame.frame.TF_IconPath.setText(item.path);
                MainFrame.frame.set_object_properties(MainFrame.currObject);
            }
        }
    }

    private class ListRenderer extends DefaultListCellRenderer {
        private static final int N = 5;

        public ListRenderer() {
            this.setBorder(BorderFactory.createLineBorder(Color.red));
        }

        @Override
        public Component getListCellRendererComponent(JList list, Object
                value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(
                list, value, index, isSelected, cellHasFocus);
            label.setBorder(BorderFactory.createEmptyBorder(N, N, N, N));
            ListItem item = (ListItem)value;
            label.setText("");
            label.setIcon(item.icon);
            label.setHorizontalTextPosition(JLabel.CENTER);
            label.setVerticalTextPosition(JLabel.BOTTOM);
            return label;
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

        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jColorChooser1 = new javax.swing.JColorChooser();
        P_Icon = new javax.swing.JPanel();
        L_IconColor = new javax.swing.JLabel();
        P_IconColor = new javax.swing.JPanel();
        L_IconScale = new javax.swing.JLabel();
        S_IconScale = new javax.swing.JSpinner();
        L_IconOpacity = new javax.swing.JLabel();
        S_IconOpacity = new javax.swing.JSpinner();
        L_SymbolID = new javax.swing.JLabel();
        TF_SymbolID = new javax.swing.JTextField();
        L_Icon = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        B_Ok = new javax.swing.JButton();
        B_Cancel = new javax.swing.JButton();
        B_AddCustomIcon = new javax.swing.JButton();
        B_NoIcon = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        L_Icons = new javax.swing.JList<>();

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        P_Icon.setBorder(javax.swing.BorderFactory.createTitledBorder("Icon"));

        L_IconColor.setText("Color");

        P_IconColor.setBackground(new java.awt.Color(255, 0, 0));
        P_IconColor.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                P_IconColorMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout P_IconColorLayout = new javax.swing.GroupLayout(P_IconColor);
        P_IconColor.setLayout(P_IconColorLayout);
        P_IconColorLayout.setHorizontalGroup(
            P_IconColorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 24, Short.MAX_VALUE)
        );
        P_IconColorLayout.setVerticalGroup(
            P_IconColorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        L_IconScale.setText("Scale");

        S_IconScale.setModel(new javax.swing.SpinnerNumberModel(1.0d, 1.0d, 10.0d, 0.1d));
        S_IconScale.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                S_IconScaleStateChanged(evt);
            }
        });

        L_IconOpacity.setText("Opacity");

        S_IconOpacity.setModel(new javax.swing.SpinnerNumberModel(100, 0, 100, 1));
        S_IconOpacity.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                S_IconOpacityStateChanged(evt);
            }
        });

        L_SymbolID.setText("Symbol ID");

        L_Icon.setBackground(new java.awt.Color(204, 255, 204));
        L_Icon.setForeground(new java.awt.Color(0, 204, 255));
        L_Icon.setToolTipText("");

        javax.swing.GroupLayout P_IconLayout = new javax.swing.GroupLayout(P_Icon);
        P_Icon.setLayout(P_IconLayout);
        P_IconLayout.setHorizontalGroup(
            P_IconLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(P_IconLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(P_IconLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(P_IconLayout.createSequentialGroup()
                        .addComponent(L_IconColor)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(P_IconColor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(L_IconScale)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(S_IconScale)
                        .addGap(18, 18, 18)
                        .addComponent(L_IconOpacity)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(S_IconOpacity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(P_IconLayout.createSequentialGroup()
                        .addComponent(L_SymbolID)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(TF_SymbolID)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(L_Icon, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        P_IconLayout.setVerticalGroup(
            P_IconLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(P_IconLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(P_IconLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(P_IconColor, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(L_IconColor, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(P_IconLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(L_IconOpacity, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(S_IconOpacity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(P_IconLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(L_IconScale)
                        .addComponent(S_IconScale, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(P_IconLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(L_SymbolID, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(TF_SymbolID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(P_IconLayout.createSequentialGroup()
                .addComponent(L_Icon, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        B_Ok.setText("Ok");
        B_Ok.setPreferredSize(new java.awt.Dimension(64, 23));
        B_Ok.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                B_OkActionPerformed(evt);
            }
        });

        B_Cancel.setText("Cancel");
        B_Cancel.setPreferredSize(new java.awt.Dimension(64, 23));
        B_Cancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                B_CancelActionPerformed(evt);
            }
        });

        B_AddCustomIcon.setText("Add Custom Icon...");
        B_AddCustomIcon.setPreferredSize(new java.awt.Dimension(64, 23));
        B_AddCustomIcon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                B_AddCustomIconActionPerformed(evt);
            }
        });

        B_NoIcon.setText("No Icon");
        B_NoIcon.setPreferredSize(new java.awt.Dimension(64, 23));
        B_NoIcon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                B_NoIconActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(B_AddCustomIcon, javax.swing.GroupLayout.DEFAULT_SIZE, 161, Short.MAX_VALUE)
                .addGap(2, 2, 2)
                .addComponent(B_NoIcon, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(B_Ok, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addComponent(B_Cancel, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(B_Ok, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(B_Cancel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(B_AddCustomIcon, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(B_NoIcon, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jScrollPane3.setViewportView(L_Icons);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane3)
                    .addComponent(P_Icon, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(P_Icon, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 340, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void P_IconColorMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_P_IconColorMouseClicked
        P_IconColor.setBackground(jColorChooser1.showDialog(this, "Select a color", P_IconColor.getBackground()));
        PropertiesFrame.frame.P_IconColor.setBackground(P_IconColor.getBackground());
        MainFrame.frame.set_object_properties(MainFrame.currObject);
    }//GEN-LAST:event_P_IconColorMouseClicked

    private void B_OkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_B_OkActionPerformed
        is_ok = true;
        this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }//GEN-LAST:event_B_OkActionPerformed

    private void B_CancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_B_CancelActionPerformed
        is_ok = false;
        this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }//GEN-LAST:event_B_CancelActionPerformed

    private void B_AddCustomIconActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_B_AddCustomIconActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_B_AddCustomIconActionPerformed

    private void B_NoIconActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_B_NoIconActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_B_NoIconActionPerformed

    private void S_IconScaleStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_S_IconScaleStateChanged
        PropertiesFrame.frame.S_IconScale.setValue(S_IconScale.getValue());
        MainFrame.frame.set_object_properties(MainFrame.currObject);
    }//GEN-LAST:event_S_IconScaleStateChanged

    private void S_IconOpacityStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_S_IconOpacityStateChanged
        PropertiesFrame.frame.S_IconOpacity.setValue(S_IconOpacity.getValue());
        MainFrame.frame.set_object_properties(MainFrame.currObject);
    }//GEN-LAST:event_S_IconOpacityStateChanged

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
            java.util.logging.Logger.getLogger(TacticalSymbolsFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(TacticalSymbolsFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(TacticalSymbolsFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TacticalSymbolsFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new TacticalSymbolsFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JButton B_AddCustomIcon;
    public javax.swing.JButton B_Cancel;
    public javax.swing.JButton B_NoIcon;
    public javax.swing.JButton B_Ok;
    public javax.swing.JLabel L_Icon;
    public javax.swing.JLabel L_IconColor;
    public javax.swing.JLabel L_IconOpacity;
    public javax.swing.JLabel L_IconScale;
    public javax.swing.JList<ListItem> L_Icons;
    public javax.swing.JLabel L_SymbolID;
    public javax.swing.JPanel P_Icon;
    public javax.swing.JPanel P_IconColor;
    public javax.swing.JSpinner S_IconOpacity;
    public javax.swing.JSpinner S_IconScale;
    public javax.swing.JTextField TF_SymbolID;
    public javax.swing.JColorChooser jColorChooser1;
    public javax.swing.JPanel jPanel2;
    public javax.swing.JScrollPane jScrollPane1;
    public javax.swing.JScrollPane jScrollPane3;
    public javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables
}
