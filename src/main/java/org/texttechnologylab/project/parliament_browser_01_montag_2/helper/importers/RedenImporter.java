package org.texttechnologylab.project.parliament_browser_01_montag_2.helper.importers;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.texttechnologylab.project.parliament_browser_01_montag_2.exceptions.InvalidInputException;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashSet;
import java.util.Set;

/**
 * Hilfsklasse, um die Bundestagreden aus der Bundestag Opendata Seite herunterzuladen
 * @author Gurpreet Singh
 */

public class RedenImporter {
    public Set<File> downloadedFiles;
    public static volatile int progress = 0;
    public static volatile String xmlFileName;

    /**
     * Init methode, um den Prozess zu starten
     * @throws IOException
     * @throws InvalidInputException
     */
    public void init() throws IOException, InvalidInputException {

        downloadedFiles = new HashSet<>(0);

        String baseUrl1 = "https://www.bundestag.de/ajax/filterlist/de/services/opendata/543410-543410";
        String baseUrl2 = "https://www.bundestag.de/ajax/filterlist/de/services/opendata/866354-866354";

        int limit = 10;
        boolean noFilterSet = true;
        int offset = 0;

        String downloadDirectory = "rede-downloads";

        // Verzeichnis für heruntergeladenen Dateien
        File directory = new File(downloadDirectory);
        if (!directory.exists()) {
            directory.mkdir();
        }

        downloadedFiles.addAll(downloadRedenFiles(baseUrl1, limit, noFilterSet, offset, downloadDirectory));
        downloadedFiles.addAll(downloadRedenFiles(baseUrl2, limit, noFilterSet, offset, downloadDirectory));

        // dtd file manuell kopieren
        copyDTDFileToDownloads(downloadDirectory);

    }

    /**
     * Webscraping Prozess hier angefangen
     * @param baseUrl
     * @param limit
     * @param noFilterSet
     * @param offset
     * @param downloadDirectory
     * @return
     */
    private Set<File> downloadRedenFiles(String baseUrl, int limit, boolean noFilterSet, int offset, String downloadDirectory) {

        int itemsPerPage = 10; // denn es gibt nur 10 Einträge auf der Seite.

        while (true) {
            String url = baseUrl + String.format("?limit=%d&noFilterSet=%b&offset=%d", limit, noFilterSet, offset);
            try {

                Document document = Jsoup.connect(url).get();
                Elements links = document.select("a[href$=.xml]");

                // All dies wegen Thread Interruption
                String hitsAttribute = document.select("div.meta-slider").attr("data-hits");
                int totalNumberOfItems = 0;
                if (hitsAttribute == null || hitsAttribute.isEmpty()) {
                    System.out.println("Data hits attribute is empty or not found. Because Download process is interrupted");
                    System.out.println("Find the downloaded files in the directory rede-downloads");
                } else {
                    try {
                        totalNumberOfItems = Integer.parseInt(hitsAttribute);
                    } catch (NumberFormatException e) {
                        System.out.println("Data hits attribute is not a valid integer: " + hitsAttribute);
                    }
                }

                int totalNumberOfPages = calculateTotalPages(totalNumberOfItems, itemsPerPage);

                // Das Herunterladen der XML-Dateien
                for (Element link : links) {
                    String fileUrl = link.absUrl("href");
                    File downloadedFile = downloadFile(fileUrl, downloadDirectory);
                    if (downloadedFile != null) {
                        downloadedFiles.add(downloadedFile);
                    }
                }

                if (links.isEmpty()) {
                    break;
                }

                //Pagination
                offset += limit;


                // Progress aktualisieren
                progress = (offset * 100) / (totalNumberOfPages * limit);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return downloadedFiles;
    }

    /**
     * Herunterladen der einzelnen Dateien mit gebaut
     * @param fileUrl
     * @param downloadDirectory
     * @return
     */
    public static File downloadFile(String fileUrl, String downloadDirectory) {
        try {
            if (fileUrl.isEmpty()) {
                throw new IllegalArgumentException("File URL cannot be empty");
            }

            URL url = new URL(fileUrl);
            URLConnection connection = url.openConnection();

            String fileName = fileUrl.substring(fileUrl.lastIndexOf('/') + 1);
            if (fileName.isEmpty()) {
                fileName = "downloaded_file"; // Provide a default name if file name is empty
            }

            File downloadedFile = new File(downloadDirectory + File.separator + fileName);

            try (InputStream inputStream = connection.getInputStream();
                 BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
                 FileOutputStream fileOutputStream = new FileOutputStream(downloadedFile)) {

                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = bufferedInputStream.read(buffer)) != -1) {
                    // Check if the thread is interrupted
                    if (Thread.currentThread().isInterrupted()) {
                        throw new InterruptedException("Download interrupted");
                    }
                    fileOutputStream.write(buffer, 0, bytesRead);
                }

                xmlFileName = fileName;
                //System.out.println("Downloaded: " + xmlFileName);

                return downloadedFile;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (InterruptedException e) {
            System.out.println("Download interrupted");
            // Clean up if needed
            return null;
        }
    }
    /**
     * Hier wird die dtd-Datei aus dem Resources ordner direkt in den Ordner von Bundestagsreden eingefügt
     */
    public static void copyDTDFileToDownloads(String downloadDirectory) throws IOException {

        InputStream inputStream = RedenImporter.class.getClassLoader().getResourceAsStream("dbtplenarprotokoll.dtd");
        if (inputStream == null) {
            throw new FileNotFoundException("Resource not found: dbtplenarprotokoll.dtd");
        }

        Path destinationFilePath = Paths.get(downloadDirectory, "dbtplenarprotokoll.dtd");
        Files.createDirectories(destinationFilePath.getParent());

        Files.copy(inputStream, destinationFilePath, StandardCopyOption.REPLACE_EXISTING);
    }

    private static int calculateTotalPages(int totalNumberOfItems, int itemsPerPage) {
        return (int) Math.ceil((double) totalNumberOfItems / itemsPerPage);
    }

//    public RedenImporter(boolean bDownload) throws IOException {
//        System.out.println("No need to download! ");
//    }


}
