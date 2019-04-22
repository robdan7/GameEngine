package core.graphics.shading;


public enum GLSLvariableType {
	VEC4("vec4", 16,4), VEC3("vec3", 16,4), VEC2("vec2", 8,2), MAT4("mat4", 64,16);
	private String type;
	private int stride, size;
	private GLSLvariableType(String type, int stride, int size) {
		this.type = type;
		this.stride = stride;
		this.size = size;
	}
	
	public int getbytes() {
		return this.stride;
	}
	
	public int getSize() {
		return this.size;
	}
	

	/**
	 * Convert a string representing of any uniform into the internal representation.
	 * @param type
	 * @return
	 */
	public static GLSLvariableType getTypeFromString(String type)  {
		
		for (GLSLvariableType t: GLSLvariableType.values()) {
			if (t.toString().equals(type)) {
				return t;
			}
		}
		throw new RuntimeException("");
		//throw new UniformTypeException("Invalid uniform type");
	}
	
	@Override
	public String toString() {
		return this.type;
	}
}
