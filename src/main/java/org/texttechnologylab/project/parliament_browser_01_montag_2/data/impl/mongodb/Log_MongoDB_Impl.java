package org.texttechnologylab.project.parliament_browser_01_montag_2.data.impl.mongodb;

import org.bson.Document;
import org.texttechnologylab.project.parliament_browser_01_montag_2.data.Log;
import org.texttechnologylab.project.parliament_browser_01_montag_2.data.impl.factory.InsightBundestagFactory;
import org.texttechnologylab.project.parliament_browser_01_montag_2.helper.StringHelper;


import java.sql.Date;


/**
 * Klasse verantwortlich zur Abbildung der Log-Aktionen als MongoDB-Dokument
 * @author Gurpreet Singh
 */
public class Log_MongoDB_Impl implements Log {
    private Document document;

    /**
     * Constructor
     * @param factory
     * @param document
     */
    public Log_MongoDB_Impl(InsightBundestagFactory factory, Document document) {
        super();
        this.document = document;
    }

    @Override
    public String getId() {
        return document.getString("_id");
    }

    @Override
    public String getAction() {
        return document.getString("action");
    }

    @Override
    public String getParameter() {
        return document.getString("parameters");
    }

    @Override
    public Date getTimeStamp() {
        // Formatiere das Datum zu einer String
        String formattedDate = StringHelper.DATE_FORMAT.format(document.getDate("timestamp"));

        // Ändere die formatierte String zu java.sql.Date
        Date sqlDate = Date.valueOf(formattedDate);
        return sqlDate;
    }
}
