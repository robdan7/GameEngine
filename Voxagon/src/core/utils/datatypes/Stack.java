package core.utils.datatypes;

import java.util.Iterator;

public class Stack <E> implements Iterable<E>{
	private Node first, last;
	private int size;

	public Stack() {
		this.first = new Node(null, null);
		this.last = new Node(null,this.first);
	}
	
	public void push (E object) {
		this.size ++;
		this.last.setObject(object);
		Node newLast = new Node(null, this.last);
		this.last = newLast;
	}
	
	public E pull() {
		if (this.last.getPrevious().equals(this.first)) {
			return null;
		}
		this.size --;
		System.out.println(this.last.getPrevious().getObject().toString());
		E result = this.last.getPrevious().getObject();
		this.last = this.last.getPrevious();
		this.last.setObject(null);
		return result;
	}
	
	public E getTop() {
		return this.last.getPrevious().getObject();
	}
	
	public int getSize() {
		return this.size;
	}
	
	private class Node {
		private E object;
		private Node previous;
		
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

	@Override
	public Iterator<E> iterator() {
		
		Iterator<E> iter = new Iterator<E>() {
			Node n = last;
			@Override
			public boolean hasNext() {
				return !n.getPrevious().equals(first);
			}

			@Override
			public E next() {
				n = last.getPrevious();
				return n.getObject();
			}
		
		};
		return iter;
	}
}
