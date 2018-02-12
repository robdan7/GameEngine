package core.input.listeners;

import java.util.ArrayList;

import core.engine.Window;
import core.utils.math.Vector2f;

public class MouseController extends Observer<MouseListener>{
	//ArrayList<MouseListener> listeners;
	private boolean lClick = false;
	private boolean rClick = false;
	private Vector2f position;
	
	private boolean isHidden = false;
	private boolean isGrabbed = false;
	
	public MouseController() {
		this.listeners = new ArrayList<>();
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
		return this.position.x;
	}
	
	public float getY() {
		return this.position.y;
	}
	
	public void setX(float x) {
		this.position.x = x;
	}
	
	public void setY(float y) {
		this.position.y = y;
	}
	
	public Vector2f getScreenPosition(Window window) {
		return new Vector2f(this.position.x-window.getWidth()/2, window.getHeight()/2-this.position.y);
	}
	
	protected void notifyLeftClick(boolean state) {
		for (MouseListener e : this.listeners) {
			if (state) {
				e.leftClick(this);
			} else {
				e.leftClickRelease(this);
			}
		}
	}
	
	protected void notifyLeftClick() {
		if (this.lClick) {
			this.notifyLeftClick(this.lClick);
		}
	}
	
	protected void notifyRightClick(boolean state) {
		for (MouseListener e : this.listeners) {
			if (state) {
				e.rightClick(this);
			} else {
				e.rightClickRelease(this);
			}
		}
	}
	
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
