package core.entities;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.lwjgl.opengl.GL11;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import core.utils.datatypes.GlueList;
import core.utils.fileSystem.XMLparser;
import core.utils.math.Vector;
import core.utils.math.Vector2f;
import core.utils.math.Vector3f;
import core.utils.math.Vector4f;

public class Mesh {
	private GlueList<Vector3f> vertices, normals, faces;
	private GlueList<Vector2f> uv;
	private String name;

	public Mesh(String name, GlueList<Vector3f> vertices, GlueList<Vector3f> normals, GlueList<Vector3f> faces, GlueList<Vector2f> uv) {
		this.vertices = vertices;
		this.normals = normals;
		this.uv = uv;
		this.faces = faces;
		this.name = name;
	}
	
	public Mesh(String file) {
		try {
			this.loadMesh(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Load mesh into memory
	 * @param file - Mesh file.
	 * @throws IOException the file does not have required attachments.
	 */
	private void loadMesh(String file) throws IOException {
		Document doc = null;
		try {
			doc = XMLparser.createParser(file);
			//System.out.println(vert.getTextContent());
			//parseVertices(vert);
		} catch (SAXException | IOException | ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Element root = doc.getDocumentElement();
		this.name = root.getAttribute("name");
		NodeList nodes = root.getChildNodes();
		for (int i = 0; i<nodes.getLength(); i++) {
			Element n = (Element)nodes.item(i);
			
			switch(n.getNodeName()) {
			case "vertex":
				this.vertices = this.parseVertices(n);
				break;
			case "normal":
				this.normals = this.parseNormals(n);
				break;
			case "face":
				this.faces = this.parseFaces(n);
				break;
			case "uv":
				this.uv = this.parseUV(n);
				break;
			}
			
		}
		if (!this.hasFaces()) {
			throw new IOException("Mesh (" + this.name + ") does not have any faces!");
		}
		if (!this.hasVertices()) {
			throw new IOException("Mesh (" + this.name + ") does not have any vertices!");
		}
		if (!this.hasNormals()) {
			throw new IOException("Mesh (" + this.name + ") does not have any normals!");
		}
		
	}
	
	/**
	 * 
	 * @param vertices
	 * @return
	 */
	private GlueList<Vector3f> parseVertices(Element vertices) {
		String[] vert =  vertices.getTextContent().split(" ");
		return this.parseVec3ToVec3(vert);
	}
	
	/**
	 * Create faces that can be added to a mesh. 
	 * The faces are automatically turned into triangles.
	 * @param faces
	 * @return
	 */
	private GlueList<Vector3f> parseFaces(Element faces) {
		String[] face = faces.getTextContent().split(" ");
		int vertPerFace = Integer.parseInt(faces.getAttribute("indices"));
		
		switch(vertPerFace) {
		case 3:
			return parseVec3ToVec3(face);
		case 4:
			return parseVec4ToVec3(face);
		}
		
		return null;
	}

	private GlueList<Vector3f> parseNormals(Element normals) {
		String[] normalStrings = normals.getTextContent().split(" ");
		return this.parseVec3ToVec3(normalStrings);
	}

	private GlueList<Vector2f> parseUV(Element uv) {
		String[] uvStrings = uv.getTextContent().split(" ");
		return this.parseVec2ToVec2(uvStrings);
	}

	private GlueList<Vector2f> parseVec2ToVec2(String[] vectors) {
		GlueList<Vector2f> result = new GlueList<Vector2f>();
		
		for (int i= 0; i < vectors.length-1; i+=2) {
			result.add(new Vector2f(Float.parseFloat(vectors[i]),Float.parseFloat(vectors[i+1])));
		}
		return result;
	}

	/**
	 * Parse a string array with three vectors per index. This should be used for 
	 * normals and vertices.
	 * @param vectors
	 * @return A list with vectors.
	 */
	private GlueList<Vector3f> parseVec3ToVec3(String[] vectors) {
		GlueList<Vector3f> result = new GlueList<Vector3f>();
		for (int i = 0; i < vectors.length - 2; i += 3) {
			result.add(new Vector3f(Float.parseFloat(vectors[i]), Float.parseFloat(vectors[i+1]), Float.parseFloat(vectors[i+2])));
		}
		return result;
	}

	/**
	 * Parse a string array with four vectors per index. This should be used for 
	 * faces with 4 indices per face.
	 * @param vectors
	 * @return
	 */
	private GlueList<Vector3f> parseVec4ToVec3(String[] vectors) {
		GlueList<Vector3f> result = new GlueList<Vector3f>();
		for (int i = 0; i < vectors.length - 3; i += 4) {
			result.add(new Vector3f(Float.parseFloat(vectors[i]), Float.parseFloat(vectors[i+1]), Float.parseFloat(vectors[i+3])));
			result.add(new Vector3f(Float.parseFloat(vectors[i+1]), Float.parseFloat(vectors[i+2]), Float.parseFloat(vectors[i+1])));
		}
		return result;
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
	
	String getName() {
		return this.name;
	}
	
	public boolean hasVertices() {
		return this.vertices != null;
	}

	public boolean hasNormals() {
		return this.normals != null;
	}
	
	public boolean hasFaces() {
		return this.faces != null;
	}
	
	public boolean hasUVcoords() {
		return this.uv != null;
	}
	
	public static enum FaceVertices {
		THREE(3), FOUR(4);
		private int vertices;
		private int glValue;

		private FaceVertices(int i) {
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
