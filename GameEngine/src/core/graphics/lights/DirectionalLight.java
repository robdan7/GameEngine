package core.graphics.lights;

import core.graphics.renderUtils.uniforms.*;
import core.utils.math.Vector;
import core.utils.math.Vector4f;

public class DirectionalLight extends Light{
	private Vector specular;
	private Vector diffuse;
	private Vector ambient;
	private Vector position;
	/**
	 * 
	 * @param position - The position.
	 * @param diffuse - Diffuse lighting.
	 * @param ambient - Ambient lighting.
	 */
	public DirectionalLight (Vector position, Vector diffuse, Vector ambient, Vector specular, int index, String uniformFile) {
		super(position.normalize(), diffuse, ambient, specular, index, uniformFile);
		this.position = position.normalize();
		this.ambient = ambient;
		this.diffuse = diffuse;
		this.specular = specular;
	}

	@Override
	public void setPosition(float x, float y, float z, float w) {
		this.position.x = x;
		this.position.y = y;
		this.position.z = z;
		this.position.w = w;
		this.position.normalize();
		float[] data = super.to1dArray(new float[][] {this.position.asFloat(), this.ambient.asFloat(), this.diffuse.asFloat(), this.specular.asFloat()});
		super.updateUniform(data);
	}

	@Override
	public void setPosition(Vector4f position) {
		this.setPosition(position.x,position.y,position.z,position.w);
	}
	@Override
	public Vector getPosition() {
		// TODO Auto-generated method stub
		return this.position;
	}

	
}
