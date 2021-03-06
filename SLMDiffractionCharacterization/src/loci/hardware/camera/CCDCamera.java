//
// CCDCamera.java
//

/*
 * Diffraction calibration of a SLM device by imaging the SLM in the far-field
 * on a CCD Camera.  Measuring the intensity of the first diffraction order.
 *
 * Copyright (C) 2010-@year@ Gunnsteinn Hall
 * Developed at the LOCI Lab (http://www.loci.wisc.edu).
 * University of Wisconsin - Madison.
 */


package loci.hardware.camera;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import loci.ao.slm.characterization.diffraction.main.Constants;
import loci.hardware.camera.swig.CCDCamWrapper;

/**
 * The CCDCamera class interfaces a CCD hardware camera.  The actual
 * communication is done through C++, which interfaces the Java code through
 * a DLL which is been generated with SWIG (http://www.swig.org).
 *
 * The class provides functionality to initialize a CCD camera, and to grab
 * images from the camera.
 */
public class CCDCamera {
    private static final String dllPath = "C:\\gunnsteinn\\AdaptiveOptics\\SWIG\\CCDCamWrapper.dll";
    private static final String spotDLL = "C:\\SpotCamDLL\\SpotCam.dll";
    //private int[] frame;
    private int width;
    private int height;
    private int origWidth, origHeight;
    private BufferedImage img;
    private boolean isInitialized;
    public final int MAX_WIDTH=Constants.CCD_X_PIXELS;
    public final int MAX_HEIGHT=Constants.CCD_Y_PIXELS;

    public CCDCamera() {
        width = MAX_WIDTH;
        height = MAX_HEIGHT;
        
        System.load(spotDLL);
        // Load the SWIG C++ bindings DLL.
        System.load(dllPath);
        //frame = new int[width*height];
        isInitialized=false;

        origWidth=width;
        origHeight=height;
    }

    public void shutdown()
    {
        CCDCamWrapper.shutdown();
    }

    public void setRoi(int roiX, int roiY, int roiWidth, int roiHeight)
    {
        this.width=roiWidth;
        this.height=roiHeight;

        int retVal = CCDCamWrapper.set_roi(roiX, roiY, roiWidth, roiHeight);

        System.out.println("Set roi x: " + roiX + " y: " + roiY + " width: " + roiWidth + " height: " + roiHeight);
        System.out.println("set_roi return value: " + retVal);
        if (retVal == -1) {
            System.out.println("Set ROI failed.");
            System.out.println("Note: " + getNote());
        }
        //frame = new int[width*height];
    }
    public void resetRoi()
    {
        setRoi(0, 0, origWidth, origHeight);
    }
    
    public synchronized int testMe() {
        return CCDCamWrapper.test_me();
    }

    public synchronized boolean initialize() {
        isInitialized=true;
        return CCDCamWrapper.init_camera(Constants.CCD_DRIVER_TYPE);
    }
    public boolean isInitialized()
    {
        return isInitialized;
    }

    public synchronized String getNote() {
        return CCDCamWrapper.get_note();
    }

    public synchronized BufferedImage getImage() {
        return img;
        /*
        BufferedImage img =
                new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);

        for (int m = 0; m < width; m++) {
            for (int n = 0; n < height; n++) {
                int index = n*width + m;
                int byteVal = (int)frame[index];
                byteVal &= 0xff;
                // 32bit
                int rgbVal = (byteVal << 16) | (byteVal << 8) | byteVal;
                img.setRGB(m, n, rgbVal);
            }
        }

        return img;*/
    }

    private synchronized void waitAndTryAgain() {
        System.out.println("Waiting 1sec before trying again!");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
        }
    }
    /**
     * Captures one frame with averaging optional.
     *
     * @param averages The number of averages to take.
     *
     * @return -1 on error, otherwise the width*height of the image (in pixels).
     */
    public synchronized int captureFrame(int averages) {
        //img = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        //img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        img = new BufferedImage(width, height, BufferedImage.TYPE_USHORT_GRAY);

        int retVal = 0;
        for (int j = 0; j < averages; j++) {
            retVal = CCDCamWrapper.capture_frame();

            if (retVal != (width * height)) {
                String errMsg = getNote();
                System.out.println("IMAGE COLLECTION ERROR: " + errMsg);
                System.out.println("Return val: " + retVal);
                System.out.println("Width x Height: " + (width*height));
                waitAndTryAgain();
            }

            //System.out.println("Width: " + width + " height: " + height);
            
            for (int n = 0; n < height; n++) {
                for (int m = 0; m < width; m++) {
                    int val = CCDCamWrapper.get_frame_at_pos(m, n);

                    /*int byteVal = (int)val;
                    byteVal &= 0xff;
                    int rgbVal = (byteVal << 16) | (byteVal << 8) | byteVal;
                    img.setRGB(m, n, rgbVal);*/
                    
                    int[] a = new int [1];
                    int wordVal = (int)val;
                    wordVal &= 0xffff;
                    a[0] = (int)(1.0 * wordVal / 16384.0 * 65536.0);
                    img.getRaster().setPixel(m, n, a);
                }
            }
        }

        return retVal;
    }


}
