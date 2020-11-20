#version 150

uniform vec3 fog_color;
uniform float gamma;

in vec4 f_color;
in vec3 f_position;
in float f_fog;

out vec4 fragColor;

void main(void) {
    vec4 color = f_color;
    color = vec4(pow(color.r, gamma), pow(color.g, gamma), pow(color.b, gamma), color.a);
    color = color * (1 - f_fog) + vec4(fog_color, 1) * f_fog;
    fragColor = color;
}
