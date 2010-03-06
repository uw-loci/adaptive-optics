/*
 * MainFrame.java
 * author: Min Ren 2005-2007, Gunnsteinn Hall 2007-2009
 * Created on December 20, 2005, 3:31 PM
 * this file accepts the zernike polynomials parameter, lut file or picture pattern
 * generate the data sent to SLM.
 */
package slmcalibration;

import java.awt.BorderLayout;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.lang.Math;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.*;
import javax.swing.JOptionPane;
import java.io.*;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import org.netbeans.lib.awtextra.AbsoluteConstraints;
import org.netbeans.lib.awtextra.AbsoluteLayout;


class ZernikePanel {
    ZernikePanel() {
        
    }
}

/**
 *
 */
public class MainWindow
        extends javax.swing.JFrame
        implements WindowListener {

    /**
     * Indicates whether the device is being used, or whether it is running
     * in graphics only mode.
     */
    final boolean USE_DEVICE = true;


    /**
     * Numerical coefficients.
     */
    private int numCoef;
    private double[] dcoef;
    private static double[][] samples;
    private static final int WIDTH = 512,  HEIGHT = 512;

    /**
     * Surface plotter.
     */
    private SurfacePlotter srt;

    private File PatternFile;
    private String Pattern_AbsolutePath;

    /**
     * Zernike coefficients file.
     */
    private File ZerCoefile;

    /**
     * dRad?
     */
    private double dRad;

    /**
     * Remember the focus correction?.
     */
    private double rememberfocus;

    /**
     * Constructor.
     * Initializes and starts the window.
     */
    public MainWindow() {
        initComponents();
        addWindowListener(this);
        fileChooserDialog.setVisible(false);

        //set the max number of zernike polynomails to 30.
        numCoef = 30;
        srt = new SurfacePlotter();
        try {
            srt.buildDisplay(plotPanelLeft);
            srt.buildDisplay1(plotPanelRight);
        } catch (Exception e) {
            System.err.println("Caught IOException: " + e.getMessage());
        }

        dcoef = new double[numCoef];
        samples = new double[1][WIDTH * HEIGHT];
    }

    public void windowActivated(WindowEvent e) { }
    public void windowClosed(WindowEvent e) { }

    //
    /**
     * Called upon window closing, send the power off command to the SLM.
     *
     * @param e Window event object.
     */
    public void windowClosing(WindowEvent e) {
        if (USE_DEVICE) {
            // Power off the system.
            com.slmcontrol.slmAPI.slmjava(samples[0], (char) 65);
        }
    }

    public void windowDeactivated(WindowEvent e) { }
    public void windowDeiconified(WindowEvent e) { }
    public void windowIconified(WindowEvent e) { }
    public void windowOpened(WindowEvent e) { }

    /**
     * Initializes the form.
     * Note: Code initially generated with the NetBeans forms designer.
     */
    private void initComponents() {
        waveFrontHeadingLabel = new JLabel();
        SLMSizeField = new JTextField();
        SLMSizeLabel = new JLabel();

        plotPanelLeft = new JPanel();
        plotPanelRight = new JPanel();

        patternPanel = new JPanel();
        patternHeadingLabel = new JLabel();
        patternBrowseButton = new JButton();
        clearPatternButton = new JButton();
        sendPatternButton = new JButton();

        showImageButton = new JButton();
        sendSLMButton = new JButton();
        powerOffButton = new JButton();

        coefficientsHeadingLabel = new JLabel();
             
        SLMResolutionLabel = new JLabel();
        SLMResolutionTextField = new JTextField();

        getContentPane().setLayout(new AbsoluteLayout());
        
        /* File chooser dialog (used in common). */
        fileChooserDialog = new JFileChooser();
        fileChooserDialog.setPreferredSize(new Dimension(587, 400));
        getContentPane().add(fileChooserDialog, new AbsoluteConstraints(390, 200, -1, -1));

        /* SLM Size & Resolution */
        SLMSizeField.setText("512");
        getContentPane().add(SLMSizeField, new AbsoluteConstraints(490, 30, 60, -1));

        SLMSizeLabel.setFont(new Font("Arial", 1, 12));
        SLMSizeLabel.setText("SLM size");
        getContentPane().add(SLMSizeLabel, new AbsoluteConstraints(390, 30, 80, 20));

        SLMResolutionLabel.setFont(new Font("Arial", 1, 12));
        SLMResolutionLabel.setText("SLM resolution");
        getContentPane().add(SLMResolutionLabel, new AbsoluteConstraints(390, 60, -1, 20));

        SLMResolutionTextField.setText("512");
        getContentPane().add(SLMResolutionTextField, new AbsoluteConstraints(490, 60, 60, -1));

        /* Plot panels: Left, Right. */
        waveFrontHeadingLabel.setFont(new Font("Arial", 1, 20));
        waveFrontHeadingLabel.setForeground(new Color(255, 51, 51));
        waveFrontHeadingLabel.setText("Wave Front");
        getContentPane().add(waveFrontHeadingLabel, new AbsoluteConstraints(630, 90, 120, 30));

        plotPanelLeft.setLayout(new BorderLayout());
        getContentPane().add(plotPanelLeft, new AbsoluteConstraints(360, 140, 350, 350));

        plotPanelRight.setLayout(new BorderLayout());
        getContentPane().add(plotPanelRight, new AbsoluteConstraints(690, 140, 350, 350));

        /* Zernike coefficient panel. */
        coefficient1Label = new JLabel();
        coefficient1TextField = new JTextField();
        
        coefficient3TextField = new JTextField();
        coefficient2Label = new JLabel();
        coefficient2TextField = new JTextField();
        coefficient3Label = new JLabel();
        
        regionsLabel = new JLabel("# Regions: ");
        regionLabel = new JLabel("Region: ");
        regionTextField = new JTextField("1");
        regionsTextField = new JTextField("16");
        
        zernikeCoefficientPanel = new JPanel();
        zernikeCoefficientPanel.setBackground(new Color(204, 204, 204));
        zernikeCoefficientPanel.setLayout(new AbsoluteLayout());
        Border loweredEtchedBorder =
                BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
        Border titledBorder = BorderFactory.createTitledBorder(
                loweredEtchedBorder, "Calibration Options");
        zernikeCoefficientPanel.setBorder(titledBorder);
        
        coefficient1Label.setText("Number of blocks:");
        zernikeCoefficientPanel.add(coefficient1Label, new AbsoluteConstraints(20, 70, 100, -1));

        coefficient1TextField.setText("2");
        zernikeCoefficientPanel.add(coefficient1TextField, new AbsoluteConstraints(130, 70, 100, -1));

        coefficient2Label.setText("Reference value:");
        zernikeCoefficientPanel.add(coefficient2Label, new AbsoluteConstraints(20, 100, 100, -1));

        coefficient2TextField.setText("255");
        zernikeCoefficientPanel.add(coefficient2TextField, new AbsoluteConstraints(130, 100, 100, -1));

        coefficient3Label.setText("Second value:");
        zernikeCoefficientPanel.add(coefficient3Label, new AbsoluteConstraints(20, 130, 100, -1));
        
        coefficient3TextField.setText("128");
        zernikeCoefficientPanel.add(coefficient3TextField, new AbsoluteConstraints(130, 130, 100, -1));
        
        zernikeCoefficientPanel.add(regionsLabel, new AbsoluteConstraints(20, 160, 100, -1));
        zernikeCoefficientPanel.add(regionsTextField, new AbsoluteConstraints(130, 160, 100, -1));
        
        zernikeCoefficientPanel.add(regionLabel, new AbsoluteConstraints(20, 190, 100, -1));
        zernikeCoefficientPanel.add(regionTextField, new AbsoluteConstraints(130, 190, 100, -1));
                

        coefficientsHeadingLabel.setText("SLM Calibration Options");
        zernikeCoefficientPanel.add(coefficientsHeadingLabel, new AbsoluteConstraints(20, 20, 300, 30));
        
        


        getContentPane().add(zernikeCoefficientPanel, new AbsoluteConstraints(10, 10, 330, 400));


        /* Show image, send SLM. */
        showImageButton.setFont(new Font("Arial", 1, 12));
        showImageButton.setText("Show image");
        showImageButton.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                jBut_showMouseClicked(evt);
            }
        });
        getContentPane().add(showImageButton, new AbsoluteConstraints(10, 500, 110, 30));

        sendSLMButton.setFont(new Font("Arial", 1, 12));
        sendSLMButton.setText("Send SLM");
        sendSLMButton.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                jBut_send2slmMouseClicked(evt);
            }
        });
        getContentPane().add(sendSLMButton, new AbsoluteConstraints(240, 500, -1, 30));

        /* Pattern panel. */
        patternTextField = new JTextField();

        patternPanel.setBackground(new Color(204, 204, 204));
        patternPanel.setLayout(new AbsoluteLayout());
        patternPanel.add(patternTextField, new AbsoluteConstraints(10, 70, 310, 20));

        patternBrowseButton.setFont(new Font("Arial", 1, 12));
        patternBrowseButton.setText("Browse");
        patternBrowseButton.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                jBut_patten_brwMouseClicked(evt);
            }
        });
        patternPanel.add(patternBrowseButton, new AbsoluteConstraints(10, 40, -1, -1));

        patternHeadingLabel.setFont(new Font("Arial", 1, 12));
        patternHeadingLabel.setForeground(new Color(255, 51, 51));
        patternHeadingLabel.setText("Select Pattern");
        patternPanel.add(patternHeadingLabel, new AbsoluteConstraints(10, 10, 90, 20));

        clearPatternButton.setFont(new Font("Arial", 1, 12));
        clearPatternButton.setText("Clear");
        clearPatternButton.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                jBut_patten_ClearMouseClicked(evt);
            }
        });
        patternPanel.add(clearPatternButton, new AbsoluteConstraints(240, 40, 80, -1));

        sendPatternButton.setFont(new Font("Arial", 1, 12));
        sendPatternButton.setText("Send");
        sendPatternButton.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                jBut_patten_SendMouseClicked(evt);
            }
        });
        patternPanel.add(sendPatternButton, new AbsoluteConstraints(150, 40, -1, -1));
        getContentPane().add(patternPanel, new AbsoluteConstraints(10, 540, 330, 100));

        /* Power off. */
        powerOffButton.setFont(new Font("Arial", 1, 12));
        powerOffButton.setText("Power Off");
        powerOffButton.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                jBut_poweroffMouseClicked(evt);
            }
        });
        getContentPane().add(powerOffButton, new AbsoluteConstraints(20, 710, 310, 30));

        /* Setup frame. */
        setSize(1080, 780);        
        setLocationRelativeTo(null);
        setTitle("SLM Calibration");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }



    private void jBut_patten_ClearMouseClicked(MouseEvent evt) {
        patternTextField.setText("");
    }

    private void jBut_patten_SendMouseClicked(MouseEvent evt) {
        File f = new File(Pattern_AbsolutePath);
        //String[] formatNames = new String[50];
        //formatNames = ImageIO.getReaderFormatNames();

        BufferedImage bi;
        Raster rasterdata;

        try {
            // read a pattern
            bi = ImageIO.read(f);
            rasterdata = bi.getData();
            samples[0] = rasterdata.getPixels(0, 0, 512, 512, samples[0]);

        } catch (Exception e) {
            System.err.println("Caught IOException: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "I/O exception occurred: " + e.getMessage(), "slm2", JOptionPane.ERROR_MESSAGE);
        }


        try {
            //show the surface on the first one that is without limitation of 0 - 2pi
            srt.showpic(samples);
        } catch (Exception e) {
            System.err.println("Caught IOException: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "I/O exception occurred: " + e.getMessage(), "slm2", JOptionPane.ERROR_MESSAGE);
        }

        for (int i = 0; i < WIDTH * HEIGHT; i++) {
            // limit the data to 0 - 255, 8 bits data
            samples[0][i] = samples[0][i] % 256;
        }


        try {
            //show the surface on the second one that is with limitation of 0 - 2pi
            srt.showpic1(samples);
        } catch (Exception e) {
            System.err.println("Caught IOException: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "I/O exception occurred: " + e.getMessage(), "slm2", JOptionPane.ERROR_MESSAGE);
        }

        if (USE_DEVICE) {
            // send the data to Phase modulator by JNI ---> slmAPI.dll ---> Phase Modulator
            com.slmcontrol.slmAPI.slmjava(samples[0], (char) 0);
        }

    }

    //close the power
    private void jBut_poweroffMouseClicked(MouseEvent evt) {
        if (USE_DEVICE) {
            //send the command to turn off the SLM, code is 65
            com.slmcontrol.slmAPI.slmjava(samples[0], (char) 65);
        }
    }

    // Send the data generated by zernike polynomials.
    private void jBut_send2slmMouseClicked(MouseEvent evt) {
        //zernike polynomials' paramets
        String tf1, tf2, tf3;
        String spec4, spec5, spec6, spec7, spec8, spec9;

        //get the parameters
        tf1 = coefficient1TextField.getText();
        tf2 = coefficient2TextField.getText();
        tf3 = coefficient3TextField.getText();

        dcoef[0] = Double.valueOf(tf1);
        dcoef[1] = Double.valueOf(tf2);
        dcoef[2] = Double.valueOf(tf3);

        // Generate wavefront data.
        samples[0] = fGrating(dcoef);

        try {
            srt.showpic(samples);
        } catch (Exception e) {
            System.err.println("Caught IOException: " + e.getMessage());
        }

        double[][] showsamples = new double[1][WIDTH * HEIGHT];

        for (int i = 0; i < WIDTH * HEIGHT; i++) {
            samples[0][i] = samples[0][i] % 256;
        }
        
        try {
            srt.showpic1(samples);
        } catch (Exception e) {
            System.err.println("Caught IOException: " + e.getMessage());
        }

        if (USE_DEVICE) {
            com.slmcontrol.slmAPI.slmjava(samples[0], (char) 0);
        }
    }

    /**
     * Returns the slm radius size in pixels. (?)
     * @return The slm radius size in pixels. (?)
     */
    private double getRad() {
        String rad;
        double dRad;
        rad = SLMSizeField.getText();
        dRad = Double.valueOf(rad);
        return dRad;
    }

    /**
     * Returns the slm resolution.
     * @return The slm resolution.
     */
    private double getRes() {
        String res;
        double dRes;
        res = SLMResolutionTextField.getText();
        dRes = Double.valueOf(res);
        return dRes;
    }

    /**
     * Generate binary grating patterns.
     *
     * @param dCoef The Zernike coefficient vector.
     * @return The output Zernike image matrix.
     */
    public double[] fGrating(double[] dCoef) {
        int x, y;
        int numberOfBlocks;
        double refValue;
        double secondValue;
        int SLMSIZE, ActSize, start, end;        
        int x1,x2,y1,y2;
        int sqrtReg, xdim, ydim;
        int xreg, yreg;
        Integer region;
        Integer numberOfRegions;

        SLMSIZE = WIDTH;
        
        numberOfBlocks = (int)dCoef[0];
        refValue = dCoef[1];
        secondValue = dCoef[2];
        
        int blockWidth = (int)(SLMSIZE / numberOfBlocks);

        double[] zern = new double[SLMSIZE * SLMSIZE];
        double total;
        
        // Get the set slm size (specified in the window).
        ActSize = (int) getRad();

        // Get the resolution (specified in the window).
        int resolution, restimes;
        resolution = (int) getRes();
        restimes = ActSize / resolution; // GH: restimes=1        

        // Determine start and end positions of the SLM.
        start = (SLMSIZE - ActSize) / 2; // GH: 0
        end = start + ActSize; // GH: 512 (OK, not included).
        
        region = new Integer(regionTextField.getText());
        numberOfRegions = new Integer(regionsTextField.getText());
        /* Figure out a range of [x, y] values which define the region.
         * [x1, x2] and [y1, y2].
         */
        // Ideally numberOfRegions is a quadratic number.
        sqrtReg = (int)Math.sqrt(1.0*numberOfRegions);
        xdim = (int)(1.0f * SLMSIZE / sqrtReg);
        ydim = (int)(1.0f * SLMSIZE / sqrtReg);
        
        /*
         * 00 01 02 03
         * 04 05 06 07
         * 08 09 10 11
         * 12 13 14 15
         */
        xreg = region % sqrtReg;
        x1 = xreg * xdim;  x2 = x1 + xdim;
        yreg = (int)(region / sqrtReg);
        y1 = yreg * ydim; y2 = y1 + ydim;

        System.out.println("numberOfBlocks: " + numberOfBlocks);
        System.out.println("Blockwidth: " + blockWidth);
        
        // set the surface by polynomia parameters, pixel by pixel
        // GH: row=0; row < 512; row++
        for (int row = start; row < end; row++) {
            //reset x

            // GH: col=0; col < 512; col++
            for (int col = start; col < end; col++) {
                //build some terms that are repeated through the equations
                

                int blockNumber = (int)(col / blockWidth);
                int blockType = (blockNumber + 1) % 2;

                if (row >= y1 && row < y2 && col >= x1 && col < x2) {
                    if (blockType == 0) {
                        total = refValue;
                    } else {
                        total = secondValue;
                    }
                } else {
                    total = refValue;
                }
                                
                zern[row * SLMSIZE + col] = (total);
            }
        }

        return zern;
    }
    
    /**
     * Show the slm surface data on computer, but don't send to slm.
     *
     * @param evt The mouse event object.
     */
    private void jBut_showMouseClicked(MouseEvent evt) {
        // TODO add your handling code here:
        String tf1, tf2, tf3;
        tf1 = coefficient1TextField.getText();
        tf2 = coefficient2TextField.getText();
        tf3 = coefficient3TextField.getText();

        dcoef[0] = Double.valueOf(tf1);
        dcoef[1] = Double.valueOf(tf2);
        dcoef[2] = Double.valueOf(tf3);


        //////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////
        samples[0] = fGrating(dcoef);

        try {
            srt.showpic(samples);
        } catch (Exception e) {
            System.err.println("Caught IOException: " + e.getMessage());
        }

        for (int i = 0; i < WIDTH * HEIGHT; i++) {
            samples[0][i] = samples[0][i] % 256;
        }

        try {
            srt.showpic1(samples);
        } catch (Exception e) {
            System.err.println("Caught IOException: " + e.getMessage());
        }
        return;
    }

    /**
     * Reset all parameters to 0.
     * @param evt The mouse event object.
     */
    private void jBut_resetMouseClicked(MouseEvent evt) {
        coefficient1TextField.setText("0");
        coefficient2TextField.setText("0");
        coefficient3TextField.setText("0");

        return;
    }

    /**
     * Open the exist file saved by "save" button that has the parameters.
     * @param evt The mouse event object.
     */
    private void jBut_openMouseClicked(MouseEvent evt) {
        char[] tfin = new char[500];
        String strin = "0";

        String[] strin_arr = new String[30];

        fileChooserDialog.setVisible(true);
        fileChooserDialog.showDialog(this, "Browse");
        ZerCoefile = fileChooserDialog.getSelectedFile();
        int i;
        FileReader InZerCoef;
        try {
            InZerCoef = new FileReader(ZerCoefile);
            i = InZerCoef.read(tfin);

        } catch (Exception e) {
            System.err.println("Caught IOException: " + e.getMessage());
        }

        strin = strin.valueOf(tfin);
        strin_arr = strin.split(", ");

        //read the file and set to the parameters
        coefficient1TextField.setText(strin_arr[0]);
        coefficient2TextField.setText(strin_arr[1]);
        coefficient3TextField.setText(strin_arr[2]);
        return;
    }

    /**
     *
     * @param evt The mouse event object.
     */
    private void jBut_patten_brwMouseClicked(MouseEvent evt) {
        fileChooserDialog.setVisible(true);
        fileChooserDialog.showDialog(this, "Browse");
        PatternFile = fileChooserDialog.getSelectedFile();
        Pattern_AbsolutePath = PatternFile.getAbsolutePath();
        patternTextField.setText(Pattern_AbsolutePath);
    }


    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedLookAndFeelException ex) {
            Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
        }

        new MainWindow().setVisible(true);
    }

    /**
     * File chooser dialog, common for many components.
     */
    private JFileChooser fileChooserDialog;

    /*
     * SLM size and resolution settings.
     */
    private JLabel SLMResolutionLabel;
    private JLabel SLMSizeLabel;
    private JTextField SLMResolutionTextField;
    private JTextField SLMSizeField;

    /*
     * Surface plots (left and right).
     */
    private JLabel waveFrontHeadingLabel;
    private JPanel plotPanelLeft;
    private JPanel plotPanelRight;

    /*
     * Zernike coefficients panel.
     */
    JPanel zernikeCoefficientPanel;
    JLabel coefficientsHeadingLabel;
    JLabel coefficient1Label;
    JLabel coefficient2Label;
    JLabel coefficient3Label;
    JLabel regionsLabel;
    JLabel regionLabel;
    JTextField coefficient1TextField;
    JTextField coefficient2TextField;
    JTextField coefficient3TextField;
    JTextField regionsTextField;
    JTextField regionTextField;

    /*
     * Show/send image controls.
     */
    JButton showImageButton;
    JButton sendSLMButton;

    /**
     * Pattern panel.
     */
    JPanel patternPanel;
    JLabel patternHeadingLabel;
    JTextField patternTextField;
    JButton clearPatternButton;
    JButton sendPatternButton;
    JButton patternBrowseButton;

    /*
     * Power the device off.
     */
    JButton powerOffButton;
}
