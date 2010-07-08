//
// RegionModeSerieRunner.java
//

/*
 * Diffraction calibration of a SLM device by imaging the SLM in the far-field
 * on a CCD Camera.  Measuring the intensity of the first diffraction order.
 * Copyright (C) 2010-@year@ Gunnsteinn Hall @LOCI Labs.
 * University of Wisconsin - Madison.
 */


package loci.ao.slm.characterization.diffraction.main;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import loci.ao.slm.characterization.diffraction.main.RegionModes.RegionMode;

/**
 * Singleton class that defines a running thread for running an experiment.
 */
public class RegionModeSerieRunner
        extends Observable
        implements Runnable
{
    /**
     * The singleton class instance object.
     */
    private static RegionModeSerieRunner instance = null;

   /**
     * The thread object.  Each data source works within its own thread.
     */
    private Thread thread;
    
    /*
     * Output file name.
     */
    private String outputFolder;

    /**
     * Variable: The range to scan over.
     */
    private double fromVar;
    private double toVar;
    private double stepSize;
    private double currentVar;
    private int modeIndex;
    private int iteration;

    /**
     * Constructor.
     */
    private RegionModeSerieRunner() {
       super();       
    }

    /**
     * Start the thread.
     */
    public void start() {
       // Thread the instance.
       thread = new Thread(this);
       thread.start();
    }

    /**
     * Stop the thread.
     */
    public void stop()
    {
        thread = null;
    }

    /**
     * Get the instance of the class.
     */
    public synchronized static RegionModeSerieRunner getInstance() {
        if (instance == null) {
            instance = new RegionModeSerieRunner();
        }
        return instance;
    }


    /**
     * Updates parameters before a sequence is executed.
     *
     * @param outputFolder The path to the output folder.
     */
    public void setParams(
            int modeIndex, String outputFolder,
            double fromVar, double toVar, double stepSize) {
        this.modeIndex = modeIndex;
        this.outputFolder = outputFolder;
        this.fromVar = fromVar;
        this.toVar = toVar;
        this.stepSize = stepSize;
    }

    /**
     * Notify observers about the parameter change.
     */
    public synchronized void upgradeParams()
    {
        notifyObservers();
    }

    /**
     * Generates the next phase image and sends to the SLM (if enabled).
     */
    public synchronized void nextSLMImage()
    {
        RegionMode mode = RegionModes.getInstance().getModeByIndex(modeIndex);
        mode.setBias(currentVar);


        double[] dataMatrix = RegionModes.getInstance().generateDataMatrix();
        Main.getInstance().getPhaseImagePanel().setDataMatrix(dataMatrix);

        if (Constants.USE_SLM_DEVICE) {
            if (Constants.DEBUG) {
                System.out.println("Sending to SLM device");
            }
            com.slmcontrol.slmAPI.slmjava(dataMatrix, (char)0);
        }
    }

    /**
     * Upgrades the camera and updates the status in the UI.
     */
    public synchronized void upgradeCamera()
    {
        Main.getInstance().runCamera();
        Main.getInstance().updateStatus();
    }

    /**
     * Records the current CCD image to a file.  Its location is defined
     * by outputFolder, and the file name will be prefix + the name of the
     * image file in the sequence.
     */
    private synchronized void recordImage()
    {
        String prefix = "out_";
        String outFilePath = outputFolder + "\\" + prefix + "out" + iteration + ".bmp" ;

        BufferedImage image = Main.getInstance().getCameraImagePanel().getImage();
        File outputFile = new File(outFilePath);
        String formatName = "bmp";

        if (image != null) {
            try {
                ImageIO.write(image, formatName, outputFile);
            } catch (IOException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Updates the status (for the UI).
     */
    private synchronized void updateStatus()
    {
        Main.getInstance().updateStatus();
    }

    /**
     * Main method of thread.
     */
    public synchronized void run() {
        if (Constants.DEBUG) {
            System.out.println("Serial Thread running");
        }
        System.out.println("Serial Thread running");

        Thread thisThread = Thread.currentThread();

        iteration = 0;
        for (currentVar = fromVar; (currentVar <= toVar) && (thread == thisThread); currentVar+=stepSize) {
            System.out.println("Iteration running");
            upgradeParams();

            nextSLMImage();

            // This is to make sure that the image has been displayed
            // appropriately and keep the GUI responsive.
            try {
                Thread.sleep(500); //100 seems to work.
            } catch (InterruptedException ex) {
            }

            // Run the camera.
            upgradeCamera();

            // Record results.
            recordImage();

            // Update the status.
            updateStatus();

            iteration++;
        }

        if (Constants.DEBUG) {
            System.out.println("Serial Thread exiting loop.");
        }
    }
}
