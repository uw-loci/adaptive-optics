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

public class slmAPI {
    /** Creates a new instance of slmcontrol */
    public slmAPI() {
        try {
            String dllPath = new File("slmAPI.dll").getAbsolutePath();
            System.load(dllPath);
        }
        catch (UnsatisfiedLinkError err) {
            JOptionPane.showMessageDialog(null, "Could not find required slmAPI.dll library.", "slmAPI", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private native void sendData(double[] arr1, char flag1);
    public static void slmjava(double[] arr, char flag) {
        new slmAPI().sendData(arr, flag);
    }
    static {

        //System.loadLibrary("slmAPI");
    }
}
