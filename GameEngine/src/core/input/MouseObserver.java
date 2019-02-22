package core.input;

import core.engine.Window;
import core.utils.math.Vector2f;
import core.utils.event.*;

public abstract class MouseObserver extends Observer<Object, MouseObserver, MouseListener>{
	//ArrayList<MouseListener> listeners;

	
	public MouseObserver() {
		super();
		//this.position = new Vector2f();
	}

	/**
	 * Get the mouse position relative to the screen size.
	 * @return
	 */
	abstract Vector2f getPosition();
	
	/**
	 * Return position delta since last mouse ping.
	 * @return
	 */
	abstract Vector2f getDeltaP();
	
	public abstract boolean isVisible();
	
	public abstract boolean isHidden();
	
	public abstract boolean isGrabbed();
	
}
