/*******************************************************************************
 * Copyright (c) 2012 Fernando E. M. López <flopez AT linti.unlp.edu.ar>.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package ar.edu.unlp.linti.remotebot;

import ar.edu.unlp.linti.remotebot.R;
import ar.edu.unlp.linti.remotebot.async.MoveAndDontCrash;
import ar.edu.unlp.linti.remotebot.async.ObstacleViewUpdater;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import ar.edu.unlp.linti.robot.*;
import ar.edu.unlp.linti.robot.exceptions.CommunicationException;


public class Controls extends Activity {

    private Board board;
	private Robot robot;
	private int vel;

    private MoveAndDontCrash moveAndDontCrash = null;
    private ObstacleViewUpdater updateObstacleView = null;
	private boolean stopOnObstacle = false;
	private boolean updateObstacle = false;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controls);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        Bundle extras = getIntent().getExtras();
        
        // Velocidad
        this.vel = 50;
        SeekBar seek = (SeekBar)findViewById(R.id.speed);
        seek.setMax(100);
        seek.setProgress(this.vel);
        seek.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				Controls.this.vel = progress;
			}
			public void onStartTrackingTouch(SeekBar seekBar) {
			}
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
        });
        
        // Creación del robot
        new AsyncTask<Object, Object, String>(){
        	String error = null;
			@Override
			protected String doInBackground(Object... args) {
				String server = (String) args[0], device = (String) args[1];
				Integer robotId = (Integer) args[2];
		        try {
					Controls.this.board = new Board(server, device);
					Controls.this.robot = new Robot(Controls.this.board, robotId);
				} catch (CommunicationException e) {
					error = "Error instanciando la placa y el robot";
				}
		        return error;
			}
			@Override
			protected void onPreExecute(){
				disable();
			}
			@Override
			protected void onPostExecute(String error){
				if (error != null){
					Util.alert(Controls.this, error);
					Controls.this.finish();
				}
				enable();
			}
        	
        }.execute(extras.getString("server"), extras.getString("device"), extras.getInt("robotId"));

        
        // Visualización de obstáculos
        CheckBox showObstacle = (CheckBox) findViewById(R.id.senses);
        showObstacle.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(CompoundButton view, boolean checked) {
				Controls.this.updateObstacle = checked;
				if (checked){
					Controls.this.startObstacleUpdate();
				}
				else{
					Controls.this.stopObstacleUpdate();
				}
			}
        	
        });        
        
        // Detección de obstáculos
        CheckBox stop = (CheckBox) findViewById(R.id.dontCrash);
        stop.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(CompoundButton view, boolean checked) {
				Controls.this.stopOnObstacle = checked;
			}
        	
        });            

        
    }
	public void stopAsyncMov(){
		if (this.moveAndDontCrash != null && 
				!this.moveAndDontCrash.isCancelled()){
			this.moveAndDontCrash.cancel(true);
		}
		this.moveAndDontCrash = null;
	}
	public void stopObstacleUpdate(){
		if (this.updateObstacleView != null &&
				!this.updateObstacleView.isCancelled()){
			this.updateObstacleView.cancel(true);
		}
		this.updateObstacleView = null;
	}
	public void startObstacleUpdate(){
		TextView tv = (TextView) findViewById(R.id.sensesView);
		tv.setVisibility(View.VISIBLE);
		stopObstacleUpdate();
		updateObstacleView = new ObstacleViewUpdater();
		updateObstacleView.execute(Controls.this.robot, tv);
	}
	public void disable(){
		//findViewById(R.id.activity_controls).setEnabled(false);
	}
	public void enable(){
		//findViewById(R.id.activity_controls).setEnabled(true);
	}
	public void forward(View view){

		new Thread(new Runnable(){
			@Override
			public void run() {
				try {
					Controls.this.robot.forward(Controls.this.vel);
				} catch (CommunicationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}).start();
		if (this.stopOnObstacle){    			
				stopAsyncMov();
				this.moveAndDontCrash = new MoveAndDontCrash();
				this.moveAndDontCrash.execute(this.robot);
		}
    }
	public void backward(View view){
		stopAsyncMov();
		new Thread(new Runnable(){
			@Override
			public void run() {
				try {
					Controls.this.robot.backward(Controls.this.vel);
				} catch (CommunicationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}).start();
	}
	public int turnSpeed(){
		boolean slow = ((CheckBox) findViewById(R.id.slow)).isChecked();
		int speed = this.vel;
		if (slow){
			return speed / 2;
		}
		return speed;
	}
	public void turnLeft(View view){
		stopAsyncMov();
		new Thread(new Runnable(){
			@Override
			public void run() {
				try {
					Controls.this.robot.turnLeft(turnSpeed());
				} catch (CommunicationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}).start();
	}
	public void turnRight(View view){
		stopAsyncMov();
		new Thread(new Runnable(){
			@Override
			public void run() {
				try {
					Controls.this.robot.turnRight(turnSpeed());
				} catch (CommunicationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}).start();
	}
	public void stop(View view){
		stopAsyncMov();
		new Thread(new Runnable(){
			@Override
			public void run() {
				try {
					Controls.this.robot.stop();
				} catch (CommunicationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}).start();
	}
	@Override
	public void onPause(){
		super.onPause();
		stopAsyncMov();
		stopObstacleUpdate();
		this.stop(null);
	}
	@Override
	public void onResume(){
		super.onResume();
		if (this.updateObstacle){
			startObstacleUpdate();
		}
	}
}
