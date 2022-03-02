package genepi.r2browser.model;

public class AggregatedBin {

	double sum = 0;

	int count = 0;

	float end = 0;

	public AggregatedBin(float end) {
		this.end = end;
	}

	public void addValue(double value) {
		sum += value;
		count++;
	}

	public double getMean() {
		return sum / (double) count;
	}

	public float getEnd() {
		return end;
	}

	public int getCount() {
		return count;
	}

}