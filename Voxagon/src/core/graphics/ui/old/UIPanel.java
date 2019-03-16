package core.graphics.ui.old;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import core.graphics.misc.Texture;
import core.graphics.renderUtils.Shaders;
import core.input.Mouse;
import core.utils.math.Matrix4f;
import core.utils.math.Vector2f;

public class UIPanel {
	ArrayList<UIElement> elements;
	Map<Elements, Shaders> shaders;
	Map<Elements, Texture> textures;

	public UIPanel() {
		elements = new ArrayList<>();
		this.shaders = new HashMap<Elements, Shaders>();
		this.textures = new HashMap<Elements, Texture>();
	}

	public void render(Matrix4f mat) {
		for (UIElement l : elements) {
			l.render(mat);
		}
	}

	public void mouseCollision( Vector2f v) {
		for (UIElement l : elements) {
			l.mouseCollision(v);
		}
	}

	public void addElement(UIElement l) {
		elements.add(l);
	}

	public void addButton(Runnable r, Texture t) {
		elements.add(new Button(r, t));
	}

	public void addButton(Vector2f position, Runnable r, Texture t) {
		Button b = new Button(position, r, t);
		b.setShader(shaders.get(Elements.BUTTON));
		elements.add(b);
	}
	
	public void addShader(String vert, String frag, Elements element) {
		if(!this.shaders.containsKey(element)) {
			this.shaders.put(element, new Shaders(vert,frag,true));
		}
	}
	
	public void addTexture(String tex, Elements element) {
		Texture texture = new Texture(tex, "tex");
		this.textures.put(element, texture);
		texture.bindAsUniform(this.shaders.get(element).getShaderProgram());
	}
	
	public Texture getTexture(Elements element) {
		return this.textures.get(element);
	}

	public void deleteElement() {

	}
	
	
	public enum Elements {
		BUTTON, LABEL, PANEL, CURSOR,
	}
}
