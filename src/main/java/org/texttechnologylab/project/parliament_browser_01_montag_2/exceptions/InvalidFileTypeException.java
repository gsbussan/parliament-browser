package org.texttechnologylab.project.parliament_browser_01_montag_2.exceptions;
/**
 * Exception für die ungültige eingegebene Datei-extensions
 * @author Gurpreet Singh
 */
public class InvalidFileTypeException extends Exception {
    /**
     * Exception kann abgefangen und entsprechend geworfen werden
     * @param exceptionMessage Message
     */
    public InvalidFileTypeException(String exceptionMessage){
        super(exceptionMessage);
    }
}
