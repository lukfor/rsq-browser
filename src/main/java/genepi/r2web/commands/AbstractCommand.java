package genepi.r2web.commands;

import java.util.concurrent.Callable;

import genepi.r2web.App;

public abstract class AbstractCommand implements Callable<Integer> {

	public AbstractCommand() {
		App app = App.getDefault();
		app.loadConfiguration(App.CONFIG_FILENAME);
	}

}
