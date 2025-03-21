package rpa.core.entities;

import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;

import botrix.internal.logging.LoggerFactory;
import rpa.core.driver.G;
import rpa.core.driver.SystemProperties;
import rpa.core.file.FileHandlingUtils;
import rpa.core.file.ParseUtils;

public class Helper {

	private static Logger LOGGER = LoggerFactory.getLogger(Helper.class);
	private static String BUCKET_NAME = "";

	public static boolean iterateToNextPageIfFound(String xpath) {
		boolean iterate = true;
		try {
			G.driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
			WebElement btnNext = G.driver.findElement(By.xpath(xpath));
			if (btnNext != null && btnNext.isDisplayed()) {
				btnNext.click();
				G.wait.forPageToLoad();
				G.wait.sleep(5);
			} else {
				iterate = false;
			}
		} catch (Exception e) {
			iterate = false;
		} finally {
			G.driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		}
		return iterate;
	}

	public static boolean iterateToNextPageIfFoundAndEnabled(String xpath) {
		boolean iterate = true;
		try {
			if (G.wait.fluentForElement(xpath, "Next Page", 1) != null
					&& G.elements.object(xpath).isEnabled()) {
				G.button.click(xpath, "Next Page");
				G.wait.forPageToLoad();
				G.wait.sleep(5);
			} else {
				iterate = false;
			}
		} catch (Exception e) {
			iterate = false;
		}
		return iterate;
	}

	public static void waitForAttachmentsToDownload(String downloadLocation, int attachmentCnt) throws Exception {
		waitForAttachmentsToDownload(downloadLocation, attachmentCnt, 240);
	}

	public static void waitForAttachmentsToDownload(int attachmentCnt) throws Exception {
		waitForAttachmentsToDownload(attachmentCnt, 300);
	}

	public static void waitForAttachmentsToDownload(int attachmentCnt, int maxWaitSecs) throws Exception {
		waitForAttachmentsToDownload(SystemProperties.DEFAULT_DOWNLOAD_LOCATION, attachmentCnt, maxWaitSecs);
	}

	public static void waitForAttachmentsToDownload(String downloadLocation, int attachmentCnt, int maxWaitSecs)
			throws Exception {
		int actualDownloadedAttachmentsCnt = 0;
		boolean waitForAttachmentsToDownload = true;
		int secsToWait = 0;
		Collection<File> downloadedFiles = new ArrayList<>();
		Collection<File> ffPartDownloadedFiles = new ArrayList<>();
		Collection<File> gcPartDownloadedFiles = new ArrayList<>();
		LOGGER.info(String.format("%s files taking longer to download. Trying to wait upto %s seconds..",
				String.valueOf(attachmentCnt), String.valueOf(maxWaitSecs)));
		do {
			downloadedFiles = FileHandlingUtils.getListOfAllFiles(downloadLocation, "*");
			ffPartDownloadedFiles = FileHandlingUtils.getListOfAllFiles(downloadLocation, "*.part");
			gcPartDownloadedFiles = FileHandlingUtils.getListOfAllFiles(downloadLocation, "*.crdownload");
			actualDownloadedAttachmentsCnt = CollectionUtils.size(downloadedFiles);
			if (actualDownloadedAttachmentsCnt != attachmentCnt || !CollectionUtils.sizeIsEmpty(ffPartDownloadedFiles)
					|| !CollectionUtils.sizeIsEmpty(gcPartDownloadedFiles)) {
				waitForAttachmentsToDownload = true;
				LOGGER.debug("Files taking longer to download. Waiting for 2 secs...");
				secsToWait = secsToWait + 2;
				G.wait.sleep(2);
			} else {
				waitForAttachmentsToDownload = false;
				G.wait.sleep(1);
				LOGGER.info("All files have been downloaded");
			}
		} while (waitForAttachmentsToDownload && secsToWait <= maxWaitSecs);
		if (waitForAttachmentsToDownload) {
			LOGGER.error(String.format(
					"All files could not download completely. Waited for %s seconds. %s%s files in %s. %s part downloads. %sAll files %s",
					String.valueOf(maxWaitSecs), System.lineSeparator(), CollectionUtils.size(downloadedFiles),
					downloadLocation,
					(CollectionUtils.size(ffPartDownloadedFiles) + CollectionUtils.size(gcPartDownloadedFiles)),
					System.lineSeparator(), downloadedFiles));
			throw new Exception(String.format("All files could not download completely."));
		}
	}

	public static boolean waitForDownloadNootification(String oBtnTransferProgressBar, int waitSecs) throws Exception {
		return waitForFileUploadOnProgressbar(oBtnTransferProgressBar, waitSecs);
	}

	public static boolean waitForFileUploadOnProgressbar(String oBtnTransferProgressBar, int waitSecs)
			throws Exception {
		try {
			WebElement progressBar = null;
			int secsElapsed = 0;
			LOGGER.info("Waiting for file to upload upto seconds " + waitSecs);
			do {
				progressBar = G.wait.fluentForElement(oBtnTransferProgressBar, "Progress..", 1);
				G.wait.sleep(2);
				secsElapsed = secsElapsed + 2;
			} while (progressBar != null && secsElapsed <= waitSecs);

			if (progressBar != null) {
				LOGGER.error(String.format("Unable to upload file. Waited for %s secs to upload.",
						String.valueOf(waitSecs)));
				return false;
			}
			G.wait.sleep(5);
		} catch (StaleElementReferenceException e) {
		} catch (Exception e) {
			LOGGER.error("", e);
			return false;
		}
		LOGGER.info("File upload complete");
		return true;
	}

	public static String mergeAllPDFInOnePDF(String attachmentsDir) throws Exception {
		return mergeAllPDFInOnePDF(attachmentsDir, "References.pdf");
	}

	public static String mergeAllPDFInOnePDF(String attachmentsDir, String name) throws Exception {
		PDFMergerUtility ut = new PDFMergerUtility();
		covertAllImagesToPDF(attachmentsDir);
		List<File> allPDF = FileHandlingUtils.getListOfAllFiles(attachmentsDir, "*.*").stream()
				.filter(file -> file.getName().contains(".pdf") || file.getName().contains(".PDF"))
				.collect(Collectors.toList());
		Collections.sort(allPDF);
		if (CollectionUtils.size(allPDF) > 1) {
			LOGGER.info("Merging PDFs into one..");
			try {
				for (File file : allPDF) {
					ut.addSource(file);
				}
				ut.setDestinationFileName(attachmentsDir + "/" + name);
				ut.mergeDocuments(null);
			} catch (Exception e) {
				LOGGER.error("Failed to merge all pdf into one ", e);
				throw new Exception("Failed to merge all pdf into one " + e.getMessage());
			} finally {
				try {
					for (File file : allPDF) {
						FileUtils.forceDelete(file);
					}
				} catch (Exception e) {
					LOGGER.error("Failed to delete old pdf", e);
					throw new Exception("Failed to delete old pdf, exiting... " + e.getMessage());
				}
			}
		}
		return attachmentsDir;
	}

	public static void covertAllImagesToPDF(String directory) throws Exception {
		Collection<File> allImages = FileHandlingUtils.getListOfAllFiles(directory, "*.*");
		try {
			if (CollectionUtils.isNotEmpty(allImages)) {
				for (File file : allImages) {
					if (StringUtils.endsWithAny(StringUtils.lowerCase(file.getName()), "jpeg", "jpg", "png")) {
						imageToPDF(file.getAbsolutePath());
						FileUtils.deleteQuietly(file);
					}
				}
			} else {
				LOGGER.info(String.format("Directory %s is blank", directory));
			}
		} catch (Exception e) {
			LOGGER.error("Failed to convert all images to PDFs.", e);
			throw new Exception("Failed to convert all images to PDFs.", e);
		}
	}

	public static void imageToPDF(String imagePath) throws Exception {
		try {
			PDDocument document = new PDDocument();
			InputStream in = new FileInputStream(imagePath);
			BufferedImage bimg = ImageIO.read(in);
			float width = bimg.getWidth();
			float height = bimg.getHeight();
			PDPage page = new PDPage(new PDRectangle(width, height));
			document.addPage(page);
			PDImageXObject img = PDImageXObject.createFromFile(imagePath, document);
			PDPageContentStream contentStream = new PDPageContentStream(document, page);
			contentStream.drawImage(img, 0, 0);
			contentStream.close();
			in.close();
			document.save(FilenameUtils.getFullPath(imagePath) + "/" + FilenameUtils.getBaseName(imagePath) + ".pdf");
			document.close();
		} catch (Exception e) {
			LOGGER.error("Failed to convert image to PDF " + imagePath, e);
			throw new Exception("Failed to convert image to PDF " + imagePath, e);
		}
	}

	public static String[] getFirstAndLastName(String fullName) {
		String[] name = StringUtils.split(fullName);
		String firstName = fullName;
		String lastName = fullName;
		if (CollectionUtils.size(name) > 0) {
			firstName = name[0];
			if (CollectionUtils.size(name) > 1) {
				lastName = name[CollectionUtils.size(name) - 1];
			}
		}
		String[] nameSplit = new String[2];
		nameSplit[0] = firstName;
		nameSplit[1] = lastName;
		return nameSplit;
	}

	public static String getCloudScreenshotsPath() {
		return Paths.get("automation", "screenshots", G.executionMetrics.getFlowId(),
				G.executionMetrics.getMasterUuid(),
				FileHandlingUtils.safeDirName(G.executionMetrics.getObjectNumber()),
				rpa.core.metrics.Helper.createUUID()).toString().replace("\\", "/");
	}

	public static String getCloudAttachmentPath() {
		return Paths.get("search", "doc_search", "pdfs", "Project Documents").toString().replace("\\", "/");
	}

	public static String getLocalAttachmentPath(String module) {
		return Paths.get("",
				ParseUtils.cleanStrForPath(G.executionMetrics.getFlow().getProject()),
				ParseUtils.cleanStrForPath(module),
				ParseUtils.cleanStrForPath(G.executionMetrics.getObjectNumber()),
				G.executionMetrics.getMasterUuid()).toString();
	}

	/**
	 * Used to copy text into ClipBoard. Then it can be used using Ctrl+V keys.
	 * Should only be used for Windows apps.
	 * 
	 * @param text
	 */
	public static void setTextInClipboard(String text) {
		StringSelection selection = new StringSelection(text);
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		clipboard.setContents(selection, selection);
	}

	public static String getTextFromClipboard() throws HeadlessException, UnsupportedFlavorException, IOException {
		return (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
	}
}
