package core.graphics.lights;

import core.utils.math.Vector4f;

public class DirectionalLight extends Light{
	private Vector4f specular;
	private Vector4f diffuse;
	private Vector4f ambient;
	private Vector4f position;
	/**
	 * 
	 * @param position - The position.
	 * @param diffuse - Diffuse lighting.
	 * @param ambient - Ambient lighting.
	 */
	public DirectionalLight (Vector4f position, Vector4f diffuse, Vector4f ambient, Vector4f specular, String uniformFile) {
		super(uniformFile, position.asNormalized().toVec4f(), diffuse.toVec4f(), ambient.toVec4f(), specular.toVec4f());
		this.position = position.asNormalized();
		this.ambient = ambient;
		this.diffuse = diffuse;
		this.specular = specular;
	}


	@Override
	public void updatePosition() {
		float[] data = super.to1dArray(new float[][] {this.position.asFloats(), this.ambient.asFloats(), this.diffuse.asFloats(), this.specular.asFloats()});
		super.updateUniform(data);
	}
	
	@Override
	public void setPosition(Vector4f v) {
		
	}
	
	@Override
	public void setPosition(float x, float y, float z, float w) {
		this.position.set(x, y, z, w);
		this.position.normalize();
		float[] data = super.to1dArray(new float[][] {this.position.asFloats(), this.ambient.asFloats(), this.diffuse.asFloats(), this.specular.asFloats()});
		super.updateUniform(data);
	}
	
	@Override
	public void bindPosition(Vector4f v) {
		
	}
	
	@Override
	public Vector4f getPosition() {
		return this.position;
	}

	
}
