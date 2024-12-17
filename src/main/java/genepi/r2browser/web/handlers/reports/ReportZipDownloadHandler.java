package genepi.r2browser.web.handlers.reports;

import genepi.r2browser.App;
import genepi.r2browser.config.Configuration;
import genepi.r2browser.model.Report;
import genepi.r2browser.web.util.AbstractHandler;
import io.javalin.http.ContentType;
import io.javalin.http.Context;
import io.javalin.http.HandlerType;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;

public class ReportZipDownloadHandler extends AbstractHandler {

	public static final String PATH = "/reports/{report}/report.zip";

	public static final HandlerType TYPE = HandlerType.GET;

	private Configuration configuration = App.getDefault().getConfiguration();

	private String workspace = configuration.getWorkspace();

	public void handle(Context context) throws Exception {

		String id = context.pathParam("report");

		Report report = Report.findById(id, workspace);

		if (report != null) {

			Date now = new Date();
			if (now.after(report.getExpiresOn())) {
				throw new Exception("Report expired.");
			}

			if (report.getZip() != null) {
				context.contentType(ContentType.APPLICATION_ZIP);
				context.result(Files.newInputStream(Paths.get(report.getZip())));
				return;
			}

		}

		throw new Exception("Report not found.");

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
