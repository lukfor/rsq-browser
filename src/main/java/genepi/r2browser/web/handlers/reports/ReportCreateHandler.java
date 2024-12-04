package genepi.r2browser.web.handlers.reports;

import genepi.io.FileUtil;
import genepi.r2browser.App;
import genepi.r2browser.config.Configuration;
import genepi.r2browser.model.Dataset;
import genepi.r2browser.model.Report;
import genepi.r2browser.model.SubDataset;
import genepi.r2browser.web.util.AbstractHandler;
import genepi.r2browser.web.util.JobQueue;
import genepi.r2browser.web.util.RouteUtil;
import io.javalin.http.Context;
import io.javalin.http.HandlerType;
import org.apache.commons.lang3.RandomStringUtils;

import javax.xml.bind.DatatypeConverter;
import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.stream.Collectors;

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

	protected Report submitReport(Map<String, List<String>> _params) throws Exception {

		String reportId = hash(RandomStringUtils.randomAlphanumeric(configuration.getJobIdLength()));
		Map<String, Object> params = convertMap(_params);
		if (!params.containsKey("population")) {
			throw new Exception("No population provided");
		}
		String population = params.get("population").toString();
		for (Dataset dataset: configuration.getDatasets()) {
			for (SubDataset subset: dataset.getSubsets()) {
				if (subset.getPopulation().equalsIgnoreCase(population)){
					params.put("population_file", new File(dataset.getFilename()).getAbsolutePath());
				}
			}
		}
		if (!params.containsKey("population_file")) {
			throw new Exception("No population file found for " + population);
		}
		Report report = Report.findById(reportId, workspace);

		if (report == null) {

			FileUtil.createDirectory(workspace);
			report = Report.create(reportId, workspace, configuration.getDatasets());
			report.setParams(params);
			report.setRmd("reports/reports.Rmd");
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


	public static Map<String, Object> convertMap(Map<String, List<String>> inputMap) {
		Map<String, Object> resultMap = new HashMap<>();

		for (Map.Entry<String, List<String>> entry : inputMap.entrySet()) {
			String key = entry.getKey();
			List<String> value = entry.getValue();

			if (value != null && value.size() == 1) {
				// Split the single string by ',' or '\n' and trim spaces
				List<String> parsedList = Arrays.stream(value.get(0).split("[,\n]"))
						.map(String::trim)
						.filter(s -> !s.isEmpty())
						.collect(Collectors.toList());
				if (parsedList.size() == 1) {
					resultMap.put(key, parsedList.get(0)); // Single element: store as a string
				} else {
					resultMap.put(key, parsedList); // Multiple elements: store as a list
				}
			} else {
				resultMap.put(key, value); // Keep the list as is
			}
		}

		return resultMap;
	}


}
