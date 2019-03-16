package core.utils.other;
//import static org.lwjgl.glfw.GLFW.glfwGetTime;
import static org.lwjgl.glfw.GLFW.*;

/**
 * Timer, or stop watch, that counts time in seconds. Nothing fancy here.
 * @author Robin
 *
 */
public class Timer {
	private double startTime, currentTime, targetTime, frequency, lastDelta;
	private boolean running = false;
	
	public Timer() {
		
		// Store a local frequency.
		this.frequency = glfwGetTimerFrequency();
		this.lastDelta = glfwGetTimerValue();
	}
	
	/**
	 * Create a timer with a target time. The timer will stop when the target it reached.
	 * @param target - Target time in seconds.
	 */
	public Timer(double target) {
		this.targetTime = target;
		
	}
	
	public void setTargetTime(double time) {
		this.targetTime = time;
	}
	
	/**
	 * Reset the timer.
	 */
	public void reset() {
		this.startTime = glfwGetTimerValue();
	}
	
	public void stop() {
		if (this.running) {
			this.currentTime = glfwGetTimerValue();
			this.running = false;
		}
	}
	
	/**
	 * Start the timer.
	 */
	public void start() {
		if (!this.running) {
			this.running = true;
			this.reset();
		}
	}
	
	/**
	 * Return the time in seconds since the timer was started.
	 * The time stops if a target time has been set.
	 * @return
	 */
	public double getTime() {
		if (this.running) {
			this.currentTime = glfwGetTimerValue();
		} 
		double seconds = (this.currentTime-this.startTime) / this.frequency;
		if (this.targetTime != 0 && seconds > this.targetTime) return targetTime;
		
		return seconds;
	}
	
	/**
	 * Get the delta time since the last call. Measured in seconds.
	 * @return
	 */
	public double getDelta() {
		double time = glfwGetTimerValue();
		double delta = time - this.lastDelta;
		this.lastDelta = time;
		return delta / this.frequency;
	}
	
	/**
	 * Get the tick frequency of this timer.
	 * @return
	 */
	public double getFrequency() {
		return this.frequency;
	}
	
}
