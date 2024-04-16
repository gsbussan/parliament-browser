package org.texttechnologylab.project.parliament_browser_01_montag_2.data.impl.file;

import org.texttechnologylab.project.parliament_browser_01_montag_2.data.Kommentar;

/**
 * Klassen bildet Kommentaren aus den Reden in XML Dateien b.
 * @author Gurpreet Singh
 */
public class Kommentar_File_Impl implements Kommentar {
    private String text;

    /**
     * Constructor
     * @param text Inhalt
     */
    public Kommentar_File_Impl(String text) {
        this.text = text;
    }

    @Override
    public String getText() {
        return text;
    }
}
