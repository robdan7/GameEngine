package core.utils.fileSystem;

import java.io.*;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.*;

public class XMLparser {
	
	private static DocumentBuilder getBuilder() throws ParserConfigurationException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		return factory.newDocumentBuilder();
	}
	
	/**
	 * Create a XML document from the input file.
	 * @param file - The file to read from.
	 * @return A XML document readable in Java.
	 * @throws SAXException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 */
	public static Document createDocument(String file) throws SAXException, IOException, ParserConfigurationException {
		InputStream stream = FileManager.getStream(file);

		DocumentBuilder builder = getBuilder();
		Document doc = builder.parse(stream);
		stream.close();
		
		return doc;
	}
}
