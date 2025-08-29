package oghab.mapviewer;

import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.avlist.AVList;
import gov.nasa.worldwind.avlist.AVListImpl;
import java.net.MalformedURLException;
import java.net.URL;

//import gov.nasa.worldwind.AVKey;
//import gov.nasa.worldwind.AVList;
//import gov.nasa.worldwind.AVListImpl;
//import gov.nasa.worldwind.Level;
//import gov.nasa.worldwind.LevelSet;
//import gov.nasa.worldwind.Tile;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.layers.BasicTiledImageLayer;
import gov.nasa.worldwind.layers.TextureTile;
import gov.nasa.worldwind.layers.TiledImageLayer;
import gov.nasa.worldwind.render.DrawContext;
import gov.nasa.worldwind.util.LevelSet;
import gov.nasa.worldwind.util.Tile;
import gov.nasa.worldwind.util.TileUrlBuilder;

public class WMSLayer  extends BasicTiledImageLayer
{   
    String dataset;

    public WMSLayer(String workspace, String service, String dataset) {
        super(makeLevels(new URLBuilder(), workspace, service, dataset));
        this.setUseTransparentTextures(true);
        this.dataset = dataset;
    }
	
    private static LevelSet makeLevels(URLBuilder urlBuilder, String workspace, String service, String dataset)
    {
        AVList params = new AVListImpl();

        params.setValue(AVKey.TILE_WIDTH, 256);
        params.setValue(AVKey.TILE_HEIGHT, 256);
//        params.setValue(AVKey.DATA_CACHE_NAME, "MapViewer/WMS/MapViewer/gis_osm_roads_free_1");
//        params.setValue(AVKey.SERVICE, "http://localhost:8080/geoserver/MapViewer/wms");
//        params.setValue(AVKey.DATASET_NAME, "MapViewer:gis_osm_roads_free_1");
        params.setValue(AVKey.DATA_CACHE_NAME, "MapViewer/WMS/"+workspace+"/"+dataset);
        params.setValue(AVKey.SERVICE, service);
        params.setValue(AVKey.DATASET_NAME, workspace+":"+dataset);
        params.setValue(AVKey.FORMAT_SUFFIX, ".png");
        params.setValue(AVKey.NUM_LEVELS, 20);
        params.setValue(AVKey.NUM_EMPTY_LEVELS, 0);
        params.setValue(AVKey.LEVEL_ZERO_TILE_DELTA, new LatLon(Angle.fromDegrees(180d), Angle.fromDegrees(180d)));
        params.setValue(AVKey.SECTOR, Sector.FULL_SPHERE);
        params.setValue(AVKey.TILE_URL_BUILDER, urlBuilder);

        return new LevelSet(params);
    }

    private static class URLBuilder implements TileUrlBuilder
    {
        public URL getURL(Tile tile, String imageFormat) throws MalformedURLException
        {
            StringBuffer sb = new StringBuffer(tile.getLevel().getService());
            if (sb.lastIndexOf("?") != sb.length() - 1)
                sb.append("?");            
            sb.append("version=1.1.1");
            sb.append("&request=GetMap");
            sb.append("&layers=");
            sb.append(tile.getLevel().getDataset());
            sb.append("&srs=EPSG:4326");
            sb.append("&width=");
            sb.append(tile.getLevel().getTileWidth());
            sb.append("&height=");
            sb.append(tile.getLevel().getTileHeight());

            Sector s = tile.getSector();
            sb.append("&bbox=");
            sb.append(s.getMinLongitude().getDegrees());
            sb.append(",");
            sb.append(s.getMinLatitude().getDegrees());
            sb.append(",");
            sb.append(s.getMaxLongitude().getDegrees());
            sb.append(",");
            sb.append(s.getMaxLatitude().getDegrees());

            sb.append("&format=image/png");
            sb.append("&SERVICE=WMS");
//            sb.append("&bgcolor=0x000000");
            
            sb.append("&transparent=TRUE");
            
          //  System.out.println(sb);

            return new java.net.URL(sb.toString());
        }
    }

    @Override
    public String toString()
    {
//        return "WMS";
        return dataset;
    }
}
