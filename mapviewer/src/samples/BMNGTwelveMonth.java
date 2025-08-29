/*
Copyright (C) 2001, 2006 United States Government
as represented by the Administrator of the
National Aeronautics and Space Administration.
All Rights Reserved.
*/
package samples;

//import gov.nasa.worldwind.layers.Earth.BMNG2004Layer;
//import gov.nasa.worldwind.layers.Earth.BMNGSurfaceLayer;
import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.layers.LayerList;
import gov.nasa.worldwindx.examples.ApplicationTemplate;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/** Using the BMNG2004Layer - shows twelve month of the year 2004.
 * @author Patrick Murris
 * @version $Id$
 */
public class BMNGTwelveMonth extends ApplicationTemplate
{
    public static class AppFrame extends ApplicationTemplate.AppFrame
    {
        private Layer[] BMNGLayers;
        private int layerID = -1;
        private int month = 5;

        public AppFrame()
        {
            super(true, true, false);

            // Find Blue Marble layer number
            LayerList layers = this.getWwd().getModel().getLayers();
            for(int i = 0; i < layers.size(); i++)
            {
                if(layers.get(i) instanceof BMNGSurfaceLayer)
                    layerID = i;
            }

            // Instantiate the twelve layers
            BMNGLayers = new Layer[12];
//AliSoft            
//            for (int i = 1; i <= 12; i++)
//                BMNGLayers[i - 1] = new BMNG2004Layer(i);

            // Add control panel
            this.getLayerPanel().add(makeControlPanel(),  BorderLayout.SOUTH);
        }

        private JPanel makeControlPanel()
        {
            JPanel controlPanel = new JPanel(new GridLayout(0, 1, 0, 0));

            // Month combo
            JPanel comboPanel = new JPanel(new GridLayout(0, 2, 0, 0));
            comboPanel.add(new JLabel("  Month:"));
            String[] monthList = new String[12];
            for (int i = 1; i <= 12; i++)
                monthList[i - 1] = i + "-2004";
            final JComboBox cbMonth = new JComboBox(monthList);
            cbMonth.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent actionEvent)
                {
                    String item = (String) cbMonth.getSelectedItem();
                    month = Integer.parseInt(item.split("-")[0]);
                    update();
                }
            });
            cbMonth.setSelectedItem("5-2004");
            comboPanel.add(cbMonth);

            controlPanel.add(comboPanel);
            controlPanel.setBorder(
                new CompoundBorder(BorderFactory.createEmptyBorder(9, 9, 9, 9), new TitledBorder("Blue Marble")));
            controlPanel.setToolTipText("Set the current BMNG month");
            return controlPanel;
        }

        // Update worldwind
        private void update()
        {
            LayerList layers = this.getWwd().getModel().getLayers();
            layers.remove(layerID);
            layers.add(layerID, BMNGLayers[month - 1]);
            this.getLayerPanel().update(this.getWwd());
            this.getWwd().redraw();
        }
    }

    public static void main(String[] args)
    {
        ApplicationTemplate.start("World Wind Blue Marble Twelve Month 2004", AppFrame.class);
    }
}
