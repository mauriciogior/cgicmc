#version 150

in vec3 a_position;

out vec3 v_position;

void main(void)
{
   v_position = a_position;
   gl_Position = vec4(a_position, 1.0);
}
