package core.graphics.ui;

import core.input.MouseListener;
import core.input.MouseObserver;
import core.utils.event.Observer;
import core.utils.math.Vector2f;

import java.util.function.BiFunction;

public class InputPointer implements MouseListener, Runnable {
	BiFunction<Observer<Object, MouseObserver, MouseListener>, Object, MouseListener> action;

	public InputPointer(BiFunction<Observer<Object, MouseObserver, MouseListener>, Object, MouseListener> action) {
		this.action = action;
	}

	@Override
	public void update(Observer<Object, MouseObserver, MouseListener> b, Object arg) {
		MouseListener listener = this.action.apply(b, arg);
		listener.update(b, arg);
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void buttonclick(int button) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void buttonRelease(int button) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deltaMovement(MouseObserver obs, Vector2f v) {
		// TODO Auto-generated method stub
		
	}

}
