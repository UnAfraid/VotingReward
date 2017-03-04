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

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author UnAfraid
 */
public class TopzoneServerData implements IVotingObject
{
	private static final long serialVersionUID = 1407655109685242745L;
	
	private static final String TOTAL_VOTES_FIELD = "totalVotes";
	private static final String SERVER_RANK_FIELD = "serverRank";
	
	@JsonProperty(TOTAL_VOTES_FIELD)
	private int _totalVotes;
	@JsonProperty(SERVER_RANK_FIELD)
	private int _serverRank;
	
	public int getTotalVotes()
	{
		return _totalVotes;
	}
	
	public int getServerRank()
	{
		return _serverRank;
	}
}
