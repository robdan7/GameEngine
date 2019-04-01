package core.physics.collision;

import core.utils.math.Vector3f;
import core.utils.math.geometry.Sphere;

public class BoundingSphere extends CollisionObject{
	private Sphere collisionSphere;
	
	public BoundingSphere(Vector3f offset, float radius) {
		this.collisionSphere = new Sphere(offset, radius);
	}
	
	BoundingSphere getCollisionSphere() {
		return this;
	}
	
	public float getRadius() {
		return this.collisionSphere.getRadius();
	}

	@Override
	public Vector3f getFurthestPoint(Vector3f direction) {
		Vector3f result = direction.asMultiplied(this.collisionSphere.getRadius());
		result.add(this.collisionSphere.getPosition());
		return result;
	}

	@Override
	public float getfurthestDistance(Vector3f direction) {
		return this.collisionSphere.getRadius()+this.collisionSphere.getPosition().dot(direction);
	}

	@Override
	public Vector3f getPosition() {
		return this.collisionSphere.getPosition();
	}

	@Override
	public void setPosition(Vector3f position) {
		this.getCollisionSphere().setPosition(position);
	}

}
