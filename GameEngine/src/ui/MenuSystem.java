package ui;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import input.Mouse;
import utils.math.Matrix4f;
import utils.other.BufferTools;
import utils.other.Texture;
import utils.rendering.Uniform;

import static org.lwjgl.opengl.GL15.*;

public class MenuSystem {
	UIPanel activePanel;
	Map<String, UIPanel> uiPanels;
	int uniformIndex;
	Mouse mouse;

	public MenuSystem(Mouse mouse, int windowWidth, int windowHeight, int uniformIndex) {
		this.mouse = mouse;
		this.uniformIndex = uniformIndex;
		this.init(windowWidth, windowHeight, uniformIndex);
		uiPanels = new HashMap<String, UIPanel>();
	}
	
	private void init(int width, int height, int index) {
		FloatBuffer buffer = BufferTools.asFloatBuffer(new float[] {width, height});
		buffer.flip();
		int UBO = glGenBuffers();
		Uniform.createUniformBlock(UBO, buffer, index, GL_DYNAMIC_DRAW);
	}

	public void addPanel(String vertexShader, String fragmentShader) {

	}
	
	public void updateWindowSize(int width, int height) {
		this.init(width, height, this.uniformIndex);
	}

	public void addPanel(String name) {
		uiPanels.put(name, new UIPanel());
		if (uiPanels.isEmpty()) {
			this.setActivePanel(name);
		}
	}

	public void render(Matrix4f mat) {
		if (this.activePanel != null) {
			this.activePanel.render(mat);
			if (this.mouse.isVisible() && this.mouse.leftClick()) {
				this.activePanel.mouseCollision(mouse.getScreenPosition());
			}
		} else {
			System.err.println("No active UI panel has been selected.");
		}
	}

	public void setActivePanel(UIPanel panel) {
		this.activePanel = panel;
	}

	public void setActivePanel(String panel) {
		this.activePanel = uiPanels.get(panel);
	}

	public UIPanel getActivePanel() {
		return this.activePanel;
	}

	public void addButton(String panel, Runnable run, Texture t) {
		uiPanels.get(panel).addButton(run, t);
	}
}
