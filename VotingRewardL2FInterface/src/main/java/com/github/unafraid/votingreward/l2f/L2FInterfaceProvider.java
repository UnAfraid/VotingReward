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

import com.github.unafraid.votingreward.interfaceprovider.IVotingRewardInterface;
import com.github.unafraid.votingreward.interfaceprovider.IVotingRewardInterfaceProvider;

/**
 * @author UnAfraid
 */
public class L2FInterfaceProvider implements IVotingRewardInterfaceProvider
{
	private static final IVotingRewardInterface INTERFACE = new L2FInterface();
	
	@Override
	public double getVersion()
	{
		return 1.0;
	}
	
	@Override
	public String getName()
	{
		return getClass().getSimpleName();
	}
	
	@Override
	public String getAuthor()
	{
		return "UnAfraid";
	}
	
	@Override
	public IVotingRewardInterface getInterface()
	{
		return INTERFACE;
	}
}
