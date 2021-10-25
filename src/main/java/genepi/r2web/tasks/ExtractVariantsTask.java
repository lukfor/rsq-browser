package genepi.r2web.tasks;

import java.io.IOException;

import genepi.r2web.model.Dataset;
import genepi.r2web.util.GenomicRegion;
import htsjdk.tribble.readers.TabixReader;
import htsjdk.tribble.readers.TabixReader.Iterator;

public class ExtractVariantsTask {

	public int findVariants(Dataset dataset, GenomicRegion location) throws IOException {

		TabixReader reader = new TabixReader(dataset.getFilename());

		Iterator result = reader.query(location.getChromosome(), location.getStart(), location.getEnd());

		String line = result.next();
		int results = 0;
		while (line != null) {
			results++;
			line = result.next();
		}

		reader.close();

		return results;

	}

}
