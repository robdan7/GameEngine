package core.entities;

import org.lwjgl.opengl.GL11;

import core.utils.datatypes.GlueList;
import core.utils.math.Vector;
import core.utils.math.Vector2f;
import core.utils.math.Vector3f;

public class Mesh {
	private GlueList<Vector3f> vertices, normals;
	private GlueList<? extends Vector<?>> faces;
	private GlueList<Vector2f> uv;
	private String name;

	public Mesh(String name, GlueList<Vector3f> vertices, GlueList<Vector3f> normals, GlueList<? extends Vector<?>> faces, GlueList<Vector2f> uv) {
		this.vertices = vertices;
		this.normals = normals;
		this.uv = uv;
		this.faces = faces;
		this.name = name;
	}

	/**
	 * 
	 * @return All vertices in this mesh.
	 */
	GlueList<Vector3f> getVertices() {
		return this.vertices;
	}

	/**
	 * 
	 * @return All texture coordinates.
	 */
	GlueList<Vector2f> getUVcoords() {
		return this.uv;
	}

	/**
	 * @return All normals.
	 */
	GlueList<Vector3f> getNormals() {
		return this.normals;
	}

	public static enum faceVertices {
		THREE(3), FOUR(4);
		private int vertices;
		private int glValue;

		private faceVertices(int i) {
			this.vertices = i;

			switch (i) {
			case 3:
				this.glValue = GL11.GL_TRIANGLES;
				break;
			case 4:
				this.glValue = GL11.GL_QUADS;
				break;
			}
		}

		/**
		 * 
		 * @return Vertices per face.
		 */
		public int value() {
			return this.vertices;
		}

		/**
		 * @return The GL drawing type.
		 */
		public int GLValue() {
			return this.glValue;
		}
	}
}
