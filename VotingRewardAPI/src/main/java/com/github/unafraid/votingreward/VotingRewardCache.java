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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.EnumMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import com.github.unafraid.votingreward.interfaceprovider.api.IPlayerInstance;
import com.github.unafraid.votingreward.model.ScopeContainer;
import com.github.unafraid.votingreward.model.UserScope;

/**
 * @author UnAfraid
 */
public class VotingRewardCache
{
	// SQL Queries
	private static final String INSERT_QUERY = "INSERT INTO mods_voting_reward (data, scope, time) VALUES (?, ?, ?)";
	private static final String DELETE_QUERY = "DELETE FROM mods_voting_reward WHERE time < ?";
	private static final String SELECT_QUERY = "SELECT * FROM mods_voting_reward";
	
	// Constants
	private static final long VOTING_INTERVAL = TimeUnit.HOURS.toMillis(12);
	
	// Cache
	private static final Map<UserScope, ScopeContainer> VOTTERS_CACHE = new EnumMap<>(UserScope.class);
	
	protected VotingRewardCache()
	{
		load();
	}
	
	private static final void load()
	{
		// Initialize the cache
		for (UserScope scope : UserScope.values())
		{
			VOTTERS_CACHE.put(scope, new ScopeContainer());
		}
		
		// Cleanup old entries and load the data for votters
		try (Connection con = VotingRewardInterfaceProvider.getInstance().getInterface().getDatabaseConnection();
			PreparedStatement ps = con.prepareStatement(DELETE_QUERY);
			Statement st = con.createStatement())
		{
			ps.setLong(1, System.currentTimeMillis());
			ps.execute();
			
			// Load the data
			try (ResultSet rset = st.executeQuery(SELECT_QUERY))
			{
				while (rset.next())
				{
					final String data = rset.getString("data");
					final UserScope scope = UserScope.findByName(rset.getString("scope"));
					final Long time = rset.getLong("time");
					if (scope != null)
					{
						VOTTERS_CACHE.get(scope).registerVotter(data, time);
					}
				}
			}
		}
		catch (SQLException e)
		{
			VotingRewardInterfaceProvider.getInstance().getInterface().logError("Failed to load voting reward data", e);
		}
	}
	
	public void markAsVotted(IPlayerInstance player)
	{
		final long reuse = System.currentTimeMillis() + VOTING_INTERVAL;
		try (Connection con = VotingRewardInterfaceProvider.getInstance().getInterface().getDatabaseConnection();
			PreparedStatement ps = con.prepareStatement(INSERT_QUERY))
		{
			for (UserScope scope : UserScope.values())
			{
				if (scope.isSupported(player))
				{
					final String data = scope.getData(player);
					final ScopeContainer container = VOTTERS_CACHE.get(scope);
					container.registerVotter(data, reuse);
					
					ps.setString(1, data);
					ps.setString(2, scope.name());
					ps.setLong(3, reuse);
					ps.addBatch();
				}
			}
			ps.executeBatch();
		}
		catch (SQLException e)
		{
			VotingRewardInterfaceProvider.getInstance().getInterface().logError("Failed to store voting reward data", e);
		}
	}
	
	public long getLastVotedTime(IPlayerInstance player)
	{
		long lastVotedTime = 0;
		for (Entry<UserScope, ScopeContainer> entry : VOTTERS_CACHE.entrySet())
		{
			if (entry.getKey().isSupported(player))
			{
				final String data = entry.getKey().getData(player);
				final long reuse = entry.getValue().getReuse(data);
				if (reuse > lastVotedTime)
				{
					lastVotedTime = reuse;
				}
			}
		}
		return lastVotedTime;
	}
	
	protected static final VotingRewardCache getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final VotingRewardCache INSTANCE = new VotingRewardCache();
	}
}
