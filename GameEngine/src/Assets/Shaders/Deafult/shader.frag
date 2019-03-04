#version 450 core

#uniform Light lightSource;
#uniform Matrices;

uniform sampler2D staticShadowmap, dynamicShadowmap;
uniform sampler2D modelTexture;

in vec4 staticDepthPos, dynamicDepthPos;
in vec4 n, lNormal, vertexPosition;
in vec2 textureCoord;
in vec4 diffuseLight;

layout(location = 0) out vec4 outColor;
layout(location = 1) out vec4 outNormal;
layout(location = 2) out vec4 worldPosition;

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
	float bias = 0.0003;
	vec2 inc = 1.0 / textureSize(map, 0);
	vec3 projCoords = inposition.xyz;
	
	//float smallOpacity = max(1-pow(max(length(inposition.xyz),0),2),0);
	
	float textDepth;
	float offset = 2;
	float step = 0.5;
	float len = length(vec2(offset,offset));
	float i = 0;
	if (length(diffuseLight) > 0) { // No diffuse light = already in shadow.
		if (projCoords.z < 1 && projCoords.x <= 1 && projCoords.x >= 0 && projCoords.y <= 1 && projCoords.y >= 0) {
			for(float x = -offset; x <= offset; x+= step) {
				for (float y = -offset; y <= offset; y += step) {
					textDepth = texture(map, projCoords.xy + vec2(x,y)*inc).r;
					shadowFactor += projCoords.z-bias > textDepth? 1/*-length(projCoords.xy + vec2(x,y)*inc)/len*/ : 0.0;
					i += 1;
				}
			}
			shadowFactor /= i;
		}
	}
	return shadowFactor;
}

void main() {
	/*float staticShadow = calcShadow(staticDepthPos, staticShadowmap);
	float dynamicShadow = calcShadow(dynamicDepthPos, dynamicShadowmap);
	float absoluteShadow = getLargest(dynamicShadow, staticShadow);
	absoluteShadow = getLargest(dynamicShadow, 0);
	*/
	/*
	vec4 mirrorLight = normalize(viewMatrix*(lNormal- 2*dot(lNormal, n)*n));
	vec4 eye = normalize(viewMatrix*vertexPosition);
	float specLight = 0;

	specLight = max(dot(eye.xyz,mirrorLight.xyz),0)*max(dot(n,lNormal),0);
	
	specLight = pow(specLight, 6);
	
	vec3 shadow = max(min(diffuseLight.rgb-absoluteShadow+specLight*normalize(lightSource.diffuse.rgb), 1+specLight), lightSource.ambient.rgb);
	*/	

	outNormal = vec4(n.xyz*0.5+0.5,1);
	outColor = texture(modelTexture, textureCoord);
	//outColor = vec4(1,0,0,1);
	worldPosition = vertexPosition;
	
	//outColor = texture(modelTexture, textureCoord)*vec4(shadow.xyz,1);
	//vec4 test = camera*vertexPosition;
	//worldPosition = vec4((test.xy/test.w)*0.5+0.5,gl_FragCoord.z/(gl_FragCoord.w*100),1);
	
	gl_FragDepth = gl_FragCoord.z;
}