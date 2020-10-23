package hjdarnel.emojipalette;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.ui.components.PluginErrorPanel;

@Slf4j
class EmojiPanel extends PluginPanel
{

	void init() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException, ClassNotFoundException
	{
		setBorder(new EmptyBorder(10, 10, 10, 10));

		final PluginErrorPanel errorPanel = new PluginErrorPanel();
		errorPanel.setBorder(new EmptyBorder(10, 25, 10, 25));
		errorPanel.setContent("Emoji Palette", "Hover over an emoji to view the text trigger");
		add(errorPanel, BorderLayout.NORTH);

		JPanel emojiPanel = new JPanel();
		emojiPanel.setLayout(new GridLayout(10, 3));
		emojiPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		emojiPanel.setBorder(new EmptyBorder(5, 0, 5, 0));

		// get Emoji.values(), Emoji.trigger, and Emoji.loadImage() accessible using reflection
		Class<Enum<?>> emojisClass = (Class<Enum<?>>) getClass().getClassLoader().loadClass("net.runelite.client.plugins.emojis.Emoji");
		Method valuesMethod = emojisClass.getDeclaredMethod("values");
		valuesMethod.setAccessible(true);
		Field triggerField = emojisClass.getDeclaredField("trigger");
		triggerField.setAccessible(true);
		Method loadImageMethod = emojisClass.getDeclaredMethod("loadImage");
		loadImageMethod.setAccessible(true);

		for (final Enum<?> emoji : (Enum<?>[]) valuesMethod.invoke(null))
		{
			JPanel panel = makeEmojiPanel(emoji, triggerField, loadImageMethod);
			emojiPanel.add(panel);
		}

		add(emojiPanel);
	}

	// Builds a JPanel displaying an icon with tooltip
	private JPanel makeEmojiPanel(Enum<?> emoji, Field triggerField, Method loadImageMethod) throws IllegalAccessException, InvocationTargetException
	{
		JLabel label = new JLabel();
		label.setToolTipText(EmojiPalettePlugin.unescapeTags((String) triggerField.get(emoji)));
		label.setIcon(new ImageIcon((Image) loadImageMethod.invoke(emoji)));

		JPanel emojiPanel = new JPanel();
		emojiPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		emojiPanel.setBorder(new EmptyBorder(2, 0, 2, 0));
		emojiPanel.add(label);

		return emojiPanel;
	}
}