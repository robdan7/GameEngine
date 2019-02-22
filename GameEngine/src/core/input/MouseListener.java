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
	
	public abstract void buttonclick(int button);

	public abstract void buttonRelease(int button);
	
	public abstract void deltaMovement(MouseObserver obs, Vector2f v);
}
