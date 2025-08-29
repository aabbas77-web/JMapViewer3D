/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package oghab.mapviewer;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.jcodec.api.SequenceEncoder;
import org.jcodec.common.Codec;
import org.jcodec.common.Format;
import org.jcodec.common.io.NIOUtils;
import org.jcodec.common.model.ColorSpace;
import org.jcodec.common.model.Rational;

/**
 *
 * @author AZUS
 */
public class VideoEncoder {
    SequenceEncoder enc = null;
    boolean is_started = false;
    
    VideoEncoder()
    {
//        File output = new File("d:/test.mp4");
////        SequenceEncoder enc = SequenceEncoder.createWithFps(NIOUtils.writableChannel(output), new Rational(1, 1));
//        Format outputFormat = Format.MOV;
//        Codec outputVideoCodec = Codec.H264;
//        Codec outputAudioCodec = Codec.AAC;
//        SequenceEncoder enc = new SequenceEncoder(NIOUtils.writableChannel(output), new Rational(1, 1), outputFormat, outputVideoCodec, outputAudioCodec);
//        String[] files = {"d:/png/DJI_0121.png", "d:/png/DJI_0122.png", "d:/png/DJI_0123.png"};
//        for (String file : files) {
//           enc.encodeNativeFrame(AWTUtil.decodePNG(new File(file), ColorSpace.RGB));
//        }
//        enc.finish();
        is_started = false;
    }
    
    void start(String filename, int fps)
    {
        try
        {
            File output = new File(filename);
            enc = SequenceEncoder.createWithFps(NIOUtils.writableChannel(output), new Rational(fps, 1));
    //        Format outputFormat = Format.MOV;
    //        Codec outputVideoCodec = Codec.H264;
    //        Codec outputAudioCodec = Codec.AAC;
    //        enc = new SequenceEncoder(NIOUtils.writableChannel(output), new Rational(1, 1), outputFormat, outputVideoCodec, outputAudioCodec);
            is_started = true;
        }
        catch(Exception ex)
        {
            
        }
    }
    
    void encode_png_image(String filename)
    {
        try
        {
            enc.encodeNativeFrame(AWTUtil.decodePNG(new File(filename), ColorSpace.RGB));
        }
        catch(Exception ex)
        {
            
        }
    }
    
    void encode_image(BufferedImage image)
    {
        try
        {
            enc.encodeNativeFrame(AWTUtil.fromBufferedImageRGB(image));
        }
        catch(Exception ex)
        {
            
        }
    }
    
    void stop()
    {
        try
        {
            if(enc != null)
            {
                enc.finish();
            }
            is_started = false;
        }
        catch(Exception ex)
        {
            
        }
    }
   
}
