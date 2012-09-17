package ar.edu.unlp.linti.robot;

import ar.edu.unlp.linti.robot.exceptions.RemoteBotException;

public interface BoardInterface {
	public int[] report() throws RemoteBotException;
}
