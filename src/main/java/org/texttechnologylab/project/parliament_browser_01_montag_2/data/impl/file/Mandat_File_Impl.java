package org.texttechnologylab.project.parliament_browser_01_montag_2.data.impl.file;

import org.texttechnologylab.project.parliament_browser_01_montag_2.data.Ausschuss;
import org.texttechnologylab.project.parliament_browser_01_montag_2.data.Fraktion;
import org.texttechnologylab.project.parliament_browser_01_montag_2.data.Mandat;
import org.texttechnologylab.project.parliament_browser_01_montag_2.data.Wahlperiode;
import org.texttechnologylab.project.parliament_browser_01_montag_2.helper.StringHelper;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.sql.Date;
import java.text.ParseException;
import java.util.HashSet;
import java.util.Set;

import static org.texttechnologylab.project.parliament_browser_01_montag_2.helper.NodeHelper.getTextValue;

public class Mandat_File_Impl implements Mandat {
    private Date fromDate;
    private Date toDate;
    private Set<Fraktion> fraktionSet;
    private Set<Ausschuss> ausschussSet;
    private String mandatType;
    private Wahlperiode wahlperiode;

    public Mandat_File_Impl(Date fromDate, Date toDate, Set<Fraktion> fraktionSet, Set<Ausschuss> ausschussSet , String mandatValue, Wahlperiode wahlperiode1) {
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.fraktionSet = fraktionSet;
        this.ausschussSet = ausschussSet;
        this.mandatType = mandatValue;
        this.wahlperiode = wahlperiode1;
    }

    public Mandat_File_Impl() {

    }

    public <R> Mandat_File_Impl(Wahlperiode wahlperiode, R collect, R collect1) {
        this.wahlperiode = wahlperiode;
        this.fraktionSet = (Set<Fraktion>) collect;
        this.ausschussSet = (Set<Ausschuss>) collect1;
    }


    /**
     * Methode extrahiert Daten aus den XMLNodes raus
     * @param element element
     * @return Set von Mandaten
     */
    public Set<Mandat> getMandatData(Element element) throws ParseException {
        NodeList mandatenList = element.getElementsByTagName("WAHLPERIODE");
        Set<Mandat> mandaten = new HashSet<>();

        for (int i = 0; i < mandatenList.getLength(); i++) {
            Node mandateNode = mandatenList.item(i);

            if (mandateNode.getNodeType() == Node.ELEMENT_NODE) {
                Element mandatElement = (Element) mandateNode;

                // Parsen von FromDate
                java.util.Date fromDate2 = StringHelper.dateFormat2.parse(getTextValue(mandatElement, "MDBWP_VON"));
                Date fromDate = new Date(fromDate2.getTime());

                //NullPointerException in toDate
                String toDateText = getTextValue(mandatElement, "MDBWP_BIS");
                java.util.Date toDate2 = null;

                if (toDateText != null && !toDateText.isEmpty()) {
                    toDate2 = StringHelper.dateFormat2.parse(toDateText);
                }

                Date toDate = (toDate2 != null) ? new Date(toDate2.getTime()) : null;

                Set<Fraktion> fraktionSet1 = new Fraktion_File_Impl().getFraktionData(element);
                Set<Ausschuss> ausschussSet1 = new Ausschuss_File_Impl().getAusschussData(element);

                String mandatValue = getTextValue(mandatElement, "MANDATSART");

                Wahlperiode wahlperiode1 = new Wahlperiode_File_Impl(getTextValue(mandatElement, "WP"), fromDate, toDate);
                mandaten.add(new Mandat_File_Impl(fromDate, toDate, fraktionSet1, ausschussSet1 ,mandatValue, wahlperiode1));

            }
        }

        return mandaten;
    }


    @Override
    public Date fromDate() {
        return this.fromDate;
    }

    @Override
    public Date toDate() {
        return this.toDate;
    }

    @Override
    public String getTyp() {
        return this.mandatType;
    }

    @Override
    public Wahlperiode getWahlperiode() {
        return this.wahlperiode;
    }

    @Override
    public Set<Fraktion> getFraktionSet() {
        return this.fraktionSet;
    }

    @Override
    public Set<Ausschuss> getAusschussSet() {
        return this.ausschussSet;
    }

    @Override
    public void setWahlperiode(Wahlperiode wahlperiode) {
        this.wahlperiode = wahlperiode;
    }

    @Override
    public void setFraktionSet(Set<Fraktion> fraktionSet) {
        this.fraktionSet = fraktionSet;
    }

    @Override
    public void setAusschussSet(Set<Ausschuss> ausschussSet) {
        this.ausschussSet = ausschussSet;
    }
}
