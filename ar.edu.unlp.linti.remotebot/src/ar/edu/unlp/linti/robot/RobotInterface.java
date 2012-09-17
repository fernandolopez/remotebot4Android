package ar.edu.unlp.linti.robot;

import ar.edu.unlp.linti.robot.exceptions.RemoteBotException;


public interface RobotInterface {
	public void forward(Integer velocity, double time) throws RemoteBotException;
	public void forward(Integer velocity) throws RemoteBotException;
	public void forward() throws RemoteBotException;
	public void backward(Integer velocity, double time) throws RemoteBotException;
	public void backward(Integer velocity) throws RemoteBotException;
	public void backward() throws RemoteBotException;
	public void turnLeft(Integer velocity, double time) throws RemoteBotException;
	public void turnLeft(Integer velocity) throws RemoteBotException;
	public void turnLeft() throws RemoteBotException;
	public void turnRight(Integer velocity, double time) throws RemoteBotException;
	public void turnRight(Integer velocity) throws RemoteBotException;
	public void turnRight() throws RemoteBotException;
	public Senses senses() throws RemoteBotException;
	public int[] getLine() throws RemoteBotException;
	public int[] getWheels() throws RemoteBotException;
	public boolean getObstacle() throws RemoteBotException;
	public boolean getObstacle(int distance) throws RemoteBotException;
	public double battery() throws RemoteBotException;
	public void stop() throws RemoteBotException;
}
