package botrix.commons.persistence.cache;

import java.sql.SQLException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import systems.postgresql.Helper;
import systems.postgresql._Cache;

public class KeyValueCache {
	private Helper db = new Helper();

	public JsonElement read(String key) throws SQLException {
		var res = db.list("select * from cache where key = '%s'".formatted(key), _Cache.class);
		if (res.size() == 0) {
			return null;
		}
		var val = res.get(0).getValue();
		return JsonParser.parseString(val);
	}

	public void write(String key, Object value) throws Exception {
		_Cache c = new _Cache();
		c.setKey(key);
		c.setValue(new ObjectMapper().writeValueAsString(value));
		db.merge(c);
	}
}
