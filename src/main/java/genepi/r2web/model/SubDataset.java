package genepi.r2web.model;

public class SubDataset {

	private String column;

	private String population;

	private String reference;

	private String chip;

	private String name;

	public void setChip(String chip) {
		this.chip = chip;
	}

	public String getChip() {
		return chip;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public String getReference() {
		return reference;
	}

	public void setColumn(String column) {
		this.column = column;
	}

	public String getColumn() {
		return column;
	}

	public void setPopulation(String population) {
		this.population = population;
	}

	public String getPopulation() {
		return population;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

}
