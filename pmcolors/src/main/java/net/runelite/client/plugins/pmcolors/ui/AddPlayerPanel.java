/*
 * Copyright (c) 2020, PresNL
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

/*
 * The Panel code has taken a lot of inspiration from the ScreenMarkers plugin by Psikoi so credits to him
 */

package net.runelite.client.plugins.pmcolors.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import net.runelite.client.plugins.pmcolors.PMColorsPlugin;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.components.FlatTextField;
import net.runelite.client.ui.components.colorpicker.RuneliteColorPicker;
import net.runelite.client.util.ImageUtil;

public class AddPlayerPanel extends JPanel
{
	private PMColorsPlugin plugin;

	private static final ImageIcon CONFIRM_ICON;
	private static final ImageIcon CONFIRM_HOVER_ICON;
	private static final ImageIcon CANCEL_ICON;
	private static final ImageIcon CANCEL_HOVER_ICON;

	private final FlatTextField nameInput = new FlatTextField();
	private final JLabel colorInput = new JLabel();
	private final JLabel confirmLabel = new JLabel();
	private final JLabel cancelLabel = new JLabel();

	private Color selectedColor;

	static
	{
		CONFIRM_ICON = new ImageIcon(ImageUtil.getResourceStreamFromClass(PMColorsPlugin.class, "confirm_icon.png"));
		CANCEL_ICON = new ImageIcon(ImageUtil.getResourceStreamFromClass(PMColorsPlugin.class, "cancel_icon.png"));

		final BufferedImage confirmIcon = ImageUtil.bufferedImageFromImage(CONFIRM_ICON.getImage());
		CONFIRM_HOVER_ICON = new ImageIcon(ImageUtil.alphaOffset(confirmIcon, 0.54f));
		CANCEL_HOVER_ICON = new ImageIcon(ImageUtil.alphaOffset(ImageUtil.bufferedImageFromImage(CANCEL_ICON.getImage()), 0.6f));
	}

	public AddPlayerPanel(PMColorsPlugin plugin)
	{
		this.plugin = plugin;
		setLayout(new BorderLayout());
		setBorder(new EmptyBorder(5, 5, 5, 5));

		this.setBackground(ColorScheme.DARKER_GRAY_COLOR);

		// use an empty unicode character in the text to allow the user called player name to be highlighted too
		nameInput.setText("Player name\u00A0");
		nameInput.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
		nameInput.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		nameInput.setPreferredSize(new Dimension(0, 24));
		nameInput.getTextField().addFocusListener(new FocusListener()
		{
			@Override
			public void focusGained(FocusEvent e)
			{
				if (nameInput.getText().equals("Player name\u00A0"))
				{
					nameInput.setText("");
					nameInput.setForeground(Color.WHITE);
				}
			}

			@Override
			public void focusLost(FocusEvent e)
			{
				if (nameInput.getText().isEmpty())
				{
					nameInput.setText("Player name\u00A0");
					nameInput.setForeground(ColorScheme.LIGHT_GRAY_COLOR);
				}
			}
		});
		selectedColor = plugin.getDefaultColor();

		colorInput.setText(colorToHex(plugin.getDefaultColor()));
		colorInput.setBackground(selectedColor.darker());

		colorInput.setForeground(Color.WHITE);
		colorInput.setHorizontalAlignment(JLabel.CENTER);
		colorInput.setOpaque(true);
		colorInput.setPreferredSize(new Dimension(75, 24));
		colorInput.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent mouseEvent)
			{
				openPlayerColorPicker();
			}

			@Override
			public void mouseEntered(MouseEvent mouseEvent)
			{
			}

			@Override
			public void mouseExited(MouseEvent mouseEvent)
			{
			}
		});

		JPanel actionsContainer = new JPanel(new GridLayout(1, 2, 8, 0));
		actionsContainer.setBackground(ColorScheme.DARKER_GRAY_COLOR);

		confirmLabel.setIcon(CONFIRM_ICON);
		confirmLabel.setToolTipText("Confirm and save");
		confirmLabel.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent mouseEvent)
			{
				/* If the confirm button is not locked */
				if (!nameInput.getText().equals("Player name\u00A0"))
				{
					plugin.finishCreation(false, nameInput.getText(), selectedColor);
					reset();
				}
			}

			@Override
			public void mouseEntered(MouseEvent mouseEvent)
			{
				confirmLabel.setIcon(CONFIRM_HOVER_ICON);
			}

			@Override
			public void mouseExited(MouseEvent mouseEvent)
			{
				confirmLabel.setIcon(CONFIRM_ICON);
			}
		});

		JLabel cancelLabel = new JLabel(CANCEL_ICON);
		cancelLabel.setToolTipText("Cancel");
		cancelLabel.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent mouseEvent)
			{
				plugin.finishCreation(true, null, null);
				reset();
			}

			@Override
			public void mouseEntered(MouseEvent mouseEvent)
			{
				cancelLabel.setIcon(CANCEL_HOVER_ICON);
			}

			@Override
			public void mouseExited(MouseEvent mouseEvent)
			{
				cancelLabel.setIcon(CANCEL_ICON);
			}
		});
		actionsContainer.add(confirmLabel);
		actionsContainer.add(cancelLabel);

		JPanel inputContainer = new JPanel(new GridLayout(1, 2, 8, 0));
		inputContainer.setBorder(new EmptyBorder(0, 0, 0, 8));
		inputContainer.setBackground(ColorScheme.DARKER_GRAY_COLOR);

		inputContainer.add(nameInput);
		inputContainer.add(colorInput);

		add(inputContainer, BorderLayout.CENTER);
		add(actionsContainer, BorderLayout.EAST);
	}


	public void reset()
	{
		nameInput.setText("Player name\u00A0");
		nameInput.setForeground(ColorScheme.LIGHT_GRAY_COLOR);

		selectedColor = plugin.getDefaultColor();

		colorInput.setText(colorToHex(plugin.getDefaultColor()));
		colorInput.setBackground(selectedColor.darker());
	}

	private String colorToHex(Color color)
	{
		return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
	}

	private void openPlayerColorPicker()
	{
		RuneliteColorPicker colorPicker = plugin.getColorPickerManager().create(
			SwingUtilities.windowForComponent(this),
			colorInput.getBackground(),
			nameInput.getText() + " highlight color",
			true);
		colorPicker.setLocation(getLocationOnScreen());
		colorPicker.setOnColorChange(c ->
		{
			selectedColor = c;
			colorInput.setBackground(selectedColor.darker());
			colorInput.setText(colorToHex(selectedColor));
		});
		colorPicker.setOnClose(c -> plugin.updateConfig());
		colorPicker.setVisible(true);
	}
}