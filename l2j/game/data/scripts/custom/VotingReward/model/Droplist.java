/*
 * Copyright (C) 2004-2015 L2J Server
 * 
 * This file is part of L2J Server.
 * 
 * L2J Server is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * L2J Server is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package custom.VotingReward.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.l2jserver.util.Rnd;

/**
 * @author UnAfraid
 */
public class Droplist
{
	private List<DroplistGroup> _groups;
	
	public void addGroup(DroplistGroup group)
	{
		if (_groups == null)
		{
			_groups = new ArrayList<>();
		}
		_groups.add(group);
	}
	
	public List<DroplistGroup> getGroups()
	{
		return _groups != null ? _groups : Collections.<DroplistGroup> emptyList();
	}
	
	public boolean hasDrops()
	{
		return (_groups != null) && !_groups.isEmpty();
	}
	
	public List<DroplistGroup> getDrops()
	{
		return _groups != null ? _groups : Collections.<DroplistGroup> emptyList();
	}
	
	public List<DroplistItem> calculateDrops()
	{
		List<DroplistItem> itemsToDrop = null;
		for (DroplistGroup group : _groups)
		{
			final double groupRandom = 100 * Rnd.nextDouble();
			if (groupRandom < (group.getChance()))
			{
				final double itemRandom = 100 * Rnd.nextDouble();
				float cumulativeChance = 0;
				for (DroplistItem item : group.getItems())
				{
					if (itemRandom < (cumulativeChance += item.getChance()))
					{
						if (itemsToDrop == null)
						{
							itemsToDrop = new ArrayList<>();
						}
						itemsToDrop.add(item);
						break;
					}
				}
			}
		}
		return itemsToDrop != null ? itemsToDrop : Collections.<DroplistItem> emptyList();
	}
}