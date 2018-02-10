package objects.lights;

import utils.math.Vector4f;
import utils.rendering.Uniform;

public class DirectionalLight extends Light{
	private Vector4f diffuse;
	private Vector4f ambient;
	private Vector4f position;
	/**
	 * 
	 * @param position - The position.
	 * @param diffuse - Diffuse lighting.
	 * @param ambient - Ambient lighting.
	 */
	public DirectionalLight (Vector4f position, Vector4f diffuse, Vector4f ambient, int index) {
		super(position.normalize().toVec4f(), diffuse, ambient, index);
		this.position = position.normalize().toVec4f();
		this.ambient = ambient;
		this.diffuse = diffuse;
		super.index = index;
	}

	@Override
	public void setPosition(float x, float y, float z, float w) {
		// TODO Auto-generated method stub
		this.position.x = x;
		this.position.y = y;
		this.position.z = z;
		this.position.w = w;
		this.position.normalize();
		super.genUniformBuffer(new float[][] {this.position.asFloat(), this.ambient.asFloat(), this.diffuse.asFloat()});
		Uniform.updateUniformBlock(UBO, this.uniformBuffer);
	}

	@Override
	public void setPosition(Vector4f position) {
		this.position = position;
		this.position.normalize();
		super.genUniformBuffer(new float[][] {this.position.asFloat(), this.ambient.asFloat(), this.diffuse.asFloat()});
		Uniform.updateUniformBlock(UBO, this.uniformBuffer);
	}
	@Override
	public Vector4f getPosition() {
		// TODO Auto-generated method stub
		return this.position;
	}

	
}
