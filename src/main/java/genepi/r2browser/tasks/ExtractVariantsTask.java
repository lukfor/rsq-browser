package genepi.r2browser.tasks;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import genepi.r2browser.model.AggregatedBin;
import genepi.r2browser.model.Dataset;
import genepi.r2browser.model.Result;
import genepi.r2browser.model.SubDataset;
import genepi.r2browser.util.GenomicRegion;
import htsjdk.tribble.readers.TabixReader;
import htsjdk.tribble.readers.TabixReader.Iterator;

public class ExtractVariantsTask {

	private List<Dataset> datasets = new Vector<Dataset>();

	private List<Result> results = new Vector<Result>();

	private Map<SubDataset, Result> index = new HashMap<SubDataset, Result>();

	private float[] bins;

	private float[] bins2 = new float[] { 0.1f, 0.2f, 0.3f, 0.4f, 0.5f, 0.6f, 0.7f, 0.8f, 0.9f, 1.0f };

	public ExtractVariantsTask(List<Dataset> datasets, float[] bins) {

		this.bins = bins;
		this.datasets = datasets;
		for (Dataset dataset : datasets) {
			for (SubDataset subDataset : dataset.getSubsets()) {
				Result result = new Result(subDataset, bins, bins2);
				index.put(subDataset, result);
				results.add(result);
			}
		}

	}

	public int findVariants(GenomicRegion location) throws IOException {
		int results = 0;
		for (Dataset dataset : datasets) {
			results += findVariants(dataset, location);
		}
		return results;
	}

	protected int findVariants(Dataset dataset, GenomicRegion location) throws IOException {

		TabixReader reader = new TabixReader(dataset.getFilename());

		Iterator result = reader.query(location.getChromosome(), location.getStart() - 1, location.getEnd());

		String line = result.next();
		int results = 0;
		while (line != null) {
			results++;
			parseLine(dataset, line);
			line = result.next();
		}

		reader.close();

		return results;

	}

	protected void parseLine(Dataset dataset, String line) {

		String[] tiles = line.split("\t");

		double af = Double.parseDouble(tiles[4]);

		int binIndex = getBinByAf(af);
		if (binIndex == -1) {
			binIndex = 0;
		}

		int subset = 0;

		for (int i = 5; i < tiles.length; i += 2) {
			SubDataset subDataset = dataset.getSubsets().get(subset);
			AggregatedBin bin = index.get(subDataset).getAggregatedBins().get(binIndex);

			if (!tiles[i].equals("NA")) {
				double value = Double.parseDouble(tiles[i]);
				bin.addValue(value);

				int in = getBinByQuality(value);
				if (in == -1) {
					in = 0;
				}

				index.get(subDataset).getAggregatedQualities().get(in).addValue(1);

			}

			subset++;
		}

	}

	public int getBinByAf(double af) {

		double maf = Math.min(af, 1.0 - af);

		int i = 0;
		for (float bin : bins) {
			if (maf <= bin) {
				return i - 1;
			}
			i++;
		}
		return -1;
	}

	public int getBinByQuality(double quality) {

		int i = 0;
		for (float bin : bins2) {
			if (quality <= bin) {
				return i ;
			}
			i++;
		}
		return bins2.length - 1;
	}

	public List<Result> getResults() {
		return results;
	}

}
