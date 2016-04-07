#version 150

uniform vec3 u_color;

out vec4 fragColor;

void main(void)
{	
   fragColor = vec4(u_color, 1.0);
}
