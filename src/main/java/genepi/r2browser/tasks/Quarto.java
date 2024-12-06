package genepi.r2browser.tasks;

import com.esotericsoftware.yamlbeans.YamlWriter;
import genepi.r2browser.util.CondaEnvManager;

import java.io.*;
import java.nio.file.*;
import java.util.Map;

public class Quarto {

    private Path qmdFilePath;
    private String output;
    private Map<String, Object> params;
    private Path workspacePath;
    private String stdOut = "";
    private String stdErr = "";

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

    public boolean render() throws Exception {
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

        Path condaEnvYaml = workspacePath.resolve("environment.yml");
        if (!Files.exists(condaEnvYaml)) {
            throw new FileNotFoundException("env not found in the copied folder: " + condaEnvYaml);
        }
        CondaEnvManager manager = new CondaEnvManager(condaEnvYaml.toString(), "cache", "micromamba");

        String command = String.format(
                "quarto render \"%s\" --output \"%s\" --execute-params \"%s\"",
                indexQmdFile.toString().replace("\\", "/"),
                output.replace("\\", "/"),
                paramsYamlFile.toString().replace("\\", "/")
        );

        manager.setWorkingDirectory(workspacePath.toFile());
        int exitCode = manager.executeWithEnv(command);
        stdErr = manager.getStdErr();
        stdOut = manager.getStdOut();

        return exitCode == 0;
    }

    public String getStdErr() {
        return stdErr;
    }

    public String getStdOut() {
        return stdOut;
    }
}
