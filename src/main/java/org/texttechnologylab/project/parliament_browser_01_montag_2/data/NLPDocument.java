package org.texttechnologylab.project.parliament_browser_01_montag_2.data;

import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS;
import de.tudarmstadt.ukp.dkpro.core.api.ner.type.NamedEntity;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.dependency.Dependency;
import org.apache.uima.UIMAException;
import org.hucompute.textimager.uima.type.Sentiment;

import java.util.HashMap;
import java.util.List;

/**
 * Schnittstelle zur Abbildung der Ergebnisse der NLP-Analyse
 * unter Verwendung eines Interfaces
 * @author Gurpreet Singh
 */
public interface NLPDocument {
    /**
     * Gibt die Tokens eines verarbeiteten Texts zurück
     * @return
     * @throws UIMAException
     */
    List<Token> getTokens() throws UIMAException;

    /**
     * Gibt die Sentences eines verarbeiteten Texts zurück
     * @return
     * @throws UIMAException
     */
    List<Sentence> getSentences() throws UIMAException;

    /**
     * Gibt Part of Speech zurück
     * @return
     * @throws UIMAException
     */
    List<POS> getPOS() throws UIMAException;

    /**
     * Gibt die Named Entities eines verarbeiteten Texts zurück
     * @return
     * @throws UIMAException
     */
    List<NamedEntity> getNamedEntities() throws UIMAException;

    /**
     * Gibt die Dependencies eines verarbeiteten Texts zurück
     * @return
     * @throws UIMAException
     */
    List<Dependency> getDependency() throws UIMAException;

    /**
     * Gibt die Sentiments des verarbeiteten Texts zurück
     * Es gibt auch die Sentiments für einzelne Sätze
     * @return
     * @throws UIMAException
     */
    List<Sentiment> getSentiments() throws UIMAException;

    /**
     * Gibt die Nomen eines verarbeiteten Texts zurück
     * Mithilfe Part of Speech
     * mehr dazu in MongoDBConnectionHandler
     * @return
     * @throws UIMAException
     */
    List<String> getNouns() throws UIMAException;
    /**
     * Gibt die Konjunktionen eines verarbeiteten Texts zurück
     * Mithilfe Part of Speech
     * mehr dazu in MongoDBConnectionHandler
     * @return
     * @throws UIMAException
     */
    List<String> getConjunctions() throws UIMAException;
    /**
     * Gibt die Artikel eines verarbeiteten Texts zurück
     * Mithilfe Part of Speech
     * mehr dazu in MongoDBConnectionHandler
     * @return
     * @throws UIMAException
     */
    List<String> getArticles() throws UIMAException;
    /**
     * Gibt die Adverben eines verarbeiteten Texts zurück
     * Mithilfe Part of Speech
     * mehr dazu in MongoDBConnectionHandler
     * @return
     * @throws UIMAException
     */
    List<String> getAdverbs() throws UIMAException;
    /**
     * Gibt die Verben eines verarbeiteten Texts zurück
     * Mithilfe Part of Speech
     * mehr dazu in MongoDBConnectionHandler
     * @return
     * @throws UIMAException
     */
    List<String> getVerbs() throws UIMAException;

    /**
     * Method used for returning the topics created with ParlBert
     * @return a map of the topics and their respective values
     */
    HashMap<String, Double> getTopics() throws UIMAException;

}
