#ifdef GL_ES
    precision mediump float;
#endif

varying vec4 v_color;
varying vec2 v_texCoords;

uniform sampler2D u_texture;
uniform mat4 u_projTrans;

// @appas  noise function from stackoverflow.xom
float snoise(vec2 co){
    return fract(sin(dot(co.xy ,vec2(12.9898,78.233))) * 43758.5453);
}

void main() {
        vec4 color = texture2D(u_texture, v_texCoords).rgba;

        //if(color.r < 1.0f || color.g < 1.0f || color.b < 1.0f){
        //    float n = (snoise(v_texCoords*0.05)*0.5) + 0.5;
        //    color = color*vec4(n,n,n,1);
        //}

        gl_FragColor = color;
}