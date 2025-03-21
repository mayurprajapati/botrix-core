package systems.postgresql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.slf4j.Logger;

import botrix.internal.logging.LoggerFactory;
import rpa.core.exceptions.BishopRuntimeException;

public class PostgreSQL<T> implements AutoCloseable {
	private static final Logger LOGGER = LoggerFactory.getLogger(PostgreSQL.class);

	private Connection connection;
	private QueryRunner runner;
	private ResultSetHandler<List<T>> rsHandler;

	public static <T> PostgreSQL<T> getInstance(String url, String username, String password,
			Class<? extends T> klass) {
		try {
			Connection c = DriverManager.getConnection(url, username, password);
			return getInstance(c, klass);
		} catch (Exception e) {
			throw new BishopRuntimeException("Failed create SQL connection", e);
		}
	}

	public static <T> PostgreSQL<T> getInstance(Connection connection, Class<? extends T> klass) {
		return new PostgreSQL<T>(connection, klass);
	}

	private PostgreSQL(Connection connection, Class<? extends T> klass) {
		this.connection = connection;
		runner = new QueryRunner();
		rsHandler = new BeanListHandler<>(klass);
	}

	public List<T> read(String query, Object... params) {
		return query(query, params);
	}

	public int createTable(String query) {
		return update(query);
	}
	
	public List<T> insert(String query, Object... params) {
		try {
			return runner.insert(connection, query, rsHandler, params);
		} catch (Exception e) {
			throw new BishopRuntimeException("Failed to execute fetch query", e);
		}
	}

	private List<T> query(String query, Object... params) {
		try {
			return runner.query(connection, query, rsHandler, params);
		} catch (Exception e) {
			throw new BishopRuntimeException("Failed to execute fetch query", e);
		}
	}

	private int update(String query) {
		try {
			return runner.update(connection, query);
		} catch (Exception e) {
			throw new BishopRuntimeException("Failed to execute update query", e);
		}
	}

	@Override
	public void close() {
		DbUtils.closeQuietly(connection);
	}
}
