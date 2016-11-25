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
package com.github.unafraid.votingreward.l2f;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.unafraid.votingreward.interfaceprovider.IVotingRewardInterface;
import com.github.unafraid.votingreward.interfaceprovider.api.IOnVoicedCommandHandler;
import com.github.unafraid.votingreward.interfaceprovider.api.IPlayerInstance;

import l2f.commons.util.Rnd;
import l2f.gameserver.Config;
import l2f.gameserver.ThreadPoolManager;
import l2f.gameserver.database.DatabaseFactory;
import l2f.gameserver.handler.voicecommands.VoicedCommandHandler;

/**
 * @author UnAfraid
 */
public class L2FInterface implements IVotingRewardInterface
{
	private static final Logger LOG = LoggerFactory.getLogger(L2FInterface.class);
	
	@Override
	public void executeTask(Runnable runnable)
	{
		ThreadPoolManager.getInstance().execute(runnable);
	}
	
	@Override
	public File getDocumentRoot()
	{
		return Config.DATAPACK_ROOT;
	}
	
	@Override
	public Connection getDatabaseConnection()
	{
		try
		{
			return DatabaseFactory.getInstance().getConnection();
		}
		catch (SQLException e)
		{
			LOG.warn("Failed to obtain database connection", e);
		}
		return null;
	}
	
	@Override
	public void registerHandler(IOnVoicedCommandHandler handler)
	{
		VoicedCommandHandler.getInstance().registerVoicedCommandHandler(new L2FVoicedHandler(handler));
		
	}
	
	@Override
	public double getRandomDouble()
	{
		return Rnd.nextDouble();
	}
	
	@Override
	public int getRandomInt()
	{
		return Rnd.nextInt();
	}
	
	@Override
	public int getRandom(int min, int max)
	{
		return Rnd.get(min, max);
	}
	
	@Override
	public void logInfo(String data)
	{
		LOG.info(data);
	}
	
	@Override
	public void logWarning(String data, Throwable t)
	{
		LOG.warn(data, t);
	}
	
	@Override
	public void logError(String data, Throwable t)
	{
		LOG.error(data, t);
	}
	
	@Override
	public void onSuccessfulVote(IPlayerInstance player)
	{
		
	}
	
	@Override
	public void onInReuse(IPlayerInstance player, long timeRemaining)
	{
		
	}
	
	@Override
	public void onNotVoted(IPlayerInstance player)
	{
		
	}
}
