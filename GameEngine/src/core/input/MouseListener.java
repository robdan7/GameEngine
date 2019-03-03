package core.input;

import core.utils.event.Listener;
import core.utils.math.Vector2f;

public interface MouseListener extends Listener<Object, MouseListener, MouseObserver>{
/*
	@Override
	public void update(Observer<Object, MouseObserver, MouseListener> b, Object arg) {
		this.deltaMovement(((MouseObserver)b), ((MouseObserver)b).getDeltaP());
	}
	*/
	
	
	
	/**
	 * A button has been pressed on the mouse.
	 * @param button
	 */
	public abstract void buttonclick(int button);

	/**
	 * A button has been released on the mouse.
	 * @param button
	 */
	public abstract void buttonRelease(int button);
	
	/**
	 * This method is called when the mouse has moved.
	 * @param obs - The mouse observer.
	 * @param v - The vector with delta x and y.
	 */
	public abstract void deltaMovement(MouseObserver obs, Vector2f v);
}
