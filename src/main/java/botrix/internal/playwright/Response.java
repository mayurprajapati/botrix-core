package botrix.internal.playwright;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import botrix.internal.gson.gsonpath.GsonPath;

public interface Response extends com.microsoft.playwright.Response {
	public JsonElement bodyAsJsonElement();

	public JsonObject bodyAsJsonObject();

	public JsonArray bodyAsJsonArray();

	public String bodyAsString();

	public GsonPath gsonPath();
}
