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

/**
 * @author UnAfraid
 */
public class VotingRewardAPIException extends Exception
{
	private static final long serialVersionUID = -6637770495305600960L;
	private String apiResponse = null;
	private Integer errorCode;
	
	public VotingRewardAPIException(String message)
	{
		super(message);
	}
	
	public VotingRewardAPIException(String message, String apiResponse, Integer errorCode)
	{
		super(message);
		this.apiResponse = apiResponse;
	}
	
	public VotingRewardAPIException(String message, Throwable cause)
	{
		super(message, cause);
	}
	
	public String getApiResponse()
	{
		return apiResponse;
	}
	
	@Override
	public String toString()
	{
		if (apiResponse == null)
		{
			return super.toString();
		}
		else if (errorCode == null)
		{
			return super.toString() + ": " + apiResponse;
		}
		else
		{
			return super.toString() + ": [" + errorCode + "] " + apiResponse;
		}
	}
}
