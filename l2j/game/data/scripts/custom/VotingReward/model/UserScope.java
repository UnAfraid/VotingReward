/*
 * Copyright (C) 2004-2015 L2J DataPack
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

import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

public enum UserScope
{
	ACCOUNT
	{
		@Override
		public String getData(L2PcInstance player)
		{
			return player.getAccountName();
		}
	},
	IP
	{
		@Override
		public String getData(L2PcInstance player)
		{
			return player.getClient().getConnectionAddress().getHostAddress();
		}
	},
	//@formatter:off
	/*HWID
	{
		@Override
		public String getData(L2PcInstance player)
		{
			return player.getHWID();
		}
	}*/
	//@formatter:on
	;
	
	public abstract String getData(L2PcInstance player);
	
	public static UserScope findByName(String name)
	{
		for (UserScope scope : values())
		{
			if (scope.name().equals(name))
			{
				return scope;
			}
		}
		return null;
	}
}