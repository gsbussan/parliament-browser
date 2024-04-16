package org.texttechnologylab.project.parliament_browser_01_montag_2.helper;



import org.texttechnologylab.project.parliament_browser_01_montag_2.exceptions.InvalidFileTypeException;
import org.texttechnologylab.project.parliament_browser_01_montag_2.exceptions.InvalidInputException;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

/**
 * Klasse verantwortlich für das Einlesen der Pfadangaben, wo die Dateien sich befinden
 * @author Gurpreet Singh
 */
public class DirectoryReader {
    Set<File> fileList;
    public DirectoryReader() {
        fileList = new HashSet<>(0);
    }
    /**
     * Schleife über alle XMLDateien
     * @param directoryPath Path
     * @return fileList
     * @throws InvalidInputException Exception
     * @throws InvalidFileTypeException Exception
     */
    public Set<File> directoryLoopForXML(String directoryPath) throws InvalidInputException, InvalidFileTypeException {

        if (directoryPath == null || directoryPath.isEmpty()) {
            throw new InvalidInputException("Invalid input: Directory path is null or empty");
        }

        File file = new File(directoryPath);

        if (!file.isDirectory()) {
            throw new InvalidInputException("Invalid input: Path is not a directory");
        }

        //Einlesen und Speicherung der Dateien mit XML oder xml Dateiendung
        File[] xmlFiles = file.listFiles((f, name) -> name.endsWith(".XML") || name.endsWith(".xml"));

        if(xmlFiles != null){
            for (File file1: xmlFiles){
                if (file1.isFile() && (file1.getName().endsWith(".xml") || file1.getName().endsWith(".XML") ||
                        file1.getName().endsWith(".DTD") )) {
                    fileList.add(file1);
                } else {
                    throw new InvalidFileTypeException("Invalid file type detected: " + file.getName());
                }
            }
        }

        return  fileList;
    }
    /**
     * Get Liste von Dateien mit Reden
     * @param directoryName Path
     * @return List<File>
     */
    public static Set<File> getRedenFiles(String directoryName) throws URISyntaxException {
        ClassLoader classLoader = DirectoryReader.class.getClassLoader();
        Path resourceDirectory = Paths.get(classLoader.getResource(directoryName).toURI());
        try {
            return new DirectoryReader().directoryLoopForXML(String.valueOf(resourceDirectory));
        } catch (InvalidInputException | InvalidFileTypeException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get Liste von Stammdatendateien
     * @param directoryName Path
     * @return List<File>
     */
    public static Set<File> getStammdatenFiles(String directoryName) throws URISyntaxException {
        ClassLoader classLoader = DirectoryReader.class.getClassLoader();
        Path resourceDirectory = Paths.get(classLoader.getResource(directoryName).toURI());
        try {
            return new DirectoryReader().directoryLoopForXML(String.valueOf(resourceDirectory));
        } catch (InvalidInputException | InvalidFileTypeException e) {
            throw new RuntimeException(e);
        }
    }


}
