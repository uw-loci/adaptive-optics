/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package loci.ao.slm.characterization.diffraction.main;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;

/**
 *
 * @author ghall
 */
public class ImageUtils {
    public static void addTilt(double[] dataMatrix, int tiltX, int tiltY) {
        int xWidth = 512;
        int yWidth = 512;

        for (int xm = 0; xm < 512; xm++) {
            for (int ym = 0; ym < 512; ym++) {

                int index = ym * xWidth + xm;

                int xr = xm - xWidth/2;
                int yr = ym - yWidth/2;

                dataMatrix[index] += tiltX * xr + tiltY * yr;
            }
        }
    }

    public static double[] imageToDataMatrix(BufferedImage image) {
        double[] dataMatrix = new double[image.getWidth()*image.getHeight()];

        Raster rasterData = image.getData();
        dataMatrix = rasterData.getPixels(0, 0, image.getWidth(), image.getHeight(), dataMatrix);

        int totRegions = LookupTable.getInstance().getNumberOfRegions();
        int sqrtRegions = (int)Math.sqrt(totRegions);
        if (sqrtRegions == 0) {
            sqrtRegions = 1;
        }
        int xWidth = image.getWidth() / sqrtRegions;
        int yWidth =  image.getHeight() / sqrtRegions;
        
        for (int row = 0; row < image.getHeight(); row++) {
            for (int col = 0; col < image.getWidth(); col++) {
                int index = row * image.getWidth() + col;

                int x = index % image.getWidth();
                int y = index / image.getHeight();
                int xi = x/xWidth;
                int yi = y/yWidth;
                int region = yi*sqrtRegions + xi;

                int val = (int)dataMatrix[index];
                val %= 256;

                //System.out.println("Val: " + val + " Region: " + region);
                dataMatrix[index] = LookupTable.getInstance().lookup(val, region);
            }
        }
        
        return dataMatrix;
    }
}
