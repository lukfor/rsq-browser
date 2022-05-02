package genepi.r2browser.model;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.FileUtils;

import genepi.io.FileUtil;
import genepi.r2browser.config.Configuration;

public class DatasetDetails {

	private String name;

	private String filename;

	private int index;

	private Set<String> references = new HashSet<String>();

	private Set<String> chips = new HashSet<String>();

	private Set<String> populations = new HashSet<String>();

	private String size;

	private Configuration configuration;
	
	public DatasetDetails(Dataset dataset, int index, Configuration configuration) {
		this.configuration = configuration;
		this.name = buildName(dataset);
		this.filename = FileUtil.getFilename(dataset.getFilename());
		this.size = FileUtils.byteCountToDisplaySize(new File(dataset.getFilename()).length());
		this.index = index;
	}

	public int getIndex() {
		return index;
	}

	public String getName() {
		return name;
	}

	public String getFilename() {
		return filename;
	}

	public String getSize() {
		return size;
	}

	protected String buildName(Dataset dataset) {
		for (SubDataset subdataset : dataset.getSubsets()) {
			references.add(configuration.getReferenceLabel(subdataset.getReference()));
			chips.add(configuration.getChipLabel(subdataset.getChip()));
			populations.add(configuration.getPopulationLabel(subdataset.getPopulation()));
		}
		return references + " " + chips + " " + populations;
	}

	public String getChips() {
		return printSet(chips);
	}

	public String getPopulations() {
		return printSet(populations);
	}

	public String getReferences() {
		return printSet(references);
	}
	
	
	public String printSet(Set<String> set) {
		int index = 0;
		String result = "";
		for (String item: set) {
			if (index > 0) {
			result += ", ";
			}
			result += item;
			index++;
		}
		return result;
	}

}
