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

import java.io.Serializable;

import org.json.JSONObject;

/**
 * @author UnAfraid
 * @param <T>
 */
public interface IVotingMethod<T extends Serializable>
{
	/**
	 * Getter for method path (that is the same as method name)
	 * @return Method path
	 */
	public String getPath();
	
	/**
	 * Deserialize a json answer to the response type to a method
	 * @param answer Json answer received
	 * @return Answer for the method
	 */
	public T deserializeResponse(JSONObject answer);
	
	/**
	 * Convert to json object
	 * @return JSONObject created in the conversion
	 */
	JSONObject toJson();
}
