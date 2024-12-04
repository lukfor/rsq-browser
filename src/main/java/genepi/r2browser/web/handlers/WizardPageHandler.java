package genepi.r2browser.web.handlers;

import genepi.r2browser.App;
import genepi.r2browser.config.Configuration;
import genepi.r2browser.web.util.AbstractHandler;
import genepi.r2browser.web.util.Page;
import io.javalin.http.Context;
import io.javalin.http.HandlerType;

public class WizardPageHandler extends AbstractHandler {

	public static final String PATH = "/wizard";

	public static final HandlerType TYPE = HandlerType.GET;

	public static final String TEMPLATE = "web/wizard.view.html";

	private Configuration configuration = App.getDefault().getConfiguration();

	public void handle(Context context) throws Exception {

		Page page = new Page(context, TEMPLATE);
		page.put("configuration", configuration);
		page.render();

	}

	@Override
	public String getPath() {
		return configuration.getBaseUrl() + PATH;
	}

	@Override
	public HandlerType getType() {
		return TYPE;
	}

}
