/*
 * Copyright (C) 2014-2015 Vote Rewarding System
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
package com.github.unafraid.votingreward.interfaceprovider;

import java.io.File;
import java.sql.Connection;

import com.github.unafraid.votingreward.interfaceprovider.api.IOnVoicedCommandHandler;
import com.github.unafraid.votingreward.interfaceprovider.api.IPlayerInstance;

/**
 * @author UnAfraid
 */
public interface IVotingRewardInterface
{
	public void executeTask(Runnable runnable);
	
	public File getDocumentRoot();
	
	public Connection getDatabaseConnection();
	
	public void registerHandler(IOnVoicedCommandHandler handler);
	
	public double getRandomDouble();
	
	public int getRandomInt();
	
	public int getRandom(int min, int max);
	
	public void logInfo(String data);
	
	public void logWarning(String data, Throwable t);
	
	public void logError(String data, Throwable t);
	
	public void onSuccessfulVote(IPlayerInstance player);
	
	public void onInReuse(IPlayerInstance player, long timeRemaining);
	
	public void onNotVoted(IPlayerInstance player);
}
