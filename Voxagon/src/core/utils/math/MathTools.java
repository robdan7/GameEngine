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

}
