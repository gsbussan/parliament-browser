package org.texttechnologylab.project.parliament_browser_01_montag_2.helper.importers;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.texttechnologylab.project.parliament_browser_01_montag_2.helper.importers.RedenImporter.downloadFile;

/**
 * Hilfsklasse, um die MdB Stammdaten aus der Bundestag Opendata Seite herunterzuladen
 * @author Gurpreet Singh
 */
public class AbgeordneterImporter {
    public String extractedDirectory;

    /**
     * Constructor, um direkt den Import zu starten, nach dem Aufruf der Klasse.
     */
    public AbgeordneterImporter() {
        String baseUrlDTD = "https://www.bundestag.de/services/opendata";
        String resourceUrl = "https://www.bundestag.de";
        String downloadDirectory = "abgeordneten-downloads";
        extractedDirectory = "extracted-abgeordneten";// Directory to store downloaded files

        try {
            Document document = Jsoup.connect(baseUrlDTD).get();
            Elements elements = document.select("a[href]");

            // Verzeichnis erstellen, wenn es nicht da ist.
            File directory = new File(downloadDirectory);
            if (!directory.exists()) {
                directory.mkdir();
            }

            for (Element element : elements) {
                String href = element.attr("href");
                if (href.endsWith(".zip")) {

                    unzip(downloadDirectory + File.separator + downloadFile(resourceUrl + href,
                            downloadDirectory).getName(), extractedDirectory);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Constructor um unnötige Downloads zu vermeiden
     * @param bDownload
     */
    public AbgeordneterImporter(boolean bDownload){
        System.out.println("No need to download! ");
    }

    /**
     * Unzip der Dateien
     * @param zipFilePath
     * @param destDirectory
     * @throws IOException
     */
    public static void unzip(String zipFilePath, String destDirectory) throws IOException {
        File destDir = new File(destDirectory);
        // create output directory if it doesn't exist
        if (!destDir.exists()) {
            destDir.mkdir();
        }
        ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFilePath));
        ZipEntry entry = zipIn.getNextEntry();
        // iterates over entries in the zip file
        while (entry != null) {
            String filePath = destDirectory + File.separator + entry.getName();
            if (!entry.isDirectory()) {
                // if the entry is a file, extract it
                extractFile(zipIn, filePath);
            } else {
                // if the entry is a directory, create the directory
                File dir = new File(filePath);
                dir.mkdir();
            }
            zipIn.closeEntry();
            entry = zipIn.getNextEntry();
        }
        zipIn.close();
    }

    /**
     * Extrahieren der Dateien
     * @param zipIn
     * @param filePath
     * @throws IOException
     */
    private static void extractFile(ZipInputStream zipIn, String filePath) throws IOException {
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
        byte[] bytesIn = new byte[4096];
        int read;
        while ((read = zipIn.read(bytesIn)) != -1) {
            bos.write(bytesIn, 0, read);
        }
        bos.close();
    }


}
