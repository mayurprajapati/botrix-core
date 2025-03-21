package rpa.core.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import botrix.internal.logging.LoggerFactory;
import rpa.core.file.ParseUtils;

public class XLSReader {

	private static Logger LOGGER = LoggerFactory.getLogger(XLSReader.class);

	public static void deleteEntryFromExcel(String excelFile, String rowTextToDelete) throws Exception {
		LOGGER.info(String.format("Deleteing entry %s from file %s", rowTextToDelete, excelFile));
		boolean writeWb = false;
		try {
			Workbook workbook = loadWorkbook(excelFile);
			if (workbook != null) {
				Sheet sheet = loadSheet(workbook, "");
				int lastnum1 = sheet.getLastRowNum();
				int i = 0;
				while (i < lastnum1 + 1) {
					Row row = sheet.getRow(i);
					if (row != null) {
						Cell cell = row.getCell(0);
						if (cell != null) {
							Object cellValue = objectFrom(workbook, cell);
							if (StringUtils.equals(String.valueOf(cellValue), rowTextToDelete)) {
								sheet.removeRow(row);
								writeWb = true;
								break;
							}
						}
					}
					i++;
				}
			}
			if (writeWb) {
				FileOutputStream out = null;
				try {
					out = new FileOutputStream(excelFile);
					workbook.write(out);
					LOGGER.info(String.format("Entry %s is deleted from file %s", rowTextToDelete, excelFile));
				} catch (FileNotFoundException e) {
					LOGGER.error(
							String.format("Failed to remove entry %s from excel file %s ", rowTextToDelete, excelFile),
							e);
					throw new Exception(e);
				} catch (IOException e) {
					LOGGER.error("File not found " + excelFile, e);
					throw new Exception(e);
				} catch (Exception e) {
					LOGGER.error("Uncaught Error while deleting entry from excel", e);
					throw new Exception(e);
				} finally {
					if (out != null) {
						out.close();
					}
				}
			} else {
				LOGGER.info(String.format("%s not found in excel %s to delete", rowTextToDelete, excelFile));
			}
		} catch (Exception e) {
			LOGGER.error(String.format("Failed to delete entry %s from file %s", rowTextToDelete, excelFile), e);
			throw new Exception(String.format("Failed to delete entry %s from file %s. ", rowTextToDelete, excelFile)
					+ e.getMessage());
		}

	}

	/**
	 * Read excel sheet into List<List<String>>
	 * 
	 * @param excelFile
	 * @param sheetName
	 * @param headerindex
	 * @param columnIndex
	 * @return List<List<String>>
	 * @throws Exception
	 */
	public static List<List<String>> readSheetToList(String excelFile, String sheetName, int headerindex,
			int columnIndex) throws Exception {
		return readSheetToList(excelFile, sheetName, headerindex, columnIndex, "");
	}

	/**
	 * Read excel sheet into List<List<String>>
	 * 
	 * @param excelFile
	 * @param sheetName
	 * @param headerindex
	 * @param columnIndex
	 * @param toDateFormat is used to format date as required
	 * @return
	 * @throws Exception
	 */
	public static List<List<String>> readSheetToList(String excelFile, String sheetName, int headerindex,
			int columnIndex, String toDateFormat) throws Exception {
		return readSheetToList(excelFile, sheetName, headerindex, columnIndex, toDateFormat, false);
	}
	public static List<List<String>> readSheetToList(String excelFile, String sheetName, int headerindex,
			int columnIndex, String toDateFormat, boolean usePhysicalIndex) throws Exception {
		List<List<String>> ret = new ArrayList<List<String>>();
		try {
			Workbook workbook = loadWorkbook(excelFile);
			Sheet sheet = loadSheet(workbook, sheetName);
			if (sheet == null) {
				LOGGER.error(
						String.format("Sheet: %s not found in excel %s", sheetName, FilenameUtils.getName(excelFile)));
				throw new Exception(
						String.format("Sheet: %s not found in excel %s", sheetName, FilenameUtils.getName(excelFile)));
			}
			int lastnum = sheet.getLastRowNum();
			int numberOfColumns = countNonEmptyColumns(sheet, headerindex, columnIndex);
			if(usePhysicalIndex) {
				numberOfColumns = sheet.getRow(headerindex).getLastCellNum();
			}
			for (int rowNo = headerindex; rowNo <= lastnum; rowNo++) {
				Row row = sheet.getRow(rowNo);
				if (row != null) {
					List<String> rowData = new ArrayList<String>();
					for (int colNo = columnIndex; colNo < numberOfColumns; colNo++) {
						String cellValue = "";
						Cell cell = row.getCell(colNo);
						Object o = objectFrom(workbook, cell);
						if (o == null)
							cellValue = StringUtils.EMPTY;
						else if (StringUtils.isNotBlank(toDateFormat) && o.getClass().equals(Date.class))
							cellValue = ParseUtils.formatDate(toDateFormat, (Date) o);
						else
							cellValue = String.valueOf(o);
						rowData.add(cellValue);
					}
					ret.add(rowData);
				}
			}
			workbook.close();
		} catch (NullPointerException e) {
			LOGGER.error("Incomplete excel read: " + excelFile, e);
			return ret;
		} catch (Exception e) {
			LOGGER.error("Failed to read from excel: " + excelFile, e);
			throw new Exception("Failed to read from excel: " + FilenameUtils.getName(excelFile));
		}
		return ret;
	}
	
	public static List<Map<String, Object>> readSheetToMap(String excelFile) throws Exception {
		return readSheetToMap(excelFile, "", 0);
	}

	public static List<Map<String, Object>> readSheetToMap(String excelFile, String sheetName, int headerindex)
			throws Exception {
		return readSheetToMap(excelFile, sheetName, headerindex, 0);
	}

	public static List<Map<String, Object>> readSheetToMap(String excelFile, String sheetName, int headerindex,
			int columnIndex) throws Exception {
		List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
		try {
			List<Row> rowlist = new ArrayList<Row>();
			int numberOfColumns = 0;
			Workbook workbook = loadWorkbook(excelFile);
			Sheet sheet = loadSheet(workbook, sheetName);
			int lastnum1 = sheet.getLastRowNum();
			if (headerindex == 0) {
				Iterator<Row> itr = sheet.iterator();
				rowlist = Lists.newArrayList(itr);
				numberOfColumns = countNonEmptyColumns(sheet, 0, columnIndex);
			} else {
				numberOfColumns = countNonEmptyColumns(sheet, headerindex, columnIndex);
				int i = headerindex;
				while (i < lastnum1 + 1) {
					Row item = sheet.getRow(i);
					if (item != null)
						rowlist.add(item);
					i++;
				}
			}
			rows = buildRowsToMap(workbook, rowlist, numberOfColumns, headerindex, columnIndex);
		} catch (NullPointerException e) {
			LOGGER.error("Incomplete excel read: " + excelFile, e);
			return rows;
		} catch (Exception e) {
			LOGGER.error("Failed to read from excel: " + excelFile, e);
			throw new Exception("Failed to read from excel: " + excelFile);
		}
		return rows;
	}

	public static String readCell(String excelFile, String sheetName, int rowNo, int columnNo) throws Exception {
		try {
			Workbook workbook = loadWorkbook(excelFile);
			if (workbook != null) {
				Sheet sheet = loadSheet(workbook, sheetName);
				if (sheet != null) {
					Row row = sheet.getRow(rowNo);
					if (row != null) {
						Cell cell = row.getCell(columnNo);
						Object o = objectFrom(workbook, cell);
						if (o == null)
							return StringUtils.EMPTY;
						return String.valueOf(o);
					}
				} else {
					LOGGER.error(String.format("Sheet %s not found in workbook %s.", sheetName,
							FilenameUtils.getBaseName(excelFile)));
					throw new Exception(String.format("Sheet %s not found in workbook %s.", sheetName,
							FilenameUtils.getBaseName(excelFile)));
				}
			} else {
				LOGGER.error("Incorrect workbook name " + FilenameUtils.getBaseName(excelFile));
				throw new Exception("Incorrect workbook name " + FilenameUtils.getBaseName(excelFile));
			}
		} catch (NullPointerException e) {
			LOGGER.error("Incomplete excel read: " + excelFile, e);
			return StringUtils.EMPTY;
		} catch (Exception e) {
			LOGGER.error("Failed to read from excel: " + excelFile, e);
			throw new Exception("Failed to read from excel: " + excelFile);
		}
		return StringUtils.EMPTY;
	}

	private static List<Map<String, Object>> buildRowsToMap(Workbook workbook, List<Row> rows, int numberOfColumns,
			int headerindex, int columnIndex) {
		List<String> headers = buildHeaderList(rows.get(0), workbook, numberOfColumns);
		return Lists.newArrayList(
				FluentIterable.from(rows.subList(1, rows.size())).filter(Predicates.not(isEmptyRow(columnIndex)))
						.transform(convertRowToMap(headers, workbook, numberOfColumns)));
	}

	private static List<String> buildHeaderList(Row row, Workbook workbook, int numberOfColumns) {
		List<String> headers = Lists.newArrayList();
		for (int column = 0; column < numberOfColumns; column++) {
			Cell cell = row.getCell(column);
			headers.add(StringUtils.trimToEmpty(String.valueOf(objectFrom(workbook, cell))));
		}
		return headers;
	}

	private static Function<Row, Map<String, Object>> convertRowToMap(List<String> headers, Workbook workbook,
			int numberOfColumns) {
		return new Function<Row, Map<String, Object>>() {
			@Override
			public Map<String, Object> apply(Row row) {
				Map<String, Object> rowMap = Maps.newHashMap();
				for (int column = 0; column < numberOfColumns; column++) {
					Cell cell = row.getCell(column);
					rowMap.put(headers.get(column), objectFrom(workbook, cell));
				}
				return rowMap;
			}
		};
	}

	private static Predicate<Row> isEmptyRow(int columnIndex) {
		return new Predicate<Row>() {
			@Override
			public boolean apply(Row row) {
				try {
					int colnum = firstEmptyCellPosition(row, columnIndex);
//					Cell firstCell = row.getCell(0);
//					return firstCell == null || firstCell.getCellType() ==Cell.CELL_TYPE_BLANK;
					return colnum == 0;
				} catch (Exception e) {
					LOGGER.error("Unable to validate if row is empty", e);
					return false;
				}
			}
		};
	}

	private static Object objectFrom(Workbook workbook, Cell cell) {
		Object cellValue = "";
		if (cell != null) {
			try {
				if (cell.getCellType() == CellType.STRING) {
					cellValue = cell.getRichStringCellValue().getString();
				} else if (cell.getCellType() == CellType.NUMERIC) {
					cellValue = getNumericCellValue(cell);
				} else if (cell.getCellType() == CellType.BOOLEAN) {
					cellValue = cell.getBooleanCellValue();
				} else if (cell.getCellType() == CellType.FORMULA) {
					cellValue = evaluateCellFormula(workbook, cell);
				}
			} catch (Exception e) {
				LOGGER.error("Unable to validate cell type and get value", e);
			}
		} else {
			cellValue = "";
		}
		return cellValue;
	}

	private static Workbook loadWorkbook(String excelFile) {
		FileInputStream fis = null;
		try {
			File excel = new File(excelFile);
			fis = new FileInputStream(excel);
			return new HSSFWorkbook(fis);
		} catch (Exception e) {
			LOGGER.error("Error reading excel " + excelFile, e);
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					LOGGER.error("Error while closing input stream for file " + excelFile, e);
				}
			}
		}
		return null;
	}

	private static Sheet loadSheet(Workbook workbook, String sheetName) {
		return buildSheetForXLSX(workbook, sheetName);
	}

	private static Sheet buildSheetForXLSX(Workbook workbook, String sheetName) {
		if (StringUtils.isNotEmpty(sheetName)) {
			return (HSSFSheet) workbook.getSheet(sheetName);
		} else {
			return (HSSFSheet) workbook.getSheetAt(0);
		}
	}

	private static int countNonEmptyColumns(Sheet sheet, int headindex, int columnIndex) {
		Row firstRow = null;
		try {
			firstRow = sheet.getRow(headindex);
		} catch (Exception e) {
			LOGGER.error("Unable to get the non-empty cell count", e);
		}
		return firstEmptyCellPosition(firstRow, columnIndex);
	}

	private static int firstEmptyCellPosition(Row cells, int columnIndex) {
		int columnCount = 0;
		try {
			for (int c = columnIndex; c < cells.getLastCellNum(); c++) {
//			for (Cell cell : cells.get) {
				Cell cell = cells.getCell(c);
				if (cell != null && cell.getCellType() == CellType.BLANK) {
					break;
				}
				columnCount++;
			}
		} catch (Exception e) {
			LOGGER.error("Unable to get the column count", e);
		}
		return columnCount;
	}

	private static Object evaluateCellFormula(Workbook workbook, Cell cell) {
		FormulaEvaluator evaluator = null;
		CellValue cellValue = null;
		Object result = null;
		try {
			evaluator = workbook.getCreationHelper().createFormulaEvaluator();
			cellValue = evaluator.evaluate(cell);
			if (cellValue.getCellType() == CellType.BOOLEAN) {
				result = cellValue.getBooleanValue();
			} else if (cellValue.getCellType() == CellType.NUMERIC) {
				result = cellValue.getNumberValue();
			} else if (cellValue.getCellType() == CellType.STRING) {
				result = cellValue.getStringValue();
			}
		} catch (Exception e) {
			LOGGER.error(String.format("Unable to evaluate cell formula. Row- %s, Column- %s. Formula- %s.",
					String.valueOf(cell.getRowIndex()), String.valueOf(cell.getColumnIndex()), cell.getCellFormula()));
		}
		return result;
	}

	private static Object getNumericCellValue(Cell cell) {
		Object cellValue = null;
		try {
			if (DateUtil.isCellDateFormatted(cell)) {
				cellValue = new Date(cell.getDateCellValue().getTime());
			} else {
				DataFormatter df = new DataFormatter();
				cellValue = df.formatCellValue(cell);
				// cellValue = cellValue.toString().replace(",", "");
//				cellValue = cell.getNumericCellValue();
			}
		} catch (Exception e) {
			LOGGER.error("Unable to get the numeric value from cell", e);
		}
		return cellValue;
	}

}
