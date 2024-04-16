package org.texttechnologylab.project.parliament_browser_01_montag_2.api.controllers;

import freemarker.template.Configuration;
import spark.ModelAndView;
import spark.Spark;
import spark.template.freemarker.FreeMarkerEngine;

import java.util.HashMap;
import java.util.Map;

/**
 * Klassenimplementierung, um die Home Seite zu mappen, mittels Java Spark+
 * @author Gurpreet Singh
 */
public class HomeController {

    /**
     * Hier wird der Controller mit dem Endpunkt einfach initialisiert
     */
    public void init(Configuration configuration){

        Spark.get("/", (request, response) -> {
            Map<String, Object> attributes = new HashMap<>();
            return new ModelAndView(attributes, "home.ftl");
        }, new FreeMarkerEngine(configuration));

        Spark.get("/home", (request, response) -> {
            Map<String, Object> attributes = new HashMap<>();
            return new ModelAndView(attributes, "home.ftl");
        }, new FreeMarkerEngine(configuration));
    }
}
