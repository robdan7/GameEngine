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
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import core.graphics.shading.Material;
import core.utils.datatypes.GlueList;
import core.utils.fileSystem.XMLparser;
import core.utils.math.Matrix4f;
import core.utils.math.Vector3f;
import core.utils.other.BufferTools;

public class Model {
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
		GL20.glDrawArrays(GL11.GL_TRIANGLES, 0, this.modelMesh.getTotalNumberOfVertices());
		
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


		// return vbo;
		// return new int[] {vbo,model.getIndicesCount()};
		return BufferTools.createVertexBuffer(GL_ARRAY_BUFFER, this.modelBuffer, GL_STATIC_DRAW);
	}
	
	/**
	 * Add a newly created model instance to the vertex buffer object.
	 */
	void updateVBO(ModelInstance newInstance) {
		this.modelBuffer = BufferTools.combineBuffers(this.modelBuffer, newInstance.getInstanceData());
		
		BufferTools.revalidateVertexBuffer(GL_ARRAY_BUFFER, this.modelVBO, modelBuffer, GL_STATIC_DRAW);
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
	 * The XML element containing model information, such as mesh, position and name.
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
		return instance;
	}
	
	public static ModelInstance createModelInstance(String file) throws SAXException, IOException, ParserConfigurationException {
		Document doc = XMLparser.createDocument(file);
		Element root = doc.getDocumentElement();
		
		return Model.createModelInstance(root);
	}
	
}
