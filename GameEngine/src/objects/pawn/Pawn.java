package objects.pawn;

import java.io.IOException;

import org.lwjgl.opengl.GL11;

import objects.Camera;
import objects.models.ModelBlueprint;
import utils.fileSystem.OBJLoader;
import utils.math.Matrix4f;
import utils.math.Vector2f;
import utils.math.Vector3f;
import utils.other.Timer;
import utils.rendering.RenderObject;
import utils.rendering.Shaders;

import static org.lwjgl.opengl.GL20.*;

/**
 * 
 * @author Robin
 *
 */
public class Pawn implements RenderObject{
	Runnable[] actions;
	private final Vector3f position;
	private Vector2f rotation;
	private Vector3f[] moveDirection;
	private float movementSmoothing = 0;
	
	private float moveVelocity;
	
	private float time;
	private Timer clock;
	
	private Camera cam;
	private float camoffset = 0;
	camFollow followmode;
	
	ModelBlueprint model;
	
	/**
	 * Camera follow mode: FPV.
	 * @param upVector - Up direction. Presumably 0,1,0.
	 * @param fovy - Angle of field of view.
	 * @param aspect - Window width divided by window height.
	 * @param zNear - The new plane. Set to roughly 0.001f.
	 * @param zFar - The far plane. Depth will be inaccurate if the far plane is to far away.
	 * @param cameraName - The name for the camera in shaders.
	 */
	public Pawn(Vector3f upVector, float fovy, float aspect, float zNear, float zFar, String cameraName) {
		cam = new Camera(upVector, fovy, aspect, zNear, zFar, cameraName);
		actions = new Runnable[65536];
		moveDirection = new Vector3f[3];
		moveDirection[0] = new Vector3f();
		moveDirection[1] = new Vector3f();
		moveDirection[2] = new Vector3f();
		moveVelocity = 1;
		this.position = new Vector3f();
		this.rotation = new Vector2f();
		this.followmode = camFollow.FPV;
		clock = new Timer();
	}
	
	public void thirdPersonPreset(float smooth, Vector3f position, float velocity) {
		this.setSmoothAmount(smooth);
		this.setPosition(position);
		this.resetMovement();
		this.setWalkVelocity(velocity);
		
		
		this.setCameraOffset(7);
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
	public void addModel(String file, String modelTerxure) {
		this.model = new ModelBlueprint(file, modelTerxure);
	}
	
	public void bindTexture(Shaders shader) {
		this.model.bindTexture(shader);
	}
	@Override
	public void discard() {
		// TODO Auto-generated method stub
		if(this.model != null) {
			this.model.discard();
		}	
	}
	@Override
	public void render(Matrix4f mat, int shader) {
		this.setMatrix(mat, shader);
		this.model.render();
	}
	@Override
	public void renderTextured (Matrix4f mat, int shader) {
		this.setMatrix(mat, shader);
		this.model.renderTextured();
	}
	
	private void setMatrix(Matrix4f mat, int shader) {
		mat.setIdentity();
		mat.translate(this.position);
		mat.rotate(this.rotation.x, 1, 0, 0);
		this.rotation.x += 0.02f;
		mat.createUniform(shader);
	}
	
	/**
	 * 
	 * @param v - 
	 */
	public void setWalkVelocity(float v) {
		this.moveVelocity = v;
	}

	/**
	 * Reset movement speed.
	 */
	public void resetMovement() {
		//System.out.println(this.moveDirection[2].z);
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
	
	/** Convert the input velocity to a change in position. All movement is locked to the x,y and z axis.
	 *
	 */
	@Deprecated
	public void updateMovement() {
		this.moveDirection[2].normalize();
		if (this.movementSmoothing != 0) {
			this.moveSmooth();
		} else {
			this.move();
		}
	}
	
	public void setFollowmode(camFollow mode) {
		followmode = mode;
	}
	
	
	/** Convert the input velocity to a change in position. Movement in y direction is locked to the y axis.
	 *
	 * @param hAngle - The horizontal angle to follow. For  example the horizontal camera direction. 
	 */
	public void updateMovement(float hAngle) {
		float tempX = this.moveDirection[2].x;
		float tempZ = this.moveDirection[2].z;
		this.moveDirection[2].x = (float)Math.sin(hAngle)*tempZ + (float)Math.cos(hAngle)*tempX; 	// Rotate x.
		this.moveDirection[2].z = (float)Math.sin(hAngle)*(-tempX) + (float)Math.cos(hAngle)*tempZ;	// Rotate z.
		this.moveDirection[2].normalize();
		if (this.movementSmoothing != 0) {
			this.moveSmooth();
		} else {
			this.move();
		}
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
			this.moveDirection[0] = this.smoothMovement(this.moveDirection[0], this.moveDirection[1], this.time/this.movementSmoothing);
			this.moveDirection[1] = this.moveDirection[2].copy();
			this.time = 0;	
		}
		this.position.add(this.smoothMovement(this.moveDirection[0], this.moveDirection[1], this.time/this.movementSmoothing).multiply(delta*this.moveVelocity));
	}
	
	private void move() {
		
	}
	
	
	/**
	 * Update camera position. Should be done before rendering a scene.
	 * Note: glUseProgram(int program) must be called before.
	 * @param shader - The shaderprogram to bind.
	 */
	public void updateCamera(Shaders shader) {
		glUseProgram(shader.getShaderProgram());
		if(this.followmode != camFollow.STATIC) {
			cam.setPosition(this.position);
			if (this.followmode == camFollow.THIRDVIEW) {
				cam.lookAt(this.camoffset);
			} else {
				cam.lookAt();
			}
		}
		cam.updateCamera(shader.getShaderProgram());
		glUseProgram(0);
	}
	/**
	 * Set the distance between player and camera.
	 * @param offset - The offset length.
	 */
	public void setCameraOffset(float offset) {
		camoffset = offset;
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
	public Vector3f smoothMovement(Vector3f v, Vector3f v2, float t) {
		return v.interpolate(v2, t);
	}
	
	/**
	 * 
	 * @return Position in world coordinates.
	 */
	public Vector3f getPosition() {
		return this.position;
	}
	
	public void setPosition(Vector3f pos) {
		this.position.x = pos.x;
		this.position.y = pos.y;
		this.position.z = pos.z;
	}
	
	public Camera getCamera() {
		return this.cam;
	}
	
	/**
	 * Set the camera to static of following.
	 * @author Robin
	 *
	 */
	public enum camFollow {
		STATIC, FPV, THIRDVIEW
	}

	@Override
	public void setShader(Shaders shader) {
		// TODO Auto-generated method stub
		this.model.setShader(shader);
	}

	@Override
	public int getShader() {
		// TODO Auto-generated method stub
		return this.model.getShader();
	}

	@Override
	public void setDepthShader(Shaders shader) {
		// TODO Auto-generated method stub
		this.model.setDepthShader(shader);
	}

	@Override
	public int getDepthShader() {
		// TODO Auto-generated method stub
		return this.model.getDepthShader();
	}
}

