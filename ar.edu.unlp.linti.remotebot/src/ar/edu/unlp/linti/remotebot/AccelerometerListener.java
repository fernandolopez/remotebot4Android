package ar.edu.unlp.linti.remotebot;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import ar.edu.unlp.linti.robot.AsyncMoveRobot;
import static java.lang.Math.abs;

public class AccelerometerListener implements SensorEventListener {
	private AsyncMoveRobot robot;
	private float oldLeft, oldBack;

	public AccelerometerListener(AsyncMoveRobot robot){
		this.robot = robot;
		oldLeft = 0;
		oldBack = 0;
	}
	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onSensorChanged(SensorEvent event) {
		float left = event.values[0];
		float back = event.values[1];
		float deltaLeft, deltaBack;
		
		int sideWaysVel = (int) (left  / 7 * 100);
		int straightVel = (int) (back / 7 * 100);
		if (sideWaysVel < 0) sideWaysVel -= 2 / 7 * 100;
		else sideWaysVel += 2 / 7 * 100;
		if (straightVel < 0) straightVel -= 2 / 7 * 100;
		else straightVel += 2 / 7 * 100;
		
		if (sideWaysVel > 100) sideWaysVel = 100;
		else if (sideWaysVel < -100) sideWaysVel = -100;
		if (straightVel > 100) straightVel = 100;
		else if (straightVel < -100) straightVel = -100;
		
		// Calculamos la diferencia respecto a la última vez que
		// movimos el robot
		deltaLeft = abs(abs(left) - abs(oldLeft));
		deltaBack = abs(abs(back) - abs(oldBack));
		if (deltaLeft < 1 && deltaBack < 1){
			// Si no es un cambio significativo lo ignoramos
			return;
		}
		if (abs(left) < 2 && abs(back) < 2){
			// Tomo un margen de error para el stop
			if (oldLeft == oldBack && oldLeft == 0){
				// Si ya mandé un stop no lo vuelvo a hacer
				return;
			}
			robot.stop();
			oldLeft = 0;
			oldBack = 0;
		}
		else if (abs(sideWaysVel) + 10 > abs(straightVel)){
			// Giro ya que está más inclinado de costado
			robot.turnLeft(sideWaysVel);
			oldLeft = left;
			oldBack = 0;
		}
		else{
			// Retrocedo (o avanzo) ya que está más inclinado a lo largo
			robot.backward(straightVel);
			oldBack = back;
			oldLeft = 0;
			
		}	
	}
}
