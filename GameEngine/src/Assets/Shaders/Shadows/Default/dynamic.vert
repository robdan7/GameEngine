#version 450 core

layout(location = 0) in vec4 vertex;
#define uniform = Matrices;

void main() {
	gl_Position  = dynamicOrthoMatrix* translateMatrix * vertex;
	//gl_Position = vertex;
}