package ar.edu.unlp.linti.remotebot;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class Util {
	public static void alert(Context c, String message){
		alert(c, "Información", message);
	}
	public static void alert(Context c, String title, String message){
		AlertDialog alertDialog = new AlertDialog.Builder(c).create();
		alertDialog.setTitle(title);
		alertDialog.setMessage(message);
		alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
		   public void onClick(DialogInterface dialog, int which) {

		   }
		});
		//alertDialog.setIcon(R.drawable.icon);
		alertDialog.show();
	}
	public static void alert(Context c, Exception e){
		alert(c, "Excepción", exceptionToString(e));
	}
	public static String exceptionToString(Exception e){
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		return sw.toString();
	}
	public static void populateWithStrings(int device, JSONObject boardList) {
		for (int i = 0; i < boardList.length(); i++){
			
		}
		
	}
}
