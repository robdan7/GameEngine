package core.graphics.shading.uniforms;

import java.util.HashMap;

import org.w3c.dom.Element;

import core.graphics.shading.InterfaceBlock;
import core.utils.datatypes.GlueList;

/**
 * <p>A uniform block is the internal (for shaders only) representation of the {@link UniformBlockReference}.
 * This holds all the data connected to a certain uniform, such as block name and string representations 
 * of all members in this block. </p>
 * 
 * <p>Several uniform instances of the same binding can exist, since every uniform can have different names
 * for every member. Therefore, a shaders can only use the members it defines internally. It allows for more 
 * flexibility for the programmer when it comes to adding new uniforms. The shaders are not depending on the 
 * surrounding application vice versa.</p>
 * @author Robin
 *
 */
public class UniformBlock extends InterfaceBlock implements Uniform {
	private String layout;
	private static HashMap<String, UniformBlock> createdUniforms;
	private UniformBlockReference applicationReference;	// A reference to the reference of this uniform.
	
	static {
		createdUniforms = new HashMap<String, UniformBlock>();
	}
	
	private UniformBlock(String blockName, String instanceName) {
		//this.uniformData = new InterfaceBlock("", blockName, instanceName);
		super("uniform", blockName, instanceName);
	}
	
	private UniformBlock(Element node) {
		super();
		
		super.setBlockName(node.getAttribute(UniformSyntaxNames.NAME.toString()));
		

		if (node.hasAttribute(UniformSyntaxNames.INSTANCE.toString())) {
			super.setInstanceName(UniformSyntaxNames.INSTANCE.toString());
		}
		
		if (!node.hasChildNodes()) {
			
		}
	}
	
	/**
	 * 
	 * @param node
	 * @return
	 */
	@Deprecated
	public static UniformBlock createBlock(Element node) {
		String name = node.getAttribute(UniformSyntaxNames.NAME.toString());
		if (createdUniforms.containsKey(name)) {
		
		}
		return new UniformBlock(node);
	}
	
	/**
	 * @return The next available (global) uniform block location.
	 */
	private final int getAvailableLocation() {
		//return UniformBlock.availableLocation++;
		return 0;
	}
	

	/**
	 * This holds all syntax names for different things used in a uniform block.
	 * @author Robin
	 *
	 */
	private static enum UniformSyntaxNames {
		STORAGE_QUALIFIER("qualifier"), NAME("name"), INSTANCE("instance");
		private String s;
		private UniformSyntaxNames(String s) {
			this.s = s;
		}
		
		@Override
		public String toString() {
			return this.s;
		}
	}
}
