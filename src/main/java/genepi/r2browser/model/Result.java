package genepi.r2browser.model;

import java.util.List;
import java.util.Vector;

public class Result {

	private SubDataset subDataset;

	private List<AggregatedBin> aggregatedBins = new Vector<AggregatedBin>();

	private List<AggregatedBin> aggregatedQualities = new Vector<AggregatedBin>();

	private int count;

	public Result(SubDataset subDataset, float[] bins, float[] bins2) {
		this.subDataset = subDataset;
		for (int i = 1; i < bins.length; i++) {
			aggregatedBins.add(new AggregatedBin(bins[i]));
		}
		for (int i = 0; i < bins2.length; i++) {
			aggregatedQualities.add(new AggregatedBin(bins2[i]));
		}
	}

	public List<AggregatedBin> getAggregatedBins() {
		return aggregatedBins;
	}

	public void setAggregatedBins(List<AggregatedBin> aggregatedBins) {
		this.aggregatedBins = aggregatedBins;
	}

	public List<AggregatedBin> getAggregatedQualities() {
		return aggregatedQualities;
	}

	public void setAggregatedQualities(List<AggregatedBin> aggregatedQualities) {
		this.aggregatedQualities = aggregatedQualities;
	}

	public SubDataset getSubDataset() {
		return subDataset;
	}

	public void setSubDataset(SubDataset subDataset) {
		this.subDataset = subDataset;
	}

	public int updateCounter() {
		count = 0;
		for (AggregatedBin bin : aggregatedBins) {
			count += bin.getCount();
		}
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

}
