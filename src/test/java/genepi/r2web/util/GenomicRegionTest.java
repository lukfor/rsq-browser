package genepi.r2web.util;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

public class GenomicRegionTest {

	@Test(expected = IOException.class)
	public void testRsId() throws IOException {
		GenomicRegion.parse("rs123456");
	}

	@Test
	public void testSinglePosition() throws IOException {
		GenomicRegion location = GenomicRegion.parse("6:5001");
		assertEquals("6", location.getChromosome());
		assertEquals(5001, location.getStart());
		assertEquals(5001, location.getEnd());
	}

	@Test
	public void testRegion() throws IOException {
		GenomicRegion location = GenomicRegion.parse("6:5001-5010");
		assertEquals("6", location.getChromosome());
		assertEquals(5001, location.getStart());
		assertEquals(5010, location.getEnd());
	}

	@Test
	public void testRegionInBedFormat() throws IOException {
		GenomicRegion location = GenomicRegion.parse("6\t5001\t5010");
		assertEquals("6", location.getChromosome());
		assertEquals(5001, location.getStart());
		assertEquals(5010, location.getEnd());
	}

	@Test
	public void testChromosome() throws IOException {
		GenomicRegion location = GenomicRegion.parse("chr6");
		assertEquals("chr6", location.getChromosome());
		assertEquals(1, location.getStart());
		assertEquals(Integer.MAX_VALUE, location.getEnd());
	}

	@Test(expected = IOException.class)
	public void testWrongRegion() throws IOException {
		GenomicRegion.parse("6:5001:5010");
	}

	@Test(expected = IOException.class)
	public void testWrongRegion2() throws IOException {
		GenomicRegion.parse("6:5001-");
	}

	@Test(expected = IOException.class)
	public void testWrongBed() throws IOException {
		GenomicRegion.parse("6\t5001:5002");
	}

}
