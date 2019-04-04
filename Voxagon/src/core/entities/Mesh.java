package core.entities;

import java.io.IOException;
import java.nio.FloatBuffer;

import javax.xml.parsers.ParserConfigurationException;

import org.lwjgl.BufferUtils;
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
	private FloatBuffer meshBuffer;

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
		this.meshBuffer = this.toFloatBuffer();
	}
	
	/**
	 * Load mesh into memory
	 * @param file - Mesh file.
	 * @throws IOException the file does not have required attachments.
	 */
	private void loadMesh(String file) throws IOException {
		Document doc = null;
		try {
			doc = XMLparser.createDocument(file);
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
			if (nodes.item(i).getNodeName() != "#text") {
				Element n = (Element) nodes.item(i);
				switch (n.getNodeName()) {
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

		
		if (vertPerFace != 3) {
			GlueList<Vector3f> result = new GlueList<Vector3f>();
			float[] faceInstance = new float[vertPerFace];
			for (int i = 0; i < face.length; i += vertPerFace) {
				for (int k = 0; k < vertPerFace; k++) {
					faceInstance[k] = Float.parseFloat(face[k + i]);
					
				}
				result.addAll(this.triangulateFace(faceInstance));
			}
			return result;
		} else {
			return parseVec3ToVec3(face);
		}


	}

	private GlueList<Vector3f> parseNormals(Element normals) {
		String[] normalStrings = normals.getTextContent().split(" ");
		return this.parseVec3ToVec3(normalStrings);
	}

	private GlueList<Vector2f> parseUV(Element uv) {
		String[] uvStrings = uv.getTextContent().split(" ");
		int vecPerUV = Integer.parseInt(uv.getAttribute("indices"));
		
		//GlueList<Vector2f> result = this.parseVec2ToVec2(uvStrings);
		GlueList<Vector2f> result = new GlueList<Vector2f>();
		
		GlueList<Vector2f> temp = new GlueList<Vector2f>();
		for (int i = 0; i < uvStrings.length; i+=(2*vecPerUV)) {
			temp.clear();
			for (int k = 0; k < vecPerUV*2; k+=2) {
				temp.add(new Vector2f(Float.parseFloat(uvStrings[i+k]),Float.parseFloat(uvStrings[i+k+1])));
			}
			this.triangulateUV(temp);
			result.addAll(temp);
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
	 * Split up a set of floats into groups of three. This is used to e.g. 
	 * trangulate faces.
	 * @param vectors - A list of vertices equal to one mesh face (three or more vertices).
	 * @return
	 */
	private GlueList<Vector3f> triangulateFace(float[] vectors) {
		GlueList<Vector3f> result = new GlueList<Vector3f>();

		for (int i = 1; i < vectors.length-1; i++) {
			result.add(new Vector3f(vectors[0],vectors[i],vectors[i+1]));
		}
		return result;
	}
	
	/**
	 * Triangulate UV coords to fit triangulated faces. The effect is immediate.
	 * @param normals
	 */
	private void triangulateUV(GlueList<Vector2f> UV) {
		
		for (int i = 2; i < UV.size()-1; i+=3) {
			UV.add(i+1,UV.get(i).copy());
			UV.add(i+1,UV.get(0).copy());
		}
		
	}
	
	
	/**
	 * Return this mesh as a float buffer.
	 * @return A float buffer with the format {vertex,normal,uv}
	 */
	private FloatBuffer toFloatBuffer() {
		int floatPerFace = 18; // 3 vertices * 3 floats + the same for normals.
		int capacity = this.uv.size()*2 + this.faces.size()*floatPerFace;
		
		FloatBuffer buffer = BufferUtils.createFloatBuffer(capacity);
		
		int x,y,z;
		for (int i = 0; i < this.faces.size(); i++) {
			x = (int)this.faces.get(i).getX();
			y = (int)this.faces.get(i).getY();
			z = (int)this.faces.get(i).getZ();
			
			buffer.put(this.vertices.get(x).asFloats());
			buffer.put(this.normals.get(x).asFloats());
			buffer.put(this.uv.get(3*i).asFloats());
			
			buffer.put(this.vertices.get(y).asFloats());
			buffer.put(this.normals.get(y).asFloats());
			buffer.put(this.uv.get(3*i+1).asFloats());
			
			buffer.put(this.vertices.get(z).asFloats());
			buffer.put(this.normals.get(z).asFloats());
			buffer.put(this.uv.get(3*i+2).asFloats());
		}
		return buffer;
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
	
	public int getTotalNumberOfVertices() {
		return this.faces.size()*3;
	}
	
	/**
	 * Return the float buffer representation of this mesh. Please do not 
	 * modify it.
	 * @return
	 */
	FloatBuffer getMeshBuffer() {
		return this.meshBuffer;
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
