package genepi.r2browser.util;

import java.io.*;
import java.nio.file.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class CondaEnvManager {

    private final String condaEnvYaml;
    private final String cacheDirectory;
    private final String tool;
    private String binary = null;
    private File workingDirectory;
    private File stdoutFile;
    private File stderrFile;

    public CondaEnvManager(String condaEnvYaml, String cacheDirectory, String tool) {
        this.condaEnvYaml = condaEnvYaml;
        this.cacheDirectory = cacheDirectory;
        this.tool = tool;
        if (tool.equalsIgnoreCase("micromamba")) {
            binary = new BinaryFinder("micromamba").env("MICROMAMBA_HOME").envPath().path("/usr/local/bin").find();
        } else if (tool.equalsIgnoreCase("mamba")) {
            binary = new BinaryFinder("mamba").env("MAMBA_HOME").envPath().path("/usr/local/bin").find();
        } else if (tool.equalsIgnoreCase("conda")) {
            binary = new BinaryFinder("conda").env("CONDA_HOME").envPath().path("/usr/local/bin").find();
        }
    }

    public int executeWithEnv(String command) throws Exception {

        if (binary == null) {
            throw new RuntimeException("Binary for tool " + tool + " not found.");
        }

        String hash = generateHash(new File(condaEnvYaml));
        String envFolder = new File(cacheDirectory + File.separator + "env-" + hash).getAbsolutePath();

        writeToOutput("Use " + tool  + " installed in " + binary + "\n");

        File envDir = new File(envFolder);
        if (envDir.exists() && envDir.isDirectory()) {
            writeToOutput("Environment already exists. Activating...\n");
            return activateAndExecute(envFolder, command);
        } else {
            writeToOutput("Environment not found. Creating new environment...\n");
            createEnvironment(envFolder);
            return activateAndExecute(envFolder, command);
        }
    }

    private String generateHash(File file) throws IOException, NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        try (InputStream fis = new FileInputStream(file)) {
            byte[] buffer = new byte[1024];
            int numRead;
            while ((numRead = fis.read(buffer)) != -1) {
                digest.update(buffer, 0, numRead);
            }
        }
        StringBuilder hashBuilder = new StringBuilder();
        for (byte b : digest.digest()) {
            hashBuilder.append(String.format("%02x", b));
        }
        return hashBuilder.toString();
    }

    private void createEnvironment(String envFolder) throws IOException, InterruptedException {
        String options = "";
        if (tool.equalsIgnoreCase("micromamba")) {
            options = "--yes";
        }
        String createCommand = binary + " env create " + options + " --file " + condaEnvYaml + " --prefix " + envFolder;
        ProcessBuilder processBuilder = new ProcessBuilder("bash", "-c", createCommand);

        writeToOutput("Executing command: " + createCommand + "\n");

        Process process = processBuilder.start();
        appendStreamToFile(process.getInputStream(), stdoutFile);
        appendStreamToFile(process.getErrorStream(), stderrFile);

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            writeToOutput("Failed to create the environment using " + tool + "\n");
            throw new RuntimeException("Failed to create the environment using " + tool);
        }
        writeToOutput("Environment created successfully.\n");
    }

    private int activateAndExecute(String envFolder, String command) throws IOException, InterruptedException {
        String activateCommand = binary + " run --prefix " + envFolder + " " + command;
        writeToOutput("Executing command: " + activateCommand + "\n");

        ProcessBuilder processBuilder = new ProcessBuilder("bash", "-c", activateCommand);
        if (workingDirectory != null) {
            processBuilder.directory(workingDirectory);
        }

        Process process = processBuilder.start();
        appendStreamToFile(process.getInputStream(), stdoutFile);
        appendStreamToFile(process.getErrorStream(), stderrFile);

        int exitCode = process.waitFor();
        if (exitCode == 0) {
            writeToOutput("Command executed successfully.\n");
        } else {
            writeToOutput("Command execution failed with exit code: " + exitCode + "\n");
        }

        return exitCode;
    }

    public void setWorkingDirectory(File file) {
        this.workingDirectory = file;
    }

    public void setStdoutFile(File file) {
        this.stdoutFile = file;
    }

    public void setStderrFile(File file) {
        this.stderrFile = file;
    }


    private void writeToOutput(String message) throws IOException {
        if (stdoutFile != null) {
            try (FileWriter writer = new FileWriter(stdoutFile, true)) {
                writer.write(message);
            }
        }
    }

    private void appendStreamToFile(InputStream inputStream, File file) throws IOException {
        if (file != null) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                 FileWriter writer = new FileWriter(file, true)) {
                String line;
                while ((line = reader.readLine()) != null) {
                    writer.write(line + "\n");
                }
            }
        }
    }

}
