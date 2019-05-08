package core.graphics.entities;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL31;
import org.lwjgl.opengl.GL33;
import org.lwjgl.opengl.GL40;
import org.lwjgl.system.NativeType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import core.graphics.renderUtils.RenderObject;
import core.graphics.renderUtils.Shaders;
import core.graphics.renderUtils.VertexAttribute;
import core.graphics.shading.Material;
import core.utils.datatypes.GlueList;
import core.utils.fileSystem.XMLparser;
import core.utils.math.Matrix4f;
import core.utils.math.Vector3f;
import core.utils.math.Vector4f;
import core.utils.other.BufferTools;

/**
 * <p>This is the abstract super class for all models used in the engine. A model can contain one or more instances 
 * with the same material and properties. All instances are rendered as one object, and it is up to the user to 
 * separate all instances from each other.</p>
 * 
 * <p>There are no vertex attributes except for vertex, normal and UV coords. These attributes are assumed to be 
 * stored in the positions 0,1 and 2.</p>
 * @author Robin
 *
 */
public abstract class Model implements RenderObject {
	private Vector3f position;
	private Mesh modelMesh;
	private String name;
	private Material material;
	FloatBuffer modelBuffer;
	private int modelVBO;
	private int modelVAO;
	
	private boolean shouldUpdate = false;
	
	private VertexAttribute[] attributes;

	private ModelInstance[] instances;
	
	public Model(String modelFile, int instanceBufferSize) {
		Document doc = null;
		try {
			doc = XMLparser.createDocument(modelFile);
		} catch (SAXException | IOException | ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Element root = doc.getDocumentElement();
		
		this.name = root.getAttribute("name");
		this.material = new Material(root.getAttribute("material"));
		
		
		/* Create mesh */
		String mesh = root.getAttribute("mesh");
		this.modelMesh = new Mesh(mesh);
		
		this.attributes = new VertexAttribute[3];
		
		
		/* Add vertex attributes for vertices etc. */
		if (this.modelMesh.hasVertices()) {
			this.attributes[0] = new VertexAttribute(0,3,GL_FLOAT,false,Float.BYTES*8,0);
		}
		
		if (this.modelMesh.hasNormals()) {
			this.attributes[1] = new VertexAttribute(1,3,GL_FLOAT,false,Float.BYTES*8,Float.BYTES*3);
		}
		
		if (this.modelMesh.hasUVcoords()) {
			this.attributes[2] = new VertexAttribute(2,2,GL_FLOAT,false,Float.BYTES*8,Float.BYTES*6);
		}
		
		
		/* Create the VBO and all instances. The VBO must exist before the instances can be created. */
		int instancecount = root.getElementsByTagName("instance").getLength();
		this.modelVBO = this.createVBO(instancecount, instanceBufferSize);
		this.instances = this.createInstances(root, instanceBufferSize);
		
		this.modelVAO = BufferTools.createVAO(this.modelVBO, this.attributes);
		
		/* All the instances are set up, but the VBO has not been updated yet. */
		this.updateVBO();
	}
	

	private ModelInstance[] createInstances(Element root, int instanceBufferSize) {
		NodeList nodes = root.getElementsByTagName("instance");
		int start = this.modelMesh.getMeshBuffer().capacity();
		if (nodes.getLength() == 0) {
			
			// There are no instances, create one instance with the model position instead.
			ModelInstance instance = this.createModelInstance(this,root,start,start+instanceBufferSize, this.modelBuffer);
			instance.setAxisPosition(this.createPosition(root));
			return new ModelInstance[] {instance};
		}
		
		ModelInstance[] instances = new ModelInstance[nodes.getLength()];
		for (int i = 0; i < nodes.getLength(); i++) {
			instances[i] = this.createModelInstance(this, (Element) nodes.item(i),start+i*instanceBufferSize,start+(i+1)*instanceBufferSize, this.modelBuffer);
		}
		return instances;
	}
	
	private Vector3f createPosition(Element root) {
		NodeList nodes = root.getElementsByTagName("position");
		String[] vectorString = nodes.item(0).getTextContent().split("\\s+");
		int x = Integer.parseInt(vectorString[0]);
		int y = Integer.parseInt(vectorString[1]);
		int z = Integer.parseInt(vectorString[2]);
		return new Vector3f(x,y,z);
	}

	
	public void renderModelInstances() {
		if (this.shouldUpdate) {
			
		}
		this.material.setToActiveMaterial();
		GL30.glBindVertexArray(this.modelVAO);
		GL31.glDrawArraysInstanced(GL11.GL_TRIANGLES, 0, this.modelMesh.getTotalNumberOfVertices(), this.instances.length);
		GL30.glBindVertexArray(0);
	}

	/**
	 * Generate a VBO for this model.
	 * @return
	 */
	private int createVBO(int instances, int instanceBufferSize) {
		
		int instanceData = 0;
		if (instances > 0 ) {
			instanceData = instances * instanceBufferSize;
		}
		this.modelBuffer = BufferUtils.createFloatBuffer(this.modelMesh.getMeshBuffer().capacity() + instanceData);
		this.modelMesh.getMeshBuffer().flip();
		this.modelBuffer.put(this.modelMesh.getMeshBuffer());

		this.modelBuffer.clear();
		return BufferTools.createVBO(GL_ARRAY_BUFFER, this.modelBuffer, GL_STATIC_DRAW);
	}
	
	/**
	 * Create and bind the VAO used in this model.
	 */
	protected void addVAOattributes(int VBO, VertexAttribute...attributes) {
		BufferTools.addVAOattributes(this.modelVAO, this.modelVBO, attributes);
	}
	
	protected void updateVBO() {
		this.modelBuffer.clear();
		BufferTools.updateVertexBuffer(GL_ARRAY_BUFFER, modelVBO, 0, this.modelBuffer);
	}
	
	protected int getMeshVerticesCount() {
		return this.modelMesh.getTotalNumberOfVertices();
	}
	

	void signalUpdate() {
		this.shouldUpdate = true;
	}

	
	/**
	 * Set the position of this model and all its instances.
	 * @param position
	 */
	public void setPosition(Vector3f position) {
		this.position.set(position);
	}
	

	
	protected abstract ModelInstance createModelInstance(Model m, Element instanceRoot, int bufferStart, int bufferStop, FloatBuffer instanceBuffer);
	
	protected abstract ModelInstance createModelInstance(Model m, int bufferStart, int bufferStop, FloatBuffer instanceBuffer);

	protected int getVBO() {
		return this.modelVBO;
	}
	
	@Override
	public void render() {
		
	}

	@Override
	public void renderTextured() {
		this.renderModelInstances();
	}

	@Override
	public void discard() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setDepthShader(Shaders shader) {
		// TODO Auto-generated method stub
		
	}	
}
