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

import com.jogamp.opengl.util.texture.*;
import com.jogamp.opengl.util.texture.awt.AWTTextureIO;
import gov.nasa.worldwind.*;
import gov.nasa.worldwind.avlist.*;
import gov.nasa.worldwind.cache.*;
import gov.nasa.worldwind.geom.*;
import gov.nasa.worldwind.layers.mercator.MercatorSector;
import gov.nasa.worldwind.render.DrawContext;
import gov.nasa.worldwind.retrieve.*;
import gov.nasa.worldwind.util.*;
import java.awt.Graphics;

import javax.imageio.ImageIO;
import java.awt.image.*;
import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Stream;
import org.jcodec.common.io.IOUtils;

/**
 * BasicTiledImageLayer modified 2009-02-03 to add support for Mercator projections.
 *
 * @author tag
 * @version $Id: BasicMercatorTiledImageLayer.java 1171 2013-02-11 21:45:02Z dcollins $
 */
public class myBasicMercatorTiledImageLayer extends myMercatorTiledImageLayer
{
    private final Object fileLock = new Object();

    public String zipFilePath;
    public String zipOverlayPath;
    
    public String rootPath;
    public String strBaseExtension;
    public String strOverlayExtension;

    public String strType;

    public FileSystem fs;
    public FileSystem fs_overlay;

    public boolean isZipFile = true;

    public myBasicMercatorTiledImageLayer()
    {
        
    }

    public void setLevelSet2(LevelSet levelSet) throws IOException
    {
//        super(levelSet);
        setLevelSet1(levelSet);
        
//        zipFilePath = Path.of(strPath);
//        URI uri = URI.create("jar:file:/"+zipFilePath);
//        URI uri = URI.create("jar:file:/"+zipFilePath);
//        URI uri = URI.create("jar:file:/C:/Map/sat.zip");
//        URI uri = URI.create("jar:file:/C:/Map/Syria/Syria1_10.zip");
//        URI uri = URI.create("jar:file:/C:/Map/Syria/Syria1_11.zip");
//        URI uri = URI.create("jar:file:/C:/Map/Syria/Syria1_15.zip");
//        rootZipPath = fs.getPath("/");
//        System.out.println("[index]");
//        Files.list(rootZipPath)
//            .forEach(path -> System.out.println("file: "+path));

        if(zipFilePath != null) zipFilePath = zipFilePath.replace("\\","/");
//        System.out.println("myBasicMercatorTiledImageLayer(LevelSet levelSet): "+zipFilePath);

        if(zipOverlayPath != null) zipOverlayPath = zipOverlayPath.replace("\\","/");
//        System.out.println("myBasicMercatorTiledImageLayer(LevelSet levelSet): "+zipOverlayPath);

        if(isZipFile)
        {
            URI uri = URI.create("jar:file:/"+zipFilePath);
//            System.out.println("myBasicMercatorTiledImageLayer(LevelSet levelSet): "+uri);
//            Map<String, String> env = Map.of("create", "false");
            Map<String,String> env = new HashMap<String,String>();//AliSoft
            env.put("create", "false");
//            env.put("capacity", "16G");
//            env.put("blockSize", "4k");
            fs = FileSystems.newFileSystem(uri, env);
/*            
            try
            {
                fs.getFileStores().forEach((t1) -> {
                    System.out.println("filestore: "+t1);
                });

                fs.getRootDirectories().forEach((t2) -> {
                    System.out.println("Root: "+t2);
                    
//                Path path = Paths.get(pathString);
                    try {
                        Stream<Path> list;
                        list = Files.list(t2);
                        list.forEach((t3) -> {
                            System.out.println("file: "+t3+", exists: "+Files.exists(t3));
                            
                        });
//                        list.limit(5).forEach(System.out::println);                    
//                        list.forEach(System.out::println);                    
                    } catch (IOException ex) {
                        Logger.getLogger(myBasicMercatorTiledImageLayer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
                    }
                    
//                    Path path_base = fs.getPath("/");
//                    File directoryPath = path_base.toFile();
        //            File directoryPath = new File("D:\\ExampleDirectory");
//                    File directoryPath = t.getFileName().toFile();
//                    String contents[] = directoryPath.list();
//                    System.out.println("List of files and directories in the specified directory:");
//                    for(int i = 0; i < contents.length; i++) {
//                        System.out.println(contents[i]);
//                    }                                        
                });                
            }
            catch(Exception ex)
            {
                System.out.println(ex.getMessage());
            }
*/
            if(zipOverlayPath != null)
            {
                URI uri_overlay = URI.create("jar:file:/"+zipOverlayPath);
//                System.out.println("myBasicMercatorTiledImageLayer(LevelSet levelSet): "+uri_overlay);
//                Map<String, String> env_overlay = Map.of("create", "false");
                Map<String,String> env_overlay = new HashMap<String,String>();//AliSoft
                env_overlay.put("create", "false");
                fs_overlay = FileSystems.newFileSystem(uri_overlay, env_overlay);
            }
        }

        if (!WorldWind.getMemoryCacheSet().containsCache(myMercatorTextureTile.class.getName()))
        {
            long size = Configuration.getLongValue(
                AVKey.TEXTURE_IMAGE_CACHE_SIZE, 3000000L);
            MemoryCache cache = new BasicMemoryCache((long) (0.85 * size), size);
            cache.setName("Texture Tiles");
            WorldWind.getMemoryCacheSet().addCache(myMercatorTextureTile.class.getName(), cache);
        }
    }

    protected void forceTextureLoad(myMercatorTextureTile tile)
    {
        final URL textureURL = this.getDataFileStore().findFile(
            tile.getPath(), true);

        if (textureURL != null && !this.isTextureExpired(tile, textureURL))
        {
            this.loadTexture(tile, textureURL);
        }
    }

    protected void requestTexture(DrawContext dc, myMercatorTextureTile tile)
    {
        Vec4 centroid = tile.getCentroidPoint(dc.getGlobe());
        if (this.getReferencePoint() != null)
            tile.setPriority(centroid.distanceTo3(this.getReferencePoint()));

        RequestTask task = new RequestTask(tile, this);
        this.getRequestQ().add(task);
    }

    private static class RequestTask implements Runnable,
        Comparable<RequestTask>
    {
        private final myBasicMercatorTiledImageLayer layer;
        private final myMercatorTextureTile tile;

        private RequestTask(myMercatorTextureTile tile,
            myBasicMercatorTiledImageLayer layer)
        {
            this.layer = layer;
            this.tile = tile;
        }

        public void run()
        {
            try {
                this.layer.downloadTexture(this.tile);
            } catch (MalformedURLException ex) {
                Logger.getLogger(myBasicMercatorTiledImageLayer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            }

            // TODO: check to ensure load is still needed

//            final java.net.URL textureURL = this.layer.getDataFileStore()
//                .findFile(tile.getPath(), false);
//            if (textureURL != null
//                && !this.layer.isTextureExpired(tile, textureURL))
//            {
//                if (this.layer.loadTexture(tile, textureURL))
//                {
//                    layer.getLevels().unmarkResourceAbsent(tile);
//                    this.layer.firePropertyChange(AVKey.LAYER, null, this);
//                    return;
//                }
//                else
//                {
//                    // Assume that something's wrong with the file and delete it.
//                    this.layer.getDataFileStore().removeFile(
//                        textureURL);
//                    layer.getLevels().markResourceAbsent(tile);
//                    String message = Logging.getMessage(
//                        "generic.DeletedCorruptDataFile", textureURL);
//                    Logging.logger().info(message);
//                }
//            }
//
//            try {
//                System.out.println("AliSoft:myBasicMercatorTiledImageLayer:RequestTask: "+tile.getResourceURL().toString());
//            } catch (MalformedURLException ex) {
//                Logger.getLogger(myBasicMercatorTiledImageLayer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//            }
//            try {
//                this.layer.downloadTexture(this.tile);
//            } catch (MalformedURLException ex) {
//                Logger.getLogger(myBasicMercatorTiledImageLayer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//            }
        }

        /**
         * @param that the task to compare
         *
         * @return -1 if <code>this</code> less than <code>that</code>, 1 if greater than, 0 if equal
         *
         * @throws IllegalArgumentException if <code>that</code> is null
         */
        public int compareTo(RequestTask that)
        {
            if (that == null)
            {
                String msg = Logging.getMessage("nullValue.RequestTaskIsNull");
                Logging.logger().severe(msg);
                throw new IllegalArgumentException(msg);
            }
            return this.tile.getPriority() == that.tile.getPriority() ? 0
                : this.tile.getPriority() < that.tile.getPriority() ? -1
                    : 1;
        }

        public boolean equals(Object o)
        {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;

            final RequestTask that = (RequestTask) o;

            // Don't include layer in comparison so that requests are shared among layers
            return !(tile != null ? !tile.equals(that.tile) : that.tile != null);
        }

        public int hashCode()
        {
            return (tile != null ? tile.hashCode() : 0);
        }

        public String toString()
        {
            return this.tile.toString();
        }
    }

    private boolean isTextureExpired(myMercatorTextureTile tile,
        java.net.URL textureURL)
    {
        if (!WWIO.isFileOutOfDate(textureURL, tile.getLevel().getExpiryTime()))
            return false;

        // The file has expired. Delete it.
        this.getDataFileStore().removeFile(textureURL);
        String message = Logging.getMessage("generic.DataFileExpired",
            textureURL);
        Logging.logger().fine(message);
        return true;
    }

    private boolean loadTexture(myMercatorTextureTile tile,
        java.net.URL textureURL)
    {
        TextureData textureData;

        synchronized (this.fileLock)
        {
            textureData = readTexture(textureURL, this.isUseMipMaps());
        }

        if (textureData == null)
            return false;

        tile.setTextureData(textureData);
        if (tile.getLevelNumber() != 0 || !this.isRetainLevelZeroTiles())
            this.addTileToCache(tile);

        return true;
    }

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
            System.out.println("SaveImage: Image could not be read.");
        }
    }

    private TextureData readTexture(java.net.URL url, boolean useMipMaps)
    {
        try
        {
            String strFilename = url.toString();
            strFilename = strFilename.replace("file://","");
            strFilename = strFilename.replace("file:/","");
            if(isZipFile)
            {
                strFilename = "/"+strFilename;
//                System.out.println("AliSoft:myBasicMercatorTiledImageLayer:readTexture: "+strFilename);
//                File file2 = new File(strFilename);
//                Path path_base = file2.toPath();
                
                Path path_base = fs.getPath(strFilename);
//                path_base = path_base.resolve(strFilename);
                
//                strFilename = strFilename.replace("/","\\");
                
                Path path_overlay = null;
                if(fs_overlay != null)  path_overlay = fs_overlay.getPath(strFilename.replace(strBaseExtension,strOverlayExtension));
//                System.out.println("AliSoft:myBasicMercatorTiledImageLayer:readTexture: "+path);
//                System.out.println("file: "+path_base+", exists: "+Files.exists(path_base));
                if(Files.exists(path_base) && ((path_overlay != null) && Files.exists(path_overlay)))
                {
                    InputStream inputStream1 = Files.newInputStream(path_base);
                    
                    File targetFile1 = new File(Paths.get(MainFrame.strDataPath, "tmp", "base"+strBaseExtension).toString());
                    java.nio.file.Files.copy(
                      inputStream1,
                      targetFile1.toPath(), 
                      StandardCopyOption.REPLACE_EXISTING);
                    IOUtils.closeQuietly(inputStream1);                    
                    
                    InputStream inputStream2 = Files.newInputStream(path_overlay);            
                    File targetFile2 = new File(Paths.get(MainFrame.strDataPath, "tmp", "overlay"+strOverlayExtension).toString());
                    java.nio.file.Files.copy(
                      inputStream2, 
                      targetFile2.toPath(), 
                      StandardCopyOption.REPLACE_EXISTING);
                    IOUtils.closeQuietly(inputStream2);                    
                    
                    String mix = Paths.get(MainFrame.strDataPath, "tmp", "mix.png").toString();
                    SaveImage(targetFile1.getAbsolutePath(),targetFile2.getAbsolutePath(), mix);
                    
                    File file = new File(mix);
                    return OGLUtil.newTextureData(Configuration.getMaxCompatibleGLProfile(), file, useMipMaps);
                }
                else
                if(Files.exists(path_base))
                {
//                    System.out.println("AliSoft: readTexture: ["+path_base+"] found ...");
                    InputStream inputStream = Files.newInputStream(path_base);            
                    return OGLUtil.newTextureData(Configuration.getMaxCompatibleGLProfile(), inputStream, useMipMaps);
                }
                else
                {
//                    System.out.println("AliSoft: readTexture: ["+path+"] not found");
                    return null;
                }
            }
            else
            {
                File file = new File(strFilename);
                return OGLUtil.newTextureData(Configuration.getMaxCompatibleGLProfile(), file, useMipMaps);
            }
        }
        catch (Exception e)
        {
            String msg = Logging.getMessage("layers.TextureLayer.ExceptionAttemptingToReadTextureFile", url.toString());
            Logging.logger().log(java.util.logging.Level.SEVERE, msg, e);
            return null;
        }
    }

    private void addTileToCache(myMercatorTextureTile tile)
    {
        WorldWind.getMemoryCache(myMercatorTextureTile.class.getName()).add(
            tile.getTileKey(), tile);
    }

    protected void downloadTexture(final myMercatorTextureTile tile) throws MalformedURLException
    {
//        System.out.println("AliSoft:myBasicMercatorTiledImageLayer:downloadTexture: "+tile.getResourceURL().toString());
        this.loadTexture(tile, tile.getResourceURL());
        return;
//        if (!WorldWind.getRetrievalService().isAvailable())
//            return;
/*
        java.net.URL url;
        try
        {
            url = tile.getResourceURL();
            if (url == null)
                return;

//            if (WorldWind.getNetworkStatus().isHostUnavailable(url))
//                return;
        }
        catch (java.net.MalformedURLException e)
        {
            Logging.logger().log(
                java.util.logging.Level.SEVERE,
                Logging.getMessage(
                    "layers.TextureLayer.ExceptionCreatingTextureUrl",
                    tile), e);
            return;
        }

        Retriever retriever;

        System.out.println("AliSoft:myBasicMercatorTiledImageLayer:getProtocol: "+url.toString());
//AliSoft        
//        if ("http".equalsIgnoreCase(url.getProtocol()))
//        {
            retriever = new HTTPRetriever(url, new DownloadPostProcessor(tile, this));
            retriever.setValue(URLRetriever.EXTRACT_ZIP_ENTRY, "true"); // supports legacy layers
//        }
//        else
//        {
//            Logging.logger().severe(
//                Logging.getMessage("layers.TextureLayer.UnknownRetrievalProtocol", url.toString()));
//            return;
//        }

        // Apply any overridden timeouts.
        Integer cto = AVListImpl.getIntegerValue(this,
            AVKey.URL_CONNECT_TIMEOUT);
        if (cto != null && cto > 0)
            retriever.setConnectTimeout(cto);
        Integer cro = AVListImpl.getIntegerValue(this, AVKey.URL_READ_TIMEOUT);
        if (cro != null && cro > 0)
            retriever.setReadTimeout(cro);
        Integer srl = AVListImpl.getIntegerValue(this,
            AVKey.RETRIEVAL_QUEUE_STALE_REQUEST_LIMIT);
        if (srl != null && srl > 0)
            retriever.setStaleRequestLimit(srl);

        WorldWind.getRetrievalService().runRetriever(retriever,
            tile.getPriority());
*/        
    }

    private void saveBuffer(java.nio.ByteBuffer buffer, java.io.File outFile)
        throws java.io.IOException
    {
        synchronized (this.fileLock) // synchronized with read of file in RequestTask.run()
        {
            WWIO.saveBuffer(buffer, outFile);
        }
    }

    private static class DownloadPostProcessor implements
        RetrievalPostProcessor
    {
        // TODO: Rewrite this inner class, factoring out the generic parts.
        private final myMercatorTextureTile tile;
        private final myBasicMercatorTiledImageLayer layer;

        public DownloadPostProcessor(myMercatorTextureTile tile,
            myBasicMercatorTiledImageLayer layer)
        {
            this.tile = tile;
            this.layer = layer;
        }

        public ByteBuffer run(Retriever retriever)
        {
            ByteBuffer buffer = null;
            String contentType = "image";
            //                System.out.println("AliSoft:myBasicMercatorTiledImageLayer:DownloadPostProcessor: "+tile.getResourceURL().toString());
            
            try
            {
                RandomAccessFile aFile = new RandomAccessFile(tile.getResourceURL().toString(),"r");
                
                FileChannel inChannel = aFile.getChannel();
                long fileSize = inChannel.size();
                
                buffer = ByteBuffer.allocate((int) fileSize);
                inChannel.read(buffer);
                buffer.flip();
                
                inChannel.close();
                aFile.close();
            }
            catch (IOException exc)
            {
                System.out.println(exc);
                System.exit(1);
            }
            if (retriever == null)
            {
                String msg = Logging.getMessage("nullValue.RetrieverIsNull");
                Logging.logger().severe(msg);
                throw new IllegalArgumentException(msg);
            }

            try
            {
//                if (!retriever.getState().equals(
//                    Retriever.RETRIEVER_STATE_SUCCESSFUL))
//                    return null;
//
//                URLRetriever r = (URLRetriever) retriever;
//                ByteBuffer buffer = r.getBuffer();
//
//                if (retriever instanceof HTTPRetriever)
//                {
//                    HTTPRetriever htr = (HTTPRetriever) retriever;
//                    if (htr.getResponseCode() == HttpURLConnection.HTTP_NO_CONTENT)
//                    {
//                        // Mark tile as missing to avoid excessive attempts
//                        this.layer.getLevels().markResourceAbsent(this.tile);
//                        return null;
//                    }
//                    else if (htr.getResponseCode() != HttpURLConnection.HTTP_OK)
//                    {
//                        // Also mark tile as missing, but for an unknown reason.
//                        this.layer.getLevels().markResourceAbsent(this.tile);
//                        return null;
//                    }
//                }

                final File outFile = this.layer.getDataFileStore().newFile(
                    this.tile.getPath());
                if (outFile == null)
                    return null;

                if (outFile.exists())
                    return buffer;

                // TODO: Better, more generic and flexible handling of file-format type
                if (buffer != null)
                {
//                    String contentType = r.getContentType();
//                    if (contentType == null)
//                    {
//                        // TODO: logger message
//                        return null;
//                    }

                    if (contentType.contains("xml")
                        || contentType.contains("html")
                        || contentType.contains("text"))
                    {
                        this.layer.getLevels().markResourceAbsent(this.tile);

                        StringBuffer sb = new StringBuffer();
                        while (buffer.hasRemaining())
                        {
                            sb.append((char) buffer.get());
                        }
                        // TODO: parse out the message if the content is xml or html.
                        Logging.logger().severe(sb.toString());

                        return null;
                    }
                    else if (contentType.contains("dds"))
                    {
                        this.layer.saveBuffer(buffer, outFile);
                    }
                    else if (contentType.contains("zip"))
                    {
                        // Assume it's zipped DDS, which the retriever would have unzipped into the buffer.
                        this.layer.saveBuffer(buffer, outFile);
                    }
//                    else if (outFile.getName().endsWith(".dds"))
//                    {
//                        // Convert to DDS and save the result.
//                        buffer = DDSConverter.convertToDDS(buffer, contentType);
//                        if (buffer != null)
//                            this.layer.saveBuffer(buffer, outFile);
//                    }
                    else if (contentType.contains("image"))
                    {
                        BufferedImage image = this.layer.convertBufferToImage(buffer);
                        if (image != null)
                        {
                            image = this.layer.modifyImage(image);
                            if (this.layer.isTileValid(image))
                            {
                                if (!this.layer.transformAndSave(image, tile.getMercatorSector(), outFile))
                                    image = null;
                            }
                            else
                            {
                                this.layer.getLevels().markResourceAbsent(this.tile);
                                return null;
                            }
                        }
                        if (image == null)
                        {
                            // Just save whatever it is to the cache.
                            this.layer.saveBuffer(buffer, outFile);
                        }
                    }

                    if (buffer != null)
                    {
                        this.layer.firePropertyChange(AVKey.LAYER, null, this);
                    }
                    return buffer;
                }
            }
            catch (java.io.IOException e)
            {
                this.layer.getLevels().markResourceAbsent(this.tile);
                Logging.logger().log(java.util.logging.Level.SEVERE,
                    Logging.getMessage("layers.TextureLayer.ExceptionSavingRetrievedTextureFile", tile.getPath()), e);
            }
            return null;
        }
    }

    protected boolean isTileValid(BufferedImage image)
    {
        //override in subclass to check image tile
        //if false is returned, then tile is marked absent
        return true;
    }

    protected BufferedImage modifyImage(BufferedImage image)
    {
        //override in subclass to modify image tile
        return image;
    }

    private BufferedImage convertBufferToImage(ByteBuffer buffer)
    {
        try
        {
            InputStream is = new ByteArrayInputStream(buffer.array());
            return ImageIO.read(is);
        }
        catch (IOException e)
        {
            return null;
        }
    }

    private boolean transformAndSave(BufferedImage image, MercatorSector sector,
        File outFile)
    {
        try
        {
            image = transform(image, sector);
            String extension = outFile.getName().substring(
                outFile.getName().lastIndexOf('.') + 1);
            synchronized (this.fileLock) // synchronized with read of file in RequestTask.run()
            {
                return ImageIO.write(image, extension, outFile);
            }
        }
        catch (IOException e)
        {
            return false;
        }
    }

    private BufferedImage transform(BufferedImage image, MercatorSector sector)
    {
        int type = image.getType();
        if (type == 0)
            type = BufferedImage.TYPE_INT_RGB;
        BufferedImage trans = new BufferedImage(image.getWidth(), image
            .getHeight(), type);
        double miny = sector.getMinLatPercent();
        double maxy = sector.getMaxLatPercent();
        for (int y = 0; y < image.getHeight(); y++)
        {
            double sy = 1.0 - y / (double) (image.getHeight() - 1);
            Angle lat = Angle.fromRadians(sy * sector.getDeltaLatRadians()
                + sector.getMinLatitude().radians);
            double dy = 1.0 - (MercatorSector.gudermannianInverse(lat) - miny)
                / (maxy - miny);
            dy = Math.max(0.0, Math.min(1.0, dy));
            int iy = (int) (dy * (image.getHeight() - 1));

            for (int x = 0; x < image.getWidth(); x++)
            {
                trans.setRGB(x, y, image.getRGB(x, iy));
            }
        }
        return trans;
    }
}
