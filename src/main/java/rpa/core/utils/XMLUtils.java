package rpa.core.utils;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

public class XMLUtils {
	public static Document doc = new Document();
	public static Namespace namespace;
	private static final String ENVELOPE = "Envelope";

	public static void setNamespaces(String namespaceUrl) {
		namespace = Namespace.getNamespace(namespaceUrl);
	}

	public static void setRoot(String label, String value, boolean withNamespace) {
		Element element;
		if (namespace != null && withNamespace) {
			element = new Element(ENVELOPE, namespace);
		} else {
			element = new Element(ENVELOPE);
		}
		doc.setRootElement(element);
	}

	public static void setParent(String label, String value, boolean withNamespace) {
		Element element;
		if (namespace != null && withNamespace) {
			element = new Element(ENVELOPE, namespace);
		} else {
			element = new Element(ENVELOPE);
		}
		doc.addContent(element);
	}

	public static void setValues(List<Map<String, String>> setOfValues) {
		for (Map<String, String> values : setOfValues) {
			for (String key : values.keySet()) {
				setValue(key, values.get(key));
			}
		}
	}

	public static void setValue(String label, String value) {
		Element element = new Element(label);
		element.setText(value);
		doc.addContent(element);
	}

	public static String getXMLStructuredData() {
		XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
		String xmlData = StringUtils.replace(outputter.outputString(doc), " xmlns=\"\"", "");
		return xmlData;
	}
}
