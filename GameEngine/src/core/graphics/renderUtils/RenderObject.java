package core.graphics.renderUtils;

import core.utils.math.Matrix4f;

public interface RenderObject {

	public void render();
	
	public void renderTextured();
	
	public void discard();
	
	public void setShader(Shaders shader);
	
	public void setDepthShader(Shaders shader);
	
	public int getShader();
	
	public int getDepthShader();
}

