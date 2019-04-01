package core.graphics.shading;

public abstract class Shader {
	private int shaderIndex;
	
	public int getShaderIndex() {
		return this.shaderIndex;
	}
	
	public abstract String getShaderCode();

}
