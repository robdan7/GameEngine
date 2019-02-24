package core.graphics.models;


import core.graphics.renderUtils.Camera;
import core.graphics.renderUtils.Shaders;
import core.input.Mouse;
import core.input.MouseListener;
import core.input.MouseObserver;
import core.utils.math.Line;
import core.utils.math.Matrix4f;
import core.utils.math.Plane;
import core.utils.math.Vector2f;
import core.utils.math.Vector3f;
import core.utils.other.Timer;

/**
 * 
 * @author Robin
 *
 */
public class Pawn extends MouseListener {
	private Vector3f[] moveDirection;
	private Vector3f position;
	private float movementSmoothing = 0;
	
	//matrix for rotating the player movement velocity
	private Matrix4f rotationMatrix;
	
	private float moveVelocity;

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
		moveDirection[0] = new Vector3f(); // The current movement direction after smoothing.
		moveDirection[1] = new Vector3f(); // New movement direction before smoothing.
		moveDirection[2] = new Vector3f(); // Movement direction from actuators.
		moveVelocity = 1;
		this.followmode = camFollow.FPV;
		clock = new Timer();
		clock.start();
		this.l = new Line(new Vector3f(0,0,0), new Vector3f(0,0,1));
		p = new Plane(new Vector3f(0,2,0), new Vector3f(0,1,0));
		rotationMatrix = new Matrix4f();
	}
	
	/**
	 * Set this pawn to a third person preset.
	 * @param smooth - How smooth the movements are.
	 * @param position - Starting position.
	 * @param velocity - Max falking velocity.
	 */
	public void thirdPersonPreset(float smooth, Vector3f position, float velocity, float camOffset) {
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
	public void addXvelocity(float f) {
		this.moveDirection[2].setX(this.moveDirection[2].getX()+f);
	}
	
	/**
	 * 
	 * @param f - Velocity Y in view space coordinates.
	 */
	public void addYvelocity(float f) {
		this.moveDirection[2].setY(this.moveDirection[2].getY()+f);
	}
	
	/**
	 * 
	 * @param f - Velocity Z in view space coordinates.
	 */
	public void addZvelocity(float f) {
		this.moveDirection[2].setZ(this.moveDirection[2].getZ()+f);
	}
	
	public void setFollowmode(camFollow mode) {
		followmode = mode;
	}
	
	
	/** Convert the input velocity to a change in position. Movement in y direction is locked to the y axis.
	 *
	 */
	public void updateMovement() {
		if (this.movementSmoothing != 0) {
			this.moveSmooth();
		} else {
			this.move();
		}
		this.model.translate(this.getPosition());
		this.updateCamera();
	}
	
	/**
	 * Move by using an interpolated vector. As time passes the vector will shift in direction.
	 * A "smoothing" variable can be set to determine the time for a change in direction. 
	 */
	private void moveSmooth() {
		Vector3f v = this.moveDirection[0].interpolate(this.moveDirection[1].asNormalized(), (float)(this.clock.getTime()/this.movementSmoothing));
		
		// The direction has changed, reset.
		if (!this.moveDirection[2].equals(this.moveDirection[1])) {
			this.moveDirection[0].set(v);
			this.moveDirection[1].set(this.moveDirection[2]);
			this.clock.reset();
		} 

		this.rotationMatrix.rotate(this.cam.gethAngle(), 0, 1, 0);
		v = rotationMatrix.multiply(v.toVec4f()).toVec3f();

		this.position.add(v.asMultiplied((float)clock.getDelta()*this.moveVelocity));
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
		this.clock.setTargetTime(v);
	}
	
	/**
	 * Get a copy of the position.
	 * @return Position in world coordinates.
	 */
	public Vector3f getPosition() {
		return this.position.copy();
	}
	
	/**
	 * Set the player position.
	 * @param pos
	 */
	public void setPosition(Vector3f pos) {
		this.position.set(pos);
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

	public ModelBlueprint getModel() {
		return this.model;
	}

	@Override
	public void buttonclick(int button) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void buttonRelease(int button) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deltaMovement(MouseObserver obs, Vector2f v) {
		if (!obs.isVisible()) {
			this.rotateCamera(-v.getX(), -v.getY());
		}
	}
}

