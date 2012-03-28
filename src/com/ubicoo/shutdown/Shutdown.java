package com.ubicoo.shutdown;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

public class Shutdown extends Activity implements OnErrorListener {

	static final String TAG = Shutdown.class.getSimpleName();

	private ShutdownThread shutdownThread;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.really_shutdown).setCancelable(false)
				.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						Shutdown.this.shutdown();
					}
				}).setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						Shutdown.this.finish();
					}
				});
		AlertDialog alert = builder.create();
		alert.show();
	}

	private void shutdown() {
		shutdownThread = new ShutdownThread(this);
		shutdownThread.start();
	}

	@Override
	public void onNotRoot() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				showNotRootedDialog();
			}
		});
	}

	@Override
	public void onError(final String msg) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				showErrorDialog(msg);
			}
		});
	}

	@Override
	public void onError(final Exception exc) {
		final String msg = exc.getClass().getSimpleName() + ": " + exc.getMessage();
		onError(msg);
	}

	private void showErrorDialog(String msg) {
		AlertDialog.Builder builder = buildErrorDialog(msg);
		AlertDialog alert = builder.create();
		alert.show();
	}

	private void showNotRootedDialog() {
		final Uri uri = Uri.parse(getString(R.string.rooting_url));
		AlertDialog.Builder builder = buildErrorDialog(getString(R.string.not_rooted));
		builder.setNegativeButton(R.string.what, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				startActivity(new Intent(Intent.ACTION_VIEW, uri));
				Shutdown.this.finish();
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}

	private AlertDialog.Builder buildErrorDialog(String msg) {
		return new AlertDialog.Builder(this).setMessage(msg).setCancelable(false)
				.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						Shutdown.this.finish();
					}
				});
	}

}