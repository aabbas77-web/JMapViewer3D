/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package oghab.mapviewer;

import com.formdev.flatlaf.FlatLightLaf;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.util.awt.AWTGLReadBufferUtil;
import gov.nasa.worldwind.BasicModel;
import gov.nasa.worldwind.Configuration;
import gov.nasa.worldwind.Model;
import gov.nasa.worldwind.View;
import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.WorldWindow;
import gov.nasa.worldwind.animation.AnimationSupport;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.avlist.AVList;
import gov.nasa.worldwind.awt.WorldWindowGLCanvas;
import gov.nasa.worldwind.data.BufferedImageRaster;
import gov.nasa.worldwind.data.DataRaster;
import gov.nasa.worldwind.data.DataRasterReader;
import gov.nasa.worldwind.data.DataRasterReaderFactory;
import gov.nasa.worldwind.event.SelectEvent;
import gov.nasa.worldwind.event.SelectListener;
import gov.nasa.worldwind.formats.georss.GeoRSSParser;
import gov.nasa.worldwind.formats.gpx.GpxReader;
import gov.nasa.worldwind.formats.shapefile.ShapefileLayerFactory;
import gov.nasa.worldwind.formats.shapefile.ShapefileRecord;
import gov.nasa.worldwind.formats.shapefile.ShapefileRenderable;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.Box;
import gov.nasa.worldwind.geom.Extent;
import gov.nasa.worldwind.geom.ExtentHolder;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.geom.Vec4;
import gov.nasa.worldwind.globes.ElevationModel;
import gov.nasa.worldwind.globes.Globe;
import gov.nasa.worldwind.layers.CompassLayer;
import gov.nasa.worldwind.layers.Earth.MGRSGraticuleLayer;
import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.layers.LayerList;
import gov.nasa.worldwind.layers.MarkerLayer;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.layers.ScalebarLayer;
import gov.nasa.worldwind.layers.SkyColorLayer;
import gov.nasa.worldwind.layers.SkyGradientLayer;
import gov.nasa.worldwind.layers.StarsLayer;
import gov.nasa.worldwind.layers.SurfaceImageLayer;
import gov.nasa.worldwind.layers.TerrainProfileLayer;
import gov.nasa.worldwind.ogc.collada.ColladaRoot;
import gov.nasa.worldwind.ogc.collada.impl.ColladaController;
import gov.nasa.worldwind.ogc.kml.KMLAbstractFeature;
import gov.nasa.worldwind.ogc.kml.KMLRoot;
import gov.nasa.worldwind.ogc.kml.impl.KMLController;
import gov.nasa.worldwind.render.AbstractBrowserBalloon;
import gov.nasa.worldwind.render.AnnotationAttributes;
import gov.nasa.worldwind.render.BalloonAttributes;
import gov.nasa.worldwind.render.BasicBalloonAttributes;
import gov.nasa.worldwind.render.BasicShapeAttributes;
import gov.nasa.worldwind.render.ExtrudedPolygon;
import gov.nasa.worldwind.render.GeographicExtent;
import gov.nasa.worldwind.render.GlobeAnnotation;
import gov.nasa.worldwind.render.GlobeBrowserBalloon;
import gov.nasa.worldwind.render.Material;
import gov.nasa.worldwind.render.Offset;
import gov.nasa.worldwind.render.PointPlacemark;
import gov.nasa.worldwind.render.PointPlacemarkAttributes;
import gov.nasa.worldwind.render.Renderable;
import gov.nasa.worldwind.render.ScreenAnnotationBalloon;
import gov.nasa.worldwind.render.ShapeAttributes;
import gov.nasa.worldwind.render.Size;
import gov.nasa.worldwind.render.SurfaceImage;
import gov.nasa.worldwind.render.SurfacePolygon;
import gov.nasa.worldwind.render.SurfaceShape;
import gov.nasa.worldwind.render.SurfaceText;
import gov.nasa.worldwind.render.airspaces.Airspace;
import gov.nasa.worldwind.render.markers.BasicMarker;
import gov.nasa.worldwind.render.markers.BasicMarkerAttributes;
import gov.nasa.worldwind.render.markers.BasicMarkerShape;
import gov.nasa.worldwind.render.markers.Marker;
import gov.nasa.worldwind.retrieve.RetrievalService;
import gov.nasa.worldwind.terrain.CompoundElevationModel;
import gov.nasa.worldwind.terrain.LocalElevationModel;
import gov.nasa.worldwind.util.Logging;
import gov.nasa.worldwind.util.WWIO;
import gov.nasa.worldwind.util.WWUtil;
import gov.nasa.worldwind.view.firstperson.BasicFlyView;
import gov.nasa.worldwind.view.orbit.BasicOrbitView;
import gov.nasa.worldwindx.applications.worldwindow.util.Util;
import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;
import javax.xml.stream.XMLStreamException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.parsers.ParserConfigurationException;
//import jdk.internal.org.xml.sax.SAXException;
 
//import com.google.common.collect.ImmutableList;
//import com.google.common.jimfs.Configuration;
//import com.google.common.jimfs.Jimfs;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.WString;
import com.sun.jna.platform.win32.BaseTSD.LONG_PTR;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.HMODULE;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.LPARAM;
import com.sun.jna.platform.win32.WinDef.LRESULT;
import com.sun.jna.platform.win32.WinDef.UINT;
import com.sun.jna.platform.win32.WinDef.WPARAM;
import com.sun.jna.platform.win32.WinUser;
import com.sun.jna.platform.win32.WinUser.MSG;
import static com.sun.jna.platform.win32.WinUser.SW_SHOW;
import com.sun.jna.platform.win32.WinUser.WNDCLASSEX;
import com.sun.jna.platform.win32.WinUser.WNDENUMPROC;
import com.sun.jna.platform.win32.WinUser.WindowProc;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;
import flanagan.interpolation.CubicSpline;
import gov.nasa.worldwind.Exportable;
import gov.nasa.worldwind.event.PositionEvent;
import gov.nasa.worldwind.event.RenderingEvent;
import gov.nasa.worldwind.event.RenderingListener;
import gov.nasa.worldwind.ogc.kml.KMLAbstractContainer;
import gov.nasa.worldwind.ogc.kml.KMLGroundOverlay;
import gov.nasa.worldwind.ogc.kml.KMLPlacemark;
import gov.nasa.worldwind.ogc.kml.KMLPoint;
import gov.nasa.worldwind.ogc.kml.impl.KMLExtrudedPolygonImpl;
import gov.nasa.worldwind.ogc.kml.impl.KMLGroundOverlayPolygonImpl;
import gov.nasa.worldwind.ogc.kml.impl.KMLPolygonImpl;
import gov.nasa.worldwind.ogc.kml.impl.KMLRenderable;
import gov.nasa.worldwind.ogc.kml.impl.KMLSurfaceImageImpl;
import gov.nasa.worldwind.ogc.kml.impl.KMLSurfacePolygonImpl;
import gov.nasa.worldwind.poi.PointOfInterest;
import gov.nasa.worldwind.render.DrawContext;
import gov.nasa.worldwind.render.Polygon;
import gov.nasa.worldwind.render.ScreenImage;
import gov.nasa.worldwind.render.SurfacePolyline;
import gov.nasa.worldwind.render.SurfaceQuad;
import gov.nasa.worldwind.render.SurfaceSector;
import gov.nasa.worldwind.render.SurfaceSquare;
import gov.nasa.worldwind.tracks.TrackPointImpl;
import gov.nasa.worldwind.util.measure.LengthMeasurer;
import gov.nasa.worldwind.view.firstperson.FlyToFlyViewAnimator;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.MouseMotionListener;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileWriter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StringWriter;
import java.io.Writer;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JFrame;

import com.github.sarxos.webcam.Webcam;
import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.WinReg;
import gov.nasa.worldwind.WWObjectImpl;
import gov.nasa.worldwind.avlist.AVListImpl;
import gov.nasa.worldwind.awt.AWTInputHandler;
import gov.nasa.worldwind.formats.shapefile.Shapefile;
import gov.nasa.worldwind.globes.Earth;
import gov.nasa.worldwind.ogc.kml.KMLAbstractGeometry;
import gov.nasa.worldwind.ogc.kml.KMLConstants;
import gov.nasa.worldwind.ogc.kml.KMLData;
import gov.nasa.worldwind.ogc.kml.KMLExtendedData;
import gov.nasa.worldwind.ogc.kml.KMLFolder;
import gov.nasa.worldwind.ogc.kml.KMLLineString;
import gov.nasa.worldwind.ogc.kml.KMLPolygon;
import gov.nasa.worldwind.ogc.kml.gx.GXConstants;
import gov.nasa.worldwind.ogc.kml.impl.KMLExportUtil;
import static gov.nasa.worldwind.ogc.kml.impl.KMLExportUtil.kmlBoolean;
import gov.nasa.worldwind.ogc.kml.impl.KMLPointPlacemarkImpl;
import gov.nasa.worldwind.ogc.kml.impl.KMLTacticalSymbolPlacemarkImpl;
import gov.nasa.worldwind.pick.PickedObject;
import gov.nasa.worldwind.pick.PickedObjectList;
import gov.nasa.worldwind.symbology.BasicTacticalSymbolAttributes;
import gov.nasa.worldwind.symbology.SymbologyConstants;
import gov.nasa.worldwind.symbology.TacticalSymbol;
import gov.nasa.worldwind.symbology.TacticalSymbolAttributes;
import gov.nasa.worldwind.symbology.milstd2525.MilStd2525TacticalSymbol;
import gov.nasa.worldwind.terrain.AbstractElevationModel;
import gov.nasa.worldwind.util.gdal.GDALUtils;
import gov.nasa.worldwind.util.layertree.KMLFeatureTreeNode;
import gov.nasa.worldwind.util.layertree.KMLLayerTreeNode;
import gov.nasa.worldwind.util.layertree.LayerTree;
import gov.nasa.worldwind.util.layertree.LayerTreeNode;
import gov.nasa.worldwind.util.measure.AreaMeasurer;
import gov.nasa.worldwind.util.tree.BasicFrameAttributes;
import gov.nasa.worldwind.util.tree.BasicTreeAttributes;
import gov.nasa.worldwind.util.tree.BasicTreeLayout;
import gov.nasa.worldwind.util.tree.BasicTreeNode;
import gov.nasa.worldwind.util.tree.Tree;
import gov.nasa.worldwind.util.tree.TreeNode;
import gov.nasa.worldwind.util.xml.XMLParserNotification;
import gov.nasa.worldwind.util.xml.XMLParserNotificationListener;
import gov.nasa.worldwind.util.xml.atom.AtomConstants;
import gov.nasa.worldwind.util.xml.xal.XALConstants;
import gov.nasa.worldwind.view.BasicView;
import gov.nasa.worldwindx.examples.kml.KMLDocumentBuilder;
import gov.nasa.worldwindx.examples.util.HighlightController;
import gov.nasa.worldwindx.examples.util.ToolTipController;
import ige.apps.AppWindowUI;
import ige.apps.BasicApp;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.Graphics;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.Random;
import java.util.TreeSet;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileFilter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;
import org.gdal.gdal.Driver;
import org.gdal.gdal.gdal;
import org.xml.sax.SAXException;
import osm.map.worldwind.gl.fire.FireRenderable;

/**
 *
 * @author AZUS
 */
public final class MainFrame extends javax.swing.JFrame {
//    protected static final String ELEVATIONS_PATH = "D:\\Ali\\MapViewer\\Syria_Maps\\World\\tif\\dem.dem";
//    protected static final String ELEVATIONS_PATH = "D:\\Ali\\MapViewer\\Syria_Maps\\World\\tif\\Syria_DEM.tif";
    protected static final String ELEVATIONS_PATH = "D:\\Ali\\MapViewer\\Syria_Maps\\World\\tif\\Syria_BIL.bil";
//    protected static final String ELEVATIONS_PATH = "C:\\DEM\\Syria.bil";

    protected static final String IMAGE_PATH = "D:\\Ali\\MapViewer\\Syria_Maps\\World\\tif\\Syria.tif";
//    protected static final String IMAGE_PATH = "D:\\Ali\\MapViewer\\Syria_Maps\\World\\tif\\Syria.ecw";
//    protected static final String IMAGE_PATH = "D:\\Ali\\MapViewer\\Syria_Maps\\World\\tif\\Syria.jp2";

    // WorldWind Earth model canvas.
    public WorldWindowGLCanvas wwd;
    BasicFlyView flyView;
    BasicOrbitView orbitView;
    StatusLayer status_layer;
    MBTileLayer mbTileLayer;
    StarsLayer starsLayer;
    myCrosshairLayer crosshairLayer;
    TerrainProfileLayer terrainProfileLayer;
    MGRSGraticuleLayer mgrsGraticuleLayer;
    protected InstalledDataFrame installedDataFrame;
    static public Object currObject = null;

    static boolean bUpdateEdit = true;

    PropertiesFrame propertiesFrame = null;
    private boolean armed = false;
    private ArrayList<Position> positions = null;
    private gov.nasa.worldwind.render.Path polyline = null;
    private gov.nasa.worldwind.render.SurfacePolygon polygon = null;
    private boolean active = false;
    private static ShapeAttributes normalShapeAttributes0 = null;
    private static ShapeAttributes highlightShapeAttributes0 = null;
    Cursor cursor;
   
    // KML
    protected LayerTree layerTree;
    protected BasicTreeLayout treeLayout;
    protected RenderableLayer hiddenLayer;

    protected myHotSpotController hotSpotController;
    protected KMLApplicationController kmlAppController;
    protected myBalloonController balloonController;
    protected HighlightController highlightController;
    protected ToolTipController toolTipController;
    protected RenderableLayer layer_kml;

    static void cmd(String command)
    {
        ProcessBuilder processBuilder = new ProcessBuilder();

        if (System.getProperty("os.name").startsWith("Windows")) {
//            processBuilder.command("cmd.exe", "/c", "setx -m GDAL_DATA \""+Settings.strGDAL_Data_Path+"\"");
//            processBuilder.command("cmd.exe", "/c", "setx -m GDAL_DATA \"%GDAL_DATA%\"");
            processBuilder.command("cmd.exe", "/c", command);
        } else {
            processBuilder.command("/bin/bash", "-c", "ping $PING_WEBSITE$");
        }

        try {
            // Starting the process...
            Process process = processBuilder.start();

            // Reading the output of the process
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {

                String line;

                while ((line = reader.readLine()) != null) {
                     System.out.println(line);
                }
            }

            // Catch the exit code of our process
            int ret = process.waitFor();

            System.out.printf("Program exited with code: %d\n", ret);

        } catch (IOException | InterruptedException e) {
            // Handle exception...
            e.printStackTrace();
        }    
    }
    
    static public String system_get_env(String key)
    {
        try
        {
            return Advapi32Util.registryGetStringValue(WinReg.HKEY_LOCAL_MACHINE, "SYSTEM\\CurrentControlSet\\Control\\Session Manager\\Environment", key);
        }
        catch(Throwable ex)
        {
            System.out.println("system_get_env: "+ex.getLocalizedMessage());
            return "";
        }
    }
    
    static public void system_set_env(String key,String value)
    {
        try
        {
            // System.getenv
            Advapi32Util.registrySetStringValue(WinReg.HKEY_LOCAL_MACHINE, "SYSTEM\\CurrentControlSet\\Control\\Session Manager\\Environment", key, value);
        }
        catch(Throwable ex)
        {
            System.out.println("system_get_env: "+ex.getLocalizedMessage());
        }
    }
    
    static void system_update()
    {
        try {
            // Setting up the environment...
//            cmd("setx -m GDAL_DATA \""+Settings.strGDAL_Data_Path+"\"");
//            cmd("set -m GDAL_DATA = \""+Settings.strGDAL_Data_Path+"\"");
            
//            String value = WinRegistry.readString (
//                    WinRegistry.HKEY_LOCAL_MACHINE,                             //HKEY
//                    "SYSTEM\\CurrentControlSet\\Control\\Session Manager\\Environment",           //Key
//                    "GDAL_DATA");                                              //ValueName
//            System.out.println("Windows Distribution = " + value);

            // Read a string
//            System.setProperty("java.library.path",Settings.strGDAL);
            String PATH = system_get_env("PATH");
            if(!PATH.contains(Settings.strGDAL))    system_set_env("PATH", Settings.strGDAL+";"+PATH);
            system_set_env("GDAL_DRIVER_PATH", Settings.strGDAL_Driver_Path);
            system_set_env("OGR_DRIVER_PATH", Settings.strOGR_Driver_Path);
            system_set_env("PROJ_LIB", Settings.strProj_lib_Path);
            system_set_env("GDAL_DATA", Settings.strGDAL_Data_Path);

//            String productName = Advapi32Util.registryGetStringValue(
//                WinReg.HKEY_LOCAL_MACHINE, "SYSTEM\\CurrentControlSet\\Control\\Session Manager\\Environment", "GDAL_DATA");
//            System.out.printf("GDAL_DATA: %s\n", productName);

//            // Read an int (& 0xFFFFFFFFL for large unsigned int)
//            int timeout = Advapi32Util.registryGetIntValue(
//                WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Microsoft\\Windows NT\\CurrentVersion\\Windows", "ShutdownWarningDialogTimeout");
//            System.out.printf("Shutdown Warning Dialog Timeout: %d (%d as unsigned long)\n", timeout, timeout & 0xFFFFFFFFL);
//
//            // Create a key and write a string
//            Advapi32Util.registryCreateKey(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\StackOverflow");
//            Advapi32Util.registrySetStringValue(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\StackOverflow", "url", "http://stackoverflow.com/a/6287763/277307");
//
//            // Delete a key
//            Advapi32Util.registryDeleteKey(WinReg.HKEY_CURRENT_USER, "SOFTWARE\\StackOverflow");


            System.out.println("GDAL~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            System.out.println("java.library.path: "+System.getProperty("java.library.path"));
            System.out.println("PATH: "+system_get_env("PATH"));// add "C:\Program Files\GDAL" to PATH
            System.out.println("GDAL_DATA: "+system_get_env("GDAL_DATA"));// "C:\Program Files\GDAL\gdal-data"
            System.out.println("GDAL_DRIVER_PATH: "+system_get_env("GDAL_DRIVER_PATH"));// "C:\Program Files\GDAL\gdalplugins"
            System.out.println("OGR_DRIVER_PATH: "+system_get_env("OGR_DRIVER_PATH"));// "C:\Program Files\GDAL\gdalplugins"
            System.out.println("PROJ_LIB: "+system_get_env("PROJ_LIB"));// "C:\Program Files\GDAL\projlib"
            System.out.println("GDAL~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }

    static void system_test()
    {
        // Setting up the environment...
        ProcessBuilder processBuilder = new ProcessBuilder();
        Map<String, String> env = processBuilder.environment();
        env.put("PING_WEBSITE", "stackabuse.com");

        if (System.getProperty("os.name").startsWith("Windows")) {
            processBuilder.command("cmd.exe", "/c", "ping -n 3 %PING_WEBSITE%");
        } else {
            processBuilder.command("/bin/bash", "-c", "ping $PING_WEBSITE$");
        }

        try {
            // Starting the process...
            Process process = processBuilder.start();

            // Reading the output of the process
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {

                String line;

                while ((line = reader.readLine()) != null) {
                     System.out.println(line);
                }
            }

            // Catch the exit code of our process
            int ret = process.waitFor();

            System.out.printf("Program exited with code: %d\n", ret);

        } catch (IOException | InterruptedException e) {
            // Handle exception...
            e.printStackTrace();
        }    
    }
    
    static class Settings
    {
//        static public String strBaseMapPath = "D:/Ali/WorldWind/MapViewer/data/maps/zip/png/Syria1_17.zip";
        static public String strBaseMapPath = "D:/Ali/WorldWind/MapViewer/data/overlays/libanon/Libanon1_19_jpg.zip";
        static public String strBaseExtension = ".jpg";
        static public String strBaseType = "MOBAC";
        static public String strOverlayMapPath = "D:/Ali/WorldWind/MapViewer/data/overlays/libanon/Libanon1_19_overlay_png.zip";
        static public String strOverlayExtension = ".png";
        static public double dZoomStep = 100;
        static public double dFOV = 60.0;
        static public int x = 0;
        static public int y = 0;
        static public int w = 500;
        static public int h = 400;
        static boolean bDebug = false;
        static boolean bOffline = true;
        static boolean bCooperation = true;
        static String strLanguage = "ar";

        static public String strGDAL = ".\\jre\\bin";//"C:\\Program Files\\GDAL";
        static public String strGDAL_JniDll = "gdalalljni.dll";
        static public String strGDAL_Driver_Path = ".\\jre\\bin\\gdalplugins";//"C:\\Program Files\\GDAL\\gdalplugins";
        static public String strGDAL_Data_Path = ".\\jre\\bin\\gdal-data";//"C:\\Program Files\\GDAL\\gdal-data";
        static public String strOGR_Driver_Path = ".\\jre\\bin\\gdalplugins";//"C:\\Program Files\\GDAL\\gdalplugins";
        static public String strProj_lib_Path = ".\\jre\\bin\\projlib";//"C:\\Program Files\\GDAL\\projlib";
    }

    static String strAppPath = "./";
    static String strParentPath = "./";
    static public String strDataPath = "./";
    static public String strPlacesPath = "./";
//    static String strImagesPath = "./";
    static String strIconsPath = "./";
    static String strSettingsPath = "./MapViewer.ini";
    static String strViewstatePath = "./ViewState.xml";
    static String strDEMPath = "./DEM_Syria.xml";
    
    static boolean is_init = false;
    static
    {
        jvm_arch = System.getProperty("sun.arch.data.model");
        if(!is_init)    init("static");
    }
    
    static void init(String strTitle)
    {
        try {
            System.out.println(strTitle+" init start~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            strAppPath = new File(".").getCanonicalPath();
            if(Settings.bDebug) System.out.println("strAppPath: "+strAppPath);
            jvm_arch = System.getProperty("sun.arch.data.model");
            System.out.println("jvm_arch: "+jvm_arch);
            
            strParentPath = Paths.get(strAppPath).getParent().toString();
            strDataPath = Paths.get(strParentPath, "data").toString();
            strPlacesPath = Paths.get(strDataPath, "db","places.db").toString();
//            imagesPath = Paths.get(strDataPath, "images");
//            strImagesPath = imagesPath.toString();
            strIconsPath = Paths.get(strDataPath, "icons").toString();
            strSettingsPath = Paths.get(strAppPath, "MapViewer"+jvm_arch+".ini").toString();
            strViewstatePath = Paths.get(strAppPath, "ViewState.xml").toString();
            strDEMPath = Paths.get(strDataPath, "dtm", "DEM_Syria", "DEM_Syria.xml").toString();

//            JOptionPane.showMessageDialog(MainFrame.this,"strAppPath: "+strAppPath,"Information",JOptionPane.WARNING_MESSAGE);
        } catch (IOException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
//        System.setProperty("java.library.path",strAppPath);
        System.setProperty("file.encoding", "UTF-8");
        
        // GDAL
        System.setProperty("gov.nasa.worldwind.prevent.gdal.loader.replacement","true");

//        System.setProperty("oghab.mapviewer.gdal.path","C:\\Program Files\\GDAL");
//        System.setProperty("oghab.mapviewer.gdal.jni_dll","gdalalljni.dll");
//        System.setProperty("oghab.mapviewer.gdal.driver.path","C:\\Program Files\\GDAL\\gdalplugins");
//        System.setProperty("oghab.mapviewer.gdal.data.path","C:\\Program Files\\GDAL\\gdal-data");
//        System.setProperty("oghab.mapviewer.ogr.driver.path","C:\\Program Files\\GDAL\\gdalplugins");
//        System.setProperty("oghab.mapviewer.proj.lib.path","C:\\Program Files\\GDAL\\projlib");
        
        Settings.strGDAL = Paths.get(strAppPath, "jre", "bin").toString();//".\\jre\\bin";//"C:\\Program Files\\GDAL";
        Settings.strGDAL_JniDll = "gdalalljni.dll";
        Settings.strGDAL_Driver_Path = Paths.get(Settings.strGDAL, "gdalplugins").toString();//".\\jre\\bin\\gdalplugins";//"C:\\Program Files\\GDAL\\gdalplugins";
        Settings.strGDAL_Data_Path = Paths.get(Settings.strGDAL, "gdal-data").toString();//".\\jre\\bin\\gdal-data";//"C:\\Program Files\\GDAL\\gdal-data";
        Settings.strOGR_Driver_Path = Paths.get(Settings.strGDAL, "gdalplugins").toString();//".\\jre\\bin\\gdalplugins";//"C:\\Program Files\\GDAL\\gdalplugins";
        Settings.strProj_lib_Path = Paths.get(Settings.strGDAL, "projlib").toString();//".\\jre\\bin\\projlib";//"C:\\Program Files\\GDAL\\projlib";

        // GDAL
        System.setProperty("oghab.mapviewer.gdal.path",Settings.strGDAL);
        System.setProperty("oghab.mapviewer.gdal.jni_dll",Settings.strGDAL_JniDll);
        System.setProperty("oghab.mapviewer.gdal.driver.path",Settings.strGDAL_Driver_Path);
        System.setProperty("oghab.mapviewer.gdal.data.path",Settings.strGDAL_Data_Path);
        System.setProperty("oghab.mapviewer.ogr.driver.path",Settings.strOGR_Driver_Path);
        System.setProperty("oghab.mapviewer.proj.lib.path",Settings.strProj_lib_Path);

//        system_update();
        
//        addDllLocationToPath(Paths.get(strAppPath, "lib-external", "gdal").toString());

        Configuration.setValue(AVKey.MIL_STD_2525_ICON_RETRIEVER_PATH, "jar:file:../data/symbols/milstd2525-symbols.zip!");        
        
//        System.setProperty("gov.nasa.worldwind.a pp.config.document","worldwind/worldwind.xml");
//        System.setProperty("gov.nasa.worldwind.config.file","worldwind/worldwind.xml");
        System.setProperty("javax.xml.parsers.DocumentBuilderFactory","com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl");
//        System.setProperty("gov.nasa.worldwind.config.file","D:/Ali/WorldWind/WorldWindJava-develop/src/config/worldwind.xml");
        
        // Specify the configuration file for the elevation model prior to starting WorldWind:
        Configuration.setValue(AVKey.DATA_FILE_STORE_CONFIGURATION_FILE_NAME, "res/DataFileStore.xml");        
        Configuration.setValue(AVKey.EARTH_ELEVATION_MODEL_CONFIG_FILE, strDEMPath);
        
////        Configuration.setValue(AVKey.DATA_FILE_STORE_CONFIGURATION_FILE_NAME, "path/DataFileStore.xml");        
//        Configuration.setValue(AVKey.GDAL_PATH,"D:\\Ali\\WorldWind\\WorldWindServerKit\\Tools\\worldwind-geoserver-0.5.0-win64\\worldwind-geoserver-0.5.0\\gdal\\lib");
        if(Settings.bDebug) System.out.println("DATA_FILE_STORE_CONFIGURATION_FILE_NAME: "+Configuration.getStringValue(AVKey.DATA_FILE_STORE_CONFIGURATION_FILE_NAME));
        if(Settings.bDebug) System.out.println("EARTH_ELEVATION_MODEL_CONFIG_FILE: "+Configuration.getStringValue(AVKey.EARTH_ELEVATION_MODEL_CONFIG_FILE));
//        System.out.println("GDAL_PATH: "+Configuration.getStringValue(AVKey.GDAL_PATH));
//        System.out.println("AVAILABLE_IMAGE_FORMATS: "+Configuration.getStringValue(AVKey.AVAILABLE_IMAGE_FORMATS));
//        System.out.println("FILE_STORE_LOCATION: "+Configuration.getStringValue(AVKey.FILE_STORE_LOCATION));
//        System.out.println("FOV: "+Configuration.getStringValue(AVKey.FOV));
//        System.out.println("TILED_IMAGERY: "+Configuration.getStringValue(AVKey.TILED_IMAGERY));
//        System.out.println("TILED_ELEVATIONS: "+Configuration.getStringValue(AVKey.TILED_ELEVATIONS));
//        System.out.println("TILED_RASTER_PRODUCER_LIMIT_MAX_LEVEL: "+Configuration.getStringValue(AVKey.TILED_RASTER_PRODUCER_LIMIT_MAX_LEVEL));
//        System.out.println("VERTICAL_EXAGGERATION: "+Configuration.getStringValue(AVKey.VERTICAL_EXAGGERATION));


        System.setProperty("java.net.useSystemProxies", "true");
        if (Configuration.isMacOS())
        {
            System.setProperty("apple.laf.useScreenMenuBar", "true");
            System.setProperty("com.apple.mrj.application.apple.menu.about.name", "WorldWind Application");
            System.setProperty("com.apple.mrj.application.growbox.intrudes", "false");
        }
        else if (Configuration.isWindowsOS())
        {
            System.setProperty("sun.awt.noerasebackground", "true"); // prevents flashing during window resizing
        }
        System.setProperty("gov.nasa.worldwind.textrender.useglyphcache","false");// RTL arabic text
        
        // Set the stereo.mode property to request stereo. Request red-blue anaglyph in this case. Can also request
        // "device" if the display device supports stereo directly. To prevent stereo, leave the property unset or set
        // it to an empty string.
//        System.setProperty(AVKey.STEREO_MODE, "redblue");
//        System.setProperty(AVKey.STEREO_MODE, "device");
        System.setProperty(AVKey.STEREO_MODE, "none");
        
        // Configure the Mac OS X application's default quit action to close all windows instead of executing
        // System.exit. This enables us to detect the application exiting by listening to the window close event. See
        // the following URL for details:
        // http://developer.apple.com/library/mac/documentation/Java/Reference/JavaSE6_AppleExtensionsRef/api/com/apple/eawt/Application.html#setQuitStrategy(com.apple.eawt.QuitStrategy)
        if (Configuration.isMacOS())
        {
            System.setProperty("apple.eawt.quitStrategy", "CLOSE_ALL_WINDOWS");
        }

        // Prior to starting World Wind, specify the cache configuration file to Configuration.
//        Configuration.setValue(
//            "gov.nasa.worldwind.avkey.DataFileStoreConfigurationFileName",
//            "gov/nasa/worldwindx/examples/CacheLocationConfiguration.xml");

        // Flat Earth: Adjust configuration values before instantiation
//        Configuration.setValue(AVKey.GLOBE_CLASS_NAME, EarthFlat.class.getName());

        // Ensure that menus and tooltips interact successfully with the WWJ window.
        ToolTipManager.sharedInstance().setLightWeightPopupEnabled(false);
        JPopupMenu.setDefaultLightWeightPopupEnabled(false);
        is_init = true;
        System.out.println("init end~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
    }
    
    static public boolean addDllLocationToPath(String dllLocation)
    {
        try
        {
            System.setProperty("java.library.path", System.getProperty("java.library.path") + ";" + dllLocation);
//            Field fieldSysPath = ClassLoader.class.getDeclaredField("sys_paths");
//            fieldSysPath.setAccessible(true);
//            fieldSysPath.set(null, null);
        }
        catch (Exception e)
        {
            System.err.println("Could not modify path");
            return false;
        }
        return true;
    }    
    
    static public final String EN_LANG = "English.Language";
    static public final String AR_LANG = "Arabic.Language";
    static public String CURR_LANG = AR_LANG;
//    static public String CURR_LANG = EN_LANG;
    
    static public void update_language_direction(Container container, String lang)
    {
        if(lang.contains(MainFrame.EN_LANG))
        {
            container.applyComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
            PopupMenuEdit.applyComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        }
        else
        {
            container.applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
            PopupMenuEdit.applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        }
    }
    
    static public ResourceBundle getLanguageBundle(Container container, String lang)
    {
        ResourceBundle bundle = null;
        if(lang.contains(MainFrame.EN_LANG))
        {
            bundle = ResourceBundle.getBundle("oghab/mapviewer/English_Language");
            container.applyComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
            if(PopupMenuEdit != null)   PopupMenuEdit.applyComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        }
        else
        {
            bundle = ResourceBundle.getBundle("oghab/mapviewer/Arabic_Language");
            container.applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
            if(PopupMenuEdit != null)   PopupMenuEdit.applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        }
        return bundle;
    }
    
    static public ResourceBundle bundle = null;
    public void set_language(String lang)
    {
        bundle = getLanguageBundle(this, lang);
        if(bRegistered)
            this.setTitle(bundle.getString("F_MapViewer")+" "+bundle.getString("F_Registered_Version"));
        else
            this.setTitle(bundle.getString("F_MapViewer"));
        
        // File Menu
        MI_File.setText(bundle.getString("MI_File"));
        MI_Open.setText(bundle.getString("MI_Open"));
        MI_ScreenShot.setText(bundle.getString("MI_ScreenShot"));
        MI_Exit.setText(bundle.getString("MI_Exit"));
        
        // Edit Menu
        MI_Edit.setText(bundle.getString("MI_Edit"));
        MI_Cut.setText(bundle.getString("MI_Cut"));
        MI_Copy.setText(bundle.getString("MI_Copy"));
        MI_Paste.setText(bundle.getString("MI_Paste"));
        MI_Delete.setText(bundle.getString("MI_Delete"));
        MI_SavePlaceAs.setText(bundle.getString("MI_SavePlaceAs"));
        MI_Simulate.setText(bundle.getString("MI_Simulate"));
        MI_Properties.setText(bundle.getString("MI_Properties"));
        
        // View Menu
        MI_View.setText(bundle.getString("MI_View"));
        MI_ZoomIn.setText(bundle.getString("MI_ZoomIn"));
        MI_ZoomOut.setText(bundle.getString("MI_ZoomOut"));
        MI_Layers.setText(bundle.getString("MI_Layers"));
        MI_Language.setText(bundle.getString("MI_Language"));
        MI_English.setText(bundle.getString("MI_English"));
        MI_Arabic.setText(bundle.getString("MI_Arabic"));
        
        // Tools Menu
        MI_Tools.setText(bundle.getString("MI_Tools"));
        MI_Measure.setText(bundle.getString("MI_Measure"));
        MI_ContourLines.setText(bundle.getString("MI_ContourLines"));
        MI_Search.setText(bundle.getString("MI_Search"));
        
        // Add Menu
        MI_Add.setText(bundle.getString("MI_Add"));
        MI_AddFolder.setText(bundle.getString("MI_AddFolder"));
        MI_AddPlacemark.setText(bundle.getString("MI_AddPlacemark"));
        MI_AddTacticalSymbol.setText(bundle.getString("MI_TacticalSymbol"));
        MI_AddPath.setText(bundle.getString("MI_AddPath"));
        MI_AddPolygon.setText(bundle.getString("MI_AddPolygon"));
        
        // Help Menu
        IM_Help.setText(bundle.getString("IM_Help"));
        MI_About.setText(bundle.getString("MI_About"));
        MI_Contents.setText(bundle.getString("MI_Contents"));
        MI_ExportOSMTiles.setText(bundle.getString("MI_ExportOSMTiles"));
        MI_MilStd2025C.setText(bundle.getString("MI_MilStd2025C"));
        
        // Context Popup Menu
        MenuItemCut.setText(bundle.getString("MI_Cut"));
        MenuItemCopy.setText(bundle.getString("MI_Copy"));
        MenuItemPaste.setText(bundle.getString("MI_Paste"));
        MenuItemDelete.setText(bundle.getString("MI_Delete"));
        MenuItemSavePlaceAs.setText(bundle.getString("MI_SavePlaceAs"));
        MenuItemSimulate.setText(bundle.getString("MI_Simulate"));
        MenuItemProperties.setText(bundle.getString("MI_Properties"));
    }

    public void set_language_for_all_frames()
    {
        if(MainFrame.frame != null) MainFrame.frame.set_language(CURR_LANG);
        if(PropertiesFrame.frame != null) PropertiesFrame.frame.set_language(CURR_LANG);
        if(IconsFrame.frame != null) IconsFrame.frame.set_language(CURR_LANG);
        if(TacticalSymbolsFrame.frame != null) TacticalSymbolsFrame.frame.set_language(CURR_LANG);
        if(AboutFrame.frame != null) AboutFrame.frame.set_language(CURR_LANG);
        if(MeasureFrame.frame != null)  MeasureFrame.frame.set_language(CURR_LANG);
        if(SearchFrame.frame != null)  SearchFrame.frame.set_language(CURR_LANG);
    }
    
    private void loadSettings()
    {
        // loads properties from file
        try {
            Properties defaultProps = new Properties();

            // sets default properties
            defaultProps.setProperty("strBaseMapPath", Settings.strBaseMapPath);
            defaultProps.setProperty("strBaseExtension", Settings.strBaseExtension);
            defaultProps.setProperty("strBaseType", Settings.strBaseType);
            defaultProps.setProperty("strOverlayMapPath", Settings.strOverlayMapPath);
            defaultProps.setProperty("strOverlayExtension", Settings.strOverlayExtension);
            defaultProps.setProperty("dZoomStep", Double.toString(Settings.dZoomStep));
            defaultProps.setProperty("dFOV", Double.toString(Settings.dFOV));
            defaultProps.setProperty("x", Integer.toString(Settings.x));
            defaultProps.setProperty("y", Integer.toString(Settings.y));
            defaultProps.setProperty("w", Integer.toString(Settings.w));
            defaultProps.setProperty("h", Integer.toString(Settings.h));
            defaultProps.setProperty("bDebug", Boolean.toString(Settings.bDebug));
            defaultProps.setProperty("bOffline", Boolean.toString(Settings.bOffline));
            defaultProps.setProperty("bCooperation", Boolean.toString(Settings.bCooperation));
            defaultProps.setProperty("strLanguage", Settings.strLanguage);

//            defaultProps.setProperty("strGDAL", Settings.strGDAL);
//            defaultProps.setProperty("strGDAL_JniDll", Settings.strGDAL_JniDll);
//            defaultProps.setProperty("strGDAL_Driver_Path", Settings.strGDAL_Driver_Path);
//            defaultProps.setProperty("strGDAL_Data_Path", Settings.strGDAL_Data_Path);
//            defaultProps.setProperty("strOGR_Driver_Path", Settings.strOGR_Driver_Path);
//            defaultProps.setProperty("strProj_lib_Path", Settings.strProj_lib_Path);

            Properties configProps = new Properties(defaultProps);
            File configFile = new File(strSettingsPath);
            InputStream inputStream = new FileInputStream(configFile);
            configProps.load(inputStream);
//            configProps.loadFromXML(inputStream);
            inputStream.close();
            
            Settings.strBaseMapPath = configProps.getProperty("strBaseMapPath");
            Settings.strBaseExtension = configProps.getProperty("strBaseExtension");
            Settings.strBaseType = configProps.getProperty("strBaseType");
            Settings.strOverlayMapPath = configProps.getProperty("strOverlayMapPath");
            Settings.strOverlayExtension = configProps.getProperty("strOverlayExtension");
            Settings.dZoomStep = Double.parseDouble(configProps.getProperty("dZoomStep"));
            Settings.dFOV = Double.parseDouble(configProps.getProperty("dFOV"));
            Settings.x = Integer.parseInt(configProps.getProperty("x"));
            Settings.y = Integer.parseInt(configProps.getProperty("y"));
            Settings.w = Integer.parseInt(configProps.getProperty("w"));
            Settings.h = Integer.parseInt(configProps.getProperty("h"));
            Settings.bDebug = Boolean.parseBoolean(configProps.getProperty("bDebug"));
            Settings.bOffline = Boolean.parseBoolean(configProps.getProperty("bOffline"));
            Settings.bCooperation = Boolean.parseBoolean(configProps.getProperty("bCooperation"));
            Settings.strLanguage = configProps.getProperty("strLanguage").toLowerCase();

//            Settings.strGDAL = configProps.getProperty("strGDAL");
//            Settings.strGDAL_JniDll = configProps.getProperty("strGDAL_JniDll");
//            Settings.strGDAL_Driver_Path = configProps.getProperty("strGDAL_Driver_Path");
//            Settings.strGDAL_Data_Path = configProps.getProperty("strGDAL_Data_Path");
//            Settings.strOGR_Driver_Path = configProps.getProperty("strOGR_Driver_Path");
//            Settings.strProj_lib_Path = configProps.getProperty("strProj_lib_Path");
            
            if(!file_exists(Settings.strOverlayMapPath))    Settings.strOverlayMapPath = null;

//            // GDAL
//            System.setProperty("oghab.mapviewer.gdal.path",Settings.strGDAL);
//            System.setProperty("oghab.mapviewer.gdal.jni_dll",Settings.strGDAL_JniDll);
//            System.setProperty("oghab.mapviewer.gdal.driver.path",Settings.strGDAL_Driver_Path);
//            System.setProperty("oghab.mapviewer.gdal.data.path",Settings.strGDAL_Data_Path);
//            System.setProperty("oghab.mapviewer.ogr.driver.path",Settings.strOGR_Driver_Path);
//            System.setProperty("oghab.mapviewer.proj.lib.path",Settings.strProj_lib_Path);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void saveSettings()
    {
        try {
            Point location = this.getLocation();
            Settings.x = location.x;
            Settings.y = location.y;
            Dimension size = this.getSize();
            Settings.w = (size.width > 0) ? size.width : 500;
            Settings.h = (size.height > 0) ? size.height : 400;

            Properties configProps = new Properties();
            configProps.setProperty("strBaseMapPath", Settings.strBaseMapPath);
            configProps.setProperty("strBaseExtension", Settings.strBaseExtension);
            configProps.setProperty("strBaseType", Settings.strBaseType);
            if(Settings.strOverlayMapPath != null)
                configProps.setProperty("strOverlayMapPath", Settings.strOverlayMapPath);
            else
                configProps.setProperty("strOverlayMapPath", "");
            configProps.setProperty("strOverlayExtension", Settings.strOverlayExtension);
            configProps.setProperty("dZoomStep", Double.toString(Settings.dZoomStep));
            configProps.setProperty("dFOV", Double.toString(Settings.dFOV));
            configProps.setProperty("x", Integer.toString(Settings.x));
            configProps.setProperty("y", Integer.toString(Settings.y));
            configProps.setProperty("w", Integer.toString(Settings.w));
            configProps.setProperty("h", Integer.toString(Settings.h));
            configProps.setProperty("bDebug", Boolean.toString(Settings.bDebug));
            configProps.setProperty("bOffline", Boolean.toString(Settings.bOffline));
            configProps.setProperty("bCooperation", Boolean.toString(Settings.bCooperation));
            configProps.setProperty("strLanguage", Settings.strLanguage);

//            configProps.setProperty("strGDAL", Settings.strGDAL);
//            configProps.setProperty("strGDAL_JniDll", Settings.strGDAL_JniDll);
//            configProps.setProperty("strGDAL_Driver_Path", Settings.strGDAL_Driver_Path);
//            configProps.setProperty("strGDAL_Data_Path", Settings.strGDAL_Data_Path);
//            configProps.setProperty("strOGR_Driver_Path", Settings.strOGR_Driver_Path);
//            configProps.setProperty("strProj_lib_Path", Settings.strProj_lib_Path);

            File configFile = new File(strSettingsPath);
            if(configFile.exists()) configFile.delete();
            try (OutputStream outputStream = new FileOutputStream(configFile)) {
                configProps.store(outputStream, "MapViewer Setttings");
//                configProps.storeToXML(outputStream, "MapViewer Setttings");
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Creates new form MainFrame
     */
   public interface myKernel32 extends Library {
       // FREQUENCY is expressed in hertz and ranges from 37 to 32767
       // DURATION is expressed in milliseconds
       public boolean Beep(int FREQUENCY, int DURATION);
       public void Sleep(int DURATION);
       public int MessageBoxW(HWND hWnd,WString lpText,WString lpCaption,UINT uType);
       
       public String plugin_name();
       public String plugin_type();
       public String plugin_version();

       public int plugin_create();
       public int plugin_destroy();
       public int plugin_about();
       public int plugin_execute();
       public int plugin_settings();
   }
   
    public interface myCore64 extends Library {
        public int System_ID();
        public int EncodeSystem_ID(int nSystem_ID,int nKey);
        public int LoadSerialNumber();
        public void SaveSerialNumber(int nSerialNumber);
        
        public void LLtoUTM(int ReferenceEllipsoid, double Lat, double Long, double UTMNorthing, double UTMEasting, char[] UTMZone);
        public void UTMtoLL(int ReferenceEllipsoid, double UTMNorthing, double UTMEasting, char[] UTMZone, double Lat,  double Long );
        void Convert_Geo_To_XY(double Lon,double Lat,int[] zone,double[] X,double[] Y);
        void Convert_XY_To_Geo(double X,double Y,int zone,double[] lon,double[] lat);
    }
    
    public interface myCore32 extends Library {
        public long _System_ID();
        public long _EncodeSystem_ID(long nSystem_ID,long nKey);
        public long _LoadSerialNumber();
        public void _SaveSerialNumber(long nSerialNumber);
        
        public void _LLtoUTM(int ReferenceEllipsoid, double Lat, double Long, double UTMNorthing, double UTMEasting, char[] UTMZone);
        public void _UTMtoLL(int ReferenceEllipsoid, double UTMNorthing, double UTMEasting, char[] UTMZone, double Lat,  double Long );
        public void _Convert_Geo_To_XY(double Lon,double Lat,int[] zone,double[] X,double[] Y);
        public void _Convert_XY_To_Geo(double X,double Y,int zone,double[] lon,double[] lat);
    }
   
    public interface User33 extends User32 {
        User33 INSTANCE = (User33) Native.load("user32", User33.class, W32APIOptions.DEFAULT_OPTIONS);
        interface WNDPROC extends StdCallLibrary.StdCallCallback {
            LRESULT callback(HWND hWnd, int uMsg, WPARAM uParam, LPARAM lParam);
        }
        LONG_PTR GetWindowLongPtrW(HWND hWnd, int  nIndex);
        LRESULT CallWindowProcW(LONG_PTR proc, HWND hWnd, int uMsg, WPARAM uParam, LPARAM lParam);
        LONG_PTR SetWindowLongPtrW(HWND hWnd, int nIndex, User33.WNDPROC wndProc);
    }
    public static final int WM_QUERYENDSESSION = 0x11;   

    private void log(String message) {
        String currThread = Thread.currentThread().getName();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        String currTime = sdf.format(new Date());
        System.out.println(currTime + " [" + currThread + "] " + message);

    }
   
    private static Throwable exceptionInCreatedThread;
    public void createWindow(final String windowClass) {
        // Runs it in a specific thread because the main thread is blocked in infinite loop otherwise.
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    createWindowAndLoop(windowClass);
                } catch (Throwable t) {
                    //will fail the test in case of exception in created thread.
                    exceptionInCreatedThread = t;
                }
            }
        }).start();
        log("Window " + windowClass + " created.");
    }

    public int getLastError() {
        int rc = Kernel32.INSTANCE.GetLastError();

        if (rc != 0) {
            log("error: " + rc);
        }

        return rc;
    }

    public void createWindowAndLoop(String windowClass) {
        // define new window class
        HMODULE hInst = Kernel32.INSTANCE.GetModuleHandle("");

        WNDCLASSEX wClass = new WNDCLASSEX();
        wClass.hInstance = hInst;
        wClass.lpfnWndProc = new WindowProc() {

            @Override
            public LRESULT callback(HWND hwnd, int uMsg, WPARAM wParam, LPARAM lParam) {
                log(hwnd + " - received a message : " + uMsg);
                switch (uMsg) {
                    case WinUser.WM_CREATE: {
                        log(hwnd + " - onCreate: WM_CREATE");
                        return new LRESULT(0);
                    }
                    case WinUser.WM_CLOSE:
                        log(hwnd + " WM_CLOSE");
                        User32.INSTANCE.DestroyWindow(hwnd);
                        return new LRESULT(0);
                    case WinUser.WM_DESTROY: {
                        log(hwnd + " - on Destroy.");
                        User32.INSTANCE.PostQuitMessage(0);
                        return new LRESULT(0);
                    }
                    case WinUser.WM_USER: {
                        log(hwnd + " - received a WM_USER message with code : '" + wParam + "' and value : '" + lParam
                                + "'");
                        return new LRESULT(0);
                    }
                    default:
                        return User32.INSTANCE.DefWindowProc(hwnd, uMsg, wParam, lParam);
                }
            }
        };
        wClass.lpszClassName = windowClass;

        // register window class
        User32.INSTANCE.RegisterClassEx(wClass);
        getLastError();

        // create new window
        HWND hWnd = User32.INSTANCE.CreateWindowEx(User32.WS_EX_TOPMOST, windowClass, "Win32 Window Application", User32.WS_OVERLAPPEDWINDOW | User32.WS_VISIBLE, 0, 0, 200, 200, null, null, hInst, null);
        User32.INSTANCE.ShowWindow(hWnd, SW_SHOW);
        User32.INSTANCE.UpdateWindow(hWnd);
  
        getLastError();
        log("window sucessfully created! window hwnd: " + hWnd.getPointer().toString());

//        User32.INSTANCE.ShowWindow(hWnd, SW_SHOW);

        MSG msg = new MSG();
        while (User32.INSTANCE.GetMessage(msg, hWnd, 0, 0) > 0)
        {
            User32.INSTANCE.TranslateMessage(msg);
            User32.INSTANCE.DispatchMessage(msg);
            log("GetMessage: "+msg.toString());
        }

        User32.INSTANCE.UnregisterClass(windowClass, hInst);
        User32.INSTANCE.DestroyWindow(hWnd);

        log("program exit!");
    }

    public HWND determineHWNDFromWindowClass(String windowClass) {
        CallBackFindWindowHandleByWindowclass cb = new CallBackFindWindowHandleByWindowclass(windowClass);
        User32.INSTANCE.EnumWindows(cb, null);
        return cb.getFoundHwnd();

    }

    private static class CallBackFindWindowHandleByWindowclass implements WNDENUMPROC {

        private HWND found;

        private String windowClass;

        public CallBackFindWindowHandleByWindowclass(String windowClass) {
            this.windowClass = windowClass;
        }

        @Override
        public boolean callback(HWND hWnd, Pointer data) {

            char[] windowText = new char[512];
//            assertCallSucceeded("GetClassName", User32.INSTANCE.GetClassName(hWnd, windowText, windowText.length) != 0);
            User32.INSTANCE.GetClassName(hWnd, windowText, windowText.length);
            String className = Native.toString(windowText);

            if (windowClass.equalsIgnoreCase(className)) {
                // Found handle. No determine root window...
                HWND hWndAncestor = User32.INSTANCE.GetAncestor(hWnd, User32.GA_ROOTOWNER);
                found = hWndAncestor;
                return false;
            }
            return true;
        }

        public HWND getFoundHwnd() {
            return this.found;
        }

    }
    
//   static myKernel32 lib_mv_plugin_radar;
   static myCore64 lib_core64;
   static myCore32 lib_core32;
//   static String jvm_arch = "32";
//   static String jvm_arch = "64";
   static String jvm_arch = System.getProperty("sun.arch.data.model");
   static String lib_core_path = "Core64.dll";
   static int idx = 0;
   static boolean is_mouse_on_globe = true;
//    ColladaController tunguska = null;
    ColladaController schools = null;
    
    class LL2STM_Output
    {
        public int zone;
        public double X;
        public double Y;
    };
    
    class STM2LL_Output
    {
        public double lon;
        public double lat;
    };
    
    LL2STM_Output LL2STM(double lon, double lat)
    {
        int[] zone = {1};
        double[] X = {0};
        double[] Y = {0};
        LL2STM_Output output = new LL2STM_Output();
        if(jvm_arch.contains("64"))
            lib_core64.Convert_Geo_To_XY(lon, lat, zone, X, Y);
        else
            lib_core32._Convert_Geo_To_XY(lon, lat, zone, X, Y);
        output.X = X[0];
        output.Y = Y[0];
        output.zone = zone[0];
        return output;
    }
    
    STM2LL_Output STM2LL(double X, double Y, int zone)
    {
        double[] lon = {0};
        double[] lat = {0};
        STM2LL_Output output = new STM2LL_Output();
        if(jvm_arch.contains("64"))
            lib_core64.Convert_XY_To_Geo(X, Y, zone, lon, lat);
        else
            lib_core32._Convert_XY_To_Geo(X, Y, zone, lon, lat);
        output.lon = lon[0];
        output.lat = lat[0];
        return output;
    }
    
    boolean file_exists(String filename)
    {
        File f = new File(filename);        
        return f.exists();
    }
    
    public void setRetrieveResources(AbstractElevationModel model, boolean value)
    {
        AVList params = (AVList) model.getValue(AVKey.CONSTRUCTION_PARAMETERS);
        if (params == null)
            return;

        params.setValue(AVKey.RETRIEVE_PROPERTIES_FROM_SERVICE, value);
    }
    
    boolean bRegistered = false;
    static public MainFrame frame = null;
    public MainFrame()
    {
        frame = this;
        jvm_arch = System.getProperty("sun.arch.data.model");
        if(!is_init)    init("MainFrame");
//        if(Settings.bDebug)
        {
            System.out.println("locale~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            System.out.println("system locale: "+Locale.getDefault().toString());
            Locale.setDefault(new Locale("EN","US"));
            System.out.println("mapviewer locale: "+Locale.getDefault().toString());
            System.out.println("mapviewer file encoding: "+System.getProperty("file.encoding"));
            System.out.println("java~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            System.out.println("java.home: "+System.getProperties().getProperty("java.home"));
            System.out.println("java.version: "+System.getProperty("java.version"));
            System.out.println("java.version.date: "+System.getProperty("java.version.date"));
            System.out.println("java.vendor.version: "+System.getProperty("java.vendor.version"));
            System.out.println("java.vendor.url: "+System.getProperty("java.vendor.url"));
            System.out.println("java.vendor.url.bug: "+System.getProperty("java.vendor.url.bug"));
            System.out.println("java.specification.name: "+System.getProperty("java.specification.name"));
            System.out.println("java.specification.vendor: "+System.getProperty("java.specification.vendor"));
            System.out.println("java.specification.version: "+System.getProperty("java.specification.version"));
            System.out.println("java.vm.name: "+System.getProperty("java.vm.name"));
            System.out.println("java.vm.vendor: "+System.getProperty("java.vm.vendor"));
            System.out.println("java.vm.version: "+System.getProperty("java.vm.version"));
            System.out.println("java.vm.info: "+System.getProperty("java.vm.info"));
            System.out.println("java.vm.specification.name: "+System.getProperty("java.vm.specification.name"));
            System.out.println("java.vm.specification.vendor: "+System.getProperty("java.vm.specification.vendor"));
            System.out.println("java.vm.specification.version: "+System.getProperty("java.vm.specification.version"));
            System.out.println("java.runtime.name: "+System.getProperty("java.runtime.name"));
            System.out.println("java.runtime.version: "+System.getProperty("java.runtime.version"));
            System.out.println("java.class.version: "+System.getProperty("java.class.version"));
            System.out.println("jdk.debug: "+System.getProperty("jdk.debug"));
            System.out.println("sun.java.launcher: "+System.getProperty("sun.java.launcher"));
            System.out.println("sun.management.compiler: "+System.getProperty("sun.management.compiler"));
            System.out.println("sun.arch.data.model: "+jvm_arch+" bits");     
            System.out.println("symbols~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            System.out.println("military symbols path: "+Configuration.getStringValue(AVKey.MIL_STD_2525_ICON_RETRIEVER_PATH));
            System.out.println("symbols~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        }

        if(!file_exists(strSettingsPath))    saveSettings();
        loadSettings();
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) {
                saveSettings();
                save_camera_settings();
                save_myplaces();
            }
        });        
        
/* Full Version        
//        if(Settings.bDebug)
//        {
//            if(jvm_arch.contains("64"))
//                lib_core_path = "D:\\Ali\\java\\Tools\\Protection\\DLL\\Win64\\Release\\Core.dll";
//            else
//                lib_core_path = "D:\\Ali\\java\\Tools\\Protection\\DLL\\Win32\\Release\\Core.dll";
//        }
//        else
        {
            if(jvm_arch.contains("64"))
                lib_core_path = Paths.get(strAppPath, "Core64.dll").toString();
            else
                lib_core_path = Paths.get(strAppPath, "Core32.dll").toString();
        }
       
        if(!file_exists(lib_core_path))
        {
            System.out.println("MapViewer [Demo Version]");
            System.exit(0);
        }
        
        if(jvm_arch.contains("64"))
        {
            lib_core64 = (myCore64) Native.load(lib_core_path, myCore64.class);
            if(lib_core64 == null)
            {
                System.out.println("MapViewer [Demo Version]");
                System.exit(0);
            }
            System.out.println("System ID: "+lib_core64.System_ID());
        }
        else
        {
            lib_core32 = (myCore32) Native.load(lib_core_path, myCore32.class);
            if(lib_core32 == null)
            {
                System.out.println("MapViewer [Demo Version]");
                System.exit(0);
            }
            System.out.println("System ID: "+lib_core32._System_ID());
        }
*/        
        
//        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
//        double lon,lat;
//        lon = 36.276549;
//        lat = 33.513790;
//        System.out.println("input: "+lon+" , "+lat);
//        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
//        LL2STM_Output output1;
//        output1 = LL2STM(lon, lat);
//        System.out.println("LL2STM: "+output1.X+" , "+output1.Y+" , "+output1.zone);
//        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
//        double[] res = mv_stm.Convert_Geo_To_XY(lon, lat);
//        output1.X = res[0];
//        output1.Y = res[1];
//        output1.zone = (int)res[2];
//        System.out.println("Java LL2STM: "+output1.X+" , "+output1.Y+" , "+output1.zone);
//        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
//        double X,Y;
//        int zone;
//        STM2LL_Output output2;
//        X = output1.X;
//        Y = output1.Y;
//        zone = output1.zone;
//        output2 = STM2LL(X, Y, zone);
//        System.out.println("STM2LL: "+output2.lon+" , "+output2.lat);
//        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
//        res = mv_stm.Convert_XY_To_Geo(X, Y, zone);
//        output2.lon = res[0];
//        output2.lat = res[1];
//        System.out.println("Java STM2LL: "+output2.lon+" , "+output2.lat);
//        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
//        Deg2UTM deg2UTM = new Deg2UTM(lat, lon);
//        System.out.println("Java LL2UTM: "+deg2UTM.Easting+" , "+deg2UTM.Northing+" , "+deg2UTM.Zone+deg2UTM.Letter);
//        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
//        UTM2Deg utm2Deg = new UTM2Deg(deg2UTM.Easting, deg2UTM.Northing, deg2UTM.Zone, deg2UTM.Letter);
//        System.out.println("Java UTM2LL: "+utm2Deg.longitude+" , "+utm2Deg.latitude);
//        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
//        String strLongitude = Location.convert(lon, Location.FORMAT_SECONDS);
//        String strLatitude = Location.convert(lat, Location.FORMAT_SECONDS);
//        System.out.println("Longitude: "+strLongitude);
//        System.out.println("Latitude: "+strLatitude);
//        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
//        double lon2 = Location.convert(strLongitude);
//        double lat2 = Location.convert(strLatitude);
//        System.out.println("Java DMS: "+lon2+" , "+lat2);
//        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
//        System.exit(0);
        
//        myKernel32 lib_kernel32 = (myKernel32) Native.load("kernel32", myKernel32.class);
//        lib_kernel32.Beep(698, 500);
//        lib_kernel32.Sleep(500);
//        lib_kernel32.Beep(698, 500);    
        
//        Kernel32 lib_user32 = (Kernel32) Native.load("user32", Kernel32.class);
//        String strText = "Text";
//        String strCaption = "Caption";
//        UINT uType = new UINT(0);
//        lib_user32.MessageBoxW(null, new WString(strText), new WString(strCaption), uType);
        
//        if(Settings.bDebug)
//            lib_mv_plugin_radar = (myKernel32) Native.load("D:\\Ali\\MapViewer\\MapViewerXE7\\MapViewer64_Bin\\Win64\\mv_plugin_Radar.dll", myKernel32.class);
//        else
//            lib_mv_plugin_radar = (myKernel32) Native.load(Paths.get(strAppPath, "mv_plugin_Radar.dll").toString(), myKernel32.class);
//        String plugin_type = lib_mv_plugin_radar.plugin_type();
//        String plugin_name = lib_mv_plugin_radar.plugin_name();
//        String plugin_version = lib_mv_plugin_radar.plugin_version();
//        System.out.println("plugin_type: "+plugin_type);
//        System.out.println("plugin_name: "+plugin_name);
//        System.out.println("plugin_version: "+plugin_version);
//        lib_mv_plugin_radar.plugin_create();
        
//        lib_mv_plugin_radar.plugin_about();
//        lib_mv_plugin_radar.plugin_execute();
//        lib_mv_plugin_radar.plugin_settings();
//        lib_mv_plugin_radar.plugin_destroy();

//    Runnable task = () -> {
//        try
//        {
//            SwingUtilities.invokeAndWait(new Runnable()
//            {
//                public void run()
//                {
//                    lib_mv_plugin_radar.plugin_execute();
//                }
//            });        
//        }catch(Exception e)
//        {
//            System.err.println("createGUI didn't successfully complete");
//        }            

//        SwingUtilities.invokeLater(() -> {
//            lib_mv_plugin_radar.plugin_execute();
//        });
//    };
//    task.run();
//    Thread thread = new Thread(task);
//    thread.start();


//    ExecutorService executor = Executors.newFixedThreadPool(5);
//    Runnable job = new Runnable() {
//         public void run() {
//            // do some work
//            SwingUtilities.invokeLater(() -> {
//                lib_mv_plugin_radar.plugin_execute();
//            });
//         }
//    };
//    executor.execute(job);


//    Thread thread = new Thread(new Runnable()
//    {
//        public void run()
//        {
////            createWindowAndLoop("MapViewer");
//            SwingUtilities.invokeLater(() -> {
//                lib_mv_plugin_radar.plugin_execute();
//            });
//            
////            int nWnd = lib_mv_plugin_radar.plugin_execute();
////            try {
////                Thread.sleep(1000);
//////            IntByReference iRef = new IntByReference();
//////            iRef.setValue(nWnd);
//////            HWND hWnd = new HWND(iRef.getPointer());
//////            User32.INSTANCE.ShowWindow(hWnd, SW_SHOW);
//////            User32.INSTANCE.UpdateWindow(hWnd);
//////            log("hWnd: "+hWnd.toString());
////            } catch (InterruptedException ex) {
////                Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
////            }
//
////            HWND hWnd = User32.INSTANCE.FindWindowEx(null, null, null, "Execute");
////            log("hWnd: "+hWnd);
////            User32.INSTANCE.SetWindowPos(hWnd, null, 0, 0, 0, 0, SWP_NOSIZE);
////
////            MSG msg = new MSG();
////            while (true)
////            {
//////                if(User32.INSTANCE.GetMessage(msg, hWnd, 0, 0) > 0)
////                if(User32.INSTANCE.GetMessage(msg, null, 0, 0) > 0)
////                {
////                    User32.INSTANCE.TranslateMessage(msg);
////                    User32.INSTANCE.DispatchMessage(msg);
//////                    if(msg.hWnd == hWnd)    log("GetMessage: "+msg.hWnd);
////                    if(msg.hWnd == hWnd)    log("GetMessage: [msg.hWnd:"+msg.hWnd+"], ["+hWnd+"]");
////                }
////                User32.INSTANCE.UpdateWindow(hWnd);
////            }
//        }
//    });
//    thread.start();

//    User33.WNDPROC wndProcCallbackListener = null;
//    final JFrame frame = new JFrame("Shutdown Test");
//    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//    frame.setPreferredSize(new Dimension(400,400));
//    frame.setVisible(true);

//    JFrame frame = new JFrame("JFrame Example");  
//    JPanel panel = new JPanel();  
//    panel.setLayout(new FlowLayout());  
//    JLabel label = new JLabel("JFrame By Example");  
//    JButton button = new JButton();  
//    button.setText("Button");  
//    panel.add(label);  
//    panel.add(button);  
//    frame.add(panel);  
//    frame.setSize(200, 300);  
////    frame.setLocationRelativeTo(null);  
//    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  
//    frame.setVisible(true);  
//        
//    Kernel32 INSTANCE = (Kernel32) Native.load("Kernel32", Kernel32.class);
//    SYSTEMTIME time = new SYSTEMTIME();
//    INSTANCE.GetSystemTime(time);
//    System.out.println("Day of the Week " + time.wDayOfWeek);
//    System.out.println("Year : " + time.wYear);
//    SYSTEM_INFO systeminfo = new SYSTEM_INFO();
//    INSTANCE.GetSystemInfo(systeminfo);
//    System.out.println("Processor Type : " + systeminfo.dwProcessorType);
//    System.out.println("System Metrics : " + User32.INSTANCE.GetSystemMetrics(1));
//    HWND hWnd = new HWND();
//    System.out.println("Setting HWPointer");
//    hWnd.setPointer(Native.getComponentPointer(frame));
//    System.out.println("Getting current wnd proc ptr");
//    final LONG_PTR prevWndProc = User33.INSTANCE.GetWindowLongPtrW(hWnd, User32.GWL_WNDPROC);
//    System.out.println("Creating new wnd proc ptr");
//    User33.WNDPROC proc = new User33.WNDPROC() {
//        public LRESULT callback(HWND wnd, int msg, WPARAM param, LPARAM param2) {
//            System.out.println("Received msg : " + msg);
//            if (msg != WM_QUERYENDSESSION) {
//                return User33.INSTANCE.CallWindowProcW(prevWndProc, wnd, msg, param, param2);
//            } else {
//                return new LRESULT(0);
//            }
//        }
//    };
//    System.out.println("Setting new Proc Handler " + proc);
//    User33.INSTANCE.SetWindowLongPtrW(hWnd, User33.GWL_WNDPROC, proc);
//    Thread thread = new Thread(new Runnable()
//    {
//        public void run()
//        {
//            WinUser.MSG msg = new WinUser.MSG();
//            while (User32.INSTANCE.GetMessage(msg, hWnd, 0, 0) != 0) {
//                User32.INSTANCE.TranslateMessage(msg);
//                User32.INSTANCE.DispatchMessage(msg);
//            }
//            System.out.println("Prcess messages finished...");
//        }
//    });
//    thread.start();
        
//        FlatLightLaf.install();
//        FlatDarkLaf.install();
//        FlatIntelliJLaf.install();
//        FlatDarculaLaf.install();
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
//            UIManager.setLookAndFeel( new com.formdev.flatlaf.intellijthemes.FlatLightFlatIJTheme() );
//            UIManager.setLookAndFeel( new com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatDraculaIJTheme() );
//            UIManager.setLookAndFeel( new com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMaterialLighterIJTheme() );
//            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");

//            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
//            UIManager.setLookAndFeel(UIManager.getAuxiliaryLookAndFeels()[0]);
        } catch( Throwable ex ) {
            System.err.println( "Failed to initialize LaF" );
            System.err.println(ex.getLocalizedMessage());
            ex.printStackTrace();
        }
        
//        this.setAlwaysOnTop(true); 

        try
        {
            initComponents();
        }
        catch(Throwable ex)
        {
            System.err.println(ex.getLocalizedMessage());
            ex.printStackTrace();
        }
        if(Settings.strLanguage.contains("ar"))
            CURR_LANG = AR_LANG;
        else
            CURR_LANG = EN_LANG;
        this.set_language(CURR_LANG);
        cursor = Cursor.getDefaultCursor();

        this.pack();

        bRegistered = true;
/* Full Version        
        bRegistered = false;
        if(jvm_arch.contains("64"))
        {
            int g_SerialNumber1 = 0;
            int c_nMapViewer_Key = 55555555;
            g_SerialNumber1 = lib_core64.LoadSerialNumber();
            if(g_SerialNumber1 != lib_core64.EncodeSystem_ID(lib_core64.System_ID(),c_nMapViewer_Key))
            {
                System.out.println("MapViewer [Demo Version]");
                System.exit(0);
            }
            else
            {
                System.out.println("MapViewer [Registered Version]");
                bRegistered = true;
                this.setTitle(bundle.getString("F_MapViewer")+" "+bundle.getString("F_Registered_Version"));
            }
        }
        else
        {
            long g_SerialNumber1 = 0;
            long c_nMapViewer_Key = 55555555;
            g_SerialNumber1 = lib_core32._LoadSerialNumber();
            System.out.println("g_SerialNumber1: "+g_SerialNumber1);
            System.out.println("EncodeSystem_ID: "+lib_core32._EncodeSystem_ID(lib_core32._System_ID(),c_nMapViewer_Key));
            if(g_SerialNumber1 != lib_core32._EncodeSystem_ID(lib_core32._System_ID(),c_nMapViewer_Key))
            {
                System.out.println("MapViewer [Demo Version]");
                System.exit(0);
            }
            else
            {
                System.out.println("MapViewer [Registered Version]");
                bRegistered = true;
                this.setTitle(bundle.getString("F_MapViewer")+" "+bundle.getString("F_Registered_Version"));
            }
        }
        System.out.println("end~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
//        System.exit(0);
*/        
//        this.setFocusable(true);
//        this.addKeyListener(new java.awt.event.KeyAdapter() {
//
//            @Override
//            public void keyTyped(KeyEvent e) {
//                System.out.println("you typed a key");
//            }
//
//            @Override
//            public void keyPressed(KeyEvent e) {
//                System.out.println("you pressed a key");
//            }
//
//            @Override
//            public void keyReleased(KeyEvent e) {
//                System.out.println("you released a key");
//            }
//        });        
    
        // Force WorldWind not to use the network
        if(Settings.bOffline)
        {
            WorldWind.getNetworkStatus().setOfflineMode(true);            
            WorldWind.setOfflineMode(true);            
            Configuration.setValue(AVKey.OFFLINE_MODE, true);
        }

        ImageIcon icon = new ImageIcon(getClass().getResource("/res/mapviewer_icon.png"));  
        this.setIconImage(icon.getImage());  

        //**********************************************************************
        // Set up Earth model
        //**********************************************************************
        // Build a WorldWindowGLCanvas object.
        // Note that a WorldWindowsGLCanvas acts like a JPanel object.
        wwd = new WorldWindowGLCanvas();
        wwd.setEnableGpuCacheReinitialization(true);
        
        // set view
        flyView = new BasicFlyView();
//        wwd.setView(flyView);

        orbitView = new BasicOrbitView();
        wwd.setView(orbitView);

        // Build a Model object.
//        Model model = (Model)WorldWind.createConfigurationComponent(AVKey.MODEL_CLASS_NAME);
        
        Model model = new BasicModel(new Earth(), null);// okkkkkkk
//        model.setValue(AVKey.RETRIEVE_PROPERTIES_FROM_SERVICE, false);// AliSoft 2021.06.30
//        Model model = new BasicModel(new EarthFlat(), null);
//        ((EarthFlat) model.getGlobe()).setProjection(new ProjectionSinusoidal());
//        ((EarthFlat) model.getGlobe()).setProjection(new ProjectionUTM(6));
//        ((EarthFlat) model.getGlobe()).setProjection(new ProjectionUPS(AVKey.NORTH));
//        ((EarthFlat) model.getGlobe()).setProjection(new ProjectionEquirectangular());
        
//        ElevationModel defaultElevationModel = model.getGlobe().getElevationModel();
//        cm.addElevationModel(defaultElevationModel);
//        cm.addElevationModel(em);

        // create empty elevation model
        CompoundElevationModel cm = new CompoundElevationModel();
//        setRetrieveResources(cm, false);
//        cm.setNetworkRetrievalEnabled(false);
//        cm.setValue(AVKey.RETRIEVE_PROPERTIES_FROM_SERVICE, false);// AliSoft 2021.06.30
        model.getGlobe().setElevationModel(cm);
        wwd.setModel(model);
        wwd.getSceneController().setVerticalExaggeration(1);

        // zero elevation model
//        wwd.getModel().getGlobe().setElevationModel(new ZeroElevationModel());

        // Apply EGM96 Corrections
//        Model m = wwd.getModel();
//        try
//        {
//            ((Earth) m.getGlobe()).applyEGMA96Offsets("config/EGM96.dat");
//        }
//        catch (IOException e)
//        {
//            e.printStackTrace();
//        }

        // remove all installed locations
        for(File file:WorldWind.getDataFileStore().getLocations())
        {
            if(WorldWind.getDataFileStore().isInstallLocation(file.getAbsolutePath()))
            {
                WorldWind.getDataFileStore().removeLocation(file.getAbsolutePath());
            }
        }

//            FileSystem fs;
//            String zipFilePath = "D:/Ali/WorldWind/MapViewer/data/dtmz/DEM_Syria.zip";
//            URI uri = URI.create("jar:file:/"+zipFilePath);
//            System.out.println("DataFileStore Source: "+uri);
//            Map<String, String> env = Map.of("create", "false");
//            fs = FileSystems.newFileSystem(uri, env);
//            Path path = fs.getPath("DEM_Syria/");

        WorldWind.getDataFileStore().addLocation(0,Paths.get(strDataPath, "dtm").toString(), true);
        
        if(Settings.bDebug)
        {
            for(File file:WorldWind.getDataFileStore().getLocations())
            {
                System.out.println("DataFileStore: ["+file.getAbsolutePath()+"]");
            }
        }
        installedDataFrame = new InstalledDataFrame(WorldWind.getDataFileStore(), wwd);
        installedDataFrame.setVisible(false);
        
        starsLayer = new StarsLayer();
//        starsLayer.setStarsFileName(getClass().getResource("/res/Hipparcos_Stars_Mag6x5044.dat").getFile());
        
        crosshairLayer = new myCrosshairLayer();
        
        terrainProfileLayer = new TerrainProfileLayer();
        terrainProfileLayer.setEventSource(wwd);
        terrainProfileLayer.setEnabled(false);
//        terrainProfileLayer.setStartLatLon(Position.fromDegrees(33.513805, 36.276518));
//        terrainProfileLayer.setEndLatLon(Position.fromDegrees(33.548778, 36.269847));

        mgrsGraticuleLayer = new MGRSGraticuleLayer();
        mgrsGraticuleLayer.setName("UTM Grid Layer");
        mgrsGraticuleLayer.setEnabled(false);
        
        // clear layers
        wwd.getModel().getLayers().clear();
        wwd.getModel().getLayers().add(new SkyColorLayer());
        wwd.getModel().getLayers().add(new SkyGradientLayer());
        wwd.getModel().getLayers().add(starsLayer);
//        wwd.getModel().getLayers().add(new NASAWFSPlaceNameLayer());
//        wwd.getModel().getLayers().add(new mv_WFSPlaceNameLayer());
//        wwd.getModel().getLayers().add(myOpenStreetMapShapefileLoader.makeLayerFromOSMPlacesSource(Paths.get(strDataPath, "shapefiles", "syria-latest-free", "gis_osm_places_free_1.shp").toString(), "village"));
//        wwd.getModel().getLayers().add(myOpenStreetMapShapefileLoader.makeLayerFromOSMPlacesSource(Paths.get(strDataPath, "shapefiles", "villages", "villages.shp").toString(), null));
//        wwd.getModel().getLayers().add(myOpenStreetMapShapefileLoader.makeLayerFromOSMPlacesSource(Paths.get(strDataPath, "shapefiles", "places", "places.shp").toString(), null));

        String strOSM = Paths.get(strDataPath, "shapefiles", "places.zip").toString();
        if(!file_exists(strOSM))
        {
            System.out.println("makeLayerFromOSMPlacesZipSource: ["+strOSM+"] not exists.");
        }
        else
        {
            Layer osm_zip_layer = myOpenStreetMapShapefileLoader.makeLayerFromOSMPlacesZipSource(strOSM, null);
            if(osm_zip_layer != null)   
            {
                osm_zip_layer.setName(bundle.getString("L_Places"));
                wwd.getModel().getLayers().add(osm_zip_layer);
            }
        }
        
//        wwd.getModel().getLayers().add(new GeoNamesLayer());
//        wwd.getModel().getLayers().add(new CountryBoundariesLayer());
        wwd.getModel().getLayers().add(new ScalebarLayer());
        wwd.getModel().getLayers().add(crosshairLayer);
//        wwd.getModel().getLayers().add(new CompassLayer());
//        wwd.getModel().getLayers().add(new WorldMapLayer());
//        wwd.getModel().getLayers().add(new UTMGraticuleLayer());
//        wwd.getModel().getLayers().add(new LatLonGraticuleLayer());
        wwd.getModel().getLayers().add(terrainProfileLayer);
        wwd.getModel().getLayers().add(mgrsGraticuleLayer);

//        load_shapefile(Paths.get(strDataPath, "shapefiles", "TM_WORLD_BORDERS-0.3", "TM_WORLD_BORDERS-0.3.shp").toString(),false);
//        load_shapefile(Paths.get(strDataPath, "shapefiles", "cities", "cities.shp").toString(),false);
        load_zip_shapefile(Paths.get(strDataPath, "shapefiles", "TM_WORLD_BORDERS-0.3.zip").toString(),false);
        load_zip_shapefile(Paths.get(strDataPath, "shapefiles", "cities.zip").toString(),false);

//        load_shapefile(Paths.get(strDataPath, "shapefiles", "syria-latest-free", "gis_osm_places_free_1.shp").toString(),false);
//        create_extruded_shapefiles(Paths.get(strDataPath, "shapefiles", "BayArea.shp").toString());
//        load_gpx(Paths.get(strDataPath, "gps", "tuolumne.gpx").toString());

//        wwd.getModel().getLayers().add(new OSMMapnikLayer());
//        wwd.getModel().getLayers().add(new GoogleEarthLayer());

//        wwd.getModel().getLayers().add(new MyOSMLayer());
//        wwd.getModel().getLayers().add(new MyOSMZipLayer("C:\\Map\\Syria\\Syria1_10_LZMA.zip"));// invalid compression method
//        wwd.getModel().getLayers().add(new MyOSMZipLayer("C:\\Map\\Syria\\Syria1_10_BZip2.zip"));// invalid compression method
//        wwd.getModel().getLayers().add(new MyOSMZipLayer("C:\\Map\\Syria\\Syria1_10_Deflate.zip"));// ok - valid compression method
//        wwd.getModel().getLayers().add(new MyOSMZipLayer("C:\\Map\\Syria\\Syria1_10.zip"));// ok - valid compression method
//        wwd.getModel().getLayers().add(new MyOSMZipLayer("C:\\Map\\Syria\\Syria1_10_Deflate2.zip"));// ok - valid compression method
//        wwd.getModel().getLayers().add(new MyOSMZipLayer("C:\\Map\\Syria\\Syria1_10_Store.zip"));// ok - valid compression method
//        wwd.getModel().getLayers().add(new MyOSMZipLayer("C:\\Map\\syria_png_1_15\\syria_png_1_15.zip"));// ok - compressed using 7zip/winrar zip
//        wwd.getModel().getLayers().add(new MyOSMZipLayer("C:\\Map\\syria_png_1_16\\syria_png_1_16.zip"));
//        wwd.getModel().getLayers().add(new MyOSMZipLayer("C:\\Map\\syria_png_1_17\\syria_png_1_17.zip"));
//        wwd.getModel().getLayers().add(new MyOSMZipLayer("C:\\Map\\mbtiles\\Syria1_15\\Syria1_15.zip"));
//        wwd.getModel().getLayers().add(new MyOSMZipLayer(Paths.get(strDataPath, "maps", "zip", "png", "World1_8.zip").toString(),".png"));
//        wwd.getModel().getLayers().add(new MyOSMZipLayer("C:\\Map\\mbtiles\\Syria1_16\\Syria1_16.zip",".png"));
//        if(Settings.bDebug) wwd.getModel().getLayers().add(new MyOSMZipLayer("D:\\Ali\\MapViewer\\Syria_Maps\\zip\\png\\Syria1_17.zip",".png", null, null));// okkkkkkkk
//        wwd.getModel().getLayers().add(new MyOSMZipLayer("D:\\Ali\\MapViewer\\Syria_Maps\\zip\\png\\Syria1_17.zip",".png", null, null));// okkkkkkkk
//        if(Settings.bDebug) wwd.getModel().getLayers().add(new MyOSMZipLayer("D:\\Ali\\MapViewer\\Syria_Maps\\zip\\png\\Syria1_17.zip",".png","MOBAC","D:\\Ali\\WorldWind\\MapViewer\\data\\overlays\\zip\\png\\Both.zip",".png"));// okkkkkkkk
//        wwd.getModel().getLayers().add(new MyOSMZipLayer("D:\\Ali\\MapViewer\\Syria_Maps\\zip\\png\\Syria1_17.zip",".png","MOBAC",null,null));// okkkkkkkk
//        wwd.getModel().getLayers().add(new MyOSMZipLayer(Paths.get(strDataPath, "maps", "zip", "png", "Syria1_17.zip").toString(),".png","MOBAC",Paths.get(strDataPath, "overlays", "zip", "png", "Both.zip").toString(),".png"));// okkkkkkkk
//        wwd.getModel().getLayers().add(new MyOSMZipLayer(Paths.get(strDataPath, "maps", "zip", "png", "Syria1_17.zip").toString(),".png","MOBAC",null,null));// okkkkkkkk
//        wwd.getModel().getLayers().add(new MyOSMZipLayer(Paths.get(strDataPath, "maps", "zip", "png", "syria_png_1_17.zip").toString(),".png","MOBAC",null,null));// okkkkkkkk
//        wwd.getModel().getLayers().add(new MyOSMZipLayer(Paths.get(strDataPath, "maps", "zip", "jpg", "SAS.zip").toString(),".jpg","SAS",null,null));// okkkkkkkk
//        wwd.getModel().getLayers().add(new MyOSMZipLayer(Paths.get(strDataPath, "maps", "zip", "jpg", "TMS.zip").toString(),".jpg","TMS",null,null));// okkkkkkkk
//        wwd.getModel().getLayers().add(new MyOSMZipLayer("C:\\Map\\png\\map1_12.zip",".png",null,null));// okkkkkkkk
//        wwd.getModel().getLayers().add(new MyOSMZipLayer("D:\\Ali\\WorldWind\\MapViewer\\data\\maps\\zip\\png\\Both.zip",".png"));// okkkkkkkk
//        wwd.getModel().getLayers().add(new MyOSMZipLayer("D:\\Ali\\MapViewer\\Syria_Maps\\zip\\jpg\\Damscus1_19.zip",".jpg"));// okkkkkkkk
//        wwd.getModel().getLayers().add(new MyOSMZipLayer("D:\\Ali\\MapViewer\\Syria_Maps\\zip\\jpg\\Homs1_19.zip",".jpg"));// okkkkkkkk
//        wwd.getModel().getLayers().add(new MyOSMZipLayer("E:\\mbtiles\\SyriaRect18.zip",".png"));

        try
        {
            wwd.getModel().getLayers().add(new MyOSMZipLayer(Settings.strBaseMapPath,Settings.strBaseExtension,Settings.strBaseType,Settings.strOverlayMapPath,Settings.strOverlayExtension));
            System.out.println("map file ["+Settings.strBaseMapPath+", "+Settings.strOverlayMapPath+" loaded successfully...");
        }
        catch(Exception ex)
        {
            System.out.println("map file ["+Settings.strBaseMapPath+" invalid...");
        }
//        wwd.getModel().getLayers().add(new MyOSMZipLayer("D:\\Ali\\WorldWind\\MapViewer\\data\\maps\\zip\\jpg\\Damscus1_19.zip",".jpg","MOBAC",null,null));
//        wwd.getModel().getLayers().add(new MyOSMZipLayer("D:\\Ali\\WorldWind\\MapViewer\\data\\maps\\zip\\jpg\\Halab1_19.zip",".jpg","MOBAC",null,null));
//        wwd.getModel().getLayers().add(new MyOSMZipLayer("D:\\Ali\\WorldWind\\MapViewer\\data\\maps\\zip\\jpg\\Latakia1_19.zip",".jpg","MOBAC",null,null));
//        wwd.getModel().getLayers().add(new MyOSMZipLayer("D:\\Ali\\WorldWind\\MapViewer\\data\\maps\\zip\\jpg\\DierAzore1_19.zip",".jpg","MOBAC",null,null));

//        wwd.getModel().getLayers().add(new MyOSMZipLayer("C:\\Map\\Syria\\Syria1_10_dds\\Syria1_10_dds.zip",".dds"));
//        wwd.getModel().getLayers().add(new MyOSMZipLayer("C:\\Users\\AZUS\\Desktop\\Syria1_10\\map\\Syria1_10.zip"));
//        wwd.getModel().getLayers().add(new MyOSMZipLayer("C:\\Map\\mbtiles\\Syria1_10\\Syria1_10.zip"));
//        wwd.getModel().getLayers().add(new OpenStreetMapWMSLayer());
//        wwd.getModel().getLayers().add(new OSMMapnikLayer());
        
//        wwd.getModel().getLayers().add(new WMSLayer("SyriaRect18", "http://localhost:8080/geoserver/SyriaRect18/wms", "SyriaRect18_02-07"));
//        wwd.getModel().getLayers().add(new WMSLayer("SyriaRect18", "http://localhost:8080/geoserver/SyriaRect18/wms", "SyriaRect18_02-08"));

//        wwd.getModel().getLayers().add(new WMSLayer("SyriaRect18", "http://localhost:8080/geoserver/SyriaRect18/wms", "SyriaRect18"));

//        wwd.getModel().getLayers().add(new WMSLayer("MapViewer", "http://localhost:8080/geoserver/MapViewer/wms", "gis_osm_roads_free_1"));
//        wwd.getModel().getLayers().add(new WMSLayer("MapViewer", "http://localhost:8080/geoserver/MapViewer/wms", "gis_osm_places_free_1"));

        load_maps();
        wwd.redrawNow();
        
//        mbTileLayer = new MBTileLayer("","",wwd);
//        wwd.getModel().getLayers().add(mbTileLayer);
        
//        double elevation = view.getGlobe().getElevation(location.getLatlon().getLatitude(), location.getLatlon().getLongitude());
        status_layer = new StatusLayer();
        status_layer.setEventSource(wwd);
        wwd.getModel().getLayers().add(status_layer);

        propertiesFrame = new PropertiesFrame();
        propertiesFrame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                if(propertiesFrame.is_ok)
                {
                    if(!MI_AddFolder.isEnabled())
                    {
                        MI_AddFolder.setEnabled(true);
                        add_folder(propertiesFrame.TF_Name.getText());
                        wwd.redraw();
                    }

                    if(!MI_AddPlacemark.isEnabled())
                    {
                        MI_AddPlacemark.setEnabled(true);
                        Vec4 tempVec4 = wwd.getView().getCenterPoint();
                        Position position = wwd.getView().getGlobe().computePositionFromPoint(tempVec4);
                        add_placemark(position, propertiesFrame.TF_Name.getText());
                        wwd.redraw();
                    }

                    if(!MI_AddTacticalSymbol.isEnabled())
                    {
                        MI_AddTacticalSymbol.setEnabled(true);
                        Vec4 tempVec4 = wwd.getView().getCenterPoint();
                        Position position = wwd.getView().getGlobe().computePositionFromPoint(tempVec4);
                        add_tactical_symbol(position, propertiesFrame.TF_Name.getText(), propertiesFrame.TF_IconPath.getText());
                        wwd.redraw();
                    }

                    if(!MI_AddPath.isEnabled())
                    {
                        MI_AddPath.setEnabled(true);
                        add_path(polyline, propertiesFrame.TF_Name.getText());
                        layer_kml.removeRenderable(polyline);
                        
//                        clear(polyline.getPositions());
                    }

                    if(!MI_AddPolygon.isEnabled())
                    {
                        MI_AddPolygon.setEnabled(true);
                        add_polygon(polygon, propertiesFrame.TF_Name.getText());
                        layer_kml.removeRenderable(polyline);
                        layer_kml.removeRenderable(polygon);
                        
//                        clear(polyline.getPositions());
//                        clear(polygon.getOuterBoundary());
                    }

                    if(!MI_Properties.isEnabled())
                    {
                        MI_Properties.setEnabled(true);
                        set_object_properties(currObject);
                    }
                }
                else
                {
                    if(!MI_AddPath.isEnabled())
                    {
                        layer_kml.removeRenderable(polyline);
                    }
                    
                    if(!MI_AddPolygon.isEnabled())
                    {
                        layer_kml.removeRenderable(polyline);
                        layer_kml.removeRenderable(polygon);
                    }
                }
            }
        });                

        layer_kml = new RenderableLayer();
        layer_kml.setName(bundle.getString("L_Drawing"));
        wwd.getModel().getLayers().add(layer_kml);
        
        normalShapeAttributes0 = new BasicShapeAttributes();
        normalShapeAttributes0.setInteriorMaterial(Material.GREEN);
        normalShapeAttributes0.setOutlineMaterial(Material.YELLOW);
        normalShapeAttributes0.setInteriorOpacity(0.5);
        normalShapeAttributes0.setOutlineOpacity(0.5);
        normalShapeAttributes0.setOutlineWidth(1.0);

        highlightShapeAttributes0 = new BasicShapeAttributes();
        highlightShapeAttributes0.setInteriorMaterial(Material.RED);
        highlightShapeAttributes0.setOutlineMaterial(Material.YELLOW);
        highlightShapeAttributes0.setInteriorOpacity(0.5);
        highlightShapeAttributes0.setOutlineOpacity(0.5);
        highlightShapeAttributes0.setOutlineWidth(2.0);

        Color lines_color = normalShapeAttributes0.getOutlineMaterial().getDiffuse();
        lines_color = setColorAlpha(lines_color, (int) Math.round(255.0*normalShapeAttributes0.getOutlineOpacity()));
        normalShapeAttributes0.setOutlineMaterial(new Material(lines_color));

        Color area_color = normalShapeAttributes0.getInteriorMaterial().getDiffuse();
        area_color = setColorAlpha(area_color, (int) Math.round(255.0*normalShapeAttributes0.getInteriorOpacity()));
        normalShapeAttributes0.setInteriorMaterial(new Material(area_color));
        
        polyline = new gov.nasa.worldwind.render.Path();
        polyline.setAttributes(normalShapeAttributes0);
        polyline.setHighlightAttributes(highlightShapeAttributes0);
        polyline.setHighlighted(true);

        polygon = new gov.nasa.worldwind.render.SurfacePolygon();
        polygon.setAttributes(normalShapeAttributes0);
        polygon.setHighlightAttributes(highlightShapeAttributes0);
        polygon.setHighlighted(true);
        
//        create_collada_layer(Paths.get(strDataPath, "collada", "cu_macky", "CU Macky.dae").toString());
//        create_collada_layer(Paths.get(strDataPath, "collada", "t72_2", "t72.dae").toString());
//        create_collada_layer(Paths.get(strDataPath, "collada", "Tunguska", "model.dae").toString());
//        create_collada_layer(Paths.get(strDataPath, "collada", "model", "model.dae").toString());
//        create_collada_layer(Paths.get(strDataPath, "collada", "t72", "models", "t72.dae").toString());
//        create_collada_layer(Paths.get(strDataPath, "collada", "tank.dae").toString());
//        create_collada_layer(Paths.get(strDataPath, "collada", "car.dae").toString());
//        create_collada_layer("D:\\Ali\\WorldWind\\3D_Models\\4060_open3dmodel\\4060_open3dmodel\\Eurofighter\\Eurofighter.dae");

//        schools = load_collada_model(Paths.get(strDataPath, "collada", "Model_KMZ_70", "model.dae").toString());
//        schools.getColladaRoot().setAltitudeMode(WorldWind.RELATIVE_TO_GROUND);
//        schools.getColladaRoot().setPosition(Position.fromDegrees(33.5704037497, 36.2249341308, -896));

//        tunguska = load_collada_model(Paths.get(strDataPath, "collada", "Tunguska", "model.dae").toString());
//        tunguska = load_collada_model(Paths.get(strDataPath, "collada", "box", "model.dae").toString());

//        tunguska = load_collada_model(Paths.get(strDataPath, "collada", "elantra", "model.dae").toString());
//        tunguska.getColladaRoot().setAltitudeMode(WorldWind.CLAMP_TO_GROUND);
//        tunguska.getColladaRoot().setPosition(Position.fromDegrees(33.5704037497, 36.2249341308, -896));
//        tunguska.getColladaRoot().setModelScale(new Vec4(3.0));
        
        //create_screen_images();
        //create_surface_images();
        create_balloons_layer();
//        create_tactical_symbols();
//        create_fire();

        
//        add_icons_layer();
//        create_anotations();
//        create_geo_rss(null);
//        create_paths();
//        create_polygons();
//        create_extruded_polygons();
//        create_placemarks_layer();
//        create_surface_shapes();
//        create_surface_texts();
        
        this.wwd.getInputHandler().addMouseListener(new MouseAdapter() {
                    public void mousePressed(MouseEvent e) {
                        if (e.getButton() == MouseEvent.BUTTON1)
                        {
                            if(propertiesFrame != null)
                            {
                                armed = propertiesFrame.isVisible();
                            }
                            if(armed)   addPosition();
//                            System.out.println("mousePressed1: ("+e.getPoint().x+" , "+e.getPoint().y+")");
                        }
                        else
                        if (e.getButton() == MouseEvent.BUTTON3)
                        {
                            if(propertiesFrame != null)
                            {
                                armed = propertiesFrame.isVisible();
                            }
                            if(armed)   removePosition();
//                            System.out.println("mousePressed3: ("+e.getPoint().x+" , "+e.getPoint().y+")");
                        }
                    }
        });
        
        this.wwd.addPositionListener((PositionEvent event) -> {
            if (!active) {
                return;
            }

            if (positions.size() == 1) {
                addPosition();
            } else {
                replacePosition();
            }
        });
                
        wwd.getInputHandler().addKeyListener(new KeyListener(){
            @Override
            public void keyTyped(KeyEvent e)
            {

            }

            @Override
            public void keyPressed(KeyEvent e)
            {
                int key = e.getKeyCode();

                if (key == KeyEvent.VK_DELETE) {
                    MI_DeleteActionPerformed(null);
                }
            }

            @Override
            public void keyReleased(KeyEvent e)
            {
                
            }
        });

        // Prohibit batch picking for the airspaces.
//        this.disableBatchPicking();

        // Tell the scene controller to perform deep picking.
        this.wwd.getSceneController().setDeepPickEnabled(true);
        this.toolTipController = new ToolTipController(wwd, AVKey.DISPLAY_NAME, null);
        this.highlightController = new HighlightController(wwd, SelectEvent.ROLLOVER);

        // Set up the context menu
//        ContextMenuController contextMenuController = new ContextMenuController();
//        wwd.addSelectListener(contextMenuController);

        // Add a select listener in order to determine when the label is selected.
        wwd.addSelectListener(new SelectListener()
        {
//            private final BasicDragger dragger = new BasicDragger(wwd);
            protected PointPlacemark lastPickedPlacemark = null;

            @SuppressWarnings({"UnusedDeclaration"})
            protected void highlight(SelectEvent event, Object o) {
                if (this.lastPickedPlacemark == o) {
                    return; // same thing selected
                }
                // Turn off highlight if on.
                if (this.lastPickedPlacemark != null) {
                    this.lastPickedPlacemark.setHighlighted(false);
                    this.lastPickedPlacemark = null;
                }

                // Turn on highlight if object selected.
                if (o != null && o instanceof PointPlacemark) {
                    this.lastPickedPlacemark = (PointPlacemark) o;
                    this.lastPickedPlacemark.setHighlighted(true);
                }
            }
            
            @Override
            public void selected(SelectEvent event)
            {
                try {
                    if (event.getEventAction().equals(SelectEvent.ROLLOVER)) {
                        highlight(event, event.getTopObject());
//                        if(Settings.bDebug)  System.out.println("ROLLOVER "+idx+": event.getTopObject(): "+event.getTopObject()+", "+event.getTopObject().getClass());
                        idx++;
                        is_mouse_on_globe = false;
                    }
                    else if (event.getEventAction().equals(SelectEvent.RIGHT_PRESS)) // Could do RIGHT_CLICK instead
                    {
                        currObject = event.getTopObject();

                        if(currObject instanceof WWObjectImpl)
                        {
                            WWObjectImpl feature = (WWObjectImpl)currObject;
                            if(feature != null)
                            {
                                KMLFeatureTreeNode featureNode = (KMLFeatureTreeNode)feature.getValue("oghab.mapviewer.avKey.tree_node");
                                if(featureNode != null)
                                {
                                    layerTree.makeVisible(featureNode.getPath());
                                    treeLayout.setCurrTreeNode(featureNode);
                                    treeLayout.invalidate();
                                    wwd.redrawNow();
                                    if(Settings.bDebug)  System.out.println("featureNode: "+featureNode.getText()+", "+featureNode.getPath().toString());
                                }
                            }
                        }

                        if(currObject instanceof BasicTreeNode)
                        {
                            BasicTreeNode node = (BasicTreeNode)currObject;
                            if(Settings.bDebug)  System.out.println("currObject: "+node.getText()+", "+node.getPath().toString());
                            treeLayout.setCurrTreeNode(node);
                            treeLayout.invalidate();
                            wwd.redrawNow();
                        }

                        show_popup_menu(currObject, event.getMouseEvent().getX(), event.getMouseEvent().getY());
                    }
                    else if (event.getEventAction().equals(SelectEvent.LEFT_PRESS))
                    {
                        currObject = event.getTopObject();

                        if(currObject instanceof WWObjectImpl)
                        {
                            WWObjectImpl feature = (WWObjectImpl)currObject;
                            if(feature != null)
                            {
                                KMLFeatureTreeNode featureNode = (KMLFeatureTreeNode)feature.getValue("oghab.mapviewer.avKey.tree_node");
                                if(featureNode != null)
                                {
                                    layerTree.makeVisible(featureNode.getPath());
                                    treeLayout.setCurrTreeNode(featureNode);
                                    treeLayout.invalidate();
                                    wwd.redrawNow();
                                    if(Settings.bDebug)  System.out.println("featureNode: "+featureNode.getText()+", "+featureNode.getPath().toString());
                                }
                            }
                        }

                        goto_object(currObject);
                        if(Settings.bDebug)  System.out.println("currObject: "+currObject+", "+currObject.getClass());
                        
//                        if(currObject instanceof AVList)
//                        {
//                            AVList list = (AVList)currObject;
////                            mv_TreeNode node = (mv_TreeNode)list.getValue("gov.nasa.worldwind.avKey.Context");
//                            Object obj = list.getValue("gov.nasa.worldwind.avKey.Context");
////                            treeLayout.setCurrTreeNode(node);
//                            treeLayout.invalidate();
//                        }
                        
                        if(currObject instanceof BasicTreeNode)
                        {
                            BasicTreeNode node = (BasicTreeNode)currObject;
                            if(Settings.bDebug)  System.out.println("currObject: "+node.getText()+", "+node.getPath().toString());
                            treeLayout.setCurrTreeNode(node);
                            treeLayout.invalidate();
                            wwd.redrawNow();
                        }
                        
//                        if(currObject instanceof mv_KMLLayerTreeNode)
//                        {
//                            mv_KMLLayerTreeNode node = (mv_KMLLayerTreeNode)currObject;
//                            if(Settings.bDebug)  System.out.println("currObject: "+node.getText()+", "+node.getPath().toString());
//                            treeLayout.setCurrTreeNode(node);
//                            treeLayout.invalidate();
//                        }
                        
//                        if(currObject instanceof mv_KMLFeatureTreeNode)
//                        {
//                            mv_KMLFeatureTreeNode node = (mv_KMLFeatureTreeNode)currObject;
//                            if(Settings.bDebug)  System.out.println("currObject: "+node.getText()+", "+node.getPath().toString());
//                            treeLayout.setCurrTreeNode(node);
//                            treeLayout.invalidate();
//                        }
                    }
                    else if (event.getEventAction().equals(SelectEvent.LEFT_DOUBLE_CLICK))
                    {
                        currObject = event.getTopObject();

                        if(currObject instanceof WWObjectImpl)
                        {
                            WWObjectImpl feature = (WWObjectImpl)currObject;
                            if(feature != null)
                            {
                                KMLFeatureTreeNode featureNode = (KMLFeatureTreeNode)feature.getValue("oghab.mapviewer.avKey.tree_node");
                                if(featureNode != null)
                                {
                                    layerTree.makeVisible(featureNode.getPath());
                                    treeLayout.setCurrTreeNode(featureNode);
                                    treeLayout.invalidate();
                                    wwd.redrawNow();
                                    if(Settings.bDebug)  System.out.println("featureNode: "+featureNode.getText()+", "+featureNode.getPath().toString());
                                }
                            }
                        }

                        goto_object(currObject);
                        if(Settings.bDebug)  System.out.println("currObject: "+currObject+", "+currObject.getClass());
                        
                        if(currObject instanceof BasicTreeNode)
                        {
                            BasicTreeNode node = (BasicTreeNode)currObject;
                            if(Settings.bDebug)  System.out.println("currObject: "+node.getText()+", "+node.getPath().toString());
                            treeLayout.setCurrTreeNode(node);
                            if(layerTree.isPathExpanded(node.getPath()))
                                layerTree.collapsePath(node.getPath());
                            else
                                layerTree.expandPath(node.getPath());
                            treeLayout.invalidate();
                            wwd.redrawNow();
                        }
                    }
                } catch (Exception e) {
                    Util.getLogger().warning(e.getMessage() != null ? e.getMessage() : e.toString());
                }
//                if (event.getTopObject() != null)
//                {
//                    if (event.getTopPickedObject().getParentLayer() instanceof MarkerLayer)
//                    {
//                        PickedObject po = event.getTopPickedObject();
//                        //noinspection RedundantCast
//                        System.out.printf("Track position %s, %s, size = %f\n",
//                            po.getValue(AVKey.PICKED_OBJECT_ID).toString(),
//                            po.getPosition(), (Double) po.getValue(AVKey.PICKED_OBJECT_SIZE));
//                    }
//                }
//                if (event.getEventAction().equals(SelectEvent.HOVER) && event.getObjects() != null)
//                {
//                    System.out.printf("%d objects\n", event.getObjects().size());
//                    if (event.getObjects().size() > 1)
//                    {
//                        for (PickedObject po : event.getObjects())
//                        {
//                            System.out.println(po.getObject().getClass().getName());
//                        }
//                    }
//                }
//
//                if (event.getEventAction().equals(SelectEvent.ROLLOVER))
//                {
//                    PickedObjectList pol = event.getObjects();
//                    System.out.println(" Picked Objects Size " + pol.size());
//                    for (PickedObject po : pol)
//                    {
//                        System.out.println(" Class " + po.getObject().getClass().getName() + "  isTerrian=" + po.isTerrain());
//                    }
//                }
//                else
//                {
//                    PickedObject po = event.getTopPickedObject();
//                    if (po != null && po.getObject() instanceof PointPlacemark)
//                    {
//                        if (event.getEventAction().equals(SelectEvent.LEFT_CLICK))
//                        {
//                            // See if it was the label that was picked. If so, raise an input dialog prompting
//                            // for new label text.
//                            Object placemarkPiece = po.getValue(AVKey.PICKED_OBJECT_ID);
//    //                        if (placemarkPiece != null && placemarkPiece.equals(AVKey.LABEL))
//                            if (placemarkPiece != null)
//                            {
//                                PointPlacemark placemark = (PointPlacemark) po.getObject();
//                                String labelText = placemark.getLabelText();
//                                labelText = JOptionPane.showInputDialog(MainFrame.this, "Enter label text", labelText);
//                                if (labelText != null)
//                                {
//                                    placemark.setLabelText(labelText);
//                                }
//                                event.consume();
//                            }
//                        }
//                    }
//                    else
//                    {
//                        // Delegate dragging computations to a dragger.
//                        this.dragger.selected(event);
//                    }
//                }
            }
        });
       
        AWTInputHandler wwHandler = null;
        // get World Wind's AWTInputHandler class:
        for (MouseMotionListener l : wwd.getMouseMotionListeners()) {
            if(l instanceof AWTInputHandler) {
                wwHandler = (AWTInputHandler)l;
                break;
            }
        }        
        
        if(wwHandler != null) {
            wwHandler.addMouseMotionListener(new MyMouseMotionListener());
            wwHandler.addMouseListener(new MyMouseListener());
        } else {
            // I don't think this should happen unless the AWTInputHandler
            // is explicitly removed by client code
//            logger.error("Couldn't find AWTInputHandler");
        }        

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                initKML();
                try
                {
                    String strMyPlaces = Paths.get(strDataPath, "kml", "My Places.kml").toString();
                    if(!file_exists(strMyPlaces))   save_myplaces();
                    File file = new File(strMyPlaces);
                    kmlDefaultRoot = null;
                    new KMLWorkerThread(file, MainFrame.this).start();
                }
                catch (Exception e)
                {  
                    System.out.println("My Places.kml is invalid...");
                }
            }
        });

//        try
//        {
//            File file = new File(Paths.get(strDataPath, "3d_models", "Model_KMZ_70.kmz").toString());
//            new KMLWorkerThread(file, this).start();
//        }
//        catch (Exception e)
//        {
//            e.printStackTrace();
//        }

        // Show the WAIT cursor because the import may take a while.
//        this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
        // Import the elevations on a thread other than the event-dispatch thread to avoid freezing the UI.
//        Thread t = new Thread(new Runnable()
//        {
//            public void run()
//            {
////                importImagery();
////                importElevations();
//                // Restore the cursor.
////                setCursor(Cursor.getDefaultCursor());
//            }
//        });
//        t.start();

//        Thread camera_thread = new Thread(new Runnable()
//        {
//            String prev_text = "";
//            public void run()
//            {
//                while(true)
//                {
//                    String text = "("+orbitView.getHeading().toDecimalDegreesString(2)+","+orbitView.getPitch().toDecimalDegreesString(2)+","+orbitView.getRoll().toDecimalDegreesString(2)+","+String.format("%.1f", orbitView.getZoom())+")\n";
//                    Position pos = orbitView.getEyePosition();
//                    if(pos != null)  
//                    {
//                        text += "("+pos.getLatitude().toDecimalDegreesString(6)+","+pos.getLongitude().toDecimalDegreesString(6)+","+String.format("%.1f", pos.getAltitude())+")"+"\n";
//                    }
////                    if(Settings.bDebug && (prev_text.compareTo(text) != 0))
////                    {
////                        System.out.println(text);
////                    }
//                    if(MainFrame.balloon != null)   MainFrame.balloon.setText(text);
//                    prev_text = text;
//                    wwd.redraw();// for fire animation
//                }
//            }
//        });
//        camera_thread.start();

        // mouse coordinates
//        try
//        {
//            wwd.addMouseMotionListener(new MouseMotionAdapter() {
//                @Override
//                public void mouseMoved(MouseEvent e){
//                     // Get clicked position on the globe.
////                     Position clickedPosition = wwd.getCurrentPosition();
////                     if(clickedPosition != null)
////                     {
////                        double latitude = clickedPosition.getLatitude().getDegrees();
////                        double longitude = clickedPosition.getLongitude().getDegrees();
////
////                        System.out.println("Moved Latitude: " + latitude);
////                        System.out.println("Moved Longitude: " + longitude);
////                     }
//                }
//            });
//            
//            wwd.addMouseListener(new MouseListener() {
//                @Override
//                public void mouseClicked(MouseEvent e) {
//                    // Get clicked position on the globe.
////                    Position clickedPosition = wwd.getCurrentPosition();
////                    if(clickedPosition != null)
////                    {
////                        double latitude = clickedPosition.getLatitude().getDegrees();
////                        double longitude = clickedPosition.getLongitude().getDegrees();
////
////                        System.out.println("Clicked Latitude: " + latitude);
////                        System.out.println("Clicked Longitude: " + longitude);
////                    }
//                    
//                    popupMenu1.show(wwd , e.getX(), e.getY()); 
//                }
//
//                @Override
//                public void mousePressed(MouseEvent e) {
//                }
//
//                @Override
//                public void mouseReleased(MouseEvent e) {
//                }
//
//                @Override
//                public void mouseEntered(MouseEvent e) {
//                }
//
//                @Override
//                public void mouseExited(MouseEvent e) {
//                }
//            });
//        }
//        catch (Exception e)
//        {
//            e.printStackTrace();
//        }

        // Load WorldWind model into the sub JPanel
        jPanel2.add(wwd);
//        jPanel2.setVisible(true);
        jPanel2.addComponentListener(new ComponentListener() {
            @Override
            public void componentResized(ComponentEvent e) {
                wwd.setSize(2*(jPanel2.getWidth()/2), 2*(jPanel2.getHeight()/2));
            }

            @Override
            public void componentMoved(ComponentEvent e) {
            }

            @Override
            public void componentShown(ComponentEvent e) {
            }

            @Override
            public void componentHidden(ComponentEvent e) {
            }
        });
        wwd.setSize(2*(jPanel2.getWidth()/2), 2*(jPanel2.getHeight()/2));
        
        jToolBar2.setVisible(Settings.bDebug);
        // File Menu
        MI_ImportImage.setVisible(Settings.bDebug);
        MI_ImportRPF.setVisible(Settings.bDebug);
        MI_SaveAs.setVisible(Settings.bDebug);
        MI_Export.setVisible(Settings.bDebug);
//        MI_ScreenShot.setVisible(Settings.bDebug);

        // Edit Menu
        
        // View Menu
        //MI_Navigation.setVisible(Settings.bDebug);
        MI_SteroMode.setVisible(Settings.bDebug);
        
        // Tools Menu
        MI_Data.setVisible(Settings.bDebug);
        MI_UTMGrid.setVisible(Settings.bDebug);
        MI_Download.setVisible(Settings.bDebug);
        MI_DDSConverter.setVisible(Settings.bDebug);

//        // Create a horizontal split pane containing the layer panel and the WorldWindow panel.
//        JSplitPane horizontalSplitPane = new JSplitPane();
//        horizontalSplitPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
//        horizontalSplitPane.setLeftComponent(jPanel1);
//        horizontalSplitPane.setRightComponent(jPanel2);
//        horizontalSplitPane.setOneTouchExpandable(true);
//        horizontalSplitPane.setContinuousLayout(true); // prevents the pane's being obscured when expanding right
//
//        // Add the vertical split-pane to the frame.
//        jPanel4.add(horizontalSplitPane, BorderLayout.CENTER);
////        this.getContentPane().add(horizontalSplitPane, BorderLayout.CENTER);
////        this.pack();
        
        UDPServer upd_server = new UDPServer(21567);
        upd_server.start();

//        // Create the WorldWindow.
////        this.wwjPanel = new LayerManagerApp.AppPanel();
////        this.getContentPane().add(wwjPanel, BorderLayout.CENTER);
//
//        // Instantiate and position the layer manager panel. This is really the only layer-manager specific code
//        // in this example.
//        LayerAndElevationManagerPanel layerManagerPanel = new LayerAndElevationManagerPanel(wwd);
//        layerManagerPanel.setPreferredSize( new Dimension(400, 400) );
//        layerManagerPanel.setBackground(Color.CYAN);
////        JPanel outerPanel = new JPanel(new BorderLayout(10, 10));
////        outerPanel.add(layerManagerPanel, BorderLayout.CENTER);
////        this.getContentPane().add(outerPanel, BorderLayout.WEST);
//        
//        layerManagerPanel.setVisible(true);
//        layerManagerPanel.update(wwd);
////        jPanel4.setSize( new Dimension(400, 400) );
////        jPanel4.add(layerManagerPanel, BorderLayout.CENTER);
////        jPanel4.setVisible(true);

//        SwingUtilities.invokeLater(new Runnable()
//        {
//            public void run()
//            {
//                layerPanel = new LayerManagerPanel(wwd);
//                layerPanel.setBackground(Color.gray);
//                layerPanel.update(wwd);
//
//                layersFrame = new JFrame("Layers"); 
//                layersFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//                layersFrame.setSize(300, 400); 
//                layersFrame.add(layerPanel, BorderLayout.CENTER);
////                layersFrame.setVisible(true);
//            }
//        });
        
        wwd.redrawNow();

        this.setLocation(new Point(Settings.x, Settings.y));
        this.setSize(new Dimension(Settings.w, Settings.h));
        
        if(!file_exists(strViewstatePath))  save_camera_settings();
        load_camera_settings();
        wwd.redrawNow();
        System.out.println("redraw~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
    }
    // JFrame 
//    private LayerManagerPanel layerPanel;
//    static JFrame layersFrame;
    
    /**w  w w  .  j  a v a2  s.  c o m
     * <p>Remove all elements from the given Iterable.</p>
     * @param iterable the Iterable
     */
    public static void clear(Iterable<?> iterable) {
        if(iterable == null)    return;
        if (iterable instanceof Collection) {
            ((Collection<?>) iterable).clear();
        } else {
            clear(iterable.iterator());
        }
    }

    /**
     * <p>Remove all elements from the given Iterator.</p>
     * @param it the Iterator
     */
    public static void clear(Iterator<?> it) {
        if(it == null)    return;
        while (it.hasNext()) {
            it.next();
            it.remove();
        }
    }
    
    public void animateToPath(gov.nasa.worldwind.render.Path polyline)
    {
        if (polyline == null)   return;
        ballonsLayer.setEnabled(true);
        
        Iterable<? extends Position> positions = polyline.getPositions();
        ArrayList<TrackPointImpl> track_list = new ArrayList<TrackPointImpl>();
        ArrayList<Vec4> speed_list = new ArrayList<Vec4>();
        for (Position p : positions) {
            track_list.add(new TrackPointImpl(p.getLatitude(),p.getLongitude(),p.getAltitude() + 1000,""));
            speed_list.add(Vec4.ONE);
        }
        TrackPointImpl[] track = (TrackPointImpl[]) track_list.toArray(new TrackPointImpl[track_list.size()]);
        Vec4[] speed = (Vec4[]) speed_list.toArray(new Vec4[speed_list.size()]);
        
        FollowTrackViewAnimator.is_first = true;
        FollowTrackViewAnimator animator = FollowTrackViewAnimator.createFollowTrackViewAnimator(orbitView,track,speed);
        orbitView.addAnimator(animator);
        animator.start();
        orbitView.firePropertyChange(AVKey.VIEW, null, orbitView);
        
//        FollowTrackAnimator animator = FollowTrackAnimator.createFollowTrackAnimator(flyView,track,speed);
//        flyView.addAnimator(animator);
//        animator.start();
//        flyView.firePropertyChange(AVKey.VIEW, null, flyView);
    }

    void update_heading_angle(Position p1, Position p2)
    {
//        Earth earth = new Earth();
//        Vec4 start = earth.computePointFromPosition(p1);
//        Vec4 end = earth.computePointFromPosition(p2);
//        Vec4 along = end.subtract3(start).normalize3();
//
//        Vec4 right_wheel = end.add3(along.cross3(upStart).multiply3(tang_width/2.0));
//        Position right_pos = earth.computePositionFromPoint(right_wheel);
//
//        Vec4 left_wheel = end.add3(along.cross3(upStart).multiply3(-tang_width/2.0));
//        Position left_pos = earth.computePositionFromPoint(left_wheel);
//
//        Vec4 front_wheel = end.add3(along.multiply3(tang_length/2.0));
//        Position front_pos = earth.computePositionFromPoint(front_wheel);
//
//        Vec4 back_wheel = end.add3(along.multiply3(-tang_length/2.0));
//        Position back_pos = earth.computePositionFromPoint(back_wheel);

        heading = Position.greatCircleAzimuth(p1, p2);
////        heading = Angle.fromDegrees(-90);
//
////        heading = Vec4.axisAngle(upStart, along, new Vec4[1]).addDegrees(0.0);
////        pitch = Vec4.axisAngle(forwardStart, along, new Vec4[1]).subtractDegrees(0.0);
////        roll = Vec4.axisAngle(forwardStart, along, new Vec4[1]).subtractDegrees(45.0);
////        heading = Angle.fromDegrees(0);
//        pitch = Angle.fromDegrees(90);
////        pitch = Angle.fromDegrees(45);
//        roll = Angle.fromDegrees(0);
//        
//        double right_wheel_ele = orbitView.getGlobe().getElevation(Angle.fromDegrees(right_pos.latitude.degrees), Angle.fromDegrees(right_pos.longitude.degrees)) + h0;
//        double left_wheel_ele = orbitView.getGlobe().getElevation(Angle.fromDegrees(left_pos.latitude.degrees), Angle.fromDegrees(left_pos.longitude.degrees)) + h0;
//        roll = Angle.fromDegrees(-Math.toDegrees(Math.atan2(right_wheel_ele - left_wheel_ele, tang_width/2.0)));
//        
//        double front_wheel_ele = orbitView.getGlobe().getElevation(Angle.fromDegrees(front_pos.latitude.degrees), Angle.fromDegrees(front_pos.longitude.degrees)) + h0;
//        double back_wheel_ele = orbitView.getGlobe().getElevation(Angle.fromDegrees(back_pos.latitude.degrees), Angle.fromDegrees(back_pos.longitude.degrees)) + h0;
//        pitch = Angle.fromDegrees(Math.toDegrees(Math.atan2(front_wheel_ele - back_wheel_ele, tang_length/2.0))).addDegrees(90.0);
    }

    Vec4 upStart = Vec4.UNIT_Y;
    Vec4 forwardStart = Vec4.UNIT_NEGATIVE_Z;
    Vec4 rightStart = Vec4.UNIT_X;
    Angle heading, prev_heading;
    Angle pitch;
    Angle roll;
    double tang_width = 2.0;
    double tang_length = 4.0;
    
//    Earth earth = new Earth();
    void update_pitch_roll_angles(Position p1, Position p2)
    {
        Globe earth = wwd.getModel().getGlobe();
        Vec4 start = earth.computePointFromPosition(p1);
        Vec4 end = earth.computePointFromPosition(p2);
        Vec4 along = end.subtract3(start).normalize3();

        Vec4 right_wheel = end.add3(along.cross3(upStart).multiply3(tang_width/2.0));
        Position right_pos = earth.computePositionFromPoint(right_wheel);

        Vec4 left_wheel = end.add3(along.cross3(upStart).multiply3(-tang_width/2.0));
        Position left_pos = earth.computePositionFromPoint(left_wheel);

        Vec4 front_wheel = end.add3(along.multiply3(tang_length/2.0));
        Position front_pos = earth.computePositionFromPoint(front_wheel);

        Vec4 back_wheel = end.add3(along.multiply3(-tang_length/2.0));
        Position back_pos = earth.computePositionFromPoint(back_wheel);
        
        double right_wheel_ele = orbitView.getGlobe().getElevation(Angle.fromDegrees(right_pos.latitude.degrees), Angle.fromDegrees(right_pos.longitude.degrees)) + h0;
        double left_wheel_ele = orbitView.getGlobe().getElevation(Angle.fromDegrees(left_pos.latitude.degrees), Angle.fromDegrees(left_pos.longitude.degrees)) + h0;
        roll = Angle.fromDegrees(-Math.toDegrees(Math.atan2(right_wheel_ele - left_wheel_ele, tang_width/2.0)));
        
        double front_wheel_ele = orbitView.getGlobe().getElevation(Angle.fromDegrees(front_pos.latitude.degrees), Angle.fromDegrees(front_pos.longitude.degrees)) + h0;
        double back_wheel_ele = orbitView.getGlobe().getElevation(Angle.fromDegrees(back_pos.latitude.degrees), Angle.fromDegrees(back_pos.longitude.degrees)) + h0;
        pitch = Angle.fromDegrees(Math.toDegrees(Math.atan2(front_wheel_ele - back_wheel_ele, tang_length/2.0))).addDegrees(90.0);
    }
    
    public static double mixDouble(double amount, double value1, double value2)
    {
        if (amount < 0)
            return value1;
        else if (amount > 1)
            return value2;
        return value1 * (1.0 - amount) + value2 * amount;
    }
    
    void spline()
    {
        // Array of wavelengths (m)
        double[] wavelength = {185.0e-9, 214.0e-9, 275.0e-9, 361.0e-9, 509.0e-9, 589.0e-9, 656.0e-9};
        // Array of corresponding refractive indices
        double[] refrindex = {1.57464, 1.53386, 1.49634, 1.47503, 1.4619, 1.4583, 1.4564};
        // Interpolation variables
        double x1, y1;

        // Create a CubicSpline instance and initialise it to the data stored in the arrays wavelength and refrindex
        CubicSpline cs = new CubicSpline(wavelength, refrindex);

        // First interpolation at a wavelength of 250 nm
        //   also calculates the required derivatives
        x1 = 2.5e-7;
        y1=cs.interpolate(x1);
        System.out.println("The refractive index of fused quartz at " + x1*1.0e9 + " nm is "+ y1);

        // Second interpolation at a wavelength of 590 nm
        //  uses the derivatives calculated in the first call
        x1 = 5.9e-7;
        y1=cs.interpolate(x1);
        System.out.println("The refractive index of fused quartz at " + x1*1.0e9 + " nm is "+ y1);
    }

    private static long getTimeToMove(Position beginCenterPos, Position endCenterPos)
    {
        final long MIN_LENGTH_MILLIS = 4000;
        final long MAX_LENGTH_MILLIS = 16000;
        return AnimationSupport.getScaledTimeMillisecs(
            beginCenterPos, endCenterPos,
            MIN_LENGTH_MILLIS, MAX_LENGTH_MILLIS);

    }
    
    double zoom = 100;
    double h0 = 20;
    static boolean bTerminate = false;
    protected LengthMeasurer measurer = new LengthMeasurer();
    public void animatePath(gov.nasa.worldwind.render.Path polyline)
    {
//        spline();
        if (polyline == null)   return;
        ballonsLayer.setEnabled(true);

        AnimationRunnableTask animation_runnable = new AnimationRunnableTask();
        animation_runnable.polyline = polyline;
        Thread simulator_thread = new Thread(animation_runnable);
        simulator_thread.start();
    }
    
    public class AnimationRunnableTask implements Runnable {
        public gov.nasa.worldwind.render.Path polyline = null;
        public void run()
        {
            boolean bSaveVideo = false;
            VideoEncoder videoEncoder = null;
            RenderingListener renderingListener = null;
            
            Iterable<? extends Position> positions = polyline.getPositions();
            final double dt = 0.005;

            int n = 0;
            for (Position position : positions) {
                n++;
            }
            double L = polyline.getLength(wwd.getModel().getGlobe());
            double[] lons = new double[n];
            double[] lats = new double[n];
            double[] Ts = new double[n];
            int i = 0;
            double M = 0;
            double T = 1.0;
            Position prev_pos = null;
            for (Position position : positions) {
                if(prev_pos != null)
                {
                    ArrayList<Position> poses = new ArrayList<>();
                    poses.add(prev_pos);
                    poses.add(position);
                    measurer.setPositions(poses);
                    double m = measurer.getLength(wwd.getModel().getGlobe());
                    M += m;
                    T = M/L;
                    Ts[i] = T;
                }
                else
                {
                    T = 0.0;
                    Ts[i] = T;
                }
                lons[i] = position.longitude.degrees;
                lats[i] = position.latitude.degrees;
                i++;
                prev_pos = position;
            }
            final double TT = T;
            CubicSpline cs_lons = new CubicSpline(Ts, lons);
            CubicSpline cs_lats = new CubicSpline(Ts, lats);
            
            if(bSaveVideo)
            {
                videoEncoder = new VideoEncoder();
                VideoEncoder finalVideoEncoder = videoEncoder;
                int fps = (int)Math.round(wwd.getSceneController().getFramesPerSecond());
                videoEncoder.start("d:/test.mp4", fps);
                renderingListener = new RenderingListener(){
                    @Override
                    public void stageChanged(RenderingEvent event)
                    {
                        if (event.getStage().equals(RenderingEvent.AFTER_BUFFER_SWAP) && finalVideoEncoder.is_started)
                        {
                            GLAutoDrawable glad = (GLAutoDrawable) event.getSource();
                            AWTGLReadBufferUtil glReadBufferUtil = new AWTGLReadBufferUtil(glad.getGLProfile(), false);
                            BufferedImage image = glReadBufferUtil.readPixelsToBufferedImage(glad.getGL(), true);
                            finalVideoEncoder.encode_image(image);
                        }
                    }
                };
                wwd.removeRenderingListener(renderingListener); // ensure not to add a duplicate
                wwd.addRenderingListener(renderingListener);
            }

            try
            {
                Position prev_position = null;
                Angle prev_heading = null;
                Angle prev_pitch = null;
                Angle prev_roll = null;
                bTerminate = false;

                double lon0 = cs_lons.interpolate(0);
                double lat0 = cs_lats.interpolate(0);
                double elevation0 = orbitView.getGlobe().getElevation(Angle.fromDegrees(lat0), Angle.fromDegrees(lon0)) + h0;
                Position position0 = Position.fromDegrees(lat0, lon0, elevation0);
                orbitView.setZoom(zoom);
                orbitView.setCenterPosition(position0);
                for(double t=0;t<=TT;t+=dt)
                {
                    double lon = cs_lons.interpolate(t);
                    double lat = cs_lats.interpolate(t);
                    double elevation = orbitView.getGlobe().getElevation(Angle.fromDegrees(lat), Angle.fromDegrees(lon)) + h0;
                    Position position = Position.fromDegrees(lat, lon, elevation);

                    if(prev_position != null)
                    {
                        Position p0 = prev_position;
                        Position p1 = Position.interpolateGreatCircle(0.99, prev_position, position);
                        update_heading_angle(p1, position);
                        update_pitch_roll_angles(p1, position);
                        if(prev_heading == null)
                        {
                            prev_heading = heading;
                            prev_pitch = pitch;
                            prev_roll = roll;
                        }

                        for(double u=0;u<=1.0;u+=0.05)
                        {
                            Position p = Position.interpolateGreatCircle(u, prev_position, position);
                            Angle Heading = Angle.mix(u, prev_heading, heading);
                            Angle Pitch = Angle.mix(u, prev_pitch, pitch);
                            Angle Roll = Angle.mix(u, prev_roll, roll);

                            try {
//                                try
//                                {
//                                    tunguska.getColladaRoot().setPosition(p);
//                                    tunguska.getColladaRoot().setHeading(Heading);
//                                    tunguska.getColladaRoot().setPitch(Pitch.subtractDegrees(90));
//                                    tunguska.getColladaRoot().setRoll(Roll);
//                                }
//                                catch(Throwable ex)
//                                {
//                                    System.out.println(ex);
//                                }

                                try
                                {
//                                    orbitView.setZoom(zoom);
                                    orbitView.setCenterPosition(p);
//                                    orbitView.setHeading(Heading.addDegrees(90));
//                                    orbitView.setPitch(Pitch.subtractDegrees(60));
//                                    orbitView.setRoll(Roll);
                                }
                                catch(IllegalArgumentException ex)
                                {
//                                    Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                                }

                                try
                                {
                                    String text = "("+orbitView.getHeading().toDecimalDegreesString(2)+","+orbitView.getPitch().toDecimalDegreesString(2)+","+orbitView.getRoll().toDecimalDegreesString(2)+","+String.format("%.1f", orbitView.getZoom())+")\n";
                                    Position pos = orbitView.getEyePosition();
                                    if(pos != null)  
                                    {
                                        text += "("+pos.getLatitude().toDecimalDegreesString(6)+","+pos.getLongitude().toDecimalDegreesString(6)+","+String.format("%.1f", pos.getAltitude())+")"+"\n";
                                    }
                                    if(MainFrame.balloon != null)   MainFrame.balloon.setText(text);
                                }
                                catch(Exception ex)
                                {
                                    
                                }
                                
                                wwd.redraw();
                                Thread.sleep(10);

                                if(bTerminate)  break;
                            } catch (IllegalArgumentException ex) {
//                                Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                            } catch (InterruptedException ex) {
                                Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                            }
//                                Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);

                            p0 = p;
                        }
                    }
                    if(bTerminate)  break;
                    prev_position = position;
                    prev_heading = heading;
                    prev_pitch = pitch;
                    prev_roll = roll;
                }

                if(bSaveVideo)
                {
                    if(videoEncoder.is_started)
                    {
                        videoEncoder.stop();
                    }
                }
            }
            finally
            {
                if(bSaveVideo)
                {
                    wwd.removeRenderingListener(renderingListener); // ensure not to add a duplicate
                }
            }
        }
    }    
    
    public void animateToLocation(PointOfInterest location)
    {
        if (location == null)
        {
            return;
        }
        BasicFlyView view = new BasicFlyView();
        wwd.setView(view);
        
        double elevation = view.getGlobe().getElevation(
            location.getLatlon().getLatitude(), location.getLatlon().getLongitude());
        FlyToFlyViewAnimator animator =
            FlyToFlyViewAnimator.createFlyToFlyViewAnimator(view,
                view.getEyePosition(),
                new Position(location.getLatlon(), elevation),
                view.getHeading(), view.getHeading(),
                view.getPitch(), view.getPitch(),
                view.getEyePosition().getElevation(), view.getEyePosition().getElevation(),
                10000, WorldWind.ABSOLUTE);
        view.addAnimator(animator);
        animator.start();
        view.firePropertyChange(AVKey.VIEW, null, view);
    }
   
    public class MyMouseMotionListener implements MouseMotionListener {
        @Override
        public void mouseDragged(MouseEvent e) {
            // consume the event so the globe position does not change
//            e.consume();
//            if (e.getSource() instanceof WorldWindowGLCanvas) {
//                // get the position of the mouse
//                final WorldWindowGLCanvas canvas = ((WorldWindowGLCanvas) e.getSource());
//                final Position p = canvas.getCurrentPosition();
//                // do something with the position here
//            }
        }
        @Override
        public void mouseMoved(MouseEvent e) {
            PickedObjectList objs = wwd.getObjectsAtCurrentPosition();
            if(objs != null)
            {
//                is_mouse_on_globe = true;
//                for(Object o:objs)
//                {
//                    if((o != null) && !(o instanceof Position))
////                    if(o != null)
//                    {
//                        set_object_cursor(o);
//                    }
//                }
                
                Object obj = objs.getTopObject();
                is_mouse_on_globe = true;
//                if((obj != null) && !(obj instanceof Position))
                if(obj != null)
                {
    //                currObject = obj;
                    set_object_cursor(obj);
                }
            }
//            e.consume();
        }
    }   

    public class MyMouseListener implements MouseListener {
        
        @Override
        public void mouseClicked(MouseEvent e) {
//            Object obj = e.getSource();
//            if (obj instanceof mv_KMLFeatureTreeNode)
//            {
//                System.out.println("mouseClicked: "+((mv_KMLFeatureTreeNode) obj).getText());
//            }
//            if (obj instanceof WorldWindowGLCanvas) {
////                // get the position of the mouse
////                final WorldWindowGLCanvas canvas = ((WorldWindowGLCanvas) e.getSource());
////                final Position p = canvas.getCurrentPosition();
////                // do something with the position here
//                e.consume();
//            }
            if(is_mouse_on_globe)
            {
                e.consume();
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {
            if(wwd.getView() != orbitView){
//                MI_OrbitView.doClick();
//                MI_OrbitViewActionPerformed(null);
                wwd.setView(orbitView);
                wwd.redraw();

                MainFrame.this.setFocusable(true);
                MainFrame.this.requestFocusInWindow();

                jPanel2.setFocusable(true);
                jPanel2.requestFocusInWindow();
                
                wwd.setFocusable(true);
                wwd.requestFocusInWindow();
            }
//            wwd.setView(orbitView);
//            wwd.redraw();
//            e.consume();// Disable globe movement on click in World Wind
        }

        @Override
        public void mouseReleased(MouseEvent e) {
//            e.consume();
        }

        @Override
        public void mouseEntered(MouseEvent e) {
//            e.consume();
        }

        @Override
        public void mouseExited(MouseEvent e) {
//            e.consume();
        }

//        @Override
//        public void mouseWheelMoved(MouseWheelEvent e){
//            e.consume();
//        }
//        
//        @Override
//        public void mouseDragged(MouseEvent e) {
//            // consume the event so the globe position does not change
//            e.consume();
//            if (e.getSource() instanceof WorldWindowGLCanvas) {
//                // get the position of the mouse
//                final WorldWindowGLCanvas canvas = ((WorldWindowGLCanvas) e.getSource());
//                final Position p = canvas.getCurrentPosition();
//                // do something with the position here
//            }
//        }
//        @Override
//        public void mouseMoved(MouseEvent e) {           
//            e.consume();
//        }
    }   

    private void addPosition() {
        if(this.positions == null)  return;
        Position curPos = this.wwd.getCurrentPosition();
        if (curPos == null) {
            return;
        }

        this.positions.add(curPos);
        if(!MI_AddPath.isEnabled())
        {
            this.polyline.setPositions(this.positions);
        }
        else
        {
            if(positions.size() >= 3)   this.polygon.setOuterBoundary(positions);
            this.polyline.setPositions(this.positions);
        }
        this.firePropertyChange("LineBuilder.AddPosition", null, curPos);
        this.wwd.redraw();
        propertiesFrame.B_Ok.setEnabled(true);
    }

    private void replacePosition() {
        if(this.positions == null)  return;
        Position curPos = this.wwd.getCurrentPosition();
        if (curPos == null) {
            return;
        }

        int index = this.positions.size() - 1;
        if (index < 0) {
            index = 0;
        }

        Position currentLastPosition = this.positions.get(index);
        this.positions.set(index, curPos);
        if(!MI_AddPath.isEnabled())
            this.polyline.setPositions(this.positions);
        else
        {
            this.polygon.setOuterBoundary(positions);
            this.polyline.setPositions(this.positions);
        }
        this.firePropertyChange("LineBuilder.ReplacePosition", currentLastPosition, curPos);
        this.wwd.redraw();
    }

    private void removePosition() {
        if(this.positions == null)  return;
        if (this.positions.isEmpty()) {
            return;
        }

        Position currentLastPosition = this.positions.get(this.positions.size() - 1);
        this.positions.remove(this.positions.size() - 1);
        if(!MI_AddPath.isEnabled())
            this.polyline.setPositions(this.positions);
        else
        {
            this.polygon.setOuterBoundary(positions);
            this.polyline.setPositions(this.positions);
        }
        this.firePropertyChange("LineBuilder.RemovePosition", currentLastPosition, null);
        this.wwd.redraw();
    }

    void load_maps()
    {
        try {
            String strMapsPath = Paths.get(strDataPath, "maps").toString();
            listZipFiles(strMapsPath);
            System.out.println("maps~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        } catch (IOException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void listZipFiles(String startDir) throws IOException
    {
        File dir = new File(startDir);
        File[] files = dir.listFiles();
//        Arrays.sort(files, new Comparator<File>(){
//            public int compare(File f1, File f2)
//            {
//                return Long.valueOf(f1.lastModified()).compareTo(f2.lastModified());
//            } });        
        if (files != null && files.length > 0)
        {
            for (File file : files)
            {
                if(file.getName().toLowerCase().startsWith("!"))    continue;
                // Check if the file is a directory
                if (file.isDirectory())
                {
                    // We will not print the directory name, just use it as a new
                    // starting point to list files from
                    listZipFiles(file.getAbsolutePath());
                }
                else
                {
                    if(file.getName().toLowerCase().endsWith(".zip"))
                    {
                        String strZipFile = startDir + "\\"+ file.getName();
                        String strExt = "."+file.getParentFile().getName();
                        System.out.println(strZipFile + " (size in bytes: " + file.length()+"), ext["+strExt+"]");
                        try
                        {
                            wwd.getModel().getLayers().add(new MyOSMZipLayer(strZipFile,strExt,"MOBAC",null,null));
                        }
                        catch(Exception ex)
                        {
                            System.out.println("map file ["+strZipFile+" invalid...");
                        }
                    }
                }
            }
        }
    }
    
    RenderableLayer symbolLayer = null;
    void create_tactical_symbols()
    {
        // Create a tactical symbol for the MIL-STD-2525 symbology set. The symbol
        // identifier specifies a MIL-STD-2525 friendly Special Operations Forces Drone
        // Aircraft. The position places the tactical symbol at 1km above mean sea level.
        MilStd2525TacticalSymbol symbol = new MilStd2525TacticalSymbol("SFAPMFQM------A", Position.fromDegrees(34.7327, -117.8347, 1000));
        symbol.setAltitudeMode(WorldWind.ABSOLUTE);

        // Create an attribute bundle and use it as the symbol's normal attributes.
        TacticalSymbolAttributes attrs = new BasicTacticalSymbolAttributes();
        attrs.setScale(0.75); // Make the symbol 75% its normal size.
        attrs.setOpacity(0.5); // Make the symbol 50% transparent.
        symbol.setAttributes(attrs);        
        
        // Create an attribute bundle and use it as the symbol's highlight attributes.
        TacticalSymbolAttributes highlightAttrs = new BasicTacticalSymbolAttributes();
        highlightAttrs.setScale(2.0); // 200% of normal size when highlighted.
        highlightAttrs.setOpacity(1.0); // 100% opaque when highlighted.
        symbol.setHighlightAttributes(highlightAttrs);
        symbol.setHighlighted(true);

        // Hide the symbol's text modifiers to reduce clutter.
        symbol.setShowTextModifiers(false);

        // Configure the symbol to display as a simple dot, and without any graphic or text
        // modifiers.
//        symbol.setShowFrameAndIcon(false);

        // Create an attribute bundle to specify the dot's diameter in screen pixels.
//        TacticalSymbolAttributes attrs = new BasicTacticalSymbolAttributes();
//        attrs.setScale(3.0); // Set the dot's diameter to 3.0 pixels.
//        symbol.setAttributes(attrs);
        
        // Create an attribute bundle and use it as the symbol's normal attributes. Specify
        // the text modifier font and material.
//        TacticalSymbolAttributes attrs = new BasicTacticalSymbolAttributes();
        attrs.setTextModifierFont(Font.decode("Tahoma-Bold-12"));
        attrs.setTextModifierMaterial(Material.RED);
        symbol.setAttributes(attrs);

        if(symbolLayer == null)
        {
            symbolLayer = new RenderableLayer();
            symbolLayer.setName(bundle.getString("L_TacticalSymbols"));
            wwd.getModel().getLayers().add(symbolLayer);
        }
        symbolLayer.addRenderable(symbol);
        
        // Create the list of key-value pairs used to specify modifiers during tactical
        // symbol construction.
        AVList modifiers = new AVListImpl();
        modifiers.setValue(SymbologyConstants.DIRECTION_OF_MOVEMENT, Angle.fromDegrees(50));
        modifiers.setValue(SymbologyConstants.ECHELON, SymbologyConstants.ECHELON_TEAM_CREW);

        // Create a ground tactical symbol for the MIL-STD-2525 symbology set. Specify the
        // ground symbol's Direction of Movement (heading) and Echelon modifiers
        // by adding each modifier as a key-value pair to the modifiers parameter.
        TacticalSymbol groundSymbol = new MilStd2525TacticalSymbol("SHGPUCFRMS----G", Position.fromDegrees(32.4, 63.4, 0), modifiers);
        groundSymbol.setAltitudeMode(WorldWind.CLAMP_TO_GROUND);
        symbolLayer.addRenderable(groundSymbol);
        
        // Set the ground symbol's Direction of Movement (heading) and Echelon modifiers.
        groundSymbol.setModifier(SymbologyConstants.DIRECTION_OF_MOVEMENT, Angle.fromDegrees(50));
        groundSymbol.setModifier(SymbologyConstants.ECHELON, SymbologyConstants.ECHELON_TEAM_CREW);

        // Remove the ground symbol's Direction of Movement (heading), Echelon, and Location
        // modifiers.
        groundSymbol.setModifier(SymbologyConstants.DIRECTION_OF_MOVEMENT, null);
        groundSymbol.setModifier(SymbologyConstants.ECHELON, null);
        groundSymbol.setShowLocation(false);

        wwd.redraw();
    }
    
    void create_balloons_layer()
    {
        // Add a controller to send input events to BrowserBalloons.
        hotSpotController = new myHotSpotController(wwd);
        // Add a controller to handle link and navigation events in BrowserBalloons.
        balloonController = new myBalloonController(wwd);

        // Create a layer to display the balloons.
        ballonsLayer = new RenderableLayer();
        ballonsLayer.setName("Balloons");
        ballonsLayer.setEnabled(false);
//        insertBeforePlacenames(wwd, ballonsLayer);
        wwd.getModel().getLayers().add(ballonsLayer);

        // Add an AnnotationBalloon and a BrowserBalloon to the balloon layer.
        this.makeAnnotationBalloon();
        this.makeBrowserBalloon();
    }
    
//    myHotSpotController hotSpotController;
//    myBalloonController balloonController;
    RenderableLayer ballonsLayer;
    static public ScreenAnnotationBalloon balloon;
    void makeAnnotationBalloon() {
//        Balloon balloon = new ScreenAnnotationBalloon("<b>AnnotationBalloon</b> attached to the screen", new Point(50, 300));
        balloon = new ScreenAnnotationBalloon("رسالة نصية على الشاشة", new Point(400, 70));

        BalloonAttributes attrs = new BasicBalloonAttributes();
        // Size the balloon to fit its text, place its lower-left corner at the point, put event padding between the
        // balloon's text and its sides, and disable the balloon's leader.
        attrs.setSize(Size.fromPixels(300, 60));
        attrs.setOffset(new Offset(0d, 0d, AVKey.PIXELS, AVKey.PIXELS));
        attrs.setInsets(new Insets(10, 10, 10, 10)); // .
        attrs.setLeaderShape(AVKey.SHAPE_NONE);
        // Configure the balloon's colors to display white text over a semi-transparent black background.
        attrs.setTextColor(Color.WHITE);
        attrs.setFont(new Font("Tahoma", Font.BOLD, 14));
        attrs.setInteriorMaterial(Material.BLACK);
        attrs.setInteriorOpacity(0.6);
        attrs.setOutlineMaterial(Material.WHITE);
        balloon.setAttributes(attrs);
        
        DrawContext dc = wwd.getSceneController().getDrawContext();
        if(dc.getView() != null)
        {
            Rectangle rect2 = balloon.getBounds(dc);
            int x,y;
            Rectangle rect1 = wwd.getBounds();
            x = (rect2.width - rect1.width)/2;
            y = (rect2.height - rect1.height)/2;
            balloon.setScreenLocation(new Point(x,y));
        }

        ballonsLayer.addRenderable(balloon);
    }

    protected void makeBrowserBalloon() {
        String htmlString = null;
        InputStream contentStream = null;
        String BROWSER_BALLOON_CONTENT_PATH = Paths.get(strDataPath, "kml", "BrowserBalloonExample.html").toString();

        try {
            // Read the URL content into a String using the default encoding (UTF-8).
            contentStream = WWIO.openFileOrResourceStream(BROWSER_BALLOON_CONTENT_PATH, this.getClass());
            htmlString = WWIO.readStreamToString(contentStream, null);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            WWIO.closeStream(contentStream, BROWSER_BALLOON_CONTENT_PATH);
        }

        if (htmlString == null) {
            htmlString = Logging.getMessage("generic.ExceptionAttemptingToReadFile", BROWSER_BALLOON_CONTENT_PATH);
        }

        Position balloonPosition = Position.fromDegrees(38.883056, -77.016389);

        // Create a Browser Balloon attached to the globe, and pointing at the NASA headquarters in Washington, D.C.
        // We use the balloon page's URL as its resource resolver to handle relative paths in the page content.
        AbstractBrowserBalloon balloon = new GlobeBrowserBalloon(htmlString, balloonPosition);
        // Size the balloon to provide enough space for its content.
        BalloonAttributes attrs = new BasicBalloonAttributes();
        attrs.setSize(new Size(Size.NATIVE_DIMENSION, 0d, null, Size.NATIVE_DIMENSION, 0d, null));
        balloon.setAttributes(attrs);

        // Create a placemark on the globe that the user can click to open the balloon.
        PointPlacemark placemark = new PointPlacemark(balloonPosition);
        placemark.setLabelText("Click to open balloon");
        // Associate the balloon with the placemark by setting AVKey.BALLOON. The BalloonController looks for this
        // value when an object is clicked.
        placemark.setValue(AVKey.BALLOON, balloon);

        ballonsLayer.addRenderable(balloon);
        ballonsLayer.addRenderable(placemark);
        
        // update sector
        Sector sector = Sector.fromDegrees(placemark.getPosition().getLatitude().getDegrees(),placemark.getPosition().getLatitude().getDegrees(),placemark.getPosition().getLongitude().getDegrees(),placemark.getPosition().getLongitude().getDegrees());
        ballonsLayer.setValue("oghab.mapviewer.avKey.Sector", sector);
    }
    
//    public static class ColladaModelThread extends Thread {
//
//        // Indicates the source of the COLLADA file loaded by this thread. Initialized during construction.
//        protected Object colladaSource;
//
//        // Geographic position of the COLLADA model.
//        protected Position position;
//
//        // Indicates the <code>AppFrame</code> the COLLADA file content is displayed in. Initialized during
//        // construction.
//        protected MainFrame appFrame;
//
//        /**
//         * Creates a new worker thread from a specified <code>colladaSource</code> and <code>appFrame</code>.
//         *
//         * @param colladaSource the source of the COLLADA file to load. May be a {@link java.io.File}, a {@link
//         *                      java.net.URL}, or an {@link java.io.InputStream}, or a {@link String} identifying a file path or URL.
//         * @param position the geographic position of the COLLADA model.
//         * @param appFrame the <code>AppFrame</code> in which to display the COLLADA source.
//         */
//        public ColladaModelThread(Object colladaSource, Position position, MainFrame appFrame) {
//            this.colladaSource = colladaSource;
//            this.position = position;
//            this.appFrame = appFrame;
//        }
//
//        /**
//         * Loads this worker thread's COLLADA source into a new
//         * <code>{@link gov.nasa.worldwind.ogc.collada.ColladaRoot}</code>, then adds the new <code>ColladaRoot</code>
//         * to this worker thread's <code>AppFrame</code>.
//         */
//        @Override
//        public void run() {
//            try {
//                ColladaRoot colladaRoot = ColladaRoot.createAndParse(this.colladaSource);
//                colladaRoot.setPosition(this.position);
//                colladaRoot.setAltitudeMode(WorldWind.RELATIVE_TO_GROUND);
//
//                // Schedule a task on the EDT to add the parsed document to a layer
//                SwingUtilities.invokeLater(() -> {
//                    appFrame.addColladaLayer(colladaRoot);
//                });
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }
    
    ColladaController load_collada_model(String strFilename)
    {
        ColladaController colladaController = null;
        try {
            final Position position = Position.fromDegrees(33.513805, 36.376518);
            final File ColladaFile = new File(strFilename);

            ColladaRoot colladaRoot = ColladaRoot.createAndParse(ColladaFile);
            colladaRoot.setAltitudeMode(WorldWind.RELATIVE_TO_GROUND);
            colladaRoot.setPosition(position);

            // Create a ColladaController to adapt the ColladaRoot to the WorldWind renderable interface.
            colladaController = new ColladaController(colladaRoot);

            // Adds a new layer containing the ColladaRoot to the end of the WorldWindow's layer list.
            RenderableLayer layer = new RenderableLayer();
            layer.setName("Collada Layer");
            layer.addRenderable(colladaController);
            this.wwd.getModel().getLayers().add(layer);

            // update sector
            Sector sector = Sector.fromDegrees(colladaRoot.getPosition().getLatitude().getDegrees(),colladaRoot.getPosition().getLatitude().getDegrees(),colladaRoot.getPosition().getLongitude().getDegrees(),colladaRoot.getPosition().getLongitude().getDegrees());
            layer.setValue("oghab.mapviewer.avKey.Sector", sector);
        } catch (IOException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        } catch (XMLStreamException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
        return colladaController;
    }
    
    void create_collada_layer(String strFilename)
    {
        final Position MackyAuditoriumPosition = Position.fromDegrees(33.513805, 36.376518);
        final File ColladaFile = new File(strFilename);

        // Invoke the <code>Thread</code> to load the COLLADA model asynchronously.
        new ColladaWorkerThread(ColladaFile, MackyAuditoriumPosition, this).start();
    }
    
    public static class ColladaWorkerThread extends Thread {

        // Indicates the source of the COLLADA file loaded by this thread. Initialized during construction.
        protected Object colladaSource;

        // Geographic position of the COLLADA model.
        protected Position position;

        // Indicates the <code>AppFrame</code> the COLLADA file content is displayed in. Initialized during
        // construction.
        protected MainFrame appFrame;

        /**
         * Creates a new worker thread from a specified <code>colladaSource</code> and <code>appFrame</code>.
         *
         * @param colladaSource the source of the COLLADA file to load. May be a {@link java.io.File}, a {@link
         *                      java.net.URL}, or an {@link java.io.InputStream}, or a {@link String} identifying a file path or URL.
         * @param position the geographic position of the COLLADA model.
         * @param appFrame the <code>AppFrame</code> in which to display the COLLADA source.
         */
        public ColladaWorkerThread(Object colladaSource, Position position, MainFrame appFrame) {
            this.colladaSource = colladaSource;
            this.position = position;
            this.appFrame = appFrame;
        }

        /**
         * Loads this worker thread's COLLADA source into a new
         * <code>{@link gov.nasa.worldwind.ogc.collada.ColladaRoot}</code>, then adds the new <code>ColladaRoot</code>
         * to this worker thread's <code>AppFrame</code>.
         */
        @Override
        public void run() {
            try {
                ColladaRoot colladaRoot = ColladaRoot.createAndParse(this.colladaSource);
                colladaRoot.setAltitudeMode(WorldWind.RELATIVE_TO_GROUND);
//                colladaRoot.setAltitudeMode(WorldWind.CLAMP_TO_GROUND);
                colladaRoot.setPosition(this.position);

                // Schedule a task on the EDT to add the parsed document to a layer
                SwingUtilities.invokeLater(() -> {
                    appFrame.addColladaLayer(colladaRoot);
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    protected void addColladaLayer(ColladaRoot colladaRoot) {
        // Create a ColladaController to adapt the ColladaRoot to the WorldWind renderable interface.
        ColladaController colladaController = new ColladaController(colladaRoot);

        // Adds a new layer containing the ColladaRoot to the end of the WorldWindow's layer list.
        RenderableLayer layer = new RenderableLayer();
        layer.setName("Collada Layer");
        layer.addRenderable(colladaController);
        this.wwd.getModel().getLayers().add(layer);
        
        // update sector
        Sector sector = Sector.fromDegrees(colladaRoot.getPosition().getLatitude().getDegrees(),colladaRoot.getPosition().getLatitude().getDegrees(),colladaRoot.getPosition().getLongitude().getDegrees(),colladaRoot.getPosition().getLongitude().getDegrees());
        layer.setValue("oghab.mapviewer.avKey.Sector", sector);
    }
    
    protected static class ContextMenuController implements SelectListener {

        protected PointPlacemark lastPickedPlacemark = null;

        @Override
        public void selected(SelectEvent event) {
            try {
                if (event.getEventAction().equals(SelectEvent.ROLLOVER)) {
                    highlight(event, event.getTopObject());
                } else if (event.getEventAction().equals(SelectEvent.RIGHT_PRESS)) // Could do RIGHT_CLICK instead
                {
                    showContextMenu(event);
                }
            } catch (Exception e) {
                Util.getLogger().warning(e.getMessage() != null ? e.getMessage() : e.toString());
            }
        }

        @SuppressWarnings({"UnusedDeclaration"})
        protected void highlight(SelectEvent event, Object o) {
            if (this.lastPickedPlacemark == o) {
                return; // same thing selected
            }
            // Turn off highlight if on.
            if (this.lastPickedPlacemark != null) {
                this.lastPickedPlacemark.setHighlighted(false);
                this.lastPickedPlacemark = null;
            }

            // Turn on highlight if object selected.
            if (o != null && o instanceof PointPlacemark) {
                this.lastPickedPlacemark = (PointPlacemark) o;
                this.lastPickedPlacemark.setHighlighted(true);
            }
        }

        protected void showContextMenu(SelectEvent event) {
            if (!(event.getTopObject() instanceof PointPlacemark)) {
                return;
            }

            // See if the top picked object has context-menu info defined. Show the menu if it does.
            Object o = event.getTopObject();
            if (o instanceof AVList) // Uses an AVList in order to be applicable to all shapes.
            {
                AVList params = (AVList) o;
                ContextMenuInfo menuInfo = (ContextMenuInfo) params.getValue(ContextMenu.CONTEXT_MENU_INFO);
                if (menuInfo == null) {
                    return;
                }

                if (!(event.getSource() instanceof Component)) {
                    return;
                }

                ContextMenu menu = new ContextMenu((Component) event.getSource(), menuInfo);
                menu.show(event.getMouseEvent());
            }
        }
    }

    /**
     * The ContextMenu class implements the context menu.
     */
    protected static class ContextMenu {

        public static final String CONTEXT_MENU_INFO = "ContextMenuInfo";

        protected ContextMenuInfo ctxMenuInfo;
        protected Component sourceComponent;
        protected JMenuItem menuTitleItem;
        protected ArrayList<JMenuItem> menuItems = new ArrayList<>();

        public ContextMenu(Component sourceComponent, ContextMenuInfo contextMenuInfo) {
            this.sourceComponent = sourceComponent;
            this.ctxMenuInfo = contextMenuInfo;

            this.makeMenuTitle();
            this.makeMenuItems();
        }

        protected void makeMenuTitle() {
            this.menuTitleItem = new JMenuItem(this.ctxMenuInfo.menuTitle);
        }

        protected void makeMenuItems() {
            for (ContextMenuItemInfo itemInfo : this.ctxMenuInfo.menuItems) {
                this.menuItems.add(new JMenuItem(new ContextMenuItemAction(itemInfo)));
            }
        }

        public void show(final MouseEvent event) {
            JPopupMenu popup = new JPopupMenu();

            popup.add(this.menuTitleItem);

            popup.addSeparator();

            for (JMenuItem subMenu : this.menuItems) {
                popup.add(subMenu);
            }

            popup.show(sourceComponent, event.getX(), event.getY());
//            System.out.println("AliSoft:popup: "+event.getX()+", "+event.getY());
        }
    }

    /**
     * The ContextMenuInfo class specifies the contents of the context menu.
     */
    protected static class ContextMenuInfo {

        protected String menuTitle;
        protected ContextMenuItemInfo[] menuItems;

        public ContextMenuInfo(String title, ContextMenuItemInfo[] menuItems) {
            this.menuTitle = title;
            this.menuItems = menuItems;
        }
    }

    /**
     * The ContextMenuItemInfo class specifies the contents of one entry in the context menu.
     */
    protected static class ContextMenuItemInfo {

        protected String displayString;
        protected int id;

        public ContextMenuItemInfo(String displayString,int id) {
            this.displayString = displayString;
            this.id = id;
        }
    }

    /**
     * The ContextMenuItemAction responds to user selection of a context menu item.
     */
    public static class ContextMenuItemAction extends AbstractAction {

        protected ContextMenuItemInfo itemInfo;

        public ContextMenuItemAction(ContextMenuItemInfo itemInfo) {
            super(itemInfo.displayString);

            this.itemInfo = itemInfo;
        }

        @Override
        public void actionPerformed(ActionEvent event) {
            System.out.println(this.itemInfo.displayString); // Replace with application's menu-item response.
            System.out.println(this.itemInfo.id); // Replace with application's menu-item response.
            switch(this.itemInfo.id)
            {
                case 1:
                {
//                    lib_mv_plugin_radar.plugin_execute();
                    break;
                }
                case 2:
                {
//                    lib_mv_plugin_radar.plugin_settings();
                    break;
                }
            }

//        Thread thread = new Thread(new Runnable()
//        {
//            public void run()
//            {
//                SwingUtilities.invokeLater(() -> {
//                    lib_mv_plugin_radar.plugin_execute();
//                });
//            }
//        });
//        thread.start();



//            Runnable task = () -> {
//                try
//                {
//                    SwingUtilities.invokeAndWait(new Runnable()
//                    {
//                        public void run()
//                        {
//                            lib_mv_plugin_radar.plugin_execute();
//                        }
//                    });        
//                }catch(Exception e)
//                {
//                    System.err.println("createGUI didn't successfully complete");
//                } 
//            };
        }
    }
    
    void create_surface_texts()
    {
        RenderableLayer layerSurfaceText = new RenderableLayer();
        layerSurfaceText.setName("Surface Texts");
        SurfaceText surfaceText = new SurfaceText("ساحة الأمويين", Position.fromDegrees(33.513805, 36.276518, 1e4));
        surfaceText.setColor(Color.red);
        surfaceText.setTextSize(50);// in meters
        layerSurfaceText.addRenderable(surfaceText);
        wwd.getModel().getLayers().add(layerSurfaceText);
        
        // update sector
        Sector sector = Sector.fromDegrees(surfaceText.getPosition().getLatitude().getDegrees(),surfaceText.getPosition().getLatitude().getDegrees(),surfaceText.getPosition().getLongitude().getDegrees(),surfaceText.getPosition().getLongitude().getDegrees());
        layerSurfaceText.setValue("oghab.mapviewer.avKey.Sector", sector);
    }
    
    RenderableLayer layer_surface_shapes = null;
    void create_surface_shapes()
    {
        layer_surface_shapes = new RenderableLayer();
        layer_surface_shapes.setName("Surface Shapes");

        // Surface polygon
        BasicShapeAttributes attrsSurfacePolygon = new BasicShapeAttributes();
        attrsSurfacePolygon.setOutlineMaterial(Material.WHITE);
        attrsSurfacePolygon.setInteriorOpacity(0.5);
        attrsSurfacePolygon.setOutlineOpacity(0.8);
        attrsSurfacePolygon.setOutlineWidth(3);
//        attrsSurfacePolygon.setImageSource(Paths.get(strIconsPath, "Splash.png").toString());
        attrsSurfacePolygon.setImageScale(1.0);

        double originLat = 35.0;
        double originLon = 33.0;
        Iterable<LatLon> locations = Arrays.asList(
            LatLon.fromDegrees(originLat + 1.0, originLon + 0.5),
            LatLon.fromDegrees(originLat + 1.0, originLon - 0.5),
            LatLon.fromDegrees(originLat + 0.5, originLon - 1.0),
            LatLon.fromDegrees(originLat - 0.5, originLon - 1.0),
            LatLon.fromDegrees(originLat - 1.0, originLon - 0.5),
            LatLon.fromDegrees(originLat - 1.0, originLon + 0.5),
            LatLon.fromDegrees(originLat - 0.5, originLon + 1.0),
            LatLon.fromDegrees(originLat + 0.5, originLon + 1.0),
            LatLon.fromDegrees(originLat + 1.0, originLon + 0.5));
        SurfaceShape shapeSurfaceShape = new SurfacePolygon(locations);
        shapeSurfaceShape.setAttributes(attrsSurfacePolygon);
        shapeSurfaceShape.setValue(AVKey.DISPLAY_NAME, "Surface Shape");
        shapeSurfaceShape.setValue(AVKey.SHORT_DESCRIPTION, "Short description of Surface Shape");
        shapeSurfaceShape.setValue(AVKey.BALLOON_TEXT, "This is a Surface Shape.");
        layer_surface_shapes.addRenderable(shapeSurfaceShape);
        
        // Surface circle over the center of the United states.
        BasicShapeAttributes attrsSurfaceCircle = new BasicShapeAttributes();
        attrsSurfaceCircle.setInteriorMaterial(Material.GREEN);
        attrsSurfaceCircle.setOutlineMaterial(new Material(WWUtil.makeColorBrighter(Color.GREEN)));
        attrsSurfaceCircle.setInteriorOpacity(0.5);
        attrsSurfaceCircle.setOutlineOpacity(0.8);
        attrsSurfaceCircle.setOutlineWidth(3);

        // Surface Circle (not exportable)
//        SurfaceCircle shapeSurfaceCircle = new SurfaceCircle(LatLon.fromDegrees(33.513805, 36.276518), 500);
//        shapeSurfaceCircle.setAttributes(attrsSurfaceCircle);
//        shapeSurfaceCircle.setValue(AVKey.DISPLAY_NAME, "Surface Sector");
//        shapeSurfaceCircle.setValue(AVKey.SHORT_DESCRIPTION, "Short description of Surface Sector");
//        shapeSurfaceCircle.setValue(AVKey.BALLOON_TEXT, "This is a Surface Sector.");
//        layer_surface_shapes.addRenderable(shapeSurfaceCircle);

        // Surface Ellipse (not exportable)
//        SurfaceEllipse shapeSurfaceEllipse = new SurfaceEllipse(LatLon.fromDegrees(33.513805, 36.276518), 500, 250);
//        shapeSurfaceEllipse.setAttributes(attrsSurfaceCircle);
//        shapeSurfaceEllipse.setValue(AVKey.DISPLAY_NAME, "Surface Sector");
//        shapeSurfaceEllipse.setValue(AVKey.SHORT_DESCRIPTION, "Short description of Surface Sector");
//        shapeSurfaceEllipse.setValue(AVKey.BALLOON_TEXT, "This is a Surface Sector.");
//        layer_surface_shapes.addRenderable(shapeSurfaceEllipse);
        
        // Surface Sector
        SurfaceSector surface_sector = new SurfaceSector(Sector.fromDegrees(60, 80, -90, -70));
        surface_sector.setAttributes(normalShapeAttributes0);
        surface_sector.setHighlightAttributes(highlightShapeAttributes0);
        surface_sector.setValue(AVKey.DISPLAY_NAME, "Surface Sector");
        surface_sector.setValue(AVKey.SHORT_DESCRIPTION, "Short description of Surface Sector");
        surface_sector.setValue(AVKey.BALLOON_TEXT, "This is a Surface Sector.");
        layer_surface_shapes.addRenderable(surface_sector);

        // Surface Polyline
        SurfacePolyline surface_polyline = new SurfacePolyline();

        List<LatLon> positions = Arrays.asList(
            LatLon.fromDegrees(37.83, -122.37),
            LatLon.fromDegrees(37.82, -122.36),
            LatLon.fromDegrees(37.82, -122.37));

        surface_polyline.setLocations(positions);

        surface_polyline.setAttributes(normalShapeAttributes0);
        surface_polyline.setHighlightAttributes(highlightShapeAttributes0);

        surface_polyline.setValue(AVKey.DISPLAY_NAME, "Surface Polyline");
        surface_polyline.setValue(AVKey.SHORT_DESCRIPTION, "Short description of Surface Polyline");
        surface_polyline.setValue(AVKey.BALLOON_TEXT, "This is a Surface Polyline.");
        layer_surface_shapes.addRenderable(surface_polyline);

        // Surface Quad
        SurfaceQuad surfaceQuad = new SurfaceQuad(LatLon.fromDegrees(24, 32), 6d, 5d, Angle.POS90);
        surfaceQuad.setValue(AVKey.DISPLAY_NAME, "Surface Quad");
        surfaceQuad.setValue(AVKey.SHORT_DESCRIPTION, "Short description of Surface Quad");
        surfaceQuad.setValue(AVKey.BALLOON_TEXT, "This is a Surface Quad.");
        layer_surface_shapes.addRenderable(surfaceQuad);
        
        // Surface Square
        LatLon position1 = new LatLon(Angle.fromDegrees(38), Angle.fromDegrees(-105));
        SurfaceSquare surfaceSquare = new SurfaceSquare(position1, 100e3);
        surfaceSquare.setValue(AVKey.DISPLAY_NAME, "Surface Square");
        surfaceSquare.setValue(AVKey.SHORT_DESCRIPTION, "Short description of Surface Square");
        surfaceSquare.setValue(AVKey.BALLOON_TEXT, "This is a Surface Square.");
        layer_surface_shapes.addRenderable(surfaceSquare);
        
        wwd.getModel().getLayers().add(layer_surface_shapes);
        
        // update sector
	ExtentHolder extentHolder = (ExtentHolder)shapeSurfaceShape;
	if(extentHolder != null)
	{
            Extent extent = extentHolder.getExtent(wwd.getModel().getGlobe(), 0);
            Position position = wwd.getModel().getGlobe().computePositionFromPoint(extent.getCenter());
            Sector sector = Sector.fromDegrees(position.getLatitude().getDegrees(),position.getLatitude().getDegrees(),position.getLongitude().getDegrees(),position.getLongitude().getDegrees());
            layer_surface_shapes.setValue("oghab.mapviewer.avKey.Sector", sector);
	}
    }
    
    void add_folder(String name)
    {
        KMLFolder kmlFolder = createKMLFolder(name);
        KMLController kmlController = new KMLController(kmlFolder.getRoot());
        RenderableLayer kmlLayer = new RenderableLayer();
        kmlLayer.setName(name);
        kmlLayer.addRenderable(kmlController);
        wwd.getModel().getLayers().add(kmlLayer);
//        Sector sector = Sector.fromDegrees(p.getLatitude().getDegrees(),p.getLatitude().getDegrees(),p.getLongitude().getDegrees(),p.getLongitude().getDegrees());
//        kmlLayer.setValue("oghab.mapviewer.avKey.Sector", sector);
        wwd.redrawNow();
        
        LayerTreeNode layerNode = new LayerTreeNode(kmlLayer);
        TreeNode parent = get_current_folder();
//        TreeNode parent = treeLayout.getCurrTreeNode();
        if(parent == null)
            layerTree.getModel().addLayer(layerNode);
        else
            layerTree.getModel().addLayer(parent, layerNode);
        wwd.redrawNow();
    }
    
    void set_value_renderable(Object obj, String name, Object value)
    {
        if(obj instanceof WWObjectImpl)
        {
            WWObjectImpl feature = (WWObjectImpl)obj;
            feature.setValue(name, value);
            return;
        }
        
//        Object obj6 = obj;
//        if(obj6 instanceof gov.nasa.worldwind.render.PointPlacemark)
//        {
//            gov.nasa.worldwind.render.PointPlacemark obj7 = (gov.nasa.worldwind.render.PointPlacemark)obj6;
//            if(Settings.bDebug)  System.out.println("obj7: "+obj7+", "+obj7.getLabelText());
//
//            obj7.setValue(name, value);
//            return;
//        }
//
//        if(obj6 instanceof gov.nasa.worldwind.render.Path)
//        {
//            gov.nasa.worldwind.render.Path obj7 = (gov.nasa.worldwind.render.Path)obj6;
//            if(Settings.bDebug)  System.out.println("obj7: "+obj7+", "+obj7.getStringValue(AVKey.DISPLAY_NAME));
//
//            goTo(wwd,obj7.getSector());
//            wwd.redraw();
//            return;
//        }
//
//        if(obj6 instanceof gov.nasa.worldwind.render.Polygon)
//        {
//            gov.nasa.worldwind.render.Polygon obj7 = (gov.nasa.worldwind.render.Polygon)obj6;
//            if(Settings.bDebug)  System.out.println("obj7: "+obj7+", "+obj7.getStringValue(AVKey.DISPLAY_NAME));
//
//            goTo(wwd,obj7.getSector());
//            wwd.redraw();
//            return;
//        }
//
////            if(obj6 instanceof KMLPointPlacemarkImpl)
////            {
////                KMLPointPlacemarkImpl obj7 = (KMLPointPlacemarkImpl)obj6;
////                if(Settings.bDebug)  System.out.println("obj7: "+obj7+", "+obj7.getLabelText());
////
////                goTo(wwd,obj7.getPosition());
////                wwd.redraw();
////                return;
////            }
//
////            if(obj6 instanceof KMLLineStringPlacemarkImpl)
////            {
////                KMLLineStringPlacemarkImpl obj7 = (KMLLineStringPlacemarkImpl)obj6;
////                if(Settings.bDebug)  System.out.println("obj7: "+obj7);
////
////                goTo(wwd,obj7.getSector());
////                wwd.redraw();
////                return;
////            }
//
//        if(obj6 instanceof KMLGroundOverlayPolygonImpl)
//        {
//            KMLGroundOverlayPolygonImpl obj7 = (KMLGroundOverlayPolygonImpl)obj6;
//            if(Settings.bDebug)  System.out.println("obj7: "+obj7);
//
//            goTo(wwd,obj7.getSector());
//            wwd.redraw();
//            return;
//        }
//
//        if(obj6 instanceof KMLPolygonImpl)
//        {
//            KMLPolygonImpl obj7 = (KMLPolygonImpl)obj6;
//            if(Settings.bDebug)  System.out.println("obj7: "+obj7);
//
//            goTo(wwd,obj7.getSector());
//            wwd.redraw();
//            return;
//        }
//
//        if(obj6 instanceof KMLSurfaceImageImpl)
//        {
//            KMLSurfaceImageImpl obj7 = (KMLSurfaceImageImpl)obj6;
//            if(Settings.bDebug)  System.out.println("obj7: "+obj7);
//
////                                        obj7.setVisible(entry.checked);
////                                        if(bZoom)   goTo(wwd,obj7.getSector());
//            wwd.redraw();
//            return;
//        }
//
//        if(obj6 instanceof KMLSurfacePolygonImpl)
//        {
//            KMLSurfacePolygonImpl obj7 = (KMLSurfacePolygonImpl)obj6;
//            if(Settings.bDebug)  System.out.println("obj7: "+obj7);
//
//            goTo(wwd,obj7.getReferencePosition());
//            wwd.redraw();
//            return;
//        }
//
//        if(obj6 instanceof KMLExtrudedPolygonImpl)
//        {
//            KMLExtrudedPolygonImpl obj7 = (KMLExtrudedPolygonImpl)obj6;
//            if(Settings.bDebug)  System.out.println("obj7: "+obj7);
//
//            goTo(wwd,obj7.getSector());
//            wwd.redraw();
//            return;
//        }
//
//        if(obj6 instanceof KMLGroundOverlay)
//        {
//            KMLGroundOverlay obj7 = (KMLGroundOverlay)obj6;
//            if(Settings.bDebug)  System.out.println("obj7: "+obj7);
//
////                                        obj7.setVisible(entry.checked);
////                                        if(bZoom)   goTo(wwd,obj7.getSector());
//            wwd.redraw();
//            return;
//        }
//            
//        if(obj6 instanceof mv_KMLFeatureTreeNode)
//        {
//            mv_KMLFeatureTreeNode obj7 = (mv_KMLFeatureTreeNode)obj6;
//            if(Settings.bDebug)  System.out.println("obj7: "+obj7+", "+obj7.getText());
//
//            Object obj8 = obj7.getValue(AVKey.CONTEXT);
//            goto_object(obj8);
//            return;
//        }
    }
    
    TreeNode get_current_folder()
    {
        TreeNode parent = treeLayout.getCurrTreeNode();
        if((parent != null) && parent.isLeaf())
        {
            while(parent != layerTree.getModel().getRoot())
            {
                parent = parent.getParent();
                if(!parent.isLeaf()) break;
            }
        }
        return parent;
    }
    
//    final RenderableLayer layer_placemarks = new RenderableLayer();
    boolean is_first_placemark = true;
    void add_placemark(Position p,String name)
    {
        KMLPoint kmlPoint = createKMLPoint(p, name);
        KMLController kmlController = new KMLController(kmlPoint.getRoot());
        kmlDefaultLayer.addRenderable(kmlController);
        wwd.redrawNow();

        KMLAbstractFeature feature = kmlController.getKmlRoot().getFeature();
        KMLFeatureTreeNode treeNode = KMLFeatureTreeNode.fromKMLFeature(feature);
        TreeNode parent = get_current_folder();
        if(parent == null)
            layerTree.getModel().getRoot().addChild(treeNode);
        else
            parent.addChild(treeNode);

        if(feature instanceof KMLPlacemark)// created from file
        {
            KMLPlacemark obj5 = (KMLPlacemark)feature;
            if(obj5.getRenderables() != null)
            {
                for(int i=0;i<obj5.getRenderables().size();i++)
                {
                    KMLRenderable obj6 = (KMLRenderable)obj5.getRenderables().get(i);
                    if(Settings.bDebug)  System.out.println("obj6: "+obj6+", "+obj6.getClass());
                    set_value_renderable(obj6, "oghab.mapviewer.avKey.tree_node", treeNode);
                }
            }
        }
        else// created online
        {
            set_value_renderable(feature, "oghab.mapviewer.avKey.tree_node", treeNode);
        }
        wwd.redrawNow();
    }
    
    void add_tactical_symbol(Position p,String name,String id)
    {
        KMLPoint kmlPoint = createKMLTacticalSymbol(p, name, id);
        KMLController kmlController = new KMLController(kmlPoint.getRoot());
        kmlDefaultLayer.addRenderable(kmlController);
        wwd.redrawNow();

        KMLAbstractFeature feature = kmlController.getKmlRoot().getFeature();
        KMLFeatureTreeNode treeNode = KMLFeatureTreeNode.fromKMLFeature(feature);
        TreeNode parent = get_current_folder();
        if(parent == null)
            layerTree.getModel().getRoot().addChild(treeNode);
        else
            parent.addChild(treeNode);

        if(feature instanceof KMLPlacemark)// created from file
        {
            KMLPlacemark obj5 = (KMLPlacemark)feature;
            if(obj5.getRenderables() != null)
            {
                for(int i=0;i<obj5.getRenderables().size();i++)
                {
                    KMLRenderable obj6 = (KMLRenderable)obj5.getRenderables().get(i);
                    if(Settings.bDebug)  System.out.println("obj6: "+obj6+", "+obj6.getClass());
                    set_value_renderable(obj6, "oghab.mapviewer.avKey.tree_node", treeNode);
                }
            }
        }
        else// created online
        {
            set_value_renderable(feature, "oghab.mapviewer.avKey.tree_node", treeNode);
        }
        wwd.redrawNow();
    }
    
//    boolean is_first_path = true;
    void add_path(gov.nasa.worldwind.render.Path path,String name)
    {
        KMLLineString kmlPath = createKMLPath(path, name);
        KMLController kmlController = new KMLController(kmlPath.getRoot());
        kmlDefaultLayer.addRenderable(kmlController);
        wwd.redrawNow();

        KMLAbstractFeature feature = kmlController.getKmlRoot().getFeature();
        KMLFeatureTreeNode treeNode = KMLFeatureTreeNode.fromKMLFeature(feature);
        TreeNode parent = get_current_folder();
        if(parent == null)
            layerTree.getModel().getRoot().addChild(treeNode);
        else
            parent.addChild(treeNode);

        if(feature instanceof KMLPlacemark)// created from file
        {
            KMLPlacemark obj5 = (KMLPlacemark)feature;
            if(obj5.getRenderables() != null)
            {
                for(int i=0;i<obj5.getRenderables().size();i++)
                {
                    KMLRenderable obj6 = (KMLRenderable)obj5.getRenderables().get(i);
                    if(Settings.bDebug)  System.out.println("obj6: "+obj6+", "+obj6.getClass());
                    set_value_renderable(obj6, "oghab.mapviewer.avKey.tree_node", treeNode);
                }
            }
        }
        else// created online
        {
            set_value_renderable(feature, "oghab.mapviewer.avKey.tree_node", treeNode);
        }
        wwd.redrawNow();
    }
    
//    boolean is_first_polygon = true;
    void add_polygon(gov.nasa.worldwind.render.SurfacePolygon polygon,String name)
    {
        if(polygon.getOuterBoundary() == null)  return;
        if(polygon.getOuterBoundary().iterator() == null) return;
        if(!polygon.getOuterBoundary().iterator().hasNext()) return;
        KMLPolygon kmlPolygon = createKMLPolygon(polygon, name);
        KMLController kmlController = new KMLController(kmlPolygon.getRoot());
        kmlDefaultLayer.addRenderable(kmlController);
        wwd.redrawNow();

        KMLAbstractFeature feature = kmlController.getKmlRoot().getFeature();
        KMLFeatureTreeNode treeNode = KMLFeatureTreeNode.fromKMLFeature(feature);
        TreeNode parent = get_current_folder();
        if(parent == null)
            layerTree.getModel().getRoot().addChild(treeNode);
        else
            parent.addChild(treeNode);

        if(feature instanceof KMLPlacemark)// created from file
        {
            KMLPlacemark obj5 = (KMLPlacemark)feature;
            if(obj5.getRenderables() != null)
            {
                for(int i=0;i<obj5.getRenderables().size();i++)
                {
                    KMLRenderable obj6 = (KMLRenderable)obj5.getRenderables().get(i);
                    if(Settings.bDebug)  System.out.println("obj6: "+obj6+", "+obj6.getClass());
                    set_value_renderable(obj6, "oghab.mapviewer.avKey.tree_node", treeNode);
                }
            }
        }
        else// created online
        {
            set_value_renderable(feature, "oghab.mapviewer.avKey.tree_node", treeNode);
        }
        wwd.redrawNow();
    }
    
//    void create_placemarks_layer() throws IOException
//    {
//        layer_placemarks.setName("Placemarks Layer");
////        layer.setPickEnabled(false);
//
//        // Add the layer to the model.
//        wwd.getModel().getLayers().add(layer_placemarks);
//        
////        add_placemark(Position.fromDegrees(33.513805, 36.276518, 1e4),"نقطة علام جديدة");
////        add_placemark(Position.fromDegrees(33.613805, 36.276518, 1e4),"Example");
//    }

    void disableBatchPicking()
    {
        for (Layer layer : this.wwd.getModel().getLayers())
        {
            if (!layer.getName().toLowerCase().contains("airspace"))
                continue;

            for (Renderable airspace : ((RenderableLayer) layer).getRenderables())
            {
                ((Airspace) airspace).setEnableBatchPicking(false);
            }
        }
    }

    private static Polygon makePolygon()
    {
        Polygon poly = new Polygon();

        List<Position> outerBoundary = Arrays.asList(
            Position.fromDegrees(37.8224479345424, -122.3739784354151, 50.0),
            Position.fromDegrees(37.82239261906633, -122.3740285701554, 50.0),
            Position.fromDegrees(37.82240608112512, -122.3744696934806, 50.0),
            Position.fromDegrees(37.82228167878964, -122.3744693163394, 50.0),
            Position.fromDegrees(37.82226619249474, -122.3739902862858, 50.0),
            Position.fromDegrees(37.82219810227204, -122.3739510452131, 50.0),
            Position.fromDegrees(37.82191990027978, -122.3742004406226, 50.0),
            Position.fromDegrees(37.82186185177756, -122.3740740264531, 50.0),
            Position.fromDegrees(37.82213350487949, -122.3738377669854, 50.0),
            Position.fromDegrees(37.82213842777661, -122.3737599855226, 50.0),
            Position.fromDegrees(37.82184815805735, -122.3735538230499, 50.0),
            Position.fromDegrees(37.82188747252212, -122.3734202823307, 50.0),
            Position.fromDegrees(37.82220302338508, -122.37362176179, 50.0),
            Position.fromDegrees(37.8222686063349, -122.3735762207482, 50.0),
            Position.fromDegrees(37.82224254303025, -122.3731468984375, 50.0),
            Position.fromDegrees(37.82237319467147, -122.3731303943743, 50.0),
            Position.fromDegrees(37.82238194814573, -122.3735637823936, 50.0),
            Position.fromDegrees(37.82244505243797, -122.3736008458059, 50.0),
            Position.fromDegrees(37.82274355652806, -122.3734009024945, 50.0),
            Position.fromDegrees(37.82280084508153, -122.3735091430554, 50.0),
            Position.fromDegrees(37.82251198652374, -122.3737489159765, 50.0),
            Position.fromDegrees(37.82251207172572, -122.3738269699774, 50.0),
            Position.fromDegrees(37.82280161524027, -122.3740332968739, 50.0),
            Position.fromDegrees(37.82275318071796, -122.3741825267907, 50.0),
            Position.fromDegrees(37.8224479345424, -122.3739784354151, 50.0));

        List<Position> innerBoundary = Arrays.asList(
            Position.fromDegrees(37.82237624346899, -122.3739179072036, 50.0),
            Position.fromDegrees(37.82226147323489, -122.3739053159649, 50.0),
            Position.fromDegrees(37.82221834573171, -122.3737889140025, 50.0),
            Position.fromDegrees(37.82226275093125, -122.3736772434448, 50.0),
            Position.fromDegrees(37.82237889526623, -122.3736727730745, 50.0),
            Position.fromDegrees(37.82243486851886, -122.3737811526564, 50.0),
            Position.fromDegrees(37.82237624346899, -122.3739179072036, 50.0));

        poly.setOuterBoundary(outerBoundary);
        poly.addInnerBoundary(innerBoundary);
        poly.setAltitudeMode(WorldWind.RELATIVE_TO_GROUND);
        poly.setAttributes(normalShapeAttributes0);
        poly.setHighlightAttributes(highlightShapeAttributes0);

        poly.setValue(AVKey.SHORT_DESCRIPTION, "Short description of Polygon");
        poly.setValue(AVKey.BALLOON_TEXT, "This is a Polygon.");

        return poly;
    }
    
//    RenderableLayer layer_polygons = null;
//    void create_polygons()
//    {
//        // Add a dragger to enable shape dragging
////        wwd.addSelectListener(new BasicDragger(wwd));
//
////        layer_polygons = new RenderableLayer();
////        layer_polygons.setName("Polygons");
//
//        // Create and set an attribute bundle.
//        ShapeAttributes sideAttributes = new BasicShapeAttributes();
//        sideAttributes.setInteriorMaterial(Material.MAGENTA);
//        sideAttributes.setOutlineOpacity(0.5);
//        sideAttributes.setInteriorOpacity(0.5);
//        sideAttributes.setOutlineMaterial(Material.GREEN);
//        sideAttributes.setOutlineWidth(2);
//        sideAttributes.setDrawOutline(true);
//        sideAttributes.setDrawInterior(true);
//        sideAttributes.setEnableLighting(true);
//
//        ShapeAttributes sideHighlightAttributes = new BasicShapeAttributes(sideAttributes);
//        sideHighlightAttributes.setOutlineMaterial(Material.WHITE);
//        sideHighlightAttributes.setOutlineOpacity(1);
//
//        ShapeAttributes capAttributes = new BasicShapeAttributes(sideAttributes);
//        capAttributes.setInteriorMaterial(Material.YELLOW);
//        capAttributes.setInteriorOpacity(0.8);
//        capAttributes.setDrawInterior(true);
//        capAttributes.setEnableLighting(true);
//
//        // Create a path, set some of its properties and set its attributes.
//        ArrayList<Position> pathPositions = new ArrayList<Position>();
//        pathPositions.add(Position.fromDegrees(28, -106, 3e4));
//        pathPositions.add(Position.fromDegrees(35, -104, 3e4));
//        pathPositions.add(Position.fromDegrees(35, -107, 9e4));
//        pathPositions.add(Position.fromDegrees(28, -107, 9e4));
//        pathPositions.add(Position.fromDegrees(28, -106, 3e4));
//        Polygon pgon = new Polygon(pathPositions);
//
//        pathPositions.clear();
//        pathPositions.add(Position.fromDegrees(29, -106.4, 4e4));
//        pathPositions.add(Position.fromDegrees(30, -106.4, 4e4));
//        pathPositions.add(Position.fromDegrees(29, -106.8, 7e4));
//        pathPositions.add(Position.fromDegrees(29, -106.4, 4e4));
//        pgon.addInnerBoundary(pathPositions);
//        pgon.setAltitudeMode(WorldWind.RELATIVE_TO_GROUND);
////        pgon.setSideAttributes(sideAttributes);
////        pgon.setSideHighlightAttributes(sideHighlightAttributes);
////        pgon.setCapAttributes(capAttributes);
//        layer_polygons.addRenderable(pgon);
//
//        ArrayList<LatLon> pathLocations = new ArrayList<LatLon>();
//        pathLocations.add(LatLon.fromDegrees(28, -110));
//        pathLocations.add(LatLon.fromDegrees(35, -108));
//        pathLocations.add(LatLon.fromDegrees(35, -111));
//        pathLocations.add(LatLon.fromDegrees(28, -111));
//        pathLocations.add(LatLon.fromDegrees(28, -110));
//        pgon = makePolygon();
////        pgon.setSideAttributes(sideAttributes);
////        pgon.setSideHighlightAttributes(sideHighlightAttributes);
////        pgon.setCapAttributes(capAttributes);
//        layer_polygons.addRenderable(pgon);
//        
//        // update sector
//        layer_polygons.setValue("oghab.mapviewer.avKey.Sector", pgon.getSector());
//
//        // Add the layer to the model.
////        insertBeforeCompass(wwd, layer);
//        wwd.getModel().getLayers().add(layer_polygons);
//    }

    private static gov.nasa.worldwind.render.Path makePath()
    {
        gov.nasa.worldwind.render.Path path = new gov.nasa.worldwind.render.Path();

        List<Position> positions = Arrays.asList(
            Position.fromDegrees(37.8304, -122.3720, 0),
            Position.fromDegrees(37.8293, -122.3679, 50),
            Position.fromDegrees(37.8282, -122.3710, 100));

        path.setPositions(positions);
        path.setExtrude(true);

        path.setAttributes(normalShapeAttributes0);
        path.setHighlightAttributes(highlightShapeAttributes0);

        path.setValue(AVKey.SHORT_DESCRIPTION, "Short description of Path");
        path.setValue(AVKey.BALLOON_TEXT, "This is a Path.");

        return path;
    }
    
//    RenderableLayer layer_paths = null;
    void create_paths()
    {
        // Add a dragger to enable shape dragging
//        wwd.addSelectListener(new BasicDragger(wwd));

//        layer_paths = new RenderableLayer();
//        layer_paths.setName("Paths");

        // Create a path, set some of its properties and set its attributes.
//        ArrayList<Position> pathPositions = new ArrayList<Position>();
//        pathPositions.add(Position.fromDegrees(28, -106, 3e4));
//        pathPositions.add(Position.fromDegrees(35, -104, 3e4));
//        pathPositions.add(Position.fromDegrees(35, -107, 9e4));
//        pathPositions.add(Position.fromDegrees(28, -107, 9e4));
//        pathPositions.add(Position.fromDegrees(28, -106, 3e4));
//        gov.nasa.worldwind.render.Path path = new gov.nasa.worldwind.render.Path(pathPositions);
//
//        pathPositions.clear();
//        pathPositions.add(Position.fromDegrees(29, -106.4, 4e4));
//        pathPositions.add(Position.fromDegrees(30, -106.4, 4e4));
//        pathPositions.add(Position.fromDegrees(29, -106.8, 7e4));
//        pathPositions.add(Position.fromDegrees(29, -106.4, 4e4));
//        layer_paths.addRenderable(path);
//
//        ArrayList<LatLon> pathLocations = new ArrayList<LatLon>();
//        pathLocations.add(LatLon.fromDegrees(28, -110));
//        pathLocations.add(LatLon.fromDegrees(35, -108));
//        pathLocations.add(LatLon.fromDegrees(35, -111));
//        pathLocations.add(LatLon.fromDegrees(28, -111));
//        pathLocations.add(LatLon.fromDegrees(28, -110));
//        path = makePath();
//        layer_paths.addRenderable(path);
//        
//        // update sector
//        layer_paths.setValue("oghab.mapviewer.avKey.Sector", path.getSector());

        // Add the layer to the model.
//        wwd.getModel().getLayers().add(layer_paths);
    }
    
    RenderableLayer layer_extruded_polygons = null;
    void create_extruded_polygons()
    {
        // Add a dragger to enable shape dragging
//        wwd.addSelectListener(new BasicDragger(wwd));

        layer_extruded_polygons = new RenderableLayer();
        layer_extruded_polygons.setName("Extuded Polygons");

        // Create and set an attribute bundle.
        ShapeAttributes sideAttributes = new BasicShapeAttributes();
        sideAttributes.setInteriorMaterial(Material.MAGENTA);
        sideAttributes.setOutlineOpacity(0.5);
        sideAttributes.setInteriorOpacity(0.5);
        sideAttributes.setOutlineMaterial(Material.GREEN);
        sideAttributes.setOutlineWidth(2);
        sideAttributes.setDrawOutline(true);
        sideAttributes.setDrawInterior(true);
        sideAttributes.setEnableLighting(true);

        ShapeAttributes sideHighlightAttributes = new BasicShapeAttributes(sideAttributes);
        sideHighlightAttributes.setOutlineMaterial(Material.WHITE);
        sideHighlightAttributes.setOutlineOpacity(1);

        ShapeAttributes capAttributes = new BasicShapeAttributes(sideAttributes);
        capAttributes.setInteriorMaterial(Material.YELLOW);
        capAttributes.setInteriorOpacity(0.8);
        capAttributes.setDrawInterior(true);
        capAttributes.setEnableLighting(true);

        // Create a path, set some of its properties and set its attributes.
        ArrayList<Position> pathPositions = new ArrayList<Position>();
        pathPositions.add(Position.fromDegrees(28, -106, 3e4));
        pathPositions.add(Position.fromDegrees(35, -104, 3e4));
        pathPositions.add(Position.fromDegrees(35, -107, 9e4));
        pathPositions.add(Position.fromDegrees(28, -107, 9e4));
        pathPositions.add(Position.fromDegrees(28, -106, 3e4));
        ExtrudedPolygon pgon = new ExtrudedPolygon(pathPositions);

        pathPositions.clear();
        pathPositions.add(Position.fromDegrees(29, -106.4, 4e4));
        pathPositions.add(Position.fromDegrees(30, -106.4, 4e4));
        pathPositions.add(Position.fromDegrees(29, -106.8, 7e4));
        pathPositions.add(Position.fromDegrees(29, -106.4, 4e4));
        pgon.addInnerBoundary(pathPositions);
        pgon.setAltitudeMode(WorldWind.RELATIVE_TO_GROUND);
        pgon.setSideAttributes(sideAttributes);
        pgon.setSideHighlightAttributes(sideHighlightAttributes);
        pgon.setCapAttributes(capAttributes);
        layer_extruded_polygons.addRenderable(pgon);

        ArrayList<LatLon> pathLocations = new ArrayList<LatLon>();
        pathLocations.add(LatLon.fromDegrees(28, -110));
        pathLocations.add(LatLon.fromDegrees(35, -108));
        pathLocations.add(LatLon.fromDegrees(35, -111));
        pathLocations.add(LatLon.fromDegrees(28, -111));
        pathLocations.add(LatLon.fromDegrees(28, -110));
        pgon = new ExtrudedPolygon(pathLocations, 6e4);
        pgon.setSideAttributes(sideAttributes);
        pgon.setSideHighlightAttributes(sideHighlightAttributes);
        pgon.setCapAttributes(capAttributes);
        layer_extruded_polygons.addRenderable(pgon);
        
        // update sector
        layer_extruded_polygons.setValue("oghab.mapviewer.avKey.Sector", pgon.getSector());

        // Add the layer to the model.
//        insertBeforeCompass(wwd, layer);
        wwd.getModel().getLayers().add(layer_extruded_polygons);
    }
    
    RenderableLayer layer_extruded_shapefiles = null;
    void create_extruded_shapefiles(String strFilename)
    {
        // Construct a factory that loads Shapefiles on a background thread.
        ShapefileLayerFactory factory = new ShapefileLayerFactory();

        // Load a Shapefile in the San Francisco bay area containing per-shape height attributes.
        factory.createFromShapefileSource(strFilename,
            new ShapefileLayerFactory.CompletionCallback()
            {
                @Override
                public void completion(Object result)
                {
                    layer_extruded_shapefiles = (RenderableLayer) result; // the result is the layer the factory created
//                    layer.setName(WWIO.getFilename(layer.getName()));
                    layer_extruded_shapefiles.setName("Extruded Shapefiles");

                    // Add the layer to the WorldWindow's layer list on the Event Dispatch Thread.
                    SwingUtilities.invokeLater(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            wwd.getModel().getLayers().add(layer_extruded_shapefiles);
                            wwd.redraw();
                        }
                    });
                }

                @Override
                public void exception(Exception e)
                {
                    Logging.logger().log(java.util.logging.Level.SEVERE, e.getMessage(), e);
                }
            });
    }
    
    MarkerLayer layer_markers = null;
    void load_gpx(String strFilename) throws org.xml.sax.SAXException, IOException
    {
        layer_markers = this.buildTracksLayer(strFilename);
        layer_markers.setName("GPX Layer");
//        insertBeforeCompass(wwd, layer);
        wwd.getModel().getLayers().add(layer_markers);

        wwd.addSelectListener(new SelectListener()
        {
            public void selected(SelectEvent event)
            {
                if (event.getTopObject() != null)
                {
                    if (event.getTopPickedObject().getParentLayer() instanceof MarkerLayer)
                    {
                        PickedObject po = event.getTopPickedObject();
                        //noinspection RedundantCast
                        System.out.printf("Track position %s, %s, size = %f\n",
                            po.getValue(AVKey.PICKED_OBJECT_ID).toString(),
                            po.getPosition(), (Double) po.getValue(AVKey.PICKED_OBJECT_SIZE));
                    }
                }
            }
        });
    }

    protected MarkerLayer buildTracksLayer(String strFilename) throws org.xml.sax.SAXException, IOException
    {
        try
        {
            GpxReader reader = new GpxReader();
            reader.readStream(WWIO.openFileOrResourceStream(strFilename, this.getClass()));
            Iterator<Position> positions = reader.getTrackPositionIterator();

            BasicMarkerAttributes attrs =
                new BasicMarkerAttributes(Material.WHITE, BasicMarkerShape.SPHERE, 1d);

            ArrayList<Marker> markers = new ArrayList<Marker>();
            ArrayList<Position> poses = new ArrayList<Position>();
            while (positions.hasNext())
            {
                Position p = positions.next();
                markers.add(new BasicMarker(p, attrs));
                poses.add(p);
            }
            
//            gov.nasa.worldwind.render.Path polyline = new gov.nasa.worldwind.render.Path();
//            polyline.setPositions(poses);

            MarkerLayer layer = new MarkerLayer(markers);
            layer.setOverrideMarkerElevation(true);
            layer.setElevation(0);
            layer.setEnablePickSizeReturn(true);
            layer.setKeepSeparated(true);
            
            // update sector
            if(markers.size() > 0)
            {
                Marker extent = (Marker)markers.get(0);
                if(extent != null)
                {
                    Sector sector = Sector.fromDegrees(extent.getPosition().getLatitude().getDegrees(),extent.getPosition().getLatitude().getDegrees(),extent.getPosition().getLongitude().getDegrees(),extent.getPosition().getLongitude().getDegrees());
                    layer.setValue("oghab.mapviewer.avKey.Sector", sector);
                }
            }

            return layer;
        }
        catch (ParserConfigurationException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    void create_geo_rss(String filename)
    {
        RenderableLayer layer = this.buildGeoRSSLayer(filename);
        layer.setName("GeoRSS Shapes");
//        insertBeforeCompass(wwd, layer);
        wwd.getModel().getLayers().add(layer);
    }

    private RenderableLayer buildGeoRSSLayer(String filename) {
        RenderableLayer layer = new RenderableLayer();
        java.util.List<Renderable> shapes;

        if(filename != null)
        {
            shapes = GeoRSSParser.parseShapes(new File(filename));
            if (shapes != null) {
                addRenderables(layer, shapes);
            }
        }
        
        shapes = GeoRSSParser.parseShapes(GeoRSS_DOCSTRING_A);
        if (shapes != null) {
            addRenderables(layer, shapes);
        }

        shapes = GeoRSSParser.parseShapes(GeoRSS_DOCSTRING_B);
        if (shapes != null) {
            addRenderables(layer, shapes);
        }

        shapes = GeoRSSParser.parseShapes(GeoRSS_DOCSTRING_C);
        if (shapes != null) {
            addRenderables(layer, shapes);
        }

        // update sector
        if(shapes.size() > 0)
        {
            GeographicExtent extent = (GeographicExtent)shapes.get(0);
            if(extent != null)
            {
        //            Sector sector = Sector.boundingSector(si1.getCorners());
        //            Sector sector = Sector.fromDegrees(position.getPosition().getLatitude().getDegrees(),position.getPosition().getLatitude().getDegrees(),position.getPosition().getLongitude().getDegrees(),position.getPosition().getLongitude().getDegrees());
                Sector sector = extent.getSector();
                layer.setValue("oghab.mapviewer.avKey.Sector", sector);
            }
        }

        return layer;
    }

    private void addRenderables(RenderableLayer layer, Iterable<Renderable> renderables) {
        for (Renderable r : renderables) {
            layer.addRenderable(r);
        }
    }

    private static final String GeoRSS_DOCSTRING_A
            = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
            + "<feed xmlns=\"http://www.w3.org/2005/Atom\""
            + "      xmlns:georss=\"http://www.georss.org/georss\""
            + "      xmlns:gml=\"http://www.opengis.net/gml\">"
            + "  <title>Earthquakes</title>"
            + "    <subtitle>International earthquake observation labs</subtitle>"
            + "    <link href=\"http://example.org/\"/>"
            + "    <updated>2005-12-13T18:30:02Z</updated>"
            + "    <author>"
            + "      <name>Dr. Thaddeus Remor</name>"
            + "      <email>tremor@quakelab.edu</email>"
            + "    </author>"
            + "    <id>urn:uuid:60a76c80-d399-11d9-b93C-0003939e0af6</id>"
            + "  <entry>"
            + "    <title>M 3.2, Mona Passage</title>"
            + "    <link href=\"http://example.org/2005/09/09/atom01\"/>"
            + "    <id>urn:uuid:1225c695-cfb8-4ebb-aaaa-80da344efa6a</id>"
            + "    <updated>2005-08-17T07:02:32Z</updated>"
            + "    <summary>We just had a big one.</summary>"
            + "    <georss:where>"
            + "      <gml:Polygon>"
            + "        <gml:exterior>"
            + "          <gml:LinearRing>"
            + "            <gml:posList>"
            + "              45.256 -110.45 46.46 -109.48 43.84 -109.86 45.256 -110.45"
            + "            </gml:posList>"
            + "          </gml:LinearRing>"
            + "        </gml:exterior>"
            + "      </gml:Polygon>"
            + "    </georss:where>"
            + "  </entry>"
            + "</feed>";

    private static final String GeoRSS_DOCSTRING_B
            = "<feed xmlns=\"http://www.w3.org/2005/Atom\""
            + "              xmlns:georss=\"http://www.georss.org/georss\">"
            + "              <title>scribble</title>"
            + "              <id>http://example.com/atom</id>"
            + "              <author><name>Christopher Schmidt</name></author>"
            + "<entry>"
            + "  <id>http://example.com/19.atom</id>"
            + "  <link href=\"http://example.com/19.html\"/>"
            + "  <title>Feature #19</title>"
            + "  <content type=\"html\">Some content.</content>"
            + "  <georss:line>"
            + "    23.1811523438 -159.609375 "
            + "    22.5 -161.564941406 "
            + "    20.654296875 -160.422363281 "
            + "    18.4350585938 -156.247558594 "
            + "    18.3471679688 -154.731445312 "
            + "    19.951171875 -153.588867188 "
            + "    21.8188476562 -155.983886719"
            + "    23.02734375 -158.994140625"
            + "    23.0932617188 -159.631347656"
            + "  </georss:line>"
            + "</entry>"
            + "</feed>";

    private static final String GeoRSS_DOCSTRING_C
            = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
            + "<feed xmlns=\"http://www.w3.org/2005/Atom\""
            + "      xmlns:georss=\"http://www.georss.org/georss\""
            + "      xmlns:gml=\"http://www.opengis.net/gml\">"
            + "  <title>An X</title>"
            + "    <subtitle>Line test</subtitle>"
            + "    <link href=\"http://example.org/\"/>"
            + "    <updated>2005-12-13T18:30:02Z</updated>"
            + "    <author>"
            + "      <name>NASA</name>"
            + "      <email>nasa@nasa.gov</email>"
            + "    </author>"
            + "    <id>urn:uuid:60a76c80-d399-11d9-b93C-0003939e0af6</id>"
            + "  <entry>"
            + "    <title>An X</title>"
            + "    <link href=\"http://example.org/2005/09/09/atom01\"/>"
            + "    <id>urn:uuid:1225c695-cfb8-4ebb-aaaa-80da344efa6a</id>"
            + "    <updated>2005-08-17T07:02:32Z</updated>"
            + "    <summary>Test</summary>"
            + "    <georss:line>45 -95 44 -94</georss:line>"
            + "    <georss:line>45 -94 44 -95</georss:line>"
            + "    <georss:elev>1000</georss:elev>"
            + "  </entry>"
            + "</feed>";
    
    void create_anotations()
    {
        RenderableLayer layer = new RenderableLayer();
        layer.setName("Annotations");
//        insertBeforeCompass(wwd, layer);
        wwd.getModel().getLayers().add(layer);

        GlobeAnnotation ga = new GlobeAnnotation("ناحية شين", Position.fromDegrees(34.781303, 36.425336, 0));
//        ga.setAlwaysOnTop(true);

        AnnotationAttributes attrs = new AnnotationAttributes();
        attrs.setFont(new Font("Traditional Arabic", Font.BOLD, 18));
        attrs.setTextColor(Color.red);
        attrs.setScale(1.0);
        attrs.setOpacity(0.5);
        ga.setAttributes(attrs);

        layer.addRenderable(ga);

        // update sector
        Sector sector = Sector.fromDegrees(ga.getPosition().getLatitude().getDegrees(),ga.getPosition().getLatitude().getDegrees(),ga.getPosition().getLongitude().getDegrees(),ga.getPosition().getLongitude().getDegrees());
        layer.setValue("oghab.mapviewer.avKey.Sector", sector);
    }
    
//    void add_icons_layer()
//    {
//        IconLayer layer = new IconLayer();
//        layer.setPickEnabled(true);
//        layer.setAllowBatchPicking(false);
//        layer.setRegionCulling(true);
//
//        UserFacingIcon icon = new UserFacingIcon(Paths.get(strImagesPath, "mapviewer_icon.png").toString(),
//            new Position(Angle.fromRadians(0), Angle.fromRadians(0), 0));
//        icon.setSize(new Dimension(24, 24));
//        layer.addIcon(icon);
//
//        icon = new UserFacingIcon(Paths.get(strImagesPath, "mapviewer_icon.png").toString(),
//            new Position(Angle.fromRadians(0.1), Angle.fromRadians(0.0), 0));
//        icon.setSize(new Dimension(24, 24));
//        layer.addIcon(icon);
//
//        icon = new UserFacingIcon(Paths.get(strImagesPath, "mapviewer_icon.png").toString(),
//            new Position(Angle.fromRadians(0.0), Angle.fromRadians(0.1), 0));
//        icon.setSize(new Dimension(24, 24));
//        layer.addIcon(icon);
//
//        icon = new UserFacingIcon(Paths.get(strImagesPath, "mapviewer_icon.png").toString(),
//            new Position(Angle.fromRadians(0.1), Angle.fromRadians(0.1), 0));
//        icon.setSize(new Dimension(24, 24));
//        layer.addIcon(icon);
//
//        icon = new UserFacingIcon(Paths.get(strImagesPath, "mapviewer_icon.png").toString(),
//            new Position(Angle.fromRadians(0), Angle.fromDegrees(180), 0));
//        icon.setSize(new Dimension(24, 24));
//        layer.addIcon(icon);
//
////        insertBeforeCompass(wwd, layer);
//        wwd.getModel().getLayers().add(layer);
//        wwd.getSceneController().setDeepPickEnabled(true);
//
//        // update sector
//        Sector sector = Sector.fromDegrees(icon.getPosition().getLatitude().getDegrees(),icon.getPosition().getLatitude().getDegrees(),icon.getPosition().getLongitude().getDegrees(),icon.getPosition().getLongitude().getDegrees());
//        layer.setValue("oghab.mapviewer.avKey.Sector", sector);
//    }

//    RenderableLayer layer_shapefiles = null;
    void load_shapefile(String strFilename, boolean bVisible)
    {
        if(!file_exists(strFilename))
        {
            System.out.println("load_shapefile: ["+strFilename+"] not exists.");
            return;
        }
        ShapefileLayerFactory factory = new ShapefileLayerFactory();

//        Shapefile shapefile = null;
//        try {
//            shapefile = new Shapefile(
//                    WWIO.openStream(strFilename),
//                    WWIO.openStream(WWIO.replaceSuffix(strFilename, ".shx")),
//                    WWIO.openStream(WWIO.replaceSuffix(strFilename, ".dbf")),
//                    WWIO.openStream(WWIO.replaceSuffix(strFilename, ".prj")));
//        } catch (Exception ex) {
//            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
//        }
        
        // Specify an attribute delegate to assign random attributes to each shapefile record.
        final myRandomShapeAttributes randomAttrs = new myRandomShapeAttributes();
        factory.setAttributeDelegate(new ShapefileRenderable.AttributeDelegate()
        {
            @Override
            public void assignAttributes(ShapefileRecord shapefileRecord, ShapefileRenderable.Record renderableRecord)
            {
//                renderableRecord.setAttributes(randomAttrs.nextAttributes().asShapeAttributes());

                Random random = new Random();  
                float r = random.nextFloat();
                float g = random.nextFloat();
                float b = random.nextFloat();
                Color color = new Color(r, g, b);
                ShapeAttributes attrs = new BasicShapeAttributes();
                attrs.setInteriorMaterial(new Material(color));
//                attrs.setOutlineMaterial(new Material(WWUtil.makeColorBrighter(color)));
                attrs.setOutlineMaterial(new Material(WWUtil.makeColorDarker(color)));
                attrs.setInteriorOpacity(0.5);
                attrs.setOutlineWidth(3);
                renderableRecord.setAttributes(attrs);
            }
        });

        // Load the shapefile. Define the completion callback.
        factory.createFromShapefileSource(strFilename,
//        factory.createFromShapefileSource(shapefile,
        new ShapefileLayerFactory.CompletionCallback()
        {
            @Override
            public void completion(Object result)
            {
//                    layer_shapefiles = (RenderableLayer) result; // the result is the layer the factory created
//                    layer_shapefiles.setName(WWIO.getFilename(layer_shapefiles.getName()));
//                    layer_shapefiles.setPickEnabled(false);
//                    layer_shapefiles.setEnabled(bVisible);

                RenderableLayer layer = (RenderableLayer) result; // the result is the layer the factory created
                layer.setName(WWIO.getFilename(layer.getName()));
                layer.setPickEnabled(false);
                layer.setEnabled(bVisible);

                // Add the layer to the WorldWindow's layer list on the Event Dispatch Thread.
                SwingUtilities.invokeLater(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        wwd.getModel().getLayers().add(layer);
//                            wwd.getModel().getLayers().add(layer_shapefiles);
                    }
                });
            }

            @Override
            public void exception(Exception e)
            {
                Logging.logger().log(java.util.logging.Level.SEVERE, e.getMessage(), e);
            }
        });
    }
    
    void load_zip_shapefile(String zipFilePath, boolean bVisible)
    {
        if(!file_exists(zipFilePath))
        {
            System.out.println("load_zip_shapefile: ["+zipFilePath+"] not exists.");
            return;
        }
        final String strLayerName = WWIO.getFilename(zipFilePath);
        ShapefileLayerFactory factory = new ShapefileLayerFactory();

        FileSystem fs = null;
        try {
            zipFilePath = zipFilePath.replace("\\","/");
            URI uri = URI.create("jar:file:/"+zipFilePath);
            Map<String,String> env = new HashMap<String,String>();//AliSoft
            env.put("create", "false");
            fs = FileSystems.newFileSystem(uri, env);
        } catch (IOException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }

        Shapefile shapefile = null;
        try {
            String strFilename = WWIO.getFilename(zipFilePath);
            strFilename = strFilename.replace("file://","");
            strFilename = strFilename.replace("file:/","");
            strFilename = "/"+strFilename;

            Path shpPath = fs.getPath(WWIO.replaceSuffix(strFilename, ".shp"));
            if(Files.exists(shpPath))
            {
                InputStream shpStream = Files.newInputStream(shpPath);

                Path shxPath = fs.getPath(WWIO.replaceSuffix(strFilename, ".shx"));
                InputStream shxStream = Files.newInputStream(shxPath);

                Path dbfPath = fs.getPath(WWIO.replaceSuffix(strFilename, ".dbf"));
                InputStream dbfStream = Files.newInputStream(dbfPath);

                Path prjPath = fs.getPath(WWIO.replaceSuffix(strFilename, ".prj"));
                InputStream prjStream = Files.newInputStream(prjPath);

                shapefile = new Shapefile(
                        shpStream,
                        shxStream,
                        dbfStream,
                        prjStream);
            }
        } catch (Exception ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        // Specify an attribute delegate to assign random attributes to each shapefile record.
        factory.setAttributeDelegate(new ShapefileRenderable.AttributeDelegate()
        {
            @Override
            public void assignAttributes(ShapefileRecord shapefileRecord, ShapefileRenderable.Record renderableRecord)
            {
                Random random = new Random();  
                float r = random.nextFloat();
                float g = random.nextFloat();
                float b = random.nextFloat();
                Color color = new Color(r, g, b);
                ShapeAttributes attrs = new BasicShapeAttributes();
                attrs.setInteriorMaterial(new Material(color));
//                attrs.setOutlineMaterial(new Material(WWUtil.makeColorBrighter(color)));
                attrs.setOutlineMaterial(new Material(WWUtil.makeColorDarker(color)));
                attrs.setInteriorOpacity(0.5);
                attrs.setOutlineWidth(3);
                renderableRecord.setAttributes(attrs);
            }
        });

        // Load the shapefile. Define the completion callback.
        factory.createFromShapefileSource(shapefile,
        new ShapefileLayerFactory.CompletionCallback()
        {
            @Override
            public void completion(Object result)
            {
                RenderableLayer layer = (RenderableLayer) result; // the result is the layer the factory created
                layer.setName(strLayerName);
                layer.setPickEnabled(false);
                layer.setEnabled(bVisible);

                // Add the layer to the WorldWindow's layer list on the Event Dispatch Thread.
                SwingUtilities.invokeLater(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        wwd.getModel().getLayers().add(layer);
                    }
                });
            }

            @Override
            public void exception(Exception e)
            {
                Logging.logger().log(java.util.logging.Level.SEVERE, e.getMessage(), e);
            }
        });
    }
    
    private static ScreenImage makeScreenImage(String filename,double x)
    {
        ScreenImage sc = new ScreenImage();

//        sc.setImageSource(Paths.get(strImagesPath, filename).toString());
        sc.setImageSource(filename);

//        sc.setScreenOffset(new Offset(0.0, 0.0, AVKey.FRACTION, AVKey.FRACTION));
//        sc.setScreenOffset(new Offset(0.0, 20.0, AVKey.FRACTION, AVKey.PIXELS));
        sc.setScreenOffset(new Offset(x, 20.0, AVKey.FRACTION, AVKey.PIXELS));
        sc.setImageOffset(new Offset(0.0, 0.0, AVKey.FRACTION, AVKey.FRACTION));

        Size size = new Size();
//        size.setHeight(Size.EXPLICIT_DIMENSION, 96.0, AVKey.PIXELS);
//        size.setWidth(Size.MAINTAIN_ASPECT_RATIO, 0.0, null);

        size.setWidth(Size.EXPLICIT_DIMENSION, 128.0, AVKey.PIXELS);
        size.setHeight(Size.MAINTAIN_ASPECT_RATIO, 0.0, null);
        sc.setSize(size);

        return sc;
    }
    
    private static ScreenImage updateScreenImage(BufferedImage image)
    {
        ScreenImage sc = new ScreenImage();

        sc.setImageSource(image);

//        sc.setScreenOffset(new Offset(0.0, 0.0, AVKey.FRACTION, AVKey.FRACTION));
        sc.setScreenOffset(new Offset(0.0, 20.0, AVKey.FRACTION, AVKey.PIXELS));
        sc.setImageOffset(new Offset(0.0, 0.0, AVKey.FRACTION, AVKey.FRACTION));

        Size size = new Size();
        size.setWidth(Size.EXPLICIT_DIMENSION, 640, AVKey.PIXELS);
        size.setHeight(Size.MAINTAIN_ASPECT_RATIO, 0.0, null);
        sc.setSize(size);

        return sc;
    }
    
    RenderableLayer layer_screen_images = null;
    void create_screen_images() {
        try {
            layer_screen_images = new RenderableLayer();
            layer_screen_images.setName("Screen Images");
            layer_screen_images.setPickEnabled(false);

//            ScreenImage screen_image = makeScreenImage("Oghab.png",0);
            ScreenImage screen_image = makeScreenImage("res/Oghab.png",0);
            layer_screen_images.addRenderable(screen_image);

            if(Settings.bCooperation)
            {
//                screen_image = makeScreenImage("OTI.png",0.5);
                screen_image = makeScreenImage("res/OTI.png",0.5);
                layer_screen_images.addRenderable(screen_image);
            }

//            insertBeforeCompass(wwd, layer);
            wwd.getModel().getLayers().add(layer_screen_images);

            // update sector
//            Sector sector = Sector.boundingSector(screen_image.getCorners());
//            layer.setValue("oghab.mapviewer.avKey.Sector", sector);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    void create_fire()
    {
        double fire_lon = 36.273732;
        double fire_lat = 33.532739;
        double fire_alt = 1144;
        RenderableLayer layer_fire = new RenderableLayer();
        layer_fire.setName("Fire Layer");
        FireRenderable fire = new FireRenderable(Position.fromDegrees(fire_lat, fire_lon, fire_alt), 10, 5, 3, 300, 1);
        fire.setElevation(30); //turn it pointing up
        fire.setSize(2); //just makes it easier to see
        layer_fire.addRenderable(fire);
        Sector sector = Sector.fromDegrees(fire.getPosition().getLatitude().getDegrees(),fire.getPosition().getLatitude().getDegrees(),fire.getPosition().getLongitude().getDegrees(),fire.getPosition().getLongitude().getDegrees());
        layer_fire.setValue("oghab.mapviewer.avKey.Sector", sector);
        wwd.getModel().getLayers().add(layer_fire);
    }
    
    RenderableLayer layer_surface_images = null;
    void create_surface_images() {
        try {
//            SurfaceImage si1 = new SurfaceImage(Paths.get(strImagesPath, "Splash.png").toString(), new ArrayList<>(Arrays.asList(
            SurfaceImage si1 = new SurfaceImage("res/Splash.png", new ArrayList<>(Arrays.asList(
                    LatLon.fromDegrees(34d, 34.5d),
                    LatLon.fromDegrees(34d, 35.5d),
                    LatLon.fromDegrees(35d, 35.5d),
                    LatLon.fromDegrees(35d, 34.5d)
            )));
            si1.setAlwaysOnTop(true);
            
            gov.nasa.worldwind.render.Path boundary = new gov.nasa.worldwind.render.Path(si1.getCorners(), 0);
            boundary.setSurfacePath(true);
            boundary.setPathType(AVKey.RHUMB_LINE);
            BasicShapeAttributes attrs = new BasicShapeAttributes();
            attrs.setOutlineMaterial(new Material(new Color(0, 255, 0)));
            boundary.setAttributes(attrs);
            boundary.makeClosed();

            layer_surface_images = new RenderableLayer();
            layer_surface_images.setName("Surface Images");
            layer_surface_images.setPickEnabled(false);
            layer_surface_images.addRenderable(si1);
            layer_surface_images.addRenderable(boundary);

//            insertBeforeCompass(wwd, layer);
            wwd.getModel().getLayers().add(layer_surface_images);

            // update sector
            Sector sector = Sector.boundingSector(si1.getCorners());
            layer_surface_images.setValue("oghab.mapviewer.avKey.Sector", sector);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    BufferedImage read_image(URL filename) throws IOException
    {
        BufferedImage in = ImageIO.read(filename);

        BufferedImage newImage = new BufferedImage(
            in.getWidth(), in.getHeight(), BufferedImage.TYPE_INT_ARGB);

        Graphics2D g = newImage.createGraphics();
        g.drawImage(in, 0, 0, null);
        g.dispose();
        return newImage;
    }
    
//    void create_layers_tree()
//    {
//        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Layers");
//        
//        DefaultMutableTreeNode raster = new DefaultMutableTreeNode("Raster");
//        wwd.getModel().getLayers().forEach(layer ->
//        {
//            String strName = layer.getName();
//            
//            mv_Layer country = new mv_Layer(strName, "/res/icons/icon_10.png", layer);
//            DefaultMutableTreeNode node = new DefaultMutableTreeNode(country);
//            raster.add(node);
//        });
//        
////        DefaultMutableTreeNode vector = new DefaultMutableTreeNode("Vector");
////        mv_Layer[] countries = new mv_Layer[]{
////                new mv_Layer("India", "/res/icons/icon_10.png"),
////                new mv_Layer("Singapore", "/res/icons/icon_11.png"),
////                new mv_Layer("Indonesia", "/res/icons/icon_12.png"),
////                new mv_Layer("Vietnam", "/res/icons/icon_13.png"),
////        };
////
////        for (mv_Layer country : countries) {
////            DefaultMutableTreeNode node = new DefaultMutableTreeNode(country);
////            vector.add(node);
////        }
////
////        DefaultMutableTreeNode points = new DefaultMutableTreeNode("Points");
////        countries = new mv_Layer[]{
////                new mv_Layer("United States", "/res/icons/icon_14.png"),
////                new mv_Layer("Canada", "/res/icons/icon_15.png")
////        };
////
////        for (mv_Layer country : countries) {
////            DefaultMutableTreeNode node = new DefaultMutableTreeNode(country);
////            points.add(node);
////        }
//
//        root.add(raster);
////        root.add(vector);
////        root.add(points);
//
//        //create the tree by passing in the root node
//        layers_tree.setModel(new DefaultTreeModel(root));
//        layers_tree.setCellRenderer((TreeCellRenderer) new CountryTreeCellRenderer());
//        
//        layers_tree.setShowsRootHandles(true);
//        layers_tree.setRootVisible(false);
//        
//        layers_tree.addMouseListener(new MouseListener(){
//            public void mouseMoved(MouseEvent e){
//            }
//            @Override
//            public void mouseClicked(MouseEvent e) {
//                TreePath path = layers_tree.getClosestPathForLocation(e.getX(), e.getY());
//                if(path == null)    return;
//                layers_tree.clearSelection();
//                layers_tree.addSelectionPath(path);
//                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode)path.getLastPathComponent();
//                if(selectedNode == null)    return;
//                selectedLabel.setIcon(null);
//                Object object = selectedNode.getUserObject();
//                if(object instanceof mv_Layer)
//                {
//                    mv_Layer layer = (mv_Layer)object;
//                    layer.getLayer().setEnabled(!layer.getLayer().isEnabled());
//                    selectedLabel.setText(layer.getName());
////                    URL imageUrl = getClass().getResource(layer.getFlagIcon());
////                    if (imageUrl != null) {
////                        selectedLabel.setIcon(new ImageIcon(imageUrl));
////                    }
//                }
//                else
//                {
//                    selectedLabel.setText(object.toString());
//                }
//            }
//
//            @Override
//            public void mousePressed(MouseEvent e) {
//            }
//
//            @Override
//            public void mouseReleased(MouseEvent e) {
//            }
//
//            @Override
//            public void mouseEntered(MouseEvent e) {
//            }
//
//            @Override
//            public void mouseExited(MouseEvent e) {
//            }
//        });
//        
//        
////        layers_tree.getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {
//        layers_tree.getCheckBoxTreeSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {
//            @Override
//            public void valueChanged(TreeSelectionEvent e) {
///*                
////                TreePath path = e.getNewLeadSelectionPath();
////                TreePath path = layers_tree.getEditingPath();
//                TreePath path = layers_tree.getSelectionPath();
//                if(path == null)    return;
//                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode)path.getLastPathComponent();
////                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode)layers_tree.getLastSelectedPathComponent();
//                if(selectedNode == null)    return;
//                selectedLabel.setIcon(null);
//                Object object = selectedNode.getUserObject();
//                if(object instanceof mv_Layer)
//                {
//                    mv_Layer layer = (mv_Layer)object;
//                    layer.getLayer().setEnabled(!layer.getLayer().isEnabled());
//                    selectedLabel.setText(layer.getName());
////                    URL imageUrl = getClass().getResource(layer.getFlagIcon());
////                    if (imageUrl != null) {
////                        selectedLabel.setIcon(new ImageIcon(imageUrl));
////                    }
//                }
//                else
//                {
//                    selectedLabel.setText(object.toString());
//                }
//*/                
//            }
//        });
//    }

    protected void setHighlightAttributes(PointPlacemark pp)
    {
        // Change the label color to orange when the placemark is selected.
        PointPlacemarkAttributes highlightAttributes = new PointPlacemarkAttributes(pp.getAttributes());
        highlightAttributes.setLabelMaterial(Material.ORANGE);
        highlightAttributes.setScale(1.1);
        pp.setHighlightAttributes(highlightAttributes);
    }
    
    void goto_object_renderable(Object obj)
    {
        Object obj6 = obj;
        if(obj6 instanceof gov.nasa.worldwind.render.PointPlacemark)
        {
            gov.nasa.worldwind.render.PointPlacemark obj7 = (gov.nasa.worldwind.render.PointPlacemark)obj6;
            if(Settings.bDebug)  System.out.println("obj7: "+obj7+", "+obj7.getLabelText());

            goTo(wwd,obj7.getPosition());
            wwd.redraw();
            return;
        }

        if(obj6 instanceof gov.nasa.worldwind.render.Path)
        {
            gov.nasa.worldwind.render.Path obj7 = (gov.nasa.worldwind.render.Path)obj6;
            if(Settings.bDebug)  System.out.println("obj7: "+obj7+", "+obj7.getStringValue(AVKey.DISPLAY_NAME));

            goTo(wwd,obj7.getSector());
            wwd.redraw();
            return;
        }

        if(obj6 instanceof gov.nasa.worldwind.render.Polygon)
        {
            gov.nasa.worldwind.render.Polygon obj7 = (gov.nasa.worldwind.render.Polygon)obj6;
            if(Settings.bDebug)  System.out.println("obj7: "+obj7+", "+obj7.getStringValue(AVKey.DISPLAY_NAME));

            goTo(wwd,obj7.getSector());
            wwd.redraw();
            return;
        }

//            if(obj6 instanceof KMLPointPlacemarkImpl)
//            {
//                KMLPointPlacemarkImpl obj7 = (KMLPointPlacemarkImpl)obj6;
//                if(Settings.bDebug)  System.out.println("obj7: "+obj7+", "+obj7.getLabelText());
//
//                goTo(wwd,obj7.getPosition());
//                wwd.redraw();
//                return;
//            }

//            if(obj6 instanceof KMLLineStringPlacemarkImpl)
//            {
//                KMLLineStringPlacemarkImpl obj7 = (KMLLineStringPlacemarkImpl)obj6;
//                if(Settings.bDebug)  System.out.println("obj7: "+obj7);
//
//                goTo(wwd,obj7.getSector());
//                wwd.redraw();
//                return;
//            }

        if(obj6 instanceof KMLGroundOverlayPolygonImpl)
        {
            KMLGroundOverlayPolygonImpl obj7 = (KMLGroundOverlayPolygonImpl)obj6;
            if(Settings.bDebug)  System.out.println("obj7: "+obj7);

            goTo(wwd,obj7.getSector());
            wwd.redraw();
            return;
        }

        if(obj6 instanceof KMLPolygonImpl)
        {
            KMLPolygonImpl obj7 = (KMLPolygonImpl)obj6;
            if(Settings.bDebug)  System.out.println("obj7: "+obj7);

            goTo(wwd,obj7.getSector());
            wwd.redraw();
            return;
        }

        if(obj6 instanceof KMLSurfaceImageImpl)
        {
            KMLSurfaceImageImpl obj7 = (KMLSurfaceImageImpl)obj6;
            if(Settings.bDebug)  System.out.println("obj7: "+obj7);

//                                        obj7.setVisible(entry.checked);
//                                        if(bZoom)   goTo(wwd,obj7.getSector());
            wwd.redraw();
            return;
        }

        if(obj6 instanceof KMLSurfacePolygonImpl)
        {
            KMLSurfacePolygonImpl obj7 = (KMLSurfacePolygonImpl)obj6;
            if(Settings.bDebug)  System.out.println("obj7: "+obj7);

            goTo(wwd,obj7.getReferencePosition());
            wwd.redraw();
            return;
        }

        if(obj6 instanceof KMLExtrudedPolygonImpl)
        {
            KMLExtrudedPolygonImpl obj7 = (KMLExtrudedPolygonImpl)obj6;
            if(Settings.bDebug)  System.out.println("obj7: "+obj7);

            goTo(wwd,obj7.getSector());
            wwd.redraw();
            return;
        }

        if(obj6 instanceof KMLGroundOverlay)
        {
            KMLGroundOverlay obj7 = (KMLGroundOverlay)obj6;
            if(Settings.bDebug)  System.out.println("obj7: "+obj7);

//                                        obj7.setVisible(entry.checked);
//                                        if(bZoom)   goTo(wwd,obj7.getSector());
            wwd.redraw();
            return;
        }
            
        if(obj6 instanceof KMLFeatureTreeNode)
        {
            KMLFeatureTreeNode obj7 = (KMLFeatureTreeNode)obj6;
            if(Settings.bDebug)  System.out.println("obj7: "+obj7+", "+obj7.getText());

            Object obj8 = obj7.getValue(AVKey.CONTEXT);
            goto_object(obj8);
            return;
        }
            
        if(obj6 instanceof KMLLayerTreeNode)
        {
            KMLLayerTreeNode obj7 = (KMLLayerTreeNode)obj6;
            if(Settings.bDebug)  System.out.println("obj7: "+obj7+", "+obj7.getText());

            Object obj8 = obj7.getValue(AVKey.CONTEXT);
            goto_object(obj8);
            return;
        }
    }
    
    void goto_object(Object obj)
    {
        if(obj instanceof KMLPlacemark)// loaded from file
        {
            KMLPlacemark obj5 = (KMLPlacemark)obj;
            if(obj5.getRenderables() != null)
            {
                for(int i=0;i<obj5.getRenderables().size();i++)
                {
                    KMLRenderable obj6 = (KMLRenderable)obj5.getRenderables().get(i);
                    if(Settings.bDebug)  System.out.println("obj6: "+obj6+", "+obj6.getClass());
                    goto_object_renderable(obj6);
                }
            }
        }
        else// created online
        {
            goto_object_renderable(obj);
        }
    }
    
    void simulate_object_renderable(Object obj)
    {
        Object obj6 = obj;
        if(obj6 instanceof gov.nasa.worldwind.render.PointPlacemark)
        {
            gov.nasa.worldwind.render.PointPlacemark obj7 = (gov.nasa.worldwind.render.PointPlacemark)obj6;
            if(Settings.bDebug)  System.out.println("obj7: "+obj7+", "+obj7.getLabelText());

            goTo(wwd,obj7.getPosition());
            wwd.redraw();
            return;
        }

        if(obj6 instanceof gov.nasa.worldwind.render.Path)
        {
            gov.nasa.worldwind.render.Path obj7 = (gov.nasa.worldwind.render.Path)obj6;
            if(Settings.bDebug)  System.out.println("obj7: "+obj7+", "+obj7.getStringValue(AVKey.DISPLAY_NAME));

            animatePath(obj7);
            wwd.redraw();
            return;
        }

        if(obj6 instanceof gov.nasa.worldwind.render.Polygon)
        {
            gov.nasa.worldwind.render.Polygon obj7 = (gov.nasa.worldwind.render.Polygon)obj6;
            if(Settings.bDebug)  System.out.println("obj7: "+obj7+", "+obj7.getStringValue(AVKey.DISPLAY_NAME));

//            animatePath(obj7);
            wwd.redraw();
            return;
        }
            
        if(obj6 instanceof KMLFeatureTreeNode)
        {
            KMLFeatureTreeNode obj7 = (KMLFeatureTreeNode)obj6;
            if(Settings.bDebug)  System.out.println("obj7: "+obj7+", "+obj7.getText());

            Object obj8 = obj7.getValue(AVKey.CONTEXT);
            simulate_object(obj8);
            return;
        }
            
        if(obj6 instanceof KMLLayerTreeNode)
        {
            KMLLayerTreeNode obj7 = (KMLLayerTreeNode)obj6;
            if(Settings.bDebug)  System.out.println("obj7: "+obj7+", "+obj7.getText());

            Object obj8 = obj7.getValue(AVKey.CONTEXT);
            simulate_object(obj8);
            return;
        }
    }
    
    void simulate_object(Object obj)
    {
        if(obj instanceof KMLPlacemark)// loaded from file
        {
            KMLPlacemark obj5 = (KMLPlacemark)obj;
            if(obj5.getRenderables() != null)
            {
                for(int i=0;i<obj5.getRenderables().size();i++)
                {
                    KMLRenderable obj6 = (KMLRenderable)obj5.getRenderables().get(i);
                    if(Settings.bDebug)  System.out.println("obj6: "+obj6+", "+obj6.getClass());
                    simulate_object_renderable(obj6);
                }
            }
        }
        else// created online
        {
            simulate_object_renderable(obj);
        }
    }
    
    void set_object_cursor_renderable(Object obj)
    {
        Object obj6 = obj;

//        if(obj6 instanceof PickedObject)
//        {
//            PickedObject obj7 = (PickedObject)obj6;
//            Object obj8 = obj7.getObject();
//            if(!(obj8 instanceof Position))
//            {
//                wwd.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
//            }
//            return;
//        }
        
        if(obj6 instanceof KMLGroundOverlay)
        {
            wwd.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            return;
        }

        if(obj6 instanceof gov.nasa.worldwind.render.PointPlacemark)
        {
            wwd.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            return;
        }

        if(obj6 instanceof gov.nasa.worldwind.render.Path)
        {
            wwd.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            return;
        }

        if(obj6 instanceof gov.nasa.worldwind.render.Polygon)
        {
            wwd.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            return;
        }

//            if(obj6 instanceof KMLPointPlacemarkImpl)
//            {
//            wwd.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
//                return;
//            }

//            if(obj6 instanceof KMLLineStringPlacemarkImpl)
//            {
//            wwd.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
//                return;
//            }

        if(obj6 instanceof KMLGroundOverlayPolygonImpl)
        {
            wwd.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            return;
        }

        if(obj6 instanceof KMLPolygonImpl)
        {
            wwd.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            return;
        }

        if(obj6 instanceof KMLSurfaceImageImpl)
        {
            wwd.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            return;
        }

        if(obj6 instanceof KMLSurfacePolygonImpl)
        {
            wwd.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            return;
        }

        if(obj6 instanceof KMLExtrudedPolygonImpl)
        {
            wwd.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            return;
        }
            
        if(obj6 instanceof KMLFeatureTreeNode)
        {
            KMLFeatureTreeNode obj7 = (KMLFeatureTreeNode)obj;
            if(Settings.bDebug)  System.out.println("obj7: "+obj7+", "+obj7.getText());

            Object obj8 = obj7.getValue(AVKey.CONTEXT);
            set_object_cursor(obj8);
            return;
        }
            
        if(obj6 instanceof KMLLayerTreeNode)
        {
            KMLLayerTreeNode obj7 = (KMLLayerTreeNode)obj;
            if(Settings.bDebug)  System.out.println("obj7: "+obj7+", "+obj7.getText());

            Object obj8 = obj7.getValue(AVKey.CONTEXT);
            set_object_cursor(obj8);
            return;
        }

        wwd.setCursor(Cursor.getDefaultCursor());
    }
    
    void set_object_cursor(Object obj)
    {
//AliSoft 2021.07.08        
        if(obj instanceof KMLPlacemark)// loaded from file
        {
            KMLPlacemark obj5 = (KMLPlacemark)obj;
            if(obj5.getRenderables() != null)
            {
                for(int i=0;i<obj5.getRenderables().size();i++)
                {
                    KMLRenderable obj6 = (KMLRenderable)obj5.getRenderables().get(i);
                    if(Settings.bDebug)  System.out.println("obj6: "+obj6+", "+obj6.getClass());
                    set_object_cursor_renderable(obj6);
                }
            }
        }
        else// created online
        {
            set_object_cursor_renderable(obj);
        }
    }
    
    void delete_object_renderable(Object obj)
    {
        if(obj instanceof Renderable)
        {
            Renderable obj7 = (Renderable)obj;
            
//            kmlController
            layer_kml.removeRenderable(obj7);
            wwd.redrawNow();
        }
            
        if(obj instanceof KMLFeatureTreeNode)
        {
            KMLFeatureTreeNode obj7 = (KMLFeatureTreeNode)obj;
            if(Settings.bDebug)  System.out.println("obj7: "+obj7+", "+obj7.getText());

            Object obj8 = obj7.getValue(AVKey.CONTEXT);
            delete_object(obj8);
        }
            
        if(obj instanceof KMLLayerTreeNode)
        {
            KMLLayerTreeNode obj7 = (KMLLayerTreeNode)obj;
            if(Settings.bDebug)  System.out.println("obj7: "+obj7+", "+obj7.getText());

            Object obj8 = obj7.getValue(AVKey.CONTEXT);
            delete_object(obj8);
        }
        
//        Object obj6 = obj;
//        if(obj6 instanceof PointPlacemark)
//        {
//            PointPlacemark obj7 = (PointPlacemark)obj6;
//            if(Settings.bDebug)  System.out.println("obj7: "+obj7+", "+obj7.getLabelText());
//            
//            layer_placemarks.removeRenderable(obj7);
//            wwd.redraw();
//        }
//
//        if(obj6 instanceof gov.nasa.worldwind.render.Path)
//        {
//            gov.nasa.worldwind.render.Path obj7 = (gov.nasa.worldwind.render.Path)obj6;
//            if(Settings.bDebug)  System.out.println("obj7: "+obj7+", "+obj7.getStringValue(AVKey.DISPLAY_NAME));
//
//            layer_placemarks.removeRenderable(obj7);
//            wwd.redraw();
//        }
//
////            if(obj6 instanceof KMLPointPlacemarkImpl)
////            {
////                KMLPointPlacemarkImpl obj7 = (KMLPointPlacemarkImpl)obj6;
////                if(Settings.bDebug)  System.out.println("obj7: "+obj7+", "+obj7.getLabelText());
////
////                goTo(wwd,obj7.getPosition());
////                wwd.redraw();
////            }
//
////            if(obj6 instanceof KMLLineStringPlacemarkImpl)
////            {
////                KMLLineStringPlacemarkImpl obj7 = (KMLLineStringPlacemarkImpl)obj6;
////                if(Settings.bDebug)  System.out.println("obj7: "+obj7);
////
////                goTo(wwd,obj7.getSector());
////                wwd.redraw();
////            }
//
//        if(obj6 instanceof KMLGroundOverlayPolygonImpl)
//        {
//            KMLGroundOverlayPolygonImpl obj7 = (KMLGroundOverlayPolygonImpl)obj6;
//            if(Settings.bDebug)  System.out.println("obj7: "+obj7);
//
//            layer_placemarks.removeRenderable(obj7);
//            wwd.redraw();
//        }
//
//        if(obj6 instanceof KMLPolygonImpl)
//        {
//            KMLPolygonImpl obj7 = (KMLPolygonImpl)obj6;
//            if(Settings.bDebug)  System.out.println("obj7: "+obj7);
//
//            layer_placemarks.removeRenderable(obj7);
//            wwd.redraw();
//        }
//
//        if(obj6 instanceof KMLSurfaceImageImpl)
//        {
//            KMLSurfaceImageImpl obj7 = (KMLSurfaceImageImpl)obj6;
//            if(Settings.bDebug)  System.out.println("obj7: "+obj7);
//
////                                        obj7.setVisible(entry.checked);
////                                        if(bZoom)   goTo(wwd,obj7.getSector());
//            wwd.redraw();
//        }
//
//        if(obj6 instanceof KMLSurfacePolygonImpl)
//        {
//            KMLSurfacePolygonImpl obj7 = (KMLSurfacePolygonImpl)obj6;
//            if(Settings.bDebug)  System.out.println("obj7: "+obj7);
//
//            layer_placemarks.removeRenderable(obj7);
//            wwd.redraw();
//        }
//
//        if(obj6 instanceof KMLExtrudedPolygonImpl)
//        {
//            KMLExtrudedPolygonImpl obj7 = (KMLExtrudedPolygonImpl)obj6;
//            if(Settings.bDebug)  System.out.println("obj7: "+obj7);
//
//            layer_placemarks.removeRenderable(obj7);
//            wwd.redraw();
//        }
//
//        if(obj6 instanceof KMLGroundOverlay)
//        {
//            KMLGroundOverlay obj7 = (KMLGroundOverlay)obj6;
//            if(Settings.bDebug)  System.out.println("obj7: "+obj7);
//
////                                        obj7.setVisible(entry.checked);
////                                        if(bZoom)   goTo(wwd,obj7.getSector());
//            wwd.redraw();
//        }
    }
    
    void delete_object(Object obj)
    {

//        mv_TreeNode node = treeLayout.getCurrTreeNode();
//        if(node != null)
//        {
//            layerTree.getModel().getRoot().removeChild(node);
//            treeLayout.invalidate();
//        }
        if(obj instanceof KMLPlacemark)
        {
            KMLPlacemark obj5 = (KMLPlacemark)obj;
            if(obj5.getRenderables() != null)
            {
                obj5.getRenderables().clear();
                wwd.redraw();
            }
        }
        else
        {
//            delete_object_renderable(obj);
        }

        if(currObject instanceof KMLFeatureTreeNode)
        {
            KMLFeatureTreeNode featureNode = (KMLFeatureTreeNode)currObject;
            TreeNode parent = featureNode.getParent();
            if(parent != null)
                parent.removeChild(featureNode);
            else
                layerTree.getModel().getRoot().removeChild(featureNode);
            treeLayout.invalidate();
            if(Settings.bDebug)  System.out.println("featureNode: "+featureNode.getText()+", "+featureNode.getPath().toString());
        }
        else
        if(currObject instanceof KMLLayerTreeNode)
        {
            KMLLayerTreeNode featureNode = (KMLLayerTreeNode)currObject;
            TreeNode parent = featureNode.getParent();
            if(parent != null)
                parent.removeChild(featureNode);
            else
                layerTree.getModel().getRoot().removeChild(featureNode);
            treeLayout.invalidate();
            if(Settings.bDebug)  System.out.println("featureNode: "+featureNode.getText()+", "+featureNode.getPath().toString());
        }
        else
        if(currObject instanceof KMLPointPlacemarkImpl)
        {
            KMLPointPlacemarkImpl feature = (KMLPointPlacemarkImpl)currObject;
            if(feature != null)
            {
                KMLFeatureTreeNode featureNode = (KMLFeatureTreeNode)feature.getValue("oghab.mapviewer.avKey.tree_node");
                if(featureNode != null)
                {
                    TreeNode parent = featureNode.getParent();
                    if(parent != null)
                        parent.removeChild(featureNode);
                    else
                        layerTree.getModel().getRoot().removeChild(featureNode);
                    treeLayout.invalidate();
                    if(Settings.bDebug)  System.out.println("featureNode: "+featureNode.getText()+", "+featureNode.getPath().toString());
                }
            }
        }
        else
        if(currObject instanceof WWObjectImpl)
        {
            WWObjectImpl feature = (WWObjectImpl)currObject;
            if(feature != null)
            {
                KMLFeatureTreeNode featureNode = (KMLFeatureTreeNode)feature.getValue("oghab.mapviewer.avKey.tree_node");
                if(featureNode != null)
                {
                    TreeNode parent = featureNode.getParent();
                    if(parent != null)
                        parent.removeChild(featureNode);
                    else
                        layerTree.getModel().getRoot().removeChild(featureNode);
                    treeLayout.invalidate();
                    if(Settings.bDebug)  System.out.println("featureNode: "+featureNode.getText()+", "+featureNode.getPath().toString());
                }
            }
        }
    }
    
    void copy_object() throws XMLStreamException, IOException
    {
        // Create a StringWriter to collect KML in a string buffer
        Writer stringWriter = new StringWriter();

        // Create a document builder that will write KML to the StringWriter
        KMLDocumentBuilder kmlBuilder = new KMLDocumentBuilder(stringWriter);
        
        TreeNode node = treeLayout.getCurrTreeNode();
        is_first_folder = true;
        n_folder_counter = 0;
        scanChildren(node, kmlBuilder);
        
        kmlBuilder.close();
        
        String myString = stringWriter.toString();
        StringSelection stringSelection = new StringSelection(myString);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);        
        
        FileWriter fw;
        try {
            String strFilename = Paths.get(strDataPath, "kml", "copy.kml").toString();
            fw = new FileWriter(strFilename);
            fw.write(myString);
            fw.close();
        } catch (IOException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    void set_object_visibility_renderable(KMLPlacemark obj5, Object obj6, boolean bVisible, boolean bUpdate)
    {
        if(obj6 instanceof KMLTacticalSymbolPlacemarkImpl)
        {
            KMLTacticalSymbolPlacemarkImpl obj7 = (KMLTacticalSymbolPlacemarkImpl)obj6;
            if(Settings.bDebug)  System.out.println("obj7: "+obj7+", "+obj7.getIdentifier());

            obj7.setVisible(bVisible);
            if(obj5 != null)    obj5.setVisibility(bVisible);
            if(bUpdate) wwd.redrawNow();
        }

        if(obj6 instanceof PointPlacemark)
        {
            PointPlacemark obj7 = (PointPlacemark)obj6;
            if(Settings.bDebug)  System.out.println("obj7: "+obj7+", "+obj7.getLabelText());

            obj7.setVisible(bVisible);
            if(obj5 != null)    obj5.setVisibility(bVisible);
            if(bUpdate) wwd.redrawNow();
        }

        if(obj6 instanceof gov.nasa.worldwind.render.Path)
        {
            gov.nasa.worldwind.render.Path obj7 = (gov.nasa.worldwind.render.Path)obj6;
            if(Settings.bDebug)  System.out.println("obj7: "+obj7+", "+obj7.getStringValue(AVKey.DISPLAY_NAME));

            obj7.setVisible(bVisible);
            if(obj5 != null)    obj5.setVisibility(bVisible);
            if(bUpdate) wwd.redraw();
        }
        
//        if(obj6 instanceof KMLPointPlacemarkImpl)
//        {
//            KMLPointPlacemarkImpl obj7 = (KMLPointPlacemarkImpl)obj6;
//            if(Settings.bDebug)  System.out.println("obj7: "+obj7+", "+obj7.getLabelText());
//
//            obj7.setVisible(bVisible);
//            if(obj5 != null)    obj5.setVisibility(bVisible);
//            if(bUpdate) wwd.redrawNow();
//        }

//        if(obj6 instanceof KMLLineStringPlacemarkImpl)
//        {
//            KMLLineStringPlacemarkImpl obj7 = (KMLLineStringPlacemarkImpl)obj6;
//            if(Settings.bDebug)  System.out.println("obj7: "+obj7);
//
//            obj7.setVisible(bVisible);
//            if(obj5 != null)    obj5.setVisibility(bVisible);
//            if(bUpdate) wwd.redraw();
//        }

        if(obj6 instanceof KMLGroundOverlayPolygonImpl)
        {
            KMLGroundOverlayPolygonImpl obj7 = (KMLGroundOverlayPolygonImpl)obj6;
            if(Settings.bDebug)  System.out.println("obj7: "+obj7);

            obj7.setVisible(bVisible);
            if(obj5 != null)    obj5.setVisibility(bVisible);
            if(bUpdate) wwd.redraw();
        }

        if(obj6 instanceof KMLPolygonImpl)
        {
            KMLPolygonImpl obj7 = (KMLPolygonImpl)obj6;
            if(Settings.bDebug)  System.out.println("obj7: "+obj7);

            obj7.setVisible(bVisible);
            if(obj5 != null)    obj5.setVisibility(bVisible);
            if(bUpdate) wwd.redraw();
        }

        if(obj6 instanceof KMLSurfaceImageImpl)
        {
            KMLSurfaceImageImpl obj7 = (KMLSurfaceImageImpl)obj6;
            if(Settings.bDebug)  System.out.println("obj7: "+obj7);

//                obj7.setVisible(bVisible);
            if(obj5 != null)    obj5.setVisibility(bVisible);
            if(bUpdate) wwd.redraw();
        }

        if(obj6 instanceof KMLSurfacePolygonImpl)
        {
            KMLSurfacePolygonImpl obj7 = (KMLSurfacePolygonImpl)obj6;
            if(Settings.bDebug)  System.out.println("obj7: "+obj7);

            obj7.setVisible(bVisible);
            if(obj5 != null)    obj5.setVisibility(bVisible);
            if(bUpdate) wwd.redraw();
        }

        if(obj6 instanceof KMLExtrudedPolygonImpl)
        {
            KMLExtrudedPolygonImpl obj7 = (KMLExtrudedPolygonImpl)obj6;
            if(Settings.bDebug)  System.out.println("obj7: "+obj7);

            obj7.setVisible(bVisible);
            if(obj5 != null)    obj5.setVisibility(bVisible);
            if(bUpdate) wwd.redraw();
        }

        if(obj6 instanceof KMLGroundOverlay)
        {
            KMLGroundOverlay obj7 = (KMLGroundOverlay)obj6;
            if(Settings.bDebug)  System.out.println("obj7: "+obj7);

//                obj7.setVisible(bVisible);
            if(obj5 != null)    obj5.setVisibility(bVisible);
            if(bUpdate) wwd.redraw();
        }
            
        if(obj6 instanceof KMLFeatureTreeNode)
        {
            KMLFeatureTreeNode obj7 = (KMLFeatureTreeNode)obj6;
            if(Settings.bDebug)  System.out.println("obj7: "+obj7+", "+obj7.getText());

            Object obj8 = obj7.getValue(AVKey.CONTEXT);
            set_object_visibility(obj8, bVisible, bUpdate);
        }
            
        if(obj6 instanceof KMLLayerTreeNode)
        {
            KMLLayerTreeNode obj7 = (KMLLayerTreeNode)obj6;
            if(Settings.bDebug)  System.out.println("obj7: "+obj7+", "+obj7.getText());

            Object obj8 = obj7.getValue(AVKey.CONTEXT);
            set_object_visibility(obj8, bVisible, bUpdate);
        }
    }
    
    void set_object_visibility(Object obj, boolean bVisible, boolean bUpdate)
    {
        if(obj instanceof KMLPlacemark)
        {
            KMLPlacemark obj5 = (KMLPlacemark)obj;
            if(obj5.getRenderables() != null)
            {
                for(int i=0;i<obj5.getRenderables().size();i++)
                {
                    KMLRenderable obj6 = (KMLRenderable)obj5.getRenderables().get(i);
                    if(Settings.bDebug)  System.out.println("obj6: "+obj6+", "+obj6.getClass());
                    set_object_visibility_renderable(obj5,obj6, bVisible, bUpdate);
                }
            }
        }
        else
        {
            set_object_visibility_renderable(null, obj, bVisible, bUpdate);
        }
    }
    
    void show_popup_menu(Object obj, int x, int y)
    {
//        if(!(obj instanceof KMLPlacemark))    return;
//        KMLPlacemark obj5 = (KMLPlacemark)obj;
//        for(int i=0;i<obj5.getRenderables().size();i++)
//        {
//            KMLRenderable obj6 = (KMLRenderable)obj;
            Object obj6 = obj;
//            KMLRenderable obj6 = obj5.getRenderables().get(i);
            if(Settings.bDebug)  System.out.println("obj6: "+obj6+", "+obj6.getClass());

            if(obj6 instanceof KMLTacticalSymbolPlacemarkImpl)
            {
                KMLTacticalSymbolPlacemarkImpl obj7 = (KMLTacticalSymbolPlacemarkImpl)obj6;
                if(Settings.bDebug)  System.out.println("obj7: "+obj7+", "+obj7.getIdentifier());

                PopupMenuEdit.show(jPanel2 , x, y);
                return;
            }

            if(obj6 instanceof PointPlacemark)
            {
                PointPlacemark obj7 = (PointPlacemark)obj6;
                if(Settings.bDebug)  System.out.println("obj7: "+obj7+", "+obj7.getLabelText());

                PopupMenuEdit.show(jPanel2 , x, y);
                return;
            }

            if(obj6 instanceof gov.nasa.worldwind.render.Path)
            {
                gov.nasa.worldwind.render.Path obj7 = (gov.nasa.worldwind.render.Path)obj6;
                if(Settings.bDebug)  System.out.println("obj7: "+obj7+", "+obj7.getStringValue(AVKey.DISPLAY_NAME));

                PopupMenuEdit.show(jPanel2 , x, y);
                return;
            }

            if(obj6 instanceof gov.nasa.worldwind.render.Polygon)
            {
                gov.nasa.worldwind.render.Polygon obj7 = (gov.nasa.worldwind.render.Polygon)obj6;
                if(Settings.bDebug)  System.out.println("obj7: "+obj7+", "+obj7.getStringValue(AVKey.DISPLAY_NAME));

                PopupMenuEdit.show(jPanel2 , x, y);
                return;
            }

//            if(obj6 instanceof KMLPointPlacemarkImpl)
//            {
//                KMLPointPlacemarkImpl obj7 = (KMLPointPlacemarkImpl)obj6;
//                if(Settings.bDebug)  System.out.println("obj7: "+obj7+", "+obj7.getLabelText());
//
//                PopupMenuEdit.show(jPanel2 , x, y);
//                return;
//            }

//            if(obj6 instanceof KMLLineStringPlacemarkImpl)
//            {
//                KMLLineStringPlacemarkImpl obj7 = (KMLLineStringPlacemarkImpl)obj6;
//                if(Settings.bDebug)  System.out.println("obj7: "+obj7);
//
//                PopupMenuEdit.show(jPanel2 , x, y);
//                return;
//            }

            if(obj6 instanceof KMLGroundOverlayPolygonImpl)
            {
                KMLGroundOverlayPolygonImpl obj7 = (KMLGroundOverlayPolygonImpl)obj6;
                if(Settings.bDebug)  System.out.println("obj7: "+obj7);

                PopupMenuEdit.show(jPanel2 , x, y);
                return;
            }

            if(obj6 instanceof KMLPolygonImpl)
            {
                KMLPolygonImpl obj7 = (KMLPolygonImpl)obj6;
                if(Settings.bDebug)  System.out.println("obj7: "+obj7);

                PopupMenuEdit.show(jPanel2 , x, y);
                return;
            }

            if(obj6 instanceof KMLSurfaceImageImpl)
            {
                KMLSurfaceImageImpl obj7 = (KMLSurfaceImageImpl)obj6;
                if(Settings.bDebug)  System.out.println("obj7: "+obj7);

                PopupMenuEdit.show(jPanel2 , x, y);
                return;
            }

            if(obj6 instanceof KMLSurfacePolygonImpl)
            {
                KMLSurfacePolygonImpl obj7 = (KMLSurfacePolygonImpl)obj6;
                if(Settings.bDebug)  System.out.println("obj7: "+obj7);

                PopupMenuEdit.show(jPanel2 , x, y);
                return;
            }

            if(obj6 instanceof KMLExtrudedPolygonImpl)
            {
                KMLExtrudedPolygonImpl obj7 = (KMLExtrudedPolygonImpl)obj6;
                if(Settings.bDebug)  System.out.println("obj7: "+obj7);

                PopupMenuEdit.show(jPanel2 , x, y);
                return;
            }

            if(obj6 instanceof KMLGroundOverlay)
            {
                KMLGroundOverlay obj7 = (KMLGroundOverlay)obj6;
                if(Settings.bDebug)  System.out.println("obj7: "+obj7);

                PopupMenuEdit.show(jPanel2 , x, y);
                return;
            }

            if(obj6 instanceof KMLPlacemark)
            {
                KMLPlacemark obj7 = (KMLPlacemark)obj6;
                if(Settings.bDebug)  System.out.println("obj7: "+obj7+", "+obj7.getName());

                PopupMenuEdit.show(jPanel2 , x, y);
                return;
            }
            
            if(obj6 instanceof KMLFeatureTreeNode)
            {
                KMLFeatureTreeNode obj7 = (KMLFeatureTreeNode)obj6;
                if(Settings.bDebug)  System.out.println("obj7: "+obj7+", "+obj7.getText());

                PopupMenuEdit.show(jPanel2 , x, y);
                return;
            }
            
            if(obj6 instanceof KMLLayerTreeNode)
            {
                KMLLayerTreeNode obj7 = (KMLLayerTreeNode)obj6;
                if(Settings.bDebug)  System.out.println("obj7: "+obj7+", "+obj7.getText());

                PopupMenuEdit.show(jPanel2 , x, y);
                return;
            }
//        }
    }

    // Measurement properties.
    protected AreaMeasurer areaMeasurer;
    public AreaMeasurer setupAreaMeasurer(Globe globe, Iterable<? extends LatLon> locations)
    {
        if (globe == null)
        {
            String message = Logging.getMessage("nullValue.GlobeIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        if (this.areaMeasurer == null)
        {
            this.areaMeasurer = new AreaMeasurer();
        }

        // The AreaMeasurer requires an ArrayList reference, but SurfaceShapes use an opaque iterable. Copy the
        // iterable contents into an ArrayList to satisfy AreaMeasurer without compromising the generality of the
        // shape's iterator.
        ArrayList<LatLon> arrayList = new ArrayList<LatLon>();

        if (locations != null)
        {
            for (LatLon ll : locations)
            {
                arrayList.add(ll);
            }

            if (arrayList.size() > 1 && !arrayList.get(0).equals(arrayList.get(arrayList.size() - 1)))
                arrayList.add(arrayList.get(0));
        }

        this.areaMeasurer.setPositions(arrayList, 0);

        // Surface shapes follow the terrain by definition.
        this.areaMeasurer.setFollowTerrain(true);

        return this.areaMeasurer;
    }

    public double getArea(Globe globe, boolean terrainConformant)
    {
        if (globe == null)
        {
            String message = Logging.getMessage("nullValue.GlobeIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        areaMeasurer.setFollowTerrain(terrainConformant);
        return areaMeasurer.getArea(globe);
    }

    public double getPerimeter(Globe globe, boolean terrainConformant)
    {
        if (globe == null)
        {
            String message = Logging.getMessage("nullValue.GlobeIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        areaMeasurer.setFollowTerrain(terrainConformant);
        return areaMeasurer.getPerimeter(globe);
    }
    
    void show_object_properties_renderable(KMLPlacemark obj5, Object obj)
    {
        Object obj6 = obj;
        propertiesFrame.TP_Properties.setSelectedIndex(0);

        if(obj6 instanceof KMLTacticalSymbolPlacemarkImpl)
        {
            MainFrame.bUpdateEdit = false;
            KMLTacticalSymbolPlacemarkImpl obj7 = (KMLTacticalSymbolPlacemarkImpl)obj6;
            if(Settings.bDebug)  System.out.println("obj7: "+obj7);
            
            boolean bVisible = true;
            propertiesFrame.is_tactical_symbols = true;
            propertiesFrame.B_Icons.setVisible(false);
            propertiesFrame.L_Latitude.setVisible(bVisible);
            propertiesFrame.TF_Latitude.setVisible(bVisible);
            propertiesFrame.L_Longitude.setVisible(bVisible);
            propertiesFrame.TF_Longitude.setVisible(bVisible);
            propertiesFrame.L_IconPath.setVisible(bVisible);
            propertiesFrame.TF_IconPath.setVisible(bVisible);
            
            propertiesFrame.P_Lines.setVisible(!bVisible);
            propertiesFrame.P_Area.setVisible(!bVisible);
            propertiesFrame.P_Label.setVisible(bVisible);
            propertiesFrame.P_Icon.setVisible(bVisible);
            
            propertiesFrame.TP_Properties.setEnabledAt(3, false);
            propertiesFrame.TP_Properties.setEnabledAt(4, true);
            
            propertiesFrame.pack();

            propertiesFrame.TF_Name.setText(obj7.getStringValue(AVKey.DISPLAY_NAME));

            Position pos = obj7.getPosition();
            propertiesFrame.show_pos(pos.getLongitude().degrees, pos.getLatitude().degrees);
            propertiesFrame.TA_Description.setText((String)obj7.getValue(AVKey.DESCRIPTION));
            propertiesFrame.TF_IconPath.setText(obj7.getIdentifier());
//            if(obj7.getAttributes() != null)
//            {
//                propertiesFrame.TF_IconPath.setText(obj7.getAttributes().getImageAddress());
//                if(obj7.getAttributes().getLabelMaterial() != null)
//                {
//                    propertiesFrame.P_LabelColor.setBackground(obj7.getAttributes().getLabelMaterial().getDiffuse());
//                    propertiesFrame.S_LabelOpacity.setValue(Math.round(100.0*(obj7.getAttributes().getLabelMaterial().getDiffuse().getAlpha()/255.0)));
//                }
//                else
//                {
//                    propertiesFrame.P_LabelColor.setBackground(Color.WHITE);
//                    propertiesFrame.S_LabelOpacity.setValue(100.0);
//                }
//                if(obj7.getAttributes().getLabelScale() != null)
//                {
//                    propertiesFrame.S_LabelScale.setValue(obj7.getAttributes().getLabelScale());
//                }
//                else
//                {
//                    propertiesFrame.S_LabelScale.setValue(1.0);
//                }
//
//                if(obj7.getAttributes().getImageColor() != null)
//                {
//                    propertiesFrame.P_IconColor.setBackground(obj7.getAttributes().getImageColor());
//                    propertiesFrame.S_IconOpacity.setValue(Math.round(100.0*(obj7.getAttributes().getImageColor().getAlpha()/255.0)));
//                }
//                else
//                {
//                    propertiesFrame.P_IconColor.setBackground(Color.WHITE);
//                    propertiesFrame.S_IconOpacity.setValue(100.0);
//                }
//                propertiesFrame.S_IconScale.setValue(obj7.getAttributes().getScale());
//            }
            
            propertiesFrame.alt = obj7.getPosition().getAltitude();
            propertiesFrame.CB_AltitudeMode.setSelectedIndex(obj7.getAltitudeMode());
//            propertiesFrame.CB_Extend.setSelected(obj7.isLineEnabled());

            propertiesFrame.setVisible(true);
            MainFrame.bUpdateEdit = true;
            return;
        }

        if(obj6 instanceof gov.nasa.worldwind.render.PointPlacemark)
        {
            MainFrame.bUpdateEdit = false;
            gov.nasa.worldwind.render.PointPlacemark obj7 = (gov.nasa.worldwind.render.PointPlacemark)obj6;
            if(Settings.bDebug)  System.out.println("obj7: "+obj7);
            
            boolean bVisible = true;
            propertiesFrame.is_tactical_symbols = false;
            propertiesFrame.B_Icons.setVisible(true);
            propertiesFrame.L_Latitude.setVisible(bVisible);
            propertiesFrame.TF_Latitude.setVisible(bVisible);
            propertiesFrame.L_Longitude.setVisible(bVisible);
            propertiesFrame.TF_Longitude.setVisible(bVisible);
            propertiesFrame.L_IconPath.setVisible(bVisible);
            propertiesFrame.TF_IconPath.setVisible(bVisible);
            
            propertiesFrame.P_Lines.setVisible(!bVisible);
            propertiesFrame.P_Area.setVisible(!bVisible);
            propertiesFrame.P_Label.setVisible(bVisible);
            propertiesFrame.P_Icon.setVisible(bVisible);
            
            propertiesFrame.TP_Properties.setEnabledAt(3, false);
            propertiesFrame.TP_Properties.setEnabledAt(4, true);
            
            propertiesFrame.pack();

            propertiesFrame.TF_Name.setText(obj7.getStringValue(AVKey.DISPLAY_NAME));

            Position pos = obj7.getPosition();
            propertiesFrame.show_pos(pos.getLongitude().degrees, pos.getLatitude().degrees);
            propertiesFrame.TA_Description.setText((String)obj7.getValue(AVKey.DESCRIPTION));
            if(obj7.getAttributes() != null)
            {
                propertiesFrame.TF_IconPath.setText(obj7.getAttributes().getImageAddress());
                if(obj7.getAttributes().getLabelMaterial() != null)
                {
                    propertiesFrame.P_LabelColor.setBackground(obj7.getAttributes().getLabelMaterial().getDiffuse());
                    propertiesFrame.S_LabelOpacity.setValue(Math.round(100.0*(obj7.getAttributes().getLabelMaterial().getDiffuse().getAlpha()/255.0)));
                }
                else
                {
                    propertiesFrame.P_LabelColor.setBackground(Color.WHITE);
                    propertiesFrame.S_LabelOpacity.setValue(100.0);
                }
                if(obj7.getAttributes().getLabelScale() != null)
                {
                    propertiesFrame.S_LabelScale.setValue(obj7.getAttributes().getLabelScale());
                }
                else
                {
                    propertiesFrame.S_LabelScale.setValue(1.0);
                }

                if(obj7.getAttributes().getImageColor() != null)
                {
                    propertiesFrame.P_IconColor.setBackground(obj7.getAttributes().getImageColor());
                    propertiesFrame.S_IconOpacity.setValue(Math.round(100.0*(obj7.getAttributes().getImageColor().getAlpha()/255.0)));
                }
                else
                {
                    propertiesFrame.P_IconColor.setBackground(Color.WHITE);
                    propertiesFrame.S_IconOpacity.setValue(100.0);
                }
                propertiesFrame.S_IconScale.setValue(obj7.getAttributes().getScale());
            }
            
            propertiesFrame.alt = obj7.getPosition().getAltitude();
            int i = obj7.getAltitudeMode();
            propertiesFrame.CB_AltitudeMode.setSelectedIndex(obj7.getAltitudeMode());
            propertiesFrame.CB_Extend.setSelected(obj7.isLineEnabled());

            propertiesFrame.setVisible(true);
            MainFrame.bUpdateEdit = true;
            return;
        }

        if(obj6 instanceof gov.nasa.worldwind.render.Path)
        {
            MainFrame.bUpdateEdit = false;
            gov.nasa.worldwind.render.Path obj7 = (gov.nasa.worldwind.render.Path)obj6;
            if(Settings.bDebug)  System.out.println("obj7: "+obj7);
            
            boolean bVisible = false;
            propertiesFrame.is_tactical_symbols = false;
            propertiesFrame.B_Icons.setVisible(true);
            propertiesFrame.L_Latitude.setVisible(bVisible);
            propertiesFrame.TF_Latitude.setVisible(bVisible);
            propertiesFrame.L_Longitude.setVisible(bVisible);
            propertiesFrame.TF_Longitude.setVisible(bVisible);
            propertiesFrame.L_IconPath.setVisible(bVisible);
            propertiesFrame.TF_IconPath.setVisible(bVisible);
            
            propertiesFrame.P_Lines.setVisible(!bVisible);
            propertiesFrame.P_Area.setVisible(bVisible);
            propertiesFrame.P_Label.setVisible(bVisible);
            propertiesFrame.P_Icon.setVisible(bVisible);
            
            propertiesFrame.TP_Properties.setEnabledAt(3, true);
            propertiesFrame.TP_Properties.setEnabledAt(4, false);
            propertiesFrame.T_Area.setVisible(bVisible);
            propertiesFrame.L_Area.setVisible(bVisible);
            propertiesFrame.CB_AreaUnits.setVisible(bVisible);

            propertiesFrame.pack();

            propertiesFrame.TF_Name.setText(obj7.getStringValue(AVKey.DISPLAY_NAME));
            
            propertiesFrame.P_LinesColor.setBackground(obj7.getAttributes().getOutlineMaterial().getDiffuse());
            propertiesFrame.S_LinesOpacity.setValue(Math.round(100.0*(obj7.getAttributes().getOutlineMaterial().getDiffuse().getAlpha()/255.0)));
            propertiesFrame.S_LinesWidth.setValue(obj7.getAttributes().getOutlineWidth());

            propertiesFrame.TA_Description.setText((String)obj7.getValue(AVKey.DESCRIPTION));
            
            propertiesFrame.perimeter = obj7.getLength(wwd.getModel().getGlobe());
            propertiesFrame.L_Perimeter.setText(String.format("%.3f" , propertiesFrame.perimeter));
            
            double alt = 0.0;
            if(obj7.getPositions() != null)
            {
                double n = 0;
                for(Position pos:obj7.getPositions())
                {
                    alt += pos.getAltitude();
                    n++;
                }
                if(n > 0)   alt /= n;
            }
            propertiesFrame.alt = alt;
            
            propertiesFrame.CB_AltitudeMode.setSelectedIndex(obj7.getAltitudeMode());
            propertiesFrame.CB_Extend.setSelected(obj7.isExtrude());

            propertiesFrame.setVisible(true);
            MainFrame.bUpdateEdit = true;
            return;
        }

        if(obj6 instanceof gov.nasa.worldwind.render.SurfacePolygon)
        {
            MainFrame.bUpdateEdit = false;
            gov.nasa.worldwind.render.SurfacePolygon obj7 = (gov.nasa.worldwind.render.SurfacePolygon)obj6;
            if(Settings.bDebug)  System.out.println("obj7: "+obj7);
            
            boolean bVisible = false;
            propertiesFrame.is_tactical_symbols = false;
            propertiesFrame.B_Icons.setVisible(true);
            propertiesFrame.L_Latitude.setVisible(bVisible);
            propertiesFrame.TF_Latitude.setVisible(bVisible);
            propertiesFrame.L_Longitude.setVisible(bVisible);
            propertiesFrame.TF_Longitude.setVisible(bVisible);
            propertiesFrame.L_IconPath.setVisible(bVisible);
            propertiesFrame.TF_IconPath.setVisible(bVisible);
            
            propertiesFrame.P_Lines.setVisible(!bVisible);
            propertiesFrame.P_Area.setVisible(!bVisible);
            propertiesFrame.P_Label.setVisible(bVisible);
            propertiesFrame.P_Icon.setVisible(bVisible);
            
            propertiesFrame.TP_Properties.setEnabledAt(3, true);
            propertiesFrame.TP_Properties.setEnabledAt(4, false);
            propertiesFrame.T_Area.setVisible(!bVisible);
            propertiesFrame.L_Area.setVisible(!bVisible);
            propertiesFrame.CB_AreaUnits.setVisible(!bVisible);

            propertiesFrame.pack();

            propertiesFrame.TF_Name.setText(obj7.getStringValue(AVKey.DISPLAY_NAME));
            
            propertiesFrame.P_LinesColor.setBackground(obj7.getAttributes().getOutlineMaterial().getDiffuse());
            propertiesFrame.S_LinesOpacity.setValue(Math.round(100.0*(obj7.getAttributes().getOutlineMaterial().getDiffuse().getAlpha()/255.0)));
            propertiesFrame.S_LinesWidth.setValue(obj7.getAttributes().getOutlineWidth());
            
            propertiesFrame.P_AreaColor.setBackground(obj7.getAttributes().getInteriorMaterial().getDiffuse());
            propertiesFrame.S_AreaOpacity.setValue(Math.round(100.0*(obj7.getAttributes().getInteriorMaterial().getDiffuse().getAlpha()/255.0)));
            if(obj7.getAttributes().isDrawInterior() && obj7.getAttributes().isDrawOutline())
                propertiesFrame.CB_AreaType.setSelectedIndex(2);
            else
            if(obj7.getAttributes().isDrawOutline())
                propertiesFrame.CB_AreaType.setSelectedIndex(1);
            else
                propertiesFrame.CB_AreaType.setSelectedIndex(0);

            propertiesFrame.TA_Description.setText((String)obj7.getValue(AVKey.DESCRIPTION));
            
            propertiesFrame.perimeter = obj7.getPerimeter(wwd.getModel().getGlobe());
            propertiesFrame.area = obj7.getArea(wwd.getModel().getGlobe());
            propertiesFrame.L_Perimeter.setText(String.format("%.3f" , propertiesFrame.perimeter));
            propertiesFrame.L_Area.setText(String.format("%.3f" , propertiesFrame.area));

            propertiesFrame.setVisible(true);
            MainFrame.bUpdateEdit = true;
            return;
        }

        if(obj6 instanceof gov.nasa.worldwind.render.Polygon)
        {
            MainFrame.bUpdateEdit = false;
            gov.nasa.worldwind.render.Polygon obj7 = (gov.nasa.worldwind.render.Polygon)obj6;
            if(Settings.bDebug)  System.out.println("obj7: "+obj7);
            
            boolean bVisible = false;
            propertiesFrame.is_tactical_symbols = false;
            propertiesFrame.B_Icons.setVisible(true);
            propertiesFrame.L_Latitude.setVisible(bVisible);
            propertiesFrame.TF_Latitude.setVisible(bVisible);
            propertiesFrame.L_Longitude.setVisible(bVisible);
            propertiesFrame.TF_Longitude.setVisible(bVisible);
            propertiesFrame.L_IconPath.setVisible(bVisible);
            propertiesFrame.TF_IconPath.setVisible(bVisible);
            
            propertiesFrame.P_Lines.setVisible(!bVisible);
            propertiesFrame.P_Area.setVisible(!bVisible);
            propertiesFrame.P_Label.setVisible(bVisible);
            propertiesFrame.P_Icon.setVisible(bVisible);
            
            propertiesFrame.TP_Properties.setEnabledAt(3, true);
            propertiesFrame.TP_Properties.setEnabledAt(4, false);
            propertiesFrame.T_Area.setVisible(!bVisible);
            propertiesFrame.L_Area.setVisible(!bVisible);
            propertiesFrame.CB_AreaUnits.setVisible(!bVisible);

            propertiesFrame.pack();

            propertiesFrame.TF_Name.setText(obj7.getStringValue(AVKey.DISPLAY_NAME));
            
            propertiesFrame.P_LinesColor.setBackground(obj7.getAttributes().getOutlineMaterial().getDiffuse());
            propertiesFrame.S_LinesOpacity.setValue(Math.round(100.0*(obj7.getAttributes().getOutlineMaterial().getDiffuse().getAlpha()/255.0)));
            propertiesFrame.S_LinesWidth.setValue(obj7.getAttributes().getOutlineWidth());
            
            propertiesFrame.P_AreaColor.setBackground(obj7.getAttributes().getInteriorMaterial().getDiffuse());
            propertiesFrame.S_AreaOpacity.setValue(Math.round(100.0*(obj7.getAttributes().getInteriorMaterial().getDiffuse().getAlpha()/255.0)));
            if(obj7.getAttributes().isDrawInterior() && obj7.getAttributes().isDrawOutline())
                propertiesFrame.CB_AreaType.setSelectedIndex(2);
            else
            if(obj7.getAttributes().isDrawOutline())
                propertiesFrame.CB_AreaType.setSelectedIndex(1);
            else
                propertiesFrame.CB_AreaType.setSelectedIndex(0);

            propertiesFrame.TA_Description.setText((String)obj7.getValue(AVKey.DESCRIPTION));
            
            setupAreaMeasurer(wwd.getModel().getGlobe(), obj7.getOuterBoundary());
            propertiesFrame.perimeter = getPerimeter(wwd.getModel().getGlobe(), false);
            propertiesFrame.area = getArea(wwd.getModel().getGlobe(), false);
            propertiesFrame.L_Perimeter.setText(String.format("%.3f" , propertiesFrame.perimeter));
            propertiesFrame.L_Area.setText(String.format("%.3f" , propertiesFrame.area));

            propertiesFrame.setVisible(true);
            MainFrame.bUpdateEdit = true;
            return;
        }

        if(obj6 instanceof gov.nasa.worldwind.render.ExtrudedPolygon)
        {
            MainFrame.bUpdateEdit = false;
            gov.nasa.worldwind.render.ExtrudedPolygon obj7 = (gov.nasa.worldwind.render.ExtrudedPolygon)obj6;
            if(Settings.bDebug)  System.out.println("obj7: "+obj7);
            
            boolean bVisible = false;
            propertiesFrame.is_tactical_symbols = false;
            propertiesFrame.B_Icons.setVisible(true);
            propertiesFrame.L_Latitude.setVisible(bVisible);
            propertiesFrame.TF_Latitude.setVisible(bVisible);
            propertiesFrame.L_Longitude.setVisible(bVisible);
            propertiesFrame.TF_Longitude.setVisible(bVisible);
            propertiesFrame.L_IconPath.setVisible(bVisible);
            propertiesFrame.TF_IconPath.setVisible(bVisible);
            
            propertiesFrame.P_Lines.setVisible(!bVisible);
            propertiesFrame.P_Area.setVisible(!bVisible);
            propertiesFrame.P_Label.setVisible(bVisible);
            propertiesFrame.P_Icon.setVisible(bVisible);
            
            propertiesFrame.TP_Properties.setEnabledAt(3, true);
            propertiesFrame.TP_Properties.setEnabledAt(4, false);
            propertiesFrame.T_Area.setVisible(!bVisible);
            propertiesFrame.L_Area.setVisible(!bVisible);
            propertiesFrame.CB_AreaUnits.setVisible(!bVisible);

            propertiesFrame.pack();

            propertiesFrame.TF_Name.setText(obj7.getStringValue(AVKey.DISPLAY_NAME));
            
            propertiesFrame.P_LinesColor.setBackground(obj7.getAttributes().getOutlineMaterial().getDiffuse());
            propertiesFrame.S_LinesOpacity.setValue(Math.round(100.0*(obj7.getAttributes().getOutlineMaterial().getDiffuse().getAlpha()/255.0)));
            propertiesFrame.S_LinesWidth.setValue(obj7.getAttributes().getOutlineWidth());
            
            propertiesFrame.P_AreaColor.setBackground(obj7.getAttributes().getInteriorMaterial().getDiffuse());
            propertiesFrame.S_AreaOpacity.setValue(Math.round(100.0*(obj7.getAttributes().getInteriorMaterial().getDiffuse().getAlpha()/255.0)));
            if(obj7.getAttributes().isDrawInterior() && obj7.getAttributes().isDrawOutline())
                propertiesFrame.CB_AreaType.setSelectedIndex(2);
            else
            if(obj7.getAttributes().isDrawOutline())
                propertiesFrame.CB_AreaType.setSelectedIndex(1);
            else
                propertiesFrame.CB_AreaType.setSelectedIndex(0);

            propertiesFrame.TA_Description.setText((String)obj7.getValue(AVKey.DESCRIPTION));
            
            setupAreaMeasurer(wwd.getModel().getGlobe(), obj7.getOuterBoundary());
            propertiesFrame.perimeter = getPerimeter(wwd.getModel().getGlobe(), false);
            propertiesFrame.area = getArea(wwd.getModel().getGlobe(), false);
            propertiesFrame.L_Perimeter.setText(String.format("%.3f" , propertiesFrame.perimeter));
            propertiesFrame.L_Area.setText(String.format("%.3f" , propertiesFrame.area));

            propertiesFrame.setVisible(true);
            MainFrame.bUpdateEdit = true;
            return;
        }

        if(obj6 instanceof SurfaceImage)
        {
            MainFrame.bUpdateEdit = false;
            SurfaceImage obj7 = (SurfaceImage)obj6;
            if(Settings.bDebug)  System.out.println("obj7: "+obj7);
            
            boolean bVisible = false;
            propertiesFrame.is_tactical_symbols = false;
            propertiesFrame.B_Icons.setVisible(true);
            propertiesFrame.L_Latitude.setVisible(bVisible);
            propertiesFrame.TF_Latitude.setVisible(bVisible);
            propertiesFrame.L_Longitude.setVisible(bVisible);
            propertiesFrame.TF_Longitude.setVisible(bVisible);
            propertiesFrame.L_IconPath.setVisible(bVisible);
            propertiesFrame.TF_IconPath.setVisible(bVisible);
            
            propertiesFrame.P_Lines.setVisible(bVisible);
            propertiesFrame.P_Area.setVisible(bVisible);
            propertiesFrame.P_Label.setVisible(bVisible);
            propertiesFrame.P_Icon.setVisible(bVisible);
            
            propertiesFrame.TP_Properties.setEnabledAt(3, true);
            propertiesFrame.TP_Properties.setEnabledAt(4, false);
            propertiesFrame.T_Area.setVisible(!bVisible);
            propertiesFrame.L_Area.setVisible(!bVisible);
            propertiesFrame.CB_AreaUnits.setVisible(!bVisible);

            propertiesFrame.pack();

            propertiesFrame.TF_Name.setText(obj7.getStringValue(AVKey.DISPLAY_NAME));

            propertiesFrame.setVisible(true);
            MainFrame.bUpdateEdit = true;
            return;
        }

        if(obj6 instanceof KMLGroundOverlay)
        {
            MainFrame.bUpdateEdit = false;
            KMLGroundOverlay obj7 = (KMLGroundOverlay)obj6;
            if(Settings.bDebug)  System.out.println("obj7: "+obj7);
            
            boolean bVisible = false;
            propertiesFrame.is_tactical_symbols = false;
            propertiesFrame.B_Icons.setVisible(true);
            propertiesFrame.L_Latitude.setVisible(bVisible);
            propertiesFrame.TF_Latitude.setVisible(bVisible);
            propertiesFrame.L_Longitude.setVisible(bVisible);
            propertiesFrame.TF_Longitude.setVisible(bVisible);
            propertiesFrame.L_IconPath.setVisible(bVisible);
            propertiesFrame.TF_IconPath.setVisible(bVisible);
            
            propertiesFrame.P_Lines.setVisible(bVisible);
            propertiesFrame.P_Area.setVisible(bVisible);
            propertiesFrame.P_Label.setVisible(bVisible);
            propertiesFrame.P_Icon.setVisible(bVisible);
            
            propertiesFrame.TP_Properties.setEnabledAt(3, true);
            propertiesFrame.TP_Properties.setEnabledAt(4, false);
            propertiesFrame.T_Area.setVisible(!bVisible);
            propertiesFrame.L_Area.setVisible(!bVisible);
            propertiesFrame.CB_AreaUnits.setVisible(!bVisible);

            propertiesFrame.pack();

//            propertiesFrame.TF_Name.setText((String)obj7.getFeature().getField("name"));

            propertiesFrame.setVisible(true);
            MainFrame.bUpdateEdit = true;
            return;
        }
        
        if(obj6 instanceof KMLFeatureTreeNode)
        {
            MainFrame.bUpdateEdit = false;
            KMLFeatureTreeNode obj7 = (KMLFeatureTreeNode)obj6;
            if(Settings.bDebug)  System.out.println("obj7: "+obj7+", "+obj7.getText());
            
            boolean bVisible = false;
            propertiesFrame.is_tactical_symbols = false;
            propertiesFrame.B_Icons.setVisible(true);
            propertiesFrame.L_Latitude.setVisible(bVisible);
            propertiesFrame.TF_Latitude.setVisible(bVisible);
            propertiesFrame.L_Longitude.setVisible(bVisible);
            propertiesFrame.TF_Longitude.setVisible(bVisible);
            propertiesFrame.L_IconPath.setVisible(bVisible);
            propertiesFrame.TF_IconPath.setVisible(bVisible);
            
            propertiesFrame.P_Lines.setVisible(bVisible);
            propertiesFrame.P_Area.setVisible(bVisible);
            propertiesFrame.P_Label.setVisible(bVisible);
            propertiesFrame.P_Icon.setVisible(bVisible);
            
            propertiesFrame.TP_Properties.setEnabledAt(3, true);
            propertiesFrame.TP_Properties.setEnabledAt(4, false);
            propertiesFrame.T_Area.setVisible(!bVisible);
            propertiesFrame.L_Area.setVisible(!bVisible);
            propertiesFrame.CB_AreaUnits.setVisible(!bVisible);

            propertiesFrame.pack();

            propertiesFrame.TF_Name.setText((String)obj7.getFeature().getField("name"));

            Object obj8 = obj7.getValue(AVKey.CONTEXT);
            show_object_properties(obj8);

            propertiesFrame.setVisible(true);
            MainFrame.bUpdateEdit = true;
            return;
        }
        
        if(obj6 instanceof KMLLayerTreeNode)
        {
            MainFrame.bUpdateEdit = false;
            KMLLayerTreeNode obj7 = (KMLLayerTreeNode)obj6;
            if(Settings.bDebug)  System.out.println("obj7: "+obj7+", "+obj7.getText());
            
            boolean bVisible = false;
            propertiesFrame.is_tactical_symbols = false;
            propertiesFrame.B_Icons.setVisible(true);
            propertiesFrame.L_Latitude.setVisible(bVisible);
            propertiesFrame.TF_Latitude.setVisible(bVisible);
            propertiesFrame.L_Longitude.setVisible(bVisible);
            propertiesFrame.TF_Longitude.setVisible(bVisible);
            propertiesFrame.L_IconPath.setVisible(bVisible);
            propertiesFrame.TF_IconPath.setVisible(bVisible);
           
            propertiesFrame.P_Lines.setVisible(bVisible);
            propertiesFrame.P_Area.setVisible(bVisible);
            propertiesFrame.P_Label.setVisible(bVisible);
            propertiesFrame.P_Icon.setVisible(bVisible);
            
            propertiesFrame.TP_Properties.setEnabledAt(3, true);
            propertiesFrame.TP_Properties.setEnabledAt(4, false);
            propertiesFrame.T_Area.setVisible(!bVisible);
            propertiesFrame.L_Area.setVisible(!bVisible);
            propertiesFrame.CB_AreaUnits.setVisible(!bVisible);

            propertiesFrame.pack();

            propertiesFrame.TF_Name.setText((String)obj7.getLayer().getName());

            Object obj8 = obj7.getValue(AVKey.CONTEXT);
            show_object_properties(obj8);

            propertiesFrame.setVisible(true);
            MainFrame.bUpdateEdit = true;
            return;
        }
    }
    
    void show_object_properties(Object obj)
    {
        if(obj instanceof KMLPlacemark)
        {
            KMLPlacemark obj5 = (KMLPlacemark)obj;
            if(obj5.getRenderables() != null)
            {
                for(int i=0;i<obj5.getRenderables().size();i++)
                {
                    KMLRenderable obj6 = (KMLRenderable)obj5.getRenderables().get(i);
                    show_object_properties_renderable(obj5,obj6);
                }
            }
        }
        else
        {
            show_object_properties_renderable(null,obj);
        }
    }
    
    public Color setColorAlpha(Color color, int alpha)
    {
        int red = color.getRed();
        int green = color.getGreen();
        int blue = color.getBlue();

        return new Color(red, green, blue, alpha);
    }

    void set_object_properties_renderable(KMLPlacemark obj5, Object obj)
    {
        Object obj6 = obj;
        
        if(obj6 instanceof KMLTacticalSymbolPlacemarkImpl)
        {
            KMLTacticalSymbolPlacemarkImpl obj7 = (KMLTacticalSymbolPlacemarkImpl)obj6;
            if(Settings.bDebug)  System.out.println("obj7: "+obj7);

            obj7.setValue(AVKey.DISPLAY_NAME, propertiesFrame.TF_Name.getText());

//            obj7.setLabelText(propertiesFrame.TF_Name.getText());
            Angle lat = Angle.fromDegrees(Double.parseDouble(propertiesFrame.TF_Latitude.getText().replace("°", "")));
            Angle lon = Angle.fromDegrees(Double.parseDouble(propertiesFrame.TF_Longitude.getText().replace("°", "")));
            obj7.setPosition(Position.fromDegrees(lat.degrees, lon.degrees));

            obj7.setValue(AVKey.DESCRIPTION, propertiesFrame.TA_Description.getText());
            obj7.setIdentifier(propertiesFrame.TF_IconPath.getText());
            
            KMLExtendedData ext_data = new KMLExtendedData(null);
            KMLData data = new KMLData(null);
            data.setField("name","tactical_symbol_id");
            data.setField("value",propertiesFrame.TF_IconPath.getText());
            ext_data.getData().add(data);
            obj7.parent.setField("ExtendedData",ext_data);

//            if(obj7.getAttributes() != null)
//            {
//                Color label_color = propertiesFrame.P_LabelColor.getBackground();
//                label_color = setColorAlpha(label_color, (int) Math.round(255.0*((Number)propertiesFrame.S_LabelOpacity.getValue()).doubleValue()/100.0));
//                obj7.getAttributes().setLabelMaterial(new Material(label_color));
//                obj7.getAttributes().setLabelScale((double)propertiesFrame.S_LabelScale.getValue());
//
//                Color icon_color = propertiesFrame.P_IconColor.getBackground();
//                icon_color = setColorAlpha(icon_color, (int) Math.round(255.0*((Number)propertiesFrame.S_IconOpacity.getValue()).doubleValue()/100.0));
//                obj7.getAttributes().setImageColor(icon_color);
//                obj7.getAttributes().setImageAddress(propertiesFrame.TF_IconPath.getText());
//                obj7.getAttributes().setScale((double)propertiesFrame.S_IconScale.getValue());
//            }

            if(propertiesFrame.CB_AltitudeMode.getSelectedIndex() != 1)
            {
                double alt = Double.parseDouble(propertiesFrame.TF_Altitude.getText());
                Position pos = obj7.getPosition();
                obj7.setPosition(Position.fromDegrees(pos.getLatitude().degrees, pos.getLongitude().degrees, alt));
            }
            obj7.setAltitudeMode(propertiesFrame.CB_AltitudeMode.getSelectedIndex());
//            obj7.setLineEnabled(propertiesFrame.CB_Extend.isSelected());

            // update tree
            KMLFeatureTreeNode featureNode = (KMLFeatureTreeNode)obj7.getValue("oghab.mapviewer.avKey.tree_node");
            if(featureNode != null)
            {
                featureNode.getFeature().setField("name", propertiesFrame.TF_Name.getText());
                treeLayout.invalidate();
            }

            wwd.redraw();
            return;
        }
        
        if(obj6 instanceof gov.nasa.worldwind.render.PointPlacemark)
        {
            gov.nasa.worldwind.render.PointPlacemark obj7 = (gov.nasa.worldwind.render.PointPlacemark)obj6;
            if(Settings.bDebug)  System.out.println("obj7: "+obj7);

            obj7.setValue(AVKey.DISPLAY_NAME, propertiesFrame.TF_Name.getText());

            obj7.setLabelText(propertiesFrame.TF_Name.getText());
            Angle lat = Angle.fromDegrees(Double.parseDouble(propertiesFrame.TF_Latitude.getText().replace("°", "")));
            Angle lon = Angle.fromDegrees(Double.parseDouble(propertiesFrame.TF_Longitude.getText().replace("°", "")));
            obj7.setPosition(Position.fromDegrees(lat.degrees, lon.degrees));

            obj7.setValue(AVKey.DESCRIPTION, propertiesFrame.TA_Description.getText());

            if(obj7.getAttributes() != null)
            {
                Color label_color = propertiesFrame.P_LabelColor.getBackground();
                label_color = setColorAlpha(label_color, (int) Math.round(255.0*((Number)propertiesFrame.S_LabelOpacity.getValue()).doubleValue()/100.0));
                obj7.getAttributes().setLabelMaterial(new Material(label_color));
                obj7.getAttributes().setLabelScale((double)propertiesFrame.S_LabelScale.getValue());

                Color icon_color = propertiesFrame.P_IconColor.getBackground();
                icon_color = setColorAlpha(icon_color, (int) Math.round(255.0*((Number)propertiesFrame.S_IconOpacity.getValue()).doubleValue()/100.0));
                obj7.getAttributes().setImageColor(icon_color);
                obj7.getAttributes().setImageAddress(propertiesFrame.TF_IconPath.getText());
                obj7.getAttributes().setScale((double)propertiesFrame.S_IconScale.getValue());
            }

            if(propertiesFrame.CB_AltitudeMode.getSelectedIndex() != 1)
            {
                double alt = Double.parseDouble(propertiesFrame.TF_Altitude.getText());
                Position pos = obj7.getPosition();
                obj7.setPosition(Position.fromDegrees(pos.getLatitude().degrees, pos.getLongitude().degrees, alt));
            }
            obj7.setAltitudeMode(propertiesFrame.CB_AltitudeMode.getSelectedIndex());
            obj7.setLineEnabled(propertiesFrame.CB_Extend.isSelected());

            // update tree
            KMLFeatureTreeNode featureNode = (KMLFeatureTreeNode)obj7.getValue("oghab.mapviewer.avKey.tree_node");
            if(featureNode != null)
            {
                featureNode.getFeature().setField("name", propertiesFrame.TF_Name.getText());
                treeLayout.invalidate();
            }

            wwd.redraw();
            return;
        }
        
        if(obj6 instanceof gov.nasa.worldwind.render.Path)
        {
            gov.nasa.worldwind.render.Path obj7 = (gov.nasa.worldwind.render.Path)obj6;
            if(Settings.bDebug)  System.out.println("obj7: "+obj7);

            obj7.setValue(AVKey.DISPLAY_NAME, propertiesFrame.TF_Name.getText());

            Color lines_color = propertiesFrame.P_LinesColor.getBackground();
            lines_color = setColorAlpha(lines_color, (int) Math.round(255.0*(((Number)propertiesFrame.S_LinesOpacity.getValue()).doubleValue()/100.0)));
            obj7.getAttributes().setOutlineMaterial(new Material(lines_color));
            obj7.getAttributes().setOutlineOpacity(((Number)propertiesFrame.S_LinesOpacity.getValue()).doubleValue()/100.0);
            obj7.getAttributes().setOutlineWidth(((Number)propertiesFrame.S_LinesWidth.getValue()).doubleValue());

            obj7.setValue(AVKey.DESCRIPTION, propertiesFrame.TA_Description.getText());

            if(propertiesFrame.CB_AltitudeMode.getSelectedIndex() != 1)
            {
                double alt = Double.parseDouble(propertiesFrame.TF_Altitude.getText());
                ArrayList<Position> poses = new ArrayList<>();
                for(Position pos:obj7.getPositions())
                {
                    poses.add(Position.fromDegrees(pos.getLatitude().degrees, pos.getLongitude().degrees, alt));
                }
                obj7.setPositions(poses);
            }
            obj7.setAltitudeMode(propertiesFrame.CB_AltitudeMode.getSelectedIndex());
            obj7.setExtrude(propertiesFrame.CB_Extend.isSelected());

            // update tree
            KMLFeatureTreeNode featureNode = (KMLFeatureTreeNode)obj7.getValue("oghab.mapviewer.avKey.tree_node");
            if(featureNode != null)
            {
                featureNode.getFeature().setField("name", propertiesFrame.TF_Name.getText());
                treeLayout.invalidate();
            }

            wwd.redraw();
            return;
        }

        if(obj6 instanceof gov.nasa.worldwind.render.SurfacePolygon)
        {
            gov.nasa.worldwind.render.SurfacePolygon obj7 = (gov.nasa.worldwind.render.SurfacePolygon)obj6;
            if(Settings.bDebug)  System.out.println("obj7: "+obj7);

            obj7.setValue(AVKey.DISPLAY_NAME, propertiesFrame.TF_Name.getText());

            Color lines_color = propertiesFrame.P_LinesColor.getBackground();
            lines_color = setColorAlpha(lines_color, (int) Math.round(255.0*(((Number)propertiesFrame.S_LinesOpacity.getValue()).doubleValue()/100.0)));
            obj7.getAttributes().setOutlineMaterial(new Material(lines_color));
            obj7.getAttributes().setOutlineOpacity(((Number)propertiesFrame.S_LinesOpacity.getValue()).doubleValue()/100.0);
            obj7.getAttributes().setOutlineWidth(((Number)propertiesFrame.S_LinesWidth.getValue()).doubleValue());

            Color area_color = propertiesFrame.P_AreaColor.getBackground();
            area_color = setColorAlpha(area_color, (int) Math.round(255.0*(((Number)propertiesFrame.S_AreaOpacity.getValue()).doubleValue()/100.0)));
            obj7.getAttributes().setInteriorMaterial(new Material(area_color));
            obj7.getAttributes().setInteriorOpacity(((Number)propertiesFrame.S_AreaOpacity.getValue()).doubleValue()/100.0);
            if(propertiesFrame.CB_AreaType.getSelectedIndex() == 2)
            {
                obj7.getAttributes().setDrawInterior(true);
                obj7.getAttributes().setDrawOutline(true);
            }
            else
            if(propertiesFrame.CB_AreaType.getSelectedIndex() == 1)
            {
                obj7.getAttributes().setDrawInterior(false);
                obj7.getAttributes().setDrawOutline(true);
            }
            else
            {
                obj7.getAttributes().setDrawInterior(true);
                obj7.getAttributes().setDrawOutline(false);
            }

            obj7.setValue(AVKey.DESCRIPTION, propertiesFrame.TA_Description.getText());

            // update tree
            KMLFeatureTreeNode featureNode = (KMLFeatureTreeNode)obj7.getValue("oghab.mapviewer.avKey.tree_node");
            if(featureNode != null)
            {
                featureNode.getFeature().setField("name", propertiesFrame.TF_Name.getText());
                treeLayout.invalidate();
            }

            wwd.redraw();
            return;
        }

        if(obj6 instanceof gov.nasa.worldwind.render.Polygon)
        {
            gov.nasa.worldwind.render.Polygon obj7 = (gov.nasa.worldwind.render.Polygon)obj6;
            if(Settings.bDebug)  System.out.println("obj7: "+obj7);

            obj7.setValue(AVKey.DISPLAY_NAME, propertiesFrame.TF_Name.getText());

            Color lines_color = propertiesFrame.P_LinesColor.getBackground();
            lines_color = setColorAlpha(lines_color, (int) Math.round(255.0*(((Number)propertiesFrame.S_LinesOpacity.getValue()).doubleValue()/100.0)));
            obj7.getAttributes().setOutlineMaterial(new Material(lines_color));
            obj7.getAttributes().setOutlineOpacity(((Number)propertiesFrame.S_LinesOpacity.getValue()).doubleValue()/100.0);
            obj7.getAttributes().setOutlineWidth(((Number)propertiesFrame.S_LinesWidth.getValue()).doubleValue());

            Color area_color = propertiesFrame.P_AreaColor.getBackground();
            area_color = setColorAlpha(area_color, (int) Math.round(255.0*(((Number)propertiesFrame.S_AreaOpacity.getValue()).doubleValue()/100.0)));
            obj7.getAttributes().setInteriorMaterial(new Material(area_color));
            obj7.getAttributes().setInteriorOpacity(((Number)propertiesFrame.S_AreaOpacity.getValue()).doubleValue()/100.0);
            if(propertiesFrame.CB_AreaType.getSelectedIndex() == 2)
            {
                obj7.getAttributes().setDrawInterior(true);
                obj7.getAttributes().setDrawOutline(true);
            }
            else
            if(propertiesFrame.CB_AreaType.getSelectedIndex() == 1)
            {
                obj7.getAttributes().setDrawInterior(false);
                obj7.getAttributes().setDrawOutline(true);
            }
            else
            {
                obj7.getAttributes().setDrawInterior(true);
                obj7.getAttributes().setDrawOutline(false);
            }

            obj7.setValue(AVKey.DESCRIPTION, propertiesFrame.TA_Description.getText());

            // update tree
            KMLFeatureTreeNode featureNode = (KMLFeatureTreeNode)obj7.getValue("oghab.mapviewer.avKey.tree_node");
            if(featureNode != null)
            {
                featureNode.getFeature().setField("name", propertiesFrame.TF_Name.getText());
                treeLayout.invalidate();
            }

            wwd.redraw();
            return;
        }

        if(obj6 instanceof gov.nasa.worldwind.render.ExtrudedPolygon)
        {
            gov.nasa.worldwind.render.ExtrudedPolygon obj7 = (gov.nasa.worldwind.render.ExtrudedPolygon)obj6;
            if(Settings.bDebug)  System.out.println("obj7: "+obj7);

            obj7.setValue(AVKey.DISPLAY_NAME, propertiesFrame.TF_Name.getText());

            Color lines_color = propertiesFrame.P_LinesColor.getBackground();
            lines_color = setColorAlpha(lines_color, (int) Math.round(255.0*(((Number)propertiesFrame.S_LinesOpacity.getValue()).doubleValue()/100.0)));
            obj7.getAttributes().setOutlineMaterial(new Material(lines_color));
            obj7.getAttributes().setOutlineOpacity(((Number)propertiesFrame.S_LinesOpacity.getValue()).doubleValue()/100.0);
            obj7.getAttributes().setOutlineWidth(((Number)propertiesFrame.S_LinesWidth.getValue()).doubleValue());

            Color area_color = propertiesFrame.P_AreaColor.getBackground();
            area_color = setColorAlpha(area_color, (int) Math.round(255.0*(((Number)propertiesFrame.S_AreaOpacity.getValue()).doubleValue()/100.0)));
            obj7.getAttributes().setInteriorMaterial(new Material(area_color));
            obj7.getAttributes().setInteriorOpacity(((Number)propertiesFrame.S_AreaOpacity.getValue()).doubleValue()/100.0);
            if(propertiesFrame.CB_AreaType.getSelectedIndex() == 2)
            {
                obj7.getAttributes().setDrawInterior(true);
                obj7.getAttributes().setDrawOutline(true);
            }
            else
            if(propertiesFrame.CB_AreaType.getSelectedIndex() == 1)
            {
                obj7.getAttributes().setDrawInterior(false);
                obj7.getAttributes().setDrawOutline(true);
            }
            else
            {
                obj7.getAttributes().setDrawInterior(true);
                obj7.getAttributes().setDrawOutline(false);
            }

            obj7.setValue(AVKey.DESCRIPTION, propertiesFrame.TA_Description.getText());

            // update tree
            KMLFeatureTreeNode featureNode = (KMLFeatureTreeNode)obj7.getValue("oghab.mapviewer.avKey.tree_node");
            if(featureNode != null)
            {
                featureNode.getFeature().setField("name", propertiesFrame.TF_Name.getText());
                treeLayout.invalidate();
            }

            wwd.redraw();
            return;
        }

        if(obj6 instanceof SurfaceImage)
        {
            SurfaceImage obj7 = (SurfaceImage)obj6;
            if(Settings.bDebug)  System.out.println("obj7: "+obj7);

            obj7.setValue(AVKey.DISPLAY_NAME, propertiesFrame.TF_Name.getText());

            // update tree
            KMLFeatureTreeNode featureNode = (KMLFeatureTreeNode)obj7.getValue("oghab.mapviewer.avKey.tree_node");
            if(featureNode != null)
            {
                featureNode.getFeature().setField("name", propertiesFrame.TF_Name.getText());
                treeLayout.invalidate();
            }

            wwd.redraw();
            return;
        }

        if(obj6 instanceof KMLGroundOverlay)
        {
            KMLGroundOverlay obj7 = (KMLGroundOverlay)obj6;
            if(Settings.bDebug)  System.out.println("obj7: "+obj7);

//            obj7.setValue(AVKey.DISPLAY_NAME, propertiesFrame.textFieldName.getText());

            wwd.redraw();
            return;
        }

        if(obj6 instanceof KMLFeatureTreeNode)
        {
            KMLFeatureTreeNode obj7 = (KMLFeatureTreeNode)obj6;
            if(Settings.bDebug)  System.out.println("obj7: "+obj7+", "+obj7.getText());

            obj7.getFeature().setField("name", propertiesFrame.TF_Name.getText());
            treeLayout.invalidate();

            Object obj8 = obj7.getValue(AVKey.CONTEXT);
            set_object_properties(obj8);

            wwd.redraw();
            return;
        }

        if(obj6 instanceof KMLLayerTreeNode)
        {
            KMLLayerTreeNode obj7 = (KMLLayerTreeNode)obj6;
            if(Settings.bDebug)  System.out.println("obj7: "+obj7+", "+obj7.getText());

            obj7.getLayer().setName(propertiesFrame.TF_Name.getText());
            treeLayout.invalidate();

            Object obj8 = obj7.getValue(AVKey.CONTEXT);
            set_object_properties(obj8);

            wwd.redraw();
            return;
        }
    }
    
    public void set_object_properties(Object obj)
    {
        if(!bUpdateEdit) return;
        if(obj instanceof KMLPlacemark)
        {
            KMLPlacemark obj5 = (KMLPlacemark)obj;
            if(obj5.getRenderables() != null)
            {
                for(int i=0;i<obj5.getRenderables().size();i++)
                {
                    KMLRenderable obj6 = (KMLRenderable)obj5.getRenderables().get(i);
                    set_object_properties_renderable(obj5, obj6);
                }
            }
        }
        else
        {
            set_object_properties_renderable(null, obj);
        }
    }
    
//    void set_object_properties(Object obj)
//    {
////        if(obj == null) return;
//        Object obj6 = obj;       
//        if(Settings.bDebug)  System.out.println("obj6: "+obj6+", "+obj6.getClass());
//
//        if(obj6 instanceof PointPlacemark)
//        {
//            PointPlacemark obj7 = (PointPlacemark)obj6;
//            if(Settings.bDebug)  System.out.println("obj7: "+obj7+", "+obj7.getLabelText());
//
//            obj7.setValue(AVKey.DISPLAY_NAME, propertiesFrame.textFieldName.getText());
//            obj7.setLabelText(propertiesFrame.textFieldName.getText());
//        }
//
//        if(obj6 instanceof gov.nasa.worldwind.render.Path)
//        {
//            gov.nasa.worldwind.render.Path obj7 = (gov.nasa.worldwind.render.Path)obj6;
//            if(Settings.bDebug)  System.out.println("obj7: "+obj7+", "+obj7.getStringValue(AVKey.DISPLAY_NAME));
//
//            obj7.setValue(AVKey.DISPLAY_NAME, propertiesFrame.textFieldName.getText());
//        }
//
////        if(obj6 instanceof KMLPointPlacemarkImpl)
////        {
////            KMLPointPlacemarkImpl obj7 = (KMLPointPlacemarkImpl)obj6;
////            if(Settings.bDebug)  System.out.println("obj7: "+obj7+", "+obj7.getLabelText());
////
////            propertiesFrame.textFieldName.setText(obj7.getLabelText());
////            propertiesFrame.textFieldName.setText(obj7.getStringValue(AVKey.DISPLAY_NAME));
////            obj7.setValue(AVKey.DISPLAY_NAME, propertiesFrame.textFieldName.getText());
////        }
//
////        if(obj6 instanceof KMLLineStringPlacemarkImpl)
////        {
////            KMLLineStringPlacemarkImpl obj7 = (KMLLineStringPlacemarkImpl)obj6;
////            if(Settings.bDebug)  System.out.println("obj7: "+obj7);
////
////            obj7.setValue(AVKey.DISPLAY_NAME, propertiesFrame.textFieldName.getText());
////        }
//
//        if(obj6 instanceof KMLGroundOverlayPolygonImpl)
//        {
//            KMLGroundOverlayPolygonImpl obj7 = (KMLGroundOverlayPolygonImpl)obj6;
//            if(Settings.bDebug)  System.out.println("obj7: "+obj7);
//
//            obj7.setValue(AVKey.DISPLAY_NAME, propertiesFrame.textFieldName.getText());
//        }
//
//        if(obj6 instanceof KMLPolygonImpl)
//        {
//            KMLPolygonImpl obj7 = (KMLPolygonImpl)obj6;
//            if(Settings.bDebug)  System.out.println("obj7: "+obj7);
//
//            obj7.setValue(AVKey.DISPLAY_NAME, propertiesFrame.textFieldName.getText());
//        }
//
//        if(obj6 instanceof KMLSurfaceImageImpl)
//        {
//            KMLSurfaceImageImpl obj7 = (KMLSurfaceImageImpl)obj6;
//            if(Settings.bDebug)  System.out.println("obj7: "+obj7);
//
//            obj7.setValue(AVKey.DISPLAY_NAME, propertiesFrame.textFieldName.getText());
//        }
//
//        if(obj6 instanceof KMLSurfacePolygonImpl)
//        {
//            KMLSurfacePolygonImpl obj7 = (KMLSurfacePolygonImpl)obj6;
//            if(Settings.bDebug)  System.out.println("obj7: "+obj7);
//
//            obj7.setValue(AVKey.DISPLAY_NAME, propertiesFrame.textFieldName.getText());
//        }
//
//        if(obj6 instanceof KMLExtrudedPolygonImpl)
//        {
//            KMLExtrudedPolygonImpl obj7 = (KMLExtrudedPolygonImpl)obj6;
//            if(Settings.bDebug)  System.out.println("obj7: "+obj7);
//
//            obj7.setValue(AVKey.DISPLAY_NAME, propertiesFrame.textFieldName.getText());
//        }
//
//        if(obj6 instanceof KMLGroundOverlay)
//        {
//            KMLGroundOverlay obj7 = (KMLGroundOverlay)obj6;
//            if(Settings.bDebug)  System.out.println("obj7: "+obj7);
//
////            obj7.setValue(AVKey.DISPLAY_NAME, propertiesFrame.textFieldName.getText());
//            obj7.setField(AVKey.DISPLAY_NAME, propertiesFrame.textFieldName.getText());
//        }
//        wwd.redraw();
//    }
    
    private Component cloneSwingComponent(Component c) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(c);
            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(bais);
            return (Component) ois.readObject();
        } catch (IOException|ClassNotFoundException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public void initKML()
    {
        // Add the on-screen layer tree, refreshing model with the WorldWindow's current layer list. We
        // intentionally refresh the tree's model before adding the layer that contains the tree itself. This
        // prevents the tree's layer from being displayed in the tree itself.
        layerTree = new LayerTree(new Offset(10d, 10d, AVKey.PIXELS, AVKey.INSET_PIXELS));
        layerTree.getModel().setIncludeHiddenLayers(true);
//        layerTree.getModel().refresh(wwd.getModel().getLayers());
//        layerTree.getModel().addLayer(layerNode);

        Offset offset = new Offset(10d, 10d, AVKey.PIXELS, AVKey.INSET_PIXELS);
        treeLayout = new BasicTreeLayout(layerTree, offset);
        treeLayout.getFrame().setSize(Size.fromPixels(300,500));
        treeLayout.getFrame().setFrameTitle(bundle.getString("T_Favorites"));
        treeLayout.getFrame().setIconImageSource("images/layer-manager-64x64.png");
        treeLayout.setShowDescription(true);

        BasicTreeAttributes attributes = new BasicTreeAttributes();
        attributes.setRootVisible(false);
        attributes.setFont(Font.decode("Tahoma-Plain-16"));
        treeLayout.setAttributes(attributes);

        BasicFrameAttributes frameAttributes = new BasicFrameAttributes();
        frameAttributes.setBackgroundOpacity(0.7);
        treeLayout.getFrame().setAttributes(frameAttributes);

        BasicTreeAttributes highlightAttributes = new BasicTreeAttributes(attributes);
        treeLayout.setHighlightAttributes(highlightAttributes);

        BasicFrameAttributes highlightFrameAttributes = new BasicFrameAttributes(frameAttributes);
        highlightFrameAttributes.setForegroundOpacity(1.0);
        highlightFrameAttributes.setBackgroundOpacity(1.0);
        treeLayout.getFrame().setHighlightAttributes(highlightFrameAttributes);

        layerTree.setLayout(treeLayout);
//        treeLayout = (mv_BasicTreeLayout)layerTree.getLayout();
        
        layerTree.addPropertyChangeListener(new PropertyChangeListener(){
            @Override
            public void propertyChange(PropertyChangeEvent evt) {   
                Object obj = evt.getSource();
//                if (obj instanceof KMLPlacemark)
                if (obj instanceof KMLFeatureTreeNode)
                {
                    System.out.println("obj: "+((KMLFeatureTreeNode) obj).getText());
                }
                if (obj instanceof KMLLayerTreeNode)
                {
                    System.out.println("obj: "+((KMLLayerTreeNode) obj).getText());
                }
//                for (Iterator<TreeNode> treeNode = layerTree.getModel().getRoot().getChildren().iterator(); treeNode.hasNext(); ) {
//                       LayerTreeNode layerTreeNode = LayerTreeNode.class.cast(treeNode.next());
//                       if(evt.getSource() instanceof LayerTreeNode && evt.getSource() != layerTreeNode)
//                        layerTreeNode.setSelected(false);
//                }       
            }       
        });

        // Set up a layer to display the on-screen layer tree in the WorldWindow. This layer is not displayed in
        // the layer tree's model. Doing so would enable the user to hide the layer tree display with no way of
        // bringing it back.
        this.hiddenLayer = new RenderableLayer();
        this.hiddenLayer.addRenderable(this.layerTree);
        this.hiddenLayer.setName(bundle.getString("T_TreeLayer"));
        wwd.getModel().getLayers().add(this.hiddenLayer);

        // Add a controller to handle input events on the layer selector and on browser balloons.
        this.hotSpotController = new myHotSpotController(wwd);

        // Add a controller to display balloons when placemarks are clicked. We override the method addDocumentLayer
        // so that loading a KML document by clicking a KML balloon link displays an entry in the on-screen layer
        // tree.
        this.balloonController = new myBalloonController(wwd)
        {
            @Override
            protected void addDocumentLayer(KMLRoot document)
            {
                try {
                    addKMLLayer(document);
                } catch (XMLStreamException ex) {
                    Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };

        // Add a controller to handle common KML application events.
        this.kmlAppController = new KMLApplicationController(wwd);

        // Give the KML app controller a reference to the BalloonController so that the app controller can open
        // KML feature balloons when feature's are selected in the on-screen layer tree.
        this.kmlAppController.setBalloonController(balloonController);

        // Size the WorldWindow to take up the space typically used by the layer panel.
//        Dimension size = new Dimension(1400, 800);
//        this.setPreferredSize(size);
//        this.pack();
//        WWUtil.alignComponent(null, this, AVKey.CENTER);

//        makeMenu(this);

        // Set up to receive SSLHandshakeExceptions that occur during resource retrieval.
        WorldWind.getRetrievalService().setSSLExceptionListener(new RetrievalService.SSLExceptionListener()
        {
            public void onException(Throwable e, String path)
            {
                System.out.println(path);
                System.out.println(e);
            }
        });
    }
    
    /**
     * Adds the specified <code>kmlRoot</code> to this app frame's <code>WorldWindow</code> as a new
     * <code>Layer</code>, and adds a new <code>KMLLayerTreeNode</code> for the <code>kmlRoot</code> to this app
     * frame's on-screen layer tree.
     * <p>
     * This expects the <code>kmlRoot</code>'s <code>AVKey.DISPLAY_NAME</code> field to contain a display name
     * suitable for use as a layer name.
     *
     * @param kmlRoot the KMLRoot to add a new layer for.
     */
//    KMLController kmlController;
    protected void addKMLLayer(KMLRoot kmlRoot) throws XMLStreamException, IOException
    {
        // Create a KMLController to adapt the KMLRoot to the WorldWind renderable interface.
        KMLController kmlController = new KMLController(kmlRoot);// okk
        kmlController.getKmlRoot().requestRedraw();

        // Adds a new layer containing the KMLRoot to the end of the WorldWindow's layer list. This
        // retrieves the layer name from the KMLRoot's DISPLAY_NAME field.
        RenderableLayer kmlLayer = new RenderableLayer();
        String strName = (String) kmlRoot.getField(AVKey.DISPLAY_NAME);
        kmlLayer.setName(strName);
        kmlLayer.addRenderable(kmlController);
        wwd.getModel().getLayers().add(kmlLayer);
        wwd.redrawNow();// very important for (getRenderables() != null)
        
        if(kmlDefaultRoot == null)
        {
            kmlDefaultRoot = kmlRoot;
            kmlDefaultController = kmlController;
            kmlDefaultLayer = kmlLayer;
        }

        // update sector
        KMLOrbitViewController viewController = (KMLOrbitViewController)KMLOrbitViewController.create(wwd);
        viewController.goTo(kmlRoot.getFeature());
//        Sector sector = Sector.fromDegrees(kmlRoot. getPosition().getLatitude().getDegrees(),colladaRoot.getPosition().getLatitude().getDegrees(),colladaRoot.getPosition().getLongitude().getDegrees(),colladaRoot.getPosition().getLongitude().getDegrees());
//        layer_kml.setValue("oghab.mapviewer.avKey.Sector", sector);
        
//        KMLDocument document = (KMLDocument) kmlDefaultRoot.getFeature();
//        document.addFeature(kmlRoot.getFeature());
        
//        int idx = document.getFeatures().indexOf(treeLayout.getCurrTreeNode());
//        if(idx >= 0)
//        {
//            KMLAbstractFeature parentFeature = document.getFeatures().get(idx);
//            if(parentFeature instanceof KMLAbstractContainer)
//            {
//                KMLAbstractContainer parentContainer = (KMLAbstractContainer)parentFeature;
//                parentContainer.addFeature(kmlRoot.getFeature());
//            }
//            else
//            {
//                document.addFeature(kmlRoot.getFeature());
//            }
//        }
//        else
//        {
//            document.addFeature(kmlRoot.getFeature());
//        }

        // Adds a new layer tree node for the KMLRoot to the on-screen layer tree, and makes the new node visible
        // in the tree. This also expands any tree paths that represent open KML containers or open KML network
        // links.
        try
        {
            KMLLayerTreeNode layerNode = new KMLLayerTreeNode(kmlLayer, kmlRoot);
            TreeNode parent = treeLayout.getCurrTreeNode();
            if(parent == null)
                layerTree.getModel().addLayer(layerNode);
            else
                layerTree.getModel().addLayer(parent, layerNode);
            this.layerTree.makeVisible(layerNode.getPath());
            layerNode.expandOpenContainers(this.layerTree);
            treeLayout.invalidate();
            wwd.redrawNow();
        }
        catch(Exception ex)
        {
            if(Settings.bDebug)  System.out.println(ex.getMessage());
        }
        
//        document.addFeature(kmlController.getRoot().getFeature());
//        mv_KMLLayerTreeNode layerNode = new mv_KMLLayerTreeNode(layer_kml, kmlPoint.getRoot());
//        layerTree.getModel().addLayer(0, layerNode);
        
//        try {
////            kmlRoot.requestRedraw();            
////            KMLTraversalContext tc = kmlController.getTraversalContext();
////            DrawContext dc = wwd.getSceneController().getDrawContext();
//////            kmlRoot.preRender(tc, dc);
////            kmlController.preRender(dc);
////            add_kml_layer_to_tree(layer_kml);
//
//            add_kml_layer_to_tree(kmlController);// okk
//        } catch (XMLStreamException ex) {
//            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (IOException ex) {
//            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
//        }

        // Listens to refresh property change events from KML network link nodes. Upon receiving such an event this
        // expands any tree paths that represent open KML containers. When a KML network link refreshes, its tree
        // node replaces its children with new nodes created from the refreshed content, then sends a refresh
        // property change event through the layer tree. By expanding open containers after a network link refresh,
        // we ensure that the network link tree view appearance is consistent with the KML specification.
//        layerNode.addPropertyChangeListener(AVKey.RETRIEVAL_STATE_SUCCESSFUL, new PropertyChangeListener()
//        {
//            public void propertyChange(final PropertyChangeEvent event)
//            {
//                Object obj = event.getSource();
//                if (obj instanceof mv_KMLNetworkLinkTreeNode)
//                {
//                    // Manipulate the tree on the EDT.
//                    SwingUtilities.invokeLater(new Runnable()
//                    {
//                        public void run()
//                        {
//                            ((mv_KMLNetworkLinkTreeNode) obj).expandOpenContainers(layerTree);
//                            wwd.redrawNow();
//                        }
//                    });
//                }
//            }
//        });
    }

    static KMLRoot kmlDefaultRoot = null;
    static RenderableLayer kmlDefaultLayer = null;
    static KMLController kmlDefaultController = null;
    
    /** A <code>Thread</code> that loads a KML file and displays it in an <code>AppFrame</code>. */
    public static class KMLWorkerThread extends Thread
    {
        /** Indicates the source of the KML file loaded by this thread. Initialized during construction. */
        protected Object kmlSource;
        /** Indicates the <code>AppFrame</code> the KML file content is displayed in. Initialized during construction. */
        protected MainFrame appFrame;

        /**
         * Creates a new worker thread from a specified <code>kmlSource</code> and <code>appFrame</code>.
         *
         * @param kmlSource the source of the KML file to load. May be a {@link File}, a {@link URL}, or an {@link
         *                  java.io.InputStream}, or a {@link String} identifying a file path or URL.
         * @param appFrame  the <code>AppFrame</code> in which to display the KML source.
         */
        public KMLWorkerThread(Object kmlSource, MainFrame appFrame)
        {
            this.kmlSource = kmlSource;
            this.appFrame = appFrame;
        }

        /**
         * Loads this worker thread's KML source into a new <code>{@link gov.nasa.worldwind.ogc.kml.KMLRoot}</code>,
         * then adds the new <code>KMLRoot</code> to this worker thread's <code>AppFrame</code>. The
         * <code>KMLRoot</code>'s <code>AVKey.DISPLAY_NAME</code> field contains a display name created from either the
         * KML source or the KML root feature name.
         * <p>
         * If loading the KML source fails, this prints the exception and its stack trace to the standard error stream,
         * but otherwise does nothing.
         */
        public void run()
        {
            try
            {
                KMLRoot kmlRoot = this.parse();
                kmlRoot.requestRedraw();

                // Set the document's display name
                kmlRoot.setField(AVKey.DISPLAY_NAME, formName(this.kmlSource, kmlRoot));

                // Schedule a task on the EDT to add the parsed document to a layer
                final KMLRoot finalKMLRoot = kmlRoot;
                SwingUtilities.invokeLater(new Runnable()
                {
                    public void run()
                    {
                        try {
                            appFrame.addKMLLayer(finalKMLRoot);
                        } catch (XMLStreamException ex) {
                            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (IOException ex) {
                            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                });
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        /**
         * Parse the KML document.
         *
         * @return The parsed document.
         *
         * @throws IOException        if the document cannot be read.
         * @throws XMLStreamException if document cannot be parsed.
         */
        protected KMLRoot parse() throws IOException, XMLStreamException
        {
            // KMLRoot.createAndParse will attempt to parse the document using a namespace aware parser, but if that
            // fails due to a parsing error it will try again using a namespace unaware parser. Note that this second
            // step may require the document to be read from the network again if the kmlSource is a stream.
            return KMLRoot.createAndParse(this.kmlSource);
        }
    }

    protected static void listAllRegisteredDrivers() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < gdal.GetDriverCount(); i++) {
            Driver drv = gdal.GetDriver(i);
            String msg = Logging.getMessage("gdal.DriverDetails", drv.getShortName(), drv.getLongName(),
                    drv.GetDescription());
            sb.append(msg).append("\n");
        }
        System.out.println(sb.toString());
        System.out.flush();
    }

    protected static final String GDAL_DRIVER_PATH = "GDAL_DRIVER_PATH";
    protected static final String OGR_DRIVER_PATH = "OGR_DRIVER_PATH";
    protected static final String GDAL_DATA_PATH = "GDAL_DATA";
    protected static final String PROJ_LIB_DRIVER_PATH = "PROJ_LIB";
    protected void importImagery(String filename)
    {
        try
        {
            // Read the data and save it in a temp file.
            System.out.println("importImagery() Started...");
            
            GDALUtils.initialize();

            System.out.println("oghab.mapviewer.gdal.path: "+System.getProperty("oghab.mapviewer.gdal.path"));
            System.out.println("oghab.mapviewer.gdal.jni_dll: "+System.getProperty("oghab.mapviewer.gdal.jni_dll"));
            System.out.println("oghab.mapviewer.gdal.driver.path: "+System.getProperty("oghab.mapviewer.gdal.driver.path"));
            System.out.println("oghab.mapviewer.gdal.data.path: "+System.getProperty("oghab.mapviewer.gdal.data.path"));
            System.out.println("oghab.mapviewer.ogr.driver.path: "+System.getProperty("oghab.mapviewer.ogr.driver.path"));
            System.out.println("oghab.mapviewer.proj.lib.path: "+System.getProperty("oghab.mapviewer.proj.lib.path"));
            
//            System.out.println("GDAL_DRIVER_PATH: "+gdal.GetConfigOption(GDAL_DRIVER_PATH));
//            System.out.println("OGR_DRIVER_PATH: "+gdal.GetConfigOption(OGR_DRIVER_PATH));
//            System.out.println("GDAL_DATA_PATH: "+gdal.GetConfigOption(GDAL_DATA_PATH));
//            System.out.println("PROJ_LIB_DRIVER_PATH: "+gdal.GetConfigOption(PROJ_LIB_DRIVER_PATH));

            File sourceFile = new File(filename);
            System.out.println("Temp file: ["+sourceFile.getAbsolutePath()+"]");

//            NativeLibraryLoader.loadLibrary("C:\\Program Files\\GDAL", "gdalalljni.dll");
//
//            gdal.SetConfigOption(GDAL_DRIVER_PATH, "C:\\Program Files\\GDAL\\gdalplugins");
//            gdal.SetConfigOption(OGR_DRIVER_PATH, "C:\\Program Files\\GDAL\\gdalplugins");
//            gdal.SetConfigOption(GDAL_DATA_PATH, "C:\\Program Files\\GDAL\\gdal-data");
//
//            gdal.AllRegister();
//            listAllRegisteredDrivers();
//            
//            Dataset ds = gdal.Open( filename, gdalconst.GA_ReadOnly );
//
//            if (ds == null)
//            {
//                System.out.println("Can't open " + filename);
//                System.exit(-1);
//            }
//            System.out.println("["+filename + "] loaded sccessfully...");
//
//            System.out.println("Raster dataset parameters:");
//            System.out.println("  Projection: " + ds.GetProjectionRef());
//            System.out.println("  RasterCount: " + ds.getRasterCount());
//            System.out.println("  RasterSize (" + ds.getRasterXSize() + "," + ds.getRasterYSize() + ")");
//            // explicit closing of dataset
//            ds.delete();
//
//            System.out.println("Completed.");
            
            
            // Create a raster reader to read this type of file. The reader is created from the currently
            // configured factory. The factory class is specified in the Configuration, and a different one can be
            // specified there.
            DataRasterReaderFactory readerFactory = (DataRasterReaderFactory) WorldWind.createConfigurationComponent(AVKey.DATA_RASTER_READER_FACTORY_CLASS_NAME);
            DataRasterReader reader = readerFactory.findReaderFor(sourceFile, null);
//            GDALDataRasterReader reader = new GDALDataRasterReader();
            System.out.println("DataRasterReader: ["+reader.getDescription()+"]");

            // Before reading the raster, verify that the file contains imagery.
            AVList metadata = reader.readMetadata(sourceFile, null);
            if (metadata == null || !AVKey.IMAGE.equals(metadata.getStringValue(AVKey.PIXEL_FORMAT)))
                throw new Exception("Not an image file.");
            System.out.println("readMetadata...");

            // Read the file into the raster. read() returns potentially several rasters if there are multiple
            // files, but in this case there is only one so just use the first element of the returned array.
            DataRaster[] rasters = reader.read(sourceFile, null);
            if (rasters == null || rasters.length == 0)
                throw new Exception("Can't read the image file.");
            System.out.println("reader.read...");

            DataRaster raster = rasters[0];
            System.out.println("raster.toString(): "+raster.toString());
//            GDALDataRaster raster = new GDALDataRaster(sourceFile, true); // read data raster quietly
//            GDALDataRaster raster = new GDALDataRaster(ds); // read data raster quietly

            // Determine the sector covered by the image. This information is in the GeoTIFF file or auxiliary
            // files associated with the image file.
            final Sector sector = (Sector) raster.getValue(AVKey.SECTOR);
            if (sector == null)
                throw new Exception("No location specified with image.");
            System.out.println("sector...");

            // Request a sub-raster that contains the whole image. This step is necessary because only sub-rasters
            // are reprojected (if necessary); primary rasters are not.
            int width = raster.getWidth();
            int height = raster.getHeight();
            System.out.println("width: "+width+", height: "+height);

            // getSubRaster() returns a sub-raster of the size specified by width and height for the area indicated
            // by a sector. The width, height and sector need not be the full width, height and sector of the data,
            // but we use the full values of those here because we know the full size isn't huge. If it were huge
            // it would be best to get only sub-regions as needed or install it as a tiled image layer rather than
            // merely import it.
            DataRaster subRaster = raster.getSubRaster(width, height, sector, null);
//            AVListImpl params = new AVListImpl();
//            params.setValue(gov.nasa.worldwind.avlist.AVKey.WIDTH, width);
//            params.setValue(gov.nasa.worldwind.avlist.AVKey.HEIGHT, height);
//            params.setValue(gov.nasa.worldwind.avlist.AVKey.SECTOR, sector);
//            DataRaster subRaster = raster.getSubRaster(width, height, sector, params);
            System.out.println("getSubRaster...");

            // Tne primary raster can be disposed now that we have a sub-raster. Disposal won't affect the
            // sub-raster.
            raster.dispose();

            // Verify that the sub-raster can create a BufferedImage, then create one.
            if (!(subRaster instanceof BufferedImageRaster))
                throw new Exception("Cannot get BufferedImage.");
            BufferedImage image = ((BufferedImageRaster) subRaster).getBufferedImage();
            System.out.println("getBufferedImage...");

            // The sub-raster can now be disposed. Disposal won't affect the BufferedImage.
            subRaster.dispose();

            // Create a SurfaceImage to display the image over the specified sector.
            final SurfaceImage si1 = new SurfaceImage(image, sector);
            System.out.println("SurfaceImage...");

            // On the event-dispatch thread, add the imported data as an SurfaceImageLayer.
            SwingUtilities.invokeLater(new Runnable()
            {
                public void run()
                {
                    // Add the SurfaceImage to a layer.
                    SurfaceImageLayer layer = new SurfaceImageLayer();
                    layer.setName("Imported Surface Image");
                    layer.setPickEnabled(false);
                    layer.addRenderable(si1);
                    layer.setValue("oghab.mapviewer.avKey.Sector", sector);

                    // Add the layer to the model and update the application's layer panel.
//                    insertBeforeCompass(wwd, layer);
                    wwd.getModel().getLayers().add(layer);
                    
                    // Set the view to look at the imported image.
                    goTo(wwd, sector);
                }
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void insertBeforeCompass(WorldWindow wwd, Layer layer) {
        // Insert the layer into the layer list just before the compass.
        int compassPosition = 0;
        LayerList layers = wwd.getModel().getLayers();
        for (Layer l : layers) {
            if (l instanceof CompassLayer) {
                compassPosition = layers.indexOf(l);
            }
        }
        layers.add(compassPosition, layer);
    }

    protected void importElevations(String filename)
    {
        try
        {
            System.out.printf("importElevations() Started...");
            File sourceFile = new File(filename);
            System.out.printf("Temp file: ["+sourceFile.getAbsolutePath()+"]");

            // Create a local elevation model from the data.
            final LocalElevationModel elevationModel = new LocalElevationModel();
            elevationModel.addElevations(sourceFile);

            SwingUtilities.invokeLater(new Runnable()
            {
                public void run()
                {
                    // Get the WorldWindow's current elevation model.
                    Globe globe = wwd.getModel().getGlobe();
                    ElevationModel currentElevationModel = globe.getElevationModel();

                    // Add the new elevation model to the globe.
                    if (currentElevationModel instanceof CompoundElevationModel)
                        ((CompoundElevationModel) currentElevationModel).addElevationModel(elevationModel);
                    else
                        globe.setElevationModel(elevationModel);

                    // Set the view to look at the imported elevations, although they might be hard to detect. To
                    // make them easier to detect, replace the globe's CompoundElevationModel with the new elevation
                    // model rather than adding it.
                    Sector modelSector = elevationModel.getSector();
                    goTo(wwd, modelSector);
                }
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    protected static String formName(Object kmlSource, KMLRoot kmlRoot)
    {
        KMLAbstractFeature rootFeature = kmlRoot.getFeature();

        if (rootFeature != null && !WWUtil.isEmpty(rootFeature.getName()))
            return rootFeature.getName();

        if (kmlSource instanceof File)
            return ((File) kmlSource).getName();

        if (kmlSource instanceof URL)
            return ((URL) kmlSource).getPath();

        if (kmlSource instanceof String && WWIO.makeURL((String) kmlSource) != null)
            return WWIO.makeURL((String) kmlSource).getPath();

        return "KML Layer";
    }

    public static void goTo(WorldWindow wwd, Position position)
    {
        Sector sector = Sector.fromDegrees(position.getLatitude().getDegrees(),position.getLatitude().getDegrees(),position.getLongitude().getDegrees(),position.getLongitude().getDegrees());
        goTo(wwd,sector);
    }

    /**
     * Causes the View attached to the specified WorldWindow to animate to the specified sector. The View starts
     * animating at its current location and stops when the sector fills the window.
     *
     * @param wwd    the WorldWindow who's View animates.
     * @param sector the sector to go to.
     *
     * @throws IllegalArgumentException if either the <code>wwd</code> or the <code>sector</code> are
     *                                  <code>null</code>.
     */
    public static void goTo(WorldWindow wwd, Sector sector)
    {
        if (wwd == null)
        {
            String message = Logging.getMessage("nullValue.WorldWindow");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        if (sector == null)
        {
            String message = Logging.getMessage("nullValue.SectorIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        // Create a bounding box for the specified sector in order to estimate its size in model coordinates.
        Box extent = Sector.computeBoundingBox(wwd.getModel().getGlobe(),
            wwd.getSceneController().getVerticalExaggeration(), sector);

        // Estimate the distance between the center position and the eye position that is necessary to cause the sector to
        // fill a viewport with the specified field of view. Note that we change the distance between the center and eye
        // position here, and leave the field of view constant.
        Angle fov = wwd.getView().getFieldOfView();
        double zoom = extent.getRadius() / fov.cosHalfAngle() / fov.tanHalfAngle();

        // Configure OrbitView to look at the center of the sector from our estimated distance. This causes OrbitView to
        // animate to the specified position over several seconds. To affect this change immediately use the following:
        // ((OrbitView) wwd.getView()).setCenterPosition(new Position(sector.getCentroid(), 0d));
        // ((OrbitView) wwd.getView()).setZoom(zoom);
        wwd.getView().goTo(new Position(sector.getCentroid(), 0d), zoom);
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
                        List<String> params = Arrays.asList(str.split(","));
                        for(int i=0;i<params.size();i++)
                        {
                            System.out.println(params.get(i));
                        }
                        
                        int idx = 0;
                        int index = Integer.parseInt(params.get(idx++));
                        camera.latitude = Double.parseDouble(params.get(idx++));
                        camera.longitude = Double.parseDouble(params.get(idx++));
                        camera.altitude = Double.parseDouble(params.get(idx++));
                        camera.heading = Double.parseDouble(params.get(idx++));
                        camera.pitch = Double.parseDouble(params.get(idx++));
                        camera.roll = Double.parseDouble(params.get(idx++));
                        long time_start = Long.parseLong(params.get(idx++));
                        long time_end = Long.parseLong(params.get(idx++));

//                        MI_FlyViewActionPerformed(null);
                        wwd.setView(flyView);
//                        updateView();
                        // Set view heading, pitch and fov
//                         BasicOrbitView
//                        BasicFlyView flyView = (BasicFlyView)wwd.getView();
                        BasicView flyView = (BasicView)wwd.getView();
                        if(flyView != null)
                        {
                            flyView.setEyePosition(Position.fromDegrees(camera.latitude, camera.longitude, camera.altitude));
                            flyView.setRoll(Angle.fromDegrees(camera.roll));
                            flyView.setPitch(Angle.fromDegrees(camera.pitch));
                            flyView.setHeading(Angle.fromDegrees(camera.heading));
                            flyView.setFieldOfView(Angle.fromDegrees(camera.fov));
                        }
//                        MI_OrbitViewActionPerformed(null);

                        wwd.redraw();

//                        wwd.setView(orbitView);
//                        wwd.redraw();
                        
                        // Return the packet to the sender
//                        socket.send(packet) ;
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
    
    mv_3d_camera camera = new mv_3d_camera();
    private void save_camera_settings() {
        View view = wwd.getView();
        try {
            Properties configProps = new Properties();
            configProps.setProperty("view_state", view.getRestorableState());

            File configFile = new File(strViewstatePath);
            OutputStream outputStream = new FileOutputStream(configFile);
            configProps.store(outputStream, "View State");
            outputStream.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void load_camera_settings() {
        View view = wwd.getView();
        try {
            Properties defaultProps = new Properties();

            // sets default properties
            defaultProps.setProperty("view_state", view.getRestorableState());

            Properties configProps = new Properties(defaultProps);
            File configFile = new File(strViewstatePath);
            InputStream inputStream = new FileInputStream(configFile);
            configProps.load(inputStream);
            inputStream.close();
            
            view.restoreState(configProps.getProperty("view_state"));
            view.setFieldOfView(Angle.fromDegrees(Settings.dFOV));
            System.out.println("FOV: "+view.getFieldOfView().getDegrees());
        } catch (FileNotFoundException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }   

    void updateView() {
//        BasicOrbitView view = (BasicOrbitView) this.wwd.getView();
//        camera.range = view.getZoom();

        // Stop iterators first
//        view.stopAnimations();

//        view.setCenterPosition(Position.fromDegrees(camera.latitude, camera.longitude, camera.altitude));
//        view.setZoom(camera.range);
//        view.setRoll(Angle.fromDegrees(camera.roll));
//        view.setPitch(Angle.fromDegrees(camera.pitch));
//        view.setHeading(Angle.fromDegrees(camera.heading));
//        view.setFieldOfView(Angle.fromDegrees(camera.fov));
//        wwd.redraw();
        
//        BasicFlyView view = (BasicFlyView)wwd.getView();
//        View view = wwd.getView();

//        Position eye_pos = view.getCurrentEyePosition();
//        camera.longitude = eye_pos.getLongitude().getDegrees();
//        camera.latitude = eye_pos.getLatitude().getDegrees();
//        camera.altitude = eye_pos.getAltitude();
//        camera.heading = view.getHeading().getDegrees();
//        camera.pitch = view.getPitch().getDegrees();
//        camera.roll = view.getRoll().getDegrees();
//        camera.fov = view.getFieldOfView().getDegrees();

//        view.setEyePosition(Position.fromDegrees(camera.latitude, camera.longitude, camera.altitude));

        // Save current eye position
//        final Position pos = view.getCurrentEyePosition();
        
//        BasicFlyView flyView = new BasicFlyView();
//        wwd.setView(flyView);
//        wwd.redraw();

        // Set view heading, pitch and fov
//         BasicOrbitView
//        BasicFlyView flyView = (BasicFlyView)wwd.getView();
        BasicView flyView = (BasicView)wwd.getView();
        if(flyView != null)
        {
            flyView.setEyePosition(Position.fromDegrees(camera.latitude, camera.longitude, camera.altitude));
            flyView.setRoll(Angle.fromDegrees(camera.roll));
            flyView.setPitch(Angle.fromDegrees(camera.pitch));
            flyView.setHeading(Angle.fromDegrees(camera.heading));
            flyView.setFieldOfView(Angle.fromDegrees(camera.fov));
        }

//        BasicOrbitView orbitView = new BasicOrbitView();
//        wwd.setView(orbitView);

        // Restore eye position
//        view.setEyePosition(pos);
//        view.setEyePosition(Position.fromDegrees(camera.latitude, camera.longitude, camera.altitude));

//        view.restoreState(state);
//        System.out.println("view2: "+view.toString());
        
        // Redraw
        wwd.redraw();
    }
    
//    void update_view_limits(){
//        ViewPropertyLimits view_limits = view.getViewPropertyLimits();
//        
//        Sector sector = rs.getStateValueAsSector(context, "eyeLocationLimits");
//        if (sector != null)
//            view_limits.setEyeLocationLimits(sector);
//
//        // Min and max center elevation.
//        double[] minAndMaxValue = this.getEyeElevationLimits();
//        Double min = rs.getStateValueAsDouble(context, "minEyeElevation");
//        if (min != null)
//            minAndMaxValue[0] = min;
//
//        Double max = rs.getStateValueAsDouble(context, "maxEyeElevation");
//        if (max != null)
//            minAndMaxValue[1] = max;
//
//        if (min != null || max != null)
//            view_limits.setEyeElevationLimits(minAndMaxValue[0], minAndMaxValue[1]);
//
//        // Min and max heading angle.
//        Angle[] minAndMaxAngle = this.getHeadingLimits();
//        min = rs.getStateValueAsDouble(context, "minHeadingDegrees");
//        if (min != null)
//            minAndMaxAngle[0] = Angle.fromDegrees(min);
//
//        max = rs.getStateValueAsDouble(context, "maxHeadingDegrees");
//        if (max != null)
//            minAndMaxAngle[1] = Angle.fromDegrees(max);
//
//        if (min != null || max != null)
//            view_limits.setHeadingLimits(minAndMaxAngle[0], minAndMaxAngle[1]);
//
//        // Min and max pitch angle.
//        minAndMaxAngle = this.getPitchLimits();
//        min = rs.getStateValueAsDouble(context, "minPitchDegrees");
//        if (min != null)
//            minAndMaxAngle[0] = Angle.fromDegrees(min);
//
//        max = rs.getStateValueAsDouble(context, "maxPitchDegrees");
//        if (max != null)
//            minAndMaxAngle[1] = Angle.fromDegrees(max);
//
//        if (min != null || max != null)
//            view_limits.setPitchLimits(minAndMaxAngle[0], minAndMaxAngle[1]);
//    }
    
//    void update_view_state()
//    {
//        RestorableSupport.StateObject so = rs.getStateObject(context, "viewPropertyLimits");
//        if (so != null)
//            this.getViewPropertyLimits().restoreState(rs, so);
//
//        Boolean b = rs.getStateValueAsBoolean(context, "detectCollisions");
//        if (b != null)
//            this.setDetectCollisions(b);
//
//        Double d = rs.getStateValueAsDouble(context, "fieldOfView");
//        if (d != null)
//            this.setFieldOfView(Angle.fromDegrees(d));
//
//        d = rs.getStateValueAsDouble(context, "nearClipDistance");
//        if (d != null)
//            this.setNearClipDistance(d);
//
//        d = rs.getStateValueAsDouble(context, "farClipDistance");
//        if (d != null)
//            this.setFarClipDistance(d);
//
//        Position p = rs.getStateValueAsPosition(context, "eyePosition");
//        if (p != null)
//            this.setEyePosition(p);
//
//        d = rs.getStateValueAsDouble(context, "heading");
//        if (d != null)
//            this.setHeading(Angle.fromDegrees(d));
//
//        d = rs.getStateValueAsDouble(context, "pitch");
//        if (d != null)
//            this.setPitch(Angle.fromDegrees(d));
//    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jFileChooser1 = new javax.swing.JFileChooser();
        PopupMenuEdit = new javax.swing.JPopupMenu();
        MenuItemCut = new javax.swing.JMenuItem();
        MenuItemCopy = new javax.swing.JMenuItem();
        MenuItemPaste = new javax.swing.JMenuItem();
        MenuItemDelete = new javax.swing.JMenuItem();
        jSeparator6 = new javax.swing.JPopupMenu.Separator();
        MenuItemSavePlaceAs = new javax.swing.JMenuItem();
        MenuItemSimulate = new javax.swing.JMenuItem();
        jSeparator8 = new javax.swing.JPopupMenu.Separator();
        MenuItemProperties = new javax.swing.JMenuItem();
        jPanel3 = new javax.swing.JPanel();
        jToolBar1 = new javax.swing.JToolBar();
        jButton17 = new javax.swing.JButton();
        jButton15 = new javax.swing.JButton();
        jButton16 = new javax.swing.JButton();
        jSeparator9 = new javax.swing.JToolBar.Separator();
        jButton11 = new javax.swing.JButton();
        jSeparator11 = new javax.swing.JToolBar.Separator();
        jButton8 = new javax.swing.JButton();
        jButton22 = new javax.swing.JButton();
        jSeparator7 = new javax.swing.JToolBar.Separator();
        jButton10 = new javax.swing.JButton();
        jButton9 = new javax.swing.JButton();
        jSeparator10 = new javax.swing.JToolBar.Separator();
        jButton20 = new javax.swing.JButton();
        jButton21 = new javax.swing.JButton();
        jButton13 = new javax.swing.JButton();
        jSeparator3 = new javax.swing.JToolBar.Separator();
        jButton12 = new javax.swing.JButton();
        jButton14 = new javax.swing.JButton();
        jButton23 = new javax.swing.JButton();
        jToolBar2 = new javax.swing.JToolBar();
        CB_FlyView = new javax.swing.JCheckBox();
        jButton7 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jButton18 = new javax.swing.JButton();
        jButton19 = new javax.swing.JButton();
        CB_Visible = new javax.swing.JCheckBox();
        jPanel2 = new javax.swing.JPanel();
        jMenuBar1 = new javax.swing.JMenuBar();
        MI_File = new javax.swing.JMenu();
        MI_Open = new javax.swing.JMenuItem();
        MI_ImportImage = new javax.swing.JMenuItem();
        MI_ImportRPF = new javax.swing.JMenuItem();
        MI_SaveAs = new javax.swing.JMenuItem();
        MI_Export = new javax.swing.JMenuItem();
        MI_ScreenShot = new javax.swing.JMenuItem();
        MI_Exit = new javax.swing.JMenuItem();
        MI_Edit = new javax.swing.JMenu();
        MI_Cut = new javax.swing.JMenuItem();
        MI_Copy = new javax.swing.JMenuItem();
        MI_Paste = new javax.swing.JMenuItem();
        MI_Delete = new javax.swing.JMenuItem();
        jSeparator4 = new javax.swing.JPopupMenu.Separator();
        MI_SavePlaceAs = new javax.swing.JMenuItem();
        MI_Simulate = new javax.swing.JMenuItem();
        jSeparator5 = new javax.swing.JPopupMenu.Separator();
        MI_Properties = new javax.swing.JMenuItem();
        MI_View = new javax.swing.JMenu();
        MI_ZoomIn = new javax.swing.JMenuItem();
        MI_ZoomOut = new javax.swing.JMenuItem();
        MI_Navigation = new javax.swing.JMenu();
        MI_OrbitView = new javax.swing.JMenuItem();
        MI_FlyView = new javax.swing.JMenuItem();
        MI_Layers = new javax.swing.JMenuItem();
        MI_Language = new javax.swing.JMenu();
        MI_English = new javax.swing.JMenuItem();
        MI_Arabic = new javax.swing.JMenuItem();
        MI_SteroMode = new javax.swing.JMenu();
        MI_None = new javax.swing.JMenuItem();
        MI_RedBlue = new javax.swing.JMenuItem();
        MI_Device = new javax.swing.JMenuItem();
        MI_Tools = new javax.swing.JMenu();
        MI_Data = new javax.swing.JMenu();
        MI_InstalledData = new javax.swing.JMenuItem();
        MI_CacheManager = new javax.swing.JMenuItem();
        MI_Measure = new javax.swing.JMenuItem();
        MI_UTMGrid = new javax.swing.JMenuItem();
        MI_ContourLines = new javax.swing.JMenuItem();
        MI_Download = new javax.swing.JMenuItem();
        MI_DDSConverter = new javax.swing.JMenuItem();
        MI_Search = new javax.swing.JMenuItem();
        MI_Add = new javax.swing.JMenu();
        MI_AddFolder = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        MI_AddPlacemark = new javax.swing.JMenuItem();
        MI_AddTacticalSymbol = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        MI_AddPath = new javax.swing.JMenuItem();
        MI_AddPolygon = new javax.swing.JMenuItem();
        IM_Help = new javax.swing.JMenu();
        MI_Contents = new javax.swing.JMenu();
        MI_ExportOSMTiles = new javax.swing.JMenuItem();
        MI_MilStd2025C = new javax.swing.JMenuItem();
        jSeparator12 = new javax.swing.JPopupMenu.Separator();
        MI_About = new javax.swing.JMenuItem();

        jFileChooser1.setMultiSelectionEnabled(true);

        MenuItemCut.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/Cut-icon24.png"))); // NOI18N
        MenuItemCut.setText("Cut");
        MenuItemCut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MenuItemCutActionPerformed(evt);
            }
        });
        PopupMenuEdit.add(MenuItemCut);

        MenuItemCopy.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/copy24.png"))); // NOI18N
        MenuItemCopy.setText("Copy");
        MenuItemCopy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MenuItemCopyActionPerformed(evt);
            }
        });
        PopupMenuEdit.add(MenuItemCopy);

        MenuItemPaste.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/clipboard_paste24.png"))); // NOI18N
        MenuItemPaste.setText("Paste");
        MenuItemPaste.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MenuItemPasteActionPerformed(evt);
            }
        });
        PopupMenuEdit.add(MenuItemPaste);

        MenuItemDelete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/delete24.png"))); // NOI18N
        MenuItemDelete.setText("Delete");
        MenuItemDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MenuItemDeleteActionPerformed(evt);
            }
        });
        PopupMenuEdit.add(MenuItemDelete);
        PopupMenuEdit.add(jSeparator6);

        MenuItemSavePlaceAs.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/save_place_as24.png"))); // NOI18N
        MenuItemSavePlaceAs.setText("Save Place As...");
        MenuItemSavePlaceAs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MenuItemSavePlaceAsActionPerformed(evt);
            }
        });
        PopupMenuEdit.add(MenuItemSavePlaceAs);

        MenuItemSimulate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/simulator24.png"))); // NOI18N
        MenuItemSimulate.setText("Simulate...");
        MenuItemSimulate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MenuItemSimulateActionPerformed(evt);
            }
        });
        PopupMenuEdit.add(MenuItemSimulate);
        PopupMenuEdit.add(jSeparator8);

        MenuItemProperties.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/properties-icon24.png"))); // NOI18N
        MenuItemProperties.setText("Properties");
        MenuItemProperties.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MenuItemPropertiesActionPerformed(evt);
            }
        });
        PopupMenuEdit.add(MenuItemProperties);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("MapViewer");
        setBackground(new java.awt.Color(0, 102, 255));
        setForeground(java.awt.Color.orange);
        setLocation(new java.awt.Point(0, 0));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });
        addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                formKeyTyped(evt);
            }
        });

        jToolBar1.setRollover(true);

        jButton17.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/exit-icon24.png"))); // NOI18N
        jButton17.setFocusable(false);
        jButton17.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton17.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton17.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton17ActionPerformed(evt);
            }
        });
        jToolBar1.add(jButton17);

        jButton15.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/kmz24.png"))); // NOI18N
        jButton15.setFocusable(false);
        jButton15.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton15.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton15.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton15ActionPerformed(evt);
            }
        });
        jToolBar1.add(jButton15);

        jButton16.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/screenshot24.png"))); // NOI18N
        jButton16.setFocusable(false);
        jButton16.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton16.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton16.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton16ActionPerformed(evt);
            }
        });
        jToolBar1.add(jButton16);
        jToolBar1.add(jSeparator9);

        jButton11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/folder24.png"))); // NOI18N
        jButton11.setFocusable(false);
        jButton11.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton11.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton11ActionPerformed(evt);
            }
        });
        jToolBar1.add(jButton11);
        jToolBar1.add(jSeparator11);

        jButton8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/ylw-pushpin24.png"))); // NOI18N
        jButton8.setFocusable(false);
        jButton8.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton8.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });
        jToolBar1.add(jButton8);

        jButton22.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/milstd24.png"))); // NOI18N
        jButton22.setFocusable(false);
        jButton22.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton22.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton22.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton22ActionPerformed(evt);
            }
        });
        jToolBar1.add(jButton22);
        jToolBar1.add(jSeparator7);

        jButton10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/path24.png"))); // NOI18N
        jButton10.setFocusable(false);
        jButton10.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton10.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton10ActionPerformed(evt);
            }
        });
        jToolBar1.add(jButton10);

        jButton9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/polygon24.png"))); // NOI18N
        jButton9.setFocusable(false);
        jButton9.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton9.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });
        jToolBar1.add(jButton9);
        jToolBar1.add(jSeparator10);

        jButton20.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/zoom_in24.png"))); // NOI18N
        jButton20.setFocusable(false);
        jButton20.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton20.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton20.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton20ActionPerformed(evt);
            }
        });
        jToolBar1.add(jButton20);

        jButton21.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/zoom_out24.png"))); // NOI18N
        jButton21.setFocusable(false);
        jButton21.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton21.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton21.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton21ActionPerformed(evt);
            }
        });
        jToolBar1.add(jButton21);

        jButton13.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/layers24.png"))); // NOI18N
        jButton13.setFocusable(false);
        jButton13.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton13.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton13ActionPerformed(evt);
            }
        });
        jToolBar1.add(jButton13);
        jToolBar1.add(jSeparator3);

        jButton12.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/ruler24.png"))); // NOI18N
        jButton12.setFocusable(false);
        jButton12.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton12.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton12ActionPerformed(evt);
            }
        });
        jToolBar1.add(jButton12);

        jButton14.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/contours24.png"))); // NOI18N
        jButton14.setFocusable(false);
        jButton14.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton14.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton14ActionPerformed(evt);
            }
        });
        jToolBar1.add(jButton14);

        jButton23.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/search24.png"))); // NOI18N
        jButton23.setFocusable(false);
        jButton23.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton23.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton23.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton23ActionPerformed(evt);
            }
        });
        jToolBar1.add(jButton23);

        jToolBar2.setRollover(true);

        CB_FlyView.setText("Fly View");
        CB_FlyView.setFocusable(false);
        CB_FlyView.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        CB_FlyView.setPreferredSize(new java.awt.Dimension(64, 39));
        CB_FlyView.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        CB_FlyView.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                CB_FlyViewItemStateChanged(evt);
            }
        });
        CB_FlyView.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                CB_FlyViewStateChanged(evt);
            }
        });
        CB_FlyView.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CB_FlyViewActionPerformed(evt);
            }
        });
        jToolBar2.add(CB_FlyView);

        jButton7.setText("KML");
        jButton7.setFocusable(false);
        jButton7.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton7.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });
        jToolBar2.add(jButton7);

        jButton6.setText("GUI");
        jButton6.setFocusable(false);
        jButton6.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton6.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });
        jToolBar2.add(jButton6);

        jButton1.setText("Save Tree");
        jButton1.setFocusable(false);
        jButton1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jToolBar2.add(jButton1);

        jButton2.setText("Load Tree");
        jButton2.setFocusable(false);
        jButton2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton2.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jToolBar2.add(jButton2);

        jButton3.setText("Video");
        jButton3.setFocusable(false);
        jButton3.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton3.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        jToolBar2.add(jButton3);

        jButton4.setText("Stop");
        jButton4.setFocusable(false);
        jButton4.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton4.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });
        jToolBar2.add(jButton4);

        jButton5.setText("Add");
        jButton5.setFocusable(false);
        jButton5.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton5.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });
        jToolBar2.add(jButton5);

        jButton18.setText("image");
        jButton18.setFocusable(false);
        jButton18.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton18.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton18.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton18ActionPerformed(evt);
            }
        });
        jToolBar2.add(jButton18);

        jButton19.setText("Test");
        jButton19.setFocusable(false);
        jButton19.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton19.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton19.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton19ActionPerformed(evt);
            }
        });
        jToolBar2.add(jButton19);

        CB_Visible.setSelected(true);
        CB_Visible.setText("Visible");
        CB_Visible.setFocusable(false);
        CB_Visible.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        CB_Visible.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        CB_Visible.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CB_VisibleActionPerformed(evt);
            }
        });
        jToolBar2.add(CB_Visible);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, 512, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jToolBar2, javax.swing.GroupLayout.PREFERRED_SIZE, 453, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(28, 28, 28))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(jToolBar2, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        jPanel2.setBackground(new java.awt.Color(102, 102, 102));
        jPanel2.setMinimumSize(new java.awt.Dimension(100, 100));
        jPanel2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jPanel2KeyTyped(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 407, Short.MAX_VALUE)
        );

        MI_File.setText("File");
        MI_File.setToolTipText("");

        MI_Open.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/kmz24.png"))); // NOI18N
        MI_Open.setText("Open...");
        MI_Open.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MI_OpenActionPerformed(evt);
            }
        });
        MI_File.add(MI_Open);

        MI_ImportImage.setText("Import Image...");
        MI_ImportImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MI_ImportImageActionPerformed(evt);
            }
        });
        MI_File.add(MI_ImportImage);

        MI_ImportRPF.setText("Import RPF...");
        MI_ImportRPF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MI_ImportRPFActionPerformed(evt);
            }
        });
        MI_File.add(MI_ImportRPF);

        MI_SaveAs.setText("Save as...");
        MI_SaveAs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MI_SaveAsActionPerformed(evt);
            }
        });
        MI_File.add(MI_SaveAs);

        MI_Export.setText("Export...");
        MI_Export.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MI_ExportActionPerformed(evt);
            }
        });
        MI_File.add(MI_Export);

        MI_ScreenShot.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/screenshot24.png"))); // NOI18N
        MI_ScreenShot.setText("Screenshot...");
        MI_ScreenShot.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MI_ScreenShotActionPerformed(evt);
            }
        });
        MI_File.add(MI_ScreenShot);

        MI_Exit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/exit-icon24.png"))); // NOI18N
        MI_Exit.setText("Exit");
        MI_Exit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MI_ExitActionPerformed(evt);
            }
        });
        MI_File.add(MI_Exit);

        jMenuBar1.add(MI_File);

        MI_Edit.setText("Edit");
        MI_Edit.addMenuListener(new javax.swing.event.MenuListener() {
            public void menuCanceled(javax.swing.event.MenuEvent evt) {
            }
            public void menuDeselected(javax.swing.event.MenuEvent evt) {
            }
            public void menuSelected(javax.swing.event.MenuEvent evt) {
                MI_EditMenuSelected(evt);
            }
        });

        MI_Cut.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/Cut-icon24.png"))); // NOI18N
        MI_Cut.setText("Cut");
        MI_Cut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MI_CutActionPerformed(evt);
            }
        });
        MI_Edit.add(MI_Cut);

        MI_Copy.setIcon(new javax.swing.ImageIcon("D:\\Ali\\WorldWind\\MapViewer\\mapviewer\\src\\res\\copy24.png")); // NOI18N
        MI_Copy.setText("Copy");
        MI_Copy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MI_CopyActionPerformed(evt);
            }
        });
        MI_Edit.add(MI_Copy);

        MI_Paste.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/clipboard_paste24.png"))); // NOI18N
        MI_Paste.setText("Paste");
        MI_Paste.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MI_PasteActionPerformed(evt);
            }
        });
        MI_Edit.add(MI_Paste);

        MI_Delete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/delete24.png"))); // NOI18N
        MI_Delete.setText("Delete");
        MI_Delete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MI_DeleteActionPerformed(evt);
            }
        });
        MI_Edit.add(MI_Delete);
        MI_Edit.add(jSeparator4);

        MI_SavePlaceAs.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/save_place_as24.png"))); // NOI18N
        MI_SavePlaceAs.setText("Save Place As...");
        MI_SavePlaceAs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MI_SavePlaceAsActionPerformed(evt);
            }
        });
        MI_Edit.add(MI_SavePlaceAs);

        MI_Simulate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/simulator24.png"))); // NOI18N
        MI_Simulate.setText("Simulate...");
        MI_Simulate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MI_SimulateActionPerformed(evt);
            }
        });
        MI_Edit.add(MI_Simulate);
        MI_Edit.add(jSeparator5);

        MI_Properties.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/properties-icon24.png"))); // NOI18N
        MI_Properties.setText("Properties");
        MI_Properties.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MI_PropertiesActionPerformed(evt);
            }
        });
        MI_Edit.add(MI_Properties);

        jMenuBar1.add(MI_Edit);

        MI_View.setText("View");

        MI_ZoomIn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/zoom_in24.png"))); // NOI18N
        MI_ZoomIn.setText("Zoom In");
        MI_ZoomIn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MI_ZoomInActionPerformed(evt);
            }
        });
        MI_View.add(MI_ZoomIn);

        MI_ZoomOut.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/zoom_out24.png"))); // NOI18N
        MI_ZoomOut.setText("Zoom Out");
        MI_ZoomOut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MI_ZoomOutActionPerformed(evt);
            }
        });
        MI_View.add(MI_ZoomOut);

        MI_Navigation.setText("Navigation");

        MI_OrbitView.setText("Orbit View");
        MI_OrbitView.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MI_OrbitViewActionPerformed(evt);
            }
        });
        MI_Navigation.add(MI_OrbitView);

        MI_FlyView.setText("Fly View");
        MI_FlyView.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MI_FlyViewActionPerformed(evt);
            }
        });
        MI_Navigation.add(MI_FlyView);

        MI_View.add(MI_Navigation);

        MI_Layers.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/layers24.png"))); // NOI18N
        MI_Layers.setText("Layers...");
        MI_Layers.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MI_LayersActionPerformed(evt);
            }
        });
        MI_View.add(MI_Layers);

        MI_Language.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/language24.png"))); // NOI18N
        MI_Language.setText("Language");

        MI_English.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/english_24.png"))); // NOI18N
        MI_English.setText("English");
        MI_English.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MI_EnglishActionPerformed(evt);
            }
        });
        MI_Language.add(MI_English);

        MI_Arabic.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/syria_flag_24.png"))); // NOI18N
        MI_Arabic.setText("Arabic");
        MI_Arabic.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MI_ArabicActionPerformed(evt);
            }
        });
        MI_Language.add(MI_Arabic);

        MI_View.add(MI_Language);

        MI_SteroMode.setText("Stereo Mode");

        MI_None.setText("None");
        MI_None.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MI_NoneActionPerformed(evt);
            }
        });
        MI_SteroMode.add(MI_None);

        MI_RedBlue.setText("Red/Blue");
        MI_RedBlue.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MI_RedBlueActionPerformed(evt);
            }
        });
        MI_SteroMode.add(MI_RedBlue);

        MI_Device.setText("Device");
        MI_Device.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MI_DeviceActionPerformed(evt);
            }
        });
        MI_SteroMode.add(MI_Device);

        MI_View.add(MI_SteroMode);

        jMenuBar1.add(MI_View);

        MI_Tools.setText("Tools");

        MI_Data.setText("Data");

        MI_InstalledData.setText("Installed Data...");
        MI_InstalledData.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MI_InstalledDataActionPerformed(evt);
            }
        });
        MI_Data.add(MI_InstalledData);

        MI_CacheManager.setText("Cache Manager...");
        MI_CacheManager.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MI_CacheManagerActionPerformed(evt);
            }
        });
        MI_Data.add(MI_CacheManager);

        MI_Tools.add(MI_Data);

        MI_Measure.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/ruler24.png"))); // NOI18N
        MI_Measure.setText("Measure...");
        MI_Measure.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MI_MeasureActionPerformed(evt);
            }
        });
        MI_Tools.add(MI_Measure);

        MI_UTMGrid.setText("UTM Grid...");
        MI_UTMGrid.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MI_UTMGridActionPerformed(evt);
            }
        });
        MI_Tools.add(MI_UTMGrid);

        MI_ContourLines.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/contours24.png"))); // NOI18N
        MI_ContourLines.setText("Contour lines...");
        MI_ContourLines.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MI_ContourLinesActionPerformed(evt);
            }
        });
        MI_Tools.add(MI_ContourLines);

        MI_Download.setText("Download...");
        MI_Download.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MI_DownloadActionPerformed(evt);
            }
        });
        MI_Tools.add(MI_Download);

        MI_DDSConverter.setText("DDS Converter...");
        MI_DDSConverter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MI_DDSConverterActionPerformed(evt);
            }
        });
        MI_Tools.add(MI_DDSConverter);

        MI_Search.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/search24.png"))); // NOI18N
        MI_Search.setText("Search");
        MI_Search.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MI_SearchActionPerformed(evt);
            }
        });
        MI_Tools.add(MI_Search);

        jMenuBar1.add(MI_Tools);

        MI_Add.setText("Add");
        MI_Add.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MI_AddActionPerformed(evt);
            }
        });

        MI_AddFolder.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/folder24.png"))); // NOI18N
        MI_AddFolder.setText("Folder");
        MI_AddFolder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MI_AddFolderActionPerformed(evt);
            }
        });
        MI_Add.add(MI_AddFolder);
        MI_Add.add(jSeparator1);

        MI_AddPlacemark.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/ylw-pushpin24.png"))); // NOI18N
        MI_AddPlacemark.setText("Placemark");
        MI_AddPlacemark.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MI_AddPlacemarkActionPerformed(evt);
            }
        });
        MI_Add.add(MI_AddPlacemark);

        MI_AddTacticalSymbol.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/milstd24.png"))); // NOI18N
        MI_AddTacticalSymbol.setText("Tactical Symbol");
        MI_AddTacticalSymbol.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MI_AddTacticalSymbolActionPerformed(evt);
            }
        });
        MI_Add.add(MI_AddTacticalSymbol);
        MI_Add.add(jSeparator2);

        MI_AddPath.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/path24.png"))); // NOI18N
        MI_AddPath.setText("Path");
        MI_AddPath.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MI_AddPathActionPerformed(evt);
            }
        });
        MI_Add.add(MI_AddPath);

        MI_AddPolygon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/polygon24.png"))); // NOI18N
        MI_AddPolygon.setText("Polygon");
        MI_AddPolygon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MI_AddPolygonActionPerformed(evt);
            }
        });
        MI_Add.add(MI_AddPolygon);

        jMenuBar1.add(MI_Add);

        IM_Help.setText("Help");

        MI_Contents.setText("Contents");

        MI_ExportOSMTiles.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/osm24.png"))); // NOI18N
        MI_ExportOSMTiles.setText("Export OSM Tiles");
        MI_ExportOSMTiles.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MI_ExportOSMTilesActionPerformed(evt);
            }
        });
        MI_Contents.add(MI_ExportOSMTiles);

        MI_MilStd2025C.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/milstd24.png"))); // NOI18N
        MI_MilStd2025C.setText("Mil-STD-2025C");
        MI_MilStd2025C.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MI_MilStd2025CActionPerformed(evt);
            }
        });
        MI_Contents.add(MI_MilStd2025C);

        IM_Help.add(MI_Contents);
        IM_Help.add(jSeparator12);

        MI_About.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/mapviewer_icon24.png"))); // NOI18N
        MI_About.setText("About");
        MI_About.setName(""); // NOI18N
        MI_About.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MI_AboutActionPerformed(evt);
            }
        });
        IM_Help.add(MI_About);

        jMenuBar1.add(IM_Help);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void MI_ExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MI_ExitActionPerformed
        UIManager.put("OptionPane.yesButtonText", bundle.getString("OP_Yes"));
        UIManager.put("OptionPane.noButtonText", bundle.getString("OP_No"));
        UIManager.put("OptionPane.cancelButtonText", bundle.getString("OP_Cancel"));
        if (JOptionPane.showConfirmDialog(MainFrame.this, 
                bundle.getString("MSG_Are_You_Sure_Close"), bundle.getString("MSG_Close_Window"), 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION){
            saveSettings();
            save_camera_settings();
            save_myplaces();
            System.exit(0);
        }
        else
        {
            MainFrame.this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        }
    }//GEN-LAST:event_MI_ExitActionPerformed

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
//        saveSettings();
//        wwd.shutdown();
//        WorldWind.shutDown();
    }//GEN-LAST:event_formWindowClosed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
    }//GEN-LAST:event_formWindowOpened

    public Optional<String> getExtensionByStringHandling(String filename) {
        return Optional.ofNullable(filename)
          .filter(f -> f.contains("."))
          .map(f -> f.substring(filename.lastIndexOf(".") + 1));
    }    
    
    final String strKmlKmzFilter = "KML/KMZ Files";
    final String strColladaFilter = "Collada Files";
    final String strShapefileFilter = "Shape Files";
    final String strGPSFilter = "GPS Files";
    final String strElevationFilter = "Elevation Files";
    final String strGDALFilter = "GDAL Files";
    
    private void MI_OpenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MI_OpenActionPerformed
        FileFilter filter = jFileChooser1.getFileFilter();
        jFileChooser1.setMultiSelectionEnabled(true);
        jFileChooser1.setAcceptAllFileFilterUsed(true);
        jFileChooser1.resetChoosableFileFilters();
        jFileChooser1.addChoosableFileFilter(new FileNameExtensionFilter(strKmlKmzFilter, "kml", "kmz"));
        jFileChooser1.addChoosableFileFilter(new FileNameExtensionFilter(strColladaFilter, "dae"));
        jFileChooser1.addChoosableFileFilter(new FileNameExtensionFilter(strShapefileFilter, "shp"));
        jFileChooser1.addChoosableFileFilter(new FileNameExtensionFilter(strGPSFilter, "gpx"));
        jFileChooser1.addChoosableFileFilter(new FileNameExtensionFilter(strElevationFilter, "dem", "bil", "tif"));
        jFileChooser1.addChoosableFileFilter(new FileNameExtensionFilter(strGDALFilter, "bmp", "jpg", "png", "tif", "ecw", "img", "jp2", "ntf"));
        jFileChooser1.setFileFilter(filter);
        int returnValue = jFileChooser1.showOpenDialog(null);
        System.out.println("FileFilter getDescription: "+jFileChooser1.getFileFilter().getDescription());
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            for (File file : jFileChooser1.getSelectedFiles())
            {
                String strFilter = jFileChooser1.getFileFilter().getDescription();
                if(strFilter.equalsIgnoreCase(strShapefileFilter))
                {
                    load_shapefile(file.getAbsolutePath(), true);
                }
                else
                if(strFilter.equalsIgnoreCase(strKmlKmzFilter))
                {
                    new KMLWorkerThread(file, this).start();
                }
                else
                if(strFilter.equalsIgnoreCase(strElevationFilter))
                {
                    importElevations(file.getAbsolutePath());
                }
                else
                if(strFilter.equalsIgnoreCase(strColladaFilter))
                {
                    create_collada_layer(file.getAbsolutePath());
                }
                else
                if(strFilter.equalsIgnoreCase(strGPSFilter))
                {
                    try {
                        load_gpx(file.getAbsolutePath());
                    } catch (SAXException ex) {
                        Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                else
                if(strFilter.equalsIgnoreCase(strGDALFilter))
                {
                    importImagery(file.getAbsolutePath());
                }
                else
                {
                    create_geo_rss(file.getAbsolutePath()); 
                }
            }
        }                
    }//GEN-LAST:event_MI_OpenActionPerformed

    private void MI_SaveAsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MI_SaveAsActionPerformed
        int returnValue = jFileChooser1.showSaveDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = jFileChooser1.getSelectedFile();
            System.out.println(selectedFile.getAbsolutePath());
        }                
    }//GEN-LAST:event_MI_SaveAsActionPerformed

    private void CB_FlyViewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CB_FlyViewActionPerformed
        
    }//GEN-LAST:event_CB_FlyViewActionPerformed

    private void CB_FlyViewStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_CB_FlyViewStateChanged
        
    }//GEN-LAST:event_CB_FlyViewStateChanged

    private void CB_FlyViewItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_CB_FlyViewItemStateChanged
        if(evt.getStateChange() == 1)
        {
            wwd.setView(flyView);
        }
        else
        {
            wwd.setView(orbitView);
        }
    }//GEN-LAST:event_CB_FlyViewItemStateChanged

    private void MI_InstalledDataActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MI_InstalledDataActionPerformed
        installedDataFrame.setVisible(true);
    }//GEN-LAST:event_MI_InstalledDataActionPerformed

    private void MI_OrbitViewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MI_OrbitViewActionPerformed
        wwd.setView(orbitView);
        CB_FlyView.setSelected(false);
        MI_OrbitView.setSelected(false);
        MI_FlyView.setSelected(true);
    }//GEN-LAST:event_MI_OrbitViewActionPerformed

    private void MI_FlyViewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MI_FlyViewActionPerformed
        wwd.setView(flyView);
        CB_FlyView.setSelected(true);
        MI_OrbitView.setSelected(true);
        MI_FlyView.setSelected(false);
    }//GEN-LAST:event_MI_FlyViewActionPerformed

    private void MI_CacheManagerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MI_CacheManagerActionPerformed
        DataCacheViewer.main(null);
    }//GEN-LAST:event_MI_CacheManagerActionPerformed

    private void MI_LayersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MI_LayersActionPerformed
        LayersViewer.create(wwd);
    }//GEN-LAST:event_MI_LayersActionPerformed

    private void MI_MeasureActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MI_MeasureActionPerformed
        MeasureFrame measureFrame = new MeasureFrame(wwd);
        measureFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        measureFrame.setPreferredSize(new Dimension(300, 600));
        // Center the application on the screen.
        Dimension prefSize = measureFrame.getPreferredSize();
        Dimension parentSize;
        java.awt.Point parentLocation = new java.awt.Point(0, 0);
        parentSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = parentLocation.x + (parentSize.width - prefSize.width) / 2;
        int y = parentLocation.y + (parentSize.height - prefSize.height) / 2;
        measureFrame.setLocation(x, y);
        
        measureFrame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                if((measureFrame.frame != null) && (measureFrame.frame.measureTool != null))
                {
                    measureFrame.frame.measureTool.clear();
                    measureFrame.frame.measureTool.setArmed(false);
                    wwd.redraw();
                }
            }
        });                
        
        measureFrame.setVisible(true);
    }//GEN-LAST:event_MI_MeasureActionPerformed

    private void MI_UTMGridActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MI_UTMGridActionPerformed
        MGRSFrame.create(mgrsGraticuleLayer);
    }//GEN-LAST:event_MI_UTMGridActionPerformed

    private void formKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyTyped
        int key = evt.getKeyCode();

        if (key == KeyEvent.VK_LEFT) {
            System.out.println("VK_LEFT");
        }

        if (key == KeyEvent.VK_RIGHT) {
            System.out.println("VK_RIGHT");
        }

        if (key == KeyEvent.VK_UP) {
            System.out.println("VK_UP");
        }

        if (key == KeyEvent.VK_DOWN) {
            System.out.println("VK_DOWN");
        }
    }//GEN-LAST:event_formKeyTyped

    private void jPanel2KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jPanel2KeyTyped
        int key = evt.getKeyCode();

        if (key == KeyEvent.VK_LEFT) {
            System.out.println("VK_LEFT");
        }

        if (key == KeyEvent.VK_RIGHT) {
            System.out.println("VK_RIGHT");
        }

        if (key == KeyEvent.VK_UP) {
            System.out.println("VK_UP");
        }

        if (key == KeyEvent.VK_DOWN) {
            System.out.println("VK_DOWN");
        }
    }//GEN-LAST:event_jPanel2KeyTyped

    private void MI_ExportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MI_ExportActionPerformed
        myExportImageOrElevations.create(wwd);
    }//GEN-LAST:event_MI_ExportActionPerformed

    private void MI_ScreenShotActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MI_ScreenShotActionPerformed
        new myScreenShotAction(this.wwd).actionPerformed(evt);
    }//GEN-LAST:event_MI_ScreenShotActionPerformed

    private void MI_ImportImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MI_ImportImageActionPerformed
        myRubberSheetImage.create(wwd);
    }//GEN-LAST:event_MI_ImportImageActionPerformed

    private void MI_ImportRPFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MI_ImportRPFActionPerformed
        myRPFDataImport.create(wwd);
    }//GEN-LAST:event_MI_ImportRPFActionPerformed

    private void MI_ContourLinesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MI_ContourLinesActionPerformed
        ContourLinesFrame contourLinesFrame = new ContourLinesFrame(wwd);
        contourLinesFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        // Center the application on the screen.
        Dimension prefSize = contourLinesFrame.getPreferredSize();
        Dimension parentSize;
        java.awt.Point parentLocation = new java.awt.Point(0, 0);
        parentSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = parentLocation.x + (parentSize.width - prefSize.width) / 2;
        int y = parentLocation.y + (parentSize.height - prefSize.height) / 2;
        contourLinesFrame.setLocation(x, y);
        
        contourLinesFrame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                contourLinesFrame.layer.setName(bundle.getString("F_ContourLines")+" "+contourLinesFrame.S_Elevations.getValue());
                wwd.redraw();
            }
        });                
        
        contourLinesFrame.setVisible(true);
    }//GEN-LAST:event_MI_ContourLinesActionPerformed

    private void MI_DownloadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MI_DownloadActionPerformed
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                myBulkDownload.main(null);
            }
        });
    }//GEN-LAST:event_MI_DownloadActionPerformed

    private void MI_DDSConverterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MI_DDSConverterActionPerformed
        myStandaloneDDSConverter.main(null);
    }//GEN-LAST:event_MI_DDSConverterActionPerformed
    
    private void MI_EnglishActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MI_EnglishActionPerformed
        Settings.strLanguage = "en";
        CURR_LANG = EN_LANG;
        set_language_for_all_frames();
    }//GEN-LAST:event_MI_EnglishActionPerformed

    private void MI_ArabicActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MI_ArabicActionPerformed
        Settings.strLanguage = "ar";
        CURR_LANG = AR_LANG;
        set_language_for_all_frames();
    }//GEN-LAST:event_MI_ArabicActionPerformed

    private void MI_AddPlacemarkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MI_AddPlacemarkActionPerformed
//        String labelText = "نقطة علام جديدة";
//        labelText = JOptionPane.showInputDialog(MainFrame.this, "Enter label text", labelText);
//        if (labelText != null)
//        {
//            Vec4 tempVec4 = wwd.getView().getCenterPoint();
//            Position position = wwd.getView().getGlobe().computePositionFromPoint(tempVec4);
//            add_placemark(position,labelText);
//            wwd.redraw();
//        }
        MI_AddPlacemark.setEnabled(false);
        propertiesFrame.TF_Name.setText("Untitled Placemark");
        propertiesFrame.B_Ok.setEnabled(true);
        propertiesFrame.setVisible(true);
    }//GEN-LAST:event_MI_AddPlacemarkActionPerformed
    
    void save_myplaces()
    {
        try
        {
            saveLayerTree();
//            save_kml_layers(Paths.get(strDataPath, "kml", "My Places.kml").toString());// okk
//            save_kml_layer(layer_kml, Paths.get(strDataPath, "kml", "My Places.kml").toString());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
//        try {
////            save_renderable_layer(layer_placemarks, "d:/layer_placemarks.kml");
//            save_renderable_layer(layer_paths, "d:/layer_paths.kml");
//            save_renderable_layer(layer_polygons, "d:/layer_polygons.kml");
//            save_renderable_layer(layer_extruded_polygons, "d:/layer_extruded_polygons.kml");
//            save_renderable_layer(layer_surface_shapes, "d:/layer_surface_shapes.kml");
//            save_renderable_layer(layer_screen_images, "d:/layer_screen_images.kml");
//            save_renderable_layer(layer_surface_images, "d:/layer_surface_images.kml");
//
////            save_renderable_layer(layer_shapefiles, "d:/layer_shapefiles.kml");
////            save_renderable_layer(layer_extruded_shapefiles, "d:/layer_extruded_shapefiles.kml");
//            
////            save_renderable_layer(layer_markers, "d:/layer_markers.kml");
//        } catch (XMLStreamException | IOException ex) {
//            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        try {
            saveLayerTree();
            
//        save_myplaces();
//serialization
//        try{
//            FileOutputStream file= new FileOutputStream("d:/checkboxTree.txt");
//            ObjectOutputStream out = new ObjectOutputStream(file);
//            out.writeObject(checkboxTree.getModelFull());
//        }
//        catch(Exception e)
//        {
//
//        }

//serialization
//        try{
//            FileOutputStream file= new FileOutputStream("d:/checkboxTree.txt");
//            ObjectOutputStream out = new ObjectOutputStream(file);
//            out.writeObject(checkboxTree.getModelFull());
//        }
//        catch(Exception e)
//        {
//            
//        }
        } catch (XMLStreamException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        try
        {
            File file = new File(Paths.get(strDataPath, "kml", "My Places.kml").toString());
            new KMLWorkerThread(file, this).start();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
//        //Deserialization
//        try{
////            FileInputStream file= new FileInputStream("d:/checkboxTree.txt");
////            ObjectInputStream in = new ObjectInputStream(file);
////            checkboxTree.removeAll();
////            CheckModel model = (CheckModel) in.readObject(); 
////            checkboxTree.setModel(model);
////            checkboxTree.updateUI();
//        }
//        catch(Exception e)
//        {
//        }
    }//GEN-LAST:event_jButton2ActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        UIManager.put("OptionPane.yesButtonText", bundle.getString("OP_Yes"));
        UIManager.put("OptionPane.noButtonText", bundle.getString("OP_No"));
        UIManager.put("OptionPane.cancelButtonText", bundle.getString("OP_Cancel"));
        if (JOptionPane.showConfirmDialog(MainFrame.this, 
                bundle.getString("MSG_Are_You_Sure_Close"), bundle.getString("MSG_Close_Window"), 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION){
            saveSettings();
            save_camera_settings();
            save_myplaces();
            System.exit(0);
        }
        else
        {
            MainFrame.this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        }
    }//GEN-LAST:event_formWindowClosing

    // set capture driver for fswebcam tool
//    private static List<MediaListItem> EMPTY = new ArrayList<MediaListItem>();
//    private static final MediaListItem dev0 = new MediaListItem("Virtual Cam", "dshow://", EMPTY);
//    private static final MediaListItem dev0 = new MediaListItem("HP HD Webcam [Fixed]", "dshow://", EMPTY);
//    private static final MediaListItem dev1 = new MediaListItem("USB2.0 Camera", "dshow://", EMPTY);
//    private static final MediaListItem dev2 = new MediaListItem("Logitech Webcam", "dshow://", EMPTY);
    static {
//            Webcam.setDriver(new VlcjDriver(Arrays.asList(dev0)));
//            Webcam.setDriver(new VlcjDriver(Arrays.asList(dev0, dev1, dev2)));
//            Webcam.setDriver(new VlcjDriver());
//            Webcam.setDriver(new GStreamerDriver());
//            Webcam.setDriver(new JmfDriver());
//            Webcam.setDriver(new JavaCvDriver());
//            Webcam.setDriver(new FsWebcamDriver());
//            Webcam.setDriver(new LtiCivilDriver());
//            Webcam.setDriver(new JavaCvDriver());
//            Webcam.setDriver(new Gst1Driver());
//            Webcam.setDriver(new IpCamDriver());
//            Webcam.setDriver(new OpenImajDriver());
                
    }

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
//        JFrame window = new JFrame("Webcam Panel");
//        window.add(new WebcamPanel(Webcam.getDefault()));
//        window.setResizable(false);
//        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        window.pack();
//        window.setVisible(true); 

        for (Webcam w : Webcam.getWebcams()) {
            System.out.println(w.getName());
        }

//        Webcam webcam = Webcam.getDefault();
        Webcam webcam = Webcam.getWebcamByName("OBS-Camera 0");
        webcam.open();
        try {
            ImageIO.write(webcam.getImage(), "PNG", new File("d:/hello-world.png"));

            RenderableLayer layer_video = new RenderableLayer();
            layer_video.setName("Video");
            wwd.getModel().getLayers().add(layer_video);

            ScreenImage video_image = new ScreenImage();
            video_image.setOpacity(0.75);
//            video_image.setImageSource(Paths.get(strImagesPath, "Oghab.png").toString());
            video_image.setImageSource("res/Oghab.png");

            video_image.setScreenOffset(new Offset(0.0, 0.0, AVKey.FRACTION, AVKey.PIXELS));
            video_image.setImageOffset(new Offset(0.0, 0.0, AVKey.FRACTION, AVKey.FRACTION));

            layer_video.addRenderable(video_image);

            javax.swing.Timer timer = new javax.swing.Timer(40, new ActionListener()
            {
                boolean is_first_frame = true;
                public void actionPerformed(ActionEvent actionEvent)
                {
                    BufferedImage image = webcam.getImage();
                    if(image != null)
                    {
                        if(is_first_frame)
                        {
                            is_first_frame = false;

                            Size size = new Size();
                            size.setWidth(Size.EXPLICIT_DIMENSION, wwd.getWidth(), AVKey.PIXELS);
                            size.setHeight(Size.EXPLICIT_DIMENSION, wwd.getHeight(), AVKey.PIXELS);
                            video_image.setSize(size);
                        }
                        
                        video_image.setImageSource(image);
                        wwd.redraw();
                    }
                }
            });
            timer.start();
        } catch (IOException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }//GEN-LAST:event_jButton3ActionPerformed

    private void MI_AddPathActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MI_AddPathActionPerformed
        MI_AddPath.setEnabled(false);
        propertiesFrame.TF_Name.setText("Untitled Path");
//        propertiesFrame.setVisible(true);
        
        polyline.setAltitudeMode(WorldWind.CLAMP_TO_GROUND);
        polyline.setSurfacePath(true);
        
        polyline.setExtrude(false);

        polyline.setAttributes(normalShapeAttributes0);
        polyline.setHighlightAttributes(highlightShapeAttributes0);

        polyline.setValue(AVKey.DISPLAY_NAME, "Untitled Path");
        polyline.setValue(AVKey.SHORT_DESCRIPTION, "Short description of Path");
        polyline.setValue(AVKey.BALLOON_TEXT, "This is a Path.");
        
        positions = new ArrayList<>();

        layer_kml.addRenderable(polyline);

        propertiesFrame.B_Ok.setEnabled(false);
        currObject = polyline;
        show_object_properties(currObject);
        
//        myLineBuilder lineBuilder = new myLineBuilder(wwd, layer_paths, null, propertiesFrame, null);
    }//GEN-LAST:event_MI_AddPathActionPerformed

    private void MI_PropertiesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MI_PropertiesActionPerformed
        MI_Properties.setEnabled(false);
        show_object_properties(currObject);
    }//GEN-LAST:event_MI_PropertiesActionPerformed

    private void MI_EditMenuSelected(javax.swing.event.MenuEvent evt) {//GEN-FIRST:event_MI_EditMenuSelected
//        MI_Cut.setEnabled(treeLayout.getCurrTreeNode() != null);
//        MI_Copy.setEnabled(treeLayout.getCurrTreeNode() != null);
//        MI_Paste.setEnabled(treeLayout.getCurrTreeNode() != null);
//        MI_Delete.setEnabled(treeLayout.getCurrTreeNode() != null);
//        MI_SavePlaceAs.setEnabled(treeLayout.getCurrTreeNode() != null);
//        MI_Simulate.setEnabled(treeLayout.getCurrTreeNode() != null);
//        MI_Properties.setEnabled(treeLayout.getCurrTreeNode() != null);
    }//GEN-LAST:event_MI_EditMenuSelected

    private void MI_AddPolygonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MI_AddPolygonActionPerformed
        MI_AddPolygon.setEnabled(false);
        propertiesFrame.TF_Name.setText("Untitled Polygon");
//        propertiesFrame.setVisible(true);
        
//        polygon.setAltitudeMode(WorldWind.CLAMP_TO_GROUND);
//        polygon.setSurfacePath(true);
        
//        polygon.setExtrude(false);

//        polygon.setAttributes(normalShapeAttributes0);
//        polygon.setHighlightAttributes(highlightShapeAttributes0);

        polygon.setValue(AVKey.DISPLAY_NAME, "Untitled Polygon");
        polygon.setValue(AVKey.SHORT_DESCRIPTION, "Short description of Polygon");
        polygon.setValue(AVKey.BALLOON_TEXT, "This is a Polygon.");
        
        polyline.setAltitudeMode(WorldWind.CLAMP_TO_GROUND);
        polyline.setSurfacePath(true);
        
        polyline.setExtrude(false);

//        polyline.setAttributes(normalShapeAttributes0);
//        polyline.setHighlightAttributes(highlightShapeAttributes0);

        polyline.setValue(AVKey.DISPLAY_NAME, "Untitled Path");
        polyline.setValue(AVKey.SHORT_DESCRIPTION, "Short description of Path");
        polyline.setValue(AVKey.BALLOON_TEXT, "This is a Path.");
        
        positions = new ArrayList<>();

        layer_kml.addRenderable(polyline);
        layer_kml.addRenderable(polygon);
        propertiesFrame.B_Ok.setEnabled(false);

        currObject = polygon;
        show_object_properties(currObject);
    }//GEN-LAST:event_MI_AddPolygonActionPerformed

    private void MI_DeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MI_DeleteActionPerformed
        if (treeLayout.getCurrTreeNode() != null) {
            UIManager.put("OptionPane.yesButtonText", bundle.getString("OP_Yes"));
            UIManager.put("OptionPane.noButtonText", bundle.getString("OP_No"));
            UIManager.put("OptionPane.cancelButtonText", bundle.getString("OP_Cancel"));
            if (JOptionPane.showConfirmDialog(MainFrame.this, 
                bundle.getString("OP_Are_You_Sure_You_Want_To_Delete"), bundle.getString("OP_Delete"), 
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION){
                Object obj = treeLayout.getCurrTreeNode().getValue(AVKey.CONTEXT);
                delete_object(obj);
            }
        }        
    }//GEN-LAST:event_MI_DeleteActionPerformed

    private void MI_CopyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MI_CopyActionPerformed
        if (treeLayout.getCurrTreeNode() != null) {
            try {
                copy_object();
            } catch (XMLStreamException ex) {
                Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
        }        
    }//GEN-LAST:event_MI_CopyActionPerformed

    private void MI_PasteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MI_PasteActionPerformed
        Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable t = c.getContents(this);
        if (t == null)
            return;
        try {
            String strText = (String) t.getTransferData(DataFlavor.stringFlavor);
            FileWriter fw;
            try {
                String strPath = Paths.get(strAppPath, "paste.kml").toString();
                fw = new FileWriter(strPath);
                fw.write(strText);
                fw.close();
                
                try
                {
                    File file = new File(strPath);
                    new KMLWorkerThread(file, this).start();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            } catch (IOException ex) {
                Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (Exception e){
            e.printStackTrace();
        }//try
    }//GEN-LAST:event_MI_PasteActionPerformed

    private void MI_CutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MI_CutActionPerformed
        if (treeLayout.getCurrTreeNode() != null) {
            Object obj = treeLayout.getCurrTreeNode().getValue(AVKey.CONTEXT);
            try {
                copy_object();
                delete_object(obj);
            } catch (XMLStreamException ex) {
                Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
        }        
    }//GEN-LAST:event_MI_CutActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        bTerminate = true;
    }//GEN-LAST:event_jButton4ActionPerformed

    private void MI_SavePlaceAsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MI_SavePlaceAsActionPerformed
        if (treeLayout.getCurrTreeNode() != null) {
            try {
                jFileChooser1.setMultiSelectionEnabled(false);
                jFileChooser1.addChoosableFileFilter(new FileNameExtensionFilter("KML Files", "kml"));
                int returnValue = jFileChooser1.showSaveDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = jFileChooser1.getSelectedFile();
                    copy_object();

                    String strFilename = Paths.get(strDataPath, "kml", "copy.kml").toString();
                    Path originalPath = Paths.get(strFilename);
                    Path copied = Paths.get(selectedFile.getAbsolutePath());
                    Files.copy(originalPath, copied, StandardCopyOption.REPLACE_EXISTING);
                }                
            } catch (XMLStreamException ex) {
                Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
        }        
    }//GEN-LAST:event_MI_SavePlaceAsActionPerformed

    private void MI_SimulateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MI_SimulateActionPerformed
        UIManager.put("OptionPane.okButtonText", bundle.getString("OP_Ok"));
        UIManager.put("OptionPane.cancelButtonText", bundle.getString("OP_Cancel"));
        String zoomText = String.valueOf(zoom);
        zoomText = JOptionPane.showInputDialog(MainFrame.this, bundle.getString("OP_EnterZoom"), zoomText);
        
//        String message = "<html><body><div width='200px' align='right'>"+bundle.getString("OP_EnterZoom")+"</div></body></html>";
//        JLabel messageLabel = new JLabel(message);
//        zoomText = JOptionPane.showInputDialog(MainFrame.this, messageLabel, zoomText);
        
        if (zoomText != null)
        {
            zoom = Double.parseDouble(zoomText);
//            animatePath(polyline);
            simulate_object(currObject);
        }
//        animateToPath(polyline);
    }//GEN-LAST:event_MI_SimulateActionPerformed

    private void MenuItemCutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MenuItemCutActionPerformed
        MI_CutActionPerformed(evt);
    }//GEN-LAST:event_MenuItemCutActionPerformed

    private void MenuItemCopyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MenuItemCopyActionPerformed
        MI_CopyActionPerformed(evt);
    }//GEN-LAST:event_MenuItemCopyActionPerformed

    private void MenuItemPasteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MenuItemPasteActionPerformed
        MI_PasteActionPerformed(evt);
    }//GEN-LAST:event_MenuItemPasteActionPerformed

    private void MenuItemDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MenuItemDeleteActionPerformed
        MI_DeleteActionPerformed(evt);
    }//GEN-LAST:event_MenuItemDeleteActionPerformed

    private void MenuItemSavePlaceAsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MenuItemSavePlaceAsActionPerformed
        MI_SavePlaceAsActionPerformed(evt);
    }//GEN-LAST:event_MenuItemSavePlaceAsActionPerformed

    private void MenuItemSimulateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MenuItemSimulateActionPerformed
        MI_SimulateActionPerformed(evt);
    }//GEN-LAST:event_MenuItemSimulateActionPerformed

    private void MenuItemPropertiesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MenuItemPropertiesActionPerformed
        MI_PropertiesActionPerformed(evt);
    }//GEN-LAST:event_MenuItemPropertiesActionPerformed

    private void MI_NoneActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MI_NoneActionPerformed
        System.setProperty(AVKey.STEREO_MODE, "none");
        Configuration.getRequiredGLCapabilities();
        wwd.redraw();
    }//GEN-LAST:event_MI_NoneActionPerformed

    private void MI_RedBlueActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MI_RedBlueActionPerformed
        System.setProperty(AVKey.STEREO_MODE, "redblue");
        Configuration.getRequiredGLCapabilities();
        wwd.redraw();
    }//GEN-LAST:event_MI_RedBlueActionPerformed

    private void MI_DeviceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MI_DeviceActionPerformed
        System.setProperty(AVKey.STEREO_MODE, "device");
        Configuration.getRequiredGLCapabilities();
        wwd.redraw();
    }//GEN-LAST:event_MI_DeviceActionPerformed
    
    public void expandOpenContainers(LayerTreeNode layerNode, Tree tree)
    {
        if (tree == null)
        {
            String message = Logging.getMessage("nullValue.TreeIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        tree.expandPath(layerNode.getPath());

        for (TreeNode child : layerNode.getChildren())
        {
            if (child instanceof KMLFeatureTreeNode)
                ((KMLFeatureTreeNode) child).expandOpenContainers(tree);
            if (child instanceof KMLLayerTreeNode)
                ((KMLLayerTreeNode) child).expandOpenContainers(tree);
        }
    }

    private StringBuilder newDocument()
    {
        StringBuilder sb = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        sb.append("<kml");
        sb.append(" xmlns=\"").append(KMLConstants.KML_NAMESPACE).append("\"");
        sb.append(" xmlns:atom=\"").append(AtomConstants.ATOM_NAMESPACE).append("\"");
        sb.append(" xmlns:xal=\"").append(XALConstants.XAL_NAMESPACE).append("\"");
        sb.append(" xmlns:gx=\"").append(GXConstants.GX_NAMESPACE).append("\"");
        sb.append(">");

        return sb;
    }

    private StringBuilder newPrefixedDocument()
    {
        StringBuilder sb = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        sb.append("<kml:kml");
        sb.append(" xmlns:kml=\"").append(KMLConstants.KML_NAMESPACE).append("\"");
        sb.append(" xmlns:atom=\"").append(AtomConstants.ATOM_NAMESPACE).append("\"");
        sb.append(" xmlns:xal=\"").append(XALConstants.XAL_NAMESPACE).append("\"");
        sb.append(" xmlns:gx=\"").append(GXConstants.GX_NAMESPACE).append("\"");
        sb.append(">");

        return sb;
    }

    private void endDocument(StringBuilder sb)
    {
        sb.append("</kml>");
    }

    private void endPrefixedDocument(StringBuilder sb)
    {
        sb.append("</kml:kml>");
    }

    private KMLRoot newParsedRoot(StringBuilder sb)
    {
        KMLRoot root;
        try
        {
            root = new KMLRoot(WWIO.getInputStreamFromString(sb.toString()), KMLConstants.KML_MIME_TYPE);
            return root.parse();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    private KMLRoot newParsedRoot(StringBuilder sb, boolean suppressLogging)
    {
        KMLRoot root;
        try
        {
            root = new KMLRoot(WWIO.getInputStreamFromString(sb.toString()), KMLConstants.KML_MIME_TYPE);

            if (suppressLogging)
            {
                root.setNotificationListener(new XMLParserNotificationListener()
                {
                    public void notify(XMLParserNotification notification)
                    {
                        // Do nothing. This prevents logging of notification messages.
                    }
                });
            }

            return root.parse();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    private KMLRoot openAndParseFile(String sourceDoc)
    {
        KMLRoot root;
        final StringBuilder parserMessage = new StringBuilder();

        try
        {
            root = new KMLRoot(new File(sourceDoc));
            root.setNotificationListener(new XMLParserNotificationListener()
            {
                public void notify(XMLParserNotification notificationEvent)
                {
                    if (parserMessage.length() != 0)
                        parserMessage.append(", ");

                    parserMessage.append(notificationEvent.toString());
                }
            });
            root.parse();
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }

        return root;
    }
    
    public KMLFolder createKMLFolder(String name)
    {
        StringBuilder sb = newPrefixedDocument();
        sb.append("<kml:Folder><kml:name>"+name+"</kml:name><kml:open>0</kml:open>");
        sb.append("</kml:Folder>");
        endPrefixedDocument(sb);

        KMLRoot root = newParsedRoot(sb);
        KMLAbstractFeature document = root.getFeature();

        KMLFolder folder = (KMLFolder) document;
        return folder;
    }
    
    public KMLPoint createKMLPoint(Position coords,String name)
    {
        String altitudeMode = "clampToGround";
        boolean extrude = true;

        StringBuilder sb = newPrefixedDocument();
//        sb.append("<kml:Folder><kml:name>Hidden</kml:name><kml:open>1</kml:open>");
        sb.append("<kml:Placemark>");
        sb.append("<kml:name>"+name+"</kml:name><kml:visibility>1</kml:visibility><kml:Snippet>Short description of Placemark</kml:Snippet><kml:StyleMap><kml:Pair><kml:key>normal</kml:key><kml:Style><kml:IconStyle><kml:color>FFFFFFFF</kml:color><kml:colorMode>normal</kml:colorMode><kml:scale>1.0</kml:scale><kml:Icon><kml:href>D:\\Ali\\WorldWind\\MapViewer\\data\\icons\\ylw-pushpin.png</kml:href></kml:Icon><kml:hotSpot x=\"19.0\" y=\"0.0\" xunits=\"pixels\" yunits=\"pixels\"></kml:hotSpot></kml:IconStyle><kml:LabelStyle><kml:scale>1.0</kml:scale><kml:color>FF888800</kml:color><kml:colorMode>normal</kml:colorMode></kml:LabelStyle><kml:LineStyle><kml:width>2.0</kml:width><kml:color>FFFF00FF</kml:color><kml:colorMode>normal</kml:colorMode></kml:LineStyle></kml:Style></kml:Pair></kml:StyleMap>");
        sb.append("<kml:Point>");
        sb.append("<kml:extrude>").append(extrude ? "1" : "0").append("</kml:extrude>");
        sb.append("<kml:altitudeMode>").append(altitudeMode).append("</kml:altitudeMode>");
        sb.append("<kml:coordinates>");
        sb.append(coords.getLongitude().degrees).append(",");
        sb.append(coords.getLatitude().degrees).append(",");
        sb.append(coords.getElevation());
        sb.append("</kml:coordinates>");
        sb.append("</kml:Point>");
        sb.append("</kml:Placemark>");
//        sb.append("</kml:Folder>");
        endPrefixedDocument(sb);

        KMLRoot root = newParsedRoot(sb);
        KMLAbstractFeature document = root.getFeature();

//        KMLFolder folder = (KMLFolder) document;
//        List<KMLAbstractFeature> features = folder.getFeatures();
//        KMLPlacemark placemark = (KMLPlacemark) features.get(0);

//        KMLAbstractFeature feature = root.getFeature();
        KMLPlacemark placemark = (KMLPlacemark) document;

//        KMLAbstractGeometry geometry = ((KMLPlacemark) feature).getGeometry();
        KMLAbstractGeometry geometry = placemark.getGeometry();

        KMLPoint point = (KMLPoint) geometry;
        return point;
    }
    
    public KMLPoint createKMLTacticalSymbol(Position coords,String name,String id)
    {
        MilStd2525TacticalSymbol symbol = new MilStd2525TacticalSymbol(id, coords);
        symbol.setAltitudeMode(WorldWind.ABSOLUTE);

        // Create an attribute bundle and use it as the symbol's normal attributes.
        TacticalSymbolAttributes attrs = new BasicTacticalSymbolAttributes();
        attrs.setScale(0.75); // Make the symbol 75% its normal size.
        attrs.setOpacity(0.5); // Make the symbol 50% transparent.
        symbol.setAttributes(attrs);        
        
        // Create an attribute bundle and use it as the symbol's highlight attributes.
        TacticalSymbolAttributes highlightAttrs = new BasicTacticalSymbolAttributes();
        highlightAttrs.setScale(1.0); // 200% of normal size when highlighted.
        highlightAttrs.setOpacity(1.0); // 100% opaque when highlighted.
        symbol.setHighlightAttributes(highlightAttrs);
        symbol.setHighlighted(true);

        // Hide the symbol's text modifiers to reduce clutter.
        symbol.setShowTextModifiers(false);
        
        // Create an attribute bundle and use it as the symbol's normal attributes. Specify
        // the text modifier font and material.
        attrs.setTextModifierFont(Font.decode("Tahoma-Bold-12"));
        attrs.setTextModifierMaterial(Material.RED);
        symbol.setAttributes(attrs);

        if(symbolLayer == null)
        {
            symbolLayer = new RenderableLayer();
            symbolLayer.setName(bundle.getString("L_TacticalSymbols"));
            wwd.getModel().getLayers().add(symbolLayer);
        }
        symbolLayer.addRenderable(symbol);

        
        
        
        
        String altitudeMode = "clampToGround";
        boolean extrude = true;

        StringBuilder sb = newPrefixedDocument();
//        sb.append("<kml:Folder><kml:name>Hidden</kml:name><kml:open>1</kml:open>");
        sb.append("<kml:Placemark>");
        sb.append("<kml:name>"+name+"</kml:name><kml:visibility>1</kml:visibility><kml:Snippet>Short description of Placemark</kml:Snippet><kml:StyleMap><kml:Pair><kml:key>normal</kml:key><kml:Style><kml:IconStyle><kml:color>FFFFFFFF</kml:color><kml:colorMode>normal</kml:colorMode><kml:scale>1.0</kml:scale><kml:Icon><kml:href>D:\\Ali\\WorldWind\\MapViewer\\data\\icons\\ylw-pushpin.png</kml:href></kml:Icon><kml:hotSpot x=\"19.0\" y=\"0.0\" xunits=\"pixels\" yunits=\"pixels\"></kml:hotSpot></kml:IconStyle><kml:LabelStyle><kml:scale>1.0</kml:scale><kml:color>FF888800</kml:color><kml:colorMode>normal</kml:colorMode></kml:LabelStyle><kml:LineStyle><kml:width>2.0</kml:width><kml:color>FFFF00FF</kml:color><kml:colorMode>normal</kml:colorMode></kml:LineStyle></kml:Style></kml:Pair></kml:StyleMap>");
        
        sb.append("<kml:ExtendedData>");
        sb.append("  <kml:Data name=\"tactical_symbol_id\">");
        sb.append("    <kml:value>"+id+"</kml:value>");
        sb.append("  </kml:Data>");
        sb.append("</kml:ExtendedData>");
                
        sb.append("<kml:Point>");
        sb.append("<kml:extrude>").append(extrude ? "1" : "0").append("</kml:extrude>");
        sb.append("<kml:altitudeMode>").append(altitudeMode).append("</kml:altitudeMode>");
        sb.append("<kml:coordinates>");
        sb.append(coords.getLongitude().degrees).append(",");
        sb.append(coords.getLatitude().degrees).append(",");
        sb.append(coords.getElevation());
        sb.append("</kml:coordinates>");
        sb.append("</kml:Point>");
        sb.append("</kml:Placemark>");
//        sb.append("</kml:Folder>");
        endPrefixedDocument(sb);

        KMLRoot root = newParsedRoot(sb);
        KMLAbstractFeature document = root.getFeature();

//        KMLFolder folder = (KMLFolder) document;
//        List<KMLAbstractFeature> features = folder.getFeatures();
//        KMLPlacemark placemark = (KMLPlacemark) features.get(0);

//        KMLAbstractFeature feature = root.getFeature();
        KMLPlacemark placemark = (KMLPlacemark) document;

//        KMLAbstractGeometry geometry = ((KMLPlacemark) feature).getGeometry();
        KMLAbstractGeometry geometry = placemark.getGeometry();

        KMLPoint point = (KMLPoint) geometry;
        return point;
    }
    
    //Displays hex representation of displayed color
    private String colorToHex(Color color)
    {
        Integer r = color.getRed();
        Integer g = color.getGreen();
        Integer b = color.getBlue();
        Color hC;
        hC = new Color(r,g,b);
        String hex = Integer.toHexString(hC.getRGB() & 0xffffff);
        while(hex.length() < 6){
            hex = "0" + hex;
        }
        return hex;
    }    
    
    private String colorAlphaToHex(Color color)
    {
        Integer a = color.getAlpha();
        Integer r = color.getRed();
        Integer g = color.getGreen();
        Integer b = color.getBlue();
        Color hC;
        hC = new Color(r,g,b);
        String hex = String.format("%02X", a & 0xff);
        hex += String.format("%02X", b & 0xff);
        hex += String.format("%02X", g & 0xff);
        hex += String.format("%02X", r & 0xff);
        return hex;
    }    
    
    public KMLLineString createKMLPath(gov.nasa.worldwind.render.Path path,String name)
    {
        String altitudeMode = "clampToGround";
        boolean extrude = false;
        boolean tessellate = true;

        Color line_color = polyline.getAttributes().getOutlineMaterial().getDiffuse();
        double line_width = polyline.getAttributes().getOutlineWidth();
        String strLineColor = colorAlphaToHex(line_color);

        StringBuilder sb = newPrefixedDocument();
        sb.append("<kml:Placemark>");
        sb.append("<kml:name>"+name+"</kml:name><kml:visibility>1</kml:visibility><kml:Snippet>Short description of Placemark</kml:Snippet>");
//        sb.append("<Style id=\"s_ylw-pushpin_hl\"><IconStyle><scale>1.3</scale><Icon><href>http://maps.google.com/mapfiles/kml/pushpin/ylw-pushpin.png</href></Icon><hotSpot x=\"20\" y=\"2\" xunits=\"pixels\" yunits=\"pixels\"/></IconStyle><LineStyle><color>cc7fffff</color><width>1.5</width></LineStyle></Style><StyleMap id=\"m_ylw-pushpin\"><Pair><key>normal</key><styleUrl>#s_ylw-pushpin</styleUrl></Pair><Pair> <key>highlight</key><styleUrl>#s_ylw-pushpin_hl</styleUrl></Pair></StyleMap><Style id=\"s_ylw-pushpin\"><IconStyle><scale>1.1</scale><Icon><href>http://maps.google.com/mapfiles/kml/pushpin/ylw-pushpin.png</href></Icon><hotSpot x=\"20\" y=\"2\" xunits=\"pixels\" yunits=\"pixels\"/></IconStyle><LineStyle><color>"+strLineColor+"</color><width>"+line_width+"</width></LineStyle></Style>");
        sb.append("<kml:Style id=\"s_ylw-pushpin_hl\"><kml:LineStyle><kml:color>cc7fffff</kml:color><kml:width>1.5</kml:width></kml:LineStyle></kml:Style><kml:StyleMap id=\"m_ylw-pushpin\"><kml:Pair><kml:key>normal</kml:key><kml:styleUrl>#s_ylw-pushpin</kml:styleUrl></kml:Pair><kml:Pair><kml:key>highlight</kml:key><kml:styleUrl>#s_ylw-pushpin_hl</kml:styleUrl></kml:Pair></kml:StyleMap><kml:Style id=\"s_ylw-pushpin\"><kml:LineStyle><kml:color>"+strLineColor+"</kml:color><kml:width>"+line_width+"</kml:width></kml:LineStyle></kml:Style>");
        sb.append("<kml:LineString>");
        sb.append("<kml:extrude>").append(extrude ? "1" : "0").append("</kml:extrude>");
        sb.append("<kml:tessellate>").append(tessellate ? "1" : "0").append("</kml:tessellate>");
        sb.append("<kml:altitudeMode>").append(altitudeMode).append("</kml:altitudeMode>");
        sb.append("<kml:coordinates>");
        for (Position p : path.getPositions())
        {
            sb.append(p.getLongitude().degrees).append(",");
            sb.append(p.getLatitude().degrees).append(",");
            sb.append(p.getElevation()).append(" ");
        }
        sb.append("</kml:coordinates>");
        sb.append("</kml:LineString>");
        sb.append("</kml:Placemark>");
        endPrefixedDocument(sb);

        KMLRoot root = this.newParsedRoot(sb);

        KMLAbstractFeature feature = root.getFeature();

        KMLAbstractGeometry geometry = ((KMLPlacemark) feature).getGeometry();

        KMLLineString ring = (KMLLineString) geometry;
        return ring;
    }
    
    public KMLPolygon createKMLPolygon(gov.nasa.worldwind.render.SurfacePolygon polygon,String name)
    {
        String altitudeMode = "clampToGround";
        boolean extrude = false;
        boolean tessellate = true;

        String outerAltitudeMode = "clampToGround";
        boolean outerExtrude = true;
        boolean outerTessellate = false;

        Color line_color = polygon.getAttributes().getOutlineMaterial().getDiffuse();
        double line_width = polygon.getAttributes().getOutlineWidth();
        String strLineColor = colorAlphaToHex(line_color);

        Color area_color = polygon.getAttributes().getInteriorMaterial().getDiffuse();
        String strAreaColor = colorAlphaToHex(area_color);
        
        StringBuilder sb = newPrefixedDocument();
        sb.append("<kml:Placemark>");
        sb.append("<kml:name>"+name+"</kml:name><kml:visibility>1</kml:visibility><kml:Snippet>Short description of Placemark</kml:Snippet>");
        sb.append("<kml:Style id=\"s_ylw-pushpin_hl\"><kml:LineStyle><kml:color>cc7fffff</kml:color><kml:width>1.5</kml:width></kml:LineStyle></kml:Style><kml:StyleMap id=\"m_ylw-pushpin\"><kml:Pair><kml:key>normal</kml:key><kml:styleUrl>#s_ylw-pushpin</kml:styleUrl></kml:Pair><kml:Pair><kml:key>highlight</kml:key><kml:styleUrl>#s_ylw-pushpin_hl</kml:styleUrl></kml:Pair></kml:StyleMap><kml:Style id=\"s_ylw-pushpin\"><kml:LineStyle><kml:color>"+strLineColor+"</kml:color><kml:width>"+line_width+"</kml:width></kml:LineStyle><kml:PolyStyle><kml:color>"+strAreaColor+"</kml:color></kml:PolyStyle></kml:Style>");
        sb.append("<kml:Polygon>");
        sb.append("<kml:extrude>").append(extrude ? "1" : "0").append("</kml:extrude>");
        sb.append("<kml:tessellate>").append(tessellate ? "1" : "0").append("</kml:tessellate>");
        sb.append("<kml:altitudeMode>").append(altitudeMode).append("</kml:altitudeMode>");

        sb.append("<kml:outerBoundaryIs>");
        sb.append("<kml:LinearRing>");
        sb.append("<kml:extrude>").append(outerExtrude ? "1" : "0").append("</kml:extrude>");
        sb.append("<kml:tessellate>").append(outerTessellate ? "1" : "0").append("</kml:tessellate>");
        sb.append("<kml:altitudeMode>").append(outerAltitudeMode).append("</kml:altitudeMode>");
        sb.append("<kml:coordinates>");
        LatLon p0 = null;
        for (LatLon p : polygon.getOuterBoundary())
        {
            if(p0 == null)  p0 = p;
            sb.append(p.getLongitude().degrees).append(",");
            sb.append(p.getLatitude().degrees).append(",");
            sb.append(0).append(" ");
        }
        sb.append(p0.getLongitude().degrees).append(",");
        sb.append(p0.getLatitude().degrees).append(",");
        sb.append(0).append(" ");
            
        sb.append("</kml:coordinates>");
        sb.append("</kml:LinearRing>");
        sb.append("</kml:outerBoundaryIs>");

        sb.append("</kml:Polygon>");
        sb.append("</kml:Placemark>");
        endPrefixedDocument(sb);

        KMLRoot root = this.newParsedRoot(sb);

        KMLAbstractFeature feature = root.getFeature();

        KMLAbstractGeometry geometry = ((KMLPlacemark) feature).getGeometry();

        KMLPolygon pgon = (KMLPolygon) geometry;
        return pgon;
    }
    
    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        String name = "Test";
        Vec4 tempVec4 = wwd.getView().getCenterPoint();
        Position position = wwd.getView().getGlobe().computePositionFromPoint(tempVec4);

        KMLPoint kmlPoint = createKMLPoint(position, name);
        KMLController kmlController = new KMLController(kmlPoint.getRoot());
        RenderableLayer kmlLayer = new RenderableLayer();
        kmlLayer.setName(name);
        kmlLayer.addRenderable(kmlController);
        wwd.getModel().getLayers().add(kmlLayer);
        wwd.redrawNow();
        
        LayerTreeNode layerNode = new LayerTreeNode(kmlLayer);
        TreeNode parent = treeLayout.getCurrTreeNode();
        if(parent == null)
            layerTree.getModel().addLayer(layerNode);
        else
            layerTree.getModel().addLayer(parent, layerNode);
        wwd.redrawNow();
        
/*
        PointPlacemark pp = new PointPlacemark(position);
        pp.setLabelText(name);
        pp.setValue(AVKey.DISPLAY_NAME, name);
        pp.setValue(AVKey.SHORT_DESCRIPTION, "Short description of Placemark");
        pp.setValue(AVKey.BALLOON_TEXT, "This is a Placemark.");
        pp.setLineEnabled(false);
        pp.setAltitudeMode(WorldWind.CLAMP_TO_GROUND);
        pp.setEnableDecluttering(false); // enable the placemark for decluttering
        pp.setEnableLabelPicking(true); // enable label picking for this placemark
        PointPlacemarkAttributes attrs = new PointPlacemarkAttributes();
        attrs.setImageAddress(Paths.get(strIconsPath, "ylw-pushpin.png").toString());
        attrs.setImageColor(new Color(1f, 1f, 1f, 1.0f));
        attrs.setLabelColor("ff888800");
        attrs.setLineMaterial(Material.MAGENTA);
        attrs.setLineWidth(2d);
        attrs.setUsePointAsDefaultImage(true);
        attrs.setScale(1.0);
        attrs.setAntiAliasHint(WorldWind.ANTIALIAS_FASTEST);
        attrs.setImageOffset(new Offset(19d, 0d, AVKey.PIXELS, AVKey.PIXELS));
        attrs.setLabelScale(1.0);
        pp.setAttributes(attrs);
        setHighlightAttributes(pp);
        
//        KMLPlacemark p = new KMLPlacemark(null);
//        p.getRenderables().add(p);
        
//        KMLPointPlacemarkImpl p = new KMLPointPlacemarkImpl(null);
//        p.getRenderables().add(p);
        
        // Load the document into a new layer.
        RenderableLayer kmlLayer = new RenderableLayer();
        kmlLayer.setName(name);
        kmlLayer.addRenderable(pp);
//        kmlLayer.addRenderable(p);
        wwd.getModel().getLayers().add(kmlLayer);
        wwd.redrawNow();
        
        mv_LayerTreeNode layerNode = new mv_LayerTreeNode(kmlLayer);
        layerTree.getModel().addLayer(treeLayout.getCurrTreeNode(), layerNode);
        wwd.redrawNow();
*/        
//        KMLDocument document = (KMLDocument) kmlDefaultRoot.getFeature();
//        document.addFeature(document);
        
//        document.addFeature(document);
//        KMLFolder folder = (KMLFolder) document;
//        folder.addFeature(folder);

//        PointPlacemark pp = new PointPlacemark(position);
//        pp.setLabelText(name);
//        pp.setValue(AVKey.DISPLAY_NAME, name);
//        pp.setValue(AVKey.SHORT_DESCRIPTION, "Short description of Placemark");
//        pp.setValue(AVKey.BALLOON_TEXT, "This is a Placemark.");
//        pp.setLineEnabled(false);
//        pp.setAltitudeMode(WorldWind.CLAMP_TO_GROUND);
//        pp.setEnableDecluttering(false); // enable the placemark for decluttering
//        pp.setEnableLabelPicking(true); // enable label picking for this placemark
//        PointPlacemarkAttributes attrs = new PointPlacemarkAttributes();
//        attrs.setImageAddress(Paths.get(strIconsPath, "ylw-pushpin.png").toString());
//        attrs.setImageColor(new Color(1f, 1f, 1f, 1.0f));
//        attrs.setLabelColor("ff888800");
//        attrs.setLineMaterial(Material.MAGENTA);
//        attrs.setLineWidth(2d);
//        attrs.setUsePointAsDefaultImage(true);
//        attrs.setScale(1.0);
//        attrs.setAntiAliasHint(WorldWind.ANTIALIAS_FASTEST);
//        attrs.setImageOffset(new Offset(19d, 0d, AVKey.PIXELS, AVKey.PIXELS));
//        attrs.setLabelScale(1.0);
//        pp.setAttributes(attrs);
//        setHighlightAttributes(pp);
        
        // Load the document into a new layer.
//        RenderableLayer kmlLayer = new RenderableLayer();
//        kmlLayer.setName(name);
//        kmlLayer.addRenderable(pp);
        
//        try {
//            addKMLLayer(kmlPoint.getRoot());
//            document.addFeature(kmlPoint.getRoot().getFeature());
//            mv_KMLLayerTreeNode layerNode = new mv_KMLLayerTreeNode(kmlLayer, kmlPoint.getRoot());
//            layerTree.getModel().addLayer(0, layerNode);
//            layerTree.getModel().getRoot().addChild(0, layerNode);
//            gov.nasa.worldwind.util.tree.TreePath treePath;
//            layerTree.getNode(treePath);
//            layerTree.getModel().refresh(wwd.getModel().getLayers());
            
//        KMLLayerTreeNode layerNode = new KMLLayerTreeNode(kmlLayer, kmlPoint.getRoot());
////        LayerTreeNode layerNode = new LayerTreeNode(kmlLayer);
//        layerTree.getModel().addLayer(layerNode);
//        layerTree.makeVisible(layerNode.getPath());
//        expandOpenContainers(layerNode, layerTree);
//        
//        // update sector
////        Sector sector = Sector.fromDegrees(pp.getPosition().getLatitude().getDegrees(),pp.getPosition().getLatitude().getDegrees(),pp.getPosition().getLongitude().getDegrees(),pp.getPosition().getLongitude().getDegrees());
////        kmlLayer.setValue("oghab.mapviewer.avKey.Sector", sector);
//
//        this.wwd.getModel().getLayers().add(kmlLayer);
//        } catch (XMLStreamException ex) {
//            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (IOException ex) {
//            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        JFrame frame = new JFrame("AppWindowUI");  
        frame.setSize(200, 300);  
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);  

        BasicApp mrpropre = new BasicApp();
        frame.setSize(new Dimension(1200, 800));
        AppWindowUI mainPanel = new AppWindowUI();
        frame.setContentPane(mainPanel);
//        mainPanel.addGraphicComponent(mrpropre.getWWD());
        mainPanel.addGraphicComponent(wwd);
        mainPanel.setLayerManager(mrpropre.getLayerManager());

        frame.setVisible(true);  
    }//GEN-LAST:event_jButton6ActionPerformed

    public void export(String mimeType, Object output, WWObjectImpl feature) throws IOException
    {
        if (mimeType == null)
        {
            String message = Logging.getMessage("nullValue.Format");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        if (output == null)
        {
            String message = Logging.getMessage("nullValue.OutputBufferIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        if (KMLConstants.KML_MIME_TYPE.equalsIgnoreCase(mimeType))
        {
            try
            {
                if(feature instanceof KMLPointPlacemarkImpl)
                    exportAsKML(output, (KMLPointPlacemarkImpl)feature);
                else
                if(feature instanceof KMLTacticalSymbolPlacemarkImpl)
                    exportAsKML(output, (KMLTacticalSymbolPlacemarkImpl)feature);
            }
            catch (XMLStreamException e)
            {
                Logging.logger().throwing(getClass().getName(), "export", e);
                throw new IOException(e);
            }
        }
        else
        {
            String message = Logging.getMessage("Export.UnsupportedFormat", mimeType);
            Logging.logger().warning(message);
            throw new UnsupportedOperationException(message);
        }
    }

    protected void exportAsKML(Object output, KMLPointPlacemarkImpl feature) throws IOException, XMLStreamException
    {
        XMLStreamWriter xmlWriter = null;
        XMLOutputFactory factory = XMLOutputFactory.newInstance();
        boolean closeWriterWhenFinished = true;

        if (output instanceof XMLStreamWriter)
        {
            xmlWriter = (XMLStreamWriter) output;
            closeWriterWhenFinished = false;
        }
        else if (output instanceof Writer)
        {
            xmlWriter = factory.createXMLStreamWriter((Writer) output);
        }
        else if (output instanceof OutputStream)
        {
            xmlWriter = factory.createXMLStreamWriter((OutputStream) output);
        }

        if (xmlWriter == null)
        {
            String message = Logging.getMessage("Export.UnsupportedOutputObject");
            Logging.logger().warning(message);
            throw new IllegalArgumentException(message);
        }

        xmlWriter.writeStartElement("Placemark");
        xmlWriter.writeStartElement("name");
        xmlWriter.writeCharacters(feature.parent.getName());
        xmlWriter.writeEndElement();

        String strVisibility = "1";
        if(feature.parent.getVisibility() != null)  strVisibility = kmlBoolean(feature.parent.getVisibility());
        xmlWriter.writeStartElement("visibility");
        xmlWriter.writeCharacters(strVisibility);
        xmlWriter.writeEndElement();

        String Snippet = feature.parent.getSnippetText();
        if (Snippet != null)
        {
            xmlWriter.writeStartElement("Snippet");
            xmlWriter.writeCharacters(Snippet);
            xmlWriter.writeEndElement();
        }
        
        KMLExtendedData ext_data = feature.parent.getExtendedData();
        if(ext_data != null)
        {
            xmlWriter.writeStartElement("ExtendedData");
            for(KMLData data:ext_data.getData())
            {
                if(Settings.bDebug)  System.out.println("<"+data.getName()+">"+data.getValue()+"</"+data.getName()+">");
                
                xmlWriter.writeStartElement("Data");
                xmlWriter.writeAttribute("name", data.getName());
                    xmlWriter.writeStartElement("value");
                    xmlWriter.writeCharacters(data.getValue());
                    xmlWriter.writeEndElement();
                xmlWriter.writeEndElement();
            }
            xmlWriter.writeEndElement();
        }

        String description = (String)feature.getValue(AVKey.DESCRIPTION);
        if (description != null)
        {
            xmlWriter.writeStartElement("description");
            xmlWriter.writeCharacters(description);
            xmlWriter.writeEndElement();
        }

        final PointPlacemarkAttributes normalAttributes = feature.getAttributes();
        final PointPlacemarkAttributes highlightAttributes = feature.getHighlightAttributes();

        // Write style map
        if (normalAttributes != null || highlightAttributes != null)
        {
            xmlWriter.writeStartElement("StyleMap");
            exportAttributesAsKML(xmlWriter, KMLConstants.NORMAL, normalAttributes);
            exportAttributesAsKML(xmlWriter, KMLConstants.HIGHLIGHT, highlightAttributes);
            xmlWriter.writeEndElement(); // StyleMap
        }

        // Write geometry
        xmlWriter.writeStartElement("Point");

        xmlWriter.writeStartElement("extrude");
        xmlWriter.writeCharacters(kmlBoolean(feature.isLineEnabled()));
        xmlWriter.writeEndElement();

        final String altitudeMode = KMLExportUtil.kmlAltitudeMode(feature.getAltitudeMode());
        xmlWriter.writeStartElement("altitudeMode");
        xmlWriter.writeCharacters(altitudeMode);
        xmlWriter.writeEndElement();

        final String coordString = String.format(Locale.US, "%f,%f,%f",
            feature.getPosition().getLongitude().getDegrees(),
            feature.getPosition().getLatitude().getDegrees(),
            feature.getPosition().getElevation());
        xmlWriter.writeStartElement("coordinates");
        xmlWriter.writeCharacters(coordString);
        xmlWriter.writeEndElement();

        xmlWriter.writeEndElement(); // Point
        xmlWriter.writeEndElement(); // Placemark

        xmlWriter.flush();
        if (closeWriterWhenFinished)
            xmlWriter.close();
    }

    protected void exportAsKML(Object output, KMLTacticalSymbolPlacemarkImpl feature) throws IOException, XMLStreamException
    {
        XMLStreamWriter xmlWriter = null;
        XMLOutputFactory factory = XMLOutputFactory.newInstance();
        boolean closeWriterWhenFinished = true;

        if (output instanceof XMLStreamWriter)
        {
            xmlWriter = (XMLStreamWriter) output;
            closeWriterWhenFinished = false;
        }
        else if (output instanceof Writer)
        {
            xmlWriter = factory.createXMLStreamWriter((Writer) output);
        }
        else if (output instanceof OutputStream)
        {
            xmlWriter = factory.createXMLStreamWriter((OutputStream) output);
        }

        if (xmlWriter == null)
        {
            String message = Logging.getMessage("Export.UnsupportedOutputObject");
            Logging.logger().warning(message);
            throw new IllegalArgumentException(message);
        }

        xmlWriter.writeStartElement("Placemark");
        xmlWriter.writeStartElement("name");
        xmlWriter.writeCharacters(feature.parent.getName());
        xmlWriter.writeEndElement();

        String strVisibility = "1";
        if(feature.parent.getVisibility() != null)  strVisibility = kmlBoolean(feature.parent.getVisibility());
        xmlWriter.writeStartElement("visibility");
        xmlWriter.writeCharacters(strVisibility);
        xmlWriter.writeEndElement();

        String Snippet = feature.parent.getSnippetText();
        if (Snippet != null)
        {
            xmlWriter.writeStartElement("Snippet");
            xmlWriter.writeCharacters(Snippet);
            xmlWriter.writeEndElement();
        }
        
        KMLExtendedData ext_data = feature.parent.getExtendedData();
        if(ext_data != null)
        {
            xmlWriter.writeStartElement("ExtendedData");
            for(KMLData data:ext_data.getData())
            {
                if(Settings.bDebug)  System.out.println("<"+data.getName()+">"+data.getValue()+"</"+data.getName()+">");
                
                xmlWriter.writeStartElement("Data");
                xmlWriter.writeAttribute("name", data.getName());
                    xmlWriter.writeStartElement("value");
                    xmlWriter.writeCharacters(data.getValue());
                    xmlWriter.writeEndElement();
                xmlWriter.writeEndElement();
            }
            xmlWriter.writeEndElement();
        }

        String description = (String)feature.getValue(AVKey.DESCRIPTION);
        if (description != null)
        {
            xmlWriter.writeStartElement("description");
            xmlWriter.writeCharacters(description);
            xmlWriter.writeEndElement();
        }

//        final PointPlacemarkAttributes normalAttributes = feature.getAttributes();
//        final PointPlacemarkAttributes highlightAttributes = feature.getHighlightAttributes();

        // Write style map
//        if (normalAttributes != null || highlightAttributes != null)
//        {
//            xmlWriter.writeStartElement("StyleMap");
//            exportAttributesAsKML(xmlWriter, KMLConstants.NORMAL, normalAttributes);
//            exportAttributesAsKML(xmlWriter, KMLConstants.HIGHLIGHT, highlightAttributes);
//            xmlWriter.writeEndElement(); // StyleMap
//        }

        // Write geometry
        xmlWriter.writeStartElement("Point");

//        xmlWriter.writeStartElement("extrude");
//        xmlWriter.writeCharacters(kmlBoolean(feature.isLineEnabled()));
//        xmlWriter.writeEndElement();

        final String altitudeMode = KMLExportUtil.kmlAltitudeMode(feature.getAltitudeMode());
        xmlWriter.writeStartElement("altitudeMode");
        xmlWriter.writeCharacters(altitudeMode);
        xmlWriter.writeEndElement();

        final String coordString = String.format(Locale.US, "%f,%f,%f",
            feature.getPosition().getLongitude().getDegrees(),
            feature.getPosition().getLatitude().getDegrees(),
            feature.getPosition().getElevation());
        xmlWriter.writeStartElement("coordinates");
        xmlWriter.writeCharacters(coordString);
        xmlWriter.writeEndElement();

        xmlWriter.writeEndElement(); // Point
        xmlWriter.writeEndElement(); // Placemark

        xmlWriter.flush();
        if (closeWriterWhenFinished)
            xmlWriter.close();
    }

    private void exportAttributesAsKML(XMLStreamWriter xmlWriter, String styleType, PointPlacemarkAttributes attributes)
        throws XMLStreamException, IOException
    {
        if (attributes != null)
        {
            xmlWriter.writeStartElement("Pair");
            xmlWriter.writeStartElement("key");
            xmlWriter.writeCharacters(styleType);
            xmlWriter.writeEndElement();

            attributes.export(KMLConstants.KML_MIME_TYPE, xmlWriter);
            xmlWriter.writeEndElement(); // Pair
        }
    }
    
    void export_node(KMLAbstractFeature obj1, KMLDocumentBuilder kmlBuilder) throws IOException
    {
        if(obj1 == null)  return;
        if(kmlBuilder == null)  return;

//        if(obj1 instanceof KMLPhotoOverlay)
//        {
//            KMLPhotoOverlay obj2 = (KMLPhotoOverlay)obj1;
//            if(Settings.bDebug)  System.out.println("obj2: "+obj2+", "+obj2.getName());
//
//            Exportable exportable = null;
//            try
//            {
//                exportable = (Exportable)obj2;
//                kmlBuilder.writeObjects(exportable);
//            }
//            catch(ClassCastException ex)
//            {
//                System.out.println("ClassCastException in export: "+ex);
//            }
//        }

//        if(obj1 instanceof KMLGroundOverlay)
//        {
//            KMLGroundOverlay obj2 = (KMLGroundOverlay)obj1;
//            if(Settings.bDebug)  System.out.println("obj2: "+obj2+", "+obj2.getName());
//
//            Exportable exportable = null;
//            try
//            {
//                exportable = (Exportable)obj2.getRenderable();
//                kmlBuilder.writeObjects(exportable);
//            }
//            catch(ClassCastException ex)
//            {
//                System.out.println("ClassCastException in export: "+ex);
//            }
//        }

//        if(obj1 instanceof KMLScreenOverlay)
//        {
//            KMLScreenOverlay obj2 = (KMLScreenOverlay)obj1;
//            if(Settings.bDebug)  System.out.println("obj2: "+obj2+", "+obj2.getName());
//
//            Exportable exportable = null;
//            try
//            {
//                exportable = (Exportable)obj2.getRenderable();
//                kmlBuilder.writeObjects(exportable);
//            }
//            catch(ClassCastException ex)
//            {
//                System.out.println("ClassCastException in export: "+ex);
//            }
//        }

//        if(obj1 instanceof KMLFolder)
//        {
//            KMLFolder obj2 = (KMLFolder)obj1;
//            if(Settings.bDebug)  System.out.println("obj2: "+obj2+", "+obj2.getName());
//
////            Exportable exportable = null;
////            try
////            {
////                exportable = (Exportable)obj2.getRenderable();
////                kmlBuilder.writeObjects(exportable);
////            }
////            catch(ClassCastException ex)
////            {
////                System.out.println("ClassCastException in export: "+ex);
////            }
//        }

        if(obj1 instanceof KMLPlacemark)
        {
            KMLPlacemark obj2 = (KMLPlacemark)obj1;
//            obj2.getExtendedData();
            List<KMLRenderable> renderables = obj2.getRenderables();
            if(renderables != null)
            {
                if(Settings.bDebug)  System.out.println("obj2: "+obj2+", "+obj2.getName()+", obj2.getRenderables() "+obj2.getRenderables());
                for(int i=0;i<renderables.size();i++)
                {
                    KMLRenderable obj3 = renderables.get(i);
                    if(Settings.bDebug)  System.out.println("obj3: "+obj3+", "+obj3.getClass());
                    if(obj3 instanceof KMLTacticalSymbolPlacemarkImpl)
                    {
                        KMLTacticalSymbolPlacemarkImpl exportable = (KMLTacticalSymbolPlacemarkImpl)obj3;
//                        kmlBuilder.writeObjects(exportable);
                        export(KMLConstants.KML_MIME_TYPE, kmlBuilder.writer, exportable);
                    }
                    else
                    if(obj3 instanceof KMLPointPlacemarkImpl)
                    {
                        KMLPointPlacemarkImpl exportable = (KMLPointPlacemarkImpl)obj3;
//                        kmlBuilder.writeObjects(exportable);
                        export(KMLConstants.KML_MIME_TYPE, kmlBuilder.writer, exportable);
                    }
                    else
                    if(obj3 instanceof Exportable)
                    {
                        Exportable exportable = (Exportable)obj3;
                        kmlBuilder.writeObjects(exportable);
                    }
                }
            }
        }
    }
    
    void exportChildFeatures(KMLAbstractFeature feature, KMLDocumentBuilder kmlBuilder) throws IOException, XMLStreamException
    {
        // If the root is a container, add its children
        if (feature instanceof KMLAbstractContainer)
        {
            KMLAbstractContainer container = (KMLAbstractContainer) feature;

//            kmlBuilder.writer.writeStartElement("Folder");
//
//            kmlBuilder.writer.writeStartElement("name");
//            kmlBuilder.writer.writeCharacters(container.getName());
//            kmlBuilder.writer.writeEndElement();
//
//            Boolean open = container.getOpen();
//            if(open != null)
//            {
//                kmlBuilder.writer.writeStartElement("open");
//                kmlBuilder.writer.writeCharacters(kmlBoolean(open));
//                kmlBuilder.writer.writeEndElement();
//            }

            for (KMLAbstractFeature child : container.getFeatures())
            {
                exportChildFeatures(child, kmlBuilder);
            }

//            kmlBuilder.writer.writeEndElement();
        }
        else
        {
            export_node(feature, kmlBuilder);
        }
    }
    
    void scanChildren(TreeNode node, KMLDocumentBuilder kmlBuilder) throws IOException, XMLStreamException
    {
        for (TreeNode child : node.getChildren())
        {
            TreeNode layerTreeNode = (TreeNode)child;
            if(layerTreeNode.isLeaf())
            {
                RenderableLayer layer = null;
                if(layerTreeNode instanceof KMLLayerTreeNode)
                {
                    KMLLayerTreeNode feature = (KMLLayerTreeNode)layerTreeNode;
//                    exportChildFeatures(feature, kmlBuilder);
                }
                else
                if(layerTreeNode instanceof KMLFeatureTreeNode)
                {
                    KMLFeatureTreeNode feature = (KMLFeatureTreeNode)layerTreeNode;
                    exportChildFeatures(feature.getFeature(), kmlBuilder);
                }
                else
//                if(layerTreeNode instanceof KMLLayerTreeNode)
//                {
//                    KMLLayerTreeNode feature = (KMLLayerTreeNode)layerTreeNode;
//                    exportChildFeatures(feature.getLayer(), kmlBuilder);
//                }
                if(layerTreeNode instanceof LayerTreeNode)
                {
                    layer = (RenderableLayer) ((LayerTreeNode)layerTreeNode).getLayer();
                    if(layer != null)
                    {
                        if(layer.getRenderables() != null)
                        {
                            Iterator<Renderable> t = layer.getRenderables().iterator();
                            while(t.hasNext()){
                                Object obj = t.next();
                                if(Settings.bDebug)  System.out.println("obj: "+obj.toString());
                                if (obj instanceof KMLController){
                                    KMLController controller = (KMLController)obj;
                                    exportChildFeatures(controller.getKmlRoot().getFeature(), kmlBuilder);
                                }
                            }  
                        }
                    }
                }
            }
            else
            {
                if(!is_first_folder)
                {
                    kmlBuilder.writer.writeStartElement("Folder");

                    kmlBuilder.writer.writeStartElement("name");
                    kmlBuilder.writer.writeCharacters(layerTreeNode.getText());
                    kmlBuilder.writer.writeEndElement();

                    Boolean open = Boolean.FALSE;
                    if(open != null)
                    {
                        kmlBuilder.writer.writeStartElement("open");
                        kmlBuilder.writer.writeCharacters(kmlBoolean(open));
                        kmlBuilder.writer.writeEndElement();
                    }
                    n_folder_counter++;
                }
                is_first_folder = false;

                scanChildren(layerTreeNode, kmlBuilder);

                if((!is_first_folder) && (n_folder_counter > 0))
                {
                    kmlBuilder.writer.writeEndElement();
                    n_folder_counter--;
                }
            }
        }
    }
    
    static boolean is_first_folder = true;
    static int n_folder_counter = 0;
    void saveLayerTree() throws XMLStreamException, IOException
    {
        // Create a StringWriter to collect KML in a string buffer
        Writer stringWriter = new StringWriter();

        // Create a document builder that will write KML to the StringWriter
        KMLDocumentBuilder kmlBuilder = new KMLDocumentBuilder(stringWriter);

        TreeNode node = layerTree.getModel().getRoot();
        is_first_folder = true;
        n_folder_counter = 0;
        scanChildren(node, kmlBuilder);

        kmlBuilder.close();

        String strFilename = Paths.get(strDataPath, "kml", "My Places.kml").toString();
        FileWriter fw = new FileWriter(strFilename);
        fw.write(stringWriter.toString());
        fw.close();
    }
    
    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        try {
            File kmlFile = new File(Paths.get(strDataPath, "kml", "My Places.kml").toString());
            KMLRoot kmlRoot = KMLRoot.createAndParse(kmlFile);
            kmlDefaultRoot = kmlRoot;

            // Set the document's display name
            kmlRoot.setField(AVKey.DISPLAY_NAME, formName(kmlFile, kmlRoot));

            KMLController kmlController = new KMLController(kmlRoot);
            kmlController.getKmlRoot().requestRedraw();

            RenderableLayer kmlLayer = new RenderableLayer();
            kmlLayer.setName((String) kmlRoot.getField(AVKey.DISPLAY_NAME));
            wwd.getModel().getLayers().add(kmlLayer);
            kmlLayer.addRenderable(kmlController);
            wwd.redrawNow();
            //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
            // Adds a new layer tree node for the KMLRoot to the on-screen layer tree, and makes the new node visible
            // in the tree. This also expands any tree paths that represent open KML containers or open KML network
            // links.
            KMLLayerTreeNode layerNode = new KMLLayerTreeNode(kmlLayer, kmlRoot);
            TreeNode parent = treeLayout.getCurrTreeNode();
            if(parent == null)
                layerTree.getModel().addLayer(layerNode);
            else
                layerTree.getModel().addLayer(parent, layerNode);
            this.layerTree.makeVisible(layerNode.getPath());
            layerNode.expandOpenContainers(this.layerTree);
            treeLayout.invalidate();
            wwd.redrawNow();
            //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
//            saveLayerTree();
            //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        } catch (IOException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        } catch (XMLStreamException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButton7ActionPerformed

    private void MI_AddFolderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MI_AddFolderActionPerformed
        MI_AddFolder.setEnabled(false);
        propertiesFrame.TF_Name.setText("Untitled Folder");
        propertiesFrame.setVisible(true);
    }//GEN-LAST:event_MI_AddFolderActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        MI_AddPlacemarkActionPerformed(evt);
    }//GEN-LAST:event_jButton8ActionPerformed

    private void MI_AddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MI_AddActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_MI_AddActionPerformed

    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton10ActionPerformed
        MI_AddPathActionPerformed(evt);
    }//GEN-LAST:event_jButton10ActionPerformed

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
        MI_AddPolygonActionPerformed(evt);
    }//GEN-LAST:event_jButton9ActionPerformed

    private void jButton11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton11ActionPerformed
        MI_AddFolderActionPerformed(evt);
    }//GEN-LAST:event_jButton11ActionPerformed

    private void MI_AboutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MI_AboutActionPerformed
//        AboutFrame aboutFrame = null;
//        aboutFrame = new AboutFrame();
//        aboutFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//        // Center the application on the screen.
//        Dimension prefSize = aboutFrame.getPreferredSize();
//        Dimension parentSize;
//        java.awt.Point parentLocation = new java.awt.Point(0, 0);
//        parentSize = Toolkit.getDefaultToolkit().getScreenSize();
//        int x = parentLocation.x + (parentSize.width - prefSize.width) / 2;
//        int y = parentLocation.y + (parentSize.height - prefSize.height) / 2;
//        aboutFrame.setLocation(x, y);
//        aboutFrame.setVisible(true);
    }//GEN-LAST:event_MI_AboutActionPerformed

    private void jButton12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton12ActionPerformed
        MI_MeasureActionPerformed(evt);
    }//GEN-LAST:event_jButton12ActionPerformed

    private void jButton13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton13ActionPerformed
        MI_LayersActionPerformed(evt);
    }//GEN-LAST:event_jButton13ActionPerformed

    private void jButton14ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton14ActionPerformed
        MI_ContourLinesActionPerformed(evt);
    }//GEN-LAST:event_jButton14ActionPerformed

    private void jButton15ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton15ActionPerformed
        MI_OpenActionPerformed(evt);
    }//GEN-LAST:event_jButton15ActionPerformed

    private void jButton16ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton16ActionPerformed
        MI_ScreenShotActionPerformed(evt);
    }//GEN-LAST:event_jButton16ActionPerformed

    private void jButton17ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton17ActionPerformed
        MI_ExitActionPerformed(evt);
    }//GEN-LAST:event_jButton17ActionPerformed

    public void SaveImage(String base,String overlay, String output)
    {
        try
        {
            BufferedImage bi1,bi2;
            int w, h;
            bi1 = ImageIO.read(new File(base));
            bi2 = ImageIO.read(new File(overlay));
            w = bi1.getWidth(null);
            h = bi1.getHeight(null);
            if (bi1.getType() != BufferedImage.TYPE_INT_RGB) {
                BufferedImage bi3 = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
                Graphics big = bi3.getGraphics();
                big.drawImage(bi1, 0, 0, null);
                big.drawImage(bi2, 0, 0, null);
                ImageIO.write(bi3, "png", new File(output));
            }
        } catch (IOException e) {
            System.out.println("Image could not be read");
            System.exit(1);
        }
    }

    public String[] getFormats() {
        String[] formats = ImageIO.getWriterFormatNames();
        TreeSet<String> formatSet = new TreeSet<String>();
        for (String s : formats) {
            formatSet.add(s.toLowerCase());
        }
        return formatSet.toArray(new String[0]);
    }
    
    private void jButton18ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton18ActionPerformed
        String[] formats = getFormats();
//        ImageIcon BothIcon = new ImageIcon("C:\\SAS\\SAS.Planet.Nightly.200718.10081\\cache_ma\\Both\\19\\315172\\210194.png");
        String base = "C:\\SAS\\SAS.Planet.Nightly.200718.10081\\cache_ma\\sat\\19\\314957\\210299.jpg";
        String overlay = "C:\\SAS\\SAS.Planet.Nightly.200718.10081\\cache_ma\\Both\\19\\315172\\210194.png";
        String output = "d:/sat.png";
        SaveImage(base,overlay, output);
    }//GEN-LAST:event_jButton18ActionPerformed

    private void jButton19ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton19ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton19ActionPerformed

    private void CB_VisibleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CB_VisibleActionPerformed
        Object obj6 = currObject;
        if(obj6 instanceof gov.nasa.worldwind.render.PointPlacemark)
        {
            gov.nasa.worldwind.render.PointPlacemark obj7 = (gov.nasa.worldwind.render.PointPlacemark)obj6;
            if(Settings.bDebug)  System.out.println("obj7: "+obj7);
            obj7.setVisible(CB_Visible.isSelected());
            wwd.redraw();
	}
        if(obj6 instanceof gov.nasa.worldwind.render.AbstractShape)
        {
            gov.nasa.worldwind.render.AbstractShape obj7 = (gov.nasa.worldwind.render.AbstractShape)obj6;
            if(Settings.bDebug)  System.out.println("obj7: "+obj7);
            obj7.setVisible(CB_Visible.isSelected());
            wwd.redraw();
        }
        if(obj6 instanceof gov.nasa.worldwind.render.AbstractSurfaceObject)
        {
            gov.nasa.worldwind.render.AbstractSurfaceObject obj7 = (gov.nasa.worldwind.render.AbstractSurfaceObject)obj6;
            if(Settings.bDebug)  System.out.println("obj7: "+obj7);
            obj7.setVisible(CB_Visible.isSelected());
            wwd.redraw();
        }
    }//GEN-LAST:event_CB_VisibleActionPerformed

    private void MI_ZoomInActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MI_ZoomInActionPerformed
        if (orbitView.isAnimating())
        {
            orbitView.stopAnimations();
        }
        double zoom = orbitView.getZoom() - Settings.dZoomStep;
        if(zoom < 0)    zoom = 0;
        orbitView.setZoom(zoom);
        wwd.redraw();
    }//GEN-LAST:event_MI_ZoomInActionPerformed

    private void MI_ZoomOutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MI_ZoomOutActionPerformed
        if (orbitView.isAnimating())
        {
            orbitView.stopAnimations();
        }
        orbitView.setZoom(orbitView.getZoom() + Settings.dZoomStep);
        wwd.redraw();
    }//GEN-LAST:event_MI_ZoomOutActionPerformed

    private void jButton20ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton20ActionPerformed
        MI_ZoomInActionPerformed(evt);
    }//GEN-LAST:event_jButton20ActionPerformed

    private void jButton21ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton21ActionPerformed
        MI_ZoomOutActionPerformed(evt);
    }//GEN-LAST:event_jButton21ActionPerformed

    private void MI_AddTacticalSymbolActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MI_AddTacticalSymbolActionPerformed
        MI_AddTacticalSymbol.setEnabled(false);
        propertiesFrame.TF_Name.setText("Untitled Tactical Symbol");
        propertiesFrame.TF_IconPath.setText("SFAPMFQM------A");
        propertiesFrame.B_Ok.setEnabled(true);
        propertiesFrame.setVisible(true);
    }//GEN-LAST:event_MI_AddTacticalSymbolActionPerformed

    private void jButton22ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton22ActionPerformed
        MI_AddTacticalSymbolActionPerformed(evt);
    }//GEN-LAST:event_jButton22ActionPerformed

    private void MI_ExportOSMTilesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MI_ExportOSMTilesActionPerformed
        if (Desktop.isDesktopSupported()) {
            try {
                File myFile = new File(Paths.get(strDataPath, "help", "export_osm_tiles.pdf").toString());
                Desktop.getDesktop().open(myFile);
            } catch (IOException ex) {
                // no application registered for PDFs
            }
        }
    }//GEN-LAST:event_MI_ExportOSMTilesActionPerformed

    private void MI_MilStd2025CActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MI_MilStd2025CActionPerformed
        if (Desktop.isDesktopSupported()) {
            try {
                File myFile = new File(Paths.get(strDataPath, "help", "Mil-STD-2525C.pdf").toString());
                Desktop.getDesktop().open(myFile);
            } catch (IOException ex) {
                // no application registered for PDFs
            }
        }
    }//GEN-LAST:event_MI_MilStd2025CActionPerformed

    private void MI_SearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MI_SearchActionPerformed
        SearchFrame searchFrame = new SearchFrame();
        searchFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        // Center the application on the screen.
        Dimension prefSize = searchFrame.getPreferredSize();
        Dimension parentSize;
        java.awt.Point parentLocation = new java.awt.Point(0, 0);
        parentSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = parentLocation.x + (parentSize.width - prefSize.width) / 2;
        int y = parentLocation.y + (parentSize.height - prefSize.height) / 2;
        searchFrame.setLocation(x, y);
        
        searchFrame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {

            }
        });                
        
        searchFrame.B_Search.doClick();
        searchFrame.setVisible(true);
    }//GEN-LAST:event_MI_SearchActionPerformed

    private void jButton23ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton23ActionPerformed
        MI_SearchActionPerformed(evt);
    }//GEN-LAST:event_jButton23ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
//        try {
//            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
//                if ("Nimbus".equals(info.getName())) {
//                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
//                    break;
//                }
//            }
//        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
//            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        }
        //</editor-fold>
        
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox CB_FlyView;
    private javax.swing.JCheckBox CB_Visible;
    private javax.swing.JMenu IM_Help;
    private javax.swing.JMenuItem MI_About;
    private javax.swing.JMenu MI_Add;
    private javax.swing.JMenuItem MI_AddFolder;
    public javax.swing.JMenuItem MI_AddPath;
    private javax.swing.JMenuItem MI_AddPlacemark;
    private javax.swing.JMenuItem MI_AddPolygon;
    private javax.swing.JMenuItem MI_AddTacticalSymbol;
    private javax.swing.JMenuItem MI_Arabic;
    private javax.swing.JMenuItem MI_CacheManager;
    private javax.swing.JMenu MI_Contents;
    private javax.swing.JMenuItem MI_ContourLines;
    private javax.swing.JMenuItem MI_Copy;
    private javax.swing.JMenuItem MI_Cut;
    private javax.swing.JMenuItem MI_DDSConverter;
    private javax.swing.JMenu MI_Data;
    private javax.swing.JMenuItem MI_Delete;
    private javax.swing.JMenuItem MI_Device;
    private javax.swing.JMenuItem MI_Download;
    private javax.swing.JMenu MI_Edit;
    private javax.swing.JMenuItem MI_English;
    private javax.swing.JMenuItem MI_Exit;
    private javax.swing.JMenuItem MI_Export;
    private javax.swing.JMenuItem MI_ExportOSMTiles;
    private javax.swing.JMenu MI_File;
    private javax.swing.JMenuItem MI_FlyView;
    private javax.swing.JMenuItem MI_ImportImage;
    private javax.swing.JMenuItem MI_ImportRPF;
    private javax.swing.JMenuItem MI_InstalledData;
    private javax.swing.JMenu MI_Language;
    private javax.swing.JMenuItem MI_Layers;
    private javax.swing.JMenuItem MI_Measure;
    private javax.swing.JMenuItem MI_MilStd2025C;
    private javax.swing.JMenu MI_Navigation;
    private javax.swing.JMenuItem MI_None;
    private javax.swing.JMenuItem MI_Open;
    private javax.swing.JMenuItem MI_OrbitView;
    private javax.swing.JMenuItem MI_Paste;
    private javax.swing.JMenuItem MI_Properties;
    private javax.swing.JMenuItem MI_RedBlue;
    private javax.swing.JMenuItem MI_SaveAs;
    private javax.swing.JMenuItem MI_SavePlaceAs;
    private javax.swing.JMenuItem MI_ScreenShot;
    private javax.swing.JMenuItem MI_Search;
    private javax.swing.JMenuItem MI_Simulate;
    private javax.swing.JMenu MI_SteroMode;
    private javax.swing.JMenu MI_Tools;
    private javax.swing.JMenuItem MI_UTMGrid;
    private javax.swing.JMenu MI_View;
    private javax.swing.JMenuItem MI_ZoomIn;
    private javax.swing.JMenuItem MI_ZoomOut;
    private javax.swing.JMenuItem MenuItemCopy;
    private javax.swing.JMenuItem MenuItemCut;
    private javax.swing.JMenuItem MenuItemDelete;
    private javax.swing.JMenuItem MenuItemPaste;
    private javax.swing.JMenuItem MenuItemProperties;
    private javax.swing.JMenuItem MenuItemSavePlaceAs;
    private javax.swing.JMenuItem MenuItemSimulate;
    private static javax.swing.JPopupMenu PopupMenuEdit;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton12;
    private javax.swing.JButton jButton13;
    private javax.swing.JButton jButton14;
    private javax.swing.JButton jButton15;
    private javax.swing.JButton jButton16;
    private javax.swing.JButton jButton17;
    private javax.swing.JButton jButton18;
    private javax.swing.JButton jButton19;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton20;
    private javax.swing.JButton jButton21;
    private javax.swing.JButton jButton22;
    private javax.swing.JButton jButton23;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JFileChooser jFileChooser1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator10;
    private javax.swing.JToolBar.Separator jSeparator11;
    private javax.swing.JPopupMenu.Separator jSeparator12;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JToolBar.Separator jSeparator3;
    private javax.swing.JPopupMenu.Separator jSeparator4;
    private javax.swing.JPopupMenu.Separator jSeparator5;
    private javax.swing.JPopupMenu.Separator jSeparator6;
    private javax.swing.JToolBar.Separator jSeparator7;
    private javax.swing.JPopupMenu.Separator jSeparator8;
    private javax.swing.JToolBar.Separator jSeparator9;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JToolBar jToolBar2;
    // End of variables declaration//GEN-END:variables
}
