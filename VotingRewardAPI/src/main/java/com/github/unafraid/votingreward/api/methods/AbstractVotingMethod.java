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
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.unafraid.votingreward.api.VotingRewardAPIException;

/**
 * @author UnAfraid
 * @param <T>
 */
public abstract class AbstractVotingMethod<T extends Serializable> implements Serializable
{
	private static final long serialVersionUID = 1515654496124376641L;
	
	@JsonIgnore
	static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
	
	/**
	 * Getter for method path (that is the same as method name)
	 * @return Method path
	 */
	public abstract String getPath();
	
	/**
	 * Deserialize a json answer to the response type to a method
	 * @param answer Json answer received
	 * @return Answer for the method
	 * @throws IOException
	 * @throws VotingRewardAPIException
	 */
	public abstract T deserializeResponse(String answer) throws IOException, VotingRewardAPIException;
}
