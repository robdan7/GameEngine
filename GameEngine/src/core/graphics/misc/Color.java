package core.graphics.misc;

import core.utils.math.Vector;
import core.utils.math.Vector4f;

public class Color {
	private Vector4f color;
	private static int colorValue = 255;
	
	public Color() {
		this(0,0,0,0);
	}
	
	public Color(float r, float g, float b, float a) {
		this.color = new Vector4f(r,g,b,a);
	}
	
	public void setColor(float r, float g, float b, float a) {
		this.color.x = r;
		this.color.y = g;
		this.color.z = b;
		this.color.w = a;
	}
	
	public void setColor(Color c) {
		this.setColor(c.color.x, c.color.y, c.color.z,c.color.w);
	}
	
	public void setColor(Vector4f v) {
		this.setColor(v.x,v.y,v.z,v.w);
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
		return rgbToPercent(v.x,v.y,v.z,v.w);
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
		return percentToRGB(v.x,v.y,v.z,v.w);
	}
	
	public static Vector4f percentToRGB(Color c) {
		return percentToRGB(c.color);
	}
	
	public float getR() {
		return this.color.x;
	}
	
	public float getG() {
		return this.color.y;
	}
	
	public float getB() {
		return this.color.z;
	}
	
	public float getAlpha() {
		return this.color.w;
	}
	
	public Vector getColor() {
		return this.color;
	}
}
