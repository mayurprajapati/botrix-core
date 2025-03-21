package rpa.core.utils;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import rpa.core.exceptions.BishopRuntimeException;

public class CSVUtils {

	private CSVUtils() {
	}

	public static void save(List<Map<?, ?>> rows, Path path) {
		save(rows, path.toFile());
	}

	public static void save(List<Map<?, ?>> rows, File file) {
		try {
			try (CSVPrinter csv = new CSVPrinter(new FileWriter(file), CSVFormat.EXCEL)) {
				Set<?> headers = rows.get(0).keySet();
				csv.printRecord(headers);

				for (Map<?, ?> row : rows.subList(0, rows.size())) {
					csv.printRecord(row.values());
				}
			}
		} catch (Exception e) {
			throw new BishopRuntimeException("Failed to save CSV", e);
		}
	}

}
