package genepi.r2browser.util;

import com.esotericsoftware.yamlbeans.YamlWriter;
import com.esotericsoftware.yamlbeans.YamlConfig;
import genepi.r2browser.App;
import genepi.r2browser.web.util.functions.GenomicRegionFunction;
import genepi.r2browser.web.util.functions.SplitMultilineFunction;
import io.marioslab.basis.template.Template;
import io.marioslab.basis.template.TemplateContext;
import io.marioslab.basis.template.TemplateLoader;

import java.io.*;
import java.nio.file.*;
import java.util.Map;

public class Quarto {

    private Path qmdFilePath;
    private String output;
    private Map<String, String> params;
    private Path workspacePath;
    private String binary = "";


    public Quarto(Path qmdFilePath, Map<String, String> params, String output, Path workspacePath) {
        binary = new BinaryFinder("quarto").env("QUARTO_HOME").envPath().path("/usr/local/bin").find();
        this.qmdFilePath = qmdFilePath;
        this.params = params;
        this.output = output;
        this.workspacePath = workspacePath;
    }

    private Path writeParamsToTempYaml() throws IOException {
        // Create the params.yaml file in the workspace
        Path paramsYamlPath = workspacePath.resolve("params.yml");
        YamlConfig config = new YamlConfig();
        config.writeConfig.setAlwaysWriteClassname(false);
        config.writeConfig.setAutoAnchor(false);
        config.writeConfig.setWriteRootElementTags(false);
        config.writeConfig.setWriteRootTags(false);


        try (Writer writer = new FileWriter(paramsYamlPath.toFile())) {
            YamlWriter yamlWriter = new YamlWriter(writer, config);
            yamlWriter.getConfig().writeConfig.setAlwaysWriteClassname(false);
            yamlWriter.getConfig().writeConfig.setAutoAnchor(false);
            yamlWriter.getConfig().writeConfig.setWriteRootElementTags(false);
            yamlWriter.getConfig().writeConfig.setWriteRootTags(false);

            yamlWriter.write(params);
            yamlWriter.close();
        }

        return paramsYamlPath;
    }

    private Path writeParamsToTempYaml(Path templatePath) throws IOException {

        TemplateLoader.FileTemplateLoader loader = new TemplateLoader.FileTemplateLoader();
        TemplateContext templateContext = new TemplateContext();
        templateContext.set("form", params);
        templateContext.set("configuration", App.getDefault().getConfiguration());
        templateContext.set("genomic_region", new GenomicRegionFunction());
        templateContext.set("split_multiline", new SplitMultilineFunction());

        Template template =  loader.load(templatePath.toAbsolutePath().toString());
        String yaml = template.render(templateContext);

        Path paramsYamlPath = workspacePath.resolve("params.yml");
        try (Writer writer = new FileWriter(paramsYamlPath.toFile())) {
            writer.write(yaml);
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

        Path paramsYamlFile = null;
        Path paramsTemplateYaml = workspacePath.resolve("params.template.yml");
        if (paramsTemplateYaml.toFile().exists()) {
            paramsYamlFile  = writeParamsToTempYaml(paramsTemplateYaml);
        } else {
            paramsYamlFile = writeParamsToTempYaml();
        }

        // Find the index.qmd file in the copied folder
        Path indexQmdFile = workspacePath.resolve("index.qmd");
        if (!Files.exists(indexQmdFile)) {
            throw new FileNotFoundException("index.qmd not found in the copied folder: " + indexQmdFile);
        }

        Path condaEnvYaml = workspacePath.resolve("environment.yml");
        if (!Files.exists(condaEnvYaml)) {
            throw new FileNotFoundException("env not found in the copied folder: " + condaEnvYaml);
        }

        Map<String, Object> options = App.getDefault().getConfiguration().getConda();
        CondaEnvManager manager = new CondaEnvManager(condaEnvYaml.toString(), options);

        String command = String.format(
                "%s render \"%s\" --output \"%s\" --execute-params \"%s\"",
                binary,
                indexQmdFile.toString().replace("\\", "/"),
                output.replace("\\", "/"),
                paramsYamlFile.toString().replace("\\", "/")
        );

        manager.setWorkingDirectory(workspacePath.toFile());
        Path stdoutFile = workspacePath.resolve("command.out");
        manager.setStdoutFile(stdoutFile.toFile());
        Path stderrFile = workspacePath.resolve("command.err");
        manager.setStderrFile(stderrFile.toFile());

        int exitCode = manager.executeWithEnv(command);

        return exitCode == 0;
    }

}
