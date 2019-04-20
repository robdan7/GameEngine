#version 450 core

#uniform Light lightSource;
#uniform Matrices;

in vec2 texCoord;
in vec2 frag;
uniform sampler2D normalBuffer, colorBuffer, positionBuffer;
uniform sampler2D inDepth;
uniform sampler2D staticShadowmap, dynamicShadowmap;

struct fragmentInfo {
	vec4 color;
	vec4 normal;
	vec4 wPosition;
	vec4 dShadowPos;
	vec4 sShadowPos;
} fragment;

struct lightInfo {
	vec4 normal;
	vec4 diffuse;
	vec4 ambient;
	vec4 specular;
} sun;

//out vec4 outColor;

// include methods for calculating light.
#include lightUtils;

vec4 colorMix(vec4 c1, vec4 c2, float w) {
	vec4 col = c1 * (1-w) + c2* w;
	return col;
}

float getLargest(float a, float b) {
	if (b > a) {
		return b;
	} else {
		return a;
	}
}

float calcShadow(vec4 inposition, sampler2D map, vec4 diffuse) {
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
	if (length(diffuse.xyz) > 0) { // No diffuse light = already in shadow.
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

float depthTest(vec2 position, sampler2D map) {
	float depth = texture(map, position).b;
	vec2 inc = 1.0 / textureSize(map, 0);
	float bias = 0.01;
	float offset = 6;
	float step = 1;
	
	float ssao = 0;
	int i = 0;
	for (float y =-offset; y <= offset; y += step) {
		for (float x = -offset; x <= offset; x += step) {
			float textDepth = texture(map, position + vec2(x,y)*inc).b;
			ssao += textDepth - depth > bias ? 1 : 0;
			i += 1;
		}
	}
	ssao /= i;
	
	return 1-ssao;
}

float SSAO2(vec2 position, sampler2D map) {
	vec4 norm = normalize(viewMatrix*vec4(texture(map, position).rgb*2-1,0));
	vec2 inc = 1.0 / textureSize(map, 0);
	float bias = 0.001;
	float offset = 10;
	float step = 2;
	
	float ssao = 0;
	int i = 0;
	for (float y =-offset; y <= offset; y += step) {
		for (float x = -offset; x <= offset; x += step) {
			vec4 n2 = normalize(viewMatrix*vec4(texture(map, position + vec2(x,y)*inc).rgb*2-1,0));
			ssao += dot(n2,norm) > 0.99 ? 1 : 0;
			i += 1;
		}
	}
	ssao /= i;
	
	return ssao;
}

float colorTest(vec2 position, sampler2D map) {
	vec4 color = normalize(texture(map, position));
	vec2 inc = 1.0 / textureSize(map, 0);
	float bias = 0.001;
	float offset = 10;
	float step = 2;
	
	float result = 0;
	int i = 0;
	for (float y =-offset; y <= offset; y += step) {
		for (float x = -offset; x <= offset; x += step) {
			vec4 color2 =normalize(texture(map, position + vec2(x,y)*inc));
			result += dot(color,color2) > 0.99 ? 1 : 0;
			i += 1;
		}
	}
	result /= i;
	
	return result;
}

vec4 composeLight(vec4 sPos, vec4 dPos, vec4 vPos, vec4 vNormal, vec4 lNormal, vec4 diffuse, vec4 ambient, vec4 specular, float shine)  {
	float staticShadow = calcShadow(sPos, staticShadowmap, diffuse);
	float dynamicShadow = calcShadow(dPos, dynamicShadowmap, diffuse);
	//vec4 eye = normalize(viewMatrix*vPos);
	vec4 eye = normalize(vPos-(inverse(viewMatrix)*vec4(0,0,0,1)));
	
	float absoluteShadow = getLargest(dynamicShadow, staticShadow);
	float spec = calcSpecularLight(lNormal, vNormal ,eye, shine);
	//float test = remove(lNormal, vNormal ,eye, 100);
	return max(min(ambient + diffuse, 1) -absoluteShadow,ambient)+vec4(spec,spec,spec,1)*specular*(1-absoluteShadow);
	//return vec4(calcSpecularLight(lNormal, vNormal ,eye, shine),0,0,1);
}

vec4 testisen(vec4 sPos, vec4 dPos, vec4 ambient, vec4 diffuse) {
	float staticShadow = calcShadow(sPos, staticShadowmap, diffuse);
	float dynamicShadow = calcShadow(dPos, dynamicShadowmap, diffuse);
	//vec4 eye = normalize(viewMatrix*vPos);
	
	float absoluteShadow = 1-getLargest(dynamicShadow, staticShadow);

	return vec4(absoluteShadow,absoluteShadow,absoluteShadow,1);
	//return 1;
}



void main() {
	fragment.color = texture(colorBuffer, texCoord);
	fragment.normal = vec4(normalize(texture(normalBuffer, texCoord).rgb*2-1).xyz,0);
	//normal = vec4((viewMatrix*vec4(normal.xyz,0)).xyz,1);
	fragment.wPosition = texture(positionBuffer, texCoord);
	fragment.dShadowPos = dynamicOrthoMatrix*fragment.wPosition*0.5+0.5;
	fragment.sShadowPos = staticOrthoMatrix*fragment.wPosition*0.5+0.5;
	
	sun.normal = normalize(lightSource.position);
	sun.diffuse = calcDiffuseLight(sun.normal, fragment.normal);
	sun.ambient = lightSource.ambient;
	sun.specular = lightSource.specular;
	

	gl_FragColor = fragment.color*composeLight(fragment.sShadowPos, fragment.dShadowPos, fragment.wPosition, fragment.normal, sun.normal,sun.diffuse, sun.ambient, sun.specular, 100);
	//gl_FragColor = fragment.color*testisen(fragment.sShadowPos, fragment.dShadowPos, sun.ambient, sun.diffuse);
	//gl_FragColor = fragment.color*testisen(fragment.sShadowPos, fragment.dShadowPos, sun.ambient, sun.diffuse);
	//gl_FragColor = vec4(fragment.normal.xyz,1);
	gl_FragDepth = texture(inDepth, texCoord).r;
	
}