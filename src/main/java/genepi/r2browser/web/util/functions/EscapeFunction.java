package genepi.r2browser.web.util.functions;
import java.text.DecimalFormat;
import java.util.function.Function;

import org.apache.commons.lang3.StringEscapeUtils;

public class EscapeFunction implements Function<Object, String> {

	public static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("###,###,###");

	@Override
	public String apply(Object text) {
		if (text == null) {
			return null;
		}
		return StringEscapeUtils.escapeHtml4(text.toString()); 

	}

}