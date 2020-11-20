package renderer.gl;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL32C.*;

public class VertexBuffer implements AutoCloseable {
    public final int buffer;
    public final int vertexArray;
    private int vertexCount;
    private boolean closed = false;

    public VertexBuffer() {
        buffer = glGenBuffers();
        vertexArray = glGenVertexArrays();
    }

    public void set(int vertexCount, ByteBuffer data) {
        if (closed) {
            throw new IllegalStateException("closed");
        }

        this.vertexCount = vertexCount;
        glBindBuffer(GL_ARRAY_BUFFER, buffer);
        glBufferData(GL_ARRAY_BUFFER, data, GL_STATIC_DRAW);
    }

    public void bind() {
        glBindVertexArray(vertexArray);
        glBindBuffer(GL_ARRAY_BUFFER, buffer);
    }

    @Override
    public void close() {
        if (!closed) {
            closed = true;
            glDeleteBuffers(buffer);
            glDeleteVertexArrays(vertexArray);
        }
    }

    public void draw() {
        glDrawArrays(GL_TRIANGLES, 0, vertexCount);
    }
}
