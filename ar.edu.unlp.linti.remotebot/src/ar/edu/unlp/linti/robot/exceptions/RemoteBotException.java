package ar.edu.unlp.linti.robot.exceptions;

import java.io.IOException;

public class RemoteBotException extends IOException {
	public RemoteBotException(){
		super();
	}
	public RemoteBotException(String message) {
		super(message);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -5859826348433632374L;

}
