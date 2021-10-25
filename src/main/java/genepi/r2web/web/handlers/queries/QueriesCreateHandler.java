package genepi.r2web.web.handlers.queries;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.RandomStringUtils;

import genepi.io.FileUtil;
import genepi.r2web.App;
import genepi.r2web.config.Configuration;
import genepi.r2web.model.Query;
import genepi.r2web.web.util.AbstractHandler;
import genepi.r2web.web.util.JobQueue;
import genepi.r2web.web.util.RouteUtil;
import io.javalin.http.Context;
import io.javalin.http.HandlerType;

public class QueriesCreateHandler extends AbstractHandler {

	public static final String PATH = "/queries";

	public static final HandlerType TYPE = HandlerType.POST;

	private Configuration configuration = App.getDefault().getConfiguration();

	private JobQueue jobQueue = App.getDefault().getJobQueue();

	private String workspace = configuration.getWorkspace();

	public void handle(Context context) throws Exception {

		if (!context.isMultipartFormData()) {
			throw new Exception("Uploaded data is not multipart form data.");
		}

		String q = context.formParam("q");

		String queryId = RandomStringUtils.randomAlphanumeric(configuration.getJobIdLength());

		FileUtil.createDirectory(workspace);

		Query query = Query.create(queryId, workspace, configuration.getDatasets().get(0), configuration.getBins());
		query.setQuery(q);

		jobQueue.submit(query);

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("query", query.getId());
		String path = RouteUtil.path(QueriesShowHandler.PATH, params);

		context.redirect(path);

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
