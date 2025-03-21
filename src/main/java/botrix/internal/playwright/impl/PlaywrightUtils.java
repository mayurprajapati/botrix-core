package botrix.internal.playwright.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.spi.json.GsonJsonProvider;
import com.microsoft.playwright.Playwright;

public class PlaywrightUtils {
	public static Gson gson;
//	public static ParseContext jsonPath;

	static {
		gson = new GsonBuilder().create();

		Configuration conf = Configuration.builder().jsonProvider(new GsonJsonProvider())
				.options(Option.SUPPRESS_EXCEPTIONS).build();

//		jsonPath = JsonPath.using(conf);
	}

	private PlaywrightUtils() {
	}

	public static Page playwright(com.microsoft.playwright.Page page) {
		return new Page(page);
	}

	public static Playwright playwright() {
		return playwrightCreateOptions().fulfill();
	}

	public static PlaywrightCreateOptions playwrightCreateOptions() {
		return new PlaywrightCreateOptions();
	}
}
