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

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

import com.github.unafraid.votingreward.interfaceprovider.IVotingRewardInterface;
import com.github.unafraid.votingreward.interfaceprovider.IVotingRewardInterfaceProvider;

/**
 * @author UnAfraid
 */
public class VotingRewardInterfaceProvider
{
	private final ServiceLoader<IVotingRewardInterfaceProvider> _provider;
	private IVotingRewardInterfaceProvider _defaultProvider;
	
	protected VotingRewardInterfaceProvider()
	{
		_provider = ServiceLoader.load(IVotingRewardInterfaceProvider.class);
		double version = 0;
		for (IVotingRewardInterfaceProvider provider : _provider)
		{
			if (provider.getVersion() > version)
			{
				_defaultProvider = provider;
			}
		}
		
		if (_defaultProvider == null)
		{
			throw new RuntimeException("Voting Reward Interface implementation was not provided!");
		}
		_defaultProvider.getInterface().logInfo("VotingReward API Using interface: " + _defaultProvider.getName() + " version: " + _defaultProvider.getVersion() + " author: " + _defaultProvider.getAuthor());
	}
	
	public IVotingRewardInterfaceProvider getProvider()
	{
		return _defaultProvider;
	}
	
	public IVotingRewardInterface getInterface()
	{
		return _defaultProvider.getInterface();
	}
	
	public IVotingRewardInterfaceProvider getProvider(String name)
	{
		for (IVotingRewardInterfaceProvider provider : _provider)
		{
			if (provider.getName().equalsIgnoreCase(name))
			{
				return provider;
			}
		}
		return null;
	}
	
	public List<IVotingRewardInterfaceProvider> getProviders()
	{
		final List<IVotingRewardInterfaceProvider> providers = new ArrayList<>();
		for (IVotingRewardInterfaceProvider provider : _provider)
		{
			providers.add(provider);
		}
		return providers;
	}
	
	public static VotingRewardInterfaceProvider getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final VotingRewardInterfaceProvider INSTANCE = new VotingRewardInterfaceProvider();
	}
}
