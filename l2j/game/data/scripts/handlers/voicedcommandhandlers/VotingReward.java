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
package handlers.voicedcommandhandlers;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.EnumMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.l2jserver.L2DatabaseFactory;
import com.l2jserver.gameserver.handler.IVoicedCommandHandler;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

/**
 * @author UnAfraid
 */
public class VotingReward implements IVoicedCommandHandler
{
	private static final Logger _log = Logger.getLogger(VotingReward.class.getName());
	// Queries
	private static final String DELETE_QUERY = "DELETE FROM mods_voting_reward WHERE time < ?";
	private static final String SELECT_QUERY = "SELECT * FROM mods_voting_reward";
	private static final String INSERT_QUERY = "INSERT INTO mods_voting_reward (data, scope, time) VALUES (?, ?, ?)";
	
	// Constants
	private static final long VOTING_INTERVAL = TimeUnit.HOURS.toMillis(12);
	private static final String USER_AGENT = "L2TopZone";
	private static final String API_URL = "http://l2topzone.com/api.php?API_KEY=%s&SERVER_ID=%d&IP=%s";
	
	// Settings
	private static final String API_KEY = "YOUR API KEY";
	private static final int SERVER_ID = 0; // YOUR SERVER ID
	
	// Cache
	private static final Map<UserScope, ScopeContainer> VOTTERS_CACHE = new EnumMap<>(UserScope.class);
	
	// Commands that triggers this script
	private static final String[] COMMANDS =
	{
		"getreward"
	};
	
	// Rewards for successfull voting
	private static final ItemHolder[] REWARDS =
	{
		new ItemHolder(5575, 1),
		new ItemHolder(6673, 1),
	};
	
	public VotingReward()
	{
		load();
	}
	
	@Override
	public boolean useVoicedCommand(String command, L2PcInstance activeChar, String params)
	{
		if (isVotter(activeChar.getClient().getConnectionAddress().getHostAddress()))
		{
			synchronized (this)
			{
				long time = getLastVotedTime(activeChar);
				
				// Make sure player haven't received reward already!
				if (time > 0)
				{
					sendReEnterMessage(time, activeChar);
					return false;
				}
				
				// Give him reward
				giveReward(activeChar);
				
				// Mark down this reward as given
				markAsVotted(activeChar);
				
				// Say thanks ;)
				activeChar.sendMessage("Thanks for voting here's some reward!");
			}
		}
		else
		{
			activeChar.sendMessage("You haven't voted yet!");
		}
		return false;
	}
	
	private static final void load()
	{
		// Initialize the cache
		for (UserScope scope : UserScope.values())
		{
			VOTTERS_CACHE.put(scope, new ScopeContainer());
		}
		
		// Cleanup old entries and load the data for votters
		try (Connection con = L2DatabaseFactory.getInstance().getConnection();
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
			_log.log(Level.WARNING, VotingReward.class.getSimpleName() + ": " + e.getMessage(), e);
		}
	}
	
	private static void giveReward(L2PcInstance activeChar)
	{
		for (ItemHolder holder : REWARDS)
		{
			activeChar.addItem("Reward", holder.getId(), holder.getCount(), activeChar, true);
		}
	}
	
	private static void sendReEnterMessage(long time, L2PcInstance player)
	{
		if (time > System.currentTimeMillis())
		{
			final long remainingTime = (time - System.currentTimeMillis()) / 1000;
			final int hours = (int) (remainingTime / 3600);
			final int minutes = (int) ((remainingTime % 3600) / 60);
			final int seconds = (int) ((remainingTime % 3600) % 60);
			
			String msg = "You have received your reward already try again in: " + hours + " hours";
			if (minutes > 0)
			{
				msg += " " + minutes + " minutes";
			}
			if (seconds > 0)
			{
				msg += " " + seconds + " seconds";
			}
			player.sendMessage(msg);
		}
	}
	
	private static final long getLastVotedTime(L2PcInstance activeChar)
	{
		for (Entry<UserScope, ScopeContainer> entry : VOTTERS_CACHE.entrySet())
		{
			final String data = entry.getKey().getData(activeChar);
			final long reuse = entry.getValue().getReuse(data);
			if (reuse > 0)
			{
				return reuse;
			}
		}
		return 0;
	}
	
	private static final void markAsVotted(final L2PcInstance player)
	{
		final long reuse = System.currentTimeMillis() + VOTING_INTERVAL;
		try (Connection con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement(INSERT_QUERY))
		{
			for (UserScope scope : UserScope.values())
			{
				final String data = scope.getData(player);
				final ScopeContainer container = VOTTERS_CACHE.get(scope);
				container.registerVotter(data, reuse);
				
				ps.setString(1, data);
				ps.setString(2, scope.name());
				ps.setLong(3, reuse);
				ps.addBatch();
			}
			ps.executeBatch();
		}
		catch (SQLException e)
		{
			_log.log(Level.WARNING, VotingReward.class.getSimpleName() + ": " + e.getMessage(), e);
		}
	}
	
	private static final boolean isVotter(String ip)
	{
		try
		{
			final URL obj = new URL(String.format(API_URL, API_KEY, SERVER_ID, ip));
			final HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			
			// add request header
			con.setRequestProperty("User-Agent", USER_AGENT);
			
			final int responseCode = con.getResponseCode();
			if (responseCode == 200) // OK
			{
				final StringBuffer sb = new StringBuffer();
				try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream())))
				{
					String inputLine;
					while ((inputLine = in.readLine()) != null)
					{
						sb.append(inputLine);
					}
				}
				return sb.toString().toUpperCase().equals("TRUE");
			}
		}
		catch (Exception e)
		{
			_log.log(Level.WARNING, VotingReward.class.getSimpleName() + ": " + e.getMessage(), e);
		}
		return false;
	}
	
	@Override
	public String[] getVoicedCommandList()
	{
		return COMMANDS;
	}
	
	private enum UserScope
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
	
	private static class ScopeContainer
	{
		private final Map<String, Long> _votters = new ConcurrentHashMap<>();
		
		public ScopeContainer()
		{
		}
		
		public void registerVotter(String data, long reuse)
		{
			_votters.put(data, reuse);
		}
		
		public long getReuse(String data)
		{
			if (_votters.containsKey(data))
			{
				long time = _votters.get(data);
				if (time > System.currentTimeMillis())
				{
					return time;
				}
			}
			return 0;
		}
	}
	
	public static class ItemHolder
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
	
}
