package core.engine;

import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;

import core.graphics.renderUtils.RenderObject;
import core.graphics.renderUtils.buffers.Framebuffer;
import core.utils.datatypes.GlueList;

public class RenderEngine {
	GlueList<RenderStage> renderStages;

	public RenderEngine() {
		this.renderStages = new GlueList<RenderStage>();
	}
	
	public void renderAll() {
		for (RenderStage stage : this.renderStages) {
			stage.render();
		}
	}
	
	public void addRenderStage(Framebuffer framebuffer, GlueList<RenderObject> objects) {
		this.renderStages.add(new RenderStage(framebuffer, objects));
	}
	
	private static class RenderStage {
		private Framebuffer framebuffer;
		private GlueList<RenderObject> rendercontent;
		
		private RenderStage(Framebuffer framebuffer) {
			this.framebuffer = framebuffer;
			this.rendercontent = new GlueList<RenderObject>();
		}
		
		private RenderStage(Framebuffer framebuffer, GlueList<RenderObject> renderObjects) {
			this.framebuffer = framebuffer;
			this.rendercontent = renderObjects;
		}
		
		public void addRenderObject(RenderObject o) {
			this.rendercontent.add(o);
		}
		
		private void render() {
			this.framebuffer.bindBuffer();
			this.framebuffer.clearBuffer();
			for (RenderObject obj : this.rendercontent) {
				//obj.render();
				obj.renderTextured();
			}
			//glBindFramebuffer(GL_FRAMEBUFFER, 0);
		}
	}

}
