package renderer.plugin;

import renderer.gl.VertexBuffer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static org.lwjgl.opengl.GL32C.*;

public class InterfaceRenderer {
    public static final int VERTEX_SIZE = 16;
    private final int program;
    private final VertexBuffer interfaceBuffer;

    public InterfaceRenderer(String vertexShader, String fragmentShader) {
        program = glCreateProgram();
        glAttachShader(program, createShader(vertexShader, GL_VERTEX_SHADER));
        glAttachShader(program, createShader(fragmentShader, GL_FRAGMENT_SHADER));
        glLinkProgram(program);

        interfaceBuffer = new VertexBuffer();

        ByteBuffer data = ByteBuffer.allocateDirect(6 * VERTEX_SIZE).order(ByteOrder.nativeOrder());
        vertex(data, -1, 1, 0, 0); // a
        vertex(data, -1, -1, 0, 1); // b
        vertex(data, 1, -1, 1, 1); // c

        vertex(data, -1, 1, 0, 0); // a
        vertex(data, 1, 1, 1, 0); // d
        vertex(data, 1, -1, 1, 1); // c

        data.position(0);

        interfaceBuffer.bind();
        interfaceBuffer.set(6, data);

        int positionAttributeLocation = glGetAttribLocation(program, "position");
        int uvAttributeLocation = glGetAttribLocation(program, "uv");
        glEnableVertexAttribArray(positionAttributeLocation);
        glVertexAttribPointer(positionAttributeLocation, 2, GL_FLOAT, false, VERTEX_SIZE, 0);
        glVertexAttribPointer(uvAttributeLocation, 2, GL_FLOAT, false, VERTEX_SIZE, 8);
        glEnableVertexAttribArray(uvAttributeLocation);
    }

    public void draw() {
        glUseProgram(program);
        interfaceBuffer.bind();
        interfaceBuffer.draw();
        glUseProgram(0);
    }

    public void vertex(ByteBuffer buffer, int x, int y, int u, int v) {
        buffer.putFloat(x);
        buffer.putFloat(y);
        buffer.putFloat(u);
        buffer.putFloat(v);
    }

    private static int createShader(String source, int type) {
        int id = glCreateShader(type);
        glShaderSource(id, source);
        glCompileShader(id);

        if (glGetShaderi(id, GL_COMPILE_STATUS) == GL_FALSE) {
            System.err.println(glGetShaderInfoLog(id));
            throw new IllegalStateException("shader compilation failed");
        }

        return id;
    }
}
