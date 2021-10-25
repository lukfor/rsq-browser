package genepi.r2web.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;
import java.util.Vector;

import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;

import genepi.r2web.App;
import genepi.r2web.model.Dataset;
import genepi.r2web.model.SubDataset;

public class Configuration {

	private int port = App.PORT;

	private int maxUploadSizeMb = 200;

	private String workspace = "queries";

	private int jobIdLength = 50;

	private int threads = 2;

	private List<Dataset> datasets = new Vector<Dataset>();

	private float[] bins = new float[0];

	public Configuration() {

	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getMaxUploadSizeMb() {
		return maxUploadSizeMb;
	}

	public void setMaxUploadSizeMb(int maxUploadSizeMb) {
		this.maxUploadSizeMb = maxUploadSizeMb;
	}

	public void setWorkspace(String workspace) {
		this.workspace = workspace;
	}

	public String getWorkspace() {
		return workspace;
	}

	public int getJobIdLength() {
		return jobIdLength;
	}

	public void setJobIdLength(int jobIdLength) {
		this.jobIdLength = jobIdLength;
	}

	public int getThreads() {
		return threads;
	}

	public void setThreads(int threads) {
		this.threads = threads;
	}

	public void setDatasets(List<Dataset> datasets) {
		this.datasets = datasets;
	}

	public List<Dataset> getDatasets() {
		return datasets;
	}

	public void setBins(float[] bins) {
		this.bins = bins;
	}

	public float[] getBins() {
		return bins;
	}

	public static Configuration loadFromFile(File file, String parent) throws YamlException, FileNotFoundException {

		YamlReader reader = new YamlReader(new FileReader(file));
		reader.getConfig().setPropertyElementType(Configuration.class, "datasets", Dataset.class);
		reader.getConfig().setPropertyElementType(Dataset.class, "subsets", SubDataset.class);

		Configuration configuration = reader.read(Configuration.class);

		return configuration;

	}

}
