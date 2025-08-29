/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package oghab.mapviewer;

import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.util.ResourceBundle;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import static oghab.mapviewer.MainFrame.CURR_LANG;

/**
 *
 * @author AZUS
 */
public class PropertiesFrame extends javax.swing.JFrame {
    public boolean is_ok = false;
    public boolean is_tactical_symbols = false;
    public double alt = 0.0;
    public double perimeter = 0.0;
    public double area = 0.0;
    static public PropertiesFrame frame = null;
    public double lon = 0.0;
    public double lat = 0.0;

    public void show_pos(double lon, double lat)
    {
        this.lon = lon;
        this.lat = lat;
        
        boolean bUpdateEdit_Prev = MainFrame.bUpdateEdit;
        MainFrame.bUpdateEdit = false;

        if(index_to_exclude != 0)
        {
            TF_Latitude.setText(String.format("%.6f", lat));
            TF_Longitude.setText(String.format("%.6f", lon));
            TF_Latitude.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
            TF_Longitude.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        }

        if(index_to_exclude != 1)
        {
            String strLongitude = Location.convert(lon, Location.FORMAT_SECONDS);
            String strLatitude = Location.convert(lat, Location.FORMAT_SECONDS);
            TF_DMS_Latitude.setText(strLatitude);
            TF_DMS_Longitude.setText(strLongitude);
            TF_DMS_Latitude.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
            TF_DMS_Longitude.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        }

        if(index_to_exclude != 2)
        {
            Deg2UTM deg2UTM = new Deg2UTM(lat, lon);
            TF_UTM_X.setText(String.format("%.2f", deg2UTM.Easting));
            TF_UTM_Y.setText(String.format("%.2f", deg2UTM.Northing));
            TF_UTM_Zone.setText(Integer.toString(deg2UTM.Zone)+deg2UTM.Letter);
            TF_UTM_X.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
            TF_UTM_Y.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
            TF_UTM_Zone.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        }
        
        if(index_to_exclude != 3)
        {
            String[] res = mv_stm.Convert_Geo_To_XY_String(lon, lat);
            TF_STM_X.setText(res[0]);
            TF_STM_Y.setText(res[1]);
            TF_STM_Zone.setText(res[2]);
            TF_STM_X.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
            TF_STM_Y.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
            TF_STM_Zone.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        }

        MainFrame.bUpdateEdit = bUpdateEdit_Prev;
    }

    public int index_to_exclude = -1;
    public void update_pos()
    {
        if(index_to_exclude == 0)
        {
            try
            {
                lon = Double.parseDouble(TF_Longitude.getText());
                lat = Double.parseDouble(TF_Latitude.getText());
            }
            catch(Exception ex)
            {
            }
        }

        if(index_to_exclude == 1)
        {
            try
            {
                lon = Location.convert(TF_DMS_Longitude.getText());
                lat = Location.convert(TF_DMS_Latitude.getText());
            }
            catch(Exception ex)
            {
            }
        }

        if(index_to_exclude == 2)
        {
            try
            {
                double X = Double.parseDouble(TF_UTM_X.getText());
                double Y = Double.parseDouble(TF_UTM_Y.getText());
                String strZone = TF_UTM_Zone.getText();
                char C = strZone.charAt(strZone.length() - 1);
                strZone = strZone.replace(C, ' ').trim();
                int zone = Integer.parseInt(strZone);

                UTM2Deg utm2Deg = new UTM2Deg(X, Y, zone, C);
                lon = utm2Deg.longitude;
                lat = utm2Deg.latitude;
            }
            catch(Exception ex)
            {
            }
        }
        
        if(index_to_exclude == 3)
        {
            try
            {
                double X = Double.parseDouble(TF_STM_X.getText());
                double Y = Double.parseDouble(TF_STM_Y.getText());
                int zone = Integer.parseInt(TF_STM_Zone.getText());

                double[] res = mv_stm.Convert_XY_To_Geo(X, Y, zone);
                lon = res[0];
                lat = res[1];
            }
            catch(Exception ex)
            {
            }
        }
        
        show_pos(lon, lat);
    }
    
    public void set_language(String lang)
    {
        ResourceBundle bundle = MainFrame.getLanguageBundle(this, lang);
        MainFrame.bUpdateEdit = false;
        this.setTitle(bundle.getString("F_Properties"));

        B_Ok.setText(bundle.getString("B_Ok"));
        B_Cancel.setText(bundle.getString("B_Cancel"));

        L_Name.setText(bundle.getString("L_Name"));
        L_Latitude.setText(bundle.getString("L_Latitude"));
        L_Longitude.setText(bundle.getString("L_Longitude"));
        L_IconPath.setText(bundle.getString("L_IconPath"));
        
        TP_Properties.setTitleAt(0, bundle.getString("TP_Description"));
        
        TP_Properties.setTitleAt(1, bundle.getString("TP_Style_Color"));
        ((TitledBorder)P_Lines.getBorder()).setTitle(bundle.getString("P_Lines"));
        L_LinesColor.setText(bundle.getString("L_Color"));
        L_Width.setText(bundle.getString("L_Width"));
        L_LinesOpacity.setText(bundle.getString("L_Opacity"));
        
        ((TitledBorder)P_Area.getBorder()).setTitle(bundle.getString("P_Area"));
        L_AreaColor.setText(bundle.getString("L_Color"));
        String labels1[] = {bundle.getString("CB_Filled"), bundle.getString("CB_Outlined"), bundle.getString("CB_Filled_Outlined")};
        final DefaultComboBoxModel model1 = new DefaultComboBoxModel(labels1);
        CB_AreaType.setModel(model1);
        CB_AreaType.setMaximumRowCount(3);
        CB_AreaType.setEditable(false);
//        CB_AreaType.setSelectedIndex(2);
        L_AreaOpacity.setText(bundle.getString("L_Opacity"));
                
        ((TitledBorder)P_Label.getBorder()).setTitle(bundle.getString("P_Label"));
        L_LabelColor.setText(bundle.getString("L_Color"));
        L_LabelScale.setText(bundle.getString("L_Scale"));
        L_LabelOpacity.setText(bundle.getString("L_Opacity"));
        
        ((TitledBorder)P_Icon.getBorder()).setTitle(bundle.getString("P_Icon"));
        L_IconColor.setText(bundle.getString("L_Color"));
        L_IconScale.setText(bundle.getString("L_Scale"));
        L_IconOpacity.setText(bundle.getString("L_Opacity"));
        
        TP_Properties.setTitleAt(2, bundle.getString("TP_Altitude"));
        L_Altitude.setText(bundle.getString("L_Altitude"));
        String labels2[] = {bundle.getString("CB_Absolute"), bundle.getString("CB_Clamped_to_ground"), bundle.getString("CB_Relative_to_ground")};
        final DefaultComboBoxModel model2 = new DefaultComboBoxModel(labels2);
        CB_AltitudeMode.setModel(model2);
        CB_AltitudeMode.setMaximumRowCount(3);
        CB_AltitudeMode.setEditable(false);
//        CB_AltitudeMode.setSelectedIndex(0);
        L_Ground.setText(bundle.getString("L_Ground"));
        L_Space.setText(bundle.getString("L_Space"));
        CB_Extend.setText(bundle.getString("CB_Extend"));
        
        TP_Properties.setTitleAt(3, bundle.getString("TP_Measurements"));
        T_Perimeter.setText(bundle.getString("T_Perimeter"));
        String labels3[] = {bundle.getString("CB_Centimeters"), bundle.getString("CB_Meters"), bundle.getString("CB_Kilometers")};
        final DefaultComboBoxModel model3 = new DefaultComboBoxModel(labels3);
        CB_PerimeterUnits.setModel(model3);
        CB_PerimeterUnits.setMaximumRowCount(3);
        CB_PerimeterUnits.setEditable(false);
        CB_PerimeterUnits.setSelectedIndex(1);
        T_Area.setText(bundle.getString("T_Area"));
        String labels4[] = {bundle.getString("CB_Square_Meters"), bundle.getString("CB_Square_Kilometers"), bundle.getString("CB_Hectars")};
        final DefaultComboBoxModel model4 = new DefaultComboBoxModel(labels4);
        CB_AreaUnits.setModel(model4);
        CB_AreaUnits.setMaximumRowCount(3);
        CB_AreaUnits.setEditable(false);
        CB_AreaUnits.setSelectedIndex(0);

        TP_Properties.setTitleAt(4, bundle.getString("TP_Coordinates"));
        ((TitledBorder)P_DMS.getBorder()).setTitle(bundle.getString("P_DMS"));
        L_DMS_Latitude.setText(bundle.getString("L_DMS_Latitude"));
        L_DMS_Longitude.setText(bundle.getString("L_DMS_Longitude"));

        ((TitledBorder)P_UTM.getBorder()).setTitle(bundle.getString("P_UTM"));
        L_UTM_X.setText(bundle.getString("L_UTM_X"));
        L_UTM_Y.setText(bundle.getString("L_UTM_Y"));
        L_UTM_Zone.setText(bundle.getString("L_UTM_Zone"));
        L_STM_X.setText(bundle.getString("L_STM_X"));
        L_STM_Y.setText(bundle.getString("L_STM_Y"));
        L_STM_Zone.setText(bundle.getString("L_STM_Zone"));
        
        MainFrame.bUpdateEdit = true;
    }
    
    private void set_events()
    {
        // 0 - Decimal
        TF_Longitude.getDocument().addDocumentListener(new DocumentListener()
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
                if(MainFrame.bUpdateEdit)
                {
                    index_to_exclude = 0;
                    update_pos();
                    MainFrame.frame.set_object_properties(MainFrame.currObject);
                }
            }
        });

        TF_Latitude.getDocument().addDocumentListener(new DocumentListener()
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
                if(MainFrame.bUpdateEdit)
                {
                    index_to_exclude = 0;
                    update_pos();
                    MainFrame.frame.set_object_properties(MainFrame.currObject);
                }
            }
        });

        // 1 - DMS
        TF_DMS_Longitude.getDocument().addDocumentListener(new DocumentListener()
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
                if(MainFrame.bUpdateEdit)
                {
                    index_to_exclude = 1;
                    update_pos();
                    MainFrame.frame.set_object_properties(MainFrame.currObject);
                }
            }
        });

        TF_DMS_Latitude.getDocument().addDocumentListener(new DocumentListener()
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
                if(MainFrame.bUpdateEdit)
                {
                    index_to_exclude = 1;
                    update_pos();
                    MainFrame.frame.set_object_properties(MainFrame.currObject);
                }
            }
        });

        // 2 - UTM
        TF_UTM_X.getDocument().addDocumentListener(new DocumentListener()
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
                if(MainFrame.bUpdateEdit)
                {
                    index_to_exclude = 2;
                    update_pos();
                    MainFrame.frame.set_object_properties(MainFrame.currObject);
                }
            }
        });

        TF_UTM_Y.getDocument().addDocumentListener(new DocumentListener()
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
                if(MainFrame.bUpdateEdit)
                {
                    index_to_exclude = 2;
                    update_pos();
                    MainFrame.frame.set_object_properties(MainFrame.currObject);
                }
            }
        });

        TF_UTM_Zone.getDocument().addDocumentListener(new DocumentListener()
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
                if(MainFrame.bUpdateEdit)
                {
                    index_to_exclude = 2;
                    update_pos();
                    MainFrame.frame.set_object_properties(MainFrame.currObject);
                }
            }
        });

        // 3 - STM
        TF_STM_X.getDocument().addDocumentListener(new DocumentListener()
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
                if(MainFrame.bUpdateEdit)
                {
                    index_to_exclude = 3;
                    update_pos();
                    MainFrame.frame.set_object_properties(MainFrame.currObject);
                }
            }
        });

        TF_STM_Y.getDocument().addDocumentListener(new DocumentListener()
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
                if(MainFrame.bUpdateEdit)
                {
                    index_to_exclude = 3;
                    update_pos();
                    MainFrame.frame.set_object_properties(MainFrame.currObject);
                }
            }
        });

        TF_STM_Zone.getDocument().addDocumentListener(new DocumentListener()
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
                if(MainFrame.bUpdateEdit)
                {
                    index_to_exclude = 3;
                    update_pos();
                    MainFrame.frame.set_object_properties(MainFrame.currObject);
                }
            }
        });

    }
    
    /**
     * Creates new form PropertiesFrame
     */
    public PropertiesFrame() {
        frame = this;
        initComponents();
        this.set_language(CURR_LANG);
        set_events();
        is_ok = false;
        
        ImageIcon icon = new ImageIcon(getClass().getResource("/res/mapviewer_icon.png"));  
        this.setIconImage(icon.getImage());  

        this.pack();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jColorChooser1 = new javax.swing.JColorChooser();
        jPanel2 = new javax.swing.JPanel();
        B_Ok = new javax.swing.JButton();
        B_Cancel = new javax.swing.JButton();
        TP_Properties = new javax.swing.JTabbedPane();
        P_Description = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        TA_Description = new javax.swing.JTextArea();
        jPanel4 = new javax.swing.JPanel();
        P_Lines = new javax.swing.JPanel();
        L_LinesColor = new javax.swing.JLabel();
        P_LinesColor = new javax.swing.JPanel();
        L_Width = new javax.swing.JLabel();
        S_LinesWidth = new javax.swing.JSpinner();
        L_LinesOpacity = new javax.swing.JLabel();
        S_LinesOpacity = new javax.swing.JSpinner();
        P_Area = new javax.swing.JPanel();
        L_AreaColor = new javax.swing.JLabel();
        P_AreaColor = new javax.swing.JPanel();
        CB_AreaType = new javax.swing.JComboBox<>();
        L_AreaOpacity = new javax.swing.JLabel();
        S_AreaOpacity = new javax.swing.JSpinner();
        P_Label = new javax.swing.JPanel();
        L_LabelColor = new javax.swing.JLabel();
        P_LabelColor = new javax.swing.JPanel();
        L_LabelScale = new javax.swing.JLabel();
        S_LabelScale = new javax.swing.JSpinner();
        L_LabelOpacity = new javax.swing.JLabel();
        S_LabelOpacity = new javax.swing.JSpinner();
        P_Icon = new javax.swing.JPanel();
        L_IconColor = new javax.swing.JLabel();
        P_IconColor = new javax.swing.JPanel();
        L_IconScale = new javax.swing.JLabel();
        S_IconScale = new javax.swing.JSpinner();
        L_IconOpacity = new javax.swing.JLabel();
        S_IconOpacity = new javax.swing.JSpinner();
        jPanel5 = new javax.swing.JPanel();
        L_Altitude = new javax.swing.JLabel();
        TF_Altitude = new javax.swing.JTextField();
        CB_AltitudeMode = new javax.swing.JComboBox<>();
        L_Ground = new javax.swing.JLabel();
        S_Altitude = new javax.swing.JSlider();
        L_Space = new javax.swing.JLabel();
        CB_Extend = new javax.swing.JCheckBox();
        jPanel1 = new javax.swing.JPanel();
        T_Perimeter = new javax.swing.JLabel();
        L_Perimeter = new javax.swing.JLabel();
        CB_PerimeterUnits = new javax.swing.JComboBox<>();
        T_Area = new javax.swing.JLabel();
        L_Area = new javax.swing.JLabel();
        CB_AreaUnits = new javax.swing.JComboBox<>();
        jPanel3 = new javax.swing.JPanel();
        P_STM = new javax.swing.JPanel();
        TF_STM_X = new javax.swing.JTextField();
        L_STM_Y = new javax.swing.JLabel();
        TF_STM_Y = new javax.swing.JTextField();
        L_STM_Zone = new javax.swing.JLabel();
        TF_STM_Zone = new javax.swing.JTextField();
        L_STM_X = new javax.swing.JLabel();
        P_DMS = new javax.swing.JPanel();
        L_DMS_Latitude = new javax.swing.JLabel();
        TF_DMS_Latitude = new javax.swing.JTextField();
        L_DMS_Longitude = new javax.swing.JLabel();
        TF_DMS_Longitude = new javax.swing.JTextField();
        P_UTM = new javax.swing.JPanel();
        TF_UTM_X = new javax.swing.JTextField();
        L_UTM_Y = new javax.swing.JLabel();
        TF_UTM_Y = new javax.swing.JTextField();
        L_UTM_Zone = new javax.swing.JLabel();
        TF_UTM_Zone = new javax.swing.JTextField();
        L_UTM_X = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        L_Name = new javax.swing.JLabel();
        TF_Name = new javax.swing.JTextField();
        B_Icons = new javax.swing.JButton();
        L_Latitude = new javax.swing.JLabel();
        TF_Latitude = new javax.swing.JTextField();
        L_Longitude = new javax.swing.JLabel();
        TF_Longitude = new javax.swing.JTextField();
        L_IconPath = new javax.swing.JLabel();
        TF_IconPath = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

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

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(B_Ok, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(B_Cancel, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(B_Ok, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(B_Cancel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        TA_Description.setColumns(20);
        TA_Description.setRows(5);
        jScrollPane1.setViewportView(TA_Description);

        javax.swing.GroupLayout P_DescriptionLayout = new javax.swing.GroupLayout(P_Description);
        P_Description.setLayout(P_DescriptionLayout);
        P_DescriptionLayout.setHorizontalGroup(
            P_DescriptionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(P_DescriptionLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 478, Short.MAX_VALUE)
                .addContainerGap())
        );
        P_DescriptionLayout.setVerticalGroup(
            P_DescriptionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(P_DescriptionLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 279, Short.MAX_VALUE)
                .addContainerGap())
        );

        TP_Properties.addTab("Description", P_Description);

        P_Lines.setBorder(javax.swing.BorderFactory.createTitledBorder("Lines"));

        L_LinesColor.setText("Color");

        P_LinesColor.setBackground(new java.awt.Color(255, 0, 0));
        P_LinesColor.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                P_LinesColorMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout P_LinesColorLayout = new javax.swing.GroupLayout(P_LinesColor);
        P_LinesColor.setLayout(P_LinesColorLayout);
        P_LinesColorLayout.setHorizontalGroup(
            P_LinesColorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 24, Short.MAX_VALUE)
        );
        P_LinesColorLayout.setVerticalGroup(
            P_LinesColorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        L_Width.setText("Width");

        S_LinesWidth.setModel(new javax.swing.SpinnerNumberModel(1, 0, 10, 1));
        S_LinesWidth.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                S_LinesWidthStateChanged(evt);
            }
        });

        L_LinesOpacity.setText("Opacity");

        S_LinesOpacity.setModel(new javax.swing.SpinnerNumberModel(100, 0, 100, 1));
        S_LinesOpacity.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                S_LinesOpacityStateChanged(evt);
            }
        });

        javax.swing.GroupLayout P_LinesLayout = new javax.swing.GroupLayout(P_Lines);
        P_Lines.setLayout(P_LinesLayout);
        P_LinesLayout.setHorizontalGroup(
            P_LinesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(P_LinesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(L_LinesColor)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(P_LinesColor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(L_Width)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(S_LinesWidth, javax.swing.GroupLayout.DEFAULT_SIZE, 98, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(L_LinesOpacity)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(S_LinesOpacity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(74, 74, 74))
        );
        P_LinesLayout.setVerticalGroup(
            P_LinesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(P_LinesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(P_LinesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(P_LinesColor, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(L_LinesColor, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(P_LinesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(L_LinesOpacity, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(S_LinesOpacity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(P_LinesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(L_Width)
                        .addComponent(S_LinesWidth, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        P_Area.setBorder(javax.swing.BorderFactory.createTitledBorder("Area"));

        L_AreaColor.setText("Color");

        P_AreaColor.setBackground(new java.awt.Color(255, 0, 0));
        P_AreaColor.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                P_AreaColorMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout P_AreaColorLayout = new javax.swing.GroupLayout(P_AreaColor);
        P_AreaColor.setLayout(P_AreaColorLayout);
        P_AreaColorLayout.setHorizontalGroup(
            P_AreaColorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 24, Short.MAX_VALUE)
        );
        P_AreaColorLayout.setVerticalGroup(
            P_AreaColorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        CB_AreaType.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Filled", "Outlined", "Filled + Outlined" }));
        CB_AreaType.setSelectedIndex(2);
        CB_AreaType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CB_AreaTypeActionPerformed(evt);
            }
        });

        L_AreaOpacity.setText("Opacity");

        S_AreaOpacity.setModel(new javax.swing.SpinnerNumberModel(100, 0, 100, 1));
        S_AreaOpacity.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                S_AreaOpacityStateChanged(evt);
            }
        });

        javax.swing.GroupLayout P_AreaLayout = new javax.swing.GroupLayout(P_Area);
        P_Area.setLayout(P_AreaLayout);
        P_AreaLayout.setHorizontalGroup(
            P_AreaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(P_AreaLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(L_AreaColor)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(P_AreaColor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(CB_AreaType, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(L_AreaOpacity)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(S_AreaOpacity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        P_AreaLayout.setVerticalGroup(
            P_AreaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(P_AreaLayout.createSequentialGroup()
                .addGap(7, 7, 7)
                .addGroup(P_AreaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(P_AreaColor, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(L_AreaColor, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(P_AreaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(L_AreaOpacity, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(S_AreaOpacity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(CB_AreaType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        P_Label.setBorder(javax.swing.BorderFactory.createTitledBorder("Label"));

        L_LabelColor.setText("Color");

        P_LabelColor.setBackground(new java.awt.Color(255, 0, 0));
        P_LabelColor.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                P_LabelColorMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout P_LabelColorLayout = new javax.swing.GroupLayout(P_LabelColor);
        P_LabelColor.setLayout(P_LabelColorLayout);
        P_LabelColorLayout.setHorizontalGroup(
            P_LabelColorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 24, Short.MAX_VALUE)
        );
        P_LabelColorLayout.setVerticalGroup(
            P_LabelColorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        L_LabelScale.setText("Scale");

        S_LabelScale.setModel(new javax.swing.SpinnerNumberModel(1.0d, 0.1d, 10.0d, 0.1d));
        S_LabelScale.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                S_LabelScaleStateChanged(evt);
            }
        });

        L_LabelOpacity.setText("Opacity");

        S_LabelOpacity.setModel(new javax.swing.SpinnerNumberModel(100, 0, 100, 1));
        S_LabelOpacity.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                S_LabelOpacityStateChanged(evt);
            }
        });

        javax.swing.GroupLayout P_LabelLayout = new javax.swing.GroupLayout(P_Label);
        P_Label.setLayout(P_LabelLayout);
        P_LabelLayout.setHorizontalGroup(
            P_LabelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(P_LabelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(L_LabelColor)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(P_LabelColor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(L_LabelScale)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(S_LabelScale)
                .addGap(18, 18, 18)
                .addComponent(L_LabelOpacity)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(S_LabelOpacity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(72, 72, 72))
        );
        P_LabelLayout.setVerticalGroup(
            P_LabelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(P_LabelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(P_LabelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(P_LabelColor, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(L_LabelColor, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(P_LabelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(L_LabelOpacity, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(S_LabelOpacity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(P_LabelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(L_LabelScale)
                        .addComponent(S_LabelScale, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

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

        S_IconScale.setModel(new javax.swing.SpinnerNumberModel(1.0d, 0.1d, 100.0d, 0.1d));
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

        javax.swing.GroupLayout P_IconLayout = new javax.swing.GroupLayout(P_Icon);
        P_Icon.setLayout(P_IconLayout);
        P_IconLayout.setHorizontalGroup(
            P_IconLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(P_IconLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(L_IconColor)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(P_IconColor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(L_IconScale)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(S_IconScale, javax.swing.GroupLayout.DEFAULT_SIZE, 103, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(L_IconOpacity)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(S_IconOpacity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(72, 72, 72))
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
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(P_Icon, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(P_Label, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(P_Lines, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(P_Area, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(P_Lines, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(P_Area, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(P_Label, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(P_Icon, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        TP_Properties.addTab("Style, Color", jPanel4);

        L_Altitude.setText("Altitude");

        TF_Altitude.setText("0");
        TF_Altitude.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TF_AltitudeActionPerformed(evt);
            }
        });

        CB_AltitudeMode.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Absolute", "Clamped to ground", "Relative to ground" }));
        CB_AltitudeMode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CB_AltitudeModeActionPerformed(evt);
            }
        });

        L_Ground.setText("Ground");

        S_Altitude.setMaximum(10000);
        S_Altitude.setValue(0);
        S_Altitude.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                S_AltitudeStateChanged(evt);
            }
        });

        L_Space.setText("Space");

        CB_Extend.setText("Extend path to ground");
        CB_Extend.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CB_ExtendActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(L_Altitude)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                        .addComponent(TF_Altitude)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(CB_AltitudeMode, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(CB_Extend)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(L_Ground)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(S_Altitude, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(12, 12, 12)
                                .addComponent(L_Space)))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(L_Altitude)
                    .addComponent(TF_Altitude, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(CB_AltitudeMode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(S_Altitude, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(L_Ground)
                    .addComponent(L_Space))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(CB_Extend)
                .addContainerGap(196, Short.MAX_VALUE))
        );

        TP_Properties.addTab("Altitude", jPanel5);

        T_Perimeter.setText("Perimeter");

        L_Perimeter.setText("0");

        CB_PerimeterUnits.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Centimeters", "Meters", "Kilometers" }));
        CB_PerimeterUnits.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CB_PerimeterUnitsActionPerformed(evt);
            }
        });

        T_Area.setText("Area");

        L_Area.setText("0");

        CB_AreaUnits.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Square Meters", "Square Kilometers", "Hectars" }));
        CB_AreaUnits.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CB_AreaUnitsActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(T_Area, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(T_Perimeter, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(31, 31, 31)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(L_Perimeter, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(L_Area, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(CB_PerimeterUnits, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(CB_AreaUnits, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(74, 74, 74))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(T_Perimeter)
                    .addComponent(L_Perimeter)
                    .addComponent(CB_PerimeterUnits, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(T_Area)
                    .addComponent(L_Area)
                    .addComponent(CB_AreaUnits, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(239, Short.MAX_VALUE))
        );

        TP_Properties.addTab("Measurements", jPanel1);

        P_STM.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "STM", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION));

        TF_STM_X.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TF_STM_XActionPerformed(evt);
            }
        });

        L_STM_Y.setText("Y");

        TF_STM_Y.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TF_STM_YActionPerformed(evt);
            }
        });

        L_STM_Zone.setText("Zone");

        TF_STM_Zone.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TF_STM_ZoneActionPerformed(evt);
            }
        });

        L_STM_X.setText("X");

        javax.swing.GroupLayout P_STMLayout = new javax.swing.GroupLayout(P_STM);
        P_STM.setLayout(P_STMLayout);
        P_STMLayout.setHorizontalGroup(
            P_STMLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(P_STMLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(L_STM_X)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(TF_STM_X, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(24, 24, 24)
                .addComponent(L_STM_Y)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(TF_STM_Y, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(L_STM_Zone)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(TF_STM_Zone, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(26, Short.MAX_VALUE))
        );
        P_STMLayout.setVerticalGroup(
            P_STMLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(P_STMLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(P_STMLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(P_STMLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(L_STM_Zone)
                        .addComponent(TF_STM_Zone, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(P_STMLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(L_STM_Y)
                        .addComponent(TF_STM_Y, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(P_STMLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(TF_STM_X, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(L_STM_X)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        P_DMS.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "DMS", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION));

        L_DMS_Latitude.setText("Latitude");

        TF_DMS_Latitude.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TF_DMS_LatitudeActionPerformed(evt);
            }
        });

        L_DMS_Longitude.setText("Longitude");

        TF_DMS_Longitude.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TF_DMS_LongitudeActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout P_DMSLayout = new javax.swing.GroupLayout(P_DMS);
        P_DMS.setLayout(P_DMSLayout);
        P_DMSLayout.setHorizontalGroup(
            P_DMSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(P_DMSLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(L_DMS_Latitude)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(TF_DMS_Latitude, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(L_DMS_Longitude)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(TF_DMS_Longitude, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(26, Short.MAX_VALUE))
        );
        P_DMSLayout.setVerticalGroup(
            P_DMSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(P_DMSLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(P_DMSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(L_DMS_Latitude)
                    .addComponent(TF_DMS_Latitude, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(L_DMS_Longitude)
                    .addComponent(TF_DMS_Longitude, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        P_UTM.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "UTM", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION));

        TF_UTM_X.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TF_UTM_XActionPerformed(evt);
            }
        });

        L_UTM_Y.setText("Y");

        TF_UTM_Y.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TF_UTM_YActionPerformed(evt);
            }
        });

        L_UTM_Zone.setText("Zone");

        TF_UTM_Zone.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TF_UTM_ZoneActionPerformed(evt);
            }
        });

        L_UTM_X.setText("X");

        javax.swing.GroupLayout P_UTMLayout = new javax.swing.GroupLayout(P_UTM);
        P_UTM.setLayout(P_UTMLayout);
        P_UTMLayout.setHorizontalGroup(
            P_UTMLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(P_UTMLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(L_UTM_X)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(TF_UTM_X, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(24, 24, 24)
                .addComponent(L_UTM_Y)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(TF_UTM_Y, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(L_UTM_Zone)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(TF_UTM_Zone, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(26, Short.MAX_VALUE))
        );
        P_UTMLayout.setVerticalGroup(
            P_UTMLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(P_UTMLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(P_UTMLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(TF_UTM_X, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(L_UTM_Y)
                    .addComponent(TF_UTM_Y, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(L_UTM_Zone)
                    .addComponent(TF_UTM_Zone, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(L_UTM_X))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(P_STM, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel3Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(P_DMS, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap()))
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel3Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(P_UTM, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(181, Short.MAX_VALUE)
                .addComponent(P_STM, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(56, 56, 56))
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel3Layout.createSequentialGroup()
                    .addGap(21, 21, 21)
                    .addComponent(P_DMS, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(216, Short.MAX_VALUE)))
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel3Layout.createSequentialGroup()
                    .addGap(104, 104, 104)
                    .addComponent(P_UTM, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(133, Short.MAX_VALUE)))
        );

        TP_Properties.addTab("Coordinates", jPanel3);

        L_Name.setText("Name");

        TF_Name.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TF_NameActionPerformed(evt);
            }
        });

        B_Icons.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/ylw-pushpin24.png"))); // NOI18N
        B_Icons.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                B_IconsActionPerformed(evt);
            }
        });

        L_Latitude.setText("Latitude");

        TF_Latitude.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TF_LatitudeActionPerformed(evt);
            }
        });

        L_Longitude.setText("Longitude");

        TF_Longitude.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TF_LongitudeActionPerformed(evt);
            }
        });

        L_IconPath.setText("Icon Path");

        TF_IconPath.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TF_IconPathActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(L_Longitude)
                    .addComponent(L_Latitude)
                    .addComponent(L_Name)
                    .addComponent(L_IconPath))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(TF_IconPath)
                    .addComponent(TF_Name)
                    .addComponent(TF_Longitude)
                    .addComponent(TF_Latitude))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(B_Icons, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(L_Name)
                            .addComponent(TF_Name, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(8, 8, 8)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(L_Latitude)
                            .addComponent(TF_Latitude, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(B_Icons, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(L_Longitude)
                    .addComponent(TF_Longitude, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(L_IconPath)
                    .addComponent(TF_IconPath, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(TP_Properties)
                    .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(TP_Properties, javax.swing.GroupLayout.PREFERRED_SIZE, 329, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        TP_Properties.getAccessibleContext().setAccessibleName("Description");
        TP_Properties.getAccessibleContext().setAccessibleDescription("");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void B_OkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_B_OkActionPerformed
        is_ok = true;
        this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }//GEN-LAST:event_B_OkActionPerformed

    private void B_CancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_B_CancelActionPerformed
        is_ok = false;
        this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }//GEN-LAST:event_B_CancelActionPerformed

    private void P_LinesColorMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_P_LinesColorMouseClicked
        P_LinesColor.setBackground(jColorChooser1.showDialog(this, "Select a color", P_LinesColor.getBackground()));
        MainFrame.frame.set_object_properties(MainFrame.currObject);
    }//GEN-LAST:event_P_LinesColorMouseClicked

    private void P_AreaColorMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_P_AreaColorMouseClicked
        P_AreaColor.setBackground(jColorChooser1.showDialog(this, "Select a color", P_AreaColor.getBackground()));
        MainFrame.frame.set_object_properties(MainFrame.currObject);
    }//GEN-LAST:event_P_AreaColorMouseClicked

    private void P_LabelColorMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_P_LabelColorMouseClicked
        P_LabelColor.setBackground(jColorChooser1.showDialog(this, "Select a color", P_LabelColor.getBackground()));
        MainFrame.frame.set_object_properties(MainFrame.currObject);
    }//GEN-LAST:event_P_LabelColorMouseClicked

    private void P_IconColorMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_P_IconColorMouseClicked
        P_IconColor.setBackground(jColorChooser1.showDialog(this, "Select a color", P_IconColor.getBackground()));
        MainFrame.frame.set_object_properties(MainFrame.currObject);
    }//GEN-LAST:event_P_IconColorMouseClicked

    private void S_AltitudeStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_S_AltitudeStateChanged
        TF_Altitude.setText(Integer.toString(S_Altitude.getValue()));
        MainFrame.frame.set_object_properties(MainFrame.currObject);
    }//GEN-LAST:event_S_AltitudeStateChanged

    private void CB_AltitudeModeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CB_AltitudeModeActionPerformed
        if(CB_AltitudeMode.getSelectedIndex() != 1)
        {
            TF_Altitude.setText(String.format("%.3f", alt));
            TF_Altitude.setEnabled(true);
            S_Altitude.setEnabled(true);
            CB_Extend.setEnabled(true);
        }
        else
        {
            TF_Altitude.setText("0");
            TF_Altitude.setEnabled(false);
            S_Altitude.setEnabled(false);
            CB_Extend.setEnabled(false);
        }
        MainFrame.frame.set_object_properties(MainFrame.currObject);
    }//GEN-LAST:event_CB_AltitudeModeActionPerformed

    private void CB_PerimeterUnitsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CB_PerimeterUnitsActionPerformed
        switch(CB_PerimeterUnits.getSelectedIndex())
        {
            case 2:// Kilometers
            {
                L_Perimeter.setText(String.format("%.3f", perimeter/1000.0));
                break;
            }
            case 1:// Meters
            {
                L_Perimeter.setText(String.format("%.3f", perimeter));
                break;
            }
            case 0:// Centimeters
            {
                L_Perimeter.setText(String.format("%.3f", perimeter*100.0));
                break;
            }
        }
    }//GEN-LAST:event_CB_PerimeterUnitsActionPerformed

    private void CB_AreaUnitsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CB_AreaUnitsActionPerformed
        switch(CB_AreaUnits.getSelectedIndex())
        {
            case 2:// Hectars
            {
                L_Area.setText(String.format("%.3f", area*0.0001));
                break;
            }
            case 1:// Square Kilometers
            {
                L_Area.setText(String.format("%.3f", area/1000000.0));
                break;
            }
            case 0:// Square Meters
            {
                L_Area.setText(String.format("%.3f", area));
                break;
            }
        }
    }//GEN-LAST:event_CB_AreaUnitsActionPerformed

    private void B_IconsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_B_IconsActionPerformed
        if(is_tactical_symbols)
        {
            TacticalSymbolsFrame symbolsFrame = new TacticalSymbolsFrame();
            symbolsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            // Center the application on the screen.
            Dimension prefSize = symbolsFrame.getPreferredSize();
            Dimension parentSize;
            java.awt.Point parentLocation = new java.awt.Point(0, 0);
            parentSize = Toolkit.getDefaultToolkit().getScreenSize();
            int x = parentLocation.x + (parentSize.width - prefSize.width) / 2;
            int y = parentLocation.y + (parentSize.height - prefSize.height) / 2;
            symbolsFrame.setLocation(x, y);
        
            symbolsFrame.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                    TF_IconPath.setText(symbolsFrame.TF_SymbolID.getText());
                }
            });                

            symbolsFrame.P_IconColor.setBackground(P_IconColor.getBackground());
            symbolsFrame.S_IconScale.setValue(S_IconScale.getValue());
            symbolsFrame.S_IconOpacity.setValue(S_IconOpacity.getValue());
            symbolsFrame.TF_SymbolID.setText(TF_IconPath.getText());
            symbolsFrame.L_Icon.setIcon(new ImageIcon(TF_IconPath.getText()));
            symbolsFrame.setVisible(true);
        }
        else
        {
            IconsFrame iconsFrame = new IconsFrame();
            iconsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            // Center the application on the screen.
            Dimension prefSize = iconsFrame.getPreferredSize();
            Dimension parentSize;
            java.awt.Point parentLocation = new java.awt.Point(0, 0);
            parentSize = Toolkit.getDefaultToolkit().getScreenSize();
            int x = parentLocation.x + (parentSize.width - prefSize.width) / 2;
            int y = parentLocation.y + (parentSize.height - prefSize.height) / 2;
            iconsFrame.setLocation(x, y);
        
            iconsFrame.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                    TF_IconPath.setText(iconsFrame.TF_IconPath.getText());
                }
            });                

            iconsFrame.P_IconColor.setBackground(P_IconColor.getBackground());
            iconsFrame.S_IconScale.setValue(S_IconScale.getValue());
            iconsFrame.S_IconOpacity.setValue(S_IconOpacity.getValue());
            iconsFrame.TF_IconPath.setText(TF_IconPath.getText());
            iconsFrame.L_Icon.setIcon(new ImageIcon(TF_IconPath.getText()));
            iconsFrame.setVisible(true);
        }
    }//GEN-LAST:event_B_IconsActionPerformed

    private void S_LinesWidthStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_S_LinesWidthStateChanged
        MainFrame.frame.set_object_properties(MainFrame.currObject);
    }//GEN-LAST:event_S_LinesWidthStateChanged

    private void S_LinesOpacityStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_S_LinesOpacityStateChanged
        MainFrame.frame.set_object_properties(MainFrame.currObject);
    }//GEN-LAST:event_S_LinesOpacityStateChanged

    private void CB_AreaTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CB_AreaTypeActionPerformed
        MainFrame.frame.set_object_properties(MainFrame.currObject);
    }//GEN-LAST:event_CB_AreaTypeActionPerformed

    private void S_AreaOpacityStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_S_AreaOpacityStateChanged
        MainFrame.frame.set_object_properties(MainFrame.currObject);
    }//GEN-LAST:event_S_AreaOpacityStateChanged

    private void S_LabelScaleStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_S_LabelScaleStateChanged
        MainFrame.frame.set_object_properties(MainFrame.currObject);
    }//GEN-LAST:event_S_LabelScaleStateChanged

    private void S_LabelOpacityStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_S_LabelOpacityStateChanged
        MainFrame.frame.set_object_properties(MainFrame.currObject);
    }//GEN-LAST:event_S_LabelOpacityStateChanged

    private void S_IconScaleStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_S_IconScaleStateChanged
        MainFrame.frame.set_object_properties(MainFrame.currObject);
    }//GEN-LAST:event_S_IconScaleStateChanged

    private void S_IconOpacityStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_S_IconOpacityStateChanged
        MainFrame.frame.set_object_properties(MainFrame.currObject);
    }//GEN-LAST:event_S_IconOpacityStateChanged

    private void TF_AltitudeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TF_AltitudeActionPerformed
        MainFrame.frame.set_object_properties(MainFrame.currObject);
    }//GEN-LAST:event_TF_AltitudeActionPerformed

    private void CB_ExtendActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CB_ExtendActionPerformed
        MainFrame.frame.set_object_properties(MainFrame.currObject);
    }//GEN-LAST:event_CB_ExtendActionPerformed

    private void TF_NameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TF_NameActionPerformed
        B_OkActionPerformed(evt);
    }//GEN-LAST:event_TF_NameActionPerformed

    private void TF_LatitudeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TF_LatitudeActionPerformed
        index_to_exclude = 0;
        update_pos();
        B_OkActionPerformed(evt);
    }//GEN-LAST:event_TF_LatitudeActionPerformed

    private void TF_LongitudeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TF_LongitudeActionPerformed
        index_to_exclude = 0;
        update_pos();
        B_OkActionPerformed(evt);
    }//GEN-LAST:event_TF_LongitudeActionPerformed

    private void TF_IconPathActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TF_IconPathActionPerformed
        B_OkActionPerformed(evt);
    }//GEN-LAST:event_TF_IconPathActionPerformed

    private void TF_DMS_LatitudeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TF_DMS_LatitudeActionPerformed
        index_to_exclude = 1;
        update_pos();
        B_OkActionPerformed(evt);
    }//GEN-LAST:event_TF_DMS_LatitudeActionPerformed

    private void TF_DMS_LongitudeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TF_DMS_LongitudeActionPerformed
        index_to_exclude = 1;
        update_pos();
        B_OkActionPerformed(evt);
    }//GEN-LAST:event_TF_DMS_LongitudeActionPerformed

    private void TF_UTM_XActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TF_UTM_XActionPerformed
        index_to_exclude = 2;
        update_pos();
        B_OkActionPerformed(evt);
    }//GEN-LAST:event_TF_UTM_XActionPerformed

    private void TF_UTM_YActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TF_UTM_YActionPerformed
        index_to_exclude = 2;
        update_pos();
        B_OkActionPerformed(evt);
    }//GEN-LAST:event_TF_UTM_YActionPerformed

    private void TF_UTM_ZoneActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TF_UTM_ZoneActionPerformed
        index_to_exclude = 2;
        update_pos();
        B_OkActionPerformed(evt);
    }//GEN-LAST:event_TF_UTM_ZoneActionPerformed

    private void TF_STM_XActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TF_STM_XActionPerformed
        index_to_exclude = 3;
        update_pos();
        B_OkActionPerformed(evt);
    }//GEN-LAST:event_TF_STM_XActionPerformed

    private void TF_STM_YActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TF_STM_YActionPerformed
        index_to_exclude = 3;
        update_pos();
        B_OkActionPerformed(evt);
    }//GEN-LAST:event_TF_STM_YActionPerformed

    private void TF_STM_ZoneActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TF_STM_ZoneActionPerformed
        index_to_exclude = 3;
        update_pos();
        B_OkActionPerformed(evt);
    }//GEN-LAST:event_TF_STM_ZoneActionPerformed

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
            java.util.logging.Logger.getLogger(PropertiesFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(PropertiesFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(PropertiesFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(PropertiesFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new PropertiesFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JButton B_Cancel;
    public javax.swing.JButton B_Icons;
    public javax.swing.JButton B_Ok;
    public javax.swing.JComboBox<String> CB_AltitudeMode;
    public javax.swing.JComboBox<String> CB_AreaType;
    public javax.swing.JComboBox<String> CB_AreaUnits;
    public javax.swing.JCheckBox CB_Extend;
    private javax.swing.JComboBox<String> CB_PerimeterUnits;
    private javax.swing.JLabel L_Altitude;
    public javax.swing.JLabel L_Area;
    private javax.swing.JLabel L_AreaColor;
    private javax.swing.JLabel L_AreaOpacity;
    private javax.swing.JLabel L_DMS_Latitude;
    private javax.swing.JLabel L_DMS_Longitude;
    private javax.swing.JLabel L_Ground;
    private javax.swing.JLabel L_IconColor;
    private javax.swing.JLabel L_IconOpacity;
    public javax.swing.JLabel L_IconPath;
    private javax.swing.JLabel L_IconScale;
    private javax.swing.JLabel L_LabelColor;
    private javax.swing.JLabel L_LabelOpacity;
    private javax.swing.JLabel L_LabelScale;
    public javax.swing.JLabel L_Latitude;
    private javax.swing.JLabel L_LinesColor;
    private javax.swing.JLabel L_LinesOpacity;
    public javax.swing.JLabel L_Longitude;
    private javax.swing.JLabel L_Name;
    public javax.swing.JLabel L_Perimeter;
    private javax.swing.JLabel L_STM_X;
    private javax.swing.JLabel L_STM_Y;
    private javax.swing.JLabel L_STM_Zone;
    private javax.swing.JLabel L_Space;
    private javax.swing.JLabel L_UTM_X;
    private javax.swing.JLabel L_UTM_Y;
    private javax.swing.JLabel L_UTM_Zone;
    private javax.swing.JLabel L_Width;
    public javax.swing.JPanel P_Area;
    public javax.swing.JPanel P_AreaColor;
    private javax.swing.JPanel P_DMS;
    private javax.swing.JPanel P_Description;
    public javax.swing.JPanel P_Icon;
    public javax.swing.JPanel P_IconColor;
    public javax.swing.JPanel P_Label;
    public javax.swing.JPanel P_LabelColor;
    public javax.swing.JPanel P_Lines;
    public javax.swing.JPanel P_LinesColor;
    private javax.swing.JPanel P_STM;
    private javax.swing.JPanel P_UTM;
    private javax.swing.JSlider S_Altitude;
    public javax.swing.JSpinner S_AreaOpacity;
    public javax.swing.JSpinner S_IconOpacity;
    public javax.swing.JSpinner S_IconScale;
    public javax.swing.JSpinner S_LabelOpacity;
    public javax.swing.JSpinner S_LabelScale;
    public javax.swing.JSpinner S_LinesOpacity;
    public javax.swing.JSpinner S_LinesWidth;
    public javax.swing.JTextArea TA_Description;
    public javax.swing.JTextField TF_Altitude;
    private javax.swing.JTextField TF_DMS_Latitude;
    private javax.swing.JTextField TF_DMS_Longitude;
    public javax.swing.JTextField TF_IconPath;
    public javax.swing.JTextField TF_Latitude;
    public javax.swing.JTextField TF_Longitude;
    public javax.swing.JTextField TF_Name;
    private javax.swing.JTextField TF_STM_X;
    private javax.swing.JTextField TF_STM_Y;
    private javax.swing.JTextField TF_STM_Zone;
    private javax.swing.JTextField TF_UTM_X;
    private javax.swing.JTextField TF_UTM_Y;
    private javax.swing.JTextField TF_UTM_Zone;
    public javax.swing.JTabbedPane TP_Properties;
    public javax.swing.JLabel T_Area;
    private javax.swing.JLabel T_Perimeter;
    private javax.swing.JColorChooser jColorChooser1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
}
