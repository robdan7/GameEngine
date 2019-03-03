package core.graphics.ui;

import java.util.ArrayList;

import core.graphics.renderUtils.RenderObject;
import core.graphics.renderUtils.Shaders;
import core.input.MouseListener;
import core.input.MouseObserver;
import core.utils.datatypes.Stack;
import core.utils.event.Observer;
import core.utils.math.Vector2f;

public class UiPanel implements RenderObject, MouseListener {
	private static Stack<UiPanel> uiStack;
	private boolean visible = false;
	private ArrayList<UiItem> items;
	private String panelfile;
	
	private ArrayList<MouseListener> mouseListeners;
	
	public static void init(String panelfile) {
		uiStack = new Stack<UiPanel>();
		uiStack.push(new UiPanel(panelfile));
	}
	
	/**
	 * Get the current active panel from the stack.
	 * @return
	 */
	public static UiPanel getActive() {
		return uiStack.getTop();
	}
	
	public UiPanel(String panelfile) {
		items = new ArrayList<UiItem>();
		this.panelfile = panelfile;
	}
	
	@Override
	public String toString() {
		return this.panelfile;
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
	
	public boolean isVisible() {
		return this.visible;
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

	@Override
	public void update(Observer<Object, MouseObserver, MouseListener> b, Object arg) {

		/*
		for (MouseListener l : this.mouseListeners) {
			l.update(b, arg);
		}
		*/
		System.out.println(this.toString());
		//this.deltaMovement(((MouseObserver)b), ((MouseObserver)b).getDeltaP());
	}

	@Override
	public void buttonclick(int button) {

	}

	@Override
	public void buttonRelease(int button) {
	
	}

	@Override
	public void deltaMovement(MouseObserver obs, Vector2f v) {
		
	}
}
