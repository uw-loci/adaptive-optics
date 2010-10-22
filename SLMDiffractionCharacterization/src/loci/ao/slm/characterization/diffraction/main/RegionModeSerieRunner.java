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
     * Bias Variable: The range to scan over.
     */
    private double biasFromVar;
    private double biasToVar;
    private double biasStepSize;
    private double biasCurrentVar;

    /**
     * Region Variable: The regions to scan over.
     */
    private int regionFromVar;
    private int regionToVar;
    private int regionStepSize;
    private int regionCurrentVar;

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
            double biasFromVar, double biasToVar, double biasStepSize,
            int regionFromVar, int regionToVar, int regionStepSize) {
        this.modeIndex = modeIndex;
        this.outputFolder = outputFolder;
        this.biasFromVar = biasFromVar;
        this.biasToVar = biasToVar;
        this.biasStepSize = biasStepSize;
        this.regionFromVar = regionFromVar;
        this.regionToVar = regionToVar;
        this.regionStepSize = regionStepSize;
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
        mode.setBias(biasCurrentVar);


        double[] dataMatrix = RegionModes.getInstance().generateDataMatrix();
        
        if (Main.getInstance().sysAbbCorrectionisEnabled) {
            ImageUtils.addImages(
                dataMatrix, Main.getInstance().sysAbbCorrectionDataMatrix);
        }

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
        String outFilePath = outputFolder + "\\";
        outFilePath += prefix + "R" + regionCurrentVar + "_" + iteration + ".png" ;
        System.out.println("output: " + outFilePath);

        BufferedImage image = Main.getInstance().getCameraImagePanel().getROIImage();
                //Main.getInstance().getCameraImagePanel().getImage();

        File outputFile = new File(outFilePath);
        String formatName = "png";

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

        long timer1=0;
        long timer2=0;
        long timer3=0;
        long timer4=0;
        long timer5=0;
        long beforeTime=0;
        long afterTime=0;
        int iterations = 0;
        
        for (regionCurrentVar = regionFromVar; (regionCurrentVar <= regionToVar) && (thread == thisThread); regionCurrentVar+=regionStepSize) {
            // Set the region of the mode.
            RegionMode mode = RegionModes.getInstance().getModeByIndex(modeIndex);
            mode.setRegion(regionCurrentVar);
            iteration = 0;
            System.out.println("Region: " + regionCurrentVar);
            
            
            for (biasCurrentVar = biasFromVar; (biasCurrentVar <= biasToVar) && (thread == thisThread); biasCurrentVar+=biasStepSize) {
                //System.out.println("Iteration running");
                beforeTime = System.currentTimeMillis();
                upgradeParams();
                afterTime = System.currentTimeMillis();
                timer1 += afterTime - beforeTime;

                beforeTime = System.currentTimeMillis();
                nextSLMImage();

                // This is to make sure that the image has been displayed
                // appropriately and keep the GUI responsive.
                /*
                try {
                    //Thread.sleep(500); //100 seems to work.
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                }*/
                afterTime = System.currentTimeMillis();
                timer2 += afterTime - beforeTime;

                // Run the camera.
                beforeTime = System.currentTimeMillis();
                upgradeCamera();
                afterTime = System.currentTimeMillis();
                timer3 += afterTime - beforeTime;

                // Record results.
                beforeTime = System.currentTimeMillis();
                recordImage();
                afterTime = System.currentTimeMillis();
                timer4 += afterTime - beforeTime;

                // Update the status.
                beforeTime = System.currentTimeMillis();
                updateStatus();
                afterTime = System.currentTimeMillis();
                timer5 += afterTime - beforeTime;

                iteration++;
                iterations++;
            }


            if (Constants.DEBUG) {
                System.out.println("Serial Thread exiting loop.");
            }
        }
        System.out.println("timer1 (upgradeParams): " + timer1/1000 + " sec");
        System.out.println("timer2 (nextSLM+100ms): " + timer2/1000 + " sec");
        System.out.println("timer3 (upgradeCamera): " + timer3/1000 + " sec");
        System.out.println("timer4 (recordImage): " + timer4/1000 + " sec");
        System.out.println("timer5 (updateStatus): " + timer5/1000 + " sec");
        System.out.println("Total iterations: " + iterations);
    }
}
