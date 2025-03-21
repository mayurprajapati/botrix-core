package rpa.core.file;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;

public class PropertiesLoader {

	private static final Logger LOGGER = botrix.internal.logging.LoggerFactory.getLogger(PropertiesLoader.class);

	FileHandlingUtils fileHandlingUtils = new FileHandlingUtils();

	/**
	 * Loads all properties defined in a property file (SystemProperties.properties)
	 */
	public void loadProps() {
		try {
			LOGGER.info("Loading properties");
			InputStream projectPropIn = new FileInputStream(
					FileHandlingUtils.getListOfAllFiles(System.getProperty("user.dir"), "SystemProperties.properties")
							.iterator().next());
			System.getProperties().load(projectPropIn);
		} catch (IOException e) {
			LOGGER.error("IOException " + e);
		} catch (Exception e) {
			LOGGER.error("Exception " + e);

		}
	}

}
