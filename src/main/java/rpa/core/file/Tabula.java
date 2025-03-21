package rpa.core.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import botrix.internal.logging.LoggerFactory;

/**
 * @author Bishop This class converts pdf tables to csvs
 *         https://github.com/tabulapdf/tabula
 */
public class Tabula {
	private static final Logger LOGGER = LoggerFactory.getLogger(Tabula.class);

	public static String tabulaDir = System.getProperty("user.dir") + File.separator + "src" + File.separator + "main"
			+ File.separator + "resources" + File.separator + "Tabula";

	public static String tabulaJarPath = tabulaDir + File.separator + "tabula-1.0.3-jar-with-dependencies.jar";

	/**
	 * This method is used to convert pdf tables to csv pass coordinates as
	 * "10,20,40,.." if we are sure of the coordinates else let tabula guess the pdf
	 * table if there are no table lines
	 * 
	 * @param page
	 * @param tempDir
	 * @param filePath
	 * @return path of csv
	 * @throws Exception
	 */
	public static String generateCSV(String page, String tempDir, String filePath, String coordinates)
			throws Exception {

		try {
			Process proc = null;
			if (StringUtils.isEmpty(coordinates)) {
				proc = Runtime.getRuntime().exec("java -jar \"" + tabulaJarPath + "\" -b \"" + tempDir
						+ "\" -g -t -o \"" + tempDir + "\" -p " + page);
			} else {
				proc = Runtime.getRuntime().exec("java -jar \"" + tabulaJarPath + "\" -b \"" + tempDir + "\" -c "
						+ coordinates + " -t -o \"" + tempDir + "\" -p " + page);
			}
			proc.waitFor();
			String s = "";
			BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
			BufferedReader stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
			StringBuilder insBuilder = new StringBuilder();
			StringBuilder errsBuilder = new StringBuilder();
			while ((s = stdInput.readLine()) != null) {
				insBuilder.append(s).append(System.lineSeparator());
			}
			while ((s = stdError.readLine()) != null) {
				errsBuilder.append(s).append(System.lineSeparator());
			}
			if (StringUtils.isNotBlank(errsBuilder)) {
				LOGGER.error("Error for the command : " + errsBuilder);
			}
			String streamOutput = insBuilder.toString() + System.lineSeparator() + errsBuilder.toString();
			LOGGER.info("Stream output >> " + streamOutput);

			String fileName = FilenameUtils.getBaseName(filePath);
			return FilenameUtils.getFullPath(filePath) + File.separator + fileName + ".csv";

		} catch (Exception e) {
			LOGGER.error("Error in capturing pdf Table for pageNo : " + page + " from Tabula", e);
			throw new Exception("Error in capturing pdf Table for pageNo : " + page + " from Tabula", e);
		}
	}

	public static String getPagesfromPageNo(int pageStart, int pageEnd) throws Exception {
		String pages = StringUtils.EMPTY;
		try {
			for (int i = pageStart; i <= pageEnd; i++) {
				pages = String.valueOf(i);
				if (i != pageEnd)
					pages = pages + ", ";
			}
			return pages;
		} catch (Exception e) {
			LOGGER.error("Error in appending page numbers", e);
			throw new Exception("Error in appending page numbers", e);
		}
	}
}
