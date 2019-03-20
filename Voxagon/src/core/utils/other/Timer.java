package core.utils.other;

import static org.lwjgl.glfw.GLFW.glfwGetTimerValue;
import static org.lwjgl.glfw.GLFW.glfwGetTimerFrequency;

/**
 * Timer, or stop watch, that counts time in seconds. No wrap-around protection is implemented, 
 * but this timer should, in theory, be able to run for a year without problems.
 * @author Robin
 *
 */
public class Timer {
	private double startTime, currentTime, targetTime, frequency, lastDelta;
	private boolean running = false;
	
	/**
	 * Create a new timer. It must be started before it can be used.
	 */
	public Timer() {
		
		// Store a local frequency.
		this.frequency = glfwGetTimerFrequency();
		this.lastDelta = glfwGetTimerValue();
	}
	
	/**
	 * Create a timer with a target time. The timer will stop when the target it reached.
	 * Note: It must be started before it can be used.
	 * @param target - Target time in seconds.
	 */
	public Timer(double target) {
		this();
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
	 * Return the total time in seconds since the timer was started.
	 * The time stops if a target time has been set.
	 * @return
	 */
	public double getTime() {
		if (this.isRunning()) {
			this.currentTime = glfwGetTimerValue();
		} 
		double seconds = (this.currentTime-this.startTime) / this.frequency;
		if (this.targetTime != 0 && seconds > this.targetTime) return targetTime;
		
		return seconds;
	}
	
	/**
	 * Get the delta time since the last call. Measured in seconds.
	 * @return - A time value if it is running, 0 otherwise.
	 */
	public double getDelta() {
		if (this.isRunning()) {
			double time = glfwGetTimerValue();
			double delta = time - this.lastDelta;
			this.lastDelta = time;
			return delta / this.frequency;
		} else {
			return 0;
		}
	}
	
	public boolean isRunning() {
		return this.running;
	}
	
	/**
	 * Get the tick frequency of this timer.
	 * @return
	 */
	public double getFrequency() {
		return this.frequency;
	}
	
}
