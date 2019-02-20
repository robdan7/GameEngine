package core.graphics.ui;

import java.util.ArrayList;

import core.graphics.renderUtils.RenderObject;
import core.graphics.renderUtils.Shaders;
import core.utils.datatypes.Stack;

public class UiPanel implements RenderObject {
	private static Stack<UiPanel> uiStack;
	private boolean visible = false;
	private ArrayList<UiItem> items;
	
	static {
		uiStack = new Stack<UiPanel>();
	}
	
	public static UiPanel getActive() {
		return uiStack.getTop();
	}
	
	public UiPanel(String panelfile) {
		items = new ArrayList<UiItem>();
	}
	
	/**
	 * Switch to a new UI panel and hide the previous.
	 * @param panelfile
	 */
	public static void switchPanel(String panelfile) {
		UiPanel panel = new UiPanel(panelfile);
		uiStack.getTop().switchPanel(panel);
		
	}
	
	/**
	 * Switch to a new UI panel and hide this one.
	 * @param panel
	 */
	private void switchPanel(UiPanel panel) {
		this.hide();
		uiStack.push(panel);
	}
	
	public UiPanel() {
		
	}
	
	public void hide() {
		this.visible = false;
	}
	
	public void show() {
		this.visible = true;
	}
	
	public void addItem(UiItem item) {
		
	}

	@Override
	public void render() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void renderTextured() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void discard() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setShader(Shaders shader) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setDepthShader(Shaders shader) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getShader() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getDepthShader() {
		// TODO Auto-generated method stub
		return 0;
	}
}
