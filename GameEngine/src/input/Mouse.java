package input;

import static org.lwjgl.glfw.GLFW.glfwSetCursorPosCallback;

import org.lwjgl.glfw.GLFW;

import static org.lwjgl.glfw.GLFW.*;

import org.lwjgl.glfw.GLFWCursorPosCallback;

import engine.Window;
import utils.math.Vector2f;

//Our MouseHandler class extends the abstract class
//abstract classes should never be instantiated so here
//we create a concrete that we can instantiate
public class Mouse extends GLFWCursorPosCallback {
	double lastX;
	double lastY;
	private final Vector2f newPosition;
	boolean isHidden;
	boolean isGrabbed;
	float movementRatio;
	Window window;
	
	int leftClickState = 0;
	
	/**
	 * 
	 * @param window - The window to bind the mouse to.
	 */
	public Mouse(Window window, float ratio) {
		this.newPosition = new Vector2f();
		this.window = window;
		glfwSetCursorPosCallback(this.window.getWindow(), (GLFWCursorPosCallback)this);
		this.movementRatio = ratio;
	}

	@Override
	public void invoke(long window, double xpos, double ypos) {
		this.newPosition.x = (float)xpos;
		this.newPosition.y = (float)ypos;
	}
	
	public boolean leftClick() {
		int state = glfwGetMouseButton(window.getWindow(), GLFW_MOUSE_BUTTON_1);
		if (this.leftClickState != 0) {
			if (state == 0) {
				this.leftClickState = 0;
			}
			return false;
		} else {
			this.leftClickState = state;
			return state == 1 ? true : false;
		}
	}
	
	/**
	 * set delta x to null.
	 */
	private void setNullDelta() {
		this.lastX = this.newPosition.x;
		this.lastY = this.newPosition.y;
	}
	
	/**
	 * 
	 * @param windowWitdth - Width of the window.
	 * @return The mouse movement relative to the window width.
	 */
	public float getDX() {
		float DX = (float)(this.newPosition.x-this.lastX)/this.window.getWidth();
		this.lastX = this.newPosition.x;
		return DX*this.movementRatio;
	}
	
	/**
	 * 
	 * @param windowHeight - Height of the window.
	 * @return The mouse movement relative to the window height.
	 */
	public float getDY() {
		float DY = (float)(this.newPosition.y-this.lastY)/this.window.getHeight();
		this.lastY = this.newPosition.y;
		return DY*this.movementRatio;
	}
	
	public Vector2f getPosition() {
		return this.newPosition;
	}
	
	/**
	 * 
	 * @return The position in screen coordinates where 0 is in the center of the screen.
	 */
	public Vector2f getScreenPosition () {
		return new Vector2f(this.newPosition.x-window.getWidth()/2, window.getHeight()/2-this.newPosition.y);
	}
	
	public void toggleHide(Window window) {
		if (this.isHidden) {
			this.showCursor(window);
			
		} else  {
			this.hideCursor(window);
		}
		this.isHidden = !this.isHidden;
		this.setNullDelta();
	}
	
	public void toggleGrab(Window window) {
		if (this.isGrabbed) {
			this.showCursor(window);
			
		} else  {
			this.grabCursor(window);
		}
		this.isGrabbed = !this.isGrabbed;
		this.setNullDelta();
	}
	
	public void hideCursor(Window window) {
		glfwSetInputMode(window.getWindow(), GLFW_CURSOR, GLFW_CURSOR_HIDDEN);
	}
	
	public void showCursor(Window window) {
		glfwSetInputMode(window.getWindow(), GLFW_CURSOR, GLFW_CURSOR_NORMAL);
	}
	
	public void grabCursor(Window window) {
		glfwSetInputMode(window.getWindow(), GLFW_CURSOR, GLFW_CURSOR_DISABLED);
	}
	
	public boolean isVisible() {
		return !(isGrabbed || isHidden);
	}
}