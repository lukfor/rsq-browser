package genepi.r2web.web.handlers;

import genepi.r2web.App;
import genepi.r2web.web.util.AbstractErrorHandler;
import genepi.r2web.web.util.ErrorPage;
import io.javalin.http.Context;

public class ErrorHandler extends AbstractErrorHandler {

	public void handle(Context context) throws Exception {

		ErrorPage page = new ErrorPage(context);
		page.setTitle("Error 404");
		page.setMessage("Page not found.");
		page.render();

	}

	public void handle(Exception exception, Context context) {

		ErrorPage page = new ErrorPage(context);
		page.setTitle("Error");
		if (exception.getMessage() != null) {
			page.setMessage(exception.getMessage());
		} else {
			page.setMessage(null);
			page.setMessage(exception.getStackTrace().toString());
		}
		if (App.isDevelopmentSystem()) {
			page.setException(exception);
		}
		page.render();

	}

}
