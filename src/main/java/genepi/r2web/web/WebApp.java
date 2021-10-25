package genepi.r2web.web;

import genepi.r2web.web.handlers.ContactPageHandler;
import genepi.r2web.web.handlers.ErrorHandler;
import genepi.r2web.web.handlers.IndexPageHandler;
import genepi.r2web.web.handlers.queries.QueriesCreateHandler;
import genepi.r2web.web.handlers.queries.QueriesShowHandler;
import genepi.r2web.web.util.AbstractErrorHandler;
import genepi.r2web.web.util.AbstractWebApp;

public class WebApp extends AbstractWebApp {

	public WebApp(int port) {
		super(port);
	}

	protected void routes() {
		route("index", new IndexPageHandler());
		route("contact", new ContactPageHandler());
		route("queries_create", new QueriesCreateHandler());
		route("queries_show", new QueriesShowHandler());
	}

	@Override
	protected AbstractErrorHandler errorHandler() {
		return new ErrorHandler();
	}

}
