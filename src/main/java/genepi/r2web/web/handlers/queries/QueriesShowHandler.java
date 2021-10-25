package genepi.r2web.web.handlers.queries;

import java.io.File;
import java.io.FileReader;

import com.google.gson.Gson;

import genepi.io.FileUtil;
import genepi.r2web.App;
import genepi.r2web.config.Configuration;
import genepi.r2web.model.Query;
import genepi.r2web.web.util.AbstractHandler;
import genepi.r2web.web.util.Page;
import io.javalin.http.Context;
import io.javalin.http.HandlerType;

public class QueriesShowHandler extends AbstractHandler {

	public static final String PATH = "/queries/{query}";

	public static final HandlerType TYPE = HandlerType.GET;

	private Configuration configuration = App.getDefault().getConfiguration();

	private String workspace = configuration.getWorkspace();

	public void handle(Context context) throws Exception {

		String queryId = context.pathParam("query");

		String filename = FileUtil.path(workspace, queryId + ".json");
		File jobFile = new File(filename);

		if (jobFile.exists()) {

			Gson gson = new Gson();

			Query query = gson.fromJson(new FileReader(jobFile), Query.class);

			String template = "web/queries/show." + query.getStatus().name().toLowerCase() + ".view.html";

			Page page = new Page(context, template);
			page.put("query", query);
			page.render();

		} else {
			throw new Exception("Job not found.");
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
