package com.eclectic;

import com.google.common.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.client.RuneLite;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.game.ItemManager;
import com.google.gson.Gson;
import net.runelite.client.plugins.PluginType;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;
import org.pf4j.Extension;

@Slf4j
@Extension
@PluginDescriptor(
	name = "Eclectic Jar Tracker",
	description = "Tracks profits of opening eclectic impling jars",
	type = PluginType.UTILITY,
	enabledByDefault = false
)

public class EclecticPlugin extends Plugin
{
	private int jarsOpened;
	private double moneySpent;
	private double moneyGained;
	private boolean looting;
	private int nextMedium;
	private static boolean firstRun;
	private List<Double> data = new ArrayList<Double>();
	private List<Item> inv = new ArrayList<Item>();
	private List<Item> invPrev = new ArrayList<Item>();
	private List<Item> newItems = new ArrayList<Item>();
	public static final File PARENT_DIRECTORY = new File(RuneLite.RUNELITE_DIR, "eclectic");
	public static File directory;

	@Inject
	private Client client;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private EclecticOverlay overlay;

	@Inject
	private ItemManager itemManager;

	@Override
	public void startUp() throws IOException
	{
		overlayManager.add(overlay);
		looting = false;
		firstRun = false;
		setUp();
		if (!firstRun)
		{
			data = loadJson();
			jarsOpened = data.get(0).intValue();
			moneySpent = data.get(1);
			moneyGained = data.get(2);
			nextMedium = data.get(3).intValue();
		}
		else
		{
			data.add(0, 0.0);
			data.add(1, 0.0);
			data.add(2, 0.0);
			data.add(3, 0.0);
		}
	}

	@Override
	protected void shutDown() throws Exception
	{
		//System.out.println("Saving");
		overlayManager.remove(overlay);
		data.set(0, (double) jarsOpened);
		data.set(1, moneySpent);
		data.set(2, moneyGained);
		data.set(3, (double) nextMedium);
		saveJson(data);
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged)
	{
		try
		{
			if (gameStateChanged.getGameState() == GameState.LOGGED_IN)
			{
				looting = false;
				firstRun = false;
				setUp();
				if (!firstRun)
				{
					data = loadJson();
					jarsOpened = data.get(0).intValue();
					moneySpent = data.get(1);
					moneyGained = data.get(2);
					nextMedium = data.get(3).intValue();
				}
				else
				{
					data.add(0, 0.0);
					data.add(1, 0.0);
					data.add(2, 0.0);
					data.add(3, 0.0);
				}
				//invPrev = Arrays.asList(client.getItemContainer(InventoryID.INVENTORY).getItems());
			}
			else if (gameStateChanged.getGameState() == GameState.CONNECTION_LOST)
			{
				//System.out.println("Shut Down");
				data.set(0, (double) jarsOpened);
				data.set(1, moneySpent);
				data.set(2, moneyGained);
				data.set(3, (double) nextMedium);
				saveJson(data);
			}
		}
		catch (IOException ignored)
		{
		}
	}

	public static void setUp() throws IOException
	{
		directory = PARENT_DIRECTORY;
		createDirectory(PARENT_DIRECTORY);
		createRequiredFiles();
	}

	@Subscribe
	public void onMenuOptionClicked(MenuOptionClicked event)
	{
		if (event.getIdentifier() == 11248 && event.getOption().equals("Loot"))
		{
			if (!looting)
			{
				looting = true;
			}
			jarsOpened++;
			moneySpent += itemManager.getItemPrice(11248);
			System.out.println(jarsOpened + " jars opened");
		}
		else if (event.getIdentifier() == 20545 && nextMedium == 1 && event.getOption().equals("Open"))
		{
			if (!looting)
			{
				looting = true;
			}
			nextMedium = 0;
		}
	}

	@Subscribe
	public void onItemContainerChanged(ItemContainerChanged event)
	{
		if (event.getContainerId() == 93 && looting)
		{
			invPrev.clear();
			invPrev.addAll(inv);
			inv = Arrays.asList(event.getItemContainer().getItems());
			newItems.clear();
			newItems.addAll(inv);
			for (var item : invPrev)
			{
				newItems.remove(item);
			}
			for (var item : newItems)
			{
				if (item.getQuantity() > 0 && looting)
				{
					looting = false;
				}
				if (item.getQuantity() > 1 && invPrev.size() > inv.indexOf(item) && invPrev.get(inv.indexOf(item)).getId() == item.getId())
				{
					System.out.println(item.getQuantity() - invPrev.get(inv.indexOf(item)).getQuantity() +
						" " + itemManager.getItemDefinition(item.getId()).getName() + ": " +
						itemManager.getItemPrice(item.getId()) * (item.getQuantity() -
							invPrev.get(inv.indexOf(item)).getQuantity()) + " gp");
					moneyGained += itemManager.getItemPrice(item.getId()) * (item.getQuantity() -
						invPrev.get(inv.indexOf(item)).getQuantity());
				}
				else
				{
					System.out.println(item.getQuantity() +
						" " + itemManager.getItemDefinition(item.getId()).getName() + ": " +
						itemManager.getItemPrice(item.getId()) * item.getQuantity() + " gp");
					moneyGained += itemManager.getItemPrice(item.getId()) * item.getQuantity();
				}

				if (itemManager.getItemDefinition(item.getId()).getName().equals("Clue scroll (medium)"))
				{
					nextMedium = 1;
				}
			}
			System.out.println("Money Spent: " + moneySpent + " gp | Money Gained: " + moneyGained +
				" gp | Profit: " + (moneyGained - moneySpent));
			//System.out.println("Shut Down");
			data.set(0, (double) jarsOpened);
			data.set(1, moneySpent);
			data.set(2, moneyGained);
			data.set(3, (double) nextMedium);
			try
			{
				saveJson(data);
			}
			catch (IOException ignored)
			{
			}
		}
		else if (event.getContainerId() == 93)
		{
			inv = Arrays.asList(event.getItemContainer().getItems());
		}
	}

	private static void createRequiredFiles() throws IOException
	{
		File file = new File(directory, "eclectic-data.json");
		if (!file.exists())
		{
			if (!file.createNewFile())
			{
				System.out.println("Failed to generate file " + file.getPath());
			}
		}
	}

	private static void createDirectory(File directory) throws IOException
	{
		if (!directory.exists())
		{
			firstRun = true;
			System.out.println("Creating eclectic directory");
			if (!directory.mkdir())
			{
				throw new IOException("unable to create parent directory!");
			}
		}
	}

	private static String getFileContent(String filename) throws IOException
	{
		Path filePath = Paths.get(directory + "\\" + filename);
		byte[] fileBytes = Files.readAllBytes(filePath);
		return new String(fileBytes);
	}

	public static void saveJson(List<?> list) throws IOException
	{
		System.out.println("Saving JSON file");
		final Gson gson = new Gson();
		File file = new File(directory, "eclectic-data.json");
		final String json = gson.toJson(list);
		Files.write(file.toPath(), json.getBytes());
	}

	public static List<Double> loadJson() throws IOException
	{
		Gson gson = new Gson();
		String jsonString = getFileContent("eclectic-data.json");
		Type type = new TypeToken<List<Double>>()
		{
		}.getType();
		List<Double> dat = gson.fromJson(jsonString, type);
		if (dat == null)
		{
			return new ArrayList<Double>();
		}
		return dat;
	}

	public int getJarsOpened()
	{
		return jarsOpened;
	}

	public double getMoneySpent()
	{
		return moneySpent;
	}

	public double getMoneyGained()
	{
		return moneyGained;
	}

	public double getProfit()
	{
		return moneyGained - moneySpent;
	}

	public Color getProfitColor()
	{
		if (getProfit() > 0)
		{
			return Color.green;
		}
		else
		{
			return Color.red;
		}
	}
}
