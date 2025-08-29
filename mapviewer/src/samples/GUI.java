/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package samples;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingWorker;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;
import oghab.mapviewer.MainFrame;

/**
 *
 * @author AZUS
 */
public class GUI extends JFrame {

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    new GUI().setVisible(true);
                }
                catch(Exception ex)
                {
                    
                }
            }
        });
    }

    private final JButton button;
    private final JTable table;
    private final DefaultTableModel tableModel = new DefaultTableModel();

    public GUI() throws HeadlessException {

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        button = new JButton("Load Data");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() throws Exception {
                        loadData();
                        return null;
                    }
                }.execute();
            }
        });
        add(button, BorderLayout.PAGE_START);

        setSize(640, 480);
    }

    private void loadData() {
//        LOG.info("START loadData method");

        button.setEnabled(false);
        
//        try (Connection conn = DriverManager.getConnection(url, usr, pwd);
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:"+Paths.get("D:/Ali/WorldWind/MapViewer/data/db/places.db").toString());
                Statement stmt = conn.createStatement()) {

            ResultSet rs = stmt.executeQuery("select * from places");
            ResultSetMetaData metaData = rs.getMetaData();

            // Names of columns
            Vector<String> columnNames = new Vector<String>();
            int columnCount = metaData.getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                columnNames.add(metaData.getColumnName(i));
            }

            // Data of the table
            Vector<Vector<Object>> data = new Vector<Vector<Object>>();
            while (rs.next()) {
                Vector<Object> vector = new Vector<Object>();
                for (int i = 1; i <= columnCount; i++) {
                    vector.add(rs.getObject(i));
                }
                data.add(vector);
            }

            tableModel.setDataVector(data, columnNames);
        } catch (Exception e) {
            System.out.println(e);
//            LOG.log(Level.SEVERE, "Exception in Load Data", e);
        }
        button.setEnabled(true);

//        LOG.info("END loadData method");
    }

}
