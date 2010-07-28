/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package loci.ao.slm.characterization.diffraction.main;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

/**
 * ImageSequence is a singleton class used to contain and manage image
 * sequences.
 */
public class ImageSequence {
    private static ImageSequence instance = null;
    private ArrayList<File> imageFileList;
    private boolean isLoaded = false;

    /**
     * Constructor to create the object.
     */
    private ImageSequence() {
        isLoaded = false;
    }

    /**
     * Return the singleton instance of the object.
     */
    public static ImageSequence getInstance() {
        if (instance == null) {
            instance = new ImageSequence();
        }
        return instance;
    }

    /**
     * Loads an image sequence from a folder.
     */
    public void loadFolder(String path) {
        // List all maps, format: "region-X.dat" where X: 0 upto (noRegions-1).
        // Load each map, into lutData[X][Y] where Y: 0 upto 255.
        File folder = new File(path);
        FilenameFilter fileFilter = new FileListFilter("", "bmp");
        File[] listOfFiles = folder.listFiles(fileFilter);

        // Lame regex, but works (not very specific).  Could be improved, and FilenameFilter removed as well.
        Pattern p = Pattern.compile("^([0-9]+)p([0-9]+).bmp$");

        imageFileList = new ArrayList();

        isLoaded = true;
        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                Matcher matcher = p.matcher(listOfFiles[i].getName());
                boolean matchFound = matcher.find();

                //if (matchFound) {
                //                    Integer mode = new Integer(matcher.group(1));
                //    Integer phaseSlice = new Integer(matcher.group(2));
                imageFileList.add(listOfFiles[i]);
                //}
            }
        }

        // Sort by mode, phase.
        Collections.sort(imageFileList, new ByModeAndPhaseComparator());

        /*for (int i = 0; i < listOfFiles.length; i++) {
            System.out.println(i + ": " +listOfFiles[i].getName());
        }*/
    }

    public ArrayList<String> getFileList()
    {
        ArrayList<String> fileList = new ArrayList();
        for (int i = 0; i < imageFileList.size(); i++) {
            fileList.add(imageFileList.get(i).getName());
        }
        return fileList;
    }

    public ArrayList<String> getPathList()
    {
        ArrayList<String> pathList = new ArrayList();
        for (int i = 0; i < imageFileList.size(); i++) {
            pathList.add(imageFileList.get(i).getAbsolutePath());
        }
        return pathList;
    }

    public File getImageFileByIndex(int index) {
        return imageFileList.get(index);
    }

    public BufferedImage getImageByIndex(int index) {
        BufferedImage bi = null;
        Raster rasterdata = null;

        try {
            // read a pattern
            bi = ImageIO.read(imageFileList.get(index));
            rasterdata = bi.getData();
            //float[][] imageData = rasterdata.getPixels(0, 0, 512, 512, samples[0]);
        } catch (Exception e) {
            System.err.println("Caught IOException: " + e.getMessage());
        }

        return bi;
    }

    /**
     * Comparator class.  For file names sorting by mode and phase.
     */
    private class ByModeAndPhaseComparator implements Comparator {
        public int compare(Object first, Object second) {
            File imageFile1 = (File)first;
            File imageFile2 = (File)second;

            Pattern p = Pattern.compile("([0-9]+)p([0-9]+).bmp$");
            Matcher matcher1 = p.matcher(imageFile1.getName());
            Matcher matcher2 = p.matcher(imageFile2.getName());
            boolean matchFound1 = matcher1.find();
            boolean matchFound2 = matcher2.find();

            int retVal = 0;

            if (matchFound1 && matchFound2) {
                Integer mode1 = new Integer(matcher1.group(1));
                Integer phaseSlice1 = new Integer(matcher1.group(2));
                Integer mode2 = new Integer(matcher2.group(1));
                Integer phaseSlice2 = new Integer(matcher2.group(2));

                /*System.out.println(imageFile1.getName() + "/m1: " + mode1 + " p: " + phaseSlice1
                        + " " + imageFile2.getName() + "/m2: " + mode2 + " p: " + phaseSlice2);*/
                if (mode1.intValue() != mode2.intValue()) {
                    int mDiff = mode1.intValue() - mode2.intValue();
                    retVal = mDiff;
                } else {
                    int pDiff = phaseSlice1.intValue() - phaseSlice2.intValue();
                    retVal = pDiff;
                }
            } else if (matchFound1 && !matchFound2) {
                retVal = 1;
            } else if (!matchFound1 && matchFound2) {
                retVal = -1;
            } else {
                // Compare as strings.
                retVal = imageFile1.getName().compareToIgnoreCase(imageFile2.getName());
            }
            
            return retVal;
        }
    }
}


