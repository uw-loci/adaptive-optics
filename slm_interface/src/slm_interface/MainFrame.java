/*
 * MainFrame.java
 * author: Min Ren 2005-2007, Gunnsteinn Hall 2007-2009
 * Created on December 20, 2005, 3:31 PM
 * this file accepts the zernike polynomials parameter, lut file or picture pattern
 * generate the data sent to SLM.
 */
package slm_interface;

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
public class MainFrame
        extends javax.swing.JFrame
        implements WindowListener {

    /**
     * Indicates whether the device is being used, or whether it is running
     * in graphics only mode.
     */
    final boolean USE_DEVICE = true;

    /**
     * Location of default LUT file.
     */
    final String LUT_PATH_DEFAULT =
            "/home/ghall/AdaptiveOptics/LUT_Files/linear.lut";

    /**
     * Numerical coefficients.
     */
    private int numCoef;
    private double[] ZCoef;
    private static double[][] samples;
    private static final int WIDTH = 512,  HEIGHT = 512;

    /**
     * Surface plotter.
     */
    private SurfacePlotter srt;

    /**
     * LUT file information.
     */
    private File LUTFile;
    private String LUT_AbsolutePath;
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
    private double[] dlut;

    /**
     * Remember the focus correction?.
     */
    private double rememberfocus;

    /**
     * Constructor.
     * Initializes and starts the window.
     */
    public MainFrame() {
        initComponents();
        //useDefaultLUT();
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

        ZCoef = new double[numCoef];
        samples = new double[1][WIDTH * HEIGHT];
        dlut = new double[256];
        for (int i = 0; i < 256; i++) {
            dlut[i] = i;
        }
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

        LUTFilePanel = new JPanel();
        LUTHeadingLabel = new JLabel();
        browseLUTButton = new JButton();
        LUTpathField = new JTextField();

        patternPanel = new JPanel();
        patternHeadingLabel = new JLabel();
        patternBrowseButton = new JButton();
        clearPatternButton = new JButton();
        sendPatternButton = new JButton();

        showImageButton = new JButton();
        sendSLMButton = new JButton();
        powerOffButton = new JButton();

        openCoefficientFileButton = new JButton();
        saveCoefficientsButton = new JButton();
        resetCoefficientsButton = new JButton();

        correctFocusButton = new JButton();
        resetFocusButton = new JButton();
        coefficientsHeadingLabel = new JLabel();
             
        SLMResolutionLabel = new JLabel();
        SLMResolutionTextField = new JTextField();
        cutCenterCheckBox = new javax.swing.JCheckBox();
        squareCheckBox = new javax.swing.JCheckBox();

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
        coefficient1Label = new JLabel("Bias");
        coefficient1TextField = new JTextField("0");
        coefficient2Label = new JLabel("Tilt-X");
        coefficient2TextField = new JTextField("0");
        coefficient3Label = new JLabel("Tilt-Y");
        coefficient3TextField = new JTextField("0");
        coefficient4Label = new JLabel("Defocus");
        coefficient4TextField = new JTextField("0");
        coefficient5Label = new JLabel("1-Astg-Y");
        coefficient5TextField = new JTextField("0");
        coefficient6Label = new JLabel("1-Astg-X");
        coefficient6TextField = new JTextField("0");
        coefficient7Label = new JLabel("1-Coma-X");
        coefficient7TextField = new JTextField("0");
        coefficient8Label = new JLabel("1-Coma-Y");
        coefficient8TextField = new JTextField("0");
        coefficient9Label = new JLabel("1-Tref-Y");
        coefficient9TextField = new JTextField("0");
        coefficient10Label = new JLabel("1-Tref-X");
        coefficient10TextField = new JTextField("0");
        coefficient11Label = new JLabel("1-Spher");
        coefficient11TextField = new JTextField("0");
        coefficient12Label = new JLabel("2-Astg-X");
        coefficient12TextField = new JTextField("0");
        coefficient13Label = new JLabel("2-Astg-Y");
        coefficient13TextField = new JTextField("0");
        coefficient14Label = new JLabel("1-Tetr-X");
        coefficient14TextField = new JTextField("0");
        coefficient15Label = new JLabel("1-Tetr-Y");
        coefficient15TextField = new JTextField("0");
        coefficient16Label = new JLabel("2-Coma-X");
        coefficient16TextField = new JTextField("0");
        coefficient17Label = new JLabel("2-Coma-Y");
        coefficient17TextField = new JTextField("0");
        coefficient18Label = new JLabel("2-Tref-X");
        coefficient18TextField = new JTextField("0");
        coefficient19Label = new JLabel("2-Tref-Y");
        coefficient19TextField = new JTextField("0");
        coefficient20Label = new JLabel("1-Pent-X");
        coefficient20TextField = new JTextField("0");
        coefficient21Label = new JLabel("1-Pent-Y");
        coefficient21TextField = new JTextField("0");
        coefficient22Label = new JLabel("2-Spher");
        coefficient22TextField = new JTextField("0");


        zernikeCoefficientPanel = new JPanel();
        zernikeCoefficientPanel.setBackground(new Color(204, 204, 204));
        zernikeCoefficientPanel.setLayout(new AbsoluteLayout());
        Border loweredEtchedBorder =
                BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
        Border titledBorder = BorderFactory.createTitledBorder(
                loweredEtchedBorder, "Zernike Polynomial Coefficients");
        zernikeCoefficientPanel.setBorder(titledBorder);

        /*
         * L: w 50
         * T: w 60
         * Left:  [L x=20, y=70 ] [T x=90,y=70]  dx=70  dy=30
         *     dx=
         *   Right: [T x=180, y=100] [L x=90, y=100] 
         *
         */

        zernikeCoefficientPanel.add(coefficient1Label,      new AbsoluteConstraints(20, 70, 50, -1));
        zernikeCoefficientPanel.add(coefficient1TextField,  new AbsoluteConstraints(90, 70, 60, -1));
        zernikeCoefficientPanel.add(coefficient2Label,      new AbsoluteConstraints(20, 100, 50, -1));
        zernikeCoefficientPanel.add(coefficient2TextField,  new AbsoluteConstraints(90, 100, 60, -1));
        zernikeCoefficientPanel.add(coefficient3Label,      new AbsoluteConstraints(20, 130, 50, -1));
        zernikeCoefficientPanel.add(coefficient3TextField,  new AbsoluteConstraints(90, 130, 60, -1));
        zernikeCoefficientPanel.add(coefficient4Label,      new AbsoluteConstraints(20, 160, 50, -1));
        zernikeCoefficientPanel.add(coefficient4TextField,  new AbsoluteConstraints(90, 160, 60, -1));
        zernikeCoefficientPanel.add(coefficient5Label,      new AbsoluteConstraints(20, 190, 50, -1));
        zernikeCoefficientPanel.add(coefficient5TextField,  new AbsoluteConstraints(90, 190, 60, -1));
        zernikeCoefficientPanel.add(coefficient6Label,      new AbsoluteConstraints(20, 220, 50, -1));
        zernikeCoefficientPanel.add(coefficient6TextField,  new AbsoluteConstraints(90, 220, 60, -1));
        zernikeCoefficientPanel.add(coefficient7Label,      new AbsoluteConstraints(20, 250, 50, -1));
        zernikeCoefficientPanel.add(coefficient7TextField,  new AbsoluteConstraints(90, 250, 60, -1));
        zernikeCoefficientPanel.add(coefficient8Label,      new AbsoluteConstraints(20, 280, 50, -1));
        zernikeCoefficientPanel.add(coefficient8TextField,  new AbsoluteConstraints(90, 280, 60, -1));
        zernikeCoefficientPanel.add(coefficient9Label,      new AbsoluteConstraints(20, 310, 50, -1));
        zernikeCoefficientPanel.add(coefficient9TextField,  new AbsoluteConstraints(90, 310, 60, -1));
        zernikeCoefficientPanel.add(coefficient10Label,     new AbsoluteConstraints(20, 340, 50, -1));
        zernikeCoefficientPanel.add(coefficient10TextField, new AbsoluteConstraints(90, 340, 60, -1));
        zernikeCoefficientPanel.add(coefficient11Label,     new AbsoluteConstraints(20, 370, 50, -1));
        zernikeCoefficientPanel.add(coefficient11TextField, new AbsoluteConstraints(90, 370, 60, -1));

        zernikeCoefficientPanel.add(coefficient12TextField, new AbsoluteConstraints(170, 70, 60, -1));
        zernikeCoefficientPanel.add(coefficient12Label,     new AbsoluteConstraints(240, 70, 50, -1));
        zernikeCoefficientPanel.add(coefficient13TextField, new AbsoluteConstraints(170, 100, 60, -1));
        zernikeCoefficientPanel.add(coefficient13Label,     new AbsoluteConstraints(240, 100, 50, -1));
        zernikeCoefficientPanel.add(coefficient14TextField, new AbsoluteConstraints(170, 130, 60, -1));
        zernikeCoefficientPanel.add(coefficient14Label,     new AbsoluteConstraints(240, 130, 50, -1));
        zernikeCoefficientPanel.add(coefficient15TextField, new AbsoluteConstraints(170, 160, 60, -1));
        zernikeCoefficientPanel.add(coefficient15Label,     new AbsoluteConstraints(240, 160, 50, -1));
        zernikeCoefficientPanel.add(coefficient16TextField, new AbsoluteConstraints(170, 190, 60, -1));
        zernikeCoefficientPanel.add(coefficient16Label,     new AbsoluteConstraints(240, 190, 50, -1));
        zernikeCoefficientPanel.add(coefficient17TextField, new AbsoluteConstraints(170, 220, 60, -1));
        zernikeCoefficientPanel.add(coefficient17Label,     new AbsoluteConstraints(240, 220, 50, -1));
        zernikeCoefficientPanel.add(coefficient18TextField, new AbsoluteConstraints(170, 250, 60, -1));
        zernikeCoefficientPanel.add(coefficient18Label,     new AbsoluteConstraints(240, 250, 50, -1));
        zernikeCoefficientPanel.add(coefficient19TextField, new AbsoluteConstraints(170, 280, 60, -1));
        zernikeCoefficientPanel.add(coefficient19Label,     new AbsoluteConstraints(240, 280, 50, -1));
        zernikeCoefficientPanel.add(coefficient20TextField, new AbsoluteConstraints(170, 310, 60, -1));
        zernikeCoefficientPanel.add(coefficient20Label,     new AbsoluteConstraints(240, 310, 50, -1));
        zernikeCoefficientPanel.add(coefficient21TextField, new AbsoluteConstraints(170, 340, 60, -1));
        zernikeCoefficientPanel.add(coefficient21Label,     new AbsoluteConstraints(240, 340, 50, -1));
        zernikeCoefficientPanel.add(coefficient22TextField, new AbsoluteConstraints(170, 370, 60, -1));
        zernikeCoefficientPanel.add(coefficient22Label,     new AbsoluteConstraints(240, 370, 50, -1));


        correctFocusButton.setFont(new Font("Arial", 1, 12));
        correctFocusButton.setText("Correct Focus");
        correctFocusButton.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                jBut_lut_changefocusMouseClicked(evt);
            }
        });
        zernikeCoefficientPanel.add(correctFocusButton, new AbsoluteConstraints(180, 220, 120, -1));

        resetFocusButton.setFont(new Font("Arial", 1, 12));
        resetFocusButton.setText("Reset Focus");
        resetFocusButton.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                jBut_lut_resetfocusMouseClicked(evt);
            }
        });
        zernikeCoefficientPanel.add(resetFocusButton, new AbsoluteConstraints(180, 130, -1, -1));

        coefficientsHeadingLabel.setFont(new Font("Arial", 1, 18));
        coefficientsHeadingLabel.setForeground(new Color(255, 51, 51));
        coefficientsHeadingLabel.setText("Zernike Polynomial Coefficicents");
        zernikeCoefficientPanel.add(coefficientsHeadingLabel, new AbsoluteConstraints(20, 20, 300, 30));

        openCoefficientFileButton.setFont(new Font("Arial", 1, 12));
        openCoefficientFileButton.setText("Open");
        openCoefficientFileButton.setToolTipText("");
        openCoefficientFileButton.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                jBut_openMouseClicked(evt);
            }
        });
        zernikeCoefficientPanel.add(openCoefficientFileButton, new AbsoluteConstraints(20, 370, 70, -1));

        saveCoefficientsButton.setFont(new Font("Arial", 1, 12));
        saveCoefficientsButton.setText("Save");
        saveCoefficientsButton.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                jBut_saveMouseClicked(evt);
            }
        });
        zernikeCoefficientPanel.add(saveCoefficientsButton, new AbsoluteConstraints(120, 370, 70, -1));

        resetCoefficientsButton.setFont(new Font("Arial", 1, 12));
        resetCoefficientsButton.setText("Reset");
        resetCoefficientsButton.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                jBut_resetMouseClicked(evt);
            }
        });
        zernikeCoefficientPanel.add(resetCoefficientsButton, new AbsoluteConstraints(230, 370, -1, -1));
        getContentPane().add(zernikeCoefficientPanel, new AbsoluteConstraints(10, 10, 330, 400));

        /* LUT file panel. */
        LUTFilePanel.setBackground(new Color(204, 204, 204));
        LUTFilePanel.setLayout(new AbsoluteLayout());

        LUTHeadingLabel.setFont(new Font("Arial", 1, 12));
        LUTHeadingLabel.setForeground(new Color(255, 51, 51));
        LUTHeadingLabel.setText("Select LUT file");
        LUTFilePanel.add(LUTHeadingLabel, new AbsoluteConstraints(10, 10, 100, 20));

        browseLUTButton.setFont(new Font("Arial", 1, 12));
        browseLUTButton.setText("Browse");
        browseLUTButton.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                jBut_lut_browseMouseClicked(evt);
            }
        });
        LUTFilePanel.add(browseLUTButton, new AbsoluteConstraints(130, 10, -1, -1));

        LUTFilePanel.add(LUTpathField, new AbsoluteConstraints(10, 40, 300, 20));
        getContentPane().add(LUTFilePanel, new AbsoluteConstraints(10, 420, 330, 70));

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

        /* Modulation shape options. */
        cutCenterCheckBox.setText("Cut out center");
        getContentPane().add(cutCenterCheckBox, new AbsoluteConstraints(20, 680, 320, -1));

        squareCheckBox.setText("Use square shape");
        getContentPane().add(squareCheckBox, new AbsoluteConstraints(20, 650, 320, -1));

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
        setTitle("SLM Interface - Zernike Plotter");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    /**
     * Called when the "Reset focus" button is clicked.
     *
     * @param evt The event instance.
     */
    private void jBut_lut_resetfocusMouseClicked(MouseEvent evt) {
        String oldfocus;
        // Take the value remembered before focus is changed due
        // to spherical change.
        oldfocus = String.valueOf((float) rememberfocus);

        // Set the old value to the defocus parameter.
        coefficient4TextField.setText(oldfocus);
    }

    /**
     * MR:
     * The focus value has to be changed if spherical parameter is changed
     * because shperical polynomials will change focus length also
     * according to the formula, changes on focus value from spherical is
     * 3 times of spherical parameter.
     */
    private void recalculate_focus() {
        double sphparameter, focusold, focuschange, secondsph;
        String sph, foc, secsph;
        sph = coefficient11TextField.getText();
        secsph = coefficient22TextField.getText();
        sphparameter = Double.valueOf(sph);
        secondsph = Double.valueOf(secsph);

        // MR:
        // This fomular is gotten by real test.
        // 1. find the relation between focus parameter and real focus change
        //    read from microscopy.
        // 2. Find the relation between spherical parameter and
        //    real focus change.
        // 3. Using three order polynomial curving fit find the relation
        //    between spherical parameter and focus parameter.
        focuschange = -0.00027 * sphparameter * sphparameter * sphparameter 
                + 0.006 * sphparameter * sphparameter
                - 2.456 * sphparameter + 0.48;
        focuschange = focuschange + 2 * secondsph;

        // The relationship:
        //focuschange = -3*sphparameter;
        foc = coefficient4TextField.getText();
        focusold = Double.valueOf(foc);

        // Remember the old value.
        rememberfocus = focusold;
        focusold = focusold - focuschange;

        foc = String.valueOf((float) focusold);
        coefficient4TextField.setText(foc);
    }

    /**
     * The focus value have to be changed for large S.A. and
     * secondary S.A. aberrations.
     *  This is an analytic correction based on numerical calculations
     * of the Strehl ratio for large aberrations with a defocus correction.
     * N.B. These are for very high values of SA.
     */
    private void correct_focus() {
        String foc;
        double SA, S2A, D;

        SA = Double.valueOf(coefficient11TextField.getText());
        S2A = Double.valueOf(coefficient22TextField.getText());

        D = 0.0;

        /* Correction for S.A. defocus. */
        if ((SA >= 0.0) && (SA < 0.78)) {
            D += 0.0;
        } else if ((SA >= 0.78) && (SA < 1.28)) {
            D += 1.0;
        } else if ((SA >= 1.28) && (SA < 1.78)) {
            D += 2.0;
        } else if ((SA >= 1.78) && (SA < 2.23)) {
            D += 3.0;
        } else if ((SA >= 1.23) && (SA < 2.67)) {
            D += 4.0;
        } else if ((SA >= 2.67) && (SA < 3.07)) {
            D += 5.0;
        } else if ((SA >= 3.07) && (SA < 3.50)) {
            D += 6.0;
        } else if ((SA >= 3.50) && (SA < 3.90)) {
            D += 7.0;
        } else if ((SA >= 3.90) && (SA < 4.30)) {
            D += 8.0;
        } else if ((SA >= 4.30) && (SA < 4.70)) {
            D += 9.0;
        } else if ((SA >= 4.70) && (SA < 5.10)) {
            D += 10.0;
        } else if ((SA >= 5.10) && (SA < 5.50)) {
            D += 11.0;
        } else if ((SA >= 5.50) && (SA < 5.87)) {
            D += 12.0;
        } else if ((SA >= 5.87) && (SA < 6.27)) {
            D += 13.0;
        } else if ((SA >= 6.27) && (SA < 6.63)) {
            D += 14.0;
        }

        /* Correction for secondary S.A. defocus. */
        if ((S2A >= 0.0) && (S2A < 0.78)) {
            D += 0.0;
        } else if ((S2A >= 0.78) && (S2A < 1.28)) {
            D += 1.0;
        } else if ((S2A >= 1.28) && (S2A < 1.78)) {
            D += 2.0;
        } else if ((S2A >= 1.78) && (S2A < 2.23)) {
            D += 3.0;
        } else if ((S2A >= 1.23) && (S2A < 2.67)) {
            D += 4.0;
        } else if ((S2A >= 2.67) && (S2A < 3.07)) {
            D += 5.0;
        } else if ((S2A >= 3.07) && (S2A < 3.50)) {
            D += 6.0;
        } else if ((S2A >= 3.50) && (S2A < 3.90)) {
            D += 7.0;
        } else if ((S2A >= 3.90) && (S2A < 4.30)) {
            D += 8.0;
        } else if ((S2A >= 4.30) && (S2A < 4.70)) {
            D += 9.0;
        } else if ((S2A >= 4.70) && (S2A < 5.10)) {
            D += 10.0;
        } else if ((S2A >= 5.10) && (S2A < 5.50)) {
            D += 11.0;
        } else if ((S2A >= 5.50) && (S2A < 5.87)) {
            D += 12.0;
        } else if ((S2A >= 5.87) && (S2A < 6.27)) {
            D += 13.0;
        } else if ((S2A >= 6.27) && (S2A < 6.63)) {
            D += 14.0;
        }

        SA = (1.5) * S2A;
        if ((SA >= 0.0) && (SA < 0.78)) {
            D -= 0.0;
        } else if ((SA >= 0.78) && (SA < 1.28)) {
            D -= 1.0;
        } else if ((SA >= 1.28) && (SA < 1.78)) {
            D -= 2.0;
        } else if ((SA >= 1.78) && (SA < 2.23)) {
            D -= 3.0;
        } else if ((SA >= 1.23) && (SA < 2.67)) {
            D -= 4.0;
        } else if ((SA >= 2.67) && (SA < 3.07)) {
            D -= 5.0;
        } else if ((SA >= 3.07) && (SA < 3.50)) {
            D -= 6.0;
        } else if ((SA >= 3.50) && (SA < 3.90)) {
            D -= 7.0;
        } else if ((SA >= 3.90) && (SA < 4.30)) {
            D -= 8.0;
        } else if ((SA >= 4.30) && (SA < 4.70)) {
            D -= 9.0;
        } else if ((SA >= 4.70) && (SA < 5.10)) {
            D -= 10.0;
        } else if ((SA >= 5.10) && (SA < 5.50)) {
            D -= 11.0;
        } else if ((SA >= 5.50) && (SA < 5.87)) {
            D -= 12.0;
        } else if ((SA >= 5.87) && (SA < 6.27)) {
            D -= 13.0;
        } else if ((SA >= 6.27) && (SA < 6.63)) {
            D -= 14.0;
        }

        coefficient4TextField.setText(String.valueOf(D));
    }

    private void jBut_lut_changefocusMouseClicked(MouseEvent evt) {
        //recalculate_focus();
        correct_focus(); //GH
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

        dlut = readLUT();

        try {
            //show the surface on the first one that is without limitation of 0 - 2pi
            srt.showpic(samples);
        } catch (Exception e) {
            System.err.println("Caught IOException: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "I/O exception occurred: " + e.getMessage(), "slm2", JOptionPane.ERROR_MESSAGE);
        }

        for (int i = 0; i < WIDTH * HEIGHT; i++) {
            // add the lut process at here.
            // limit the data to 0 - 255, 8 bits data
            samples[0][i] = (int) dlut[(int) Math.round(samples[0][i]) % 256];
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
        String tf1, tf2, tf3, tf4, tf5, tf6, tf7, tf8, tf9, tf10, tf11, tf12, tf13, tf14, tf15, tf16;
        String tf17, tf18, tf19, tf20, tf21, tf22, tf23, tf24;
        String spec4, spec5, spec6, spec7, spec8, spec9;

        //read the look up table
        dlut = readLUT();
        //get the parameters
        ZCoef[0] = Double.valueOf(coefficient1TextField.getText());
        ZCoef[1] = Double.valueOf(coefficient2TextField.getText());
        ZCoef[2] = Double.valueOf(coefficient3TextField.getText());
        ZCoef[3] = Double.valueOf(coefficient4TextField.getText());
        ZCoef[4] = Double.valueOf(coefficient5TextField.getText());
        ZCoef[5] = Double.valueOf(coefficient6TextField.getText());
        ZCoef[6] = Double.valueOf(coefficient7TextField.getText());
        ZCoef[7] = Double.valueOf(coefficient8TextField.getText());
        ZCoef[8] = Double.valueOf(coefficient9TextField.getText());
        ZCoef[9] = Double.valueOf(coefficient10TextField.getText());
        ZCoef[10] = Double.valueOf(coefficient11TextField.getText());
        ZCoef[11] = Double.valueOf(coefficient12TextField.getText());
        ZCoef[12] = Double.valueOf(coefficient13TextField.getText());
        ZCoef[13] = Double.valueOf(coefficient14TextField.getText());
        ZCoef[14] = Double.valueOf(coefficient15TextField.getText());
        ZCoef[15] = Double.valueOf(coefficient16TextField.getText());
        ZCoef[16] = Double.valueOf(coefficient17TextField.getText());
        ZCoef[17] = Double.valueOf(coefficient18TextField.getText());
        ZCoef[18] = Double.valueOf(coefficient19TextField.getText());
        ZCoef[19] = Double.valueOf(coefficient20TextField.getText());
        ZCoef[20] = Double.valueOf(coefficient21TextField.getText());
        ZCoef[21] = Double.valueOf(coefficient22TextField.getText());

        //generate wavefront data by zernike polynomials parameters
        samples[0] = generateZernikeWavefront();

        try {
            srt.showpic(samples);
        } catch (Exception e) {
            System.err.println("Caught IOException: " + e.getMessage());
        }

        double[][] showsamples = new double[1][WIDTH * HEIGHT];

        //double [][] show_samples = new double[1][WIDTH * HEIGHT];

        for (int i = 0; i < WIDTH * HEIGHT; i++) {
            double startVal = samples[0][i];
            samples[0][i] = (samples[0][i]) * 256;
            samples[0][i] = (Math.floor(samples[0][i]));
            int val = (int) (samples[0][i]);
            val %= 256;
            samples[0][i] = val;
            //System.out.println("startVal: " + startVal + " -> val is: " + val);
        }

        for (int i = 0; i < WIDTH * HEIGHT; i++) {
            int index = (int) (samples[0][i]);
            samples[0][i] = dlut[index];
        }

        
        /*
        double XTilt = Double.valueOf(tf2);
        if (XTilt > 5) {
            for (int row = 0; row < 512; row++) {
                for (int col = 0; col < 512; col++) {

                    double radius = 175;
                    double realx = (row - 256)/radius;
                    double realy = (col - 256)/radius;

                    double r = realx * realx + realy * realy;
                    if (r <= 1.0) {
                        
                        samples[0][row * 512 + col] += 255 - (255 - XTilt)*(row - 81)/350;
                    }

                    //samples[0][row * SLMSIZE + col] = (total);
                }
            }*/

            // take the modulo.
        /*
            for (int i = 0; i < WIDTH * HEIGHT; i++) {
                samples[0][i] = samples[0][i] % 256;
            }
        }*/
        
        
        
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
     * Reads and loads the look up table (from a file).
     * @return The look up table.
     */
    private double[] readLUT() {
        double[] lut = new double[256];
        char[] inpu = new char[3000];
        int i = 0;
        String temp;
        String[] temp1 = new String[512];

        FileReader Inlut;

        if (LUTFile == null) {
            JOptionPane.showMessageDialog(this, "Please select a lut file",
                    "slm2", JOptionPane.ERROR_MESSAGE);
        }

        try {
            Inlut = new FileReader(LUTFile);
            Inlut.read(inpu);
        } catch (Exception e) {
            System.err.println("Caught IOException: " + e.getMessage());
        }

        temp = String.valueOf(inpu);
        temp = temp.replaceAll("\r\n", " ");
        temp = temp.replaceAll("\t", " ");
        temp = temp.replaceAll("  ", " ");
        temp1 = temp.split(" ");

        for (i = 0; i < 256; i++) {
            lut[i] = Double.valueOf(temp1[2 * i + 1]);
        }
        return lut;
    }

    /**
     * Generate slm surface data according to zernike polynomial parameters.
     *
     * @param ZCoef The Zernike coefficient vector.
     * @return The output Zernike image matrix.
     */
    public double[] generateZernikeWavefront() {
//        double realx, realy, Radius;
        double radius;
        double xs,
               xs2,
               xs3,
               xs4,
               xs5,
               ys,
               ys2,
               ys3,
               ys4,
               ys5,
               p2,
               p4,
               p6;
        double Z1,  //Piston, 
               Z2,  //XTilt, 
               Z3,  //YTilt, 
               Z4,  //Defocus, 
               Z5,  //AstigOne, 
               Z6,  //AstigTwo, 
               Z7,  //ComaY,
               Z8,  //ComaX, 
               Z9,  //TrefoilY, 
               Z10, //TrefoilX, 
               Z11, //PrimarySpherical,
               Z12, //SecondaryAstigX, 
               Z13, //SecondaryAstigY, 
               Z14, //TetrafoilX,
               Z15, //TetrafoilY,
               Z16, //SecondaryComaX,
               Z17, //SecondaryComaY,
               Z18, //SecTrefoilX,
               Z19, //SecTrefoilY,
               Z20, //PentafoilX,
               Z21, //PentafoilY,
               Z22; //SecondarySpherical;
        int defocusbins, stigxbins, stigybins, comaxbins, comaybins, speribins;

        int SLMSIZE, ActSize, start, end;

        SLMSIZE = WIDTH;
        double[] zern = new double[SLMSIZE * SLMSIZE];
        double[] defoterm = new double[SLMSIZE * SLMSIZE];
        double[] stigxterm = new double[SLMSIZE * SLMSIZE];
        double[] stigyterm = new double[SLMSIZE * SLMSIZE];
        double[] comaxterm = new double[SLMSIZE * SLMSIZE];
        double[] comayterm = new double[SLMSIZE * SLMSIZE];
        double[] speriterm = new double[SLMSIZE * SLMSIZE];



        /*
        for (int i = 0; i < SLMSIZE; i++)
        for(int j = 0; j < SLMSIZE; j++)
        zern[i*SLMSIZE+j] = 0;
         */
        // Get the set slm size (specified in the window).
        ActSize = (int) getRad();

        // Get the resolution (specified in the window).
        int resolution, restimes;
        resolution = (int) getRes();
        restimes = ActSize / resolution;
        // GH: restimes=1

        // change the square to round
        // change the slm size to radius, if slm size if max value (512), the radius is 300.
        //radius = ActSize*300/512;
        //radius = ActSize * 256 / 512;
        //radius = ActSize * 175 / 512;
        // GH: Selected radius 175 (diam: 350) to make field match scan mirrors
        // (entrance pupil).
        radius = ActSize * 220 / 512; // radius 220 for S-H.

        System.out.println("radius is " + radius);

        // if the set slm size is not 512, take the center of the phase modulator
        start = (SLMSIZE - ActSize) / 2; // GH: 0
        end = start + ActSize; // GH: 512 (OK, not included).

        System.out.println("Start is " + start+ " end is " + end);

        // GH: Is this resolution stuff still applicable?
        

        // set the surface by polynomia parameters, pixel by pixel
        // GH: row=0; row < 512; row++        
        for (int row = 0; row < 512; row++) {
            for (int col = 0; col < 512; col++) {
                xs = (row - 256) / radius; // -256/300 = -0.8533
                ys = (col - 256) / radius; // +256/300 = -0.8533
                p2 = xs*xs + ys*ys;
                p4 = p2*p2;
                p6 = p4*p2;
                xs2 = xs*xs;
                xs3 = xs2 * xs;
                xs4 = xs3 * xs;
                xs5 = xs4 * xs;
                ys2 = ys*ys;
                ys3 = ys2 * ys;
                ys4 = ys3 * ys;
                ys5 = ys4 * ys;
                
                double total = 0;
                
                if ((squareCheckBox.isSelected() || p2 <= 1) &&
                        (!cutCenterCheckBox.isSelected() ||
                        (p2 >= 0.10))) {
                    
                    /*
                    double scalingFactor = 1.00;
                    xSc = realx / radius * scalingFactor; // -256/300 = -0.8533
                    = realy / radius * scalingFactor; // +256/300 = -0.8533
                    XSquPlusYSqu = divX * divX + divY * divY;
                    XPYSquSqu = XSquPlusYSqu * XSquPlusYSqu;
                    divXSqu = divX * divX;
                    divYSqu = divY * divY;
                    */
                    
                    /*
                     * XXX/NOTE:
                     * Min Ren had all coefficients like: Coeff/2, instead of the like sqrt(5) in front of sph. aberration.
                     * (conventional definition).  
                     * XXX/NOTE: Changed to conventional definitions.
                     */
                
                    double[] term = new double[22];
                    term[0]  =  ZCoef[0]*(1);
                    term[1]  =  ZCoef[1]*(2*xs);
                    term[2]  =  ZCoef[2]*(2*ys);
                    term[3]  =  ZCoef[3]*(Math.sqrt(3)) *(2*xs2 + 2*ys2 - 1);
                    term[4]  =  ZCoef[4]*(Math.sqrt(6)) *(2*xs*ys); 
                    term[5]  =  ZCoef[5]*(Math.sqrt(6)) *(xs2 - ys2);
                    term[6]  =  ZCoef[6]*(Math.sqrt(8)) *(3*ys*xs2 + 3*ys3 - 2*ys);
                    term[7]  =  ZCoef[7]*(Math.sqrt(8)) *(3*xs3 + 3*xs*ys2 - 2*xs);
                    term[8]  =  ZCoef[8]*(Math.sqrt(8)) *(3*ys*xs2 - ys3);
                    term[9]  =  ZCoef[9]*(Math.sqrt(8)) *(xs3 - 3*xs*ys2);
                    term[10] = ZCoef[10]*(Math.sqrt(5)) *(6*p4 - 6*p2 + 1);
                    term[11] = ZCoef[11]*(Math.sqrt(10))*(4*xs4 - 4*ys4 - 3*xs2 + 3*ys2); 
                    term[12] = ZCoef[12]*(Math.sqrt(10))*(8*xs3*ys + 8*xs*ys3 - 6*xs*ys);
                    term[13] = ZCoef[13]*(Math.sqrt(10))*(xs4 - 6*xs2*ys2 + ys4);
                    term[14] = ZCoef[14]*(Math.sqrt(10))*(4*xs3*ys - 4*xs*ys3);
                    term[15] = ZCoef[15]*(Math.sqrt(12))*(10*xs5 + 20*xs3*ys2 + 10*xs*ys4 - 12*xs3 - 12*xs*ys2 + 3*xs); 
                    term[16] = ZCoef[16]*(Math.sqrt(12))*(10*ys*xs4 + 20*xs2*ys3 + 10*ys5 - 12*ys*xs2 - 12*ys3 + 3*ys); 
                    term[17] = ZCoef[17]*(Math.sqrt(12))*(5*xs5 - 10*xs3*ys2 - 15*xs*ys4 - 4*xs3 + 12*xs*ys2);
                    term[18] = ZCoef[18]*(Math.sqrt(12))*(15*ys*xs4 + 10*xs2*ys3 - 5*ys5 - 12*ys*xs2 + 4*ys3);
                    term[19] = ZCoef[19]*(Math.sqrt(12))*(xs5 - 10*xs3*ys2 + 5*xs*ys4);
                    term[20] = ZCoef[20]*(Math.sqrt(12))*(5*ys*xs4 - 10*xs2*ys3 + ys5);
                    term[21] = ZCoef[21]*(Math.sqrt(7)) *(20*p6 - 30*p4 + 12*p2 - 1);
                     
                    total = 0;
                    for (int i = 0; i < 22; i++) {
                      total += term[i];
                    }
                } else {
                    total = 0;
                }


                zern[row * SLMSIZE + col] = (total);
            }
        }
        
        // Find the smallest value.
        double smallestVal = 0;
        boolean isFirst = true;
        
        for (int row = 0; row < 512; row++) {
            // GH: col=0; col < 512; col++
            for (int col = 0; col < 512; col++) {
                xs = (row - 256) / radius; // -256/300 = -0.8533
                ys = (col - 256) / radius; // +256/300 = -0.8533
                p2 = xs*xs + ys*ys;
                
                if ((squareCheckBox.isSelected() || p2 <= 1) &&
                        (!cutCenterCheckBox.isSelected() ||
                        (p2 >= 0.10))) {
                    
                    int i = row * SLMSIZE + col;
                    if (isFirst || zern[i] < smallestVal) {
                        smallestVal = zern[i];
                        isFirst = false;
                    }
                }
            }
        }

        
        //Subtract it (the smallest value) everywhere.
        for (int row = 0; row < 512; row++) {
            // GH: col=0; col < 512; col++
            for (int col = 0; col < 512; col++) {
                xs = (row - 256) / radius; // -256/300 = -0.8533
                ys = (col - 256) / radius; // +256/300 = -0.8533
                p2 = xs*xs + ys*ys;
 
                if ((squareCheckBox.isSelected() || p2 <= 1) &&
                        (!cutCenterCheckBox.isSelected() ||
                        (p2 >= 0.10))) {
                    
                    int i = row * SLMSIZE + col;
                    zern[i] -= smallestVal;
                }
            }
        }


        return zern;
    }


    /**
     * Save the parameter to a file so the parameters can be loaded
     * by "open" button later.
     *
     * @param evt The mouse event object.
     */
    private void jBut_saveMouseClicked(MouseEvent evt) {
        String tf1, tf2, tf3, tf4, tf5, tf6, tf7, tf8, tf9, tf10, tf11, tf12, tf13, tf14, tf15, tf16;
        String tf17, tf18, tf19, tf20, tf21, tf22, tfout;
        //get the parameters
        tf1 = coefficient1TextField.getText();
        tf2 = coefficient2TextField.getText();
        tf3 = coefficient3TextField.getText();
        tf4 = coefficient4TextField.getText();
        tf5 = coefficient5TextField.getText();
        tf6 = coefficient6TextField.getText();
        tf7 = coefficient7TextField.getText();
        tf8 = coefficient8TextField.getText();
        tf9 = coefficient9TextField.getText();
        tf10 = coefficient10TextField.getText();
        tf11 = coefficient11TextField.getText();
        tf12 = coefficient12TextField.getText();
        tf13 = coefficient13TextField.getText();
        tf14 = coefficient14TextField.getText();
        tf15 = coefficient15TextField.getText();
        tf16 = coefficient16TextField.getText();
        tf17 = coefficient17TextField.getText();
        tf18 = coefficient18TextField.getText();
        tf19 = coefficient19TextField.getText();
        tf20 = coefficient20TextField.getText();
        tf21 = coefficient21TextField.getText();
        tf22 = coefficient22TextField.getText();

        tfout = tf1 + ", " + tf2 + ", " + tf3 + ", " + tf4 + ", " + tf5 + ", " + tf6 + ", " + tf7 + ", " + tf8;
        tfout = tfout + ", " + tf9 + ", " + tf10 + ", " + tf11 + ", " + tf12 + ", " + tf13 + ", " + tf14 + ", " + tf15 + ", " + tf16;
        tfout = tfout + ", " + tf17 + ", " + tf18 + ", " + tf19 + ", " + tf20 + ", " + tf21 + ", " + tf22;

        //write to the file
        fileChooserDialog.setVisible(true);
        fileChooserDialog.showDialog(this, "save");
        ZerCoefile = fileChooserDialog.getSelectedFile();

        try {
            FileWriter OutZerCoef;
            OutZerCoef = new FileWriter(ZerCoefile);
            OutZerCoef.write(tfout);
            OutZerCoef.close();

        } catch (Exception e) {
            System.err.println("Caught IOException: " + e.getMessage());
        }
        return;
    }

    //
    /**
     * Show the slm surface data on computer, but don't send to slm.
     *
     * @param evt The mouse event object.
     */
    private void jBut_showMouseClicked(MouseEvent evt) {
        // TODO add your handling code here:
        ZCoef[0] = Double.valueOf(coefficient1TextField.getText());
        ZCoef[1] = Double.valueOf(coefficient2TextField.getText());
        ZCoef[2] = Double.valueOf(coefficient3TextField.getText());
        ZCoef[3] = Double.valueOf(coefficient4TextField.getText());
        ZCoef[4] = Double.valueOf(coefficient5TextField.getText());
        ZCoef[5] = Double.valueOf(coefficient6TextField.getText());
        ZCoef[6] = Double.valueOf(coefficient7TextField.getText());
        ZCoef[7] = Double.valueOf(coefficient8TextField.getText());
        ZCoef[8] = Double.valueOf(coefficient9TextField.getText());
        ZCoef[9] = Double.valueOf(coefficient10TextField.getText());
        ZCoef[10] = Double.valueOf(coefficient11TextField.getText());
        ZCoef[11] = Double.valueOf(coefficient12TextField.getText());
        ZCoef[12] = Double.valueOf(coefficient13TextField.getText());
        ZCoef[13] = Double.valueOf(coefficient14TextField.getText());
        ZCoef[14] = Double.valueOf(coefficient15TextField.getText());
        ZCoef[15] = Double.valueOf(coefficient16TextField.getText());
        ZCoef[16] = Double.valueOf(coefficient17TextField.getText());
        ZCoef[17] = Double.valueOf(coefficient18TextField.getText());
        ZCoef[18] = Double.valueOf(coefficient19TextField.getText());
        ZCoef[19] = Double.valueOf(coefficient20TextField.getText());
        ZCoef[20] = Double.valueOf(coefficient21TextField.getText());
        ZCoef[21] = Double.valueOf(coefficient22TextField.getText());


        //////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////
        samples[0] = generateZernikeWavefront();

        try {
            srt.showpic(samples);
        } catch (Exception e) {
            System.err.println("Caught IOException: " + e.getMessage());
        }

        for (int i = 0; i < WIDTH * HEIGHT; i++) {
            samples[0][i] = ((samples[0][i] * 256)) % 256;
            if (samples[0][i] < 0) {
                samples[0][i] = samples[0][i] + 256;
            }
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
        coefficient4TextField.setText("0");
        coefficient5TextField.setText("0");
        coefficient6TextField.setText("0");
        coefficient7TextField.setText("0");
        coefficient8TextField.setText("0");
        coefficient9TextField.setText("0");
        coefficient10TextField.setText("0");
        coefficient11TextField.setText("0");
        coefficient12TextField.setText("0");
        coefficient13TextField.setText("0");
        coefficient14TextField.setText("0");
        coefficient15TextField.setText("0");
        coefficient16TextField.setText("0");
        coefficient17TextField.setText("0");
        coefficient18TextField.setText("0");
        coefficient19TextField.setText("0");
        coefficient20TextField.setText("0");
        coefficient21TextField.setText("0");
        coefficient22TextField.setText("0");

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
        coefficient4TextField.setText(strin_arr[3]);
        coefficient5TextField.setText(strin_arr[4]);
        coefficient6TextField.setText(strin_arr[5]);
        coefficient7TextField.setText(strin_arr[6]);
        coefficient8TextField.setText(strin_arr[7]);
        coefficient9TextField.setText(strin_arr[8]);
        coefficient10TextField.setText(strin_arr[9]);
        coefficient11TextField.setText(strin_arr[10]);
        coefficient12TextField.setText(strin_arr[11]);
        coefficient13TextField.setText(strin_arr[12]);
        coefficient14TextField.setText(strin_arr[13]);
        coefficient15TextField.setText(strin_arr[14]);
        coefficient16TextField.setText(strin_arr[15]);
        coefficient17TextField.setText(strin_arr[16]);
        coefficient18TextField.setText(strin_arr[17]);
        coefficient19TextField.setText(strin_arr[18]);
        coefficient20TextField.setText(strin_arr[19]);
        coefficient21TextField.setText(strin_arr[20]);
        coefficient22TextField.setText(strin_arr[21]);
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
     * Use the default LUT file.  (Initially).
     */
    private void useDefaultLUT()
    {
        LUTFile = new File(LUT_PATH_DEFAULT);
        LUT_AbsolutePath = LUTFile.getAbsolutePath();
        LUTpathField.setText(LUT_AbsolutePath);
        fileChooserDialog.setSelectedFile(LUTFile);
    }
    /**
     *
     * @param evt The mouse event object.
     */
    private void jBut_lut_browseMouseClicked(MouseEvent evt) {
        fileChooserDialog.setVisible(true);
        fileChooserDialog.showDialog(this, "Browse");
        LUTFile = fileChooserDialog.getSelectedFile();
        LUT_AbsolutePath = LUTFile.getAbsolutePath();
        LUTpathField.setText(LUT_AbsolutePath);
    }

    /**
     * ??
     * Calculate the average bin size.??
     * @param term Terms?
     * @param size Size?
     * @return ??
     */
    /*
    private double[] averagebinsize(double[] term, int size) {
        double sum, average;
        average = 0;
        for (int ii = 0; ii < 512; ii = ii + size) {
            for (int jj = 0; jj < 512; jj = jj + size) {
                sum = 0;
                for (int kk = 0; kk < size; kk++) {
                    for (int ll = 0; ll < size; ll++) {
                        sum = sum + term[(ii + kk) * 512 + jj + ll];
                    }
                }
                average = sum / (size * size);
                for (int kk = 0; kk < size; kk++) {
                    for (int ll = 0; ll < size; ll++) {
                        term[(ii + kk) * 512 + jj + ll] = average;
                    }
                }
            }
        }
        return term;
    }
     */

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedLookAndFeelException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }

        new MainFrame().setVisible(true);
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
    JLabel coefficient4Label;
    JLabel coefficient5Label;
    JLabel coefficient6Label;
    JLabel coefficient7Label;
    JLabel coefficient8Label;
    JLabel coefficient9Label;
    JLabel coefficient10Label;
    JLabel coefficient11Label;
    JLabel coefficient12Label;
    JLabel coefficient13Label;
    JLabel coefficient14Label;
    JLabel coefficient15Label;
    JLabel coefficient16Label;
    JLabel coefficient17Label;
    JLabel coefficient18Label;
    JLabel coefficient19Label;
    JLabel coefficient20Label;
    JLabel coefficient21Label;
    JLabel coefficient22Label;
    JTextField coefficient1TextField;
    JTextField coefficient2TextField;
    JTextField coefficient3TextField;
    JTextField coefficient4TextField;
    JTextField coefficient5TextField;
    JTextField coefficient6TextField;
    JTextField coefficient7TextField;
    JTextField coefficient8TextField;
    JTextField coefficient9TextField;
    JTextField coefficient10TextField;
    JTextField coefficient11TextField;
    JTextField coefficient12TextField;
    JTextField coefficient13TextField;
    JTextField coefficient14TextField;
    JTextField coefficient15TextField;
    JTextField coefficient16TextField;
    JTextField coefficient17TextField;
    JTextField coefficient18TextField;
    JTextField coefficient19TextField;
    JTextField coefficient20TextField;
    JTextField coefficient21TextField;
    JTextField coefficient22TextField;

    JButton correctFocusButton;
    JButton resetFocusButton;
    JButton openCoefficientFileButton;
    JButton resetCoefficientsButton;
    JButton saveCoefficientsButton;

    /*
     * LUT file panel.
     */
    JPanel LUTFilePanel;
    JLabel LUTHeadingLabel;
    JTextField LUTpathField;
    JButton browseLUTButton;

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
     * Modulation shape options.
     */
    JCheckBox squareCheckBox;
    JCheckBox cutCenterCheckBox;

    /*
     * Power the device off.
     */
    JButton powerOffButton;
}
