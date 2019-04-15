package core.graphics.shading.attributes;

import org.w3c.dom.Element;

import core.graphics.shading.GLSLvariableType;
import core.graphics.shading.InterfaceVariable;

public class Attribute extends InterfaceVariable {
	private GLSLvariableType type;
	private int location = -1;

	public Attribute(Element root, int location) throws AttributeCreationException {
		root.setAttribute(AttributeSyntax.LOCATION.toString(), Integer.toString(location));
		this.init(root);
	}

	public Attribute(Element root) throws AttributeCreationException {
		this.init(root);
	}

	private void init(Element root) throws AttributeCreationException {
		super.setName(root.getAttribute(AttributeSyntax.NAME.toString()));

		String qualifier = root.getAttribute(AttributeSyntax.QUALIFIER.toString());

		if (!qualifier.equals(AttributeSyntax.IN.toString()) && !qualifier.equals(AttributeSyntax.OUT.toString())) {
			throw new AttributeCreationException(this.getName() + ": Unauthorized qualifier \"" + qualifier + "\"");
		}
		this.type = GLSLvariableType.getTypeFromString(root.getAttribute(AttributeSyntax.TYPE.toString()));
		super.setStride(this.type.getStride());
		super.setType(this.type.toString());
		super.setQualifier(qualifier);
		this.location = Integer.parseInt(root.getAttribute(AttributeSyntax.LOCATION.toString()));
	}
	
	public int getLocation() {
		return this.location;
	}
	
	
	public enum AttributeSyntax {
		IN("in"), OUT("out"), LOCATION("location"), NAME("name"), QUALIFIER("qualifier"), TYPE("type");
		private String s;
		AttributeSyntax(String s) {
			this.s = s;
		}
		
		@Override
		public String toString() {
			return this.s;
		}
	}
	
	@Override
	public String toString() {
		return "layout(location = " + this.location + ") " + super.toString();
	}
	
	public static class AttributeCreationException extends Exception {
		private static final long serialVersionUID = 1L;

		public AttributeCreationException() {
			super();
		}
		
		public AttributeCreationException(String s) {
			super(s);
		}
	}
}
