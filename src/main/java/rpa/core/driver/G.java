package rpa.core.driver;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.ParseContext;
import com.jayway.jsonpath.spi.json.GsonJsonProvider;
import com.jayway.jsonpath.spi.mapper.GsonMappingProvider;

import botrix.internal.gson.GsonUtils;
import botrix.internal.logging.LoggerFactory;
import io.restassured.RestAssured;
import io.restassured.config.ObjectMapperConfig;
import io.restassured.mapper.ObjectMapperType;
import rpa.core.entities._BotInfo;
import rpa.core.exceptions.BishopRuleViolationException;
import rpa.core.file.OSValidator;
import rpa.core.file.ParseUtils;
import rpa.core.file.PropertiesLoader;
import rpa.core.metrics.ExecutionMetrics;
import rpa.core.metrics.Helper;
import rpa.core.sikuli.Interaction;
import rpa.core.web.Button;
import rpa.core.web.Checkbox;
import rpa.core.web.Dropdown;
import rpa.core.web.Elements;
import rpa.core.web.InputField;
import rpa.core.web.Link;
import rpa.core.web.MouseActions;
import rpa.core.web.Screenshot;
import rpa.core.web.Wait;
import rpa.core.web.Window;

public class G {

	private static Logger LOGGER = LoggerFactory.getLogger(G.class);
	public static Button button = null;
	public static Dropdown dropdown = null;
	public static Elements elements = null;
	public static Checkbox checkbox = null;
	public static InputField inputfield = null;
	public static Link link = null;
	public static Screenshot screenshot = null;
	public static Wait wait = null;
	public static Window window = null;
	public static JavascriptExecutor jse = null;
	public static RemoteWebDriver driver = null;
	public static Process winAppProcess = null;
	public static SystemProperties sysProps = null;
	public static MouseActions mouseAction = null;
	public static ExecutionMetrics executionMetrics = null;
	private static Map<String, String> recordData = new HashMap<String, String>();
	public static Interaction interactions = null;
	public static boolean nonUI = true;
	public static boolean windowsApp = false;
	public static boolean webApp = false;
	public static boolean isEtl = false;
	public static boolean cloudRecords = false;
	public static _BotInfo currentRunningBot = null;
	private static final String BATCH_ID = Helper.createUUID();
	private static final String BATCH_DATE = ParseUtils.now("yyyy-MM-dd hh:mm:ss");
	private static String ProjectUniqueID = StringUtils.EMPTY;
	private static String ProjectUniqueName = StringUtils.EMPTY;

	public static ParseContext jsonPath;

	public static void setup() {
		new G();

		setupRestassured();
		setupJsonPath();
	}

	private static void setupJsonPath() {
		Configuration conf = Configuration.builder().jsonProvider(new GsonJsonProvider(GsonUtils.gson))
				.mappingProvider(new GsonMappingProvider(GsonUtils.gson)).options(Option.SUPPRESS_EXCEPTIONS).build();

		jsonPath = JsonPath.using(conf);
	}

	private static void setupRestassured() {
		ObjectMapperConfig gsonObjectMapper = new ObjectMapperConfig(ObjectMapperType.GSON)//
				.gsonObjectMapperFactory((type, s) -> {
					return GsonUtils.gson;
				});

		RestAssured.config = RestAssured.config()
//				.connectionConfig(ConnectionConfig.connectionConfig().closeIdleConnectionsAfterEachResponse())
				.objectMapperConfig(gsonObjectMapper);
	}

	private G() {
		PropertiesLoader p = new PropertiesLoader();
		p.loadProps();
//		sysProps = new SystemProperties();
		button = new Button();
		dropdown = new Dropdown();
		elements = new Elements();
		inputfield = new InputField();
		link = new Link();
		screenshot = new Screenshot();
		wait = new Wait();
		window = new Window();
		checkbox = new Checkbox();
		mouseAction = new MouseActions();
		interactions = new Interaction();
	}

	/**
	 * Method which should be called on completion of a transaction.<br>
	 * To clear out resources & values of that transaction
	 */
	public static void reset() {
		resetAutomateNotifications();
	}

	public static void resetAutomateNotifications() {
		// resetting original notifications
		// was causing a bug where emails of earlier transaction was not getting reset
		// ref: https://Bishop.atlassian.net/browse/IM-4599
//		if (executionMetrics.getOriginalAutomateNotifications() != null) {
//			executionMetrics.setAutomateNotifications(
//					new AutomateNotifications(executionMetrics.getOriginalAutomateNotifications()));
//		}
	}

	public static String getPlatform() throws BishopRuleViolationException {
		return OSValidator.getOS();
	}

	public static Map<String, String> getRecordData() {
		return recordData;
	}

	public static void setRecordData(Map<String, String> recordData) {
		G.recordData = recordData;
	}

	public static String getBatchId() {
		return BATCH_ID;
	}

	public static String getBatchDate() {
		return BATCH_DATE;
	}

	public static String getProjectUniqueID() {
		if (!StringUtils.isBlank(ProjectUniqueID))
			return ProjectUniqueID;
		else
			return StringUtils.trim(StringUtils.split(executionMetrics.getFlow().getProject(), " ")[0]);
	}

	public static void setProjectUniqueID(String projectUniqueID) {
		ProjectUniqueID = projectUniqueID;
	}

	public static String getProjectUniqueName() {
		if (!StringUtils.isBlank(ProjectUniqueName))
			return ProjectUniqueName;
		else
			return executionMetrics.getFlow().getProject();
	}

	public static void setProjectUniqueName(String projectUniqueName) {
		ProjectUniqueName = projectUniqueName;
	}
}
