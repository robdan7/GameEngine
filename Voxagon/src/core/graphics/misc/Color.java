package core.graphics.misc;

import core.utils.math.Vector4f;

public class Color {
	private Vector4f color;
	private static float colorValue = 255.0f;
	
	public Color() {
		this(0,0,0,0);
	}
	
	public Color(float r, float g, float b, float a) {
		this.color = new Vector4f(r,g,b,a);
	}
	
	public void setColor(float r, float g, float b, float a) {
		this.color.set(r,g,b,a);
	}
	
	public void setColor(Color c) {
		this.color.set(c.getColor());
	}
	
	public void setColor(Vector4f v) {
		this.color.set(v);
	}
	
	/**
	 * Translate RGB values to percentages (0-1).
	 * @param r
	 * @param g
	 * @param b
	 * @param a
	 * @return A vector with all values.
	 */
	public static Vector4f rgbToPercent(float r, float g, float b, float a) {
		return new Vector4f(r/colorValue, g/colorValue, b/colorValue,a/colorValue);
	}
	
	/**
	 * Translate RGB values to percentages (0-1).
	 * @param v
	 * @return A vector with all values.
	 */
	public static Vector4f rgbToPercent(Vector4f v) {
		return v.asMultiplied(1/colorValue);
	}
	
	/**
	 * translate RGB values to percentages (0-1).
	 * @param c
	 * @return A vector with all values.
	 */
	public static Vector4f rgbToPercent(Color c) {
		return rgbToPercent(c.color);
	}
	
	public static Vector4f percentToRGB(float r, float g, float b, float a) {
		return new Vector4f(r*colorValue, g*colorValue,b*colorValue,a*colorValue);
	}
	
	public static Vector4f percentToRGB(Vector4f v) {
		return v.asMultiplied(colorValue);
	}
	
	public static Vector4f percentToRGB(Color c) {
		return percentToRGB(c.color);
	}
	
	// TODO Change these
	public float getR() {
		return this.color.getX();
	}
	
	public float getG() {
		return this.color.getY();
	}
	
	public float getB() {
		return this.color.getZ();
	}
	
	public float getAlpha() {
		return this.color.getW();
	}
	
	public Vector4f getColor() {
		return this.color;
	}
}
