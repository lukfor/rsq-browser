package genepi.r2browser.web.handlers.reports;

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
import java.util.*;

public class ReportCreateHandler extends AbstractHandler {

	public static final String PATH = "/reports";

	public static final String WIZARD_REPORT = "reports/wizard";

	public static final HandlerType TYPE = HandlerType.POST;

	private Configuration configuration = App.getDefault().getConfiguration();

	private JobQueue jobQueue = App.getDefault().getJobQueue();

	private String workspace = configuration.getWorkspace();

	public void handle(Context context) throws Exception {

        Map<String, String> form = convertMap(context.formParamMap());
		//TODO: read template from url.
        Report report = submitReport(WIZARD_REPORT, form);

		HashMap<String, Object> params = new HashMap<String, Object>();
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

	protected Report submitReport(String template, Map<String, String> params) throws Exception {

		String reportId = hash(RandomStringUtils.randomAlphanumeric(configuration.getJobIdLength()));

		Report report = Report.findById(reportId, workspace);

		if (report == null) {

			FileUtil.createDirectory(workspace);
			report = Report.create(reportId, workspace);
			report.setTemplate(template);
			report.setParams(params);
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

	public static Map<String, String> convertMap(Map<String, List<String>> inputMap) {
		Map<String, String> resultMap = new HashMap<>();

		for (Map.Entry<String, List<String>> entry : inputMap.entrySet()) {
			String key = entry.getKey();
			List<String> valueList = entry.getValue();

			// Use the first element of the list if it exists
			if (valueList != null && !valueList.isEmpty()) {
				resultMap.put(key, valueList.get(0));
			} else {
				resultMap.put(key, null); // Optionally set null if the list is empty
			}
		}

		return resultMap;
	}

}
