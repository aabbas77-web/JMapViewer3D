package samples.gdal;

/******************************************************************************
 * $Id$
 *
 * Name:     GDALOverviews.java
 * Project:  GDAL Java Interface
 * Purpose:  A sample app to create GDAL raster overviews.
 * Author:   Even Rouault, <even dot rouault at spatialys.com>
 *
 * Port from GDALOverviews.cs by Tamas Szekeres
 *
 ******************************************************************************
 * Copyright (c) 2009, Even Rouault
 * Copyright (c) 2007, Tamas Szekeres
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *****************************************************************************/

import gov.nasa.worldwind.util.Logging;
import org.gdal.gdalconst.gdalconst;
import org.gdal.gdal.gdal;
import org.gdal.gdal.Dataset;
import org.gdal.gdal.Band;
import org.gdal.gdal.Driver;
import org.gdal.gdal.TermProgressCallback;


/**
 * <p>Title: GDAL Java GDALOverviews example.</p>
 * <p>Description: A sample app to create GDAL raster overviews.</p>
 * @author Tamas Szekeres (szekerest@gmail.com)
 * @version 1.0
 */



/// <summary>
/// A Java based sample to create GDAL raster overviews.
/// </summary>

class GDALOverviews {

    public static void usage()

    {
            System.out.println("usage: gdaloverviews {GDAL dataset name} {resamplealg} {level1} {level2} ....");
            System.out.println("example: gdaloverviews sample.tif \"NEAREST\" 2 4");
            System.exit(-1);
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

    public static void main(String[] args)
    {
        System.out.println("GDAL_DATA: "+System.getenv("GDAL_DATA"));
        System.out.println("GDAL_DRIVER_PATH: "+System.getenv("GDAL_DRIVER_PATH"));
        /* -------------------------------------------------------------------- */
        /*      Register driver(s).                                             */
        /* -------------------------------------------------------------------- */
        gdal.AllRegister();
        listAllRegisteredDrivers();

//        args = gdal.GeneralCmdLineProcessor(args);
        
//        String[] args0 = {"d:\\SyriaTiff.tif", "NEAREST", "2", "4"};
//        String[] args0 = {"d:\\SyriaImg.img", "NEAREST", "2", "4"};
        String[] args0 = {"d:\\SyriaRect10.ecw", "NEAREST", "2", "4"};
        args = args0;
        
        if (args.length <= 2) usage();

        try
        {
            /* -------------------------------------------------------------------- */
            /*      Open dataset.                                                   */
            /* -------------------------------------------------------------------- */
//            Dataset ds = gdal.Open( args[0], gdalconst.GA_Update );
            Dataset ds = gdal.Open( args[0], gdalconst.GA_ReadOnly );

            if (ds == null)
            {
                System.out.println("Can't open " + args[0]);
                System.exit(-1);
            }
            System.out.println("["+args[0] + "] loaded sccessfully...");

            System.out.println("Raster dataset parameters:");
            System.out.println("  Projection: " + ds.GetProjectionRef());
            System.out.println("  RasterCount: " + ds.getRasterCount());
            System.out.println("  RasterSize (" + ds.getRasterXSize() + "," + ds.getRasterYSize() + ")");

//            int[] levels = new int[args.length -2];
//
//            System.out.println(levels.length);
//
//            for (int i = 2; i < args.length; i++)
//            {
//                levels[i-2] = Integer.parseInt(args[i]);
//            }
//
//            if (ds.BuildOverviews(args[1], levels, new TermProgressCallback()) != gdalconst.CE_None)
//            {
//                System.out.println("The BuildOverviews operation doesn't work");
//                System.exit(-1);
//            }

            /* -------------------------------------------------------------------- */
            /*      Displaying the raster parameters                                */
            /* -------------------------------------------------------------------- */
//            for (int iBand = 1; iBand <= ds.getRasterCount(); iBand++)
//            {
//                Band band = ds.GetRasterBand(iBand);
//                System.out.println("Band " + iBand + " :");
//                System.out.println("   DataType: " + band.getDataType());
//                System.out.println("   Size (" + band.getXSize() + "," + band.getYSize() + ")");
//                System.out.println("   PaletteInterp: " + gdal.GetColorInterpretationName(band.GetRasterColorInterpretation()));
//
//                for (int iOver = 0; iOver < band.GetOverviewCount(); iOver++)
//                {
//                    Band over = band.GetOverview(iOver);
//                    System.out.println("      OverView " + iOver + " :");
//                    System.out.println("         DataType: " + over.getDataType());
//                    System.out.println("         Size (" + over.getXSize() + "," + over.getYSize() + ")");
//                    System.out.println("         PaletteInterp: " + gdal.GetColorInterpretationName(over.GetRasterColorInterpretation()));
//                }
//            }

            /* explicit closing of dataset */
            ds.delete();

            System.out.println("Completed.");
//            System.out.println("Use:  gdalread " + args[0] + " outfile.png [overview] to extract a particular overview!" );
        }
        catch (Exception e)
        {
            System.out.println("Application error: " + e.getMessage());
        }
    }

}