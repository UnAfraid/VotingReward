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
package com.github.unafraid.votingreward.api;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.unafraid.votingreward.api.methods.AbstractVotingMethod;
import com.github.unafraid.votingreward.api.methods.GetServerData;
import com.github.unafraid.votingreward.api.methods.GetUserData;
import com.github.unafraid.votingreward.api.objects.ServerData;
import com.github.unafraid.votingreward.api.objects.UserData;

/**
 * @author UnAfraid
 */
public class VotingRewardAPIClient
{
	private static final String BASEURL = "https://api.l2topzone.com/v1/server_%s/";
	private static final String USER_AGENT = "L2TopZone";
	private static final String CONTENT_TYPE = "application/json";
	private static final String HTTP_METHOD = "POST";
	private static final int SOCKET_TIMEOUT = 30 * 1000;
	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
	
	public final UserData getUserData(String ip, String apiKey) throws VotingRewardAPIException
	{
		final GetUserData getVoteData = new GetUserData(ip);
		return sendApiMethod(getVoteData, apiKey);
	}
	
	public final ServerData getServerData(String apiKey) throws VotingRewardAPIException
	{
		final GetServerData getVoteData = new GetServerData();
		return sendApiMethod(getVoteData, apiKey);
	}
	
	private <T extends Serializable> T sendApiMethod(AbstractVotingMethod<T> method, String apiKey) throws VotingRewardAPIException
	{
		try
		{
			Objects.requireNonNull(method, "method cannot be null!");
			Objects.requireNonNull(apiKey, "apiKey cannot be null!");
			
			final Map<String, String> headers = new HashMap<>();
			headers.put("Content-Type", CONTENT_TYPE);
			headers.put("User-Agent", USER_AGENT);
			headers.put("Charset", StandardCharsets.UTF_8.name());
			
			final URL urlAddress = new URL(getUrl(method, apiKey));
			final HttpURLConnection connection = (HttpURLConnection) urlAddress.openConnection();
			
			// Set timeouts
			connection.setConnectTimeout(SOCKET_TIMEOUT);
			connection.setReadTimeout(SOCKET_TIMEOUT);
			
			// Set POST type
			connection.setRequestMethod(HTTP_METHOD);
			
			// Set headers
			for (Entry<String, String> entry : headers.entrySet())
			{
				connection.setRequestProperty(entry.getKey(), entry.getValue());
			}
			
			// Set output
			connection.setDoOutput(true);
			
			// Write output
			try (final OutputStream os = connection.getOutputStream();
				final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8.name())))
			{
				writer.write(OBJECT_MAPPER.writeValueAsString(method));
			}
			
			connection.connect();
			
			// Read input
			String responseContent = null;
			try
			{
				responseContent = readStream(connection.getInputStream());
				return method.deserializeResponse(responseContent);
			}
			catch (Exception e)
			{
				throw new VotingRewardAPIException("Couldn't parse response content to json: " + responseContent, e);
			}
			finally
			{
				connection.disconnect();
			}
		}
		catch (Exception e)
		{
			throw new VotingRewardAPIException("Unable to execute " + method.getPath() + " method", e);
		}
	}
	
	private String readStream(InputStream input) throws IOException
	{
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(input)))
		{
			final StringBuilder result = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null)
			{
				result.append(line).append(System.lineSeparator());
			}
			return result.toString();
		}
	}
	
	<T extends Serializable> String getUrl(AbstractVotingMethod<T> method, String apiKey)
	{
		return String.format(BASEURL + method.getPath(), apiKey);
	}
	
	public static final VotingRewardAPIClient getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final VotingRewardAPIClient INSTANCE = new VotingRewardAPIClient();
	}
}
