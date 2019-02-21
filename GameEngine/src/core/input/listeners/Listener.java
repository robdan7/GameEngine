package core.input.listeners;

interface Listener<L extends Listener<L,O>,O extends Observer<O,L>> {
	public void update(Observer<O,L> b, Object arg);
}
