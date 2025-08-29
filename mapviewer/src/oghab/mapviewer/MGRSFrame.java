/*
 * Copyright 2006-2009, 2017, 2020 United States Government, as represented by the
 * Administrator of the National Aeronautics and Space Administration.
 * All rights reserved.
 * 
 * The NASA World Wind Java (WWJ) platform is licensed under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 * 
 * NASA World Wind Java (WWJ) also contains the following 3rd party Open Source
 * software:
 * 
 *     Jackson Parser – Licensed under Apache 2.0
 *     GDAL – Licensed under MIT
 *     JOGL – Licensed under  Berkeley Software Distribution (BSD)
 *     Gluegen – Licensed under Berkeley Software Distribution (BSD)
 * 
 * A complete listing of 3rd Party software notices and licenses included in
 * NASA World Wind Java (WWJ)  can be found in the WorldWindJava-v2.2 3rd-party
 * notices and licenses PDF found in code directory.
 */

package oghab.mapviewer;

import gov.nasa.worldwind.Configuration;
import gov.nasa.worldwind.awt.WorldWindowGLCanvas;
import gov.nasa.worldwind.cache.*;
import gov.nasa.worldwind.layers.Earth.MGRSGraticuleLayer;
import gov.nasa.worldwind.util.measure.MeasureTool;
import gov.nasa.worldwind.util.measure.MeasureToolController;
import gov.nasa.worldwindx.examples.layermanager.LayerManagerPanel;
import gov.nasa.worldwindx.examples.util.FileStoreDataSet;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.*;
import java.util.List;

/**
 * The DataCacheViewer is a tool that allows the user to view and delete cached WorldWind files based on how old they
 * are.  The utility shows the various directories in the cache root, how large they each are, when they were last used,
 * and how many files exist in them that are older than a day, week, month or year. It also allows the user to delete
 * all files older than a specified number of days, weeks, months or years.
 *
 * @author tag
 * @version $Id: DataCacheViewer.java 1171 2013-02-11 21:45:02Z dcollins $
 */
@SuppressWarnings("unchecked")
public class MGRSFrame
{
    protected JPanel panel;
    protected myMGRSAttributesPanel layerPanel;
    
    public MGRSFrame(MGRSGraticuleLayer mgrsGraticuleLayer)
    {
        this.panel = new JPanel(new BorderLayout(5, 5));

        layerPanel = new myMGRSAttributesPanel(mgrsGraticuleLayer);
        layerPanel.setBackground(Color.gray);

        this.panel.add(layerPanel, BorderLayout.CENTER);
    }

    static
    {
        if (Configuration.isMacOS())
        {
            System.setProperty("com.apple.mrj.application.apple.menu.about.name", "WorldWind Cache Cleaner");
        }
    }

    static JFrame frame;// AliSoft
    public static void create(MGRSGraticuleLayer mgrsGraticuleLayer)
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
//                JFrame frame = new JFrame();
                frame = new JFrame();
                frame.setPreferredSize(new Dimension(500, 450));
//                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);// AliSoft
                frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

                // AliSoft
                ImageIcon icon = new ImageIcon(getClass().getResource("/res/mapviewer_icon.png"));  
                frame.setIconImage(icon.getImage());  
                
                MGRSFrame viewerPanel = new MGRSFrame(mgrsGraticuleLayer);
                frame.getContentPane().add(viewerPanel.panel, BorderLayout.CENTER);
                frame.pack();

                // Center the application on the screen.
                Dimension prefSize = frame.getPreferredSize();
                Dimension parentSize;
                java.awt.Point parentLocation = new java.awt.Point(0, 0);
                parentSize = Toolkit.getDefaultToolkit().getScreenSize();
                int x = parentLocation.x + (parentSize.width - prefSize.width) / 2;
                int y = parentLocation.y + (parentSize.height - prefSize.height) / 2;
                frame.setLocation(x, y);
                frame.setVisible(true);
            }
        });
    }
}
