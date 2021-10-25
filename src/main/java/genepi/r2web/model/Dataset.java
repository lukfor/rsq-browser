package genepi.r2web.model;

public class Dataset {

	private String name;

	private String filename;

	private String[] subsets;

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

	public String[] getSubsets() {
		return subsets;
	}

	public void setSubsets(String[] subsets) {
		this.subsets = subsets;
	}

}
