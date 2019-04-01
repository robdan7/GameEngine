package core.utils.math.geometry;

import core.utils.math.Vector3f;
import core.utils.math.Vector4f;

public class Plane {
	private Vector3f normal, origin;
	private float a, b, c, d;
	private Vector4f informationVector;

	/**
	 * Create a plane from three known points.
	 * @param p1
	 * @param p2
	 * @param p3
	 */
	public Plane(Vector3f p1, Vector3f p2, Vector3f p3) {
		float u1 = p2.getX()-p1.getX(), u2 = p2.getY()-p1.getY(), u3 = p2.getZ()-p1.getZ();
		float v1 = p3.getX()-p1.getX(), v2 = p3.getY()-p1.getY(), v3 = p3.getZ()-p1.getZ();
		Vector3f normal = new Vector3f(u2*v3-u3*v2, u3*v1-u1*v3, u1*v2-u2*v1);
		normal.normalize();
		this.init(p1, normal);
	}
	
	private void init(Vector3f start, Vector3f n) {
		informationVector = new Vector4f(n.getX(), n.getY(), n.getZ(), start.dot(n));
		/*this.a = n.x;
		this.b = n.y;
		this.c = n.z;
		//this.d = n.x*start.x + n.y*start.y + n.z*start.z;
		this.d = start.dotProduct(n);
		*/
		this.normal = n;
		this.origin = start.toVec3f();
	}
	
	/**
	 * Create a plane from one starting point and a normal vector relative to the starting point.
	 * @param start
	 * @param n
	 */
	public Plane(Vector3f start, Vector3f n) {
		this.init(start, n);
	}
	
	public Vector4f getInformation() {
		return this.informationVector.copy();
	}
	
	/**
	 * Return a copy of the starting point.
	 * @return
	 */
	public Vector3f getOgirin() {
		return this.origin;
	}
	
	public Vector3f getNormalCopy() {
		return this.normal.copy();
	}
}
