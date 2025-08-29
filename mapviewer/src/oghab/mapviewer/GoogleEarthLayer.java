package oghab.mapviewer;

import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.avlist.AVList;
import gov.nasa.worldwind.avlist.AVListImpl;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.layers.mercator.BasicMercatorTiledImageLayer;
import gov.nasa.worldwind.layers.mercator.MercatorSector;
import gov.nasa.worldwind.util.LevelSet;
import gov.nasa.worldwind.util.Tile;
import gov.nasa.worldwind.util.TileUrlBuilder;

import java.awt.image.BufferedImage;
import java.net.MalformedURLException;
import java.net.URL;

public class GoogleEarthLayer extends BasicMercatorTiledImageLayer {
    public static enum Dataset {
        // https://maps.googleapis.com/maps/api/js
//        AERIAL("Aerial", "a", ".jpg", "http://khm%d.google.com/kh/v=57&x=%d&y=%d&z=%d&s=Galile");
//        AERIAL("Aerial", "a", ".jpg", "https://khms%d.google.com/kh/v=%s&src=app&x=%d&y=%d&z=%d");
        AERIAL("Aerial", "a", ".jpg", "https://khms%d.google.com/kh/v=129&src=app&x=%d&y=%d&z=%d");

        public final String label;
        public final String dataset;
        public final String formatSuffix;
        public final String urlFormat;

        private Dataset(String label, String dataset, String formatSuffix, String urlFormat)
        {
            this.label = label;
            this.dataset = dataset;
            this.formatSuffix = formatSuffix;
            this.urlFormat = urlFormat;
        }
    }

    private final Dataset dataset;

    public GoogleEarthLayer() {
        this(Dataset.AERIAL);
    }

    public GoogleEarthLayer(Dataset dataset) {
        super(makeLevels(dataset));
        if (dataset == null)
            throw new NullPointerException("Dataset cannot be null");
        this.dataset = dataset;
        this.setValue(AVKey.DISPLAY_NAME, "Google Earth " + dataset.label);
//        this.setSplitScale(1.3);
    }

    protected static LevelSet makeLevels(Dataset dataset) {
        AVList params = new AVListImpl();

        params.setValue(AVKey.TILE_WIDTH, 256);
        params.setValue(AVKey.TILE_HEIGHT, 256);
        params
                .setValue(AVKey.DATA_CACHE_NAME, "Google Earth7 "
                        + dataset.label);
        params.setValue(AVKey.SERVICE, "http://kh0.google.com/kh?n=404&v=17&t=t");
        params.setValue(AVKey.DATASET_NAME, dataset.dataset);
        params.setValue(AVKey.FORMAT_SUFFIX, dataset.formatSuffix);
        params.setValue(AVKey.NUM_LEVELS, 16);
        params.setValue(AVKey.NUM_EMPTY_LEVELS, 0);
        params.setValue(AVKey.LEVEL_ZERO_TILE_DELTA, new LatLon(Angle.fromDegrees(22.5d), Angle.fromDegrees(45d)));
        params.setValue(AVKey.SECTOR, new MercatorSector(-1.0, 1.0, Angle.NEG180, Angle.POS180));
        params.setValue(AVKey.TILE_URL_BUILDER, new URLBuilder(dataset));
        params.setValue(AVKey.DISPLAY_NAME, "Google Maps " + dataset.label);

        return new LevelSet(params);
    }

    public static class URLBuilder implements TileUrlBuilder {

        private Dataset dataset;

        public URLBuilder(Dataset dataset) {
            this.dataset = dataset;
        }

        public URL getURL(Tile tile, String imageFormat)
                throws MalformedURLException {
            LatLon center = tile.getSector().getCentroid();
            String urlString = computeTileUrl(dataset.urlFormat, center
                    .getLatitude().degrees, center.getLongitude().degrees, tile
                    .getLevelNumber() + 3);
            System.out.println("Need:" + urlString);
            URL url = new URL(urlString);
            return url;
        }

        private static String computeTileUrl(String urlFormat, double lat, double lon, int zoom) {

            if (lon > 180.0) {
                lon -= 360.0;
            }

            lon = (180.0 + lon) / 360.0;
            lat = 0.5
                    - Math.log(Math.tan((Math.PI / 4.0)
                            + ((Math.PI * lat) / (2.0 * 180.0)))) / (2.0 * Math.PI);

            int scale = 1 << (int) zoom;

            // can just truncate to integer, this looses the fractional
            // "pixel offset"
            int x = (int) (lon * scale);
            int y = (int) (lat * scale);
            return String.format(urlFormat, (int) (Math.random() * 4), x, y, zoom);
        }

    }

    protected boolean isTileValid(BufferedImage image) {
        // return false if the tile is white (this will mark the tile as absent)
        boolean white = true;
        // JPEG compression will cause white to be not quite white
        String lowercaseFormat = getDataset().formatSuffix.toLowerCase();
        int threshold = lowercaseFormat.contains("jpg")
                || lowercaseFormat.contains("jpeg") ? 200 : 250;
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                int rgb = image.getRGB(x, y);
                white = isWhite(rgb, threshold);
                if (!white)
                    break;
            }
            if (!white)
                break;
        }
        return !white;
    }

    private boolean isWhite(int rgb, int threshold) {
        int r = (rgb >> 16) & 0xff;
        int g = (rgb >> 8) & 0xff;
        int b = (rgb >> 0) & 0xff;
        return r + b + g > threshold * 3;
    }

    public Dataset getDataset() {
        return dataset;
    }
}
