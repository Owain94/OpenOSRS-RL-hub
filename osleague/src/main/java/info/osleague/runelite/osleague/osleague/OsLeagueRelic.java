package info.osleague.runelite.osleague.osleague;

import com.google.gson.annotations.SerializedName;
import info.osleague.runelite.osleague.Relic;

public class OsLeagueRelic
{
	public boolean passive = true;
	@SerializedName("relic")
	public int relicId;

	public OsLeagueRelic(Relic relic)
	{
		this.relicId = relic.getRelicId();
	}
}
