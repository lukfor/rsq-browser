package genepi.r2browser.util;

import java.io.IOException;

import genepi.r2browser.App;
import genepi.r2browser.config.Configuration;
import genepi.r2browser.util.DbSnpReader.Snp;

public class GenomicRegion {

	private String chromosome;

	private int start;

	private int end;

	private String name = "";

	public String getChromosome() {
		return chromosome;
	}

	public void setChromosome(String chromosome) {
		this.chromosome = chromosome;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getEnd() {
		return end;
	}

	public void setEnd(int end) {
		this.end = end;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public static GenomicRegion parse(String string, String build) throws IOException {

		if (string.startsWith("rs")) {

			Configuration configuration = App.getDefault().getConfiguration();

			String dbsnpIndex = configuration.getDbSnpIndex();

			if (dbsnpIndex == null) {
				throw new IOException("RsIDs no supported. No dbSNP Index file set.");
			}

			DbSnpReader dbsnp = new DbSnpReader(dbsnpIndex);
			Snp snp = dbsnp.getByRsId(string);
			if (snp == null) {
				throw new IOException("SNP '" + string + "' not found.");
			}

			GenomicRegion location = new GenomicRegion();
			location.chromosome = chromosome(snp.getChromosome(), build);
			location.start = (int) snp.getPosition();
			location.end = (int) snp.getPosition();
			location.setName(string + " (" + location.chromosome + ":" + location.start + ")");
			return location;

		}

		// bed
		if (string.contains("\t")) {
			String[] tiles = string.split("\t");
			if (tiles.length == 3) {
				GenomicRegion location = new GenomicRegion();
				location.chromosome = chromosome(tiles[0], build);
				int start = Integer.parseInt(tiles[1]);
				location.start = start;
				int end = Integer.parseInt(tiles[2]);
				location.end = end;
				location.setName(string);
				return location;
			}
		} else if (string.contains(":")) {
			String[] tiles = string.split(":");
			if (tiles.length == 2) {
				if (string.contains("-")) {
					// region
					String[] tiles2 = tiles[1].split("-");
					if (tiles2.length == 2) {
						GenomicRegion location = new GenomicRegion();
						location.chromosome = chromosome(tiles[0], build);
						int start = Integer.parseInt(tiles2[0]);
						location.start = start;
						int end = Integer.parseInt(tiles2[1]);
						location.end = end;
						location.setName(string);
						return location;
					}
				} else {
					// single position
					GenomicRegion location = new GenomicRegion();
					location.chromosome = chromosome(tiles[0], build);
					int start = Integer.parseInt(tiles[1]);
					location.start = start;
					location.end = start;
					location.setName(string);
					return location;
				}
			}

		}

		// check if chr
		if (string.startsWith("chr")) {
			GenomicRegion location = new GenomicRegion();
			location.chromosome = chromosome(string, build);
			location.start = 1;
			location.end = Integer.MAX_VALUE;
			location.setName(string);
			return location;
		}

		//check if string is gene
		Configuration configuration = App.getDefault().getConfiguration();
		GenomicRegion location = configuration.getGenesIndex().get(string.toLowerCase());

		if (location != null) {
			location.setName(string + " (" + location.chromosome + ":" + location.start + "-" + location.end + ")");
			return location;
		}

		throw new IOException("Unknown format: " + string);
	}

	public static String chromosome(String chromosome, String build) {
		if (build.equals("hg19")) {
			return chromosome.replaceAll("chr", "");
		} else {
			if (chromosome.contains("chr")) {
				return chromosome;
			} else {
				return "chr" + chromosome;
			}
		}
	}

	public String toString() {
		return chromosome + ":" + start + "-" + end;
	}

}
