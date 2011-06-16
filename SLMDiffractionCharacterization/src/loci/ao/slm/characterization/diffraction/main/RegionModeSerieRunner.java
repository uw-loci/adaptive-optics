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
     * Will the screen be updated while the regions are scanned?
     */
    private boolean updateScreen;

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

    private int regionNextIterVar;
    private double biasNextIterVar;

    private int modeIndex;
    private int iteration; // iterations within one region
    private int iterations; // total iterations
    private int slmFrame;

    private int printint = 0;

    private RegionMode scanMode;

    private boolean preloadMode;
    private boolean isPreloaded;

    public enum RunModes {RUN_MODE, PRELOAD_MODE };
    private RunModes runMode;


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
            int regionFromVar, int regionToVar, int regionStepSize,
            boolean updateScreen) {
        this.modeIndex = modeIndex;
        this.outputFolder = outputFolder;
        this.biasFromVar = biasFromVar;
        this.biasToVar = biasToVar;
        this.biasStepSize = biasStepSize;
        this.regionFromVar = regionFromVar;
        this.regionToVar = regionToVar;
        this.regionStepSize = regionStepSize;
        this.updateScreen = updateScreen;
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
        //RegionMode mode = RegionModes.getInstance().getModeByIndex(modeIndex);
        if (iterations == 0) {
            System.out.println("Loading and displaying first region mode");
            slmFrame=0;

            if (!isPreloaded) {
                // the first one, load and display.
                scanMode.setRegion(regionCurrentVar);
                scanMode.setBias(biasCurrentVar);
                double[] dataMatrix = RegionModes.getInstance().generateDataMatrix();
                if (Main.getInstance().sysAbbCorrectionisEnabled) {
                    ImageUtils.addImages(
                        dataMatrix, Main.getInstance().sysAbbCorrectionDataMatrix);
                }

                Main.getInstance().getPhaseImagePanel().setEnableScreenUpdates(updateScreen);
                if (updateScreen) {
                    Main.getInstance().getPhaseImagePanel().setDataMatrix(dataMatrix);
                }

                ImageUtils.translateThroughLUT(dataMatrix);
                if (Constants.USE_SLM_DEVICE) {
                    //System.out.println("Writing and loading display frame " + slmFrame);
                    com.slmcontrol.slmAPI.writeDataFrame(dataMatrix, slmFrame);
                    com.slmcontrol.slmAPI.selectDisplayFrame(slmFrame);
                    //slmFrame = (slmFrame == 1) ? 0 : 1;
                    slmFrame++;
                }
            } else {
                // Already pre-loaded.
                com.slmcontrol.slmAPI.selectDisplayFrame(slmFrame);
            }
        } else {
            // Not the first, one.  Select the next frame use time in between to write
            // next frame.
            if (!preloadMode) {
                //System.out.println("SElecting display frame " + slmFrame);
                com.slmcontrol.slmAPI.selectDisplayFrame(slmFrame);
            }

            slmFrame++;
            if (slmFrame > 63) {
                slmFrame = 0;
            }
        }

        long durStart = System.currentTimeMillis();
        if (!isPreloaded) {            
            //LOAD NEXT REGION
            scanMode.setRegion(regionNextIterVar);
            scanMode.setBias(biasNextIterVar);
            double[] dataMatrix = RegionModes.getInstance().generateDataMatrix();
            if (Main.getInstance().sysAbbCorrectionisEnabled) {
                ImageUtils.addImages(
                    dataMatrix, Main.getInstance().sysAbbCorrectionDataMatrix);
            }

            Main.getInstance().getPhaseImagePanel().setEnableScreenUpdates(updateScreen);
            if (updateScreen) {
                Main.getInstance().getPhaseImagePanel().setDataMatrix(dataMatrix);
            }
            ImageUtils.translateThroughLUT(dataMatrix);

            if (Constants.USE_SLM_DEVICE) {
                if (Constants.DEBUG) {
                    System.out.println("Sending to SLM device");
                }

                //System.out.println("Writing to frame " + slmFrame);
                com.slmcontrol.slmAPI.writeDataFrame(dataMatrix, slmFrame);
            }
        }
        
        if (!preloadMode) {
            long durEnd = System.currentTimeMillis();
            long duration = durEnd-durStart;

            try {
                /*
                 * Delay due to response time of SLM.
                 *
                 * OLD SLM:
                 * We found that 50ms seemed to work for 100 regions.
                 * However, 20ms did not work and the images were unclear.
                 * 45ms seems to work, e.g. when switching between mode 200
                 * and 0 in 900region runs.
                 * Thus, using 50ms.
                 *
                 * NEW SLM:
                 * Works with 15.  Actually runs at 30ms instead because of time
                 * it takes to write the data to the buffer.
                 *
                 * Duration is the time it took to write the next frame
                 * to the slm.
                 */
                //System.out.println("Dur: " + duration);
                if (duration < 18) {
                    Thread.sleep(18-duration);
                }
            } catch (InterruptedException ex) {
            }
        }
    }

    /**
     * Upgrades the camera and updates the status in the UI.
     */
    public synchronized void upgradeCamera()
    {
        Main.getInstance().runCamera(updateScreen);
        if (updateScreen) {
            Main.getInstance().updateStatus();
        }
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

        if (Constants.DEBUG) {
            System.out.println("output: " + outFilePath);
        }

        BufferedImage image = Main.getInstance().getCameraImagePanel().getImage();
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
        if (updateScreen) {
            Main.getInstance().updateStatus();
        }
    }
    
    public synchronized void setRunMode(RunModes mode) {
        runMode = mode;
    }

     /* Main method of thread.    */
    public synchronized void run() {
        Thread thisThread = Thread.currentThread();
        if (thread != thisThread) {
            return;
        }

        if (runMode == RunModes.PRELOAD_MODE) {
            System.out.println("Run mode: PRELOAD");
            preloadSeries();
        } else if (runMode == RunModes.RUN_MODE) {
            System.out.println("Run mode: RUN");
            runSeries();
        }
    }

    /**
     * Runs the aberration correction.
     */
    public synchronized void runSeries() {
        
        preloadMode = false;
        if (isPreloaded) {
            System.out.println("Using preloaded series.");
        }

        if (Constants.DEBUG) {
            System.out.println("Serial Thread running");
        }

        Thread thisThread = Thread.currentThread();

        long timer1=0;
        long timer2=0;
        long timer3=0;
        long timer4=0;
        long timer5=0;
        long beforeTime=0;
        long afterTime=0;
        iterations = 0;
        slmFrame=0;
        printint=0;
        
        long absStartTime = System.currentTimeMillis();

        scanMode = RegionModes.getInstance().getModeByIndex(modeIndex);
        for (regionCurrentVar = regionFromVar; (regionCurrentVar <= regionToVar) && (thread == thisThread); regionCurrentVar+=regionStepSize) {
            iteration = 0;
            
            // Set the region of the (scan) mode.
            scanMode.setRegion(regionCurrentVar);
            if ((regionCurrentVar % 100) == 0) {
                System.out.println("Region: " + regionCurrentVar);
            }
            
            regionNextIterVar = regionCurrentVar;
            for (biasCurrentVar = biasFromVar; (biasCurrentVar <= biasToVar) && (thread == thisThread); biasCurrentVar+=biasStepSize) {
                if ((biasCurrentVar + biasStepSize) > biasToVar) {
                    regionNextIterVar = regionCurrentVar + regionStepSize;
                    biasNextIterVar = biasFromVar;
                } else {
                    biasNextIterVar = biasCurrentVar+biasStepSize;
                }

                beforeTime = System.currentTimeMillis();
                upgradeParams();
                afterTime = System.currentTimeMillis();
                timer1 += afterTime - beforeTime;

                beforeTime = System.currentTimeMillis();
                nextSLMImage();
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
        }

        if (thread == thisThread) {
            long absEndTime = System.currentTimeMillis();
            long absTime=absEndTime-absStartTime;

            System.out.println("timer1 (upgradeParams): " + timer1/1000 + " sec");
            System.out.println("timer2 (nextSLM): " + timer2/1000 + " sec");
            System.out.println("timer3 (upgradeCamera): " + timer3/1000 + " sec");
            System.out.println("timer4 (recordImage): " + timer4/1000 + " sec");
            System.out.println("timer5 (updateStatus): " + timer5/1000 + " sec");
            System.out.println("Total time: " + absTime/1000 + " sec");
            System.out.println("Total iterations: " + iterations);
            System.out.println("Total time per iteration: " + (1.0*absTime/iterations) + " msec");
        }
    }

    public boolean isPreloadable() {
        int frameCount = (int)(1.0*(regionToVar - regionFromVar + 1)/regionStepSize*(biasToVar - biasFromVar)/biasStepSize);
        if (frameCount > 63)
            return false;
        return true;
    }



    public synchronized void preloadSeries() {
        preloadMode = true;
        isPreloaded = false;
        System.out.println("Preload Series running.");
        if (Constants.DEBUG) {
            System.out.println("Serial Thread running");
        }

        Thread thisThread = Thread.currentThread();

        long timer1=0;
        long timer2=0;
        long beforeTime=0;
        long afterTime=0;
        iterations = 0;
        slmFrame=0;
        printint=0;


        long absStartTime = System.currentTimeMillis();

        scanMode = RegionModes.getInstance().getModeByIndex(modeIndex);
        for (regionCurrentVar = regionFromVar; (regionCurrentVar <= regionToVar) && (thread == thisThread); regionCurrentVar+=regionStepSize) {
            iteration = 0;

            // Set the region of the (scan) mode.
            scanMode.setRegion(regionCurrentVar);
            if ((regionCurrentVar % 100) == 0) {
                System.out.println("Region: " + regionCurrentVar);
            }

            regionNextIterVar = regionCurrentVar;
            for (biasCurrentVar = biasFromVar; (biasCurrentVar <= biasToVar) && (thread == thisThread); biasCurrentVar+=biasStepSize) {
                if ((biasCurrentVar + biasStepSize) > biasToVar) {
                    regionNextIterVar = regionCurrentVar + regionStepSize;
                    biasNextIterVar = biasFromVar;
                } else {
                    biasNextIterVar = biasCurrentVar+biasStepSize;
                }

                beforeTime = System.currentTimeMillis();
                upgradeParams();
                afterTime = System.currentTimeMillis();
                timer1 += afterTime - beforeTime;

                beforeTime = System.currentTimeMillis();
                nextSLMImage();
                afterTime = System.currentTimeMillis();
                timer2 += afterTime - beforeTime;

                iteration++;
                iterations++;
            }
        }

        long absEndTime = System.currentTimeMillis();
        long absTime=absEndTime-absStartTime;

        if (thread == thisThread) {
            System.out.println("timer1 (upgradeParams): " + timer1/1000 + " sec");
            System.out.println("timer2 (nextSLM): " + timer2/1000 + " sec");
            System.out.println("Total time: " + absTime/1000 + " sec");
            System.out.println("Total iterations: " + iterations);
            System.out.println("Total time per iteration (preload): " + (1.0*absTime/iterations) + " msec");
            isPreloaded = true;
            Main.getInstance().updatePreloadStatus(true);
        }
    }

}
