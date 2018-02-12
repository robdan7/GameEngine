varying vec2 texCoord;

void main() {
	texCoord = (vec2(gl_Vertex.x,-gl_Vertex.y)*0.5+0.5);
	gl_Position = gl_Vertex;
}
