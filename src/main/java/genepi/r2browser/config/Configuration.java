package genepi.r2browser.config;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.esotericsoftware.yamlbeans.YamlReader;

import genepi.io.text.LineReader;
import genepi.r2browser.App;
import genepi.r2browser.model.AdditionalDownload;
import genepi.r2browser.model.Dataset;
import genepi.r2browser.model.DatasetDetails;
import genepi.r2browser.model.IdLabel;
import genepi.r2browser.model.SubDataset;
import genepi.r2browser.util.GenomicRegion;

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

	private String baseUrl = "";

	private String dbSnpIndex;

	private String build = "hg38";

	private Map<String, GenomicRegion> genesIndex = new HashMap<String, GenomicRegion>();

	private String genes = null;

	private List<DatasetDetails> downloads = new Vector<DatasetDetails>();

	private List<AdditionalDownload> files = new Vector<AdditionalDownload>();

	private Map<String, Object> conda = null;

	public Configuration() {
		conda = new HashMap<>();
		conda.put("enabled", false);
		conda.put("useMamba", false);
		conda.put("useMicromamba", false);
		conda.put("cacheDir", "cache");
	}

	public Map<String, GenomicRegion> getGenesIndex() {
		return genesIndex;
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

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public String getBaseUrl() {
		return baseUrl;
	}

	public String getDbSnpIndex() {
		return dbSnpIndex;
	}

	public void setDbSnpIndex(String dbSnpIndex) {
		this.dbSnpIndex = dbSnpIndex;
	}

	public String getLabel(List<IdLabel> labels, String id) {
		for (IdLabel label : labels) {
			if (label.getId().equalsIgnoreCase(id)) {
				return label.getLabel();
			}
		}
		return "?";
	}

	public void setBuild(String build) {
		this.build = build;
	}

	public String getBuild() {
		return build;
	}

	public String getGenes() {
		return genes;
	}

	public void setGenes(String genes) {
		this.genes = genes;
	}

	public List<DatasetDetails> getDownloads() {
		return downloads;
	}

	public void setDownloads(List<DatasetDetails> downloads) {
		this.downloads = downloads;
	}

	public List<AdditionalDownload> getFiles() {
		return files;
	}

	public void setFiles(List<AdditionalDownload> files) {
		this.files = files;
	}

	protected void init() throws IOException {
		for (Dataset dataset : datasets) {
			for (SubDataset subdataset : dataset.getSubsets()) {
				String name = String.format("%s - %s (%s)", getPopulationLabel(subdataset.getPopulation()),
						getChipLabel(subdataset.getChip()), getReferenceLabel(subdataset.getReference()));
				subdataset.setName(name);
			}
		}

		// load genes
		if (genes != null) {
			LineReader reader = new LineReader(genes);
			while (reader.next()) {
				String line = reader.get();
				String[] tiles = line.split("\t");
				if (tiles.length == 4) {
					GenomicRegion region = new GenomicRegion();
					if (build == "hg38") {
						region.setChromosome("chr" + tiles[0]);
					} else {
						region.setChromosome(tiles[0]);
					}
					region.setStart(Integer.parseInt(tiles[1]));
					region.setEnd(Integer.parseInt(tiles[2]));
					genesIndex.put(tiles[3].toLowerCase(), region);
				}
			}
			reader.close();

			System.out.println("Loaded " + genesIndex.size() + " genes from file " + genes);

		}

		int index = 0;
		for (Dataset dataset : getDatasets()) {
			downloads.add(new DatasetDetails(dataset, index, this));
			index++;
		}

	}

	public void setConda(Map<String, Object> conda) {
		this.conda.putAll(conda);
	}

	public Map<String, Object> getConda() {
		return conda;
	}

	public static Configuration loadFromFile(File file, String parent) throws IOException {

		YamlReader reader = new YamlReader(new FileReader(file));
		reader.getConfig().setPropertyElementType(Configuration.class, "datasets", Dataset.class);
		reader.getConfig().setPropertyElementType(Dataset.class, "subsets", SubDataset.class);
		reader.getConfig().setPropertyElementType(Configuration.class, "populations", IdLabel.class);
		reader.getConfig().setPropertyElementType(Configuration.class, "chips", IdLabel.class);
		reader.getConfig().setPropertyElementType(Configuration.class, "references", IdLabel.class);
		reader.getConfig().setPropertyElementType(Configuration.class, "files", AdditionalDownload.class);

		Configuration configuration = reader.read(Configuration.class);
		configuration.init();

		return configuration;

	}

	public String getFileByPopulation(String population) {
		for (Dataset dataset: getDatasets()) {
			for (SubDataset subset: dataset.getSubsets()) {
				if (subset.getPopulation().equalsIgnoreCase(population)){
					return new File(dataset.getFilename()).getAbsolutePath();
				}
			}
		}
		return null;
	}

}
