package core.graphics.renderUtils;

import core.graphics.renderUtils.UniformBufferObject.glVariableType;

public class UniformBufferMultiSource {
	private int offset; // offset in bytes.
	private String[] names;
	private glVariableType[] types;

	public UniformBufferMultiSource(glVariableType[] types, String[] names) {
		
	}
	
	int  getOffset() {
		return this.offset;
	}
	
	String[] getNames() {
		return this.names;
	}
	
	glVariableType[] getTypes() {
		return this.types;
	}
	
	public void updateSource() {
		
	}

}
