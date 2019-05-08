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
	 * @param index
	 * @param size
	 * @param type
	 * @param normalized
	 * @param stride
	 * @param pointer
	 * @param divisor
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
	 * @param index
	 * @param size
	 * @param type
	 * @param normalized
	 * @param stride
	 * @param pointer
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
