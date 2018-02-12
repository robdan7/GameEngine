package core.graphics.lights;

import core.graphics.renderUtils.Uniform;
import core.utils.math.Vector;

public class DirectionalLight extends Light{
	private Vector diffuse;
	private Vector ambient;
	private Vector position;
	/**
	 * 
	 * @param position - The position.
	 * @param diffuse - Diffuse lighting.
	 * @param ambient - Ambient lighting.
	 */
	public DirectionalLight (Vector position, Vector diffuse, Vector ambient, int index) {
		super(position.normalize(), diffuse, ambient, index);
		this.position = position.normalize();
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
	public void setPosition(Vector position) {
		this.position = position;
		this.position.normalize();
		super.genUniformBuffer(new float[][] {this.position.asFloat(), this.ambient.asFloat(), this.diffuse.asFloat()});
		Uniform.updateUniformBlock(UBO, this.uniformBuffer);
	}
	@Override
	public Vector getPosition() {
		// TODO Auto-generated method stub
		return this.position;
	}

	
}
