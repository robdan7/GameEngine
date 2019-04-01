package core.physics.collision;

import core.utils.datatypes.GlueList;
import core.utils.math.Vector3f;

public class GJK {

	/**
	 * Given a convex hull a and b: Compute a tetrahedron based on the minkowski 
	 * difference that contains the null vector.
	 * @param a
	 * @param b
	 * @return True if there was a collision. False if not.
	 */
	public static boolean GJKcollision(CollisionObject a, CollisionObject b) {
		GlueList<Vector3f> pointList = new GlueList<Vector3f>();
		Vector3f direction = new Vector3f((float)Math.random()-0.5f,(float)Math.random()-0.5f,(float)Math.random()-0.5f);
		direction = new Vector3f(-0.22f,1,0.4f);
		direction.normalize();
		Vector3f s = calcDifference(a,b, direction);

		pointList.add(s);
		//System.out.println(a.getFurthestPoint(direction));
		
		direction.set(s);
		direction.flip();
		direction.normalize();
		
		//System.out.println(calcDifference(a,b, direction));
		//System.out.println(s.asAdded(direction.asMultiplied(calcDifference(a,b, direction))));

		boolean flag = true;
		float f = 0;
		while (flag) {
			
			//s = direction.asMultiplied(calcDifference(a,b, direction));
			s = calcDifference(a,b,direction);
			/* We have found a point beyond the origin */
			pointList.add(0, s);

			if (s.dot(direction) < 0) {
				/*
				 * We didn't go past the origin, so no intersection has been found yet.
				 */
				flag = false;
				//System.out.println("Caught a false direction");
				return false;
			} else if (pointList.get(1).asSubtracted(pointList.get(0)).crossProduct(pointList.get(0)).length() == 0) {
				//System.out.println("Found a line!");
				return true;
			}
			
			
			
			//s.set(direction.asMultiplied(calcDifference(a,b, direction)));
			
			doSimplex(pointList, direction);
			//if (direction.length() == 0) direction.set((float)Math.random()-0.5f,(float)Math.random()-0.5f,(float)Math.random()-0.5f);
			if (pointList.size() == 4) {
				flag = false;
				 /* The simplex function returned a tetrahedron that surrounds the null vector! */
				return true;
			}
		
			//System.out.println(pointList.toString());
		}
		return false;
	}
	
	/**
	 * Compute a simplex shape based on input simplex. A lesser simplex will 
	 * be returned if the input is an optimal solution to GJK.
	 * @param size
	 * @param points
	 * @param direction
	 */
	private static void doSimplex( GlueList<Vector3f> points, Vector3f direction) {
		Vector3f AB = points.get(1).asSubtracted(points.get(0));
		Vector3f AC = null;
		Vector3f AO = points.get(0).asFlipped();
		switch (points.size()) {
		case 2:	/* Line */
			/* This is a null-check state. We already know point A is beyond the origin.*/
			direction.set(AB.crossProduct(AO));
			
			direction.set(direction.crossProduct(AB));
			/* The direction is now perpendicular to AB in the direction of AO*/
			direction.normalize();
			//System.out.println("Cross product: " + AB.crossProduct(AB));
			
			/* points = [A,B] */
			break;		// BREAK
		case 3: /* Triangle */
			AC = points.get(2).asSubtracted(points.get(0));
			Vector3f ABC = AB.crossProduct(AC);
			if (ABC.crossProduct(AC).dot(AO) >= 0) {
				/* The origin is beyond the line AC */
				if (AC.dot(AO) >= 0) {
					direction = AC.crossProduct(AO).crossProduct(AC);
					direction.normalize();
					/* points = [A,C] */
					points.remove(1);
					break;		// BREAK
				} else {
					/* star case */
				}
			} else {
				if (AB.crossProduct(ABC).dot(AO) >= 0) {
					/* star case */
				} else {
					/* the origin is "inside" the triangle */
					direction.set(ABC);
					if (ABC.dot(AO) > 0) {
						/* points = [A,C,B] */
						points.add(points.remove(1));
					} else {
						direction.flip();
					}
					direction.normalize();
					
					break;		// BREAK
				}
			}
			/* star case */
			if (AB.dot(AO) >= 0) {
				/* points = [A,B]*/
				points.remove(2);
				direction.set(AB.crossProduct(AO).crossProduct(AB));
			} else {
				/* points = [A] */
				points.remove(1);
				points.remove(2);
				direction.set(AO);
			}
			direction.normalize();
			break;		// BREAK
		case 4: /* Tetrahedron */
			AC = points.get(2).asSubtracted(points.get(0));
			Vector3f AD = points.get(3).asSubtracted(points.get(0));
			ABC =  AB.crossProduct(AC);
			Vector3f ADB = AD.crossProduct(AB);
			Vector3f ACD = AC.crossProduct(AD);

			if (ABC.dot(AO) < 0) {
				//System.out.println("ABC is out: " + ABC.dot(AO));
				if (ADB.dot(AO) < 0) {	// Outside first edge.
					/* points = [B,C,D]*/
					points.remove(0);
					//points.add(points.remove(1));
					direction.set(AB.crossProduct(AO).crossProduct(AB));
				} else if (ACD.dot(AO) < 0) {	// Outside second edge.
					/* points = [B,C,D]*/
					points.remove(0);
					//points.add(points.remove(1));
					direction.set(AC.crossProduct(AO));
				} else {	// Only outside the plane.
					/* points = [A,B,C]*/
					points.remove(3);
					points.add(points.remove(1));
					direction.set(ABC);
					direction.flip();
				}
			} else {	// Within the first triangle plane
				
				if (ACD.dot(AO) < 0) {	// Outside the second triangle plane.
					if (ADB.dot(AO) < 0) {	// outside an edge.
						/* points = [B,C,D] */
						points.remove(0);
						//points.add(points.remove(1));
						direction.set(AD.crossProduct(AO).crossProduct(AD));
					} else {	// Only outside the second plane.
						/* points = [A,C,D] */
						points.remove(1);
						direction.set(ACD);
						direction.flip();
					}

				} else if (ADB.dot(AO) < 0) {	// Within two planes, but not the third.
					/* points = [A,D,B] */
					points.remove(2);
					points.add(points.remove(1));
					direction.set(ADB);
					direction.flip();
				}
				/* else: We have a collision! */
			}
			
			direction.normalize();
			break;		// BREAK
		}
	}
	
	/**
	 * Helper function that calculates the distance between a point along 
	 * the direction in A and the opposite direction in B.
	 * @param direction
	 */
	public static Vector3f calcDifference(CollisionObject a, CollisionObject b, Vector3f direction) {
		//return Math.abs(a.getfurthestDistance(direction) - b.getfurthestDistance(direction));
		Vector3f result = a.getFurthestPoint(direction);
		result.subtract(b.getFurthestPoint(direction.asFlipped()));
		return result;
	}

}
