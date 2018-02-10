#version 450 core

layout(location = 0) in vec4 vertex;
uniform mat4 orthomatrix;
uniform mat4 translateMatrix;

void main() {
	gl_Position  = orthomatrix* translateMatrix * vertex;
	//gl_Position = vertex;
}