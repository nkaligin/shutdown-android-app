package com.ubicoo.shutdown;

import java.io.DataOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

public class Shutdown extends Activity {

	private static final String TAG = Shutdown.class.getSimpleName();

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.really_shutdown).setCancelable(false)
				.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						Shutdown.this.shutdown();
					}
				}).setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						Shutdown.this.finish();
					}
				});
		AlertDialog alert = builder.create();
		alert.show();
	}

	private void shutdown() {
		runRootCommand("reboot -p");

		//this only works for system applications with special permissions
		//startActivity(new Intent("android.intent.action.ACTION_REQUEST_SHUTDOWN"));
	}

	private void handleError(Exception ex) {
		String msg = ex.getClass().getSimpleName() + ": " + ex.getMessage();
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(msg).setCancelable(false).setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				Shutdown.this.finish();
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}

	public boolean runRootCommand(String command) {
		Process process = null;
		DataOutputStream os = null;
		try {
			process = Runtime.getRuntime().exec("su");
			os = new DataOutputStream(process.getOutputStream());
			os.writeBytes(command + "\n");
			os.writeBytes("exit\n");
			os.flush();
			process.waitFor();
		} catch (IOException e) {
			handleError(e);
			return false;
		} catch (SecurityException e) {
			handleError(e);
			return false;
		} catch (Exception e) {
			Log.e(TAG, "Unexpected error", e);
			handleError(e);
			return false;
		} finally {
			try {
				if (os != null) {
					os.close();
				}
				process.destroy();
			} catch (Exception e) {
				// nothing
			}
		}
		return true;
	}

}