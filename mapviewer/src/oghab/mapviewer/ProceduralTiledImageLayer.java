package oghab.mapviewer;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/*

 Copyright (C) 2001, 2006 United States Government
 as represented by the Administrator of the
 National Aeronautics and Space Administration.
 All Rights Reserved.
 */

import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.avlist.AVList;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.layers.mercator.BasicMercatorTiledImageLayer;
import gov.nasa.worldwind.layers.mercator.MercatorSector;
import gov.nasa.worldwind.layers.mercator.MercatorTextureTile;
import gov.nasa.worldwind.util.LevelSet;

/**
 * 
 * @author Alessio Iannone - Adapt to WorldWind 2.0
 * @author Patrick Murris
 * @version $Id:$
 */
public abstract class ProceduralTiledImageLayer extends BasicMercatorTiledImageLayer {
	private boolean imageTransformation = true;

	public ProceduralTiledImageLayer(LevelSet levelSet) {
		super(levelSet);

	}

	public ProceduralTiledImageLayer(AVList params) {
		super(params);
	}

	abstract BufferedImage createTileImage(MercatorTextureTile tile, BufferedImage image);

	public void setImageTransformation(boolean enableImageTransformation) {
		this.imageTransformation = enableImageTransformation;
	}

	public boolean isImageTransformation() {
		return imageTransformation;
	}

	@Override
	protected void downloadTexture(final MercatorTextureTile tile) {

		final File outFile = WorldWind.getDataFileStore().newFile(tile.getPath());
		if (outFile == null)
			return;

		if (outFile.exists())
			return;

		// Create and save tile texture image
		BufferedImage image = new BufferedImage(tile.getLevel().getTileWidth(), tile.getLevel().getTileHeight(),
				BufferedImage.TYPE_4BYTE_ABGR);
		image = createTileImage(tile, image);
		if (image != null) {
			if (isImageTransformation()) {
				image = transform(image, tile.getMercatorSector());
			}
			try {
				ImageIO.write(image, "png", outFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private BufferedImage transform(BufferedImage image, MercatorSector sector) {
		// Force to be INT_ARGB

		BufferedImage trans = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
		double miny = sector.getMinLatPercent();
		double maxy = sector.getMaxLatPercent();
		for (int y = 0; y < image.getHeight(); y++) {
			double sy = 1.0 - y / (double) (image.getHeight() - 1);
			Angle lat = Angle.fromRadians(sy * sector.getDeltaLatRadians() + sector.getMinLatitude().radians);
			double dy = 1.0 - (MercatorSector.gudermannianInverse(lat) - miny) / (maxy - miny);
			dy = Math.max(0.0, Math.min(1.0, dy));
			int iy = (int) (dy * (image.getHeight() - 1));

			for (int x = 0; x < image.getWidth(); x++) {
				trans.setRGB(x, y, image.getRGB(x, iy));
			}
		}
		return trans;
	}

}
