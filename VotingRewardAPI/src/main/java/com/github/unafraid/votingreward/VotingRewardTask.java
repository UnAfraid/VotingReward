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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.github.unafraid.votingreward.VotingSettings.MessageType;
import com.github.unafraid.votingreward.interfaceprovider.api.IPlayerInstance;
import com.github.unafraid.votingreward.model.RewardItem;

/**
 * @author UnAfraid
 */
public class VotingRewardTask implements Runnable
{
	// Constants
	private static final String USER_AGENT = "L2TopZone";
	private static final String API_URL = "http://l2topzone.com/api.php?API_KEY=%s&SERVER_ID=%d&IP=%s";
	
	private final IPlayerInstance _player;
	
	public VotingRewardTask(IPlayerInstance player)
	{
		_player = player;
	}
	
	@Override
	public void run()
	{
		// Check if player votted
		if (isVotter(_player.getIPAddress()))
		{
			// Give him reward
			giveReward(_player);
			
			// Mark down this reward as given
			VotingRewardCache.getInstance().markAsVotted(_player);
			
			// Send message to player
			final String msg = VotingSettings.getInstance().getMessage(MessageType.ON_SUCCESS);
			if (msg != null)
			{
				_player.sendMessage(msg);
			}
			
			// Notify to scripts
			VotingRewardInterfaceProvider.getInstance().getInterface().onSuccessfulVote(_player);
		}
		else
		{
			final String msg = VotingSettings.getInstance().getMessage(MessageType.ON_NOT_VOTED);
			if (msg != null)
			{
				_player.sendMessage(msg);
			}
			
			// Notify to scripts
			VotingRewardInterfaceProvider.getInstance().getInterface().onNotVoted(_player);
		}
	}
	
	private static void giveReward(IPlayerInstance activeChar)
	{
		for (RewardItem holder : VotingSettings.getInstance().getDroplist().calculateDrops())
		{
			activeChar.addItem(holder);
		}
	}
	
	private static final boolean isVotter(String ip)
	{
		try
		{
			final URL obj = new URL(String.format(API_URL, VotingSettings.getInstance().getAPIKey(), VotingSettings.getInstance().getServerId(), ip));
			final HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			
			// add request header
			con.setRequestProperty("User-Agent", USER_AGENT);
			con.setConnectTimeout(10 * 1000);
			
			final int responseCode = con.getResponseCode();
			if (responseCode == 200) // OK
			{
				final StringBuffer sb = new StringBuffer();
				try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream())))
				{
					String inputLine;
					while ((inputLine = in.readLine()) != null)
					{
						sb.append(inputLine);
					}
				}
				return sb.toString().toUpperCase().equals("TRUE");
			}
		}
		catch (Exception e)
		{
			VotingRewardInterfaceProvider.getInstance().getInterface().logError("Failed to establish connection with voting provider", e);
		}
		return false;
	}
}
