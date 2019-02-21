package core.input.listeners;

import java.util.ArrayList;

/**
 * This class represents an observer that can notify listeners when something happens.
 * An observer is the "whistle blower".
 * @author Robin
 *
 * @param <O>
 * @param <L>
 */
class Observer<O extends Observer<O,L>, L extends Listener<L,O>> {
	ArrayList<L> listeners;
	int i;
	
	
	public Observer() {
		this.listeners = new ArrayList<L>();
	}
	
	public void notifyListeners(Object arg) {
		for (L listener : this.listeners) {
			listener.update(this, arg);
		}
	}
	
	
	public void addListener(L l) {
		this.listeners.add(l);
	}
	
	public void removeListener(L l) {
		this.listeners.remove(l);
	}
}
