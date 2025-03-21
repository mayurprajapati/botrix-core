package botrix.internal.playwright.impl;

import static botrix.internal.playwright.impl.PlaywrightUtils.gson;

import java.util.List;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.jayway.jsonpath.DocumentContext;
import com.microsoft.playwright.Frame;
import com.microsoft.playwright.options.HttpHeader;
import com.microsoft.playwright.options.SecurityDetails;
import com.microsoft.playwright.options.ServerAddr;

import botrix.internal.gson.gsonpath.GsonPath;
import botrix.internal.playwright.Response;

public class ResponseWrapper implements Response {
	private com.microsoft.playwright.Response r;

	public ResponseWrapper(com.microsoft.playwright.Response response) {
		this.r = response;
	}

	@Override
	public Map<String, String> allHeaders() {
		return r.allHeaders();
	}

	@Override
	public byte[] body() {
		return r.body();
	}

	@Override
	public JsonElement bodyAsJsonElement() {
		return gson.fromJson(text(), JsonElement.class);
	}

	@Override
	public JsonObject bodyAsJsonObject() {
		return gson.fromJson(text(), JsonObject.class);
	}

	@Override
	public JsonArray bodyAsJsonArray() {
		return gson.fromJson(text(), JsonArray.class);
	}

	@Override
	public String finished() {
		return r.finished();
	}

	@Override
	public Frame frame() {
		return r.frame();
	}

	@Override
	public boolean fromServiceWorker() {
		return r.fromServiceWorker();
	}

	@Override
	public Map<String, String> headers() {
		return r.headers();
	}

	@Override
	public List<HttpHeader> headersArray() {
		return r.headersArray();
	}

	@Override
	public String headerValue(String name) {
		return r.headerValue(name);
	}

	@Override
	public List<String> headerValues(String name) {
		return r.headerValues(name);
	}

	@Override
	public boolean ok() {
		return r.ok();
	}

	@Override
	public RequestWrapper request() {
		return new RequestWrapper(r.request());
	}

	@Override
	public SecurityDetails securityDetails() {
		return r.securityDetails();
	}

	@Override
	public ServerAddr serverAddr() {
		return r.serverAddr();
	}

	@Override
	public int status() {
		return r.status();
	}

	@Override
	public String statusText() {
		return r.statusText();
	}

	@Override
	public String text() {
		return r.text();
	}

	@Override
	public String url() {
		return r.url();
	}

	@Override
	public String bodyAsString() {
		return r.text();
	}

	@Override
	public GsonPath gsonPath() {
		return new GsonPath(text());
	}
}
