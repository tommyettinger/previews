#ifdef GL_ES
precision mediump float;
#endif

uniform sampler2D u_texture;
uniform float u_time;
uniform float u_shield;

varying vec4 v_color;
varying vec2 v_texCoord;


void main() {
    vec2 noiseCoord1 = v_texCoord;
    vec2 noiseCoord2 = v_texCoord * 2.;

//    float sin_factor1 = sin(u_time * .432);
//    float cos_factor1 = cos(u_time * .432);
//
//    float sin_factor2 = sin(-u_time * .252);
//    float cos_factor2 = cos(-u_time * .252);
//
//    noiseCoord1 = (noiseCoord1 - .5) * mat2(cos_factor1, sin_factor1, -sin_factor1, cos_factor1);
//    noiseCoord1 += 0.5 + vec2(u_time*.1, 0);
//
//    noiseCoord2 = (noiseCoord2 - .5) * mat2(cos_factor2, sin_factor2, -sin_factor2, cos_factor2);
//    noiseCoord2 += 0.5 + vec2(u_time*.1, 0);

    vec4 noise1 = texture2D(u_texture, vec2(v_texCoord.x + u_time * .05, v_texCoord.y + u_time *.05));
    vec4 noise2 = texture2D(u_texture, vec2(v_texCoord.x + u_time * -.03, v_texCoord.y + u_time * -.03));
    vec4 noise3 = texture2D(u_texture, vec2(v_texCoord.x + u_time * -.04, v_texCoord.y + u_time * .04));
    vec4 noise4 = texture2D(u_texture, vec2(v_texCoord.x + u_time * .2, v_texCoord.y + u_time * -.2));

    float dist = distance(vec2(0.5), v_texCoord) * 2.0;
    float alpha = smoothstep(.2, .75, dist);
//    if (dist > 1.) alpha = 0.;

    float noise = noise1.b * .25 + noise1.r * .25 + noise3.b * .25 + noise4.g * .25;

//    alpha *= noise1.b * 1.5;
//    alpha *= noise2.r * 1.5;
//    alpha *= noise2.b * 1.5;
    vec3 color1 = vec3(.4, .4, .8);
    vec3 color2 = vec3(.9, .3, .6);
    vec3 color3 = vec3(.5, .8, .3);
    vec3 color4 = vec3(.8, .8, .8);

    vec3 color = mix(color1, color2, smoothstep(.5, .55, noise));
    color = mix(color, color3 , smoothstep(.6, .65, noise));
    color = mix(color, color4 , smoothstep(.68, .7, noise));

    alpha *= noise;
    vec4 shieldColor = vec4(color, alpha);

    shieldColor *= u_shield;

    vec4 finalColor = mix(shieldColor, vec4(vec3(.4 + (.4*u_shield)), .9), smoothstep(.97, .99, dist));

    finalColor = mix(finalColor, vec4(0), smoothstep(.99, 1., dist));

    gl_FragColor = finalColor;
}