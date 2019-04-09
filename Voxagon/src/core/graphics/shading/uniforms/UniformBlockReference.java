package core.graphics.shading.uniforms;

import java.util.HashMap;

import core.utils.datatypes.GlueList;

/**
 * <p>This class keeps a pointer to a global uniform block. When a uniform is created a 
 * reference to that object must be generater, in order for order classes to not create
 * duplicates of the same uniform.</p>
 * 
 * <p>An instance of this class is not required to store individual member names of a block. 
 * It instead stores the allocated data at a specified index.</p>
 * 
 * 
 * @author Robin
 *
 */
public class UniformBlockReference {
	private int binding;
	private String name;
	private static HashMap<String, UniformBlockReference> createdReferences;
	private GlueList<UniformBlockMember> members;
	
	static {
		createdReferences = new HashMap<String,UniformBlockReference>();
	}

	private UniformBlockReference(int binding, String name) {
		this.members = new GlueList<UniformBlockMember>();
		this.binding = binding;
		this.name = name;
	}
	
	public static UniformBlockReference requestBlockReference(String name) {
		if (!createdReferences.containsKey(name)) {
			// create a new reference. The binding is equal to the current size (0,1,2,3...)
			UniformBlockReference r = new UniformBlockReference(createdReferences.size(), name);
		} else {
			return createdReferences.get(name);
		}
		return null;
	}
	
	public int getbinding() {
		return this.binding;
	}
	
	public String getName() {
		return this.name;
	}
	
	/**
	 * Request a new member to be added to this uniform reference. 
	 * The member is only added if no other member currently exist at the 
	 * same index.
	 * @param index - The index in the uniform to use. 1, 2, 3...
	 * @param stride - The data size in bytes.
	 * @throws UniformTypeEception If a member exists with the same index but a different type
	 * this exception is thrown as an indication of user error. It is not allowed to interpret 
	 * a member as different variable types.
	 */
	public void requestNewMember(int index, UniformMemberType type) throws UniformTypeEception {
		// TODO find out what to do if memory is allocated to slot 1 and 3, but not 2.
		// We cant calculate the offset in a struct if one index is missing!
		/* Solution? Don't allow index in appendMember, or don't allow appending at all 
		 * outside the shader package. 
		 * 
		 * Solution 2: Let the shaders define index if a member doesn't exist. Throw an error is a complication occurs.
		 * 
		 * Solution 3: Throw an error if the shaders sees a hole in the index count. 1..?..3 -> ERROR
		 * 
		 * Solution 4: don't care about it. This means that every index after 2 will be shifted. This would be fixed if 
		 * an index is added later.
		 * 
		 * Solution 4 is the simplest. Do that.
		 */
		if (this.members.size() == 0) {
			this.members.add(new UniformBlockMember(index, type));
			return;
		}
		
		/* Add a member at the target index in the list. There could be a gap in the list, 
		 * so the code must detect where a member should be even if the uniform is incomplete. 
		 */
		int i = 0;
		for (; i < this.members.size() && i < index; i++) {
			UniformBlockMember m = this.members.get(i);
			if (m.getIndex() == index && type != m.getType()) {
				throw new UniformTypeEception("In "+ this.name + "At index " + i + ": A member already exist different type!");
			}
			if (m.getIndex() > index) {
				break;
			}
		}
		this.members.add(i,new UniformBlockMember(index, type));
	}
	
	public static enum UniformMemberType {
		VEC4("vec4", 16), VEC3("vec3", 16), VEC2("vec2", 8), MAT4("mat4", 64);
		private String type;
		private int stride;
		UniformMemberType(String type, int stride) {
			this.type = type;
			this.stride = stride;
		}
		
		int getStride() {
			return this.stride;
		}
	}
	
	/**
	 * representation of a block member
	 * @author Robin
	 *
	 */
	private static class UniformBlockMember {
		private int index, stride;
		UniformMemberType type;
		/**
		 * Create a member with a stride and and index.
		 * @param index
		 * @param stride
		 */
		private UniformBlockMember(int index, UniformMemberType type) {
			this.index = index;
			this.type = type;
		}
		
		/**
		 * Get the size of this member. Measured in bytes.
		 * @return
		 */
		private int getStride() {
			return this.type.getStride();
		}
		
		private int getIndex() {
			return this.index;
		}
		
		private UniformMemberType getType() {
			return this.type;
		}
		
		@Override
		public boolean equals(Object o) {
			if (!(o instanceof UniformBlockMember)) {
				return false;
			}
			
			if (((UniformBlockMember)o).type != this.type) {
				return false;
			}
			
			if (this.index != ((UniformBlockMember)o).index) {
				return false;
			}
			
			return true;
		}
	}

	public static class UniformTypeEception extends Exception {
		private static final long serialVersionUID = 1L;
		
		public UniformTypeEception() {
			this("");
		}
		
		public UniformTypeEception(String s) {
			super(s);
		}
	}
}
