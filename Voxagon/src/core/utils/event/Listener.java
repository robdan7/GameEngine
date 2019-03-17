package core.utils.event;

public interface Listener<T, L extends Listener<T,L,O>,O extends Observer<T,O,L>> {
	public void update(Observer<T,O,L> b, T arg);
}
