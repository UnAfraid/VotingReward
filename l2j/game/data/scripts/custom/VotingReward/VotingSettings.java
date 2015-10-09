/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package custom.VotingReward;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import custom.VotingReward.model.Droplist;
import custom.VotingReward.model.DroplistGroup;
import custom.VotingReward.model.DroplistItem;
import custom.VotingReward.util.DocumentParser;
import custom.VotingReward.util.VotingUtil;

/**
 * @author UnAfraid
 */
public class VotingSettings extends DocumentParser
{
	private String _votingCommand;
	private long _votingInterval;
	private Color _color;
	private final Droplist _droplist = new Droplist();
	private final Map<MessageType, String> _messages = new HashMap<>();
	private String _apiKey;
	private int _serverId;
	
	protected VotingSettings()
	{
		load();
	}
	
	@Override
	public void load()
	{
		_droplist.getGroups().clear();
		parseDatapackFile("config/VotingReward.xml");
		_log.log(Level.INFO, getClass().getSimpleName() + ": Loaded " + _messages.size() + " messages, " + _droplist.getGroups().size() + " drops!");
	}
	
	@Override
	protected void parseDocument()
	{
		NamedNodeMap attrs;
		for (Node n = getCurrentDocument().getFirstChild(); n != null; n = n.getNextSibling())
		{
			if ("list".equalsIgnoreCase(n.getNodeName()))
			{
				for (Node b = n.getFirstChild(); b != null; b = b.getNextSibling())
				{
					switch (b.getNodeName())
					{
						case "api":
						{
							attrs = b.getAttributes();
							_serverId = parseInteger(attrs, "id");
							_apiKey = parseString(attrs, "key");
							break;
						}
						case "voting":
						{
							attrs = b.getAttributes();
							_votingCommand = parseString(attrs, "command", "getreward");
							_votingInterval = VotingUtil.parseTimeString(parseString(attrs, "interval", "12hours"));
							break;
						}
						case "nameColor":
						{
							attrs = b.getAttributes();
							int R = parseInteger(attrs, "r");
							int G = parseInteger(attrs, "g");
							int B = parseInteger(attrs, "b");
							_color = new Color(R, G, B);
							break;
						}
						case "messages":
						{
							for (Node a = b.getFirstChild(); a != null; a = a.getNextSibling())
							{
								switch (a.getNodeName())
								{
									case "message":
									{
										attrs = a.getAttributes();
										final MessageType type = parseEnum(attrs, MessageType.class, "type");
										final String content = a.getTextContent();
										_messages.put(type, content);
										break;
									}
								}
							}
							break;
						}
						case "rewards":
						{
							for (Node a = b.getFirstChild(); a != null; a = a.getNextSibling())
							{
								switch (a.getNodeName())
								{
									case "group":
									{
										attrs = a.getAttributes();
										final float groupChance = parseFloat(attrs, "chance");
										final DroplistGroup group = new DroplistGroup(groupChance);
										for (Node z = a.getFirstChild(); z != null; z = z.getNextSibling())
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
													group.addItem(new DroplistItem(itemId, min, max, chance));
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
	
	public Droplist getDroplist()
	{
		return _droplist;
	}
	
	public String getMessage(MessageType type)
	{
		return _messages.get(type);
	}
	
	public int getServerId()
	{
		return _serverId;
	}
	
	public String getAPIKey()
	{
		return _apiKey;
	}
	
	public enum MessageType
	{
		ON_SUCCESS,
		ON_NOT_VOTED,
		ON_REUSE,
	}
	
	protected static final VotingSettings getInstance()
	{
		return SingletonHolder._instance;
	}
	
	private static class SingletonHolder
	{
		protected static final VotingSettings _instance = new VotingSettings();
	}
}