/*
 * Copyright (c) 2020, Lotto <https://github.com/devLotto>
 * Copyright (c) 2020, Psikoi <https://github.com/psikoi>
 * Copyright (c) 2020, Henry Darnell <https://github.com/hjdarnel>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.client.plugins.emojipalette;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.plugins.emojis.Emoji;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.ui.components.PluginErrorPanel;
import net.runelite.client.util.ImageUtil;

@Slf4j
class EmojiPanel extends PluginPanel
{
	private static final Pattern TAG_REGEXP = Pattern.compile("<[^>]*>");
	private EmojiPalettePlugin plugin;

	void init(final EmojiPalettePlugin plugin)
	{
		this.plugin = plugin;

		setBorder(new EmptyBorder(10, 10, 10, 10));

		final PluginErrorPanel errorPanel = new PluginErrorPanel();
		errorPanel.setBorder(new EmptyBorder(10, 25, 10, 25));
		errorPanel.setContent("Emoji Palette", "Hover over an emoji to view the text trigger, click an emoji to insert it in your chatbox");
		add(errorPanel, BorderLayout.NORTH);

		JPanel emojiPanel = new JPanel();
		emojiPanel.setLayout(new GridLayout(11, 3));
		emojiPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		emojiPanel.setBorder(new EmptyBorder(5, 0, 5, 0));

		for (final Emoji emoji : Emoji.getAllEmoji())
		{
			JPanel panel = makeEmojiPanel(emoji);
			emojiPanel.add(panel);
		}

		add(emojiPanel);
	}

	/* Builds a JPanel displaying an icon with tooltip */
	private JPanel makeEmojiPanel(Emoji emoji)
	{
		JLabel label = new JLabel();
		label.setToolTipText(unescapeTags(emoji.trigger));
		label.setIcon(new ImageIcon(ImageUtil.getResourceStreamFromClass(Emoji.class, emoji.name().toLowerCase() + ".png")));
		label.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent e)
			{
				if (plugin != null)
				{
					plugin.addEmoji(unescapeTags(emoji.trigger));
				}

			}
		});

		JPanel skillPanel = new JPanel();
		skillPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		skillPanel.setBorder(new EmptyBorder(2, 0, 2, 0));
		skillPanel.add(label);

		return skillPanel;
	}

	/**
	 * Unescape a string for widgets, replacing &lt;lt&gt; and &lt;gt&gt; with their unescaped counterparts
	 */
	public static String unescapeTags(String str)
	{
		StringBuffer out = new StringBuffer(str.length());
		Matcher matcher = TAG_REGEXP.matcher(str);

		while (matcher.find())
		{
			matcher.appendReplacement(out, "");
			String match = matcher.group(0);
			switch (match)
			{
				case "<lt>":
					out.append("<");
					break;
				case "<gt>":
					out.append(">");
					break;
				case "<br>":
					out.append("\n");
					break;
				default:
					out.append(match);
			}
		}
		matcher.appendTail(out);

		return out.toString();
	}
}