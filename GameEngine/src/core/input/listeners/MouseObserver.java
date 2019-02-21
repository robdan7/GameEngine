package core.input.listeners;

import core.engine.Window;
import core.utils.math.Vector2f;

public class MouseObserver extends Observer<MouseObserver, MouseListener>{
	//ArrayList<MouseListener> listeners;
	private boolean lClick = false;
	private boolean rClick = false;
	private Vector2f position;
	
	private boolean isHidden = false;
	private boolean isGrabbed = false;
	
	public MouseObserver() {
		super();
		this.position = new Vector2f();
	}
	
	public boolean getlClick() {
		return this.lClick;
	}
	
	public boolean getrClick() {
		return this.rClick;
	}
	protected void setRightClick(boolean click) {
		this.rClick = click;
	}
	
	protected void setLeftClick(boolean click) {
		this.lClick = click;
	}
	
	public Vector2f getPosition() {
		return this.position.toVec2f();
	}
	

	public float getX() {
		return this.position.getX();
	}

	public float getY() {
		return this.position.getY();
	}

	public void setX(float x) {
		this.position.setX(x);
	}

	public void setY(float y) {
		this.position.setY(y);
	}

	public Vector2f getScreenPosition(Window window) {
		return new Vector2f(this.position.getX()-window.getWidth()/2, window.getHeight()/2-this.position.getY());
	}
	
	@Deprecated
	protected void notifyMouseMovement(Vector2f v) {
		for (MouseListener e : super.listeners) {
			e.mouseMovement(this, v);
		}
	}
	
	/**
	 * The mouse cursor has been moved. Notify all listeners.
	 */
	protected void notifyMouseMovement() {
		for (MouseListener e : super.listeners) {
			e.mouseMovement(this, this.getPosition());
		}
	}
	
	/**
	 * 
	 * @param state
	 */
	@Deprecated
	protected void notifyLeftClick(boolean state) {
		for (MouseListener e : this.listeners) {
			if (state) {
				e.leftClick(this);
			} else {
				e.leftClickRelease(this);
			}
		}
	}
	
	/**
	 * The left mouse button has been clicked. Notify all listeners.
	 */
	protected void notifyLeftClick() {
		if (this.lClick) {
			this.notifyLeftClick(this.lClick);
		}
	}
	
	@Deprecated
	protected void notifyRightClick(boolean state) {
		for (MouseListener e : this.listeners) {
			if (state) {
				e.rightClick(this);
			} else {
				e.rightClickRelease(this);
			}
		}
	}
	
	/**
	 * The right mouse button has been clicked. Notify all listeners.
	 */
	protected void notifyRightClick() {
		if (this.rClick) {
			this.notifyRightClick(this.rClick);
		}
	}
	
	protected void hide() {
		this.isHidden = true;
	}
	
	protected void grab() {
		this.isGrabbed = true;
	}
	
	public void toggleGrab() {
		this.isGrabbed = !this.isGrabbed;
	}
	
	public void toggleHide() {
		this.isHidden = !this.isHidden;
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
	
	protected void show() {
		this.isHidden = false;
		this.isGrabbed = false;
	}
}
