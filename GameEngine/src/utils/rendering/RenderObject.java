package utils.rendering;

import utils.math.Matrix4f;

public interface RenderObject {

	public abstract void render(Matrix4f mat, int shader);
	
	public abstract void renderTextured(Matrix4f mat, int shader);
	
	public abstract void discard();
	
	public abstract void setShader(Shaders shader);
	
	
	
	public abstract void setDepthShader(Shaders shader);
	
	public abstract int getShader();
	
	public abstract int getDepthShader();
}

