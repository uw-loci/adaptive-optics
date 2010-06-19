/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package loci.ao.slm.characterization.diffraction.main;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
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
     */
    public void loadFolder(String path) {
        // List all maps, format: "region-X.dat" where X: 0 upto (noRegions-1).
        // Load each map, into lutData[X][Y] where Y: 0 upto 255.


        File folder = new File(path);
        FilenameFilter fileFilter = new FileListFilter("region-", "dat");
        File[] listOfFiles = folder.listFiles(fileFilter);

        // Lame regex, but works (not very specific).  Could be improved, and FilenameFilter removed as well.
        Pattern p = Pattern.compile("(\\d+)");

        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                Matcher matcher = p.matcher(listOfFiles[i].getName());
                boolean matchFound = matcher.find();

                if (matchFound) {
                    Integer region = new Integer(matcher.group(0));
                    System.out.println("File " + listOfFiles[i].getName() + " region: " + region);
                    loadLutFile(listOfFiles[i].getPath(), region);
                }
            }
        }
    }

    private boolean loadLutFile(String path, int region) {
        return true;
    }



}
