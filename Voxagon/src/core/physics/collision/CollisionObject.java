package core.physics.collision;

import core.utils.math.Vector3f;

public abstract class CollisionObject {
	private BoundingSphere fastCollisionSphere;

	public CollisionObject() {
		// TODO Auto-generated constructor stub
	}
	
	BoundingSphere getCollisionSphere() {
		return this.fastCollisionSphere;
	}
	
	/**
	 * Retrieve the furthest point in a certain direction. The 
	 * position of the point on a plane perpendicular to the direction can 
	 * be anywhere, as long as no other point is on the other side of the plane normal 
	 * (the negative direction).
	 * @param direction
	 * @return
	 */
	public abstract Vector3f getFurthestPoint(Vector3f direction);
	
	public abstract float getfurthestDistance(Vector3f direction);
	
	public abstract Vector3f getPosition();

	public abstract void setPosition(Vector3f position);
}
