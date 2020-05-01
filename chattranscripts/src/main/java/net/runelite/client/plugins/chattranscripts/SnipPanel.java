package net.runelite.client.plugins.chattranscripts;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.swing.CellRendererPane;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import net.runelite.api.ClanMemberRank;
import net.runelite.api.Client;
import net.runelite.api.widgets.Widget;
import static net.runelite.client.RuneLite.SCREENSHOT_DIR;
import net.runelite.client.game.ClanManager;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.PluginPanel;
import org.apache.commons.lang3.StringUtils;

public class SnipPanel extends PluginPanel
{
	private static final DateFormat TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
	JButton imageButton = new JButton("Generate Image");

	@Inject
	private Client client;

	@Inject
	private SnipConfig config;

	@Inject
	private ClanManager clanManager;

	private String First = "Line of starting message.";
	private String Second = "Line of ending message.";
	private String Error = "Please check that both lines match messages in chat box. Ranks/ Irons/ Mod status/ Emojis are not detected.";
	private String Output = "\nWaiting to generate transcript. \n\nQuick Commands: \n\nTo generate a transcript of the entire chat use ^all and all$ as the starting and " +
		"ending messages. \n\nIf you have the starting line you can use +# (ex \"+3\") as the end line to make a transcript of that many extra lines. this will error " +
		"if you try to use too many lines.\n";
	private Boolean Ready = false;
	private String Transcript;
	private JTextArea firstBar;
	private JTextArea secondBar;
	private JTextArea OutputField = new JTextArea(Output);

	public SnipPanel(SnipConfig config, Client client, ClanManager clanManager)
	{
		this.client = client;
		this.config = config;
		this.clanManager = clanManager;

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 0;
		c.insets = new Insets(0, 0, 10, 0);

		firstBar = new JTextArea(First);
		firstBar.setForeground(ColorScheme.MEDIUM_GRAY_COLOR);
		firstBar.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		firstBar.setLineWrap(true);
		firstBar.setWrapStyleWord(true);
		firstBar.setMinimumSize(new Dimension(PluginPanel.PANEL_WIDTH - 20, 30));
		firstBar.addFocusListener(new FocusListener()
		{
			@Override
			public void focusGained(FocusEvent e)
			{
				if (firstBar.getText().equals(First))
				{
					firstBar.setText("");
					firstBar.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
				}
			}

			@Override
			public void focusLost(FocusEvent e)
			{
				if (firstBar.getText().isEmpty())
				{
					firstBar.setForeground(ColorScheme.MEDIUM_GRAY_COLOR);
					firstBar.setText(First);
				}
			}
		});
		add(firstBar, c);
		c.gridy++;

		secondBar = new JTextArea(Second);
		secondBar.setForeground(ColorScheme.MEDIUM_GRAY_COLOR);
		secondBar.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		secondBar.setLineWrap(true);
		secondBar.setWrapStyleWord(true);
		secondBar.setMinimumSize(new Dimension(PluginPanel.PANEL_WIDTH - 20, 30));
		secondBar.addFocusListener(new FocusListener()
		{
			@Override
			public void focusGained(FocusEvent e)
			{
				if (secondBar.getText().equals(Second))
				{
					secondBar.setText("");
					secondBar.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
				}
			}

			@Override
			public void focusLost(FocusEvent e)
			{
				if (secondBar.getText().isEmpty())
				{
					secondBar.setForeground(ColorScheme.MEDIUM_GRAY_COLOR);
					secondBar.setText(Second);
				}
			}
		});
		add(secondBar, c);
		c.gridy++;

		JPanel refreshPanel = new JPanel();
		refreshPanel.setLayout(new BorderLayout());
		JButton refreshButton = new JButton("Generate Transcript");
		refreshButton.setFocusPainted(false);
		refreshButton.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
		refreshButton.addActionListener((event) ->
		{
			String startPoint = firstBar.getText();
			String endPoint = secondBar.getText();
			if (startPoint.equals(First) || endPoint.equals(Second))
			{
				Output = Error;
				OutputField.setText(Output);
				return;
			}
			if (!scrubChat(startPoint.trim(), endPoint.trim()))
			{
				Output = Error;
				OutputField.setText(Output);
				return;
			}
		});
		refreshPanel.add(refreshButton, BorderLayout.CENTER);
		add(refreshPanel, c);
		c.gridy++;

		OutputField.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		OutputField.setLineWrap(true);
		OutputField.setWrapStyleWord(true);
		OutputField.setMinimumSize(new Dimension(PluginPanel.PANEL_WIDTH - 20, 30));
		add(OutputField, c);
		c.gridy++;

		JPanel imagePanel = new JPanel();
		imagePanel.setLayout(new BorderLayout());
		imageButton.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
		imageButton.setFocusPainted(false);
		imageButton.addActionListener((event) ->
		{
			if (Ready)
			{
				try
				{
					makeImage(Transcript);
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		});
		imagePanel.add(imageButton, BorderLayout.CENTER);
		add(imagePanel, c);
		c.gridy++;


	}

	static String format(Date date)
	{
		synchronized (TIME_FORMAT)
		{
			return TIME_FORMAT.format(date);
		}
	}

	private Boolean scrubChat(String start, String end)
	{
		Ready = false;
		if (client.getWidget(162, 58) != null)
		{
			Widget[] Testing = client.getWidget(162, 58).getDynamicChildren();
			if (Testing.length == 0)
			{
				return (false);
			}
			String check = "";
			String temp = "";
			String tempSplit = "";
			String finalSplit = "";
			String out = "";
			Boolean first = false;
			Boolean last = false;
			Transcript = "";
			int stopAt = -1;
			int counter = 0;
			if (start.equals("^all") && end.equals("all$"))
			{
				first = true;
				last = true;
			}
			else if (end.matches("^\\+\\d+$"))
			{
				stopAt = Integer.parseInt(end.replace("+", ""));
			}
			for (int x = Testing.length - 1; x >= 0; x--)
			{
				if (!Testing[x].getText().isEmpty() && !Testing[x + 1].getText().isEmpty()
					&& (Testing[x].getRelativeY() == Testing[x + 1].getRelativeY()))
				{
					check = Testing[x].getText() + " " + Testing[x + 1].getText();

					if (check.split("<col=.{6}>").length > 0)
					{
						temp = "";
						tempSplit = "";
						finalSplit = "";
						for (String hold : check.split("<col=.{6}>"))
						{
							temp += hold;
						}
						for (String hold : temp.split("</col>"))
						{
							tempSplit += hold;
						}
						for (String hold : tempSplit.split("<img=\\d{1,3}>"))
						{
							finalSplit += hold.trim();
						}
						finalSplit = finalSplit.replaceAll("<lt>", "<").replaceAll("<gt>", ">");
					}
					if (finalSplit.trim().toLowerCase().endsWith(start.toLowerCase()))
					{
						first = true;
					}
					if (first && (finalSplit.trim().toLowerCase().endsWith(end.toLowerCase()) || counter == stopAt))
					{
						out += finalSplit;
						Transcript += Testing[x].getText() + " " + Testing[x + 1].getText();
						last = true;
						break;
					}
					if (!finalSplit.isEmpty() && first)
					{
						Transcript += Testing[x].getText() + " " + Testing[x + 1].getText() + "\n";
						out += finalSplit + "\n";
						if (stopAt != -1)
						{
							counter++;
						}
					}
				}
			}
			if (!out.isEmpty() && last)
			{
				if (start.equals("^all") && end.equals("all$"))
				{
					out = out.substring(0, out.lastIndexOf("\n"));
				}
				Output = out;
				OutputField.setText(Output);
				Ready = true;
				return (true);
			}
		}
		return (false);
	}

	private void makeImage(String chat) throws IOException
	{

		String newTranscript = Transcript.replaceAll("<col=", "<font color=#").replaceAll("</col>", "</font color>").replaceAll("\n", "<br>").replaceAll("<lt>", "\\&lt;").replaceAll("<gt>", "\\>");//.replaceAll("<img=\\d*>", "");
		String newerTranscript = "";
		String[] newSplit = newTranscript.split("<br>");
		for (int x = 0; x < newSplit.length; x++)
		{
			if (newSplit[x].split("<font color=#.{6}>").length != newSplit[x].split("</font color>").length)
			{
				for (int y = 0; y < newSplit[x].split("<font color=#.{6}>").length - newSplit[x].split("</font color>").length; y++)
				{
					newSplit[x] += "</font color>";
				}
			}
			if (x != newSplit.length - 1)
			{
				newSplit[x] += "<br>";
			}
			if (newSplit[x].contains("<img="))
			{
				ArrayList<String> newerSplit = new ArrayList<String>();
				int lastChecked = 0;
				for (int y = 0; y < StringUtils.countMatches(newSplit[x], "<img="); y++)
				{
					String toCheck = newSplit[x].substring(lastChecked);
					lastChecked += toCheck.indexOf("<img=") + 1;
					toCheck = toCheck.substring(toCheck.indexOf("<img="));
					newerSplit.add(toCheck.substring(5, toCheck.indexOf(">")));
				}
				for (int y = 0; y < newerSplit.size(); y++)
				{
					//it aint pretty but its worked every time I've tried it
					int url = Integer.valueOf(newerSplit.get(y));
					if (url > 10)
					{
						url -= (clanManager.getIconNumber(ClanMemberRank.OWNER) - 27);
					}
					URL path = getClass().getResource(url + ".png");
					String path2;
					if (path != null)
					{
						path2 = path.toString();
						newSplit[x] = newSplit[x].replaceFirst("<img=\\d*>", "<img src=\"" + path2 + "\">");
					}
					else
					{
						newSplit[x] = newSplit[x].replaceFirst("<img=\\d*>", "");
					}
				}
			}
			newerTranscript += newSplit[x];
		}
		JLabel label = new JLabel("<html>" + newerTranscript.replaceAll(" ", "&nbsp;") + "</html>");
		label.setBackground(new Color(208, 188, 157));
		label.setForeground(Color.BLACK);
		label.setOpaque(true);
		int width = label.getPreferredSize().width;
		int height = label.getPreferredSize().height;
		BufferedImage bufferedImage = new BufferedImage(width + 1, height + 3, BufferedImage.TYPE_INT_ARGB);
		Graphics g2d = bufferedImage.createGraphics();
		SwingUtilities.paintComponent(g2d, label, new CellRendererPane(), 1, 0, width, height);
		g2d.dispose();
		File parentFolder = new File(SCREENSHOT_DIR, "Transcripts");
		parentFolder.mkdirs();
		File file = new File(parentFolder, client.getLocalPlayer().getName() + format(new Date()) + ".png");
		try
		{
			if (config.clipboard())
			{
				TransferableImage trans = new TransferableImage(bufferedImage);
				Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();
				c.setContents(trans, null);
				OutputField.setText("Transcript saved to clipboard.");
			}
			if (config.saveImage())
			{
				ImageIO.write(bufferedImage, "png", file);
				OutputField.setText("Transcript saved to Screenshots folder.");
				if (config.postOpen())
				{
					Desktop.getDesktop().open(file);
				}
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

	}

	private class TransferableImage implements Transferable
	{

		Image i;

		public TransferableImage(Image i)
		{
			this.i = i;
		}

		public Object getTransferData(DataFlavor flavor)
			throws UnsupportedFlavorException, IOException
		{
			if (flavor.equals(DataFlavor.imageFlavor) && i != null)
			{
				return i;
			}
			else
			{
				throw new UnsupportedFlavorException(flavor);
			}
		}

		public DataFlavor[] getTransferDataFlavors()
		{
			DataFlavor[] flavors = new DataFlavor[1];
			flavors[0] = DataFlavor.imageFlavor;
			return flavors;
		}

		public boolean isDataFlavorSupported(DataFlavor flavor)
		{
			DataFlavor[] flavors = getTransferDataFlavors();
			for (int i = 0; i < flavors.length; i++)
			{
				if (flavor.equals(flavors[i]))
				{
					return true;
				}
			}
			return false;
		}
	}
}