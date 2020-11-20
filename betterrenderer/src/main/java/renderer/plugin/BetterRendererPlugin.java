package renderer.plugin;

import com.google.common.util.concurrent.UncheckedExecutionException;
import com.google.inject.Provides;
import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.hooks.DrawCallbacks;
import net.runelite.client.RuneLite;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginInstantiationException;
import net.runelite.client.plugins.PluginManager;
import net.runelite.client.plugins.PluginType;
import net.runelite.client.ui.DrawManager;
import net.runelite.client.ui.overlay.OverlayManager;
import org.joml.Vector3d;
import org.lwjgl.opengl.awt.*;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.Platform;
import org.lwjgl.system.windows.RECT;
import org.lwjgl.system.windows.User32;
import org.pf4j.Extension;
import renderer.cache.CacheSystem;
import renderer.model.TextureDefinition;
import renderer.renderer.BufferBuilder;
import renderer.renderer.Renderer;
import renderer.renderer.WorldRenderer;
import renderer.util.Colors;
import renderer.util.Util;

import javax.inject.Inject;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL.createCapabilities;
import static org.lwjgl.opengl.GL32C.*;

@Extension
@PluginDescriptor(
	name = "Better Renderer",
	description = "Optimized renderer providing nearly infinite view distance and minor graphical improvements",
	type = PluginType.UTILITY,
	enabledByDefault = false
)
public class BetterRendererPlugin extends Plugin implements DrawCallbacks {
    private static final String XTEA_LOCATION = "https://gist.githubusercontent.com/Runemoro/d68a388aeb35ad432adf8af027eae832/raw/xtea.json";
    @Inject public Client client;
    @Inject public BetterRendererConfig config;
    @Inject private DrawManager drawManager;
    @Inject private ClientThread clientThread;
    @Inject private PluginManager pluginManager;
    @Inject private OverlayManager overlayManager;
    @Inject private LoadingCacheOverlay loadingCacheOverlay;

    private PlatformGLCanvas platformCanvas;
    public Renderer renderer;
    private WorldRenderer dynamicBuffer;
    private boolean hasFrame = false;
    private final FramerateTracker framerateTracker = new FramerateTracker(10);

    private int interfaceTexture = -1;
    private int lastCanvasWidth = -1;
    private int lastCanvasHeight = -1;
    private int lastWidth = -1;
    private int lastHeight = -1;
    private int lastSamples = -1;
    private int framebuffer = -1;
    private int colorRenderbuffer = -1;
    private int framebufferTexture = -1;
    private int depthRenderbuffer = -1;
    private int width = -1;
    private int height = -1;

    private boolean executorInitialized = false;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private Future<?> frameFuture;
    private Thread initThread;
    private long context;
    private InterfaceRenderer interfaceRenderer;

    @Override
    protected void startUp() {
        // Stop the GPU plugin before starting up
        for (Plugin plugin : pluginManager.getPlugins()) {
            if (plugin.getName().equals("GPU") && pluginManager.isPluginEnabled(plugin)) {
                try {
                    System.out.println("Stopping GPU plugin");
                    pluginManager.setPluginEnabled(plugin, false);
                    pluginManager.stopPlugin(plugin);
                } catch (PluginInstantiationException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        // Remove off-heap memory limit (default is equal to Xmx)
        try {
            ByteBuffer.allocateDirect(0);
            Class<?> bitsClass = Class.forName("java.nio.Bits");
            Field maxMemoryField;

            try {
                maxMemoryField = bitsClass.getDeclaredField("MAX_MEMORY");
            } catch (NoSuchFieldException e) {
                maxMemoryField = bitsClass.getDeclaredField("maxMemory"); // Java 8
            }

            maxMemoryField.setAccessible(true);
            maxMemoryField.set(null, Long.MAX_VALUE);
        } catch (ReflectiveOperationException e) {
            throw new AssertionError(e);
        }

        // Download the cache

        initThread = new Thread(() -> {
            overlayManager.add(loadingCacheOverlay);

            try {
                Path xteaPath = RuneLite.RUNELITE_DIR.toPath().resolve("better-renderer/xtea.json");
                Files.createDirectories(xteaPath.getParent());
                Files.write(xteaPath, Util.readAllBytes(new URL(XTEA_LOCATION).openStream()));
                CacheSystem.CACHE.init(client.getWorld(), client.getRevision());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            overlayManager.remove(loadingCacheOverlay);

            clientThread.invoke(this::init);
        });

        initThread.start();
    }

    private void init() {
        client.setDrawCallbacks(this);
        client.setGpu(true);
        client.resizeCanvas();

        glfwInit();
        glfwSetErrorCallback((id, description) -> {
            throw new RuntimeException(id + ": " + MemoryUtil.memUTF8(description));
        });

        switch (Platform.get()) {
            case WINDOWS:
                platformCanvas = new PlatformWin32GLCanvas();
                break;
            case LINUX:
                platformCanvas = new PlatformLinuxGLCanvas();
                break;
            case MACOSX:
                platformCanvas = new PlatformMacOSXGLCanvas();
                break;
            default:
                throw new AssertionError();
        }

        client.getCanvas().removeNotify();
        client.getCanvas().addNotify();

        try {
            GLData data = new GLData();
            data.majorVersion = 3;
            data.minorVersion = 2;
            data.profile = GLData.Profile.CORE;
            context = platformCanvas.create(client.getCanvas(), data, data);
        } catch (AWTException e) {
            throw new RuntimeException(e);
        }

        attachCanvas();
        createCapabilities();
        GlUtil.enableThrowOnError();

        createInterfaceTexture();
        renderer = new Renderer();
        dynamicBuffer = new WorldRenderer(renderer.world);
        renderer.init();

        try {
            interfaceRenderer = new InterfaceRenderer(
                    new String(Util.readAllBytes(Renderer.class.getResourceAsStream("/shaders/ui-vertex-shader.glsl"))),
                    new String(Util.readAllBytes(Renderer.class.getResourceAsStream("/shaders/ui-fragment-shader.glsl")))
            );
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        detachCanvas();
    }

    @Override
    protected void shutDown() {
        clientThread.invoke(() -> {
            try {
                overlayManager.remove(loadingCacheOverlay);
                initThread.stop();
                renderer.chunkScheduler.stopAllThreads();
            } catch (Throwable ignored) {

            }

            client.setDrawCallbacks(null);
            client.setGpu(false);

            try {
                glfwTerminate();
                platformCanvas.dispose();
            } catch (Throwable ignored) {

            }

            platformCanvas = null;
            hasFrame = false;
            interfaceTexture = -1;
            lastCanvasWidth = -1;
            lastCanvasHeight = -1;
            lastWidth = -1;
            lastHeight = -1;
            lastSamples = -1;
            framebuffer = -1;
            colorRenderbuffer = -1;
            framebufferTexture = -1;
            depthRenderbuffer = -1;
            width = -1;
            height = -1;
            executorInitialized = false;
            frameFuture = null;
            renderer = null;
            dynamicBuffer = null;

            client.resizeCanvas();
        });
    }

    @Provides
    public BetterRendererConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(BetterRendererConfig.class);
    }

    @Override
    public void draw(Entity renderable, int orientation, int pitchSin, int pitchCos, int yawSin, int yawCos, int x, int y, int z, long hash) {
        try {
            Model model = renderable instanceof Model ? (Model) renderable : renderable.getModel();

            if (model == null) {
                return;
            }

            model.calculateBoundsCylinder();
            model.calculateExtreme(orientation);
            client.checkClickbox(model, orientation, pitchSin, pitchCos, yawSin, yawCos, x, y, z, hash);

            if (!(renderable instanceof Model)) {
                Vector3d pos = new Vector3d(
                        client.getBaseX() + (x + client.getCameraX2()) / 128.,
                        client.getBaseY() + (z + client.getCameraZ2()) / 128.,
                        -(y + client.getCameraY2()) / 128.
                );

                for (int faceIndex = 0; faceIndex < model.getTrianglesCount(); faceIndex++) {
                    int alpha = model.getTriangleTransparencies() == null ? 0xff : 0xff - model.getTriangleTransparencies()[faceIndex];
                    BufferBuilder buffer = alpha == 0xff ? dynamicBuffer.opaqueBuffer : dynamicBuffer.translucentBuffer;
                    byte priority = model.getFaceRenderPriorities() == null ? 0 : model.getFaceRenderPriorities()[faceIndex];

                    int i = model.getTrianglesX()[faceIndex];
                    int j = model.getTrianglesY()[faceIndex];
                    int k = model.getTrianglesZ()[faceIndex];

                    Vector3d a = new Vector3d(model.getVerticesX()[i] * WorldRenderer.SCALE, model.getVerticesZ()[i] * WorldRenderer.SCALE, -model.getVerticesY()[i] * WorldRenderer.SCALE).rotateZ(-(Math.PI * orientation / 1024.)).add(pos);
                    Vector3d b = new Vector3d(model.getVerticesX()[j] * WorldRenderer.SCALE, model.getVerticesZ()[j] * WorldRenderer.SCALE, -model.getVerticesY()[j] * WorldRenderer.SCALE).rotateZ(-(Math.PI * orientation / 1024.)).add(pos);
                    Vector3d c = new Vector3d(model.getVerticesX()[k] * WorldRenderer.SCALE, model.getVerticesZ()[k] * WorldRenderer.SCALE, -model.getVerticesY()[k] * WorldRenderer.SCALE).rotateZ(-(Math.PI * orientation / 1024.)).add(pos);

                    int color1 = model.getFaceColors1()[faceIndex];
                    int color2 = model.getFaceColors2()[faceIndex];
                    int color3 = model.getFaceColors3()[faceIndex];


                    if (color3 == -1) {
                        color2 = color3 = color1;
                    } else if (color3 == -2) {
                        continue; // hidden
                    }

                    color1 = Colors.hsl(color1) & 0xffffff;
                    color2 = Colors.hsl(color2) & 0xffffff;
                    color3 = Colors.hsl(color3) & 0xffffff;

                    short textureId = model.getFaceTextures() == null ? -1 : model.getFaceTextures()[faceIndex];

                    if (textureId != -1) {
                        if (true) continue;
                        TextureDefinition texture = CacheSystem.getTextureDefinition(textureId);
                        if (texture != null) {
                            color1 = Colors.darken(texture.averageColor, (color1 & 0xff) / 255.);
                            color2 = Colors.darken(texture.averageColor, (color2 & 0xff) / 255.);
                            color3 = Colors.darken(texture.averageColor, (color3 & 0xff) / 255.);
                        }
                    }

                    buffer.vertex(a, alpha << 24 | color1, 40 + priority);
                    buffer.vertex(b, alpha << 24 | color2, 40 + priority);
                    buffer.vertex(c, alpha << 24 | color3, 40 + priority);
                }
            }
        } catch (Throwable t) {
            handleCrash(t);
        }
    }

	@Override
	public void drawScenePaint(int orientation, int pitchSin, int pitchCos, int yawSin, int yawCos, int x, int y, int z, TilePaint paint, int tileZ, int tileX, int tileY, int zoom, int centerX, int centerY)
	{

	}

    @Override
    public void drawSceneModel(int orientation, int pitchSin, int pitchCos, int yawSin, int yawCos, int x, int y, int z, TileModel model, int tileZ, int tileX, int tileY, int zoom, int centerX, int centerY) {

    }

    @Override
    public void draw() {
        try {
            if (hasFrame) {
                finishFrame();
            }

            startFrame();
            hasFrame = true;
        } catch (Throwable t) {
            handleCrash(t);
        }
    }

    private void finishFrame() {
        if (config.offThreadRendering() && Platform.get() == Platform.WINDOWS) {
            if (frameFuture != null) {
                try {
                    frameFuture.get();
                } catch (InterruptedException | ExecutionException e) {
                    throw new UncheckedExecutionException(e);
                }

                attachCanvas();
            } else {
                attachCanvas();
                renderWorld(width, height, dynamicBuffer);
            }
        } else {
            attachCanvas();
            renderWorld(width, height, dynamicBuffer);
        }

        glBindFramebuffer(GL_READ_FRAMEBUFFER, framebuffer);
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, 0);
        glBlitFramebuffer(0, 0, width, height, 0, 0, width, height, GL_COLOR_BUFFER_BIT, GL_NEAREST);

        glBindFramebuffer(GL_READ_FRAMEBUFFER, 0);

        drawInterface();
        updateWindow();
        drawManager.processDrawComplete(this::screenshot);
        framerateTracker.nextFrame();

        int err = glGetError();

        if (err != 0) {
            throw new IllegalStateException("0x" + Integer.toHexString(err));
        }

        detachCanvas();
    }

    private void startFrame() { // todo
        width = client.getCanvas().getWidth();
        height = client.getCanvas().getHeight();

        if (platformCanvas instanceof PlatformWin32GLCanvas) { // dpi-aware height
            RECT rect = RECT.calloc();
            User32.GetWindowRect(((PlatformWin32GLCanvas) platformCanvas).hwnd, rect);
            width = rect.right() - rect.left();
            height = rect.bottom() - rect.top();
            rect.free();
        }

        // Create or update the FBO
        attachCanvas();
        updateFramebuffer();
        detachCanvas();

        if (client.getLocalPlayer() != null && Math.abs(client.getBaseX() + client.getCameraX() / 128.) > 1) { // ???
            // Update renderer settings
            double cameraX = client.getBaseX() + client.getCameraX() / 128.;
            double cameraY = client.getBaseY() + client.getCameraY() / 128.;
            double cameraZ = -client.getCameraZ() / 128.;
            double cameraPitch = -Math.PI * client.getCameraPitch() / 1024.;
            double cameraYaw = -Math.PI * client.getCameraYaw() / 1024.;
            double zoom = 2. * client.getScale() / client.getCanvasHeight();

            Vector3d cameraPosition = new Vector3d(cameraX, cameraY, cameraZ);
            renderer.rotation.set(0, 0, 0, 1);
            renderer.rotation.rotateX(-Math.PI / 2);
            renderer.rotation.rotateX(-cameraPitch);
            renderer.rotation.rotateZ(cameraYaw);

            if (config.improvedZoom()) {
                renderer.scale = 1;
                double amount = (1 - 1 / zoom) * getActorPosition(client.getLocalPlayer()).distance(cameraPosition);
                cameraPosition.add(renderer.rotation.transformInverse(new Vector3d(0, 0, -amount)));
            } else {
                renderer.scale = zoom;
            }

            renderer.position.set(cameraPosition);
            renderer.viewDistance = config.viewDistance();
            renderer.gamma = 1 - 0.1 * client.getVarpValue(166) - config.gammaOffset() / 100.;
            renderer.fogColor = Colors.unpack(client.getSkyboxColor());

            // Roofs
            WorldPoint p = client.getLocalPlayer().getWorldLocation();
            renderer.world.updateRoofs(p.getX(), p.getY(), p.getPlane(), config.roofRemovalRadius());
            renderer.chunkScheduler.setRoofsRemoved(renderer.world.roofsRemoved, renderer.world.roofRemovalPlane);
        }

        // Submit frame render task to the executor
        WorldRenderer localDynamic = dynamicBuffer;

        dynamicBuffer = new WorldRenderer(renderer.world);

        if (config.offThreadRendering() && Platform.get() == Platform.WINDOWS) {
            frameFuture = executor.submit(() -> {
                attachCanvas();
                if (!executorInitialized) {
                    createCapabilities();
                    executorInitialized = true;
                }

                renderWorld(width, height, localDynamic);
                detachCanvas();
            });
        }
    }

    private void renderWorld(int width, int height, WorldRenderer dynamic) {
        if (client.getGameState().getState() <= 20) {
            return;
        }

        glBindFramebuffer(GL_FRAMEBUFFER, framebuffer);
        renderer.draw(width, height, dynamic);
    }

    private Vector3d getActorPosition(Actor player) {
        WorldPoint pos = WorldPoint.fromLocal(client, player.getLocalLocation());
        Tile tile = client.getScene().getTiles()[pos.getPlane()][pos.getX() - client.getBaseX()][pos.getY() - client.getBaseY()];

        if (tile == null) { // ??
            return new Vector3d(0, 0, 0);
        }

        return convert((tile.getBridge() == null ? tile.getPlane() : tile.getPlane() + 1), player.getLocalLocation());
    }

    private Vector3d convert(int plane, LocalPoint local) {
        return renderer.world.position(
                client.getBaseX() + local.getX() / 128., client.getBaseY() + local.getY() / 128., plane
        );
    }

    private void drawInterface() {
        glDisable(GL_DEPTH_TEST);
        glDisable(GL_CULL_FACE);
        int canvasWidth = client.getCanvasWidth();
        int canvasHeight = client.getCanvasHeight();

        glBindTexture(GL_TEXTURE_2D, interfaceTexture);

        if (canvasWidth != lastCanvasWidth || canvasHeight != lastCanvasHeight) {
            lastCanvasWidth = canvasWidth;
            lastCanvasHeight = canvasHeight;
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, canvasWidth, canvasHeight, 0, GL_BGRA, GL_UNSIGNED_INT_8_8_8_8_REV, 0);
        }

        final BufferProvider b = client.getBufferProvider();
        glTexSubImage2D(GL_TEXTURE_2D, 0, 0, 0, b.getWidth(), b.getHeight(), GL_BGRA, GL_UNSIGNED_INT_8_8_8_8_REV, b.getPixels());

        interfaceRenderer.draw();

        glBindTexture(GL_TEXTURE_2D, 0);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);
    }

    @Override
    public boolean drawFace(Model model, int face) {
        return false;
    }

    @Override
    public void drawScene(int cameraX, int cameraY, int cameraZ, int cameraPitch, int cameraYaw, int plane) {
        client.getScene().setDrawDistance(90);
    }

    @Override
    public void animate(Texture texture, int diff) {
        // ignored
    }

    private BufferedImage screenshot() {
        int width = client.getCanvasWidth();
        int height = client.getCanvasWidth();

        ByteBuffer buffer = ByteBuffer.allocateDirect(width * height * 4 * 10)
                .order(ByteOrder.nativeOrder());

        glReadBuffer(GL_FRONT);
        glReadPixels(0, 0, width, height, GL_RGBA, GL_UNSIGNED_BYTE, buffer);

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        int[] pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();

        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                int r = buffer.get() & 0xff;
                int g = buffer.get() & 0xff;
                int b = buffer.get() & 0xff;
                buffer.get(); // alpha

                pixels[(height - y - 1) * width + x] = (r << 16) | (g << 8) | b;
            }
        }

        return image;
    }

    private void createInterfaceTexture() {
        interfaceTexture = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, interfaceTexture);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    private void updateFramebuffer() {
        if (lastWidth == width && lastHeight == height && lastSamples == config.samples().getSamples()) {
            return;
        }

        if (framebufferTexture != -1) {
            glDeleteTextures(framebufferTexture);
            framebufferTexture = -1;
        }

        if (framebuffer != -1) {
            glDeleteFramebuffers(framebuffer);
            framebuffer = -1;
        }

        if (colorRenderbuffer != -1) {
            glDeleteRenderbuffers(colorRenderbuffer);
            colorRenderbuffer = -1;
        }

        if (depthRenderbuffer != -1) {
            glDeleteRenderbuffers(depthRenderbuffer);
            colorRenderbuffer = -1;
        }

        int samples = Math.max(1, Math.min(glGetInteger(GL_MAX_SAMPLES), config.samples().getSamples()));
        framebuffer = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, framebuffer);

        // Create color render buffer
        colorRenderbuffer = glGenRenderbuffers();
        glBindRenderbuffer(GL_RENDERBUFFER, colorRenderbuffer);
        glRenderbufferStorageMultisample(GL_RENDERBUFFER, samples, GL_RGBA, width, height);
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_RENDERBUFFER, colorRenderbuffer);

        // Create depth render buffer
        depthRenderbuffer = glGenRenderbuffers();
        glBindRenderbuffer(GL_RENDERBUFFER, depthRenderbuffer);
        glRenderbufferStorageMultisample(GL_RENDERBUFFER, samples, GL_DEPTH_COMPONENT, width, height);
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, depthRenderbuffer);

        // Create texture
        framebufferTexture = glGenTextures();
        glBindTexture(GL_TEXTURE_2D_MULTISAMPLE, framebufferTexture);
        glTexImage2DMultisample(GL_TEXTURE_2D_MULTISAMPLE, samples, GL_RGBA, width, height, true);

        // Bind texture
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D_MULTISAMPLE, framebufferTexture, 0);

        // Reset
        glBindTexture(GL_TEXTURE_2D_MULTISAMPLE, 0);
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        glBindRenderbuffer(GL_RENDERBUFFER, 0);

        lastWidth = width;
        lastHeight = height;
        lastSamples = config.samples().getSamples();
    }

    private void handleCrash(Throwable t) {
        t.printStackTrace();
        try {
            pluginManager.setPluginEnabled(this, false);
            pluginManager.stopPlugin(this);
        } catch (PluginInstantiationException e) {
            RuntimeException e2 = new RuntimeException(e);
            e2.addSuppressed(t);
            throw e2;
        }
    }

    private void attachCanvas() {
        try {
            platformCanvas.lock();
            platformCanvas.makeCurrent(context);
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    private void detachCanvas() {
        try {
            platformCanvas.makeCurrent(0);
            platformCanvas.unlock();
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    private void updateWindow() {
        platformCanvas.swapBuffers();
    }
}
