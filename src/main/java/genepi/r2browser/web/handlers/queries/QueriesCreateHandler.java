package genepi.r2browser.web.handlers.queries;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.DatatypeConverter;

import org.apache.commons.lang3.RandomStringUtils;

import genepi.io.FileUtil;
import genepi.r2browser.App;
import genepi.r2browser.config.Configuration;
import genepi.r2browser.model.Query;
import genepi.r2browser.web.util.AbstractHandler;
import genepi.r2browser.web.util.JobQueue;
import genepi.r2browser.web.util.RouteUtil;
import io.javalin.http.Context;
import io.javalin.http.HandlerType;

public class QueriesCreateHandler extends AbstractHandler {

	public static final String PATH = "/queries";

	public static final HandlerType TYPE = HandlerType.POST;

	private Configuration configuration = App.getDefault().getConfiguration();

	private JobQueue jobQueue = App.getDefault().getJobQueue();

	private String workspace = configuration.getWorkspace();

	public void handle(Context context) throws Exception {

		String q = context.formParam("q");
		Query query = submitQuery(q);

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("query", query.getId());
		String path = RouteUtil.path(configuration.getBaseUrl() + QueriesShowHandler.PATH, params);

		context.redirect(path);

	}

	@Override
	public String getPath() {
		return configuration.getBaseUrl() + PATH;
	}

	@Override
	public HandlerType getType() {
		return TYPE;
	}

	protected Query submitQuery(String q) throws Exception {

		String queryId = null;
		if (configuration.isCaching()) {
			// caching: queryId is hash of query string
			queryId = hash(q);
		} else {
			// no caching: queryId is always random
			queryId = hash(RandomStringUtils.randomAlphanumeric(configuration.getJobIdLength()));
		}

		Query query = Query.findById(queryId, workspace);

		if (query == null) {

			FileUtil.createDirectory(workspace);
			query = Query.create(queryId, workspace, configuration.getDatasets(), configuration.getBins());
			query.setQuery(q);

			jobQueue.submit(query);

		}

		return query;
	}

	protected String hash(String hash) throws NoSuchAlgorithmException {

		MessageDigest md = MessageDigest.getInstance("MD5");
		md.update(hash.getBytes());
		byte[] digest = md.digest();
		return DatatypeConverter.printHexBinary(digest).toLowerCase();

	}

}
