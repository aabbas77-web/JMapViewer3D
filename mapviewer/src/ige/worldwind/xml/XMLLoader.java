package ige.worldwind.xml;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.font.TextAttribute;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import org.w3c.dom.*;
import org.xml.sax.*;

import javax.xml.parsers.*;

import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.avlist.AVList;
import gov.nasa.worldwind.avlist.AVListImpl;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.layers.*;
import gov.nasa.worldwind.render.SurfacePolygon;
import gov.nasa.worldwind.render.SurfacePolyline;
import gov.nasa.worldwind.util.LevelSet;
import gov.nasa.worldwind.util.Logging;
import gov.nasa.worldwind.util.Tile;
import gov.nasa.worldwind.util.TileUrlBuilder;

import ige.worldwind.layers.*;
import ige.worldwind.globes.*;

public class XMLLoader {
	
	public XMLLoader()
	{
		
	}
	
//--------------------Icon Loading part --------------------//
	/**
	 * Load an Icon layer from an xml node
	 * @param Node : the xml node
	 * @return IconLayer : the icon layer
	 */
	public IconLayer loadIconFromNode(Node node)
	{
		//Create the layer with default name
    	IconLayer iclayer = new IconLayer();
    	iclayer.setName("Icon");
		
		//Read node
    	Node child = node;
    	Node n;
      	n = findChildByName(child, "Name");
      	String name = n.getTextContent();
      	n = findChildByName(child, "Latitude");
    	double latitude = Double.parseDouble(findChildByName(n, "Value").getTextContent());
      	n = findChildByName(child, "Longitude");
    	double longitude = Double.parseDouble(findChildByName(n, "Value").getTextContent());
      	n = findChildByName(child, "DistanceAboveSurface");
      	double distanceAboveSurface = Double.parseDouble(n.getTextContent());
      	n = findChildByName(child, "TextureFilePath");
      	String textureFilePath = n.getTextContent();
      	n = findChildByName(child, "TextureWidthPixels");
      	int textureWidthPixels = Integer.parseInt(n.getTextContent());
      	n = findChildByName(child, "TextureHeightPixels");
      	int textureHeightPixels = Integer.parseInt(n.getTextContent());
      	n = findChildByName(child, "IconWidthPixels");
      	int iconWidthPixels = Integer.parseInt(n.getTextContent());
      	n = findChildByName(child, "IconHeightPixels");
      	int iconHeightPixels = Integer.parseInt(n.getTextContent());
    	String clickableUrl = "";
      	n = findChildByName(child, "ClickableUrl");
      	if(n != null) clickableUrl = n.getTextContent();
    	String description = "";
      	n = findChildByName(child, "Description");
      	if(n != null) description = n.getTextContent();
      	double min=Double.NaN, max=Double.NaN;
      	n = findChildByName(child, "MinimumDisplayAltitude");
      	if(n != null) min = Double.parseDouble(n.getTextContent());
      	n = findChildByName(child, "MaximumDisplayAltitude");
      	if(n != null) max = Double.parseDouble(n.getTextContent());
      	
      	// Add icon
      	ClickableIcon icon = new ClickableIcon(textureFilePath,
                new Position(Angle.fromDegrees(latitude), Angle.fromDegrees(longitude), distanceAboveSurface));
        icon.setHighlightScale(1.5);
        icon.setToolTipFont(makeToolTipFont());
        icon.setToolTipText(name);
        //if(description.length() >  0) icon.setToolTipText(name + " : " + description);
        icon.setToolTipTextColor(java.awt.Color.DARK_GRAY);
        icon.setSize(new Dimension(iconWidthPixels, iconHeightPixels));
        icon.setName(name);
        icon.setDescription(description);
        icon.setUrl(clickableUrl);
        icon.setParent(iclayer);
        if (min!=Double.NaN) iclayer.setMinActiveAltitude(min);
        if (max!=Double.NaN) iclayer.setMaxActiveAltitude(max);
        iclayer.addIcon(icon);  
        iclayer.setName(name);
    	return iclayer;
	}
	
    // TODO: make font and color configurable
    private Font makeToolTipFont()
    {
        HashMap<TextAttribute, Object> fontAttributes = new HashMap<TextAttribute, Object>();
        fontAttributes.put(TextAttribute.BACKGROUND, new java.awt.Color(0.4f, 0.4f, 0.4f, 1f));
        return Font.decode("Arial-BOLD-14").deriveFont(fontAttributes);
    }

//  --------------------Raster Loading part --------------------//
	/**
	 * Load a Raster layer from an xml node
	 * @param Node : the xml node
	 * @return BasicTiledImageLayer : the raster layer
	 */
	public BasicTiledImageLayer loadRasterFromNode(Node node)
	{
	 	// Create layer
	 	AVList params = loadRasterParams(node);
	 	LevelSet levels = new LevelSet(params);
	 	BasicTiledImageLayer layer = new BasicTiledImageLayer(levels);
	 	layer.setEnabled(true);
	 	// Layer name
	 	layer.setName((String)params.getValue(AVKey.DISPLAY_NAME));
	 	// Layer enabled
	 	Node attr = node.getAttributes().getNamedItem("ShowAtStartup");
		if(attr != null) layer.setEnabled(attr.getTextContent().compareToIgnoreCase("True") == 0 ? true : false);
	 	layer.setUseTransparentTextures(true);
	 	return layer;

	}
	
	private AVList loadRasterParams(Node node)
	 {
	     // Extract useful info
	     Node child;
	     // Name
	     child = findChildByName(node, "Name");
	     String layerName = child.getTextContent();        
	     // Sector
	     Sector sector = Sector.FULL_SPHERE;
	     child = findChildByName(node, "BoundingBox");
	     if(child != null) {
	     	Node n;
	       	n = findChildByName(child, "North");
	     	double north = Double.parseDouble(findChildByName(n, "Value").getTextContent());
	       	n = findChildByName(child, "South");
	     	double south = Double.parseDouble(findChildByName(n, "Value").getTextContent());
	       	n = findChildByName(child, "West");
	     	double west = Double.parseDouble(findChildByName(n, "Value").getTextContent());
	       	n = findChildByName(child, "East");
	     	double east = Double.parseDouble(findChildByName(n, "Value").getTextContent());
	     	sector = new Sector(Angle.fromDegrees(south), Angle.fromDegrees(north), Angle.fromDegrees(west), Angle.fromDegrees(east));
	     }
	     // Images and server access
	     Node imageTileService, wmsAccessor;
	     double levelZeroTileSizeDegree;
	     int numLevels;
	     int tileSize = 512;
	     String service, datasetName, formatSuffix;
	     String wmsServiceName = "", wmsVersion = "", wmsImageFormat = "", wmsTransparency = "", wmsStyle = "";
	     child = findChildByName(node, "ImageAccessor");
	     if(child != null) {
	     	Node n;
	       	n = findChildByName(child, "LevelZeroTileSizeDegrees");
	       	levelZeroTileSizeDegree = Double.parseDouble(n.getTextContent());
	       	n = findChildByName(child, "NumberLevels");
	       	numLevels = Integer.parseInt(n.getTextContent());
	       	n = findChildByName(child, "TextureSizePixels");
	       	tileSize = Integer.parseInt(n.getTextContent());
	       	n = findChildByName(child, "ImageFileExtension");
	       	formatSuffix = "." + n.getTextContent();
	       	imageTileService = findChildByName(child, "ImageTileService");
	       	wmsAccessor = findChildByName(child, "WMSAccessor");
	       	if(imageTileService != null) {
	       		// WW image tile service
	       		n = imageTileService;
	       		Node n2;
	           	n2 = findChildByName(n, "ServerUrl");
	           	service = n2.getTextContent();
	           	n2 = findChildByName(n, "DataSetName");
	           	datasetName = n2.getTextContent();
	       	} else if(wmsAccessor != null) {
	       		// WMS server
	       		n = wmsAccessor;
	       		Node n2;
	           	n2 = findChildByName(n, "ServerGetMapUrl");
	           	service = n2.getTextContent();
	           	n2 = findChildByName(n, "WMSLayerName");
	           	datasetName = n2.getTextContent();
	           	n2 = findChildByName(n, "ServiceName");
	           	if(n2 != null) wmsServiceName = n2.getTextContent();
	           	n2 = findChildByName(n, "Version");
	           	if(n2 != null) wmsVersion = n2.getTextContent();
	           	n2 = findChildByName(n, "ImageFormat");
	           	if(n2 != null) wmsImageFormat = n2.getTextContent();
	           	n2 = findChildByName(n, "UseTransparency");
	           	if(n2 != null) wmsTransparency = n2.getTextContent();          		
	           	n2 = findChildByName(n, "WMSLayerStyle");
	           	if(n2 != null) wmsStyle = n2.getTextContent();          		
	       	} else {
	       		// No ImageTileService or WMSAccessor node
	       		// TODO: throw exception
	       		return null;
	       	}
	     } else {
	     	// No Image accessor node
	     	// TODO: throw exception
	     	return null;
	     }
	     
	     double deuxpuissel = 36/levelZeroTileSizeDegree;
	     int emptylevels =  (int) Math.floor( Math.log(deuxpuissel)/Math.log(2) ); 
	     Angle tileDelta = Angle.fromDegrees(levelZeroTileSizeDegree * Math.pow(2,emptylevels));
	 //    System.out.println("layer "+layerName +" / lzstd : "+levelZeroTileSizeDegree +" / empty levels : " + emptylevels);
	     // Compose level param
	     AVList params = new AVListImpl();
	     params.setValue(AVKey.DISPLAY_NAME, layerName);
	     params.setValue(AVKey.TILE_WIDTH, tileSize);
	     params.setValue(AVKey.TILE_HEIGHT, tileSize);
	     params.setValue(AVKey.DATA_CACHE_NAME, "Earth/" + layerName); // TODO: get cache root from somewhere
	     params.setValue(AVKey.SERVICE, service);
	     params.setValue(AVKey.DATASET_NAME, datasetName);
	     params.setValue(AVKey.FORMAT_SUFFIX, formatSuffix);
	     params.setValue(AVKey.NUM_LEVELS, numLevels+emptylevels);
	     params.setValue(AVKey.NUM_EMPTY_LEVELS, emptylevels); // TODO: compute empty levels from levelZeroTileSize
	     params.setValue(AVKey.LEVEL_ZERO_TILE_DELTA, new LatLon(tileDelta, tileDelta));
	     params.setValue(AVKey.SECTOR, sector);
	/*
	     params.setValue(AVKey.DISPLAY_NAME, "landsat i3 bis");
	     params.setValue(AVKey.TILE_WIDTH, 512);
	     params.setValue(AVKey.TILE_HEIGHT, 512);
	     params.setValue(AVKey.CACHE_NAME, "Earth/NASA LandSat I3");
	     params.setValue(AVKey.SERVICE, "http://worldwind25.arc.nasa.gov/lstile/lstile.aspx");
	     params.setValue(AVKey.DATASET_NAME, "esat_worlddds");
	     params.setValue(AVKey.FORMAT_SUFFIX, ".dds");
	     params.setValue(AVKey.NUM_LEVELS, 10);
	     params.setValue(AVKey.NUM_EMPTY_LEVELS, 4);
	     params.setValue(AVKey.LEVEL_ZERO_TILE_DELTA, new LatLon(Angle.fromDegrees(36d), Angle.fromDegrees(36d)));
	     params.setValue(AVKey.SECTOR, Sector.FULL_SPHERE);
	     params.setValue(AVKey.EXPIRY_TIME, new GregorianCalendar(2007, 2, 22).getTimeInMillis());
	*/
	     // WMS params
	     if(wmsAccessor != null) {
	     	// TODO: use static Level string constants
	         params.setValue("WMS_SERVICE_NAME", wmsServiceName);
	         params.setValue("WMS_VERSION", wmsVersion);
	         params.setValue("WMS_IMAGE_FORMAT", wmsImageFormat);
	         params.setValue("WMS_TRANSPARENCY", wmsTransparency);
	         params.setValue("WMS_STYLE", wmsStyle);
	         params.setValue(AVKey.TILE_URL_BUILDER, new URLBuilderWMS());
		
	     }

	     // Return LevelSet params
	     return params;
	 }

	private static class URLBuilderWMS implements TileUrlBuilder
	 {
	     public URL getURL(Tile tile) throws MalformedURLException
	     {
	         StringBuffer sb = new StringBuffer(tile.getLevel().getService());
	         if (sb.lastIndexOf("?") != sb.length() - 1)
	             sb.append("?");
	         sb.append("request=GetMap");
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

	         sb.append("&format=");
	         sb.append(tile.getLevel().getParams().getValue("WMS_IMAGE_FORMAT"));
	         sb.append("&version=");
	         sb.append(tile.getLevel().getParams().getValue("WMS_VERSION"));
	         sb.append("&styles=");
	         sb.append(tile.getLevel().getParams().getValue("WMS_STYLE"));
	         // TODO: sort out what parameters are required and in which case...
	         //sb.append("&service=");
	         //sb.append(tile.getLevel().getParams().getValue("WMS_SERVICE_NAME"));
	         //sb.append("&transparent=");
	         //sb.append(tile.getLevel().getParams().getValue("WMS_TRANSPARENCY"));

	         System.out.println(sb.toString());
	         
	         return new java.net.URL(sb.toString());
	     }

        @Override
        public URL getURL(Tile tile, String string) throws MalformedURLException {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
	 }

//	--------------------Polygon/Polyline Loading part --------------------//
	public RenderableLayer loadPolylineFromNode(Node node)
	{
		//Create the layer with default name
		RenderableLayer layer = new RenderableLayer();
		layer.setName("Polyline layer");
		
		//Read the node
		Node noeud = node;
    	Node n;
      	n = findChildByName(noeud, "Name");
      	String name = n.getTextContent();

     	boolean extrude = false;
    	n = findChildByName(noeud, "Extrude");
     	if (n!=null) extrude= Boolean.parseBoolean(n.getTextContent());
     	
     	int extrudeheight = 0;
    	n = findChildByName(noeud, "ExtrudeHeight");
     	if (n!=null) extrudeheight= Integer.parseInt(n.getTextContent());
     	
     	boolean extrudeupwards =true;
    	n = findChildByName(noeud, "ExtrudeUpwards");
     	if (n!=null)  extrudeupwards = Boolean.parseBoolean(n.getTextContent());

     	boolean extrudetoground =true;
    	n = findChildByName(noeud, "ExtrudeToGround");
     	if (n!=null)  extrudetoground = Boolean.parseBoolean(n.getTextContent());
      	
     	int distabovesurf = 0;
    	n = findChildByName(noeud, "DistanceAboveSurface");
     	if (n!=null) distabovesurf = Integer.parseInt(n.getTextContent());
     	
      	double min=Double.NaN, max=Double.NaN;
      	n = findChildByName(noeud, "MinimumDisplayAltitude");
      	if(n != null) min = Double.parseDouble(n.getTextContent());
      	n = findChildByName(noeud, "MaximumDisplayAltitude");
      	if(n != null) max = Double.parseDouble(n.getTextContent());

      	int linewidth=1;
      	n = findChildByName(noeud, "LineWidth");
      	if(n != null) linewidth = Integer.parseInt(n.getTextContent());
      	
      	n = findChildByName(noeud, "FeatureColor");
      	int color_red= ( findChildByName(n, "Red")==null ? 0 :  Integer.parseInt(findChildByName(n, "Red").getTextContent()) );
      	int color_green = ( findChildByName(n, "Green")==null ? 0 :  Integer.parseInt(findChildByName(n, "Green").getTextContent()) );
     	int color_blue = ( findChildByName(n, "Blue")==null ? 0 :  Integer.parseInt(findChildByName(n, "Blue").getTextContent()) );
     	int color_alpha = ( findChildByName(n, "Alpha")==null ? 255 :  Integer.parseInt(findChildByName(n, "Alpha").getTextContent()) );
     	
      	String imageuri="";
      	n = findChildByName(noeud, "ImageUri");
      	if(n != null) imageuri = n.getTextContent();
      	
     	boolean outline =false;
    	n = findChildByName(noeud, "Outline");
     	if (n!=null)  outline = Boolean.parseBoolean(n.getTextContent());
 
     	String altitudemode="clampedtoground";
      	n = findChildByName(noeud, "AltitudeMode");
      	if(n != null) altitudemode = n.getTextContent();
      	
     	n = findChildByName(noeud, "Opacity");
    	int opacity = ( n==null ? 255 : Integer.parseInt(n.getTextContent()) );

    	n = findChildByName(noeud, "LineString");
      	String linestring = findChildByName(n, "posList").getTextContent();
      	
      	//ArrayList<Position> values = getDoubleValuesWithAltitude(linestring);
      	ArrayList<LatLon> values = getDoubleValues(linestring);
      	
      	if (values == null)
      	{
	        String message = Logging.getMessage("nullValue.DataSetIsNull");
	        Logging.logger().severe(message);
	        throw new IllegalArgumentException(message);
      	}
      	
        if (values.size() < 2)
        {
	        String message = Logging.getMessage("GeoRSS.InvalidCoordinateCount" + node.getLocalName());
	        Logging.logger().severe(message);
	        throw new IllegalArgumentException(message);
        }

        Color color = new Color(color_red,color_green,color_blue, color_alpha);
//AliSoft        SurfacePolyline polyline = new SurfacePolyline(values,color,color);
        SurfacePolyline polyline = new SurfacePolyline(values);
//AliSoft        polyline.setAntiAlias(true);
        layer.addRenderable(polyline);
        layer.setMinActiveAltitude(min);
        layer.setMaxActiveAltitude(max);
        return layer;
	}
	
	public RenderableLayer loadPolygonFromNode(Node node)
	{
		//Create the layer with default name
		RenderableLayer layer = new RenderableLayer();
		layer.setName("Polygon layer");
		
		//Read the node
		Node noeud = node;
    	Node n;
      	n = findChildByName(noeud, "Name");
      	String name = n.getTextContent();
      	
      	String altmode = "clampedtoground";
      	n = findChildByName(noeud, "AltitudeMode");
      	if (n!=null) altmode = n.getTextContent();
      	
     	boolean extrude = false;
    	n = findChildByName(noeud, "Extrude");
     	if (n!=null) extrude= Boolean.parseBoolean(n.getTextContent());
     	
     	int extrudeheight = 0;
    	n = findChildByName(noeud, "ExtrudeHeight");
     	if (n!=null) extrudeheight= Integer.parseInt(n.getTextContent());
     	
     	boolean extrudeupwards =true;
    	n = findChildByName(noeud, "ExtrudeUpwards");
     	if (n!=null)  extrudeupwards = Boolean.parseBoolean(n.getTextContent());
     	
     	int distabovesurf = 0;
    	n = findChildByName(noeud, "DistanceAboveSurface");
     	if (n!=null) distabovesurf = Integer.parseInt(n.getTextContent());
     	
      	double min=Double.NaN, max=Double.NaN;
      	n = findChildByName(noeud, "MinimumDisplayAltitude");
      	if(n != null) min = Double.parseDouble(n.getTextContent());
      	n = findChildByName(noeud, "MaximumDisplayAltitude");
      	if(n != null) max = Double.parseDouble(n.getTextContent());
      	
      	n = findChildByName(noeud, "exterior");
      	Node nn = findChildByName(n, "LinearRing");
      	String poslist = findChildByName(nn, "posList").getTextContent();
      	
      	int linewidth=1;
      	n = findChildByName(noeud, "LineWidth");
      	if(n != null) linewidth = Integer.parseInt(n.getTextContent());
      	
      	n = findChildByName(noeud, "FeatureColor");
      	int color_red= ( findChildByName(n, "Red")==null ? 0 :  Integer.parseInt(findChildByName(n, "Red").getTextContent()) );
      	int color_green = ( findChildByName(n, "Green")==null ? 0 :  Integer.parseInt(findChildByName(n, "Green").getTextContent()) );
     	int color_blue = ( findChildByName(n, "Blue")==null ? 0 :  Integer.parseInt(findChildByName(n, "Blue").getTextContent()) );
     	int color_alpha = ( findChildByName(n, "Alpha")==null ? 255 :  Integer.parseInt(findChildByName(n, "Alpha").getTextContent()) );
      	n = findChildByName(noeud, "OutlineColor");
      	int outl_red= ( findChildByName(n, "Red")==null ? 0 :  Integer.parseInt(findChildByName(n, "Red").getTextContent()) );
      	int outl_green = ( findChildByName(n, "Green")==null ? 0 :  Integer.parseInt(findChildByName(n, "Green").getTextContent()) );
     	int outl_blue = ( findChildByName(n, "Blue")==null ? 0 :  Integer.parseInt(findChildByName(n, "Blue").getTextContent()) );
     	int outl_alpha = ( findChildByName(n, "Alpha")==null ? 255 :  Integer.parseInt(findChildByName(n, "Alpha").getTextContent()) );
     	
     	n = findChildByName(noeud, "Opacity");
    	int opacity = ( n==null ? 255 : Integer.parseInt(n.getTextContent()) );
    	
     	n = findChildByName(noeud, "Outline");
    	boolean isoutline = ( n==null ? false : Boolean.parseBoolean(n.getTextContent()) );
 

      	
      	//ArrayList<Position> values = getDoubleValuesWithAltitude(linestring);
      	ArrayList<LatLon> values = getDoubleValues(poslist);
      	
      	if (values == null)
      	{
	        String message = Logging.getMessage("nullValue.DataSetIsNull");
	        Logging.logger().severe(message);
	        throw new IllegalArgumentException(message);
      	}
      	
        if (values.size() < 2)
        {
	        String message = Logging.getMessage("GeoRSS.InvalidCoordinateCount" + node.getLocalName());
	        Logging.logger().severe(message);
	        throw new IllegalArgumentException(message);
        }

        layer.setName(name);
        Color color = new Color(color_red,color_green,color_blue, color_alpha);
        Color outlColor = new Color(outl_red, outl_green, outl_blue, outl_alpha);
//AliSoft        SurfacePolygon polygon = new SurfacePolygon(values,color,outlColor);
        SurfacePolygon polygon = new SurfacePolygon(values);
//AliSoft        polygon.setAntiAlias(true);
        layer.addRenderable(polygon);
        layer.setMinActiveAltitude(min);
        layer.setMaxActiveAltitude(max);
      	return layer;
	}

    private ArrayList<LatLon> getDoubleValues(String stringValues)
    {
        String[] tokens = stringValues.trim().split("[ \n]");
        if (tokens.length < 1)
            return null;

        ArrayList<LatLon> arl = new ArrayList<LatLon>();
        for (String s : tokens)
        {
//        	System.out.println(s);
        	
            if (s == null || s.length() < 1)
                continue;

            String[] sequence = s.trim().split(",");
            
            double dlat, dlon, dalt;
            try
            {
                dlon = Double.parseDouble(sequence[0]);
                dlat = Double.parseDouble(sequence[1]);
                dalt = sequence.length==3 ? Double.parseDouble(sequence[2]) : 0d;
 //               System.out.println(Position.fromDegrees(dlat,dlon,dalt).toString());
                arl.add(LatLon.fromDegrees(dlat,dlon));
            }
            catch (NumberFormatException e)
            {
 //               String message = WorldWind.retrieveErrMsg("GeoRSS.NumberFormatException" + s);
 //               WorldWind.logger().log(java.util.logging.Level.FINE, message);
                continue;
            }
       }
       return arl;
    }
    
    private ArrayList<Position> getDoubleValuesWithAltitude(String stringValues)
    {
        String[] tokens = stringValues.trim().split("[ \n]");
        if (tokens.length < 1)
            return null;

        ArrayList<Position> arl = new ArrayList<Position>();
        for (String s : tokens)
        {
//        	System.out.println(s);
        	
            if (s == null || s.length() < 1)
                continue;

            String[] sequence = s.trim().split(",");
            
            double dlat, dlon, dalt;
            try
            {
                dlon = Double.parseDouble(sequence[0]);
                dlat = Double.parseDouble(sequence[1]);
                dalt = sequence.length==3 ? Double.parseDouble(sequence[2]) : 0d;
//                System.out.println(Position.fromDegrees(dlat,dlon,dalt).toString());
                arl.add(Position.fromDegrees(dlat,dlon,dalt));
            }
            catch (NumberFormatException e)
            {
//                String message = WorldWind.retrieveErrMsg("GeoRSS.NumberFormatException" + s);
//                WorldWind.logger().log(java.util.logging.Level.FINE, message);
                continue;
            }
       }
       return arl;
    }

	
//	 Find a child node by its name - not case sensitive
	 private Node findChildByName(Node parent, String name)
	 {
	     NodeList children = parent.getChildNodes();
	     if (children == null || children.getLength() < 1)
	         return null;

	     for (int i = 0; i < children.getLength(); i++)
	     {
	         String n = children.item(i).getNodeName();
	         if (n != null && n.toLowerCase().equals(name.toLowerCase()))
	             return children.item(i);
	     }

	     return null;
	 }

}
