package ar.edu.unlp.linti.robot;

public class N6Senses extends Senses {

	private int[] line;
	private boolean obstacle;
	private int[] wheels;
	private double battery;

	public N6Senses(int[] line, boolean obstacle, int[] wheels, double battery) {
		this.line = line;
		this.obstacle = obstacle;
		this.wheels = wheels;
		this.battery = battery;
	}

	

	public int[] getLine() {
		return line;
	}

	public boolean getObstacle() {
		return obstacle;
	}

	public int[] getWheels() {
		return wheels;
	}

	public double getBattery() {
		return battery;
	}

}
