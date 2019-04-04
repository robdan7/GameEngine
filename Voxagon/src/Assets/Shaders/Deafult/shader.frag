#version 450 core

//#uniform Light lightSource;
//#uniform Matrices;

//uniform sampler2D staticShadowmap, dynamicShadowmap;
uniform sampler2D modelTexture;

//in vec4 staticDepthPos, dynamicDepthPos;
layout(location = 0) in Data {
	layout(location = 0) vec4 vertexPosition;
	layout(location = 1) vec4 n;
	layout(location = 2) vec2 uv;
} fragmentData;

layout(location = 0) out vec4 outColor;	// locations are stored in Quad.java
layout(location = 1) out vec4 outNormal;
layout(location = 2) out vec4 worldPosition;

void main() {

	outNormal = vec4(fragmentData.n.xyz*0.5+0.5,1);
	outColor = texture(modelTexture, fragmentData.uv);
	worldPosition = fragmentData.vertexPosition;
	
	gl_FragDepth = gl_FragCoord.z;
}