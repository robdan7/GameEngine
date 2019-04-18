package core.graphics.shading;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.GL_COMPILE_STATUS;
import static org.lwjgl.opengl.GL20.glGetShaderi;

import java.io.IOException;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;

import org.lwjgl.opengl.GL20;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import core.utils.datatypes.GlueList;
import core.utils.fileSystem.XMLparser;
import core.graphics.shading.attributes.Attribute;
import core.graphics.shading.attributes.Attribute.AttributeCreationException;
import core.graphics.shading.attributes.AttributeBlock;
import core.graphics.shading.uniforms.*;

public class Shader {
	private int shaderIndex = -1;
	private String name = "NULL";
	private GlueList<UniformBlock> uniformBlocks;
	private GlueList<Uniform> localUniforms;
	private GlueList<Attribute> attributes;
	private GlueList<AttributeBlock> attributeBlocks;
	private Element code;
	private Shader parent;
	
	private static HashMap<String, Shader> shaderImports;
	
	static {
		shaderImports = new HashMap<String, Shader>();
	}

	/**
	 * Create a shaders that is just an empty shell with code in it. Used for internal imports.
	 * @param file
	 * @throws SAXException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 */
	private Shader(String file) throws SAXException, IOException, ParserConfigurationException {
		Document doc = XMLparser.createDocument(file);
		Element root = doc.getDocumentElement();
		NodeList nodes = root.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			if (!nodes.item(i).getNodeName().equals("#text") && nodes.item(i).getNodeName().equals("code")) {
				this.code = (Element) nodes.item(i);
			}
		}
		this.insertPlaceHolders();
	}
	
	private Shader(Element shaderElement) {
		this.attributes = new GlueList<Attribute>();
		this.uniformBlocks = new GlueList<UniformBlock>();
		this.localUniforms = new GlueList<Uniform>();
		this.attributes = new GlueList<Attribute>();
		this.attributeBlocks = new GlueList<AttributeBlock>();
		if (shaderElement.hasAttribute("name")) {
			this.name = shaderElement.getAttribute("name");
		}
	}
	
	public Shader(Element shaderElement, int shaderType) {
		this(shaderElement, null, shaderType);
		
		/*
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
		*/
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
		//System.out.println(this.code.getTextContent());
	}
	
	
	public Shader(Element shaderRoot, Shader parent, int shaderType) {
		this(shaderRoot);
		
		this.parent = parent;
		//this.iterateChildren(shaderRoot);
		if (this.hasParent()) {
			this.uniformBlocks = (GlueList<UniformBlock>) this.parent.uniformBlocks.clone();
			this.localUniforms = (GlueList<Uniform>) this.parent.localUniforms.clone();
			Element parentCode = (Element)this.parent.code.cloneNode(true);
			NodeList parentNode = parentCode.getChildNodes();
			//System.out.println(this.code.getTextContent());
		}
		
		this.iterateChildren(shaderRoot);		
		this.insertToParentCode();
		this.insertPlaceHolders();
		
		/*
		Element el;
		for (int i = 0; i < parentNode.getLength(); i++) {
			if (!parentNode.item(i).getNodeName().equals("#text")) {
				el = (Element)parentNode.item(i);
				
				if (el.getNodeName().equals("placeholder")) {
					String attribute = el.getAttribute("type");
					if (attribute.equals("attribute")) {
						
						el.setTextContent(el.getTextContent() + this.getAttributeStrings());
					} else if (attribute.equals("code")) {
						el.setTextContent(el.getTextContent() + this.code.getTextContent());
					} else if (attribute.equals("uniform")) {
						System.out.println("hello world");
					}
				}
			}
		}
		this.code = parentCode;
		*/
		this.shaderIndex = this.compileshader(this.code.getTextContent(), shaderType);
	}
	
	/**
	 * Merge the code of this shader with the parent code. The code will be inserted at 
	 * the specified user-code pointer.
	 */
	private void insertToParentCode() {
		if (!this.hasParent()) {
			return;
		}
		Element parentCode = (Element)this.parent.code.cloneNode(true);
		NodeList nodes = parentCode.getChildNodes();
		
		Element el;
		for (int i = 0; i < nodes.getLength(); i++) {

			if (nodes.item(i).getNodeName().equals("usercode")) {
				parentCode.replaceChild(this.code, nodes.item(i));
			}
		}
		this.code = parentCode;
	}
	
	/**
	 * Add all uniforms and attributes to the placeholders in the code. 
	 * The placeholders can only exist in one place, so no duplicates will be created if 
	 * the shader has a parent and both have the same placeholders.
	 */
	private void insertPlaceHolders() {
		NodeList codeNodes = this.code.getChildNodes();
		Element el;
		for (int i = 0; i < codeNodes.getLength(); i++) {
			if (!codeNodes.item(i).getNodeName().equals("#text")) {
				el = (Element) codeNodes.item(i);
				if (el.getNodeName().equals("placeholder")) {
					String type = el.getAttribute("type");
					if (type.equals("attribute")) {;
						el.setTextContent(this.getAttributeStrings());
					} else if (type.equals("uniform")) {
						el.setTextContent(this.getUniformStrings());
					}
					
				} else {
					if (el.getNodeName().equals("import")) {
						this.importShader(el, el.getTextContent());
					}
				}
			}
		}
	}
	

	
	private String getUniformStrings() {
		String result = "";
		
		for (UniformBlock b : this.uniformBlocks) {
			result += b.toString();
		}
		
		for (Uniform u : this.localUniforms) {
			result += u.toString();
		}
		
		return result;
	}
	
	private String getAttributeStrings() {
		String result = "";
		
		for (Attribute b : this.attributes) {
			result += b.toString();
		}
		
		for (AttributeBlock b: this.attributeBlocks) {
			result += b.toString();
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
					this.parseAttribute(el);
					continue;
				case "uniform":
					this.parseUniform(el);
					break;
				case "code":
					this.code = el;
					break;
				}
			}
		}
	}
	
	private void importShader(Element el, String file) {
		String[] files = file.split("\\s+");
		
		for (String f : files) {
			//System.out.println(f);
			if (shaderImports.containsKey(f)) {
				el.setTextContent(shaderImports.get(f).code.getTextContent());
			} else {
				Shader sh = null;
				try {
					sh = new Shader(f);
				} catch (SAXException | IOException | ParserConfigurationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				shaderImports.put(f, sh);
				el.setTextContent(sh.code.getTextContent());
				//el.setTextContent("");
			}
		}
	}
	
	private void parseAttribute(Element attrib) {
		if (attrib.hasChildNodes()) {
			try {
				this.attributeBlocks.add(new AttributeBlock(attrib));
			} catch (AttributeCreationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			int location = 0;
			if (this.attributes.size() > 0) {
				location = this.attributes.get(this.attributes.size()-1).getLocation()+1;
			}

			if (!attrib.hasAttribute(Attribute.AttributeSyntax.LOCATION.toString())) {
				attrib.setAttribute(Attribute.AttributeSyntax.LOCATION.toString(), Integer.toString(location));
			}
			try {
				this.attributes.add(new Attribute(attrib));
			} catch (AttributeCreationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private void parseUniform(Element uniformNode) {
		if (uniformNode.hasChildNodes()) {
			this.uniformBlocks.add(new UniformBlock(uniformNode));
		} else {
			try {
				this.localUniforms.add(new Uniform(uniformNode, this.localUniforms));
			} catch (UniformCreationException e) {
				// TODO Auto-generated catch block
				System.err.println("Error in shader " + this.getName());
				e.printStackTrace();
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
	
	public String getName() {
		return this.name;
	}
	
	@Deprecated
	private static abstract class ShaderVariable {
		private String name="", type="", data="", structName="";
		int location = -1;
		NodeList children;
	}
}
