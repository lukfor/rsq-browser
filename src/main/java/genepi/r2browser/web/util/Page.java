package genepi.r2browser.web.util;

import java.util.HashMap;

import genepi.r2browser.App;
import io.javalin.http.Context;

public class Page extends HashMap<String, Object> {

	private static final long serialVersionUID = 1L;

	protected Context context;

	protected String template;

	public Page(Context context, String template) {

		this.context = context;
		this.template = template;

		put("application", App.NAME);
		put("version", App.VERSION);
		put("baseUrl", App.getDefault().getConfiguration().getBaseUrl());
		put("debug", App.isDevelopmentSystem());

	}

	public void render() {
		context.render(template, this);
	}

}
