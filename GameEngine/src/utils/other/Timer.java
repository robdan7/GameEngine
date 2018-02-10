package utils.other;
import static org.lwjgl.glfw.GLFW.glfwGetTime;

public class Timer {
	double previousTime;
	public Timer() {
		previousTime = 0;
	}
	
	public void startLap() {
		previousTime = glfwGetTime();
	}
	
	public float getDeltaT() {
		double t0 = this.previousTime;
		this.previousTime = glfwGetTime();
		return (float)(this.previousTime-t0);
	}
}
