package core.graphics.entities.staticMesh;

import static org.lwjgl.opengl.GL11.GL_FLOAT;

import java.nio.FloatBuffer;

import org.lwjgl.opengl.GL20;
import org.w3c.dom.Element;

import core.graphics.entities.Model;
import core.graphics.entities.ModelInstance;
import core.graphics.renderUtils.VertexAttribute;
import core.utils.math.Vector4f;

public class StaticModel extends Model{

	public StaticModel(String modelFile) {
		super(modelFile, StaticModelInstance.bufferSize);
		
		//GL20.glVertexAttribPointer(3, 4, GL_FLOAT, false, Float.BYTES*4, Float.BYTES*this.modelMesh.getTotalNumberOfVertices()*8);
		VertexAttribute attrib = new VertexAttribute(3, 4, GL_FLOAT, false, Float.BYTES*4, 0,1);
	
		super.addVAOattributes(attrib);
		//super.addAttribute(attrib);
	}

	@Override
	protected ModelInstance createModelInstance(Model m, Element instanceRoot, int bufferStart, int bufferStop, FloatBuffer instanceBuffer) {
		return new StaticModelInstance(m,instanceRoot, bufferStart, bufferStop, instanceBuffer);
	}

	@Override
	protected ModelInstance createModelInstance(Model m, int bufferStart, int bufferStop, FloatBuffer instanceBuffer) {
		return new StaticModelInstance(m, bufferStart, bufferStop, instanceBuffer);
	}

}
