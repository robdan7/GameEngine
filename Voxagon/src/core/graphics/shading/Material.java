package core.graphics.shading;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.lwjgl.opengl.GL20;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import core.graphics.renderUtils.Shaders;
import core.graphics.shading.uniforms.references.UniformBlockReference;
import core.utils.datatypes.GlueList;
import core.utils.fileSystem.XMLparser;

public class Material {
	private int shaderProgram;
	private Shaders sh;
	
	public static void loadVertexShaderBlueprint(String file) {
		
	}
	
	public static void loadFragmentShaderBlueprint(String file) {
		
	}

	public Material(String materialFile) {
		Document matDoc = null;
		try {
			matDoc = XMLparser.createDocument(materialFile);
		} catch (SAXException | IOException | ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Element root = matDoc.getDocumentElement();
		NodeList nodes = root.getChildNodes();
		
		Element el = null;
		GlueList<Shader> shaders = new GlueList<Shader>();
		
		/* Create and add shaders to the shader list */
		for (int i = 0; i < nodes.getLength(); i++) {
			if (nodes.item(i).getNodeName() != "#text") {
				el = (Element)nodes.item(i);
				
				if (el.getNodeName() == "shader") {
					switch(el.getAttribute("type")) {
					case "vertex":	/* Found a vertex shader */
						shaders.add(new Shader(el, GL20.GL_VERTEX_SHADER));
						break;
					case "fragment": /* Found a fragment shader */
						shaders.add(new Shader(el, GL20.GL_FRAGMENT_SHADER));
						break;
					}
				}
				
			}
		}
		
		/*
		Shaders sh = null;
		try {
			sh = new Shaders("/Assets/Shaders/combined/default.sha");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
*/
		this.shaderProgram = GL20.glCreateProgram();
		
		/* add all genererate shaders to the program */
		for (Shader s: shaders) {
			GL20.glAttachShader(shaderProgram, s.getShaderIndex());
		}
		
		/* Link and validate the program */
		GL20.glLinkProgram(shaderProgram);
		GL20.glValidateProgram(shaderProgram);
		GL20.glUseProgram(shaderProgram);
	}
	
	
	
	/**
	 * Set this material to be the active shader in the pipeline.
	 * @throws Exception 
	 */
	public void setToActiveMaterial() {
		GL20.glUseProgram(this.shaderProgram);
	}
	

}
