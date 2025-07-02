#version 330 core

in vec2 outTexCoord;
out vec4 fragColor;

uniform sampler2D texture_sampler;

void main() {
    fragColor = texture(texture_sampler, outTexCoord);
    // Fallback para teste caso a textura não carregue
    if (fragColor.a < 0.1) {
        fragColor = vec4(1.0, 0.0, 0.0, 1.0); // Vermelho para blocos visíveis
    }
}