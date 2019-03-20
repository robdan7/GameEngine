package core.utils.math;

public class MathTools {

	/**
	 * Return the point of contact between a line and a plane in euclidean space.
	 * @param p - The plane.
	 * @param l - The line.
	 * @return The contact position.
	 */
	public static Vector3f LineToPlaneProjection(Plane p, Line l) {
		Vector3f normal = p.getNormalCopy();
		Vector4f info = p.getInformation();
		if ((l.getPointer().x*normal.x + l.getPointer().y*normal.y + l.getPointer().z*normal.z) == 0) {
			throw new RuntimeException("Line and plane are orthogonal");
		}
		float t = (info.getW() - info.getX()*l.getStart().x-info.getY()*l.getStart().y-info.getZ()*l.getStart().z) / 
				(info.getX()*l.getPointer().x + info.getY()*l.getPointer().y + info.getZ()*l.getPointer().z);
		return l.getTranslated(t);
	}
	
	/**
	 * Calculate the distance between a point and a plane.
	 * @param p - The plane
	 * @param point - The point
	 * @return Minimum distance between the point and the plane.
	 */
	public static float pointToPlaneDistance(Plane p, Vector3f point) {
		Vector3f dist = p.getOgirin();
		dist.subtract(point);
		dist.flip();
		return dist.dotProduct(p.getOgirin());
	}
	
	
	/**
	 * Project a point to a plane through the shortest distance.
	 * @param p - The plane.
	 * @param point - The point.
	 * @return A projected point onto the plane p.
	 */
	public static Vector3f pointToPlaneProjection(Plane p, Vector3f point) {
		Vector3f distance = p.getNormalCopy();
		distance.flip();
		distance.multiply(pointToPlaneDistance(p, point));
		distance.add(point);
		return distance;
	}

}
