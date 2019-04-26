package core.utils.datatypes.buffers;

import java.nio.Buffer;

/**
 * A buffer partition is a partition with a fixed length within a buffer. The partition itself is not 
 * allowed to read or write outside its own mark and limit. This allows the user to divide one singular buffer 
 * into smaller portions, thus allowing several objects to access the buffer without their own buffers as 
 * remporary storage. Note that there is no multi-thread support built in.
 * @author Robin
 *
 * @param <BUFF>
 */
public abstract class BufferPartition<BUFF extends Buffer> {
	private int start, stop;
	private int limit, position, mark;
	private BUFF buffer;
	private boolean markDefined = false;

	public BufferPartition(BUFF buffer, int start, int stop) {
		this.buffer = buffer;
		this.start = start;
		this.stop = stop;
		this.limit = stop-1;
		this.position = start;
	}
	
	/**
	 * Prepare the buffer before using the allocated partition.
	 * The limit is set to the current limit, the position is set to 
	 * the current position and the mark is set to the current mark. Invoke this
	 * method before reading or writing to the buffer.
	 */
	protected final void setActivePartition() {
		this.getBuffer().clear();
		this.getBuffer().limit(this.limit);
		this.getBuffer().position(this.mark);
		this.getBuffer().mark();
		this.getBuffer().position(this.position);
	}
	
	protected final BUFF getBuffer() {
		return this.buffer;
	}

	
	protected final int getStart() {
		return this.start;
	}
	
	protected final int getStop() {
		return this.stop;
	}
	

	public final void flip() {
		this.limit = this.position;
		this.position = this.start;
		this.mark = this.start;
		if (this.markDefined) this.markDefined = false;
	}
	
	public final int capacity() {
		return this.stop-this.start;
	}
	
	
	/**
	 * Get the position of this buffer partition. The position is relative to the start of 
	 * this partition, not the entire buffer.
	 * @return
	 */
	public final int position() {
		return this.position-this.start;
	}
	
	public final void position(int position) {
		position = position + this.start;
		if ( position >= this.start && position < this.stop) {
			if (!(this.markDefined && this.mark > position) ) {
				this.position = position;
			}
			
		} else {
			throw new IndexOutOfBoundsException(": " + position + " is not in range " + this.start + " to " + this.stop);
		}
	}
	
	/**
	 * Increase the position of this partition by one (1).
	 */
	public final void positionPP() {
		if (this.position < this.limit) {
			this.position ++;
		}
	}
	
	/**
	 * Get the limit of this buffer partition. The limit is relative to the start of this 
	 * partition, not the entire buffer.
	 * @return
	 */
	public final int limit() {
		return this.limit-this.start;
	}
	
	public final void limit(int limit) {
		limit = limit + this.start;
		if (this.position > limit && !(this.mark > limit && this.markDefined)) {
			this.limit = limit+this.start;
			this.markDefined = true;
		}
	}
	
	/**
	 * Set the mark of this buffer partition. 
	 */
	public final void mark() {
		this.mark = this.position;
	}
	
	/**
	 * Get the current mark of this buffer. The mark is relative to the start 
	 * of this partition, not the entire buffer.
	 * @return
	 */
	public final int getMark() {
		return this.mark-this.start;
	}
	
	public final void reset() {
		this.position = this.mark;
	}
	
	public final void clear() {
		this.position = this.start;
		this.limit = this.stop-1;
		this.mark = this.start;
		this.markDefined = false;
	}
	
	public final void rewind() {
		this.position = this.start;
		this.mark = this.start;
	}
	
	public final int remaining() {
		return this.limit - this.position;
	}
	
	public final boolean hasRemaining() {
		return this.limit - this.position > 0;
	}
	
	public boolean isReadOnly() {
		return this.buffer.isReadOnly();
	}
}
