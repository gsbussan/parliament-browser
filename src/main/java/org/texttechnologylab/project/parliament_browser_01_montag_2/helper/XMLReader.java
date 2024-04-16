package org.texttechnologylab.project.parliament_browser_01_montag_2.helper;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
/**
 * Klasse verantwortlich für das Parsen der XMLDateien
 * @author Gurpreet Singh
 */
public class XMLReader {

    Set<Document> documentList;
    public XMLReader() {
        documentList = new HashSet<>(0);
    }

    /**
     * Methode parst eine Datei, und gibt das DOM Dokument zurück
     * @param fileList Liste aller XML Dateien
     * @return Liste von DOM Document
     * @throws IOException exception1
     * @throws SAXException exception2
     * @throws ParserConfigurationException exception3
     */
    public Set<Document> parseFiles(Set<File> fileList) throws IOException, SAXException, ParserConfigurationException {

        for (File file: fileList){

            //DOM-Parser für XML-Dateien
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(file);
            doc.getDocumentElement().normalize();

            documentList.add(doc);
        }
        return  documentList;
    }

    /**
     * Verarbeitet das Document, um allgemein eine Liste der Objekte auszugeben,
     * die entsprechend instanziiert werden kann
     * Bsp. Abgeordneter abgeordneten = (Abgeordneter) listObject.get(i);
     * @param document DOM Document
     * @param tagName Name des Tags
     * @param listObject Liste der Objekte
     * @param function eine Funktion, wo element angewandt wird
     */
    public static void processDocument(Document document, String tagName, Set<Object> listObject, Function<Element, Object> function){
        NodeList nList = document.getElementsByTagName(tagName);

        if (nList != null && nList.getLength() > 0) {
            for (int temp = 0; temp < nList.getLength(); temp++) {

                Node nNode = nList.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element solEl = (Element) nList.item(temp);
                    Object o = function.apply(solEl);
                    listObject.add(o);

                }
            }
        }
    }



}
