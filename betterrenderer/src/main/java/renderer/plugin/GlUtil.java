package renderer.plugin;


import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLCapabilities;
import org.lwjgl.opengl.GLDebugMessageCallback;
import org.lwjgl.opengl.KHRDebug;

import static org.lwjgl.opengl.GL43C.*;

public class GlUtil {
    public static void enableThrowOnError() {
        GLCapabilities caps = GL.getCapabilities();

        if (caps.GL_KHR_debug) {
            GLDebugMessageCallback proc = GLDebugMessageCallback.create((source, type, id, severity, length, message, userParam) -> {
                if (severity == GL_DEBUG_SEVERITY_HIGH) {
                    throw new RuntimeException(String.format("0x%X", id) + ": " + GLDebugMessageCallback.getMessage(length, message));
                }
            });
            KHRDebug.glDebugMessageCallback(proc, 0);

            if (caps.OpenGL30 && (glGetInteger(GL_CONTEXT_FLAGS) & GL_CONTEXT_FLAG_DEBUG_BIT) == 0) {
                glEnable(GL_DEBUG_OUTPUT);
            }
        }
    }

    public static void checkError() {
        int err = glGetError();

        if (err != 0) {
            throw new IllegalStateException("0x" + Integer.toHexString(err));
        }
    }
}
