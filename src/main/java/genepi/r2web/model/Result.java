package genepi.r2web.model;

import java.util.List;
import java.util.Vector;

public class Result {

	private SubDataset subDataset;

	private List<AggregatedBin> aggregatedBins = new Vector<AggregatedBin>();

	public Result(SubDataset subDataset, float[] bins) {
		this.subDataset = subDataset;
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

	public SubDataset getSubDataset() {
		return subDataset;
	}

	public void setSubDataset(SubDataset subDataset) {
		this.subDataset = subDataset;
	}
}