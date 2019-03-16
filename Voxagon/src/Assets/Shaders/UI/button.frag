#version 450 core
in vec2 textureCoords;

uniform sampler2D tex;

void main(){
	if (texture(tex,textureCoords) != vec4(1,1,1,1)) {
		gl_FragColor = texture(tex,textureCoords);
	} else {
		gl_FragColor = vec4(0,0,0,0);
	}
}