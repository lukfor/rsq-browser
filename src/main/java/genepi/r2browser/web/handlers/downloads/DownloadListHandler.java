package genepi.r2browser.web.handlers.downloads;

import genepi.r2browser.App;
import genepi.r2browser.config.Configuration;
import genepi.r2browser.web.util.AbstractHandler;
import genepi.r2browser.web.util.Page;
import io.javalin.http.Context;
import io.javalin.http.HandlerType;

public class DownloadListHandler extends AbstractHandler {

	public static final String PATH = "/downloads";

	public static final HandlerType TYPE = HandlerType.GET;

	public static final String TEMPLATE = "web/downloads/index.view.html";

	private Configuration configuration = App.getDefault().getConfiguration();

	public void handle(Context context) throws Exception {
		Page page = new Page(context, TEMPLATE);
		page.put("downloads", configuration.getDownloads());
		page.put("files", configuration.getFiles());
		page.render();

	}

	@Override
	public String getPath() {
		return configuration.getBaseUrl() + PATH;
	}

	@Override
	public HandlerType getType() {
		return TYPE;
	}

}
