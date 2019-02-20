package core.graphics.models;

import java.io.IOException;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import core.graphics.renderUtils.Camera;
import core.graphics.renderUtils.RenderObject;
import core.graphics.renderUtils.Shaders;
import core.input.Mouse;
import core.input.listeners.MouseController;
import core.input.listeners.MouseListener;
import core.utils.math.Line;
import core.utils.math.Matrix4f;
import core.utils.math.Plane;
import core.utils.math.Vector;
import core.utils.math.Vector2f;
import core.utils.math.Vector3f;
import core.utils.math.Vector4f;
import core.utils.other.Timer;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL20.*;

/**
 * 
 * @author Robin
 *
 */
public class Pawn extends MouseListener {
	private Vector3f[] moveDirection;
	private Vector3f position;
	private float movementSmoothing = 0;
	
	private float moveVelocity;
	
	private float time;
	private Timer clock;
	
	private Camera cam;
	camFollow followmode;
	
	ModelBlueprint model;

	Plane p;
	Line l;
	
	/**
	 * Camera follow mode: FPV.
	 * @param upVector - Up direction. Presumably 0,1,0.
	 * @param right - Right vector. Must not be the same as up.
	 * @param fovy - Angle of field of view.
	 * @param aspect - Window width divided by window height.
	 * @param zNear - The new plane. Set to roughly 0.001f.
	 * @param zFar - The far plane. Depth will be inaccurate if the far plane is to far away.
	 */
	public Pawn(Vector3f upVector, Vector3f right, float fovy, float aspect, float zNear, float zFar) {
		this.position = new Vector3f();
		cam = new Camera(upVector, right, fovy, aspect, zNear, zFar, Camera.updateType.BOTH);
		this.cam.bindFocusPos(this.position);
		moveDirection = new Vector3f[3];
		moveDirection[0] = new Vector3f(); // The current movement direction.
		moveDirection[1] = new Vector3f(); // The new movement direction.
		moveDirection[2] = new Vector3f(); // Movement direction from actuators.
		moveVelocity = 1;
		this.followmode = camFollow.FPV;
		clock = new Timer();
		this.l = new Line(new Vector3f(0,0,0), new Vector3f(0,0,1));
		p = new Plane(new Vector3f(0,2,0), new Vector3f(0,1,0));
	}
	
	/**
	 * Set this pawn to a third person preset.
	 * @param smooth - How smooth the movements are.
	 * @param position - Starting position.
	 * @param velocity - Max falking velocity.
	 */
	public void thirdPersonPreset(float smooth, Vector position, float velocity, float camOffset) {
		this.setSmoothAmount(smooth);
		this.setPosition(position);
		this.resetMovement();
		this.setWalkVelocity(velocity);
		
		
		this.cam.setFocusOffset(camOffset);
		this.setFollowmode(Pawn.camFollow.THIRDVIEW);
	}
	
	/**
	 * Add a .obj model to the pawn.
	 */
	public void addModel(String file) {
		this.model = new ModelBlueprint(file);
	}
	
	/**
	 * Add a .obj model to the pawn.
	 */
	public void addModel(ModelBlueprint m) {
		this.model = m;
	}
	
	/**
	 * Bind a model texture to a shader.
	 * @param shader
	 */
	public void bindTexture(Shaders shader) {
		if (this.model == null) {
			throw new RuntimeException("No model exists");
		}
		this.model.bindTexture(shader);
	}
	
	/**
	 * Set the movement velocity of the pawn.
	 * @param v - velocity in unit size.
	 */
	public void setWalkVelocity(float v) {
		this.moveVelocity = v;
	}

	/**
	 * Reset the movement speed.
	 */
	public void resetMovement() {
		moveDirection[2].setZero();
	}
	
	/**
	 * 
	 * @param f - Velocity X in view space coordinates.
	 */
	public void setXvelocity(float f) {
		this.moveDirection[2].x += f;
	}
	
	/**
	 * 
	 * @param f - Velocity Y in view space coordinates.
	 */
	public void setYvelocity(float f) {
		this.moveDirection[2].y += f;
	}
	
	/**
	 * 
	 * @param f - Velocity Z in view space coordinates.
	 */
	public void setZvelocity(float f) {
		this.moveDirection[2].z += f;
	}
	
	public void setFollowmode(camFollow mode) {
		followmode = mode;
	}
	
	
	/** Convert the input velocity to a change in position. Movement in y direction is locked to the y axis.
	 *
	 */
	public void updateMovement() {
		float tempX = this.moveDirection[2].x;
		float tempZ = this.moveDirection[2].z;
		this.moveDirection[2].x = (float)Math.sin(this.cam.gethAngle())*tempZ + (float)Math.cos(this.cam.gethAngle())*tempX; 	// Rotate x.
		this.moveDirection[2].z = (float)Math.sin(this.cam.gethAngle())*(-tempX) + (float)Math.cos(this.cam.gethAngle())*tempZ;	// Rotate z.
		this.moveDirection[2].normalize();
		if (this.movementSmoothing != 0) {
			this.moveSmooth();
		} else {
			this.move();
		}
		this.model.translate(this.position);
		//if (!this.position.equals(oldPos)) {
		this.updateCamera();
		//}
	}
	
	/**
	 * Move by using an interpolated vector. As time passes the vector will shift in direction.
	 * A "smoothing" variable can be set to determine the time for a change in direction. 
	 */
	private void moveSmooth() {
		float delta = clock.getDeltaT();
		this.time += delta;
		if (this.time > this.movementSmoothing) this.time = this.movementSmoothing;
			
		if (!this.moveDirection[2].equals(this.moveDirection[1])) {
			this.moveDirection[0] = this.smoothMovement(this.moveDirection[0], this.moveDirection[1], this.time/this.movementSmoothing).toVec3f();
			this.moveDirection[1] = this.moveDirection[2].toVec3f();
			this.time = 0;
		}
		Vector v = this.smoothMovement(this.moveDirection[0], this.moveDirection[1], this.time/this.movementSmoothing);
		
		this.position.add(v.multiply(delta*this.moveVelocity));
	}
	
	private void move() {
		throw new IllegalArgumentException("This method is not supported yet");
	}
	
	
	/**
	 * Update the camera view for all shaders with the correct uniform index for the connected block.
	 */
	private void updateCamera() {
		//glUseProgram(shader.getShaderProgram());
		if(this.followmode != camFollow.STATIC) {
			//cam.bindFocusPos(this.position);
			/*if (this.followmode == camFollow.THIRDVIEW) {
				cam.lookAt(this.camoffset);
			} else {
				cam.lookAt();
			}*/
			//this.cam.lookAt();
		}
		this.cam.lookAt();
		cam.updateUniform();
		//glUseProgram(0);
	}
	
	/**
	 * Rotate camera view if the camera is not set to static.
	 * @param x - Horizontal angle.
	 * @param y - Vertical angle.
	 */
	public void rotateCamera(float x, float y) {
		cam.rotate(x, y);
	}
	/**
	 * Enable momentum from moving after keys are released.
	 * @param v - Vector with parameters 0 to 1.
	 */
	public void setSmoothAmount(float v) {
		this.movementSmoothing = v;
	}
	
	/**
	 * Use this for smooth movement.
	 * It interpolates the current movement vector with the previous movement direction.
	 * @param t - Time that should pass before the movement direction has changed completely. Set to 0 for no smoothing and 1 for ice skating.
	 */
	public Vector smoothMovement(Vector v, Vector v2, float t) {
		return v.interpolate(v2, t);
	}
	
	/**
	 * Set the position.
	 * @return Position in world coordinates.
	 */
	public Vector3f getPosition() {
		return this.position;
	}
	
	public void setPosition(Vector pos) {
		this.position.x = pos.x;
		this.position.y = pos.y;
		this.position.z = pos.z;
	}
	
	public Camera getCamera() {
		return this.cam;
	}
	
	/**
	 * Set the camera to static of following.
	 *
	 */
	public enum camFollow {
		STATIC, FPV, THIRDVIEW
	}
/*
	@Override
	public void setShader(Shaders shader) {
		
		this.model.setShader(shader);
	}

	@Override
	public int getShader() {
		
		return this.model.getShader();
	}

	@Override
	public void setDepthShader(Shaders shader) {
		
		this.model.setDepthShader(shader);
	}

	@Override
	public int getDepthShader() {
		
		return this.model.getDepthShader();
	}
*/
	public ModelBlueprint getModel() {
		return this.model;
	}
	
	@Override
	public void leftClick(MouseController obs) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void leftClickRelease(MouseController obs) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void rightClick(MouseController obs) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void rightClickRelease(MouseController obs) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseMovement(MouseController obs, Vector2f v) {
		Vector3f pointer = ((Mouse)obs).getNormalizedPosition().toVec3f();
		pointer.z = -1;
		l.setDirection(pointer);
		Vector3f v2 = Vector.multiply(this.cam.getForward(), this.cam.getCamOffset()).toVec3f();
		l.setStart(Vector.subtract(this.position, v2));
	}
}

