package core.physics.collision;

public abstract class CollisionObject {
	private BoundingSphere fastCollisionSphere;

	public CollisionObject() {
		// TODO Auto-generated constructor stub
	}
	
	BoundingSphere getCollisionSphere() {
		return this.fastCollisionSphere;
	}

}
