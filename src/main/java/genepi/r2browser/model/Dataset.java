package genepi.r2browser.model;

import java.util.List;

public class Dataset {

	private String name;

	private String filename;

	private List<SubDataset> subsets;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public List<SubDataset> getSubsets() {
		return subsets;
	}

	public void setSubsets(List<SubDataset> subsets) {
		this.subsets = subsets;
	}

}
