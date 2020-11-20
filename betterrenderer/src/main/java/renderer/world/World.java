package renderer.world;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import org.joml.Vector2d;
import org.joml.Vector3d;
import org.joml.Vector4d;
import renderer.cache.CacheSystem;
import renderer.renderer.WorldRenderer;
import renderer.util.Colors;
import renderer.util.Util;

import java.util.*;

public class World {
    public static final int BLEND_RADIUS = 5;
    private final Int2ObjectMap<List<Position>> roofs = new Int2ObjectOpenHashMap<>();
    public HashSet<Position> roofsRemoved = new HashSet<>();
    public int roofRemovalPlane;
    public Int2ObjectMap<Region> instanceRegions = null;
    private Int2ObjectMap<List<Location>> localChunks = new Int2ObjectOpenHashMap<>();

    private Region region(int x, int y) {
        if (instanceRegions != null) {
            Region instanceRegion = instanceRegions.get(x * 256 + y);

            if (instanceRegion != null) {
                return instanceRegion;
            }
        }

        return CacheSystem.region(x, y);
    }

    public UnderlayDefinition underlay(int x, int y, int z) {
        Region region = region(x / 64, y / 64);
        return region == null ? null : region.underlays[z][x % 64][y % 64];
    }

    public OverlayDefinition overlay(int x, int y, int z) {
        Region region = region(x / 64, y / 64);
        return region == null ? null : region.overlays[z][x % 64][y % 64];
    }

    public OverlayShape getOverlayShape(int x, int y, int plane) {
        Region region = region(x / 64, y / 64);
        return region == null ? null : region.overlayShapes[plane][x % 64][y % 64];
    }

    public byte getOverlayRotation(int x, int y, int plane) {
        Region region = region(x / 64, y / 64);
        return region == null ? 0 : region.overlayRotations[plane][x % 64][y % 64];
    }

    public byte settings(int x, int y, int plane) {
        Region region = region(x / 64, y / 64);
        return region == null ? 0 : region.settings[plane][x % 64][y % 64];
    }

    public List<Location> locations(int chunkX, int chunkY) {
        List<Location> locations = localChunks.get((chunkX << 16) + chunkY);

        if (locations != null) {
            return locations;
        }

        Region region = region(chunkX / 8, chunkY / 8);

        if (region == null) {
            return Collections.emptyList();
        }

        locations = new ArrayList<>();

        for (Location location : region.locations) {
            if (location.position.x / 8 == chunkX && location.position.y / 8 == chunkY) {
                locations.add(location);
            }
        }

        return locations;
    }

    /////////////////////////////////////////////////////
    //                    Heights                      //
    /////////////////////////////////////////////////////

    public double height(double x, double y, int z) {
        double h00 = height((int) x, (int) y, z);
        double h10 = height((int) x + 1, (int) y, z);
        double h01 = height((int) x, (int) y + 1, z);
        double h11 = height((int) x + 1, (int) y + 1, z);

        return h00 * (1 - x % 1) * (1 - y % 1) +
                h10 * (x % 1) * (1 - y % 1) +
                h01 * (1 - x % 1) * (y % 1) +
                h11 * (x % 1) * (y % 1);
    }

    public double height(int x, int y, int z) {
        Region region = region(x / 64, y / 64);

        if (region == null) {
            return -extendedHeight(x, y, z) * WorldRenderer.SCALE;
        }

        return -region.heights[z][x % 64][y % 64] * WorldRenderer.SCALE;
    }

    private int extendedHeight(int x, int y, int z) {
        int height = -1;
        if (height == -1) height = directHeight(x, y, z);
        if (height == -1) height = directHeight(x - 1, y, z);
        if (height == -1) height = directHeight(x + 1, y, z);
        if (height == -1) height = directHeight(x, y - 1, z);
        if (height == -1) height = directHeight(x, y + 1, z);
        if (height == -1) height = directHeight(x - 1, y - 1, z);
        if (height == -1) height = directHeight(x + 1, y + 1, z);
        if (height == -1) height = directHeight(x - 1, y + 1, z);
        if (height == -1) height = directHeight(x + 1, y - 1, z);
        if (height == -1) height = directHeight(x - 2, y, z);
        if (height == -1) height = directHeight(x + 2, y, z);
        if (height == -1) height = directHeight(x, y - 2, z);
        if (height == -1) height = directHeight(x, y + 2, z);
        if (height == -1) height = directHeight(x - 2, y - 2, z);
        if (height == -1) height = directHeight(x + 2, y + 2, z);
        if (height == -1) height = directHeight(x - 2, y + 2, z);
        if (height == -1) height = directHeight(x + 2, y - 2, z);
        return height;
    }

    private int directHeight(int x, int y, int z) {
        Region region = region(x / 64, y / 64);

        if (region == null) {
            return -1;
        }

        return region.heights[z][x % 64][y % 64];
    }

    public Vector3d position(int x, int y, int z) {
        return new Vector3d(x, y, height(x, y, z));
    }

    public Vector3d position(double x, double y, int z) {
        return new Vector3d(x, y, height(x, y, z));
    }

    public Vector3d normal(double x, double y, int z) {
        Vector3d center = position(x, y, z);
        Vector3d e = position(x + 0.01, y, z);
        Vector3d n = position(x, y + 0.01, z);
        Vector3d w = position(x - 0.01, y, z);
        Vector3d s = position(x, y - 0.01, z);

        return new Vector3d()
                .add(Util.normal(center, e, n))
                .add(Util.normal(center, n, w))
                .add(Util.normal(center, w, s))
                .add(Util.normal(center, s, e))
                .normalize();
    }

    /////////////////////////////////////////////////////
    //                     Colors                      //
    /////////////////////////////////////////////////////

    public int color(double x, double y, int z) {
        Vector3d n00 = Colors.unpack(color((int) x, (int) y, z));
        Vector3d n10 = Colors.unpack(color((int) x + 1, (int) y, z));
        Vector3d n01 = Colors.unpack(color((int) x, (int) y + 1, z));
        Vector3d n11 = Colors.unpack(color((int) x + 1, (int) y + 1, z));

        return Colors.pack(new Vector3d()
                .add(n00.mul(1 - x % 1).mul(1 - y % 1))
                .add(n10.mul(x % 1).mul(1 - y % 1))
                .add(n01.mul(1 - x % 1).mul(y % 1))
                .add(n11.mul(x % 1).mul(y % 1))
        );
    }

    public int color(int x, int y, int z) {
        Region region = region(x / 64, y / 64);

        if (region == null) {
            return 0;
        }

        if (region.blendedColors == null) {
            region.blendedColors = blendColors(x / 64, y / 64);
        }

        return region.blendedColors[z][x % 64][y % 64];
    }

    private int[][][] blendColors(int regionX, int regionY) {
        int[][][] colors = new int[4][64][64];

        for (int plane = 0; plane < 4; plane++) {
            Vector4d[][] blended = new Vector4d[64 + 2 * BLEND_RADIUS][64 + 2 * BLEND_RADIUS];

            for (int dx = -BLEND_RADIUS; dx < 64 + BLEND_RADIUS; dx++) {
                for (int dy = -BLEND_RADIUS; dy < 64 + BLEND_RADIUS; dy++) {
                    Vector3d color = Colors.unpack(unblendedColor(regionX * 64 + dx, regionY * 64 + dy, plane));
                    blended[BLEND_RADIUS + dx][BLEND_RADIUS + dy] = color == null ?
                            new Vector4d(0, 0, 0, 0) :
                            new Vector4d(color.x, color.y, color.z, 1);
                }
            }

            blended = Util.boxBlur(blended, BLEND_RADIUS, 64);

            for (int dx = 0; dx < 64; dx++) {
                for (int dy = 0; dy < 64; dy++) {
                    Vector4d c = blended[dx + BLEND_RADIUS][dy + BLEND_RADIUS];
                    colors[plane][dx][dy] = Colors.pack(new Vector3d(c.x / c.w, c.y / c.w, c.z / c.w));
                }
            }
        }

        return colors;
    }

    public int unblendedColor(int x, int y, int z) {
        UnderlayDefinition underlay = underlay(x, y, z);
        return underlay == null ? -1 : underlay.color;
    }

    /////////////////////////////////////////////////////
    //                      Roofs                      //
    /////////////////////////////////////////////////////

    public List<Position> getRoof(int x, int y, int z) {
        if (!hasRoof(x, y, z)) {
            return Collections.emptyList();
        }

        List<Position> roof = roofs.get((z << 30) + (x << 16) + y);

        if (roof != null) {
            return roof;
        }

        roof = new ArrayList<>();
        HashSet<Position> visited = new HashSet<>();
        Deque<Position> queue = new ArrayDeque<>();
        queue.add(new Position(x, y, z));

        while (!queue.isEmpty()) {
            Position pos = queue.poll();

            if (!visited.add(pos) || !hasRoof(pos.x, pos.y, z)) {
                continue;
            }

            if (hasRoof(pos.x, pos.y, pos.z)) {
                roof.add(pos);
                roofs.put((pos.z << 30) + (pos.x << 16) + pos.y, roof);
            }

            queue.add(pos.north());
            queue.add(pos.south());
            queue.add(pos.east());
            queue.add(pos.west());
        }

        return roof;
    }

    private boolean hasRoof(int x, int y, int z) {
        return (settings(x, y, z) & 4) != 0;
    }

    public void updateRoofs(int x, int y, int z, int radius) {
        Set<List<Position>> roofs = Collections.newSetFromMap(new IdentityHashMap<>());

        for (int roofX = x - radius; roofX < x + radius; roofX++) {
            for (int roofY = y - radius; roofY < y + radius; roofY++) {
                if (new Vector2d(roofX - x, roofY - y).length() < radius) {
                    roofs.add(getRoof(roofX, roofY, z));
                }
            }
        }

        roofsRemoved.clear();

        for (List<Position> roof : roofs) {
            for (Position p : roof) {
                roofsRemoved.add(new Position(p.x, p.y, 0));
            }
        }

        for (int i = 0; i < 2; i++) {
            for (Position p : new HashSet<>(roofsRemoved)) {
                roofsRemoved.add(p.north());
                roofsRemoved.add(p.south());
                roofsRemoved.add(p.east());
                roofsRemoved.add(p.west());
                roofsRemoved.add(p.north().west());
                roofsRemoved.add(p.north().east());
                roofsRemoved.add(p.south().west());
                roofsRemoved.add(p.south().east());
            }
        }

        roofRemovalPlane = z + 1;
    }
}
