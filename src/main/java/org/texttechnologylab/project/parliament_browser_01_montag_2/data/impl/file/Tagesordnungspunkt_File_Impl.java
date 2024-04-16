package org.texttechnologylab.project.parliament_browser_01_montag_2.data.impl.file;

import org.texttechnologylab.project.parliament_browser_01_montag_2.data.Rede;
import org.texttechnologylab.project.parliament_browser_01_montag_2.data.Tagesordnungspunkt;
import org.texttechnologylab.project.parliament_browser_01_montag_2.data.impl.factory.InsightBundestagFactory;
import org.texttechnologylab.project.parliament_browser_01_montag_2.exceptions.InvalidFileTypeException;
import org.texttechnologylab.project.parliament_browser_01_montag_2.exceptions.InvalidInputException;
import org.texttechnologylab.project.parliament_browser_01_montag_2.helper.DirectoryReader;
import org.texttechnologylab.project.parliament_browser_01_montag_2.helper.XMLReader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import static org.texttechnologylab.project.parliament_browser_01_montag_2.helper.NodeHelper.getAttributeValue;
import static org.texttechnologylab.project.parliament_browser_01_montag_2.helper.XMLReader.processDocument;

/**
 * Klasse bildet Tagesordnungspunkte aus XML-Dateien ab.
 * @author Mihai Paun
 */
public class Tagesordnungspunkt_File_Impl implements Tagesordnungspunkt {
    private String topId;
    private Set<Rede> redeList;
    private Set<Object> objectSet;

    /**
     * Constructor 1
     * Initialisiert Hashset, um die Objekte der Tagesordnungspunkte hinzuzufügen.
     */
    public Tagesordnungspunkt_File_Impl() {
        objectSet = new HashSet<>(0);
    }

    /**
     * Constructor 2
     * @param topId Tagesordnungspunkt ID
     */
    public Tagesordnungspunkt_File_Impl(String topId, Set<Rede> redeList) {
        this.topId = topId;
        this.redeList = redeList;
    }

    /**
     * Constructor 3 für Factory-Implementierung
     * @param factory Factory
     */
    public Tagesordnungspunkt_File_Impl(InsightBundestagFactory factory) {
        super();
    }

    /**
     * Methode verarbeitet das DOM Document, gibt Tagesordnungspunkten zurück
     * @param documents Liste von Dokumenten
     * @return Liste von Objekten
     */
    public Set<Object> getTagesordnungspunkteData(Set<Document> documents) {
        for (Document document : documents) {
            processDocument(document, "tagesordnungspunkt", objectSet, getTagesordnungsFunction);
        }
        return objectSet;
    }

    /**
     * Methode nutzt Element, auf individuelle Textinhalte zuzugreifen
     * @param tElement Element des MDB XMLNodes
     * @return Object Abgeordneter
     */
    public Tagesordnungspunkt getTagesordnungspunkte(Element tElement){
        this.topId = getAttributeValue(tElement, "top-id");

        //Reden werden hier verarbeitet
        Set<Rede> redeSet = new HashSet<>(0);
        NodeList rede = tElement.getElementsByTagName("rede");

        for(int i =0;i<rede.getLength();i++) {

            Node redeNode = rede.item(i);
            if (redeNode.getNodeType() == Node.ELEMENT_NODE) {

                Element redeElement = (Element) redeNode;
                redeSet.add(new Rede_File_Impl().getReden(redeElement));
            }
        }

        return new Tagesordnungspunkt_File_Impl(topId, redeSet);
    }
    Function<Element, Object> getTagesordnungsFunction = this::getTagesordnungspunkte;

    @Override
    public String getTopId() {
        return topId;
    }

    @Override
    public Set<Rede> getReden() {
        return redeList;
    }

}

