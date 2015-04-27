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
package custom.VotingReward;

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
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.l2jserver.L2DatabaseFactory;
import com.l2jserver.gameserver.ThreadPoolManager;
import com.l2jserver.gameserver.handler.IVoicedCommandHandler;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

import custom.VotingReward.VotingSettings.MessageType;
import custom.VotingReward.model.DroplistItem;
import custom.VotingReward.model.ScopeContainer;
import custom.VotingReward.model.UserScope;

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
	
	// Cache
	private static final Map<UserScope, ScopeContainer> VOTTERS_CACHE = new EnumMap<>(UserScope.class);
	
	// Initialize settings first
	static
	{
		VotingSettings.getInstance();
	}
	
	// Commands that triggers this script
	private static final String[] COMMANDS =
	{
		VotingSettings.getInstance().getVotingCommand(),
	};
	
	private VotingReward()
	{
		load();
	}
	
	@Override
	public boolean useVoicedCommand(String command, L2PcInstance activeChar, String params)
	{
		if (activeChar.isGM() && "reload".equals(params))
		{
			VotingSettings.getInstance().load();
			activeChar.sendMessage("Reloaded VotingReward.xml");
			return true;
		}
		
		final long time = getLastVotedTime(activeChar);
		
		// Make sure player haven't received reward already!
		if (time > 0)
		{
			sendReEnterMessage(time, activeChar);
			return false;
		}
		
		ThreadPoolManager.getInstance().executeGeneral(() ->
		{
			// Check if player votted
			if (isVotter(activeChar.getClient().getConnectionAddress().getHostAddress()))
			{
				// Give him reward
				giveReward(activeChar);
				
				// Mark down this reward as given
				markAsVotted(activeChar);
				
				// Send message to player
				final String msg = VotingSettings.getInstance().getMessage(MessageType.ON_SUCCESS);
				if (msg != null)
				{
					activeChar.sendMessage(msg);
				}
			}
			else
			{
				final String msg = VotingSettings.getInstance().getMessage(MessageType.ON_NOT_VOTED);
				if (msg != null)
				{
					activeChar.sendMessage(msg);
				}
			}
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
		for (DroplistItem holder : VotingSettings.getInstance().getDroplist().calculateDrops())
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
			
			String msg = VotingSettings.getInstance().getMessage(MessageType.ON_REUSE);
			if (msg != null)
			{
				msg = msg.replaceAll("%hours%", Integer.toString(hours));
				msg = msg.replaceAll("%mins%", Integer.toString(minutes));
				msg = msg.replaceAll("%secs%", Integer.toString(seconds));
				player.sendMessage(msg);
			}
			player.sendMessage(msg);
		}
	}
	
	private static final synchronized long getLastVotedTime(L2PcInstance activeChar)
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
			final URL obj = new URL(String.format(API_URL, VotingSettings.getInstance().getAPIKey(), VotingSettings.getInstance().getServerId(), ip));
			final HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			
			// add request header
			con.setRequestProperty("User-Agent", USER_AGENT);
			con.setConnectTimeout(10 * 1000);
			
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
	
	public static void main(String[] args) 
	{
		new VotingReward();
	}
}