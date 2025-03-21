package systems.postgresql;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.criteria.CriteriaQuery;

import org.apache.commons.lang3.function.FailableConsumer;
import org.apache.commons.lang3.function.FailableRunnable;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.query.Query;
import org.slf4j.Logger;

import botrix.internal.gson.GsonUtils;
import botrix.internal.logging.LoggerFactory;
import botrix.internal.utils.HibernateUtils;
import botrix.internal.utils.HibernateUtils.TransactionUtils;
import lombok.Synchronized;
import lombok.experimental.ExtensionMethod;

@ExtensionMethod({ HibernateUtils.class, TransactionUtils.class })
public class Helper implements AutoCloseable {
	private static final Logger logger = LoggerFactory.getLogger(Helper.class);
	private static ThreadLocal<Helper> sessions = new ThreadLocal<>();
	private static final Logger LOG = LoggerFactory.getLogger(Helper.class);

	private static SessionFactory factory;

	public Session session = null;

	static {
		StandardServiceRegistry ssr = new StandardServiceRegistryBuilder().configure("hibernate.cfg.xml").build();
		Metadata meta = new MetadataSources(ssr).getMetadataBuilder().build();

		factory = meta.getSessionFactoryBuilder().build();
	}

	public Helper() {
		session = factory.openSession();
//		Connection c = ((SessionImpl) session).connection();
//		PGConnection pg = (PGConnection) c;
//
//		while (true) {
//			try {
//				PGNotification[] n = pg.getNotifications();
//				if (n != null && n.length > 0) {
//					System.out.println();
//				}
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//			WaitUtils.sleep(Duration.ofSeconds(1));
//
//		}
	}

//	public static void main(String[] args) {
//		try (Helper h = new Helper()) {
//
//		}
//	}

	@Synchronized("session")
	public <T> CriteriaQuery<T> createCriteriaQuery(Class<T> klass) {
		return session.getCriteriaBuilder().createQuery(klass);
//		return session.createQuery(q);
//		return session.createNativeQuery(query, _StockQuoteIndice.class).list();
	}

	@Synchronized("session")
	public <T> T find(Class<T> klass, Object id) {
		return session.find(klass, id);
	}

	@Synchronized("session")
	public void withTransaction(FailableConsumer<Transaction, Throwable> code) {
		Transaction t = null;
		boolean existingTransaction = session.getTransaction() != null && session.getTransaction().isActive();
		try {
			if (!existingTransaction)
				t = session.beginTransaction();

			code.accept(session.getTransaction());

			if (!existingTransaction)
				t.commit();
			session.flush();
			session.clear();
		} catch (Throwable e) {
			if (!existingTransaction)
				t.rollbackSilent();
			if (!e.getMessage().contains("no transaction is in progress"))
				throw new RuntimeException(e);
		}
	}

	@Synchronized("session")
	public <T> Query<T> createNativeQuery(String query, Class<T> klass) {
		return session.createNativeQuery(query, klass);
	}

	@Synchronized("session")
	public <T> List<T> list(String query, Class<T> klass) {
		return createNativeQuery(query, klass).list();
	}

	public <T> List<T> readAsListOfPojo(String query, Class<T> klass) {
		return readAsListOfMap(query).stream().map(el -> GsonUtils.fromObjectToPojo(el, klass)).toList();
	}

	@Synchronized("session")
	public List<Map<String, Object>> readAsListOfMap(String query) {
		final List<Map<String, Object>> result = new ArrayList<>();
		LOG.info("Fetching {}", query.replace("\n", " "));

		session.doWork((connection) -> {
			try (Statement stmt = connection.createStatement()) {
				ResultSet rs = stmt.executeQuery(query);
				ResultSetMetaData metaData = rs.getMetaData();
				int columnCount = metaData.getColumnCount();
				String[] columnNames = new String[columnCount];
				for (int i = 1; i <= columnCount; i++) {
					columnNames[i - 1] = metaData.getColumnLabel(i);
				}
				while (rs.next()) {
					Map<String, Object> row = new LinkedHashMap<>();
					for (int i = 0; i < columnCount; i++) {
						row.put(columnNames[i], rs.getObject(i + 1));
					}
					result.add(row);
				}
			}
		});

		LOG.info("Fetched {} records", result.size());

		return result;
	}

	@Override
	@Synchronized("session")
	public void close() {
		session.close();
	}

	@Synchronized("session")
	public void save(Object object) {
		withTransaction((t) -> {
			session.saveOrUpdate(object);
		});
	}

	@Synchronized("session")
	public void update(Object object) {
		withTransaction((t) -> {
			session.merge(object);
		});
	}

	@Synchronized("session")
	public <T> void mergeAll(List<T> list) {
		try {
			if (list == null || list.isEmpty())
				return;

			LOG.info("Saving {} values", list.size());

			withTransaction((t) -> {
				int c = 0;

				for (T obj : list) {
					session.merge(obj);

					if (c++ % 450 == 0) {
						session.flush();
						session.clear();
					}
				}

				session.flush();
				session.clear();
			});

			LOG.info("Saved {} values", list.size());
		} catch (Exception e) {
			throw e;
		}
	}

	@Synchronized("session")
	public void remove(Object object) {
		withTransaction((t) -> {
			session.remove(object);
		});
	}

	public void merge(Object obj) {
		withTransaction((t) -> {
			session.merge(obj);
		});
	}

	public static <T extends Throwable> void withSessionFactory(FailableRunnable<T> code) throws T {
		try {
			code.run();
		} catch (Throwable e) {
			e.printStackTrace();
			throw e;
		} finally {
			factory.close();
		}
	}

	public static void closeSessionFactory() {
		factory.close();
	}
}
