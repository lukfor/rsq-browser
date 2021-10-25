package genepi.r2web.web.util;

import io.javalin.http.Handler;
import io.javalin.http.HandlerType;

public abstract class AbstractHandler implements Handler {

	public abstract HandlerType getType();
	
	public abstract String getPath();	
	
}
