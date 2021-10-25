package genepi.r2web.tasks;

import java.io.IOException;
import java.util.List;
import java.util.Vector;

import genepi.r2web.model.AggregatedBin;
import genepi.r2web.model.Dataset;
import genepi.r2web.model.SubDataset;
import genepi.r2web.util.GenomicRegion;
import htsjdk.tribble.readers.TabixReader;
import htsjdk.tribble.readers.TabixReader.Iterator;

public class ExtractVariantsTask {

	private List<SubDataset> subDatasets = new Vector<SubDataset>();

	private float[] bins;

	public ExtractVariantsTask(String[] names, float[] bins) {

		this.bins = bins;

		for (String name : names) {
			subDatasets.add(new SubDataset(name, bins));
		}

	}

	public int findVariants(Dataset dataset, GenomicRegion location) throws IOException {

		TabixReader reader = new TabixReader(dataset.getFilename());

		Iterator result = reader.query(location.getChromosome(), location.getStart(), location.getEnd());

		String line = result.next();
		int results = 0;
		while (line != null) {
			results++;
			parseLine(line);
			line = result.next();
		}

		reader.close();

		return results;

	}

	protected void parseLine(String line) {

		String[] tiles = line.split("\t");

		double af = Double.parseDouble(tiles[4]);

		int binIndex = getBinByAf(af);

		int subset = 0;

		for (int i = 5; i < tiles.length; i += 2) {

			AggregatedBin bin = subDatasets.get(subset).getAggregatedBins().get(binIndex);

			if (!tiles[i].equals("NA")) {
				double value = Double.parseDouble(tiles[i]);
				bin.addValue(value);
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

	public List<SubDataset> getSubDatasets() {
		return subDatasets;
	}

}
