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

import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import com.gmail.filoghost.combatlog.CombatLog;
import com.gmail.filoghost.combatlog.TagManager;

import lombok.AllArgsConstructor;
import lombok.Getter;
import wild.api.util.CaseInsensitiveSet;

@AllArgsConstructor
public class JoinQuitDeathListener implements Listener {
	
	private CombatLog plugin;
	@Getter private Set<String> killedForLoggingPlayernames;

	
	public JoinQuitDeathListener(CombatLog plugin) {
		this.plugin = plugin;
		this.killedForLoggingPlayernames = new CaseInsensitiveSet<>();
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		final Player player = event.getPlayer();

		if (killedForLoggingPlayernames.contains(player.getName())) {
			killedForLoggingPlayernames.remove(player.getName());
			
			Bukkit.getScheduler().runTaskLater(plugin, () -> {
				player.sendMessage(plugin.getSettings().messages_prefix + plugin.getSettings().messages_killedForCombatLogSelf);
			}, 10L);
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();

		if (TagManager.isTagged(player) && plugin.getSettings().killOnCombatLog) {

			if (!plugin.getSettings().silent) {
				plugin.broadcastMessage(plugin.getSettings().messages_prefix + plugin.getSettings().messages_killedForCombatLogBroadcast.replace("{player}", player.getName()));
			}
			dropPunishment(player);
			player.setHealth(0.0);
			player.spigot().respawn();
			killedForLoggingPlayernames.add(player.getName());
		}
		
		TagManager.removeTagged(player);
	}
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		TagManager.removeTagged(event.getEntity());
	}

	public void dropPunishment(Player player) {
		ItemStack[] stacks = player.getInventory().getContents().clone();
		player.getInventory().clear();
		
		for (ItemStack stack : stacks) {
			if (stack != null && stack.getType() != Material.AIR) {
				player.getLocation().getWorld().dropItemNaturally(player.getLocation(), stack);
			}
		}

		ItemStack[] armorstacks = player.getInventory().getArmorContents().clone();
		player.getInventory().setArmorContents(null);
		for (ItemStack stack : armorstacks) {
			if (stack != null && stack.getType() != Material.AIR) {
				player.getLocation().getWorld().dropItemNaturally(player.getLocation(), stack);
			}
		}

		player.setExp(0.0F);
		for (int i = 0; i < player.getLevel(); i++) {
			player.getLocation().getWorld().spawn(player.getLocation(), ExperienceOrb.class).setExperience(2 * i + 1);
		}
		player.setLevel(0);
	}
	

}
