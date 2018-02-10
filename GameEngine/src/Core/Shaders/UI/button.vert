#version 450 core

layout(location = 0) in vec4 vertex;
uniform mat4 translateMatrix;
uniform sampler2D tex;

out vec2 textureCoords;

layout (std140, binding = 2) uniform Window {
	layout(offset = 0) vec2 size;
} window;

void main() {
	textureCoords = vertex.xy;
	gl_Position = (translateMatrix*(vertex * vec4(textureSize(tex,0)*0.5,1,1)))/vec4(window.size.x/2,window.size.y/2,1,1);	
}