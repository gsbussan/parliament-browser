package org.texttechnologylab.project.parliament_browser_01_montag_2.exceptions;

/**
 * Exception für die ungültige parametrisierte Datum bei den Tests
 * @author Gurpreet Singh
 */
public class DatumNichtGefundenException extends Exception {
    /**
     * Exception kann abgefangen und entsprechend geworfen werden
     * @param message Message
     */
    public DatumNichtGefundenException(String message) {
        super(message);
    }
}
