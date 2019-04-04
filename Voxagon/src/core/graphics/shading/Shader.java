package core.graphics.shading;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.GL_COMPILE_STATUS;
import static org.lwjgl.opengl.GL20.glGetShaderi;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.lwjgl.opengl.GL20;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import core.utils.fileSystem.XMLparser;


public class Shader {
	private int shaderIndex = -1;
	private String name = "NULL";
	
	public Shader(Element shaderElement, int shaderType) {
		NodeList nodes = shaderElement.getChildNodes();
		
		if (shaderElement.hasAttribute("name")) {
			this.name = shaderElement.getAttribute("name");
		}
		
		Element el = null;
		for (int i = 0; i < nodes.getLength(); i++) {
			if (nodes.item(i).getNodeName() != "#text") {
				el = (Element) nodes.item(i);
				
				switch(el.getNodeName()) {
				case "code":
					if (this.shaderIndex == -1) {
						this.shaderIndex = this.compileshader(el.getTextContent(), shaderType);
					} else {
						throw new RuntimeException("shader (" + this.name + ") has two code nodes");
					}
					break;
				}
			}
		}
	}
	
	private int compileshader(String code, int shaderType) {
		int shader = GL20.glCreateShader(shaderType);
		GL20.glShaderSource(shader, code);
		GL20.glCompileShader(shader);
		
		if (glGetShaderi(shader, GL_COMPILE_STATUS) == GL_FALSE) {
            System.err.println("Shader wasn't able to be compiled correctly: " + this.name);
        }
		return shader;
	}
	
	public Shader(String file, int shaderType) throws SAXException, IOException, ParserConfigurationException {
		this(XMLparser.createDocument(file).getDocumentElement(), shaderType);
	}
	
	public int getShaderIndex() {
		return this.shaderIndex;
	}
	
}
