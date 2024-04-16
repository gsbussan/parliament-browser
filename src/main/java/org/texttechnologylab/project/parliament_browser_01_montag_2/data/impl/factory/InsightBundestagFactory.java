package org.texttechnologylab.project.parliament_browser_01_montag_2.data.impl.factory;

import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.texttechnologylab.project.parliament_browser_01_montag_2.data.*;
import org.texttechnologylab.project.parliament_browser_01_montag_2.data.impl.mongodb.Rede_MongoDB_Impl;

import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Schnittstelle zur Abbildung von Factory-Methoden, die mit DB interagieren.
 * @author Gurpreet Singh
 */
public interface InsightBundestagFactory {
    /**
     * Gibt die Daten aus dem "abgeordneter" Collection zurück.
     */
    Set<Abgeordneter> getAbgeordneteFromDB();

    /**
     * Such die Eingabestring in dem Redetext durch, und gibt dem Redeobjekt zurück
     * @param query
     * @return
     */

    Map<String, Object> search(String query);

    /**
     * Gibt der Abgeordnete mit dem gegebenem Id zurück
     * @param abgeordneterId
     * @return
     */
    Set<Abgeordneter> getAbgeordneteFromDB(String abgeordneterId);
    /**
     * Gibt die Daten aus dem "rede" Collection zurück.
     */
    Set<Rede> getRedenFromDB();

    /**
     * Gibt die Protokolle aus dem "protokoll" Collection zurück
     * @return
     */

    Set<Protokoll> getProtokollenFromDB();

    /**
     * Gibt die Rede mit dem gegebenem Rede zurück
     * @param redeId
     * @return
     */
    Set<Rede> getRedenFromDB(String redeId);

    /**
     * Gibt die Reden in einem bestimmten Batch zurück
     * @param offset
     * @param batchSize
     * @return
     */
    Set<Rede> getRedenBatchFromDB(int offset, int batchSize);

    /**
     * Hilfsmethode, die nur bei der NLP-Analyse der Reden geholfen hat
     * @param offset
     * @param batchSize
     * @return
     */
    Set<Rede> getRestRedenBatchFromDB(int offset, int batchSize);

    /**
     * Hilfsmethode, die schreibt daten in die DB rein.
     * @return
     */
    Set<Rede> getRestRedenBatchFromDB();
    /**
     * Gibt die Daten aus dem "tagesordnungspunkt" Collection zurück.
     */
    Set<Tagesordnungspunkt> getTagesordnungspunktFromDB();
    /**
     * Gibt die Daten aus dem "log" Collection zurück.
     * @return
     */
    Set<Log> getLogEntriesFromDB();

    /**
     * Returns a speaker's ID from the backup collection because of an issue with speaker IDs in the rede collection
     * @param redeID
     * @return
     */
    String getSpeakerIDFromBackup(String redeID);

    /**
     * Returns a URL for the speaker's profile picture
     * @param abgeordneterID
     * @return
     */
    String getPictureURL(String abgeordneterID);

    /**
     * Returns the name of the fraction the speaker belongs to
     * @param abgeordneterID
     * @return
     */
    String getFraktionName(String abgeordneterID);

    /**
     * Returns a set of speeches that match a certain search text
     * @param searchText
     * @return
     */
    Set<Rede_MongoDB_Impl> getRedenFromText(String searchText);

    /**
     * Returns a set with all sitzungen of the selected WP
     * @param wpNr
     * @return
     */
    Set<Integer> getSitzungen(Integer wpNr);

    /**
     * Returns a set with the ToPunkte of a certain Sitzung
     * @param wpNr
     * @param sitzungsNr
     * @return
     */
    Set<String> getTOP(Integer wpNr, Integer sitzungsNr);

    /**
     * Returns a set with the speech IDs contained by the given Tagesordnungspunkt
     * @param wpNr
     * @param sitzungsNr
     * @param top
     * @return
     */
    Set<String> getRedenIDsFromTOP(Integer wpNr, Integer sitzungsNr, String top);

    /**
     * Hilfsmethode, um mit der DB zu interagieren und für den Bar chart nötigen Daten zurückzuliefern
     * @param rede
     * @param startDate
     * @param endDate
     * @return
     * @throws ParseException
     */
    Map<String, Integer> getBarChartData(String rede, String startDate, String endDate) throws ParseException;

    /**
     * Hilfsmethode, um mit der DB zu interagieren und Datumsauswahl für die Diagramme zu ermöglichen
     * @param collection
     * @param startDateString
     * @param endDateString
     * @return
     * @throws ParseException
     */
    List<Document> filterDocumentsByDate(MongoCollection<Document> collection, String startDateString, String endDateString) throws ParseException;

    /**
     * Hilfsmethode, um mit der DB zu interagieren und für den Redner Bar chart nötigen Daten zurückzuliefern
     * @param collectionName
     * @param abgeordneterCollectionName
     * @param startDateString
     * @param endDateString
     * @return
     * @throws ParseException
     */
    Map<String, Map<String, Object>> countSpeechesBySpeakerWithImages(String collectionName, String abgeordneterCollectionName, String startDateString, String endDateString) throws ParseException;

    /**
     * Hilfsmethode, um mit der DB zu interagieren und für den Bubble chart nötigen Daten zurückzuliefern
     * @param collectionName
     * @param startDateString
     * @param endDateString
     * @return
     * @throws ParseException
     */
    Map<String, Integer> countNamedEntities(String collectionName, String startDateString, String endDateString) throws ParseException;

    /**
     * Hilfsmethode, um mit der DB zu interagieren und für den Sentiment radar chart nötigen Daten zurückzuliefern
     * @param collectionName
     * @param startDateString
     * @param endDateString
     * @return
     * @throws ParseException
     */
    Map<String, Double> calculateSentimentAverages(String collectionName, String startDateString, String endDateString) throws ParseException;

    /**
     * Hilfsmethode, um mit der DB zu interagieren und für den Sunburst radar chart nötigen Daten zurückzuliefern
     * @param collectionName
     * @param startDateString
     * @param endDateString
     * @return
     */
    Map<String, Object> processDocumentsForSunburst(String collectionName, String startDateString, String endDateString);

    /**
     * Gibt das Protokoll basierend auf die id zurück
     * @param protocolId
     * @return
     */
    Set<Protokoll> getProtokollById(String protocolId);
}
