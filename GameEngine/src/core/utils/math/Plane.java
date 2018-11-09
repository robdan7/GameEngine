package core.utils.math;

public class Plane {
	private Vector3f normal, start;
	private float a, b, c, d;

	/**
	 * Create a plane from three known points.
	 * @param p1
	 * @param p2
	 * @param p3
	 */
	public Plane(Vector p1, Vector p2, Vector p3) {
		float u1 = p2.x-p1.x, u2 = p2.y-p1.y, u3 = p2.z-p1.z;
		float v1 = p3.x-p1.x, v2 = p3.y-p1.y, v3 = p3.z-p1.z;
		Vector3f normal = new Vector3f(u2*v3-u3*v2, u3*v1-u1*v3, u1*v2-u2*v1);
		this.init(p1, normal);
	}
	
	private void init(Vector start, Vector3f n) {
		this.a = n.x;
		this.b = n.y;
		this.c = n.z;
		this.d = n.x*start.x + n.y*start.y + n.z*start.z;
		this.normal = n;
		this.start = start.toVec3f();
	}
	
	/**
	 * Create a plane from one starting point and a normal vector relative to the starting point.
	 * @param start
	 * @param n
	 */
	public Plane(Vector start, Vector3f n) {
		this.init(start, n);
	}
	
	/**
	 * Return a copy of the starting point.
	 * @return
	 */
	Vector3f getStart() {
		return this.start;
	}
	
	Vector3f getNormal() {
		return this.normal;
	}
	
	public Vector3f projectToPlane(Line l) {
		if ((l.getPointer().x*normal.x + l.getPointer().y*normal.y + l.getPointer().z*normal.z) == 0) {
			throw new RuntimeException("Line and plane are orthogonal");
		}
		float t = (d - a*l.getStart().x-b*l.getStart().y-c*l.getStart().z) / (a*l.getPointer().x + b*l.getPointer().y + c*l.getPointer().z);
		return l.getTranslated(t);
	}
}
