package genepi.r2browser.web.util;

import java.util.Map;
import java.util.function.Function;

import com.google.gson.Gson;

import genepi.r2browser.App;
import genepi.r2browser.web.util.functions.DecimalFunction;
import genepi.r2browser.web.util.functions.DoubleFormatFunction;
import genepi.r2browser.web.util.functions.IncludeScriptFunction;
import genepi.r2browser.web.util.functions.IncludeStyleFunction;
import genepi.r2browser.web.util.functions.PercentageFunction;
import genepi.r2browser.web.util.functions.RouteFunction;
import genepi.r2browser.web.util.functions.ToJsonFunction;
import io.marioslab.basis.template.Template;
import io.marioslab.basis.template.TemplateContext;
import io.marioslab.basis.template.TemplateLoader;
import io.micronaut.web.router.AnnotatedMethodRouteBuilder;
import io.micronaut.web.router.RouteBuilder;
import jakarta.inject.Inject;

public class PageRenderer {

	protected String template;

	@Inject
	protected App app;
	
	private String root = "";
	
	private TemplateLoader loader;
	
	@Inject
	protected AnnotatedMethodRouteBuilder routeBuilder;
	
	public String render(String template, Map<String, Object> page) throws Exception {

		this.template = template;

		page.put("application", App.NAME);
		page.put("version", App.VERSION);
		page.put("baseUrl", app.getConfiguration().getBaseUrl());
		page.put("debug", App.isDevelopmentSystem());

		return renderBasisTemplate(template, page);
		
	}

	
	public String renderBasisTemplate(String filePath, Map<String, Object> model) throws Exception {

		System.out.println(routeBuilder.getUriRoutes());
		
		// reload external files on every call (hot reloading for development)
		loader = new TemplateLoader.ClasspathTemplateLoader();

		TemplateContext templateContext = new TemplateContext();
		for (String name : model.keySet()) {
			templateContext.set(name, model.get(name));
		}

		// Add default functions
		templateContext.set("percentage", new PercentageFunction());
		templateContext.set("decimal", new DecimalFunction());
		templateContext.set("formatDouble", new DoubleFormatFunction());
		templateContext.set("includeScript", new IncludeScriptFunction());
		templateContext.set("includeStyle", new IncludeStyleFunction());
		templateContext.set("json", new ToJsonFunction());
		templateContext.set("routeUrl", new RouteFunction());

		/*if (context.handlerType() != HandlerType.BEFORE) {
			String path = context.endpointHandlerPath();
			String route = server.getNameByPath(path);
			templateContext.set("route", route != null ? route : "");
			templateContext.set("isRouteActive", new IsRouteActiveFunction(route != null ? route : ""));
		} else { */
			templateContext.set("route", "");
			templateContext.set("isRouteActive", new Function<String, Boolean>() {				
				@Override
				public Boolean apply(String arg0) {
					return false;
				}
			});
		//}

		templateContext.set("gson", new Gson());
		// application specific helper

		try {
			Template template = loadTemplate(filePath);
			return template.render(templateContext);
		} catch (Exception e) {
			return "Error in template '" + filePath + "': " + e.toString();
		}

	}

	public Template loadTemplate(String path) {
		String filename = "";
		if (path.startsWith("/")) {
			filename = root + path;
		} else {
			filename = root + "/" + path;
		}
		return loader.load(filename);
	}
	
}
