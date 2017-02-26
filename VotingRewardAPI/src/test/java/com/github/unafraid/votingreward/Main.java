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
package com.github.unafraid.votingreward;

import java.io.IOException;

import org.junit.Test;

import com.github.unafraid.votingreward.api.VotingRewardAPIClient;
import com.github.unafraid.votingreward.api.VotingRewardAPIException;
import com.github.unafraid.votingreward.api.objects.ServerData;
import com.github.unafraid.votingreward.api.objects.UserData;

import junit.framework.TestCase;

/**
 * @author UnAfraid
 */
public class Main extends TestCase
{
	private static final String API_KEY = "testtesttesttesttest";
	private static final String TEST_IP = "127.0.0.1";
	
	@Test
	public void testGetUserData() throws VotingRewardAPIException, IOException
	{
		final VotingRewardAPIClient client = new VotingRewardAPIClient(API_KEY);
		final UserData data = client.getUserData(TEST_IP);
		
		// Make sure we read the object
		assertNotNull(data);
		
		// Make sure properly is what's expected to be
		assertEquals(data.isVoted(), true);
		
		// Make sure time difference is below 30 seconds
		assertEquals(Math.abs((System.currentTimeMillis() / 1000) - data.getServerTime()) < 30, true);
	}
	
	@Test
	public void testGetServerData() throws VotingRewardAPIException, IOException
	{
		final VotingRewardAPIClient client = new VotingRewardAPIClient(API_KEY);
		final ServerData data = client.getServerData();
		
		// Make sure we read the object
		assertNotNull(data);
		
		// Make sure properly is what's expected to be
		assertSame(data.getTotalVotes(), 1);
		
		// Make sure the server rank is what's expected to be
		assertSame(data.getServerRank(), 0);
	}
	
	public static void main(String[] args)
	{
		new Main();
	}
}
