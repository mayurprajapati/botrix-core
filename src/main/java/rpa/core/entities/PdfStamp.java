package rpa.core.entities;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.slf4j.Logger;

import botrix.internal.logging.LoggerFactory;
import rpa.core.file.FileHandlingUtils;

public class PdfStamp {
	private static Logger LOGGER = LoggerFactory.getLogger(PdfStamp.class);
	public static final double STAMP_SCALE = 0.30;
	public static final int WHITE_PIXEL_VALUE_LIMIT = 250;

	public static void editStamp(String stampPath, String targetStampPath, Map<String, String> stampParams)
			throws Exception {
		Set<String> keys = stampParams.keySet();
		try {
			File stampImageDir = new File(new File(targetStampPath).getParent());
			if (stampImageDir.exists() && stampImageDir.isDirectory()) {
				FileUtils.cleanDirectory(stampImageDir);
			}
			PDDocument stampPdf = PDDocument.load(new File(stampPath));
			PDDocumentCatalog pdCatalog = stampPdf.getDocumentCatalog();
			PDAcroForm pdAcroForm = pdCatalog.getAcroForm();
			List<PDField> presentFields = pdAcroForm.getFields();
			for (PDField field : presentFields) {
				if (keys.contains(field.getFullyQualifiedName())) {
					field.setValue(stampParams.get(field.getFullyQualifiedName()));
					LOGGER.info(String.format("set value - '%s' in the field '%s'", field.getFullyQualifiedName(),
							stampParams.get(field.getFullyQualifiedName())));
				} else {
					LOGGER.info(String.format("No value was provided to set for the field - '%s'",
							field.getFullyQualifiedName()));
				}
			}
			FileHandlingUtils.mkdir(FilenameUtils.getFullPath(targetStampPath));
			stampPdf.save(targetStampPath);
		} catch (Exception e) {
			LOGGER.error("Stamp fields were NOT updated successfully.", e);
			throw new Exception("Stamp fields were NOT updated successfully.", e);
		}
	}

	/**
	 * creates a new pdf by the filename provided in the same path.
	 * 
	 * @param path
	 * @param fileName
	 * @return
	 * @throws Exception
	 */
	public static String imageToPDF(String path, String fileName, boolean exceptionToggle) throws Exception {
		String response = StringUtils.EMPTY;
		try {
			PDDocument document = new PDDocument();
			InputStream in = new FileInputStream(path);
			BufferedImage bimg = ImageIO.read(in);
			float width = bimg.getWidth();
			float height = bimg.getHeight();
			PDPage page = new PDPage(new PDRectangle(width, height));
			document.addPage(page);
			PDImageXObject img = PDImageXObject.createFromFile(path, document);
			PDPageContentStream contentStream = new PDPageContentStream(document, page);
			contentStream.drawImage(img, 0, 0);
			contentStream.close();
			in.close();
			document.save(FilenameUtils.getFullPath(path) + fileName + ".pdf");
			document.close();
			response = FilenameUtils.getFullPath(path) + fileName + ".pdf";
		} catch (Exception e) {
			LOGGER.error("Failed to Create Pdf with image ", e);
			if (exceptionToggle) {
				throw new Exception("Failed to Create Pdf with image ", e);
			}
		}
		return response;
	}


	public static void punchStampToDocument(String stampImage, File inputFile, String outputFilePath, double stampScale)
			throws Exception {
		int whiteLimit = 250;
		double borderFactor = 0.1;
		try {
			PDDocument inputDoc = PDDocument.load(inputFile);
			inputDoc.setAllSecurityToBeRemoved(true);
			PDFRenderer pdfRenderer = new PDFRenderer(inputDoc);
			PDPageTree pages = inputDoc.getPages();
			PDImageXObject pdImage = PDImageXObject.createFromFile(stampImage, inputDoc);
			LOGGER.info("pdImage Stamp size - {}x{}", pdImage.getWidth(), pdImage.getHeight());

			BufferedImage stamp = ImageIO.read(new File(stampImage));
			int stampWidth = (int) (stamp.getWidth() * stampScale);
			int stampHeight = (int) (stamp.getHeight() * stampScale);
			LOGGER.info("Scaled stamp Size is {}x{}", stampWidth, stampHeight);

			for (int i = 0; i < pages.getCount(); i++) {
				BufferedImage pageImgGrey = pdfRenderer.renderImage(i, 1.0f, ImageType.GRAY);
				Point stampPoint = checkBlockforEmptySpace(pageImgGrey, 0, 0, pageImgGrey.getWidth(),
						pageImgGrey.getHeight(), whiteLimit, stampWidth + ((int) (stampWidth * borderFactor)),
						stampHeight + ((int) (stampHeight * borderFactor)), stampScale);
				if (stampPoint != null) {
					PDPageContentStream contents = new PDPageContentStream(inputDoc, pages.get(i), true, false);
					int drawX = stampPoint.x + ((int) (stampWidth * borderFactor * stampScale * 0.5));
					int drawY = pageImgGrey.getHeight() - stampPoint.y - ((int) (pdImage.getHeight() * stampScale))
							+ ((int) (stampHeight * borderFactor * stampScale * 0.5));
					float stampX = (float) (pdImage.getWidth() * stampScale);
					float stampY = (float) (pdImage.getHeight() * stampScale);
					// LOGGER.info(String.format(
					// "Stamp Location Info for page %s -\ndrawX = %s \ndrawY = %s \nstampX = %s
					// \nstampY = %s ",
					// i + 1, drawX, drawY, stampX, stampY));
					contents.drawImage(pdImage, drawX, drawY, stampX, stampY);
					LOGGER.info("Stamp Added in page {}", i + 1);
					contents.close();
				} else {
					LOGGER.error("Not enough space to stamp in page {}", i + 1);
				}
			}
			FileHandlingUtils.mkdir(FilenameUtils.getFullPath(outputFilePath));
			inputDoc.save(outputFilePath);
			inputDoc.close();
		} catch (InvalidPasswordException e) {
			LOGGER.error("Document is password protected.", e);
			throw new Exception(
					String.format("Stamp was not added successfully in document - '%s'", inputFile.getAbsolutePath()),
					e);
		} catch (IOException e) {
			LOGGER.error("Error occured due to file handling while adding stamp.", e);
			throw new Exception(
					String.format("Stamp was not added successfully in document - '%s'", inputFile.getAbsolutePath()),
					e);
		} catch (Exception e) {
			LOGGER.error("Error occured due to file handling while adding stamp.", e);
			throw new Exception(
					String.format("Stamp was not added successfully in document - '%s'", inputFile.getAbsolutePath()),
					e);
		}
	}

	private static Point checkBlockforEmptySpace(BufferedImage pageImg, int startX, int startY, int endX, int endY,
			int whiteLimit, int stampWidth, int stampHeight, double stampScale) throws Exception {
		Point p = null;
		List<Point> blocks = new ArrayList<Point>();
		if ((endX - startX) >= stampWidth && (endY - startY) >= stampHeight) {
			// LOGGER.info("Calculating areas to check for empty space");
			blocks = getBlocks(startX, startY, endX, endY, stampWidth, stampHeight, 4);
			p = findEmptySpace(stampWidth, stampHeight, blocks, whiteLimit, pageImg);
		} else {
			LOGGER.error("Block size is smaller than stamp size");
			throw new Exception("Block size is smaller than stamp size");
		}
		return p;
	}

	private static Point findEmptySpace(int stampWidth, int stampHeight, List<Point> blocks, int whiteLimit,
			BufferedImage page) {
		for (Point point : blocks) {
			if (isEmptySpace((int) point.getX(), (int) point.getY(), stampWidth, stampHeight, whiteLimit, page)) {
				// LOGGER.info("Empty space found in page from point - {}", point);
				return point;
			}
		}
		return null;
	}

	private static List<Point> getBlocks(int startX, int startY, int endX, int endY, int stampWidth, int stampHeight,
			int pixelShift) {
		List<Point> blocks = new ArrayList<Point>();
		int x = startX;
		int y = startY;
		do {
			do {
				blocks.add(new Point(x, y));
				y = y + pixelShift;
			} while (y + stampHeight <= endY);
			y = startY;
			x = x + pixelShift;
		} while (x + stampWidth <= endX);
		return blocks;
	}

	public static File createPdfToImage(File stampPdf, int whiteLimit) throws Exception {
		File stampImage = null;
		try {
			if (stampPdf.exists()) {
				stampImage = new File(stampPdf.getParent() + File.separator + "stamp.png");
				deleteFileIfExistsAlready(stampImage);
				PDDocument stampPdDoc = PDDocument.load(stampPdf);
				PDFRenderer pdfRenderer = new PDFRenderer(stampPdDoc);
				BufferedImage stamp = pdfRenderer.renderImage(0, 1.0f, ImageType.RGB);
				Point startPoint = getStartPoint(stamp.getWidth(), stamp.getHeight(), whiteLimit, stamp);
				Point endPoint = getEndPoint(stamp.getWidth(), stamp.getHeight(), whiteLimit, stamp);
				if (startPoint != null && endPoint != null) {
					printStamp(startPoint, endPoint, stamp, stampImage);
				} else {
					LOGGER.error(String.format("Stamp was not captured correctly. Limit points found - '%s' and '%s'",
							startPoint, endPoint));
					throw new Exception(
							String.format("Stamp was not captured correctly. Limit points found - '%s' and '%s'",
									startPoint, endPoint));
				}
			} else {
				LOGGER.error(String.format("Source PDF is missing in location - '%s'", stampPdf.getAbsolutePath()));
			}
		} catch (Exception e) {
			LOGGER.error(String.format("PDF to image conversion failed for stamp in location - '%s'",
					stampPdf.getAbsolutePath()), e);
			throw new Exception(String.format("PDF to image conversion failed for stamp in location - '%s'",
					stampPdf.getAbsolutePath()), e);
		}
		return stampImage;
	}

	public static void deleteFileIfExistsAlready(File file) {
		if (file.exists()) {
			try {
				FileUtils.forceDelete(file);
			} catch (IOException e) {
				LOGGER.error(String.format("Unable to delete file - '%s'", file), e.getMessage());
			}
		}
	}

	private static Point getStartPoint(int width, int height, int whiteLimit, BufferedImage page) {
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				Color color = new Color(page.getRGB(x, y));
				if (color.getRed() <= whiteLimit && color.getGreen() <= whiteLimit && color.getBlue() <= whiteLimit) {
					return new Point(x, y);
				}
			}
		}
		return null;
	}

	private static Point getEndPoint(int width, int height, int whiteLimit, BufferedImage page) {
		for (int y = height - 1; y > 0; y--) {
			for (int x = width - 1; x > 0; x--) {
				Color color = new Color(page.getRGB(x, y));
				if (color.getRed() <= whiteLimit && color.getGreen() <= whiteLimit && color.getBlue() <= whiteLimit) {
					return new Point(x, y);
				}
			}
		}
		return null;
	}

	public static void printStamp(Point startPoint, Point endPoint, BufferedImage page, File outputfile)
			throws Exception {
		BufferedImage stamp;
		try {
			LOGGER.info(String.format("extracting stamp withing '%s' and '%s' points", startPoint, endPoint));
			stamp = page.getSubimage(Math.min(startPoint.x, endPoint.x), Math.min(startPoint.y, endPoint.y),
					Math.abs(startPoint.x - endPoint.x), Math.abs(startPoint.y - endPoint.y));
			exportImage(stamp, outputfile);
		} catch (Exception e) {
			LOGGER.error("Stamp print failed", e);
			throw new Exception("Stamp print failed", e);
		}
	}

	public static void exportImage(BufferedImage stamp, File outputfile) throws Exception {
		try {
			LOGGER.info("Printing BufferedImage Stamp of size - {}x{}", stamp.getWidth(), stamp.getHeight());
			ImageIO.write(stamp, "png", outputfile);
			LOGGER.info(String.format("File Saved at '%s'", outputfile.getAbsolutePath()));
		} catch (IOException e) {
			LOGGER.error("SubImage export failed for stamp", e);
			throw new Exception("SubImage export failed for stamp", e);
		}
	}

	public static boolean isEmptySpace(int pointX, int pointY, int stampWidth, int stampHeight, int whiteLimit,
			BufferedImage pageImg) {
		int limitX = pointX + stampWidth;
		int limity = pointY + stampHeight;
		int x = 0;
		int y = 0;
		try {
			for (x = pointX; x < limitX; x++) {
				for (y = pointY; y < limity; y++) {
					Color c = new Color(pageImg.getRGB(x, y));
					if (c.getRed() <= whiteLimit && c.getRed() <= whiteLimit && c.getRed() <= whiteLimit) {
						return false;
					}
				}
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			LOGGER.error(String.format("Unable to check area. '%s,%s'Coordinate out of page", x, y), e);
			return false;
		}
		return true;
	}

}