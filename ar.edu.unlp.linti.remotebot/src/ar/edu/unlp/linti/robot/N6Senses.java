/*******************************************************************************
 * Copyright (c) 2012 Fernando E. M. López <flopez AT linti.unlp.edu.ar>.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
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
