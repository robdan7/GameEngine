package core.demo;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.lwjgl.opengl.GL20;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import core.graphics.shading.Shader;
import core.utils.datatypes.GlueList;
import core.utils.fileSystem.XMLparser;

public class ShaderDemo {

	public ShaderDemo() {
		Document doc = null;
		try {
			doc = XMLparser.createDocument("/Assets/new/shaders/Template.shd");
		} catch (SAXException | IOException | ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Element root = doc.getDocumentElement();
		NodeList nodes = root.getChildNodes();
		Element el;
		GlueList<Element> shaders = new GlueList<Element>();
		for (int i = 0; i < nodes.getLength(); i++) {
			if (nodes.item(i).getNodeName() != "#text") {
				el = (Element)nodes.item(i);
				
				switch(el.getNodeName()) {
				case "shader":
					shaders.add(el);
					break;
				}
			}
		}
		
		Element shader = shaders.get(0);
		
		Shader sh = new Shader(shader, 0);
		
		// child
		try {
			doc = XMLparser.createDocument("/Assets/new/shaders/child_test.shd");
		} catch (SAXException | IOException | ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		root = doc.getDocumentElement();

		
		Shader child = new Shader(root, sh, 0);
		
		/*
		try {
			doc = XMLparser.createDocument("/Assets/new/shaders/child_test.shd");
		} catch (SAXException | IOException | ParserConfigurationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Element childRoot = doc.getDocumentElement();
		
		for (Element e: shaders) {
			switch(e.getAttribute("type")) {
			case "vertex":
				Shader s = new Shader(e, GL20.GL_VERTEX_SHADER);
				Shader child = new Shader(childRoot, s, GL20.GL_VERTEX_SHADER);
				break;
			}
			
		}
		*/
		//iterateChildren(root);
		
		//Element copy = (Element)root.cloneNode(true);
		//System.out.println(copy.getTextContent());
	}
	

	
	public static void main(String[] args) {
		new ShaderDemo();
	}

}
