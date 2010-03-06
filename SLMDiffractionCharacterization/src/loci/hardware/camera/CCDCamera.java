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
    private static final String dllPath = "C:\\SWIGExchange\\CCDCamWrapper.dll";

    public CCDCamera() {
        // Load the SWIG C++ bindings DLL.
        System.load(dllPath);
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

    public int captureFrame() {
        return CCDCamWrapper.capture_frame();
    }
}
