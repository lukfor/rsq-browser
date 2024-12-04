package genepi.r2browser.web;

import genepi.r2browser.web.handlers.AboutPageHandler;
import genepi.r2browser.web.handlers.WizardPageHandler;
import genepi.r2browser.web.handlers.ErrorHandler;
import genepi.r2browser.web.handlers.IndexPageHandler;
import genepi.r2browser.web.handlers.downloads.DownloadDetailsHandler;
import genepi.r2browser.web.handlers.downloads.DownloadListHandler;
import genepi.r2browser.web.handlers.downloads.FileDetailsHandler;
import genepi.r2browser.web.handlers.queries.QueriesCreateHandler;
import genepi.r2browser.web.handlers.queries.QueriesShowHandler;
import genepi.r2browser.web.handlers.wizard.ReportCreateHandler;
import genepi.r2browser.web.handlers.wizard.ReportShowHandler;
import genepi.r2browser.web.util.AbstractErrorHandler;
import genepi.r2browser.web.util.AbstractWebApp;

public class WebApp extends AbstractWebApp {

	public WebApp(int port) {
		super(port);
	}

	protected void routes() {
		route("index", new IndexPageHandler());
		route("about", new AboutPageHandler());
		route("wizard", new WizardPageHandler());
		route("queries_create", new QueriesCreateHandler());
		route("queries_show", new QueriesShowHandler());
		route("reports_create", new ReportCreateHandler());
		route("reports_show", new ReportShowHandler());
		route("download_list", new DownloadListHandler());
		route("download_details", new DownloadDetailsHandler());
		route("file_details", new FileDetailsHandler());
	}

	@Override
	protected AbstractErrorHandler errorHandler() {
		return new ErrorHandler();
	}

}
