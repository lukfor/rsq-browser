package genepi.r2browser.web.controllers;

import java.net.URI;
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
import genepi.r2browser.web.util.JobQueue;
import genepi.r2browser.web.util.Page;
import genepi.r2browser.web.util.PageRenderer;
import genepi.r2browser.web.util.RouteUtil;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Produces;
import jakarta.inject.Inject;

@Controller("/queries")
@Produces(MediaType.TEXT_HTML)
public class QueryController {

	@Inject
	protected App app;

	@Inject
	protected PageRenderer renderer;

	@Get("/{queryId}")
	public String getQuery(String queryId) throws Exception {

		String workspace = app.getConfiguration().getWorkspace();

		Query query = Query.findById(queryId, workspace);

		if (query == null) {
			throw new Exception("Query not found.");
		}

		String template = "web/queries/show." + query.getStatus().name().toLowerCase() + ".view.html";
		Page page = new Page();
		page.put("query", query);
		page.put("configuration", app.getConfiguration());

		return renderer.render(template, page);

	}
	
	@Post("/")
	public HttpResponse<String> submitQuery(String q) throws Exception{
		
		Configuration configuration = app.getConfiguration();
		
		Query query = _submitQuery(q);

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("query", query.getId());
		String path = RouteUtil.path(configuration.getBaseUrl() + "/{queryId}", params);
		
		return HttpResponse.redirect(new URI(path));
	}
	
	protected Query _submitQuery(String q) throws Exception {

		Configuration configuration = app.getConfiguration();
		String workspace = configuration.getWorkspace();
		JobQueue jobQueue = app.getJobQueue();
		
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
			query = Query.create(queryId, workspace, configuration.getDatasets(), configuration.getBins(), configuration.getBuild());
			query.setQuery(q);
			query.setConfiguration(configuration);
			
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
