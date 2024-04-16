package org.texttechnologylab.project.parliament_browser_01_montag_2.data.impl.file;

import org.texttechnologylab.project.parliament_browser_01_montag_2.data.Abgeordneter;
import org.texttechnologylab.project.parliament_browser_01_montag_2.data.Bild;
import org.texttechnologylab.project.parliament_browser_01_montag_2.data.Mandat;
import org.texttechnologylab.project.parliament_browser_01_montag_2.data.Partei;
import org.texttechnologylab.project.parliament_browser_01_montag_2.data.impl.factory.InsightBundestagFactory;
import org.texttechnologylab.project.parliament_browser_01_montag_2.helper.StringHelper;
import org.texttechnologylab.project.parliament_browser_01_montag_2.helper.importers.BildImporter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.IOException;
import java.sql.Date;
import java.text.ParseException;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

import static org.texttechnologylab.project.parliament_browser_01_montag_2.helper.NodeHelper.getTextValue;
import static org.texttechnologylab.project.parliament_browser_01_montag_2.helper.XMLReader.processDocument;

/**
 * Klasse bildet Abgeordneter-Daten aus XMLDateien ab.
 * @author Gurpreet Singh
 */
public class Abgeordneter_File_Impl implements Abgeordneter {
    private String id;
    private String vorname;
    private String nachname;
    private Partei partei;
    private String ortszusatz;
    private String adelssuffix;
    private String anrede;
    private String akadTitel;
    private Date geburtsdatum;
    private String geburtsort;
    private Date sterbedatum;
    private String geschlecht;
    private String religion;
    private String beruf;
    private String vita;
    private Date redeDatum;
    private Set<Mandat> mandatSet;

    private Set<Object> objectSet;
    private Set<Bild> bildSet;

    /**
     * Constructor 1
     * Initialisiert Hashset, um die Objekte der Abgeordneten hinzuzufügen.
     */
    public Abgeordneter_File_Impl(){
        objectSet = new HashSet<>(0);
    }

    /**
     * Constructor 2
     */
    public Abgeordneter_File_Impl(String nachname, String vorname, String ortszusatz, String adelssuffix, String anrede, String akadTitel, Date geburtsdatum, String geburtsort, Date sterbedatum, String geschlecht, String religion, String beruf, String vita, Partei partei, String id, Set<Mandat> mandatSet, Set<Bild> bildSet) {
        this.nachname = nachname;
        this.vorname = vorname;
        this.ortszusatz = ortszusatz;
        this.adelssuffix = adelssuffix;
        this.anrede = anrede;
        this.akadTitel = akadTitel;
        this.geburtsdatum = geburtsdatum;
        this.geburtsort = geburtsort;
        this.sterbedatum = sterbedatum;
        this.geschlecht = geschlecht;
        this.religion = religion;
        this.beruf = beruf;
        this.vita = vita;
        this.partei = partei;
        this.id = id;
        this.mandatSet = mandatSet;
        this.bildSet = bildSet;
    }

    /**
     * Constructor 3 für Factory-Implementierung
     */
    public Abgeordneter_File_Impl(InsightBundestagFactory factory) {
        super();
    }

    /**
     * Constructor 4 zur Erstellung der Redner bei den Reden
     */
    public Abgeordneter_File_Impl(Partei partei,String id ,String titel, String vorname, String nachname, Date redeDatum) {
        this.partei = partei;
        this.id = id;
        this.akadTitel = titel;
        this.vorname = vorname;
        this.nachname = nachname;
        this.redeDatum = redeDatum;
    }

    /**
     * Methode verarbeitet das DOM Document, gibt Abgeordneten zurück
     * @param documents Liste von Dokumenten
     * @return Liste von Objekten
     */
    public Set<Object> getAbgeordneterData(Set<Document> documents){
        for (Document document: documents){
            processDocument(document, "MDB", objectSet, getAbgeordnetenFunction);
        }
        return objectSet;
    }

    /**
     * Methode nutzt Element, auf individuelle Textinhalte zuzugreifen
     * @param aElement Element des MDB XMLNodes
     * @return Object Abgeordneter
     * @throws ParseException exception5
     */
    public Abgeordneter getAbgeordneten(Element aElement) throws  ParseException {

        this.nachname = getTextValue(aElement, "NACHNAME");
        this.vorname = getTextValue(aElement, "VORNAME");

        this.ortszusatz = getTextValue(aElement, "ORTSZUSATZ");
        this.adelssuffix =  getTextValue(aElement, "ADEL");
        this.anrede = getTextValue(aElement, "ANREDE_TITEL");
        this.akadTitel = getTextValue(aElement, "AKAD_TITEL");
        this.geburtsort = getTextValue(aElement, "GEBURTSORT");
        this.geschlecht = getTextValue(aElement, "GESCHLECHT");
        this.religion = getTextValue(aElement, "RELIGION");
        this.beruf = getTextValue(aElement, "BERUF");
        this.vita = getTextValue(aElement, "VITA_KURZ");

        // Parsen des Geburtsdatums
        java.util.Date geburtsdatum2 = StringHelper.dateFormat2.parse(getTextValue(aElement, "GEBURTSDATUM"));
        this.geburtsdatum = new Date(geburtsdatum2.getTime());

        //NullPointerException in Sterbedatum
        String sterbeDatumText = getTextValue(aElement, "STERBEDATUM");
        java.util.Date sterbeDatum2 = null;
        if (sterbeDatumText != null && !sterbeDatumText.isEmpty()) {
            sterbeDatum2 = StringHelper.dateFormat2.parse(sterbeDatumText);
        }
        this.sterbedatum = (sterbeDatum2 != null) ? new Date(sterbeDatum2.getTime()) : null;
        this.partei = new Partei_File_Impl(getTextValue(aElement, "PARTEI_KURZ"));
        this.id = getTextValue(aElement, "ID");

        this.mandatSet = new Mandat_File_Impl().getMandatData(aElement);
        try {
            this.bildSet = new BildImporter().importBilder(vorname + " " + nachname);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return new Abgeordneter_File_Impl(nachname, vorname, ortszusatz, adelssuffix, anrede, akadTitel, geburtsdatum, geburtsort, sterbedatum, geschlecht, religion, beruf, vita, partei, id, mandatSet, bildSet);
    }

    Function<Element, Object> getAbgeordnetenFunction = (element) -> {
        try {
            return getAbgeordneten(element);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    };

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getNachname() {
        return nachname;
    }

    @Override
    public String getVorname() {
        return vorname;
    }

    @Override
    public String getOrtsZusatz() {
        return ortszusatz;
    }

    @Override
    public String getAnrede() {
        return anrede;
    }

    @Override
    public String getAkadTitel() {
        return akadTitel;
    }

    @Override
    public Date getGeburtsdatum() {
        return geburtsdatum;
    }

    @Override
    public String getGeburtsort() {
        return geburtsort;
    }

    @Override
    public Date getSterbedatum() {
        return sterbedatum;
    }

    @Override
    public String getGeschlecht() {
        return geschlecht;
    }

    @Override
    public String getReligion() {
        return religion;
    }

    @Override
    public String getBeruf() {
        return beruf;
    }

    @Override
    public String getVita() {
        return vita;
    }

    @Override
    public Partei getPartei() {
        return partei;
    }

    @Override
    public Date getRedeDatum() {
        return redeDatum;
    }

    @Override
    public Set<Mandat> listMandate() {
        return this.mandatSet;
    }

    @Override
    public Set<Bild> listBilder() {
        return this.bildSet;
    }


}

