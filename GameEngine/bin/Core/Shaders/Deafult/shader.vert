#version 450 core

uniform mat4 camera;
uniform mat4 translateMatrix;
uniform mat4 staticOrthoMatrix, dynamicOrthoMatrix;

uniform sampler2D modelTexture;

layout(location = 0) in vec4 vertex;
layout(location = 1) in vec4 normal;
layout(location = 2) in vec2 texCoord;

out vec4 staticDepthPos, dynamicDepthPos;
out vec2 textureCoord;
out vec4 lightFactor;
out float shadowAngle;

out vec4 staticDepthNormal;

layout (std140, binding = 0) uniform Light {
	layout(offset = 0) vec4 position;
	layout(offset = 16) vec4 ambient;
	layout(offset = 32) vec4 diffuse;
} lightSource;


vec4 light() {
	vec4 n = normalize(translateMatrix*vec4(normal.xyz,0));
	
	vec4 lightNormal = normalize(lightSource.position-translateMatrix* vertex*lightSource.position.w);
	
	shadowAngle = max(dot(n, lightNormal),0);
	vec4 light = min(lightSource.diffuse*vec4(vec3(shadowAngle),1) + lightSource.ambient,1);

	return light;
}

void main() {
	staticDepthNormal = normalize(staticOrthoMatrix*translateMatrix*normal);
	lightFactor = light();
	textureCoord = texCoord.xy;
	vec4 vertexPosition = translateMatrix* vertex;
	staticDepthPos = (staticOrthoMatrix*vertexPosition)*0.5+0.5;
	dynamicDepthPos = (dynamicOrthoMatrix*vertexPosition)*0.5+0.5;
	gl_Position  = camera* vertexPosition;
}