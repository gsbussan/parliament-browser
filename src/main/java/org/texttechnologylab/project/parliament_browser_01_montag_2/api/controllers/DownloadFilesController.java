package org.texttechnologylab.project.parliament_browser_01_montag_2.api.controllers;

import freemarker.template.Configuration;
import org.texttechnologylab.project.parliament_browser_01_montag_2.exceptions.InvalidInputException;
import org.texttechnologylab.project.parliament_browser_01_montag_2.helper.importers.RedenImporter;
import spark.ModelAndView;
import spark.Spark;
import spark.template.freemarker.FreeMarkerEngine;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Klassenimplementierung, um den Fortschrittsanzeige bei dem Herunterladen der Dateien anzuzeigen.
 * @author Gurpreet Singh
 */
import static org.texttechnologylab.project.parliament_browser_01_montag_2.helper.importers.RedenImporter.*;
public class DownloadFilesController {
    private static final RedenImporter rImporter;

    static {
        rImporter = new RedenImporter();
    }

    //Volatile Variablen, denn sie werden sich im asynchronischen Prozess von dem Herunterladen immer ändern
    private static volatile boolean isDownloading = false;
    private static Thread downloadThread;

    /**
     * Init methode, um die Routen zu initialisieren
     * @param configuration Freemarker Configuration
     */
    public void init(Configuration configuration){

            //Fortschritt
            Spark.get("/progress", (req, res) -> progress);

            //Filename
            Spark.get("/file-name", (req, res) -> xmlFileName);

            //Boolean wert, um herauszufinden, ob im Backend das Download angehalten wird oder nicht
            Spark.get("/download-files", (req, res) -> {
                Map<String, Object> model = new HashMap<>();
                model.put("isDownloading", isDownloading);
                return new ModelAndView(model, "download-files.ftl");
            }, new FreeMarkerEngine(configuration));

            //Download Start mittels Reden Importer
            Spark.get("/start-download", (req, res) -> {
                isDownloading = true;
                downloadThread = new Thread(() -> {
                    try {
                        rImporter.init();
                    } catch (IOException | InvalidInputException e) {
                        throw new RuntimeException(e);
                    }
                    isDownloading = false;
                });
                downloadThread.start();
                return "{\"download complete\": " + progress + "}";
            });


            //Download Stop mittels Thread Verarbeitung
            Spark.get("/stop-download", (req, res) -> {
                if (downloadThread != null && downloadThread.isAlive()) {
                    downloadThread.interrupt();
                }
                return "{\"download stopped\": " + progress + "}";
            });
        }
}
