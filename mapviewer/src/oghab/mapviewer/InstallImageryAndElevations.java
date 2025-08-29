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

import gov.nasa.worldwind.*;
import gov.nasa.worldwind.avlist.*;
import gov.nasa.worldwind.cache.FileStore;
import gov.nasa.worldwind.data.*;
import gov.nasa.worldwind.exception.WWRuntimeException;
import gov.nasa.worldwind.globes.Earth;
import gov.nasa.worldwind.util.*;
import gov.nasa.worldwindx.examples.ApplicationTemplate;
import org.w3c.dom.*;

import javax.swing.*;
import javax.xml.xpath.XPath;
import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.io.File;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Illustrates a simple application that installs imagery and elevation data for use in WorldWind. The application
 * enables the user to locate and install imagery or elevation data on the local hard drive. Once installed, the data is
 * visualized in WorldWind either as a <code>{@link gov.nasa.worldwind.layers.TiledImageLayer}</code> or an
 * <code>{@link gov.nasa.worldwind.globes.ElevationModel}</code>. The application also illustrates how to visualize data
 * that has been installed during a previous session.
 * <p>
 * For the simplest possible examples of installing imagery and elevation data, see the examples <code>{@link
 * InstallImagery}</code> and <code>{@link InstallElevations}</code>.
 *
 * @author dcollins
 * @version $Id: InstallImageryAndElevationsDemo.java 2915 2015-03-20 16:48:43Z tgaskins $
 */
public class InstallImageryAndElevations
{
    protected static void addInstalledData(final Document dataConfig, final AVList params,
        final InstalledDataPanel panel)
    {
        if (!SwingUtilities.isEventDispatchThread())
        {
            SwingUtilities.invokeLater(new Runnable()
            {
                public void run()
                {
                    addInstalledData(dataConfig, params, panel);
                }
            });
        }
        else
        {
            panel.addInstalledData(dataConfig.getDocumentElement(), params);
        }
    }

    //**************************************************************//
    //********************  Loading Previously Installed Data  *****//
    //**************************************************************//
    protected static void setFallbackParams(Document dataConfig, String filename, AVList params)
    {
        XPath xpath = WWXML.makeXPath();
        Element domElement = dataConfig.getDocumentElement();

        // If the data configuration document doesn't define a cache name, then compute one using the file's path
        // relative to its file cache directory.
        String s = WWXML.getText(domElement, "DataCacheName", xpath);
        if (s == null || s.length() == 0)
            DataConfigurationUtils.getDataConfigCacheName(filename, params);

        // If the data configuration document doesn't define the data's extreme elevations, provide default values using
        // the minimum and maximum elevations of Earth.
        String type = DataConfigurationUtils.getDataConfigType(domElement);
        if (type.equalsIgnoreCase("ElevationModel"))
        {
            if (WWXML.getDouble(domElement, "ExtremeElevations/@min", xpath) == null)
                params.setValue(AVKey.ELEVATION_MIN, Earth.ELEVATION_MIN);
            if (WWXML.getDouble(domElement, "ExtremeElevations/@max", xpath) == null)
                params.setValue(AVKey.ELEVATION_MAX, Earth.ELEVATION_MAX);
        }
    }

    //**************************************************************//
    //********************  Installing Data From File  *************//
    //**************************************************************//

    protected static Document createDataStore(File[] files,
        FileStore fileStore, DataStoreProducer producer) throws Exception
    {
        File installLocation = DataInstallUtil.getDefaultInstallLocation(fileStore);
        if (installLocation == null)
        {
            String message = Logging.getMessage("generic.NoDefaultImportLocation");
            Logging.logger().severe(message);
            return null;
        }

        // Create the production parameters. These parameters instruct the DataStoreProducer where to install the cached
        // data, and what name to put in the data configuration document.
        AVList params = new AVListImpl();

        String datasetName = askForDatasetName(suggestDatasetName(files));

        params.setValue(AVKey.DATASET_NAME, datasetName);
        params.setValue(AVKey.DATA_CACHE_NAME, datasetName);
        params.setValue(AVKey.FILE_STORE_LOCATION, installLocation.getAbsolutePath());

        // These parameters define producer's behavior:
        // create a full tile cache OR generate only first two low resolution levels
        boolean enableFullPyramid = Configuration.getBooleanValue(AVKey.PRODUCER_ENABLE_FULL_PYRAMID, false);
        if (!enableFullPyramid)
        {
            params.setValue(AVKey.SERVICE_NAME, AVKey.SERVICE_NAME_LOCAL_RASTER_SERVER);
            // retrieve the value of the AVKey.TILED_RASTER_PRODUCER_LIMIT_MAX_LEVEL, default to "Auto" if missing
            String maxLevel = Configuration.getStringValue(AVKey.TILED_RASTER_PRODUCER_LIMIT_MAX_LEVEL, "Auto");
            params.setValue(AVKey.TILED_RASTER_PRODUCER_LIMIT_MAX_LEVEL, maxLevel);
        }
        else
        {
            params.setValue(AVKey.PRODUCER_ENABLE_FULL_PYRAMID, true);
        }

        producer.setStoreParameters(params);

        try
        {
            for (File file : files)
            {
                producer.offerDataSource(file, null);
                Thread.yield();
            }

            // Convert the file to a form usable by WorldWind components, according to the specified DataStoreProducer.
            // This throws an exception if production fails for any reason.
            producer.startProduction();
        }
        catch (InterruptedException ie)
        {
            producer.removeProductionState();
            Thread.interrupted();
            throw ie;
        }
        catch (Exception e)
        {
            // Exception attempting to convert the file. Revert any change made during production.
            producer.removeProductionState();
            throw e;
        }

        // Return the DataConfiguration from the production results. Since production successfully completed, the
        // DataStoreProducer should contain a DataConfiguration in the production results. We test the production
        // results anyway.
        Iterable results = producer.getProductionResults();
        if (results != null && results.iterator() != null && results.iterator().hasNext())
        {
            Object o = results.iterator().next();
            if (o != null && o instanceof Document)
            {
                return (Document) o;
            }
        }

        return null;
    }

    protected static String askForDatasetName(String suggestedName)
    {
        String datasetName = suggestedName;

        for (; ; )
        {
            Object o = JOptionPane.showInputDialog(null, "Name:", "Enter dataset name",
                JOptionPane.QUESTION_MESSAGE, null, null, datasetName);

            if (!(o instanceof String)) // user canceled the input
            {
                Thread.interrupted();

                String msg = Logging.getMessage("generic.OperationCancelled", "Import");
                Logging.logger().info(msg);
                throw new WWRuntimeException(msg);
            }

            datasetName = WWIO.replaceIllegalFileNameCharacters((String) o);

            String message = "Import as `" + datasetName + "` ?";

            int userChoice = JOptionPane.showOptionDialog(
                null, // parentComponent
                message,
                null, // title
                JOptionPane.YES_NO_CANCEL_OPTION, // option type
                JOptionPane.QUESTION_MESSAGE, // message type
                null, // icon
                new Object[] {"Yes", "Edit name", "Cancel import"}, // options
                "Yes" // default option
            );

            if (userChoice == JOptionPane.YES_OPTION)
            {
                return datasetName;
            }
            else if (userChoice == JOptionPane.NO_OPTION)
            {
//                continue;
            }
            else if (userChoice == JOptionPane.CANCEL_OPTION)
            {
                Thread.interrupted();

                String msg = Logging.getMessage("generic.OperationCancelled", "Import");
                Logging.logger().info(msg);
                throw new WWRuntimeException(msg);
            }
        }
    }

    /**
     * Suggests a name for a dataset based on pathnames of the passed files.
     * <p>
     * Attempts to extract all common words that files' path can share, removes all non-alpha-numeric chars
     *
     * @param files Array of raster files
     *
     * @return A suggested name
     */
    protected static String suggestDatasetName(File[] files)
    {
        if (null == files || files.length == 0)
            return null;

        // extract file and folder names that all files have in common
        StringBuilder sb = new StringBuilder();
        for (File file : files)
        {
            String name = file.getAbsolutePath();
            if (WWUtil.isEmpty(name))
                continue;

            name = WWIO.replaceIllegalFileNameCharacters(WWIO.replaceSuffix(name, ""));

            if (sb.length() == 0)
            {
                sb.append(name);
                continue;
            }
            else
            {
                int size = Math.min(name.length(), sb.length());
                for (int i = 0; i < size; i++)
                {
                    if (name.charAt(i) != sb.charAt(i))
                    {
                        sb.setLength(i);
                        break;
                    }
                }
            }
        }

        String name = sb.toString();
        sb.setLength(0);

        ArrayList<String> words = new ArrayList<String>();

        StringTokenizer tokens = new StringTokenizer(name, " _:/\\-=!@#$%^&()[]{}|\".,<>;`+");
        String lastWord = null;
        while (tokens.hasMoreTokens())
        {
            String word = tokens.nextToken();
            // discard empty, one-char long, and duplicated keys
            if (WWUtil.isEmpty(word) || word.length() < 2 || word.equalsIgnoreCase(lastWord))
                continue;

            lastWord = word;

            words.add(word);
            if (words.size() > 4)  // let's keep only last four words
                words.remove(0);
        }

        if (words.size() > 0)
        {
            sb.setLength(0);
            for (String word : words)
            {
                sb.append(word).append(' ');
            }
            return sb.toString().trim();
        }
        else
            return (WWUtil.isEmpty(name)) ? "change me" : name;
    }

    //**************************************************************//
    //********************  Utility Methods  ***********************//
    //**************************************************************//

    /**
     * Creates an instance of the DataStoreProducer basing on raster type. Also validates that all rasters are the same
     * types.
     *
     * @param files Array of raster files
     *
     * @return instance of the DataStoreProducer
     *
     * @throws IllegalArgumentException if types of rasters do not match, or array of raster files is null or empty
     */
    protected static DataStoreProducer createDataStoreProducerFromFiles(File[] files) throws IllegalArgumentException
    {
        if (files == null || files.length == 0)
        {
            String message = Logging.getMessage("nullValue.ArrayIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        String commonPixelFormat = null;

        for (File file : files)
        {
            AVList params = new AVListImpl();
            if (DataInstallUtil.isDataRaster(file, params))
            {
                String pixelFormat = params.getStringValue(AVKey.PIXEL_FORMAT);
                if (WWUtil.isEmpty(commonPixelFormat))
                {
                    if (WWUtil.isEmpty(pixelFormat))
                    {
                        String message = Logging.getMessage("generic.UnrecognizedSourceType", file.getAbsolutePath());
                        Logging.logger().severe(message);
                        throw new IllegalArgumentException(message);
                    }
                    else
                    {
                        commonPixelFormat = pixelFormat;
                    }
                }
                else if (commonPixelFormat != null && !commonPixelFormat.equals(pixelFormat))
                {
                    if (WWUtil.isEmpty(pixelFormat))
                    {
                        String message = Logging.getMessage("generic.UnrecognizedSourceType", file.getAbsolutePath());
                        Logging.logger().severe(message);
                        throw new IllegalArgumentException(message);
                    }
                    else
                    {
                        String reason = Logging.getMessage("generic.UnexpectedRasterType", pixelFormat);
                        String details = file.getAbsolutePath() + ": " + reason;
                        String message = Logging.getMessage("DataRaster.IncompatibleRaster", details);
                        Logging.logger().severe(message);
                        throw new IllegalArgumentException(message);
                    }
                }
            }
            else if (DataInstallUtil.isWWDotNetLayerSet(file))
            {
                // you cannot select multiple WorldWind .NET Layer Sets
                // bail out on a first raster
                return new WWDotNetLayerSetConverter();
            }
        }

        if (AVKey.IMAGE.equals(commonPixelFormat))
        {
            return new TiledImageProducer();
        }
        else if (AVKey.ELEVATION.equals(commonPixelFormat))
        {
            return new TiledElevationProducer();
        }

        String message = Logging.getMessage("generic.UnexpectedRasterType", commonPixelFormat);
        Logging.logger().severe(message);
        throw new IllegalArgumentException(message);
    }

    //**************************************************************//
    //********************  Main Method  ***************************//
    //**************************************************************//
//    public static void main(String[] args)
//    {
//        ApplicationTemplate.start("WorldWind Imagery and Elevation Installation", AppFrame.class);
//    }
}
