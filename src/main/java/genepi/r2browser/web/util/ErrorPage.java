package genepi.r2browser.web.util;

import org.apache.commons.lang3.exception.ExceptionUtils;

public class ErrorPage extends Page {

	private static final long serialVersionUID = 1L;

	public static final String TEMPLATE = "web/error.view.html";

	public ErrorPage() {
		put("stackTrace", "");
	}

	public void setTitle(String title) {
		put("title", title);
	}

	public void setMessage(String message) {
		put("message", message);
	}

	public void setException(Throwable exception) {
		String stackTrace = ExceptionUtils.getStackTrace(exception);
		put("stackTrace", stackTrace);
	}


}
