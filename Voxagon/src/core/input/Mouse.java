package core.input;

import static org.lwjgl.glfw.GLFW.glfwSetCursorPosCallback;
import static org.lwjgl.glfw.GLFW.glfwSetMouseButtonCallback;

import org.lwjgl.glfw.GLFW;

import static org.lwjgl.glfw.GLFW.*;

import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;

import core.engine.Window;
import core.utils.math.Vector2f;

/**
 * This class represents the actual mouse.
 * N.B. No mouse input will be received unless glfwPollEvents is called.
 * @author Robin
 *
 */
public class Mouse extends MouseObserver{
	//double lastX;
	//double lastY;
	
	float movementRatio;
	Window window;
	GLFWCursorPosCallback cursorCallback;
	GLFWMouseButtonCallback buttonCallback;
	
	private boolean isHidden = false;
	private boolean isGrabbed = false;
	
	private Vector2f position, deltaPosition;
	
	
	/**
	 * Create the mouse and connect it to a window.
	 * @param window - The window to bind the mouse to.
	 * @param ratio - Input to movement ratio.
	 */
	public Mouse(Window window, float ratio) {
		super();
		
		this.movementRatio = ratio;
		this.position = new Vector2f();
		this.deltaPosition = new Vector2f();
		
		this.cursorCallback = new GLFWCursorPosCallback() {
			@Override
			public void invoke(long arg0, double x, double y) {
				//setX((float)x);
				//setY((float)y);
				updateX((float)x);
				updateY((float)y);
				notifyListeners(deltaPosition);
				//notifyMouseMovement();
			}
		};
		this.buttonCallback = new GLFWMouseButtonCallback() {
			@Override
			public void invoke(long window, int button, int state, int arg3) {
				if(button == 1) {
					//setRightClick(state == 1 ? true : false);
					//notifyRightClick(state == 1 ? true : false);
				} else {
					//setLeftClick(state == 1 ? true : false);
					//notifyLeftClick();
				}
			}
		};
		this.window = window;
		glfwSetCursorPosCallback(this.window.getWindow(), this.cursorCallback);
		glfwSetMouseButtonCallback(this.window.getWindow(), this.buttonCallback);

	}
	
	/**
	 * set delta x to null.
	 */
	@Deprecated
	private void setNullDelta() {
		//this.lastX = this.getPosition().getX();
		//this.lastY = this.getPosition().getY();
	}
	

	/**
	 * Update the mouse position and calculate the delta multiplied by the movement ratio.
	 * @param x - The new x position.
	 */
	private void updateX(float x) {
		float DX = (float)(x-this.getPosition().getX())/this.window.getWidth()*this.movementRatio;
		this.deltaPosition.setX(DX);
		this.position.setX(x);
		//this.lastX = this.getPosition().getX();
		//return DX*this.movementRatio;
	}
	

	/**
	 * Update the mouse position and calculate the delta multiplied by the movement ratio.
	 * @param y - The new y position.
	 */
	private void updateY(float y) {
		float DY = (float)(y-this.getPosition().getY())/this.window.getHeight()*this.movementRatio;
		this.deltaPosition.setY(DY);
		this.position.setY(y);
		//this.lastY = this.getPosition().getY();
		//return DY*this.movementRatio;
	}
	
	public float getX() {
		return this.position.getX();
	}

	public float getY() {
		return this.position.getY();
	}

	public void setX(float x) {
		this.position.setX(x);
		GLFW.glfwSetCursorPos(window.getWindow(), this.position.getX(), this.position.getY());
	}

	public void setY(float y) {
		this.position.setY(y);
		GLFW.glfwSetCursorPos(window.getWindow(), this.position.getX(), this.position.getY());
	}
	
	@Override
	protected Vector2f getPosition() {
		return this.position;
	}
	
	/**
	 * 
	 * @return The position in screen coordinates. 0 is in the center of the screen.
	 */
	public Vector2f getScreenPosition () {
		return new Vector2f(this.getPosition().getX()-window.getWidth()/2, window.getHeight()/2-this.getPosition().getY());
	}
	
	/**
	 * 
	 * @return The position relative to the screen size.
	 */
	public Vector2f getNormalizedPosition() {
		return new Vector2f((getX()/window.getWidth())*2-1,-getY()/window.getHeight()*(2)+1);
	}
	
	public boolean isHidden() {
		return this.isHidden;
	}
	
	public boolean isGrabbed() {
		return this.isGrabbed;
	}
	
	public boolean isVisible() {
		return !(this.isGrabbed || this.isHidden);
	}
	
	/**
	 * Hide and show the mouse cursor.
	 */
	public void toggleHide() {
		this.isHidden = !this.isHidden;
		if (this.isHidden()) {
			this.showCursor(this.window);
			
		} else  {
			this.hideCursor(this.window);
		}
		this.setNullDelta();
	}
	
	/**
	 * Grab and release the mouse. The mouse cursor will not move or be shown when it is grabbed.
	 */
	public void toggleGrab() {
		this.isGrabbed = !this.isGrabbed;
		if (!this.isGrabbed()) {
			this.showCursor(this.window);
		} else  {
			this.grabCursor(this.window);
		}
		this.setNullDelta();
	}
	
	/**
	 * Hide the mouse cursor. The cursor can still move when it is hidden.
	 * @param window
	 */
	public void hideCursor(Window window) {
		this.isHidden = true;
		glfwSetInputMode(window.getWindow(), GLFW_CURSOR, GLFW_CURSOR_HIDDEN);
	}
	
	/**
	 * Show the mouse cursor. This is pretty self explanatory.
	 * @param window
	 */
	public void showCursor(Window window) {
		this.isHidden = false;
		this.isGrabbed = false;
		glfwSetInputMode(window.getWindow(), GLFW_CURSOR, GLFW_CURSOR_NORMAL);
	}
	
	/**
	 * Grab the mouse cursor. This disables cursor movement.
	 * @param window
	 */
	public void grabCursor(Window window) {
		this.isGrabbed = true;
		glfwSetInputMode(window.getWindow(), GLFW_CURSOR, GLFW_CURSOR_DISABLED);
	}

	@Override
	public Vector2f getDeltaP() {
		return this.deltaPosition;
	}
}