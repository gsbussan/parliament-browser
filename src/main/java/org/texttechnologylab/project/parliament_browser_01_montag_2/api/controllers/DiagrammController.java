package org.texttechnologylab.project.parliament_browser_01_montag_2.api.controllers;

import com.google.gson.Gson;
import freemarker.template.Configuration;
import org.texttechnologylab.project.parliament_browser_01_montag_2.api.InsightBundestagApi;
import org.texttechnologylab.project.parliament_browser_01_montag_2.data.impl.factory.InsightBundestagFactory;
import org.texttechnologylab.project.parliament_browser_01_montag_2.data.impl.factory.InsightBundestagFactory_Impl;
import spark.ModelAndView;
import spark.Spark;
import spark.template.freemarker.FreeMarkerEngine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Klassenimplementierung, um die Diagramme zu generieren.
 * Wichtig zu wissen: Haben hier mit der Gson Bibliothek gearbeitet
 * @author Taylan Özcelik
 */
public class DiagrammController {
    private static InsightBundestagFactory bundestagFactory;

    /**
     * Constructor für den DiagrammController
     * @throws Exception Exception
     */
    public DiagrammController() throws Exception {
        this.bundestagFactory = new InsightBundestagFactory_Impl();
    }

    /**
     * Init methode, um die Routen zu initialisieren
     */
    public void init(){

        Configuration configuration = new Configuration(Configuration.VERSION_2_3_32);
        configuration.setClassForTemplateLoading(InsightBundestagApi.class, "/templates/diagrams");

        Spark.get("/diagram", (req, res)-> new ModelAndView(new HashMap<>(), "date-selection.ftl"), new FreeMarkerEngine(configuration));

        //Route, um den Barchart zu generieren.
        Spark.get("/bar-chart", (req, res) -> {
            String startDate = req.queryParams("startDate");
            String endDate = req.queryParams("endDate");

            if (startDate == null || endDate == null || startDate.isEmpty() || endDate.isEmpty()) {
                return new ModelAndView(new HashMap<>(), "date-selection.ftl");
            } else {
                Map<String, Integer> barChartData = bundestagFactory.getBarChartData("rede", startDate, endDate);

                Gson gson = new Gson();
                String jsonBarChartData = gson.toJson(barChartData);
                HashMap<String, Object> model = new HashMap<>();
                model.put("barChartCounts", jsonBarChartData);
                return new ModelAndView(model, "bar-chart.ftl");
            }

        }, new FreeMarkerEngine(configuration));

        //Route, um den Speaker Bar Chat zu generieren
        Spark.get("/speaker-bar-chart", (req, res) -> {
            String startDate = req.queryParams("startDate");
            String endDate = req.queryParams("endDate");

            if (startDate == null || endDate == null || startDate.isEmpty() || endDate.isEmpty()) {
                return new ModelAndView(new HashMap<>(), "date-selection.ftl");
            } else {

                Map<String, Map<String, Object>> speakerDetails = bundestagFactory.countSpeechesBySpeakerWithImages("rede", "abgeordneter", startDate, endDate);

                Gson gson = new Gson();
                String jsonSpeakerDetails = gson.toJson(speakerDetails);

                Map<String, Object> model = new HashMap<>();
                model.put("speakerDetails", jsonSpeakerDetails);
                return new ModelAndView(model, "speaker-bar-chart.ftl");
            }
        }, new FreeMarkerEngine(configuration));

        //Route, um den Bubble Chart zu generieren
        Spark.get("/bubble-chart", (req, res) -> {
            String startDate = req.queryParams("startDate");
            String endDate = req.queryParams("endDate");

            if (startDate == null || endDate == null || startDate.isEmpty() || endDate.isEmpty()) {
                return new ModelAndView(new HashMap<>(), "date-selection.ftl");
            } else {

                Map<String, Integer> namedEntitiesCount = bundestagFactory.countNamedEntities("rede", startDate, endDate);

                Gson gson = new Gson();
                String jsonNamedEntitiesCount = gson.toJson(namedEntitiesCount);

                Map<String, Object> model = new HashMap<>();
                model.put("namedEntitiesCount", jsonNamedEntitiesCount);
                return new ModelAndView(model, "bubble-chart.ftl");
            }
        }, new FreeMarkerEngine(configuration));

        //Route, um den Sentiment Radar Chart zu generieren
        Spark.get("/sentiment-radar-chart", (req, res) -> {

            String startDate = req.queryParams("startDate");
            String endDate = req.queryParams("endDate");

            if (startDate == null || endDate == null || startDate.isEmpty() || endDate.isEmpty()) {
                return new ModelAndView(new HashMap<>(), "date-selection.ftl");
            } else {
                Map<String, Double> sentimentAverages = bundestagFactory.calculateSentimentAverages("rede", startDate, endDate);

                Gson gson = new Gson();
                String jsonSentimentAverages = gson.toJson(sentimentAverages);

                Map<String, Object> model = new HashMap<>();
                model.put("sentimentAverages", jsonSentimentAverages);
                return new ModelAndView(model, "sentiment-radar-chart.ftl");
            }
        }, new FreeMarkerEngine(configuration));

        //Route, um den Sunburst Topic zu generieren
        Spark.get("/sunburst-topic", (req, res) -> {
            String startDate = req.queryParams("startDate");
            String endDate = req.queryParams("endDate");

            if (startDate == null || endDate == null || startDate.isEmpty() || endDate.isEmpty()) {
                return new ModelAndView(new HashMap<>(), "data-selection.ftl");
            } else {
                Map<String, Object> sunburstData = bundestagFactory.processDocumentsForSunburst("rede", startDate, endDate);

                Gson gson = new Gson();
                String jsonSunburstData = gson.toJson(sunburstData);

                HashMap<String, Object> model = new HashMap<>();
                model.put("topicDistribution", jsonSunburstData);

                return new ModelAndView(model, "sunburst-topic.ftl");
            }
        }, new FreeMarkerEngine(configuration));

        //Route, um mit Swagger io zu interagieren
        Spark.get("/swagger.json", (req, res) -> {
            res.type("application/json");
            try (InputStream is = getClass().getResourceAsStream("/swagger.json");
                 BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }
                return stringBuilder.toString();
            } catch (IOException e) {
                res.status(404);
                return "Datei nicht gefunden";
            }
        });

    }

}
