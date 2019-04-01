package core.utils.event;

import core.utils.datatypes.GlueList;

/**
 * This class represents an observer that can notify listeners when something happens.
 * An observer is the "whistle blower".
 * @author Robin
 *
 * @param <O>
 * @param <L>
 */
public class Observer<T, O extends Observer<T,O,L>, L extends Listener<T,L,O>> {
	protected GlueList<L> listeners;
	int i;
	
	
	public Observer() {
		this.listeners = new GlueList<L>();
	}
	
	/**
	 * Notify all listeners that are currently connected to this observer.
	 * @param arg - T an argument of pre-specified type.
	 */
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
