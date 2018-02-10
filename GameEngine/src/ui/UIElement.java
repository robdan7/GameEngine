package ui;

import utils.math.Matrix4f;
import utils.math.Vector2f;

public interface UIElement {

	
	public abstract void render(Matrix4f mat);
	
	public abstract void onPress();
	
	public abstract void mouseCollision(Vector2f mouse);
}
