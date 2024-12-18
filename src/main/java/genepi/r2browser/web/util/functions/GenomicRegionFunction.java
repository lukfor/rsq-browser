package genepi.r2browser.web.util.functions;

import genepi.r2browser.util.GenomicRegion;
import java.util.function.Function;

public class GenomicRegionFunction implements Function<Object, String> {

	@Override
	public String apply(Object text) {
		String build = "hg38";
		String defaultValue = "unknown";
		try{
			GenomicRegion genomicRegion = GenomicRegion.parse(text.toString(), build);
			return genomicRegion.toString();
		} catch (Exception e) {
			return defaultValue;
		}

	}

}