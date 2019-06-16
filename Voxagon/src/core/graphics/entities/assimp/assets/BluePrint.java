package core.graphics.entities.assimp.assets;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import core.utils.fileSystem.XMLparser;

/**
 * The blueprint is used for importing single models which can then be instanced 
 * whenever it is needed.
 * @author Robin
 *
 */
public class BluePrint {
	private Model model;

	public BluePrint(String file) {
		Document doc = null;
		try {
			doc = XMLparser.createDocument(file);
		} catch (SAXException | IOException | ParserConfigurationException e) {
			e.printStackTrace();
		}
		
		NodeList childs = doc.getFirstChild().getChildNodes();
		
		for (int i = 0; i< childs.getLength(); i++) {
			if (childs.item(i).getNodeName().equals(Syntax.TEXT.value())) {
				continue;
			}
			System.out.println(childs.item(i).getNodeName());
			String name = childs.item(i).getNodeName();
			if (name.equals(Syntax.MODEL.value())) {
				/*
				 * TODO The model could be anything with animations and rigging. 
				 * Dunno how it will work, but it requires some form of system
				 * that might involve the scene object.
				 */
				Element n = (Element)childs.item(i);
				String url = n.getAttribute(Syntax.URL.value());
				this.model = new Model(url,16,url);
			} else if (name.equals(Syntax.MATERIAL.value())) {
				
			}
		}
	}
	
	private static enum Syntax {
		MODEL("model") ,URL("url"), MATERIAL("material"), TEXT("#text");
		private String s;
		private Syntax(String s) {
			this.s = s;
		}
		
		private String value() {
			return this.s;
		}
	}

}
