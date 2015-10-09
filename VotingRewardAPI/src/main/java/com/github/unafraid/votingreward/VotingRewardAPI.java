/*
 * Copyright (C) 2014-2015 Vote Rewarding System
 * 
 * This file is part of Vote Rewarding System.
 * 
 * Vote Rewarding System is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Vote Rewarding System is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.github.unafraid.votingreward;

import com.github.unafraid.votingreward.VotingSettings.MessageType;
import com.github.unafraid.votingreward.interfaceprovider.api.IOnVoicedCommandHandler;
import com.github.unafraid.votingreward.interfaceprovider.api.IPlayerInstance;

/**
 * @author UnAfraid
 */
public class VotingRewardAPI implements IOnVoicedCommandHandler
{
	private static final String[] COMMANDS =
	{
		VotingSettings.getInstance().getVotingCommand(),
	};
	
	protected VotingRewardAPI()
	{
		VotingRewardInterfaceProvider.getInstance().getInterface().registerHandler(this);
		VotingSettings.getInstance();
	}
	
	@Override
	public boolean useVoicedCommand(String command, IPlayerInstance player, String params)
	{
		if (player.isGM() && "reload".equals(params))
		{
			VotingSettings.getInstance().load();
			player.sendMessage("Reloaded VotingReward.xml");
			return true;
		}
		
		final long timeRemaining = VotingRewardCache.getInstance().getLastVotedTime(player);
		
		// Make sure player haven't received reward already!
		if (timeRemaining > 0)
		{
			sendReEnterMessage(timeRemaining, player);
			VotingRewardInterfaceProvider.getInstance().getInterface().onInReuse(player, timeRemaining);
			return false;
		}
		
		VotingRewardInterfaceProvider.getInstance().getInterface().executeTask(new VotingRewardTask(player));
		return true;
	}
	
	private static void sendReEnterMessage(long time, IPlayerInstance player)
	{
		if (time > System.currentTimeMillis())
		{
			final long remainingTime = (time - System.currentTimeMillis()) / 1000;
			final int hours = (int) (remainingTime / 3600);
			final int minutes = (int) ((remainingTime % 3600) / 60);
			final int seconds = (int) ((remainingTime % 3600) % 60);
			
			String msg = VotingSettings.getInstance().getMessage(MessageType.ON_REUSE);
			if (msg != null)
			{
				msg = msg.replaceAll("%hours%", Integer.toString(hours));
				msg = msg.replaceAll("%mins%", Integer.toString(minutes));
				msg = msg.replaceAll("%secs%", Integer.toString(seconds));
				player.sendMessage(msg);
			}
			player.sendMessage(msg);
		}
	}
	
	@Override
	public String[] getVoicedCommandList()
	{
		return COMMANDS;
	}
	
	public static final VotingRewardAPI getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final VotingRewardAPI INSTANCE = new VotingRewardAPI();
	}
}