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

                int x = index % xWidth;
                int y = index / yWidth;
                int xi = x/xWidth;
                int yi = y/yWidth;
                int region = yi*sqrtRegions + xi;

                int val = (int)dataMatrix[index];
                dataMatrix[index] = LookupTable.getInstance().lookup(val, region);
            }
        }
        
        return dataMatrix;
    }
}
