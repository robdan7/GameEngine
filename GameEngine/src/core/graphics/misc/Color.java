package core.graphics.misc;

import core.utils.math.Vector;
import core.utils.math.Vector4f;

public class Color {
	private Vector4f color;
	private final static int colorValue = 255;
	
	public Color() {
		this(0,0,0,0);
	}
	
	public Color(float r, float g, float b, float a) {
		this.color = new Vector4f(r,g,b,a);
	}
	
	public void setColor(float r, float g, float b, float a) {
		this.color.set(r, g, b, a);
	}
	
	public void setColor(Color c) {
		this.setColor(c.color.getW(), c.color.getY(), c.color.getZ(),c.color.getW());
	}
	
	public void setColor(Vector4f v) {
		this.color = (Vector4f) v.copy();
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
		Vector4f temp = v.copy();
		temp.multiply(1/colorValue);
		return temp;
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
		//return (Vector4f)Vector.multiply(v, colorValue);
		v.multiply(colorValue);
		return v;
	}
	
	public static Vector4f percentToRGB(Color c) {
		return percentToRGB(c.color);
	}
	
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
