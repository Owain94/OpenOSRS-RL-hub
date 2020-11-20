package renderer.gl;

import org.joml.Vector3d;
import renderer.renderer.BufferBuilder;

import static org.lwjgl.opengl.GL32C.*;

public class GlProgram implements AutoCloseable {
    private final int program;
    private final int vs;
    private final int fs;
    private boolean closed = false;
    private final int positionAttributeLocation;
    private final int colorAttributeLocation;
    private final int priorityAttributeLocation;
    private final int transformUniformLocation;
    private final int projectionUniformLocation;
    private final int viewDistanceUniformLocation;
    private final int fogColorUniformLocation;
    private final int cameraPositionUniformLocation;
    private final int gammaUniformLocation;

    public GlProgram(String vertexShader, String fragmentShader) {
        program = glCreateProgram();
        glAttachShader(program, vs = createShader(vertexShader, GL_VERTEX_SHADER));
        glAttachShader(program, fs = createShader(fragmentShader, GL_FRAGMENT_SHADER));

        glLinkProgram(program);
        if (glGetProgrami(program, GL_LINK_STATUS) == GL_FALSE) {
            throw new IllegalStateException("program link failed");
        }

        transformUniformLocation = getUniformLocation("transform");
        projectionUniformLocation = getUniformLocation("projection");
        viewDistanceUniformLocation = getUniformLocation("view_distance");
        fogColorUniformLocation = getUniformLocation("fog_color");
        cameraPositionUniformLocation = getUniformLocation("camera_position");
        gammaUniformLocation = getUniformLocation("gamma");
        positionAttributeLocation = getAttributeLocation("position");
        colorAttributeLocation = getAttributeLocation("color");
        priorityAttributeLocation = getAttributeLocation("priority");
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

    public int getAttributeLocation(String name) {
        int location = glGetAttribLocation(program, name);

        if (location == -1) {
            throw new IllegalArgumentException("no attribute '" + name + "'");
        }

        return location;
    }

    public int getUniformLocation(String name) {
        int location = glGetUniformLocation(program, name);

        if (location == -1) {
            throw new IllegalArgumentException("no attribute '" + name + "'");
        }

        return location;
    }

    public void render(VertexBuffer vertexBuffer) {
        glBindBuffer(GL_ARRAY_BUFFER, vertexBuffer.buffer);
        vertexBuffer.bind();

        glVertexAttribPointer(positionAttributeLocation, 3, GL_FLOAT, false, BufferBuilder.VERTEX_SIZE, 0);
        glEnableVertexAttribArray(positionAttributeLocation);

        glVertexAttribIPointer(colorAttributeLocation, 1, GL_INT, BufferBuilder.VERTEX_SIZE, 24);
        glEnableVertexAttribArray(colorAttributeLocation);

        glVertexAttribPointer(priorityAttributeLocation, 1, GL_FLOAT, false, BufferBuilder.VERTEX_SIZE, 28);
        glEnableVertexAttribArray(priorityAttributeLocation);

        vertexBuffer.draw();
    }

    public void enable(float[] transform, float[] projection, float[] light, float viewDistance, Vector3d fogColor, float[] position, float gamma) {
        glUseProgram(program);
        glUniformMatrix4fv(transformUniformLocation, false, transform);
        glUniformMatrix4fv(projectionUniformLocation, false, projection);
        glUniform1f(viewDistanceUniformLocation, viewDistance);
        glUniform3f(fogColorUniformLocation, (float) fogColor.x, (float) fogColor.y, (float) fogColor.z);
        glUniform3fv(cameraPositionUniformLocation, position);
        glUniform1f(gammaUniformLocation, gamma);
    }

    public void disable() {
        glUseProgram(0);
    }

    @Override
    public void close() {
        if (!closed) {
            closed = true;
            glDeleteProgram(program);
            glDeleteShader(vs);
            glDeleteShader(fs);
        }
    }
}
