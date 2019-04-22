#version 450 core

layout(location = 0) in vec4 vertex;
//#uniform Matrices;

layout (std140, binding = 0) uniform Light {
		layout(offset = 0) uniform vec4 position;
		layout(offset = 16) uniform vec4 diffuse;
		layout(offset = 32) uniform vec4 ambient;
		layout(offset = 48) uniform vec4 specular;
} lightSource;

layout(std140, binding = 1) uniform Matrices {
	layout(offset = 0) uniform mat4 camera;
	layout(offset = 64) uniform mat4 viewMatrix;
	layout(offset = 128) uniform mat4 translateMatrix;
	layout(offset = 192) uniform mat4 staticOrthoMatrix;
	layout(offset = 256) uniform mat4 dynamicOrthoMatrix;
};

void main() {
	gl_Position  = dynamicOrthoMatrix* translateMatrix * vertex;
	//gl_Position = vertex;
}