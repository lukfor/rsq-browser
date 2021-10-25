package genepi.r2web.web.util.functions;

import java.util.Map;
import java.util.function.BiFunction;

import genepi.r2web.web.util.AbstractHandler;
import genepi.r2web.web.util.AbstractWebApp;
import genepi.r2web.web.util.RouteUtil;

public class RouteFunction implements BiFunction<String, Map<String, Object>, String> {

	private AbstractWebApp server;

	public RouteFunction(AbstractWebApp server) {
		this.server = server;
	}

	@Override
	public String apply(String route, Map<String, Object> params) {

		AbstractHandler handler = server.getHandlerByName(route);
		if (handler == null) {
			// TODO: throw TemplateEngine Exception
			return "Route '" + route + "' not found.";
		}

		return RouteUtil.path(handler.getPath(), params);
	}

}