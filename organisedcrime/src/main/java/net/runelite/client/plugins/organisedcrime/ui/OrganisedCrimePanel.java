package net.runelite.client.plugins.organisedcrime.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.List;
import java.util.function.Consumer;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.plugins.organisedcrime.config.OrganisedCrimeConfig;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.ui.components.PluginErrorPanel;

@Slf4j
public class OrganisedCrimePanel extends PluginPanel
{

	private final OrganisedCrimeConfig config;
	private final JPanel layoutPanel = new JPanel();
	private final Consumer<Integer> onWorldClicked;

	public OrganisedCrimePanel(OrganisedCrimeConfig config, Consumer<Integer> onWorldClicked)
	{
		this.config = config;
		this.onWorldClicked = onWorldClicked;

		setLayout(new BorderLayout(0, 8));
		setBorder(new EmptyBorder(0, 0, 0, 0));
		setBackground(ColorScheme.DARK_GRAY_COLOR);

		layoutPanel.setLayout(new BoxLayout(layoutPanel, BoxLayout.Y_AXIS));
		add(layoutPanel, BorderLayout.NORTH);
	}

	public void displayEmpty()
	{
		layoutPanel.removeAll();
		PluginErrorPanel errorPanel = new PluginErrorPanel();
		errorPanel.setContent(
			"Organised Crime Tracker",
			"View the information board to start tracking locations."
		);
		layoutPanel.add(errorPanel);
	}

	public void display(List<LocationViewState> viewState)
	{
		layoutPanel.removeAll();
		viewState.forEach(locationViewState -> {
			layoutPanel.add(new LocationPanel(locationViewState, onWorldClicked));
			layoutPanel.revalidate();
		});
	}

	public void refresh()
	{
		for (Component component : layoutPanel.getComponents())
		{
			if (component instanceof LocationPanel)
			{
				((LocationPanel) component).refreshWorldButtons();
			}
		}
	}
}
