package org.texttechnologylab.project.parliament_browser_01_montag_2.data.impl.file;

import org.texttechnologylab.project.parliament_browser_01_montag_2.data.Fraktion;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.HashSet;
import java.util.Set;

import static org.texttechnologylab.project.parliament_browser_01_montag_2.helper.NodeHelper.getTextValue;

/**
 * Klasse bildet Fraktionen aus den XMLDateien ab.
 * @author Gurpreet Singh
 */
public class Fraktion_File_Impl implements Fraktion {
    private String name;

    /**
     * Constructor
     * @param name FraktionName
     */
    public Fraktion_File_Impl(String name) {
        this.name = name;
    }

    public Fraktion_File_Impl() {

    }

    /**
     * Methode extrahiert Daten aus den XMLNodes raus
     * @param element element
     * @return Set von Fraktionen mit den Namen
     */
    public Set<Fraktion> getFraktionData(Element element) {
        Set<Fraktion> fraktionSet = new HashSet<>();
        NodeList fraktionElements = element.getElementsByTagName("INSTITUTION");

        for (int fraktionIndex = 0; fraktionIndex < fraktionElements.getLength(); fraktionIndex++) {
            Node fraktionNode = fraktionElements.item(fraktionIndex);

            if (fraktionNode.getNodeType() == Node.ELEMENT_NODE) {
                Element fraktionElement = (Element) fraktionNode;
                String fraktionName = getTextValue(fraktionElement, "INS_LANG");
                String inseratLang = getTextValue(fraktionElement, "INSART_LANG");
                if ("Fraktion/Gruppe".equals(inseratLang)) {
                    fraktionSet.add(new Fraktion_File_Impl(fraktionName));
                }
            }
        }

        return fraktionSet;
    }

    @Override
    public String getName() {
        return name;
    }
}
