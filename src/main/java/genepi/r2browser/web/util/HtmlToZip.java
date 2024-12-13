package genepi.r2browser.web.util;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class HtmlToZip {

    /**
     * Compresses the given HTML file into a ZIP file.
     *
     * @param htmlFilePath  The path to the HTML file.
     * @param outputZipPath The path where the ZIP file will be created.
     * @throws IOException If an I/O error occurs.
     */
    public static void createZipFromHtml(String htmlFilePath, String outputZipPath) throws IOException {
        File htmlFile = new File(htmlFilePath);

        // Validate the HTML file
        if (!htmlFile.exists() || htmlFile.isDirectory()) {
            throw new FileNotFoundException("The specified HTML file does not exist or is a directory.");
        }

        // Create the ZIP file
        try (FileOutputStream fos = new FileOutputStream(outputZipPath);
             ZipOutputStream zos = new ZipOutputStream(fos);
             FileInputStream fis = new FileInputStream(htmlFile)) {

            // Add the HTML file to the ZIP
            ZipEntry zipEntry = new ZipEntry(htmlFile.getName());
            zos.putNextEntry(zipEntry);

            // Write the file contents to the ZIP
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) >= 0) {
                zos.write(buffer, 0, length);
            }

            zos.closeEntry();
        }
    }
}
