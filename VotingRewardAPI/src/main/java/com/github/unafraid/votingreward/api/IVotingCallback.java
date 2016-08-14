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

import java.io.Serializable;

import org.json.JSONObject;

import com.github.unafraid.votingreward.api.methods.IVotingMethod;

/**
 * @author Ruben Bermudez
 * @version 1.0
 * @param <T>
 * @brief Callback to execute api method asynchronously
 * @date 10 of September of 2015
 */
public interface IVotingCallback<T extends Serializable>
{
	/**
	 * Called when the request is successful
	 * @param method Method executed
	 * @param jsonObject Answer from Telegram server
	 */
	void onResult(IVotingMethod<T> method, JSONObject jsonObject);
	
	/**
	 * Called when the request fails
	 * @param method Method executed
	 * @param jsonObject Answer from Telegram server (contains error information)
	 */
	void onError(IVotingMethod<T> method, JSONObject jsonObject);
	
	/**
	 * Called when the http request throw an exception
	 * @param method Method executed
	 * @param exception Excepction thrown
	 */
	void onException(IVotingMethod<T> method, Exception exception);
}
