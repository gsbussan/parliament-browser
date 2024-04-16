package org.texttechnologylab.project.parliament_browser_01_montag_2.api;

import freemarker.template.Configuration;
import org.texttechnologylab.project.parliament_browser_01_montag_2.api.controllers.*;
import spark.Spark;

/**
 * Main klasse, um alle Controllers zu starten
 * @author Gurpreet Singh
 * @author Mihai Paun
 * @author Taylan Özcelik
 */
public class InsightBundestagApi {

    private static final HomeController homeController = new HomeController();
    private static final DownloadFilesController filesController = new DownloadFilesController();
    private static final SearchController searchController;

    static {
        try {
            searchController = new SearchController();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static final DiagrammController diagrammController;

    static {
        try {
            diagrammController = new DiagrammController();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static final RedenController redenController;

    static {
        try {
            redenController = new RedenController();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static final AdminController adminController;

    static {
        try {
            adminController = new AdminController();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static final LatexExportController latexController;

    static {
        try {
            latexController = new LatexExportController();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Main Implementierung
     * @param args
     */
    public static void main(String[] args) {

        //Sparkport
        Spark.port(4567);

        //Swagger UI Dokumentation erforderliche Dateiein
        Spark.staticFiles.location("/swagger-ui-master/dist");

        //Free marker Konfiguration
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_32);
        configuration.setClassForTemplateLoading(InsightBundestagApi.class, "/templates");

        //HomeController(Port: 4567/home)
        homeController.init(configuration);

        //FilesController Initialisierung
        filesController.init(configuration);

        // SearchController Initialisierung
        searchController.init(configuration);

        // RedenController Initialisierung
        redenController.init(configuration);

        //DiagrammController Initialisierung
        diagrammController.init();

        //AdminController Initialisierung
        adminController.init(configuration);

        //LatexController Initialisierung
        latexController.init(configuration);

    }



}