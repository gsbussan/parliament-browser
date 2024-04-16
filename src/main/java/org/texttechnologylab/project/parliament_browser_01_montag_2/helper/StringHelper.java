package org.texttechnologylab.project.parliament_browser_01_montag_2.helper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;

/**
 * Hilfeklassen, um das Datum zu formatieren
 * Quelle: Hier habe ich mich an der Lösung von Herrn Abrami orientiert.
 */
public class StringHelper {
    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    public static final SimpleDateFormat dateFormat2 = new SimpleDateFormat("dd.MM.yyyy");
    public static final  DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    public static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("H:mm");
}
