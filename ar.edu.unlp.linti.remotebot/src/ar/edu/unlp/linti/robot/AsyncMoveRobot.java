package ar.edu.unlp.linti.robot;

import ar.edu.unlp.linti.robot.exceptions.RemoteBotException;

public class AsyncMoveRobot extends Robot{
/* 
 * Esta es una clase con la filosofía del mejor esfuerzo y pensada para 
 * ser utilizada directamente desde el thread principal en una Activity
 * provee una versión realmente asincrona de los métodos de movimiento
 * que permite evitar que la interfaz se congele y a la vez simplifica
 * el código de la Activity evitando crear los threads e ignorar las
 * posibles excepciones desde la misma.
 * Las excepciones se ignoran ya que no es importante que absolutamente
 * todos los comandos lleguen al servidor, si se manejaran estas excepciones
 * generarían errores que se mostrarían a destiempo y solamente molestarían
 * al usuario.
 * 
 * Los métodos heredadados y no sobreescritos SON bloqueantes y en caso
 * de error lanzan exceptiones, esto es así porque por ejemplo
 * si pedimos el valor de un sensor tiene sentido esperar su resultado.
 */
	public AsyncMoveRobot(Board board, int id) throws RemoteBotException {
		super(board, id);
	}
	protected void asyncMove(final String movement, final Integer velocity){
		new Thread(new Runnable(){
			@Override
			public void run() {
				try {
					AsyncMoveRobot.super.move(movement, velocity, -1);
				} catch (RemoteBotException e) {
					// En esta variante se ignoran los errores
					e.printStackTrace();
				}
				
			}
		}).start();
	}
	public void forward(Integer vel){
		asyncMove("forward", vel);
	}
	public void forward(){
		forward(null);
	}
	public void backward(Integer vel){
		asyncMove("backward", vel);
	}
	public void backward(){
		backward(null);
	}
	public void turnLeft(Integer vel){
		asyncMove("turnLeft", vel);
	}
	public void turnLeft(){
		turnLeft(null);
	}
	public void turnRight(Integer vel){
		asyncMove("turnRight", vel);
	}
	public void turnRight(){
		turnRight(null);
	}
	public void stop(){
		new Thread(new Runnable(){
			@Override
			public void run() {
				try {
					AsyncMoveRobot.super.stop();
				} catch (RemoteBotException e) {
					// El error se ignora en esta clase
					e.printStackTrace();
				}
			}
			
		}).start();
	}
}
