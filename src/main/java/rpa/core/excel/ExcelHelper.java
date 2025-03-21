package rpa.core.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;

import botrix.internal.logging.LoggerFactory;
import rpa.core.exceptions.BishopRuleViolationException;

public class ExcelHelper {
	private static Logger LOGGER = LoggerFactory.getLogger(ExcelHelper.class);
	private static FileInputStream fis = null;
	private static FileOutputStream fos = null;
	private static XSSFWorkbook workbook = null;
	private static XSSFSheet sheet = null;
	public static boolean formatNumbers = false;
	private static final String MESSAGE = "Sheet: %s data created successfully";

	// ################# Reading Excel #################

	public static List<List<Object>> readExcel(String filePath, String sheetName) throws BishopRuleViolationException {
		try {
			fis = new FileInputStream(new File(filePath));
			workbook = new XSSFWorkbook(fis);
			sheet = workbook.getSheet(sheetName);
			List<List<Object>> data = new ArrayList<>();
			Iterator<Row> rowIterator = sheet.iterator();
			while (rowIterator.hasNext()) {
				Row row = rowIterator.next();
				Iterator<Cell> cellIterator = row.cellIterator();
				List<Object> record = new ArrayList<>();
				while (cellIterator.hasNext()) {
					Cell cell = cellIterator.next();
					record.add(cell);
				}
			}
			workbook.close();
			fis.close();
			LOGGER.info(String.format("Sheet: %s data fetched successfully", sheetName));
			return data;
		} catch (Exception e) {
			throw new BishopRuleViolationException(String.format("Failed to delete sheet: %s", sheetName), e);
		}
	}

	// ################# Creating Excel #################

	public static void createNewExcel(String filePath, String sheetName, List<Map<String, Object>> data)
			throws BishopRuleViolationException {
		createNewExcel(filePath, sheetName, data, 0, 0);
	}

	public static void createNewExcel(String filePath, String sheetName, List<Map<String, Object>> data, int rowNum,
			int colNum) throws BishopRuleViolationException {
		try {
			workbook = new XSSFWorkbook();
			sheet = workbook.createSheet(sheetName);
			createSheetHeader(data.get(0).keySet(), rowNum++, colNum);
			for (Map<String, Object> record : data) {
				createSheetRow(record, rowNum++, colNum);
			}
			fos = new FileOutputStream(new File(filePath));
			workbook.write(fos);
			workbook.close();
			fos.close();
			LOGGER.info(String.format(MESSAGE, sheetName));
		} catch (Exception e) {
			throw new BishopRuleViolationException(String.format("Failed to create sheet: %s", sheetName), e);
		}
	}

	public static void createNewExcel(String filePath, String sheetName, Map<String, Object[]> data) throws Exception {
		createNewExcel(filePath, sheetName, data, 0, 0);
	}

	public static void createNewExcel(String filePath, String sheetName, Map<String, Object[]> data, int rowNum,
			int colNum) throws BishopRuleViolationException {
		try {
			workbook = new XSSFWorkbook();
			sheet = workbook.createSheet(sheetName);
			Set<String> keyset = data.keySet();
			for (String key : keyset) {
				Row row = sheet.createRow(rowNum++);
				Object[] objArr = data.get(key);
				int startCol = colNum;
				for (Object obj : objArr) {
					Cell cell = row.createCell(startCol++);
					if (obj instanceof String)
						cell.setCellValue((String) obj);
					else if (obj instanceof Integer)
						cell.setCellValue((Integer) obj);
					else if (obj instanceof Date)
						cell.setCellValue((Date) obj);
					else if (obj instanceof Boolean)
						cell.setCellValue((Boolean) obj);
					else if (obj instanceof LocalDate)
						cell.setCellValue((LocalDate) obj);
					else if (obj instanceof Calendar)
						cell.setCellValue((Calendar) obj);
					else if (obj instanceof RichTextString)
						cell.setCellValue((RichTextString) obj);
					else if (obj instanceof LocalDateTime)
						cell.setCellValue((LocalDateTime) obj);
					else
						cell.setCellValue(obj + "");
				}
			}
			fos = new FileOutputStream(new File(filePath));
			workbook.write(fos);
			workbook.close();
			fos.close();
			LOGGER.info(String.format(MESSAGE, sheetName));
		} catch (Exception e) {
			throw new BishopRuleViolationException(String.format("Failed to create sheet: %s", sheetName), e);
		}
	}

	private static void createSheetHeader(Set<String> headers, int rowNum, int colNum) {
		Row row = sheet.createRow(rowNum);
		for (String header : headers) {
			Cell cell = row.createCell(colNum++);
			cell.setCellValue((String) header);
		}
	}

	private static void createSheetRow(Map<String, Object> rowData, int rowNum, int colNum) {
		Set<String> keyset = rowData.keySet();
		Row row = sheet.createRow(rowNum++);
		for (String key : keyset) {
			Object obj = rowData.get(key);
			Cell cell = row.createCell(colNum++);
			if (obj instanceof String)
				cell.setCellValue((String) obj);
			else if (obj instanceof Integer)
				cell.setCellValue((Integer) obj);
			else if (obj instanceof Date)
				cell.setCellValue((Date) obj);
			else if (obj instanceof Boolean)
				cell.setCellValue((Boolean) obj);
			else if (obj instanceof LocalDate)
				cell.setCellValue((LocalDate) obj);
			else if (obj instanceof Calendar)
				cell.setCellValue((Calendar) obj);
			else if (obj instanceof RichTextString)
				cell.setCellValue((RichTextString) obj);
			else if (obj instanceof LocalDateTime)
				cell.setCellValue((LocalDateTime) obj);
			else
				cell.setCellValue(obj + "");
		}
	}

	// ################# Updating Excel #################

	public static void updateExcel(String filePath, String sheetName, List<Map<String, Object>> data, int rowNum,
			int colNum) throws BishopRuleViolationException {
		updateExcel(filePath, sheetName, data, rowNum, colNum, false);
	}

	public static void updateExcel(String filePath, String sheetName, List<Map<String, Object>> data, int rowNum,
			int colNum, boolean deleteSheetIfExists) throws BishopRuleViolationException {
		try {
			fis = new FileInputStream(new File(filePath));
			workbook = new XSSFWorkbook(fis);

			if (deleteSheetIfExists) {
				sheet = workbook.getSheet(sheetName);
				if (sheet != null) {
					workbook.removeSheetAt(workbook.getSheetIndex(sheetName));
					sheet = null;
				}
			} else {
				sheet = workbook.getSheet(sheetName);
			}
			if (sheet == null) {
				sheet = workbook.createSheet(sheetName);
			}

			// Creating Headers If doesn't Exists and Setting Row Indexes
			int startRowIndex = 0;
			int lastRowIndex = sheet.getLastRowNum();
			if (lastRowIndex == -1) {
				startRowIndex = rowNum;
				createSheetHeader(data.get(0).keySet(), startRowIndex++, colNum);
			} else {
				startRowIndex = ++lastRowIndex + rowNum;
			}

			// Creating Data Rows
			for (Map<String, Object> record : data) {
				createSheetRow(record, startRowIndex++, colNum);
			}

			fos = new FileOutputStream(new File(filePath));
			workbook.write(fos);
			workbook.close();
			fis.close();
			fos.close();
			LOGGER.info(String.format(MESSAGE, sheetName));
		} catch (Exception e) {
			throw new BishopRuleViolationException(String.format("Failed to create sheet: %s", sheetName), e);
		}
	}

	// ################# Deleting Excel #################

	public static void deleteSheet(String filePath, String sheetName) throws BishopRuleViolationException {
		try {
			fis = new FileInputStream(new File(filePath));
			workbook = new XSSFWorkbook(fis);
			workbook.removeSheetAt(workbook.getSheetIndex(sheetName));
			fos = new FileOutputStream(new File(filePath));
			workbook.write(fos);
			workbook.close();
			fis.close();
			fos.close();
			LOGGER.info(String.format("Sheet: %s removed successfully", sheetName));
		} catch (Exception e) {
			throw new BishopRuleViolationException(String.format("Failed to delete sheet: %s", sheetName), e);
		}
	}

	// ################# Managing Excel #################

	public static void refreshWorkbook(String filePath) throws BishopRuleViolationException {
		try {
			fis = new FileInputStream(new File(filePath));
			workbook = new XSSFWorkbook(fis);
			workbook.setForceFormulaRecalculation(true);
//			XSSFFormulaEvaluator.evaluateAllFormulaCells(workbook);
			fos = new FileOutputStream(new File(filePath));
			workbook.write(fos);
			workbook.close();
			fis.close();
			fos.close();
			LOGGER.info(String.format("Workbook refreshed successfully", filePath));
		} catch (Exception e) {
			throw new BishopRuleViolationException(
					String.format("Something went wrong while refreshing workbook: ", filePath), e);
		}
	}

	public static void hideSheets(String filePath, List<String> sheetNames) throws Exception {
		try {
			fis = new FileInputStream(new File(filePath));
			workbook = new XSSFWorkbook(fis);
			for (String sheetName : sheetNames) {
				sheet = workbook.getSheet(sheetName);
				if (sheet != null) {
					workbook.setSheetHidden(workbook.getSheetIndex(sheet), true);
				}
			}
			fos = new FileOutputStream(new File(filePath));
			workbook.write(fos);
			workbook.close();
			fis.close();
			fos.close();
			LOGGER.info(String.format("Sheets: %s hidden successfully", sheetNames.toString()));
		} catch (Exception e) {
			throw new BishopRuleViolationException(String.format("Sheets: %s hidden successfully", sheetNames.toString()),
					e);
		}
	}

	public static void copySheet(String fromFilePath, String fromSheetName, String toFilePath, String toSheetName)
			throws BishopRuleViolationException {
		try {
			fis = new FileInputStream(new File(fromFilePath));
			workbook = new XSSFWorkbook(fis);
			sheet = workbook.getSheet(fromSheetName);

			FileInputStream newFis = new FileInputStream(new File(toFilePath));
			XSSFWorkbook newWorkbook = new XSSFWorkbook(newFis);

			newWorkbook.removeSheetAt(newWorkbook.getSheetIndex(toSheetName));
			XSSFSheet newSheet = newWorkbook.createSheet(toSheetName);
			copySheets(sheet, newSheet, true);

			FileOutputStream newFos = new FileOutputStream(new File(toFilePath));
			newWorkbook.write(newFos);
			newWorkbook.close();
			workbook.close();
			fis.close();
			newFis.close();
			newFos.close();
		} catch (Exception e) {
			throw new BishopRuleViolationException("Failed to copy sheet", e);
		}
	}

	private static void copySheets(XSSFSheet fromSheet, XSSFSheet toSheet, boolean copyStyle) {
		int maxColumnNum = 0;
		Map<Integer, XSSFCellStyle> styleMap = (copyStyle) ? new HashMap<Integer, XSSFCellStyle>() : null;
		for (int i = fromSheet.getFirstRowNum(); i <= fromSheet.getLastRowNum(); i++) {
			XSSFRow srcRow = fromSheet.getRow(i);
			XSSFRow destRow = toSheet.createRow(i);
			if (srcRow != null) {
				copyRow(fromSheet, toSheet, srcRow, destRow, styleMap);
				if (srcRow.getLastCellNum() > maxColumnNum) {
					maxColumnNum = srcRow.getLastCellNum();
				}
			}
		}
		for (int i = 0; i <= maxColumnNum; i++) {
			toSheet.setColumnWidth(i, fromSheet.getColumnWidth(i));
		}
	}

	private static void copyRow(XSSFSheet srcSheet, XSSFSheet destSheet, XSSFRow srcRow, XSSFRow destRow,
			Map<Integer, XSSFCellStyle> styleMap) {
		Set<CellRangeAddress> mergedRegions = new TreeSet<CellRangeAddress>();
		destRow.setHeight(srcRow.getHeight());
		for (int j = srcRow.getFirstCellNum(); j <= srcRow.getLastCellNum(); j++) {
			XSSFCell oldCell = srcRow.getCell(j);
			XSSFCell newCell = destRow.getCell(j);
			if (oldCell != null) {
				if (newCell == null) {
					newCell = destRow.createCell(j);
				}
				copyCell(oldCell, newCell, styleMap);
				CellRangeAddress mergedRegion = getMergedRegion(srcSheet, srcRow.getRowNum(),
						(short) oldCell.getColumnIndex());
				if (mergedRegion != null) {
					CellRangeAddress newMergedRegion = new CellRangeAddress(mergedRegion.getFirstRow(),
							mergedRegion.getFirstColumn(), mergedRegion.getLastRow(), mergedRegion.getLastColumn());
					if (isNewMergedRegion(newMergedRegion, mergedRegions)) {
						mergedRegions.add(newMergedRegion);
						destSheet.addMergedRegion(newMergedRegion);
					}
				}
			}
		}
	}

	private static CellRangeAddress getMergedRegion(XSSFSheet sheet, int rowNum, short cellNum) {
		for (int i = 0; i < sheet.getNumMergedRegions(); i++) {
			CellRangeAddress merged = sheet.getMergedRegion(i);
			if (merged.isInRange(rowNum, cellNum)) {
				return merged;
			}
		}
		return null;
	}

	private static boolean isNewMergedRegion(CellRangeAddress newMergedRegion,
			Collection<CellRangeAddress> mergedRegions) {
		return !mergedRegions.contains(newMergedRegion);
	}

	private static void copyCell(XSSFCell oldCell, XSSFCell newCell, Map<Integer, XSSFCellStyle> styleMap) {
		if (styleMap != null) {
			if (oldCell.getSheet().getWorkbook() == newCell.getSheet().getWorkbook()) {
				newCell.setCellStyle(oldCell.getCellStyle());
			} else {
				int stHashCode = oldCell.getCellStyle().hashCode();
				XSSFCellStyle newCellStyle = styleMap.get(stHashCode);
				if (newCellStyle == null) {
					newCellStyle = newCell.getSheet().getWorkbook().createCellStyle();
					newCellStyle.cloneStyleFrom(oldCell.getCellStyle());
					styleMap.put(stHashCode, newCellStyle);
				}
				newCell.setCellStyle(newCellStyle);
			}
		}
		String value = "";
		switch (oldCell.getCellType()) {
		case STRING:
			value = oldCell.getStringCellValue();
			if (formatNumbers) {
				try {
					newCell.setCellValue(Double.parseDouble(value));
				} catch (Exception e) {
					newCell.setCellValue(value);
				}
			} else {
				newCell.setCellValue(value);
			}
			break;
		case NUMERIC:
			newCell.setCellValue(oldCell.getNumericCellValue());
			break;
		case BLANK:
			newCell.setCellType(CellType.BLANK);
			break;
		case BOOLEAN:
			newCell.setCellValue(oldCell.getBooleanCellValue());
			break;
		case ERROR:
			newCell.setCellErrorValue(oldCell.getErrorCellValue());
			break;
		case FORMULA:
			switch (oldCell.getCachedFormulaResultType()) {
			case BOOLEAN:
				newCell.setCellValue(oldCell.getBooleanCellValue());
				break;
			case NUMERIC:
				newCell.setCellValue(oldCell.getNumericCellValue());
				break;
			case STRING:
				value = oldCell.getStringCellValue();
				if (formatNumbers) {
					try {
						newCell.setCellValue(Double.parseDouble(value));
					} catch (Exception e) {
						newCell.setCellValue(value);
					}
				} else {
					newCell.setCellValue(value);
				}
				break;
			default:
				break;
			}
			break;
		default:
			break;
		}
	}
}