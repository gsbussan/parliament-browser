package org.texttechnologylab.project.parliament_browser_01_montag_2.helper;

import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS;
import de.tudarmstadt.ukp.dkpro.core.api.ner.type.NamedEntity;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.dependency.Dependency;
import org.apache.commons.compress.compressors.CompressorException;
import org.apache.uima.UIMAException;
import org.apache.uima.cas.SerialFormat;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.util.CasIOUtils;
import org.apache.uima.util.InvalidXMLException;
import org.hucompute.textimager.uima.type.Sentiment;
import org.hucompute.textimager.uima.type.category.CategoryCoveredTagged;
import org.texttechnologylab.DockerUnifiedUIMAInterface.DUUIComposer;
import org.texttechnologylab.DockerUnifiedUIMAInterface.driver.DUUIDockerDriver;
import org.texttechnologylab.DockerUnifiedUIMAInterface.driver.DUUIRemoteDriver;
import org.texttechnologylab.DockerUnifiedUIMAInterface.lua.DUUILuaContext;
import org.texttechnologylab.project.parliament_browser_01_montag_2.data.Rede;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Class that handles the NLP analysis of parliament speeches
 * @author Mihai Paun
 */
public class NLP_Implementation implements NLP_Interface{
    private DUUIComposer localComposer;
    private DUUIComposer wslComposer;

    public JCas processTest(String sTest) throws Exception {
        if (sTest.isEmpty()){
            throw new IllegalArgumentException("Speech cannot be empty! ");
        }

        JCas jcas = JCasFactory.createText(sTest, "de");
        if (jcas == null){
            throw new IllegalStateException("JCas cannot be null! ");
        }

        if (wslComposer == null){
            throw new IllegalStateException("Composer has not been initiated! ");
        }

        wslComposer.run(jcas);
        return jcas;
    }

    /**
     * Method for processing a speech, without having to manually choose between the 2 methods
     * @param localOrWSL a boolean which helps us choose between local (true) and WSL (false) processing
     * @param rede the speech we want to process
     * @return the processed speech as a JCas
     * @throws Exception
     */
    public JCas processSpeech(boolean localOrWSL, Rede rede) throws Exception {
        if (localOrWSL){
            createLocalComposer();
            return processSpeechLocal(rede);
        }
        else {
            createWSLComposer();
            return processSpeechWSL(rede);
        }
    }

    @Override
    public void createLocalComposer() throws IOException, URISyntaxException, UIMAException, SAXException, CompressorException {
        int iWorkers = 1;
        DUUILuaContext ctx = new DUUILuaContext().withJsonLibrary();

        // initialising the composer
        localComposer = new DUUIComposer()
                .withSkipVerification(true)
                .withLuaContext(ctx)
                .withWorkers(iWorkers);

        // adding the docker drivers needed for the analysis
        DUUIDockerDriver dockerDriver = new DUUIDockerDriver();
        localComposer.addDriver(dockerDriver);

        // adding the spaCy image
        localComposer.add(new DUUIDockerDriver
                .Component("docker.texttechnologylab.org/textimager-duui-spacy-single-de_core_news_sm:0.1.4")
                .withScale(iWorkers)
                .build());

        // adding the GerVader image
        localComposer.add(new DUUIDockerDriver
                .Component("docker.texttechnologylab.org/gervader_duui:latest")
                .withScale(iWorkers)
                .withParameter("selection", "text")
                .build());

        // adding the ParlBert image
        localComposer.add(new DUUIDockerDriver
                .Component("docker.texttechnologylab.org/parlbert-topic-german:latest")
                .withScale(iWorkers)
                .build());
    }

    @Override
    public void createWSLComposer() throws IOException, URISyntaxException, CompressorException, InvalidXMLException, SAXException {
        int iWorkers = 1;
        DUUILuaContext ctx = new DUUILuaContext().withJsonLibrary();

        // initialising the composer
        wslComposer = new DUUIComposer()
                .withSkipVerification(true)
                .withLuaContext(ctx)
                .withWorkers(iWorkers);

        // adding the Docker driver
        DUUIRemoteDriver remoteDriver = new DUUIRemoteDriver();
        wslComposer.addDriver(remoteDriver);

        // adding the Docker images
        wslComposer.add(new DUUIRemoteDriver.Component("http://127.0.0.1:1000") // the spaCy container running on port 1000
                .withScale(iWorkers)
                .build());
        wslComposer.add(new DUUIRemoteDriver.Component("http://127.0.0.1:1001") // the GerVader container running on port 1001
                .withScale(iWorkers)
                .withParameter("selection", "text")
                .build());
        wslComposer.add(new DUUIRemoteDriver.Component("http://127.0.0.1:1002") // the ParlBert container running on port 1002
                .withScale(iWorkers)
                .build());
    }

    @Override
    public JCas processSpeechLocal(Rede rede) throws Exception {
        // making sure the speech is not empty
        if (rede == null){
            throw new IllegalArgumentException("Speech cannot be null! ");
        }

        JCas jcas = rede.toCAS();
        // making sure the JCas object is not null
        if (jcas == null){
            throw new IllegalStateException("toCAS() returned null! ");
        }

        // making sure the composer has been initialised
        if (localComposer == null){
            throw new IllegalStateException("Composer has not been initialised! ");
        }

        // running the analysis
        localComposer.run(jcas);

        return jcas;
    }

    @Override
    public JCas processSpeechWSL(Rede rede) throws Exception {
        // making sure the speech is not empty
        if (rede == null){
            throw new IllegalArgumentException("Speech cannot be null! ");
        }

        JCas jcas = rede.toCAS();
        // making sure the JCas object is not null
        if (jcas == null){
            throw new IllegalStateException("toCAS() returned null! ");
        }

        // making sure the composer has been initialised
        if (wslComposer == null){
            throw new IllegalStateException("Composer has not been initialised! ");
        }

        // running the analysis
        wslComposer.run(jcas);

        return jcas;
    }

    @Override
    public String serializeJCas(JCas pCas) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        CasIOUtils.save(pCas.getCas(), baos, SerialFormat.XMI);
        String xmiString = baos.toString(StandardCharsets.UTF_8.name());

        return xmiString;
    }

    @Override
    public JCas deserializeJCas(String xmiString) throws UIMAException, IOException {
        JCas desJCas = JCasFactory.createJCas();
        InputStream is = new ByteArrayInputStream(xmiString.getBytes(StandardCharsets.UTF_8));
        CasIOUtils.load(is, desJCas.getCas());

        return desJCas;
    }

    @Override
    public List<Double> getSentiments(JCas pCas) {
        List<Double> rList = new ArrayList<>();

        JCasUtil.select(pCas, Sentiment.class).forEach(sentiment -> {
            // print test
//            System.out.println("Sentiment: " + sentiment.getSentiment());
            rList.add(sentiment.getSentiment());
        });

        return rList;
    }

    @Override
    public Set<String> getNomen(JCas pCas) {
        Set<String> rSet = new HashSet<>();

        JCasUtil.select(pCas, Sentence.class).forEach(sentence -> {
            for (Token t : JCasUtil.selectCovered(pCas, Token.class, sentence)){
                if (t.getPosValue().equals("NN")){
                    rSet.add(t.getText());
                }
            }
        });

        return rSet;
    }

    @Override
    public Set<String> getNamedEntities(JCas pCas) {
        Set<String> rSet = new HashSet<>();

        JCasUtil.select(pCas, Sentence.class).forEach(sentence -> {
            for (Token t : JCasUtil.selectCovered(pCas, Token.class, sentence)){
                if (t.getPosValue().equals("NE")){
                    rSet.add(t.getText());
                }
            }
        });

        return rSet;
    }

    /**
     * Getter for the topics and their values
     * @param pCas The NLP processed speech
     * @return
     */
    public HashMap<String, Double> getTopics(JCas pCas){
        // init the needed variables
        HashMap<String, Double> rMap = new HashMap<>();
        int speechLen = pCas.getDocumentText().length();
        boolean fullTextAnalysed = false; // needed in order to check if annotation from 0 to speechLen exists

        Collection<CategoryCoveredTagged> categoryCoveredTaggeds = JCasUtil.select(pCas, CategoryCoveredTagged.class).stream()
                .sorted((c1, c2) -> c1.getBegin()-c2.getBegin()).collect(Collectors.toList());
        for (CategoryCoveredTagged categoryCoveredTagged : categoryCoveredTaggeds){
            if (categoryCoveredTagged.getBegin() == 0 && categoryCoveredTagged.getEnd() == speechLen){
                rMap.put(categoryCoveredTagged.getValue(), categoryCoveredTagged.getScore());
                fullTextAnalysed = true;
            }

            if (!fullTextAnalysed){
                rMap.put(categoryCoveredTagged.getValue(), categoryCoveredTagged.getScore());
            }
        }

        return rMap;
    }

    /**
     * Getter for all tokens of the CAS
     * @param pCas The CAS we want to get tokens from
     * @return
     */
    public Set<String> getTokens(JCas pCas){
        Set<String> rSet = new HashSet<>();

        // add all tokens to the set
        for (Token t : JCasUtil.select(pCas, Token.class)){
            rSet.add(t.getText());
        }

        return rSet;
    }

    /**
     * Method for getting the sentences in a speech
     * @param pCas The analyzed speech
     * @return
     */
    public Set<String> getSentences(JCas pCas){
        Set<String> rSet = new HashSet<>();

        // add the sentences to the set
        for (Sentence s : JCasUtil.select(pCas, Sentence.class)){
            rSet.add(s.getCoveredText());
        }

        return rSet;
    }

    /**
     * Method for getting all PartsOfSpeech present in the text
     * @param pCas The analyzed text
     * @return
     */
    public Set<String> getPOS(JCas pCas){
        Set<String> rSet = new HashSet<>();

        // add the Parts Of Speech to the set
        for (POS pos : JCasUtil.select(pCas, POS.class)){
            rSet.add(pos.getCoveredText());
        }

        return rSet;
    }

    /**
     * Method for getting all dependencies in a text
     * @param pCas The analyzed text
     * @return
     */
    public Set<String> getDependencies(JCas pCas){
        Set<String> rSet = new HashSet<>();

        for (Dependency d : JCasUtil.select(pCas, Dependency.class)){
            rSet.add(d.getCoveredText());
        }

        return rSet;
    }

    /**
     * Method for getting all conjunctions in a text
     * @param pCas The analyzed text
     * @return
     */
    public Set<String> getConjunctions(JCas pCas) {
        Set<String> rSet = new HashSet<>();

        JCasUtil.select(pCas, Sentence.class).forEach(sentence -> {
            for (Token t : JCasUtil.selectCovered(pCas, Token.class, sentence)){
                if (t.getPosValue().equals("KON")){
                    rSet.add(t.getText());
                }
            }
        });

        return rSet;
    }

    /**
     * Method for getting all articles in a text
     * @param pCas The analyzed text
     * @return
     */
    public Set<String> getArticles(JCas pCas) {
        Set<String> rSet = new HashSet<>();

        JCasUtil.select(pCas, Sentence.class).forEach(sentence -> {
            for (Token t : JCasUtil.selectCovered(pCas, Token.class, sentence)){
                if (t.getPosValue().equals("ART")){
                    rSet.add(t.getText());
                }
            }
        });

        return rSet;
    }

    /**
     * Method for getting all adverbs in a text
     * @param pCas The analyzed text
     * @return
     */
    public Set<String> getAdverbs(JCas pCas) {
        Set<String> rSet = new HashSet<>();

        JCasUtil.select(pCas, Sentence.class).forEach(sentence -> {
            for (Token t : JCasUtil.selectCovered(pCas, Token.class, sentence)){
                if (t.getPosValue().equals("ADV")){
                    rSet.add(t.getText());
                }
            }
        });

        return rSet;
    }

    /**
     * Method for getting all verbs in a text
     * @param pCas The analyzed text
     * @return
     */
    public Set<String> getVerbs(JCas pCas) {
        Set<String> rSet = new HashSet<>();

        JCasUtil.select(pCas, Sentence.class).forEach(sentence -> {
            for (Token t : JCasUtil.selectCovered(pCas, Token.class, sentence)){
                if (t.getPosValue().equals("VVFIN")){
                    rSet.add(t.getText());
                }
            }
        });

        return rSet;
    }

    /**
     * Method for getting the persons mentioned in a text
     * @param pCas The analyzed text
     * @return
     */
    public Set<String> getPersons(JCas pCas){
        Set<String> rSet = new HashSet<>();

        JCasUtil.select(pCas, NamedEntity.class).forEach(namedEntity -> {
//            System.out.println("NE value: " + namedEntity.getValue());
            if (namedEntity.getValue().equals("PER")){
                rSet.add(namedEntity.getCoveredText());
            }
        });

        return rSet;
    }

    /**
     * Method used for getting the locations mentioned in a text
     * @param pCas The analyzed text
     * @return
     */
    public Set<String> getLocations(JCas pCas){
        Set<String> rSet = new HashSet<>();

        JCasUtil.select(pCas, NamedEntity.class).forEach(namedEntity -> {
            System.out.println("NE value: " + namedEntity.getValue());
            if (namedEntity.getValue().equals("LOC")){
                rSet.add(namedEntity.getCoveredText());
            }
        });

        return rSet;
    }

    /**
     * Method used for getting the organisations mentioned in a speech
     * @param pCas The analyzed speech
     * @return
     */
    public Set<String> getOrganisations(JCas pCas){
        Set<String> rSet = new HashSet<>();

        JCasUtil.select(pCas, NamedEntity.class).forEach(namedEntity -> {
            System.out.println("NE value: " + namedEntity.getValue());
            if (namedEntity.getValue().equals("ORG")){
                rSet.add(namedEntity.getCoveredText());
            }
        });

        return rSet;
    }

    /**
     * Method used for getting the MISC tokens in a text
     * @param pCas The analyzed text
     * @return
     */
    public Set<String> getMisc(JCas pCas){
        Set<String> rSet = new HashSet<>();

        JCasUtil.select(pCas, NamedEntity.class).forEach(namedEntity -> {
            System.out.println("NE value: " + namedEntity.getValue());
            if (namedEntity.getValue().equals("MISC")){
                rSet.add(namedEntity.getCoveredText());
            }
        });

        System.out.println("Stupid print test! ");
        System.out.println(rSet.toString());
        return rSet;
    }
}
