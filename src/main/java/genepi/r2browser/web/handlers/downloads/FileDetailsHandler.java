package genepi.r2browser.web.handlers.downloads;

import java.io.File;
import java.io.FileInputStream;

import genepi.r2browser.App;
import genepi.r2browser.config.Configuration;
import genepi.r2browser.model.AdditionalDownload;
import genepi.r2browser.web.util.AbstractHandler;
import io.javalin.http.Context;
import io.javalin.http.HandlerType;

public class FileDetailsHandler extends AbstractHandler {

	public static final String PATH = "/files/{index}/{filename}";

	public static final HandlerType TYPE = HandlerType.GET;

	private Configuration configuration = App.getDefault().getConfiguration();

	public void handle(Context context) throws Exception {

		String index = context.pathParam("index");
		AdditionalDownload download = configuration.getFiles().get(Integer.parseInt(index));
		
		File file =  new File(download.getFilename());
		context.result(new FileInputStream(file));

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
