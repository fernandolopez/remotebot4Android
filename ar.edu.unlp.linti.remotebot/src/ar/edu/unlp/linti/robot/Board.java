/*******************************************************************************
 * Copyright (c) 2012 Fernando E. M. López <flopez AT linti.unlp.edu.ar>.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package ar.edu.unlp.linti.robot;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ar.edu.unlp.linti.remotebot.AndroidBridge;
import ar.edu.unlp.linti.robot.exceptions.CommunicationException;

public class Board implements BoardInterface {
	private String device;
	private String server;

	public Board(String server, String devicename) throws CommunicationException{
		this.server = server;
		this.device = devicename;
		this.command("board", 0, "__init__", new JSONArray());

	}
	public void setDevice(String device) {
		this.device = device;
	}
	public String getDevice() {
		return device;
	}
	public JSONObject command(String target, int id, String command, JSONArray args) throws CommunicationException {
		JSONArray container = new JSONArray();
		JSONObject message = new JSONObject();
		JSONObject jsonResponse = null;
		container.put(message);
		if (args == null) args = new JSONArray();	
		try {
			message.put("target", target);
			JSONObject dev = new JSONObject();
			dev.put("device", this.device);
			message.put("board", dev);
			message.put("id", id);
			message.put("command", command);
			message.put("args", args);
		} catch (JSONException e) {
			throw new CommunicationException(e.getMessage());
		}		
		// Cliente en un thread separado
		jsonResponse = AndroidBridge.post(server, container);

		return jsonResponse;

	}
	public int[] report() throws CommunicationException {
		JSONArray robots;
		int []ret;
		try {
			robots = this.command("board", 0, "report", null)
					.getJSONArray("values")
					.getJSONArray(0);
			ret = new int[robots.length()];
			for (int i = 0; i < robots.length(); i++){
				ret[i] = robots.getInt(i);
			}
		} catch (JSONException e) {
			throw new CommunicationException(e.toString());
		}
		return ret;
	}
	


}
