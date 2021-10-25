package genepi.r2web.model;

import java.util.Date;

import com.google.gson.Gson;

import genepi.io.FileUtil;
import genepi.r2web.tasks.ExtractVariantsTask;
import genepi.r2web.util.GenomicRegion;

public class Query implements Runnable {

	private String id;

	private String hash;

	private String query;

	private int results;

	private Date submittedOn;

	private Date finisehdOn;

	private Date expiresOn;

	private long executionTime;

	private String error;

	private QueryStatus status;

	private transient String _workspace;

	private transient Dataset _dataset;

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

	public void setResults(int results) {
		this.results = results;
	}

	public int getResults() {
		return results;
	}

	public static Query create(String id, String workspace, Dataset dataset) {
		Query query = new Query();
		query.setId(id);
		query.setStatus(QueryStatus.SUBMITTED);
		query.setSubmittedOn(new Date());
		query.setExpiresOn(new Date(System.currentTimeMillis() + (EXPIRES_HOURS * 60 * 60 * 1000)));
		query._workspace = workspace;
		query._dataset = dataset;
		query.save();
		return query;
	}

	public void run() {

		setStatus(QueryStatus.RUNNING);
		save();

		try {

			long start = System.currentTimeMillis();

			GenomicRegion region = GenomicRegion.parse(query);

			ExtractVariantsTask task = new ExtractVariantsTask();
			results = task.findVariants(_dataset, region);

			long end = System.currentTimeMillis();

			setExecutionTime(end - start);
			setFinisehdOn(new Date());
			setStatus(QueryStatus.SUCCEDED);
			save();

		} catch (Exception e) {

			setExecutionTime(0);
			setFinisehdOn(new Date());
			setStatus(QueryStatus.FAILED);
			setError(e.getMessage());
			save();

		}
	}

	protected void save() {
		String jobFilename = FileUtil.path(_workspace, getId() + ".json");
		Gson gson = new Gson();
		FileUtil.writeStringBufferToFile(jobFilename, new StringBuffer(gson.toJson(this)));
	}

}
