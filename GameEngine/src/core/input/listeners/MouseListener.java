package core.input.listeners;

import core.utils.math.Vector2f;

public abstract class MouseListener implements Listener{

	@Override
	public void update(Observer b, Object arg) {
		if (!(b instanceof MouseController)) {
			throw new Error("Not right observer");
		}
		
		this.leftClick(((MouseController)b));
		this.rightClick(((MouseController)b));
		this.mouseMovement(((MouseController)b), ((MouseController)b).getPosition());
	}
	
	public abstract void leftClick(MouseController obs);
	
	public abstract void leftClickRelease(MouseController obs);
	
	public abstract void rightClick(MouseController obs);
	
	public abstract void rightClickRelease(MouseController obs);
	
	public abstract void mouseMovement(MouseController obs, Vector2f v);
}
