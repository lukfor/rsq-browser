package genepi.r2browser.model;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import genepi.io.FileUtil;
import genepi.r2browser.util.Quarto;
import genepi.r2browser.util.GenomicRegion;
import genepi.r2browser.web.util.HtmlToZip;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.Path;
import java.util.*;

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

	private String zip = null;

	private String stdErr;

	private String stdOut;

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

	public void setZip(String zip) {
		this.zip = zip;
	}

	public String getZip() {
		return zip;
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

			List<String> genesCoordinates = new ArrayList<>();
			if (params.containsKey("genes")) {
				Object genes = params.get("genes");
				if (genes instanceof  String) {
					GenomicRegion region = GenomicRegion.parse(genes.toString(), "hg38");
					genesCoordinates.add(region.toString());
					List<String> list = new ArrayList<>();
					list.add(genes.toString());
					params.put("genes", list);
				}
				if (genes instanceof  List) {
					for (Object gene: (List) genes) {
						GenomicRegion region = GenomicRegion.parse(gene.toString(), "hg38");
						genesCoordinates.add(region.toString());
					}
				}
			}
			params.put("genes_coordinates", genesCoordinates);

			List<String> snpsCoordinates = new ArrayList<>();
			if (params.containsKey("snps")) {
				Object snps = params.get("snps");
				if (snps instanceof  String) {
					GenomicRegion region = GenomicRegion.parse(snps.toString(), "hg38");
					snpsCoordinates.add(region.toString());
					List<String> list = new ArrayList<>();
					list.add(snps.toString());
					params.put("snps", list);
				}
				if (snps instanceof  List) {
					for (Object snp: (List) snps) {
						GenomicRegion region = GenomicRegion.parse(snp.toString(), "hg38");
						snpsCoordinates.add(region.toString());
					}
				}
			}
			params.put("snps_coordinates", snpsCoordinates);

			if (params.containsKey("pgs")) {
				Object pgs = params.get("pgs");
				if (pgs instanceof  String) {
					List<String> list = new ArrayList<>();
					list.add(pgs.toString());
					params.put("pgs", list);
				}
			}

			Path stdoutFile = workspaceDir.resolve("command.out");
			Path stderrFile = workspaceDir.resolve("command.err");
			setStdOut(stdoutFile.toString());
			setStdErr(stderrFile.toString());

			String output = getId() + ".html";
			Quarto quarto = new Quarto(report, params, output, workspaceDir);
			boolean success = quarto.render();

			long end = System.currentTimeMillis();
			setExecutionTime(end - start);
			setFinisehdOn(new Date());
			if (success) {
				setStatus(QueryStatus.SUCCEDED);
				setOutput(FileUtil.path(_workspace, getId(), output));
				String zipFilename =  getOutput() + ".zip";
				HtmlToZip.createZipFromHtml(getOutput(), zipFilename);
				setZip(zipFilename);
			} else {
				setStatus(QueryStatus.FAILED);
				setError("Rendering report failed.");
			}
			Report.save(this, _workspace);

		} catch (Exception e) {
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

	public String getStdErr() {
		return stdErr;
	}

	public void setStdErr(String stdErr) {
		this.stdErr = stdErr;
	}

	public String getStdOut() {
		return stdOut;
	}

	public void setStdOut(String stdOut) {
		this.stdOut = stdOut;
	}
}
