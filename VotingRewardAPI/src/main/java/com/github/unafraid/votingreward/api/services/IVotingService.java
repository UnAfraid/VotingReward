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
package com.github.unafraid.votingreward.api.services;

import com.github.unafraid.votingreward.api.ApiResponse;
import com.github.unafraid.votingreward.api.objects.ServerData;
import com.github.unafraid.votingreward.api.objects.UserData;
import com.github.unafraid.votingreward.api.requests.UserDataRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * @author UnAfraid
 */
public interface IVotingService
{
	@Headers(
	{
		"Content-Type: application/json",
		"User-Agent: L2TopZone",
		"Charset: UTF-8"
	})
	@POST("server_{apiKey}/getServerData")
	Call<ApiResponse<ServerData>> getServerData(@Path("apiKey") String apiKey);
	
	@Headers(
	{
		"Content-Type: application/json",
		"User-Agent: L2TopZone",
		"Charset: UTF-8"
	})
	@POST("server_{apiKey}/getUserData")
	Call<ApiResponse<UserData>> getUserData(@Body UserDataRequest request, @Path("apiKey") String apiKey);
}
