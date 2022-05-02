package genepi.r2browser.web.handlers.downloads;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import genepi.io.text.GzipLineWriter;
import genepi.io.text.LineReader;
import genepi.r2browser.App;
import genepi.r2browser.config.Configuration;
import genepi.r2browser.model.Dataset;
import genepi.r2browser.util.GenomicRegion;
import genepi.r2browser.web.util.AbstractHandler;
import htsjdk.tribble.readers.TabixReader;
import htsjdk.tribble.readers.TabixReader.Iterator;
import io.javalin.http.Context;
import io.javalin.http.HandlerType;

public class DownloadDetailsHandler extends AbstractHandler {

	public static final String PATH = "/downloads/{index}/{filename}";

	public static final HandlerType TYPE = HandlerType.GET;

	private Configuration configuration = App.getDefault().getConfiguration();

	public void handle(Context context) throws Exception {

		String index = context.pathParam("index");
		Dataset dataset = configuration.getDatasets().get(Integer.parseInt(index));
		// TODO: check dataset != null and index = integer;

		String query = context.queryParam("q");

		File file = null;

		if (query != null) {
			GenomicRegion region = GenomicRegion.parse(query, configuration.getBuild());
			file = extractsVariants(dataset, region);
		} else {
			file = new File(dataset.getFilename());
		}
		context.result(new FileInputStream(file));

	}

	@Override
	public String getPath() {
		return configuration.getBaseUrl() + PATH;
	}

	@Override
	public HandlerType getType() {
		return TYPE;
	}

	protected File extractsVariants(Dataset dataset, GenomicRegion location) throws IOException {

		File file = File.createTempFile("rsq-browser", ".txt.gz");

		LineReader readerHeader = new LineReader(dataset.getFilename());
		readerHeader.next();
		String header = readerHeader.get();
		readerHeader.close();
		
		GzipLineWriter writer = new GzipLineWriter(file.getAbsolutePath());
		writer.write(header);

		TabixReader reader = new TabixReader(dataset.getFilename());

		Iterator result = reader.query(location.getChromosome(), location.getStart() - 1, location.getEnd());

		String line = result.next();
		while (line != null) {
			writer.write(line);
			line = result.next();
		}

		reader.close();
		writer.close();

		return file;

	}
}
