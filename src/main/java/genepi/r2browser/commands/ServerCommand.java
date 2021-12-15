package genepi.r2browser.commands;

import genepi.r2browser.App;
import genepi.r2browser.web.WebApp;
import picocli.CommandLine.Command;

@Command
public class ServerCommand extends AbstractCommand {

	@Override
	public Integer call() {

		App app = App.getDefault();
		int port = app.getConfiguration().getPort();

		WebApp server = new WebApp(port);
		server.start();

		return 0;

	}

}
