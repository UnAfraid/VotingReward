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
package com.github.unafraid.votingreward.l2j;

import com.github.unafraid.votingreward.interfaceprovider.api.IOnVoicedCommandHandler;
import com.l2jserver.gameserver.handler.IVoicedCommandHandler;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

/**
 * @author UnAfraid
 */
public class L2JVoicedHandler implements IVoicedCommandHandler
{
	private final IOnVoicedCommandHandler _handler;
	
	public L2JVoicedHandler(IOnVoicedCommandHandler handler)
	{
		_handler = handler;
	}
	
	@Override
	public boolean useVoicedCommand(String command, L2PcInstance activeChar, String params)
	{
		return _handler.useVoicedCommand(command, new L2JPlayer(activeChar), params);
	}
	
	@Override
	public String[] getVoicedCommandList()
	{
		return _handler.getVoicedCommandList();
	}
}
