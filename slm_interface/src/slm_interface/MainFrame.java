/*
 * MainFrame.java
 * author: Min Ren 2005-2007, Gunnsteinn Hall 2007-2009
 * Created on December 20, 2005, 3:31 PM
 * this file accepts the zernike polynomials parameter, lut file or picture pattern
 * generate the data sent to SLM.
 *
 *
 *change record,
 *1) line635 + ---> -
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
    private double[] dcoef;
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

        dcoef = new double[numCoef];
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
        coefficient1Label = new JLabel();
        coefficient1TextField = new JTextField();
        coefficient3TextField = new JTextField();
        coefficient2Label = new JLabel();
        coefficient2TextField = new JTextField();
        coefficient3Label = new JLabel();
        coefficient6TextFields = new JTextField();
        coefficient4Label = new JLabel();
        coefficient5TextField = new JTextField();
        coefficient5Label = new JLabel();
        coefficient4TextField = new JTextField();
        coefficient6Label = new JLabel();
        coefficient7Label = new JLabel();
        coefficient7TextField = new JTextField();
        coefficient8Label = new JLabel();
        coefficient10TextField = new JTextField();
        coefficient9Label = new JLabel();
        coefficient8TextField = new JTextField();
        coefficient10Label = new JLabel();
        coefficient11TextField = new JTextField();
        coefficient11Label = new JLabel();
        coefficient9TextField = new JTextField();
        coefficient12Label = new JLabel();
        coefficient12TextField = new JTextField();
        coefficient13Label = new JLabel();
        coefficient13TextField = new JTextField();
        coefficient14Label = new JLabel();
        coefficient14TextField = new JTextField();
        coefficient15Label = new JLabel();
        coefficient15TextField = new JTextField();
        coefficient16TextField = new JTextField();
        coefficient18Label = new JLabel();
        zernikeCoefficientPanel = new JPanel();
        zernikeCoefficientPanel.setBackground(new Color(204, 204, 204));
        zernikeCoefficientPanel.setLayout(new AbsoluteLayout());
        Border loweredEtchedBorder =
                BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
        Border titledBorder = BorderFactory.createTitledBorder(
                loweredEtchedBorder, "Zernike Polynomial Coefficients");
        zernikeCoefficientPanel.setBorder(titledBorder);

        coefficient1Label.setFont(new Font("Arial", 1, 12));
        coefficient1Label.setText("Piston");
        zernikeCoefficientPanel.add(coefficient1Label, new AbsoluteConstraints(20, 70, 40, -1));

        coefficient1TextField.setText("0");
        zernikeCoefficientPanel.add(coefficient1TextField, new AbsoluteConstraints(90, 70, 60, -1));

        coefficient3TextField.setText("0");
        zernikeCoefficientPanel.add(coefficient3TextField, new AbsoluteConstraints(250, 100, 60, -1));

        coefficient2Label.setFont(new Font("Arial", 1, 12));
        coefficient2Label.setText("Tilt y");
        zernikeCoefficientPanel.add(coefficient2Label, new AbsoluteConstraints(180, 100, 70, -1));

        coefficient2TextField.setText("0");
        zernikeCoefficientPanel.add(coefficient2TextField, new AbsoluteConstraints(90, 100, 60, -1));

        coefficient3Label.setFont(new Font("Arial", 1, 12));
        coefficient3Label.setText("Tilt x");
        zernikeCoefficientPanel.add(coefficient3Label, new AbsoluteConstraints(20, 100, 30, -1));

        coefficient6TextFields.setText("0");
        zernikeCoefficientPanel.add(coefficient6TextFields, new AbsoluteConstraints(250, 160, 60, -1));

        coefficient4Label.setFont(new Font("Arial", 1, 12));
        coefficient4Label.setText("Astig y");
        zernikeCoefficientPanel.add(coefficient4Label, new AbsoluteConstraints(180, 160, 60, -1));

        coefficient5TextField.setText("0");
        zernikeCoefficientPanel.add(coefficient5TextField, new AbsoluteConstraints(90, 160, 60, -1));

        coefficient5Label.setFont(new Font("Arial", 1, 12));
        coefficient5Label.setText("Astig x");
        zernikeCoefficientPanel.add(coefficient5Label, new AbsoluteConstraints(20, 160, 50, -1));

        coefficient4TextField.setText("0");
        zernikeCoefficientPanel.add(coefficient4TextField, new AbsoluteConstraints(90, 130, 60, -1));

        coefficient6Label.setFont(new Font("Arial", 1, 12));
        coefficient6Label.setText("Defocus");
        zernikeCoefficientPanel.add(coefficient6Label, new AbsoluteConstraints(20, 130, 50, -1));

        coefficient7Label.setFont(new Font("Arial", 1, 12));
        coefficient7Label.setText("Coma x");
        zernikeCoefficientPanel.add(coefficient7Label, new AbsoluteConstraints(20, 190, 50, -1));

        coefficient7TextField.setText("0");
        zernikeCoefficientPanel.add(coefficient7TextField, new AbsoluteConstraints(90, 190, 60, -1));

        coefficient8Label.setFont(new Font("Arial", 1, 12));
        coefficient8Label.setText("Trefoil x");
        zernikeCoefficientPanel.add(coefficient8Label, new AbsoluteConstraints(20, 250, 70, -1));

        coefficient10TextField.setText("0");
        zernikeCoefficientPanel.add(coefficient10TextField, new AbsoluteConstraints(90, 250, 60, -1));

        coefficient9Label.setFont(new Font("Arial", 1, 12));
        coefficient9Label.setText("Coma y");
        zernikeCoefficientPanel.add(coefficient9Label, new AbsoluteConstraints(180, 190, 70, -1));

        coefficient8TextField.setText("0");
        zernikeCoefficientPanel.add(coefficient8TextField, new AbsoluteConstraints(250, 190, 60, -1));

        coefficient10Label.setFont(new Font("Arial", 1, 12));
        coefficient10Label.setText("Trefoil y");
        zernikeCoefficientPanel.add(coefficient10Label, new AbsoluteConstraints(180, 250, 70, -1));

        coefficient11TextField.setText("0");
        zernikeCoefficientPanel.add(coefficient11TextField, new AbsoluteConstraints(250, 250, 60, -1));

        coefficient11Label.setFont(new Font("Arial", 1, 12));
        coefficient11Label.setText("Spherical");
        zernikeCoefficientPanel.add(coefficient11Label, new AbsoluteConstraints(20, 220, 80, -1));

        coefficient9TextField.setText("0");
        zernikeCoefficientPanel.add(coefficient9TextField, new AbsoluteConstraints(90, 220, 60, -1));

        coefficient12Label.setFont(new Font("Arial", 1, 12));
        coefficient12Label.setText("2nd Astig x");
        zernikeCoefficientPanel.add(coefficient12Label, new AbsoluteConstraints(20, 280, 70, -1));

        coefficient12TextField.setText("0");
        zernikeCoefficientPanel.add(coefficient12TextField, new AbsoluteConstraints(90, 280, 60, -1));

        coefficient13Label.setFont(new Font("Arial", 1, 12));
        coefficient13Label.setText("2nd Astig y");
        zernikeCoefficientPanel.add(coefficient13Label, new AbsoluteConstraints(180, 280, 70, -1));

        coefficient13TextField.setText("0");
        zernikeCoefficientPanel.add(coefficient13TextField, new AbsoluteConstraints(250, 280, 60, -1));

        coefficient14Label.setFont(new Font("Arial", 1, 12));
        coefficient14Label.setText("2nd Coma x");
        zernikeCoefficientPanel.add(coefficient14Label, new AbsoluteConstraints(20, 310, 70, -1));

        coefficient14TextField.setText("0");
        zernikeCoefficientPanel.add(coefficient14TextField, new AbsoluteConstraints(90, 310, 60, -1));

        coefficient15Label.setFont(new Font("Arial", 1, 12));
        coefficient15Label.setText("2nd Coma y");
        zernikeCoefficientPanel.add(coefficient15Label, new AbsoluteConstraints(180, 310, 70, -1));

        coefficient15TextField.setText("0");
        zernikeCoefficientPanel.add(coefficient15TextField, new AbsoluteConstraints(250, 310, 60, -1));

        coefficient16TextField.setText("0");
        zernikeCoefficientPanel.add(coefficient16TextField, new AbsoluteConstraints(110, 340, 40, -1));

        coefficient18Label.setFont(new Font("Arial", 1, 12));
        coefficient18Label.setText("2nd Spherical");
        zernikeCoefficientPanel.add(coefficient18Label, new AbsoluteConstraints(20, 340, 80, -1));

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
        sph = coefficient9TextField.getText();
        secsph = coefficient16TextField.getText();
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

        SA = Double.valueOf(coefficient9TextField.getText());
        S2A = Double.valueOf(coefficient16TextField.getText());

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
        tf1 = coefficient1TextField.getText();
        tf2 = coefficient2TextField.getText();
        tf3 = coefficient3TextField.getText();
        tf4 = coefficient4TextField.getText();
        tf5 = coefficient5TextField.getText();
        tf6 = coefficient6TextFields.getText();
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

        dcoef[0] = Double.valueOf(tf1);
        dcoef[1] = Double.valueOf(tf2);
        dcoef[2] = Double.valueOf(tf3);
        dcoef[3] = Double.valueOf(tf4);
        dcoef[4] = Double.valueOf(tf5);
        dcoef[5] = Double.valueOf(tf6);
        dcoef[6] = Double.valueOf(tf7);
        dcoef[7] = Double.valueOf(tf8);
        dcoef[8] = Double.valueOf(tf9);
        dcoef[9] = Double.valueOf(tf10);
        dcoef[10] = Double.valueOf(tf11);
        dcoef[11] = Double.valueOf(tf12);
        dcoef[12] = Double.valueOf(tf13);
        dcoef[13] = Double.valueOf(tf14);
        dcoef[14] = Double.valueOf(tf15);
        dcoef[15] = Double.valueOf(tf16);


        //generate wavefront data by zernike polynomials parameters
        samples[0] = fzernike(dcoef);

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
     * @param dCoef The Zernike coefficient vector.
     * @return The output Zernike image matrix.
     */
    public double[] fzernike(double[] dCoef) {
        int x, y;
        double realx, realy, Radius;
        double divX, divY, XSquPlusYSqu, divXSqu, divYSqu, XPYSquSqu;
        double term1, term2, term3, term4, term5, term6, term7, term8, term9, term10, term11, term12, term13, term14, term15, term16, total;
        double term17, term18, term19, term20, term21, term22, term23, term24;
        double Piston, XTilt, YTilt, Power, AstigOne, AstigTwo, ComaX, ComaY;
        double PrimarySpherical, TrefoilX, TrefoilY, SecondaryAstigX, SecondaryAstigY, SecondaryComaX, SecondaryComaY, SecondarySpherical;
        double TetrafoilX, TetrafoilY, SecTrefoilX, SecTrefoilY, TertiaryAstigX, TertiaryAstigY, TertiaryComaX, TertiaryComaY;
        int defocusbins, stigxbins, stigybins, comaxbins, comaybins, speribins;

        int SLMSIZE, ActSize, start, end;

        SLMSIZE = WIDTH;
        Piston = dCoef[0];
        XTilt = dCoef[1];
        YTilt = dCoef[2];
        Power = dCoef[3];
        AstigOne = dCoef[4];
        AstigTwo = dCoef[5];
        ComaX = dCoef[6];
        ComaY = dCoef[7];
        PrimarySpherical = dCoef[8];
        TrefoilX = dCoef[9];
        TrefoilY = dCoef[10];
        SecondaryAstigX = dCoef[11];
        SecondaryAstigY = dCoef[12];
        SecondaryComaX = dCoef[13];
        SecondaryComaY = dCoef[14];
        SecondarySpherical = dCoef[15];
        /*
        TetrafoilX = dCoef[16]; TetrafoilY = dCoef[17]; SecTrefoilX = dCoef[18]; SecTrefoilY = dCoef[19];
        TertiaryAstigX = dCoef[20]; TertiaryAstigY = dCoef[21]; TertiaryComaX = dCoef[22]; TertiaryComaY = dCoef[23];
         */

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
        //Radius = ActSize*300/512;
        //Radius = ActSize * 256 / 512;
        //Radius = ActSize * 175 / 512;
        // GH: Selected radius 175 (diam: 350) to make field match scan mirrors
        // (entrance pupil).
        Radius = ActSize * 220 / 512; // radius 220 for S-H.

        System.out.println("Radius is " + Radius);

        // if the set slm size is not 512, take the center of the phase modulator
        start = (SLMSIZE - ActSize) / 2; // GH: 0
        end = start + ActSize; // GH: 512 (OK, not included).

        y = ActSize / 2; // 256
        realy = y; // 256
        
        System.out.println("Start is " + start+ " end is " + end);
        

        // set the surface by polynomia parameters, pixel by pixel
        // GH: row=0; row < 512; row++        
        for (int row = 0; row < 512; row++) {
            //reset x
            //x = (ActSize / 2) * -1;  // GH: -256
            //realx = x;   // GH: -256
            x = (row - 256);
            realx = x;

            // GH: col=0; col < 512; col++
            for (int col = 0; col < 512; col++) {
                //build some terms that are repeated through the equations
                y = (col - 256);
                realy = y;
                
                divX = realx / Radius; // -256/300 = -0.8533
                divY = realy / Radius; // +256/300 = -0.8533
                XSquPlusYSqu = divX * divX + divY * divY;
                XPYSquSqu = XSquPlusYSqu * XSquPlusYSqu;
                divXSqu = divX * divX;
                divYSqu = divY * divY;
                
                /*
                if (x == 0) {
                    System.out.println("("+x+","+y+") divX: " 
                         + divX + " divY: " + divY + " rho^2: " + XSquPlusYSqu);
                }*/

                //JOptionPane.showMessageDialog(this, "rho = " + Math.sqrt(divXSqu + divYSqu), "slm2", JOptionPane.INFORMATION_MESSAGE);

                if ((squareCheckBox.isSelected() || (divXSqu + divYSqu) <= 1) &&
                        (!cutCenterCheckBox.isSelected() ||
                        ((divXSqu + divYSqu) >= 0.10))) {
                    
                    double scalingFactor = 1.00;
                    
                    /* Only defined on the "unit" circle. */
                    divX = realx / Radius * scalingFactor; // -256/300 = -0.8533
                    divY = realy / Radius * scalingFactor; // +256/300 = -0.8533
                    XSquPlusYSqu = divX * divX + divY * divY;
                    XPYSquSqu = XSquPlusYSqu * XSquPlusYSqu;
                    divXSqu = divX * divX;
                    divYSqu = divY * divY;

                    //figure out what each term in the equation is
                    /*
                     *XXX/NOTE:
                     *Min Ren had all coefficients like: Coeff/2, instead
                     *of the like sqrt(5) in front of sph. aberration.
                     *(conventional definition).
                     *XXX/NOTE: Changed to conventional definitions.
                     */
                    term1 = (Piston);
                    term2 = 2*(XTilt)*divX;
                    
                    /*if (XTilt > 5) {
                        term2 = 2*(5)*divX;
                    }*/
                    
                    term3 = 2*(YTilt)*divY;
                    //term4 = Math.sqrt(3)*(Power)*(2*XSquPlusYSqu - 1);
                    //Removed Piston
                    term4 = Math.sqrt(3)*(Power)*(2*XSquPlusYSqu);
                    term5 = Math.sqrt(6)*(AstigOne)*(divXSqu - divY*divY);
                    term6 = Math.sqrt(6)*(AstigTwo)*(2*divX*divY);
                    term7 = Math.sqrt(8)*(ComaX)*(3*divX*XSquPlusYSqu - 2*divX);
                    term8 = Math.sqrt(8)*(ComaY)*(3*divY*XSquPlusYSqu - 2*divY);
                    //term9 = Math.sqrt(5)*(PrimarySpherical)*(1 - 6*XSquPlusYSqu + 6*XPYSquSqu);
                    // Removed piston:
                    term9 = Math.sqrt(5)*(PrimarySpherical)*(- 6*XSquPlusYSqu + 6*XPYSquSqu);
                    term10 = Math.sqrt(8)*(TrefoilX)*(divXSqu*divX - 3*divX*divYSqu);
                    term11 = Math.sqrt(8)*(TrefoilY)*(3*divXSqu*divY - divYSqu*divY);
                    term12 = Math.sqrt(10)*(SecondaryAstigX)*(3*divYSqu - 3*divXSqu + 4*divXSqu*XSquPlusYSqu - 4*divYSqu*XSquPlusYSqu);
                    term13 = Math.sqrt(10)*(SecondaryAstigY)*(8*divX*divY*XSquPlusYSqu - 6*divX*divY);
                    term14 = Math.sqrt(12)*(SecondaryComaX)*(3*divX - 12*divX*XSquPlusYSqu + 10*divX*XPYSquSqu);
                    term15 = Math.sqrt(12)*(SecondaryComaY)*(3*divY - 12*divY*XSquPlusYSqu + 10*divY*XPYSquSqu);
                    term16 = Math.sqrt(7)*(SecondarySpherical)*(12*XSquPlusYSqu - 1 - 30*XPYSquSqu + 20*XSquPlusYSqu*XPYSquSqu);
                     
                    /*
                    term1 = (Piston / 2);
                    term2 = (XTilt / 2) * divX;
                    term3 = (YTilt / 2) * divY;
                    term4 = (Power / 2) * (2 * XSquPlusYSqu - 1);
                    term5 = (AstigOne / 2) * (divXSqu - divY * divY);
                    term6 = (AstigTwo / 2) * (2 * divX * divY);
                    term7 = (ComaX / 2) * (3 * divX * XSquPlusYSqu - 2 * divX);
                    term8 = (ComaY / 2) * (3 * divY * XSquPlusYSqu - 2 * divY);
                    term9 = (PrimarySpherical / 2) * (1 - 6 * XSquPlusYSqu + 6 * XPYSquSqu);
                    term10 = (TrefoilX / 2) * (divXSqu * divX - 3 * divX * divYSqu);
                    term11 = (TrefoilY / 2) * (3 * divXSqu * divY - divYSqu * divY);
                    term12 = (SecondaryAstigX / 2) * (3 * divYSqu - 3 * divXSqu + 4 * divXSqu * XSquPlusYSqu - 4 * divYSqu * XSquPlusYSqu);
                    term13 = (SecondaryAstigY / 2) * (8 * divX * divY * XSquPlusYSqu - 6 * divX * divY);
                    term14 = (SecondaryComaX / 2) * (3 * divX - 12 * divX * XSquPlusYSqu + 10 * divX * XPYSquSqu);
                    term15 = (SecondaryComaY / 2) * (3 * divY - 12 * divY * XSquPlusYSqu + 10 * divY * XPYSquSqu);
                    term16 = (SecondarySpherical / 2) * (12 * XSquPlusYSqu - 1 - 30 * XPYSquSqu + 20 * XSquPlusYSqu * XPYSquSqu);
                     * */
                    /*
                     * Note: these (commented) coefficients need to be fixed (if to be used).
                    term17 = (TetrafoilX/2)*(divXSqu*divXSqu - 6*divXSqu*divYSqu + divYSqu*divYSqu);
                    term18 = (TetrafoilY/2)*(4*divXSqu*divX*divY - 4*divX*divY*divYSqu);
                    term19 = (SecTrefoilX/2)*(divXSqu*divX - 3*divX*divYSqu)*(-4 + 5*XSquPlusYSqu);
                    term20 = (SecTrefoilY/2)*(3*divXSqu*divY - divYSqu*divY)*(-4 + 5*XSquPlusYSqu);
                    term21 = (TertiaryAstigX/2)*(6*divXSqu - 6*divYSqu + 20*XSquPlusYSqu*divYSqu - 20*XSquPlusYSqu*divXSqu + 15*XPYSquSqu*divXSqu - 15*XPYSquSqu*divYSqu);
                    term22 = (TertiaryAstigY/2)*(12*divX*divY - 40*XSquPlusYSqu*divX*divY + 30*XPYSquSqu*divX*divY);
                    term23 = (TertiaryComaX/2)*(-4*divX + 30*divX*XSquPlusYSqu - 60*divX*XPYSquSqu + 35*divX*XSquPlusYSqu*XPYSquSqu);
                    term24 = (TertiaryComaY/2)*(-4*divY + 30*divY*XSquPlusYSqu - 60*divY*XPYSquSqu + 35*divY*XSquPlusYSqu*XPYSquSqu);
                     */
                    total = term1 + term2 + term3 + term10 + term11 + term12 + term13 + term14 + term15 + term16;
                    total += term4 + term5 + term6 + term7 + term8 + term9;
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
            x = (row - 256);
            realx = x;

            // GH: col=0; col < 512; col++
            for (int col = 0; col < 512; col++) {
                y = (col - 256);
                realy = y;
                
                divX = realx / Radius;
                divY = realy / Radius;
                divXSqu = divX * divX;
                divYSqu = divY * divY;
                
                if ((squareCheckBox.isSelected() || (divXSqu + divYSqu) <= 1) &&
                        (!cutCenterCheckBox.isSelected() ||
                        ((divXSqu + divYSqu) >= 0.10))) {
                    
                    int i = row * SLMSIZE + col;
                    if (isFirst || zern[i] < smallestVal) {
                        smallestVal = zern[i];
                        isFirst = false;
                    }
                }
            }
        }

        
        //Subtract it everywhere.
        for (int row = 0; row < 512; row++) {
            x = (row - 256);
            realx = x;

            // GH: col=0; col < 512; col++
            for (int col = 0; col < 512; col++) {
                y = (col - 256);
                realy = y;
                
                divX = realx / Radius;
                divY = realy / Radius;
                divXSqu = divX * divX;
                divYSqu = divY * divY;
                
                if ((squareCheckBox.isSelected() || (divXSqu + divYSqu) <= 1) &&
                        (!cutCenterCheckBox.isSelected() ||
                        ((divXSqu + divYSqu) >= 0.10))) {
                    
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
        String tf17, tf18, tf19, tf20, tf21, tf22, tf23, tf24, tfout;
        //get the parameters
        tf1 = coefficient1TextField.getText();
        tf2 = coefficient2TextField.getText();
        tf3 = coefficient3TextField.getText();
        tf4 = coefficient4TextField.getText();
        tf5 = coefficient5TextField.getText();
        tf6 = coefficient6TextFields.getText();
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

        tfout = tf1 + ", " + tf2 + ", " + tf3 + ", " + tf4 + ", " + tf5 + ", " + tf6 + ", " + tf7 + ", " + tf8;
        tfout = tfout + ", " + tf9 + ", " + tf10 + ", " + tf11 + ", " + tf12 + ", " + tf13 + ", " + tf14 + ", " + tf15 + ", " + tf16;

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
        String tf1, tf2, tf3, tf4, tf5, tf6, tf7, tf8, tf9, tf10, tf11, tf12, tf13, tf14, tf15, tf16;
        String tf17, tf18, tf19, tf20, tf21, tf22, tf23, tf24;
        tf1 = coefficient1TextField.getText();
        tf2 = coefficient2TextField.getText();
        tf3 = coefficient3TextField.getText();
        tf4 = coefficient4TextField.getText();
        tf5 = coefficient5TextField.getText();
        tf6 = coefficient6TextFields.getText();
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

        dcoef[0] = Double.valueOf(tf1);
        dcoef[1] = Double.valueOf(tf2);
        dcoef[2] = Double.valueOf(tf3);
        dcoef[3] = Double.valueOf(tf4);
        dcoef[4] = Double.valueOf(tf5);
        dcoef[5] = Double.valueOf(tf6);
        dcoef[6] = Double.valueOf(tf7);
        dcoef[7] = Double.valueOf(tf8);
        dcoef[8] = Double.valueOf(tf9);
        dcoef[9] = Double.valueOf(tf10);
        dcoef[10] = Double.valueOf(tf11);
        dcoef[11] = Double.valueOf(tf12);
        dcoef[12] = Double.valueOf(tf13);
        dcoef[13] = Double.valueOf(tf14);
        dcoef[14] = Double.valueOf(tf15);
        dcoef[15] = Double.valueOf(tf16);


        //////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////
        samples[0] = fzernike(dcoef);

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
        coefficient6TextFields.setText("0");
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
        coefficient6TextFields.setText(strin_arr[5]);
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
    JLabel coefficient18Label;
    JTextField coefficient1TextField;
    JTextField coefficient2TextField;
    JTextField coefficient3TextField;
    JTextField coefficient4TextField;
    JTextField coefficient5TextField;
    JTextField coefficient6TextFields;
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
