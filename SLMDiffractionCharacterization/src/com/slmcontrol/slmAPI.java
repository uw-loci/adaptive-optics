/*
 * slmAPI.java
 *
 * Created on November 13, 2005, 11:10 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.slmcontrol;

import java.io.File;

import javax.swing.JOptionPane;

import loci.ao.slm.characterization.diffraction.main.Constants;

public class slmAPI {
    /** Creates a new instance of slmcontrol */
    private static slmAPI instance = null;

    private slmAPI() {
        try {
            String dllPath = new File("slmAPI.dll").getAbsolutePath();
            //String dllPath = new File("slmAPI_old.dll").getAbsolutePath();
            if (Constants.DEBUG) {
                System.out.println("dllPath: " + dllPath);
            }
            System.load(dllPath);
        }
        catch (UnsatisfiedLinkError err) {
            JOptionPane.showMessageDialog(null, "Could not find required slmAPI.dll library.", "slmAPI", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static slmAPI getInstance() {
        if (instance == null) {
            instance = new slmAPI();
        }
        return instance;
    }
    
    private synchronized native void sendData(double[] arr1, char flag1);

    public synchronized static void slmjava(double[] arr, char flag) {
        slmAPI.getInstance().sendData(arr, flag);
        arr = null; //release for gc. (not sure if needed)
    }
    /*
    static {
        //System.loadLibrary("slmAPI");
    }*/
}
