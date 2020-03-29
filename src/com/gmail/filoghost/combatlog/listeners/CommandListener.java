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
package com.gmail.filoghost.combatlog.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import com.gmail.filoghost.combatlog.CombatLog;
import com.gmail.filoghost.combatlog.TagManager;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CommandListener implements Listener {
	
	private CombatLog plugin;
	
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
		String[] command = event.getMessage().substring(1).toLowerCase().split(" ");
		if (TagManager.isTagged(event.getPlayer()) && !isAllowedCommand(command)) {
			event.setCancelled(true);
			event.getPlayer().sendMessage(plugin.getSettings().messages_prefix + plugin.getSettings().messages_blockedCommand);
		}
	}
	
	private boolean isAllowedCommand(String[] command) {
		if (!plugin.getSettings().allowedCommands.contains(command[0])) {
			return false;
		}
			
		for (String[] blacklistedCommand : plugin.getSettings().allowedCommandsBlacklistArrays) {
			if (matchesPartial(command, blacklistedCommand)) {
				return false;
			}
		}
			
		return true;
	}
	
	private boolean matchesPartial(String[] command, String[] check) {
		if (command.length < check.length) {
			return false; // Can't match
		}
		
		for (int i = 0; i < check.length; i++) {
			if (!command[i].equals(check[i])) {
				return false;
			}
		}
		
		return true;
	}

}
