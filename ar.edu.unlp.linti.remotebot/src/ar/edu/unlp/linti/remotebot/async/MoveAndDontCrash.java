package ar.edu.unlp.linti.remotebot.async;

import android.os.AsyncTask;
import ar.edu.unlp.linti.robot.Robot;
import ar.edu.unlp.linti.robot.exceptions.RemoteBotException;



public class MoveAndDontCrash extends AsyncTask<Robot, Object, Object> {

	@Override
	protected Object doInBackground(Robot... params) {
		//android.os.Debug.waitForDebugger();
		Robot r = params[0];
		while (!isCancelled()){
			try {
				 if (r.getObstacle()){
					 r.stop();
					 r.beep(2000, 1);
					 Thread.sleep(1000);
				 }
				 Thread.sleep(500);
			} catch (RemoteBotException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

}
