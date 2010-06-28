//
// GratingImagePanel.java
//

/*
 * Diffraction calibration of a SLM device by imaging the SLM in the far-field
 * on a CCD Camera.  Measuring the intensity of the first diffraction order.
 * Copyright (C) 2010-@year@ Gunnsteinn Hall @LOCI Labs.
 * University of Wisconsin - Madison.
 */

package loci.ao.slm.characterization.diffraction.main;

import java.awt.image.BufferedImage;

/**
 *
 * @author ghall
 */
public class GratingImagePanel extends ImagePanel {
    private static final int SLM_DIM = 512;
    private BufferedImage image;
    private double[] dataMatrix;

    public GratingImagePanel() {
        super();
        image = new BufferedImage(SLM_DIM, SLM_DIM,
                BufferedImage.TYPE_BYTE_GRAY);
        int slmSize = SLM_DIM;
        dataMatrix = new double[slmSize * slmSize];
    }

    /**
     * Upgrades the params for the grating image, and draws the image.
     *
     * @param numberOfBlocks The number of blocks.
     * @param refValue The reference value (0-255).
     * @param secondValue The variable value (0-255).
     * @param region The selected region (0-max).
     * @param numberOfRegions The number of regions.
     */
    public synchronized void setParams(
            int numberOfBlocks,
            double refValue,
            double secondValue,
            Integer region,
            Integer numberOfRegions) {

        dataMatrix = fGrating(
                numberOfBlocks, refValue, secondValue, region, numberOfRegions);

        for (int i = 0; i < SLM_DIM; i++) {
            for (int j = 0; j < SLM_DIM; j++) {
                int byteVal = (int)dataMatrix[j*SLM_DIM + i];
                byteVal &= 0xff;
                int rgbVal = (byteVal << 16) | (byteVal << 8) | byteVal;
                image.setRGB(i, j, rgbVal);
            }
        }

        setImage(image);
        repaint();
    }

    /**
     * Returns the last (most recently) generated data matrix.
     *
     * @return The last (most recently) generated data matrix.
     */
    public synchronized double[] getDataMatrix()
    {
        return dataMatrix;
    }

    /**
     * Generate a grayscale Ronchi-grating pattern.
     *
     * @param numberOfBlocks The number of grating blocks.
     * @param refValue The reference value (not changing).
     * @param secondValue The variable value (changing/variable).
     * @param region The currently selected region.
     * @param numberOfRegions The number of regions.
     * 
     * @return The output image matrix (SLM_DIM x SLM_DIM).
     */
    private synchronized double[] fGrating(
            int numberOfBlocks,
            double refValue,
            double secondValue,
            Integer region,
            Integer numberOfRegions) {
        int x, y;
        int slmSize, actSize, start, end;
        int x1, x2, y1, y2;
        int sqrtReg, xdim, ydim;
        int xreg, yreg;

        
        slmSize = SLM_DIM;
        int blockWidth = (int)(slmSize / numberOfBlocks);

        double total;
        int val;

        // Get the set slm size (specified in the window).
        actSize = slmSize;

        // Determine start and end positions of the SLM.
        start = (slmSize - actSize) / 2; // GH: 0
        end = start + actSize; // GH: 512 (OK, not included).

        /* Figure out a range of [x, y] values which define the region.
         * [x1, x2] and [y1, y2].
         */
        // Ideally numberOfRegions is a quadratic number.
        sqrtReg = (int)Math.sqrt(1.0*numberOfRegions);
        xdim = (int)(1.0f * slmSize / sqrtReg);
        ydim = (int)(1.0f * slmSize / sqrtReg);

        /*
         * 00 01 02 03
         * 04 05 06 07
         * 08 09 10 11
         * 12 13 14 15
         */
        xreg = region % sqrtReg;
        x1 = xreg * xdim;  x2 = x1 + xdim;
        yreg = (int)(region / sqrtReg);
        y1 = yreg * ydim; y2 = y1 + ydim;

        System.out.println("Grating, region: " + region);

        // Set the surface by polynomia parameters, pixel by pixel.
        // GH: row=0; row < 512; row++
        for (int row = 0; row < 512; row++) {
            //reset x

            // GH: col=0; col < 512; col++
            for (int col = 0; col < 512; col++) {
                // Build some terms that are repeated through the equations.
                int blockNumber = (int)((col) / blockWidth);
                int blockType = (blockNumber + 1) % 2;

                if (row >= y1 && row < y2 && col >= x1 && col < x2) {
                    if (blockType == 0) {
                        total = refValue;
                    } else {
                        total = secondValue;
                    }
                } else {
                    total = refValue;
                }

                val = LookupTable.getInstance().lookup((int)total, region);
                dataMatrix[row * slmSize + col] = val;
            }
        }
        
        return dataMatrix;
    }


}
