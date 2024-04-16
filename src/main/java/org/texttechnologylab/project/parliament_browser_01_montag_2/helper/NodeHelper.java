package org.texttechnologylab.project.parliament_browser_01_montag_2.helper;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Klasse um Daten aus XMLNodes zu extrahieren
 * @author Gurpreet Singh
 */
public class NodeHelper {
    /**
     * Method gibt den Inhalt eines Elements zurück
     * @param element DOM Element
     * @param tagName tagName
     * @return Inhalt
     */
    public static String getTextValue(Element element, String tagName) {
        String textVal = null;
        NodeList nl = element.getElementsByTagName(tagName);

        if (nl.getLength() > 0) {
            Element el = (Element) nl.item(0);
            Node firstChild = el.getFirstChild();

            //Prüfen, ob das Child Node ist nicht null
            if (firstChild != null) {
                textVal = firstChild.getTextContent();
            }
        }

        return textVal;
    }

    /**
     * Methode gibt den Inhalt der Attribute eines DOM Elements zurück
     * @param element DOM Element
     * @param attributeName attributeName
     * @return Textinhalt der Attribute
     */
    public static String getAttributeValue(Element element, String attributeName) {
        if (element.hasAttribute(attributeName)) {
            return element.getAttribute(attributeName);
        }
        return null;
    }
}