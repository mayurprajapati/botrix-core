package rpa.core.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;

import botrix.internal.logging.LoggerFactory;
import rpa.core.file.FileHandlingUtils;

public class ExcelWriter {
	private Logger LOGGER = LoggerFactory.getLogger(ExcelWriter.class);
	FileInputStream excelFileToRead = null;
	FileOutputStream out = null;
	XSSFWorkbook workbook = null;
	XSSFSheet sheet = null;

	public void refreshFormulas(String excelFile) throws Exception {
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(excelFile);
			if (fis != null) {
				workbook = new XSSFWorkbook(fis);
				XSSFFormulaEvaluator x = new XSSFFormulaEvaluator(workbook);
				x.clearAllCachedResultValues();
				x.evaluateAll();
				out = new FileOutputStream(excelFile);
				workbook.write(out);
			} else {
				LOGGER.error("No excel found at " + excelFile);
				throw new Exception("No excel found at " + excelFile);
			}
		} catch (Exception e) {
			LOGGER.error("Error while creating excel", e);
			throw new Exception(e);
		}

	}

	public void addCellValuesToSheet(String excelFile, String sheetname, Map<String, String> data) throws Exception {
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(excelFile);
			if (fis != null) {
				workbook = new XSSFWorkbook(fis);
				sheet = loadSheet(workbook, sheetname);
				setCellData(data);
				out = new FileOutputStream(excelFile);
				workbook.write(out);
				LOGGER.info("Data added to excel file: " + excelFile);
			} else {
				LOGGER.error("No excel found at " + excelFile);
				throw new Exception("No excel found at " + excelFile);
			}
		} catch (Exception e) {
			LOGGER.error("Error while creating excel", e);
			throw new Exception(e);
		} finally {
			workbook.close();
			out.close();
		}
	}

	public void addCellValuesToSheet(String excelFile, String sheetname, String value, int rowNum) throws Exception {
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(excelFile);
			if (fis != null) {
				workbook = new XSSFWorkbook(fis);
				sheet = loadSheet(workbook, sheetname);
				List<String[]> allLines = new ArrayList<>();
				String[] a = new String[1];
				a[0] = value;
				allLines.add(a);
				setDataInExcel(allLines, rowNum);
				out = new FileOutputStream(excelFile);
				workbook.write(out);
				LOGGER.info("Data added to excel file: " + excelFile);
			} else {
				LOGGER.error("No excel found at " + excelFile);
				throw new Exception("No excel found at " + excelFile);
			}
		} catch (Exception e) {
			LOGGER.error("Error while creating excel", e);
			throw new Exception(e);
		} finally {
			workbook.close();
			out.close();
		}
	}

	private int getColumnNoFromAlphabet(String range) {
		String alphabet = range.replaceAll("\\d", StringUtils.EMPTY);
		int result = 0;
		for (int i = 0; i < alphabet.length(); i++) {
			result *= 26;
			result += alphabet.charAt(i) - 'A' + 1;
		}
		return result - 1;
	}

	private void setCellData(Map<String, String> data) {
		int rowCount = getLastRowCount();
		for (String range : data.keySet()) {
			int rowNo = Integer.valueOf(range.replaceAll("\\D", StringUtils.EMPTY)) - 1;
			int columnNo = getColumnNoFromAlphabet(range);
			String cellValue = data.get(range);
			XSSFRow row = sheet.getRow(rowNo);
			if (row == null) {
				row = sheet.createRow(rowNo);
			}

			XSSFCell cell = row.getCell(columnNo);
			if (cell != null) {
				cell.setCellType(CellType.STRING);
				cell.setCellValue(cellValue);
			} else {
				cell = row.createCell(columnNo);
				cell.setCellType(CellType.STRING);
				cell.setCellValue(cellValue);
			}
		}
	}

	public void createExcel(String excelFile, String sheetname, String[] header, int headerRowNum, int headerCellNum)
			throws Exception {
		File file = new File(excelFile);
		FileInputStream fis = null;
		String dir = FilenameUtils.getFullPath(file.getAbsolutePath());
		FileHandlingUtils.mkdir(dir);
		createnewWBAndAddHeader(excelFile, sheetname, header, file, headerRowNum, headerCellNum);
		fis = new FileInputStream(excelFile);
		workbook = new XSSFWorkbook(fis);
		sheet = loadSheet(workbook, sheetname);
		if (sheet == null) {
			sheet = workbook.createSheet(sheetname);
			if (header.length > 0) {
				addHeaderInSheet(header, headerRowNum, headerCellNum);
			}
		}
	}

	public String appendToExcel(String excelFile, String sheetname, List<String[]> data, String[] header) {
		return appendToExcel(excelFile, sheetname, data, header, 0, 0, 0);
	}

	public String appendToExcel(String excelFile, String sheetname, List<String[]> data, String[] header,
			int headerRowNum, int headerCellNum, int rowNum) {
		try {
			createExcel(excelFile, sheetname, header, headerRowNum, headerCellNum);
			setDataInExcel(data, rowNum);
			out = new FileOutputStream(excelFile);
			workbook.write(out);
			LOGGER.info("Data added to excel file: " + excelFile);
			return excelFile;
		} catch (Exception e) {
			LOGGER.error("Error while creating excel", e);
		}
		return StringUtils.EMPTY;
	}

	public String appendListToExcel(String excelFile, String sheetname, List<String> data, String[] header) {
		return appendListToExcel(excelFile, sheetname, data, header, 0, 0, 0);
	}

	public String appendListToExcel(String excelFile, String sheetname, List<String> data, String[] header,
			int headerRowNum, int headerCellNum, int rowNum) {
		try {
			createExcel(excelFile, sheetname, header, headerRowNum, headerCellNum);
			listToExcel(data, rowNum);
			out = new FileOutputStream(excelFile);
			workbook.write(out);
			LOGGER.info("Data added to excel file: " + excelFile);
			return excelFile;
		} catch (Exception e) {
			LOGGER.error("Error while creating excel", e);
		}
		return StringUtils.EMPTY;
	}

	private XSSFSheet loadSheet(Workbook workbook, String sheetname) {
		return buildSheetForXLSX(sheetname, workbook);
	}

	private XSSFSheet buildSheetForXLSX(String sheetname, Workbook workbook) {
		if (StringUtils.isNotEmpty(sheetname)) {
			return (XSSFSheet) workbook.getSheet(sheetname);
		} else {
			return (XSSFSheet) workbook.getSheetAt(0);
		}
	}

	private void setDataInExcel(List<String[]> data, int rowNum) {
		int rowCount = rowNum != 0 ? rowNum : getLastRowCount();
		for (String[] rowData : data) {
			XSSFRow row = sheet.getRow(++rowCount);
			if (row == null) {
				row = sheet.createRow(rowCount);
			}
			setRowData(rowData, row);
		}
	}

	private int getLastRowCount() {
		int phyRowCnt = sheet.getPhysicalNumberOfRows();
		int rowCount = sheet.getLastRowNum();
		if (phyRowCnt == 0 && rowCount == 0) {
			rowCount = -1;
		}
		return rowCount;
	}

	private void createnewWBAndAddHeader(String excelFile, String sheetname, String[] header, File file,
			int headerRowNum, int headerCellNum) throws IOException {
		if (!file.exists()) {
			createNewXSheet(sheetname, excelFile);
			out = new FileOutputStream(excelFile);
			if (header.length > 0) {
				addHeaderInSheet(header, headerRowNum, headerCellNum);
			}
			out = null;
		}
	}

	private boolean createNewXSheet(String sheetname, String fullFileName) throws IOException {
		boolean createNewXSheet = false;
		out = new FileOutputStream(new File(fullFileName));
		workbook = new XSSFWorkbook();
		if (sheetname == null || sheetname.isEmpty()) {
			sheet = workbook.createSheet("Sheet1");
		} else {
			sheet = workbook.createSheet(sheetname);
			if (sheet == null)
				sheet = workbook.createSheet(sheetname);
		}
		workbook.write(out);
		createNewXSheet = true;
		return createNewXSheet;
	}

	private void addHeaderInSheet(String[] headers, int rowNum, int cellNum) throws IOException {
		Row row = null;
		if (sheet != null) {
			row = sheet.createRow(rowNum);
		}
		setCellValue(headers, row, cellNum);
		if (workbook != null) {
			workbook.write(out);
		}
	}

	private void setCellValue(Object[] arr, Row row, int cellNum) {
		for (Object obj : arr) {
			Cell cell = row.createCell(cellNum++);
			if (obj instanceof String) {
				cell.setCellValue((String) obj);
			} else if (obj instanceof Integer) {
				cell.setCellValue((Integer) obj);
			}
		}
	}

	private void setRowData(String[] rowData, XSSFRow row) {
		for (int i = 0; i < rowData.length; i++) {
			XSSFCell cell = row.getCell(i);
			if (cell != null) {
				cell.setCellType(CellType.STRING);
				cell.setCellValue(rowData[i]);
			} else {
				cell = row.createCell(i);
				cell.setCellType(CellType.STRING);
				cell.setCellValue(rowData[i]);
			}
		}
	}

	public void listToExcel(List<String> rowData, int rowNum) {

		int rowCount = rowNum != 0 ? rowNum : getLastRowCount();
		for (String data : rowData) {
			XSSFRow row = sheet.getRow(++rowCount);
			if (row == null) {
				row = sheet.createRow(rowCount);
			}
			XSSFCell cell = row.getCell(0);
			if (cell != null) {
				cell.setCellType(CellType.STRING);
				cell.setCellValue(data);
			} else {
				cell = row.createCell(0);
				cell.setCellType(CellType.STRING);
				cell.setCellValue(data);
			}
		}

	}

	public static void updateExtractedRecord(String excelpath, String recordNumber) {
		ExcelWriter writer = new ExcelWriter();
		String[] header = { "Extracts" };
		List<String[]> outputExcel = new ArrayList<String[]>();
		String[] recordExtracted = { recordNumber };
		outputExcel.add(recordExtracted);
		writer.appendToExcel(excelpath, null, outputExcel, header);
	}
}
