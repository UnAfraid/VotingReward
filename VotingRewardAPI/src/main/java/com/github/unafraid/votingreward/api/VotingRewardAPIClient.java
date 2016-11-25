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

import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import com.github.unafraid.votingreward.VotingRewardInterfaceProvider;
import com.github.unafraid.votingreward.api.methods.GetServerData;
import com.github.unafraid.votingreward.api.methods.GetUserData;
import com.github.unafraid.votingreward.api.methods.IVotingMethod;
import com.github.unafraid.votingreward.api.objects.ServerVotingResultData;
import com.github.unafraid.votingreward.api.objects.UserVotingResultData;

/**
 * @author UnAfraid
 */
public class VotingRewardAPIClient
{
	private static final String BASEURL = "http://api.l2topzone.com/v1/server_%s/";
	private static final String USER_AGENT = "L2TopZone";
	private static final int SOCKET_TIMEOUT = 30 * 1000;
	
	final CloseableHttpClient _client;
	final RequestConfig _requestConfig;
	
	public VotingRewardAPIClient()
	{
		//@formatter:off
        _client = HttpClientBuilder.create()
            .setSSLHostnameVerifier(new NoopHostnameVerifier())
            .setConnectionTimeToLive(70, TimeUnit.SECONDS)
            .setMaxConnTotal(100)
            .build();

	    final RequestConfig.Builder configBuilder = RequestConfig.copy(RequestConfig.custom().build());
	    _requestConfig = configBuilder.setSocketTimeout(SOCKET_TIMEOUT)
            .setConnectTimeout(SOCKET_TIMEOUT)
            .setConnectionRequestTimeout(SOCKET_TIMEOUT)
            .build();
		//@formatter:on
	}
	
	public final UserVotingResultData getVoteData(String ip, String apiKey) throws VotingRewardAPIException
	{
		final GetUserData getVoteData = new GetUserData(ip);
		return sendApiMethod(getVoteData, apiKey);
	}
	
	public final ServerVotingResultData getVotesData(String apiKey) throws VotingRewardAPIException
	{
		final GetServerData getVoteData = new GetServerData();
		return sendApiMethod(getVoteData, apiKey);
	}
	
	public final void getVoteDataAsync(GetUserData userData, IVotingCallback<UserVotingResultData> sentCallback, String apiKey) throws VotingRewardAPIException
	{
		if (userData == null)
		{
			throw new VotingRewardAPIException("Parameter sendMessage can not be null");
		}
		
		if (sentCallback == null)
		{
			throw new VotingRewardAPIException("Parameter sentCallback can not be null");
		}
		
		sendApiMethodAsync(userData, sentCallback, apiKey);
	}
	
	public final void getVotesDataAsync(GetServerData serverData, IVotingCallback<ServerVotingResultData> sentCallback, String apiKey) throws VotingRewardAPIException
	{
		if (serverData == null)
		{
			throw new VotingRewardAPIException("Parameter sendMessage can not be null");
		}
		
		if (sentCallback == null)
		{
			throw new VotingRewardAPIException("Parameter sentCallback can not be null");
		}
		
		sendApiMethodAsync(serverData, sentCallback, apiKey);
	}
	
	<T extends Serializable> String getUrl(IVotingMethod<T> method, String apiKey)
	{
		return String.format(BASEURL + method.getPath(), apiKey);
	}
	
	private <T extends Serializable> T sendApiMethod(IVotingMethod<T> method, String apiKey) throws VotingRewardAPIException
	{
		try
		{
			final String url = getUrl(method, apiKey);
			final HttpPost post = new HttpPost(url);
			post.setConfig(_requestConfig);
			post.addHeader("charset", StandardCharsets.UTF_8.name());
			post.addHeader("User-Agent", USER_AGENT);
			post.setEntity(new StringEntity(method.toJson().toString(), ContentType.APPLICATION_JSON));
			try (CloseableHttpResponse response = _client.execute(post))
			{
				final HttpEntity entity = response.getEntity();
				final BufferedHttpEntity bufferedEntity = new BufferedHttpEntity(entity);
				final String responseContent = EntityUtils.toString(bufferedEntity, StandardCharsets.UTF_8);
				try
				{
					final JSONObject jsonObject = new JSONObject(responseContent);
					if (!jsonObject.getBoolean(VotingResponces.RESPONSE_FIELD_OK))
					{
						throw new VotingRewardAPIException("Error at " + method.getPath(), jsonObject.getString(VotingResponces.ERROR_DESCRIPTION_FIELD), jsonObject.getInt(VotingResponces.ERROR_CODE_FIELD));
					}
					return method.deserializeResponse(jsonObject);
				}
				catch (Exception e)
				{
					throw new VotingRewardAPIException("Couldn't parse response content to json: " + responseContent);
				}
			}
		}
		catch (IOException e)
		{
			throw new VotingRewardAPIException("Unable to execute " + method.getPath() + " method", e);
		}
	}
	
	private <T extends Serializable, Method extends IVotingMethod<T>, Callback extends IVotingCallback<T>> void sendApiMethodAsync(Method method, Callback callback, String apiKey)
	{
		VotingRewardInterfaceProvider.getInterface().executeTask(new AsyncTask<>(method, callback, apiKey));
	}
	
	class AsyncTask<T extends Serializable> implements Runnable
	{
		private final IVotingMethod<T> _method;
		private final IVotingCallback<T> _callback;
		private final String _apiKey;
		
		public AsyncTask(IVotingMethod<T> method, IVotingCallback<T> callback, String apiKey)
		{
			_method = method;
			_callback = callback;
			_apiKey = apiKey;
		}
		
		@Override
		public void run()
		{
			try
			{
				final String url = getUrl(_method, _apiKey);
				final HttpPost post = new HttpPost(url);
				post.setConfig(_requestConfig);
				post.addHeader("charset", StandardCharsets.UTF_8.name());
				post.addHeader("User-Agent", USER_AGENT);
				post.setEntity(new StringEntity(_method.toJson().toString(), ContentType.APPLICATION_JSON));
				try (CloseableHttpResponse response = _client.execute(post))
				{
					final HttpEntity entity = response.getEntity();
					final BufferedHttpEntity bufferedEntity = new BufferedHttpEntity(entity);
					final String responseContent = EntityUtils.toString(bufferedEntity, StandardCharsets.UTF_8);
					try
					{
						final JSONObject jsonObject = new JSONObject(responseContent);
						if (!jsonObject.getBoolean(VotingResponces.RESPONSE_FIELD_OK))
						{
							_callback.onError(_method, jsonObject);
						}
						_callback.onResult(_method, jsonObject);
					}
					catch (Exception e)
					{
						_callback.onException(_method, e);
					}
				}
			}
			catch (IOException e)
			{
				_callback.onException(_method, e);
			}
		}
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
