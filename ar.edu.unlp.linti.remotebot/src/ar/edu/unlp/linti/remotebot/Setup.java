package ar.edu.unlp.linti.remotebot;

import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ar.edu.unlp.linti.remotebot.R;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import ar.edu.unlp.linti.robot.Board;
import ar.edu.unlp.linti.robot.exceptions.ClientSideException;
import ar.edu.unlp.linti.robot.exceptions.RemoteBotException;
import ar.edu.unlp.linti.robot.exceptions.ConnectionException;
import ar.edu.unlp.linti.robot.exceptions.ServerSideException;
import ar.edu.unlp.linti.robot.exceptions.ServerTimeoutException;

public class Setup extends Activity {
	// Defaults
	//private final String defaultServer = "http://10.0.0.1:8000";
	//private final String defaultServer = "http://192.168.1.3:8000";
	private final String defaultServer = "http://192.168.1.116:8000";
	//private final String defaultServer = "http://192.168.43.210:8000";
	private final String defaultDevice = "/dev/ttyUSB0";
    private final Integer defaultRobotId = 1;
	
	// Parámetros para la otra activity
    private String server = defaultServer;
	public String device = defaultDevice;
    public Integer robotId = defaultRobotId;
    

    // Colecciones para los Spinners
	private Vector<String> devices = new Vector<String>();
    private Vector<Integer> robots = new Vector<Integer>();
	private Spinner deviceSpinner;
	private Spinner robotSpinner;
	private ArrayAdapter<String> devAdapter;
	private ArrayAdapter<Integer> robAdapter;
	
	// Tareas
	private AsyncTask<String, Object, int[]> deviceTask;
	private AsyncTask<String, Object, JSONObject> serverTask;


	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        toggleActive(R.id.progressBar1, false);
        toggleActive(R.id.deviceOk, false);
        toggleActive(R.id.robotOk, false);

        ((TextView) findViewById(R.id.url)).setText(this.defaultServer);

        this.deviceSpinner = (Spinner) findViewById(R.id.device);
        devAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, devices);
        devAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.deviceSpinner.setAdapter(devAdapter);

        this.robotSpinner = (Spinner) findViewById(R.id.robotId);
        robAdapter = new ArrayAdapter<Integer>(this, android.R.layout.simple_spinner_item, robots);
        robAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.robotSpinner.setAdapter(robAdapter);
        
        this.deviceSpinner.setOnItemSelectedListener(new OnItemSelectedListener(){
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
				Setup.this.device = (String) parent.getItemAtPosition(pos);
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {}
        	
        });
        this.robotSpinner.setOnItemSelectedListener(new OnItemSelectedListener(){
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
				Setup.this.robotId = (Integer) parent.getItemAtPosition(pos);
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {}
        	
        });
    }


    private void toggleActive(int id, boolean active){
    	View v = findViewById(id);
    	if (active) v.setVisibility(View.VISIBLE);
    	else v.setVisibility(View.INVISIBLE);
    }
    public void serverOk(View v){
    	TextView tv = (TextView) findViewById(R.id.url);
    	this.server = tv.getText().toString();

    	cancelThreads();

    	
    	this.serverTask = new AsyncTask<String,Object,JSONObject>(){
    		String error = null;
    		@Override
    		protected void onPreExecute(){
    	        toggleActive(R.id.deviceOk, false);
    	        toggleActive(R.id.robotOk, false);
    	    	toggleActive(R.id.progressBar1, true);
    		}
			@Override
			protected JSONObject doInBackground(String... args) {
				String server = args[0];
				JSONObject result = null;
				try{
					result  = AndroidBridge.moduleCommand(server, "boards", null);
				} catch (ClientSideException e) {
					error = "Error decodificando datos del servidor";
				} catch (ServerTimeoutException e) {
					error = "Timeout intentando conectar con el servidor";
				} catch (ServerSideException e) {
					error = "Error del lado del servidor. " + e.getMessage();
				} catch (ConnectionException e) {
					error = "Error conectando con el servidor.\nVerifique la URL.";
				} catch (RemoteBotException e) {
					error = "Error indeterminado al conectarse con el servidor";
				}
				return result;
			}
			@Override
			protected void onPostExecute(JSONObject result){
				toggleActive(R.id.progressBar1, false);
				if (this.error != null){
					Util.alert(Setup.this, this.error);
					return;
				}
				
		    	try {
					JSONArray boards = result.getJSONArray("values").getJSONArray(0);
					//this.devices = new Vector<String>();
					Setup.this.devices.clear();
					for (int i = 0; i < boards.length(); i++){
						Setup.this.devices.add(boards.getString(i));
					}
					Setup.this.devAdapter.notifyDataSetChanged();
					if (boards.length() == 0){
						Util.alert(Setup.this, "No se encontraron placas XBee conectadas al servidor");
						return;
					}
		    	} catch (JSONException e) {
					Util.alert(Setup.this, "El servidor devolvió un código desconocido");
					return;
				}
		    	toggleActive(R.id.deviceOk, true);
			}
    	}.execute(this.server);

    }
    private void cancelThreads(){
       	if (this.deviceTask != null && !this.deviceTask.isCancelled()){
    		this.deviceTask.cancel(true);
    	}
       	if (this.serverTask != null && !this.serverTask.isCancelled()){
       		this.serverTask.cancel(true);
       	}
    }
    public void deviceOk(View v) throws RemoteBotException{
    	this.cancelThreads();
    	this.deviceTask = new AsyncTask<String,Object,int[]>(){
    		@Override
    		protected void onPreExecute(){
    	    	toggleActive(R.id.progressBar1, true);
    	    	toggleActive(R.id.robotOk, false);
    		}
    		String error = null;
			@Override
			protected int[] doInBackground(String... args) {
		    	// Work
				Board board = null;
				try {
					board = new Board(args[0], args[1]);
				} catch (ServerTimeoutException e) {
					error = "Timeout intentando conectar con el servidor";
				} catch (ServerSideException e) {
					error = "Error instanciando Board(), verifique que el XBee esté conectado";
				} catch (RemoteBotException e) {
					error = "Error indeterminado comunicandose con el servidor: " + e.toString();
				}
				try {
					return  board.report();
				} catch (RemoteBotException e) {
					int[] def = new int[4];
					for (int i = 0; i < 4; i++){
						def[i] = i + 1;
					}
					return def;
				}
			}
			@Override
			protected void onPostExecute(int[] robotsArray){
		    	toggleActive(R.id.progressBar1, false);
				if (error != null){
					Util.alert(Setup.this, error);
					return;
				}
				Setup.this.robots.clear();
				for (int i = 0; i < robotsArray.length; i++){
					Setup.this.robots.add(robotsArray[i]);
				}
				if (robotsArray == null || robotsArray.length == 0){
					for (int i = 1; i < 5; i++){
						Setup.this.robots.add(i);
					}
				}
				Setup.this.robAdapter.notifyDataSetChanged();
		    	toggleActive(R.id.robotOk, true);
			}
    	}.execute(this.server, this.device);

    }
    public void robotOk(View v){
    	toggleActive(R.id.progressBar1, true);
    	// Work
    	Intent controls = new Intent(this, Controls.class);
    	controls.putExtra("server", this.server);
    	controls.putExtra("device", this.device);
    	controls.putExtra("robotId", this.robotId);
    	this.startActivity(controls);
    	toggleActive(R.id.progressBar1, false);
    }
    @Override
    public void onPause(){
    	super.onPause();
    	cancelThreads();
    }

}
