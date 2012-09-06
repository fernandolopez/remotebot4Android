/*******************************************************************************
 * Copyright (c) 2012 Fernando E. M. López <flopez AT linti.unlp.edu.ar>.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package ar.edu.unlp.linti.remotebot.async;

import android.os.AsyncTask;
import ar.edu.unlp.linti.robot.Robot;
import ar.edu.unlp.linti.robot.exceptions.CommunicationException;



public class MoveAndDontCrash extends AsyncTask<Robot, Object, Object> {

	@Override
	protected Object doInBackground(Robot... params) {
		//android.os.Debug.waitForDebugger();
		Robot r = params[0];
		while (!isCancelled()){
			try {
				 if (r.getObstacle()){
					 r.stop();
					 r.beep(2000);
					 return null;
				 }
				 Thread.sleep(500);
			} catch (CommunicationException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}

}
