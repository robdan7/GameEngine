package core.demo;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import core.graphics.entities.Mesh;
import core.utils.fileSystem.XMLparser;

public class XMLdemo {

	public XMLdemo() {
		String file = "/Assets/new/models/Object_cube.001.XML";
		Document doc = null;
		try {
			doc = XMLparser.createDocument(file);
		} catch (SAXException | IOException | ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Element root = doc.getDocumentElement();
		
		NodeList nodes = root.getElementsByTagName("position");
		
		for (int i = 0; i < nodes.getLength(); i++) {
			System.out.println(nodes.item(i).getNodeName());
		}
		
		//System.out.println(root.getAttribute("mesh"));
		
		//ModelInstance model = Model.createModelInstance(root);
		
		//file = "/Assets/new/mesh/imports.shd";
		//Mesh mesh = new Mesh(file);
	}
	
	public static void main(String[] args) {
		//System.out.println(Float.BYTES);
		new XMLdemo();
	}

}
