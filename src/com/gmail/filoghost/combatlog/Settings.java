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

import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.List;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.collect.Lists;

import net.cubespace.yamler.InternalConverter;
import net.cubespace.yamler.InvalidConverterException;
import net.cubespace.yamler.YamlerConfig;
import net.cubespace.yamler.YamlerConfigurationException;
import net.cubespace.yamler.converter.Converter;
import wild.api.WildCommons;

public class Settings extends YamlerConfig {
	
	
	
	private int tagDuration = 15;
	public List<String> allowedCommands = Arrays.asList("spawn", "tpa");
	private List<String> allowedCommandsBlacklist = Arrays.asList("t home");
	
	public boolean killOnCombatLog = true;
	public boolean silent = false;
	
	public String messages_prefix = "&6[Combat Log] &7";
	public String messages_inCombat = "&cSei in combattimento, non disconnettere!";
	public String messages_noCombatAnymore = "&aNon sei più in combattimento.";
	public String messages_noCombat = "&aNon sei in combattimento.";
	public String messages_blockedCommand = "&cNon puoi usare questo comando in combattimento!";
	public String messages_killedForCombatLogSelf = "&cSei stato ucciso per aver disconnesso in combattimento!";
	public String messages_killedForCombatLogBroadcast = "&f{player} è stato ucciso per aver disconnesso in combattimento!";
	public String messages_petSentAway = "&cIl tuo pet è stato mandato via.";
	
	public String bar_inCombat = "&c&lSei in combattimento, non disconnettere!";
	public String bar_noCombatAnymore = "&a&lNon sei più in combattimento.";
	
	public transient List<String[]> allowedCommandsBlacklistArrays;
	public transient int tagDurationMillis;
	

	public Settings(JavaPlugin plugin) {
		super(plugin, "config.yml", "Configurazione del plugin CombatLog");
		try {
			this.addConverter(StringConverter.class);
		} catch (InvalidConverterException e) {
			e.printStackTrace();
		}
	}
	
	
	@Override
	public void init() throws YamlerConfigurationException {
		super.init();
		allowedCommandsBlacklistArrays = Lists.newArrayList();
		for (String blacklistedCommand : allowedCommandsBlacklist) {
			allowedCommandsBlacklistArrays.add(blacklistedCommand.toLowerCase().split(" "));
		}
		for (int i = 0; i < allowedCommands.size(); i++) {
			allowedCommands.set(i, allowedCommands.get(i).toLowerCase());
		}
		tagDurationMillis = tagDuration * 1000;
	}


	public static class StringConverter implements Converter {
		
		public StringConverter(InternalConverter internal) { }

		@Override
		public Object toConfig(Class<?> type, Object obj, ParameterizedType parameterizedType) throws Exception {
			return obj;
		}

		@Override
		public Object fromConfig(Class<?> type, Object section, ParameterizedType parameterizedType) throws Exception {
			return WildCommons.color(section.toString());
		}

		@Override
		public boolean supports(Class<?> type) {
			return type == String.class;
		}
	}
	
}
