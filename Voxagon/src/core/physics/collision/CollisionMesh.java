package core.physics.collision;

import core.graphics.models.Model;
import core.graphics.models.ModelBlueprint;
import core.utils.math.Vector3f;

public class CollisionMesh extends CollisionObject{
	private ModelBlueprint m;
	private Vector3f postition;

	public CollisionMesh(ModelBlueprint m) {
		this.m = m;
	}

	@Override
	public Vector3f getFurthestPoint(Vector3f direction) {
		Vector3f result = m.getVertices().get(0).copy();
		result.add(this.getPosition());
		float dot = result.dot(direction);
		float newDot = dot;
		
		
		for (Vector3f v : m.getVertices()) {
			if ((newDot = v.asAdded(this.getPosition()).dot(direction)) >= dot) {
				dot = newDot;
				result.set(v);
				result.add(this.getPosition());
			}
		}
		
		return result;
	}

	@Override
	public float getfurthestDistance(Vector3f direction) {
		return 0;
	}

	@Override
	public Vector3f getPosition() {
		return this.m.getPosition();
	}

	@Override
	public void setPosition(Vector3f position) {
		this.postition.set(position);
	}

}
