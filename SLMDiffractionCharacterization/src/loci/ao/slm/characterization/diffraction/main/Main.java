//
// Main.java
//

/*
 * Diffraction calibration of a SLM device by imaging the SLM in the far-field
 * on a CCD Camera.  Measuring the intensity of the first diffraction order.
 * Copyright (C) 2010-@year@ Gunnsteinn Hall @LOCI Labs.
 * University of Wisconsin - Madison.
 */

package loci.ao.slm.characterization.diffraction.main;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;

import loci.hardware.camera.CCDCamera;


/**
 * The Main class handles controlling the flow of control and setting up
 * the experiment as well as displaying the user interface.
 */
public class Main extends JFrame implements Observer {
    /**
     * Textfield that specifies the x coordinate of the upper left corner of
     * the ROI.
     */
    private JTextField upperLeftX;

    /**
     * Textfield that specifies the y coordinate of the upper left corner of
     * the ROI.
     */
    private JTextField upperLeftY;

    /**
     * Textfield that specifies the x coordinate of the lower right corner of
     * the ROI.
     */
    private JTextField lowerRightX;

    /**
     * Textfield that specifies the y coordinate of the lower right corner of
     * the ROI.
     */
    private JTextField lowerRightY;

    /**
     * Used to display the current calibration pattern.
     */
    private GratingImagePanel calibImagePanel;
   
    /**
     * Used to isplay an image from a CCD camera.  Also used in showing and
     * specifying the ROI for the CCD.
     */
    private ImagePanel ccdImagePanel;

    /**
     * CCD zoom region.
     */
    private ImagePanel ccdZoomRegion;

    /**
     * FileChooser widget used to select output file location.
     */
    private JFileChooser fileChooser;

    /**
     * Textfield used to show/modify the output folder path.
     */
    private JTextField outPathEdit;

    /**
     * A button that triggers the folder chooser dialog to appear.
     */
    private JButton browseButton;

    /**
     * Specifies the number of gratings.
     */
    private JTextField gratingsEdit;

    /**
     * Specifies the numbers of regions.
     */
    private JTextField regionsEdit;

    /**
     * Specifies the currently selected region.
     */
    private JTextField regionEdit;

    /**
     * Specifies the 8-bit reference value.
     */
    private JTextField refValueEdit;

    /**
     * Specifies the 8-bit variable value.
     */
    private JTextField varValueEdit;

    /**
     * Series - Output path text field.
     */
    private JTextField seriesOutPathEdit;

    /**
     * Series - Output file browse button.
     */
    private JButton seriesBrowseButton;

    /**
     * Output writer handler.
     */
    private OutputWriter outpWriter = null;

    /**
     * Labels that indicate the current state of the application.
     */
    private JLabel regionLabel;
    private JLabel curValueLabel;
    private JLabel refValueLabel;
    private JLabel percentageLabel;
    private JLabel roiIntensityLabel;

    /**
     * Constants.
     */
    private final boolean USE_SLM_DEVICE = false;


    /**
     * Constructs the Main class.  Sets up and displays the GUI.
     */
    private Main() {        
        initComponents();        
    }

    /**
     * Builds and sets up the necessary panels.
     */
    private void initComponents() {
        // Master panel.
        JPanel masterPanel = (JPanel)getContentPane();
        CellConstraints cc = new CellConstraints();

        // 2 columns.
        String masterColSpec = "p:grow, 20dlu, fill:p"; //3
        String masterRowSpec = "fill:p:grow"; // 1

        FormLayout masterLayout = new FormLayout(masterColSpec, masterRowSpec);
        PanelBuilder masterBuilder = new PanelBuilder(masterLayout, masterPanel);

        masterBuilder.setDefaultDialogBorder();
        JPanel leftPanel = buildLeftPanel();
        JPanel rightPanel = buildRightPanel();
        masterBuilder.add(leftPanel,                    cc.xy(1, 1));
        masterBuilder.add(rightPanel,                   cc.xy(3, 1));

        /* Setup the frame. */
        pack();
        setLocationRelativeTo(null);
        setTitle("SLM LUT Diffraction Calibration");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    /**
     * Builds and returns the left panel.
     *
     * @return The left content panel.
     */
    private JPanel buildLeftPanel() {
        String leftColSpec = "p:grow"; // 1
        String leftRowSpec   = "p, 7dlu, p"; //3

        CellConstraints cc = new CellConstraints();
        FormLayout leftLayout = new FormLayout(leftColSpec, leftRowSpec);
        PanelBuilder leftBuilder = new PanelBuilder(leftLayout);

        /* Calibration Pattern Panel. */
        String calibImageColSpecs = "550dlu";
        String calibImageRowSpecs = "p, 4dlu, 260dlu";
        FormLayout calibImageLayout =
                new FormLayout(calibImageColSpecs, calibImageRowSpecs);
        PanelBuilder calibImageBuilder = new PanelBuilder(calibImageLayout);

        // Prepare contents.
        calibImagePanel = new GratingImagePanel();

        // Arrange contents.
        int row = 1;
        calibImageBuilder.addSeparator("Calibration Pattern",      cc.xy(1, row));
        row += 2;
        calibImageBuilder.add(calibImagePanel,                     cc.xy(1, row));

        leftBuilder.add(calibImageBuilder.getPanel(),              cc.xy(1, 1));

        /* CCD Image Panel. */
        String ccdColSpecs = "550dlu";
        String ccdRowSpecs = "p, 4dlu, 260dlu";
        FormLayout ccdLayout = new FormLayout(ccdColSpecs, ccdRowSpecs);
        PanelBuilder ccdBuilder = new PanelBuilder(ccdLayout);

        // Prepare contents.
        ccdImagePanel = new ImagePanel(true);
        ccdImagePanel.addObserver(this);

        // Arrange contents.
        row = 1;
        ccdBuilder.addSeparator("CCD Camera",               cc.xy(1, row));
        row += 2;
        ccdBuilder.add(ccdImagePanel,                       cc.xy(1, row));

        leftBuilder.add(ccdBuilder.getPanel(),              cc.xy(1, 3));
        
        return leftBuilder.getPanel();
    }

    /**
     * The update method listens for events from Observable objects.
     * Currently that is the CCD Image Panel.
     *
     * @param o The observable object that registered the update.
     * @param arg Input arguments, currently the actual class.
     */
    public void update(Observable o, Object arg) {
        if (arg == null)
            return;

        Rectangle ROI = ((ImagePanel)arg).getROI();

        upperLeftX.setText("" + ((int)ROI.getMinX()));
        upperLeftY.setText("" + ((int)ROI.getMinY()));
        lowerRightX.setText("" + ((int)ROI.getMaxX()));
        lowerRightY.setText("" + ((int)ROI.getMaxY()));
    }


    /**
     * Updates the status panel.
     */
    private void updateStatus()
    {
        roiIntensityLabel.setText("" + ccdImagePanel.getROIIntensity());
    }

    /**
     * Builds and returns the right panel.
     *
     * @return The right content panel.
     */
    private JPanel buildRightPanel() {
        String rightColSpec = "fill:p"; // 1
        String rightRowSpec   = "p, 8dlu, p, 8dlu, p, 8dlu, p, 8dlu, p, 8dlu, p"; // 11

        CellConstraints cc = new CellConstraints();
        FormLayout rightLayout = new FormLayout(rightColSpec, rightRowSpec);
        PanelBuilder rightBuilder = new PanelBuilder(rightLayout);


        /* 1. Grating Settings Panel. */
        String calibColSpecs = "p, 4dlu, 30dlu, 8dlu, p, 4dlu, 30dlu, 4dlu:grow"; // 8
        String calibRowSpecs = "p, 4dlu, p, 4dlu, p, 4dlu, p, 4dlu, p"; // 9
        FormLayout calibLayout = new FormLayout(calibColSpecs, calibRowSpecs);
        PanelBuilder calibBuilder = new PanelBuilder(calibLayout);

        // Prepare contents.
        gratingsEdit = new JTextField("100");
        regionsEdit = new JTextField("16");
        regionEdit = new JTextField("0");
        refValueEdit = new JTextField("0");
        varValueEdit = new JTextField("255");

        JButton calApplyButton = new JButton("Apply");
        calApplyButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                calibImagePanel.setParams(
                        new Integer(gratingsEdit.getText()),
                        new Integer(refValueEdit.getText()),
                        new Integer(varValueEdit.getText()),
                        new Integer(regionEdit.getText()),
                        new Integer(regionsEdit.getText()));

                if (USE_SLM_DEVICE) {
                    double[] dataMatrix = calibImagePanel.getDataMatrix();
                    com.slmcontrol.slmAPI.slmjava(dataMatrix, (char)0);
                }
            }
        });
        
        // Arrange contents.
        int row = 1;
        calibBuilder.addSeparator("Grating Settings",     cc.xyw(1, row, 8));
        row += 2;
        calibBuilder.addLabel("# Gratings:",              cc.xy(1, row));
        calibBuilder.add(gratingsEdit,                    cc.xy(3, row));
        calibBuilder.addLabel("Ref. val [0-255]:",        cc.xy(5, row));
        calibBuilder.add(refValueEdit,                    cc.xy(7, row));

        row += 2;
        calibBuilder.addLabel("# Regions:",               cc.xy(1, row));
        calibBuilder.add(regionsEdit,                     cc.xy(3, row));
        calibBuilder.addLabel("Var. val [0-255]:",        cc.xy(5, row));
        calibBuilder.add(varValueEdit,                    cc.xy(7, row));

        row += 2;
        calibBuilder.addLabel("Region:",                  cc.xy(1, row));
        calibBuilder.add(regionEdit,                      cc.xy(3, row));
        row += 2;
        calibBuilder.add(calApplyButton,                  cc.xy(1, row));

        rightBuilder.add(calibBuilder.getPanel(),         cc.xy(1, 1));


        /* 2. CCD Camera Panel. */
        String roiColSpecs = "p, 4dlu, p, 4dlu, 20dlu, 4dlu, p, 4dlu, 20dlu, 4dlu:grow"; // 10
        String roiRowSpecs = "p, 4dlu, p, 4dlu, p, 4dlu, p"; // 7
        FormLayout roiLayout = new FormLayout(roiColSpecs, roiRowSpecs);
        PanelBuilder roiBuilder = new PanelBuilder(roiLayout);

        // Prepare contents.
        upperLeftX = new JTextField();
        upperLeftY = new JTextField();
        lowerRightX = new JTextField();
        lowerRightY = new JTextField();
        JButton ROIApplyButton = new JButton("Set ROI");
        ROIApplyButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Integer x1 = new Integer(upperLeftX.getText());
                Integer y1 = new Integer(upperLeftY.getText());
                Integer x2 = new Integer(lowerRightX.getText());
                Integer y2 = new Integer(lowerRightY.getText());
                ccdImagePanel.setRectangle(x1, y1, x2, y2);
                ccdImagePanel.repaint();

                ccdZoomRegion.setImage(ccdImagePanel.getROIImage());
                updateStatus();
            }
        });
        JButton ccdCaptureButton = new JButton("Capture");
        ccdCaptureButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Update camera.
                runCamera();
                updateStatus();
            }
        });

        // Arrange items.
        row = 1;
        roiBuilder.addSeparator("CCD Camera",               cc.xyw(1, row, 10));
        row += 2;
        roiBuilder.addLabel("ROI - Upper left:",            cc.xy(1, row));
        roiBuilder.addLabel("X:",                           cc.xy(3, row));
        roiBuilder.add(upperLeftX,                          cc.xy(5, row));
        roiBuilder.addLabel("Y:",                           cc.xy(7, row));
        roiBuilder.add(upperLeftY,                          cc.xy(9, row));

        row += 2;
        roiBuilder.addLabel("ROI - Lower right:",           cc.xy(1, row));
        roiBuilder.addLabel("X:",                           cc.xy(3, row));
        roiBuilder.add(lowerRightX,                         cc.xy(5, row));
        roiBuilder.addLabel("Y:",                           cc.xy(7, row));
        roiBuilder.add(lowerRightY,                         cc.xy(9, row));

        row += 2;
        roiBuilder.add(ccdCaptureButton,                    cc.xy(1, row));
        roiBuilder.add(ROIApplyButton,                      cc.xyw(3, row, 7));
        
        rightBuilder.add(roiBuilder.getPanel(),             cc.xy(1, 3));


        /* 3. Status panel. */
        String statColSpecs = "p, 4dlu, p, 4dlu:grow"; // 4
        String statRowSpecs = "p, 4dlu, p, 4dlu, p, 4dlu, p, 4dlu, p, 4dlu, p, 4dlu, p, 4dlu:grow"; // 13
        FormLayout statLayout = new FormLayout(statColSpecs, statRowSpecs);
        PanelBuilder statBuilder = new PanelBuilder(statLayout);

        // Prepare contents.
        regionLabel = new JLabel("");
        curValueLabel = new JLabel("");
        refValueLabel = new JLabel("");
        percentageLabel = new JLabel("");
        roiIntensityLabel = new JLabel("");        

        // Arrange contents.
        row = 1;
        statBuilder.addSeparator("Status",                 cc.xyw(1, row, 4));
        row += 2;
        statBuilder.addLabel("Region:"    ,                cc.xy(1, row));
        statBuilder.add(regionLabel,                       cc.xy(3, row));
        row += 2;
        statBuilder.addLabel("Value:",                     cc.xy(1, row));
        statBuilder.add(curValueLabel,                     cc.xy(3, row));
        row += 2;
        statBuilder.addLabel("Reference:",                 cc.xy(1, row));
        statBuilder.add(refValueLabel,                     cc.xy(3, row));
        row += 2;
        statBuilder.addLabel("Percentage:",                cc.xy(1, row));
        statBuilder.add(percentageLabel,                   cc.xy(3, row));
        row += 2;
        statBuilder.addLabel("ROI intensity:",             cc.xy(1, row));
        statBuilder.add(roiIntensityLabel,                 cc.xy(3, row));

        rightBuilder.add(statBuilder.getPanel(),           cc.xy(1, 5));
        

        /* 4. Manual Output Panel. */
        String outpColSpecs = "p, 4dlu, 120dlu, 4dlu, p, 4dlu:grow"; // 6
        String outpRowSpecs = "p, 4dlu, p, 4dlu, p"; // 5
        FormLayout outpLayout = new FormLayout(outpColSpecs, outpRowSpecs);
        PanelBuilder outpBuilder = new PanelBuilder(outpLayout);

        // Prepare contents.
        fileChooser = new JFileChooser();
        outPathEdit = new JTextField("");
        browseButton = new JButton("Browse");
        browseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                int retVal = fileChooser.showOpenDialog(browseButton);
                if (retVal == JFileChooser.APPROVE_OPTION) {
                    File chosenFile = fileChooser.getSelectedFile();
                    outPathEdit.setText(chosenFile.getPath());
                }
            }
        });
        JButton manualUpdateButton = new JButton("Update");
        manualUpdateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                runCamera();
                updateStatus();
            }
        });
        JButton manualRecordButton = new JButton("Record");
        manualRecordButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Record the current status to file.
                // recordData();

                try {
                    Integer numGratings = new Integer(gratingsEdit.getText());
                    Integer refVal = new Integer(refValueEdit.getText());
                    Integer region = new Integer(regionEdit.getText());
                    Integer varVal = new Integer(varValueEdit.getText());
                    Double roiInt = new Double(roiIntensityLabel.getText());
                    if (outpWriter == null) {
                        outpWriter = new OutputWriter(outPathEdit.getText());
                        outpWriter.writeHeader(numGratings, refVal);
                    } else {
                        if (!outpWriter.getFilePath().equals(outPathEdit.getText())) {
                            outpWriter = new OutputWriter(outPathEdit.getText());
                            outpWriter.writeHeader(numGratings, refVal);
                        }
                    }

                    outpWriter.recordData(region, refVal, varVal, roiInt);
                } catch (NumberFormatException nfe) {
                    System.out.println("Number format error - nothing recorded");
                }
            }
        });

        // Arrange items.
        row = 1;
        outpBuilder.addSeparator("Output - Manual",       cc.xyw(1, row, 6));
        row += 2;
        outpBuilder.addLabel("Output File:",              cc.xy(1, row));
        outpBuilder.add(outPathEdit,                      cc.xy(3, row));
        outpBuilder.add(browseButton,                     cc.xy(5, row));
        row += 2;
        outpBuilder.add(manualUpdateButton,               cc.xy(1, row));
        outpBuilder.add(manualRecordButton,               cc.xy(3, row));

        rightBuilder.add(outpBuilder.getPanel(),          cc.xy(1, 7));


        /* 5. Series Output Panel. */
        String opColSpecs = "p, 4dlu, p, 4dlu, 20dlu, 4dlu, p, 4dlu, 20dlu, 4dlu, p, 4dlu, 20dlu, 4dlu:grow"; // 14
        String opRowSpecs = "p, 4dlu, p, 4dlu, p, 4dlu, p"; // 7
        FormLayout opLayout = new FormLayout(opColSpecs, opRowSpecs);
        PanelBuilder outSeriesBuilder = new PanelBuilder(opLayout);

        // Prepare items.
        JButton seriesRunButton = new JButton("Run Series");
        seriesRunButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.println("Start button pressed");
                runCamera();
                updateStatus();
            }
        });
        JButton seriesCancelButton = new JButton("Cancel");
        seriesCancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.println("Cancel");
            }
        });

        seriesOutPathEdit = new JTextField("");
        seriesBrowseButton = new JButton("Browse");
        seriesBrowseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                int retVal = fileChooser.showOpenDialog(seriesBrowseButton);
                if (retVal == JFileChooser.APPROVE_OPTION) {
                    File chosenFile = fileChooser.getSelectedFile();
                    seriesOutPathEdit.setText(chosenFile.getPath());
                }
            }        
        });
        JTextField varValueFromField = new JTextField("");
        JTextField varValueToField = new JTextField("");
        JTextField varValueStepField = new JTextField("");
        
        // Arrange items.
        row = 1;
        outSeriesBuilder.addSeparator("Output - Series",  cc.xyw(1, row, 14));
        row += 2;
        outSeriesBuilder.addLabel("Output File:",         cc.xy(1, row));
        outSeriesBuilder.add(seriesOutPathEdit,           cc.xyw(3, row, 9));
        outSeriesBuilder.add(seriesBrowseButton,          cc.xyw(13, row, 2));
        row += 2;
        outSeriesBuilder.addLabel("Var. value",           cc.xy(1, row));
        outSeriesBuilder.addLabel("From:",                cc.xy(3, row));
        outSeriesBuilder.add(varValueFromField,           cc.xy(5, row));
        outSeriesBuilder.addLabel("To:",                  cc.xy(7, row));
        outSeriesBuilder.add(varValueToField,             cc.xy(9, row));
        outSeriesBuilder.addLabel("Step size:",            cc.xy(11, row));
        outSeriesBuilder.add(varValueStepField,           cc.xy(13, row));

        row += 2;
        outSeriesBuilder.add(seriesRunButton,             cc.xyw(1, row, 5));
        outSeriesBuilder.add(seriesCancelButton,          cc.xyw(7, row, 5));

        rightBuilder.add(outSeriesBuilder.getPanel(),     cc.xy(1, 9));


        /* 6. CCD zoom panel. */
        String ccdZoomColSpecs = "200dlu"; // 1
        String ccdZoomRowSpecs = "200dlu"; // 1
        FormLayout ccdZoomLayout = new FormLayout(ccdZoomColSpecs, ccdZoomRowSpecs);
        PanelBuilder ccdZoomBuilder = new PanelBuilder(ccdZoomLayout);

        // Prepare contents.
        ccdZoomRegion = new ImagePanel(false);

        // Arrange contents.
        row = 1;
        ccdZoomBuilder.add(ccdZoomRegion,                     cc.xy(1, row));

        rightBuilder.add(ccdZoomBuilder.getPanel(),           cc.xy(1, 11));

        return rightBuilder.getPanel();
    }

    /**
     * Runs the camera (for test purposes).
     */
    private void runCamera() {
        CCDCamera ccdCamera = new CCDCamera();

        System.out.println("testMe(): " + ccdCamera.testMe());

        if (ccdCamera.initialize()) {
            System.out.println("Successfully initialized the CCD Camera");
        } else {
            System.out.println("Failed to initialize the CCD Camera");
        }

        System.out.println("Note: " + ccdCamera.getNote());
        
        int frameLen = ccdCamera.captureFrame();
        if (frameLen < 0) {
            System.out.println("Err: " + ccdCamera.getNote());
        } else {
            ccdImagePanel.setImage(ccdCamera.getImage());
        }
        System.out.println("frameLength: " + frameLen);
    }
    
    /**
     * Starts the application.
     *
     * @param args The command line arguments.
     */
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedLookAndFeelException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

        new Main().setVisible(true);
    }
}
