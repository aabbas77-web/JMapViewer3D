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

import gov.nasa.worldwind.avlist.*;
import gov.nasa.worldwind.formats.shapefile.*;
import gov.nasa.worldwind.geom.*;
import gov.nasa.worldwind.layers.*;
import gov.nasa.worldwind.render.*;
import gov.nasa.worldwind.render.markers.BasicMarker;
import gov.nasa.worldwind.render.markers.MarkerAttributes;
import gov.nasa.worldwind.render.markers.MarkerRenderer;
import gov.nasa.worldwind.util.*;

import java.awt.*;
import java.awt.image.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.*;
import java.util.logging.Logger;

/**
 * @author dcollins
 * @version $Id: OpenStreetMapShapefileLoader.java 1171 2013-02-11 21:45:02Z dcollins $
 */
public class myOpenStreetMapShapefileLoader
{
    /**
     * Returns true if the specified Shapefile source is an OpenStreetMap Shapefile containing placemarks, and false
     * otherwise. The source is considered to be an OpenStreetMap source if it can be converted to an abstract path, and
     * the path's filename is equal to "places.shp", ignoring case.
     *
     * @param source the source of the Shapefile.
     *
     * @return true if the Shapefile is an OpenStreetMap Shapefile; false otherwise.
     *
     * @throws IllegalArgumentException if the source is null or an empty string.
     */
    public static boolean isOSMPlacesSource(Object source)
    {
        if (source == null || WWUtil.isEmpty(source))
        {
            String message = Logging.getMessage("nullValue.SourceIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        String path = WWIO.getSourcePath(source);
        return path != null && WWIO.getFilename(path).equalsIgnoreCase("places.shp");
//        return path != null && WWIO.getFilename(path).equalsIgnoreCase("gis_osm_places_free_1.shp");
    }

    /**
     * Creates a {@link gov.nasa.worldwind.layers.Layer} from an OpenStreetMap Shapefile source of placemarks. The
     * source type may be one of the following: <ul> <li>{@link java.io.InputStream}</li> <li>{@link java.net.URL}</li>
     * <li>absolute {@link java.net.URI}</li> <li>{@link java.io.File}</li> <li>{@link String} containing a valid URL
     * description or a file or resource name available on the classpath.</li> </ul>
     * <p>
     * The returned Layer renders each Shapefile record as a surface circle with an associated screen label. The label
     * text is taken from the Shapefile record attribute key "name". This determines each surface circle's appearance
     * from the Shapefile record attribute key "type" as follows: <table> <caption style="font-weight: bold;">Mapping</caption>
     * <tr><th>Type</th><th>Color</th></tr>
     * <tr><td>hamlet</td><td>Black</td></tr> <tr><td>village</td><td>Green</td></tr>
     * <tr><td>town</td><td>Cyan</td></tr> <tr><td>city</td><td>Yellow</td></tr> </table>
     *
     * @param source the source of the OpenStreetMap Shapefile.
     *
     * @return a Layer that renders the Shapefile's contents on the surface of the Globe.
     *
     * @throws IllegalArgumentException if the source is null or an empty string.
     */
    public static Layer makeLayerFromOSMPlacesSource(Object source, String except)
    {
        if (source == null || WWUtil.isEmpty(source))
        {
            String message = Logging.getMessage("nullValue.SourceIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        Shapefile shp = null;
        Layer layer = null;
        try
        {
            shp = new Shapefile(source);
            layer = makeLayerFromOSMPlacesShapefile(shp, except);
        }
        finally
        {
            if (shp != null)
                shp.close();
        }

        return layer;
    }

    public static Layer makeLayerFromOSMPlacesZipSource(String zipFilePath, String except)
    {
        if (zipFilePath == null || WWUtil.isEmpty(zipFilePath))
        {
            String message = Logging.getMessage("nullValue.SourceIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        FileSystem fs = null;
        try {
            zipFilePath = zipFilePath.replace("\\","/");
            URI uri = URI.create("jar:file:/"+zipFilePath);
            Map<String,String> env = new HashMap<String,String>();//AliSoft
            env.put("create", "false");
            fs = FileSystems.newFileSystem(uri, env);
        } catch (IOException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        Shapefile shapefile = null;
        try {
            String strFilename = WWIO.getFilename(zipFilePath);
            strFilename = strFilename.replace("file://","");
            strFilename = strFilename.replace("file:/","");
            strFilename = "/"+strFilename;

            java.nio.file.Path shpPath = fs.getPath(WWIO.replaceSuffix(strFilename, ".shp"));
            if(Files.exists(shpPath))
            {
                InputStream shpStream = Files.newInputStream(shpPath);

                java.nio.file.Path shxPath = fs.getPath(WWIO.replaceSuffix(strFilename, ".shx"));
                InputStream shxStream = Files.newInputStream(shxPath);

                java.nio.file.Path dbfPath = fs.getPath(WWIO.replaceSuffix(strFilename, ".dbf"));
                InputStream dbfStream = Files.newInputStream(dbfPath);

                java.nio.file.Path prjPath = fs.getPath(WWIO.replaceSuffix(strFilename, ".prj"));
                InputStream prjStream = Files.newInputStream(prjPath);

                shapefile = new Shapefile(
                        shpStream,
                        shxStream,
                        dbfStream,
                        prjStream);
            }
        } catch (Exception ex) {
            Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        
        Layer layer = null;
        try
        {
            layer = makeLayerFromOSMPlacesShapefile(shapefile, except);
        }
        finally
        {
            if (shapefile != null)
                shapefile.close();
        }

        return layer;
    }

    /**
     * Creates a {@link gov.nasa.worldwind.layers.Layer} from an OpenStreetMap Shapefile of placemarks.
     * <p>
     * The returned Layer renders each Shapefile record as a surface circle with an associated screen label. The label
     * text is taken from the Shapefile record attribute key "name". This determines each surface circle's appearance
     * from the Shapefile record attribute key "type" as follows: <table> <caption style="font-weight: bold;">Mapping</caption><tr><th>Type</th><th>Color</th></tr>
     * <tr><td>hamlet</td><td>Black</td></tr> <tr><td>village</td><td>Green</td></tr>
     * <tr><td>town</td><td>Cyan</td></tr> <tr><td>city</td><td>Yellow</td></tr> </table>
     *
     * @param shp the Shapefile to create a layer for.
     *
     * @return a Layer that renders the Shapefile's contents on the surface of the Globe.
     *
     * @throws IllegalArgumentException if the Shapefile is null, or if the Shapefile's primitive type is unrecognized.
     */
    public static Layer makeLayerFromOSMPlacesShapefile(Shapefile shp, String except)
    {
        if (shp == null)
        {
            String message = Logging.getMessage("nullValue.ShapefileIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        OSMShapes[] shapeArray =
        {
            new OSMShapes(Color.BLACK, 0.5, 0, 30e3), // hamlet
            new OSMShapes(Color.GREEN, 1, 0, 40e3), // village
            new OSMShapes(Color.CYAN, 2, 40e3, 500e3), // town
            new OSMShapes(Color.YELLOW, 3, 500e3, 3000e3), // city
            new OSMShapes(Color.RED, 0.75, 0, 10e3), // other
        };

        // Filter records for a particular sector
        while (shp.hasNext())
        {
            ShapefileRecord record = shp.nextRecord();
            if (record == null || !record.getShapeType().equals(Shapefile.SHAPE_POINT))
                continue;

//AliSof            Object o = record.getAttributes().getValue("type");
            Object o = record.getAttributes().getValue("fclass");
            if (o == null || !(o instanceof String))
                continue;

            // Add points with different rendering attribute for different subsets
            OSMShapes shapes = null;
            String type = (String) o;
            if ((except != null) && (except.toLowerCase().contains(type.toLowerCase())))    continue;
            if (type.equalsIgnoreCase("hamlet"))
            {
                shapes = shapeArray[0];
            }
            else if (type.equalsIgnoreCase("village"))
            {
                shapes = shapeArray[1];
            }
            else if (type.equalsIgnoreCase("town"))
            {
                shapes = shapeArray[2];
            }
            else if (type.equalsIgnoreCase("city"))
            {
                shapes = shapeArray[3];
            }
            else
            {
                // locality, suburb, farm, region, island, national_capital, dwelling, county
                shapes = shapeArray[4];
//                System.out.println("type: "+type);
            }

            if (shapes == null)
                continue;

            String name = null;

            AVList attr = record.getAttributes();
            if (attr.getEntries() != null)
            {
                for (Map.Entry<String, Object> entry : attr.getEntries())
                {
                    if (entry.getKey().equalsIgnoreCase("name"))
                    {
                        name = (String) entry.getValue();
                        break;
                    }
                }
            }

            // Note: points are stored in the buffer as a sequence of X and Y with X = longitude, Y = latitude.
            double[] pointCoords = ((ShapefileRecordPoint) record).getPoint();
            LatLon location = LatLon.fromDegrees(pointCoords[1], pointCoords[0]);

            if (!WWUtil.isEmpty(name))
            {
                Label label = new Label(name, new Position(location, 0));
                label.setFont(shapes.font);
                label.setColor(shapes.foreground);
                label.setBackgroundColor(shapes.background);
                label.setMinActiveAltitude(shapes.labelMinAltitude);
                label.setMaxActiveAltitude(shapes.labelMaxAltitude);
                label.setPriority(shapes.labelMaxAltitude);
                shapes.labels.add(label);
            }

            shapes.locations.add(location);
        }

        TextAndShapesLayer layer = new TextAndShapesLayer();

        for (OSMShapes shapes : shapeArray)
        {
//            // Use one SurfaceIcons instance for all points.
//            BufferedImage image = PatternFactory.createPattern(PatternFactory.PATTERN_CIRCLE, .8f, shapes.foreground);
//            SurfaceIcons sis = new SurfaceIcons(image, shp.getPointBuffer().getLocations());
//            sis.setMaxSize(4e3 * shapes.scale); // 4km
//            sis.setMinSize(100);  // 100m
//            sis.setScale(shapes.scale);
//            sis.setOpacity(.8);
//            layer.addRenderable(sis);
//            shapes.locations.clear();

            for (Label label : shapes.labels)
            {
                layer.addLabel(label);
            }
            shapes.labels.clear();
        }

        return layer;
    }

    //**************************************************************//
    //********************  Helper Classes  ************************//
    //**************************************************************//

    protected static class OSMShapes
    {
        public ArrayList<LatLon> locations = new ArrayList<LatLon>();
        public ArrayList<Label> labels = new ArrayList<Label>();
        public Color foreground;
        public Color background;
        public Font font;
        public double scale;
        public double labelMinAltitude;
        public double labelMaxAltitude;

        public OSMShapes(Color color, double scale, double labelMinAltitude, double labelMaxAltitude)
        {
            this.foreground = color;
            this.background = WWUtil.computeContrastingColor(color);
            this.font = new Font("Tahoma", Font.BOLD, 10 + (int) (3 * scale));
            this.scale = scale;
            this.labelMinAltitude = labelMinAltitude;
            this.labelMaxAltitude = labelMaxAltitude;
        }
    }

    protected static class TextAndShapesLayer extends RenderableLayer
    {
        protected ArrayList<GeographicText> labels = new ArrayList<GeographicText>();
        protected GeographicTextRenderer textRenderer = new GeographicTextRenderer();

        public TextAndShapesLayer()
        {
            this.textRenderer.setCullTextEnabled(true);
            this.textRenderer.setCullTextMargin(2);
            this.textRenderer.setDistanceMaxScale(2);
            this.textRenderer.setDistanceMinScale(.5);
            this.textRenderer.setDistanceMinOpacity(.5);
            this.textRenderer.setEffect(AVKey.TEXT_EFFECT_OUTLINE);
            
            this.setPickEnabled(true);
        }

        public void addLabel(GeographicText label)
        {
            this.labels.add(label);
        }

        public void doRender(DrawContext dc)
        {
            super.doRender(dc);
            this.setActiveLabels(dc);
            this.textRenderer.render(dc, this.labels);
        }

        protected void setActiveLabels(DrawContext dc)
        {
            for (GeographicText text : this.labels)
            {
                if (text instanceof Label)
                    text.setVisible(((Label) text).isActive(dc));
            }
        }
    }

    protected static class Label extends UserFacingText
    {
        protected double minActiveAltitude = -Double.MAX_VALUE;
        protected double maxActiveAltitude = Double.MAX_VALUE;

        public Label(String text, Position position)
        {
            super(text, position);
        }

        public void setMinActiveAltitude(double altitude)
        {
            this.minActiveAltitude = altitude;
        }

        public void setMaxActiveAltitude(double altitude)
        {
            this.maxActiveAltitude = altitude;
        }

        public boolean isActive(DrawContext dc)
        {
            double eyeElevation = dc.getView().getEyePosition().getElevation();
            return this.minActiveAltitude <= eyeElevation && eyeElevation <= this.maxActiveAltitude;
        }
    }
}
