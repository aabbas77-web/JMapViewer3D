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


import gov.nasa.worldwind.WorldWindow;
import gov.nasa.worldwind.layers.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Represents one layer in the layer manager's layer list.
 *
 * @author tag
 * @version $Id: LayerPanel.java 1179 2013-02-15 17:47:37Z tgaskins $
 */
public class myLayerPanel2 extends JPanel
{
    protected myLayerManagerPanel layerManagerPanel;
    protected myElevationModelManagerPanel elevationModelManagerPanel;

    public myLayerPanel2(WorldWindow wwd)
    {
        super(new BorderLayout(10, 10));

        this.add(this.layerManagerPanel = new myLayerManagerPanel(wwd), BorderLayout.CENTER);

        this.add(this.elevationModelManagerPanel = new myElevationModelManagerPanel(wwd), BorderLayout.SOUTH);
    }

    public void updateLayers(WorldWindow wwd)
    {
        this.layerManagerPanel.update(wwd);
    }

    public void updateElevations(WorldWindow wwd)
    {
        this.elevationModelManagerPanel.update(wwd);
    }

    @SuppressWarnings("UnusedParameters")
    /**
     * @deprecated There is no need to call this method. As of 6/30/14 it is a no-op.
     */
    public void update(WorldWindow wwd)
    {
        // This is here merely to provide backwards compatibility for users of the previous version of LayerPanel.
    }
}
