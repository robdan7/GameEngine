#version 450 core

layout(location = 0) in vec4 vertex;
#import uniform = Matrices;

void main() {
	gl_Position  = staticOrthoMatrix* translateMatrix * vertex;
	//gl_Position = vertex;
}