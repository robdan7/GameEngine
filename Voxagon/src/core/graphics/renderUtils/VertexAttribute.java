package core.graphics.renderUtils;

import org.lwjgl.opengl.GL20;
import org.lwjgl.system.NativeType;
import org.w3c.dom.Element;

public class VertexAttribute {
	private int index, size, type, stride, divisor;
	private long pointer;
	private boolean normalized;

	/**
	 * 
	 * @param index      - Specifies the index of the generic vertex attribute to be
	 *                   modified.
	 * @param size       - Specifies the number of components per generic vertex
	 *                   attribute. Must be 1,2,3,4.
	 * @param type       - Specifies the data type of each component in the array.
	 * @param normalized - Specifies wether fixed-point data should be normalized or
	 *                   converted directly as fixed-point values when they are
	 *                   accessed.
	 * @param stride     - Specifies the byte offset between consecutive attributes.
	 *                   0 for tightly packed array.
	 * @param pointer    - Specifies an offset of the first component of the first
	 *                   generic vertex attribute in the array in the data store of
	 *                   the buffer.
	 * @param divisor - Specifies the number of instances that will pass between updates of the generic attribute.
	 */
	public VertexAttribute(@NativeType(value = "GLuint") int index, @NativeType(value = "GLint") int size,
			@NativeType(value = "GLenum") int type, @NativeType(value = "GLboolean") boolean normalized,
			@NativeType(value = "GLsizei") int stride, @NativeType(value = "void cont *") long pointer,
			@NativeType(value = "GLuint") int divisor) {
		this.index = index;
		this.size = size;
		this.type = type;
		this.stride = stride;
		this.pointer = pointer;
		this.normalized = normalized;
		this.divisor = divisor;
	}

	/**
	 * 
	 * @param index      - Specifies the index of the generic vertex attribute to be
	 *                   modified.
	 * @param size       - Specifies the number of components per generic vertex
	 *                   attribute. Must be 1,2,3,4.
	 * @param type       - Specifies the data type of each component in the array.
	 * @param normalized - Specifies wether fixed-point data should be normalized or
	 *                   converted directly as fixed-point values when they are
	 *                   accessed.
	 * @param stride     - Specifies the byte offset between consecutive attributes.
	 *                   0 for tightly packed array.
	 * @param pointer    - Specifies an offset of the first component of the first
	 *                   generic vertex attribute in the array in the data store of
	 *                   the buffer.
	 */
	public VertexAttribute(@NativeType(value = "GLuint") int index, @NativeType(value = "GLint") int size,
			@NativeType(value = "GLenum") int type, @NativeType(value = "GLboolean") boolean normalized,
			@NativeType(value = "GLsizei") int stride, @NativeType(value = "void cont *") long pointer) {
		this(index,size,type,normalized,stride,pointer,0);
	}

	protected VertexAttribute(Element root) {
		this.index = Integer.parseInt( root.getAttribute("index"));
		
		switch(root.getAttribute("type")) {
		case "vec4":
			
			break;
		}
		
	}
	
	public int getDivisor() {
		return this.divisor;
	}
	
	public int getIndex() {
		return this.index;
	}

	public void bindAttribute() {
		GL20.glVertexAttribPointer(this.index, this.size, this.type, this.normalized, this.stride, this.pointer);
	}
}
