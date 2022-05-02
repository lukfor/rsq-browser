package genepi.r2browser.web.controllers;

import genepi.r2browser.App;
import genepi.r2browser.web.util.Page;
import genepi.r2browser.web.util.PageRenderer;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Produces;
import jakarta.inject.Inject;

@Controller
@Produces(MediaType.TEXT_HTML)
public class PageController {

	@Inject
	protected App app;

	@Inject
	protected PageRenderer renderer;

	@Get("/")
	public String index() throws Exception {

		Page page = new Page();
		page.put("configuration", app.getConfiguration());
		return renderer.render("web/index.view.html", page);

	}

	@Get("/about")
	public String about() throws Exception {

		Page page = new Page();
		page.put("configuration", app.getConfiguration());
		return renderer.render("web/about.view.html", page);

		
	}

}
