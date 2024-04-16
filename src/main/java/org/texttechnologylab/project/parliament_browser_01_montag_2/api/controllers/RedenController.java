package org.texttechnologylab.project.parliament_browser_01_montag_2.api.controllers;

import freemarker.template.Configuration;
import org.texttechnologylab.project.parliament_browser_01_montag_2.data.Abgeordneter;
import org.texttechnologylab.project.parliament_browser_01_montag_2.data.Rede;
import org.texttechnologylab.project.parliament_browser_01_montag_2.data.impl.factory.InsightBundestagFactory;
import org.texttechnologylab.project.parliament_browser_01_montag_2.data.impl.factory.InsightBundestagFactory_Impl;
import org.texttechnologylab.project.parliament_browser_01_montag_2.data.impl.mongodb.Rede_MongoDB_Impl;
import spark.ModelAndView;
import spark.Spark;
import spark.template.freemarker.FreeMarkerEngine;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Klassenimplementierung, um die Routen bezüglich Reden zu mappen
 * @author Mihai Paun
 */
public class RedenController {
    private static InsightBundestagFactory bundestagFactory;

    /**
     * Constructor für den RedenController
     * @throws Exception
     */
    public RedenController() throws Exception {
        bundestagFactory = new InsightBundestagFactory_Impl();
    }

    /**
     * Init methode, um die Reden zu initialisieren
     * @param configuration free marker config
     */
    public void init(Configuration configuration) {
        // saving the route if we redirect to /createUser
        Spark.before("/redenHome", (request, response) -> {
            request.session().attribute("previousPage", request.pathInfo());
        });

        Spark.get("/redenHome", (request, response) -> {
            // init attributes Map
            Map<String, Object> attributes = new HashMap<>();

            // check if there is any logged-in user
            Boolean loggedIn = request.session().attribute("loggedIn");
            // if loggedIn is null, then no user is logged-in xD
            if (loggedIn != null) {
                attributes.put("loggedIn", true);
            } else {
                attributes.put("loggedIn", false);
            }

            return new ModelAndView(attributes, "redenHome.ftl");
        }, new FreeMarkerEngine(configuration));

        // saving the route if we redirect to /createUser
        Spark.before("/reden", (request, response) -> {
            request.session().attribute("previousPage", request.pathInfo());
        });

        Spark.get("/reden", (request, response) -> {
            Map<String, Object> attributes = new HashMap<>();
            Set<Rede> redeSet = bundestagFactory.getRedenBatchFromDB(0, 10);
            Set<Rede_MongoDB_Impl> rMongoSet = new HashSet<>();
            List<Abgeordneter> abgeordneterList = new ArrayList<>();
            List<String> parteiNamen = new ArrayList<>();
            List<String> fractionNamen = new ArrayList<>();
            List<String> picURLs = new ArrayList<>();
            String abgeordneterID = "";

            for (Rede r : redeSet) {
                Rede_MongoDB_Impl redeMongoDB = (Rede_MongoDB_Impl) r;
                abgeordneterID = bundestagFactory.getSpeakerIDFromBackup(r.getId());
                List<Abgeordneter> aList = bundestagFactory.getAbgeordneteFromDB(abgeordneterID).stream().collect(Collectors.toList());
                Abgeordneter abgeordneter = null;
                String parteiName = "";
                String picUrl = "";
                if (!aList.isEmpty()) {
                    abgeordneter = aList.get(0);
                    parteiName = abgeordneter.getPartei().getName();
                    picUrl = bundestagFactory.getPictureURL(abgeordneterID); // accessing the pic url
                    abgeordneterList.add(abgeordneter);
                }
                picURLs.add(picUrl);
                if (!parteiName.isEmpty()) {
                    parteiNamen.add(parteiName);
                } else {
                    parteiNamen.add("Parteilos");
                }
                String fractionName = bundestagFactory.getFraktionName(abgeordneterID);
                if (!fractionName.isEmpty()) {
                    fractionNamen.add(fractionName);
                } else {
                    fractionNamen.add("Fraktionslos");
                }
                rMongoSet.add(redeMongoDB);

                // getting the map of comments and positions
                Map<Integer, String> commentsMap = redeMongoDB.getCommentsMap();
            }

            // check if there is any logged-in user
            Boolean loggedIn = request.session().attribute("loggedIn");
            // if loggedIn is null, then no user is logged-in xD
            if (loggedIn != null) {
                attributes.put("loggedIn", true);
            } else {
                attributes.put("loggedIn", false);
            }

            // adding the data to the attributes map
            attributes.put("redenListe", rMongoSet);
            attributes.put("abgeordneterList", abgeordneterList);
            attributes.put("picURLs", picURLs);
            attributes.put("parteiNamen", parteiNamen);
            attributes.put("fraktionNamen", fractionNamen);
            return new ModelAndView(attributes, "redenBatch.ftl");
        }, new FreeMarkerEngine(configuration));

        // saving the route if we redirect to /createUser
        Spark.before("/reden/:redeId", (request, response) -> {
            request.session().attribute("previousPage", request.pathInfo());
        });

        //Get the Reden by id
        Spark.get("/reden/:redeId", (request, response) -> {
            String redeId = request.params(":redeId");
            redeId = redeId.replace(":", "");
            List<Rede> matchingReden = bundestagFactory.getRedenFromDB(redeId).stream().collect(Collectors.toList());
            List<Rede_MongoDB_Impl> mongoReden = new ArrayList<>();
            List<Abgeordneter> abgeordneterList = new ArrayList<>();
            String abgeordneterID = "";
            Map<Integer, String> commentsMap = new HashMap<>();

            for (Rede r : matchingReden) {
                Rede_MongoDB_Impl mongoRede = (Rede_MongoDB_Impl) r;
                abgeordneterID = bundestagFactory.getSpeakerIDFromBackup(r.getId());
                mongoReden.add(mongoRede);
                commentsMap = mongoRede.getCommentsMap();
//                inspectCharacters(mongoRede.getText());
            }

            if (matchingReden.isEmpty()) {
                Map<String, Object> notExistsAttributes = new HashMap<>();
                notExistsAttributes.put("message", "ID does not exist");
                return new ModelAndView(notExistsAttributes, "id_missing.ftl");
            } else {
                Map<String, Object> attributes = new HashMap<>();

                // getting all the data we need
                abgeordneterList = bundestagFactory.getAbgeordneteFromDB(abgeordneterID).stream().collect(Collectors.toList());
                String picUrl = bundestagFactory.getPictureURL(abgeordneterID); // accessing the pic url
                Abgeordneter abgeordneter = abgeordneterList.get(0);
                String parteiName = abgeordneter.getPartei().getName();
                String fractionName = bundestagFactory.getFraktionName(abgeordneterID);
                Map<String, String> strCommentsMap = new HashMap<>();
                for (Map.Entry<Integer, String> entry : commentsMap.entrySet()) {
                    strCommentsMap.put(String.valueOf(entry.getKey()), entry.getValue());
                }

                String potentialFirstComment = null;
                if (strCommentsMap.containsKey("-1")) {
                    potentialFirstComment = strCommentsMap.get("-1");
                }


                // check if there is any logged-in user
                Boolean loggedIn = request.session().attribute("loggedIn");
                // if loggedIn is null, then no user is logged-in xD
                if (loggedIn != null) {
                    attributes.put("loggedIn", true);
                } else {
                    attributes.put("loggedIn", false);
                }

                // adding the data to the attributes map
                attributes.put("redenListe", mongoReden);
                attributes.put("abgeordneterList", abgeordneterList);
                attributes.put("picURL", picUrl);
                attributes.put("partei", parteiName);
                attributes.put("fraktion", fractionName);
                attributes.put("redeID", redeId);
                attributes.put("comments", strCommentsMap);
                attributes.put("fstComm", potentialFirstComment);
                return new ModelAndView(attributes, "reden.ftl");
            }
        }, new FreeMarkerEngine(configuration));

        // saving the route if we redirect to /createUser
        Spark.before("/rede/text", (request, response) -> {
            request.session().attribute("previousPage", request.pathInfo());
        });

        // Get the Reden by text search
        Spark.get("/rede/text", (request, response) -> {
            // getting the queried text and creating the attribute map
            String textSearch = request.queryParams("textQuery");
            System.out.println(textSearch);
            Map<String, Object> attributes = new HashMap<>();

            // adding the speeches to a list
            List<Rede_MongoDB_Impl> matchingRedenBackup = bundestagFactory.getRedenFromText(textSearch).stream().collect(Collectors.toList());
            List<Rede_MongoDB_Impl> matchingRedenFinal = new ArrayList<>();
            for (Rede_MongoDB_Impl r : matchingRedenBackup) {
                String redeID = r.getId();
                Rede_MongoDB_Impl newRede = (Rede_MongoDB_Impl) bundestagFactory.getRedenFromDB(redeID).stream().collect(Collectors.toList()).get(0);
                matchingRedenFinal.add(newRede);
            }

            // init the other data we need to gather
            List<Abgeordneter> abgeordneterList = new ArrayList<>();
            List<String> picURLs = new ArrayList<>();
            List<String> parteiNamen = new ArrayList<>();
            List<String> fractionNamen = new ArrayList<>();
            String abgeordneterID = "";

            for (Rede_MongoDB_Impl r : matchingRedenFinal) {
                abgeordneterID = bundestagFactory.getSpeakerIDFromBackup(r.getId());
                List<Abgeordneter> aList = bundestagFactory.getAbgeordneteFromDB(abgeordneterID).stream().collect(Collectors.toList());
                Abgeordneter abgeordneter = null;
                String parteiName = "";
                String picUrl = "";
                if (!aList.isEmpty()) {
                    abgeordneter = aList.get(0);
                    parteiName = abgeordneter.getPartei().getName();
                    picUrl = bundestagFactory.getPictureURL(abgeordneterID); // accessing the pic url
                    abgeordneterList.add(abgeordneter);
                }
                picURLs.add(picUrl);
                if (!parteiName.isEmpty()) {
                    parteiNamen.add(parteiName);
                } else {
                    parteiNamen.add("Parteilos");
                }
                String fractionName = bundestagFactory.getFraktionName(abgeordneterID);
                if (!fractionName.isEmpty()) {
                    fractionNamen.add(fractionName);
                } else {
                    fractionNamen.add("Fraktionslos");
                }
            }

            // check if there is any logged-in user
            Boolean loggedIn = request.session().attribute("loggedIn");
            // if loggedIn is null, then no user is logged in xD
            if (loggedIn != null) {
                attributes.put("loggedIn", true);
            } else {
                attributes.put("loggedIn", false);
            }

            // adding the data to the attributes' map
            attributes.put("redenListe", matchingRedenFinal);
            attributes.put("abgeordneterList", abgeordneterList);
            attributes.put("picURLs", picURLs);
            attributes.put("parteiNamen", parteiNamen);
            attributes.put("fraktionNamen", fractionNamen);

            return new ModelAndView(attributes, "redenBatch.ftl");
        }, new FreeMarkerEngine(configuration));

        // saving the route if we redirect to /createUser
        Spark.before("/redenSearch", (request, response) -> {
            request.session().attribute("previousPage", request.pathInfo());
        });

        // load the page the user can use to navigate to a specific speech starting from the wahlperiode
        Spark.get("/redenSearch", (request, response) -> {
            Map<String, Object> attributes = new HashMap<>();

            // getting all the needed data
            List<Integer> wpList = Arrays.asList(19, 20);
            List<Integer> meetings19List = bundestagFactory.getSitzungen(19).stream().collect(Collectors.toList());
            List<Integer> meetings20List = bundestagFactory.getSitzungen(20).stream().collect(Collectors.toList());
            // sorting the meeting numbers
            Collections.sort(meetings19List);
            Collections.sort(meetings20List);

            // check if there is any logged-in user
            Boolean loggedIn = request.session().attribute("loggedIn");
            // if loggedIn is null, then no user is logged-in xD
            if (loggedIn != null) {
                attributes.put("loggedIn", true);
            } else {
                attributes.put("loggedIn", false);
            }

            // adding the data to the attributes map
            attributes.put("wahlperioden", wpList);
            attributes.put("wp19", meetings19List);
            attributes.put("wp20", meetings20List);

            return new ModelAndView(attributes, "redenSearch.ftl");
        }, new FreeMarkerEngine(configuration));

        // saving the route if we redirect to /createUser
        Spark.before("/redenSearch/:sitzungsNr", (request, response) -> {
            request.session().attribute("previousPage", request.pathInfo());
        });

        // load the page the user can use to navigate to a specific speech starting from the wahlperiode
        Spark.get("/redenSearch/:sitzungsNr", (request, response) -> {
            Map<String, Object> attributes = new HashMap<>();

            // getting the wp and sitzungs nr from the request
            String sParams = request.params(":sitzungsNr");
            String[] paramParts = sParams.split(":");
            String sWP = paramParts[0];
            Integer iWP = Integer.parseInt(sWP);
            String sSitzungsNr = paramParts[1];
            Integer iSitzungsNr = Integer.parseInt(sSitzungsNr);

            // getting all the TOP
            List<String> topList = bundestagFactory.getTOP(iWP, iSitzungsNr).stream().collect(Collectors.toList());

            // sorting the meeting numbers
            Collections.sort(topList);

            // check if there is any logged-in user
            Boolean loggedIn = request.session().attribute("loggedIn");
            // if loggedIn is null, then no user is logged-in xD
            if (loggedIn != null) {
                attributes.put("loggedIn", true);
            } else {
                attributes.put("loggedIn", false);
            }

            // adding the data to the attributes map
            attributes.put("wahlperiode", iWP);
            attributes.put("sitzung", iSitzungsNr);
            attributes.put("topList", topList);

            return new ModelAndView(attributes, "sitzungSearch.ftl");
        }, new FreeMarkerEngine(configuration));

        // saving the route if we redirect to /createUser
        Spark.before("/redenSearch/:sitzungsNr/:top", (request, response) -> {
            request.session().attribute("previousPage", request.pathInfo());
        });

        // load the page the user can use to navigate to a specific speech starting from the wahlperiode
        Spark.get("/redenSearch/:sitzungsNr/:top", (request, response) -> {
            Map<String, Object> attributes = new HashMap<>();

            // getting the wp and sitzungs nr from the request
            String sParams = request.params(":sitzungsNr");
            String[] paramParts = sParams.split(":");
            String sWP = paramParts[0];
            Integer iWP = Integer.parseInt(sWP);
            String sSitzungsNr = paramParts[1];
            Integer iSitzungsNr = Integer.parseInt(sSitzungsNr);

            // getting the top nr from the request
            String sTOP = request.params(":top");
            sTOP = sTOP.replace(":", "");

            // getting all the TOP
            List<String> redenIDs = bundestagFactory.getRedenIDsFromTOP(iWP, iSitzungsNr, sTOP).stream().collect(Collectors.toList());

            // sorting the meeting numbers
            Collections.sort(redenIDs);

            // check if there is any logged-in user
            Boolean loggedIn = request.session().attribute("loggedIn");
            // if loggedIn is null, then no user is logged-in xD
            if (loggedIn != null) {
                attributes.put("loggedIn", true);
            } else {
                attributes.put("loggedIn", false);
            }

            // adding the data to the attributes map
            attributes.put("wp", iWP);
            attributes.put("sitzung", iSitzungsNr);
            attributes.put("top", sTOP);
            attributes.put("redenIDs", redenIDs);

            return new ModelAndView(attributes, "topSearch.ftl");
        }, new FreeMarkerEngine(configuration));
    }

    /**
     * Method used for finding the UniCode of certain characters that the BreakIterator jumps over
     * (when splitting the text into sentences)
     *
     * @param text
     */
    public static void inspectCharacters(String text) {
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            System.out.println("Character: " + c + ", Unicode value: " + (int) c);
        }
    }
}
