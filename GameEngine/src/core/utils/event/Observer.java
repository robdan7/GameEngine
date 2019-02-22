package core.utils.event;

import java.util.ArrayList;

/**
 * This class represents an observer that can notify listeners when something happens.
 * An observer is the "whistle blower".
 * @author Robin
 *
 * @param <O>
 * @param <L>
 */
public class Observer<T, O extends Observer<T,O,L>, L extends Listener<T,L,O>> {
	ArrayList<L> listeners;
	int i;
	
	
	public Observer() {
		this.listeners = new ArrayList<L>();
	}
	
	public void notifyListeners(T arg) {
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
