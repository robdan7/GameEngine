#version 450 core
layout(location = 0) in vec4 vertex;
out vec2 texCoord;
out vec2 frag;

void main() {
	texCoord = (vertex.xy*0.5+0.5);
	frag = vertex.xy;
	gl_Position = vertex;
}