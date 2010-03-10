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
    private final int maxWidth = 800;

    /**
     * The maximum height of the Image Panel (in pixels).
     */
    private final int maxHeight = 600;

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
           img = ImageIO.read(new File("C:/Users/Public/Pictures/Sample Pictures/Tulips.jpg"));
        } catch (IOException e) {
        }

        setPreferredSize(getPreferredSize());
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
    public Dimension getPreferredSize() {
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
    public void setRectangle(int x1, int y1, int x2, int y2) {
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
    public Rectangle getROI() {
        return new Rectangle(x1, y1, x2 - x1, y2 - y1);
    }

    /**
     * Returns true if the ROI has been defined, false otherwise.
     * 
     * @return True if the ROI has been defined, false otherwise.
     */
    public boolean isROIDefined() {
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
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(img, 0, 0, null);
        if (x1 >= 0 && x2 >= 0 && y1 >= 0 && y2 >= 0) {
            g.setColor(Color.RED);
            g.drawRect(x1, y1, x2 - x1, y2 - y1);
        }
    }

    /**
     * Acts when the mouse is clicked within the ImagePanel.  Used to specify
     * the ROI.
     *
     * @param e The event description object.
     */
    public void mousePressed(MouseEvent e) {
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
    class ImagePanelNotifier extends Observable {
        protected void notifyChange(ImagePanel ip) {
            setChanged();
            notifyObservers(ip);
        }
    }
}