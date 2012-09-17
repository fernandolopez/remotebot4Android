package ar.edu.unlp.linti.robot;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ar.edu.unlp.linti.remotebot.AndroidBridge;
import ar.edu.unlp.linti.robot.exceptions.RemoteBotException;

public class Board implements BoardInterface {
	/*
	 * Representa la conexión de la App con el par servidor-placa
	 */
	private String device;
	private String server;

	public Board(String server, String devicename) throws RemoteBotException{
		this.server = server;
		this.device = devicename;
		this.command("board", 0, "__init__", new JSONArray());

	}

	public JSONObject command(String target, int id, String command, JSONArray args) throws RemoteBotException {
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
			throw new RemoteBotException(e.getMessage());
		}		
		jsonResponse = AndroidBridge.post(server, container);

		return jsonResponse;

	}
	public int[] report() throws RemoteBotException {
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
			throw new RemoteBotException(e.toString());
		}
		return ret;
	}
	


}
