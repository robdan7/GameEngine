package core.graphics.shading.uniforms.references;

import core.graphics.shading.GLSLvariableType;
import core.graphics.shading.uniforms.Uniform;
import core.utils.datatypes.GlueList;

/**
 * representation of a block member
 * @author Robin
 *
 */
public class UniformReference {
		private int index;
		GLSLvariableType type;
		GlueList<UniformReference> siblings;
		/**
		 * Create a member with a stride and and index.
		 * @param index
		 * @param stride
		 * @throws ReferenceCreationException 
		 */
		UniformReference(int index, GLSLvariableType type, GlueList<UniformReference> siblings) throws ReferenceCreationException {
			this.index = index;
			this.type = type;
			this.siblings = siblings;
			
			if (this.hasSiblings()) {
				if (this.siblings.contains(this)) {
					throw new ReferenceCreationException(this.index + ", "+ this.type +" A sibling with the same name exist already!");
				}
			}
		}
		
		public UniformReference(GLSLvariableType type) {
			
		}
		
		/**
		 * Get the size of this member. Measured in bytes.
		 * @return
		 */
		int getStride() {
			return this.type.getStride();
		}
		
		int getIndex() {
			return this.index;
		}
		
		private boolean hasSiblings() {
			return this.siblings != null && this.siblings.size() > 0;
		}
		
		GLSLvariableType getType() {
			return this.type;
		}
		
		@Override
		public boolean equals(Object o) {
			if (!(o instanceof UniformReference)) {
				return false;
			}
			
			if (((UniformReference)o).type != this.type) {
				return false;
			}
			
			if (this.index != ((UniformReference)o).index) {
				return false;
			}
			
			return true;
		}
		
		public static class ReferenceCreationException extends Exception {
			public ReferenceCreationException() {
				super();
			}
			
			public ReferenceCreationException(String s) {
				super(s);
			}
		}
	}
