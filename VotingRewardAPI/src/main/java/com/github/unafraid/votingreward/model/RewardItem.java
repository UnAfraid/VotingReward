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
package com.github.unafraid.votingreward.model;

import com.github.unafraid.votingreward.VotingRewardInterfaceProvider;
import com.github.unafraid.votingreward.interfaceprovider.model.RewardItemHolder;

/**
 * @author UnAfraid
 */
public class RewardItem extends RewardItemHolder
{
	private final int _itemId;
	private final int _min;
	private final int _max;
	private final double _chance;
	
	public RewardItem(int itemId, int min, int max, double chance)
	{
		super(itemId, 0);
		_itemId = itemId;
		_min = min;
		_max = max;
		_chance = chance;
	}
	
	@Override
	public int getId()
	{
		return _itemId;
	}
	
	@Override
	public long getCount()
	{
		return VotingRewardInterfaceProvider.getInterface().getRandom(_min, _max);
	}
	
	public int getMin()
	{
		return _min;
	}
	
	public int getMax()
	{
		return _max;
	}
	
	public double getChance()
	{
		return _chance;
	}
}