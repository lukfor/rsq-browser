package genepi.r2browser.tasks;

import com.esotericsoftware.yamlbeans.YamlWriter;

import java.io.*;
import java.nio.file.*;
import java.util.Map;

public class Quarto {

    private Path qmdFilePath;
    private String output;
    private Map<String, Object> params;
    private Path workspacePath;

    public Quarto(Path qmdFilePath, Map<String, Object> params, String output, Path workspacePath) {
        this.qmdFilePath = qmdFilePath;
        this.params = params;
        this.output = output;
        this.workspacePath = workspacePath;
    }

    private Path writeParamsToTempYaml() throws IOException {
        // Create the params.yaml file in the workspace
        Path paramsYamlPath = workspacePath.resolve("params.yaml");

        try (Writer writer = new FileWriter(paramsYamlPath.toFile())) {
            YamlWriter yamlWriter = new YamlWriter(writer);
            yamlWriter.write(params);
            yamlWriter.close();
        }

        return paramsYamlPath;
    }

    private void copyFolderToWorkspace() throws IOException {
        Files.walk(qmdFilePath).forEach(sourcePath -> {
            try {
                Path targetPath = workspacePath.resolve(qmdFilePath.relativize(sourcePath));
                if (Files.isDirectory(sourcePath)) {
                    Files.createDirectories(targetPath);
                } else {
                    Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void render() throws IOException, InterruptedException {
        if (!Files.exists(qmdFilePath)) {
            throw new FileNotFoundException("Quarto folder not found: " + qmdFilePath);
        }

        // Copy the folder to workspace
        copyFolderToWorkspace();

        // Write the params.yaml file to the workspace
        Path paramsYamlFile = writeParamsToTempYaml();

        // Find the index.qmd file in the copied folder
        Path indexQmdFile = workspacePath.resolve("index.qmd");
        if (!Files.exists(indexQmdFile)) {
            throw new FileNotFoundException("index.qmd not found in the copied folder: " + indexQmdFile);
        }

        String command = String.format(
                "quarto render \"%s\" --output \"%s\" --execute-params \"%s\"",
                indexQmdFile.toString().replace("\\", "/"),
                output.replace("\\", "/"),
                paramsYamlFile.toString().replace("\\", "/")
        );

        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command("bash", "-c", command);
        processBuilder.directory(workspacePath.toFile());  // Set the workspace as the working directory

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
            throw new RuntimeException("Quarto rendering failed with exit code: " + exitCode);
        }
    }
}
