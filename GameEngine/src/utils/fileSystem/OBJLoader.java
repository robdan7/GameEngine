/*
 * Copyright (c) 2013, Oskar Veerhoek
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those
 * of the authors and should not be interpreted as representing official policies,
 * either expressed or implied, of the FreeBSD Project.
 */

package utils.fileSystem;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL15;

import objects.models.Model;
import objects.models.Model.Face;
import objects.models.Model.Material;
import utils.math.*;
import utils.other.BufferTools;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;

/**
 * @author Oskar Veerhoek
 */
public class OBJLoader {

    public static int createDisplayList(Model m) {
        int displayList = glGenLists(1);
        glNewList(displayList, GL_COMPILE);
        {
            //glMaterialf(GL_FRONT, GL_SHININESS, 120);
            //glColor3f(0.4f, 0.6f, 0.17f);
            glBegin(GL_TRIANGLES);
            for (Model.Face face : m.getFaces()) {
                if (face.hasNormals()) {
                    Vector3f n1 = m.getNormals().get(face.getNormalIndices()[0] - 1);
                    glNormal3f(n1.x, n1.y, n1.z);
                }
                Vector3f v1 = m.getVertices().get(face.getVertexIndices()[0] - 1);
                glVertex3f(v1.x, v1.y, v1.z);
                if (face.hasNormals()) {
                    Vector3f n2 = m.getNormals().get(face.getNormalIndices()[1] - 1);
                    glNormal3f(n2.x, n2.y, n2.z);
                }
                Vector3f v2 = m.getVertices().get(face.getVertexIndices()[1] - 1);
                glVertex3f(v2.x, v2.y, v2.z);
                if (face.hasNormals()) {
                    Vector3f n3 = m.getNormals().get(face.getNormalIndices()[2] - 1);
                    glNormal3f(n3.x, n3.y, n3.z);
                }
                Vector3f v3 = m.getVertices().get(face.getVertexIndices()[2] - 1);
                glVertex3f(v3.x, v3.y, v3.z);
            }
            glEnd();
        }
        glEndList();
        return displayList;
    }

    private static FloatBuffer reserveData(int size) {
        return BufferUtils.createFloatBuffer(size);
    }

    private static float[] asFloats(Vector3f v) {
        return new float[]{v.x, v.y, v.z};
    }
    
    private static float[] asFloats(Vector2f v) {
        return new float[]{v.x, v.y};
    }

    public static int[] createVBO(Model model) {
        int vbo = glGenBuffers();
        int vertices = 0;
        int vertPerFace = 3;
        int floatsPerFace = vertPerFace*8; // 8 is the number of floats per vertex. Vertex + normal + texture = 3 + 3 + 2
        FloatBuffer data = reserveData(model.getFaces().size() * floatsPerFace);
        for (Model.Face face : model.getFaces()) {            
            data.put(asFloats(model.getVertices().get(face.getVertexIndices()[0] - 1)));
            data.put(asFloats(model.getNormals().get(face.getNormalIndices()[0] - 1)));
            data.put(asFloats(model.getTextureCoordinates().get(face.getTextureCoordinateIndices()[0]-1)));
            
            data.put(asFloats(model.getVertices().get(face.getVertexIndices()[1] - 1)));
            data.put(asFloats(model.getNormals().get(face.getNormalIndices()[1] - 1)));
            data.put(asFloats(model.getTextureCoordinates().get(face.getTextureCoordinateIndices()[1]-1)));
            
            data.put(asFloats(model.getVertices().get(face.getVertexIndices()[2] - 1)));
            data.put(asFloats(model.getNormals().get(face.getNormalIndices()[2] - 1)));
            data.put(asFloats(model.getTextureCoordinates().get(face.getTextureCoordinateIndices()[2]-1)));
            
            vertices += vertPerFace;
        }
        data.flip(); 
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, data, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        //return vbo;
        return new int[] {vbo,vertices};
    }
  
    
    /**@author Robin
     * Create a buffer object containing several instances.
     * @param model - The model to create.
     * @param instances - The amount of instances.
     * @param positionFloats - The amount of floats reserved to position.
     * @param rotationFloats - The amount of floats reserved to rotation.
     * @param scaling - The amount of float reserver to scaling.
     * @param extraData - Any extra floats.
     * @return - The vbo OpenGL index and the amount of vertices to draw.
     */
    public static int[] createInstancedVBO(Model model, int instances, int positionFloats, int rotationFloats, int scaling, int extraData) {
    	int vbo = glGenBuffers();
        int vertices = 0;
        int vertPerFace = 3;
        int floatsPerFace = vertPerFace*8; // 8 is the number of floats per vertex. Vertex + normal + texture = 3 + 3 + 2
        FloatBuffer data = reserveData(
        		model.getFaces().size() * floatsPerFace + (
        				positionFloats+rotationFloats+scaling+extraData)*instances);
        float[] emptyData = new float[0];
        data.put(emptyData);
        for (Model.Face face : model.getFaces()) {
            data.put(asFloats(model.getNormals().get(face.getNormalIndices()[0] - 1)));
            data.put(asFloats(model.getNormals().get(face.getNormalIndices()[1] - 1)));
            data.put(asFloats(model.getNormals().get(face.getNormalIndices()[2] - 1)));
            
            data.put(asFloats(model.getVertices().get(face.getVertexIndices()[0] - 1)));
            data.put(asFloats(model.getVertices().get(face.getVertexIndices()[1] - 1)));
            data.put(asFloats(model.getVertices().get(face.getVertexIndices()[2] - 1)));
            
            data.put(asFloats(model.getTextureCoordinates().get(face.getTextureCoordinateIndices()[0]-1)));
            data.put(asFloats(model.getTextureCoordinates().get(face.getTextureCoordinateIndices()[1]-1)));
            data.put(asFloats(model.getTextureCoordinates().get(face.getTextureCoordinateIndices()[2]-1)));
            
            vertices += vertPerFace;
        } 
        data.flip();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, data, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        //return vbo;
        return new int[] {vbo,vertices};
    }
    
    /**
     * @author Robin
     * @param buffer - The buffer to bind.
     * @param offset - The offset in the buffer.
     * @param data - The data to replace.
     */
    public static void updateVBO(int buffer, int offset, FloatBuffer data) {
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, buffer);
		GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, offset, data);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}
    
    /**
     * @author Robin
     * @param buffer - The buffer to bind.
     * @param offset - The offset in the buffer.
     * @param data - The data to replace.
     */
    public static void updateVBO(int buffer, int offset, ByteBuffer data) {
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, buffer);
		GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, offset, data);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}
    
    /**Delete a buffer. Do this before exiting the program.
     * @author Robin
     * @param buffer - The buffer to delete.
     */
    public static void deleteVBO (int buffer) {
    	GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, buffer);
    	GL15.glDeleteBuffers(buffer);
    	GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
    }

    private static Vector3f parseVertex(String line) {
        String[] xyz = line.split(" ");
        float x = Float.valueOf(xyz[1]);
        float y = Float.valueOf(xyz[2]);
        float z = Float.valueOf(xyz[3]);
        return new Vector3f(x, y, z);
    }

    private static Vector3f parseNormal(String line) {
        String[] xyz = line.split(" ");
        float x = Float.valueOf(xyz[1]);
        float y = Float.valueOf(xyz[2]);
        float z = Float.valueOf(xyz[3]);
        return new Vector3f(x, y, z);
    }

    private static Model.Face parseFace(boolean hasNormals, String line) {
        String[] faceIndices = line.split(" ");
        int[] vertexIndicesArray = {Integer.parseInt(faceIndices[1].split("/")[0]),
                Integer.parseInt(faceIndices[2].split("/")[0]), Integer.parseInt(faceIndices[3].split("/")[0])};
        if (hasNormals) {
            int[] normalIndicesArray = new int[3];
            normalIndicesArray[0] = Integer.parseInt(faceIndices[1].split("/")[2]);
            normalIndicesArray[1] = Integer.parseInt(faceIndices[2].split("/")[2]);
            normalIndicesArray[2] = Integer.parseInt(faceIndices[3].split("/")[2]);
            return new Model.Face(vertexIndicesArray, normalIndicesArray);
        } else {
            return new Model.Face((vertexIndicesArray));
        }
    }

    public static Model loadModel(String f) throws IOException {
        BufferedReader reader = FileManager.getReader(f);
        Model m = new Model();
        String line;
        int i = 0;
        while ((line = reader.readLine()) != null) {
            String prefix = line.split(" ")[0];
            i ++;
            if (prefix.equals("#")) {
                continue;
            } else if (prefix.equals("v")) {
                m.getVertices().add(parseVertex(line));
            } else if (prefix.equals("vn")) {
                m.getNormals().add(parseNormal(line));
            } else if (prefix.equals("f")) {
                m.getFaces().add(parseFace(m.hasNormals(), line));
                System.out.println(line);
            } else {
            	reader.close();
                throw new RuntimeException("OBJ file contains line which cannot be parsed correctly: " + prefix);
            }
        }
        reader.close();
        return m;
    }

    public static int createTexturedDisplayList(Model m, int amount, ArrayList<Vector3f> vec) {
        int displayList = glGenLists(amount);
        for (int i = 0; i < amount; i++) {
        	glNewList(displayList+i, GL_COMPILE);
                glBegin(GL_TRIANGLES);
                for (Model.Face face : m.getFaces()) {
                    if (face.hasTextureCoordinates()) {
                        glMaterialfv(GL_FRONT, GL_DIFFUSE, BufferTools.asFlippedFloatBuffer(face.getMaterial()
                                .diffuseColour[0], face.getMaterial().diffuseColour[1],
                                face.getMaterial().diffuseColour[2], 1));
                        glMaterialfv(GL_FRONT, GL_AMBIENT, BufferTools.asFlippedFloatBuffer(face.getMaterial()
                                .ambientColour[0], face.getMaterial().ambientColour[1],
                                face.getMaterial().ambientColour[2], 1));
                        glMaterialf(GL_FRONT, GL_SHININESS, face.getMaterial().specularCoefficient);
                    }
                    if (face.hasNormals()) {
                        Vector3f n1 = m.getNormals().get(face.getNormalIndices()[0] - 1);
                        glNormal3f(n1.x, n1.y, n1.z);
                    }
                    if (face.hasTextureCoordinates()) {
                        Vector2f t1 = m.getTextureCoordinates().get(face.getTextureCoordinateIndices()[0] - 1);
                        glTexCoord2f(t1.x, t1.y);
                    }
                    Vector3f v1 = m.getVertices().get(face.getVertexIndices()[0] - 1);
                    glVertex3f(v1.x + vec.get(i).x, v1.y+ vec.get(i).y, v1.z+ vec.get(i).z);
                    if (face.hasNormals()) {
                        Vector3f n2 = m.getNormals().get(face.getNormalIndices()[1] - 1);
                        glNormal3f(n2.x, n2.y, n2.z);
                    }
                    if (face.hasTextureCoordinates()) {
                        Vector2f t2 = m.getTextureCoordinates().get(face.getTextureCoordinateIndices()[1] - 1);
                        glTexCoord2f(t2.x, t2.y);
                    }
                    Vector3f v2 = m.getVertices().get(face.getVertexIndices()[1] - 1);
                    glVertex3f(v2.x+ vec.get(i).x, v2.y+ vec.get(i).y, v2.z+ vec.get(i).z);
                    if (face.hasNormals()) {
                        Vector3f n3 = m.getNormals().get(face.getNormalIndices()[2] - 1);
                        glNormal3f(n3.x, n3.y, n3.z);
                    }
                    if (face.hasTextureCoordinates()) {
                        Vector2f t3 = m.getTextureCoordinates().get(face.getTextureCoordinateIndices()[2] - 1);
                        glTexCoord2f(t3.x, t3.y);
                    }
                    Vector3f v3 = m.getVertices().get(face.getVertexIndices()[2] - 1);
                    glVertex3f(v3.x+ vec.get(i).x, v3.y+ vec.get(i).y, v3.z+ vec.get(i).z);
                }
                glEnd();
            glEndList();
        }
        return displayList;
    }
    
    public static int createTexturedDisplayList(Model m, float scale) {
    	int displayList = glGenLists(1);
        	glNewList(displayList, GL_COMPILE);
            {
                glBegin(GL_TRIANGLES);
                for (Model.Face face : m.getFaces()) {
                    if (face.hasTextureCoordinates()) {
                        glMaterialfv(GL_FRONT, GL_DIFFUSE, BufferTools.asFlippedFloatBuffer(face.getMaterial()
                                .diffuseColour[0], face.getMaterial().diffuseColour[1],
                                face.getMaterial().diffuseColour[2], 1));
                        glMaterialfv(GL_FRONT, GL_AMBIENT, BufferTools.asFlippedFloatBuffer(face.getMaterial()
                                .ambientColour[0], face.getMaterial().ambientColour[1],
                                face.getMaterial().ambientColour[2], 1));
                        glMaterialf(GL_FRONT, GL_SHININESS, face.getMaterial().specularCoefficient);
                    }
                    if (face.hasNormals()) {
                        Vector3f n1 = m.getNormals().get(face.getNormalIndices()[0] - 1);
                        glNormal3f(n1.x, n1.y, n1.z);
                    }
                    if (face.hasTextureCoordinates()) {
                        Vector2f t1 = m.getTextureCoordinates().get(face.getTextureCoordinateIndices()[0] - 1);
                        glTexCoord2f(t1.x, t1.y);
                    }
                    Vector3f v1 = m.getVertices().get(face.getVertexIndices()[0] - 1);
                    glVertex3f(v1.x*scale, v1.y*scale, v1.z*scale);
                    if (face.hasNormals()) {
                        Vector3f n2 = m.getNormals().get(face.getNormalIndices()[1] - 1);
                        glNormal3f(n2.x, n2.y, n2.z);
                    }
                    if (face.hasTextureCoordinates()) {
                        Vector2f t2 = m.getTextureCoordinates().get(face.getTextureCoordinateIndices()[1] - 1);
                        glTexCoord2f(t2.x, t2.y);
                    }
                    Vector3f v2 = m.getVertices().get(face.getVertexIndices()[1] - 1);
                    glVertex3f(v2.x*scale, v2.y*scale, v2.z*scale);
                    if (face.hasNormals()) {
                        Vector3f n3 = m.getNormals().get(face.getNormalIndices()[2] - 1);
                        glNormal3f(n3.x, n3.y, n3.z);
                    }
                    if (face.hasTextureCoordinates()) {
                        Vector2f t3 = m.getTextureCoordinates().get(face.getTextureCoordinateIndices()[2] - 1);
                        glTexCoord2f(t3.x, t3.y);
                    }
                    Vector3f v3 = m.getVertices().get(face.getVertexIndices()[2] - 1);
                    glVertex3f(v3.x*scale, v3.y*scale, v3.z*scale);
                }
                glEnd();
            }
            glEndList();
        return displayList;
    }

    public static Model loadTexturedModel(String file, float scale) throws IOException {
        BufferedReader reader = FileManager.getReader(file);
        String[] fileName = file.split("/");
        String folder = "";
        for (int i= 0; i < fileName.length-1; i++) {
        	folder += fileName[i] + "/";
        }
        Model m = new Model();
        Model.Material currentMaterial = new Model.Material();
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.startsWith("#")) {
                continue;
            }
            if (line.startsWith("mtllib ")) {
                String materialFileName = line.split(" ")[1];
               // File materialFile = new File(f.getParentFile().getAbsolutePath() + "/" + materialFileName);
                BufferedReader materialFileReader = FileManager.getReader(folder  + materialFileName);
                String materialLine;
                Model.Material parseMaterial = new Model.Material();
                String parseMaterialName = "";
                while ((materialLine = materialFileReader.readLine()) != null) {
                    if (materialLine.startsWith("#")) {
                        continue;
                    }
                    if (materialLine.startsWith("newmtl ")) {
                        if (!parseMaterialName.equals("")) {
                            m.getMaterials().put(parseMaterialName, parseMaterial);
                        }
                        parseMaterialName = materialLine.split(" ")[1];
                        parseMaterial = new Model.Material();
                    } else if (materialLine.startsWith("Ns ")) {
                        parseMaterial.specularCoefficient = Float.valueOf(materialLine.split(" ")[1]);
                    } else if (materialLine.startsWith("Ka ")) {
                        String[] rgb = materialLine.split(" ");
                        parseMaterial.ambientColour[0] = Float.valueOf(rgb[1]);
                        parseMaterial.ambientColour[1] = Float.valueOf(rgb[2]);
                        parseMaterial.ambientColour[2] = Float.valueOf(rgb[3]);
                    } else if (materialLine.startsWith("Ks ")) {
                        String[] rgb = materialLine.split(" ");
                        parseMaterial.specularColour[0] = Float.valueOf(rgb[1]);
                        parseMaterial.specularColour[1] = Float.valueOf(rgb[2]);
                        parseMaterial.specularColour[2] = Float.valueOf(rgb[3]);
                    } else if (materialLine.startsWith("Kd ")) {
                        String[] rgb = materialLine.split(" ");
                        parseMaterial.diffuseColour[0] = Float.valueOf(rgb[1]);
                        parseMaterial.diffuseColour[1] = Float.valueOf(rgb[2]);
                        parseMaterial.diffuseColour[2] = Float.valueOf(rgb[3]);
                    } else if (materialLine.startsWith("map_Kd")) {
                    	int index = file.lastIndexOf("/");
                    	m.setTexture(file.substring(0,index) + "/" + materialLine.split(" ")[1]);
                    }
                    else {
                        System.err.println("[MTL] Unknown Line: " + materialLine);
                    }
                }
                m.getMaterials().put(parseMaterialName, parseMaterial);
                materialFileReader.close();
            } else if (line.startsWith("usemtl ")) {
                currentMaterial = m.getMaterials().get(line.split(" ")[1]);
            } else if (line.startsWith("v ")) {
                String[] xyz = line.split(" ");
                float x = Float.valueOf(xyz[1])*scale;
                float y = Float.valueOf(xyz[2])*scale;
                float z = Float.valueOf(xyz[3])*scale;
                m.getVertices().add(new Vector3f(x, y, z));
            } else if (line.startsWith("vn ")) {
                String[] xyz = line.split(" ");
                float x = Float.valueOf(xyz[1]);
                float y = Float.valueOf(xyz[2]);
                float z = Float.valueOf(xyz[3]);
                m.getNormals().add(new Vector3f(x, y, z));
            } else if (line.startsWith("vt ")) {
                String[] xyz = line.split(" ");
                float s = Float.valueOf(xyz[1]);
                float t = Float.valueOf(xyz[2]);
                m.getTextureCoordinates().add(new Vector2f(s, t));
            } else if (line.startsWith("f ")) {
                String[] faceIndices = line.split(" ");
                int[] vertexIndicesArray = {Integer.parseInt(faceIndices[1].split("/")[0]),
                        Integer.parseInt(faceIndices[2].split("/")[0]), Integer.parseInt(faceIndices[3].split("/")[0])};
                int[] textureCoordinateIndicesArray = {-1, -1, -1};
                if (m.hasTextureCoordinates()) {
                    textureCoordinateIndicesArray[0] = Integer.parseInt(faceIndices[1].split("/")[1]);
                    textureCoordinateIndicesArray[1] = Integer.parseInt(faceIndices[2].split("/")[1]);
                    textureCoordinateIndicesArray[2] = Integer.parseInt(faceIndices[3].split("/")[1]);
                }
                int[] normalIndicesArray = {0, 0, 0};
                if (m.hasNormals()) {
                    normalIndicesArray[0] = Integer.parseInt(faceIndices[1].split("/")[2]);
                    normalIndicesArray[1] = Integer.parseInt(faceIndices[2].split("/")[2]);
                    normalIndicesArray[2] = Integer.parseInt(faceIndices[3].split("/")[2]);
                }
                //                Vector3f vertexIndices = new Vector3f(Float.valueOf(faceIndices[1].split("/")[0]),
                //                        Float.valueOf(faceIndices[2].split("/")[0]),
                // Float.valueOf(faceIndices[3].split("/")[0]));
                //                Vector3f normalIndices = new Vector3f(Float.valueOf(faceIndices[1].split("/")[2]),
                //                        Float.valueOf(faceIndices[2].split("/")[2]),
                // Float.valueOf(faceIndices[3].split("/")[2]));
                m.getFaces().add(new Model.Face(vertexIndicesArray, normalIndicesArray,
                        textureCoordinateIndicesArray, currentMaterial));
            } else if (line.startsWith("s ")) {
                boolean enableSmoothShading = !line.contains("off");
                m.setSmoothShadingEnabled(enableSmoothShading);
            } else {
                System.err.println("[OBJ] Unknown Line: " + line);
            }
        }
        reader.close();
        return m;
    }
}
