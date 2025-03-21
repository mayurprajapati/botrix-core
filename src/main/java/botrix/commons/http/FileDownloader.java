package botrix.commons.http;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import lombok.SneakyThrows;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileDownloader {
	/**
	 * Downloads a file from the given URL and saves it to the specified folder with
	 * the original filename.
	 *
	 * @param fileUrl    The URL of the file to download.
	 * @param folderPath The folder path where the file will be saved.
	 * @throws IOException If an I/O error occurs during the file download or save
	 *                     process.
	 */
	public static File downloadFileAndSaveToFolder(String fileUrl, String folderPath) throws IOException {
		Response response = RestAssured.given().when().get(fileUrl);

		String fileName = extractFileNameFromUrl(fileUrl);
		String filePath = Paths.get(folderPath, fileName).toString();

		File file = new File(filePath);
		FileUtils.copyInputStreamToFile(response.getBody().asInputStream(), file);
		return file;
	}

	/**
	 * Downloads a file from the given URL and saves it to the specified path with
	 * the given filename.
	 *
	 * @param fileUrl     The URL of the file to download.
	 * @param destination The full path where the file will be saved, including the
	 *                    filename.
	 * @throws IOException If an I/O error occurs during the file download or save
	 *                     process.
	 */
	public static void downloadFileAndSaveToPath(String fileUrl, String destination) throws IOException {
		Response response = RestAssured.given().when().get(fileUrl);

		FileUtils.copyInputStreamToFile(response.getBody().asInputStream(), new File(destination));
	}

	/**
	 * Extracts the filename from a URL.
	 *
	 * @param url The URL from which to extract the filename.
	 * @return The filename extracted from the URL.
	 * @throws URISyntaxException
	 */
	@SneakyThrows
	private static String extractFileNameFromUrl(String url) {
		URI uri = new URI(url);
		String[] split = uri.getPath().split("/");
		String path = split[split.length - 1];
		return path;
	}
}
