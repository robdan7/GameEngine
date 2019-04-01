package core.entities;

import core.utils.math.Vector3f;

public class Model {
	private Vector3f position;
	private Mesh modelMesh;
	private String name;

	public Model(String name, Vector3f position, Mesh mesh) {
		this.position = position;
		this.modelMesh = mesh;
	}

}
