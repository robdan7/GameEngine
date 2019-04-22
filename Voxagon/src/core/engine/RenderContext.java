package core.engine;

import core.utils.datatypes.GlueList;

public class RenderContext {
	private GlueList<RenderStage> renderStages;

	public RenderContext() {
		this.renderStages = new GlueList<RenderStage>();
	}
	
	GlueList<RenderStage> getRendeStages() {
		return this.renderStages;
	}
	
	/**
	 * Render all stages currently active.
	 */
	void renderAll() {
		for (RenderStage stage : this.renderStages) {
			stage.render();
		}
	}


}
