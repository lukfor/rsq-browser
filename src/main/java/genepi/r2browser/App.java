package genepi.r2browser;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import genepi.io.FileUtil;
import genepi.r2browser.commands.ServerCommand;
import genepi.r2browser.config.Configuration;
import genepi.r2browser.web.util.JobQueue;
import picocli.CommandLine;
import picocli.CommandLine.Command;

@Command(name = "covidgrep")
public class App implements Runnable {

	public static final String NAME = "rsq-browser";

	public static final String VERSION = "0.1.0";

	public static final String COPYRIGHT = "";

	public static final int PORT = 7000;

	public static final String CONFIG_FILENAME = "rsq-browser.yaml";

	private static App instance;

	private Configuration configuration = new Configuration();

	private String configFilename = null;

	private JobQueue jobQueue;

	private static CommandLine commandLine;

	public static synchronized App getDefault() {
		if (instance == null) {
			instance = new App();
		}
		return instance;
	}

	public void loadConfiguration(String configFilename) {

		if (this.configFilename != null && this.configFilename.equals(configFilename)) {
			return;
		}

		try {

			String parent = ".";

			File configFile = new File(configFilename);
			if (!configFile.exists()) {

				File jarFile = new File(
						App.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
				configFile = new File(FileUtil.path(jarFile.getParent(), CONFIG_FILENAME));

				parent = jarFile.getParent();

				if (!configFile.exists()) {

					System.out.println("Configuration file '" + configFilename + "' not found.");
					System.exit(1);

				}
			}

			configuration = Configuration.loadFromFile(configFile, parent);
			jobQueue = new JobQueue(configuration.getThreads());

			this.configFilename = configFilename;

		} catch (IOException | URISyntaxException e) {
			System.out.println("Loading configuration from file '" + configFilename + "' failed.");
			System.out.println(e.getMessage());
			System.exit(1);
		}

	}

	public Configuration getConfiguration() {
		return configuration;
	}

	public JobQueue getJobQueue() {
		return jobQueue;
	}

	public static void main(String[] args) throws URISyntaxException {

		System.out.println();
		System.out.println(NAME + " " + VERSION);
		if (COPYRIGHT != null && !COPYRIGHT.isEmpty()) {
			System.out.println(COPYRIGHT);
		}
		
		new App();

		commandLine = new CommandLine(new ServerCommand());
		int result = commandLine.execute(args);
		System.exit(result);

	}

	public static boolean isDevelopmentSystem() {
		return new File("src/main/resources").exists();
	}

	@Override
	public void run() {
		commandLine.usage(System.out);
	}

}
