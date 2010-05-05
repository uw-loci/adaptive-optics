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


    public CCDCamera() {
        // Load the SWIG C++ bindings DLL.
        System.load(dllPath);
        frame = new int[width*height];
    }

    public int testMe() {
        return CCDCamWrapper.test_me();
    }

    public boolean initialize() {
        return CCDCamWrapper.init_camera();
    }

    public String getNote() {
        return CCDCamWrapper.get_note();
    }

    public BufferedImage getImage() {
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

        return img;
    }

    /**
     * Captures one frame with averaging optional.
     *
     * @param averages The number of averages to take.
     *
     * @return -1 on error, otherwise the width*height of the image (in pixels).
     */
    public int captureFrame(int averages) {
        // Clean up frame.
        for (int i = 0; i < width*height; i++) {
           frame[i] = 0;
        }

        int retVal = 0;
        for (int j = 0; j < averages; j++) {
            retVal = CCDCamWrapper.capture_frame();
            if (retVal != (width * height)) {
                System.out.println("error in buf length");
                return -1;
            }

            for (int i = 0; i < width*height; i++) {
                frame[i] += CCDCamWrapper.get_frame_at_pos(i) / averages;
            }
        }

        return retVal;
    }


}
