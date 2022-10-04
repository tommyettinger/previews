#ifdef GL_ES
precision mediump float;
#endif

uniform sampler2D u_texture;
uniform float u_time;
uniform int u_phase;

varying vec4 v_color;
varying vec2 v_texCoord;

const vec3 neutralColor = vec3(.4, .4, .4);

#define PI 3.14159265358979323846

vec2 rotate2D(vec2 _st, float _angle){
    _st -= 0.5;
    _st =  mat2(cos(_angle),-sin(_angle),
    sin(_angle),cos(_angle)) * _st;
    _st += 0.5;
    return _st;
}

vec2 tile(vec2 _st, float _zoom){
    _st *= _zoom;
    return fract(_st);
}

float box(vec2 _st, vec2 _size, float _smoothEdges){
    _size = vec2(0.5)-_size*0.5;
    vec2 aa = vec2(_smoothEdges*0.5);
    vec2 uv = smoothstep(_size,_size+aa,_st);
    uv *= smoothstep(_size,_size+aa,vec2(1.0)-_st);
    return uv.x*uv.y;
}

vec2 rotateTilePattern(vec2 _st){

    //  Scale the coordinate system by 2x2
    _st *= 2.0;

    //  Give each cell an index number
    //  according to its position
    float index = 0.0;
    index += step(1., mod(_st.x,2.0));
    index += step(1., mod(_st.y,2.0))*2.0;

    //      |
    //  2   |   3
    //      |
    //--------------
    //      |
    //  0   |   1
    //      |

    // Make each cell between 0.0 - 1.0
    _st = fract(_st);

    // Rotate each cell according to the index
    if(index == 1.0){
        //  Rotate cell 1 by 90 degrees
        _st = rotate2D(_st,PI*0.5);
    } else if(index == 2.0){
        //  Rotate cell 2 by -90 degrees
        _st = rotate2D(_st,PI*-0.5);
    } else if(index == 3.0){
        //  Rotate cell 3 by 180 degrees
        _st = rotate2D(_st,PI);
    }

    return _st;
}


void main() {
    vec3 mainColor;
    float phaseChange = max(smoothstep(.1, 0., u_time), smoothstep (9.9, 10., u_time));
    float placeInBar = mod(u_time, 1.25) / 1.25;
    if (u_phase == 0){
        mainColor = vec3(.55, .1, .1);
    } else if (u_phase == 1) {
        mainColor = vec3(.1, .55, .1);
    } else if (u_phase == 2) {
        mainColor = vec3(.1, .1, .55);
    } else if (u_phase == 3) {
        mainColor = vec3(.6, .6, .6);
    }
    vec3 accentColor = mainColor * .85;

    vec3 finalColor = mainColor;
    float borderHighlight = max(smoothstep(.2, 0., placeInBar), smoothstep (.8, 1., placeInBar));
    vec3 borderColor = vec3(.1 + .2 * borderHighlight);


    if (u_phase == 0) {
        // red phase
        float squareSize = .03;
        float borderSize = .015;
        float index = mod(floor(v_texCoord.x / squareSize) + floor(v_texCoord.y / squareSize), 2.);
        vec3 gridColor = mix(mainColor, accentColor, index);

        float dx = mod(v_texCoord.x, squareSize) / squareSize;
        float dy = mod(v_texCoord.y, squareSize) / squareSize;
        float borderline = min(min(smoothstep(0., borderSize, dx), smoothstep(1., 1. -borderSize, dx)),
                               min(smoothstep(0., borderSize, dy), smoothstep(1., 1. -borderSize, dy)));
        finalColor = mix(borderColor, gridColor, borderline);
    }
    else if (u_phase == 1) {
        // Green Phase
        float tiles = 30.;
        vec2 st = tile(v_texCoord, tiles);
        st = rotate2D(st, PI*0.25);
        float boxIndex = box(st, vec2(.710, .710), 0.06);
        vec3 gridColor = mix(mainColor, accentColor, boxIndex);
        float border = min(smoothstep(.01, .1, boxIndex), smoothstep(.99, .9, boxIndex));
        finalColor = mix(gridColor, borderColor,  border);
    }
    else if (u_phase == 2) {
        // blue Phase
        float tiles = 20.;
        vec2 st = tile(v_texCoord, tiles);
        st = rotateTilePattern(st);
        float grid = smoothstep(st.x - .01, st.x + .01, st.y);
        vec3 gridColor = mix(mainColor, accentColor, grid);
        float border = min(smoothstep(.01, .1, grid), smoothstep(.99, .9, grid));
        finalColor = mix(gridColor, borderColor,  border);
    } else if (u_phase == 3) {
        // wizard
        float tiles = 20.;
        vec2 st = tile(v_texCoord, tiles);
        st = rotateTilePattern(st);
        st = rotate2D(st, PI*u_time*.1);
        float grid = smoothstep(st.x - .00, st.x + .00, st.y);
        vec3 gridColor = mix(mainColor, accentColor, grid);
        float border = min(smoothstep(.01, .1, grid), smoothstep(.99, .9, grid));
        finalColor = mix(gridColor, borderColor,  border);
    }





    finalColor = mix(finalColor, neutralColor, phaseChange);

    gl_FragColor = vec4(finalColor, 1);
}