/*
Copyright (C) 2001, 2008 United States Government
as represented by the Administrator of the
National Aeronautics and Space Administration.
All Rights Reserved.
*/
package samples;

import gov.nasa.worldwind.layers.*;
//import gov.nasa.worldwind.globes.EarthElevationModel;
import gov.nasa.worldwind.globes.Globe;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.util.Logging;
import gov.nasa.worldwind.render.SurfaceImage;
import gov.nasa.worldwind.render.PatternFactory;
import gov.nasa.worldwind.Model;
import gov.nasa.worldwindx.examples.ApplicationTemplate;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Elevation model override example to produce a truncated globe.
 *
 * @author Patrick Murris
 * @version $Id: TruncatedEarth.java 6321 2008-09-01 02:08:30Z patrickmurris $
 */
public class TruncatedEarth extends ApplicationTemplate
{
    public static class AppFrame extends ApplicationTemplate.AppFrame
    {

        public AppFrame()
        {
            super(true, true, false);

            Model model = getWwd().getModel();
            Globe globe = model.getGlobe();

            // Use our truncated elevation model
            Sector sector = Sector.fromDegrees(0, 90, -100, -20);
            double elevation = -4e6;
//            globe.setElevationModel(new TruncatedEarthElevationModel(sector, elevation));

            // Add truncated sector surface image layer
            RenderableLayer sectionlayer = new RenderableLayer();
            sectionlayer.setPickEnabled(false);
            sectionlayer.setName("Earth Section");
            BufferedImage sectionImage = PatternFactory.createPattern(PatternFactory.GRADIENT_HLINEAR,
                    1f, Color.RED.brighter().brighter(),  Color.RED.darker().darker());
            BufferedImage coreImage = PatternFactory.createPattern(PatternFactory.GRADIENT_HLINEAR,
                    1f, Color.ORANGE.brighter().brighter(),  Color.ORANGE.darker().darker());
            // Extend image one degree west and east to properly cover the section edge
            sectionlayer.addRenderable(new SurfaceImage(sectionImage, Sector.fromDegrees(
                    Math.max(sector.getMinLatitude().degrees - 2, -90), Math.min(sector.getMaxLatitude().degrees + 2, 90),
                    sector.getMinLongitude().degrees - 1, sector.getMaxLongitude().degrees + 1)));
            // Shrink image sector to paint core only
            sectionlayer.addRenderable(new SurfaceImage(coreImage, Sector.fromDegrees(
                    sector.getMinLatitude().degrees + 1, sector.getMaxLatitude().degrees - 1,
                    sector.getMinLongitude().degrees + 2, sector.getMaxLongitude().degrees - 2)));
            insertBeforePlacenames(getWwd(), sectionlayer);

            // Turn off sky gradient
            for (Layer layer : model.getLayers())
                    if (layer instanceof SkyGradientLayer)
                            layer.setEnabled(false);

            // Update layer panel
            getLayerPanel().update(getWwd());

            // Max out orbit view far clipping distance to keep the whole globe visible
//            getWwd().getView().setFarClipDistance(globe.getDiameter() * 3);

        }
/*
        private class TruncatedEarthElevationModel extends EarthElevationModel
        {
            private Sector truncatedSector;
            private double truncatedElevation;

            public TruncatedEarthElevationModel(Sector truncatedSector, Double truncatedElevation)
            {
                super();
                this.truncatedSector = truncatedSector;
                this.truncatedElevation = truncatedElevation;
            }
            
            public double getElevation(Angle latitude, Angle longitude)
            {
                if (latitude == null || longitude == null)
                {
                    String message = Logging.getMessage("nullValue.LatLonIsNull");
                    Logging.logger().severe(message);
                    throw new IllegalArgumentException(message);
                }

                if (this.truncatedSector.contains(latitude, longitude))
                    return this.truncatedElevation;

                return super.getElevation(latitude, longitude);
            }

            public double getElevations(Sector sector, java.util.List<LatLon> latlons, double targetResolution, double[] buffer)
            {
                double resolutionAchieved = super.getElevations(sector, latlons, targetResolution, buffer);

                int i = 0;
                for (LatLon ll : latlons)
                {
                    if (this.truncatedSector.contains(ll))
                        buffer[i] = this.truncatedElevation;
                    i++;
                }

                return resolutionAchieved;
            }

            public double[] getMinAndMaxElevations(Sector sector)
            {
                double[] elevations = super.getMinAndMaxElevations(sector);
                if (this.truncatedSector.intersects(sector))
                    elevations[0] = this.truncatedElevation;
                return elevations;
            }
        }
*/
    }

    public static void main(String[] args)
    {
        ApplicationTemplate.start("World Wind Truncated Earth", AppFrame.class);
    }
}
