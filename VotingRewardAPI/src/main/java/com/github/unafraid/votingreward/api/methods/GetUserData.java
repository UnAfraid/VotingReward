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

import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.github.unafraid.votingreward.api.ApiResponse;
import com.github.unafraid.votingreward.api.VotingRewardAPIException;
import com.github.unafraid.votingreward.api.objects.UserData;

/**
 * @author UnAfraid
 */
public class GetUserData extends AbstractVotingMethod<UserData>
{
	private static final long serialVersionUID = -8002314184042420217L;
	private static final String PATH = "getUserData";
	private static final String CLIENT_IP_FIELD = "ip";
	
	@JsonProperty(CLIENT_IP_FIELD)
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
	public UserData deserializeResponse(String answer) throws IOException, VotingRewardAPIException
	{
		final ApiResponse<UserData> response = OBJECT_MAPPER.readValue(answer, new TypeReference<ApiResponse<UserData>>()
		{
		});
		if (response.isOkay())
		{
			return response.getResult();
		}
		throw new VotingRewardAPIException("Error getting result", answer, response.getErrorCode());
	}
}
