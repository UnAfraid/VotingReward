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

import java.io.File;
import java.sql.Connection;
import java.util.ServiceLoader;

import com.github.unafraid.votingreward.interfaceprovider.IVotingRewardInterface;
import com.github.unafraid.votingreward.interfaceprovider.IVotingRewardInterfaceProvider;
import com.github.unafraid.votingreward.interfaceprovider.api.IOnVoicedCommandHandler;
import com.github.unafraid.votingreward.interfaceprovider.api.IPlayerInstance;

/**
 * @author UnAfraid
 */
public class VotingRewardInterface implements IVotingRewardInterfaceProvider, IVotingRewardInterface
{
	private IVotingRewardInterfaceProvider _defaultProvider;
	
	protected VotingRewardInterface()
	{
		init();
	}
	
	public void init()
	{
		init(Thread.currentThread().getContextClassLoader());
	}
	
	public void init(ClassLoader classLoader)
	{
		ServiceLoader<IVotingRewardInterfaceProvider> serviceProvider = ServiceLoader.load(IVotingRewardInterfaceProvider.class, classLoader);
		double version = 0;
		for (IVotingRewardInterfaceProvider provider : serviceProvider)
		{
			if (provider.getVersion() > version)
			{
				_defaultProvider = provider;
			}
		}
		
		if (_defaultProvider != null)
		{
			_defaultProvider.getInterface().logInfo("VotingReward API Using interface: " + _defaultProvider.getName() + " version: " + _defaultProvider.getVersion() + " author: " + _defaultProvider.getAuthor());
		}
	}
	
	public void verify()
	{
		if (_defaultProvider == null)
		{
			throw new RuntimeException("Voting Reward Interface implementation was not provided!");
		}
	}
	
	@Override
	public double getVersion()
	{
		return _defaultProvider.getVersion();
	}
	
	@Override
	public String getName()
	{
		return _defaultProvider.getName();
	}
	
	@Override
	public String getAuthor()
	{
		return _defaultProvider.getAuthor();
	}
	
	@Override
	public void executeTask(Runnable runnable)
	{
		_defaultProvider.getInterface().executeTask(runnable);
	}
	
	@Override
	public File getDocumentRoot()
	{
		return _defaultProvider.getInterface().getDocumentRoot();
	}
	
	@Override
	public Connection getDatabaseConnection()
	{
		return _defaultProvider.getInterface().getDatabaseConnection();
	}
	
	@Override
	public void registerHandler(IOnVoicedCommandHandler handler)
	{
		_defaultProvider.getInterface().registerHandler(handler);
	}
	
	@Override
	public double getRandomDouble()
	{
		return _defaultProvider.getInterface().getRandomDouble();
	}
	
	@Override
	public int getRandomInt()
	{
		return _defaultProvider.getInterface().getRandomInt();
	}
	
	@Override
	public int getRandom(int min, int max)
	{
		return _defaultProvider.getInterface().getRandom(min, max);
	}
	
	@Override
	public void logInfo(String data)
	{
		_defaultProvider.getInterface().logInfo(data);
	}
	
	@Override
	public void logWarning(String data, Throwable t)
	{
		_defaultProvider.getInterface().logWarning(data, t);
	}
	
	@Override
	public void logError(String data, Throwable t)
	{
		_defaultProvider.getInterface().logError(data, t);
	}
	
	@Override
	public void onSuccessfulVote(IPlayerInstance player)
	{
		_defaultProvider.getInterface().onSuccessfulVote(player);
	}
	
	@Override
	public void onInReuse(IPlayerInstance player, long timeRemaining)
	{
		_defaultProvider.getInterface().onInReuse(player, timeRemaining);
	}
	
	@Override
	public void onNotVoted(IPlayerInstance player)
	{
		_defaultProvider.getInterface().onNotVoted(player);
	}
	
	@Override
	public IVotingRewardInterface getInterface()
	{
		return _defaultProvider.getInterface();
	}
	
	public static final VotingRewardInterface getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final VotingRewardInterface INSTANCE = new VotingRewardInterface();
	}
}
