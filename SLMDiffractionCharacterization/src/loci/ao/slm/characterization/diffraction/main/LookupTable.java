//
// LookupTable.java
//

/*
 * Diffraction calibration of a SLM device by imaging the SLM in the far-field
 * on a CCD Camera.  Measuring the intensity of the first diffraction order.
 * Copyright (C) 2010-@year@ Gunnsteinn Hall @LOCI Labs.
 * University of Wisconsin - Madison.
 */

package loci.ao.slm.characterization.diffraction.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * LookupTable is singleton class for managing the regional LUTs.
 */
public class LookupTable {
    /**
     * Indicates whether a lookup table has been loaded or not.
     */
    private boolean isLoaded;

    /**
     * Indicates whether the use of LUTs is enabled or not.
     */
    private boolean isEnabled;

    /**
     * Number of regions for the given lookup table.
     */
    private int noRegions;
    
    /**
     * The singleton instance of the class, accessible via getInstance().
     */
    public static LookupTable instance = null;

    /**
     * Lookup tables.
     */
    public int[][] lutData;

    /**
     * Constructor.
     */
    private LookupTable() {
        isEnabled = false;
        isLoaded = false;
        noRegions = 0;
    }

    /**
     * Returns the singleton instance of the class.
     *
     * @return The singleton instance of the class.
     */
    public static LookupTable getInstance() {
        if (instance == null) {
            instance = new LookupTable();
        }

        return instance;
    }

    /**
     * Loads the LUTs from a folder.
     *
     * @param path The path to the LUT folder.
     */
    public void loadFolder(String path) {
        // List all maps, format: "region-X.dat" where X: 0 upto (noRegions-1).
        // Load each map, into lutData[X][Y] where Y: 0 upto 255.
        File folder = new File(path);
        FilenameFilter fileFilter = new FileListFilter("region-", "dat");
        File[] listOfFiles = folder.listFiles(fileFilter);

        // Lame regex, but works (not very specific).  Could be improved, and FilenameFilter removed as well.
        Pattern p = Pattern.compile("(\\d+)");

        lutData = new int[listOfFiles.length][256];

        isLoaded = true;
        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                Matcher matcher = p.matcher(listOfFiles[i].getName());
                boolean matchFound = matcher.find();

                if (matchFound) {
                    Integer region = new Integer(matcher.group(0));
                    //System.out.println("File " + listOfFiles[i].getName() + " region: " + region);
                    if (!loadLutFile(listOfFiles[i].getPath(), region)) {
                        System.out.println("Error in loading lut file");
                        isLoaded = false;
                    } else {
                        noRegions++;
                    }
                }
            }
        }
    }

    /**
     * Get the number of loaded regions.
     *
     * @return The number of regions.
     */
    public int getNumberOfRegions() {
        return noRegions;
    }

    /**
     * Load a LUT file for a specific region.
     *
     * @param path Path to the LUT file to load.
     * @param region The region whose LUT is to be loaded.
     *
     * @return True if successful, false otherwise.
     */
    private boolean loadLutFile(String path, int region) {
        BufferedReader input = null;
        try {
            input = new BufferedReader(new FileReader(path));
            String line = null; //not declared within while loop
            int i = 0;
            while ((line = input.readLine()) != null) {
                Double val = new Double(line);                
                lutData[region][i++] = val.intValue();                
            }
            input.close();
        } catch (IOException ex) {
            Logger.getLogger(LookupTable.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        
        return true;
    }

    /**
     * Sets the status indicating whether the use of a LUT is enabled or not.
     *
     * @param isEnabled True if enabled, false otherwise.
     */
    public void setEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    /**
     * Returns true if the use of the lookup table is enabled, false if not.
     *
     * @return True if the use of the lookup table is enabled, false if not.
     */
    public boolean isEnabled() {
        if (isEnabled && isLoaded) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Performs a lookup in the table and returns the mapped value.
     *
     * @param value The value to look up in the table.
     * @param region The region whose LUT is to be used.
     * 
     * @return The mapped value.
     */
    public int lookup(int value, int region) {
        if (isEnabled) {
            return lutData[region][value];
        } else {
            return value;
        }
    }
}
