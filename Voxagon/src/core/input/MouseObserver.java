package core.input;

import core.utils.math.Vector2f;
import core.utils.event.*;

/**
 * This is a place holder for any class that can 
 * notify listeners when a mouse action has been activated.
 * @author Robin
 *
 */
public abstract class MouseObserver extends Observer<Object, MouseObserver, MouseListener>{
	
	public MouseObserver() {
		super();
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
	public abstract Vector2f getDeltaP();
	
	public abstract boolean isVisible();
	
	public abstract boolean isHidden();
	
	public abstract boolean isGrabbed();
	
}
