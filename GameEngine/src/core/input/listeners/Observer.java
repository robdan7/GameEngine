package core.input.listeners;

import java.util.ArrayList;

class Observer<E> {
	ArrayList<E> listeners;
	int i;
	
	
	public Observer() {
		this.listeners = new ArrayList<>();
	}
	
	public void notifyObservers(Object arg) {
		for (E obs : this.listeners) {
			((Listener)obs).update(this, arg);
		}
	}
	
	public void notifyListeners() {
		this.notifyObservers(null);
	}
	
	public void addListener(E l) {
		this.listeners.add(l);
	}
	
	public void removeListener(E l) {
		this.listeners.remove(l);
	}
}
