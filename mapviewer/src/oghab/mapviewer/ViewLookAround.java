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
import gov.nasa.worldwind.awt.WorldWindowGLCanvas;
import gov.nasa.worldwind.geom.*;
import gov.nasa.worldwind.layers.CrosshairLayer;
import gov.nasa.worldwind.view.firstperson.BasicFlyView;
import gov.nasa.worldwind.view.orbit.*;
import gov.nasa.worldwindx.examples.ApplicationTemplate;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Arrays;

/**
 * This example demonstrates how to 'look around' a scene by controlling the view's pitch, heading, roll and field of
 * view, in this case by using a simple set of sliders.
 *
 * @author Patrick Murris
 * @version $Id: ViewLookAround.java 2109 2014-06-30 16:52:38Z tgaskins $
 */
public class ViewLookAround extends ApplicationTemplate {

    public static class AppFrame extends ApplicationTemplate.AppFrame {

        private final ViewControlPanel vcp;

        public AppFrame() {
            super(true, true, false);

            // Add view control panel to the layer panel
            this.vcp = new ViewControlPanel(getWwd());
            BasicFlyView flyView = new BasicFlyView();
            getWwd().setView(flyView);
            this.getControlPanel().add(this.vcp, BorderLayout.SOUTH);
            Position pos = new Position(new LatLon(Angle.fromDegrees(45), Angle.fromDegrees(-120)), 2000);
            flyView.setEyePosition(pos);
            flyView.setHeading(Angle.fromDegrees(0));
            flyView.setPitch(Angle.fromDegrees(90));
            flyView.setFieldOfView(Angle.fromDegrees(45));
            flyView.setRoll(Angle.fromDegrees(0));
        }

        private class ViewControlPanel extends JPanel {

            private final WorldWindow wwd;
            private JSlider pitchSlider;
            private JSlider headingSlider;
            private JSlider rollSlider;
            private JSlider fovSlider;

            private boolean suspendEvents = false;

            mv_3d_camera camera = new mv_3d_camera();

            public ViewControlPanel(WorldWindow wwd) {
                this.wwd = wwd;
                // Add view property listener
                this.wwd.getView().addPropertyChangeListener((PropertyChangeEvent propertyChangeEvent) -> {
                    update();
                });

                // Compose panel
                this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

                insertBeforeCompass(getWwd(), new CrosshairLayer());

                // Pitch slider
                JPanel pitchPanel = new JPanel(new GridLayout(0, 1, 5, 5));
                pitchPanel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
                pitchPanel.add(new JLabel("Pitch:"));
                pitchSlider = new JSlider(0, 180, 90);
                pitchSlider.addChangeListener((ChangeEvent changeEvent) -> {
                    updateView();
                });
                pitchPanel.add(pitchSlider);

                // Heading slider
                JPanel headingPanel = new JPanel(new GridLayout(0, 1, 5, 5));
                headingPanel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
                headingPanel.add(new JLabel("Heading:"));
                headingSlider = new JSlider(-180, 180, 0);
                headingSlider.addChangeListener((ChangeEvent changeEvent) -> {
                    updateView();
                });
                headingPanel.add(headingSlider);

                // Roll slider
                JPanel rollPanel = new JPanel(new GridLayout(0, 1, 5, 5));
                rollPanel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
                rollPanel.add(new JLabel("Roll:"));
                rollSlider = new JSlider(-180, 180, 0);
                rollSlider.addChangeListener((ChangeEvent changeEvent) -> {
                    updateView();
                });
                rollPanel.add(rollSlider);

                // Field of view slider
                JPanel fovPanel = new JPanel(new GridLayout(0, 1, 5, 5));
                fovPanel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
                fovPanel.add(new JLabel("Field of view:"));
                fovSlider = new JSlider(10, 120, 45);
                fovSlider.addChangeListener((ChangeEvent changeEvent) -> {
                    updateView();
                });
                fovPanel.add(fovSlider);

                // Assembly
                this.add(pitchPanel);
                this.add(headingPanel);
                this.add(rollPanel);
                this.add(fovPanel);

                JButton resetBut = new JButton("Reset");
                resetBut.addActionListener((ActionEvent e) -> {
                    pitchSlider.setValue(90);
                    rollSlider.setValue(0);
                    headingSlider.setValue(0);
                    fovSlider.setValue(45);
                    updateView();
                });
                this.add(resetBut);

                this.setBorder(
                        new CompoundBorder(BorderFactory.createEmptyBorder(9, 9, 9, 9), new TitledBorder("View")));
                this.setToolTipText("View controls");
        
                UDPServer upd_server = new UDPServer(21567);
                upd_server.start();
            }
    
            void updateControls() {
                if (!suspendEvents) {
                    BasicFlyView view = (BasicFlyView) this.wwd.getView();
                    
                    // Stop iterators first
                    view.stopAnimations();

                    view.setEyePosition(Position.fromDegrees(camera.latitude, camera.longitude, camera.altitude));

                    // Save current eye position
//                    final Position pos = view.getEyePosition();

                    // Set view heading, pitch and fov
                    view.setHeading(Angle.fromDegrees(camera.heading));
                    view.setPitch(Angle.fromDegrees(camera.pitch));
                    view.setRoll(Angle.fromDegrees(camera.roll));
                    view.setFieldOfView(Angle.fromDegrees(camera.fov));

                    // Restore eye position
//                    view.setEyePosition(pos);

                    // Redraw
                    this.wwd.redraw();
                }
                
/*                
//                this.suspendEvents = true;
                {
                    // Set view heading, pitch and fov
//                    BasicOrbitView view = (BasicOrbitView)wwd.getView();
//                    view.setEyePosition(Position.fromDegrees(camera.latitude, camera.longitude, camera.altitude));
//                    view.setRoll(Angle.fromDegrees(camera.roll));
//                    view.setPitch(Angle.fromDegrees(camera.pitch));
//                    view.setHeading(Angle.fromDegrees(camera.heading));
//                    view.setFieldOfView(Angle.fromDegrees(camera.fov));

                    this.pitchSlider.setValue((int) camera.pitch);
                    this.headingSlider.setValue((int) camera.heading);
                    this.rollSlider.setValue((int) camera.roll);
                    this.fovSlider.setValue((int) camera.fov);

                    // Redraw
                    wwd.redraw();

//                    OrbitView view = (OrbitView) wwd.getView();
//                    this.pitchSlider.setValue((int) view.getPitch().degrees);
//                    this.headingSlider.setValue((int) view.getHeading().degrees);
//                    this.fovSlider.setValue((int) view.getFieldOfView().degrees);
                }
//                this.suspendEvents = false;
*/
            }

            private final static int PACKETSIZE = 1024;
            class UDPServer extends Thread
            {
                private boolean running = false;
                private int port;

                public UDPServer(int port)
                {
                    this.port = port;
                }

                @Override
                public void run()
                {
                    running = true;
                    try
                    {
                        // Convert the argument to ensure that is it valid
                        int port = 21567;
                        // Construct the socket
                        DatagramSocket socket = new DatagramSocket(port) ;
                        System.out.println("Start server on port: " + port);
                        while( running )
                        {
                            try
                            {
                                // Create a packet
                                DatagramPacket packet = new DatagramPacket(new byte[PACKETSIZE],PACKETSIZE);

                                // Receive a packet (blocking)
                                socket.receive(packet) ;

                                // Print the packet
            //                    System.out.println( packet.getAddress() + " " + packet.getPort() + ": " + new String(packet.getData()) ) ;

                                String str = new String(packet.getData());
                                System.out.println(str);
                                java.util.List<String> params = Arrays.asList(str.split(","));
                                for(int i=0;i<params.size();i++)
                                {
                                    System.out.println(params.get(i));
                                }

                                int idx = 0;
                                int index = Integer.parseInt(params.get(idx++));
                                camera.latitude = Double.parseDouble(params.get(idx++));
                                camera.longitude = Double.parseDouble(params.get(idx++));
                                camera.altitude = Double.parseDouble(params.get(idx++));
                                camera.heading = Double.parseDouble(params.get(idx++)) - 180;
//                                camera.pitch = Double.parseDouble(params.get(idx++)) - 90;
                                camera.pitch = Double.parseDouble(params.get(idx++));
                                camera.roll = Double.parseDouble(params.get(idx++));
                                long time_start = Long.parseLong(params.get(idx++));
                                long time_end = Long.parseLong(params.get(idx++));
                                updateControls();

                                // Return the packet to the sender
                                socket.send(packet) ;
                            }
                            catch( Exception e )
                            {
                                e.printStackTrace();
                            }
                        }
                        System.out.println( "Shutting down server" );
                     }
                     catch(Exception e)
                     {
                         System.out.println(e) ;
                     }
                }
            }

            // Update view settings from control panel in a 'first person' perspective
            private void updateView() {
//                if (!suspendEvents) {
//                    BasicFlyView view = (BasicFlyView) this.wwd.getView();
//
//                    // Stop iterators first
//                    view.stopAnimations();
//
//                    // Save current eye position
//                    final Position pos = view.getEyePosition();
//
//                    // Set view heading, pitch and fov
//                    view.setHeading(Angle.fromDegrees(this.headingSlider.getValue()));
//                    view.setPitch(Angle.fromDegrees(this.pitchSlider.getValue()));
//                    view.setRoll(Angle.fromDegrees(this.rollSlider.getValue()));
//                    view.setFieldOfView(Angle.fromDegrees(this.fovSlider.getValue()));
//
//                    // Restore eye position
//                    view.setEyePosition(pos);
//
//                    // Redraw
//                    this.wwd.redraw();
//                }
            }

            // Update control panel from view
            public void update() {
//                this.suspendEvents = true;
//                {
//                    OrbitView view = (OrbitView) wwd.getView();
//                    this.pitchSlider.setValue((int) view.getPitch().degrees);
//                    this.headingSlider.setValue((int) view.getHeading().degrees);
//                    this.rollSlider.setValue((int) view.getRoll().degrees);
//                    this.fovSlider.setValue((int) view.getFieldOfView().degrees);
//                }
//                this.suspendEvents = false;
            }
        }
    }

    public static void main(String[] args) {
        ApplicationTemplate.start("WorldWind View Look Around", AppFrame.class);
    }
}
