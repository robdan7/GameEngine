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

import core.utils.datatypes.GlueList;
import core.utils.fileSystem.XMLparser;


public class Shader {
	private int shaderIndex = -1;
	private String name = "NULL";
	private GlueList<Attribute> attributes;
	private Element code;
	private Shader parent;

	
	private Shader(Element shaderElement) {
		this.attributes = new GlueList<Attribute>();
		
		if (shaderElement.hasAttribute("name")) {
			this.name = shaderElement.getAttribute("name");
		}
		
		
	}
	
	public Shader(Element shaderElement, int shaderType) {
		this(shaderElement);
		this.iterateChildren(shaderElement);
		NodeList codeNodes = this.code.getChildNodes();
		Element el;
		for (int i = 0; i < codeNodes.getLength(); i++) {
			if (!codeNodes.item(i).getNodeName().equals("#text")) {
				el = (Element) codeNodes.item(i);
				if (el.getNodeName().equals("placeholder") && el.getAttribute("type").equals("attribute")) {
					el.setTextContent(this.getAttributeStrings());
				}
			}
		}
		
		/*
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
		}*/

		this.shaderIndex = this.compileshader(this.code.getTextContent(), shaderType);
	}
	
	
	public Shader(Element shader, Shader parent, int shaderType) {
		this(shader);
		this.parent = parent;
		this.iterateChildren(shader);
		Element parentCode = (Element)this.parent.code.cloneNode(true);
		NodeList parentNode = parentCode.getChildNodes();
		
		Element el;
		for (int i = 0; i < parentNode.getLength(); i++) {
			if (!parentNode.item(i).getNodeName().equals("#text")) {
				el = (Element)parentNode.item(i);
				
				if (el.getNodeName().equals("placeholder")) {
					
					if (el.getAttribute("type").equals("attribute")) {
						
						el.setTextContent(el.getTextContent() + this.getAttributeStrings());
					} else if (el.getAttribute("type").equals("code")) {
						el.setTextContent(el.getTextContent() + this.code.getTextContent());
					}
				}
			}
		}
		this.code = parentCode;
		System.out.println(this.code.getTextContent());
	}
	
	
	private String getAttributeStrings() {
		String result = "";
		for (int i = 0; i < this.attributes.size(); i++) {
			result += this.attributes.get(i).toString();
		}
		return result;
	}
	
	private void iterateChildren(Element root) {
		NodeList nodes = root.getChildNodes();
		
		Element el;
		for (int i = 0; i < nodes.getLength(); i++) {
			if (nodes.item(i).getNodeName() != "#text") {
				el = (Element) nodes.item(i);
				
				switch(el.getNodeName()) {
				case "attribute":
					// TODO fix this code, please.
					int lastIndex = 0;
					if (this.attributes.size() > 0) {
						lastIndex = this.attributes.get(this.attributes.size()-1).getLocation()+1;
					}
					int attributeLength = this.attributes.size() < lastIndex? lastIndex : this.attributes.size();				
					if (this.hasParent()) {	/* The parent could already have attributes with higher index. */
						int parentIndex = this.parent.attributes.get(parent.attributes.size()-1).location+1;
						if (parentIndex > lastIndex) {
							attributeLength += this.parent.attributes.get(parent.attributes.size()-1).location+1;
						}
					}
					this.attributes.add(new Attribute(el,attributeLength));
					
					continue;
				case "code":
					this.code = el;
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
	
	public boolean hasParent() {
		return this.parent != null;
	}
	
	private static class Throughput {
		private GlueList<Attribute> attributes;
		private GlueList<Uniform> uniforms;
		
		private Throughput() {
			
		}
		
		private int getNexAttributeIndex() {
			if (this.attributes.size() > 0) {
				return this.attributes.get(this.attributes.size()-1).location+1;
			} else {
				return 0;
			}
		}
		
		private int getNextUniformIndex() {
			if (this.uniforms.size() > 0) {
				//return this.uniforms.get(this.uniforms.size()-1)
				return 0;
			} else {
				return 0;
			}
		}
	}
	
	private static abstract class ShaderVariable {
		private String name="", type="", data="", structName="";
		int location = -1;
		NodeList children;
	}
	
	private static class Uniform extends ShaderVariable {
		
		
	}
	
	// TODO make a clear structure for the attributes and how input/output works. Maybe one super class 
	//with two classes that inherits from it and adds in/out to the attributes.
	public static class Attribute extends ShaderVariable {

		
		public Attribute(Element attribute, int location) {
			if (attribute.hasChildNodes()) {
				this.children = attribute.getChildNodes();
			} else if (!attribute.hasAttribute("type")) {
				throw new RuntimeException("Attribute is missing a type");
			} else if(!attribute.hasAttribute("name")) {
				throw new RuntimeException("Attribute is missing a name");
			}
			super.name = attribute.getAttribute("name");
			super.type = attribute.getAttribute("type");
			
			if (attribute.hasAttribute("location")) {
				super.location = Integer.parseInt(attribute.getAttribute("location"));
				if (super.location < location) {
					throw new RuntimeException("Attribute " + super.name + " has illegal location: " + (super.location-location));
				}
			} else {
				super.location = location;
			}
			
			super.data = attribute.getTextContent();
			
			if (attribute.hasAttribute("struct")) {
				super.structName = attribute.getAttribute("struct");
			}
		}
		
		private int getLocation() {
			return super.location;
		}
		
		@Override
		public String toString() {
			String result = "";
			if (super.location != -1) {
				result = "layout(location = " + super.location + ") in " ;
			} else {
				result = "in ";
			}
			 
			if (this.children != null) {
				result += super.structName + "{\n";
				for (int i = 0, k = 0; i < this.children.getLength(); i++) {
					if (this.children.item(i).getNodeName() != "#text") {
						result += new Attribute((Element)super.children.item(i), k).toString() + "\n";
						k++;
					}
 					//
				}
				return result + "}" + super.name + ";";
			} else {
				return result + super.type + " " + super.name + ";";
			}
		}
	}
}
