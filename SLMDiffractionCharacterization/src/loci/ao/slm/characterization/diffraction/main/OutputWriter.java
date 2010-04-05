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
    FileWriter fStream = null;

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
    public String getFilePath()
    {
        return filePath;
    }

    /**
     * Open the stream for writing.
     *
     * @param append If true, then appends to the file, otherwise creates it.
     */
    private void openFile(boolean append) {
        try {
            fStream = new FileWriter(filePath, append);
        } catch (IOException ex) {
            Logger.getLogger(OutputWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Close the file.
     */
    private void closeFile() {
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
     * @param refVal The reference value.
     */
    public void writeHeader(int numGratings, int refVal) {
        try {
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            openFile(true);
            fStream.write("% SLM Diffraction Calibration\n");
            fStream.write("% Created at " + dateFormat.format(date) +  "\n");
            fStream.write("% Gratings: " + numGratings + "\n");
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
    public void recordData(int region, int refVal, int varVal, double roiInt) {
        try {
            openFile(true);
            String txtLine = region + "\t" + refVal + "\t" + varVal + "\t" + roiInt + "\n";
            System.out.println("Line: " + txtLine);
            fStream.write(txtLine);
            closeFile();
        } catch (IOException ex) {
            Logger.getLogger(OutputWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
