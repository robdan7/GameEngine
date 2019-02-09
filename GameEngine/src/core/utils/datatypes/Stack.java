package core.utils.datatypes;

public class Stack <E>{
	private Node first, last;

	public Stack() {
		this.first = new Node(null, null);
		this.last = new Node(null,this.first);
	}
	
	public void push (E object) {
		this.last.setObject(object);
		Node newLast = new Node(null, this.last);
		this.last = newLast;
	}
	
	public E pull() {
		E result = this.last.getPrevious().getObject();
		this.last = this.last.getPrevious();
		this.last.setObject(null);
		return result;
	}
	
	private class Node {
		E object;
		Node previous;
		
		private Node(E object, Node previous) {
			this.object = object;
			this.previous = previous;
		}
		
		private void setObject(E object) {
			this.object = object;
		}
		
		private E getObject() {
			return this.object;
		}
		
		private Node getPrevious() {
			return this.previous;
		}
		
		
	}
}
