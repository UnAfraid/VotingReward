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
package com.github.unafraid.votingreward;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.github.unafraid.votingreward.model.RewardGroup;
import com.github.unafraid.votingreward.model.RewardItem;
import com.github.unafraid.votingreward.model.RewardList;
import com.github.unafraid.votingreward.util.DocumentParser;
import com.github.unafraid.votingreward.util.VotingUtil;

/**
 * @author UnAfraid
 */
public class VotingSettings extends DocumentParser
{
	private String _votingCommand;
	private long _votingInterval;
	private Color _color;
	private final RewardList _droplist = new RewardList();
	private final Map<MessageType, String> _messages = new HashMap<>();
	private String _apiKey;
	
	protected VotingSettings()
	{
		load();
	}
	
	@Override
	public void load()
	{
		_droplist.getGroups().clear();
		parseDatapackFile("config/VotingReward.xml");
		VotingRewardInterface.getInstance().logInfo(getClass().getSimpleName() + ": Loaded " + _messages.size() + " messages, " + _droplist.getGroups().size() + " drops!");
	}
	
	@Override
	protected void parseDocument()
	{
		NamedNodeMap attrs;
		for (Node docNode = getCurrentDocument().getFirstChild(); docNode != null; docNode = docNode.getNextSibling())
		{
			if ("list".equalsIgnoreCase(docNode.getNodeName()))
			{
				for (Node listNode = docNode.getFirstChild(); listNode != null; listNode = listNode.getNextSibling())
				{
					switch (listNode.getNodeName())
					{
						case "api":
						{
							attrs = listNode.getAttributes();
							_apiKey = parseString(attrs, "key");
							break;
						}
						case "voting":
						{
							attrs = listNode.getAttributes();
							_votingCommand = parseString(attrs, "command", "getreward");
							_votingInterval = VotingUtil.parseTimeString(parseString(attrs, "interval", "12hours"));
							break;
						}
						case "nameColor":
						{
							attrs = listNode.getAttributes();
							int r = parseInteger(attrs, "r");
							int g = parseInteger(attrs, "g");
							int b = parseInteger(attrs, "b");
							_color = new Color(r, g, b);
							break;
						}
						case "messages":
						{
							for (Node messagesNode = listNode.getFirstChild(); messagesNode != null; messagesNode = messagesNode.getNextSibling())
							{
								switch (messagesNode.getNodeName())
								{
									case "message":
									{
										attrs = messagesNode.getAttributes();
										final MessageType type = parseEnum(attrs, MessageType.class, "type");
										final String content = messagesNode.getTextContent();
										_messages.put(type, content);
										break;
									}
								}
							}
							break;
						}
						case "rewards":
						{
							for (Node rewardsNode = listNode.getFirstChild(); rewardsNode != null; rewardsNode = rewardsNode.getNextSibling())
							{
								switch (rewardsNode.getNodeName())
								{
									case "group":
									{
										attrs = rewardsNode.getAttributes();
										final float groupChance = parseFloat(attrs, "chance");
										final RewardGroup group = new RewardGroup(groupChance);
										for (Node z = rewardsNode.getFirstChild(); z != null; z = z.getNextSibling())
										{
											switch (z.getNodeName())
											{
												case "item":
												{
													attrs = z.getAttributes();
													final int itemId = parseInteger(attrs, "id");
													final int min = parseInteger(attrs, "min");
													final int max = parseInteger(attrs, "max");
													final float chance = parseFloat(attrs, "chance");
													group.addItem(new RewardItem(itemId, min, max, chance));
													break;
												}
											}
										}
										_droplist.addGroup(group);
										break;
									}
								}
							}
							break;
						}
					}
				}
			}
		}
	}
	
	public String getVotingCommand()
	{
		return _votingCommand;
	}
	
	public long getVotingInterval()
	{
		return _votingInterval;
	}
	
	public Color getColor()
	{
		return _color;
	}
	
	public RewardList getDroplist()
	{
		return _droplist;
	}
	
	public String getMessage(MessageType type)
	{
		return _messages.get(type);
	}
	
	public String getAPIKey()
	{
		return _apiKey;
	}
	
	public static enum MessageType
	{
		ON_SUCCESS,
		ON_NOT_VOTED,
		ON_REUSE,
		ON_ERROR,
	}
	
	public static final VotingSettings getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final VotingSettings INSTANCE = new VotingSettings();
	}
}