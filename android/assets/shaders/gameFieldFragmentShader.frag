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

float edgeDetect(){
    mat3 scharrX;
    scharrX[0] = vec3( 3f,  0f, -3f);
    scharrX[1] = vec3( 10f,  0f,-10f);
    scharrX[2] = vec3( 3f, 0f , -3f);

    mat3 scharrY;
    scharrY[0] = vec3( 3, 10, 3);
    scharrY[1] = vec3( 0,  0, 0);
    scharrY[2] = vec3( -3,-10,-3);


    float stretch = 0.001f;
    float resX = 0.0f;
    float resY = 0.0f;

    for(int y=0;y<3;y++){
        for(int x=0;x<3;x++){
            vec2 offset = vec2(stretch*(float(x)-1.0f),stretch*(float(y)-1.0f));
            vec4 col = texture2D(u_texture, v_texCoords+offset).rgba;
            float gray = (col.r+col.g+col.b)/3.0f;
            resX+=scharrX[x][y]*gray;
            resY+=scharrY[x][y]*gray;
        }
    }

    return sqrt(resX*resX+resY*resY);
}

void main() {
        vec4 color = texture2D(u_texture, v_texCoords).rgba;

        float edge = edgeDetect();
        if(edge > 0.1f)
            gl_FragColor = vec4(0,0,0,1.0);
        else
            gl_FragColor = color;
}