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
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Observable;
import javax.swing.JPanel;

/**
 * The ImagePanel class defines a panel that can contain an image.
 */
class ImagePanel extends JPanel implements MouseListener {
    /**
     * The Image object to be displayed in the panel.
     */
    private Image image;
    private int x1, y1;
    private int x2, y2;
    private boolean isFirstPoint;

    public ImagePanel() {
        addMouseListener(this);
        x1 = y1 = -1;
        x2 = y2 = -1;
        isFirstPoint = true;
        image =
                Toolkit.getDefaultToolkit().getImage(
                "C:/Users/Public/Pictures/Sample Pictures/Tulips.jpg");
        //setSize(image.getWidth(this), image.getHeight(this));
    }
    /**
     * Set the image to be displayed in the panel.
     *
     * @param image The image to be displayed.
     */
    public void setImage(Image image) {
        this.image = image;
    }

    public void setRectangle(int x1, int y1, int x2, int y2) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    /**
     * Handles drawing the image in the panel.
     *
     * @param g The graphics object.
     */
    @Override
    public void paintComponent(Graphics g) {
        //System.out.println("Paint Runs on: " + Thread.currentThread().getName());
        super.paintComponent(g);
        g.drawImage(image, 0, 0, this);
        if (x1 >= 0 && x2 >= 0 && y1 >= 0 && y2 >= 0) {
            g.drawRect(x1, y1, x2 - x1, y2 - y1);
        }
    }

    public void mouseClicked(MouseEvent e) {
        Point cursor = e.getLocationOnScreen();

        System.out.println("Location: " + cursor.toString());
        if (isFirstPoint) {

            x1 = cursor.x - getLocationOnScreen().x;
            y1 = cursor.y - getLocationOnScreen().y;
            isFirstPoint = false;
        } else {
            x2 = cursor.x - getLocationOnScreen().x;
            y2 = cursor.y - getLocationOnScreen().y;
            isFirstPoint = true;
            System.out.println("Repainting rectangle");
            repaint();
        }
    }

    public void mousePressed(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
}