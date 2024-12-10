package genepi.r2browser.web.handlers.reports;

import genepi.r2browser.App;
import genepi.r2browser.config.Configuration;
import genepi.r2browser.model.Report;
import genepi.r2browser.web.util.AbstractHandler;
import genepi.r2browser.web.util.Page;
import io.javalin.http.ContentType;
import io.javalin.http.Context;
import io.javalin.http.HandlerType;

import java.nio.file.Files;
import java.nio.file.Paths;

public class ReportShowHandler extends AbstractHandler {

	public static final String PATH = "/reports/{report}";

	public static final HandlerType TYPE = HandlerType.GET;

	private Configuration configuration = App.getDefault().getConfiguration();

	private String workspace = configuration.getWorkspace();

	public void handle(Context context) throws Exception {

		String id = context.pathParam("report");

		Report report = Report.findById(id, workspace);

		if (report != null) {

			String template = "web/reports/show." + report.getStatus().name().toLowerCase() + ".view.html";

			Page page = new Page(context, template);
			page.put("report", report);
			page.put("configuration", configuration);
			page.put("downloads", configuration.getDownloads());
			page.render();


		} else {
			throw new Exception("Report not found.");
		}

	}

	@Override
	public String getPath() {
		return configuration.getBaseUrl()+ PATH;
	}

	@Override
	public HandlerType getType() {
		return TYPE;
	}

}
