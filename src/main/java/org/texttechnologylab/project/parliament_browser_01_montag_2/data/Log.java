package org.texttechnologylab.project.parliament_browser_01_montag_2.data;

import java.sql.Date;

/**
 * Schnittstelle zur Abbildung der Log-Aktionen auf dem Web
 */
public interface Log {
    /**
     * Gibt den Bson Document ID zurück
     * @return
     */
    String getId();

    /**
     * Gibt die ausgeführte Aktion (Rest Routen) zurück
     * @return
     */
    String getAction();

    /**
     * Gibt der Parameter zurück
     * @return
     */
    String getParameter();

    /**
     * Gibt den Zeitstempel zurück
     * @return
     */
    Date getTimeStamp();
}
