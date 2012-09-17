package ar.edu.unlp.linti.remotebot.async;

import android.os.AsyncTask;
import android.widget.TextView;
import ar.edu.unlp.linti.robot.Robot;
import ar.edu.unlp.linti.robot.exceptions.RemoteBotException;

public class ObstacleViewUpdater extends AsyncTask<Object, Integer, Object> {

	private TextView tv;
	@Override
	protected Object doInBackground(Object... params) {
		Robot r = (Robot) params[0];
		tv = (TextView) params[1];
		while (!isCancelled()){
			try {
				publishProgress(r.ping());
				Thread.sleep(1000);
			} catch (RemoteBotException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (NullPointerException e) {
				e.printStackTrace();
			}
			
		}

		return null;
	}
	@Override
	protected void onProgressUpdate(Integer... progress){
		this.tv.setText("Obstáculo a " + progress[0].toString() + "cm");
	}

}
