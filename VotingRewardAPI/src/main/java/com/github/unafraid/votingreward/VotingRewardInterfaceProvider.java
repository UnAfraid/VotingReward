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
	private static final ServiceLoader<IVotingRewardInterfaceProvider> PROVIDER;
	private static IVotingRewardInterfaceProvider DEFAULT_PROVIDER;
	
	static
	{
		PROVIDER = ServiceLoader.load(IVotingRewardInterfaceProvider.class);
		double version = 0;
		for (IVotingRewardInterfaceProvider provider : PROVIDER)
		{
			if (provider.getVersion() > version)
			{
				DEFAULT_PROVIDER = provider;
			}
		}
		
		if (DEFAULT_PROVIDER == null)
		{
			throw new RuntimeException("Voting Reward Interface implementation was not provided!");
		}
		DEFAULT_PROVIDER.getInterface().logInfo("VotingReward API Using interface: " + DEFAULT_PROVIDER.getName() + " version: " + DEFAULT_PROVIDER.getVersion() + " author: " + DEFAULT_PROVIDER.getAuthor());
	}
	
	protected VotingRewardInterfaceProvider()
	{
	}
	
	public static IVotingRewardInterfaceProvider getProvider()
	{
		return DEFAULT_PROVIDER;
	}
	
	public static IVotingRewardInterface getInterface()
	{
		return DEFAULT_PROVIDER.getInterface();
	}
	
	public IVotingRewardInterfaceProvider getProvider(String name)
	{
		for (IVotingRewardInterfaceProvider provider : PROVIDER)
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
		for (IVotingRewardInterfaceProvider provider : PROVIDER)
		{
			providers.add(provider);
		}
		return providers;
	}
}
