package org.texttechnologylab.project.parliament_browser_01_montag_2.data;


import java.sql.Date;

/**
 * Schnittstelle zur Abbildung der Wahlperioden
 * @author Gurpreet Singh
 */
public interface Wahlperiode {
    /**
     * Gibt die Nummer der Wahlperiode zurück
     * @return
     */
    int getNummer();

    /**
     * Gibt Startdatum zurück
     * @return
     */
    Date getStartDatum();

    /**
     * Gibt EndDatum zurück
     * @return
     */
    Date getEndeDatum();

}
