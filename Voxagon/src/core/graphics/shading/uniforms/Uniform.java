package core.graphics.shading.uniforms;

import org.w3c.dom.Element;

import core.graphics.shading.GLSLvariableType;
import core.graphics.shading.InterfaceVariable;
import core.utils.datatypes.GlueList;

import static core.graphics.shading.GLSLvariableType.*;


/**
 * This is the internal representation of a single uniform. 
 * @author Robin
 *
 */
public class Uniform extends InterfaceVariable {
	GLSLvariableType type;
	private GlueList<Uniform> siblings;

	
	Uniform(Element root) {
		super();
		super.setQualifier("uniform");
		super.setName(root.getAttribute(Uniform.UniformSyntaxName.NAME.toString()));
		if (root.hasChildNodes()) {
			throw new RuntimeException("Uniform " + super.getName() + " has children");
		}
		
		
		super.setType(root.getAttribute(Uniform.UniformSyntaxName.TYPE.toString()));
		try {
			this.type = getTypeFromString(root.getAttribute(Uniform.UniformSyntaxName.TYPE.toString()));
		} catch (Exception e) {
			// TODO Fix a proper exception.
			e.printStackTrace();
		}
		super.setStride(this.type.getStride());
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


	public GLSLvariableType getType() {
		return this.type;
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


