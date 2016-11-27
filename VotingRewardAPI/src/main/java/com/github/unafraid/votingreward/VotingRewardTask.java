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
import com.github.unafraid.votingreward.api.VotingRewardAPIClient;
import com.github.unafraid.votingreward.api.objects.UserData;
import com.github.unafraid.votingreward.interfaceprovider.api.IPlayerInstance;
import com.github.unafraid.votingreward.model.RewardItem;

/**
 * @author UnAfraid
 */
public class VotingRewardTask
{
	public VotingRewardTask(IPlayerInstance player)
	{
		try
		{
			final long timeRemaining = VotingRewardCache.getInstance().getLastVotedTime(player);
			final String apiKey = VotingSettings.getInstance().getAPIKey();
			final UserData data = VotingRewardAPIClient.getInstance().getUserData(player.getIPAddress(), apiKey);
			if ((timeRemaining <= 0) && data.isVoted())
			{
				// Give him reward
				giveReward(player);
				
				// Mark down this reward as given
				VotingRewardCache.getInstance().markAsVotted(player);
				
				// Send message to player
				final String msg = VotingSettings.getInstance().getMessage(MessageType.ON_SUCCESS);
				if (msg != null)
				{
					player.sendMessage(msg);
				}
				
				// Notify to scripts
				VotingRewardInterfaceProvider.getInterface().onSuccessfulVote(player);
			}
			else
			{
				final String msg = VotingSettings.getInstance().getMessage(MessageType.ON_NOT_VOTED);
				if (msg != null)
				{
					player.sendMessage(msg);
				}
				
				// Notify to scripts
				VotingRewardInterfaceProvider.getInterface().onNotVoted(player);
			}
		}
		catch (Exception e)
		{
			final String msg = VotingSettings.getInstance().getMessage(MessageType.ON_ERROR);
			if (msg != null)
			{
				player.sendMessage(msg);
			}
			
			VotingRewardInterfaceProvider.getInterface().logError("Failed to read user data", e);
		}
	}
	
	private static void giveReward(IPlayerInstance activeChar)
	{
		for (RewardItem holder : VotingSettings.getInstance().getDroplist().calculateDrops())
		{
			activeChar.addItem(holder);
		}
	}
}
