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
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;
import loci.ao.slm.characterization.diffraction.main.RegionModes.RegionMode;

import loci.hardware.camera.CCDCamera;


/**
 * The Main class handles controlling the flow of control and setting up
 * the experiment as well as displaying the user interface.
 */
public class Main extends JFrame implements Observer, WindowListener {
    /**
     * Singleton instance of the class.
     */
    private static Main instance = null;

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
    private GratingImagePanel phaseImagePanel;
   
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

    // Image sequence.
    private JTextField imageSeqFolderEdit;
    private JComboBox imageSeqComboBox;
    private JButton imageApplyButton;
    private JButton imageSeqBrowseButton;
    private JButton imageSeqLoadButton;
    private JButton imageSeqPrevButton;
    private JButton imageSeqNextButton;
    private JFileChooser imageSeqDirChooser;

    // CCD save as.
    private JTextField saveImageEdit;
    private JButton saveImageBrowseButton;
    private JButton saveImageButton;
    private JFileChooser saveImageFileChooser;

    // LUT

    /**
     * A button that triggers the folder chooser dialog to appear.
     */
    private JButton browseButton;

    /**
     * Textfield used to show/modify the lut folder path.
     */
    private JTextField lutPathEdit;

    /**
     * Checkbox to enable/disable use of LUTs.
     */
    private JCheckBox lutUsageCheckBox;

    /**
     * A button that triggers the folder chooser dialog to appear.
     */
    private JButton lutBrowseButton;

    /**
     * A button that triggers the loading of the LUT location folder.
     */
    private JButton lutLoadButton;

    /**
     * FileChooser widget used to select output file location.
     */
    private JFileChooser lutFileChooser;

    /**
     * LUT status label.
     */
    private JLabel lutStatusLabel;

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
     * Grating Series - Output path text field.
     */
    private JTextField gratingSeriesOutPathEdit;

    /**
     * Grating Series - Output file browse button.
     */
    private JButton gratingSeriesBrowseButton;

    /**
     * For running grating series.
     */
    private GratingSerieRunner gratingSerieRunner;

    /**
     * Image Sequence Series - Output path text field.
     */
    private JTextField isSeriesOutPathEdit;

    /**
     * Image Sequence Series - Output file browse button.
     */
    private JButton isSeriesBrowseButton;

    /**
     * For running image sequence series.
     */
    private ImageSequenceSerieRunner isSerieRunner;

    /**
     * Number of averages for the CCD camera.
     */
    private JTextField ccdAveragesField;

    /**
     * For the series: variable settings.
     */
    private JTextField varValueFromField;
    private JTextField varValueToField;
    private JTextField varValueStepField;

    /**
     * For the series: region settings.
     */
    private JTextField regionFromField;
    private JTextField regionToField;

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
    private JLabel roiSatPixelsLabel;
    private JLabel roiIntensityLabel;

    /**
     * Region modes.
     */
    private JTextField rmRegionEdit;
    private JTextField modeRegionEdit;
    private JComboBox rmModeComboBox;
    private JTextField modeBiasEdit;
    private JTextField modeTiltXEdit;
    private JTextField modeTiltYEdit;
    private JTextField rmSeriesBiasFromEdit;
    private JTextField rmSeriesBiasToEdit;
    private JTextField rmSeriesBiasStepSizeEdit;
    private JTextField rmSeriesRegionFromEdit;
    private JTextField rmSeriesRegionToEdit;
    private JTextField rmSeriesRegionStepSizeEdit;
    private JTextField rmSeriesOutputFolderEdit;
    private JButton rmSeriesBrowseButton;
    private JFileChooser rmFileChooser;
    private JButton rmSeriesRunButton;
    private JComboBox rmSeriesRmModeComboBox;

    /**
     * For running region mode series.
     */
    private RegionModeSerieRunner rmSerieRunner;



    /**
     * Constructs the Main class.  Sets up and displays the GUI.
     */
    private Main() {        
        initComponents();        
    }

    /**
     * Get the singleton instance of the class.
     */
    public static Main getInstance()
    {
        if (instance == null) {
            instance = new Main();
        }
        return instance;
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

        // Listen for close operations.
        addWindowListener(this);

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
        String leftRowSpec = "p, 7dlu, p"; //3

        CellConstraints cc = new CellConstraints();
        FormLayout leftLayout = new FormLayout(leftColSpec, leftRowSpec);
        PanelBuilder leftBuilder = new PanelBuilder(leftLayout);

        /* Calibration Pattern Panel. */
        String calibImageColSpecs = "650dlu";
        String calibImageRowSpecs = "p, 4dlu, 200dlu";
        FormLayout calibImageLayout =
                new FormLayout(calibImageColSpecs, calibImageRowSpecs);
        PanelBuilder calibImageBuilder = new PanelBuilder(calibImageLayout);

        // Prepare contents.
        phaseImagePanel = new GratingImagePanel();

        // Arrange contents.
        int row = 1;
        calibImageBuilder.addSeparator("SLM Phase Pattern",      cc.xy(1, row));
        row += 2;
        calibImageBuilder.add(phaseImagePanel,                     cc.xy(1, row));

        leftBuilder.add(calibImageBuilder.getPanel(),              cc.xy(1, 1));

        /* CCD Image Panel. */
        String ccdColSpecs = "650dlu";
        String ccdRowSpecs = "p, 4dlu, 350dlu";
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
     * Get the phase image panel.
     */
    public synchronized GratingImagePanel getPhaseImagePanel()
    {
        return phaseImagePanel;
    }

    /**
     * Get the ccd camera panel.
     */
    public synchronized ImagePanel getCameraImagePanel()
    {
        return ccdImagePanel;
    }

    /**
     * The update method listens for events from Observable objects.
     * Currently that is the CCD Image Panel.
     *
     * @param o The observable object that registered the update.
     * @param arg Input arguments, currently the actual class.
     */
    public synchronized void update(Observable o, Object arg) {
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
    public synchronized void updateStatus()
    {
        // Determine and set the status of the current ROI image.
        GratingExperimentStatus.getInstance().setRoiIntensity(
                ccdImagePanel.getROIIntensity());
        GratingExperimentStatus.getInstance().setRoiSaturatedPixelCount(
                ccdImagePanel.getROISaturatedPixelCount());

        // Update the status labels.
        regionLabel.setText(
                "" + GratingExperimentStatus.getInstance().getRegion());
        curValueLabel.setText(
                "" + GratingExperimentStatus.getInstance().getVarValue());
        refValueLabel.setText(
                "" + GratingExperimentStatus.getInstance().getRefValue());
        roiIntensityLabel.setText(
                "" + GratingExperimentStatus.getInstance().getRoiIntensity());
        roiSatPixelsLabel.setText(
                "" + GratingExperimentStatus.getInstance().getRoiSaturatedPixelCount());
    }

    /**
     * Builds and returns the right panel.
     *
     * @return The right content panel.
     */
    private JPanel buildRightPanel() {
        String rightColSpec = "fill:p"; // 1
        String rightRowSpec = "p, 8dlu, p, 8dlu, p, 8dlu, p, 8dlu, p, 8dlu"; // 10

        CellConstraints cc = new CellConstraints();
        FormLayout rightLayout = new FormLayout(rightColSpec, rightRowSpec);
        PanelBuilder rightBuilder = new PanelBuilder(rightLayout);

        int mainRow=1;
        int row=1;

        /* 1. LUT panel */
        String lutColSpecs = "p, 4dlu, 120dlu, 4dlu, p, 4dlu:grow"; // 6
        String lutRowSpecs = "p, 4dlu, p, 4dlu, p, 4dlu, p, 4dlu, p, 4dlu:grow"; // 10
        FormLayout lutLayout = new FormLayout(lutColSpecs, lutRowSpecs);
        PanelBuilder lutBuilder = new PanelBuilder(lutLayout);

        // Prepare contents.
        lutFileChooser = new JFileChooser();
        lutStatusLabel = new JLabel("");
        lutPathEdit = new JTextField("");
        lutUsageCheckBox = new JCheckBox("Enable use of LUTs");
        lutUsageCheckBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (lutUsageCheckBox.isSelected()) {
                    System.out.println("isSelected");
                    LookupTable.getInstance().setEnabled(true);
                } else {
                    System.out.println("is nicht selected");
                    LookupTable.getInstance().setEnabled(false);
                }
                updateLutStatus();
            }
        });
        lutBrowseButton = new JButton("Browse");
        lutBrowseButton.addActionListener(new ActionListener() {
            public synchronized void actionPerformed(ActionEvent e) {
                lutFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int retVal = lutFileChooser.showOpenDialog(browseButton);
                if (retVal == JFileChooser.APPROVE_OPTION) {
                    File chosenFile = lutFileChooser.getSelectedFile();
                    lutPathEdit.setText(chosenFile.getPath());
                }
            }
        });
        lutLoadButton = new JButton("Load LUT Folder");
        lutLoadButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.println("Loading the lut folder");
                LookupTable.getInstance().loadFolder(lutPathEdit.getText());
                updateLutStatus();
            }
        });
        updateLutStatus();

        // Arrange contents.
        row = 1;
        lutBuilder.addSeparator("Look-up tables (LUTs)",  cc.xyw(1, row, 6));
        row += 2;
        lutBuilder.add(lutUsageCheckBox,                  cc.xyw(1, row, 6));
        row += 2;
        lutBuilder.addLabel("Folder:",                    cc.xy(1, row));
        lutBuilder.add(lutPathEdit,                       cc.xy(3, row));
        lutBuilder.add(lutBrowseButton,                   cc.xy(5, row));
        row += 2;
        lutBuilder.addLabel("Status:",                    cc.xy(1, row));
        lutBuilder.add(lutStatusLabel,                    cc.xy(3, row));
        row += 2;
        lutBuilder.add(lutLoadButton,                     cc.xy(1, row));

        rightBuilder.add(lutBuilder.getPanel(),           cc.xy(1, mainRow));
        mainRow += 2;


        /* 2. Image sequence panel. */
        JTabbedPane tPane = new JTabbedPane();
        tPane.add("Image sequence", buildImageSequencePanel());
        tPane.add("Ronchi grating", buildRonchiGratingPanel());
        tPane.add("Region modes", buildRegionModePanel());

        rightBuilder.add(tPane,                           cc.xy(1, mainRow));
        mainRow += 2;


        /* 3. CCD Camera Panel. */
        String roiColSpecs = "p, 4dlu, p, 4dlu, 20dlu, 4dlu, p, 4dlu, 20dlu, 60dlu, p, 4dlu, p, 4dlu, p, 4dlu, p, 4dlu:grow"; // 16
        String roiRowSpecs = "p, 4dlu, p, 4dlu, p, 4dlu, p, 4dlu, p, 4dlu, p"; // 11
        FormLayout roiLayout = new FormLayout(roiColSpecs, roiRowSpecs);
        PanelBuilder roiBuilder = new PanelBuilder(roiLayout);

        // Prepare contents.
        upperLeftX = new JTextField();
        upperLeftY = new JTextField();
        lowerRightX = new JTextField();
        lowerRightY = new JTextField();
        ccdAveragesField = new JTextField("1");
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

        saveImageEdit = new JTextField();
        saveImageBrowseButton = new JButton("Browse");
        saveImageButton = new JButton("Save image");
        saveImageFileChooser = new JFileChooser();

        // Action handlers.
        saveImageBrowseButton.addActionListener(new ActionListener() {
            public synchronized void actionPerformed(ActionEvent e) {
                saveImageFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                int retVal = saveImageFileChooser.showOpenDialog(saveImageBrowseButton);
                if (retVal == JFileChooser.APPROVE_OPTION) {
                    File chosenFile = saveImageFileChooser.getSelectedFile();
                    saveImageEdit.setText(chosenFile.getPath());
                }
            }
        });

        saveImageButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Save the image in the CCD panel to file.
                BufferedImage image = ccdImagePanel.getImage();
                File outputFile = new File(saveImageEdit.getText());
                String formatName = "bmp";

                try {
                    ImageIO.write(image, formatName, outputFile);
                } catch (IOException ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });


        // Arrange items.
        row = 1;
        roiBuilder.addSeparator("CCD Camera",               cc.xyw(1, row, 18));
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
        roiBuilder.addLabel("# Averages:",                  cc.xy(1, row));
        roiBuilder.add(ccdAveragesField,                    cc.xy(5, row));

        row += 2;
        roiBuilder.add(ccdCaptureButton,                    cc.xy(1, row));
        roiBuilder.add(ROIApplyButton,                      cc.xyw(3, row, 7));

        row += 2;
        roiBuilder.addLabel("Save as",                      cc.xy(1, row));
        roiBuilder.add(saveImageEdit,                       cc.xyw(3, row, 8));
        roiBuilder.add(saveImageBrowseButton,               cc.xy(15, row));
        roiBuilder.add(saveImageButton,                     cc.xy(17, row));

        rightBuilder.add(roiBuilder.getPanel(),             cc.xy(1, mainRow));
        mainRow += 2;


        /* 4. CCD zoom panel. */
        String ccdZoomColSpecs = "50dlu"; // 1
        String ccdZoomRowSpecs = "50dlu"; // 1
        FormLayout ccdZoomLayout = new FormLayout(ccdZoomColSpecs, ccdZoomRowSpecs);
        PanelBuilder ccdZoomBuilder = new PanelBuilder(ccdZoomLayout);

        // Prepare contents.
        ccdZoomRegion = new ImagePanel(false);

        // Arrange contents.
        row = 1;
        ccdZoomBuilder.add(ccdZoomRegion,                     cc.xy(1, row));

        rightBuilder.add(ccdZoomBuilder.getPanel(),           cc.xy(1, mainRow));
        mainRow += 2;

        /* 5. Status panel. */
        String statColSpecs = "p, 4dlu, p, 4dlu:grow"; // 4
        String statRowSpecs = "p, 4dlu, p, 4dlu, p, 4dlu, p, 4dlu, p, 4dlu, p, 4dlu, p, 4dlu:grow"; // 13
        FormLayout statLayout = new FormLayout(statColSpecs, statRowSpecs);
        PanelBuilder statBuilder = new PanelBuilder(statLayout);

        // Prepare contents.
        regionLabel = new JLabel("");
        curValueLabel = new JLabel("");
        refValueLabel = new JLabel("");
        roiSatPixelsLabel = new JLabel("");
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
        statBuilder.addLabel("ROI intensity:",             cc.xy(1, row));
        statBuilder.add(roiIntensityLabel,                 cc.xy(3, row));
        row += 2;
        statBuilder.addLabel("ROI saturated pixels:",      cc.xy(1, row));
        statBuilder.add(roiSatPixelsLabel,                 cc.xy(3, row));

        rightBuilder.add(statBuilder.getPanel(),           cc.xy(1, mainRow));
        mainRow += 2;

        return rightBuilder.getPanel();
    }

    /**
     * Build the image sequence tab panel.
     */
    private JPanel buildImageSequencePanel() {
        String isOuterColSpec = "fill:p"; // 1
        String isOuterRowSpec = "p, 8dlu, p, 8dlu"; // 4

        CellConstraints cc = new CellConstraints();
        FormLayout isOuterLayout = new FormLayout(isOuterColSpec, isOuterRowSpec);
        PanelBuilder isOuterBuilder = new PanelBuilder(isOuterLayout);

        int outerRow=1;
        int row=1;

        /* IS/1. Image sequence. */
        // Image sequence folder:
        // Load Folder
        // [combobox:Current image] <prev> <next>
        // [Apply]
        String isColSpecs = "p, 4dlu, 120dlu, 4dlu, p, 4dlu, p, 4dlu:grow"; // 8
        String isRowSpecs = "p, 4dlu, p, 4dlu, p, 4dlu, p, 4dlu"; // 8
        FormLayout isLayout = new FormLayout(isColSpecs, isRowSpecs);
        PanelBuilder isBuilder = new PanelBuilder(isLayout);

        // Prepare contents.
        imageSeqFolderEdit = new JTextField();
        imageSeqComboBox = new JComboBox();
        imageApplyButton = new JButton("Apply image");
        imageSeqBrowseButton = new JButton("Browse");
        imageSeqLoadButton = new JButton("Load");
        imageSeqPrevButton = new JButton("<");
        imageSeqNextButton = new JButton(">");
        imageSeqDirChooser = new JFileChooser();

        // Action handlers.
        imageSeqBrowseButton.addActionListener(new ActionListener() {
            public synchronized void actionPerformed(ActionEvent e) {
                imageSeqDirChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int retVal = imageSeqDirChooser.showOpenDialog(imageSeqBrowseButton);
                if (retVal == JFileChooser.APPROVE_OPTION) {
                    File chosenFile = imageSeqDirChooser.getSelectedFile();
                    imageSeqFolderEdit.setText(chosenFile.getPath());
                }
            }
        });

        imageSeqLoadButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ImageSequence.getInstance().loadFolder(imageSeqFolderEdit.getText());
                ArrayList<String> imageList = ImageSequence.getInstance().getFileList();
                imageSeqComboBox.removeAllItems();
                for (int i = 0; i < imageList.size(); i++) {
                    imageSeqComboBox.addItem(imageList.get(i));
                }
            }
        });

        imageSeqPrevButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int itemIdx = imageSeqComboBox.getSelectedIndex();
                if (itemIdx > 0) {
                    imageSeqComboBox.setSelectedIndex(itemIdx-1);
                }
            }
        });

        imageSeqNextButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int itemIdx = imageSeqComboBox.getSelectedIndex();
                if (itemIdx < (imageSeqComboBox.getItemCount()-1)) {
                    imageSeqComboBox.setSelectedIndex(itemIdx+1);
                }
            }
        });

        imageApplyButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int itemIdx = imageSeqComboBox.getSelectedIndex();
                BufferedImage image = ImageSequence.getInstance().getImageByIndex(itemIdx);
                double[] dataMatrix = ImageUtils.imageToDataMatrix(image);
                phaseImagePanel.setDataMatrix(dataMatrix);

                if (Constants.USE_SLM_DEVICE) {
                    System.out.println("Sending to SLM device");
                    com.slmcontrol.slmAPI.slmjava(dataMatrix, (char)0);
                }
            }
        });

        // Arrange items.
        row = 1;
        isBuilder.addSeparator("Image sequence",           cc.xyw(1, row, 8));

        row += 2;
        isBuilder.addLabel("Image sequence folder",        cc.xy(1, row));
        isBuilder.add(imageSeqFolderEdit,                  cc.xy(3, row));
        isBuilder.add(imageSeqBrowseButton,                cc.xy(5, row));
        isBuilder.add(imageSeqLoadButton,                  cc.xy(7, row));

        row += 2;
        isBuilder.addLabel("Image:",                       cc.xy(1, row));
        isBuilder.add(imageSeqComboBox,                    cc.xy(3, row));
        isBuilder.add(imageSeqPrevButton,                  cc.xy(5, row));
        isBuilder.add(imageSeqNextButton,                  cc.xy(7, row));

        row += 2;
        isBuilder.add(imageApplyButton,                    cc.xy(1, row));
        
        isOuterBuilder.add(isBuilder.getPanel(),     cc.xy(1, outerRow));
        outerRow += 2;


        /* IS/2. Series Output Panel. */
        String opColSpecs = "p, 4dlu, p, 80dlu, 4dlu, p, 4dlu"; // 7
        String opRowSpecs = "p, 4dlu, p, 4dlu, p, 4dlu"; // 6
        FormLayout opLayout = new FormLayout(opColSpecs, opRowSpecs);
        PanelBuilder outSeriesBuilder = new PanelBuilder(opLayout);

        // Prepare items.
        JButton isSeriesRunButton = new JButton("Run Series");
        isSeriesRunButton.addActionListener(new ActionListener() {
            public synchronized void actionPerformed(ActionEvent e) {
                System.out.println("IS Run Series button pressed");

                // Sets the parameters for the series and starts the job
                // by initiating the thread.
                isSerieRunner = ImageSequenceSerieRunner.getInstance();
                isSerieRunner.start();
                isSerieRunner.setParams(isSeriesOutPathEdit.getText());
                isSerieRunner.run();
            }
        });
        JButton isSeriesCancelButton = new JButton("Cancel");
        isSeriesCancelButton.addActionListener(new ActionListener() {
            public synchronized void actionPerformed(ActionEvent e) {
                System.out.println("Cancelling");
                // Stops the image sequence series.
                isSerieRunner.stop();
            }
        });

        isSeriesOutPathEdit = new JTextField("");
        isSeriesBrowseButton = new JButton("Browse");
        isSeriesBrowseButton.addActionListener(new ActionListener() {
            public synchronized void actionPerformed(ActionEvent e) {
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int retVal = fileChooser.showOpenDialog(isSeriesBrowseButton);
                if (retVal == JFileChooser.APPROVE_OPTION) {
                    File chosenFile = fileChooser.getSelectedFile();
                    isSeriesOutPathEdit.setText(chosenFile.getPath());
                }
            }
        });

        // Arrange items.
        row = 1;
        outSeriesBuilder.addSeparator("Output - Series",    cc.xyw(1, row, 7));
        row += 2;
        outSeriesBuilder.addLabel("Output Folder:",         cc.xy(1, row));
        outSeriesBuilder.add(isSeriesOutPathEdit,           cc.xyw(3, row, 2));
        outSeriesBuilder.add(isSeriesBrowseButton,          cc.xy(6, row));
        row += 2;
        outSeriesBuilder.add(isSeriesRunButton,             cc.xy(1, row));
        outSeriesBuilder.add(isSeriesCancelButton,          cc.xy(3, row));

        isOuterBuilder.add(outSeriesBuilder.getPanel(),     cc.xy(1, outerRow));
        outerRow += 2;

        return isOuterBuilder.getPanel();
    }

    /**
     * Build the ronchi grating panel.
     */
    private JPanel buildRonchiGratingPanel() {
        String gratingColSpec = "fill:p"; // 1
        String gratingRowSpec = "p, 8dlu, p, 8dlu, p, 8dlu"; // 6

        CellConstraints cc = new CellConstraints();
        FormLayout gratingLayout = new FormLayout(gratingColSpec, gratingRowSpec);
        PanelBuilder gratingBuilder = new PanelBuilder(gratingLayout);

        int outerRow=1;

        /* 1. Grating Settings Panel. */
        String calibColSpecs = "p, 4dlu, 30dlu, 8dlu, p, 4dlu, 30dlu, 4dlu:grow"; // 8
        String calibRowSpecs = "p, 4dlu, p, 4dlu, p, 4dlu, p, 4dlu, p"; // 9
        FormLayout calibLayout = new FormLayout(calibColSpecs, calibRowSpecs);
        PanelBuilder calibBuilder = new PanelBuilder(calibLayout);

        // Prepare contents.
        gratingsEdit = new JTextField("256");
        regionsEdit = new JTextField("16");
        regionEdit = new JTextField("0");
        refValueEdit = new JTextField("255");
        varValueEdit = new JTextField("0");

        JButton calApplyButton = new JButton("Apply");
        calApplyButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateSLM();
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

        gratingBuilder.add(calibBuilder.getPanel(),       cc.xy(1, outerRow));
        outerRow += 2;

        /* 2. Manual Output Panel. */
        String outpColSpecs = "p, 4dlu, 120dlu, 4dlu, p, 4dlu:grow"; // 6
        String outpRowSpecs = "p, 4dlu, p, 4dlu, p"; // 5
        FormLayout outpLayout = new FormLayout(outpColSpecs, outpRowSpecs);
        PanelBuilder outpBuilder = new PanelBuilder(outpLayout);

        // Prepare contents.
        fileChooser = new JFileChooser();
        outPathEdit = new JTextField("");
        browseButton = new JButton("Browse");
        browseButton.addActionListener(new ActionListener() {
            public synchronized void actionPerformed(ActionEvent e) {
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
            public synchronized void actionPerformed(ActionEvent e) {
                updateSLM();

                try {
                    Thread.sleep(600);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                }
                runCamera();
                updateStatus();
            }
        });
        JButton manualRecordButton = new JButton("Record");
        manualRecordButton.addActionListener(new ActionListener() {
            public synchronized void actionPerformed(ActionEvent e) {
                // Record the current status to file.
                // recordData();

                try {
                    Integer numGratings = new Integer(gratingsEdit.getText());
                    Integer refVal = new Integer(refValueEdit.getText());
                    Integer region = new Integer(regionEdit.getText());
                    Integer regions = new Integer(regionsEdit.getText());
                    Integer varVal = new Integer(varValueEdit.getText());
                    Integer roiULCornerX = new Integer(upperLeftX.getText());
                    Integer roiULCornerY = new Integer(upperLeftY.getText());
                    Integer roiLRCornerX = new Integer(lowerRightX.getText());
                    Integer roiLRCornerY = new Integer(lowerRightY.getText());

                    Double roiInt = ccdImagePanel.getROIIntensity();
                    if (outpWriter == null) {
                        outpWriter = new OutputWriter(outPathEdit.getText());
                        outpWriter.writeHeader(
                                numGratings, regions, refVal,
                                roiULCornerX, roiULCornerY, roiLRCornerX, roiLRCornerY);
                    } else {
                        if (!outpWriter.getFilePath().equals(outPathEdit.getText())) {
                            outpWriter = new OutputWriter(outPathEdit.getText());
                            outpWriter.writeHeader(numGratings, regions, refVal,
                                    roiULCornerX, roiULCornerY, roiLRCornerX, roiLRCornerY);
                        }
                    }

                    outpWriter.recordData(region, refVal, varVal, roiInt);
                } catch (NumberFormatException nfe) {
                    System.out.println("Number format error - nothing recorded");
                } catch (NullPointerException npe) {
                    System.out.println("Null pointer excp. - nothing recorded");
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

        gratingBuilder.add(outpBuilder.getPanel(),        cc.xy(1, outerRow));
        outerRow += 2;


        /* 3. Series Output Panel. */
        String opColSpecs = "p, 4dlu, p, 4dlu, 20dlu, 4dlu, p, 4dlu, 20dlu, 4dlu, p, 4dlu, 20dlu, 4dlu:grow"; // 14
        String opRowSpecs = "p, 4dlu, p, 4dlu, p, 4dlu, p, 4dlu, p"; // 9
        FormLayout opLayout = new FormLayout(opColSpecs, opRowSpecs);
        PanelBuilder outSeriesBuilder = new PanelBuilder(opLayout);

        // Prepare items.
        JButton seriesRunButton = new JButton("Run Series");
        seriesRunButton.addActionListener(new ActionListener() {
            public synchronized void actionPerformed(ActionEvent e) {
                System.out.println("Run Series button pressed");
                gratingSerieRunner = GratingSerieRunner.getInstance();
                gratingSerieRunner.start();
                Integer numGratings = new Integer(gratingsEdit.getText());
                Integer refVal = new Integer(refValueEdit.getText());
                Integer region = new Integer(regionEdit.getText());
                Integer regions = new Integer(regionsEdit.getText());

                Integer regionFrom = new Integer(regionFromField.getText());
                Integer regionTo = new Integer(regionToField.getText());

                Integer varFrom = new Integer(varValueFromField.getText());
                Integer varTo = new Integer(varValueToField.getText());
                Integer varStepSize = new Integer(varValueStepField.getText());

                Integer roiULCornerX = new Integer(upperLeftX.getText());
                Integer roiULCornerY = new Integer(upperLeftY.getText());
                Integer roiLRCornerX = new Integer(lowerRightX.getText());
                Integer roiLRCornerY = new Integer(lowerRightY.getText());

                gratingSerieRunner.setGratingVars(numGratings, refVal, regions);
                gratingSerieRunner.setRange(regionFrom, regionTo,
                        varFrom, varTo, varStepSize);
                gratingSerieRunner.setOutputFileName(gratingSeriesOutPathEdit.getText());
                gratingSerieRunner.setROIInformation(roiULCornerX, roiULCornerY,
                        roiLRCornerX, roiLRCornerY);
                gratingSerieRunner.run();
                //runCamera();
                //updateStatus();
            }
        });
        JButton seriesCancelButton = new JButton("Cancel");
        seriesCancelButton.addActionListener(new ActionListener() {
            public synchronized void actionPerformed(ActionEvent e) {
                System.out.println("Cancelling");
                gratingSerieRunner.stop();
            }
        });

        gratingSeriesOutPathEdit = new JTextField("");
        gratingSeriesBrowseButton = new JButton("Browse");
        gratingSeriesBrowseButton.addActionListener(new ActionListener() {
            public synchronized void actionPerformed(ActionEvent e) {
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                int retVal = fileChooser.showOpenDialog(gratingSeriesBrowseButton);
                if (retVal == JFileChooser.APPROVE_OPTION) {
                    File chosenFile = fileChooser.getSelectedFile();
                    gratingSeriesOutPathEdit.setText(chosenFile.getPath());
                }
            }
        });
        varValueFromField = new JTextField("0");
        varValueToField = new JTextField("255");
        varValueStepField = new JTextField("1");

        regionFromField = new JTextField("0");
        regionToField = new JTextField("15");

        // Arrange items.
        row = 1;
        outSeriesBuilder.addSeparator("Output - Series",  cc.xyw(1, row, 14));
        row += 2;
        outSeriesBuilder.addLabel("Output File:",         cc.xy(1, row));
        outSeriesBuilder.add(gratingSeriesOutPathEdit,           cc.xyw(3, row, 9));
        outSeriesBuilder.add(gratingSeriesBrowseButton,          cc.xyw(13, row, 2));
        row += 2;
        outSeriesBuilder.addLabel("Region",               cc.xy(1, row));
        outSeriesBuilder.addLabel("From:",                cc.xy(3, row));
        outSeriesBuilder.add(regionFromField,             cc.xy(5, row));
        outSeriesBuilder.addLabel("To:",                  cc.xy(7, row));
        outSeriesBuilder.add(regionToField,               cc.xy(9, row));
        row += 2;
        outSeriesBuilder.addLabel("Var. value",           cc.xy(1, row));
        outSeriesBuilder.addLabel("From:",                cc.xy(3, row));
        outSeriesBuilder.add(varValueFromField,           cc.xy(5, row));
        outSeriesBuilder.addLabel("To:",                  cc.xy(7, row));
        outSeriesBuilder.add(varValueToField,             cc.xy(9, row));
        outSeriesBuilder.addLabel("Step size:",           cc.xy(11, row));
        outSeriesBuilder.add(varValueStepField,           cc.xy(13, row));
        row += 2;
        outSeriesBuilder.add(seriesRunButton,             cc.xyw(1, row, 5));
        outSeriesBuilder.add(seriesCancelButton,          cc.xyw(7, row, 5));

        gratingBuilder.add(outSeriesBuilder.getPanel(),     cc.xy(1, outerRow));
        outerRow += 2;

        return gratingBuilder.getPanel();
    }

    /**
     * Build the region mode tab panel.
     */
    private JPanel buildRegionModePanel() {
        /*String rmOuterColSpec = "fill:p"; // 1
        String rmOuterRowSpec = "p, 8dlu, p, 8dlu"; // 4

        CellConstraints cc = new CellConstraints();
        FormLayout rmOuterLayout = new FormLayout(rmOuterColSpec, rmOuterRowSpec);
        PanelBuilder rmOuterBuilder = new PanelBuilder(rmOuterLayout);

        int outerRow=1;*/
        CellConstraints cc = new CellConstraints();
        int row=1;

        /* IS/1. Image sequence. */
        // Image sequence folder:
        // Load Folder
        // [combobox:Current image] <prev> <next>
        // [Apply]
        String rmColSpecs = "p, 4dlu, 50dlu, 4dlu, p, 4dlu, 50dlu, 4dlu, p, 4dlu, 50dlu, 4dlu:grow"; // 12
        String rmRowSpecs = "p, 4dlu, p, 4dlu, p, 4dlu, p, 4dlu, p, 4dlu, p,"
                          + "4dlu, p, 4dlu, p, 4dlu, p, 4dlu, p, 4dlu, p, 4dlu,"
                          + "p, 4dlu, p, 4dlu, p, 4dlu, p, 4dlu, p, 4dlu"; // 32
        FormLayout rmLayout = new FormLayout(rmColSpecs, rmRowSpecs);
        PanelBuilder rmBuilder = new PanelBuilder(rmLayout);

        // Prepare contents.
        rmRegionEdit = new JTextField();
        modeRegionEdit = new JTextField();
        rmModeComboBox = new JComboBox();
        modeBiasEdit = new JTextField();
        modeTiltXEdit = new JTextField();
        modeTiltYEdit = new JTextField();

        JButton rmRegionApplyButton = new JButton("Apply");
        rmRegionApplyButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Integer numRegions = new Integer(rmRegionEdit.getText());
                RegionModes.getInstance().setNumberOfRegions(numRegions.intValue());
                updateModeSettings();
            }
        });

        JButton rmAddModeButton = new JButton("Add mode");
        rmAddModeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                RegionModes.getInstance().addMode();
                updateModeSettings();
            }
        });

        JButton rmDelModeButton = new JButton("Remove mode");
        rmDelModeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int modeIdx = rmModeComboBox.getSelectedIndex();
                if (modeIdx >= 0) {
                    RegionModes.getInstance().removeModeByIndex(modeIdx);
                }
                updateModeSettings();
            }
        });

        rmModeComboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateModeSettings();
            }
        });

        JButton modeApplyButton = new JButton("Apply");
        modeApplyButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int modeIdx = rmModeComboBox.getSelectedIndex();
                if (modeIdx >= 0) {
                    RegionMode mode =
                            RegionModes.getInstance().getModeByIndex(modeIdx);
                    Integer mRegion = new Integer(modeRegionEdit.getText());
                    Double mBias = new Double(modeBiasEdit.getText());
                    Double mTiltX = new Double(modeTiltXEdit.getText());
                    Double mTiltY = new Double(modeTiltYEdit.getText());
                    mode.setRegion(mRegion.intValue());
                    mode.setBias(mBias.doubleValue());
                    mode.setTiltX(mTiltX.doubleValue());
                    mode.setTiltY(mTiltY.doubleValue());
                }
                updateModeSettings();
            }
        });
        JButton rmSendSLMButton = new JButton("Send to SLM");
        rmSendSLMButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                double[] dataMatrix = RegionModes.getInstance().generateDataMatrix();
                phaseImagePanel.setDataMatrix(dataMatrix);

                if (Constants.USE_SLM_DEVICE) {
                    System.out.println("Sending to SLM device");
                    com.slmcontrol.slmAPI.slmjava(dataMatrix, (char)0);
                }
            }
        });

        rmSeriesBiasFromEdit = new JTextField("0");
        rmSeriesBiasToEdit = new JTextField("255");
        rmSeriesBiasStepSizeEdit = new JTextField("5");
        rmSeriesRegionFromEdit = new JTextField("0");
        rmSeriesRegionToEdit = new JTextField("0");
        rmSeriesRegionStepSizeEdit = new JTextField("1");
        rmSeriesOutputFolderEdit = new JTextField();
        rmSeriesBrowseButton = new JButton("Browse");
        rmFileChooser = new JFileChooser();
        rmSeriesRmModeComboBox = new JComboBox();

        rmSeriesBrowseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                rmFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int retVal = rmFileChooser.showOpenDialog(rmSeriesBrowseButton);
                if (retVal == JFileChooser.APPROVE_OPTION) {
                    File chosenFile = rmFileChooser.getSelectedFile();
                    rmSeriesOutputFolderEdit.setText(chosenFile.getPath());
                }
            }
        });
        rmSeriesRunButton = new JButton("Run Series");
        rmSeriesRunButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.println("Running series");

                // Sets the parameters for the series and starts the job
                // by initiating the thread.
                rmSerieRunner = RegionModeSerieRunner.getInstance();
                rmSerieRunner.start();
                int modeIndex = rmSeriesRmModeComboBox.getSelectedIndex();
                Double biasFromVal = new Double(rmSeriesBiasFromEdit.getText());
                Double biasToVal = new Double(rmSeriesBiasToEdit.getText());
                Double biasStepSizeVal = new Double(rmSeriesBiasStepSizeEdit.getText());
                Integer regionFromVal = new Integer(rmSeriesRegionFromEdit.getText());
                Integer regionToVal = new Integer(rmSeriesRegionToEdit.getText());
                Integer regionStepSizeVal = new Integer(rmSeriesRegionStepSizeEdit.getText());

                rmSerieRunner.setParams(
                        modeIndex, rmSeriesOutputFolderEdit.getText(),
                        biasFromVal.doubleValue(), biasToVal.doubleValue(),
                        biasStepSizeVal.doubleValue(),
                        regionFromVal.intValue(), regionToVal.intValue(),
                        regionStepSizeVal.intValue());
                rmSerieRunner.run();
            }
        });
        JButton rmSeriesCancelButton = new JButton("Cancel");
        rmSeriesCancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (rmSerieRunner != null) {
                    rmSerieRunner.stop();
                    rmSerieRunner = null;
                }
            }
        });


        // Arrange contents.
        rmBuilder.addSeparator("Regions Definition",            cc.xyw(1, row, 12));
        row += 2;

        rmBuilder.addLabel("#Regions:",                         cc.xy(1, row));
        rmBuilder.add(rmRegionEdit,                             cc.xy(3, row));
        rmBuilder.add(rmRegionApplyButton,                      cc.xy(5, row));
        row += 2;

        rmBuilder.addSeparator("Modes",                         cc.xyw(1, row, 12));
        row += 2;

        rmBuilder.addLabel("Mode "    ,                         cc.xy(1, row));
        rmBuilder.add(rmModeComboBox,                           cc.xy(3, row));
        rmBuilder.add(rmAddModeButton,                          cc.xy(5, row));
        row += 2;

        rmBuilder.addLabel("Mode region:",                      cc.xy(1, row));
        rmBuilder.add(modeRegionEdit,                           cc.xy(3, row));
        rmBuilder.add(rmDelModeButton,                          cc.xy(5, row));
        row += 2;

        rmBuilder.addLabel("1. Bias:",                          cc.xy(1, row));
        rmBuilder.add(modeBiasEdit,                             cc.xy(3, row));
        row += 2;

        rmBuilder.addLabel("2. Tilt-X:",                        cc.xy(1, row));
        rmBuilder.add(modeTiltXEdit,                            cc.xy(3, row));
        row += 2;

        rmBuilder.addLabel("3. Tilt-Y:",                        cc.xy(1, row));
        rmBuilder.add(modeTiltYEdit,                            cc.xy(3, row));
        row += 2;

        rmBuilder.add(modeApplyButton,                          cc.xy(1, row));
        rmBuilder.add(rmSendSLMButton,                          cc.xy(3, row));
        row += 2;

        rmBuilder.addSeparator("Mode Series",                   cc.xyw(1, row, 12));
        row += 2;

        rmBuilder.addLabel("Mode to scan:",                     cc.xy(1, row));
        rmBuilder.add(rmSeriesRmModeComboBox,                   cc.xy(3, row));
        row += 2;

        rmBuilder.addLabel("Region From:",                      cc.xy(1, row));
        rmBuilder.add(rmSeriesRegionFromEdit,                   cc.xy(3, row));
        rmBuilder.addLabel("To:",                               cc.xy(5, row));
        rmBuilder.add(rmSeriesRegionToEdit,                     cc.xy(7, row));
        rmBuilder.addLabel("Step size:",                        cc.xy(9, row));
        rmBuilder.add(rmSeriesRegionStepSizeEdit,               cc.xy(11, row));
        row += 2;

        rmBuilder.addLabel("Bias From:",                        cc.xy(1, row));
        rmBuilder.add(rmSeriesBiasFromEdit,                     cc.xy(3, row));
        rmBuilder.addLabel("To:",                               cc.xy(5, row));
        rmBuilder.add(rmSeriesBiasToEdit,                       cc.xy(7, row));
        rmBuilder.addLabel("Step size:",                        cc.xy(9, row));
        rmBuilder.add(rmSeriesBiasStepSizeEdit,                 cc.xy(11, row));
        row += 2;



        rmBuilder.addLabel("Output Folder:",                    cc.xy(1, row));
        rmBuilder.add(rmSeriesOutputFolderEdit,                 cc.xyw(3, row, 7));
        rmBuilder.add(rmSeriesBrowseButton,                     cc.xy(11, row));
        row += 2;

        rmBuilder.add(rmSeriesRunButton,                        cc.xy(1, row));
        row += 2;


        return rmBuilder.getPanel();
    }


    /**
     *
     */
    private void updateModeSettings()
    {
        int modeCount = RegionModes.getInstance().countModes();
        if (modeCount == 0) {
            modeRegionEdit.setText("");
            modeBiasEdit.setText("");
            modeTiltXEdit.setText("");
            modeTiltYEdit.setText("");
            rmModeComboBox.removeAllItems();
            rmModeComboBox.setSelectedIndex(-1);
            return;
        }

        // Update combobox.
        int selModeIdx = rmModeComboBox.getSelectedIndex();
        rmModeComboBox.removeAllItems();
        for (int i = 0; i < modeCount; i++) {
            rmModeComboBox.addItem("Mode " + i);
        }

        if (selModeIdx >= modeCount || selModeIdx < 0) {
            selModeIdx = 0;
        }
        rmModeComboBox.setSelectedIndex(selModeIdx);

        // Update series combobox.

        int seriesSelModeIdx = rmSeriesRmModeComboBox.getSelectedIndex();
        rmSeriesRmModeComboBox.removeAllItems();
        for (int i = 0; i < modeCount; i++) {
            rmSeriesRmModeComboBox.addItem("Mode " + i);
        }

        if (selModeIdx >= modeCount || selModeIdx < 0) {
            selModeIdx = 0;
        }
        rmSeriesRmModeComboBox.setSelectedIndex(seriesSelModeIdx);


        // Setup information for selected item.
        if (modeCount > 0 && selModeIdx >= 0) {
            RegionMode selMode =
                    RegionModes.getInstance().getModeByIndex(selModeIdx);
            modeRegionEdit.setText("" + selMode.getRegion());
            modeBiasEdit.setText("" + selMode.getBias());
            modeTiltXEdit.setText("" + selMode.getTiltX());
            modeTiltYEdit.setText("" + selMode.getTiltY());
        }
    }

    /**
     * Updates the status of the LUT panel.
     */
    private void updateLutStatus()
    {
        String statusMsg = "";

        if (LookupTable.getInstance().isEnabled()) {
            statusMsg += "enabled";
        } else {
            statusMsg += "disabled";
        }

        lutStatusLabel.setText(statusMsg);
    }

    /**
     * Updates the SLM device, sets the correct parameters and sends the
     * corresponding grating image to the device.
     */
    private synchronized void updateSLM() {
        phaseImagePanel.setGratingParams(
                new Integer(gratingsEdit.getText()),
                new Integer(refValueEdit.getText()),
                new Integer(varValueEdit.getText()),
                new Integer(regionEdit.getText()),
                new Integer(regionsEdit.getText()));

        if (Constants.USE_SLM_DEVICE) {
            double[] dataMatrix = phaseImagePanel.getDataMatrix();
            com.slmcontrol.slmAPI.slmjava(dataMatrix, (char)0);
        }

        // Update experiment status.
        Integer refVal = new Integer(refValueEdit.getText());
        Integer region = new Integer(regionEdit.getText());
        Integer varVal = new Integer(varValueEdit.getText());
        GratingExperimentStatus.getInstance().setRefValue(refVal);
        GratingExperimentStatus.getInstance().setRegion(region);
        GratingExperimentStatus.getInstance().setVarValue(varVal);
    }

    /**
     * Runs the camera (for test purposes).
     */
    public synchronized void runCamera() {
        if (!Constants.USE_CCD) {
            return;
        }

        CCDCamera ccdCamera = new CCDCamera();

        if (Constants.DEBUG) {
            System.out.println("testMe(): " + ccdCamera.testMe());
        }

        if (ccdCamera.initialize()) {
            if (Constants.DEBUG) {
                System.out.println("Successfully initialized the CCD Camera");
            }
        } else {
            System.out.println("Failed to initialize the CCD Camera");
        }

        if (Constants.DEBUG) {
            System.out.println("Note: " + ccdCamera.getNote());
        }

        Integer imageAverages = new Integer(ccdAveragesField.getText());

        //long durStart = System.currentTimeMillis();
        int frameLen = ccdCamera.captureFrame(imageAverages);
        //long durEnd = System.currentTimeMillis();
        //long duration = durEnd - durStart;
        //System.out.println("cpature frame duration: " + duration);

        if (frameLen < 0) {
            System.out.println("Err: " + ccdCamera.getNote());
        } else {
            BufferedImage camImage = ccdCamera.getImage();
            if (camImage != null) {
                ccdImagePanel.setImage(camImage);
            }
            
            BufferedImage roiImage = ccdImagePanel.getROIImage();
            if (roiImage != null) {
                ccdZoomRegion.setImage(ccdImagePanel.getROIImage());
            }
        }
        if (Constants.DEBUG) {
            System.out.println("frameLength: " + frameLen);
        }
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

        Main.getInstance().setVisible(true);
    }    

   /**
     * Called upon window closing, send the power off command to the SLM.
     *
     * @param e Window event object.
     */
    public void windowClosing(WindowEvent e) {
        System.out.println("window closing!");
        if (Constants.USE_SLM_DEVICE) {
            System.out.println("- turning off");
            // Power off the system.
            double[] dataMatrix = phaseImagePanel.getDataMatrix();
            // XXX/FIXME data should not be necessary to turn it off!
            com.slmcontrol.slmAPI.slmjava(dataMatrix, (char) 65);
        }
    }

    /*
     * Unused window operations.
     */
    public void windowOpened(WindowEvent e) {}
    public void windowClosed(WindowEvent e) {}
    public void windowIconified(WindowEvent e) {}
    public void windowDeiconified(WindowEvent e) {}
    public void windowActivated(WindowEvent e) {}
    public void windowDeactivated(WindowEvent e) { }
}
