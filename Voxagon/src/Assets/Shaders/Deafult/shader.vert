#version 450 core

//#uniform Light lightSource;
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

layout(location = 0) in vec4 vertexIn;
layout(location = 1) in vec3 normal;
layout(location = 2) in vec2 uvIn;


//out vec4 staticDepthPos, dynamicDepthPos;
layout(location = 0) out Data {
	layout(location = 0) vec4 vertexPosition;
	layout(location = 1) vec4 n;
	layout(location = 2) vec2 uv;
} outData;

//out vec4 staticDepthNormal;

// include methods for calculating light.
//#include lightUtils;

void main() {
	//outData.n = calcNormal(translateMatrix, normal);
	outData.n = normalize(translateMatrix * vec4(normal,0));
	//outData.lNormal = calcLightNormal(translateMatrix, lightSource.position, vertexIn);
	
	
	
	//diffuseLight = light(outData.lNormal, outData.n);
	outData.uv = uvIn.xy;
	outData.vertexPosition = translateMatrix* vertexIn;

	
	gl_Position  = camera* outData.vertexPosition;
}