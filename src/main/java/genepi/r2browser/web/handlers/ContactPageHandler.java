package genepi.r2browser.web.handlers;

import genepi.r2browser.web.util.AbstractHandler;
import genepi.r2browser.web.util.Page;
import io.javalin.http.Context;
import io.javalin.http.HandlerType;

public class ContactPageHandler extends AbstractHandler {

	public static final String PATH = "/contact";

	public static final HandlerType TYPE = HandlerType.GET;

	public static final String TEMPLATE = "web/contact.view.html";

	public void handle(Context context) throws Exception {

		Page page = new Page(context, TEMPLATE);
		page.render();

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
