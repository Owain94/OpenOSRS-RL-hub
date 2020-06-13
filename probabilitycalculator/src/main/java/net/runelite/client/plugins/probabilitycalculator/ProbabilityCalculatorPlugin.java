package net.runelite.client.plugins.probabilitycalculator;

import com.google.inject.Provides;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.image.BufferedImage;
import java.util.function.Consumer;
import javax.inject.Inject;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginType;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;
import org.pf4j.Extension;

@Extension
@PluginDescriptor(
	name = "Probability Calculator",
	description = "Calculates the statistical probability of various mechanics such as drops",
	enabledByDefault = false,
	type = PluginType.MISCELLANEOUS
)
public class ProbabilityCalculatorPlugin extends Plugin
{

	@Inject
	private ProbabilityCalculatorConfig config;

	@Inject
	private ClientToolbar clientToolbar;

	private ProbabilityCalculatorInputArea input;
	private ProbabilityCalculatorOutputArea output;
	private NavigationButton navButton;
	private double dropRate = 1.0 / 100;
	private int killCount = 100;
	private int dropsReceived = 1;

	@Override
	protected void startUp()
	{
		input = new ProbabilityCalculatorInputArea();
		output = new ProbabilityCalculatorOutputArea(dropRate, killCount, dropsReceived, config);
		ProbabilityCalculatorPanel panel = new ProbabilityCalculatorPanel(input, output);
		//panel.init(config);

		final BufferedImage icon = ImageUtil.getResourceStreamFromClass(ProbabilityCalculatorPlugin.class, "probabilitycalculator_icon.png");

		//Action listeners
		input.getUiDropRate().addActionListener(e ->
			onFieldDropRateUpdated());
		input.getUiKillCount().addActionListener(e ->
			onFieldKillCountUpdated());
		input.getUiDropsReceived().addActionListener(e ->
			onFieldDropsReceivedUpdated());

		//Focus listeners
		input.getUiDropRate().addFocusListener(buildFocusAdapter(e -> onFieldDropRateUpdated()));
		input.getUiKillCount().addFocusListener(buildFocusAdapter(e -> onFieldKillCountUpdated()));
		input.getUiDropsReceived().addFocusListener(buildFocusAdapter(e -> onFieldDropsReceivedUpdated()));

		updateInputFields();

		navButton = NavigationButton.builder()
			.tooltip("Probability Calculator")
			.icon(icon)
			.priority(7)
			.panel(panel)
			.build();

		clientToolbar.addNavigation(navButton);

	}

	private void onFieldDropRateUpdated()
	{
		dropRate = input.getDropRateInput();
		updateInputFields();
	}

	private void onFieldKillCountUpdated()
	{
		killCount = (int) input.getKillCountInput();
		updateInputFields();
	}

	private void onFieldDropsReceivedUpdated()
	{
		dropsReceived = (int) input.getDropsReceivedInput();
		updateInputFields();
	}

	private void updateInputFields()
	{
		input.setDropRateInput(dropRate);
		input.setDropsReceivedInput(dropsReceived);
		input.setKillCountInput(killCount);

		output.setDropRate(dropRate);
		output.setKillCount(killCount);
		output.setDropsReceived(dropsReceived);
		output.updateTextArea();
	}

	private FocusAdapter buildFocusAdapter(Consumer<FocusEvent> focusLostConsumer)
	{
		return new FocusAdapter()
		{
			@Override
			public void focusLost(FocusEvent e)
			{
				focusLostConsumer.accept(e);
			}
		};
	}

	@Override
	protected void shutDown()
	{
		clientToolbar.removeNavigation(navButton);
	}

	@Provides
	ProbabilityCalculatorConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(ProbabilityCalculatorConfig.class);
	}
}