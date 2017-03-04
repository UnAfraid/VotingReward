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
package com.github.unafraid.votingreward.api.requests;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author UnAfraid
 */
public class UserDataRequest implements IVotingRequest
{
	private static final long serialVersionUID = -5438752347332688890L;
	
	@JsonProperty("ip")
	private final String _ip;
	
	public UserDataRequest(String ip)
	{
		_ip = ip;
	}
	
	public String getIp()
	{
		return _ip;
	}
}
