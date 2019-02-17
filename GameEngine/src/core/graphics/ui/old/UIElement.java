package core.graphics.ui;

import core.utils.math.Matrix4f;
import core.utils.math.Vector2f;

public interface UIElement {

	
	public abstract void render(Matrix4f mat);
	
	public abstract void onPress();
	
	public abstract void mouseCollision(Vector2f mouse);
}
