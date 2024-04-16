package org.texttechnologylab.project.parliament_browser_01_montag_2.data.impl.file;

import org.texttechnologylab.project.parliament_browser_01_montag_2.data.Protokoll;
import org.texttechnologylab.project.parliament_browser_01_montag_2.data.Rede;
import org.texttechnologylab.project.parliament_browser_01_montag_2.data.Tagesordnungspunkt;
import org.texttechnologylab.project.parliament_browser_01_montag_2.data.impl.factory.InsightBundestagFactory;
import org.texttechnologylab.project.parliament_browser_01_montag_2.exceptions.InvalidFileTypeException;
import org.texttechnologylab.project.parliament_browser_01_montag_2.exceptions.InvalidInputException;
import org.texttechnologylab.project.parliament_browser_01_montag_2.helper.DirectoryReader;
import org.texttechnologylab.project.parliament_browser_01_montag_2.helper.StringHelper;
import org.texttechnologylab.project.parliament_browser_01_montag_2.helper.XMLReader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.sql.Date;
import java.text.ParseException;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

import static org.texttechnologylab.project.parliament_browser_01_montag_2.helper.NodeHelper.getAttributeValue;
import static org.texttechnologylab.project.parliament_browser_01_montag_2.helper.XMLReader.processDocument;

/**
 * Klasse bildet Sitzungen aus den XMLDateien ab.
 * @author Gurpreet Singh
 */
public class Protokoll_File_Impl implements Protokoll {
    private int sitzungNummer;
    private int wahlperiode;
    private LocalTime sitzungStart;
    private LocalTime sitzungEnde;
    private Date sitzungDatum;
    private Set<Object> objectSet;
    private Set<Tagesordnungspunkt> tagesordnungspunktSet;

    /**
     * Constructor 1
     * Initialisiert Hashset, um die Objekte der Reden hinzuzufügen.
     */
    public Protokoll_File_Impl() {
        objectSet = new HashSet<>(0);
    }

    /**
     * Constructor 2
     */
    public Protokoll_File_Impl(int sitzungNummer, int wahlperiode, LocalTime sitzungStart, LocalTime sitzungEnde
            , Date sitzungDatum, Set<Tagesordnungspunkt> tagesordnungspunktSet) {
        this.sitzungNummer = sitzungNummer;
        this.wahlperiode = wahlperiode;
        this.sitzungStart = sitzungStart;
        this.sitzungEnde = sitzungEnde;
        this.sitzungDatum = sitzungDatum;
        this.tagesordnungspunktSet = tagesordnungspunktSet;
    }

    /**
     * Constructor für Factory-Implementierung
     * @param factory Factory
     */
    public Protokoll_File_Impl(InsightBundestagFactory factory) {
    }

    /**
     * Methode verarbeitet das DOM Document, gibt Sitzungen zurück
     * @param documentSet Liste von Dokumenten
     * @return Liste von Objekten
     */
    public Set<Object> getSitzungData(Set<Document> documentSet){
        for(Document document: documentSet){
            processDocument(document, "dbtplenarprotokoll", objectSet, getSitzungFunction);
        }
        return objectSet;
    }

    /**
     * Methode nutzt Element, auf individuelle Textinhalte zuzugreifen
     * @param element Element der XMLNodes
     */
    public Protokoll getSitzung(Element element) throws ParseException {

        this.sitzungNummer = Integer.parseInt(getAttributeValue(element, "sitzung-nr"));
        this.wahlperiode = Integer.parseInt(getAttributeValue(element, "wahlperiode"));

        java.util.Date date = StringHelper.dateFormat2.parse(getAttributeValue(element, "sitzung-datum"));
        this.sitzungDatum = new Date(date.getTime());

        String sStart = getAttributeValue(element, "sitzung-start-uhrzeit");
        LocalTime sBeginn = LocalTime.parse(sStart.replace(".",":").replace("Uhr","").trim(), StringHelper.timeFormatter);

        String sEnde = getAttributeValue(element, "sitzung-ende-uhrzeit" );
        LocalTime sitzEnde = LocalTime.parse(sEnde.replace(".", ":").replace("Uhr","").trim(), StringHelper.timeFormatter);

        //Hier werden die Tagesordnungspunkten in einem Protokoll verarbeitet.
        Set<Tagesordnungspunkt> tagesordnungspunkts = new HashSet<>(0);
        NodeList tPunkt = element.getElementsByTagName("tagesordnungspunkt");

        for(int i =0;i<tPunkt.getLength();i++) {

            Node tPunkNode = tPunkt.item(i);
            if (tPunkNode.getNodeType() == Node.ELEMENT_NODE) {

                Element tElement = (Element) tPunkNode;
                tagesordnungspunkts.add( new Tagesordnungspunkt_File_Impl().getTagesordnungspunkte(tElement));
            }
        }

        return new Protokoll_File_Impl(sitzungNummer, wahlperiode, sBeginn, sitzEnde, sitzungDatum, tagesordnungspunkts);
    }

    Function<Element, Object> getSitzungFunction = element -> {
        try {
            return getSitzung(element);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    };

    @Override
    public int getSitzungNummer() {
        return sitzungNummer;
    }

    @Override
    public int getWahlperiode() {
        return wahlperiode;
    }

    @Override
    public Date getSitzungDatum() {
        return sitzungDatum;
    }

    @Override
    public LocalTime getSitzungStart() {
        return sitzungStart;
    }

    @Override
    public LocalTime getSitzungEnde() {
        return sitzungEnde;
    }

    @Override
    public Set<Tagesordnungspunkt> getTagesordnungspunkten() {
        return tagesordnungspunktSet;
    }

}
