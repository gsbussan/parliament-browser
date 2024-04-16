package org.texttechnologylab.project.parliament_browser_01_montag_2.data;

import java.sql.Date;
import java.util.Set;

/**
 * Schnittstelle zur Abbildung von Mandaten.
 * @author Gurpreet Singh
 */
public interface Mandat {
    /**
     * Gibt von Datum zurück
     * @return
     */
    Date fromDate();

    /**
     * Gibt bis Datum zurück
     * @return
     */
    Date toDate();

    /**
     * Gibt die Art von Mandat zurück
     * @return
     */
    String getTyp();

    /**
     * Gibt die Wahlperiode(als Objekt zurück)
     * @return
     */
    Wahlperiode getWahlperiode();

    /**
     * Gibt die Fraktionen nach Wahlperiode gruppiert zurück
     * @return
     */
    Set<Fraktion> getFraktionSet();

    /**
     * Gibt die Ausschüsse nach Wahlperiode gruppiert zurück
     * @return
     */
    Set<Ausschuss> getAusschussSet();

    /**
     * Setzt die Wahlperiode ein
     * @param wahlperiode
     */
    void setWahlperiode(Wahlperiode wahlperiode);

    /**
     * Setzt die Fraktionen ein
     * @param fraktionSet
     */

    void setFraktionSet(Set<Fraktion> fraktionSet);

    /**
     * Setzt die Ausschüsse ein
     * @param ausschussSet
     */
    void setAusschussSet(Set<Ausschuss> ausschussSet);
}
