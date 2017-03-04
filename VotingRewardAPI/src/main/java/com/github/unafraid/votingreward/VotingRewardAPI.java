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

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.github.unafraid.votingreward.VotingSettings.MessageType;
import com.github.unafraid.votingreward.api.TozoneVotingAPIClient;
import com.github.unafraid.votingreward.api.VotingRewardAPIException;
import com.github.unafraid.votingreward.api.objects.TopzoneUserData;
import com.github.unafraid.votingreward.interfaceprovider.api.IOnVoicedCommandHandler;
import com.github.unafraid.votingreward.interfaceprovider.api.IPlayerInstance;
import com.github.unafraid.votingreward.model.RewardItem;

/**
 * @author UnAfraid
 */
public class VotingRewardAPI implements IOnVoicedCommandHandler, Runnable
{
	private static final String[] COMMANDS =
	{
		VotingSettings.getInstance().getVotingCommand(),
	};
	
	private final Queue<IPlayerInstance> _tasks = new ConcurrentLinkedQueue<>();
	private final TozoneVotingAPIClient _apiClient = new TozoneVotingAPIClient(VotingSettings.getInstance().getAPIKey());
	
	protected VotingRewardAPI()
	{
		VotingRewardInterface.getInstance().verify();
		VotingRewardInterface.getInstance().registerHandler(this);
		Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(this, 1, 1, TimeUnit.SECONDS);
	}
	
	@Override
	public boolean useVoicedCommand(String command, IPlayerInstance player, String params)
	{
		if ("127.0.0.1".equals(player.getIPAddress()))
		{
			player.sendMessage("Localhost is not supported.");
			return false;
		}
		
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
			VotingRewardInterface.getInstance().onInReuse(player, timeRemaining);
			return false;
		}
		
		//
		// Add rewarding task
		if (_tasks.contains(player))
		{
			player.sendMessage("You already requested reward, please wait..");
			return false;
		}
		player.sendMessage("You're rewarding request has been enqueued, verifying your vote please wait..");
		_tasks.offer(player);
		return true;
	}
	
	@Override
	public void run()
	{
		if (_tasks.isEmpty())
		{
			return;
		}
		
		while (!_tasks.isEmpty())
		{
			final IPlayerInstance player = _tasks.poll();
			if (player == null)
			{
				break;
			}
			
			try
			{
				final long timeRemaining = VotingRewardCache.getInstance().getLastVotedTime(player);
				final TopzoneUserData data = _apiClient.getTopzoneUserData(player.getIPAddress());
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
					VotingRewardInterface.getInstance().onSuccessfulVote(player);
				}
				else
				{
					final String msg = VotingSettings.getInstance().getMessage(MessageType.ON_NOT_VOTED);
					if (msg != null)
					{
						player.sendMessage(msg);
					}
					
					// Notify to scripts
					VotingRewardInterface.getInstance().onNotVoted(player);
				}
			}
			catch (VotingRewardAPIException | IOException e)
			{
				final String msg = VotingSettings.getInstance().getMessage(MessageType.ON_ERROR);
				if (msg != null)
				{
					player.sendMessage(msg);
				}
				
				VotingRewardInterface.getInstance().logError("Failed to read user data", e);
			}
		}
	}
	
	private static void giveReward(IPlayerInstance activeChar)
	{
		for (RewardItem holder : VotingSettings.getInstance().getDroplist().calculateDrops())
		{
			activeChar.addItem(holder);
		}
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
