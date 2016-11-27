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

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.unafraid.votingreward.api.ApiResponse;
import com.github.unafraid.votingreward.api.VotingRewardAPIException;
import com.github.unafraid.votingreward.api.objects.ServerData;

/**
 * @author UnAfraid
 */
public class GetServerData extends AbstractVotingMethod<ServerData>
{
	private static final long serialVersionUID = 8749483424991211147L;
	private static final String PATH = "getServerData";
	
	public GetServerData()
	{
	}
	
	@Override
	public String getPath()
	{
		return PATH;
	}
	
	@Override
	public ServerData deserializeResponse(String answer) throws VotingRewardAPIException
	{
		try
		{
			final ApiResponse<ServerData> result = OBJECT_MAPPER.readValue(answer, new TypeReference<ApiResponse<ServerData>>()
			{
			});
			if (result.getOk())
			{
				return result.getResult();
			}
			throw new VotingRewardAPIException("Error getting result", answer, result.getErrorCode());
		}
		catch (IOException e2)
		{
			throw new VotingRewardAPIException("Unable to deserialize response", e2);
		}
	}
}
