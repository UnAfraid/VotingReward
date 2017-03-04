package com.github.unafraid.votingreward.api;
/*
 * Copyright (C) 2004-2017 Vote Rewarding System
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

import java.io.IOException;

import com.github.unafraid.votingreward.api.objects.ServerData;
import com.github.unafraid.votingreward.api.objects.UserData;
import com.github.unafraid.votingreward.api.requests.UserDataRequest;
import com.github.unafraid.votingreward.api.services.IVotingService;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

/**
 * @author UnAfraid
 */
public class VotingRewardAPIClient
{
	private final Retrofit _retrofit;
	private final IVotingService _service;
	private final String _apiKey;
	
	public VotingRewardAPIClient(String apiKey)
	{
		//@formatter:off
		_retrofit = new Retrofit.Builder()
			.baseUrl("https://api.l2topzone.com/v1/")
			.addConverterFactory(JacksonConverterFactory.create())
			.build();
		//@formatter:on
		_service = _retrofit.create(IVotingService.class);
		_apiKey = apiKey;
	}
	
	public ServerData getServerData() throws VotingRewardAPIException, IOException
	{
		return getResponse(_service.getServerData(_apiKey));
	}
	
	public UserData getUserData(String ip) throws VotingRewardAPIException, IOException
	{
		return getResponse(_service.getUserData(new UserDataRequest(ip), _apiKey));
	}
	
	private <T> T getResponse(Call<ApiResponse<T>> call) throws VotingRewardAPIException, IOException
	{
		final Response<ApiResponse<T>> response = call.execute();
		if (!response.isSuccessful())
		{
			throw new VotingRewardAPIException(response.message());
		}
		return response.body().getResult();
	}
}
