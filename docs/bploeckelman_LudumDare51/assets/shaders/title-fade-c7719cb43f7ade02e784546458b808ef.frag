#ifdef GL_ES
precision mediump float;
#endif

uniform sampler2D u_texture;

// This is mad.....
varying vec4 v_color; // Not really color but packed as (U1, U2, V1, V2)
varying vec2 v_texCoord;

const float margin = .03;

void main() {
    vec4 color = texture2D(u_texture, v_texCoord);

    float alphaU = min(
        smoothstep(v_color.r - margin, v_color.r + margin, v_texCoord.x),
        smoothstep(v_color.g + margin, v_color.g - margin, v_texCoord.x)
    );
    float alphaV = min(
        smoothstep(v_color.b - margin, v_color.b + margin, v_texCoord.y),
        smoothstep(v_color.a + margin, v_color.a - margin, v_texCoord.y)
    );

    color.a *= min(alphaU, alphaV);

    gl_FragColor = color;
}