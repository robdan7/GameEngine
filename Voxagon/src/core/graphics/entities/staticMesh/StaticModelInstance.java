package core.graphics.entities.staticMesh;

import java.nio.FloatBuffer;

import org.w3c.dom.Element;

import core.graphics.entities.Model;
import core.graphics.entities.ModelInstance;
import core.utils.math.Vector3f;
import core.utils.math.Vector4f;

public class StaticModelInstance extends ModelInstance {
	static final int bufferSize = Vector4f.SIZE;

	public StaticModelInstance(Model m, Element root, int bufferStart, int bufferStop, FloatBuffer instanceBuffer) {
		super(m, root, bufferStart, bufferStop, instanceBuffer);
		
		super.getInstanceData().put(super.getPosition().asFloats());
		//super.getBuffer().put(super.getPosition().asFloats());
	}
	
	public StaticModelInstance(Model m, int bufferStart, int bufferStop, FloatBuffer instanceBuffer) {
		super(m, Vector4f.SIZE, bufferStart, bufferStop, instanceBuffer);
		//super.getBuffer().put(super.getPosition().asFloats());
		super.getInstanceData().put(super.getPosition().asFloats());
	}


}
