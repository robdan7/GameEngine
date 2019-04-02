package core.demo;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import core.entities.Mesh;
import core.utils.fileSystem.XMLparser;

public class XMLdemo {

	public XMLdemo() {
		String file = "/Assets/new/mesh/Mesh_plane.XML";
		//file = "/Assets/new/mesh/imports.shd";
		Mesh mesh = new Mesh(file);
	}
	
	public static void main(String[] args) {
		new XMLdemo();
	}

}
