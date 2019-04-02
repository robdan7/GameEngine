package core.entities;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import core.utils.math.Vector3f;

public class Model {
	private Vector3f position;
	private Mesh modelMesh;
	private String name;

	public Model(String name, Vector3f position, Mesh mesh) {
		this.position = position;
		this.modelMesh = mesh;
	}
	
	public Model(Element model) {
		// TODO this should probably not be here
		this.name = model.getAttribute("name");
		String mesh = model.getAttribute("mesh");
		
		NodeList nodes = model.getChildNodes();
		
		Element el = null;
		for (int i = 0; i < nodes.getLength(); i++) {
			if ((el = (Element)nodes.item(i)).getNodeName() == "position") {
				this.position = this.parsePosition(el);
			}
		}
	}
	
	private Vector3f parsePosition(Element position) {
		String[] vectorString = position.getTextContent().split(" ");
		
		return new Vector3f(Float.parseFloat(vectorString[0]), Float.parseFloat(vectorString[1]), Float.parseFloat(vectorString[2]));
	}

}
