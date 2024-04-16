package org.texttechnologylab.project.parliament_browser_01_montag_2.data.impl.mongodb;

import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS;
import de.tudarmstadt.ukp.dkpro.core.api.ner.type.NamedEntity;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.dependency.Dependency;
import org.apache.ivy.plugins.version.Match;
import org.apache.uima.UIMAException;
import org.apache.uima.cas.impl.XmiCasDeserializer;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.bson.Document;
import org.hucompute.textimager.uima.type.Sentiment;
import org.hucompute.textimager.uima.type.category.CategoryCoveredTagged;
import org.texttechnologylab.project.parliament_browser_01_montag_2.data.*;
import org.texttechnologylab.project.parliament_browser_01_montag_2.data.impl.factory.InsightBundestagFactory;
import org.texttechnologylab.project.parliament_browser_01_montag_2.data.impl.file.Abgeordneter_File_Impl;
import org.texttechnologylab.project.parliament_browser_01_montag_2.data.impl.file.Kommentar_File_Impl;
import org.texttechnologylab.project.parliament_browser_01_montag_2.data.impl.file.Partei_File_Impl;
import org.texttechnologylab.project.parliament_browser_01_montag_2.data.impl.file.Rede_File_Impl;
import org.texttechnologylab.project.parliament_browser_01_montag_2.helper.StringHelper;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.BreakIterator;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Klasse verantwortlich zur Abbildung der Rede als MongoDB-Dokument
 * @author Mihai Paun
 */
public class Rede_MongoDB_Impl extends Rede_File_Impl implements Rede, NLPDocument {
    private Document document;
    private JCas pCas = null;
    private List<String> htmlTextList = new ArrayList<>();
    private List<Double> sentiments = new ArrayList<>();
    private List<String> persons = new ArrayList<>();
    private List<String> locations = new ArrayList<>();
    private List<String> orgs = new ArrayList<>();
    private List<String> miscs = new ArrayList<>();
    private Map<Integer, String> commentsMap = new HashMap<>();


    public Rede_MongoDB_Impl(InsightBundestagFactory factory, Document nlpDocument, Document commentsDocument) {
        super(factory);
        this.document = nlpDocument;

        // populate the Lists with information needed for 3.1 d by checking if the field exists and if it is a List
        if (document.containsKey("sentiment") && document.get("sentiment") instanceof List<?>){
            this.sentiments = (List<Double>) document.get("sentiment");
        }
        if (document.containsKey("persons") && document.get("persons") instanceof List<?>){
            this.persons = (List<String>) document.get("persons");
        }
        if (document.containsKey("locations") && document.get("locations") instanceof List<?>){
            this.locations = (List<String>) document.get("locations");
        }
        if (document.containsKey("organisations") && document.get("organisations") instanceof List<?>){
            this.orgs = (List<String>) document.get("organisations");
        }
        if (document.containsKey("miscellaneous") && document.get("miscellaneous") instanceof List<?>){
            this.miscs = (List<String>) document.get("miscellaneous");
        }

        // get the list of sentences, and the text, and then put them in the correct order and mark the NE with <span>
        List<String> sentences = (List<String>) document.get("sentences");
        String text = document.getString("text");

        // getting a list with the given comments
//        if (commentsDocument != null){
//            String textWithComments = commentsDocument.getString("text");
//            textWithComments = textWithComments.replace("[]", ""); // removing some brackets that ruin the format
//            List<Document> comments = (List<Document>) document.get("kommentar");
//            List<String> commentTexts = new ArrayList<>();
//            for (Document commDoc : comments){
//                commentTexts.add((String) commDoc.get("text"));
//            }
//        }


        // highlighting all NamedEntities in the text
        if (sentences != null){
            // sorting the sentences (so that the correct sentiment can be shown next each sentence)
            Collections.sort(sentences, new Comparator<String>() {
                @Override
                public int compare(String o1, String o2) {
                    return Integer.compare(text.indexOf(o1), text.indexOf(o2));
                }
            });

            // finding the indexes of the comments
            // init a map to store the index of the sentence before the comment and the comment
            if (commentsDocument != null){
                String textWithComments = commentsDocument.getString("text");
                textWithComments = textWithComments.replace("[]", ""); // removing some brackets that ruin the format
                List<Document> comments = (List<Document>) document.get("kommentar");
                List<String> commentTexts = new ArrayList<>();
                for (Document commDoc : comments){
                    commentTexts.add((String) commDoc.get("text"));
                }

                Map<Integer, String> commentPositionMap = new HashMap<>();
                for (String commentText : commentTexts){
                    // find position of comment in the full text
                    int commentPosition = textWithComments.indexOf(commentText);

                    // if the comment was found
                    if (commentPosition != -1) {
                        // find position of the last sentence before the comment
                        int sentenceIndex = -1;
                        for (int i = sentences.size() - 1; i >= 0; i--) {
                            int sentencePosition = textWithComments.lastIndexOf(sentences.get(i));
                            if (sentencePosition != -1 && sentencePosition < commentPosition) {
                                // if condition is met, add to map and exit out of the for loop since there can
                                // only be ONE sentence before the comment
                                sentenceIndex = i;
                                break;
                            }
                        }

                        // comments can be given one after the other, in this case, check if the map already has an entry
                        // at key = sentenceIndex and concatenate the 2 comments if yes:
                        if (commentPositionMap.containsKey(sentenceIndex)) {
                            commentPositionMap.put(sentenceIndex, commentPositionMap.get(sentenceIndex) + "\n" + commentText);
                        } else {
                            commentPositionMap.put(sentenceIndex, commentText);
                        }
                    }
                }

                this.commentsMap = commentPositionMap;

            }

            for (String s : sentences){
                for (String person : persons){
                    s = s.replaceAll("\\b" + person + "\\b", "<span class='person'>" + person + "</span>");
                }
                for (String location : locations){
                    s = s.replaceAll("\\b" + location + "\\b", "<span class='location'>" + location + "</span>");
                }
                for (String org : orgs){
                    s = s.replaceAll("\\b" + org + "\\b", "<span class='organisation'>" + org + "</span>");
                }
                for (String misc : miscs){
                    s = s.replaceAll("\\b" + misc + "\\b", "<span class='misc'>" + misc + "</span>");
                }
                this.htmlTextList.add(s);
            }
        }
    }

    @Override
    public String getId() {
        return document.getString("_id");
    }

    @Override
    public Abgeordneter getRedner() {

        // Hier davon ausgegangen, dass "redner"-feld einfach nested ist.
        Document rednerDocument = document.get("redner", Document.class);
        if (rednerDocument != null) {

            Partei partei = new Partei_File_Impl(rednerDocument.getString("partei"));

            String formattedDate = StringHelper.DATE_FORMAT.format(rednerDocument.getDate("redeDatum"));
            java.sql.Date sqlDate = java.sql.Date.valueOf(formattedDate);

            return new Abgeordneter_File_Impl(
                    partei,
                    rednerDocument.getString("_id"),
                    rednerDocument.getString("titel"),
                    rednerDocument.getString("vorname"),
                    rednerDocument.getString("nachname"),
                    sqlDate
            );
        }
        return null;// Habe ich null gelassen, könnte man aber auch eine Exception werfen.
    }


    @Override
    public String getText() {
        return document.getString("text");
    }

    @Override
    public Set<Kommentar> getKommentar() {
        Set<Kommentar> kommentarSet = new HashSet<>();

        // Hier auch ähnliche Vorgehensweise wie oben, weil "kommentar" eigentlich auch nested ist
        List<Document> kommentarList = document.getList("kommentar", Document.class);

        if (kommentarList != null) {
            for (Document kommentarDoc : kommentarList) {
                String commentText = kommentarDoc.getString("text");
                Kommentar kommentar = new Kommentar_File_Impl(commentText);

                kommentarSet.add(kommentar);
            }
        }

        return kommentarSet;
    }
    //Würde in der toCas schauen, ob die Rede bereits analysiert wurde (Also, ob eine serialsiertes jcas in der db existiert)...
    //Wenn eins existiert: deserialisieren.
    //Wenn keins existiert: Neues Jcas erstellen.
    @Override
    public JCas toCAS() throws UIMAException {
        if (pCas == null) {
            if (document.containsKey("serCas")) {
                String serializedData = document.getString("serCas");

                if (serializedData != null) {
                    byte[] serializedBytes = Base64.getDecoder().decode(serializedData);

                    try (ByteArrayInputStream inputStream = new ByteArrayInputStream(serializedBytes)) {
                        // Initialize pCas before deserialization
                        pCas = JCasFactory.createText(new String(serializedBytes));
                        // Deserialize the CAS data directly into the existing JCas
                        XmiCasDeserializer.deserialize(inputStream, pCas.getCas(), true);

                    } catch (IOException | SAXException e) {
                        e.printStackTrace();
                    }
                } else {
                }
            } else {
                this.pCas = JCasFactory.createText(document.getString("text"), "de");
            }
        }
        return pCas;
    }


    @Override
    public List<Token> getTokens() throws UIMAException {
        return JCasUtil.select(this.toCAS(), Token.class).stream().collect(Collectors.toList());
    }

    @Override
    public List<Sentence> getSentences() throws UIMAException {
        return JCasUtil.select(this.toCAS(), Sentence.class).stream().collect(Collectors.toList());
    }

    @Override
    public List<POS> getPOS() throws UIMAException {
        return JCasUtil.select(this.toCAS(), POS.class).stream().collect(Collectors.toList());
    }

    @Override
    public List<NamedEntity> getNamedEntities() throws UIMAException {
        return JCasUtil.select(this.toCAS(), NamedEntity.class).stream().collect(Collectors.toList());
    }

    @Override
    public List<Dependency> getDependency() throws UIMAException {
        return JCasUtil.select(this.toCAS(), Dependency.class).stream().collect(Collectors.toList());
    }

    @Override
    public List<Sentiment> getSentiments() throws UIMAException {
        return JCasUtil.select(this.toCAS(), Sentiment.class).stream().collect(Collectors.toList());
    }
    @Override
    public List<String> getNouns() throws UIMAException {
        return JCasUtil.select(this.toCAS(), POS.class)
                .stream()
                .filter(pos -> pos.getPosValue().equals("NN"))
                .map(pos -> pos.getCoveredText())
                .collect(Collectors.toList());    }

    @Override
    public List<String> getConjunctions() throws UIMAException {
        return JCasUtil.select(this.toCAS(), POS.class)
                .stream()
                .filter(pos -> pos.getPosValue().equals("KON"))
                .map(pos -> pos.getCoveredText())
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getArticles() throws UIMAException {
        return JCasUtil.select(this.toCAS(), POS.class)
                .stream()
                .filter(pos -> pos.getPosValue().equals("ART"))
                .map(pos -> pos.getCoveredText())
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getAdverbs() throws UIMAException {
        return JCasUtil.select(this.toCAS(), POS.class)
                .stream()
                .filter(pos -> pos.getPosValue().equals("ADV"))
                .map(pos -> pos.getCoveredText())
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getVerbs() throws UIMAException {
        return JCasUtil.select(this.toCAS(), POS.class)
                .stream()
                .filter(pos -> pos.getPosValue().equals("VVFIN"))
                .map(pos -> pos.getCoveredText())
                .collect(Collectors.toList());
    }

    @Override
    public HashMap<String, Double> getTopics() throws UIMAException {
        // innit the return map and the speech len
        HashMap<String, Double> rMap = new HashMap<>();
        int speechLen = this.getText().length();

        // access the annotations created by ParlBert
        Collection<CategoryCoveredTagged> categoryCoveredTaggeds =  JCasUtil.select(this.toCAS(), CategoryCoveredTagged.class).stream()
                .sorted((c1, c2) -> c1.getBegin()-c2.getBegin()).collect(Collectors.toList());
        for (CategoryCoveredTagged categoryCoveredTagged : categoryCoveredTaggeds){
            // add the text topics/scores to the return map
            if (categoryCoveredTagged.getBegin() == 0 && categoryCoveredTagged.getEnd() == speechLen){
                rMap.put(categoryCoveredTagged.getValue(), categoryCoveredTagged.getScore());
            }
        }

        return rMap;
    }

    /**
     * Method for accessing the Sentiment values saved in the DB
     * @return a List with the sentiment values saved in the DB
     */
    public List<Double> getSentimentValues(){
        return this.sentiments;
    }

    /**
     * Method for accessing the Persons NE saved in the DB
     * @return A List with the Persons NE
     */
    public List<String> getPersons(){
        return this.persons;
    }

    /**
     * Method for accessing the Locations NE saved in the DB
     * @return A list with the Locations NE
     */
    public List<String> getLocations(){
        return this.locations;
    }

    /**
     * Method for accessing the Organisations NE saved in the DB
     * @return A list with the Organisations NE
     */
    public List<String> getOrgs(){
        return this.orgs;
    }

    /**
     * Method for accessing the Miscellaneous NE saved in the DB
     * @return A list with the Miscellaneous NE
     */
    public List<String> getMiscs(){
        return this.miscs;
    }

    /**
     * Method used for accessing the html-processed sentences (NamedEntities are marked with <span>) of the speech
     * @return
     */
    public List<String> getHtmlTextList(){
        return this.htmlTextList;
    }

    /**
     * Method used for accessing the map of indexed and comments. Useful for displaying comments at their correct position
     * @return
     */
    public Map<Integer, String> getCommentsMap(){ return this.commentsMap; }


}
