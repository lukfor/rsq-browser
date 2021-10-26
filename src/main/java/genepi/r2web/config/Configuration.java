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
import genepi.r2web.model.IdLabel;
import genepi.r2web.model.SubDataset;

public class Configuration {

	private int port = App.PORT;

	private int maxUploadSizeMb = 200;

	private String workspace = "queries";

	private int jobIdLength = 50;

	private boolean caching = false;

	private int threads = 2;

	private List<Dataset> datasets = new Vector<Dataset>();

	private List<IdLabel> populations = new Vector<IdLabel>();

	private List<IdLabel> references = new Vector<IdLabel>();

	private List<IdLabel> chips = new Vector<IdLabel>();

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

	public void setChips(List<IdLabel> chips) {
		this.chips = chips;
	}

	public List<IdLabel> getChips() {
		return chips;
	}

	public void setReferences(List<IdLabel> references) {
		this.references = references;
	}

	public List<IdLabel> getReferences() {
		return references;
	}

	public void setPopulations(List<IdLabel> populations) {
		this.populations = populations;
	}

	public List<IdLabel> getPopulations() {
		return populations;
	}

	public void setBins(float[] bins) {
		this.bins = bins;
	}

	public float[] getBins() {
		return bins;
	}

	public void setCaching(boolean caching) {
		this.caching = caching;
	}

	public boolean isCaching() {
		return caching;
	}

	public String getPopulationLabel(String id) {
		return getLabel(populations, id);
	}

	public String getChipLabel(String id) {
		return getLabel(chips, id);
	}

	public String getReferenceLabel(String id) {
		return getLabel(references, id);
	}

	public String getLabel(List<IdLabel> labels, String id) {
		for (IdLabel label : labels) {
			if (label.getId().equalsIgnoreCase(id)) {
				return label.getLabel();
			}
		}
		return "?";
	}

	protected void init() {
		for (Dataset dataset : datasets) {
			for (SubDataset subdataset : dataset.getSubsets()) {
				String name = String.format("%s - %s (%s)", getPopulationLabel(subdataset.getPopulation()),
						getChipLabel(subdataset.getChip()), getReferenceLabel(subdataset.getReference()));
				subdataset.setName(name);
			}
		}
	}

	public static Configuration loadFromFile(File file, String parent) throws YamlException, FileNotFoundException {

		YamlReader reader = new YamlReader(new FileReader(file));
		reader.getConfig().setPropertyElementType(Configuration.class, "datasets", Dataset.class);
		reader.getConfig().setPropertyElementType(Dataset.class, "subsets", SubDataset.class);
		reader.getConfig().setPropertyElementType(Configuration.class, "populations", IdLabel.class);
		reader.getConfig().setPropertyElementType(Configuration.class, "chips", IdLabel.class);
		reader.getConfig().setPropertyElementType(Configuration.class, "references", IdLabel.class);

		Configuration configuration = reader.read(Configuration.class);
		configuration.init();

		return configuration;

	}

}
