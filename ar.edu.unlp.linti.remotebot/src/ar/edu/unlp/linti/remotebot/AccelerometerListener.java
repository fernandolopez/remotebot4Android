package ar.edu.unlp.linti.remotebot;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.SystemClock;
import ar.edu.unlp.linti.robot.AsyncMoveRobot;
import static java.lang.Math.abs;

public class AccelerometerListener implements SensorEventListener {
	private AsyncMoveRobot robot;
	private int oldSideways, oldStraight;
	private float millis;

	public AccelerometerListener(AsyncMoveRobot robot){
		this.robot = robot;
		oldSideways = 0;
		oldStraight = 0;
		millis = SystemClock.uptimeMillis();
	}
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {

	}
	@Override
	public void onSensorChanged(SensorEvent event) {
		float[] axis = new float[2];
		float newMillis = SystemClock.uptimeMillis();
		int sideways, straight;
		
		if (abs(newMillis - millis) < 250){
			// El tiempo de reacción de un humano a un estímulo visual es de alrededor de
			// 0.25 segundos, para no forzar los motores le exigimos este mismo tiempo al robot
			return;
		}
		millis = newMillis;
		
		// Cortamos los valores con valor absoluto mayor a 7
		for (int i = 0; i < 2; i++){
			if (event.values[i] > 7)		axis[i] = 7;
			else if (event.values[i] < -7)	axis[i] = -7;
			else							axis[i] = event.values[i];
		}
		// Las siguientes variables toman valores entre -100 y 100
		sideways = (int) ((float) axis[0] / 7 * 100);
		straight = (int) ((float) axis[1] / 7 * 100);
		
		
		// Stop
		if (abs(sideways) < 15 && abs(straight) < 15){
			// A velocidad 15 el robot casi no se mueve, así que lo podemos frenar
			// a menos que estuviera frenado desde antes, en dicho caso no hacemos nada
			if (oldSideways == 0 && oldStraight == 0) return;
			oldSideways = 0;
			oldStraight = 0;
			robot.stop();
			return;
		}

		// Forward, solamente si hay suficiente variación
		// y la velocidad del sideways es baja
		if (enoughtDifference(straight, oldStraight) && abs(sideways) < 15){
			robot.backward(straight);
			oldSideways = 0;
			oldStraight = straight;
		}
		else if (enoughtDifference(sideways, oldSideways) && abs(straight) < 15){
			robot.turnLeft(sideways);
			oldStraight = 0;
			oldSideways = sideways;
		}
	}
	private boolean enoughtDifference(int newVel, int oldVel) {
		int newPositive = newVel + 100;
		int oldPositive = oldVel + 100;
		// Una diferencia de velocidad x en cualquier sentido
		return (abs(newPositive - oldPositive) > 10); 
		
	}
}
