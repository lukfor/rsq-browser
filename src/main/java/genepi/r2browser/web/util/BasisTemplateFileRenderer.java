package genepi.r2browser.web.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Map;
import java.util.function.Function;

import com.google.gson.Gson;

import genepi.io.FileUtil;
import genepi.r2browser.web.WebApp;
import genepi.r2browser.web.util.functions.DecimalFunction;
import genepi.r2browser.web.util.functions.DoubleFormatFunction;
import genepi.r2browser.web.util.functions.EscapeFunction;
import genepi.r2browser.web.util.functions.IncludeScriptFunction;
import genepi.r2browser.web.util.functions.IncludeStyleFunction;
import genepi.r2browser.web.util.functions.IsRouteActiveFunction;
import genepi.r2browser.web.util.functions.PercentageFunction;
import genepi.r2browser.web.util.functions.RouteFunction;
import genepi.r2browser.web.util.functions.ToJsonFunction;
import io.javalin.http.Context;
import io.javalin.http.HandlerType;
import io.javalin.http.staticfiles.Location;
import io.javalin.plugin.rendering.FileRenderer;
import io.marioslab.basis.template.Error;
import io.marioslab.basis.template.Template;
import io.marioslab.basis.template.TemplateContext;
import io.marioslab.basis.template.TemplateLoader;
import io.marioslab.basis.template.TemplateLoader.CachingTemplateLoader;
import io.marioslab.basis.template.TemplateLoader.Source;
import io.marioslab.basis.template.parsing.Span;

public class BasisTemplateFileRenderer implements FileRenderer {

	private String root;

	private TemplateLoader loader;

	private Location location;

	private AbstractWebApp server;

	public BasisTemplateFileRenderer(String root, Location location, AbstractWebApp server) {

		this.root = root;
		this.location = location;
		if (location == Location.EXTERNAL) {
			loader = new TemplateLoader.FileTemplateLoader();
		} else {
			loader = new MyClasspathTemplateLoader();
		}

		this.server = server;
	}

	public String render(String filePath, Map<String, Object> model, Context context) throws Exception {

		// reload external files on every call (hot reloading for development)
		if (location == Location.EXTERNAL) {
			loader = new TemplateLoader.FileTemplateLoader();
		} else {
			loader = new MyClasspathTemplateLoader();
		}

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
		templateContext.set("routeUrl", new RouteFunction(server));
		templateContext.set("escape", new EscapeFunction());
		
		if (context.handlerType() != HandlerType.BEFORE) {
			String path = context.endpointHandlerPath();
			String route = server.getNameByPath(path);
			templateContext.set("route", route != null ? route : "");
			templateContext.set("isRouteActive", new IsRouteActiveFunction(route != null ? route : ""));
		} else {
			templateContext.set("route", "");
			templateContext.set("isRouteActive", new Function<String, Boolean>() {				
				@Override
				public Boolean apply(String arg0) {
					return false;
				}
			});
		}

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

	/** A TemplateLoader to load templates from the classpath. Extended to support relative paths with ../ **/
	public static class MyClasspathTemplateLoader extends CachingTemplateLoader {
		@Override
		protected Source loadSource (String path) {
			try {
								
				String filename = FileUtil.getFilename(path);				
				URI uri = URI.create(path);
				String resolvedPath =  uri.resolve("").toString() + filename;
				
				return new Source(path, MyStreamUtils.readString(WebApp.class.getResourceAsStream(resolvedPath)));
			} catch (Throwable t) {
				t.printStackTrace();
				Error.error("Couldn't load template '" + path + "'.", new Span(new Source(path, " "), 0, 0), t);
				throw new RuntimeException(""); // never reached
			}
		}
	}
	
	static class MyStreamUtils {
		private static String readString (InputStream in) throws IOException {
			byte[] buffer = new byte[1024 * 10];
			int read = 0;
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			while ((read = in.read(buffer)) != -1) {
				out.write(buffer, 0, read);
			}
			return new String(out.toByteArray(), "UTF-8");
		}
	}
}
