package core.graphics.shading;

/**
 * The interface variable is a singular variable used for uniforms and attributes 
 * etc. This type of variable can be put in a block or used individually (not recommended).
 * @author Robin
 *
 */
public class InterfaceVariable {
	private String storageQualifier = "", type = "", name = "";

	/**
	 * 
	 * @param qualifier - The GLSL qualifier. E.g. uniform, attribute of buffer.
	 * @param type - The GLSL type represented as a string. No type check is done for this.
	 * @param name - The instance name of the variable.
	 */
	protected InterfaceVariable(String qualifier,String type, String name) {
		this.setQualifier(qualifier);
		this.setName(name);
		this.setType(type);
	}
	
	/**
	 * 
	 * @param type - The GLSL type represented as a string. No type check is done for this.
	 * @param name - The instance name of the variable.
	 */
	protected InterfaceVariable(String type, String name) {
		this.setName(name);
		this.setType(type);
	}
	
	/**
	 * Protected constructor.
	 */
	protected InterfaceVariable() {
		
	}
	
	protected void setName(String name) {
		this.name = name;
	}
	
	protected void setQualifier(String qualifier) {
		this.storageQualifier = qualifier;
	}
	
	protected void setType(String type) {
		this.type = type;
	}
	
	protected String getQualifier() {
		return this.storageQualifier;
	}
	
	protected String getName() {
		return this.name;
	}
	
	protected String getType() {
		return this.type;
	}
	
	@Override
	public String toString() {
		return this.getQualifier() + " " + this.getType() + " "+ this.getName() + ";";
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof InterfaceVariable)) {
			return false;
		}
		
		if (this.getName().equals(((InterfaceVariable)o).getName()) && this.getQualifier().equals(((InterfaceVariable)o).getQualifier())
				&& this.getType().equals(((InterfaceVariable)o).getType())) {
			return true;
		}
		
		return false;
	}

}
