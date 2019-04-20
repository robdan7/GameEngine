package core.entities;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glGenBuffers;

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

public class Model implements RenderObject {
	private Vector3f position;
	private Mesh modelMesh;
	private String name;
	private Material material;
	GlueList<ModelInstance> instances;
	FloatBuffer modelBuffer;
	int modelVBO;
	private boolean shouldUpdate = false;

	private static HashMap<String, Model> createdModels;
	
	static {
		createdModels = new HashMap<String, Model>();
	}
	
	public Model(String name, Vector3f position, Mesh mesh) {
		this.position = position;
		this.modelMesh = mesh;
		this.instances = new GlueList<ModelInstance>();
		this.modelVBO = this.createVBO();
	}
	
	Model(Element model) {
		// TODO this should probably not be here
		
		this.name = model.getAttribute("name");
		this.material = new Material(model.getAttribute("material"));
		
		
		String mesh = model.getAttribute("mesh");
		this.modelMesh = new Mesh(mesh);
		
		NodeList nodes = model.getChildNodes();
		
		Element el = null;
		for (int i = 0; i < nodes.getLength(); i++) {
			if (nodes.item(i).getNodeName() != "#text") {
				el = (Element)nodes.item(i);
				if (el.getNodeName() == "position") {
					this.position = this.parsePosition(el);
				}
			}
			
		}
		this.instances = new GlueList<ModelInstance>();
		this.modelVBO = this.createVBO();
	}
	
	public void renderModelInstances() {
		if (this.shouldUpdate) {
			
		}
		this.material.setToActiveMaterial();
		
		GL20.glBindBuffer(GL_ARRAY_BUFFER, this.modelVBO);
		
		GL20.glVertexAttribPointer(0, 3, GL_FLOAT, false, Float.BYTES*8, 0);
		GL20.glVertexAttribPointer(1, 3, GL_FLOAT, false, Float.BYTES*8, Float.BYTES*3);
		GL20.glVertexAttribPointer(2, 2, GL_FLOAT, false, Float.BYTES*8, Float.BYTES*6);
		
		/*
		int index = 3;
		for (int i = 0; i < 4; i++) {
			GL20.glVertexAttribPointer(i+index, 4, GL_FLOAT, false, Float.BYTES*16, Float.BYTES*this.modelMesh.getTotalNumberOfVertices()*8);
			GL33.glVertexAttribDivisor(index+i, 1);
		}*/
		GL20.glVertexAttribPointer(3, 4, GL_FLOAT, false, Float.BYTES*4, Float.BYTES*this.modelMesh.getTotalNumberOfVertices()*8);
		
		//GL20.glDrawArrays(GL11.GL_TRIANGLES, 0, this.modelMesh.getTotalNumberOfVertices());
		GL31.glDrawArraysInstanced(GL11.GL_TRIANGLES, 0, this.modelMesh.getTotalNumberOfVertices(), this.instances.size());
		
		GL20.glBindBuffer(GL_ARRAY_BUFFER, 0);
	}

	private int createVBO() {
		
		
		int instanceData = 0;
		if (this.instances.size() > 0 ) {
			instanceData = this.instances.size() * this.instances.get(0).getInstanceData().capacity();
		}
		this.modelBuffer = BufferUtils.createFloatBuffer(this.modelMesh.getMeshBuffer().capacity() + instanceData);
		this.modelMesh.getMeshBuffer().flip();
		this.modelBuffer.put(this.modelMesh.getMeshBuffer());
		
		for(ModelInstance instance : this.instances) {
			// TODO move this if statement to the creation of instances.
			if (instance.getInstanceData().capacity() != this.instances.get(0).getInstanceData().capacity()) {
				throw new RuntimeException("The capacities are not equal!");
			}
			this.modelBuffer.put(instance.getInstanceData());
		}
		this.modelBuffer.flip();


		// return vbo;
		// return new int[] {vbo,model.getIndicesCount()};
		return BufferTools.createVertexBuffer(GL_ARRAY_BUFFER, this.modelBuffer, GL_STATIC_DRAW);
	}
	
	
	/**
	 * Add a newly created model instance to the vertex buffer object.
	 */
	void updateVBO(ModelInstance... instances) {
		
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
	}

	void signalUpdate() {
		this.shouldUpdate = true;
	}
	
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
	
	private void addInstance(ModelInstance m) {
		this.instances.add(m);
	}
	
	/**
	 * Create a singular model instance.
	 * @param m
	 */
	public static ModelInstance createModelInstance(Element m) {
		Model model = null;
		if (Model.createdModels.containsKey(m.getAttribute("name"))) {
			model = Model.createdModels.get(m.getAttribute("name"));
		} else {
			model = new Model(m);
			Model.createdModels.put(m.getAttribute("name"), model);
		}
		
		ModelInstance instance = new ModelInstance(model);
		model.addInstance(instance);
		
		//model.updateVBO(instance);
		model.updateVBO(instance);
		return instance;
	}
	
	public static ModelInstance[] createInstancedModel(Element m) {
		Model model = null;
		if (Model.createdModels.containsKey(m.getAttribute("name"))) {
			model = Model.createdModels.get(m.getAttribute("name"));
		} else {
			model = new Model(m);
			Model.createdModels.put(m.getAttribute("name"), model);
		}
		
		
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
					model.addInstance(instances[k]);
					k++;
					break;
				}
			}
		}
		model.updateVBO(instances);
		return instances;
	}
	
	private static ModelInstance createPlainInstance(Model m, Element instance) {
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
	}
	
	public static ModelInstance createModelInstance(String file) throws SAXException, IOException, ParserConfigurationException {
		Document doc = XMLparser.createDocument(file);
		Element root = doc.getDocumentElement();
		
		return Model.createModelInstance(root);
	}
	
	public static ModelInstance[] createModelInstances(String file) throws SAXException, IOException, ParserConfigurationException {
		Document doc = XMLparser.createDocument(file);
		Element root = doc.getDocumentElement();
		
		return Model.createInstancedModel(root);
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
	public void setShader(Shaders shader) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setDepthShader(Shaders shader) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getShader() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getDepthShader() {
		// TODO Auto-generated method stub
		return 0;
	}
	
}
