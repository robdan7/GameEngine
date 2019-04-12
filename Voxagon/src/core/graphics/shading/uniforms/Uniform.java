package core.graphics.shading.uniforms;

import org.w3c.dom.Element;

import core.graphics.shading.InterfaceVariable;
import core.utils.datatypes.GlueList;


/**
 * This is the internal representation of a single uniform. 
 * @author Robin
 *
 */
public class Uniform extends InterfaceVariable {
	UniformType type;
	private GlueList<Uniform> siblings;

	
	Uniform(Element root) {
		super();
		super.setQualifier("uniform");
		super.setName(root.getAttribute(Uniform.UniformSyntaxName.NAME.toString()));
		if (root.hasChildNodes()) {
			throw new RuntimeException("Uniform " + super.getName() + " has children");
		}
		
		
		super.setType(root.getAttribute(Uniform.UniformSyntaxName.TYPE.toString()));
	}
	
	/**
	 * Create a uniform with siblings. Siblings are uniforms on the same 
	 * level (in the same block etc.). This functionality can be used to check for 
	 * variables with the same name in a shader, since GLSL has no extended functionality 
	 * for error checking.
	 * @param root
	 * @param siblings
	 * @throws UniformCreationException A sibling has the same name.
	 */
	public Uniform(Element root, GlueList<Uniform> siblings) throws UniformCreationException {
		this(root);
		this.siblings = siblings;
		
		if (this.siblings.contains(this)) {
			throw new UniformCreationException("A uniform sibling has the same name: " + this.getName());
		}		
	}

	
	/**
	 * Convert a string representing of any uniform into the internal representation.
	 * @param type
	 * @return
	 * @throws UniformTypeException
	 */
	static UniformType getTypeFromString(String type) throws UniformTypeException {
		
		for (UniformType t: UniformType.values()) {
			if (t.toString().equals(type)) {
				return t;
			}
		}
		throw new UniformTypeException("Invalid uniform type");
	}


	/**
	 * Check if this uniform has siblings or not.
	 * @return
	 */
	private boolean hasSiblings() {
		return this.siblings != null && this.siblings.size() > 0;
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Uniform)) {
			
			return false;
		}
		
		if (!((Uniform)o).getName().equals(this.getName())) {
			return false;
		}
		
		return true;
	}
	
	/**
	 * 
	 * @author Robin
	 *
	 */
	public static enum UniformType {
		VEC4("vec4", 16), VEC3("vec3", 16), VEC2("vec2", 8), MAT4("mat4", 64);
		private String type;
		private int stride;
		UniformType(String type, int stride) {
			this.type = type;
			this.stride = stride;
		}
		
		public int getStride() {
			return this.stride;
		}
		
		@Override
		public String toString() {
			return this.type;
		}
	}
	
	public static enum UniformSyntaxName {
		TYPE("type"), NAME("name");
		private String representation;
		UniformSyntaxName(String s)  {
			this.representation = s;
		}
		
		@Override
		public String toString() {
			return this.representation;
		}
	}
}

class UniformTypeException  extends Exception {
	private static final long serialVersionUID = 1L;
	
	UniformTypeException() {
		this("");
	}
	
	UniformTypeException(String s) {
		super(s);
	}
	
}


