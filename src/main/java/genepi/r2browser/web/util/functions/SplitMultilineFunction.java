package genepi.r2browser.web.util.functions;

import genepi.r2browser.util.GenomicRegion;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SplitMultilineFunction implements Function<Object, List> {

	@Override
	public List apply(Object text) {
		return Arrays.stream(text.toString().split("[,\n]"))
				.map(String::trim)
				.filter(s -> !s.isEmpty())
				.collect(Collectors.toList());
	}

}