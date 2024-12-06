package genepi.r2browser.util;

import java.io.*;
import java.nio.file.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class CondaEnvManager {

    private final String condaEnvYaml;
    private final String cacheDirectory;
    private final String tool;
    private String stdOut = "";
    private String stdErr = "";

    private File workingDirectory;

    public CondaEnvManager(String condaEnvYaml, String cacheDirectory, String tool) {
        this.condaEnvYaml = condaEnvYaml;
        this.cacheDirectory = cacheDirectory;
        this.tool = tool;
    }

    public int executeWithEnv(String command) throws Exception {
        String hash = generateHash(new File(condaEnvYaml));
        String envFolder = new File(cacheDirectory + File.separator + "env-" + hash).getAbsolutePath();

        File envDir = new File(envFolder);
        if (envDir.exists() && envDir.isDirectory()) {
            System.out.println("Environment already exists. Activating...");
            return activateAndExecute(envFolder, command);
        } else {
            System.out.println("Environment not found. Creating new environment...");
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
        String createCommand = tool + " env create --yes -f " + condaEnvYaml + " --prefix " + envFolder;
        ProcessBuilder processBuilder = new ProcessBuilder("bash", "-c", "PATH=$PATH:$HOME/.local/bin " + createCommand);
        processBuilder.inheritIO();
        Process process = processBuilder.start();
        int exitCode = process.waitFor();
        System.out.println(createCommand);
        if (exitCode != 0) {
            throw new RuntimeException("Failed to create the environment using " + tool);
        }
    }

    private int activateAndExecute(String envFolder, String command) throws IOException, InterruptedException {
        String activateCommand = tool + " run -p " + envFolder + " " + command;

        ProcessBuilder processBuilder = new ProcessBuilder("bash", "-c", "PATH=$PATH:$HOME/.local/bin " + activateCommand);
        processBuilder.inheritIO();
        if (workingDirectory != null) {
            processBuilder.directory(workingDirectory);
        }
        Process process = processBuilder.start();
        // Capture stdout
        StringBuilder stdoutBuilder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                stdoutBuilder.append(line).append("\n");
            }
        }
        stdOut = stdoutBuilder.toString();

        // Capture stderr
        StringBuilder stderrBuilder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                stderrBuilder.append(line).append("\n");
            }
        }
        stdErr = stderrBuilder.toString();


        int exitCode = process.waitFor();
        return exitCode;
    }

    public String getStdErr() {
        return stdErr;
    }

    public String getStdOut() {
        return stdOut;
    }
    public void setWorkingDirectory(File file) {
        this.workingDirectory = file;
    }
}
