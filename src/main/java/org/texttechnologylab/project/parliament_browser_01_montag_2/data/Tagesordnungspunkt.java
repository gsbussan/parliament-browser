package org.texttechnologylab.project.parliament_browser_01_montag_2.data;

import java.util.Set;

/**
 * Schnittstelle zur Abbildung von Tagesordnungspunkten.
 * @author Gurpreet Singh
 */
public interface Tagesordnungspunkt {
    /**
     * Gibt ID eines Tagesordnungspunkts zurück.
     * @return
     */
    String getTopId();
    Set<Rede> getReden();
}
