#version 150

in vec2 position;
in vec2 uv;

out vec2 f_uv;

void main(void) {
    f_uv = uv;
    gl_Position = vec4(position, 0, 1);
}
