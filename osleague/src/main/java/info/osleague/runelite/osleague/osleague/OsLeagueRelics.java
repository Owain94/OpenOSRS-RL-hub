package info.osleague.runelite.osleague.osleague;

import com.google.gson.annotations.SerializedName;
import info.osleague.runelite.osleague.Relic;
import java.util.List;

public class OsLeagueRelics
{
	@SerializedName("0")
	public OsLeagueRelic relic1 = null;
	@SerializedName("1")
	public OsLeagueRelic relic2 = null;
	@SerializedName("2")
	public OsLeagueRelic relic3 = null;
	@SerializedName("3")
	public OsLeagueRelic relic4 = null;
	@SerializedName("4")
	public OsLeagueRelic relic5 = null;
	@SerializedName("5")
	public OsLeagueRelic relic6 = null;

	public OsLeagueRelics(List<Relic> relics)
	{
		for (Relic relic : relics)
		{
			int tierId = relic.getTierId();
			switch (tierId)
			{
				case 0:
					relic1 = new OsLeagueRelic(relic);
					continue;
				case 1:
					relic2 = new OsLeagueRelic(relic);
					continue;
				case 2:
					relic3 = new OsLeagueRelic(relic);
					continue;
				case 3:
					relic4 = new OsLeagueRelic(relic);
					continue;
				case 4:
					relic5 = new OsLeagueRelic(relic);
					continue;
				case 5:
					relic6 = new OsLeagueRelic(relic);
					continue;
			}
		}
	}
}
