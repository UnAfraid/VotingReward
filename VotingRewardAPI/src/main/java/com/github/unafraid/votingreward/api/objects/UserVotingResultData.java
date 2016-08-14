/*
 * Copyright (C) 2004-2016 Vote Rewarding System
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
package com.github.unafraid.votingreward.api.objects;

import org.json.JSONObject;

/**
 * @author UnAfraid
 */
public class UserVotingResultData implements IVotingObject
{
	private static final long serialVersionUID = 1407655109685242745L;
	
	private static final String VOTTED_FIELD = "isVoted";
	private static final String SERVER_TIME_FIELD = "serverTime";
	
	private final boolean _hasVoted;
	private final long _serverTime;
	
	public UserVotingResultData(JSONObject jsonObject)
	{
		_hasVoted = jsonObject.getBoolean(VOTTED_FIELD);
		_serverTime = jsonObject.getLong(SERVER_TIME_FIELD) * 1000;
	}
	
	public boolean isVoted()
	{
		return _hasVoted;
	}
	
	public long getServerTime()
	{
		return _serverTime;
	}
}
