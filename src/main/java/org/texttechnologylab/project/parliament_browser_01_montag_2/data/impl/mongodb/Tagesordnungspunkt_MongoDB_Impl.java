package org.texttechnologylab.project.parliament_browser_01_montag_2.data.impl.mongodb;

import org.bson.Document;
import org.texttechnologylab.project.parliament_browser_01_montag_2.data.impl.factory.InsightBundestagFactory;
import org.texttechnologylab.project.parliament_browser_01_montag_2.data.impl.file.Tagesordnungspunkt_File_Impl;


/**
 * Klasse verantwortlich zur Abbildung der Tagesordnungspunkte als MongoDB-Dokument
 * @author Taylan Özcelik
 */
public class Tagesordnungspunkt_MongoDB_Impl extends Tagesordnungspunkt_File_Impl {
    private Document document;
    public Tagesordnungspunkt_MongoDB_Impl(InsightBundestagFactory factory, Document document) {
        super(factory);
        this.document = document;
    }
    @Override
    public String getTopId() {
        return document.getString("_id");
    }
}
