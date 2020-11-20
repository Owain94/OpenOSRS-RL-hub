/*
 * Copyright (c) 2020, RKGman
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

package com.music.firebeats;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Range;

@ConfigGroup("firebeats")
public interface FireBeatsConfig extends Config
{
	@ConfigItem(
			keyName = "mute",
			name = "Mute",
			description = "Mutes everything.",
			position = 0
	)
	default boolean mute()
	{
		return false;
	}

	@ConfigItem(
			keyName = "mute",
			name = "Mute",
			description = "Mutes everything.",
			hidden = true
	)
	void setMute(boolean value);

	@Range(
			max = 100
	)
	@ConfigItem(
			keyName = "volume",
			name = "Volume",
			description = "Specify the volume.",
			position = 1
	)
	default int volume()
	{
		return 100;
	}

	@ConfigItem(
			keyName = "volume",
			name = "Volume",
			description = "",
			hidden = true
	)
	void setVolume(int val);

	@ConfigItem(
			keyName = "loop",
			name = "Loop",
			description = "Loop the remix track",
			position = 2
	)
	default boolean loop()
	{
		return true;
	}

	@ConfigItem(
			keyName = "loop",
			name = "Loop",
			description = "Loop the remix track",
			hidden = true
	)
	void setLoop(boolean value);

	@ConfigItem(
			keyName = "shuffleMode",
			name = "Shuffle Mode",
			description = "Shuffle tracks on command",
			position = 3
	)
	default boolean shuffleMode()
	{
		return false;
	}

	@ConfigItem(
			keyName = "shuffleMode",
			name = "Shuffle Mode",
			description = "Shuffle tracks on command",
			hidden = true
	)
	void setShuffleMode(boolean value);

	@ConfigItem(
			keyName = "playOriginalIfNoRemix",
			name = "Play original track if no remix",
			description = "Play the original track if the remix link is broken or does not exist.",
			position = 4
	)
	default boolean playOriginalIfNoRemix()
	{
		return true;
	}

	@ConfigItem(
			keyName = "playOriginalIfNoRemix",
			name = "Play original track if no remix",
			description = "Play the original track if the remix link is broken or does not exist.",
			hidden = true
	)
	void setPlayOriginalIfNoRemix(boolean value);

	@ConfigItem(
			keyName = "updateFromRepo",
			name = "Automatically Update From Repository",
			description = "On start, automatically pull a running list from the repository.",
			position = 5
	)
	default boolean updateFromRepo()
	{
		return true;
	}

	@ConfigItem(
			keyName = "updateFromRepo",
			name = "",
			description = "",
			hidden = true
	)
	void setUpdateFromRepo(boolean value);

	@ConfigItem(
			keyName = "showCurrentTrackName",
			name = "Show the current track name",
			description = "Displays the current track name without having to open the music tab.",
			position = 6
	)
	default boolean showCurrentTrackName() { return true; }

	@ConfigItem(
			keyName = "showCurrentTrackName",
			name = "Show the current track name",
			description = "Displays the current track name without having to open the music tab.",
			hidden = true
	)
	void setShowCurrentTrackName(boolean value);

	@Range(
			max = 100
	)
	@ConfigItem(
			keyName = "remixVolumeOffset",
			name = "Remix volume offset",
			description = "Amount to decrease volume of remix to match in-game volume.",
			position = 7
	)
	default int remixVolumeOffset()
	{
		return 45;
	}

	@ConfigItem(
			keyName = "remixVolumeOffset",
			name = "Remix volume offset",
			description = "",
			hidden = true
	)
	void setRemixVolumeOffset(int val);

	@ConfigItem(
			keyName = "musicVolume",
			name = "",
			description = "",
			hidden = true
	)
	default int getMusicVolume()
	{
		return 0;
	}

	@ConfigItem(
			keyName = "musicVolume",
			name = "",
			description = "",
			hidden = true
	)
	void setMusicVolume(int vol);

	@ConfigItem(
			keyName = "soundEffectVolume",
			name = "",
			description = "",
			hidden = true
	)
	default int getSoundEffectVolume()
	{
		return 0;
	}

	@ConfigItem(
			keyName = "soundEffectVolume",
			name = "",
			description = "",
			hidden = true
	)
	void setSoundEffectVolume(int val);

	@ConfigItem(
			keyName = "areaSoundEffectVolume",
			name = "",
			description = "",
			hidden = true
	)
	default int getAreaSoundEffectVolume()
	{
		return 0;
	}

	@ConfigItem(
			keyName = "areaSoundEffectVolume",
			name = "",
			description = "",
			hidden = true
	)
	void setAreaSoundEffectVolume(int vol);
}
