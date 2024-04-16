package org.texttechnologylab.project.parliament_browser_01_montag_2.database;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.*;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS;
import de.tudarmstadt.ukp.dkpro.core.api.ner.type.NamedEntity;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.dependency.Dependency;
import org.apache.uima.cas.impl.XmiCasSerializer;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.util.XmlCasSerializer;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.hucompute.textimager.uima.type.Sentiment;
import org.hucompute.textimager.uima.type.category.CategoryCoveredTagged;
import org.texttechnologylab.project.parliament_browser_01_montag_2.data.*;
//import org.texttechnologylab.project.parliament_browser_01_montag_2.helper.DUUIHelper;
import org.texttechnologylab.project.parliament_browser_01_montag_2.helper.NLP_Implementation;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Klasse für die Interaktion mit der MongoDB
 * @author Mihai Paun
 * @author Gurpreet Singh
 * @author Taylan Özcelik
 */
public class MongoDBHandler {
    private final MongoClient mongoClient;
    private final MongoDatabase database;
    private final NLP_Implementation duuiHelper;
    private MongoCollection<Document> abgeordneterCollection;
    private MongoCollection<Document> redeCollection;
    private MongoCollection<Document> protokollCollection;

    public MongoDBHandler() throws Exception {

        Properties properties = loadProperties();

        // Lade Konfiguration aus der properties-Datei
        String host = properties.getProperty("remote_host");
        String port = properties.getProperty("remote_port");
        String user = properties.getProperty("remote_user");
        String password = properties.getProperty("remote_password");
        String databaseName = properties.getProperty("remote_database");


        // Quelle: Hier habe ich mich an die Beispiele der Vorlesungen orientiert.
        MongoCredential credential = MongoCredential.createScramSha1Credential(user, databaseName, password.toCharArray());

        ServerAddress seed = new ServerAddress(host, Integer.parseInt(port));
        List<ServerAddress> seeds = new ArrayList(0);
        seeds.add(seed);

        MongoClientOptions options = MongoClientOptions.builder()
                .connectionsPerHost(2)
                .socketTimeout(20000)
                .maxWaitTime(10000)
                .connectTimeout(1000)
                .sslEnabled(false)
                .build();

        // mit MongoDB verbinden
        this.mongoClient = new MongoClient(seeds, credential, options);
        this.database = mongoClient.getDatabase(databaseName);



        // Collection in der Datenbank rein
        this.abgeordneterCollection = this.database.getCollection("abgeordneter");
        this.redeCollection = this.database.getCollection("rede");
        this.protokollCollection = this.database.getCollection("protokoll");

        this.duuiHelper = new NLP_Implementation();


    }


    /**
     * Lädt die Daten aus der Datenbank-Datei
     */
    private Properties loadProperties() {
        Properties properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("Project_01_02.txt")) {
            if (input == null) {
                System.err.println("Datenbankdatei nicht gefunden.");
                System.exit(1);
            }

            // Lade die Eigenschaften aus der Datei
            properties.load(input);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return properties;
    }

    /**
     * Gibt eine Collection aus der DB zurück
     * @param sCollection
     * @return
     */
    public MongoCollection getCollection(String sCollection){
        return this.database.getCollection(sCollection);
    }

    /**
     * Dokumenten in die DB collection "abgeordneter" rein"
     * @param abgeordneter
     */
    public void saveAbgeordneter(Abgeordneter abgeordneter) {

        Document abgeordneterDocument = new Document()
                .append("_id", abgeordneter.getId())
                .append("nachname", abgeordneter.getNachname())
                .append("vorname", abgeordneter.getVorname())
                .append("ortszusatz", abgeordneter.getOrtsZusatz())
                .append("anrede", abgeordneter.getAnrede())
                .append("akadtitel", abgeordneter.getAkadTitel())
                .append("geburtsdatum", abgeordneter.getGeburtsdatum())
                .append("geburtsort", abgeordneter.getGeburtsort())
                .append("sterbedatum", abgeordneter.getSterbedatum())
                .append("geschlecht", abgeordneter.getGeschlecht())
                .append("religion", abgeordneter.getReligion())
                .append("beruf", abgeordneter.getBeruf())
                .append("vita", abgeordneter.getVita())
                .append("partei", abgeordneter.getPartei().getName())
                //Muss auch in die DB, wegen der Visualisierung der Wahlperioden und Mandaten
                .append("mandat", convertMandateSetToDocuments(abgeordneter.listMandate()))
                // Muss auch in die DB, weil wir Bilder der Abgeordneten benötigen
                .append("bilder", convertBilderSetToDocuments(abgeordneter.listBilder()));
        abgeordneterCollection.insertOne(abgeordneterDocument);
    }

    /**
     * Hilfsmethode, um die Bilder auf die File_Impl Klasse zu mappen
     * @param bilder
     * @return
     */
    private List<Document> convertBilderSetToDocuments(Set<Bild> bilder) {
        List<Document> bildDocuments = new ArrayList<>();

        for(Bild bild: bilder){
            bildDocuments
                    .add(new Document()
                            .append("metadata", bild.getMetadaten())
                            .append("bildUrl", bild.getBildUrl()));
        }

        return bildDocuments;
    }

    /**
     * Hilfsmethode, um die Kommentare auf die File_Impl klasse zu mappen
     * @param mandatSet
     * @return
     */
    private List<Document> convertMandateSetToDocuments(Set<Mandat> mandatSet) {
        List<Document> mandatDocuments = new ArrayList<>();

        for (Mandat mandat : mandatSet) {
            mandatDocuments
                    .add(new Document()
                            .append("fromDate", mandat.fromDate())
                            .append("toDate", mandat.toDate())
                            .append("typ", mandat.getTyp())
                            .append("wahlperiode", convertWahlperiodeToDocument(mandat.getWahlperiode()))
                            .append("fraktion", convertFraktionSetToDocuments(mandat.getFraktionSet()))
                            .append("ausschuss", convertAusschussSetToDocuments(mandat.getAusschussSet())));
        }

        return mandatDocuments;
    }

    /**
     * Hilfsmethode, um die Ausschüsse auf die File_Impl Klasse zu mappen
     * @param ausschussSet
     * @return
     */
    private List<Document> convertAusschussSetToDocuments(Set<Ausschuss> ausschussSet) {
        List<Document> ausschussDocuments = new ArrayList<>();

        for(Ausschuss ausschuss: ausschussSet){
            ausschussDocuments
                    .add(new Document()
                            .append("aName", ausschuss.getName()));
        }
        return ausschussDocuments;
    }

    /**
     * Hilfsmethode, um die Fraktionen auf die File_Impl Klasse zu mappen
     * @param fraktionSet
     * @return
     */
    private List<Document> convertFraktionSetToDocuments(Set<Fraktion> fraktionSet) {
        List<Document> fraktionDocuments = new ArrayList<>();

        for (Fraktion fraktion : fraktionSet) {
            fraktionDocuments
                    .add(new Document()
                            .append("fName", fraktion.getName()));
        }

        return fraktionDocuments;
    }

    /**
     * Hilfsmethode, um die Wahlperioden auf die File_Impl Klasse zu mappen
     * @param wahlperiode
     * @return
     */
    private Document convertWahlperiodeToDocument(Wahlperiode wahlperiode) {
        return  new Document().append("nummer", wahlperiode.getNummer())
                .append("startDatum", wahlperiode.getStartDatum())
                .append("endDatum", wahlperiode.getEndeDatum());
    }


    /**
     * Dokumenten in die DB collection "rede" rein"
     *
     * @param rede
     * @return
     */
    public Document saveRede(Rede rede) throws Exception {
        System.out.println("Entered saveRede method! ");

        // Create a filter to identify the document to be updated or inserted
        Document filter = new Document("_id", rede.getId());

        Document redeDocument = new Document()
                .append("_id", rede.getId())
                .append("redner", convertAbgeordneterToDocument(rede.getRedner()))
                .append("text", rede.getText())
                .append("kommentar", convertKommentarSetToDocuments(rede.getKommentar()));

        // remove next comment when you want to begin processing
        JCas pCas = rede.toCAS();

        // Hier kurz überprüfen, ob die Reden bereits analysiert wurden
        // remove comments until 275 when you want to start processing
        if (!annotationsExist(pCas, Token.class) ||
                !annotationsExist(pCas, Sentence.class) ||
                !annotationsExist(pCas, POS.class) ||
                !annotationsExist(pCas, Dependency.class) ||
                !annotationsExist(pCas, NamedEntity.class) ||
                !annotationsExist(pCas, Sentiment.class) ||
                // next line checks if the speech has been processed with ParlBert
                !annotationsExist(pCas, CategoryCoveredTagged.class)) {

            System.out.println("Entered the annotations missing if-statement! ");


            boolean bLocal = false; // change this to true if you want to process locally
            // remove next comment when you want to process
            pCas = duuiHelper.processSpeech(bLocal, rede);

        }

        // remove comment on line 278 when you want to process
        if (pCas != null) {
//            String outputDirectory = "src/main/xmi";
//
//            // Erstellen eines Dateinamens mit Zeitstempel für die XMI-Datei
//            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//            String xmiFileName = "cas_" + timestamp + ".xmi";
//
//            // Kombiniere das Ausgabeverzeichnis und den Dateinamen
//            String xmiFilePath = outputDirectory + File.separator + xmiFileName;
//
//            File xmiFile = new File(xmiFilePath);
//            try (FileOutputStream xmiOutputStream = new FileOutputStream(xmiFile)) {
//
//                // Verwendung von XmlCasSerializer zur Serialisierung der CAS in das XMI-Format
//                XmiCasSerializer.serialize(pCas.getCas(), xmiOutputStream);
//
//            }


            // Schreiben der serialisierten CAS in das Mongo-Dokument
            // remove next comment when you want to process
            redeDocument.append("serCas", duuiHelper.serializeJCas(pCas));

//            NLPDocument nlpDocument = (NLPDocument) pCas;

            // remove comments until 331 when you want to process again
            redeDocument.append("token", duuiHelper.getTokens(pCas));

            redeDocument.append("sentences", duuiHelper.getSentences(pCas));

            redeDocument.append("pos", duuiHelper.getPOS(pCas));

            redeDocument.append("persons", duuiHelper.getPersons(pCas));

            redeDocument.append("locations", duuiHelper.getLocations(pCas));

            redeDocument.append("organisations", duuiHelper.getOrganisations(pCas));

            redeDocument.append("miscellaneous", duuiHelper.getMisc(pCas));

            redeDocument.append("dependency", duuiHelper.getDependencies(pCas));

            redeDocument.append("sentiment", duuiHelper.getSentiments(pCas));

            redeDocument.append("topic", duuiHelper.getTopics(pCas));

            redeDocument.append("namedEntities", duuiHelper.getNamedEntities(pCas));

            redeDocument.append("nomen", duuiHelper.getNomen(pCas));
            redeDocument.append("konjunktionen", duuiHelper.getConjunctions(pCas));
            redeDocument.append("artikeln", duuiHelper.getArticles(pCas));
            redeDocument.append("adverb", duuiHelper.getAdverbs(pCas));
            redeDocument.append("verb", duuiHelper.getVerbs(pCas));
        }



        //Verwenden von updateOne mit der Option upsert, um das Dokument zu aktualisieren oder einzufügen.
        redeCollection.updateOne(filter, new Document("$set", redeDocument), new UpdateOptions().upsert(true));
        return redeDocument;
    }

    /**
     * Hilfsmethode, um zu überprüfen, ob die Annotationen bereits existieren
     * @param jCas
     * @param annotationType
     * @return
     */
    private boolean annotationsExist(JCas jCas, Class<? extends Annotation> annotationType) {
        return (jCas != null) && JCasUtil.exists(jCas, annotationType);
    }

    /**
     * Die Datenbank updaten mit der analysierten Ergebnissen
     * @param updatedRede
     * @throws Exception
     */

    public void updateRede(Rede updatedRede) throws Exception {

        String redeId = updatedRede.getId();
        Document filter = new Document("_id", redeId);
        Document updatedDocument = saveRede(updatedRede);
        redeCollection.replaceOne(filter, updatedDocument);

    }


    /**
     * Hilfsmethode, um die Redner auf die File_Impl klasse zu mappen
     * @param redner
     * @return
     */
    private Document convertAbgeordneterToDocument(Abgeordneter redner) {
        return new Document()
                .append("id", redner.getId())
                .append("vorname", redner.getVorname())
                .append("nachname", redner.getNachname())
                .append("partei", redner.getPartei().getName())
                .append("redeDatum", redner.getRedeDatum());

    }

    /**
     * Hilfsmethode, um die Kommentare auf die File_Impl klasse zu mappen
     * @param kommentarSet
     * @return
     */
    private List<Document> convertKommentarSetToDocuments(Set<Kommentar> kommentarSet) {
        List<Document> kommentarDocuments = new ArrayList<>();

        for (Kommentar kommentar : kommentarSet) {
            kommentarDocuments.add(new Document("text", kommentar.getText()));
        }

        return kommentarDocuments;
    }


    /**
     * Dokumenten in die DB collection "sitzung" rein"
     * @param sitzung
     */
    public void saveProtokoll(Protokoll sitzung){
        Document sDocument = new Document()
                .append("nummer", sitzung.getSitzungNummer())
                .append("beginn", sitzung.getSitzungStart())
                .append("ende", sitzung.getSitzungEnde())
                .append("wahlperiode", sitzung.getWahlperiode())
                .append("datum", sitzung.getSitzungDatum())
                .append("top", convertTagesordnungspunktToDocuments(sitzung.getTagesordnungspunkten()));
        protokollCollection.insertOne(sDocument);
    }

    /**
     * Hilfsmethode, um die TOP in die Datenbank zu mappen
     *
     * @return
     */
    public List<Document> convertTagesordnungspunktToDocuments(Set<Tagesordnungspunkt> tagesordnungspunkten){
        List<Document> tDocuments = new ArrayList<>(0);
        for(Tagesordnungspunkt tagesordnungspunkt: tagesordnungspunkten){
            tDocuments.add(new Document()
                            .append("_id", tagesordnungspunkt.getTopId())
                            .append("reden", convertRedenSetToDocuments(tagesordnungspunkt.getReden())));
        }


        return tDocuments;
    }

    /**
     * Hilfsmethode, um die Reden in die TOP und endgültig in die DB zu mappen
     * @param reden
     * @return
     */
    private List<Document> convertRedenSetToDocuments(Set<Rede> reden) {
        List<Document> rDoc = new ArrayList<>(0);
        for(Rede rede: reden){
            rDoc.add(new Document()
                    .append("_id", rede.getId())
                    .append("text", rede.getText()));
        }
        return rDoc;
    }

    /**
     * Alle Collections, die in DB da sind.
     * @return
     */
    public Set<String> listCollections() {
        Set<String> collectionSet = new HashSet<>();

        for (String collectionName : this.database.listCollectionNames()) {
            collectionSet.add(collectionName);
        }

        return collectionSet;
    }

    /**
     * Methode zum Zählen von Dokumenten in einer Sammlung
     * @param collectionName
     * @return
     */

    public long countDocuments(String collectionName) {
        MongoCollection<Document> collection = this.database.getCollection(collectionName);
        return collection.countDocuments();
    }


    /**
     * Methode zum Einfügen eines Dokuments
     * @param collectionName
     * @param document
     */
    public void insertDocument(String collectionName, Document document) {
        MongoCollection<Document> collection = this.database.getCollection(collectionName);
        collection.insertOne(document);
    }

    //

    /**
     * Methode zum Lesen von Dokumenten mit einem optionalen Filter. Gibt eine Liste von Bson Dokumenten zurück.
     * @param collectionName
     * @param filter
     * @return
     */
    public List<Document> readDocuments(String collectionName, Bson filter) {
        MongoCollection<Document> collection = this.database.getCollection(collectionName);
        FindIterable<Document> documents = (filter != null) ? collection.find(filter) : collection.find();

        List<Document> result = new ArrayList<>();

        try (MongoCursor<Document> cursor = documents.iterator()) {
            while (cursor.hasNext()) {
                result.add(cursor.next());
            }
        }

        return result;
    }

    //

    /**
     * Methode zum Aktualisieren von Dokumenten in einer Sammlung
     * @param collectionName
     * @param filter
     * @param update
     * @return
     */
    public UpdateResult updateDocument(String collectionName, Bson filter, Bson update) {
        MongoCollection<Document> collection = this.database.getCollection(collectionName);
        return collection.updateMany(filter, update);
    }

    /**
     * Methode zum Löschen von Dokumenten in einer Sammlung
     * @param collectionName
     * @param filter
     * @return
     */
    public DeleteResult deleteDocuments(String collectionName, Bson filter) {
        MongoCollection<Document> collection = this.database.getCollection(collectionName);
        return collection.deleteMany(filter);
    }

    /**
     * Methode zum Aggregieren von Dokumenten in einer Sammlung
     * @param collectionName
     * @param pipeline
     */
    public void aggregateDocuments(String collectionName, List<Bson> pipeline) {
        MongoCollection<Document> collection = this.database.getCollection(collectionName);
        AggregateIterable<Document> result = collection.aggregate(pipeline);

        for (Document document : result) {
            System.out.println(document.toJson());
        }
    }

    /**
     * Hilfsmethode, um die serialisierte Cas Objekte in String umzuwandeln
     * @param jCas
     * @return
     */

    public static String serializeCASToString(JCas jCas) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            try (OutputStream base64OutputStream = Base64.getEncoder().wrap(byteArrayOutputStream)) {
                XmlCasSerializer.serialize(jCas.getCas(), base64OutputStream);
            }

            return byteArrayOutputStream.toString("UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
