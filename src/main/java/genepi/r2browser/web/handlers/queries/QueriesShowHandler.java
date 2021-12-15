package genepi.r2browser.web.handlers.queries;

import genepi.r2browser.App;
import genepi.r2browser.config.Configuration;
import genepi.r2browser.model.Query;
import genepi.r2browser.web.util.AbstractHandler;
import genepi.r2browser.web.util.Page;
import io.javalin.http.Context;
import io.javalin.http.HandlerType;

public class QueriesShowHandler extends AbstractHandler {

	public static final String PATH = "/queries/{query}";

	public static final HandlerType TYPE = HandlerType.GET;

	private Configuration configuration = App.getDefault().getConfiguration();

	private String workspace = configuration.getWorkspace();

	public void handle(Context context) throws Exception {

		String queryId = context.pathParam("query");

		Query query = Query.findById(queryId, workspace);

		if (query != null) {

			String template = "web/queries/show." + query.getStatus().name().toLowerCase() + ".view.html";

			Page page = new Page(context, template);
			page.put("query", query);
			page.put("configuration", configuration);
			page.render();

		} else {
			throw new Exception("Query not found.");
		}

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
