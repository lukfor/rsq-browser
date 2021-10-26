package genepi.r2web.web.handlers;

import genepi.r2web.App;
import genepi.r2web.config.Configuration;
import genepi.r2web.web.util.AbstractHandler;
import genepi.r2web.web.util.Page;
import io.javalin.http.Context;
import io.javalin.http.HandlerType;

public class IndexPageHandler extends AbstractHandler {

	public static final String PATH = "/";

	public static final HandlerType TYPE = HandlerType.GET;

	public static final String TEMPLATE = "web/index.view.html";

	private Configuration configuration = App.getDefault().getConfiguration();

	public void handle(Context context) throws Exception {

		Page page = new Page(context, TEMPLATE);
		page.put("configuration", configuration);
		page.render();

	}

	@Override
	public String getPath() {
		return PATH;
	}

	@Override
	public HandlerType getType() {
		return TYPE;
	}

}
