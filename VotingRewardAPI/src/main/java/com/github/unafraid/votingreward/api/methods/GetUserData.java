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
package com.github.unafraid.votingreward.api.methods;

import org.json.JSONObject;

import com.github.unafraid.votingreward.api.VotingResponces;
import com.github.unafraid.votingreward.api.objects.UserVotingResultData;

/**
 * @author UnAfraid
 */
public class GetUserData implements IVotingMethod<UserVotingResultData>
{
	private static final String PATH = "getUserData";
	private static final String CLIENT_IP_FIELD = "ip";
	
	private final String _ip;
	
	public GetUserData(String ip)
	{
		_ip = ip;
	}
	
	@Override
	public String getPath()
	{
		return PATH;
	}
	
	@Override
	public UserVotingResultData deserializeResponse(JSONObject answer)
	{
		return new UserVotingResultData(answer.getJSONObject(VotingResponces.RESPONSE_FIELD_RESULT));
	}
	
	@Override
	public JSONObject toJson()
	{
		final JSONObject object = new JSONObject();
		object.put(CLIENT_IP_FIELD, _ip);
		return object;
	}
}
