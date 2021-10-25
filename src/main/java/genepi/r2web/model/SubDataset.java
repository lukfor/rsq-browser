package genepi.r2web.model;

import java.util.List;
import java.util.Vector;

public class SubDataset {

	private String name;

	private List<AggregatedBin> aggregatedBins = new Vector<AggregatedBin>();

	public SubDataset(String name, float[] bins) {
		this.name = name;
		for (int i = 1; i < bins.length; i++) {
			aggregatedBins.add(new AggregatedBin(bins[i]));
		}
	}

	public List<AggregatedBin> getAggregatedBins() {
		return aggregatedBins;
	}

	public void setAggregatedBins(List<AggregatedBin> aggregatedBins) {
		this.aggregatedBins = aggregatedBins;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
