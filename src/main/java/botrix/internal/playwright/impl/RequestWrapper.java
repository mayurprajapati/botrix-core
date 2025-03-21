package botrix.internal.playwright.impl;

import static botrix.internal.playwright.impl.PlaywrightUtils.gson;

import java.util.List;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.microsoft.playwright.Frame;
import com.microsoft.playwright.Request;
import com.microsoft.playwright.options.HttpHeader;
import com.microsoft.playwright.options.Sizes;
import com.microsoft.playwright.options.Timing;

import botrix.internal.gson.gsonpath.GsonPath;

public class RequestWrapper implements Request {
	private Request r;

	public RequestWrapper(Request request) {
		this.r = request;
	}

	@Override
	public Map<String, String> allHeaders() {
		return r.allHeaders();
	}

	@Override
	public String failure() {

		return r.failure();
	}

	@Override
	public Frame frame() {

		return r.frame();
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
	public boolean isNavigationRequest() {

		return r.isNavigationRequest();
	}

	@Override
	public String method() {

		return null;
	}

	@Override
	public String postData() {

		return r.postData();
	}

	@Override
	public byte[] postDataBuffer() {

		return r.postDataBuffer();
	}

	@Override
	public Request redirectedFrom() {

		return r.redirectedFrom();
	}

	@Override
	public Request redirectedTo() {

		return r.redirectedTo();
	}

	@Override
	public String resourceType() {

		return r.resourceType();
	}

	@Override
	public ResponseWrapper response() {

		return new ResponseWrapper(r.response());
	}

	public JsonElement bodyAsJsonElement() {
		return gson.fromJson(postData(), JsonElement.class);
	}

	public JsonElement bodyAsJsonObject() {
		return gson.fromJson(postData(), JsonObject.class);
	}

	public JsonElement bodyAsJsonArray() {
		return gson.fromJson(postData(), JsonArray.class);
	}

	public GsonPath gsonPath() {
		return new GsonPath(postData());
	}

	@Override
	public Sizes sizes() {

		return r.sizes();
	}

	@Override
	public Timing timing() {

		return r.timing();
	}

	@Override
	public String url() {

		return r.url();
	}

}
