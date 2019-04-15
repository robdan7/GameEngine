package core.graphics.shading;


public enum GLSLvariableType {
	VEC4("vec4", 16), VEC3("vec3", 16), VEC2("vec2", 8), MAT4("mat4", 64);
	private String type;
	private int stride;
	private GLSLvariableType(String type, int stride) {
		this.type = type;
		this.stride = stride;
	}
	
	public int getStride() {
		return this.stride;
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
