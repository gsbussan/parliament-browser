package org.texttechnologylab.project.parliament_browser_01_montag_2.data.impl.file;

import org.texttechnologylab.project.parliament_browser_01_montag_2.data.Bild;

/**
 * Klasse bildet Bilder-Daten der Abgeordneten aus XMLDateien ab.
 * @author Mihai Paun
 */
public class Bild_File_Impl implements Bild {
    private String metaDaten;
    private String bildUrl;

    public Bild_File_Impl(String metadaten, String imageUrl) {
        this.metaDaten = metadaten;
        this.bildUrl = imageUrl;
    }

    @Override
    public String getMetadaten() {
        return this.metaDaten;
    }

    @Override
    public String getBildUrl() {
        return this.bildUrl;
    }
}
