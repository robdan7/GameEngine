package core.utils.datatypes;

import java.util.Iterator;

/**
 * Normal stack class. Nothing special here.
 * @author Robin
 *
 * @param <E>
 */
public class Stack <E> implements Iterable<E>{
	private Node first, last;
	private int size;

	public Stack() {
		this.first = new Node(null, null);
		this.last = new Node(null,this.first);
	}
	
	/**
	 * Add an element to the top of the stack.
	 * @param object
	 */
	public void push (E object) {
		this.size ++;
		this.last.setObject(object);
		Node newLast = new Node(null, this.last);
		this.last = newLast;
	}
	
	/**
	 * Remove the element at the top of the stack.
	 * @return
	 */
	public E pull() {
		if (this.last.getPrevious().equals(this.first)) {
			return null;
		}
		this.size --;
		E result = this.last.getPrevious().getObject();
		this.last = this.last.getPrevious();
		this.last.setObject(null);
		return result;
	}
	
	/**
	 * Remove an item from the stack. Null is returned if the item does not exist.
	 * @param item - The item to remove.
	 * @return True if the item was remove, false otherwise.
	 */
	public boolean extract(E item) {
		if (item == null) throw new IllegalArgumentException();
		Node iterator = this.last;

		while(!iterator.getPrevious().equals(this.first)) {
			
			if (iterator.getPrevious().getObject().equals(item)) {
				/* We found the object. Remove the node */
				iterator.setPrevious(iterator.getPrevious().getPrevious());
				return true;
			}
			
			iterator = iterator.getPrevious();
		}
		return false;
	}
	
	/**
	 * Get the current element at the top. Null is returned if 
	 * the stack is empty.
	 * @return
	 */
	public E getTop() {
		return this.last.getPrevious().getObject();
	}
	
	/**
	 * Get the size of this stack.
	 * @return
	 */
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
		
		private void setPrevious(Node n) {
			this.previous = n;
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
