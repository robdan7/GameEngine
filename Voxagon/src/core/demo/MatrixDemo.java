package core.demo;

import core.utils.math.Matrix4f;
import core.utils.math.Vector3f;
import core.utils.math.Vector4f;

public class MatrixDemo {

	/**
	 * This demonstrates rotation before translation. 
	 * The vector is first rotated 180 degrees before it is translated in the 
	 * positive x direction, which is turned to -x.
	 */
	public MatrixDemo() {
		Matrix4f m = new Matrix4f();
		Vector4f v = new Vector4f(1,0,0,1);
		m.rotateAbsolute(Math.PI, 0, 1, 0);
		m.translateAbsolute(new Vector3f(1,0,0));
		System.out.println(Matrix4f.multiply(m, v));
	}
	
	public static void main(String[] args) {
		new MatrixDemo();
	}

}
