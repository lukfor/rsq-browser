package genepi.r2browser.model;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import genepi.io.FileUtil;
import genepi.io.table.writer.CsvTableWriter;
import genepi.r2browser.tasks.ExtractVariantsTask;
import genepi.r2browser.tasks.Quarto;
import genepi.r2browser.tasks.RMarkdownScript;
import genepi.r2browser.util.GenomicRegion;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class Report implements Runnable {

	public static final String WIZARD_REPORT = "reports/wizard";

	private String id;

	private String rmd;

	private Map<String, Object> params;

	private Date submittedOn;

	private Date finisehdOn;

	private Date expiresOn;

	private long executionTime;

	private String output = null;

	private String error;

	private QueryStatus status;

	private transient String _workspace;

	public static int EXPIRES_HOURS = 4;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setParams(Map<String, Object> params) {
		this.params = params;
	}

	public Map<String, Object> getParams() {
		return params;
	}

	public void setRmd(String rmd) {
		this.rmd = rmd;
	}

	public String getRmd() {
		return rmd;
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

	public void setStatus(QueryStatus status) {
		this.status = status;
	}

	public QueryStatus getStatus() {
		return status;
	}

	public void setOutput(String output) {
		this.output = output;
	}

	public String getOutput() {
		return output;
	}

	public void run() {

		setStatus(QueryStatus.RUNNING);

		Report.save(this, _workspace);

		try {

			long start = System.currentTimeMillis();

			Path report = new File(WIZARD_REPORT).getAbsoluteFile().toPath();
			Path workspaceDir = new File(_workspace, getId()).getAbsoluteFile().toPath();

			//RMarkdownScript rMarkdownScript = new RMarkdownScript(report, params, output);
			//rMarkdownScript.run();


			String output = getId() + ".html";
			Quarto quarto = new Quarto(report, params, output, workspaceDir);
			quarto.render();

			long end = System.currentTimeMillis();

			setExecutionTime(end - start);
			setFinisehdOn(new Date());
			setStatus(QueryStatus.SUCCEDED);
			setOutput(FileUtil.path(_workspace, getId(), output));
			Report.save(this, _workspace);

		} catch (Exception e) {
			e.printStackTrace();
			setExecutionTime(0);
			setFinisehdOn(new Date());
			setStatus(QueryStatus.FAILED);
			setError(e.getMessage());
			Report.save(this, _workspace);

		}
	}

	public static synchronized void save(Report query, String workspace) {
		String jobFilename = FileUtil.path(workspace, query.getId() + ".json");
		Gson gson = new Gson();
		FileUtil.writeStringBufferToFile(jobFilename, new StringBuffer(gson.toJson(query)));
	}

	public synchronized static Report create(String id, String workspace, List<Dataset> datasets) {
		Report report = new Report();
		report.setId(id);
		report.setStatus(QueryStatus.SUBMITTED);
		report.setSubmittedOn(new Date());
		report.setExpiresOn(new Date(System.currentTimeMillis() + (EXPIRES_HOURS * 60 * 60 * 1000)));
		report._workspace = workspace;
		Report.save(report, workspace);
		return report;
	}

	public synchronized static Report findById(String id, String workspace)
			throws JsonSyntaxException, JsonIOException, FileNotFoundException {
		String filename = FileUtil.path(workspace, id + ".json");
		File jobFile = new File(filename);

		if (jobFile.exists()) {
			Gson gson = new Gson();
			Report report = gson.fromJson(new FileReader(jobFile), Report.class);
			return report;
		} else {
			return null;
		}
	}

}