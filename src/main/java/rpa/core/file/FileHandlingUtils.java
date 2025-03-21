package rpa.core.file;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.compress.utils.FileNameUtils;
import org.apache.commons.io.FileExistsException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.AgeFileFilter;
import org.apache.commons.io.filefilter.AndFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import botrix.internal.logging.LoggerFactory;
import rpa.core.exceptions.BishopRuleViolationException;
import rpa.core.exceptions.BishopRuntimeException;

/**
 * Utility class to manage and handle file I/O and related activities
 * 
 * @author aishvaryakapoor
 *
 */
public class FileHandlingUtils {
	private static final Logger logger = LoggerFactory.getLogger(FileHandlingUtils.class);

	public static final String MIME_TYPE_TEXT_PLAIN = "text/plain";
	public static final String MIME_TYPE_TEXT_CSS = "text/css";
	public static final String MIME_TYPE_TEXT_CSV = "text/csv";
	public static final String MIME_TYPE_TEXT_HTML = "text/html";
	public static final String MIME_TYPE_TEXT_CALENDAR = "text/calendar";
	public static final String MIME_TYPE_TEXT_JAVASCRIPT = "text/javascript";

	public static final String MIME_TYPE_AUDIO_AAC = "audio/aac";
	public static final String MIME_TYPE_AUDIO_MIDI_AUDIO_X_MIDI = "audio/midi audio/x-midi";
	public static final String MIME_TYPE_AUDIO_MPEG = "audio/mpeg";
	public static final String MIME_TYPE_AUDIO_OGG = "audio/ogg";
	public static final String MIME_TYPE_AUDIO_OPUS = "audio/opus";
	public static final String MIME_TYPE_AUDIO_WAV = "audio/wav";
	public static final String MIME_TYPE_AUDIO_WEBM = "audio/webm";

	public static final String MIME_TYPE_VIDEO_X_MSVIDEO = "video/x-msvideo";
	public static final String MIME_TYPE_VIDEO_MPEG = "video/mpeg";
	public static final String MIME_TYPE_VIDEO_OGG = "video/ogg";
	public static final String MIME_TYPE_VIDEO_MP2T = "video/mp2t";
	public static final String MIME_TYPE_VIDEO_WEBM = "video/webm";
	public static final String MIME_TYPE_VIDEO_3GPP = "video/3gpp";
	public static final String MIME_TYPE_VIDEO_3GPP2 = "video/3gpp2";
	public static final String MIME_TYPE_VIDEO_MP4 = "video/mp4";

	public static final String MIME_TYPE_IMAGE_BMP = "image/bmp";
	public static final String MIME_TYPE_IMAGE_GIF = "image/gif";
	public static final String MIME_TYPE_IMAGE_VND_MICROSOFT_ICON = "image/vnd.microsoft.icon";
	public static final String MIME_TYPE_IMAGE_JPEG = "image/jpeg";
	public static final String MIME_TYPE_IMAGE_PNG = "image/png";
	public static final String MIME_TYPE_IMAGE_SVG_XML = "image/svg+xml";
	public static final String MIME_TYPE_IMAGE_TIFF = "image/tiff";
	public static final String MIME_TYPE_IMAGE_WEBP = "image/webp";

	public static final String MIME_TYPE_FONT_OTF = "font/otf";
	public static final String MIME_TYPE_FONT_TTF = "font/ttf";
	public static final String MIME_TYPE_FONT_WOFF = "font/woff";
	public static final String MIME_TYPE_FONT_WOFF2 = "font/woff2";

	public static final String MIME_TYPE_APPLICATION_OCTET_STREAM = "application/octet-stream";
	public static final String MIME_TYPE_APPLICATION_MSWORD = "application/msword";
	public static final String MIME_TYPE_APPLICATION_EPUB_ZIP = "application/epub+zip";
	public static final String MIME_TYPE_APPLICATION_GZIP = "application/gzip";
	public static final String MIME_TYPE_APPLICATION_JAVA_ARCHIVE = "application/java-archive";
	public static final String MIME_TYPE_APPLICATION_JSON = "application/json";
	public static final String MIME_TYPE_APPLICATION_LD_JSON = "application/ld+json";
	public static final String MIME_TYPE_APPLICATION_PDF = "application/pdf";
	public static final String MIME_TYPE_APPLICATION_PHP = "application/php";
	public static final String MIME_TYPE_APPLICATION_RTF = "application/rtf";
	public static final String MIME_TYPE_APPLICATION_OGG = "application/ogg";
	public static final String MIME_TYPE_APPLICATION_XHTML_XML = "application/xhtml+xml";
	public static final String MIME_TYPE_APPLICATION_XML = "application/xml";
	public static final String MIME_TYPE_APPLICATION_ZIP = "application/zip";

	public static final String MIME_TYPE_APPLICATION_X_ABIWORD = "application/x-abiword";
	public static final String MIME_TYPE_APPLICATION_X_FREEARC = "application/x-freearc";
	public static final String MIME_TYPE_APPLICATION_X_BZIP = "application/x-bzip";
	public static final String MIME_TYPE_APPLICATION_X_BZIP2 = "application/x-bzip2";
	public static final String MIME_TYPE_APPLICATION_X_CSH = "application/x-csh";
	public static final String MIME_TYPE_APPLICATION_X_RAR_COMPRESSED = "application/x-rar-compressed";
	public static final String MIME_TYPE_APPLICATION_X_SH = "application/x-sh";
	public static final String MIME_TYPE_APPLICATION_X_7Z_COMPRESSED = "application/x-7z-compressed";
	public static final String MIME_TYPE_APPLICATION_X_SHOCKWAVE_FLASH = "application/x-shockwave-flash";
	public static final String MIME_TYPE_APPLICATION_X_TAR = "application/x-tar";

	public static final String MIME_TYPE_APPLICATION_VND_VISIO = "application/vnd.visio";
	public static final String MIME_TYPE_APPLICATION_VND_APPLE_INSTALLER_XML = "application/vnd.apple.installer+xml";
	public static final String MIME_TYPE_APPLICATION_VND_OASIS_OPENDOCUMENT_PRESENTATION = "application/vnd.oasis.opendocument.presentation";
	public static final String MIME_TYPE_APPLICATION_VND_OASIS_OPENDOCUMENT_SPREADSHEET = "application/vnd.oasis.opendocument.spreadsheet";
	public static final String MIME_TYPE_APPLICATION_VND_OASIS_OPENDOCUMENT_TEXT = "application/vnd.oasis.opendocument.text";
	public static final String MIME_TYPE_APPLICATION_VND_OPENXMLFORMATS_OFFICEDOCUMENT_WORDPROCESSINGML_DOCUMENT = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
	public static final String MIME_TYPE_APPLICATION_VND_MS_FONTOBJECT = "application/vnd.ms-fontobject";
	public static final String MIME_TYPE_APPLICATION_VND_AMAZON_EBOOK = "application/vnd.amazon.ebook";
	public static final String MIME_TYPE_APPLICATION_VND_MOZILLA_XUL_XML = "application/vnd.mozilla.xul+xml";
	public static final String MIME_TYPE_APPLICATION_VND_MS_EXCEL = "application/vnd.ms-excel";
	public static final String MIME_TYPE_APPLICATION_VND_OPENXMLFORMATS_OFFICEDOCUMENT_SPREADSHEETML_SHEET = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
	public static final String MIME_TYPE_APPLICATION_VND_MS_POWERPOINT = "application/vnd.ms-powerpoint";
	public static final String MIME_TYPE_APPLICATION_VND_OPENXMLFORMATS_OFFICEDOCUMENT_PRESENTATIONML_PRESENTATION = "application/vnd.openxmlformats-officedocument.presentationml.presentation";

	public static Collection<File> getListOfAllFiles(String directoryName, String pattern) {
		return getListOfAllFiles(directoryName, pattern, IOCase.SENSITIVE);
	}

	/**
	 * Returns a list of all files matching the @param pattern in the directory and
	 * all sub-directories Examples - When pattern is *.* then returns list of all
	 * files with any name and file type. If there are files without extension they
	 * will not be returned When pattern is * then returns any file in the directory
	 * When pattern is *.pdf then returns any file with extension pdf (case
	 * sensitive) When pattern is report_*.csv then returns all csv files whose name
	 * start with "report"
	 * 
	 * @param directoryName
	 * @param pattern
	 * @return
	 */
	public static Collection<File> getListOfAllFiles(String directoryName, String pattern,
			final IOCase caseSensitivity) {
		try {
			File directory = new File(directoryName);
			return FileUtils.listFiles(directory, new WildcardFileFilter(pattern, caseSensitivity),
					TrueFileFilter.INSTANCE);
		} catch (Exception e) {
			return List.of();
		}
	}

	public static String readResourceAsString(String path) {
		try {
			return new String(Files.readAllBytes(getResourceAsFile(path).toPath()));
		} catch (Exception e) {
			throw new BishopRuntimeException("Failed to read resource file", e);
		}
	}

	public static File getResourceAsFile(String path) {
		try {
			URL resource = FileHandlingUtils.class.getClassLoader().getResource(path);
			return new File(resource.toURI());
		} catch (Exception e) {
			throw new BishopRuntimeException("Failed to get resource file", e);
		}
	}

	/**
	 * Returns a list of all files matching the @param pattern in the directory and
	 * all sub-directories Examples - When pattern is *.* then returns list of all
	 * files with any name and file type. If there are files without extension they
	 * will not be returned When pattern is * then returns any file in the directory
	 * When pattern is *.pdf then returns any file with extension pdf (case
	 * sensitive) When pattern is report_*.csv then returns all csv files whose name
	 * start with "report" and Date for the Date reference followed by boolean flag
	 * as 'true' to accept older files then date or 'false' for newer files after
	 * the date
	 * 
	 * @param directoryName
	 * @param pattern
	 * @param date
	 * @param acceptOlder
	 * @return
	 */
	public static Collection<File> getListOfAllFiles(String directoryName, String pattern, Date date,
			Boolean acceptOlder) {
		try {
			File directory = new File(directoryName);
			AgeFileFilter ageFilter = new AgeFileFilter(date.getTime(), acceptOlder);
			WildcardFileFilter wildcardFilter = new WildcardFileFilter(pattern);
			return FileUtils.listFiles(directory, new AndFileFilter(ageFilter, wildcardFilter),
					TrueFileFilter.INSTANCE);
		} catch (Exception e) {
			return CollectionUtils.EMPTY_COLLECTION;
		}
	}

	/**
	 * Cleans @param dirPath
	 * 
	 * @param dirPath
	 * @throws BishopRuleViolationException - if unable to clean directory
	 *                                      Underlying exceptions
	 * @throws IOException                  in case cleaning is unsuccessful
	 * @throws IllegalArgumentException     if {@code directory} does not exist or
	 *                                      is not a directory
	 * 
	 */
	public static void cleanDirectory(String dirPath) throws BishopRuleViolationException {
		cleanDirectory(dirPath, true);
	}

	public static void cleanDirectory(String dirPath, boolean throwEx) throws BishopRuleViolationException {
		try {
			File dir = new File(dirPath);
			if (dir != null && dir.isDirectory()) {
				FileUtils.cleanDirectory(new File(dirPath));
//				logger.info("Cleaned directory " + dirPath);
			}
//			else 
//				logger.info(dirPath + " does not exists.");
		} catch (Exception e) {
			logger.error("Failed to clean directory " + dirPath, e);
			if (throwEx)
				throw new BishopRuleViolationException("Failed to clean directory " + dirPath, e);
		}
	}

	public static void moveFile(String from, String to) {
		moveFile(new File(from), new File(to));
	}

	/**
	 * Moves the file from @param from to @param to
	 * 
	 * @param from
	 * @param to
	 */
	public static void moveFile(File from, File to) {
		try {
			FileUtils.moveFile(from, to);
			logger.info(String.format("Move %s to %s", from, to));
		} catch (FileExistsException e) {
			try {
				logger.error("File already exists in destination. Trying to replace destination file");
				Files.move(from.toPath(), to.toPath(), REPLACE_EXISTING);
				logger.info(String.format("Move %s to %s", from, to));
			} catch (IOException e1) {
				logger.error(String.format("Failed to move %s to %s", from, to));
			}
		} catch (IOException e) {
			logger.error(String.format("Failed to move %s to %s.", from, to));
		}
	}

	/**
	 * Unzip a zip file provided by @param zipfilepath
	 * 
	 * @param zipfilepath
	 * @param output      - output directory for the unzipped files
	 * @throws Exception
	 */
	public static void unzip(String zipfilepath, String output) throws Exception {
		try {
			logger.debug(String.format("Unzipping file for: %s", zipfilepath));
			net.lingala.zip4j.core.ZipFile zipFile = new net.lingala.zip4j.core.ZipFile(zipfilepath);
			zipFile.extractAll(output);
		} catch (Exception e) {
			throw new BishopRuleViolationException("Failed to unzip folder " + zipfilepath, e);
		}
	}

	/**
	 * Makes a directory, including any necessary but nonexistent parent
	 * directories. If a file already exists with specified name but it is not a
	 * directory then an IOException is thrown. If the directory cannot be created
	 * (or does not already exist) then an IOException is thrown.
	 *
	 * @param directory directory to create, must not be {@code null}
	 * @throws NullPointerException if the directory is {@code null}
	 * @throws IOException          if the directory cannot be created or the file
	 *                              already exists but is not a directory
	 */
	public static void mkdir(String path) {
		try {
			FileUtils.forceMkdir(new File(path));
		} catch (IOException e) {
			logger.error("", e);
		} catch (Exception e) {
			logger.error("", e);
		}
	}

	public static String path(String path) {
		return Paths.get(path).toString();
	}

	/**
	 * Copies a whole directory to a new location preserving the file dates.
	 * <p>
	 * This method copies the specified directory and all its child directories and
	 * files to the specified destination. The destination is the new location and
	 * name of the directory.
	 * <p>
	 * The destination directory is created if it does not exist. If the destination
	 * directory did exist, then this method merges the source with the destination,
	 * with the source taking precedence.
	 * <p>
	 * <strong>Note:</strong> This method tries to preserve the files' last modified
	 * date/times using {@link File#setLastModified(long)}, however it is not
	 * guaranteed that those operations will succeed. If the modification operation
	 * fails, no indication is provided.
	 *
	 * @param srcDir  an existing directory to copy, must not be {@code null}
	 * @param destDir the new directory, must not be {@code null}
	 *
	 * @throws NullPointerException if source or destination is {@code null}
	 * @throws IOException          if source or destination is invalid
	 * @throws IOException          if an IO error occurs during copying
	 */
	public static void copyDirectory(String from, String to) {
		try {
			FileUtils.copyDirectory(new File(from), new File(to));
		} catch (Exception e) {
			logger.error("", e);
		}
	}

	public static void copyFile(String from, String to) {
		try {
			FileUtils.copyFile(new File(from), new File(to));
		} catch (Exception e) {
			logger.error("", e);
		}
	}

	/**
	 * Unzip a zip file provided by @param zipfilepath
	 * 
	 * @param zipfilepath
	 * @param output      - output directory for the unzipped files
	 * @throws Exception
	 */
	public static void upzip(String zipfilepath, String output) throws Exception {
		String unzipfolder = zipfilepath;
		ZipFile zipFile = null;
		try {
			logger.info("Unzipping file for " + unzipfolder);
			FileHandlingUtils.mkdir(unzipfolder);
			Collection<File> zipFiles = FileHandlingUtils.getListOfAllFiles(zipfilepath, "*.zip");
			if (CollectionUtils.isNotEmpty(zipFiles)) {
				for (File file : zipFiles) {
					String zipfileName = FilenameUtils.getBaseName(file.getName());
					byte[] buffer = new byte[1024];
					zipFile = new ZipFile(file);
					Enumeration<? extends ZipEntry> entries = zipFile.entries();
					while (entries.hasMoreElements()) {
						try {
							ZipEntry entry = entries.nextElement();
							String subDir = StringUtils.substringAfter(FilenameUtils.getFullPath(entry.getName()),
									File.separator);
							String outpath = Paths.get(FilenameUtils.getFullPath(output + File.separator + subDir))
									.toString();
							FileHandlingUtils.mkdir(outpath);
							File newFile = new File(outpath, FilenameUtils.getName(entry.getName()));
							if (StringUtils.isBlank(FileNameUtils.getExtension(newFile.getAbsolutePath())))
								continue;
							FileOutputStream fos = null;
							InputStream inputStream = null;
							try {
								inputStream = zipFile.getInputStream(entry);
								fos = new FileOutputStream(newFile);
								int len;
								while ((len = inputStream.read(buffer)) > 0) {
									fos.write(buffer, 0, len);
								}

							} catch (Exception e) {
								logger.error("", e);
								throw new Exception(e);
							} finally {
								if (fos != null)
									fos.close();
								if (inputStream != null)
									inputStream.close();
							}
						} catch (Exception e) {
							System.out.println(e);
						}
					}

				}
			}
		} catch (Exception e) {
			logger.error("Failed to unzip folder " + unzipfolder, e);
			throw new Exception("Failed to unzip folder " + unzipfolder, e);
		} finally {
			if (zipFile != null)
				zipFile.close();
		}
	}

	/**
	 * Returns content type or MIME type of a file determined by it's extension
	 * 
	 * @param filename
	 * @return
	 */
	public static String identifyContentType(String filename) {
		String extention = FilenameUtils.getExtension(filename);
		String contentType = "";
		switch (extention) {
		case "aac":
			contentType = MIME_TYPE_AUDIO_AAC;
			break;
		case "abw":
			contentType = MIME_TYPE_APPLICATION_X_ABIWORD;
			break;
		case "arc":
			contentType = MIME_TYPE_APPLICATION_X_FREEARC;
			break;
		case "avi":
			contentType = MIME_TYPE_VIDEO_X_MSVIDEO;
			break;
		case "azw":
			contentType = MIME_TYPE_APPLICATION_VND_AMAZON_EBOOK;
			break;
		case "bin":
			contentType = MIME_TYPE_APPLICATION_OCTET_STREAM;
			break;
		case "bmp":
			contentType = MIME_TYPE_IMAGE_BMP;
			break;
		case "bz":
			contentType = MIME_TYPE_APPLICATION_X_BZIP;
			break;
		case "bz2":
			contentType = MIME_TYPE_APPLICATION_X_BZIP2;
			break;
		case "csh":
			contentType = MIME_TYPE_APPLICATION_X_CSH;
			break;
		case "css":
			contentType = MIME_TYPE_TEXT_CSS;
			break;
		case "csv":
			contentType = MIME_TYPE_TEXT_CSV;
			break;
		case "doc":
			contentType = MIME_TYPE_APPLICATION_MSWORD;
			break;
		case "docx":
			contentType = MIME_TYPE_APPLICATION_VND_OPENXMLFORMATS_OFFICEDOCUMENT_WORDPROCESSINGML_DOCUMENT;
			break;
		case "eot":
			contentType = MIME_TYPE_APPLICATION_VND_MS_FONTOBJECT;
			break;
		case "epub":
			contentType = MIME_TYPE_APPLICATION_EPUB_ZIP;
			break;
		case "gz":
			contentType = MIME_TYPE_APPLICATION_GZIP;
			break;
		case "gif":
			contentType = MIME_TYPE_IMAGE_GIF;
			break;
		case "htm":
		case "html":
			contentType = MIME_TYPE_TEXT_HTML;
			break;
		case "ico":
			contentType = MIME_TYPE_IMAGE_VND_MICROSOFT_ICON;
			break;
		case "ics":
			contentType = MIME_TYPE_TEXT_CALENDAR;
			break;
		case "jar":
			contentType = MIME_TYPE_APPLICATION_JAVA_ARCHIVE;
			break;
		case "jpeg":
		case "jpg":
			contentType = MIME_TYPE_IMAGE_JPEG;
			break;
		case "js":
		case "mjs":
			contentType = MIME_TYPE_TEXT_JAVASCRIPT;
			break;
		case "json":
			contentType = MIME_TYPE_APPLICATION_JSON;
			break;
		case "jsonld":
			contentType = MIME_TYPE_APPLICATION_LD_JSON;
			break;
		case "mid":
		case "midi":
			contentType = MIME_TYPE_AUDIO_MIDI_AUDIO_X_MIDI;
			break;
		case "mp3":
			contentType = MIME_TYPE_AUDIO_MPEG;
			break;
		case "mpeg":
			contentType = MIME_TYPE_VIDEO_MPEG;
			break;
		case "mpkg":
			contentType = MIME_TYPE_APPLICATION_VND_APPLE_INSTALLER_XML;
			break;
		case "odp":
			contentType = MIME_TYPE_APPLICATION_VND_OASIS_OPENDOCUMENT_PRESENTATION;
			break;
		case "ods":
			contentType = MIME_TYPE_APPLICATION_VND_OASIS_OPENDOCUMENT_SPREADSHEET;
			break;
		case "odt":
			contentType = MIME_TYPE_APPLICATION_VND_OASIS_OPENDOCUMENT_TEXT;
			break;
		case "oga":
			contentType = MIME_TYPE_AUDIO_OGG;
			break;
		case "ogv":
			contentType = MIME_TYPE_VIDEO_OGG;
			break;
		case "ogx":
			contentType = MIME_TYPE_APPLICATION_OGG;
			break;
		case "opus":
			contentType = MIME_TYPE_AUDIO_OPUS;
			break;
		case "otf":
			contentType = MIME_TYPE_FONT_OTF;
			break;
		case "png":
			contentType = MIME_TYPE_IMAGE_PNG;
			break;
		case "pdf":
			contentType = MIME_TYPE_APPLICATION_PDF;
			break;
		case "php":
			contentType = MIME_TYPE_APPLICATION_PHP;
			break;
		case "ppt":
			contentType = MIME_TYPE_APPLICATION_VND_MS_POWERPOINT;
			break;
		case "pptx":
			contentType = MIME_TYPE_APPLICATION_VND_OPENXMLFORMATS_OFFICEDOCUMENT_PRESENTATIONML_PRESENTATION;
			break;
		case "rar":
			contentType = MIME_TYPE_APPLICATION_X_RAR_COMPRESSED;
			break;
		case "rtf":
			contentType = MIME_TYPE_APPLICATION_RTF;
			break;
		case "sh":
			contentType = MIME_TYPE_APPLICATION_X_SH;
			break;
		case "svg":
			contentType = MIME_TYPE_IMAGE_SVG_XML;
			break;
		case "swf":
			contentType = MIME_TYPE_APPLICATION_X_SHOCKWAVE_FLASH;
			break;
		case "tar":
			contentType = MIME_TYPE_APPLICATION_X_TAR;
			break;
		case "tif":
		case "tiff":
			contentType = MIME_TYPE_IMAGE_TIFF;
			break;
		case "ts":
			contentType = MIME_TYPE_VIDEO_MP2T;
			break;
		case "ttf":
			contentType = MIME_TYPE_FONT_TTF;
			break;
		case "txt":
			contentType = MIME_TYPE_TEXT_PLAIN;
			break;
		case "vsd":
			contentType = MIME_TYPE_APPLICATION_VND_VISIO;
			break;
		case "wav":
			contentType = MIME_TYPE_AUDIO_WAV;
			break;
		case "weba":
			contentType = MIME_TYPE_AUDIO_WEBM;
			break;
		case "webm":
			contentType = MIME_TYPE_VIDEO_WEBM;
			break;
		case "webp":
			contentType = MIME_TYPE_IMAGE_WEBP;
			break;
		case "woff":
			contentType = MIME_TYPE_FONT_WOFF;
			break;
		case "woff2":
			contentType = MIME_TYPE_FONT_WOFF2;
			break;
		case "xhtml":
			contentType = MIME_TYPE_APPLICATION_XHTML_XML;
			break;
		case "xls":
			contentType = MIME_TYPE_APPLICATION_VND_MS_EXCEL;
			break;
		case "xlsx":
			contentType = MIME_TYPE_APPLICATION_VND_OPENXMLFORMATS_OFFICEDOCUMENT_SPREADSHEETML_SHEET;
			break;
		case "xml":
			contentType = MIME_TYPE_APPLICATION_XML;
			break;
		case "xul":
			contentType = MIME_TYPE_APPLICATION_VND_MOZILLA_XUL_XML;
			break;
		case "zip":
			contentType = MIME_TYPE_APPLICATION_ZIP;
			break;
		case "3gp":
			contentType = MIME_TYPE_VIDEO_3GPP;
			break;
		case "3g2":
			contentType = MIME_TYPE_VIDEO_3GPP2;
			break;
		case "7z":
			contentType = MIME_TYPE_APPLICATION_X_7Z_COMPRESSED;
			break;
		case "mp4":
			contentType = MIME_TYPE_VIDEO_MP4;
			break;
		}
		return contentType;
	}

	/**
	 * Returns a windows safe directory path to avoid creating unsupported file
	 * names.
	 * 
	 * @param str
	 * @return
	 */
	public static String safeDirName(String str) {
		if (StringUtils.isBlank(str))
			return StringUtils.EMPTY;
		else
			return str.replaceAll("[^\\d\\sa-zA-Z._\\-\\(\\)]", "").trim();
	}

	/**
	 * Converts List<List<String>> to List<String[]>
	 * 
	 * @return
	 */
	public static List<String[]> convertListOfStringListToListOfArray(List<List<String>> listOfListOfString) {
		List<String[]> listOfArrayOfString = new ArrayList<>();

		for (List<String> listOfString : listOfListOfString) {
			String[] arrayOfString = new String[listOfString.size()];

			Object[] arrayOfObjects = listOfString.toArray();
			int i = 0;
			for (Object obj : arrayOfObjects) {
				arrayOfString[i++] = (String) obj;
			}

			listOfArrayOfString.add(arrayOfString);
		}

		return listOfArrayOfString;
	}

	/**
	 * Returns a list of all files matching the @param pattern in the directory and
	 * all sub-directories and which are older than specified time in minutes
	 * Examples - When pattern is *.* then returns list of all files with any name
	 * and file type. If there are files without extension they will not be returned
	 * When pattern is * then returns any file in the directory When pattern is
	 * *.pdf then returns any file with extension pdf (case sensitive) When pattern
	 * is report_*.csv then returns all csv files whose name start with "report"
	 * 
	 * @param directoryName
	 * @param pattern
	 * @param olderThanTimeMinutes - If 10 provided here, files which have last
	 *                             modified before 10 minutes will be returned
	 * @return
	 */
	public static Collection<File> getListOfAllFiles(String directoryName, String pattern, int olderThanTimeMinutes) {
		try {
			Calendar date = Calendar.getInstance();
			long t = date.getTimeInMillis();
			Date dateOlder = new Date(t - (60000 * olderThanTimeMinutes));

			Collection<File> files = getListOfAllFiles(directoryName, pattern);
			for (File file : files) {
				if (FileUtils.isFileNewer(file, dateOlder)) {
					files.remove(file);
				}
			}
			return files;
		} catch (Exception e) {
			return CollectionUtils.EMPTY_COLLECTION;
		}
	}
}
