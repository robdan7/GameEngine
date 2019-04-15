#version 450 core

//#uniform Light lightSource;
#uniform Matrices;

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