package org.texttechnologylab.project.parliament_browser_01_montag_2.data.impl.file;

import org.texttechnologylab.project.parliament_browser_01_montag_2.data.Wahlperiode;

import java.sql.Date;

/**
 * Klasse bildet Wahlperiode-Daten aus XMLDateien ab.
 * @author Taylan Özcelik
 */
public class Wahlperiode_File_Impl implements Wahlperiode {
    private int wNummer;
    private Date startDate;
    private Date endDate;


    public Wahlperiode_File_Impl(String wp, Date fromDate, Date toDate) {
        this.wNummer = Integer.parseInt(wp);
        this.startDate = fromDate;
        this.endDate = toDate;
    }

    public Wahlperiode_File_Impl(int wNummer) {
        this.wNummer=wNummer;
    }

    @Override
    public int getNummer() {
        return this.wNummer;
    }

    @Override
    public Date getStartDatum() {
        return this.startDate;
    }

    @Override
    public Date getEndeDatum() {
        return this.endDate;
    }
}
