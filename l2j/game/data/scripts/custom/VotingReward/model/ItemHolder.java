/*
 * Copyright (C) 2004-2014 L2J DataPack
 * 
 * This file is part of L2J DataPack.
 * 
 * L2J DataPack is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * L2J DataPack is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package custom.VotingReward.model;

public class ItemHolder
{
	private final int _id;
	private final long _count;
	
	public ItemHolder(int id, long count)
	{
		_id = id;
		_count = count;
	}
	
	/**
	 * @return the ID of the item contained in this object
	 */
	public int getId()
	{
		return _id;
	}
	
	/**
	 * @return the count of items contained in this object
	 */
	public long getCount()
	{
		return _count;
	}
}