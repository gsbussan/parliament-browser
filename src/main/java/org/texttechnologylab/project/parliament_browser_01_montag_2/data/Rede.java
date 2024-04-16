package org.texttechnologylab.project.parliament_browser_01_montag_2.data;

import org.apache.uima.UIMAException;
import org.apache.uima.jcas.JCas;

import java.util.Set;

/**
 * Schnittstelle zur Abbildung von Reden.
 * @author Gurpreet Singh
 */
public interface Rede {
    /**
     * Gibt ID einer Rede zurück
     * @return
     */
    String getId();

    /**
     * Gibt Redner Objekt zurück
     * @return
     */
    Abgeordneter getRedner();

    /**
     * Gibt Inhalt einer Rede zurück
     * @return
     */
    String getText();

    /**
     * Gibt Kommentaren einer Rede zurück
     * @return
     */
    Set<Kommentar> getKommentar();

    JCas toCAS() throws UIMAException;
}
