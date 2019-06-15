package core.utils.datatypes.buffers.pointers;

import java.nio.Buffer;

/**
 * The buffer pointer is an abstraction of a memory pointer such as
 * in C. The pointer simpy points at a buffer object, which can 
 * be interchanged at any time.
 * @author Robin
 *
 * @param <B>
 */
public interface BufferPointer<B extends Buffer> {

	/**
	 * Change the buffer source. Be careful when performing this action, since 
	 * a smaller buffer can cause unwanted errors and lost data!
	 * @param buffer
	 */
	public void changeBuffer(B buffer);
	
	/**
	 * 
	 * @return The buffer source.
	 */
	public B getBuffer();
}
