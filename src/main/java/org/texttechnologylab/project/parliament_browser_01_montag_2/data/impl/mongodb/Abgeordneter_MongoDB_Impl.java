package org.texttechnologylab.project.parliament_browser_01_montag_2.data.impl.mongodb;

import org.bson.Document;
import org.texttechnologylab.project.parliament_browser_01_montag_2.data.*;
import org.texttechnologylab.project.parliament_browser_01_montag_2.data.impl.factory.InsightBundestagFactory;
import org.texttechnologylab.project.parliament_browser_01_montag_2.data.impl.file.*;
import org.texttechnologylab.project.parliament_browser_01_montag_2.helper.StringHelper;

import java.sql.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Klasse verantwortlich zur Abbildung der Abgeordneten als MongoDB-Dokument
 * @author Gurpreet Singh
 */
public class Abgeordneter_MongoDB_Impl  extends Abgeordneter_File_Impl {

    private final Document document;

    public Abgeordneter_MongoDB_Impl(InsightBundestagFactory factory, Document document) {
        super(factory);
        this.document = document;
    }

    @Override
    public String getId() {
        return document.getString("_id");
    }

    @Override
    public String getNachname() {
        return document.getString("nachname");
    }

    @Override
    public String getVorname() {
        return document.getString("vorname");
    }

    @Override
    public String getOrtsZusatz() {
        return document.getString("ortszusatz");
    }

    @Override
    public String getAnrede() {
        return document.getString("anrede");
    }

    @Override
    public String getAkadTitel() {
        return document.getString("akadtitel");
    }

    @Override
    public Date getGeburtsdatum() {
        // Formatiere das Datum zu einer String
        String formattedDate = StringHelper.DATE_FORMAT.format(document.getDate("geburtsdatum"));

        // Ändere die formatierte String zu java.sql.Date
        Date sqlDate = Date.valueOf(formattedDate);
        return sqlDate;
    }

    @Override
    public String getGeburtsort() {
        return document.getString("geburtsort");
    }

    @Override
    public Date getSterbedatum() {
        // Formatiere das Datum zu einer String
        String formattedDate = StringHelper.DATE_FORMAT.format(document.getDate("sterbedatum"));

        // Ändere die formatierte String zu java.sql.Date
        Date sqlDate = Date.valueOf(formattedDate);
        return sqlDate;
    }

    @Override
    public String getGeschlecht() {
        return document.getString("geschlecht");
    }

    @Override
    public String getReligion() {
        return document.getString("religion");
    }

    @Override
    public String getBeruf() {
        return document.getString("beruf");
    }

    @Override
    public String getVita() {
        return document.getString("vita");
    }

    @Override
    public Partei getPartei() {
        Partei partei = new Partei_File_Impl(document.getString("partei"));
        return partei;
    }

    @Override
    public Date getRedeDatum() {
        // Formatiere das Datum zu einer String
        String formattedDate = StringHelper.DATE_FORMAT.format(document.getDate("redeDatum"));

        // Ändere die formatierte String zu java.sql.Date
        Date sqlDate = Date.valueOf(formattedDate);
        return sqlDate;
    }
    @Override
    public Set<Mandat> listMandate() {
        Set<Mandat> mandatSet = new HashSet<>();

        List<Document> mandatList = document.getList("mandat", Document.class);

        if (mandatList != null) {
            for (Document mandatDoc : mandatList) {

                Document wahlperiodeDoc = mandatDoc.get("wahlperiode", Document.class);
                Wahlperiode wahlperiode = null;

                int wNummer = wahlperiodeDoc.getInteger("nummer");
                wahlperiode = new Wahlperiode_File_Impl(wNummer);

                List<Document> fraktionList = mandatDoc.getList("fraktion", Document.class);
                Set<Fraktion> fraktionSet = new HashSet<>();

                if (fraktionList != null && !fraktionList.isEmpty()) {
                    for (Document fraktionDoc : fraktionList) {
                        String fName = fraktionDoc.getString("fName");
                        Fraktion fraktion = new Fraktion_File_Impl(fName);
                        fraktionSet.add(fraktion);
                    }
                }

                List<Document> ausschussList = mandatDoc.getList("ausschuss", Document.class);
                Set<Ausschuss> ausschussSet = new HashSet<>();

                if (ausschussList != null && !ausschussList.isEmpty()) {
                    for (Document ausschussDoc : ausschussList) {
                        String aName = ausschussDoc.getString("aName");
                        Ausschuss ausschuss = new Ausschuss_File_Impl(aName);
                        ausschussSet.add(ausschuss);
                    }
                }

                // Create Mandat instance and set Fraktion and Ausschuss sets
                Mandat mandat = new Mandat_File_Impl();
                mandat.setWahlperiode(wahlperiode);
                mandat.setFraktionSet(fraktionSet);
                mandat.setAusschussSet(ausschussSet);

                mandatSet.add(mandat);
            }
        }

        return mandatSet;
    }

    @Override
    public Set<Bild> listBilder() {
        Set<Bild> bildSet = new HashSet<>();

        // Hier auch ähnliche Vorgehensweise wie oben, weil "kommentar" eigentlich auch nested ist
        List<Document> bilderList = document.getList("bilder", Document.class);

        if (bilderList != null) {
            for (Document bildDoc : bilderList) {
                String metadaten = bildDoc.getString("metadata");
                String imageUrl = bildDoc.getString("bildUrl");

                Bild bild = new Bild_File_Impl(metadaten, imageUrl);

                bildSet.add(bild);
            }
        }

        return bildSet;
    }


}

