package genepi.r2web.model;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import com.google.gson.Gson;

import genepi.io.FileUtil;
import genepi.r2web.tasks.ExtractVariantsTask;
import genepi.r2web.util.GenomicRegion;

public class Query implements Runnable {

	private String id;

	private String hash;

	private String query;

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

	private List<String> populations = null;

	private List<String> chips = null;

	private List<String> references = null;

	public static int EXPIRES_HOURS = 4;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
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

	public static Query create(String id, String workspace, List<Dataset> datasets, float[] bins) {
		Query query = new Query();
		query.setId(id);
		query.setStatus(QueryStatus.SUBMITTED);
		query.setSubmittedOn(new Date());
		query.setExpiresOn(new Date(System.currentTimeMillis() + (EXPIRES_HOURS * 60 * 60 * 1000)));
		query._workspace = workspace;
		query._datasets = datasets;
		query._bins = bins;
		query.save();
		return query;
	}

	public void run() {

		setStatus(QueryStatus.RUNNING);
		save();

		try {

			long start = System.currentTimeMillis();

			GenomicRegion region = GenomicRegion.parse(query);

			ExtractVariantsTask task = new ExtractVariantsTask(_datasets, _bins);
			variants = task.findVariants(region);
			results = task.getResults();
			long end = System.currentTimeMillis();

			setExecutionTime(end - start);
			setFinisehdOn(new Date());
			setStatus(QueryStatus.SUCCEDED);
			save();

		} catch (Exception e) {
			e.printStackTrace();
			setExecutionTime(0);
			setFinisehdOn(new Date());
			setStatus(QueryStatus.FAILED);
			setError(e.getMessage());
			save();

		}
	}

	public List<String> getReferences() {

		if (references == null) {
			references = new Vector<String>();
			;
			for (Result result : results) {
				if (!references.contains(result.getSubDataset().getReference())) {
					references.add(result.getSubDataset().getReference());
				}
			}
			Collections.sort(references);
		}

		return references;
	}

	public List<String> getPopulations() {

		if (populations == null) {
			populations = new Vector<String>();
			;
			for (Result result : results) {
				if (!populations.contains(result.getSubDataset().getPopulation())) {
					populations.add(result.getSubDataset().getPopulation());
				}
			}
			Collections.sort(populations);
		}

		return populations;
	}

	public List<String> getChips() {

		if (chips == null) {
			chips = new Vector<String>();
			;
			for (Result result : results) {
				if (!chips.contains(result.getSubDataset().getChip())) {
					chips.add(result.getSubDataset().getChip());
				}
			}
			Collections.sort(chips);
		}

		return chips;
	}

	protected void save() {
		String jobFilename = FileUtil.path(_workspace, getId() + ".json");
		Gson gson = new Gson();
		FileUtil.writeStringBufferToFile(jobFilename, new StringBuffer(gson.toJson(this)));
	}

}
