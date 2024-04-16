package org.texttechnologylab.project.parliament_browser_01_montag_2.data;

import java.sql.Date;
import java.time.LocalTime;
import java.util.Set;

/**
 * Schnittstelle zur Abbildung von Sitzungen.
 * @author Gurpreet Singh
 */
public interface Protokoll {
    /**
     * Gibt Sitzung Nummer zurück.
     * @return
     */
    int getSitzungNummer();

    /**
     * Gibt Wahlperiode einer Sitzung zurück
     * @return
     */
    int getWahlperiode();

    /**
     * Gibt Datum einer Sitzung zurück
     * @return
     */
    Date getSitzungDatum();

    /**
     * Gibt StartZeit einer Sitzung zurück
     * @return
     */
    LocalTime getSitzungStart();

    /**
     * Gibt EndZeit einer Sitzung zurück
     * @return
     */
    LocalTime getSitzungEnde();

    /**
     * Gibt die Tagesordnungspunkten in einem Plenarprotokoll zurück
     * @return
     */
    Set<Tagesordnungspunkt> getTagesordnungspunkten();


}
