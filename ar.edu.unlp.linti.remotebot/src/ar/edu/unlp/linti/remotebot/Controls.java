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
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import ar.edu.unlp.linti.robot.*;
import ar.edu.unlp.linti.robot.exceptions.RemoteBotException;


public class Controls extends Activity {

    private Board board;
	private AsyncMoveRobot robot;
	private int vel;

    private MoveAndDontCrash moveAndDontCrash = null;
    private ObstacleViewUpdater updateObstacleView = null;
	private boolean stopOnObstacle = false;
	private boolean updateObstacle = false;
	private TextView distance;
	private Sensor accelerometer;
	protected boolean useAccelerometer;
	private SensorManager sensormanager;
	private AccelerometerListener accelerometerListener = null;
	
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controls);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        Bundle extras = getIntent().getExtras();
        
        this.distance = (TextView) findViewById(R.id.sensesView);
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
					Controls.this.robot = new AsyncMoveRobot(Controls.this.board, robotId);
				} catch (RemoteBotException e) {
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
		        accelerometerListener = new AccelerometerListener(robot);
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
        
        // Avanzar sin chocar
        CheckBox stop = (CheckBox) findViewById(R.id.dontCrash);
        stop.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(CompoundButton view, boolean checked) {
				Controls.this.stopOnObstacle = checked;
				if (checked){
					Controls.this.startDontCrash();
				}
				else {
					Controls.this.stopDontCrash();
				}
			}
        	
        });            

        // Acelerómetro
        sensormanager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensormanager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (accelerometer == null){
        	Util.alert(this, "No se detectaron acelerómetros");
        }
        CheckBox useAccelerometer = (CheckBox) findViewById(R.id.useAccelerometer);
        useAccelerometer.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(CompoundButton view, boolean checked) {
				if (checked && Controls.this.accelerometer != null){
					Controls.this.useAccelerometer = true;
					enableAccel();
				}
				else{
					Controls.this.useAccelerometer = false;
					disableAccel();
				}
			}
        	
        });
        
    }
	public void startDontCrash(){
		if (this.stopOnObstacle){    			
			stopDontCrash();
			this.moveAndDontCrash = new MoveAndDontCrash();
			this.moveAndDontCrash.execute(this.robot);
		}
	}
	public void stopDontCrash(){
		if (this.moveAndDontCrash != null && 
				!this.moveAndDontCrash.isCancelled()){
			this.moveAndDontCrash.cancel(true);
		}
		this.moveAndDontCrash = null;
	}
	public void stopObstacleUpdate(){
		distance.setVisibility(View.INVISIBLE);
		if (this.updateObstacleView != null &&
				!this.updateObstacleView.isCancelled()){
			this.updateObstacleView.cancel(true);
		}
		this.updateObstacleView = null;
	}
	public void startObstacleUpdate(){
		if (this.updateObstacle){
			stopObstacleUpdate();
			distance.setVisibility(View.VISIBLE);
			updateObstacleView = new ObstacleViewUpdater();
			updateObstacleView.execute(robot, distance);
		}
	}
	public void disable(){
		findViewById(R.id.activity_controls).setEnabled(false);
	}
	public void enable(){
		findViewById(R.id.activity_controls).setEnabled(true);
	}
	public void forward(View view){
		robot.forward(vel);
    }
	public void backward(View view){
		robot.backward(vel);
	}
	public int turnSpeed(){
		boolean slow = ((CheckBox) findViewById(R.id.slow)).isChecked();
		int speed = this.vel;
		if (slow){
			return speed / 2;
		}
		return speed;
	}
	public void turnLeft(View view){;
		robot.turnLeft(turnSpeed());
		
	}
	public void turnRight(View view){
		robot.turnRight(turnSpeed());
	}
	public void stop(View view){
		if (this.robot == null){
			return;
		}
		robot.stop();
	}
	
	private void enableAccel(){
		if (!this.useAccelerometer){
			return;
		}
		if (this.accelerometer != null && this.accelerometerListener != null){
			sensormanager.registerListener(accelerometerListener, this.accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
		}		
	}
	private void disableAccel(){
		if (this.accelerometer != null && this.accelerometerListener != null){
			sensormanager.unregisterListener(accelerometerListener);
		}		
	}
	
	@Override
	public void onPause(){
		super.onPause();
		stopDontCrash();
		stopObstacleUpdate();
		disableAccel();
		this.stop(null);
	}
	@Override
	public void onResume(){
		super.onResume();
		if (board == null || robot == null){
			return;
		}
		startDontCrash();
		startObstacleUpdate();
		enableAccel();
	}

}
