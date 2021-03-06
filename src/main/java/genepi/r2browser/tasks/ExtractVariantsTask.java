package genepi.r2browser.tasks;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import genepi.io.text.LineReader;
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

	private SubDataset[] subsets;

	private float[] bins;

	public ExtractVariantsTask(List<Dataset> datasets, float[] bins) {

		this.bins = bins;
		this.datasets = datasets;
		for (Dataset dataset : datasets) {
			for (SubDataset subDataset : dataset.getSubsets()) {
				Result result = new Result(subDataset, bins);
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

		LineReader readerHeader = new LineReader(dataset.getFilename());
		readerHeader.next();
		String header = readerHeader.get();
		String[] tiles = header.split("\t");
		Map<String, Integer> columns = new HashMap<String, Integer>();
		for (int i = 0; i < tiles.length; i++) {
			columns.put(tiles[i], i);
		}
		subsets = new SubDataset[tiles.length];
		for (SubDataset subDataset : dataset.getSubsets()) {
			int column = columns.get(subDataset.getColumn());
			subsets[column] = subDataset;
		}

		readerHeader.close();

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

		for (int column = 0; column < subsets.length; column++) {
			SubDataset subDataset = subsets[column];
			if (subDataset != null) {

				if (!tiles[column].equals("NA")) {
					AggregatedBin bin = index.get(subDataset).getAggregatedBins().get(binIndex);
					double value = Double.parseDouble(tiles[column]);
					bin.addValue(value);
				}
			}

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

	public List<Result> getResults() {
		return results;
	}

}
