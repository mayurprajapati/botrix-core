package rpa.core.plugins;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.slf4j.Logger;

import botrix.internal.logging.LoggerFactory;
import rpa.core.annotations.BishopPlugin;
import rpa.core.annotations.BishopPluginLibrary;
import rpa.core.exceptions.BishopException;
import rpa.core.exceptions.BishopPluginNotFoundException;
import rpa.core.exceptions.BishopUnknownException;
import rpa.core.file.ParseUtils;

public class BishopPluginInvoker {
	private static final Logger LOGGER = LoggerFactory.getLogger(BishopPluginInvoker.class);

	private static final String METHOD_AND_PARAMS_REGEX = "\\$\\{[^\\$\\{]+?\\)((?<!\\\\)})";

	protected BishopPluginInvoker() {
	}

	/**
	 * Plugins are methods defined in {@see rpa.core.plugins.BishopPlugins} class <br>
	 * Use it like below
	 * 
	 * e.g.
	 * 
	 * <pre>
	 * "WHERE inTime BETWEEN '${getDateForCurrentWeekWeekday(day=Monday,format=yyyy-MM-dd)} 00:00:00' AND '${getDateForCurrentWeekWeekday(day=Sunday,format=yyyy-MM-dd)} 00:00:00' AND Activity.activityCode <> ‘LLUN’"
	 * </pre>
	 * 
	 * Syntax:
	 * 
	 * <pre>
	 *  ${methodNameSameAsInPluginsClass(param1=Value1,param2=Value2)}
	 * </pre>
	 * 
	 * @param payload
	 * @return updated payload
	 * @throws Exception
	 */
	public static String execute(String payload) throws BishopException {
		return execute(payload, null);
	}

	public static String execute(String payload, Object additionalParams) throws BishopException {
		while (hasPluginMethod(payload)) {
			try {
				LOGGER.info("activating plugin...");
				LOGGER.info("Payload before plugin\n{}", payload);
				String methodAndParams = getMethodAndParams(payload);
				String methodCall = getMethodCall(methodAndParams);
				Map<String, String> args = getMethodArgs(methodAndParams);

				Class<?> klass = getPluginClass(methodCall);
				Method method = getPluginMethod(klass, methodCall, args, additionalParams);
				LOGGER.info("running plugin '{}'", methodAndParams);

				String result = "";

				if (method.getParameterCount() == 0)
					result = (String) method.invoke(null);
				else if (method.getParameterCount() == 1)
					result = (String) method.invoke(null, args);
				else
					result = (String) method.invoke(null, args, additionalParams);

				LOGGER.info("result for plugin call '{}' is '{}'", methodAndParams, result);
				payload = payload.replaceFirst(METHOD_AND_PARAMS_REGEX, result);
				LOGGER.info("Payload after plugin\n{}", payload);
			} catch (BishopException e) {
				throw e;
			} catch (Exception e) {
				throw new BishopUnknownException("Failed to execute Bishop Plugin.", e);
			}
		}

		return payload;
	}

	protected static final Method getPluginMethod(Class<?> klass, String methodCall, Map<String, String> args,
			Object additionalParams) throws BishopPluginNotFoundException {
		Method m = null;
		if (methodCall.contains("."))
			methodCall = ParseUtils.evalRegexAndReturn(".*\\.(.*)", methodCall, 1);
		if (MapUtils.isEmpty(args))
			m = MethodUtils.getMatchingAccessibleMethod(klass, methodCall);
		else if (additionalParams == null)
			m = MethodUtils.getMatchingAccessibleMethod(klass, methodCall, Map.class);
		else
			m = MethodUtils.getMatchingAccessibleMethod(klass, methodCall, Map.class, Object.class);

		// make additionalParams optional
		if (m == null)
			m = MethodUtils.getMatchingAccessibleMethod(klass, methodCall, Map.class);

		Objects.requireNonNull(m, "Plugin method not found with argument of type 'Map.class'");
		validateBishopPluginAnnotationPresent(m);
		return m;
	}

	protected static Class<?> getPluginClass(String methodCall) throws ClassNotFoundException {
		if (methodCall.contains(".")) {
			return Class.forName(ParseUtils.evalRegexAndReturn("(.*)\\.", methodCall, 1));
		}
		// default
		return BishopPlugins.class;
	}

	protected static final void validateBishopPluginAnnotationPresent(Method method) throws BishopPluginNotFoundException {
		Class<?> klass = method.getDeclaringClass();
		if (!(method.isAnnotationPresent(BishopPlugin.class) || klass.isAnnotationPresent(BishopPluginLibrary.class))) {
			String msg = "Plugin method '" + method.getDeclaringClass().getName() + "." + method.getName()
					+ "' should be annotated with '@BishopPlugin'";
			msg += " OR class '" + klass.getName() + "' should be annotated with '@BishopPluginLibrary'";
			throw new BishopPluginNotFoundException(msg);
		}
	}

	protected static final boolean hasPluginMethod(String payload) {
		return getMethodAndParams(payload).length() != 0;
	}

	protected static final String getMethodAndParams(String payload) {
		String methodAndParams = ParseUtils.evalRegexAndReturnFirstMatch(METHOD_AND_PARAMS_REGEX, payload, 0);
		// replace escaped
		return methodAndParams.replace("\\}", "}");
	}

	protected static final HashMap<String, String> getMethodArgs(String methodAndParams) {
		String params = ParseUtils.evalRegexAndReturn("\\$\\{.*\\((.*)\\)\\}", methodAndParams, 1);
		HashMap<String, String> map = new HashMap<>();

		if (StringUtils.isBlank(params)) {
			return map;
		}

		for (String keyValue : params.split("((?<!\\\\),)")) {
			String key = keyValue.split("=", 2)[0];
			// replace escaped
			String value = keyValue.split("=", 2)[1].replace("\\,", ",");
			map.put(key.trim(), value.trim());
		}
		return map;
	}

	protected static final String getMethodCall(String methodAndParams) {
		return ParseUtils.evalRegexAndReturn("\\$\\{(.*)\\(", methodAndParams, 1);
	}
}
