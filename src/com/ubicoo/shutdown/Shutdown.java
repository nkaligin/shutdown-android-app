package com.ubicoo.shutdown;

import java.io.DataOutputStream;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

public class Shutdown extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.main);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.are_you_sure_you_want_to_exit)
               .setCancelable(false)
               .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                	   Shutdown.this.shutdown();
                   }
               })
               .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                	   Shutdown.this.finish();
                   }
               });
        AlertDialog alert = builder.create();
        alert.show();
    }
    
    private void shutdown() {
    	runRootCommand("reboot -p");
    	//super.startActivity(new Intent("android.intent.action.ACTION_REQUEST_SHUTDOWN"));
    }
    
    private void handleError(Exception ex) {
    	//TODO give UI feedback via dialog (plus show Exception details)

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(ex.getClass().getSimpleName() + ": " + ex.getMessage())
               .setCancelable(false)
               .setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                	   Shutdown.this.finish();
                   }
               });
        AlertDialog alert = builder.create();
        alert.show();
    	
    	//finish();
    }
    
    public boolean runRootCommand(String command) {
        Process process = null;
        DataOutputStream os = null;
        try {
            process = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(command+"\n");
            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();
        } catch (Exception e) {
            //Log.d("*** DEBUG ***", "Unexpected error - Here is what I know: "+e.getMessage());
            handleError(e);
        	return false;
        }
        finally {
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