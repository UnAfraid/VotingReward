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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author UnAfraid
 * @param <T>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> implements Serializable
{
	private static final long serialVersionUID = -2146873845725578266L;
	private static final String OK_FIELD = "ok";
	private static final String ERROR_CODE_FIELD = "error_code";
	private static final String DESCRIPTION_CODE_FIELD = "description";
	private static final String RESULT_FIELD = "result";
	
	@JsonProperty(OK_FIELD)
	private Boolean _ok;
	@JsonProperty(ERROR_CODE_FIELD)
	private Integer _errorCode;
	@JsonProperty(DESCRIPTION_CODE_FIELD)
	private String _errorDescription;
	@JsonProperty(RESULT_FIELD)
	private T _result;
	
	public Boolean isOkay()
	{
		return _ok;
	}
	
	public Integer getErrorCode()
	{
		return _errorCode;
	}
	
	public String getErrorDescription()
	{
		return _errorDescription;
	}
	
	public T getResult()
	{
		return _result;
	}
	
	@Override
	public String toString()
	{
		//@formatter:off
		return _ok ? 
			"ApiResponse{ok=" + _ok + ", result=" + _result + '}' 
		:
			"ApiResponse{ok=" + _ok + ", errorCode=" + _errorCode + ", errorDescription='" + _errorDescription + '\'' + '}';
		//@formatter:on
	}
	
}
