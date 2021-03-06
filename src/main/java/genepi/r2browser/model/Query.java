package genepi.r2browser.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Date;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import genepi.io.FileUtil;
import genepi.io.table.writer.CsvTableWriter;
import genepi.r2browser.tasks.ExtractVariantsTask;
import genepi.r2browser.util.GenomicRegion;

public class Query implements Runnable {

	private String id;

	private String query;

	private String rawQuery;

	private int variants;

	private Date submittedOn;

	private Date finisehdOn;

	private Date expiresOn;

	private long executionTime;

	private String error;

	private QueryStatus status;

	private transient String _workspace;

	private transient float[] _bins;

	private transient List<Dataset> _datasets;

	private List<Result> results;

	private String build = "hg38";

	public static int EXPIRES_HOURS = 4;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Date getSubmittedOn() {
		return submittedOn;
	}

	public void setSubmittedOn(Date submittedOn) {
		this.submittedOn = submittedOn;
	}

	public Date getFinisehdOn() {
		return finisehdOn;
	}

	public void setFinisehdOn(Date finisehdOn) {
		this.finisehdOn = finisehdOn;
	}

	public Date getExpiresOn() {
		return expiresOn;
	}

	public void setExpiresOn(Date expiresOn) {
		this.expiresOn = expiresOn;
	}

	public long getExecutionTime() {
		return executionTime;
	}

	public void setExecutionTime(long executionTime) {
		this.executionTime = executionTime;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public String getRawQuery() {
		return rawQuery;
	}

	public void setRawQuery(String rawQuery) {
		this.rawQuery = rawQuery;
	}

	public void setBuild(String build) {
		this.build = build;
	}

	public String getBuild() {
		return build;
	}

	public void setStatus(QueryStatus status) {
		this.status = status;
	}

	public QueryStatus getStatus() {
		return status;
	}

	public int getVariants() {
		return variants;
	}

	public void set_bins(float[] _bins) {
		this._bins = _bins;
	}

	public List<Result> getResults() {
		return results;
	}

	public void setResults(List<Result> results) {
		this.results = results;
	}

	public void run() {

		setStatus(QueryStatus.RUNNING);
		setRawQuery(query);
		Query.save(this, _workspace);

		try {

			long start = System.currentTimeMillis();
			
			GenomicRegion region = GenomicRegion.parse(query, build);
			setQuery(region.getName());

			ExtractVariantsTask task = new ExtractVariantsTask(_datasets, _bins);
			variants = task.findVariants(region);
			results = task.getResults();
			long end = System.currentTimeMillis();

			setExecutionTime(end - start);
			setFinisehdOn(new Date());
			setStatus(QueryStatus.SUCCEDED);
			Query.save(this, _workspace);

		} catch (Exception e) {
			e.printStackTrace();
			setExecutionTime(0);
			setFinisehdOn(new Date());
			setStatus(QueryStatus.FAILED);
			setError(e.getMessage());
			Query.save(this, _workspace);

		}
	}

	public static synchronized void save(Query query, String workspace) {
		String jobFilename = FileUtil.path(workspace, query.getId() + ".json");
		Gson gson = new Gson();
		FileUtil.writeStringBufferToFile(jobFilename, new StringBuffer(gson.toJson(query)));

		if (query.getResults() == null) {
			return;
		}

		String csvFilename = FileUtil.path(workspace, query.getId() + ".csv");
		CsvTableWriter writer = new CsvTableWriter(csvFilename);
		writer.setColumns(new String[] { "name", "reference", "chip", "population", "maf", "mean", "percentage08" });
		for (Result result : query.getResults()) {
			for (AggregatedBin bin : result.getAggregatedBins()) {
				writer.setString("name", result.getSubDataset().getName());
				writer.setString("reference", result.getSubDataset().getReference());
				writer.setString("chip", result.getSubDataset().getChip());
				writer.setString("population", result.getSubDataset().getPopulation());
				writer.setDouble("maf", bin.getEnd());
				writer.setDouble("mean", bin.getMean());
				writer.setDouble("percentage08", bin.getPercentage08());
				writer.next();
			}
		}
		writer.close();

	}

	public synchronized static Query create(String id, String workspace, List<Dataset> datasets, float[] bins,
			String build) {
		Query query = new Query();
		query.setId(id);
		query.setStatus(QueryStatus.SUBMITTED);
		query.setSubmittedOn(new Date());
		query.setExpiresOn(new Date(System.currentTimeMillis() + (EXPIRES_HOURS * 60 * 60 * 1000)));
		query._workspace = workspace;
		query._datasets = datasets;
		query._bins = bins;
		query.build = build;
		Query.save(query, workspace);
		return query;
	}

	public synchronized static Query findById(String id, String workspace)
			throws JsonSyntaxException, JsonIOException, FileNotFoundException {
		String filename = FileUtil.path(workspace, id + ".json");
		File jobFile = new File(filename);

		if (jobFile.exists()) {
			Gson gson = new Gson();
			Query query = gson.fromJson(new FileReader(jobFile), Query.class);
			if (query.getResults() != null) {
				for (Result result : query.getResults()) {
					result.updateCounter();
				}
			}
			return query;
		} else {
			return null;
		}
	}

}
