package org.texttechnologylab.project.parliament_browser_01_montag_2.api.controllers;

import freemarker.template.Configuration;
import org.texttechnologylab.project.parliament_browser_01_montag_2.data.impl.factory.InsightBundestagFactory;
import org.texttechnologylab.project.parliament_browser_01_montag_2.data.impl.factory.InsightBundestagFactory_Impl;
import spark.ModelAndView;
import spark.Spark;
import spark.template.freemarker.FreeMarkerEngine;

import java.util.Map;

/**
 * Suche nur für den Redetext
 * @author Gurpreet Singh
 */
public class SearchController {
    private static InsightBundestagFactory bundestagFactory;

    /**
     * Constructor für den SearchController
     * @throws Exception
     */
    public SearchController() throws Exception {
        bundestagFactory = new InsightBundestagFactory_Impl();
    }

    /**
     *Init methode, um die Such Eingabe in die Db zu suchen und dann entsprechend ftl zu laden
     * @param configuration free marker config
     */
    public void init(Configuration configuration){

        // Route, um die generische such funktion zu mappen(Postmethode)
        Spark.post("/search", (req, res) -> {

            String query = req.queryParams("query");
            Map<String, Object> model = bundestagFactory.search(query);
            model.put("query", query);

            return new ModelAndView(model, "search-result.ftl");
        }, new FreeMarkerEngine(configuration));
    }


}
