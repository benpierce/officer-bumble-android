precision mediump float;

uniform sampler2D u_TextureUnit;
varying vec2 v_TextureCoordinates;

void main() 
{
	vec4 color = texture2D(u_TextureUnit, v_TextureCoordinates);
	gl_FragColor = color;
}