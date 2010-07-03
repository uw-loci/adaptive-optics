//
// SerieRunner.java
//

/*
 * Diffraction calibration of a SLM device by imaging the SLM in the far-field
 * on a CCD Camera.  Measuring the intensity of the first diffraction order.
 * Copyright (C) 2010-@year@ Gunnsteinn Hall @LOCI Labs.
 * University of Wisconsin - Madison.
 */


package loci.ao.slm.characterization.diffraction.main;

import java.util.Observable;

/**
 * Singleton class that defines a running thread for running an experiment.
 */
public class GratingSerieRunner
        extends Observable
        implements Runnable
{
    /**
     * The singleton class instance object.
     */
    private static GratingSerieRunner instance = null;

   /**
     * The thread object.  Each data source works within its own thread.
     */
    private Thread thread;

    /**
     * Variable modulation: The range to scan over.
     */
    private int fromVar;
    private int toVar;
    private int stepSize;
    
    /**
     * Regions: The regions to scan over.
     */
    private int fromRegion;
    private int toRegion;

    /**
     * Fixed grating settings.
     */
    private int fixedGratings;
    private int fixedRefValue;
    private int fixedRegions;
    
    /*
     * Output file name.
     */
    private String outputFileName;

    /**
     * Output writer.
     */
    private OutputWriter outpWriter = null;

    /**
     * Current variable value;
     */
    private int currentVar;

    /**
     * Current region.
     */
    private int currentRegion;

    /**
     * Camera ROI definition.
     */
    private int roiULCornerX;
    private int roiULCornerY;
    private int roiLRCornerX;
    private int roiLRCornerY;

    /**
     * Constructor.
     */
    private GratingSerieRunner() {
       super();       
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
    public synchronized static GratingSerieRunner getInstance() {
        if (instance == null) {
            instance = new GratingSerieRunner();
        }
        return instance;
    }

    public synchronized void setOutputFileName(String filePath)
    {
        this.outputFileName = filePath;
    }

    /**
     * Setup fixed grating variables.
     */
    public synchronized void setGratingVars(
            int fixedGratings, int fixedRefValue, int fixedRegions)
    {
        this.fixedGratings = fixedGratings;
        this.fixedRefValue = fixedRefValue;
        this.fixedRegions = fixedRegions;
    }

    /**
     * Set parameters of the serie runner.
     *
     * @param fromRegion The region to start at.
     * @param toRegion The region to end at.
     * @param fromVar Variable value range: starting point (from).
     * @param toVar Variable value range: end point (to).
     * @param stepSize The step size to scan the variable in.
     */
    public synchronized void setRange(int fromRegion, int toRegion,
            int fromVar, int toVar, int stepSize)
    {
        this.fromRegion = fromRegion;
        this.toRegion = toRegion;
        this.fromVar = fromVar;
        this.toVar = toVar;
        this.stepSize = stepSize;
    }

    /**
     * Set ROI information, e.g. the corner locations.
     * 
     * @param roiULCornerX The U.L. corner of the ROI, X-coordinate.
     * @param roiULCornerY The U.L. corner of the ROI, Y-coordinate.
     * @param roiLRCornerX The L.R. corner of the ROI, X-coordinate.
     * @param roiLRCornerY The L.R. corner of the ROI, Y-coordinate.
     */
    public synchronized void setROIInformation(int roiULCornerX, int roiULCornerY,
            int roiLRCornerX, int roiLRCornerY)
    {
        this.roiULCornerX = roiULCornerX;
        this.roiULCornerY = roiULCornerY;
        this.roiLRCornerX = roiLRCornerX;
        this.roiLRCornerY = roiLRCornerY;
    }

    /**
     * Notify observers about the parameter change.
     */
    public synchronized void upgradeParams()
    {
        notifyObservers();
    }

    public synchronized int getCurrentVar()
    {
        return currentVar;
    }

    public synchronized void updateSLMGrating()
    {
        GratingImagePanel calibImagePanel
                = Main.getInstance().getPhaseImagePanel();

        calibImagePanel.setGratingParams(
                new Integer(fixedGratings),
                new Integer(fixedRefValue),
                new Integer(currentVar),
                new Integer(currentRegion),
                new Integer(fixedRegions));

        if (Constants.USE_SLM_DEVICE) {
            double[] dataMatrix = calibImagePanel.getDataMatrix();
            com.slmcontrol.slmAPI.slmjava(dataMatrix, (char)0);
        }
    }

    public synchronized void upgradeCamera()
    {
        Main.getInstance().runCamera();
        Main.getInstance().updateStatus();
    }

    private synchronized void recordData()
    {
        ImagePanel ccdImagePanel = Main.getInstance().getCameraImagePanel();
        Double roiInt = ccdImagePanel.getROIIntensity();

        // Get the roi intensity.
        outpWriter.recordData(currentRegion, fixedRefValue, currentVar, roiInt);
    }

    private synchronized void updateStatus()
    {
        Main.getInstance().updateStatus();
    }

    public synchronized void start() {
       // Thread the instance.
       thread = new Thread(this);
       thread.start();
    }

    public synchronized void run() {
        if (Constants.DEBUG) {
            System.out.println("Serial Thread running");
        }
        Thread thisThread = Thread.currentThread();

        outpWriter = new OutputWriter(outputFileName);
        outpWriter.writeHeader(fixedGratings, fixedRegions, fixedRefValue,
                roiULCornerX, roiULCornerY, roiLRCornerX, roiLRCornerY);

        for (currentRegion = fromRegion; (currentRegion <= toRegion) && (thread == thisThread); currentRegion++) {
            for (currentVar = fromVar; (currentVar <= toVar) && (thread == thisThread); currentVar+=stepSize) {
                if (Constants.DEBUG) {
                    System.out.println("Region: " + currentRegion + "; Current var: " + currentVar);
                }

                //long durStart = System.currentTimeMillis();

                upgradeParams();
                updateSLMGrating();

                //long durEnd = System.currentTimeMillis();
                //long duration = durEnd - durStart;
                //System.out.println("SLM upgrade duration: " + duration);

                GratingExperimentStatus.getInstance().setRefValue(fixedRefValue);
                GratingExperimentStatus.getInstance().setRegion(currentRegion);
                GratingExperimentStatus.getInstance().setVarValue(currentVar);

                // This is to make sure that the image has been displayed
                // appropriately.
                try {
                    //Thread.sleep(500);
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                }

                // Run the camera.
                //durStart = System.currentTimeMillis();
                upgradeCamera();
                //durEnd = System.currentTimeMillis();
                //duration = durEnd - durStart;
                //System.out.println("Camera upgrade duration: " + duration);

                // Record results.
                recordData();

                // Update the status.
                updateStatus();

                // This is to keep the GUI responsive.
                // Might not be necessary.
                /*
                try {
                    Thread.sleep(50);
                    //Thread.sleep(1000); // XXX: Debugging 1 second.
                } catch (InterruptedException ex) {
                }*/
            }
        }

        if (Constants.DEBUG) {
            System.out.println("Serial Thread exiting loop.");
        }
    }
}
