package com.dklights;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import lombok.NonNull;
import net.runelite.api.coords.WorldPoint;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DKLightsHelper
{

	// Anything with y >= 5312 is in the north map square of Dorgesh-Kaan
	public static final int WORLDMAP_LINE = 5312;

	// HashMap containing WorldPoints for each lamp on Plane Pn for N(orth) and S(outh)
	public final HashMap<Integer, LampPoint> P0_N = new HashMap<>();
	public final HashMap<Integer, LampPoint> P0_S = new HashMap<>();
	public final HashMap<Integer, LampPoint> P1_N = new HashMap<>();
	public final HashMap<Integer, LampPoint> P1_S = new HashMap<>();
	public final HashMap<Integer, LampPoint> P2_N = new HashMap<>();
	public final HashMap<Integer, LampPoint> P2_S = new HashMap<>();

	public final HashMap<Integer, HashMap<Integer, LampPoint>[]> maps = new HashMap<>();

	// Initialize the HashMaps for each region with the points found in them.
	// The key refers to the bit in the Dorgesh-Kaan lamps varbit that the lamp
	// is indicated by (little-endian bits). The value is the WorldPoint of the lamp.
	public void init()
	{
		P0_N.put(5, new LampPoint(5, new WorldPoint(2691, 5328, 0), "Room just NW of the market"));
		P0_N.put(6, new LampPoint(6, new WorldPoint(2746, 5323, 0), "Group of rooms just NE of the market"));
		P0_N.put(7, new LampPoint(7, new WorldPoint(2749, 5329, 0), "Group of rooms just NE of the market"));
		P0_N.put(8, new LampPoint(8, new WorldPoint(2742, 5327, 0), "Group of rooms just NE of the market"));
		P0_N.put(9, new LampPoint(9, new WorldPoint(2737, 5324, 0), "Group of rooms just NE of the market"));
		P0_N.put(10, new LampPoint(10, new WorldPoint(2701, 5345, 0), "Bank area"));
		P0_N.put(11, new LampPoint(11, new WorldPoint(2706, 5354, 0), "Bank area"));
		P0_N.put(12, new LampPoint(12, new WorldPoint(2701, 5362, 0), "Oldak's teleportation lab"));
		P0_N.put(13, new LampPoint(13, new WorldPoint(2706, 5369, 0), "Oldak's teleportation lab"));
		P0_N.put(14, new LampPoint(14, new WorldPoint(2745, 5360, 0), "NE most room"));
		P0_N.put(15, new LampPoint(15, new WorldPoint(2739, 5360, 0), "NE most room"));
		P0_N.put(16, new LampPoint(16, new WorldPoint(2736, 5350, 0), "Room east of the bank area"));
		P0_N.put(17, new LampPoint(17, new WorldPoint(2747, 5348, 0), "Group of rooms just NE of the market"));
		P0_N.put(18, new LampPoint(18, new WorldPoint(2741, 5344, 0), "Group of rooms just NE of the market"));
		P0_N.put(19, new LampPoint(19, new WorldPoint(2744, 5348, 0), "Group of rooms just NE of the market"));

		P0_S.put(0, new LampPoint(0, new WorldPoint(2738, 5283, 0), "Group of rooms just SE of the market"));
		P0_S.put(1, new LampPoint(1, new WorldPoint(2749, 5292, 0), "Group of rooms just SE of the market"));
		P0_S.put(2, new LampPoint(2, new WorldPoint(2744, 5299, 0), "Group of rooms just SE of the market"));
		P0_S.put(3, new LampPoint(3, new WorldPoint(2690, 5302, 0), "Group of rooms just SW of the market"));
		P0_S.put(4, new LampPoint(4, new WorldPoint(2698, 5302, 0), "Group of rooms just SW of the market"));
		P0_S.put(10, new LampPoint(10, new WorldPoint(2699, 5256, 0), "SW most group of rooms"));
		P0_S.put(11, new LampPoint(11, new WorldPoint(2695, 5260, 0), "SW most group of rooms"));
		P0_S.put(12, new LampPoint(12, new WorldPoint(2698, 5269, 0), "SW most group of rooms"));
		P0_S.put(13, new LampPoint(13, new WorldPoint(2735, 5278, 0), "Eastern house in south part of the city"));
		P0_S.put(14, new LampPoint(14, new WorldPoint(2739, 5253, 0), "SE most group of rooms"));
		P0_S.put(15, new LampPoint(15, new WorldPoint(2749, 5261, 0), "SE most group of rooms"));
		P0_S.put(16, new LampPoint(16, new WorldPoint(2707, 5274, 0), "House just west of the wire machine"));

		P1_N.put(5, new LampPoint(5, new WorldPoint(2693, 5331, 1), "Western house with a garden"));
		P1_N.put(6, new LampPoint(6, new WorldPoint(2742, 5335, 1), "Nursery"));
		P1_N.put(7, new LampPoint(7, new WorldPoint(2738, 5324, 1), "House south of the nursery"));
		P1_N.put(8, new LampPoint(8, new WorldPoint(2693, 5333, 1), "Western house with a garden"));
		P1_N.put(9, new LampPoint(9, new WorldPoint(2742, 5341, 1), "Nursery"));
		P1_N.put(10, new LampPoint(10, new WorldPoint(2697, 5344, 1), "Western house north of the garden house"));
		P1_N.put(11, new LampPoint(11, new WorldPoint(2705, 5354, 1), "House just south of Oldak's lab"));
		P1_N.put(12, new LampPoint(12, new WorldPoint(2716, 5364, 1), "Council chamber"));
		P1_N.put(13, new LampPoint(13, new WorldPoint(2736, 5363, 1), "House with Ur-tag"));
		P1_N.put(14, new LampPoint(14, new WorldPoint(2739, 5362, 1), "House just east of Ur-tag"));
		P1_N.put(15, new LampPoint(15, new WorldPoint(2733, 5350, 1), "House just south of Ur-tag"));
		P1_N.put(16, new LampPoint(16, new WorldPoint(2705, 5348, 1), "House just south of Oldak's lab"));

		P1_S.put(0, new LampPoint(0, new WorldPoint(2699, 5305, 1), "Western house"));
		P1_S.put(1, new LampPoint(1, new WorldPoint(2739, 5286, 1), "Eastern house"));
		P1_S.put(2, new LampPoint(2, new WorldPoint(2737, 5294, 1), "Eastern house"));
		P1_S.put(3, new LampPoint(3, new WorldPoint(2741, 5283, 1), "Eastern house"));
		P1_S.put(4, new LampPoint(4, new WorldPoint(2695, 5294, 1), "Western house"));
		P1_S.put(10, new LampPoint(10, new WorldPoint(2736, 5272, 1), "Upstairs of the eastern house in the south part of the city"));
		P1_S.put(11, new LampPoint(11, new WorldPoint(2731, 5272, 1), "Upstairs of the eastern house in the south part of the city"));
		P1_S.put(12, new LampPoint(12, new WorldPoint(2736, 5278, 1), "Upstairs of the eastern house in the south part of the city"));
		P1_S.put(13, new LampPoint(13, new WorldPoint(2709, 5270, 1), "Upstairs of the house west of the wire machine"));
		P1_S.put(14, new LampPoint(14, new WorldPoint(2707, 5278, 1), "Upstairs of the house west of the wire machine"));

		P2_N.put(9, new LampPoint(9, new WorldPoint(2746, 5355, 2), "Zanik's bedroom"));
		P2_N.put(10, new LampPoint(10, new WorldPoint(2739, 5362, 2), "Upstairs of the house just east of Ur-tag"));
		P2_N.put(11, new LampPoint(11, new WorldPoint(2736, 5363, 2), "Upstairs of the house with Ur-tag"));
		P2_N.put(12, new LampPoint(12, new WorldPoint(2729, 5368, 2), "Upstairs of the house with Ur-tag"));

		P2_S.put(0, new LampPoint(0, new WorldPoint(2741, 5283, 2), "Upstairs of the eastern house two houses south of the empty building"));
		P2_S.put(1, new LampPoint(1, new WorldPoint(2737, 5298, 2), "Upstairs of the eastern house just south of the empty building"));
		P2_S.put(2, new LampPoint(2, new WorldPoint(2741, 5294, 2), "Upstairs of the eastern house just south of the empty building"));
		P2_S.put(3, new LampPoint(3, new WorldPoint(2741, 5287, 2), "Upstairs of the eastern house two houses south of the empty building"));
		P2_S.put(4, new LampPoint(4, new WorldPoint(2744, 5282, 2), "Upstairs of the eastern house two houses south of the empty building"));
		P2_S.put(5, new LampPoint(5, new WorldPoint(2695, 5294, 2), "Upstairs of the western house just north of the train station"));
		P2_S.put(6, new LampPoint(6, new WorldPoint(2699, 5289, 2), "Upstairs of the western house just north of the train station"));
		P2_S.put(7, new LampPoint(7, new WorldPoint(2699, 5305, 2), "Upstairs of the western house two houses north of the train station"));
		P2_S.put(8, new LampPoint(8, new WorldPoint(2695, 5301, 2), "Upstairs of the western house two houses north of the train station"));
		P2_S.put(9, new LampPoint(9, new WorldPoint(2740, 5264, 2), "Upstairs of the SE most house"));

		// There is probably a more concise way of doing this
		for (Integer key : P0_N.keySet())
		{
			P0_N.get(key).setArea(DKLightsEnum.P0_N);
		}
		for (Integer key : P0_S.keySet())
		{
			P0_S.get(key).setArea(DKLightsEnum.P0_S);
		}
		for (Integer key : P1_N.keySet())
		{
			P1_N.get(key).setArea(DKLightsEnum.P1_N);
		}
		for (Integer key : P1_S.keySet())
		{
			P1_S.get(key).setArea(DKLightsEnum.P1_S);
		}
		for (Integer key : P2_N.keySet())
		{
			P2_N.get(key).setArea(DKLightsEnum.P2_N);
		}
		for (Integer key : P2_S.keySet())
		{
			P2_S.get(key).setArea(DKLightsEnum.P2_S);
		}

		maps.put(DKLightsEnum.P0_N.value, new HashMap[]{P0_N, P0_S});
		maps.put(DKLightsEnum.P0_S.value, new HashMap[]{P0_S, P0_N});
		maps.put(DKLightsEnum.P1_N.value, new HashMap[]{P1_N, P1_S});
		maps.put(DKLightsEnum.P1_S.value, new HashMap[]{P1_S, P1_N});
		maps.put(DKLightsEnum.P2_N.value, new HashMap[]{P2_N, P2_S});
		maps.put(DKLightsEnum.P2_S.value, new HashMap[]{P2_S, P2_N});
	}

	// Determine which region of Dorgesh-Kaan the player is currently in.
	// The city is split across a northern and southern map square.
	// The interpretation of the Dorgesh-Kaan lamps varbit depends on whether the player is in the
	// north or south square and which plane the player is located in.
	public DKLightsEnum determineLocation(@NonNull WorldPoint w)
	{
		// Note that this is very explicit for readability.
		int plane = w.getPlane();
		int y = w.getY();
		if (plane == 0)
		{
			if (y >= WORLDMAP_LINE)
			{
				return DKLightsEnum.P0_N;
			}
			else
			{
				return DKLightsEnum.P0_S;
			}
		}
		else if (plane == 1)
		{
			if (y >= WORLDMAP_LINE)
			{
				return DKLightsEnum.P1_N;
			}
			else
			{
				return DKLightsEnum.P1_S;
			}
		}
		else if (plane == 2)
		{
			if (y >= WORLDMAP_LINE)
			{
				return DKLightsEnum.P2_N;
			}
			else
			{
				return DKLightsEnum.P2_S;
			}
		}
		return DKLightsEnum.BAD_AREA;
	}

	// Return a list of LampPoint objects corresponding to the broken lamps
	// based on the player's current region.
	// Typically, the parameter lamps should be the value of the Dorgesh-Kaan lamps varbit (4038).
	public ArrayList<LampPoint> getAreaLamps(int lamps, DKLightsEnum currentArea)
	{
		BitSet bits = BitSet.valueOf(new long[]{lamps});
		ArrayList<LampPoint> lampPoints = new ArrayList<>();

		HashMap<Integer, LampPoint> currentMap = maps.get(currentArea.value)[0];
		HashMap<Integer, LampPoint> oppositeMap = maps.get(currentArea.value)[1];

		// Only check up to the highest key in the current area.
		// Lower ones that are missing are correctly shown in the varbit, higher ones are not.
		int maxKey = Collections.max(currentMap.keySet());
		for (int i = 0; i <= maxKey; i++)
		{
			// For this set bit, grab the lamp loc based on the current area
			LampPoint l = currentMap.get(i);

			// If there is no lamp indicated by the bit i in the first map square,
			// the bit actually refers to a lamp in the other map square.
			if (l == null)
			{
				l = oppositeMap.get(i);
			}

			if (l != null)
			{
				boolean isBroken = false;
				if (i < bits.length())
				{
					isBroken = bits.get(i);
				}

				l.setBroken(isBroken);
				lampPoints.add(l);
			}
		}
		return lampPoints;
	}

	// Return a sorted ArrayList of n world points such that the WorldPoint closest to the player
	// is at index 0 and the farthest point is at index n-1.
	public ArrayList<LampPoint> sortBrokenLamps(HashSet<LampPoint> lampPoints, @NonNull WorldPoint currentPoint)
	{

		ArrayList<LampPoint> sortedPoints = new ArrayList<>(lampPoints);

		Comparator<LampPoint> comparator = new Comparator<LampPoint>()
		{
			public int compare(LampPoint a, LampPoint b)
			{
				return currentPoint.distanceTo(a.getWorldPoint()) - currentPoint.distanceTo(b.getWorldPoint());
			}
		};

		sortedPoints.sort(comparator);

		return sortedPoints;
	}
}
