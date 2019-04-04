package core.demo;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import core.entities.Mesh;
import core.entities.*;
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
		//System.out.println(root.getAttribute("mesh"));
		
		ModelInstance model = Model.createModelInstance(root);
		
		//file = "/Assets/new/mesh/imports.shd";
		//Mesh mesh = new Mesh(file);
	}
	
	public static void main(String[] args) {
		//System.out.println(Float.BYTES);
		new XMLdemo();
	}

}
