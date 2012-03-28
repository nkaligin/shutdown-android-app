package com.ubicoo.shutdown;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.util.Log;

public final class Utils {

	private Utils() {
	}

	public static String readAll(InputStream inputStream) {
		try {
			int size = inputStream.available();
			if (size == 0) {
				return "";
			}
			BufferedReader r = new BufferedReader(new InputStreamReader(inputStream), size);
			StringBuilder total = new StringBuilder();
			String line;
			while ((line = r.readLine()) != null) {
				total.append(line);
			}
			return total.toString();
		} catch (IOException e) {
			Log.e(Shutdown.TAG, "Error reading from stream.", e);
			return "";
		}
	}
}