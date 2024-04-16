package org.texttechnologylab.project.parliament_browser_01_montag_2.data.impl.file;

import org.texttechnologylab.project.parliament_browser_01_montag_2.data.Ausschuss;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.HashSet;
import java.util.Set;
/**
 * Klasse bildet Ausschüsse-Daten aus XMLDateien ab.
 * @author Taylan Özcelik
 */

import static org.texttechnologylab.project.parliament_browser_01_montag_2.helper.NodeHelper.getTextValue;

public class Ausschuss_File_Impl implements Ausschuss {
    private String label;
    public Ausschuss_File_Impl(String ausschussName) {
        this.label = ausschussName;
    }

    public Ausschuss_File_Impl() {

    }

    /**
     * Methode extrahiert Daten aus den XMLNodes raus
     * @param element element
     * @return Set von Ausschüssen mit den Namen
     */
    public Set<Ausschuss> getAusschussData(Element element) {
        Set<Ausschuss> ausschussSet = new HashSet<>();
        NodeList ausschussElements = element.getElementsByTagName("INSTITUTION");

        for (int ausschussIndex = 0; ausschussIndex< ausschussElements.getLength(); ausschussIndex++) {
            Node ausschussNode = ausschussElements.item(ausschussIndex);

            if (ausschussNode.getNodeType() == Node.ELEMENT_NODE) {
                Element ausschussElement = (Element) ausschussNode;
                String ausschussName = getTextValue(ausschussElement, "INS_LANG");
                String inseratLang = getTextValue(ausschussElement, "INSART_LANG");
                if ("Ausschuss".equals(inseratLang)){
                    ausschussSet.add(new Ausschuss_File_Impl(ausschussName));
                }
            }
        }

        return ausschussSet;
    }

    @Override
    public String getName() {
        return this.label;
    }
}
