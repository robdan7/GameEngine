#version 450 core

layout(location = 0) in vec4 vertex;


#uniform Matrices;


void main() {
	//texCoord = (vec2(gl_Vertex.x,-gl_Vertex.y)*0.5+0.5);
	gl_Position = camera*vertex;
}
