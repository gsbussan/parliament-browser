package org.texttechnologylab.project.parliament_browser_01_montag_2.data.impl.factory;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.texttechnologylab.project.parliament_browser_01_montag_2.data.*;
import org.texttechnologylab.project.parliament_browser_01_montag_2.data.impl.mongodb.*;
import org.texttechnologylab.project.parliament_browser_01_montag_2.database.MongoDBHandler;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.mongodb.client.model.Filters.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Zentrale klasse verantwortlich für die Überführung der extrahierten Daten aus den XMLs in MongoDB,
 * und die Abbildung der Daten an den MongoDB Impls
 * @author Gurpreet Singh
 */
public class InsightBundestagFactory_Impl implements InsightBundestagFactory{
    MongoDBHandler dbConnectionHandler;
    public InsightBundestagFactory_Impl() throws Exception {
        this.dbConnectionHandler = new MongoDBHandler();

    }
    private static final Map<String, String> topicToCategoryMap = new HashMap<>();

    static {
        topicToCategoryMap.put("Social", "Society");
        topicToCategoryMap.put("Health", "Society");
        topicToCategoryMap.put("Housing", "Society");
        topicToCategoryMap.put("Labor", "Society");
        topicToCategoryMap.put("Education", "Society");
        topicToCategoryMap.put("Culture", "Society");

        topicToCategoryMap.put("Foreign", "Policy");
        topicToCategoryMap.put("Law", "Policy");
        topicToCategoryMap.put("Government", "Policy");
        topicToCategoryMap.put("Defense", "Policy");
        topicToCategoryMap.put("Civil", "Policy");
        topicToCategoryMap.put("Domestic", "Policy");
        topicToCategoryMap.put("Immigration", "Policy");


        topicToCategoryMap.put("Transportation", "Infrastructure");
        topicToCategoryMap.put("Public", "Infrastructure");
        topicToCategoryMap.put("Energy", "Infrastructure");
        topicToCategoryMap.put("Technology", "Infrastructure");

        topicToCategoryMap.put("Environment", "Environment");
        topicToCategoryMap.put("Agriculture", "Environment");

        topicToCategoryMap.put("Macroeconomics", "Economics");
        topicToCategoryMap.put("International", "Economics");
    }


    @Override
    public  Set<Abgeordneter> getAbgeordneteFromDB() {

        MongoCollection<Document> abgeordneterCollection = dbConnectionHandler.getCollection("abgeordneter");
        Set<Abgeordneter> abgeordneterList = new HashSet<>(0);

        FindIterable<Document> result = abgeordneterCollection.find();
        for (Document document : result) {
            Abgeordneter_MongoDB_Impl abgeordneter = new Abgeordneter_MongoDB_Impl(this, document);
            abgeordneterList.add(abgeordneter);
        }

        return abgeordneterList;
    }

    @Override
    public Map<String, Object> search(String query) {
        Map<String, Object> model = new HashMap<>();

        // Suchabfrage erstellen und durchführen
        MongoCursor<Document> cursor = dbConnectionHandler.getCollection("rede").find(regex("text", query)).iterator();

        // Die Ergebnisse in eine Liste speichern
        List<String> results = new ArrayList<>();
        while (cursor.hasNext()) {
            Document speech = cursor.next();
            results.add(speech.getString("text"));
        }
        model.put("results", results);

        // Cursor schließen
        cursor.close();

        return model;
    }


    @Override
    public Set<Abgeordneter> getAbgeordneteFromDB(String abgeordneterId) {
        MongoCollection<Document> abgeordneterCollection = dbConnectionHandler.getCollection("abgeordneter");
        Set<Abgeordneter> abgeordneterList = new HashSet<>(0);

        // Assuming redeId is a field in your documents, create a filter
        Document filter = new Document("_id", abgeordneterId);

        FindIterable<Document> result = abgeordneterCollection.find(filter);
        for (Document document : result) {
            Abgeordneter_MongoDB_Impl abgeordneter = new Abgeordneter_MongoDB_Impl(this, document);
            abgeordneterList.add(abgeordneter);
        }

        return abgeordneterList;
    }


    @Override
    public Set<Rede> getRedenFromDB(){

        MongoCollection<Document> redeCollection = dbConnectionHandler.getCollection("rede");
        Set<Rede> redeSet = new HashSet<>(0);

        FindIterable<Document> result = redeCollection.find();
        for(Document document: result){
            Rede_MongoDB_Impl rede = new Rede_MongoDB_Impl(this, document, null);
            redeSet.add(rede);
        }
        return redeSet;

    }

    @Override
    public Set<Protokoll> getProtokollById(String protocolId) {
        MongoCollection<Document> protokollCollection = dbConnectionHandler.getCollection("protokoll");
        Set<Protokoll> protokollSet = new HashSet<>(0);

        String[] parts = protocolId.split("-");
        int sitzungNummer = Integer.parseInt(parts[0]);
        int wahlperiode = Integer.parseInt(parts[1]);

        Document filter = new Document("nummer", sitzungNummer).append("wahlperiode", wahlperiode);
        FindIterable<Document> result = protokollCollection.find(filter);
        for(Document document: result){
            Protokoll_MongoDB_Impl protokollMongoDB = new Protokoll_MongoDB_Impl(this, document);
            protokollSet.add(protokollMongoDB);
        }

        return protokollSet;

    }

    @Override
    public Set<Protokoll> getProtokollenFromDB() {
        MongoCollection<Document> sCollection = dbConnectionHandler.getCollection("protokoll");
        Set<Protokoll> protokollSet = new HashSet<>(0);

        FindIterable<Document> result = sCollection.find();
        for(Document document: result){
            Protokoll_MongoDB_Impl sitzung = new Protokoll_MongoDB_Impl(this,document);
            protokollSet.add(sitzung);
        }
        return protokollSet;
    }


    @Override
    public Set<Rede> getRedenFromDB(String redeId) {
        MongoCollection<Document> redeCollection = dbConnectionHandler.getCollection("rede");
        Set<Rede> redeSet = new HashSet<>();

        Document filter = new Document("_id", redeId);
        FindIterable<Document> result = redeCollection.find(filter);

        // accessing the collection of speeches with comments
        MongoCollection<Document> redeCommentsCollection = dbConnectionHandler.getCollection("rede-with-comments");
        Document filterComments = new Document("id", redeId);
        FindIterable<Document> resultComments = redeCommentsCollection.find(filterComments);
        Document redeWithComments = resultComments.first();

        for (Document document : result) {
            Rede_MongoDB_Impl rede = new Rede_MongoDB_Impl(this, document, redeWithComments);
            redeSet.add(rede);
        }

        return redeSet;
    }

    @Override
    public Set<Rede> getRedenBatchFromDB(int offset, int batchSize) {
        MongoCollection<Document> redeCollection = dbConnectionHandler.getCollection("rede");
        Set<Rede> redeSet = new HashSet<>(0);

        FindIterable<Document> result = redeCollection.find().skip(offset).limit(batchSize);

        // accessing the collection of speeches with comments
        MongoCollection<Document> redeCommentsCollection = dbConnectionHandler.getCollection("rede-with-comments");

        for (Document document : result) {
            String redeID = (String) document.get("_id");
            Document filterComments = new Document("id", redeID);
            FindIterable<Document> resultComments = redeCommentsCollection.find(filterComments);
            Document redeWithComments = resultComments.first();
            Rede_MongoDB_Impl rede = new Rede_MongoDB_Impl(this, document, redeWithComments);
            redeSet.add(rede);
        }

        return redeSet;
    }

    @Override
    public Set<Rede> getRestRedenBatchFromDB(int offset, int batchSize) {
        MongoCollection<Document> redeCollection = dbConnectionHandler.getCollection("rede");
        Set<Rede> redeSet = new HashSet<>(0);

        Document query = new Document("serCas", new Document("$exists", false));

        FindIterable<Document> result = redeCollection.find(query).skip(offset).limit(batchSize);

        for (Document document : result) {
            if (document != null){
                Rede_MongoDB_Impl rede = new Rede_MongoDB_Impl(this, document, null);
                redeSet.add(rede);
            }
        }

        return redeSet;
    }

    @Override
    public Set<Rede> getRestRedenBatchFromDB() {
        MongoCollection<Document> redeCollection = dbConnectionHandler.getCollection("rede");
        Set<Rede> redeSet = new HashSet<>(0);

        Document query = new Document("serCas", new Document("$exists", false));

        FindIterable<Document> result = redeCollection.find(query);

        for (Document document : result) {
            Rede_MongoDB_Impl rede = new Rede_MongoDB_Impl(this, document, null);
            redeSet.add(rede);
        }

        return redeSet;
    }
    @Override
    public Set<Tagesordnungspunkt> getTagesordnungspunktFromDB() {
        MongoCollection<Document> tCollection = dbConnectionHandler.getCollection("tagesordnung");
        Set<Tagesordnungspunkt> tagesordnungspunktSet = new HashSet<>(0);

        FindIterable<Document> result = tCollection.find();
        for(Document document: result){
            Tagesordnungspunkt_MongoDB_Impl tPunkt = new Tagesordnungspunkt_MongoDB_Impl(this, document);
            tagesordnungspunktSet.add(tPunkt);
        }
        return tagesordnungspunktSet;
    }
    @Override
    public Set<Log> getLogEntriesFromDB() {
        MongoCollection<Document> lCollection = dbConnectionHandler.getCollection("log");
        Set<Log> logSet = new HashSet<>(0);

        FindIterable<Document> result = lCollection.find();
        for(Document document: result){
            Log_MongoDB_Impl log = new Log_MongoDB_Impl(this, document);
            logSet.add(log);
        }

        return logSet;
    }

    @Override
    public String getSpeakerIDFromBackup(String redeID) {
        MongoCollection<Document> backupCollection = dbConnectionHandler.getCollection("Backup_Of_Rede");
        String abgeordneterID = "";

        Document filter = new Document("_id", redeID);
        FindIterable<Document> result = backupCollection.find(filter);

        for (Document document : result) {
            Document abgeordneterData = (Document) document.get("redner");
            abgeordneterID = abgeordneterData.getString("id");
        }


        return abgeordneterID;
    }

    @Override
    public String getPictureURL(String abgeordneterID) {
        String picURL = "";
        MongoCollection<Document> abgeordneterCollection = dbConnectionHandler.getCollection("abgeordneter");

        Document filter = new Document("_id", abgeordneterID);
        FindIterable<Document> result = abgeordneterCollection.find(filter);

        for (Document doc : result){
            List<?> pictureData = (List<?>) doc.get("bilder");
            if (!pictureData.isEmpty()){
                Document primaryPicDoc = (Document) pictureData.get(0);
                picURL = picURL + primaryPicDoc.get("bildUrl");
            }
        }

        return picURL;
    }

    @Override
    public String getFraktionName(String abgeordneterID) {
        String fractionName = "";
        MongoCollection<Document> abgeordneterCollection = dbConnectionHandler.getCollection("abgeordneter");

        Document filter = new Document("_id", abgeordneterID);
        FindIterable<Document> result = abgeordneterCollection.find(filter);

        for (Document doc : result){
            List<Document> mandatInfo = (List<Document>) doc.get("mandat");
            List<Document> fraktionList = (List<Document>) mandatInfo.get(0).get("fraktion");
            fractionName = fractionName + ((String) fraktionList.get(0).get("fName"));
        }

        return fractionName;
    }

    @Override
    public Set<Rede_MongoDB_Impl> getRedenFromText(String searchText) {
        // init the return set
        Set<Rede_MongoDB_Impl> returnSet = new HashSet<>();

        // finding the speeches that match the search text
        MongoCollection<Document> redeCollection = dbConnectionHandler.getCollection("Backup_Of_Rede");
        Bson filter = Filters.text(searchText); // the field "text" in the collection has to be indexed first
        FindIterable<Document> result = redeCollection.find(filter);

        // accessing the collection of speeches with comments
        MongoCollection<Document> redeCommentsCollection = dbConnectionHandler.getCollection("rede-with-comments");

        for (Document doc : result){
            String redeId = (String) doc.get("_id");
            Document filterComments = new Document("id", redeId);
            FindIterable<Document> resultComments = redeCommentsCollection.find(filterComments);
            Document redeWithComments = resultComments.first();
            Rede_MongoDB_Impl rede = new Rede_MongoDB_Impl(this, doc, redeWithComments);
            returnSet.add(rede);
        }

        return returnSet;
    }

    @Override
    public Set<Integer> getSitzungen(Integer wpNr) {
        Set<Integer> returnSet = new HashSet<>();
        MongoCollection<Document> meetingCollection = dbConnectionHandler.getCollection("protokoll");

        Document filter = new Document("wahlperiode", wpNr);
        FindIterable<Document> result = meetingCollection.find(filter);

        // getting the meeting NRs and adding them to the set
        for (Document doc : result){
            Integer meetingNr = (Integer) doc.get("nummer");
            returnSet.add(meetingNr);
        }

        return returnSet;
    }

    @Override
    public Set<String> getTOP(Integer wpNr, Integer sitzungsNr) {
        Set<String> returnSet = new HashSet<>();
        MongoCollection<Document> meetingCollection = dbConnectionHandler.getCollection("protokoll");

        Document filter = new Document("wahlperiode", wpNr).append("nummer", sitzungsNr);
        FindIterable<Document> result = meetingCollection.find(filter);

        // getting the Tagesordnungspunkte and adding them to the list
        for (Document doc : result){
            List<Document> topList = (List<Document>) doc.get("top");
            for (Document top : topList){
                String tagesordnungspunkt = top.getString("_id");
                returnSet.add(tagesordnungspunkt);
            }
        }

        return returnSet;

    }

    @Override
    public Set<String> getRedenIDsFromTOP(Integer wpNr, Integer sitzungsNr, String top) {
        Set<String> returnSet = new HashSet<>();

        MongoCollection<Document> protokollCollection = dbConnectionHandler.getCollection("protokoll");
        Document filter = new Document("wahlperiode", wpNr).append("nummer", sitzungsNr);
        FindIterable<Document> result = protokollCollection.find(filter);

        // iterating over the Tagesordnungspunkte and finding the one that matches
        for (Document doc : result){
            List<Document> topList = (List<Document>) doc.get("top");
            for (Document docTOP : topList){
                // checking if the TOP matches and saving its speeches
                if (top.equals(docTOP.getString("_id"))){
                    List<Document> redenList = (List<Document>) docTOP.get("reden");
                    for (Document rDoc : redenList){
                        String rID = (String) rDoc.get("_id");
                        returnSet.add(rID);
                    }
                };
            }
        }

        return returnSet;
    }


    @Override
    public  Map<String, Integer> getBarChartData(String collectionName, String startDateString, String endDateString) throws ParseException {
        Map<String, Integer> barChartCounts = new HashMap<>(0);

        MongoCollection<Document> collection = this.dbConnectionHandler.getCollection(collectionName);
        List<Document> filteredDocumentsList = filterDocumentsByDate(collection, startDateString, endDateString);

        // Initialisieren der Zählungen für jede Kategorie mit 0
        String[] categories = {"nomen", "konjunktionen", "artikeln", "adverb", "verb"};
        for (String category : categories) {
            barChartCounts.put(category, 0);
        }

        // Durchlaufen der gefilterten Dokumente und Zählen der Elemente in den angegebenen Feldern
        for (Document doc : filteredDocumentsList) {
            for (String category : categories) {
                List<Document> items = (List<Document>) doc.get(category);
                if (items != null) {
                    barChartCounts.put(category, barChartCounts.get(category) + items.size());
                }
            }
        }

        return barChartCounts;
    }

    /**
     * Hilfsmethode, um Dokumente basierend auf dem Datum zu filtern
     */
    @Override
    public List<Document> filterDocumentsByDate(MongoCollection<Document> collection, String startDateString, String endDateString) throws ParseException {
        List<Document> filteredDocumentsList = new ArrayList<>();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date startDate = sdf.parse(startDateString);
        Date endDate = sdf.parse(endDateString);
        endDate = new Date(endDate.getTime() + (1000 * 60 * 60 * 24)); // Addiert einen Tag, um das Enddatum einzubeziehen

        Bson dateFilter = and(gte("redner.redeDatum", startDate), lt("redner.redeDatum", endDate));

        FindIterable<Document> documents = collection.find(dateFilter);
        for (Document doc : documents) {
            filteredDocumentsList.add(doc);
        }

        return filteredDocumentsList;
    }

    @Override
    public Map<String, Map<String, Object>> countSpeechesBySpeakerWithImages(String collectionName, String abgeordneterCollectionName, String startDateString, String endDateString) throws ParseException {

        MongoCollection<Document> speechesCollection = this.dbConnectionHandler.getCollection(collectionName);
        MongoCollection<Document> abgeordneterCollection = this.dbConnectionHandler.getCollection(abgeordneterCollectionName);

        List<Document> filteredSpeeches = filterDocumentsByDate(speechesCollection, startDateString, endDateString);
        Map<String, Map<String, Object>> speakerDetails = new HashMap<>();

        for (Document speech : filteredSpeeches) {
            Document redner = speech.get("redner", Document.class);
            if (redner != null) {
                String vorname = redner.getString("vorname");
                String nachname = redner.getString("nachname");
                String fullname = vorname + " " + nachname;

                speakerDetails.putIfAbsent(fullname, new HashMap<>());
                Map<String, Object> details = speakerDetails.get(fullname);
                details.merge("count", 1, (oldVal, newVal) -> (Integer) oldVal + (Integer) newVal);

                Document abgeordneterDoc = abgeordneterCollection.find(and(eq("vorname", vorname), eq("nachname", nachname))).first();
                if (abgeordneterDoc != null) {
                    List<Document> bilder = abgeordneterDoc.getList("bilder", Document.class);
                    if (bilder != null && !bilder.isEmpty()) {
                        Document bild = bilder.get(0);
                        String bildUrl = bild.getString("bildUrl");
                        if (bildUrl != null && !bildUrl.isEmpty()) {
                            details.put("imageUrl", bildUrl);
                        }
                    }
                }
            }
        }
        return speakerDetails;
    }

    @Override
    public Map<String, Integer> countNamedEntities(String collectionName, String startDateString, String endDateString) throws ParseException {
        MongoCollection<Document> collection = this.dbConnectionHandler.getCollection(collectionName);

        List<Document> filteredDocuments = filterDocumentsByDate(collection, startDateString, endDateString);
        Map<String, Integer> entityCount = new HashMap<>();

        for (Document document : filteredDocuments) {
            List<String> namedEntities = (List<String>) document.get("namedEntities");
            if (namedEntities != null) {
                for (String entity : namedEntities) {
                    entityCount.merge(entity, 1, Integer::sum);
                }
            }
        }
        return entityCount;
    }
    @Override
    public  Map<String, Double> calculateSentimentAverages(String collectionName, String startDateString, String endDateString) throws ParseException {
        MongoCollection<Document> collection = this.dbConnectionHandler.getCollection(collectionName);

        List<Document> filteredDocuments = filterDocumentsByDate(collection, startDateString, endDateString);

        double positiveSum = 0;
        double negativeSum = 0;
        double neutralSum = 0;
        int positiveCount = 0;
        int negativeCount = 0;
        int neutralCount = 0;

        if (filteredDocuments != null) {
            for (Document document : filteredDocuments) {
                List<Double> sentiments = document.getList("sentiment", Double.class);
                if (sentiments != null) {
                    for (Double sentiment : sentiments) {
                        if (sentiment == null) {
                            neutralCount++;
                        } else if (sentiment > 0) {
                            positiveSum += sentiment;
                            positiveCount++;
                        } else if (sentiment < 0) {
                            negativeSum += Math.abs(sentiment);
                            negativeCount++;
                        }
                    }
                }
            }
        }

        double averagePositive = positiveCount > 0 ? positiveSum / positiveCount : 0;
        double averageNegative = negativeCount > 0 ? negativeSum / negativeCount : 0;
        double averageNeutral = neutralCount; // Wenn Sie die Anzahl der Neutralen als Durchschnitt haben möchten

        Map<String, Double> averages = new HashMap<>();
        averages.put("Positive", averagePositive);
        averages.put("Negative", averageNegative);
        averages.put("Neutral", averageNeutral);

        return averages;
    }

    /**
     * Hauptmethode, um die Sunburst-Datenstruktur zu erstellen
     */
    public  Map<String, Object> processDocumentsForSunburst(String collectionName, String startDateString, String endDateString) {
        Map<String, Object> sunburstData = new HashMap<>();

        try {
            MongoCollection<Document> collection = this.dbConnectionHandler.getCollection(collectionName);

            List<Document> filteredDocuments = filterDocumentsByDate(collection, startDateString, endDateString);
            Map<String, Double> topicValues = new HashMap<>();

            for (Document doc : filteredDocuments) {
                Document topicsDoc = (Document) doc.get("topic"); // Angenommen, dass "topic" das richtige Feld im Dokument ist
                if (topicsDoc != null) {
                    for (Map.Entry<String, Object> entry : topicsDoc.entrySet()) {
                        String topic = entry.getKey();
                        Object valueObj = entry.getValue();

                        if (valueObj instanceof Number) {
                            topicValues.merge(topic, ((Number) valueObj).doubleValue(), Double::sum);
                        }
                    }
                }
            }

            // Erstellen Sie die Struktur für das Sunburst-Diagramm
            sunburstData.put("name", "Topics");
            List<Map<String, Object>> children = new ArrayList<>();

            topicValues.forEach((topic, sum) -> {
                String category = topicToCategoryMap.get(topic);
                if (category != null) {
                    List<Map<String, Object>> categoryList = children.stream()
                            .filter(map -> category.equals(map.get("name")))
                            .findFirst()
                            .map(map -> (List<Map<String, Object>>) map.get("children"))
                            .orElseGet(() -> {
                                Map<String, Object> newCategory = new HashMap<>();
                                newCategory.put("name", category);
                                List<Map<String, Object>> newChildren = new ArrayList<>();
                                newCategory.put("children", newChildren);
                                children.add(newCategory);
                                return newChildren;
                            });

                    Map<String, Object> topicMap = new HashMap<>();
                    topicMap.put("name", topic);
                    topicMap.put("value", sum);
                    categoryList.add(topicMap);
                }
            });

            sunburstData.put("children", children);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return sunburstData;
    }


}
