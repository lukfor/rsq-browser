package genepi.r2web.web.util;

import java.util.HashMap;
import java.util.Map;

import genepi.r2web.App;
import genepi.r2web.web.handlers.ErrorHandler;
import io.javalin.Javalin;
import io.javalin.http.ExceptionHandler;
import io.javalin.http.staticfiles.Location;
import io.javalin.plugin.rendering.JavalinRenderer;

public abstract class AbstractWebApp {

	private static final String VIEW_EXTENSION = ".html";

	public final String ROOT_DIR = "/web/public";

	private int port;

	private Javalin server;
	
	public AbstractWebApp(int port) {

		this.port = port;

	}

	public void start() {

		server = Javalin.create();
		defaultRoutes();
		routes();

		if (App.isDevelopmentSystem()) {

			// load templates and static files from external files not from classpath
			// auto reloading possible, no restart needed, ....
			server._conf.addStaticFiles("src/main/resources" + ROOT_DIR, Location.EXTERNAL);
			JavalinRenderer.register(new BasisTemplateFileRenderer("src/main/resources", Location.EXTERNAL, this),
					VIEW_EXTENSION);

		} else {

			server._conf.addStaticFiles(ROOT_DIR, Location.CLASSPATH);
			JavalinRenderer.register(new BasisTemplateFileRenderer("", Location.CLASSPATH, this), VIEW_EXTENSION);

		}

		server.start(port);

		while (true) {
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

	protected void defaultRoutes() {
		server.error(404, errorHandler());
		server.exception(Exception.class, errorHandler());
	}
	
	abstract protected AbstractErrorHandler errorHandler();
	
	abstract protected void routes();


	Map<String, AbstractHandler> namedRoutes = new HashMap<String, AbstractHandler>();
	Map<String, String> pathRoutes = new HashMap<String, String>();

	public void route(String route, AbstractHandler handler) {
		server.addHandler(handler.getType(), handler.getPath(), handler);
		namedRoutes.put(route, handler);
		pathRoutes.put(handler.getPath(), route);
	}

	public AbstractHandler getHandlerByName(String name) {
		return namedRoutes.get(name);
	}

	public String getNameByPath(String path) {
		return pathRoutes.get(path);
	}

}