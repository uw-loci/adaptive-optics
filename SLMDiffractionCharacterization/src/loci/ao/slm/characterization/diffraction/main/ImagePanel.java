//
// ImagePanel.java
//

/*
 * Diffraction calibration of a SLM device by imaging the SLM in the far-field
 * on a CCD Camera.  Measuring the intensity of the first diffraction order.
 * Copyright (C) 2010-@year@ Gunnsteinn Hall @LOCI Labs.
 * University of Wisconsin - Madison.
 */

package loci.ao.slm.characterization.diffraction.main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

/**
 * The ImagePanel class defines a panel that can contain an image.
 */
class ImagePanel extends JPanel implements MouseListener {
    /**
     * The coordinates of the upper-left corner of the ROI.
     */
    private int x1, y1;

    /**
     * The coordinates of the lower-right corner of the ROI.
     */
    private int x2, y2;

    /**
     * Helps with the state of ROI definition - associated with mouse clicks.
     */
    private boolean isFirstPoint;

    /**
     * Contains the image to be displayed.
     */
    private BufferedImage img;

    /**
     * The maximum width of the Image Panel (in pixels).
     */
    protected final int maxWidth = 1000;

    /**
     * The maximum height of the Image Panel (in pixels).
     */
    protected final int maxHeight = 800;

    /**
     * A notifier object used to inform observers when a change has been
     * made, e.g. when a ROI has been defined.
     */
    private ImagePanelNotifier notifier;

    /**
     * Defines whether or not to use the ROI features.
     */
    private boolean enableROI;

    /**
     * Constructs the ImagePanel object.  Initializes and sets up the image.
     *
     * @param enableROI True if the ROI selection is to be used, false otherwise.
     */
    public ImagePanel(boolean enableROI) {
        this.enableROI = enableROI;
        if (enableROI) {
            addMouseListener(this);
            x1 = y1 = -1;
            x2 = y2 = -1;
            isFirstPoint = true;
            notifier = new ImagePanelNotifier();
        }

        // Load the image.
        try {
           img = ImageIO.read(new File("Standby.jpg"));
        } catch (IOException e) {
        }
        setImage(img);

        setPreferredSize(getPreferredSize());
    }

    /**
     * Constructs the ImagePanel object.  Initializes and sets up the image.
     * Disables the ROI.
     */
    public ImagePanel() {
        this(false);
    }

    /**
     * Set the new image.
     * 
     * @param img The new image to be shown.
     */
    public synchronized void setImage(BufferedImage img) {
        this.img = img;
        repaint();
    }

    /**
     * Returns the currently displayed CCD image.
     *
     * @return The currently displayed CCD image.
     */
    public synchronized BufferedImage getImage() {
        return img;
    }

    /**
     * Add an observer to monitor the object.
     * 
     * @param obs The observer to be added.
     */
    public void addObserver(Observer obs) {
        notifier.addObserver(obs);
    }

    /**
     * Delete an observer.
     *
     * @param obs The observer to be deleted.
     */
    public void deleteObserver(Observer obs) {
        notifier.deleteObserver(obs);
    }

    /**
     * Overrides the default get preferred size function.
     * Returns the size of the image that was loaded, up to a certain
     * maximum width and height.  If not specified then returns a
     * default value of max width, max height.
     *
     * @return The dimension of the image panel.
     */
    @Override
    public synchronized Dimension getPreferredSize() {
        if (img == null) {
             return new Dimension(maxWidth, maxHeight);
        } else {
            int width = img.getWidth(null);
            int height = img.getHeight(null);
            if (width > maxWidth)
                width = maxWidth;
            if (height > maxHeight)
                height = maxHeight;

           return new Dimension(width, height);
       }
    }

    /**
     * Specifies the ROI (region of interest).
     *
     * @param x1 X-coordinate of the upper-left corner.
     * @param y1 Y-coordinate of the upper-left corner.
     * @param x2 X-coordinate of the lower-right corner.
     * @param y2 Y-coordinate of the lower-right corner.
     */
    public synchronized void setRectangle(int x1, int y1, int x2, int y2) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    /**
     * Returns the ROI.
     *
     * @return The ROI (region of interest).
     */
    public synchronized Rectangle getROI() {
        return new Rectangle(x1, y1, x2 - x1, y2 - y1);
    }

    /**
     * Returns true if the ROI has been defined, false otherwise.
     * 
     * @return True if the ROI has been defined, false otherwise.
     */
    public synchronized boolean isROIDefined() {
        if (x1 < 0 || x2 < 0 || y1 < 0 || y2 < 0)
            return false;
        else
            return true;
    }

    /**
     * Handles drawing the image in the panel.
     *
     * @param g The graphics object.
     */
    @Override
    public synchronized void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.drawImage(img, 0, 0, getWidth(), getHeight(), null);
        if (x1 >= 0 && x2 >= 0 && y1 >= 0 && y2 >= 0) {
            g.setColor(Color.RED);
            g.drawRect(x1, y1, x2 - x1, y2 - y1);
        }
    }

    /**
     * Calculates the mean intensity of the image within the ROI.
     * XXX/FIXME: has not been tested properly.
     * @return The mean intensity of the image within the ROI.
     */
    public synchronized double getROIIntensity()
    {
        if (!Constants.USE_CCD) {
            return -1;
        }
        
        BufferedImage roiImage = getROIImage();

        double area = roiImage.getWidth() * roiImage.getHeight();
        double mean = 0.0;
        for (int m = 0; m < roiImage.getWidth(); m++) {
            for (int n = 0; n < roiImage.getHeight(); n++) {
                int rgb = roiImage.getRGB(m, n);
                int grayVal = rgb & 0xff;
                mean = mean + 1.0 * grayVal / area;
            }
        }
        
        return mean;
    }

    /**
     * Determines the number of saturated pixel values in the ROI.
     *
     * @return The number of saturated pixel values in the ROI.
     */
    public synchronized int getROISaturatedPixelCount()
    {
        if (!Constants.USE_CCD) {
            return -1;
        }

        BufferedImage roiImage = getROIImage();
        int satPixelCount = 0;
        for (int m = 0; m < roiImage.getWidth(); m++) {
            for (int n = 0; n < roiImage.getHeight(); n++) {
                int rgb = roiImage.getRGB(m, n);
                int grayVal = rgb & 0xff;

                if (grayVal == 255) {
                    satPixelCount ++;
                }
            }
        }
        
        return satPixelCount;
    }


    /**
     * Get the ROI image.  Useful for testing.
     */
    public synchronized BufferedImage getROIImage()
    {
        if (!enableROI || img == null) {
            return null;
        }
        
        double scalingWidth = 1.0 * img.getWidth() / getWidth();
        double scalingHeight = 1.0 * img.getHeight() / getHeight();
        int imgRoiX1 = (int) (x1 * scalingWidth);
        int imgRoiX2 = (int) (x2 * scalingWidth);
        int imgRoiY1 = (int) (y1 * scalingHeight);
        int imgRoiY2 = (int) (y2 * scalingHeight);
        int roiWidth = (int)Math.abs(imgRoiX2 - imgRoiX1);
        int roiHeight = (int)Math.abs(imgRoiY2 - imgRoiY1);

        BufferedImage roiImage =
                new BufferedImage(roiWidth, roiHeight, BufferedImage.TYPE_INT_RGB);
        for (int x = imgRoiX1; x < imgRoiX2; x++) {
            for (int y = imgRoiY1; y < imgRoiY2; y++) {
                int rgb = img.getRGB(x, y);
                roiImage.setRGB(x - imgRoiX1, y - imgRoiY1, rgb);
            }
        }

        return roiImage;
    }

    /**
     * Acts when the mouse is clicked within the ImagePanel.  Used to specify
     * the ROI.
     *
     * @param e The event description object.
     */
    public synchronized void mousePressed(MouseEvent e) {
        Point cursor = e.getLocationOnScreen();

        if (isFirstPoint) {
            x1 = cursor.x - getLocationOnScreen().x;
            y1 = cursor.y - getLocationOnScreen().y;
            isFirstPoint = false;
        } else {
            x2 = cursor.x - getLocationOnScreen().x;
            y2 = cursor.y - getLocationOnScreen().y;
            isFirstPoint = true;
            repaint();
            notifier.notifyChange(this);
        }
    }

    /*
     * Unsupported events.
     */
    public void mouseClicked(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}


    /**
     * The ImagePanelNotifier class is intended only as a means for the
     * ImagePanel class to notify observers about changes.
     */
    class  ImagePanelNotifier extends Observable {
        protected void notifyChange(ImagePanel ip) {
            setChanged();
            notifyObservers(ip);
        }
    }
}