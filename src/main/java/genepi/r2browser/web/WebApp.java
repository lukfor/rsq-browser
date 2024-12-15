package genepi.r2browser.web;

import genepi.r2browser.web.handlers.*;
import genepi.r2browser.web.handlers.downloads.DownloadDetailsHandler;
import genepi.r2browser.web.handlers.downloads.DownloadListHandler;
import genepi.r2browser.web.handlers.downloads.FileDetailsHandler;
import genepi.r2browser.web.handlers.queries.QueriesCreateHandler;
import genepi.r2browser.web.handlers.queries.QueriesShowHandler;
import genepi.r2browser.web.handlers.reports.*;
import genepi.r2browser.web.util.AbstractErrorHandler;
import genepi.r2browser.web.util.AbstractWebApp;

public class WebApp extends AbstractWebApp {

	public WebApp(int port) {
		super(port);
	}

	protected void routes() {
		route("index", new IndexPageHandler());
		route("about", new AboutPageHandler());
		route("search", new SearchPageHandler());
		route("wizard", new WizardPageHandler());
		route("queries_create", new QueriesCreateHandler());
		route("queries_show", new QueriesShowHandler());
		route("reports_create", new ReportCreateHandler());
		route("reports_show", new ReportShowHandler());
		route("reports_logs", new ReportLogsHandler());
		route("reports_download", new ReportDownloadHandler());
		route("reports_download_zip", new ReportZipDownloadHandler());
		route("download_list", new DownloadListHandler());
		route("download_details", new DownloadDetailsHandler());
		route("file_details", new FileDetailsHandler());
	}

	@Override
	protected AbstractErrorHandler errorHandler() {
		return new ErrorHandler();
	}

}
