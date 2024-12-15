package genepi.r2browser.web.handlers;

import genepi.io.FileUtil;
import genepi.r2browser.App;
import genepi.r2browser.config.Configuration;
import genepi.r2browser.web.util.AbstractHandler;
import genepi.r2browser.web.util.BasisTemplateFileRenderer;
import genepi.r2browser.web.util.Page;
import io.javalin.http.Context;
import io.javalin.http.HandlerType;
import io.javalin.http.staticfiles.Location;
import io.marioslab.basis.template.Template;
import io.marioslab.basis.template.TemplateContext;
import io.marioslab.basis.template.TemplateLoader;

public class WizardPageHandler extends AbstractHandler {

	public static final String PATH = "/wizard";

	public static final String WIZARD_REPORT = "reports/wizard";

	public static final HandlerType TYPE = HandlerType.GET;

	public static final String TEMPLATE = "web/wizard.view.html";

	private Configuration configuration = App.getDefault().getConfiguration();

	public void handle(Context context) throws Exception {

		TemplateLoader.FileTemplateLoader loader = new TemplateLoader.FileTemplateLoader();
		TemplateContext templateContext = new TemplateContext();
		//TODO: read report from url and add to post_url?
		templateContext.set("post_url", configuration.getBaseUrl() +  "/reports");
		templateContext.set("configuration", configuration);

		Template template =  loader.load(FileUtil.path(WIZARD_REPORT, "form.html"));
		//TODO: check if file exists. required.
		String form = template.render(templateContext);

		Template templateJs =  loader.load(FileUtil.path(WIZARD_REPORT, "form.js"));
		//TODO: check if file exists. optional.
		String script = templateJs.render(templateContext);

		Page page = new Page(context, TEMPLATE);
		page.put("configuration", configuration);
		page.put("form", form);
		page.put("script", script);
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
