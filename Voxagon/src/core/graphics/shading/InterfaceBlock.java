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
	GlueList<InterfaceVariable> members;
	
	/**
	 * @param qualifier - The storage qualifier. uniform, in or out etc.
	 * @param blockName - The type of all instances of this block.
	 * @param instanceName - An optional instance name.
	 */
	protected InterfaceBlock(String qualifier, String blockName, String instanceName) {
		this.setQualifier(qualifier);
		this.setBlockName(blockName);
		this.setInstanceName(instanceName);
		this.members = new GlueList<InterfaceVariable>();
	}
	
	/**
	 * @param qualifier - The storage qualifier. uniform, in or out etc.
	 * @param blockName - The type of all instances of this block.
	 */
	protected InterfaceBlock(String qualifier, String blockName) {
		this(qualifier, blockName, "");
	}
	
	protected InterfaceBlock() {
		this("","","");
	}
	
	/**
	 * Add variable to this block. 
	 * @param member
	 */
	protected void addMember(InterfaceVariable member) {
		this.members.add(member);
	}
	
	protected GlueList<InterfaceVariable> getMembers() {
		return this.members;
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
	
	/**
	 * The block name is the name used for identifying the block itself.
	 * @return The the unique name of this block.
	 */
	protected String getBlockName() {
		return this.blockName;
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
