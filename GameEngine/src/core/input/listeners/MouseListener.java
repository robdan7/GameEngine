package core.input.listeners;

import core.utils.math.Vector2f;

public abstract class MouseListener implements Listener<MouseListener, MouseObserver>{

	@Override
	public void update(Observer<MouseObserver, MouseListener> b, Object arg) {
		if (!(b instanceof MouseObserver)) {
			throw new Error("Not right observer");
		}
		
		this.leftClick(((MouseObserver)b));
		this.rightClick(((MouseObserver)b));
		this.mouseMovement(((MouseObserver)b), ((MouseObserver)b).getPosition());
	}
	
	public abstract void leftClick(MouseObserver obs);
	
	public abstract void leftClickRelease(MouseObserver obs);
	
	public abstract void rightClick(MouseObserver obs);
	
	public abstract void rightClickRelease(MouseObserver obs);
	
	public abstract void mouseMovement(MouseObserver obs, Vector2f v);
}
