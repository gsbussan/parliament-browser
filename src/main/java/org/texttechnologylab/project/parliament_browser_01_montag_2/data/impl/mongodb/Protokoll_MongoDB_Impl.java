package org.texttechnologylab.project.parliament_browser_01_montag_2.data.impl.mongodb;

import org.bson.Document;
import org.texttechnologylab.project.parliament_browser_01_montag_2.data.Bild;
import org.texttechnologylab.project.parliament_browser_01_montag_2.data.Protokoll;
import org.texttechnologylab.project.parliament_browser_01_montag_2.data.Rede;
import org.texttechnologylab.project.parliament_browser_01_montag_2.data.Tagesordnungspunkt;
import org.texttechnologylab.project.parliament_browser_01_montag_2.data.impl.factory.InsightBundestagFactory;
import org.texttechnologylab.project.parliament_browser_01_montag_2.data.impl.file.Bild_File_Impl;
import org.texttechnologylab.project.parliament_browser_01_montag_2.data.impl.file.Protokoll_File_Impl;
import org.texttechnologylab.project.parliament_browser_01_montag_2.data.impl.file.Rede_File_Impl;
import org.texttechnologylab.project.parliament_browser_01_montag_2.data.impl.file.Tagesordnungspunkt_File_Impl;
import org.texttechnologylab.project.parliament_browser_01_montag_2.helper.StringHelper;

import java.time.LocalTime;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Klasse verantwortlich zur Abbildung der Sitzung als MongoDB-Dokumnet
 * @author Gurpreet Singh
 * @author Mihai Paun
 */
public class Protokoll_MongoDB_Impl extends Protokoll_File_Impl{
    private Document document;

    public Protokoll_MongoDB_Impl(InsightBundestagFactory factory, Document document) {
        super(factory);
        this.document = document;
    }

    @Override
    public int getSitzungNummer() {
        return document.getInteger("nummer");
    }

    @Override
    public int getWahlperiode() {
        return document.getInteger("wahlperiode");
    }

    @Override
    public java.sql.Date getSitzungDatum() {

        String formattedDate = StringHelper.DATE_FORMAT.format(document.getDate("datum"));
        java.sql.Date sqlDate = java.sql.Date.valueOf(formattedDate);
        return sqlDate;

    }

    @Override
    public LocalTime getSitzungStart() {
        Date date = document.getDate("beginn");
        String formattedDate = StringHelper.dateFormat.format(date);
        LocalTime sStart = LocalTime.parse(formattedDate, StringHelper.FORMATTER);
        return sStart;
    }

    @Override
    public LocalTime getSitzungEnde() {
        Date date = document.getDate("ende");
        String formattedDate = StringHelper.dateFormat.format(date);
        LocalTime sEnde = LocalTime.parse(formattedDate, StringHelper.FORMATTER);
        return sEnde;
    }

    @Override
    public Set<Tagesordnungspunkt> getTagesordnungspunkten() {
        Set<Tagesordnungspunkt> tagesordnungspunkts = new HashSet<>(0);

        List<Document> tList = document.getList("top", Document.class);
        if(tList!=null){
            for(Document tDoc: tList){
                String topId = tDoc.getString("_id");

                //Hier werden die Reden verarbeitet
                List<Document> rList = tDoc.getList("reden", Document.class);
                Set<Rede> reden = new HashSet<>(0);

                if(rList!=null && !rList.isEmpty()){
                    for(Document redeDoc : rList){
                        String id = redeDoc.getString("_id");
                        String text = redeDoc.getString("text");

                        Rede rede = new Rede_File_Impl(id, text);
                        reden.add(rede);

                    }
                }

                Tagesordnungspunkt tagesordnungspunkt = new Tagesordnungspunkt_File_Impl(topId, reden);
                tagesordnungspunkts.add(tagesordnungspunkt);
            }
        }
        return tagesordnungspunkts;
    }
}
