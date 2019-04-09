package core.graphics.shading;

import core.utils.datatypes.GlueList;

interface ShaderDataStorage {
	
}

/**
 * Represents a GLSL interface block of any kind; buffer, uniform or attribute. 
 * It is up to the implementer to decide name and storage qualifier for the block.
 * Use this as an internal representation of the data you want to store, do 
 * not use as-is.
 * @author Robin
 *
 */
public class InterfaceBlock implements ShaderDataStorage {
	private String storageQualifier = "", blockName = "", instanceName = "";
	GlueList<ShaderVariable> members;
	
	/**
	 * @param qualifier - The storage qualifier. uniform, in or out etc.
	 * @param blockName - The type of all instances of this block.
	 * @param instanceName - An optional instance name.
	 */
	public InterfaceBlock(String qualifier, String blockName, String instanceName) {
		this.setQualifier(qualifier);
		this.setBlockName(blockName);
		this.setInstanceName(instanceName);
	}
	
	/**
	 * @param qualifier - The storage qualifier. uniform, in or out etc.
	 * @param blockName - The type of all instances of this block.
	 */
	public InterfaceBlock(String qualifier, String blockName) {
		this(qualifier, blockName, "");
	}
	
	protected InterfaceBlock() {
		this("","","");
	}
	
	/**
	 * Add variable to this block. 
	 * @param member
	 */
	void addMember(ShaderVariable member) {
		this.members.add(member);
	}
	
	protected void setQualifier(String qualifier) {
		this.storageQualifier = qualifier;
	}
	
	protected void setBlockName(String name) {
		this.blockName = name;
	}
	
	protected void setInstanceName(String name) {
		this.instanceName = name;
	}
	
	protected String getQualifier() {
		return this.storageQualifier;
	}
	
	protected String getInstanceName() {
		return this.instanceName;
	}
	
	protected String getBlockName() {
		return this.blockName;
	}
	
	@Override
	public String toString() {
		String result = this.getQualifier() + " " + this.getBlockName() + "{ ";
		for(ShaderVariable v : this.members) {
			result += v.toString();
		}
		result += "}" + this.getInstanceName() + ";";
		return result;
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof InterfaceBlock)) {
			return false;
		}
		
		if (this.getBlockName().equals(((InterfaceBlock)o).getBlockName()) && this.getQualifier().equals(((InterfaceBlock)o).getQualifier())) {
			return true;
		}
		
		return false;
	}
}

class ShaderVariable implements ShaderDataStorage {
	private String qualifier, name;

	public ShaderVariable() {
		// TODO Auto-generated constructor stub
	}
	
	protected String getName() {
		return this.name;
	}
	
	protected String getQualifier() {
		return this.qualifier;
	}
	
	@Override
	public String toString() {
		String result = this.qualifier + " " + this.name + ";";
		return result;
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof ShaderVariable)) {
			return false;
		}
		
		if (this.getName().equals(((ShaderVariable)o).getName()) && this.getQualifier().equals(((ShaderVariable)o).getName())) {
			return true;
		}
		
		return false;
	}

}