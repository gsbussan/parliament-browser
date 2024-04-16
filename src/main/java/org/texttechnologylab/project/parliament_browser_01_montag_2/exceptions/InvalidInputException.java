package org.texttechnologylab.project.parliament_browser_01_montag_2.exceptions;
/**
 * Exception für die ungültige Eingabe durch den Nutzer
 * @author Gurpreet Singh
 */
public class InvalidInputException extends Exception {
    /**
     * Exception kann abgefangen und entsprechend geworfen werden
     * @param exceptionMessage Message
     */
    public InvalidInputException(String exceptionMessage){
        super(exceptionMessage);
    }
}
