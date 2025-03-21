package rpa.core.file;

import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.multipdf.Splitter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;
import org.apache.pdfbox.pdmodel.interactive.action.PDAction;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionURI;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationLink;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDCheckBox;
import org.apache.pdfbox.pdmodel.interactive.form.PDChoice;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.apache.pdfbox.pdmodel.interactive.form.PDTextField;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.PDFTextStripperByArea;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;
import org.slf4j.Logger;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;

import botrix.internal.logging.LoggerFactory;
import rpa.core.driver.SystemProperties;
import rpa.core.exceptions.BishopRuleViolationException;

public class PDFUtils {
	private static Logger LOGGER = LoggerFactory.getLogger(PDFUtils.class);

	/**
	 * Returns content from a text based pdf file
	 * 
	 * @param pdf
	 * @return
	 * @throws IOException
	 */
	public static String getTextFromPDF(File pdf) throws IOException {
		return getTextFromPDF(pdf, false);
	}

	/**
	 * Returns content from a text based pdf file with sortByPosition as true/false
	 * 
	 * @param pdf , sortByPosition
	 * @return
	 * @throws IOException
	 */
	public static String getTextFromPDF(File pdf, boolean sortByPosition) throws IOException {
		PDDocument document = null;
		try {
			document = PDDocument.load(pdf);
		} catch (InvalidPasswordException e) {
			LOGGER.error("File is encrypted", e);
		} catch (IOException e) {
			LOGGER.error("Error in reading file", e);
		}
		PDFTextStripper pdfStripper = new PDFTextStripper();
		if (sortByPosition) {
			pdfStripper.setSortByPosition(sortByPosition);
		}

		String text = pdfStripper.getText(document);
		if (document != null) {
			document.close();
		}
		return text;
	}

	/**
	 * Returns content from a text based pdf file with sortByPosition as true/false
	 * 
	 * @param pdf , sortByPosition
	 * @return
	 * @throws IOException
	 */
	public static String getTextFromPDF(File pdf, int startPage, int endPage) throws IOException {
		PDDocument document = null;
		try {
			document = PDDocument.load(pdf);
		} catch (InvalidPasswordException e) {
			LOGGER.error("File is encrypted", e);
		} catch (IOException e) {
			LOGGER.error("Error in reading file", e);
		}
		PDFTextStripper pdfStripper = new PDFTextStripper();
		pdfStripper.setStartPage(startPage);
		pdfStripper.setEndPage(endPage);
		String text = pdfStripper.getText(document);
		if (document != null) {
			document.close();
		}
		return text;
	}

	/**
	 * This method returns the no of pages of pdf
	 * 
	 * @param filePath
	 * @return
	 * @throws IOException
	 */
	public static int getNoofPages(String filePath) throws IOException {
		PDDocument document = null;
		try {
			document = PDDocument.load(new File(filePath));
		} catch (InvalidPasswordException e) {
			LOGGER.error("File is encrypted", e);
		} catch (IOException e) {
			LOGGER.error("Error in reading file", e);
		}
		int pageCount = document.getNumberOfPages();
		if (document != null) {
			document.close();
		}
		return pageCount;
	}

	/**
	 * Returns all links embedded in a PDF file
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static Set<String> getAttachmentLinks(File file) throws IOException {
		Set<String> attachments = new HashSet<String>();
		PDDocument document = null;
		try {
			document = PDDocument.load(file);
			for (int page = 0; page < document.getNumberOfPages(); page++) {
				PDPage pdfpage = document.getPage(page);
				List<PDAnnotation> annotations = pdfpage.getAnnotations();

				for (int j = 0; j < annotations.size(); j++) {
					PDAnnotation annot = annotations.get(j);
					if (annot instanceof PDAnnotationLink) {
						PDAnnotationLink link = (PDAnnotationLink) annot;
						PDAction action = link.getAction();
						if (action instanceof PDActionURI) {
							PDActionURI uri = (PDActionURI) action;
							String url = uri.getURI();
							attachments.add(url);
						}
					}
				}
			}
		} catch (Exception e) {
			LOGGER.error("Error in getting attachment links", e);
		} finally {
			if (document != null)
				document.close();
		}
		return attachments;
	}

	/**
	 * Find out all the links embedded in a PDF with their visible text
	 * 
	 * @param file
	 * @return a hasmap wher @key is the text and @value is the url
	 * @throws IOException
	 */
	public static Map<String, String> getLinksWithTexts(File file) throws IOException {
		PDDocument doc = PDDocument.load(file);
		Map<String, String> links = new LinkedHashMap<String, String>();
		try {
			for (PDPage page : doc.getPages()) {
				PDFTextStripperByArea stripper = new PDFTextStripperByArea();
				List<PDAnnotation> annotations = page.getAnnotations();
				// first setup text extraction regions
				for (int j = 0; j < annotations.size(); j++) {
					PDAnnotation annot = annotations.get(j);
					if (annot instanceof PDAnnotationLink) {
						PDAnnotationLink link = (PDAnnotationLink) annot;
						PDRectangle rect = link.getRectangle();
						// need to reposition link rectangle to match text space
						float x = rect.getLowerLeftX();
						float y = rect.getUpperRightY();
						float width = rect.getWidth();
						float height = rect.getHeight();
						int rotation = page.getRotation();
						if (rotation == 0) {
							PDRectangle pageSize = page.getMediaBox();
							y = pageSize.getHeight() - y;
						} else if (rotation == 90) {
							// do nothing
						}

						Rectangle2D.Float awtRect = new Rectangle2D.Float(x, y, width, height);
						stripper.addRegion("" + j, awtRect);
					}
				}

				stripper.extractRegions(page);

				for (int j = 0; j < annotations.size(); j++) {
					PDAnnotation annot = annotations.get(j);
					if (annot instanceof PDAnnotationLink) {
						PDAnnotationLink link = (PDAnnotationLink) annot;
						PDAction action = link.getAction();
						String urlText = stripper.getTextForRegion("" + j);
						if (action instanceof PDActionURI) {
							PDActionURI uri = (PDActionURI) action;
							links.put(urlText.trim(), uri.getURI());
						}
					}
				}
			}
		} catch (Exception e) {
			LOGGER.error("Error in reading links present in PDF", e);
		} finally {
			doc.close();
		}
		return links;
	}

	/**
	 * Split a PDF into png images. The resultant images are stored in
	 * {user.dir}/pdfimages These images are only persisted for the time period of
	 * an execution and will be delete as soon as execution finishes. Result image
	 * files are named as "image-{PDF_NAME}-{PAGE_NO}.png"
	 * 
	 * @param pdf
	 * @return
	 * @throws Exception
	 */
	private static ArrayList<String> convertPdfToImages(File pdf) throws Exception {
		ArrayList<String> fileNames = new ArrayList<String>();
		try (final PDDocument document = PDDocument.load(pdf)) {
			PDFRenderer pdfRenderer = new PDFRenderer(document);
			String pdfName = pdf.getName().split("\\.")[0];
			String directoryName = Paths.get(System.getProperty("user.dir"), "pdfimages", pdfName).toString();
			File directory = new File(directoryName);
			if (!directory.exists()) {
				FileHandlingUtils.mkdir(directoryName);
			}
			for (int page = 0; page < document.getNumberOfPages(); ++page) {
				BufferedImage bim = pdfRenderer.renderImageWithDPI(page, 100, ImageType.RGB);
				String fileName = Paths.get(directoryName, "image-" + pdfName + "-" + page + ".png").toString();
				ImageIOUtil.writeImage(bim, fileName, 300);
				fileNames.add(fileName);
			}
			document.close();
		} catch (IOException e) {
			LOGGER.error("PDF {} was not read", pdf.getName(), e);
			throw new Exception(String.format("PDF {} was not read", pdf.getName()), e);
		}
		return fileNames;
	}

	/**
	 * This method flatten editable PDF and returns extracted text
	 * 
	 * @param filePath
	 * @return
	 * @throws IOException
	 */
	public static String getTextFromEditablePDF(String pdfPath, boolean sortByPosition) throws IOException {
		File pdf = new File(pdfPath);
		PDDocument document = null;
		String text = "";
		try {
			document = PDDocument.load(pdf);
			PDAcroForm acroForm = document.getDocumentCatalog().getAcroForm();
			if (acroForm != null) {
				acroForm.flatten();
				PDFTextStripper stripper = new PDFTextStripper();
				stripper.setSortByPosition(sortByPosition);
				text = stripper.getText(document);
			} else {
				throw new Exception("Input PDF is not editable.");
			}

		} catch (InvalidPasswordException e) {
			LOGGER.error("File is encrypted", e);
		} catch (IOException e) {
			LOGGER.error("Error in reading file", e);
		} catch (Exception e) {
			LOGGER.error("Error in reading file", e);
		} finally {
			if (document != null) {
				document.close();
			}
		}

		return text;
	}

	/**
	 * This method checks if a PDF is editable or not
	 */
	public static boolean isPdfEitable(String pdfPath) throws IOException {
		File pdf = new File(pdfPath);
		PDDocument document = null;
		try {
			document = PDDocument.load(pdf);
			PDAcroForm acroForm = document.getDocumentCatalog().getAcroForm();
			if (acroForm != null) {
				return true;
			} else {
				return false;
			}

		} catch (InvalidPasswordException e) {
			LOGGER.error("File is encrypted", e);
		} catch (IOException e) {
			LOGGER.error("Error in reading file", e);
		} finally {
			if (document != null) {
				document.close();
			}
		}

		return false;
	}

	/**
	 * Reads PDF form and extract all fields along with values
	 * 
	 * @param filePath
	 * @return
	 * @throws IOException
	 */
	public static String extractPdfFormDetails(String pdfPath) throws IOException {
		File pdf = new File(pdfPath);
		PDDocument document = null;
		String allFieldsWithValue = "";
		try {
			document = PDDocument.load(pdf);
			PDDocumentCatalog catalog = document.getDocumentCatalog();
			PDAcroForm form = catalog.getAcroForm();
			List<PDField> fields = form.getFields();

			for (Object field : fields) {
				if (field instanceof PDTextField) {
					PDTextField pdTextbox = (PDTextField) field;
					allFieldsWithValue = allFieldsWithValue + "PDTextBox " + pdTextbox.getFullyQualifiedName() + ": "
							+ pdTextbox.getValue() + "\n";
				} else if (field instanceof PDChoice) {
					PDChoice pdChoiceField = (PDChoice) field;
					allFieldsWithValue = allFieldsWithValue + "PDChoice Field " + pdChoiceField.getFullyQualifiedName()
							+ ": " + pdChoiceField.getValue() + "\n";
				} else if (field instanceof PDCheckBox) {
					PDCheckBox pdCheckbox = (PDCheckBox) field;
					allFieldsWithValue = allFieldsWithValue + "PDCheckbox Field " + pdCheckbox.getFullyQualifiedName()
							+ ": " + pdCheckbox.getValue() + "\n";
				} else {
					System.out.print(field);
					System.out.print(" = ");
					System.out.print(field.getClass());
					System.out.println();
				}
			}

		} catch (InvalidPasswordException e) {
			LOGGER.error("File is encrypted", e);
		} catch (IOException e) {
			LOGGER.error("Error in reading file", e);
		} finally {
			if (document != null) {
				document.close();
			}
		}

		return allFieldsWithValue;
	}

	/**
	 * Extract PDF table into CSV using tabula.
	 * 
	 * @return
	 * @throws IOException
	 * @throws BishopRuleViolationException
	 */
	public static String extractWithTabula() throws IOException, BishopRuleViolationException {
		String tabulaPath = FileHandlingUtils
				.getListOfAllFiles(System.getProperty("user.dir"), "tabula-1.0.3-jar-with-dependencies.jar").iterator()
				.next().getAbsolutePath();
		String[] string;
		if (OSValidator.isWindows())
			string = new String[] { "java", "-jar", tabulaPath.replace(" ", "^ "), "-b",
					SystemProperties.DEFAULT_DOWNLOAD_LOCATION.replace(" ", "^ "), "-l", "-p", "all" };
		else
			string = new String[] { "java", "-jar", tabulaPath.replace(" ", "\\ "), "-b",
					SystemProperties.DEFAULT_DOWNLOAD_LOCATION.replace(" ", "\\ "), "-l", "-p", "all" };
		LOGGER.info(Arrays.deepToString(string));
		try {
			ProcessBuilder pb = new ProcessBuilder();
			pb.command(string);
			Process proc = pb.start();
			proc.waitFor(60, TimeUnit.SECONDS);
			String s = "";
			BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
			BufferedReader stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
			StringBuilder insBuilder = new StringBuilder();
			StringBuilder errsBuilder = new StringBuilder();
			while ((s = stdInput.readLine()) != null) {
				insBuilder.append(s).append(System.lineSeparator());
			}
			while ((s = stdError.readLine()) != null) {
				errsBuilder.append(s).append(System.lineSeparator());
			}
			if (StringUtils.isNotBlank(errsBuilder)) {
				LOGGER.error("Error for the command : " + errsBuilder);
			}
			String streamOutput = insBuilder.toString() + System.lineSeparator() + errsBuilder.toString();
			LOGGER.info("Stream output >> " + streamOutput);
			Collection<File> files = FileHandlingUtils.getListOfAllFiles(SystemProperties.DEFAULT_DOWNLOAD_LOCATION,
					"*.csv");
			if (CollectionUtils.isNotEmpty(files)) {
				return files.iterator().next().getAbsolutePath();
			} else {
				LOGGER.error("Unable to convert PDF to CSV");
				throw new BishopRuleViolationException("Unable to convert PDF to CSV");
			}
		} catch (InterruptedException e) {
			LOGGER.error("Unable to convert PDF to CSV", e);
			throw new BishopRuleViolationException("Unable to convert PDF to CSV", e);
		}
	}

	public static void splitPdf(String filePath, String fileNamePath, int fromPage, int toPage) throws Exception {
		PDDocument document = PDDocument.load(new File(filePath));
		try {
			Splitter splitter = new Splitter();
			splitter.setStartPage(fromPage);
			splitter.setEndPage(toPage);
			splitter.setSplitAtPage(toPage - fromPage + 1);
			List<PDDocument> splittedList = splitter.split(document);
			for (PDDocument doc : splittedList) {
				doc.save(fileNamePath);
				doc.close();
			}
			LOGGER.info("PDF split successful, pdf saved with name : " + FilenameUtils.getBaseName(fileNamePath)
					+ " from page " + fromPage + " to " + toPage);
		} catch (Exception e) {
			LOGGER.error("Error in spliting and saving pdf", e);
			throw new Exception("Error in spliting and saving pdf", e);
		} finally {
			if (document != null) {
				document.close();
			}
		}
	}

	/**
	 * Convert text into PDF
	 * 
	 * @param text     - input text
	 * @param filename - Output file name
	 * @param path     - Output location
	 * @return - Output path
	 * @throws IOException
	 * @throws BishopRuleViolationException
	 */
	public static String convertTextIntoPDF(String text, String filename, String path)
			throws IOException, BishopRuleViolationException {
		return convertTextIntoPDF(text, filename, path, "");
	}

	/**
	 * Convert text into PDF and also adds a logo
	 * 
	 * @param text      - input text
	 * @param filename  - Output file name
	 * @param path      - Output location
	 * @param imagePath - The path of of logo image
	 * @return - Output path
	 * @throws IOException
	 * @throws BishopRuleViolationException
	 */
	public static String convertTextIntoPDF(String text, String filename, String path, String imagePath)
			throws IOException, BishopRuleViolationException {
		Document document = null;
		try {
			PdfWriter writer = new PdfWriter(Paths.get(path, filename + ".pdf").toString());
			PdfDocument pdf = new PdfDocument(writer);
			document = new Document(pdf, PageSize.A4);

			if (StringUtils.isNotBlank(imagePath)) {
				Paragraph paragraphLogo = new Paragraph();
				ImageData data = ImageDataFactory.create(imagePath);
				com.itextpdf.layout.element.Image logo = new com.itextpdf.layout.element.Image(data);
				logo.scaleAbsolute(120f, 50f);
				paragraphLogo.add(logo);
				document.add(paragraphLogo);
			}

			Text plainText = new Text(text);
			PdfFont font = PdfFontFactory.createFont();
			plainText.setFont(font);
			Paragraph paragraphText1 = new Paragraph().setFontSize(10);
			paragraphText1.setMarginTop(20);
			paragraphText1.add(plainText);
			document.add(paragraphText1);
		} catch (Exception e) {
			throw new BishopRuleViolationException("Unable to create pdf " + filename, e);
		} finally {
			if (document != null) {
				document.close();
			}
		}
		return path;
	}
}
