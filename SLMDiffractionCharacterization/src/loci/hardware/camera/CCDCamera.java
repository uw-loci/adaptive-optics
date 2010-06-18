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
    private int[] frame;
    private int width = 1024;
    private int height = 768;
    private BufferedImage img;


    public CCDCamera() {
        // Load the SWIG C++ bindings DLL.
        System.load(dllPath);
        frame = new int[width*height];
    }

    public synchronized int testMe() {
        return CCDCamWrapper.test_me();
    }

    public synchronized boolean initialize() {
        return CCDCamWrapper.init_camera();
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
        img = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);

        int retVal = 0;
        for (int j = 0; j < averages; j++) {
            retVal = CCDCamWrapper.capture_frame();
            if (retVal != (width * height)) {
                System.out.println("IMAGE COLLECTION ERROR");
                waitAndTryAgain();
            }

            for (int i = 0; i < width*height; i++) {
                if (j == 0) {
                    frame[i] = 0; // Clean up frame beforehand (first image in averaging).
                }
                frame[i] += CCDCamWrapper.get_frame_at_pos(i);

                int m = i % width;
                int n = i / width;

                int byteVal = (int)frame[i];
                byteVal &= 0xff;
                int rgbVal = (byteVal << 16) | (byteVal << 8) | byteVal;
                img.setRGB(m, n, rgbVal);
            }
            // Indicate that memory can be released in the C program:
            CCDCamWrapper.get_frame_at_pos(-1);

            if ((averages > 0) && (j < (averages-1))) {
                // Give it a little time to prepare for next.
                // Experimental.
                try {
                    Thread.sleep(200);
                } catch (InterruptedException ex) {
                }
            }
        }

        // Average it out.
        for (int i = 0; i < width*height; i++) {
           frame[i] = Math.round(frame[i]/averages);

           if (frame[i] > 255) frame[i] = 255;
           if (frame[i] < 0) frame[i] = 0;
        }

        return retVal;
    }


}
