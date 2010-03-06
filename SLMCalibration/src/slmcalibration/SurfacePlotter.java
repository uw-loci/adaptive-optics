/*
 * SurfacePlotter.java
 * author: Min Ren 2005-2007
 * Created on November 14, 2005, 10:27 AM

 * Surface plotting class using Java 3D.
 */

package slmcalibration;

import visad.*;
import visad.java3d.*;
import javax.swing.*;

/**
 * Class for drawing surface plots using Java 3D.
 */
public class SurfacePlotter {
    private static final int WIDTH = 512,  HEIGHT = 512,  PHASE = 255;
    private static FlatField ff,  ff1;
    private static DataReferenceImpl ref,  ref1;
    private static FunctionType type,  type1;
    private static Integer2DSet set,  set1;

    public SurfacePlotter() {
    }

    /**
     * Prepares the left panel for display.
     *
     * @param Panel The left panel to display the graph on.
     * @throws java.lang.Exception An Exception.
     */
    public static void buildDisplay(JPanel Panel)
            throws Exception {
        // set up types
        RealType x = RealType.getRealType("x");
        RealType y = RealType.getRealType("y");
        RealType v = RealType.getRealType("v");
        RealTupleType xy = new RealTupleType(x, y);
        type = new FunctionType(xy, v);
        set = new Integer2DSet(xy, WIDTH, HEIGHT);

        // construct VisAD display
        DisplayImplJ3D display = new DisplayImplJ3D("display");
        ref = new DataReferenceImpl("ref");
        ff = new FlatField(type, set);
        ref.setData(ff);
        display.getGraphicsModeControl().setScaleEnable(true);
        display.getGraphicsModeControl().setTextureEnable(false);
        display.setAlwaysAutoScale(true);
        display.addMap(new ScalarMap(x, Display.XAxis));
        display.addMap(new ScalarMap(y, Display.YAxis));
        display.addMap(new ScalarMap(v, Display.ZAxis));
        display.addMap(new ScalarMap(v, Display.RGB));
        display.addReference(ref);

        // add display to panel
        Panel.add(display.getComponent());
    }

    /**
     * Prepares the right panel for display.
     *
     * @param Panel The right panel to display the graph on.
     * @throws java.lang.Exception An Exception.
     */
    public static void buildDisplay1(JPanel Panel)
            throws Exception {
        // set up types
        RealType x = RealType.getRealType("xx");
        RealType y = RealType.getRealType("yy");
        RealType v = RealType.getRealType("vv");
        RealTupleType xy = new RealTupleType(x, y);
        type1 = new FunctionType(xy, v);
        set1 = new Integer2DSet(xy, WIDTH, HEIGHT);

        // construct VisAD display
        DisplayImplJ3D display = new DisplayImplJ3D("display1");
        ref1 = new DataReferenceImpl("ref1");
        ff1 = new FlatField(type1, set1);
        ref1.setData(ff1);
        ScalarMap xmap = new ScalarMap(x, Display.XAxis);
        ScalarMap ymap = new ScalarMap(y, Display.YAxis);
        ScalarMap zmap = new ScalarMap(v, Display.ZAxis);
        ScalarMap vmap = new ScalarMap(v, Display.RGB);

        display.getGraphicsModeControl().setScaleEnable(true);
        display.getGraphicsModeControl().setTextureEnable(false);
        display.setAlwaysAutoScale(true);

        display.addMap(xmap);
        display.addMap(ymap);
        display.addMap(zmap);
        display.addMap(vmap);
        display.addReference(ref1);

        zmap.setRange(0.0f, 255.0f);
        vmap.setRange(0.0f, 255.0f);

        // add display to panel
        Panel.add(display.getComponent());
    }

    /**
     * Displays the picture in the left chart panel.
     *
     * @param samps The data to plot (left frame).
     * @throws java.lang.Exception
     */
    public static void showpic(double[][] samps)
            throws Exception {
        ff.setSamples(samps, false);
        ref.setData(ff);
        DisplayImplJ3D display = new DisplayImplJ3D("display");
        display.reAutoScale();
    }

    /**
     * Displays the picture in the right chart panel.
     *
     * @param samps The data to plot (right frame).
     * @throws java.lang.Exception
     */
    public static void showpic1(double[][] samps)
            throws Exception {
        //show the next pic
        ff1.setSamples(samps, false);
        ref1.setData(ff1);
        DisplayImplJ3D display = new DisplayImplJ3D("display1");
        display.reAutoScale();
    }
}
