/*******************************************************************************
 * Copyright (c) 2012 Fernando E. M. López <flopez AT linti.unlp.edu.ar>.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package ar.edu.unlp.linti.remotebot.async;

import android.os.AsyncTask;
import android.widget.TextView;
import ar.edu.unlp.linti.robot.Robot;
import ar.edu.unlp.linti.robot.exceptions.CommunicationException;

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
			} catch (CommunicationException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}

		return null;
	}
	@Override
	protected void onProgressUpdate(Integer... progress){
		this.tv.setText("Obstáculo a " + progress[0].toString() + "cm");
	}
    @Override
	protected void onCancelled() {
		this.tv.setText("");
		super.onCancelled();
		//this.tv.setVisibility(View.INVISIBLE);	
	}
}
