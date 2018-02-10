#version 450 core

uniform sampler2D staticShadowmap, dynamicShadowmap;
uniform sampler2D modelTexture;

in vec4 staticDepthPos, dynamicDepthPos;
in vec2 textureCoord;
in vec4 lightFactor;
in float shadowAngle;
in vec4 staticDepthNormal;

layout (std140, binding = 0) uniform Light {
	layout(offset = 0) vec4 position;
	layout(offset = 16) vec4 ambient;
	layout(offset = 32) vec4 diffuse;
} lightSource;


float rand(vec2 n)
{
  return fract(sin(dot(n.xy, vec2(12.9898, 78.233)))* 43758.5453);
}

float getLargest(float a, float b) {
	if (b > a) {
		return b;
	} else {
		return a;
	}
}

float calcShadow(vec4 inposition, sampler2D map) {
	float shadowFactor = 0.0;
	float bias = 0.0006;
	vec2 inc = 1.0 / textureSize(map, 0);
	vec3 projCoords = inposition.xyz;
	
	//float smallOpacity = max(1-pow(max(length(inposition.xyz),0),2),0);
	
	float textDepth;
	float offset = 1;
	float step = 0.2;
	float ran;
	float test;
	float i = 0;
	if (shadowAngle > 0) {
		if (texture(map, projCoords.xy).r != 1 && projCoords.x <= 1 && projCoords.x >= 0 && projCoords.y <= 1 && projCoords.y >= 0) {
			for(float x = -offset; x <= offset; x+= step) {
				for (float y = -offset; y <= offset; y += step) {
					textDepth = texture(map, projCoords.xy + vec2(x,y)*inc).r;
					shadowFactor += projCoords.z-bias > textDepth? 1 : 0.0;
					i += 1;
				}
			}
			shadowFactor /= i;
		}
	}
	return shadowFactor;
}

void main() {
	float staticShadow = calcShadow(staticDepthPos, staticShadowmap);
	float dynamicShadow = calcShadow(dynamicDepthPos, dynamicShadowmap);
	float absoluteShadow = getLargest(staticShadow, dynamicShadow);
	vec4 shadow = max(min(lightFactor-absoluteShadow, 1), lightSource.ambient );

	gl_FragColor = texture(modelTexture, textureCoord)*shadow;
	//gl_FragColor = texture(staticShadowmap, staticDepthPos.xy)*lightFactor;
	//gl_FragColor = texture(modelTexture, textureCoord)*lightFactor;
}