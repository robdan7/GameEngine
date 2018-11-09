#version 450 core

#define uniform = Light;
#define uniform = Matrices;

uniform sampler2D modelTexture;

layout(location = 0) in vec4 vertex;
layout(location = 1) in vec3 normal;
layout(location = 2) in vec2 texCoord;

out vec4 staticDepthPos, dynamicDepthPos;
out vec4 n, lNormal, vertexPosition;
out vec2 textureCoord;
out vec4 diffuseLight;

out vec4 staticDepthNormal;

// Import methods for calculating light.
#import lightUtils;

void main() {
	n = calcNormal(translateMatrix, normal);
	lNormal = calcLightNormal(translateMatrix, lightSource.position, vertex);
	
	
	
	diffuseLight = light(lNormal, n);
	textureCoord = texCoord.xy;
	vertexPosition = translateMatrix* vertex;
	staticDepthPos = staticOrthoMatrix*vertexPosition*0.5+0.5;
	dynamicDepthPos = dynamicOrthoMatrix*vertexPosition*0.5+0.5;
	
	
	//eyeNormal = normalize(viewMatrix*vertexPosition);
	//vec4 mirrorLight = lNormal- 2*dot(lNormal, n)*n;
	//eye = normalize(camera*mirrorLight);
	//mirrorLight = vec4(dot(eye,vec3(0,0,1)),0,0,0);
	
	gl_Position  = camera* vertexPosition;
}