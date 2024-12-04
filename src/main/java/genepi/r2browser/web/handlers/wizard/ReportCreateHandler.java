package genepi.r2browser.web.handlers.wizard;

import genepi.io.FileUtil;
import genepi.r2browser.App;
import genepi.r2browser.config.Configuration;
import genepi.r2browser.model.Report;
import genepi.r2browser.web.util.AbstractHandler;
import genepi.r2browser.web.util.JobQueue;
import genepi.r2browser.web.util.RouteUtil;
import io.javalin.http.Context;
import io.javalin.http.HandlerType;
import org.apache.commons.lang3.RandomStringUtils;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class ReportCreateHandler extends AbstractHandler {

	public static final String PATH = "/reports";

	public static final HandlerType TYPE = HandlerType.POST;

	private Configuration configuration = App.getDefault().getConfiguration();

	private JobQueue jobQueue = App.getDefault().getJobQueue();

	private String workspace = configuration.getWorkspace();

	public void handle(Context context) throws Exception {

		Report report = submitReport(context.formParamMap());

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("report", report.getId());
		String path = RouteUtil.path(configuration.getBaseUrl() + ReportShowHandler.PATH, params);

		context.redirect(path);

	}

	@Override
	public String getPath() {
		return configuration.getBaseUrl() + PATH;
	}

	@Override
	public HandlerType getType() {
		return TYPE;
	}

	protected Report submitReport(Map<String, List<String>> params) throws Exception {

		String reportId = hash(RandomStringUtils.randomAlphanumeric(configuration.getJobIdLength()));

		Report report = Report.findById(reportId, workspace);

		if (report == null) {

			FileUtil.createDirectory(workspace);
			report = Report.create(reportId, workspace, configuration.getDatasets());
			report.setParams(params);
			report.setRmd("reports/wizard.Rmd");
			jobQueue.submit(report);

		}

		return report;
	}

	protected String hash(String hash) throws NoSuchAlgorithmException {

		MessageDigest md = MessageDigest.getInstance("MD5");
		md.update(hash.getBytes());
		byte[] digest = md.digest();
		return DatatypeConverter.printHexBinary(digest).toLowerCase();

	}

}
