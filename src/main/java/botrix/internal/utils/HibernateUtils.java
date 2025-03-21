package botrix.internal.utils;

import javax.annotation.Nullable;

import org.hibernate.Transaction;
import org.slf4j.Logger;

import botrix.internal.logging.LoggerFactory;
import lombok.experimental.UtilityClass;
import systems.postgresql.Helper;

@UtilityClass
public class HibernateUtils {

	// Hibernate Transaction Utils
	@UtilityClass
	public static class TransactionUtils {
		private static final Logger logger = LoggerFactory.getLogger(Helper.class);

		/**
		 * Try rollback - if not null and is active also suppresses exception if any,
		 * 
		 * @param t - Transaction to rollback
		 */
		public void rollbackSilent(@Nullable Transaction t) {
			try {
				if (t != null && t.isActive()) {
					t.rollback();
				}
			} catch (Throwable ignored) {
				logger.debug("", ignored);
			}
		}
	}
}
