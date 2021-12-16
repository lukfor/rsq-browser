package genepi.r2browser.web;

import genepi.r2browser.web.handlers.AboutPageHandler;
import genepi.r2browser.web.handlers.ErrorHandler;
import genepi.r2browser.web.handlers.IndexPageHandler;
import genepi.r2browser.web.handlers.queries.QueriesCreateHandler;
import genepi.r2browser.web.handlers.queries.QueriesShowHandler;
import genepi.r2browser.web.util.AbstractErrorHandler;
import genepi.r2browser.web.util.AbstractWebApp;

public class WebApp extends AbstractWebApp {

	public WebApp(int port) {
		super(port);
	}

	protected void routes() {
		route("index", new IndexPageHandler());
		route("about", new AboutPageHandler());
		route("queries_create", new QueriesCreateHandler());
		route("queries_show", new QueriesShowHandler());
	}

	@Override
	protected AbstractErrorHandler errorHandler() {
		return new ErrorHandler();
	}

}
