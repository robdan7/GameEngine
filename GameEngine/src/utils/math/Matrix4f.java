package utils.math;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL20;
import utils.math.Vector3f;
import utils.math.Vector4f;

/**
 * 
 * @author Robin
 * 
 */
public class Matrix4f{
	public float left, mat, top, bottom, far, near, widthRadius, heightRadius;
	
	float m00;
	float m01;
	float m02;
	float m03;
	float m10;
	float m11;
	float m12;
	float m13;
	float m20;
	float m21;
	float m22;
	float m23;
	float m30;
	float m31;
	float m32;
	float m33;
	
	FloatBuffer shaderBuffer;
	
	String uniformName;
	
	public Matrix4f () {
		this.setIdentity();
	}
	
	public Matrix4f (String name) {
		this.setIdentity();
		this.uniformName = name;
	}

	public void setIdentity() {
		m00 = 1.0f;
	    m01 = 0.0f;
	    m02 = 0.0f;
	    m03 = 0.0f;
	    m10 = 0.0f;
	    m11 = 1.0f;
	    m12 = 0.0f;
	    m13 = 0.0f;
	    m20 = 0.0f;
	    m21 = 0.0f;
	    m22 = 1.0f;
	    m23 = 0.0f;
	    m30 = 0.0f;
	    m31 = 0.0f;
	    m32 = 0.0f;
	    m33 = 1.0f;
	}

	public void setOrtho(float left, float right, float bottom, float top, float zNear, float zFar) {
		this.widthRadius = Math.abs(left-right)/2;
		this.heightRadius = Math.abs(top-bottom)/2;
		this.left = left;
		this.mat = right;
		this.top = top;
		this.bottom = bottom;
		this.far = zFar;
		this.near = zNear;
		m00 = 2.0f / (right - left);
	    m01 = 0.0f;
	    m02 = 0.0f;
	    m03 = 0.0f;
	    m10 = 0.0f;
	    m11 = 2.0f / (top - bottom);
	    m12 = 0.0f;
	    m13 = 0.0f;
	    m20 = 0.0f;
	    m21 = 0.0f;
	    m22 = 2.0f / (zNear - zFar);
	    m23 = 0.0f;
	    m30 = (right + left) / (left - right);
	    m31 = (top + bottom) / (bottom - top);
	    m32 = (zFar + zNear) / (zNear - zFar);
	    m33 = 1.0f;
	}
	
	public void loadTransformIdentity() {
		this.m30 = (mat + left) / (left - mat);
        this.m31 = (top + bottom) / (bottom - top);
        this.m32 = (far + near) / (near - far);
	}
	
	public void rotate(double angle, float x, float y, float z) {
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        double C = 1.0 - cos;
        float xy = x * y, xz = x * z, yz = y * z;
        this.m00 = (float)(cos + x * x * C);
        this.m10 = (float)(xy * C - z * sin);
        this.m20 = (float)(xz * C + y * sin);
        //mat.m30 = 0.0f;
        this.m01 = (float)(xy * C + z * sin);
        this.m11 = (float)(cos + y * y * C);
        this.m21 = (float)(yz * C - x * sin);
        //mat.m31 = 0.0f;
        this.m02 = (float)(xz * C - y * sin);
        this.m12 = (float)(yz * C + x * sin);
        this.m22 = (float)(cos + z * z * C);
        //mat.m32 = 0.0f;
        /*this.m03 = 0.0f;
        this.m13 = 0.0f;
        this.m23 = 0.0f;
        this.m33 = 1.0f;*/
    }
	
	/**
	 * Method gluPerspective.
	 *
	 * @param fovy
	 * @param aspect
	 * @param zNear
	 * @param zFar
	 */
	public  void setPerspective(float fovy, float aspect, float zNear, float zFar) {
		float sine, cotangent, deltaZ;
		float radians = fovy / 2 * (float)Math.PI / 180;

		deltaZ = zFar - zNear;
		sine = (float) Math.sin(radians);

		if ((deltaZ == 0) || (sine == 0) || (aspect == 0)) {
			return;
		}

		cotangent = (float) Math.cos(radians) / sine;
		
		this.setIdentity();

		m00 =  cotangent / aspect;
		m11 =  cotangent;
		m22 = - (zFar + zNear) / deltaZ;
		m23 =  -1;
		m32 = -2 * zNear * zFar / deltaZ;
		m33 = 0;
	}
	
	/**Move the camera to a point and look in the same direction as the forward vector.
	 * Note: The view space is in left-handed coordinates, while world space is right handed.
	 * @param e - The eye.
	 * @param f - Forward vector. This must be a unit vector.
	 * @param u - Universal up vector.
	 */
	public void lookAt(Vector3f e, Vector3f f, Vector3f u) {
		Vector3f s = f.crossProduct(u);
		s.normalize();
		Vector3f viewU = s.crossProduct(f);
		viewU.normalize();
		this.m00 = s.x;
	    this.m10 = s.y;
	    this.m20 = s.z;
	    //this.m30 = -f.x;
	    
	    this.m01 = viewU.x;
	    this.m11 = viewU.y;
	    this.m21 = viewU.z;
	    //this.m31 = -f.y;
	    
	    this.m02 = -f.x;
	    this.m12 = -f.y;
	    this.m22 = -f.z;
	    //this.m32 = -f.z;
	    
	    this.translate(e.flip());
	    //m30 = 0.0f;
	    //m31 = 0.0f;
	    //m32 = 0.0f;
	    //m33 = 1.0f;

	}
	
	
	/**
	 * 
	 * @param v - The new position.
	 */
	public void translate(Vector3f v) {
        this.m30 = (float) (this.m00 * v.x + this.m10 * v.y + this.m20 * v.z);
        this.m31 = (float) (this.m01 * v.x + this.m11 * v.y + this.m21 * v.z);
        this.m32 = (float) (this.m02 * v.x + this.m12 * v.y + this.m22 * v.z);
    }
	
	/**
	 * 
	 * @param v
	 * @return
	 */
	public Vector4f multiply(Vector4f v) {
		Vector4f v2 = new Vector4f();
		v2.x = m00*v.x+m10*v.y+m20*v.z+m30*v.w;
		v2.y = m01*v.x+m11*v.y+m21*v.z+m31*v.w;
		v2.z = m02*v.x+m12*v.y+m22*v.z+m32*v.w;
		v2.w = m03*v.x+m13*v.y+m23*v.z+m33*v.w;
		return v2;
	}
	
	/**
	 * 
	 * @param v
	 * @return
	 */
	public Vector3f multiply(Vector3f v) {
		Vector3f v2 = new Vector3f();
		v2.x = m00*v.x+m10*v.y+m20*v.z;
		v2.y = m01*v.x+m11*v.y+m21*v.z;
		v2.z = m02*v.x+m12*v.y+m22*v.z;
		return v2;
	}
	
	/**
	 * 
	 * @param mat - The matrix to multiply with.
	 * @return - The new multiplied matrix.
	 */
	public Matrix4f multiply(Matrix4f mat) {
		Matrix4f result = new Matrix4f();
		result.m00 = this.m00 * mat.m00 + this.m10 * mat.m01 + this.m20 * mat.m02 + this.m30 * mat.m03;
		result.m01 = this.m01 * mat.m00 + this.m11 * mat.m01 + this.m21 * mat.m02 + this.m31 * mat.m03;
		result.m02 = this.m02 * mat.m00 + this.m12 * mat.m01 + this.m22 * mat.m02 + this.m32 * mat.m03;
		result.m03 = this.m03 * mat.m00 + this.m13 * mat.m01 + this.m23 * mat.m02 + this.m33 * mat.m03;
		result.m10 = this.m00 * mat.m10 + this.m10 * mat.m11 + this.m20 * mat.m12 + this.m30 * mat.m13;
		result.m11 = this.m01 * mat.m10 + this.m11 * mat.m11 + this.m21 * mat.m12 + this.m31 * mat.m13;
		result.m12 = this.m02 * mat.m10 + this.m12 * mat.m11 + this.m22 * mat.m12 + this.m32 * mat.m13;
		result.m13 = this.m03 * mat.m10 + this.m13 * mat.m11 + this.m23 * mat.m12 + this.m33 * mat.m13;
		result.m20 = this.m00 * mat.m20 + this.m10 * mat.m21 + this.m20 * mat.m22 + this.m30 * mat.m23;
		result.m21 = this.m01 * mat.m20 + this.m11 * mat.m21 + this.m21 * mat.m22 + this.m31 * mat.m23;
		result.m22 = this.m02 * mat.m20 + this.m12 * mat.m21 + this.m22 * mat.m22 + this.m32 * mat.m23;
		result.m23 = this.m03 * mat.m20 + this.m13 * mat.m21 + this.m23 * mat.m22 + this.m33 * mat.m23;
		result.m30 = this.m00 * mat.m30 + this.m10 * mat.m31 + this.m20 * mat.m32 + this.m30 * mat.m33;
		result.m31 = this.m01 * mat.m30 + this.m11 * mat.m31 + this.m21 * mat.m32 + this.m31 * mat.m33;
		result.m32 = this.m02 * mat.m30 + this.m12 * mat.m31 + this.m22 * mat.m32 + this.m32 * mat.m33;
		result.m33 = this.m03 * mat.m30 + this.m13 * mat.m31 + this.m23 * mat.m32 + this.m33 * mat.m33;
		return result;
	}
	
	/**
	 * bind en uniform till definierade shaders och flyttalsbuffer.
	 *@param
	 * shaders - Det program som uniformen ska bindas till.
	 * @param
	 * buf - Flyttalsbufferten med alla värden i matrisen, används för att lagra matrisen externt.
	 * */
	public void createUniform (int shaders, FloatBuffer buf, String uniformName) {
		int loc1 = GL20.glGetUniformLocation(shaders, uniformName);
		GL20.glUniformMatrix4fv(loc1, false, buf);
	}
	
	/**
	 * 
	 * @param shaders
	 * @param name
	 */
	public void createUniform (int shaders, String name) {
		int location = GL20.glGetUniformLocation(shaders, name);
		GL20.glUniformMatrix4fv(location, false, this.put());
	}
	
	public void createUniform (int shaders) {
		int location = GL20.glGetUniformLocation(shaders, this.uniformName);
		GL20.glUniformMatrix4fv(location, false, this.put());
	}
	
	
	@Deprecated
	public static void createIntUniform (int shaders, int num, String uniformName) {
		int loc1 = GL20.glGetUniformLocation(shaders, uniformName);
		GL20.glUniform1i(loc1, num);
	}
	
	/**
	 * Store this matrix in a floatbuffer.
	 * @return Return the matrix as a buffer.
	 * */
	public FloatBuffer put() {
		if (this.shaderBuffer == null) {
			this.shaderBuffer = BufferUtils.createFloatBuffer(16);
		}
		shaderBuffer.put(0,    this.m00);
		shaderBuffer.put(1,  this.m01);
		shaderBuffer.put(2,  this.m02);
		shaderBuffer.put(3,  this.m03);
		shaderBuffer.put(4,  this.m10);
		shaderBuffer.put(5,  this.m11);
		shaderBuffer.put(6,  this.m12);
		shaderBuffer.put(7,  this.m13);
		shaderBuffer.put(8,  this.m20);
		shaderBuffer.put(9,  this.m21);
		shaderBuffer.put(10, this.m22);
		shaderBuffer.put(11, this.m23);
		shaderBuffer.put(12, this.m30);
		shaderBuffer.put(13, this.m31);
		shaderBuffer.put(14, this.m32);
		shaderBuffer.put(15, this.m33);
        
        return shaderBuffer;
    }
	
	/**
	 * Store this matrix in a floatbuffer.
	 * @return Return the matrix as a buffer.
	 * */
	public void put(FloatBuffer dest) {
        dest.put(0,    this.m00);
        dest.put(1,  this.m01);
        dest.put(2,  this.m02);
        dest.put(3,  this.m03);
        dest.put(4,  this.m10);
        dest.put(5,  this.m11);
        dest.put(6,  this.m12);
        dest.put(7,  this.m13);
        dest.put(8,  this.m20);
        dest.put(9,  this.m21);
        dest.put(10, this.m22);
        dest.put(11, this.m23);
        dest.put(12, this.m30);
        dest.put(13, this.m31);
        dest.put(14, this.m32);
        dest.put(15, this.m33);
    }
}
