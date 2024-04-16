package org.texttechnologylab.project.parliament_browser_01_montag_2.data.impl.file;

import org.texttechnologylab.project.parliament_browser_01_montag_2.data.Partei;

/**
 * Klasse bildet Parteien aus den XMLDateien ab.
 * @author Gurpreet Singh
 */
public class Partei_File_Impl implements Partei {
    private String name;

    /**
     * Constructor
     * @param parteiName name
     */
    public Partei_File_Impl(String parteiName) {
        this.name = parteiName;
    }

    @Override
    public String getName() {
        return name;
    }
}
