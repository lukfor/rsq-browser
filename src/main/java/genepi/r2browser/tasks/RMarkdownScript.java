package genepi.r2browser.tasks;

import com.esotericsoftware.yamlbeans.YamlWriter;

import java.io.*;
import java.nio.file.Path;
import java.util.Map;
import java.nio.file.*;

public class RMarkdownScript {

    private Path rmdFilePath;

    private String output;

    private Map<String, Object> params;

    public RMarkdownScript(Path rmdFilePath, Map<String, Object> params, String output) {
        this.rmdFilePath = rmdFilePath;
        this.params = params;
        this.output = output;
    }

    private Path writeParamsToTempYaml() throws IOException {
        Path tempFile = Files.createTempFile("params", ".yaml");

        try (Writer writer = new FileWriter(tempFile.toFile())) {
            YamlWriter yamlWriter = new YamlWriter(writer);
            yamlWriter.write(params);
            yamlWriter.close();
        }

        return tempFile;
    }

    public void renderRMarkdown() throws IOException, InterruptedException {
        if (!Files.exists(rmdFilePath)) {
            throw new FileNotFoundException("R Markdown file not found: " + rmdFilePath);
        }

        Path tempYamlFile = writeParamsToTempYaml();

        String command = String.format(
                "Rscript -e \"rmarkdown::render('%s', output_file='%s', params = yaml::yaml.load_file('%s'))\"",
                rmdFilePath.toString().replace("\\", "/"),
                output.replace("\\", "/"),
                tempYamlFile.toString().replace("\\", "/")
        );

        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command("bash", "-c", command);
        processBuilder.directory(rmdFilePath.getParent().toFile());

        Process process = processBuilder.start();
        int exitCode = process.waitFor();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.err.println(line);
            }
        }

        if (exitCode != 0) {
            throw new RuntimeException("R Markdown rendering failed with exit code: " + exitCode);
        }
    }


    public void run() throws IOException, InterruptedException {
        renderRMarkdown();
    }
}
