package ige.worldwind.globes;

import gov.nasa.worldwind.globes.*;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.avlist.AVList;
import gov.nasa.worldwind.avlist.AVListImpl;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.geom.Sector;


public class EarthGlobe  extends EllipsoidalGlobe
{
    public static final double WGS84_EQUATORIAL_RADIUS = 6378137.0; // ellipsoid equatorial getRadius, in meters
    public static final double WGS84_POLAR_RADIUS = 6378137.0; //6356752.3; // ellipsoid polar getRadius, in meters
    public static final double WGS84_ES = 0;//0.00669437999013; // eccentricity squared, semi-major axis

    public EarthGlobe(AVList elevationparams)
    {
/*		AVList elevparams = new AVListImpl();
		elevparams.setValue(AVKey.TILE_WIDTH, 150);
		elevparams.setValue(AVKey.TILE_HEIGHT, 150);
		elevparams.setValue(AVKey.DATA_CACHE_NAME, "Earth/srtm30pluszip");
		elevparams.setValue(AVKey.SERVICE, "http://worldwind25.arc.nasa.gov/wwelevation/wwelevation.aspx");
		elevparams.setValue(AVKey.DATASET_NAME, "srtm30pluszip");
		elevparams.setValue(AVKey.FORMAT_SUFFIX, ".bil");
		elevparams.setValue(AVKey.NUM_LEVELS, 10);
		elevparams.setValue(AVKey.NUM_EMPTY_LEVELS, 0);
		elevparams.setValue(AVKey.LEVEL_ZERO_TILE_DELTA, new LatLon(Angle.fromDegrees(20d), Angle.fromDegrees(20d)));
		elevparams.setValue(AVKey.SECTOR, Sector.FULL_SPHERE);
		elevparams.setValue(AVKey.SECTOR_RESOLUTION_LIMITS, new LevelSet.SectorResolution[]
		                                                                              {
				new LevelSet.SectorResolution(Sector.fromDegrees(24, 50, -125, -66.8), 9), // CONUS
				new LevelSet.SectorResolution(Sector.fromDegrees(18.5, 22.5, -160.5, -154.5), 9), // HI
				new LevelSet.SectorResolution(Sector.fromDegrees(17.8, 18.7, -67.4, -64.5), 9), // PR, VI
				new LevelSet.SectorResolution(Sector.fromDegrees(48, 108, -179.9, -128), 9), // AK
				new LevelSet.SectorResolution(Sector.fromDegrees(-54, 60, -180, 180), 8), // SRTM3
				new LevelSet.SectorResolution(Sector.FULL_SPHERE, 4) // SRTM30Plus
		                                                                              });
*/
        super(WGS84_EQUATORIAL_RADIUS, WGS84_POLAR_RADIUS, WGS84_ES, new ConfigurableElevationModel("SRTM 30+", elevationparams));
    }
}
