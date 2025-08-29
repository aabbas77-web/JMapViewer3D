package ige.worldwind.globes;

import gov.nasa.worldwind.globes.*;
import gov.nasa.worldwind.avlist.*;
import gov.nasa.worldwind.geom.*;
import gov.nasa.worldwind.terrain.BasicElevationModel;
import gov.nasa.worldwind.util.LevelSet;

public class ConfigurableElevationModel extends BasicElevationModel {
	private static double HEIGHT_OF_MT_EVEREST = 8850d; // meters
	private static double DEPTH_OF_MARIANAS_TRENCH = -11000d; // meters
	private String name = "Elevation Layer";
	
	public ConfigurableElevationModel(AVList params)
	{
//		super(makeLevels(params), DEPTH_OF_MARIANAS_TRENCH, HEIGHT_OF_MT_EVEREST);
//		this.setNumExpectedValuesPerTile(22500);
		super(makeLevels(params));
	}
	
	public ConfigurableElevationModel(String name, AVList params)
	{
//		super(makeLevels(params), DEPTH_OF_MARIANAS_TRENCH, HEIGHT_OF_MT_EVEREST);
//		this.setNumExpectedValuesPerTile(22500);
		super(makeLevels(params));
		this.setName(name);
	}

	private static LevelSet makeLevels(AVList parameters)
	{
		AVList params = parameters;
/*
		params.setValue(AVKey.TILE_WIDTH, 150);
		params.setValue(AVKey.TILE_HEIGHT, 150);
		params.setValue(AVKey.DATA_CACHE_NAME, "Earth/srtm30pluszip");
		params.setValue(AVKey.SERVICE, "http://worldwind25.arc.nasa.gov/wwelevation/wwelevation.aspx");
		params.setValue(AVKey.DATASET_NAME, "srtm30pluszip");
		params.setValue(AVKey.FORMAT_SUFFIX, ".bil");
		params.setValue(AVKey.NUM_LEVELS, 10);
		params.setValue(AVKey.NUM_EMPTY_LEVELS, 0);
		params.setValue(AVKey.LEVEL_ZERO_TILE_DELTA, new LatLon(Angle.fromDegrees(20d), Angle.fromDegrees(20d)));
		params.setValue(AVKey.SECTOR, Sector.FULL_SPHERE);
		params.setValue(AVKey.SECTOR_RESOLUTION_LIMITS, new LevelSet.SectorResolution[]
		                                                                              {
				new LevelSet.SectorResolution(Sector.fromDegrees(24, 50, -125, -66.8), 9), // CONUS
				new LevelSet.SectorResolution(Sector.fromDegrees(18.5, 22.5, -160.5, -154.5), 9), // HI
				new LevelSet.SectorResolution(Sector.fromDegrees(17.8, 18.7, -67.4, -64.5), 9), // PR, VI
				new LevelSet.SectorResolution(Sector.fromDegrees(48, 108, -179.9, -128), 9), // AK
				new LevelSet.SectorResolution(Sector.fromDegrees(-54, 60, -180, 180), 8), // SRTM3
				new LevelSet.SectorResolution(Sector.FULL_SPHERE, 4) // SRTM30Plus
		                                                                              });
*/
		return new LevelSet(params);
	}

	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String toString()
	{
		return this.getName();
	}
}
