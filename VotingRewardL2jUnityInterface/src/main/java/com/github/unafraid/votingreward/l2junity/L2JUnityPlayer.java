/*
 * Copyright (C) 2004-2015 Vote Rewarding System
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
package com.github.unafraid.votingreward.l2junity;

import org.l2junity.gameserver.model.actor.instance.PlayerInstance;

import com.github.unafraid.votingreward.interfaceprovider.api.IPlayerInstance;
import com.github.unafraid.votingreward.interfaceprovider.model.RewardItemHolder;

/**
 * @author UnAfraid
 */
public class L2JUnityPlayer implements IPlayerInstance
{
	private final PlayerInstance _player;
	
	public L2JUnityPlayer(PlayerInstance player)
	{
		_player = player;
	}
	
	@Override
	public int getObjectId()
	{
		return _player.getObjectId();
	}
	
	@Override
	public String getName()
	{
		return _player.getName();
	}
	
	@Override
	public boolean isGM()
	{
		return _player.isGM();
	}
	
	@Override
	public void sendMessage(String message)
	{
		_player.sendMessage(message);
	}
	
	@Override
	public void addItem(RewardItemHolder holder)
	{
		_player.addItem("Reward", holder.getId(), holder.getCount(), null, true);
	}
	
	@Override
	public String getAccountName()
	{
		return _player.getAccountName();
	}
	
	@Override
	public String getIPAddress()
	{
		return _player.getClient().getConnectionAddress().getHostAddress();
	}
	
	@Override
	public String getHWID()
	{
		// Implement if lameguard is present
		return null;
	}
	
	@Override
	public boolean isHWIDSupported()
	{
		return getHWID() != null;
	}
}
