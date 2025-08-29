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
import gov.nasa.worldwind.geom.*;
import gov.nasa.worldwind.layers.mercator.*;
import gov.nasa.worldwind.util.*;
import java.io.File;
import java.io.IOException;

import java.net.*;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

// Use SAS.Planet to Generate "Mobile Atlas Creator" format then compress it using "zip" format.

/**
 * @version $Id: MyOSMLayer.java 1171 2013-02-11 21:45:02Z dcollins $
 */
public class MyOSMZipLayer extends myBasicMercatorTiledImageLayer
{

    public MyOSMZipLayer()
    {

    }

    public MyOSMZipLayer(String zipBasePath, String strBaseExtension, String strType, String zipOverlayPath, String strOverlayExtension) throws IOException
    {
//        super(makeLevels(zipPath));
        setLevelSet2(makeLevels(zipBasePath, strBaseExtension, strType, zipOverlayPath, strOverlayExtension));
    }

    // 'c:/Map/sat/{z}/{x}/{y}.jpg'
    public LevelSet makeLevels(String zipBasePath, String strBaseExtension, String strType, String zipOverlayPath, String strOverlayExtension)
    {
//        zipFilePath = "C:\\Map\\Syria\\Syria1_15.zip";
//        zipFilePath = zipFilePath.replace("\\","/");
//        rootPath = "";
//        strExtension = ".jpg";

//        zipFilePath = "C:\\Map\\syria_png_1_13.zip";
//        zipFilePath = zipFilePath.replace("\\","/");
//        rootPath = "";
//        strExtension = ".png";

//        zipFilePath = "C:\\Map\\syria_png_1_16\\syria_png_1_16.zip";
        this.zipFilePath = zipBasePath;
        this.zipFilePath = this.zipFilePath.replace("\\","/");
        
        this.zipOverlayPath = zipOverlayPath;
        if(this.zipOverlayPath != null) this.zipOverlayPath = this.zipOverlayPath.replace("\\","/");
        rootPath = "";
//        strExtension = ".png";
        if(strBaseExtension != null)
        {
            if(strBaseExtension.startsWith("."))
                this.strBaseExtension = strBaseExtension;
            else
                this.strBaseExtension = "."+strBaseExtension;
        }

        if(strOverlayExtension != null)
        {
            if(strOverlayExtension.startsWith("."))
                this.strOverlayExtension = strOverlayExtension;
            else
                this.strOverlayExtension = "."+strOverlayExtension;
        }

        this.strType = strType;
        
//        this.zipOverlayPath = "D:\\Ali\\WorldWind\\MapViewer\\data\\maps\\zip\\png\\Both.zip";
//        this.zipOverlayPath = this.zipOverlayPath.replace("\\","/");
        
//        zipFilePath = "";
//        zipFilePath = zipFilePath.replace("\\","/");
//        rootPath = "c:/Map/jpg/";
//        strExtension = ".jpg";
        
//        zipFilePath = "";
//        zipFilePath = zipFilePath.replace("\\","/");
//        rootPath = "c:/Map/png/";
//        strExtension = ".png";

//        if(zipFilePath != null)
//            isZipFile = Files.exists(Path.of(zipFilePath));
//        else
            isZipFile = true;

        AVList params = new AVListImpl();

        params.setValue(AVKey.TILE_WIDTH, 256);
        params.setValue(AVKey.TILE_HEIGHT, 256);
        params.setValue(AVKey.DATA_CACHE_NAME, "Earth/OSM-Mercator/Oghab_MapViewer");
//        params.setValue(AVKey.SERVICE, "c:/Map/sat/");
        params.setValue(AVKey.SERVICE, rootPath);
        params.setValue(AVKey.DATASET_NAME, "h");
        params.setValue(AVKey.FORMAT_SUFFIX, strBaseExtension);
        params.setValue(AVKey.NUM_LEVELS, 20);
        params.setValue(AVKey.NUM_EMPTY_LEVELS, 0);
        params.setValue(AVKey.LEVEL_ZERO_TILE_DELTA, new LatLon(Angle.fromDegrees(22.5d), Angle.fromDegrees(45d)));
        params.setValue(AVKey.SECTOR, new MercatorSector(-1.0, 1.0, Angle.NEG180, Angle.POS180));
        params.setValue(AVKey.TILE_URL_BUILDER, new URLBuilder());

        return new LevelSet(params);
    }
    
//    public static final String fileSep = System.getProperty("file.separator");
    public static final String fileSep = "/";
    private static String get_SAS_CachePath(String cachePath, int x, int y, int zoom, String ext)
    {
        // sasplanet\cache\sat\z17\37\x38349\21\y22110.jpg
        // result:=path+'\z'+zoom+'\'+(x div 1024)+'\x'+x+'\'+(y div
        // 1024)+'\y'+y+ext;
        // u_TileFileNameSAS.pas
        return cachePath
                        + fileSep + "z" + zoom + fileSep
                        + (x / 1024) + fileSep + "x" + x
                        + fileSep + (y / 1024) + fileSep + "y"
                        + y + ext;
    }
    
    // Mobile Atlas Creator (MOBAC)
    private static String get_MOBAC_CachePath(String cachePath, int x, int y, int zoom, String ext)
    {
        // 'c:/Map/sat/{z}/{x}/{y}.jpg'
        // u_TileFileNameMOBAC.pas
        return cachePath
                        + fileSep + zoom + fileSep
                        + x + fileSep
                        + y
                        + ext;
    }
    
    // Mobile Atlas Creator (MOBAC)
    private static String get_TMS_CachePath(String cachePath, int x, int y, int zoom, String ext)
    {
        // 'c:/Map/sat/{z}/{x}/{y}.jpg'
        // u_TileFileNameTMS.pas
        return cachePath
                        + fileSep + zoom + fileSep
                        + x + fileSep
                        + ((1 << zoom) - 1 - y)
                        + ext;
    }
    
    private class URLBuilder implements TileUrlBuilder
    {
        public URL getURL(Tile tile, String imageFormat) throws MalformedURLException
        {
//            if(isZipFile)
//            {
//                String strPath = "file://"+tile.getLevel().getService()+"/"
//                    + (tile.getLevelNumber() + 3) + "/" + tile.getColumn() + "/"
//                    + ((1 << (tile.getLevelNumber()) + 3) - 1 - tile.getRow()) + strExtension;
//                URL url = new URL(strPath);
//                return url;
//            }
//            else
//            {
//                return new URL("file://"+tile.getLevel().getService()
//                    + (tile.getLevelNumber() + 3) + "/" + tile.getColumn() + "/"
//                    + ((1 << (tile.getLevelNumber()) + 3) - 1 - tile.getRow()) + strBaseExtension);
//                String strPath = getMOBACCachePath("file://"+tile.getLevel().getService(), tile.getColumn(), tile.getRow(), tile.getLevelNumber() + 3, strBaseExtension);
//                String strPath = getSASCachePath("file://"+tile.getLevel().getService(), tile.getColumn(), tile.getRow(), tile.getLevelNumber() + 3, strBaseExtension);
//                int row = (int) Math.pow(2, (tile.getLevelNumber() + 2)) - 1 - tile.getRow();
//                int row = (1 << (tile.getLevelNumber() + 3) - 1 - tile.getRow());
//                int row = ((tile.getLevelNumber() + 3) - 1 - tile.getRow());
//                int x = tile.getColumn();
//                int y = tile.getRow();
//                int zoom = tile.getLevelNumber() + 3;
                int zoom = tile.getLevelNumber() + 3;
                int x = tile.getColumn();
                int y = ((1 << zoom) - 1 - tile.getRow());
                String strPath;
                if(strType.toUpperCase().contains("SAS"))
                    strPath = get_SAS_CachePath("file://"+tile.getLevel().getService(), x, y, zoom+1, strBaseExtension);
                else
                if(strType.toUpperCase().contains("TMS"))
                    strPath = get_TMS_CachePath("file://"+tile.getLevel().getService(), x, y, zoom, strBaseExtension);
                else
                    strPath = get_MOBAC_CachePath("file://"+tile.getLevel().getService(), x, y, zoom, strBaseExtension);
//                System.out.println("strPath: "+strPath);
                return new URL(strPath);
//            }
        }
    }

    @Override
    public String toString()
    {
//        return "Oghab MapViewer";
        File f = new File(zipFilePath);
        return f.getName();
    }
}
