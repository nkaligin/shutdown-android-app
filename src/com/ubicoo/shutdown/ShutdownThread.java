package com.ubicoo.shutdown;

import java.io.IOException;
import java.io.OutputStreamWriter;

import android.util.Log;

public class ShutdownThread extends Thread {

	private static final String TAG = ShutdownThread.class.getSimpleName();

	private final OnErrorListener errorListener;

	public ShutdownThread(OnErrorListener errorListener) {
		if (errorListener == null) {
			throw new IllegalArgumentException("errorListener cannot be null.");
		}
		this.errorListener = errorListener;
	}

	@Override
	public void run() {
		executeRootCommand("/system/bin/reboot -p");
	}

	private void executeRootCommand(final String command) {
		Log.d(TAG, "Executing root command... " + command);
		Runtime runtime = Runtime.getRuntime();
		Process proc = null;
		OutputStreamWriter osw = null;

		try { // Run Script

			proc = runtime.exec("su");
			osw = new OutputStreamWriter(proc.getOutputStream());
			osw.write(command);
			osw.flush();
			osw.close();

		} catch (Exception e) {
			Log.e(TAG, "Failed to execute root command: " + command, e);
			errorListener.onError(e);
		} finally {
			if (osw != null) {
				try {
					osw.close();
				} catch (IOException e) {
					Log.w(TAG, "Failed to close output stream writer.", e);
				}
			}
		}
		try {
			if (proc != null) {
				proc.waitFor();
			}
		} catch (InterruptedException e) {
			Log.e(TAG, "Failed to execute root command: " + command);
			Log.e(TAG, "Interrupted while waiting for process to finish.");
		}
		if (proc != null) {
			String stdOut = Utils.readAll(proc.getInputStream());
			String stdErr = Utils.readAll(proc.getErrorStream());
			if (proc.exitValue() != 0) {
				Log.e(TAG, "Failed to execute root command: " + command);
				Log.e(TAG, "Process exited with code " + proc.exitValue());
				if (stdOut.length() > 0) {
					Log.e(TAG, "Process console output: \n" + stdOut);
				}
				if (stdErr.length() > 0) {
					Log.e(TAG, "Process error output: \n" + stdErr);
					if (stdErr.contains("not allowed to su")) {
						errorListener.onNotRoot();
					} else {
						errorListener.onError(stdErr);
					}
				} else {
					errorListener.onError("Failed to shutdown the phone.");
				}
			}
		} else {
			Log.e(TAG, "Failed to execute root command: " + command);
			Log.e(TAG, "Process could not be created.");
		}
	}
}