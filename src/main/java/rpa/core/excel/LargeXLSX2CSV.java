/* ====================================================================
   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
==================================================================== */

package rpa.core.excel;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FileUtils;
import org.apache.poi.openxml4j.exceptions.InvalidOperationException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackageAccess;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.util.XMLHelper;
import org.apache.poi.xssf.eventusermodel.ReadOnlySharedStringsTable;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler.SheetContentsHandler;
import org.apache.poi.xssf.extractor.XSSFEventBasedExcelExtractor;
import org.apache.poi.xssf.model.SharedStrings;
import org.apache.poi.xssf.model.Styles;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.usermodel.XSSFComment;
import org.slf4j.Logger;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import botrix.internal.logging.LoggerFactory;
import rpa.core.exceptions.BishopRuleViolationException;

/**
 * REF:<br>
 * https://poi.apache.org/components/spreadsheet/how-to.html#event_api <br>
 * https://svn.apache.org/repos/asf/poi/trunk/poi-examples/src/main/java/org/apache/poi/examples/xssf/eventusermodel/XLSX2CSV.java
 * 
 * A rudimentary XLSX -&gt; CSV processor modeled on the POI sample program
 * XLS2CSVmra from the package org.apache.poi.hssf.eventusermodel.examples. As
 * with the HSSF version, this tries to spot missing rows and cells, and output
 * empty entries for them.
 * <p>
 * Data sheets are read using a SAX parser to keep the memory footprint
 * relatively small, so this should be able to read enormous workbooks. The
 * styles table and the shared-string table must be kept in memory. The standard
 * POI styles table class is used, but a custom (read-only) class is used for
 * the shared string table because the standard POI SharedStringsTable grows
 * very quickly with the number of unique strings.
 * <p>
 * For a more advanced implementation of SAX event parsing of XLSX files, see
 * {@link XSSFEventBasedExcelExtractor} and {@link XSSFSheetXMLHandler}. Note
 * that for many cases, it may be possible to simply use those with a custom
 * {@link SheetContentsHandler} and no SAX code needed of your own!
 */
@SuppressWarnings({})
public class LargeXLSX2CSV {
	private static final Logger LOGGER = LoggerFactory.getLogger(LargeXLSX2CSV.class);

	/**
	 * Uses the XSSF Event SAX helpers to do most of the work of parsing the Sheet
	 * XML, and outputs the contents as a (basic) CSV.
	 */
	private class SheetToCSV implements SheetContentsHandler {
		private boolean firstCellOfRow;
		private int currentRow = -1;
		private int currentCol = -1;

		private void outputMissingRows(int number) {
			for (int i = 0; i < number; i++) {
				for (int j = 0; j < minColumns; j++) {
					output.append(',');
				}
				output.append('\n');
			}
		}

		@Override
		public void endSheet() {
			SheetContentsHandler.super.endSheet();
		}

		@Override
		public void startRow(int rowNum) {
			// If there were gaps, output the missing rows
			outputMissingRows(rowNum - currentRow - 1);
			// Prepare for this row
			firstCellOfRow = true;
			currentRow = rowNum;
			currentCol = -1;
		}

		@Override
		public void endRow(int rowNum) {
			// Ensure the minimum number of columns
			for (int i = currentCol; i < minColumns; i++) {
				output.append(',');
			}
			output.append('\n');
		}

		@Override
		public void cell(String cellReference, String formattedValue, XSSFComment comment) {
			if (firstCellOfRow) {
				firstCellOfRow = false;
			} else {
				output.append(',');
			}

			// gracefully handle missing CellRef here in a similar way as XSSFCell does
			if (cellReference == null) {
				cellReference = new CellAddress(currentRow, currentCol).formatAsString();
			}

			// Did we miss any cells?
			int thisCol = (new CellReference(cellReference)).getCol();
			int missedCols = thisCol - currentCol - 1;
			for (int i = 0; i < missedCols; i++) {
				output.append(',');
			}

			// no need to append anything if we do not have a value
			if (formattedValue == null) {
				return;
			}

			currentCol = thisCol;

			// Number or string?
			try {
				// noinspection ResultOfMethodCallIgnored
				Double.parseDouble(formattedValue);
				output.append(formattedValue);
			} catch (Exception e) {
				// let's remove quotes if they are already there
				if (formattedValue.startsWith("\"") && formattedValue.endsWith("\"")) {
					formattedValue = formattedValue.substring(1, formattedValue.length() - 1);
				}

				output.append('"');
				// encode double-quote with two double-quotes to produce a valid CSV format
				output.append(formattedValue.replace("\"", "\"\""));
				output.append('"');
			}
		}
	}

	///////////////////////////////////////

	private final OPCPackage xlsxPackage;

	/**
	 * Number of columns to read starting with leftmost
	 */
	private final int minColumns;

	/**
	 * Destination for data
	 */
	private PrintStream output;

	private final ReadOnlySharedStringsTable strings;
	private final XSSFReader xssfReader;
	private final StylesTable styles;
	private final XSSFReader.SheetIterator iter;

	/**
	 * Creates a new XLSX -&gt; CSV converter
	 *
	 * @param pkg The XLSX package to process
	 * @throws SAXException
	 * @throws IOException
	 * @throws OpenXML4JException
	 */
	public LargeXLSX2CSV(OPCPackage pkg) throws IOException, SAXException, OpenXML4JException {
		this.xlsxPackage = pkg;
		this.minColumns = -1;
		this.strings = new ReadOnlySharedStringsTable(this.xlsxPackage);
		this.xssfReader = new XSSFReader(this.xlsxPackage);
		this.styles = xssfReader.getStylesTable();
		this.iter = (XSSFReader.SheetIterator) xssfReader.getSheetsData();
	}

	/**
	 * Parses and shows the content of one sheet using the specified styles and
	 * shared-strings tables.
	 *
	 * @param styles           The table of styles that may be referenced by cells
	 *                         in the sheet
	 * @param strings          The table of strings that may be referenced by cells
	 *                         in the sheet
	 * @param sheetInputStream The stream to read the sheet-data from.
	 * 
	 * @throws java.io.IOException        An IO exception from the parser, possibly
	 *                                    from a byte stream or character stream
	 *                                    supplied by the application.
	 * @throws SAXException               if parsing the XML data fails.
	 * @throws BishopRuleViolationException
	 */
	public void processSheet(Styles styles, SharedStrings strings, SheetContentsHandler sheetHandler,
			InputStream sheetInputStream, File csv) throws IOException, SAXException, BishopRuleViolationException {
		// set emulateCSV=true on DataFormatter - it is also possible to provide a
		// Locale
		// when POI 5.2.0 is released, you can call
		// formatter.setUse4DigitYearsInAllDateFormats(true)
		// to ensure all dates are formatted with 4 digit years
		DataFormatter formatter = new DataFormatter(true);
		InputSource sheetSource = new InputSource(sheetInputStream);
		try (PrintStream out = new PrintStream(csv)) {
			this.output = out;
			XMLReader sheetParser = XMLHelper.newXMLReader();
			ContentHandler handler = new XSSFSheetXMLHandler(styles, null, strings, sheetHandler, formatter, false);
			sheetParser.setContentHandler(handler);
			sheetParser.parse(sheetSource);
			LOGGER.info("Successfuly converted file: {}", csv.getAbsolutePath());

		} catch (ParserConfigurationException e) {
			throw new BishopRuleViolationException("Failed to convert CSV file " + csv.getAbsolutePath(), e);
		}
	}

	/**
	 * Initiates the processing of the XLS workbook file to CSV.
	 * 
	 * @param sheetName Sheet name of workbook
	 * 
	 * @param out       To write CSV data
	 * 
	 * @throws IOException                If reading the data from the package
	 *                                    fails.
	 * @throws SAXException               if parsing the XML data fails.
	 * @throws BishopRuleViolationException
	 */
	public void process(String sheetName, File csv) throws IOException, SAXException, BishopRuleViolationException {

		while (iter.hasNext()) {
			try (InputStream stream = iter.next()) {
				if (sheetName.equalsIgnoreCase(iter.getSheetName())) {
					processSheet(styles, strings, new SheetToCSV(), stream, csv);
				}
			}
		}
	}

	public List<File> processAllSheets(String dir) throws BishopRuleViolationException, IOException, SAXException {
		List<File> allFiles = new ArrayList<>();

		try {
			while (iter.hasNext()) {
				try (InputStream stream = iter.next()) {
					File f = new File(dir, iter.getSheetName() + ".csv");
					processSheet(styles, strings, new SheetToCSV(), stream, f);
					allFiles.add(f);
				}
			}
		} catch (Exception e) {
			throw new BishopRuleViolationException("Failed to convert all sheet");
		}
		return allFiles;
	}

	/**
	 * Convert xlsx file & saves in temp csv file
	 * 
	 * @param xlsxFile  excel file to convert
	 * @param sheetName name of the sheet
	 * @throws InvalidOperationException
	 * @throws IOException
	 * @throws OpenXML4JException
	 * @throws SAXException
	 * @throws BishopRuleViolationException
	 */
	public static File convertToCsv(File xlsxFile, String sheetName) throws InvalidOperationException, IOException,
			OpenXML4JException, SAXException, BishopRuleViolationException {
		File tempCsvFile = new File(java.nio.file.Files.createTempDirectory(xlsxFile.getName()).toString(),
				sheetName + ".csv");
		convertToCsv(xlsxFile, sheetName, tempCsvFile);
		return tempCsvFile;
	}

	/**
	 * Convert xlsx file & save as csv file
	 * 
	 * @param xlsxFile  excel file to convert
	 * @param sheetName name of the sheet
	 * @param csv       to where save the converted data
	 * @throws InvalidOperationException
	 * @throws IOException
	 * @throws OpenXML4JException
	 * @throws SAXException
	 * @throws BishopRuleViolationException
	 */
	public static void convertToCsv(File xlsxFile, String sheetName, File csv) throws InvalidOperationException,
			IOException, OpenXML4JException, SAXException, BishopRuleViolationException {
		FileUtils.deleteQuietly(csv);

		// The package open is instantaneous, as it should be.
		try (OPCPackage p = OPCPackage.open(xlsxFile.getPath(), PackageAccess.READ)) {

			LargeXLSX2CSV xlsx2csv = new LargeXLSX2CSV(p);
			xlsx2csv.process(sheetName, csv);
			// revert all changes before closing
			p.revert();
		}
	}

	public static List<File> convertAllSheetsToCsv(File xlsxFile, String directory) throws InvalidOperationException,
			IOException, OpenXML4JException, SAXException, BishopRuleViolationException {
		List<File> allFiles = new ArrayList<>();
		try (OPCPackage p = OPCPackage.open(xlsxFile.getPath(), PackageAccess.READ)) {
			LargeXLSX2CSV xlsx2csv = new LargeXLSX2CSV(p);
			allFiles = xlsx2csv.processAllSheets(directory);
			// revert all changes before closing
			p.revert();
		}
		return allFiles;
	}

}
