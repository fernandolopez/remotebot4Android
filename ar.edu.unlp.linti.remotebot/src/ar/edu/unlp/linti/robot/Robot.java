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

import ar.edu.unlp.linti.robot.exceptions.CommunicationException;

public class Robot implements RobotInterface{
	private Board board;
	private int id;

	public Robot(Board board, int id) throws CommunicationException{
		this.board = board;
		this.id = id;
		this.command("__init__", new JSONArray());
	}
	private JSONObject command(String command, JSONArray args) throws CommunicationException{
		return board.command("robot", this.id, command, args);
	}
	// Move
	private void move(String movement, Integer velocity, double time) throws CommunicationException{
		JSONArray args = new JSONArray();
		if (velocity != null){
			args.put(velocity);
			if (time >= 0){
				try {
					args.put(time);
				} catch (JSONException e) {
					throw new CommunicationException(e.getMessage());
				}
			}
		}
		this.command(movement, args);
	}
	public void backward(Integer velocity, double time) throws CommunicationException {
		this.move("backward", velocity, time);
	}

	public void backward(Integer velocity) throws CommunicationException {
		this.backward(velocity, -1);
	}

	public void backward() throws CommunicationException {
		this.backward(null, -1);
	}
	
	public void forward(Integer velocity, double time) throws CommunicationException {
		this.move("forward", velocity, time);
	}

	public void forward(Integer velocity) throws CommunicationException{
		this.forward(velocity, -1);
	}

	public void forward() throws CommunicationException {
		this.forward(null, -1);
	}

	public void turnLeft(Integer velocity, double time) throws CommunicationException {
		this.move("turnLeft", velocity, time);
	}

	public void turnLeft(Integer velocity) throws CommunicationException {
		this.turnLeft(velocity, -1);
	}

	public void turnLeft() throws CommunicationException {
		this.turnLeft(null, -1);
	}
	
	public void turnRight(Integer velocity, double time) throws CommunicationException {
		this.move("turnRight", velocity, time);
	}

	public void turnRight(Integer velocity) throws CommunicationException {
		this.turnRight(velocity, -1);
	}

	public void turnRight() throws CommunicationException {
		this.turnRight(null, -1);
	}

	// Senses
	private int[] noArgsBinaryResponse(String command) throws CommunicationException{
		JSONObject jresponse = this.command(command, null);
		int[] response = new int[2];
		try {
			JSONArray array = jresponse.getJSONArray("values").getJSONArray(0);
			response[0] = array.getInt(0);
			response[1] = array.getInt(1);
		} catch (JSONException e) {
			throw new CommunicationException(e.getMessage());
		}
		return response;
		
	}
	public int[] getLine() throws CommunicationException {
		return this.noArgsBinaryResponse("getLight");
	}
	public int ping() throws CommunicationException {
		JSONObject jresponse = this.command("ping", null);
		int response;
		try {
			response = jresponse.getJSONArray("values").getInt(0);
		} catch (JSONException e) {
			throw new CommunicationException(e.getMessage());
		}
		return response;
	}
	public boolean getObstacle(int distance) throws CommunicationException {
		JSONObject jresponse = this.command("getObstacle", new JSONArray().put(distance));
		boolean response;
		try {
			response = jresponse.getJSONArray("values").getBoolean(0);
		} catch (JSONException e) {
			throw new CommunicationException(e.getMessage());
		}
		return response;
	}
	public boolean getObstacle() throws CommunicationException {
		return getObstacle(50);
	}

	public int[] getWheels() throws CommunicationException {
		return this.noArgsBinaryResponse("getWheels");
	}
	public double battery() throws CommunicationException{
		JSONObject jresponse = this.command("battery", null);
		try{
			return jresponse.getJSONArray("values").getInt(0);
		}
		catch (JSONException e){
			throw new CommunicationException(e.getMessage());
		}
	}

	public N6Senses senses() throws CommunicationException {
		// Puede hacerse más eficiente armando un JSONArray de comandos y envíandolos en un solo POST
		// temporalmente se implementa de forma ineficiente
		int[] line = this.getLine();
		boolean obstacle = this.getObstacle();
		int[] wheels = this.getWheels();
		double battery = this.battery();
		return new N6Senses(line, obstacle, wheels, battery);
	}
	public void stop() throws CommunicationException {
		this.command("stop", null);
	}
	public void beep(Integer freq) throws CommunicationException {
		JSONArray args = new JSONArray();
		if (freq != null){
			args.put(freq);
		}
		this.command("beep", args);
	}
	public void beep() throws CommunicationException {
		beep(null);
	}

}
