/*
 * Copyright (c) 2020, Wild Adventure
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holder nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 * 4. Redistribution of this software in source or binary forms shall be free
 *    of all charges or fees to the recipient of this software.
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
package com.gmail.filoghost.combatlog;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.google.common.collect.Maps;

import wild.api.WildCommons;
import wild.api.util.UnitFormatter;

public class TagManager {
	
	private static Settings settings;
	private static Map<Player, TagInfo> taggedPlayers;
	
	
	public static void init(Plugin plugin, Settings settings) {
		TagManager.settings = settings;
		taggedPlayers = Maps.newHashMap();
		
		Bukkit.getScheduler().runTaskTimer(plugin, () -> {
			long now = System.currentTimeMillis();
			
			for (Iterator<Entry<Player, TagInfo>> iter = taggedPlayers.entrySet().iterator(); iter.hasNext();) {
				Entry<Player, TagInfo> entry = iter.next();
				final Player player = entry.getKey();
				TagInfo tagInfo = entry.getValue();
				
				long remainingTime = tagInfo.remainingTime(now, settings.tagDurationMillis);
				
				if (remainingTime > 0) {
					// Ancora attivo, manda l'action bar
					sendInCombatBar(player, remainingTime);
					
				} else {
					// Scaduto
					iter.remove();
					if (!settings.silent) {
						player.sendMessage(settings.messages_prefix + settings.messages_noCombatAnymore);
					}
					sendOutOfCombatBar(player);
				}
			}
			
		}, 0, 5L);
	}
	
	public static boolean isTagged(Player player) {
		TagInfo tagInfo = taggedPlayers.get(player);
		if (tagInfo == null) {
			return false;
		}
		
		return tagInfo.remainingTime(System.currentTimeMillis(), settings.tagDurationMillis) > 0;
	}

	/**
	 * @return true if player was not tagged before
	 */
	public static boolean setTagged(Player player) {
		long now = System.currentTimeMillis();
		TagInfo tagInfo = taggedPlayers.get(player);
		boolean newlyTagged;
		
				
		if (tagInfo == null) {
			tagInfo = new TagInfo(now);
			taggedPlayers.put(player, tagInfo);
			newlyTagged = true;
		} else {
			if (tagInfo.remainingTime(now, settings.tagDurationMillis) > 0) {
				newlyTagged = false;
			} else {
				newlyTagged = true;
			}

			tagInfo.updateTagStart(now);
		}
		
		if (newlyTagged) {
			if (!settings.silent) {
				player.sendMessage(settings.messages_prefix + settings.messages_inCombat);
			}
			if (EchoPetBridge.disablePet(player)) {
				if (!settings.silent) {
					player.sendMessage(settings.messages_prefix + settings.messages_petSentAway);
				}
			}
		}
		
		sendInCombatBar(player, settings.tagDurationMillis);
		return newlyTagged;
	}

	public static void restartIfTagged(Player player) {
		long now = System.currentTimeMillis();
		TagInfo tagInfo = taggedPlayers.get(player);
		
		if (tagInfo != null && tagInfo.remainingTime(now, settings.tagDurationMillis) > 0) {
			tagInfo.updateTagStart(now);
		}
	}

	public static void removeTagged(Player player) {
		taggedPlayers.remove(player);
	}
	
	private static void sendInCombatBar(Player player, long remainingMillis) {
		int secondsRemaining = (int) Math.ceil(remainingMillis / 1000.0);
		WildCommons.sendActionBar(player, settings.bar_inCombat.replace("{time}", UnitFormatter.formatMinutesOrSeconds(secondsRemaining)));
	}
	
	private static void sendOutOfCombatBar(Player player) {
		WildCommons.sendActionBar(player, settings.bar_noCombatAnymore);
	}


}
