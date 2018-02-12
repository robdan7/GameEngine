package core.graphics.ui;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import core.engine.Window;
import core.graphics.renderUtils.Uniform;
import core.input.Mouse;
import core.utils.math.Matrix4f;
import core.utils.math.Vector2f;
import core.utils.other.BufferTools;
import core.utils.other.Texture;
import core.input.listeners.MouseListener;
import core.input.listeners.MouseController;

import static org.lwjgl.opengl.GL15.*;

public class MenuSystem extends MouseListener{
	UIPanel activePanel;
	Map<String, UIPanel> uiPanels;
	int uniformIndex;
	Window window;
	
	public MenuSystem(Window window, int uniformIndex) {
		this.uniformIndex = uniformIndex;
		this.init(window.getWidth(), window.getHeight(), uniformIndex);
		uiPanels = new HashMap<String, UIPanel>();
		this.window = window;
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
			/*if (this.mouse.isVisible() && this.mouse.leftClick()) {
				this.activePanel.mouseCollision(mouse.getScreenPosition());
			}*/
		} else {
			//System.err.println("No active UI panel has been selected.");
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

	@Override
	public void leftClick(MouseController obs) {
		if (obs.isVisible()) {
			this.activePanel.mouseCollision(obs.getScreenPosition(this.window));
		}
	}

	@Override
	public void rightClick(MouseController obs) {
		
	}

	@Override
	public void mouseHover(MouseController obs, Vector2f v) {
		
	}

	@Override
	public void leftClickRelease(MouseController obs) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void rightClickRelease(MouseController obs) {
		// TODO Auto-generated method stub
		
	}
}
