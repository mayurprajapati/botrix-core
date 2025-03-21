package rpa.core.excel;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.output.FileWriterWithEncoding;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import com.github.opendevl.JFlat;
import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.univocity.parsers.csv.CsvFormat;
import com.univocity.parsers.csv.CsvParserSettings;

import botrix.internal.logging.LoggerFactory;
import rpa.core.exceptions.BishopRuleViolationException;
import rpa.core.file.FileHandlingUtils;

public class CSVReader {
	public static char separator = CSVWriter.DEFAULT_SEPARATOR;
	public static char quotechar = CSVWriter.DEFAULT_QUOTE_CHARACTER;
	public static char escapechar = CSVWriter.DEFAULT_ESCAPE_CHARACTER;
	public static String lineEnd = System.lineSeparator();
	public static boolean overwrite = false;
	private static Logger LOGGER = LoggerFactory.getLogger(CSVReader.class);

	public CSVReader() {

	}

	public CSVReader(char separator, char quotechar, char escapechar, String lineEnd) {
		this.separator = separator;
		this.quotechar = quotechar;
		this.escapechar = escapechar;
		this.lineEnd = lineEnd;
		this.overwrite = true;
	}

	public char getSeparator() {
		return separator;
	}

	public void setSeparator(char separator) {
		this.separator = separator;
	}

	public char getQuotechar() {
		return quotechar;
	}

	public void setQuotechar(char quotechar) {
		this.quotechar = quotechar;
	}

	public char getEscapechar() {
		return escapechar;
	}

	public void setEscapechar(char escapechar) {
		this.escapechar = escapechar;
	}

	public String getLineEnd() {
		return lineEnd;
	}

	public void setLineEnd(String lineEnd) {
		this.lineEnd = lineEnd;
	}

	public static List<String[]> read(String fileName) throws IOException {
		return read(fileName, false);
	}

	public static List<String[]> readAll(String fileName) {
		List<String[]> rows = new ArrayList<>();
		try {
			FileReader freader = new FileReader(fileName);
			com.opencsv.CSVReader creader = new com.opencsv.CSVReader(freader);
			rows = creader.readAll();
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
		return rows;
	}

	public static List<String[]> read(String filepath, boolean skipHeader) throws IOException {
		int firstNLinesToSkip = 0;
		if (skipHeader)
			firstNLinesToSkip = 1;
		return read(filepath, firstNLinesToSkip);
	}

	public static List<String[]> read(String filepath, int firstNLinesToSkip) throws IOException {
		LOGGER.debug("reading csv - " + filepath);
		FileReader filereader = new FileReader(new File(filepath));
//		com.opencsv.CSVReader csvReader = new CSVReaderBuilder(filereader).withSkipLines(firstNLinesToSkip).withCSVParser(new CSVParserBuilder().withQuoteChar('"').withSeparator(',').build()).build();
		com.opencsv.CSVReader csvReader = new com.opencsv.CSVReader(filereader, ',', '"', firstNLinesToSkip);
		List<String[]> allData = csvReader.readAll();
		csvReader.close();
		filereader.close();
		return allData;
	}

	public static List<Map> readToMap(String filepath, int firstNLinesToSkip) throws IOException {
		LOGGER.debug("reading csv - " + filepath);
		FileReader filereader = new FileReader(new File(filepath));
//	        com.opencsv.CSVReader csvReader = new CSVReaderBuilder(filereader).withSkipLines(firstNLinesToSkip).withCSVParser(new CSVParserBuilder().withQuoteChar('"').withSeparator(',').build()).build();
		com.opencsv.CSVReader csvReader = new com.opencsv.CSVReader(filereader, ',', '"', firstNLinesToSkip);
		List<String[]> allData = csvReader.readAll();
		csvReader.close();
		filereader.close();
		List<String> headers = new ArrayList<>();
		Collections.addAll(headers, allData.get(0));
		List<Map> data = new ArrayList<>();
		int index = -1;
		for (String row[] : allData) {
			++index;
			if (row.length == 0 || (row.length == 1 && StringUtils.isBlank(row[0])))
				continue; // ignore empty rows
			Map map = new LinkedHashMap<>();
			for (String header : headers) {
				try {
					map.put(header, row[headers.indexOf(header)]);
				} catch (IndexOutOfBoundsException iex) {
					throw new RuntimeException(String.format("Index error at index %s", index), iex);
				}
			}
			data.add(map);
		}
		return data;
	}

	/**
	 * Create an object of csvreader class to set behaviour of output
	 * 
	 * Constructs CSVWriter with supplied separator, quote char, escape char and
	 * line ending.
	 * 
	 * Constructor Params
	 * 
	 * @param writer     The writer to an underlying CSV source.
	 * @param separator  The delimiter to use for separating entries
	 * @param quotechar  The character to use for quoted elements
	 * @param escapechar The character to use for escaping quotechars or escapechars
	 * @param lineEnd    The line feed terminator to use
	 * 
	 * @param directory
	 * @param fileName
	 * @param allLines
	 * @return
	 */
	public static String writeToCsv(String directory, String fileName, List<String[]> allLines) {
		LOGGER.info("writing to csv - " + directory + File.separator + fileName);
		try {
			File dir = new File(directory);
			if (!dir.exists() || !dir.isDirectory()) {
				dir.mkdir();
			}
			FileWriterWithEncoding writer = new FileWriterWithEncoding(directory + "/" + fileName,
					StandardCharsets.UTF_8);

			CSVWriter csvwriter = null;
			csvwriter = new CSVWriter(writer);
			if (CSVReader.overwrite)
				csvwriter = new CSVWriter(writer, CSVReader.separator, CSVReader.quotechar, CSVReader.escapechar,
						CSVReader.lineEnd);
			else
				csvwriter = new CSVWriter(writer);
			csvwriter.writeAll(allLines, false);
			csvwriter.close();
			writer.close();
		} catch (IOException e) {
			LOGGER.error("All lines: ", allLines);
			LOGGER.error("Failed to write data to csv.", e);
		}
		return directory + File.separator + fileName;
	}

	/**
	 * Convert excel to Csv where header index is 0 creates dir if not exist .
	 * 
	 * @param excelFile
	 * @param sheetName
	 * 
	 * @param directory
	 * @param filename
	 * 
	 * @return file path
	 * @throws Exception
	 */
	public static String excelToCsv(String excelFile, String sheetName, String directory, String filename)
			throws Exception {
		try {
			List<Map<String, Object>> readSheetToMap = ExcelReader.readSheetToMap(excelFile, sheetName, 0);
			List<String[]> csvRecord = new ArrayList<>();
			String[] header = readSheetToMap.get(0).keySet().toArray(new String[readSheetToMap.get(0).keySet().size()]);
			csvRecord.add(header);
			for (Map<String, Object> each : readSheetToMap) {
				if (!(readSheetToMap.indexOf(each) == 0)) {
					String[] values = each.values().toArray(new String[each.size()]);
					csvRecord.add(values);
				}
			}
			appendToCsv(directory, filename + ".csv", csvRecord);
		} catch (Exception e) {
			LOGGER.error("Error while Creating csv", e);
			throw new Exception("Error while Creating csv", e);
		}
		LOGGER.info("file path : " + Paths.get(directory, filename + ".csv").toString());
		return Paths.get(directory, filename + ".csv").toString();
	}

	/**
	 * Creates CSV file from List<Map<String, String>> and returns file path creates
	 * dir if not exist ;
	 * 
	 * @param mapString
	 * @param directory
	 * @param filename
	 * 
	 * @return file path
	 * 
	 * @throws Exception
	 */
	public static String mapToCsv(List<Map<String, String>> mapString, String directory, String filename)
			throws Exception {
		try {
			CSVReader csv = new CSVReader();
			List<String[]> csvData = new ArrayList<String[]>();
			String[] header = mapString.get(0).keySet().toArray(new String[mapString.get(0).keySet().size()]);
			csvData.add(header);
			for (Map<String, String> each : mapString) {
				if (!(mapString.indexOf(each) == 0)) {
					String[] values = each.values().toArray(new String[each.size()]);
					csvData.add(values);
				}
			}
			csv.appendToCsv(directory, filename + ".csv", csvData);

		} catch (Exception e) {
			LOGGER.error("Error while Creating csv", e);
			throw new Exception("Error while Creating csv", e);
		}
		return Paths.get(directory, filename + ".csv").toString();

	}

	public static void appendToCsv(String directory, String fileName, String value) {
		List<String[]> allLines = new ArrayList<>();
		String[] a = new String[1];
		a[0] = value;
		allLines.add(a);
		appendToCsv(directory, fileName, allLines);
	}

	public static void appendToCsv(String directory, String fileName, List<String[]> allLines) {
		LOGGER.info("appending to csv - " + directory + "/" + fileName);
		try {
			File dir = new File(directory);
			if (!dir.exists() || !dir.isDirectory()) {
				dir.mkdir();
			}
			FileWriterWithEncoding writer = new FileWriterWithEncoding(directory + "/" + fileName,
					StandardCharsets.UTF_8, true);
			CSVWriter csvwriter = null;
			if (overwrite)
				csvwriter = new CSVWriter(writer, separator, quotechar, escapechar, lineEnd);
			else
				csvwriter = new CSVWriter(writer);
			for (String[] line : allLines) {
				csvwriter.writeNext(line, false);
			}
			csvwriter.close();
			writer.close();
		} catch (IOException e) {
			LOGGER.error("All Lines: ", allLines);
			LOGGER.error("", e);
		}
	}

	public static char detectCSVDelimiter(String path) {
		return detectCSVFormat(path).getDelimiter();
	}

	public static CsvFormat detectCSVFormat(String path) {
		CsvParserSettings settings = new CsvParserSettings();
		settings.detectFormatAutomatically();
		settings.setMaxCharsPerColumn(-1);
		settings.getFormat().setQuote('\0');
		com.univocity.parsers.csv.CsvParser parser = new com.univocity.parsers.csv.CsvParser(settings);
		List<String[]> rows = parser.parseAll(new File(path));
		return parser.getDetectedFormat();
	}

	public static List<String[]> readUsingParser(String path) {
		CsvParserSettings settings = new CsvParserSettings();
		settings.detectFormatAutomatically();
		settings.setMaxCharsPerColumn(-1);
		settings.getFormat().setQuote('\0');
		settings.setDelimiterDetectionEnabled(true, ',', '\t');
		settings.setQuoteDetectionEnabled(true);
		settings.trimQuotedValues(true);
		settings.getFormat().setQuote('\0');
		com.univocity.parsers.csv.CsvParser parser = new com.univocity.parsers.csv.CsvParser(settings);
		List<String[]> rows = parser.parseAll(new File(path));
		return rows;
	}

	/**
	 * Converts nested json/array objects to CSV
	 * 
	 * @param response        Response in string format, could be nested json/array
	 * @param destinationFile File path
	 * @throws BishopRuleViolationException
	 */
	public static void fromJsonToCsv(String response, String destinationFile) throws BishopRuleViolationException {
		JFlat flatMe = new JFlat(response);
		flatMe.json2Sheet().getJsonAsSheet();
		try {
			flatMe.headerSeparator("_").write2csv(destinationFile);
			LOGGER.info("JSON converted to CSV. File path: {}", destinationFile);
		} catch (Exception e) {
			throw new BishopRuleViolationException("Failed to convert JSON to CSV", e);
		}
	}

	/**
	 * Write a list of objects to a csv.
	 * 
	 * @param obj
	 * @param clz
	 * @param name
	 * @param outputDir
	 * @return
	 * @throws Exception
	 */
	public static String write(List<Object> obj, Class clz, String name, String outputDir) throws Exception {
		FileWriterWithEncoding writer = null;
		try {
			if (CollectionUtils.isNotEmpty(obj)) {
				FileHandlingUtils.mkdir(outputDir);
				String csvpath = Paths.get(outputDir, name).toString();
				if (!"csv".equals(FilenameUtils.getExtension(csvpath))) {
					csvpath = csvpath + ".csv";
				}
				File file = new File(csvpath);
				writer = new FileWriterWithEncoding(file, StandardCharsets.UTF_8, true);
				ColumnPositionMappingStrategy mappingStrategy = new ColumnPositionMappingStrategy();
				mappingStrategy.setType(clz);
				Field[] fieldArr = clz.getDeclaredFields();
				List<String> fields = new ArrayList<>();
				for (Field f : fieldArr) {
					fields.add(f.getName());
				}
				mappingStrategy.setColumnMapping(fields.toArray(new String[fields.size()]));

				StatefulBeanToCsv beanToCsv = new StatefulBeanToCsvBuilder(writer).withMappingStrategy(mappingStrategy)
//				    		. withSeparator('#')
//				    		.withQuotechar(CSVWriter.NO_QUOTE_CHARACTER)
						.build();
				beanToCsv.write(obj);
				writer.close();

				LOGGER.info("Data is convereted to csv from map. Records converted " + CollectionUtils.size(obj));
				LOGGER.info("CSV file: " + csvpath);
//				encryptCsv(csvpath);
				return csvpath;
			} else {
				LOGGER.info("No map data found to convert to csv");
				return StringUtils.EMPTY;
			}
		} catch (Exception e) {
			LOGGER.error("", e);
			throw e;
		} finally {
			if (writer != null)
				writer.close();
		}

	}

}
