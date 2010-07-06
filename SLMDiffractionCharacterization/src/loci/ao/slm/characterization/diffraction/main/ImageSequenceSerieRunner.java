//
// ImageSequenceSerieRunner.java
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

/**
 * Singleton class that defines a running thread for running an experiment.
 */
public class ImageSequenceSerieRunner
        extends Observable
        implements Runnable
{
    /**
     * The singleton class instance object.
     */
    private static ImageSequenceSerieRunner instance = null;

   /**
     * The thread object.  Each data source works within its own thread.
     */
    private Thread thread;
    
    /*
     * Output file name.
     */
    private String outputFolder;

    /**
     * Current image in the image sequences.
     */
    private int currentImageIndex;

    /**
     * Constructor.
     */
    private ImageSequenceSerieRunner() {
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
    public synchronized static ImageSequenceSerieRunner getInstance() {
        if (instance == null) {
            instance = new ImageSequenceSerieRunner();
        }
        return instance;
    }


    /**
     * Updates parameters before a sequence is executed.
     *
     * @param outputFolder The path to the output folder.
     */
    public void setParams(String outputFolder) {
        this.outputFolder = outputFolder;
        currentImageIndex = 0;
    }

    /**
     * Notify observers about the parameter change.
     */
    public synchronized void upgradeParams()
    {
        notifyObservers();
    }

    /**
     * Fetches the next image (current) in the sequence and sends to the
     * SLM (if enabled).
     */
    public synchronized void nextSLMImage()
    {
        BufferedImage slmImage =
            ImageSequence.getInstance().getImageByIndex(currentImageIndex);        

        double[] dataMatrix = ImageUtils.imageToDataMatrix(slmImage);
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
        ImagePanel ccdImagePanel = Main.getInstance().getCameraImagePanel();
        Double roiInt = ccdImagePanel.getROIIntensity();

        File slmImage =
                ImageSequence.getInstance().getImageFileByIndex(currentImageIndex);

        //
        String prefix = "out_";
        String outFilePath = outputFolder + "\\" + prefix + slmImage.getName();


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

        Thread thisThread = Thread.currentThread();
        ArrayList<String> imagePathList =
                ImageSequence.getInstance().getPathList();
        currentImageIndex = 0;

        for (int j = 0; (j < imagePathList.size()) && (thread == thisThread); j++) {
            if (Constants.DEBUG) {
                System.out.println("Image #" + j);
            }

            upgradeParams();

            nextSLMImage();

            // This is to make sure that the image has been displayed
            // appropriately and keep the GUI responsive.
            try {
                //Thread.sleep(200); //100 seems to work.
                //Thread.sleep(500); //100 seems to work.
                Thread.sleep(500); //100 seems to work.
            } catch (InterruptedException ex) {
            }

            // Run the camera.
            upgradeCamera();

            // Record results.
            recordImage();

            // Update the status.
            updateStatus();

            currentImageIndex++;
        }

        if (Constants.DEBUG) {
            System.out.println("Serial Thread exiting loop.");
        }
    }
}
