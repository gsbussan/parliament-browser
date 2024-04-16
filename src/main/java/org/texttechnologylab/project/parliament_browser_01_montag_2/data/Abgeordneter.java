package org.texttechnologylab.project.parliament_browser_01_montag_2.data;

import java.sql.Date;
import java.util.Set;

/**
 * Schnittstelle zur Abbildung von Abgeordneter.
 * @author Gurpreet Singh
 */
public interface Abgeordneter {
    /**
     * Gibt ID des Abgeordneten zurück
     * @return
     */
    String getId();
    /**
     * Gibt Nachname des Abgeordneten zurück
     * @return
     */
    String getNachname();
    /**
     * Gibt Vorname des Abgeordneten zurück
     * @return
     */
    String getVorname();
    /**
     * Gibt Ortszusatz des Abgeordneten zurück
     * @return
     */
    String getOrtsZusatz();
    /**
     * Gibt Anrede des Abgeordneten zurück
     * @return
     */
    String getAnrede();
    /**
     * Gibt AkadTitel des Abgeordneten zurück
     * @return
     */
    String getAkadTitel();
    /**
     * Gibt Geburtsdatum des Abgeordneten zurück
     * @return
     */
    Date getGeburtsdatum();
    /**
     * Gibt Geburtsort des Abgeordneten zurück
     * @return
     */
    String getGeburtsort();
    /**
     * Gibt Sterbedatum des Abgeordneten zurück
     * @return
     */
    Date getSterbedatum();
    /**
     * Gibt Geschlecht des Abgeordneten zurück
     * @return
     */
    String getGeschlecht();
    /**
     * Gibt Religion des Abgeordneten zurück
     * @return
     */
    String getReligion();
    /**
     * Gibt Beruf des Abgeordneten zurück
     * @return
     */
    String getBeruf();
    /**
     * Gibt Vita des Abgeordneten zurück
     * @return
     */
    String getVita();
    /**
     * Gibt Partei des Abgeordneten zurück
     * @return
     */
    Partei getPartei();

    /**
     * Get Datum der von Abgeordneten gehaltenen Reden zurück
     * @return
     */
    Date getRedeDatum();

    /**
     * Gibt die Informationen zu Mandate zurück
     * @return
     */
    Set<Mandat> listMandate();

    /**
     * Gibt die Bilder der Abgeordneten zurück
     * @return
     */
    Set<Bild> listBilder();

}
