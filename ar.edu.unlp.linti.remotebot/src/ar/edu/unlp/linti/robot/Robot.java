package ar.edu.unlp.linti.robot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ar.edu.unlp.linti.robot.exceptions.RemoteBotException;

public class Robot implements RobotInterface{
/*
 * RobotInterface replica de forma bastante fiel la interfaz
 * de la clase Robot
 * del módulo duinobot en Python. Si bien no todos los métodos
 * se usan en esta App la intención era crear una interfaz completa
 * para futuras implementaciones de wrappers para duinobot en Java.
 */
	private Board board;
	private int id;
	
	public Robot(Board board, int id) throws RemoteBotException{
		this.board = board;
		this.id = id;
		this.command("__init__", new JSONArray());
	}
	private JSONObject command(String command, JSONArray args) throws RemoteBotException{
		return board.command("robot", this.id, command, args);
	}
	// Move
	protected void move(String movement, Integer velocity, double time) throws RemoteBotException{
		JSONArray args = new JSONArray();
		if (velocity != null){
			args.put(velocity);
			if (time >= 0){
				try {
					args.put(time);
				} catch (JSONException e) {
					throw new RemoteBotException(e.getMessage());
				}
			}
		}
		this.command(movement, args);
	}
	public void backward(Integer velocity, double time) throws RemoteBotException {
		this.move("backward", velocity, time);
	}

	public void backward(Integer velocity) throws RemoteBotException {
		this.backward(velocity, -1);
	}

	public void backward() throws RemoteBotException {
		this.backward(null, -1);
	}
	
	public void forward(Integer velocity, double time) throws RemoteBotException {
		this.move("forward", velocity, time);
	}

	public void forward(Integer velocity) throws RemoteBotException{
		this.forward(velocity, -1);
	}

	public void forward() throws RemoteBotException {
		this.forward(null, -1);
	}

	public void turnLeft(Integer velocity, double time) throws RemoteBotException {
		this.move("turnLeft", velocity, time);
	}

	public void turnLeft(Integer velocity) throws RemoteBotException {
		this.turnLeft(velocity, -1);
	}

	public void turnLeft() throws RemoteBotException {
		this.turnLeft(null, -1);
	}
	
	public void turnRight(Integer velocity, double time) throws RemoteBotException {
		this.move("turnRight", velocity, time);
	}

	public void turnRight(Integer velocity) throws RemoteBotException {
		this.turnRight(velocity, -1);
	}

	public void turnRight() throws RemoteBotException {
		this.turnRight(null, -1);
	}

	// Senses
	private int[] noArgsBinaryResponse(String command) throws RemoteBotException{
		JSONObject jresponse = this.command(command, null);
		int[] response = new int[2];
		try {
			JSONArray array = jresponse.getJSONArray("values").getJSONArray(0);
			response[0] = array.getInt(0);
			response[1] = array.getInt(1);
		} catch (JSONException e) {
			throw new RemoteBotException(e.getMessage());
		}
		return response;
		
	}
	public int[] getLine() throws RemoteBotException {
		return this.noArgsBinaryResponse("getLight");
	}
	public int ping() throws RemoteBotException {
		JSONObject jresponse = this.command("ping", null);
		int response;
		try {
			response = jresponse.getJSONArray("values").getInt(0);
		} catch (JSONException e) {
			throw new RemoteBotException(e.getMessage());
		}
		return response;
	}
	public boolean getObstacle(int distance) throws RemoteBotException {
		JSONObject jresponse = this.command("getObstacle", new JSONArray().put(distance));
		boolean response;
		try {
			response = jresponse.getJSONArray("values").getBoolean(0);
		} catch (JSONException e) {
			throw new RemoteBotException(e.getMessage());
		}
		return response;
	}
	public boolean getObstacle() throws RemoteBotException {
		return getObstacle(50);
	}

	public int[] getWheels() throws RemoteBotException {
		return this.noArgsBinaryResponse("getWheels");
	}
	public double battery() throws RemoteBotException{
		JSONObject jresponse = this.command("battery", null);
		try{
			return jresponse.getJSONArray("values").getInt(0);
		}
		catch (JSONException e){
			throw new RemoteBotException(e.getMessage());
		}
	}

	public N6Senses senses() throws RemoteBotException {
		// Puede hacerse más eficiente armando un JSONArray de comandos y envíandolos en un solo POST
		// temporalmente se implementa de forma ineficiente
		int[] line = this.getLine();
		boolean obstacle = this.getObstacle();
		int[] wheels = this.getWheels();
		double battery = this.battery();
		return new N6Senses(line, obstacle, wheels, battery);
	}
	public void stop() throws RemoteBotException {
		this.command("stop", null);
	}
	public void beep(Integer freq, Integer time) throws RemoteBotException {
		JSONArray args = new JSONArray();
		if (freq != null){
			args.put(freq);
		}
		else{
			args.put(500);
		}
		if (time != null){
			args.put(time);
		}
		this.command("beep", args);
	}
	public void beep(Integer freq) throws RemoteBotException {
		beep(freq, null);
	}
	public void beep() throws RemoteBotException {
		beep(null);
	}

}
