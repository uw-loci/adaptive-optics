/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package loci.ao.slm.characterization.diffraction.main;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 *
 * @author ghall
 */
public class RegionModes {
    private static RegionModes instance = null;
    private ArrayList<RegionMode> modes;
    private int numberOfRegions;

    private RegionModes() {
        modes = new ArrayList<RegionMode>();
        numberOfRegions = 0;
    }

    public static RegionModes getInstance() {
        if (instance == null) {
            instance = new RegionModes();
        }
        return instance;
    }

    public void setNumberOfRegions(int numRegions) {
        if (numRegions != this.numberOfRegions) {
            modes.clear();
            numberOfRegions = numRegions;
        }
    }

    public RegionMode addMode() {
        RegionMode mode = new RegionMode();
        modes.add(mode);
        return mode;
    }

    public void removeModeByIndex(int modeIdx) {
        modes.remove(modeIdx);
    }


    public int countModes() {
        return modes.size();
    }

    public RegionMode getModeByIndex(int modeIndex) {
        return modes.get(modeIndex);
    }

    public ArrayList<RegionMode> getModes() {
        return modes;
    }

    public RegionMode getModeByRegion(int region) {
        RegionMode mode = null;
        for (int i = 0; i < countModes(); i++) {
            if (modes.get(i).region == region) {
                mode = modes.get(i);
                return mode;
            }
        }
        return mode;
    }

    public double[] generateDataMatrix() {
        double[] dataMatrix = new double[512*512];
        //BufferedImage image = null;

        for (int i = 0; i < 512*512; i++) {
            dataMatrix[i] = 0; // LookupTable.getInstance().lookup(0, 0);
        }

        int sqrtRegions = (int)Math.sqrt(1.0*numberOfRegions);
        int xWidth = 512 / sqrtRegions;
        int yWidth = 512 / sqrtRegions;
        
        for (int i = 0; i < countModes(); i++) {
            RegionMode mode = modes.get(i);

            int region = mode.region;
            int regX = region % sqrtRegions;
            int regY = region / sqrtRegions;
            int regXstart = regX*xWidth;
            int regXend = Math.min((regX+1)*xWidth, 512);
            int regYstart = regY*yWidth;
            int regYend = Math.min((regY+1)*yWidth, 512);
            for (int x=regXstart; x < regXend; x++) {
                for (int y=regYstart; y < regYend; y++) {
                    int index = y * 512 + x;

                    int val = (int) mode.getPhaseValue(x,y);
                    /*if (val < 0) {
                        while (val < 0) val += 256;
                    }
                    val %= 256;

                    // Translate through lookup table.  Single region lut only.
                    val = LookupTable.getInstance().lookup(val, 0);*/
                    dataMatrix[index] = val;
                }
            }
        }

        /*
        int lastRegion = -1;
        RegionMode mode = null;
        for (int xm = 0; xm < 512; xm++) {
            for (int ym = 0; ym < 512; ym++) {
                int index = ym * 512 + xm;

                int xi = xm/xWidth;
                int yi = ym/yWidth;
                int region = yi*sqrtRegions + xi;
                if (xm >= sqrtRegions*xWidth && ym >= sqrtRegions*yWidth) {
                    xi = sqrtRegions*xWidth;
                    region = sqrtRegions*sqrtRegions - 1;
                } else if (xm >= sqrtRegions*xWidth) {
                    region = yi*sqrtRegions + sqrtRegions - 1;
                } else if (ym >= sqrtRegions*yWidth) {
                    region = (sqrtRegions-1)*sqrtRegions + xi;
                }

                if (region != lastRegion) {
                    mode = getModeByRegion(region);
                }
                lastRegion = region;
                
                int val = 0;
                if (mode != null) {
                    val = (int) mode.getPhaseValue(xm,ym);
                }

                dataMatrix[index] = val;
            }
        }*/

        return dataMatrix;
    }

    public class RegionMode {
        private int region;
        private double bias;
        private double tiltX;
        private double tiltY;

        public RegionMode() {
            region = 0;
            bias = 0.0;
            tiltX = 0.0;
            tiltY = 0.0;
        }

        public void setRegion(int region) {
            this.region = region;
        }

        public void setBias(double bias) {
            this.bias = bias;
        }

        public void setTiltX(double tiltX) {
            this.tiltX = tiltX;
        }

        public void setTiltY(double tiltY) {
            this.tiltY = tiltY;
        }

        public int getRegion() {
            return region;
        }

        public double getBias() {
            return bias;
        }

        public double getTiltX() {
            return tiltX;
        }

        public double getTiltY() {
            return tiltY;
        }

        public double getPhaseValue(int x, int y) {
            int val = 0;

            val += bias;
            val += tiltX*x;
            val += tiltY*y;

            return val;
        }
    }
}

