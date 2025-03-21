package botrix.internal.gson.gsonpath;

import java.io.File;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.ParseContext;
import com.jayway.jsonpath.TypeRef;
import com.jayway.jsonpath.spi.json.GsonJsonProvider;
import com.jayway.jsonpath.spi.mapper.GsonMappingProvider;

import botrix.internal.gson.GsonUtils;

public class GsonPath {
	private Gson gson;
	private DocumentContext jsonPath;

	public GsonPath(Object document) {
		this(document, GsonUtils.gson);
	}

	public GsonPath(Object document, Gson gson) {
		this.gson = gson;

		Configuration conf = Configuration.builder()//
				.mappingProvider(new GsonMappingProvider(this.gson))//
				.jsonProvider(new GsonJsonProvider(this.gson))//
				.options(Option.SUPPRESS_EXCEPTIONS).build();

		ParseContext context = JsonPath.using(conf);

		try {
			if (document instanceof String) {
				this.jsonPath = context.parse((String) document);
			} else if (document instanceof File) {
				this.jsonPath = context.parse((File) document);
			} else if (document instanceof InputStream) {
				this.jsonPath = context.parse((InputStream) document, "UTF-8");
			} else {
				this.jsonPath = context.parse(document);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public String readString(String path) {
		JsonElement el = readJsonElement(path);
		if (el == null || el.isJsonNull())
			return null;

		if (el.isJsonPrimitive())
			return el.getAsString();

		if (el.isJsonArray() && el.getAsJsonArray().size() != 0)
			return el.getAsJsonArray().get(0).isJsonNull() ? null : el.getAsJsonArray().get(0).getAsString();
		return null;
	}

	public Number readNumber(String path) {
		return readJsonPrimitive(path).getAsNumber();
	}

	public BigDecimal readBidDecimal(String path) {
		return readJsonPrimitive(path).getAsBigDecimal();
	}

	public Boolean readBoolean(String path) {
		return readJsonPrimitive(path).getAsBoolean();
	}

	public Byte readByte(String path) {
		return readJsonPrimitive(path).getAsByte();
	}

	public Character readCharacter(String path) {
		return readJsonPrimitive(path).getAsCharacter();
	}

	public Short readShort(String path) {
		return readJsonPrimitive(path).getAsShort();
	}

	public BigInteger readBidInteger(String path) {
		return readJsonPrimitive(path).getAsBigInteger();
	}

	public Integer readInt(String path) {
		return readJsonPrimitive(path).getAsInt();
	}

	public Long readLong(String path) {
		return readJsonPrimitive(path).getAsLong();
	}

	public Float readFloat(String path) {
		return readJsonPrimitive(path).getAsFloat();
	}

	public Double readDouble(String path) {
		return readJsonPrimitive(path).getAsDouble();
	}

	public JsonObject readJsonObject(String path) {
		return read(path, JsonObject.class);
	}

	public JsonElement readJsonElement(String path) {
		return read(path, JsonElement.class);
	}

	public JsonArray readJsonArray(String path) {
		return read(path, JsonArray.class);
	}

	public JsonPrimitive readJsonPrimitive(String path) {
		// try getting array
		try {
			return read(path, JsonArray.class).get(0).getAsJsonPrimitive();
		} catch (Exception e) {
			return read(path, JsonPrimitive.class);
		}
	}

	public List<JsonObject> readListOfJsonObject(String path) {
		return read(path, new TypeRef<List<JsonObject>>() {
		});
	}

	public List<String> readListOfString(String path) {
		return read(path, new TypeRef<List<String>>() {
		});
	}

	public <T> T read(String path, Class<T> klass) {
		return jsonPath.read(path, klass);
	}

	public <T> T read(String path) {
		return jsonPath.read(path);
	}

	public <T> T read(String path, TypeRef<T> typeRef) {
		return jsonPath.read(path, typeRef);
	}

	public static void main(String[] args) {
		GsonPath path = new GsonPath("{\n" + "        \"title\": null,\n" + "        \"name\": \"main_window\",\n"
				+ "        \"width\": 500,\n" + "        \"height\": 500\n" + "    }");
		System.out.println(path.readString("title"));
	}
}
