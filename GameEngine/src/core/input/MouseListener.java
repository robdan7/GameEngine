package core.input;

import core.utils.event.Listener;
import core.utils.event.Observer;
import core.utils.math.Vector2f;

public abstract class MouseListener implements Listener<Object, MouseListener, MouseObserver>{

	@Override
	public void update(Observer<Object, MouseObserver, MouseListener> b, Object arg) {
		if (!(b instanceof MouseObserver)) {
			throw new Error("Not right observer");
		}
		
		this.deltaMovement(((MouseObserver)b), ((MouseObserver)b).getDeltaP());
		//this.leftClick(((MouseObserver)b));
		//this.rightClick(((MouseObserver)b));
		//this.mouseMovement(((MouseObserver)b), ((MouseObserver)b).getPosition());
	}
	
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
