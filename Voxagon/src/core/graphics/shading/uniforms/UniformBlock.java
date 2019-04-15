package core.graphics.shading.uniforms;

import java.util.HashMap;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import core.graphics.shading.GLSLvariableType;
import core.graphics.shading.InterfaceBlock;
import core.graphics.shading.InterfaceVariable;
import core.graphics.shading.uniforms.references.UniformBlockReference;
import core.graphics.shading.uniforms.references.UniformBlockReference.UniformTypeEception;
import core.graphics.shading.uniforms.references.UniformReference.ReferenceCreationException;
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
public class UniformBlock extends InterfaceBlock {
	private String layout;
	private UniformBlockReference applicationReference;	// A reference to the reference of this uniform.
	GlueList<Uniform> siblingUniforms;
	

	
	private UniformBlock(String blockName, String instanceName) {
		//this.uniformData = new InterfaceBlock("", blockName, instanceName);
		super("uniform", blockName, instanceName);
	}
	
	/**
	 * Create a uniform block based on the node containing the uniform data.
	 * @param node
	 */
	public UniformBlock(Element node) {
		this(node, null);
	}
	
	private UniformBlock(Element node, GlueList<Uniform> siblingUniforms) {
		super();
		super.setBlockName(node.getAttribute(UniformBlockSyntaxName.NAME.toString()));
		super.setQualifier("uniform");
		
		this.applicationReference = UniformBlockReference.requestBlockReference(super.getBlockName());
		

		if (node.hasAttribute(UniformBlockSyntaxName.INSTANCE.toString())) {
			super.setInstanceName(node.getAttribute(UniformBlockSyntaxName.INSTANCE.toString()));
		}
		
		if (!node.hasChildNodes()) {
			throw new NullPointerException("The uniform block is empty!");
		}
		
		this.siblingUniforms = siblingUniforms;
		
		try {
			this.createChildren(node);
		} catch (UniformTypeException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Add a new child to this uniform block.
	 * @param root
	 * @throws UniformTypeException
	 */
	private void createChildren(Element root) throws UniformTypeException {
		NodeList nodes = root.getChildNodes();
		
		Element el;
		GLSLvariableType type = null;
		for (int i = 0; i < nodes.getLength(); i++) {
			if (!nodes.item(i).getNodeName().equals("#text")) {
				el = (Element)nodes.item(i);
				
				Uniform u = new Uniform(el);
				//type = Uniform.getTypeFromString(el.getAttribute(Uniform.UniformSyntaxName.TYPE.toString()));
				
				try {
					this.applicationReference.requestNewMember(i, u.getType());
					super.addMember(new Uniform(el));
				} catch (NumberFormatException | UniformTypeEception | ReferenceCreationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	private boolean hasSiblingUniforms() {
		return this.siblingUniforms != null && this.siblingUniforms.size() > 0;
	}
	

	@Override
	public String toString() {
		String result = "layout (std140, binding = " + this.applicationReference.getbinding() + ") ";
		result += this.getQualifier() + " " + this.getBlockName() + " {\n";
		int offset = 0;
		for(InterfaceVariable v : super.getMembers()) {
			result += "layout(offset = " +offset+") " + v.toString();
			offset += v.getStride();
		}
		result += "}" + this.getInstanceName() + ";\n";
		return result;
	}

	/**
	 * This holds all syntax names for different things used in a uniform block.
	 * @author Robin
	 *
	 */
	private static enum UniformBlockSyntaxName {
		LAYOUT("layout"), NAME("name"), INSTANCE("instance");
		private String s;
		private UniformBlockSyntaxName(String s) {
			this.s = s;
		}
		
		@Override
		public String toString() {
			return this.s;
		}
	}
}
