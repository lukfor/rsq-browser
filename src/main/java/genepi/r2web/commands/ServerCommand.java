package genepi.r2web.commands;

import genepi.r2web.App;
import genepi.r2web.web.WebApp;
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
