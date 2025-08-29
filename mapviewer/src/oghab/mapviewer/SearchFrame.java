/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package oghab.mapviewer;

import gov.nasa.worldwind.geom.Position;
import java.awt.ComponentOrientation;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ResourceBundle;
import java.util.Vector;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListSelectionModel;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import static oghab.mapviewer.MainFrame.CURR_LANG;
import static oghab.mapviewer.MainFrame.strPlacesPath;

/**
 *
 * @author AZUS
 */
public class SearchFrame extends javax.swing.JFrame {
    private final DefaultTableModel tableModel = new DefaultTableModel();
    static public SearchFrame frame = null;

    ResourceBundle bundle = null;
    public void set_language(String lang)
    {
        bundle = MainFrame.getLanguageBundle(this, lang);
        MainFrame.bUpdateEdit = false;
        this.setTitle(bundle.getString("F_Search"));

//        T_Results.applyComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);

        B_Search.setText(bundle.getString("B_Search"));
        L_Name.setText(bundle.getString("L_Name"));
        CB_AutoSearch.setText(bundle.getString("CB_AutoSearch"));
        CB_AutoGoto.setText(bundle.getString("CB_AutoGoto"));
        
        MainFrame.bUpdateEdit = true;
    }

    /**
     * Creates new form SearchFrame
     */
    public SearchFrame() {
        frame = this;
        initComponents();
        this.set_language(CURR_LANG);
        
        T_Results.setModel(tableModel);
//        T_Results.setRowSelectionAllowed(false);
//        T_Results.setColumnSelectionAllowed(false);
//        T_Results.setCellSelectionEnabled(false);

//        T_Results.setSelectionModel(new ForcedListSelectionModel());
        
        T_Results.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
//        T_Results.setRowSelectionInterval(0, 0);

        T_Results.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
            public void valueChanged(ListSelectionEvent event) {
//                    System.out.println(T_Results.getValueAt(T_Results.getSelectedRow(), T_Results.getSelectedColumn()).toString());
                if(CB_AutoGoto.isSelected())
                {
                    if(T_Results.getSelectedRow() >= 0)
                    {
                        double lon = Double.parseDouble(T_Results.getValueAt(T_Results.getSelectedRow(), 0).toString());
                        double lat = Double.parseDouble(T_Results.getValueAt(T_Results.getSelectedRow(), 1).toString());
                        MainFrame.goTo(MainFrame.frame.wwd, Position.fromDegrees(lat, lon));
                    }
                }
            }
        });

        TF_Name.getDocument().addDocumentListener(new DocumentListener()
        {
            public void changedUpdate(DocumentEvent e) {
                warn();
            }
            public void removeUpdate(DocumentEvent e) {
                warn();
            }
            public void insertUpdate(DocumentEvent e) {
                warn();
            }

            public void warn()
            {
                if(CB_AutoSearch.isSelected())
                {
                    if (!TF_Name.getText().isEmpty())
                    {
                        B_SearchActionPerformed(null);
                    }
                }
          }
        });

        ImageIcon icon = new ImageIcon(getClass().getResource("/res/mapviewer_icon.png"));  
        this.setIconImage(icon.getImage());  

        this.pack();
    }

    public class ForcedListSelectionModel extends DefaultListSelectionModel {

        public ForcedListSelectionModel () {
            setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        }

        @Override
        public void clearSelection() {
        }

        @Override
        public void removeSelectionInterval(int index0, int index1) {
        }

    }

    private void loadData() {
//        LOG.info("START loadData method");

        B_Search.setEnabled(false);
        tableModel.setRowCount(0);
        
//        try (Connection conn = DriverManager.getConnection(url, usr, pwd);
//        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:"+Paths.get("D:/Ali/WorldWind/MapViewer/data/db/places.db").toString());
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:"+strPlacesPath);
                Statement stmt = conn.createStatement()) {

            ResultSet rs = stmt.executeQuery("SELECT LONG,LAT,FULL_NAME_RO FROM places WHERE (FULL_NAME_RO LIKE '%"+TF_Name.getText()+"%')");
            ResultSetMetaData metaData = rs.getMetaData();

            // Names of columns
            Vector<String> columnNames = new Vector<String>();
            int columnCount = metaData.getColumnCount();
//            for (int i = 1; i <= columnCount; i++) {
//                columnNames.add(metaData.getColumnName(i));
//            }
            columnNames.add(bundle.getString("L_Longitude"));
            columnNames.add(bundle.getString("L_Latitude"));
            columnNames.add(bundle.getString("L_Name"));

            // Data of the table
            Vector<Vector<Object>> data = new Vector<Vector<Object>>();
            while (rs.next()) {
                Vector<Object> vector = new Vector<Object>();
                for (int i = 1; i <= columnCount; i++) {
                    vector.add(rs.getObject(i));
                }
                data.add(vector);
            }
            rs.close();
            stmt.close();
            conn.close();

            tableModel.setDataVector(data, columnNames);

            DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
            centerRenderer.setHorizontalAlignment(JLabel.CENTER);
//            T_Results.setDefaultRenderer(String.class, centerRenderer);
            T_Results.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
            T_Results.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
            T_Results.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        } catch (Exception e) {
            System.out.println(e);
//            LOG.log(Level.SEVERE, "Exception in Load Data", e);
        }
        B_Search.setEnabled(true);

//        LOG.info("END loadData method");
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        L_Name = new javax.swing.JLabel();
        TF_Name = new javax.swing.JTextField();
        B_Search = new javax.swing.JButton();
        CB_AutoSearch = new javax.swing.JCheckBox();
        CB_AutoGoto = new javax.swing.JCheckBox();
        jScrollPane1 = new javax.swing.JScrollPane();
        T_Results = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        L_Name.setText("Name");

        TF_Name.setText("ناحية شين");

        B_Search.setText("Search");
        B_Search.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                B_SearchActionPerformed(evt);
            }
        });

        CB_AutoSearch.setSelected(true);
        CB_AutoSearch.setText("Auto Search");

        CB_AutoGoto.setSelected(true);
        CB_AutoGoto.setText("Auto Goto");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(L_Name)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(TF_Name, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(B_Search)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(CB_AutoSearch)
                .addGap(10, 10, 10)
                .addComponent(CB_AutoGoto)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(L_Name)
                    .addComponent(TF_Name, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(B_Search)
                    .addComponent(CB_AutoSearch)
                    .addComponent(CB_AutoGoto))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        T_Results.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(T_Results);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 519, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 323, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void B_SearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_B_SearchActionPerformed
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                loadData();
                return null;
            }
        }.execute();
    }//GEN-LAST:event_B_SearchActionPerformed

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
            java.util.logging.Logger.getLogger(SearchFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(SearchFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(SearchFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(SearchFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new SearchFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JButton B_Search;
    public javax.swing.JCheckBox CB_AutoGoto;
    public javax.swing.JCheckBox CB_AutoSearch;
    public javax.swing.JLabel L_Name;
    public javax.swing.JTextField TF_Name;
    public javax.swing.JTable T_Results;
    public javax.swing.JPanel jPanel1;
    public javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
}
