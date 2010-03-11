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
    private ImagePanel calibImagePanel;
   
    /**
     * Used to isplay an image from a CCD camera.  Also used in showing and
     * specifying the ROI for the CCD.
     */
    private ImagePanel ccdImagePanel;

    /**
     * FileChooser widget used to select output folder location.
     */
    private JFileChooser folderChooser;

    /**
     * Textfield used to show/modify the output folder path.
     */
    private JTextField outPathEdit;

    /**
     * A button that triggers the folder chooser dialog to appear.
     */
    private JButton browseButton;

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
        String calibImageColSpecs = "600dlu";
        String calibImageRowSpecs = "p, 4dlu, 260dlu";
        FormLayout calibImageLayout =
                new FormLayout(calibImageColSpecs, calibImageRowSpecs);
        PanelBuilder calibImageBuilder = new PanelBuilder(calibImageLayout);

        // Prepare contents.
        calibImagePanel = new ImagePanel(false);

        // Arrange contents.
        int row = 1;
        calibImageBuilder.addSeparator("Calibration Pattern",      cc.xy(1, row));
        row += 2;
        calibImageBuilder.add(calibImagePanel,                     cc.xy(1, row));

        leftBuilder.add(calibImageBuilder.getPanel(),              cc.xy(1, 1));


        /* CCD Image Panel. */
        String ccdColSpecs = "600dlu";
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
     * Builds and returns the right panel.
     *
     * @return The right content panel.
     */
    private JPanel buildRightPanel() {
        String rightColSpec = "fill:p"; // 1
        String rightRowSpec   = "p, 7dlu, p, 7dlu, p, 7dlu, p, 7dlu, p"; // 9

        CellConstraints cc = new CellConstraints();
        FormLayout rightLayout = new FormLayout(rightColSpec, rightRowSpec);
        PanelBuilder rightBuilder = new PanelBuilder(rightLayout);

        /* ROI Panel. */
        String roiColSpecs = "p, 4dlu, p, 4dlu, 20dlu, 4dlu, p, 4dlu, 20dlu, 4dlu:grow"; // 10
        String roiRowSpecs = "p, 4dlu, p, 4dlu, p, 4dlu, p"; // 7
        FormLayout roiLayout = new FormLayout(roiColSpecs, roiRowSpecs);
        PanelBuilder roiBuilder = new PanelBuilder(roiLayout);

        // Prepare contents.
        upperLeftX = new JTextField();
        upperLeftY = new JTextField();
        lowerRightX = new JTextField();
        lowerRightY = new JTextField();
        JButton applyButton = new JButton("Apply");
        applyButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Integer x1 = new Integer(upperLeftX.getText());
                Integer y1 = new Integer(upperLeftY.getText());
                Integer x2 = new Integer(lowerRightX.getText());
                Integer y2 = new Integer(lowerRightY.getText());
                ccdImagePanel.setRectangle(x1, y1, x2, y2);
                ccdImagePanel.repaint();
            }
        });

        // Arrange items.
        int row = 1;
        roiBuilder.addSeparator("Define ROI",               cc.xyw(1, row, 10));
        row += 2;
        roiBuilder.addLabel("Upper left:",                  cc.xy(1, row));
        roiBuilder.addLabel("X:",                           cc.xy(3, row));
        roiBuilder.add(upperLeftX,                          cc.xy(5, row));
        roiBuilder.addLabel("Y:",                           cc.xy(7, row));
        roiBuilder.add(upperLeftY,                          cc.xy(9, row));

        row += 2;
        roiBuilder.addLabel("Lower right:",                  cc.xy(1, row));
        roiBuilder.addLabel("X:",                           cc.xy(3, row));
        roiBuilder.add(lowerRightX,                         cc.xy(5, row));
        roiBuilder.addLabel("Y:",                           cc.xy(7, row));
        roiBuilder.add(lowerRightY,                         cc.xy(9, row));

        row += 2;
        roiBuilder.add(applyButton,                         cc.xy(1, row));
        
        rightBuilder.add(roiBuilder.getPanel(),             cc.xy(1, 1));

        /* Calibration Settings Panel. */
        String calibColSpecs = "p, 4dlu, 30dlu, 4dlu:grow"; // 4
        String calibRowSpecs = "p, 4dlu, p, 4dlu, p, 4dlu, p"; // 7
        FormLayout calibLayout = new FormLayout(calibColSpecs, calibRowSpecs);
        PanelBuilder calibBuilder = new PanelBuilder(calibLayout);

        // Prepare contents.
        JTextField gratingsEdit = new JTextField("");
        JTextField regionsEdit = new JTextField("");
        JButton calSetButton = new JButton("Set");
        calSetButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.println("Set");
            }
        });

        // Arrange contents.
        row = 1;
        calibBuilder.addSeparator("Calibration Settings",   cc.xyw(1, row, 4));
        row += 2;
        calibBuilder.addLabel("# Gratings:",                cc.xy(1, row));
        calibBuilder.add(gratingsEdit,                      cc.xy(3, row));
        row += 2;
        calibBuilder.addLabel("# Regions:",                 cc.xy(1, row));
        calibBuilder.add(regionsEdit,                       cc.xy(3, row));
        row += 2;
        calibBuilder.add(calSetButton,                       cc.xy(1, row));

        rightBuilder.add(calibBuilder.getPanel(),           cc.xy(1, 3));

        /* Output Panel. */
        String outpColSpecs = "p, 4dlu, 120dlu, 4dlu, p, 4dlu:grow"; // 6
        String outpRowSpecs = "p, 4dlu, p"; // 3
        FormLayout outpLayout = new FormLayout(outpColSpecs, outpRowSpecs);
        PanelBuilder outpBuilder = new PanelBuilder(outpLayout);

        // Prepare contents.
        folderChooser = new JFileChooser();
        outPathEdit = new JTextField("");
        browseButton = new JButton("Browse");
        browseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                folderChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int retVal = folderChooser.showOpenDialog(browseButton);
                if (retVal == JFileChooser.APPROVE_OPTION) {
                    File chosenFile = folderChooser.getSelectedFile();
                    outPathEdit.setText(chosenFile.getPath());
                }
            }
        });

        // Arrange items.
        row = 1;
        outpBuilder.addSeparator("Output options",          cc.xyw(1, row, 6));
        row += 2;
        outpBuilder.addLabel("Output Folder:",              cc.xy(1, row));
        outpBuilder.add(outPathEdit,                        cc.xy(3, row));
        outpBuilder.add(browseButton,                       cc.xy(5, row));

        rightBuilder.add(outpBuilder.getPanel(),            cc.xy(1, 5));

        /* Measurement Operations Panel. */
        String opColSpecs = "p, 4dlu, p, 4dlu:grow"; // 4
        String opRowSpecs = "p, 4dlu, p"; // 3
        FormLayout opLayout = new FormLayout(opColSpecs, opRowSpecs);
        PanelBuilder opBuilder = new PanelBuilder(opLayout);

        // Prepare items.
        JButton startButton = new JButton("Start");
        startButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.println("Start button pressed");
                runCamera();
            }
        });
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.println("Cancel");
            }
        });

        // Arrange items.
        row = 1;
        opBuilder.addSeparator("Operations",                cc.xyw(1, row, 4));
        row += 2;
        opBuilder.add(startButton,                          cc.xy(1, row));
        opBuilder.add(cancelButton,                         cc.xy(3, row));

        rightBuilder.add(opBuilder.getPanel(),              cc.xy(1, 7));

        /* Status panel. */
        String statColSpecs = "p, 4dlu, p, 4dlu:grow"; // 4
        String statRowSpecs = "p, 4dlu, p, 4dlu, p, 4dlu, p, 4dlu, p, 4dlu, p, 4dlu"; // 12
        FormLayout statLayout = new FormLayout(statColSpecs, statRowSpecs);
        PanelBuilder statBuilder = new PanelBuilder(statLayout);

        // Prepare contents.
        JLabel regionLabel = new JLabel("");
        JLabel curValueLabel = new JLabel("");
        JLabel refValueLabel = new JLabel("");
        JLabel percentageLabel = new JLabel("");
        JLabel roiIntensityLabel = new JLabel("");

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

        rightBuilder.add(statBuilder.getPanel(),            cc.xy(1, 9));

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