package utils.other;

import static org.lwjgl.opengl.GL11.*;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;
import org.lwjgl.opengl.*;

import de.matthiasmann.twl.utils.PNGDecoder;
import utils.fileSystem.FileManager;

/**
 * 
 * @author Robin
 *
 */
public class Texture {
	
	private int id;
	private int height = 0;
	private int width = 0;
	String name;
	
	public Texture(int width, int height, String textureName, int filtering, int internalFormat) throws Exception {
		this.name = textureName;
	    this.id = glGenTextures();
	    
	    glBindTexture(GL_TEXTURE_2D, this.id);
	    GL11.glTexImage2D(GL_TEXTURE_2D, 0, internalFormat, width, height, 0,  internalFormat, GL_FLOAT, (ByteBuffer) null);
	    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, filtering);
	    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, filtering);
	    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
	    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
	    glBindTexture(GL_TEXTURE_2D, 0);
	}
	
	/**
	 * 
	 * @param file - Texture file.
	 * @param textureName - Name in shaders. 
	 */
	public Texture(String file, String textureName) {
		ByteBuffer buf = null;
		int tWidth = 0;
		int tHeight = 0;
		 
		try {
		    // Open the PNG file as an InputStream
		    InputStream in = FileManager.getStream(file);
		    // Link the PNG decoder to this stream
		    PNGDecoder decoder = new PNGDecoder(in);
		    this.height = decoder.getHeight();
		    this.width = decoder.getWidth();
		    // Get the width and height of the texture
		    tWidth = decoder.getWidth();
		    tHeight = decoder.getHeight();
		     
		     
		    // Decode the PNG file in a ByteBuffer
		    buf = ByteBuffer.allocateDirect(4 * decoder.getWidth() * decoder.getHeight());
		    decoder.decode(buf, decoder.getWidth() * 4, PNGDecoder.Format.RGBA);
		    buf.flip();
		    in.close();
		} catch (IOException e) {
		    e.printStackTrace();
		}
		
		
		this.name = textureName;
        this.id = glGenTextures();
        GL13.glActiveTexture(GL13.GL_TEXTURE0 + id);
        glBindTexture(GL_TEXTURE_2D, id);
        GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGB, tWidth, tHeight, 0, 
                GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buf);
        //GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP); 
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        
        glBindTexture(GL_TEXTURE_2D, 0);
       // return texture;
    }
	
	public static void screenshot (ByteBuffer buf,int width, int height, String image) {
		File file = new File(image);
		String format = "PNG";
		BufferedImage readImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				int i = (x + (width * y)) * 4;
				int r = buf.get(i) & 0xFF;
				int g = buf.get(i+1) & 0xFF;
				int b = buf.get(i+2) & 0xFF;
				readImage.setRGB(x, height-(y+1), (0xFF << 24) | (r << 16) | (g << 8) | b);
			}
			
		}
		try {
			ImageIO.write(readImage, format, file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Radera texturen som lagrats p� grafikkortet.
	 * */
	public void cleanup() {
		// TODO Auto-generated method stub
		GL11.glDeleteTextures(id);
	}
	
	/**
	 * Binder en textur som uniform. Till�ter flera texturer samtidigt och beh�ver bara bindas en g�ng.
	 * */
	public void bindAsUniform(int shaderProgram) {
		GL20.glUseProgram(shaderProgram);
		GL13.glActiveTexture(GL13.GL_TEXTURE0 + id);
        glBindTexture(GL_TEXTURE_2D, id);
        GL20.glUniform1i(GL20.glGetUniformLocation(shaderProgram, name), id);
        //glBindTexture(GL_TEXTURE_2D, 0);
        GL20.glUseProgram(0);
    }
	/**
	 * Binder en textur till grafikkortet. Fungerar med default-matriser och grafikkortets standard-shaders.
	 * */
	public void bind() {
        glBindTexture(GL_TEXTURE_2D, id);
    }
	
	public void unbind() {
		glBindTexture(GL_TEXTURE_2D, 0);
	}

	public int getId() {
		// TODO Auto-generated method stub
		return this.id;
	}
	
	public int getHeight() {
		return this.height;
	}
	
	public int getWidth() {
		return this.width;
	}
}
