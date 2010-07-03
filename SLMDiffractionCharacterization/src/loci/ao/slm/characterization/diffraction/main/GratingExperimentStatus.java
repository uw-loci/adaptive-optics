//
// ExperimentStatus.java
//

/*
 * Diffraction calibration of a SLM device by imaging the SLM in the far-field
 * on a CCD Camera.  Measuring the intensity of the first diffraction order.
 *
 * Copyright (C) 2010-@year@ Gunnsteinn Hall
 * Developed at the LOCI Lab (http://www.loci.wisc.edu).
 * University of Wisconsin - Madison.
 */


package loci.ao.slm.characterization.diffraction.main;

/**
 * ExperimentStatus keeps track of the current status of the experiment, no
 * matter what mode it is in (e.g. manual or series).
 *
 * It is a singleton class (only one instance exists).
 */
public class GratingExperimentStatus {
    private int region;
    private int varValue;
    private int refValue;
    private double roiIntensity;
    private int roiSaturatedPixelCount;
    private static GratingExperimentStatus instance;

    /**
     * Initializes the instance of the singleton class, and resets all the
     * values.
     */
    private GratingExperimentStatus() {
        region = 0;
        varValue = -1;
        refValue = -1;
        roiIntensity = -1;
        roiSaturatedPixelCount = 0;
    }

    /**
     * Returns the instance of the ExperimentStatus singleton class.
     *
     * @return The instance of the ExperimentStatus singleton class.
     */
    public synchronized static GratingExperimentStatus getInstance() {
        if (instance == null) {
            instance = new GratingExperimentStatus();
        }
        return instance;
    }
    
    public synchronized void setRegion(int region) {
        this.region = region;
    }

    public synchronized int getRegion() {
        return region;
    }

    public synchronized void setVarValue(int varValue) {
        this.varValue = varValue;
    }

    public synchronized int getVarValue() {
        return varValue;
    }

    public synchronized void setRefValue(int refValue) {
        this.refValue = refValue;
    }

    public synchronized int getRefValue() {
        return refValue;
    }

    public synchronized void setRoiIntensity(double roiIntensity) {
        this.roiIntensity = roiIntensity;
    }

    public synchronized double getRoiIntensity() {
        return roiIntensity;
    }

    public synchronized void setRoiSaturatedPixelCount(int satPixelCount) {
        this.roiSaturatedPixelCount = satPixelCount;
    }

    public synchronized int getRoiSaturatedPixelCount() {
        return roiSaturatedPixelCount;
    }
}
