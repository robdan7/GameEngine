package core.input.ui;

import core.graphics.renderUtils.RenderObject;
import core.graphics.renderUtils.Shaders;
import core.input.InputInterface;
import core.input.KeyboardListener;
import core.input.MouseListener;
import core.input.MouseObserver;
import core.utils.datatypes.GlueList;
import core.utils.datatypes.Stack;
import core.utils.event.Observer;
import core.utils.math.Vector2f;

public class UiPanel extends  InputInterface implements RenderObject {
	private static Stack<UiPanel> uiStack;
	private boolean visible = false;
	private GlueList<UiItem> items;
	private String panelfile;
	Runnable onHide, onShow;
	
	public static void init(String panelfile) {
		uiStack = new Stack<UiPanel>();
		uiStack.push(new UiPanel(panelfile));
	}
	
	public static void init(MouseListener listener, String panelFile) {
		uiStack = new Stack<UiPanel>();
		uiStack.push(new UiPanel(listener, panelFile));
	}
	
	/**
	 * Get the current active panel from the stack.
	 * @return
	 */
	public static UiPanel getActive() {
		return uiStack.getTop();
	}
	
	public static MouseListener getActiveMouseListener() {
		return uiStack.getTop().getMouseListener();
	}
	
	public static KeyboardListener getActiveKeyboardListener() {
		return uiStack.getTop().getKeyboardListener();
	}
	
	public UiPanel(MouseListener listener, String panelFile) {
		super(listener);
		items = new GlueList<UiItem>();
		this.panelfile = panelFile;
		
		this.onHide = new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				
			}
			
		};
		this.onShow = this.onHide;
	}
	
	public UiPanel(String panelfile) {
		super(new MouseListener() {

			@Override
			public void update(Observer<Object, MouseObserver, MouseListener> b, Object arg) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void buttonclick(int button) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void buttonRelease(int button) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void deltaMovement(MouseObserver obs, Vector2f v) {
				
			}
			
		});
		items = new GlueList<UiItem>();
		this.panelfile = panelfile;
		
		this.onHide = new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				
			}
			
		};
		
		this.onShow = this.onHide;
	}
	
	/**
	 * Switch to a new UI panel and hide the previous one.
	 * @param panelfile
	 */
	public static void addPanel(String panelfile) {
		UiPanel panel = new UiPanel(panelfile);
		addPanel(panel);
	}
	
	/**
	 * Add a pre-defined panel to the top and activate it.
	 * @param panel
	 */
	public static void addPanel(UiPanel panel) {
		uiStack.getTop().hide();
		uiStack.getTop().switchPanel(panel);
	}
	
	/**
	 * Remove the active panel (first in the stack) and re-activate the
	 * previous one.
	 */
	public static void popPanel() {
		uiStack.getTop().hide();
		UiPanel p = uiStack.pull();
		p.releaseAllButtons();
		UiPanel.getActive().show();
	}
	
	
	/**
	 * Switch to a new UI panel. This panel will stay visible 
	 * unless it is hidden first.
	 * @param panel
	 */
	public void switchPanel(UiPanel panel) {
		this.releaseAllButtons();
		uiStack.push(panel);
		panel.show();
	}
	
	/**
	 * Hide this panel. This will activate the hide-action if enabled. 
	 * This can be used to add a new panel and hide this one at the same time, thus 
	 * enabling automatic panel switching when a button is pressed.
	 */
	public void hide() {
		this.onHide.run();
		this.visible = false;
	}
	
	/**
	 * Show this panel. This will activate the show-action if enabled.
	 */
	public void show() {
		this.onShow.run();
		this.visible = true;
	}
	
	/**
	 * Enable a certain action when this panel is disabled.
	 * @param r
	 */
	public void setHideAction(Runnable r) {
		this.onHide = r;
	}
	
	/**
	 * Enable a certain action then this panel is enabled.
	 * @param r
	 */
	public void setShowAction(Runnable r) {
		this.onShow = r;
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
	public void setDepthShader(Shaders shader) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public String toString() {
		return this.panelfile;
	}
}
