package org.texttechnologylab.project.parliament_browser_01_montag_2.helper;

import org.apache.commons.compress.compressors.CompressorException;
import org.apache.uima.UIMAException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.util.InvalidXMLException;
import org.texttechnologylab.project.parliament_browser_01_montag_2.data.Rede;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Set;

/**
 * Interface for the NLP class
 * @author Mihai Paun
 */
public interface NLP_Interface {
    /**
     * Method for creating a composer when using Docker locally
     */
    void createLocalComposer() throws IOException, URISyntaxException, UIMAException, SAXException, CompressorException;

    /**
     * Method for creating a composer when using Docker on WSL
     */
    void createWSLComposer() throws IOException, URISyntaxException, CompressorException, InvalidXMLException, SAXException;

    /**
     * Method for performing the NLP analysis on a speech using Docker locally
     * @param rede The parliament speech we want to analyse
     * @return The JCas object representing the analysed speech
     */
    JCas processSpeechLocal(Rede rede) throws Exception;

    /**
     * Method for performing the NLP analysis on a speech using Docker via WSL
     * @param rede The parliament speech we want to analyse
     * @return The JCas object representing the analysed speech
     */
    JCas processSpeechWSL(Rede rede) throws Exception;

    /**
     * Method for serializing a JCas object so that we can store it in the DB
     * @param pCas The JCas object we want to serialize
     * @return The XMI String representing the JCas object
     */
    String serializeJCas(JCas pCas) throws IOException;

    /**
     * Method for deserializing an XMI String representing a JCas object in the DB
     * @param xmiString The XMI String that corresponds to the JCas we want to access
     * @return The deserialized JCas object
     */
    JCas deserializeJCas(String xmiString) throws UIMAException, IOException;

    /**
     * MMethod for accessing a speech's sentiment values
     * @param pCas The analysed speech
     * @return A list of sentiment values. item[0] is the overall speech sentiment, item[1] is the sentiment for the
     * first sentence and so on...
     */
    List<Double> getSentiments(JCas pCas);

    /**
     * Method for accessing a speech's Nomen tokens (NN)
     * @param pCas The analysed speech
     * @return A set with all Nomen tokens in the speech
     */
    Set<String> getNomen(JCas pCas);

    /**
     * Method for accessing a speech's NamedEntities tokens (NE)
     * @param pCas The analysed speech
     * @return A set with all NamedEntities tokens in the speech
     */
    Set<String> getNamedEntities(JCas pCas);
}
