package rpa.core.audittrail.evaluator;

import static java.lang.String.format;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import rpa.core.exceptions.BishopAuditTrailEvaluatorException;

public class AuditTrailEvaluatorRegistry {
	private static final Map<String, AuditTrailEvaluator> cache = new HashMap<>();
	private static final Map<String, Class<? extends AuditTrailEvaluator>> registry = new HashMap<>();

	static {
		AuditTrailEvaluatorRegistry.register("default", DefaultAuditTrailEvaluator.class);
	}

	public static void register(String id, Class<? extends AuditTrailEvaluator> klass) {
		registry.put(id, klass);
	}

	public static void unregister(String id) {
		registry.remove(id);
		cache.remove(id);
	}

	public static AuditTrailEvaluator getAuditTrailEvaluator(String id) {
		if (!registry.containsKey(id))
			throw new BishopAuditTrailEvaluatorException(format("AuditTrailEvaluator not found with id '%s'.", id));

		if (!cache.containsKey(id)) {
			AuditTrailEvaluator evaluator;
			try {
				evaluator = (AuditTrailEvaluator) registry.get(id).getConstructors()[0].newInstance();
				cache.put(id, evaluator);
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException | SecurityException e) {
				throw new BishopAuditTrailEvaluatorException(
						format("Failed to create instance of AuditTrailEvaluator for id '%s'", id), e);
			}
		}

		return cache.get(id);
	}

	private AuditTrailEvaluatorRegistry() {
	}
}
