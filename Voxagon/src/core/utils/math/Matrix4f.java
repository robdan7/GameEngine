package core.utils.math;

import java.nio.FloatBuffer;

import org.lwjgl.opengl.GL20;


/**
 * 
 * @author Robin
 * 
 */
public class Matrix4f{
	public final static int SIZE = 16;
	private float left, right, top, bottom, zFar, zNear, fovy;
	private Matrix4f inverse = null;
	
	protected float m00;
	protected float m01;
	protected float m02;
	protected float m03;
	protected float m10;
	protected float m11;
	protected float m12;
	protected float m13;
	protected float m20;
	protected float m21;
	protected float m22;
	protected float m23;
	protected float m30;
	protected float m31;
	protected float m32;
	protected float m33;
	
	private float[] dest;
	
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

	/**
	 * Set this matrix to have an orthographic perspective.
	 * @param left
	 * @param right
	 * @param bottom
	 * @param top
	 * @param zNear
	 * @param zFar
	 */
	public void setOrtho(float left, float right, float bottom, float top, float zNear, float zFar) {
		//this.widthRadius = Math.abs(left-right)/2;
		//this.heightRadius = Math.abs(top-bottom)/2;
		this.left = left;
		this.right = right;
		this.top = top;
		this.bottom = bottom;
		this.zFar = zFar;
		this.zNear = zNear;
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
		this.m30 = (right + left) / (left - right);
        this.m31 = (top + bottom) / (bottom - top);
        this.m32 = (zFar + zNear) / (zNear - zFar);
	}
	
	public void rotate(double angle, float x, float y, float z) {
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        this.rotate(sin, cos, x, y, z);
    }
	
	public void rotateFromPoint(Vector3f v) {
		if (v.length() == 0) {
			throw new RuntimeException("Length of vector is 0");
		}
		v.normalize();
		
		if (v.z == 1 || v.z == -1) {
			throw new RuntimeException("Illegal Vector (|z| == 1)");
		}
		Vector3f u = new Vector3f(0,0,-1);
		Vector3f n = u.crossProduct(v);
		
		float lengthProduct = u.length()*v.length();
		double sin = n.length()/lengthProduct;
		double cos = u.dot(v)/lengthProduct;
		
		n.normalize();
		this.rotate(sin, cos, n.x, n.y, n.z);
	}
	
	private void rotate(double sin, double cos, float x,float y, float z) {
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
	}
	
	/**
	 * Set the perspective of this matrix.
	 *
	 * @param fovy - The view angle in degrees.
	 * @param aspect - the window length divided by it's height.
	 * @param zNear - closest point in space.
	 * @param zFar - Furthest point in space.
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
		
		this.zFar = zFar;
		this.zNear = zNear;
		this.fovy = radians*2;
		//this.aspectRatio = aspect;
	}
	
	/**Move the camera to a point and look in the same direction as the forward vector.
	 * Note: The view space is in left-handed coordinates, while world space is right handed.
	 * @param e - The eye.
	 * @param f - Forward vector. This must be a unit vector.
	 * @param u - Universal up vector.
	 * @param r - Default left vector. This is used when e and f are opposite or the same.
	 */
	public void lookAt(Vector3f e, Vector3f f, Vector3f u, Vector3f r) {
		Vector3f s = f.crossProduct(u);
		s.normalize();
		if (s.length() == 0) {
			s = r;
		}
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
	    
	    // TODO change this
	    this.translate(e.asFlipped());
	    //m30 = 0.0f;
	    //m31 = 0.0f;
	    //m32 = 0.0f;
	    //m33 = 1.0f;

	}
	
	
	/**
	 * 
	 * @param v - The new position.
	 */
	public void translate(Vector<?> v) {
        this.m30 = (float) (this.m00 * v.x + this.m10 * v.y + this.m20 * v.z);
        this.m31 = (float) (this.m01 * v.x + this.m11 * v.y + this.m21 * v.z);
        this.m32 = (float) (this.m02 * v.x + this.m12 * v.y + this.m22 * v.z);
    }
	
	/**
	 * Multiply this matrix by a 4-point vector.
	 * @param v
	 * @return The multiplied vector.
	 */
	public static Vector4f multiply(Matrix4f m, Vector4f v) {
		Vector4f v2 = new Vector4f();
		v2.x = m.m00*v.x+m.m10*v.y+m.m20*v.z+m.m30*v.w;
		v2.y = m.m01*v.x+m.m11*v.y+m.m21*v.z+m.m31*v.w;
		v2.z = m.m02*v.x+m.m12*v.y+m.m22*v.z+m.m32*v.w;
		v2.w = m.m03*v.x+m.m13*v.y+m.m23*v.z+m.m33*v.w;
		return v2;
	}
	
	/**
	 * Multiply a vector by this matrix. The effect is immediate.
	 * @param v
	 */
	public void multiply(Vector4f v) {
		float x = v.x, y = v.y, z = v.z, w = v.w;
		v.x = m00*x+m10*y+m20*z+m30*w;
		v.y = m01*x+m11*y+m21*z+m31*w;
		v.z = m02*x+m12*y+m22*z+m32*w;
		v.w = m03*x+m13*y+m23*z+m33*w;
	}
	
	/**
	 * Multiply a vector by this matrix. The effect is immediate.
	 * @param v
	 */
	public void multiply(Vector3f v) {
		float x = v.x, y = v.y, z = v.z;
		v.x = m00*x+m10*y+m20*z;
		v.y = m01*x+m11*y+m21*z;
		v.z = m02*x+m12*y+m22*z;
	}
	
	/**
	 * Multiply this matrix by a 3-point vector.
	 * @param v
	 * @return The multiplied vector.
	 */
	public static Vector3f multiply(Matrix4f m, Vector3f v) {
		Vector3f result = new Vector3f();
		result.x = m.m00*v.x+m.m10*v.y+m.m20*v.z;
		result.y = m.m01*v.x+m.m11*v.y+m.m21*v.z;
		result.z = m.m02*v.x+m.m12*v.y+m.m22*v.z;
		return result;
	}
	
	/**
	 * 
	 * @param v
	 * @return
	 */
	/*public Vector3f multiply(Vector3f v) {
		Vector3f v2 = new Vector3f();
		v2.x = m00*v.x+m10*v.y+m20*v.z;
		v2.y = m01*v.x+m11*v.y+m21*v.z;
		v2.z = m02*v.x+m12*v.y+m22*v.z;
		return v2;
	}*/
	
	/**
	 * Multiply with a matrix.
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
	 * Calculate the inverse of this matrix. A matrix multiplied by it's inverse is the identity matrix.
	 * @return
	 */
	public Matrix4f getInverse() {
		if (this.inverse == null) this.inverse = new Matrix4f();
		//Matrix4f m = new Matrix4f();
		this.inverse.m00 = m12*m23*m31 - m13*m22*m31 + m13*m21*m32 - m11*m23*m32 - m12*m21*m33 + m11*m22*m33;
		this.inverse.m01 = m03*m22*m31 - m02*m23*m31 - m03*m21*m32 + m01*m23*m32 + m02*m21*m33 - m01*m22*m33;
		this.inverse.m02 = m02*m13*m31 - m03*m12*m31 + m03*m11*m32 - m01*m13*m32 - m02*m11*m33 + m01*m12*m33;
		this.inverse.m03 = m03*m12*m21 - m02*m13*m21 - m03*m11*m22 + m01*m13*m22 + m02*m11*m23 - m01*m12*m23;
		this.inverse.m10 = m13*m22*m30 - m12*m23*m30 - m13*m20*m32 + m10*m23*m32 + m12*m20*m33 - m10*m22*m33;
		this.inverse.m11 = m02*m23*m30 - m03*m22*m30 + m03*m20*m32 - m00*m23*m32 - m02*m20*m33 + m00*m22*m33;
		this.inverse.m12 = m03*m12*m30 - m02*m13*m30 - m03*m10*m32 + m00*m13*m32 + m02*m10*m33 - m00*m12*m33;
		this.inverse.m13 = m02*m13*m20 - m03*m12*m20 + m03*m10*m22 - m00*m13*m22 - m02*m10*m23 + m00*m12*m23;
		this.inverse.m20 = m11*m23*m30 - m13*m21*m30 + m13*m20*m31 - m10*m23*m31 - m11*m20*m33 + m10*m21*m33;
		this.inverse.m21 = m03*m21*m30 - m01*m23*m30 - m03*m20*m31 + m00*m23*m31 + m01*m20*m33 - m00*m21*m33;
		this.inverse.m22 = m01*m13*m30 - m03*m11*m30 + m03*m10*m31 - m00*m13*m31 - m01*m10*m33 + m00*m11*m33;
		this.inverse.m23 = m03*m11*m20 - m01*m13*m20 - m03*m10*m21 + m00*m13*m21 + m01*m10*m23 - m00*m11*m23;
		this.inverse.m30 = m12*m21*m30 - m11*m22*m30 - m12*m20*m31 + m10*m22*m31 + m11*m20*m32 - m10*m21*m32;
		this.inverse.m31 = m01*m22*m30 - m02*m21*m30 + m02*m20*m31 - m00*m22*m31 - m01*m20*m32 + m00*m21*m32;
		this.inverse.m32 = m02*m11*m30 - m01*m12*m30 - m02*m10*m31 + m00*m12*m31 + m01*m10*m32 - m00*m11*m32;
		this.inverse.m33 = m01*m12*m20 - m02*m11*m20 + m02*m10*m21 - m00*m12*m21 - m01*m10*m22 + m00*m11*m22;
		this.inverse.scale(1/this.determinant());
		return this.inverse;
	}
	
	/**
	 * Calculate the determinant of this matrix.
	 * @return
	 */
	public Double determinant() {
		double value;
		value = m03 * m12 * m21 * m30 - m02 * m13 * m21 * m30 - m03 * m11 * m22 * m30 + m01 * m13 * m22 * m30
				+ m02 * m11 * m23 * m30 - m01 * m12 * m23 * m30 - m03 * m12 * m20 * m31 + m02 * m13 * m20 * m31
				+ m03 * m10 * m22 * m31 - m00 * m13 * m22 * m31 - m02 * m10 * m23 * m31 + m00 * m12 * m23 * m31
				+ m03 * m11 * m20 * m32 - m01 * m13 * m20 * m32 - m03 * m10 * m21 * m32 + m00 * m13 * m21 * m32
				+ m01 * m10 * m23 * m32 - m00 * m11 * m23 * m32 - m02 * m11 * m20 * m33 + m01 * m12 * m20 * m33
				+ m02 * m10 * m21 * m33 - m00 * m12 * m21 * m33 - m01 * m10 * m22 * m33 + m00 * m11 * m22 * m33;
		return value;
	}
	
	/**
	 * Scale this matrix.
	 * @param scale
	 */
	public void scale(double scale) {
		m00 *= scale;
	    m01 *= scale;
	    m02 *= scale;
	    m03 *= scale;
	    m10 *= scale;
	    m11 *= scale;
	    m12 *= scale;
	    m13 *= scale;
	    m20 *= scale;
	    m21 *= scale;
	    m22 *= scale;
	    m23 *= scale;
	    m30 *= scale;
	    m31 *= scale;
	    m32 *= scale;
	    m33 *= scale;
	}
	
	/**
	 * Calculate a pointer in 3d space form the relative mouse position on screen.
	 * @param p - Relative position in screen coordinates. From -1 to 1.
	 * @return
	 */
	public Vector3f pointToVector(Vector2f p) {
		Vector3f v = new Vector3f();
		v.z =-this.zNear;
		v.y = (p.y)*(float)Math.tan(this.fovy/2)*this.zNear;
		v.x = (p.x)*(float)Math.tan(this.fovy/2)*this.zNear;
		return v;
	}
	
	/**
	 * Create a matrix shader uniform.
	 *@param
	 * shaders - The shader program.
	 * @param
	 * buf - The matrix float buffer. Can be created with {@link #put(FloatBuffer)}.
	 */
	public void createUniform (int shaders, FloatBuffer buf, String uniformName) {
		int loc1 = GL20.glGetUniformLocation(shaders, uniformName);
		GL20.glUniformMatrix4fv(loc1, false, buf);
	}
	
	/**
	 * 
	 * @param shaders
	 * @param name
	 */
	/*public void createUniform (int shaders, String name) {
		int location = GL20.glGetUniformLocation(shaders, name);
		GL20.glUniformMatrix4fv(location, false, this.put());
	}*/
	
	/*public void createUniform (int shaders) {
		int location = GL20.glGetUniformLocation(shaders, this.uniformName);
		GL20.glUniformMatrix4fv(location, false, this.put());
	}*/
	
	/**
	 * Create a matrix shader uniform.
	 *@param
	 * shaders - The shader program.
	 */
	@Deprecated
	public static void createIntUniform (int shaders, int num, String uniformName) {
		int loc1 = GL20.glGetUniformLocation(shaders, uniformName);
		GL20.glUniform1i(loc1, num);
	}
	
	/**
	 * Store this matrix in a floatbuffer.
	 * @return Return the matrix as a buffer.
	 * */
	/*public FloatBuffer put() {
		if (this.shaderBuffer == null) {
			this.shaderBuffer = BufferUtils.createFloatBuffer(16);
		}
		this.put(this.shaderBuffer);
        return shaderBuffer;
    }*/
	
	/**
	 * Store this matrix in a float buffer.
	 * The buffer is not cleared before writing, make sure to set the writing position first.
	 */
	public void put(FloatBuffer dest) {
		//dest.clear();
        dest.put(this.m00);
        dest.put(this.m01);
        dest.put(this.m02);
        dest.put(this.m03);
        dest.put(this.m10);
        dest.put(this.m11);
        dest.put(this.m12);
        dest.put(this.m13);
        dest.put(this.m20);
        dest.put(this.m21);
        dest.put(this.m22);
        dest.put(this.m23);
        dest.put(this.m30);
        dest.put(this.m31);
        dest.put(this.m32);
        dest.put(this.m33);
    }
	
	public float[] toFloatArray() {
		if (this.dest == null) {
			this.dest = new float[SIZE];
		}
		dest[0] = this.m00;
		dest[1] = this.m01;
		dest[2] = this.m02;
		dest[3] = this.m03;
		
		dest[4] = this.m10;
		dest[5] = this.m11;
		dest[6] = this.m12;
		dest[7] = this.m13;
		
		dest[8] = this.m20;
		dest[9] = this.m21;
		dest[10] = this.m22;
		dest[11] = this.m23;
		
		dest[12] = this.m30;
		dest[13] = this.m31;
		dest[14] = this.m32;
		dest[15] = this.m33;
		return dest;
	}
	
	/*
	public void updateUniformBlock() {
		this.put();
		UniformTools.updateUniformBlock(this.UBO, this.shaderBuffer, this.uniformOffset);
	}*/
	
	@Override
	public String toString() {
		String s = m00 + " " + m10 + " " + m20 + " " + m30 + "\n";
		s += m01 + " " + m11 + " " + m21 + " " + m31 + "\n";
		s += m02 + " " + m12 + " " + m22 + " " + m32 + "\n";
		s += m03 + " " + m13 + " " + m23 + " " + m33 + "\n";
		return s;
	}
}
