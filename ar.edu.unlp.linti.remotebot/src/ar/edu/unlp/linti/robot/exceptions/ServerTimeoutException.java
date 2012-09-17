package ar.edu.unlp.linti.robot.exceptions;

public class ServerTimeoutException extends RemoteBotException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 750279211236109972L;

	public ServerTimeoutException(String string) {
		super(string);
	}

}
