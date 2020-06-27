package net.runelite.client.plugins.banktabnames;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.events.ScriptPostFired;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginType;
import org.pf4j.Extension;

@Extension
@PluginDescriptor(
	name = "Bank Tab Names",
	description = "Customize your bank tabs with custom styled names",
	tags = {"bank", "tab", "name", "custom", "edit", "psyda"},
	enabledByDefault = false,
	type = PluginType.MISCELLANEOUS
)
@Slf4j
public class BankTabNamesPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	@Inject
	private BankTabNamesConfig config;

	private static final int TAB_MAX_LENGTH = 15;

	@Provides
	BankTabNamesConfig getConfig(ConfigManager configManager)
	{
		return configManager.getConfig(BankTabNamesConfig.class);
	}

	@Override
	protected void startUp() throws Exception
	{
		clientThread.invoke(this::replaceBankTabNumbers);
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		replaceBankTabNumbers();
	}

	@Subscribe
	public void onScriptPostFired(ScriptPostFired scriptPostFired)
	{
		if (scriptPostFired.getScriptId() == 504 || scriptPostFired.getScriptId() == 276 || scriptPostFired.getScriptId() == 3275) // 504 and 276 = Bank tabs being reloaded | 3275 = Bank Settings menu closing
		{
			replaceBankTabNumbers();

		}
	}

	private void replaceBankTabNumbers()
	{

		final Widget bankTabCont = client.getWidget(WidgetInfo.BANK_TAB_CONTAINER);
		if (bankTabCont != null)
		{
			//Checking if Bank tab is on the "First item in Tab" OR the "Roman Numerals" Modes.
			if (bankTabCont.getChild(11).getType() == 5 || bankTabCont.getChild(11).getHeight() == 35)
			{
				for (int i = 11; i < 20; i++) // This loops over checking for Different variables for each bank tab set in either mode and sets them accordingly so that each mode looks identical with tags on.
				{
					Widget bankTabChildren = bankTabCont.getChild(i);
					int getChildX = (bankTabChildren.getOriginalX());
					int widgetType = bankTabCont.getChild(19).getType();
					if (widgetType == 4 && bankTabCont.getChild(19).getHeight() != 35)
					{
						continue;
					}
					bankTabChildren.setOpacity(0);
					bankTabChildren.setOriginalY(0);
					bankTabChildren.setXTextAlignment(1);
					bankTabChildren.setYTextAlignment(1);
					bankTabChildren.setOriginalWidth(41);
					bankTabChildren.setOriginalHeight(40);
					bankTabChildren.setOriginalHeight(40);
					bankTabChildren.setItemId(-1);
					bankTabChildren.setType(4);
					bankTabChildren.setTextShadowed(true);
					clientThread.invoke(bankTabChildren::revalidate);
					if (widgetType != 4)
					{
						bankTabChildren.setOriginalX(getChildX - 3);
						clientThread.invoke(bankTabChildren::revalidate);
					}
				}
				replaceText();
				clientThread.invokeLater(bankTabCont::revalidate);
				return;
			}
			replaceText();
			clientThread.invokeLater(bankTabCont::revalidate);
		}
	}


	private void replaceText()
	{
		final Widget bankTabCont = client.getWidget(WidgetInfo.BANK_TAB_CONTAINER);
		if (bankTabCont != null)
		{
			bankTabCont.getChild(11).setText(config.tab1Name());
			bankTabCont.getChild(11).setText(config.tab1Name());
			bankTabCont.getChild(12).setText(config.tab2Name());
			bankTabCont.getChild(13).setText(config.tab3Name());
			bankTabCont.getChild(14).setText(config.tab4Name());
			bankTabCont.getChild(15).setText(config.tab5Name());
			bankTabCont.getChild(16).setText(config.tab6Name());
			bankTabCont.getChild(17).setText(config.tab7Name());
			bankTabCont.getChild(18).setText(config.tab8Name());
			bankTabCont.getChild(19).setText(config.tab9Name());

			bankTabCont.getChild(11).setFontId(config.bankFont1().tabFontId);
			bankTabCont.getChild(12).setFontId(config.bankFont2().tabFontId);
			bankTabCont.getChild(13).setFontId(config.bankFont3().tabFontId);
			bankTabCont.getChild(14).setFontId(config.bankFont4().tabFontId);
			bankTabCont.getChild(15).setFontId(config.bankFont5().tabFontId);
			bankTabCont.getChild(16).setFontId(config.bankFont6().tabFontId);
			bankTabCont.getChild(17).setFontId(config.bankFont7().tabFontId);
			bankTabCont.getChild(18).setFontId(config.bankFont8().tabFontId);
			bankTabCont.getChild(19).setFontId(config.bankFont9().tabFontId);

			bankTabCont.getChild(11).setTextColor(config.bankFontColor1().getRGB());
			bankTabCont.getChild(12).setTextColor(config.bankFontColor2().getRGB());
			bankTabCont.getChild(13).setTextColor(config.bankFontColor3().getRGB());
			bankTabCont.getChild(14).setTextColor(config.bankFontColor4().getRGB());
			bankTabCont.getChild(15).setTextColor(config.bankFontColor5().getRGB());
			bankTabCont.getChild(16).setTextColor(config.bankFontColor6().getRGB());
			bankTabCont.getChild(17).setTextColor(config.bankFontColor7().getRGB());
			bankTabCont.getChild(18).setTextColor(config.bankFontColor8().getRGB());
			bankTabCont.getChild(19).setTextColor(config.bankFontColor9().getRGB());

			if (!config.disableMainTabName())
			{
				bankTabCont.getChild(10).setType(4);
				bankTabCont.getChild(10).setOpacity(0);
				bankTabCont.getChild(10).setOriginalY(0);
				bankTabCont.getChild(10).setXTextAlignment(1);
				bankTabCont.getChild(10).setYTextAlignment(1);
				bankTabCont.getChild(10).setOriginalWidth(41);
				bankTabCont.getChild(10).setOriginalHeight(40);
				bankTabCont.getChild(10).setText(config.tab0Name());
				bankTabCont.getChild(10).setTextColor(config.bankFontColor0().getRGB());
				bankTabCont.getChild(10).setFontId(config.bankFont0().tabFontId);
				clientThread.invoke(bankTabCont.getChild(10)::revalidate);
			}
			if (config.disableMainTabName())
			{
				bankTabCont.getChild(10).setOpacity(20);
				bankTabCont.getChild(10).setType(5);
				bankTabCont.getChild(10).setOriginalWidth(36);
				bankTabCont.getChild(10).setOriginalHeight(32);
				bankTabCont.getChild(10).setOriginalY(4);
				clientThread.invoke(bankTabCont.getChild(10)::revalidate);
			}
		}
	}
}
