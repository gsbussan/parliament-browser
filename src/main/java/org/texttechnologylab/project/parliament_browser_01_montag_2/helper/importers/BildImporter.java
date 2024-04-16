package org.texttechnologylab.project.parliament_browser_01_montag_2.helper.importers;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.texttechnologylab.project.parliament_browser_01_montag_2.data.Bild;
import org.texttechnologylab.project.parliament_browser_01_montag_2.data.impl.file.Bild_File_Impl;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Hilfsklasse, um die Bilder der Abgeordneten aus der Bundestag Bilderdatenbank herunterzuladen
 * @author Taylan Özcelik
 */
public class BildImporter {
    /**
     * Durchführung des Imports
     */
    public Set<Bild> importBilder(String abgeordneterName) throws IOException {

        Set<Bild> bildSet = new HashSet<>(0);

        String baseUrl = "https://bilddatenbank.bundestag.de/search/picture-result?query=";
        String url = baseUrl + abgeordneterName;

        Document document = Jsoup.connect(url).get();
        Elements elements = document.select("a[data-fancybox='group']");
        for(Element element: elements){
            String caption = element.outerHtml();
            if (caption.contains(abgeordneterName)) {

                String metadaten = extractMetadata(caption);
                Element imgElement = element.select("img").first();
                if (imgElement != null) {
                    String imageUrlVonHtml = imgElement.attr("src");
                    String imageUrl = "https://bilddatenbank.bundestag.de" + imageUrlVonHtml;
                    bildSet.add(new Bild_File_Impl(metadaten, imageUrl));
                }

            }
        }
        return bildSet;
    }

    /**
     * Extrahieren der Metadaten
     * @param caption
     * @return
     */
    private String extractMetadata(String caption) {
        int startIndex = caption.indexOf("<div class='col-md-9 col-sm-12'>");
        if (startIndex == -1) {
            return "";
        }
        int endIndex = caption.indexOf("Herunterladen");
        if (endIndex == -1) {
            return "";
        }

        String metadata = caption.substring(startIndex, endIndex).trim();
        metadata = Jsoup.parse(metadata).text();
        //hier wird lediglich die Form der Metadaten angepasst, damit diese vom Aufbau her denen auf der Webseite gleichen
        metadata = metadata.replace("Abgebildete Personen:", System.lineSeparator() + "Abgebildete Personen:");
        metadata = metadata.replace("Abgebildete Person:", System.lineSeparator() + "Abgebildete Person:");
        metadata = metadata.replace("Ort:", System.getProperty("line.separator") + "Ort:");
        metadata = metadata.replace("Aufgenommen:", System.getProperty("line.separator") + "Aufgenommen:");
        metadata = metadata.replace("Bildnummer:", System.getProperty("line.separator") + "Bildnummer:");
        metadata = metadata.replace("Fotograf/in:", System.getProperty("line.separator") + "Fotograf/in:");
        metadata = metadata.replace("Dieses Bild", System.getProperty("line.separator") + "Dieses Bild");

        return metadata;
    }


}
