/*******************************************************************************
 * Copyright (c) 2012 Fernando E. M. López <flopez AT linti.unlp.edu.ar>.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package ar.edu.unlp.linti.robot;

import ar.edu.unlp.linti.robot.exceptions.CommunicationException;


public interface RobotInterface {
	public void forward(Integer velocity, double time) throws CommunicationException;
	public void forward(Integer velocity) throws CommunicationException;
	public void forward() throws CommunicationException;
	public void backward(Integer velocity, double time) throws CommunicationException;
	public void backward(Integer velocity) throws CommunicationException;
	public void backward() throws CommunicationException;
	public void turnLeft(Integer velocity, double time) throws CommunicationException;
	public void turnLeft(Integer velocity) throws CommunicationException;
	public void turnLeft() throws CommunicationException;
	public void turnRight(Integer velocity, double time) throws CommunicationException;
	public void turnRight(Integer velocity) throws CommunicationException;
	public void turnRight() throws CommunicationException;
	public Senses senses() throws CommunicationException;
	public int[] getLine() throws CommunicationException;
	public int[] getWheels() throws CommunicationException;
	public boolean getObstacle() throws CommunicationException;
	public boolean getObstacle(int distance) throws CommunicationException;
	public double battery() throws CommunicationException;
	public void stop() throws CommunicationException;
}
