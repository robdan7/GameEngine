package core.entities;

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
import org.lwjgl.system.NativeType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import core.graphics.renderUtils.RenderObject;
import core.graphics.renderUtils.Shaders;
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
	public int modelVBO;
	private boolean shouldUpdate = false;
	
	GlueList<VertexAttribute> attributes;

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
		
		this.attributes = new GlueList<VertexAttribute>();
		
		
		/* Add vertex attributes for vertices etc. */
		if (this.modelMesh.hasVertices()) {
			this.attributes.add(new VertexAttribute(0,3,GL_FLOAT,false,Float.BYTES*8,0));
		}
		
		if (this.modelMesh.hasNormals()) {
			this.attributes.add(new VertexAttribute(1,3,GL_FLOAT,false,Float.BYTES*8,Float.BYTES*3));
		}
		
		if (this.modelMesh.hasUVcoords()) {
			this.attributes.add(new VertexAttribute(2,2,GL_FLOAT,false,Float.BYTES*8,Float.BYTES*6));
		}
		
		/* Create the VBO and all instances. The VBO must exist before the instances can be created. */
		int instancecount = root.getElementsByTagName("instance").getLength();
		this.modelVBO = this.createVBO(instancecount, instanceBufferSize);
		this.instances = this.createInstances(root, instanceBufferSize);
		
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
	
	@Deprecated
	private void setModelInstances(ModelInstance... instances) {
		this.instances = instances;
	}
	
	public void renderModelInstances() {
		if (this.shouldUpdate) {
			
		}
		this.material.setToActiveMaterial();
		
		GL20.glBindBuffer(GL_ARRAY_BUFFER, this.modelVBO);

		for (VertexAttribute attrib : this.attributes) {
			attrib.bindAttribute();
		}

		GL31.glDrawArraysInstanced(GL11.GL_TRIANGLES, 0, this.modelMesh.getTotalNumberOfVertices(), this.instances.length);
		
		GL20.glBindBuffer(GL_ARRAY_BUFFER, 0);
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
		return BufferTools.createVertexBuffer(GL_ARRAY_BUFFER, this.modelBuffer, GL_STATIC_DRAW);
	}
	
	protected void updateVBO() {
		this.modelBuffer.clear();
		BufferTools.updateVertexBuffer(GL_ARRAY_BUFFER, modelVBO, 0, this.modelBuffer);
	}
	
	protected void addAttribute(VertexAttribute attrib) {
		this.attributes.add(attrib);
	}
	
	protected int getMeshVerticesCount() {
		return this.modelMesh.getTotalNumberOfVertices();
	}
	
	/**
	 * Add a newly created model instance to the vertex buffer object.
	 */
	@Deprecated
	void updateVBO(ModelInstance... instances) {
		/*
		FloatBuffer[] instanceBuffers = new FloatBuffer[instances.length];
		int i= 0;
		for (ModelInstance instance : instances) {
			instanceBuffers[i] = instance.getInstanceData();
			i++;
		}
		
		FloatBuffer buffers = BufferTools.combineBuffers(instanceBuffers);
		
		
		this.modelBuffer = BufferTools.combineBuffers(this.modelBuffer,buffers);
		this.modelBuffer.flip();

		BufferTools.revalidateVertexBuffer(GL_ARRAY_BUFFER, this.modelVBO, this.modelBuffer, GL_STATIC_DRAW);
		*/
		throw new UnsupportedOperationException();
	}

	void signalUpdate() {
		this.shouldUpdate = true;
	}
	
	@Deprecated
	private Vector3f parsePosition(Element position) {
		String[] vectorString = position.getTextContent().split(" ");
		
		return new Vector3f(Float.parseFloat(vectorString[0]), Float.parseFloat(vectorString[1]), Float.parseFloat(vectorString[2]));
	}
	
	/**
	 * Set the position of this model and all its instances.
	 * @param position
	 */
	public void setPosition(Vector3f position) {
		this.position.set(position);
	}
	
	
	/**
	 * Create a singular model instance.
	 * @param m
	 */
	@Deprecated
	public static ModelInstance createModelInstance(Element m) {
		throw new UnsupportedOperationException("This method is deprecated.");
		/*Model model = new Model(m);

		ModelInstance instance = new ModelInstance(model);
		model.setModelInstances(instance);
		
		//model.updateVBO(instance);
		model.updateVBO(instance);
		return instance;
		*/
	}
	
	@Deprecated
	public static Model createInstancedModel(Element m) {
		throw new UnsupportedOperationException("This method is deprecated.");
		/*
		Model model = new Model(m);
		
		NodeList nodes = m.getChildNodes();
		
		ModelInstance[] instances = new ModelInstance[Integer.parseInt(m.getAttribute("instances"))];
		if (instances.length == 0) {
			throw new RuntimeException("zero instances");
		}
		Element el;
		for (int i = 0, k = 0; i < nodes.getLength() && k < instances.length; i++) {
			if (!nodes.item(i).getNodeName().equals("#text")) {
				el = (Element) nodes.item(i);

				switch (el.getNodeName()) {
				case "instance":
					instances[k] = createPlainInstance(model, el);
					k++;
					break;
				}
			}
		}
		model.setModelInstances(instances);
		model.updateVBO(instances);
		//return instances;
		return model;
		*/
	}
	
	@Deprecated
	private static ModelInstance createPlainInstance(Model m, Element instance) {
		/*
		NodeList nodes = instance.getChildNodes();
		Vector3f position = new Vector3f();
		Element el;
		for (int i = 0; i < nodes.getLength(); i++) {
			if (!nodes.item(i).getNodeName().equals("#text")) {
				el = (Element) nodes.item(i);
				
				switch (el.getNodeName()) {
				case "position":
					String[] pos = el.getTextContent().split("\\s+");
					position.set(Integer.parseInt(pos[0]), Integer.parseInt(pos[1]), Integer.parseInt(pos[2]));
					break;
				}
			}
		}
		return new ModelInstance(m,position);
		*/
		return null;
	}
	
	@Deprecated
	public static ModelInstance createModelInstance(String file) throws SAXException, IOException, ParserConfigurationException {
		throw new UnsupportedOperationException("This method is deprecated.");
		/*Document doc = XMLparser.createDocument(file);
		Element root = doc.getDocumentElement();
		
		return Model.createModelInstance(root);
		*/
	}
	
	@Deprecated
	public static Model createModelInstances(String file) throws SAXException, IOException, ParserConfigurationException {
		/*
		Document doc = XMLparser.createDocument(file);
		Element root = doc.getDocumentElement();
		
		return Model.createInstancedModel(root);
		*/
		throw new UnsupportedOperationException("This method is deprecated.");
	}
	
	protected abstract ModelInstance createModelInstance(Model m, Element instanceRoot, int bufferStart, int bufferStop, FloatBuffer instanceBuffer);
	
	protected abstract ModelInstance createModelInstance(Model m, int bufferStart, int bufferStop, FloatBuffer instanceBuffer);

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

	
	protected static class VertexAttribute {
		private int index, size, type, stride;
		private long pointer;
		private boolean normalized;

		public VertexAttribute (@NativeType(value="GLuint") int index, @NativeType(value="GLint") int size, @NativeType(value="GLenum") int type, @NativeType(value="GLboolean") boolean normalized, @NativeType(value="GLsizei") int stride, @NativeType(value="void cont *") long pointer) {
			this.index = index;
			this.size = size;
			this.type = type;
			this.stride = stride;
			this.pointer = pointer;
			this.normalized = normalized;
		}
		
		protected VertexAttribute(Element root) {
			this.index = Integer.parseInt( root.getAttribute("index"));
			
			switch(root.getAttribute("type")) {
			case "vec4":
				
				break;
			}
			
		}

		void bindAttribute() {
			GL20.glVertexAttribPointer(this.index, this.size, this.type, this.normalized, this.stride, this.pointer);
		}
	}

	
}
