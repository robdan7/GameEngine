#version 450 core

//in vec2 texCoord;
//uniform sampler2D shadowmap;

vec4 colorMix(vec4 c1, vec4 c2, float w) {
	vec4 col = c1 * (1-w) + c2* w;
	return col;
}

void main() {
	/*vec4 color;
	vec2 inc = 1.0 / textureSize(texturetest, 0);
	for (int x = -3; x <= 3; x += 1) {
		for (int y = -3; y <= 3; y += 1) {
			color += texture(texturetest, texCoord + vec2(x,y)*inc);
		}
	}
	color /= 49;*/
	//gl_FragColor = texture(shadowmap, texCoord);
	gl_FragColor = vec4(1,0,0,1);
}