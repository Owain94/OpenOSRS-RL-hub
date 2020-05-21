package net.runelite.client.plugins.dataexport.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.Constants;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.dataexport.DataExport;
import net.runelite.client.plugins.dataexport.DataExportConfig;
import net.runelite.client.plugins.dataexport.DataExportPlugin;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;

public class DataExportTabPanel extends JPanel
{
	private static final Color COLOR = ColorScheme.DARK_GRAY_COLOR;

	private static final Color HOVER_COLOR = ColorScheme.DARKER_GRAY_HOVER_COLOR;

	private final ItemManager itemManager;

	private final DataExportPlugin plugin;

	private final DataExportPluginPanel panel;

	private final DataExportConfig config;

	private final DataExport dataExport;

	@Getter
	public String title;

	@Getter
	@Setter
	public String status;

	@Getter
	@Setter
	public boolean visibility = true;

	JLabel readyLabel;

	JButton buttonExport;

	JButton buttonDownload;

	DataExportTabPanel(DataExportPlugin plugin, DataExportPluginPanel panel, DataExportConfig config, DataExport dataExport, ItemManager itemManager, Tab tab, String title, String container, String status)
	{
		this.plugin = plugin;
		this.panel = panel;
		this.config = config;
		this.dataExport = dataExport;
		this.itemManager = itemManager;
		this.title = title;
		this.status = status;

		setBackground(ColorScheme.DARKER_GRAY_COLOR);
		setLayout(new BorderLayout());
		setBorder(new EmptyBorder(5, 10, 5, 10));

		JPanel panelWrapper = new JPanel(new BorderLayout());
		panelWrapper.setLayout(new GridLayout(1, 2));
		panelWrapper.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		panelWrapper.setBorder(new EmptyBorder(5, 10, 5, 10));
		panelWrapper.setVisible(true);

		JPanel leftContainer = new JPanel();
		leftContainer.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		leftContainer.setLayout(new BorderLayout());
		//leftContainer.setBorder(new EmptyBorder(5, 7, 5, 7));

		JLabel titleLabel = new JLabel(title);
		titleLabel.setForeground(Color.WHITE);

		JLabel iconLabel = new JLabel();
		iconLabel.setMinimumSize(new Dimension(Constants.ITEM_SPRITE_WIDTH, Constants.ITEM_SPRITE_HEIGHT));
		itemManager.getImage(tab.getItemID()).addTo(iconLabel);

		leftContainer.add(titleLabel, BorderLayout.NORTH);
		leftContainer.add(iconLabel, BorderLayout.SOUTH);

		JPanel rightContainer = new JPanel();
		rightContainer.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		rightContainer.setLayout(new GridLayout(2, 1));
		//rightContainer.setBorder(new EmptyBorder(5, 10, 5, 10));

		readyLabel = new JLabel(status);
		readyLabel.setForeground(Color.RED);
		readyLabel.setFont(FontManager.getRunescapeSmallFont());

		JPanel buttonContainer = new JPanel();
		buttonContainer.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		buttonContainer.setLayout(new GridLayout(1, 2));
		buttonContainer.setBorder(new EmptyBorder(5, 10, 5, 10));

		buttonExport = new JButton();
		buttonExport.setText("Export");
		buttonExport.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		buttonExport.setBorder(new EmptyBorder(3, 7, 3, 7));

		buttonExport.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent e)
			{
				buttonExport.setBackground(ColorScheme.BRAND_BLUE);
				plugin.exportContainer(container);
			}

			@Override
			public void mouseReleased(MouseEvent e)
			{
				buttonExport.setBackground(ColorScheme.DARKER_GRAY_COLOR);
			}

			@Override
			public void mouseEntered(MouseEvent e)
			{
				buttonExport.setBackground(HOVER_COLOR);
				setCursor(new Cursor(Cursor.HAND_CURSOR));
			}

			@Override
			public void mouseExited(MouseEvent e)
			{
				buttonExport.setBackground(COLOR);
				setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}
		});

		buttonDownload = new JButton();
		buttonDownload.setText("Download");
		buttonDownload.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		buttonDownload.setBorder(new EmptyBorder(3, 7, 3, 7));

		buttonDownload.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent e)
			{
				buttonDownload.setBackground(ColorScheme.BRAND_BLUE);
				plugin.downloadContainer(container);
			}

			@Override
			public void mouseReleased(MouseEvent e)
			{
				buttonDownload.setBackground(ColorScheme.DARKER_GRAY_COLOR);
			}

			@Override
			public void mouseEntered(MouseEvent e)
			{
				buttonDownload.setBackground(HOVER_COLOR);
				setCursor(new Cursor(Cursor.HAND_CURSOR));
			}

			@Override
			public void mouseExited(MouseEvent e)
			{
				buttonDownload.setBackground(COLOR);
				setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}
		});

		buttonContainer.add(buttonExport);
		buttonContainer.add(buttonDownload);

		rightContainer.add(readyLabel);
		rightContainer.add(buttonContainer);

		panelWrapper.add(leftContainer, BorderLayout.WEST);
		panelWrapper.add(rightContainer, BorderLayout.CENTER);

		add(panelWrapper);

		updateVisibility();
	}

	private void updateVisibility()
	{
		if (!config.displayExport())
		{
			buttonExport.setVisible(false);
		}

		if (!config.displayDownload())
		{
			buttonDownload.setVisible(false);
		}
	}

	public void updateStatus(String status)
	{
		readyLabel.setText(status);

		if (status.equals("Visit a bank!"))
		{
			readyLabel.setForeground(Color.RED);
		}
		else
		{
			readyLabel.setForeground(Color.GREEN);
		}
	}

	@Override
	public String toString()
	{
		return title + ", " + isVisible();
	}
}