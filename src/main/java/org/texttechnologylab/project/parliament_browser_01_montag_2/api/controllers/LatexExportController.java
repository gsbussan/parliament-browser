package org.texttechnologylab.project.parliament_browser_01_montag_2.api.controllers;

import freemarker.template.Configuration;
import org.texttechnologylab.project.parliament_browser_01_montag_2.data.Protokoll;
import org.texttechnologylab.project.parliament_browser_01_montag_2.data.Rede;
import org.texttechnologylab.project.parliament_browser_01_montag_2.data.impl.factory.InsightBundestagFactory;
import org.texttechnologylab.project.parliament_browser_01_montag_2.data.impl.factory.InsightBundestagFactory_Impl;
import spark.ModelAndView;
import spark.Spark;
import spark.template.freemarker.FreeMarkerEngine;

import java.io.*;
import java.util.*;

/**
 * Klassenimplementierung, um die Routen zum Protokoll-Export zu definieren
 * @author Gurpreet Singh
 */
public class LatexExportController {
    private static FreeMarkerEngine freeMarkerEngine = new FreeMarkerEngine();
    private static InsightBundestagFactory factory;

    /**
     * Constructor für den LatexController
     * @throws Exception
     */
    public LatexExportController() throws Exception {
        factory = new InsightBundestagFactory_Impl();
    }

    /**
     * Init methode, um die Routen zu initialisieren
     * @param configuration Free marker configuration
     */
    public void init(Configuration configuration){

        //Route, um den TexFile zu rendern und mit ab in Körper zu schicken
        Spark.get("/render-tex-file", (req, res) -> {

            //Hier werden Protokolle aus der DB geholt
            Set<Protokoll> protocol = factory.getProtokollenFromDB();
            Set<Rede> rede = factory.getRedenBatchFromDB(0, 5);

            // Protokolle nach Sitzungsindex und Datum gruppieren
            Map<String, Set<Protokoll>> protokolleByChapter = new LinkedHashMap<>();
            for (Protokoll protokoll : protocol) {
                String chapterKey = "Sitzungsindex " + protokoll.getSitzungNummer() + " - Datum " + protokoll.getSitzungDatum();
                protokolleByChapter.computeIfAbsent(chapterKey, k -> new HashSet<>()).add(protokoll);
            }

            //Neulich entdeckt dann benötigen wir eigentlich ganz am Ende Freemarker(Configuration) nicht
            freeMarkerEngine.setConfiguration(configuration);

            Map<String, Object> model = new HashMap<>();
            model.put("protocol", protocol);
            model.put("speeches", rede);
            model.put("protokolleByChapter", protokolleByChapter);

            //Render Process Start
            String latexContent = freeMarkerEngine.render(new ModelAndView(model, "latex-template.ftl"));

            //Generierung TexDatei
            try (FileWriter writer = new FileWriter("document.tex")) {
                writer.write(latexContent);
            } catch (IOException e) {
                e.printStackTrace();
            }

            //Für den Schritt muss eigentlich pdflatex bzw. TexLive heruntergeladen werden
            ProcessBuilder pb = new ProcessBuilder("pdflatex", "document.tex");
            pb.directory(new File("."));
            Process process = pb.start();
            process.waitFor();

            //Generierung pdf Datei
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try (FileInputStream fis = new FileInputStream("document.pdf")) {
                byte[] buffer = new byte[1024];
                int length;
                while ((length = fis.read(buffer)) != -1) {
                    baos.write(buffer, 0, length);
                }
            }

            // PDF inhalt als Response schicken
            res.header("Content-Type", "application/pdf");
            res.header("Content-Disposition", "inline; filename=document.pdf");

            return baos.toByteArray();
        });

        // Route zum Anzeigen eines bestimmten Protokolls
        Spark.post("/view-protocol", (req, res) -> {

            // Protokoll-ID aus dem Formular erhalten
            String protocolId = req.queryParams("protocolId");

            Set<Protokoll> protokol = factory.getProtokollById(protocolId);
            Set<Rede> rede = factory.getRedenBatchFromDB(0, 5);

            Map<String, Set<Protokoll>> protokolleByChapter = new LinkedHashMap<>();
            for (Protokoll protokoll : protokol) {
                String chapterKey = "Sitzungsindex " + protokoll.getSitzungNummer() + " - Datum " + protokoll.getSitzungDatum();
                protokolleByChapter.computeIfAbsent(chapterKey, k -> new HashSet<>()).add(protokoll);
            }

            freeMarkerEngine.setConfiguration(configuration);

            Map<String, Object> model = new HashMap<>();
            model.put("protocol", protokol);
            model.put("speeches", rede);
            model.put("protokolleByChapter", protokolleByChapter);

            String latexContent = freeMarkerEngine.render(new ModelAndView(model, "latex-template.ftl"));

            try (FileWriter writer = new FileWriter("document.tex")) {
                writer.write(latexContent);
            } catch (IOException e) {
                e.printStackTrace();
            }

            ProcessBuilder pb = new ProcessBuilder("pdflatex", "document.tex");
            pb.directory(new File("."));
            Process process = pb.start();
            process.waitFor();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try (FileInputStream fis = new FileInputStream("document.pdf")) {
                byte[] buffer = new byte[1024];
                int length;
                while ((length = fis.read(buffer)) != -1) {
                    baos.write(buffer, 0, length);
                }
            }

            res.header("Content-Type", "application/pdf");
            res.header("Content-Disposition", "inline; filename=document.pdf");

            return baos.toByteArray();
        });


        // Endpunkt zum Aufruf des Protokoll-Exports
        Spark.get("/protocol-export", (req, res) -> {
            freeMarkerEngine.setConfiguration(configuration);

            Set<Protokoll> protocol = factory.getProtokollenFromDB();
            Map<String, Object> model = new HashMap<>();
            model.put("protocol", protocol);
            return freeMarkerEngine.render(new ModelAndView(model, "protocol-export.ftl"));
        });

    }
}

