package org.texttechnologylab.project.parliament_browser_01_montag_2.data.impl.file;

import org.apache.uima.UIMAException;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.jcas.JCas;
import org.texttechnologylab.project.parliament_browser_01_montag_2.data.Abgeordneter;
import org.texttechnologylab.project.parliament_browser_01_montag_2.data.Kommentar;
import org.texttechnologylab.project.parliament_browser_01_montag_2.data.Partei;
import org.texttechnologylab.project.parliament_browser_01_montag_2.data.Rede;
import org.texttechnologylab.project.parliament_browser_01_montag_2.data.impl.factory.InsightBundestagFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.sql.Date;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import static org.texttechnologylab.project.parliament_browser_01_montag_2.helper.NodeHelper.getAttributeValue;
import static org.texttechnologylab.project.parliament_browser_01_montag_2.helper.NodeHelper.getTextValue;
import static org.texttechnologylab.project.parliament_browser_01_montag_2.helper.StringHelper.dateFormat2;
import static org.texttechnologylab.project.parliament_browser_01_montag_2.helper.XMLReader.processDocument;

/**
 * Klasse bildet Reden aus XML-Dateien ab.
 * @author Gurpreet Singh
 * @author Mihai Paun
 * @autor Toylan Özcelik
 */
public class Rede_File_Impl implements Rede {
    private String redeId;
    private Abgeordneter redner;
    private String text;
    private Set<Kommentar> kommentarSet;
    private Date redeDatum;
    private Set<Object> objectSet;
    private JCas pCas;

    /**
     * Constructor 1
     * Initialisiert Hashset, um die Objekte der Reden hinzuzufügen.
     */
    public Rede_File_Impl() {
        objectSet = new HashSet<>(0);
    }

    /**
     * Constructor 2
     */
    public Rede_File_Impl(String redeId, Abgeordneter redner, String text , Set<Kommentar> kommentarSet) {
        this.redeId = redeId;
        this.redner = redner;
        this.text = text;
        this.kommentarSet = kommentarSet;
    }

    /**
     * Constructor 3 für Factory-Implementierung
     */
    public Rede_File_Impl(InsightBundestagFactory factory) {
        super();
    }

    public Rede_File_Impl(String id, String text) {
        this.redeId = id;
        this.text = text;
    }

    /**
     * Methode verarbeitet das DOM Document, gibt Reden und RedeDatum zurück
     * @param documentSet Liste von Dokumenten
     * @return Liste von Objekten
     */
    public Set<Object> getRedenData(Set<Document> documentSet) throws ParseException {
        for (Document document : documentSet) {

            // Datum einer Rede später für Abfragen nützlich.
            NodeList date = document.getElementsByTagName("datum");

            for(int i =0;i<date.getLength();i++){

                Node rednerNode = date.item(i);
                if (rednerNode.getNodeType() == Node.ELEMENT_NODE) {

                    Element dateElement = (Element) rednerNode;

                    java.util.Date date2 = dateFormat2.parse(getAttributeValue(dateElement, "date"));
                    this.redeDatum = new Date(date2.getTime());

                }
            }
            processDocument(document, "rede", objectSet, getRedenFunction);
        }
        return objectSet;
    }

    /**
     * Methode nutzt Element, auf individuelle Textinhalte zuzugreifen
     * @param element Element der XMLNodes
     */
    public Rede getReden(Element element) {
        this.redeId = getAttributeValue(element, "id");

        // Parsen von child elements der <rede>
        NodeList childNodes = element.getChildNodes();

        this.kommentarSet = new HashSet<>(0);
        Set<String> paragraphs = new HashSet<>(0);

        for (int j = 0; j < childNodes.getLength(); j++) {
            Node childNode = childNodes.item(j);

            if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                Element childElement = (Element) childNode;

                // Verschiedene Elemente betrachten
                String tagName = childElement.getTagName();
                String textContent = childElement.getTextContent();

                // Verschiedene Klassen betrachten
                String pKlasse = getAttributeValue(childElement, "klasse");



                //Redner aus den Rede Nodes holen
                if ("redner".equals(pKlasse)){
                    NodeList rednerList = childElement.getElementsByTagName("redner");
                    for(int rIndex = 0; rIndex<rednerList.getLength();rIndex++){
                        Node rNode = rednerList.item(rIndex);
                        if(rNode.getNodeType() == Node.ELEMENT_NODE){
                            Element rednerElement = (Element) rNode;
                            String rednerId = getAttributeValue(rednerElement, "id");

                            Partei partei = new Partei_File_Impl(getTextValue(rednerElement, "fraktion"));

                            // Redner erstellen
                            this.redner = new Abgeordneter_File_Impl(partei,
                                    rednerId,getTextValue(rednerElement, "titel"),
                                    getTextValue(rednerElement, "vorname"),
                                    getTextValue(rednerElement, "nachname"),
                                    redeDatum);

                        }
                    }
                }


                // Kommentare aus den Rede Nodes holen
                else if ("kommentar".equals(tagName)){
                    kommentarSet.add(new Kommentar_File_Impl(textContent));
                }

                else  {
                    List<String> comments = getCommentsForElement(childElement);
                    paragraphs.add(textContent);
                    paragraphs.add(comments.toString());
                    this.text = String.join(" ", paragraphs);
                }


            }
        }

        return new Rede_File_Impl(redeId, redner,text, kommentarSet);
    }

    /**
     * Hilfsmethode, um die Kommentare an die zugehörigen Stellen zu markieren.
     * @param element
     * @return
     */
    public static List<String> getCommentsForElement(Element element) {
        List<String> comments = new ArrayList<>();
        Node node = element.getNextSibling();
        while (node != null) {
            if (node.getNodeType() == Node.ELEMENT_NODE && node.getNodeName().equals("p")) {
                // If the next node is another <p> element, stop collecting comments
                break;
            } else if (node.getNodeType() == Node.ELEMENT_NODE && node.getNodeName().equals("kommentar")) {
                String commentText = node.getTextContent().trim();
                comments.add(commentText);
            }
            node = node.getNextSibling();
        }
        return comments;
    }

    Function<Element, Object> getRedenFunction = this::getReden;

    @Override
    public String getId() {
        return redeId;
    }

    @Override
    public Abgeordneter getRedner() {
        return redner;
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public Set<Kommentar> getKommentar() {
        return kommentarSet;
    }

    @Override
    public JCas toCAS() throws UIMAException {
        this.pCas = JCasFactory.createText(this.text, "de");
        return this.pCas;
    }
}
