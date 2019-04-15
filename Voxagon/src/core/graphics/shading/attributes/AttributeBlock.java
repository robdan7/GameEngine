package core.graphics.shading.attributes;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import core.graphics.shading.InterfaceBlock;
import core.graphics.shading.InterfaceVariable;
import core.graphics.shading.attributes.Attribute.AttributeCreationException;
import core.utils.datatypes.GlueList;

public class AttributeBlock extends InterfaceBlock {
	private int location = -1;
	
	/**
	 * Create an attribute with a provided location. This location will override any previous 
	 * location attribute.
	 * @param root - The root node for the attribute.
	 * @param location - The GLSL attribute location.
	 * @throws AttributeCreationException - The attribute was not initiated properly.
	 */
	public AttributeBlock(Element root, int location) throws AttributeCreationException {
		root.setAttribute(AttributeBlockSyntax.LOCATION.toString(), Integer.toString(location));
		this.init(root);
	}
	
	/**
	 * Create an attribute from the root node containing the data.
	 * @param root - The root node for the attribute.
	 * @throws AttributeCreationException - The attribute was not initiated properly.
	 */
	public AttributeBlock(Element root) throws AttributeCreationException {
		this.init(root);
	}
	
	private void init(Element root) throws AttributeCreationException {
		String qualifier = root.getAttribute(AttributeBlockSyntax.QUALIFIER.toString());
		String instance = root.getAttribute(AttributeBlockSyntax.INSTANCE_NAME.toString());
		String name = root.getAttribute(AttributeBlockSyntax.NAME.toString());
		if (!qualifier.equals(AttributeBlockSyntax.IN.toString())
				&& !qualifier.equals(AttributeBlockSyntax.OUT.toString())) {
			throw new AttributeCreationException(this.getBlockName() + ": Unauthorized attribute qualifier \"" + qualifier + "\"");
		}
		
		if (root.hasAttribute(AttributeBlockSyntax.LOCATION.toString())) {
			this.location = Integer.parseInt(root.getAttribute(AttributeBlockSyntax.LOCATION.toString()));
		} else {
			throw new AttributeCreationException("Attribute " + this.getBlockName() + " has no location.");
		}

		if (this.location < 0) {
			throw new AttributeCreationException(
					this.getBlockName() + ": Location was not provided or is not allowed.");
		}
		
		super.setQualifier(qualifier);
		super.setBlockName(name);
		super.setInstanceName(instance);
		
		this.appendChildren(root);
	}
	
	private void appendChildren(Element root) throws AttributeCreationException {
		NodeList nodes = root.getChildNodes();
		Element el;

		for (int i = 0, v = 0; i < nodes.getLength(); i++) {
			if (!nodes.item(i).getNodeName().equals("#text")) {
				el = (Element) nodes.item(i);
				if (!el.hasAttribute(Attribute.AttributeSyntax.QUALIFIER.toString())) {
					el.setAttribute(Attribute.AttributeSyntax.QUALIFIER.toString(), super.getQualifier());
				}

				if (!el.hasAttribute(Attribute.AttributeSyntax.LOCATION.toString())) {
					super.addMember(new Attribute(el, v));
				} else {
					super.addMember(new Attribute(el));
					
					int tempLocation = Integer.parseInt(el.getAttribute(Attribute.AttributeSyntax.LOCATION.toString()));
					
					v = v < tempLocation ? tempLocation : v;
				}

				v++;
			}

		}
	}

	
	@Override
	public String toString() {
		String result = "layout (location = " + this.location + ") ";
		result += this.getQualifier() + " " + this.getBlockName() + " {\n";
		for(InterfaceVariable v : super.getMembers()) {
			result += v.toString();
		}
		result += "}" + this.getInstanceName() + ";\n";
		return result;
	}
	
	
	public static enum AttributeBlockSyntax {
		IN("in"), OUT("out"), NAME("name"), INSTANCE_NAME("instance"), QUALIFIER("qualifier"), LOCATION("location");
		private String s;
		AttributeBlockSyntax(String s) {
			this.s = s;
		}
		
		@Override
		public String toString() {
			return this.s;
		}
	}
	


}
