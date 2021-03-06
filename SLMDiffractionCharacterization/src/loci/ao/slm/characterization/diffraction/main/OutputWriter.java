//
// OutputWriter.java
//

/*
 * Diffraction calibration of a SLM device by imaging the SLM in the far-field
 * on a CCD Camera.  Measuring the intensity of the first diffraction order.
 * Copyright (C) 2010-@year@ Gunnsteinn Hall @LOCI Labs.
 * University of Wisconsin - Madison.
 */

package loci.ao.slm.characterization.diffraction.main;

import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * OutputWriter is a class for writing output data files.
 */
public class OutputWriter {
    /**
     * The file path to write the output to.
     */
    private String filePath;
    private FileWriter fStream = null;

    /**
     * Initialize the OutputWriter class.
     *
     * @param fileName The name of the output file.
     */
    public OutputWriter(String filePath) {
        this.filePath = filePath;

        // Create the file.
        openFile(false);
        closeFile();
    }

    /**
     * Returns the output file path.
     *
     * @param The output file path.
     */
    public synchronized String getFilePath()
    {
        return filePath;
    }

    /**
     * Open the stream for writing.
     *
     * @param append If true, then appends to the file, otherwise creates it.
     */
    private synchronized void openFile(boolean append) {
        try {
            fStream = new FileWriter(filePath, append);
        } catch (IOException ex) {
            Logger.getLogger(OutputWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Close the file.
     */
    private synchronized void closeFile() {
        try {
            fStream.close();
        } catch (IOException ex) {
            Logger.getLogger(OutputWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Write the header.
     * 
     * @param numGratings The number of gratings.
     * @param numRegions The number of regions specified.
     * @param refVal The reference value.
     * @param roiULCornerX The X-coordinate of the U.L. corner of the ROI.
     * @param roiULCornerY The Y-coordinate of the U.L. corner of the ROI.
     * @param roiLRCornerX The X-coordinate of the L.R. corner of the ROI.
     * @param roiLRCornerY The Y-coordinate of the U.L. corner of the ROI.
     */
    public synchronized void writeHeader(int numGratings, int numRegions, int refVal,
            int roiULCornerX, int roiULCornerY,
            int roiLRCornerX, int roiLRCornerY) {
        try {
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            openFile(true);
            fStream.write("% SLM Diffraction Calibration\n");
            fStream.write("% Created at " + dateFormat.format(date) +  "\n");
            fStream.write("% #Gratings: " + numGratings + "\n");
            fStream.write("% #Regions: " + numRegions + "\n");
            fStream.write("% ROI UL: X: "
                    + roiULCornerX + " Y: " + roiULCornerY + "\n");
            fStream.write("% ROI LR: X: "
                    + roiLRCornerX + " Y: " + roiLRCornerY + "\n");

            int roiHeight = (int)Math.abs(roiLRCornerY - roiULCornerY);
            int roiWidth = (int)Math.abs(roiLRCornerX - roiULCornerX);
            fStream.write("% ROI Width: " + roiWidth
                    + "; Height: " + roiHeight + "\n");
            fStream.write("% Ref. val.: " + refVal + "\n");
            fStream.write("%[region] [refval] [value] [roi int.]\n");
            closeFile();
        } catch (IOException ex) {
            Logger.getLogger(OutputWriter.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }

    /**
     * Records one set of data.
     */
    public synchronized void recordData(int region, int refVal, int varVal, double roiInt) {
        try {
            openFile(true);
            String txtLine = region + "\t" + refVal + "\t" + varVal + "\t" + roiInt + "\n";
            if (Constants.DEBUG) {
                System.out.println("Line: " + txtLine);
            }
            fStream.write(txtLine);
            closeFile();
        } catch (IOException ex) {
            Logger.getLogger(OutputWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
