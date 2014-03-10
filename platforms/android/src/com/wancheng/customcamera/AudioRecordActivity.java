package com.wancheng.customcamera;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.widget.ProgressBar;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.MotionEvent;
import android.widget.Button;
import android.view.View;
import android.view.View.OnTouchListener;
import android.util.Log;
import android.media.MediaRecorder;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;


public class AudioRecordActivity extends Activity {

    private static final String LOG_TAG = "AudioRecordTest";
    private static String mFileName = null;

    private Button mRecordButton = null;
    private MediaRecorder mRecorder = null;
    
    private Timer myTimer;

    private ProgressBar progressBar;
    private int recordingTime;
    
    private static Context mContext;


    private String initFile() {
    	Uri fileUri = (Uri) getIntent().getExtras().get(
    			    MediaStore.EXTRA_OUTPUT);

    	mFileName = fileUri.getPath();
        return mFileName;
    }
    
    private void startRecording() {
    	recordingTime = 0;
		progressBar.setProgress(recordingTime);
		
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(initFile());
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }

        mRecorder.start();
    }

    private void stopRecording() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        
        int layoutID = getResources().getIdentifier("audiocapture", "layout", getPackageName());
        setContentView(layoutID);
        
		this.mContext = this;

        mRecordButton = (Button) findViewById(getResources().getIdentifier("capture", "id", getPackageName()));
        mRecordButton.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
			    if (event.getAction() == android.view.MotionEvent.ACTION_DOWN) {
			    	startRecording();
			    	
			    	myTimer = new Timer();
					myTimer.schedule(new TimerTask() {			
						@Override
						public void run() {
							TimerMethod();
						}
						
					}, 0, 10);
				} else if (event.getAction() == android.view.MotionEvent.ACTION_UP) {
				    stopRecording();		
				    myTimer.cancel();
				}
			    return true;
			}
		});
        
        Button previewButton = (Button) findViewById(getResources().getIdentifier("button_preview", "id", getPackageName()));
        previewButton.setOnClickListener(new View.OnClickListener() {

        	 public void onClick(View v) {
        		 if(recordingTime < 1000)
        		 {
        			 AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        			 builder
        			    .setTitle("Record at least 10 seconds")
        			    .setMessage("")
        			    .setPositiveButton("Ok", new DialogInterface.OnClickListener() 
        			    {
        			        public void onClick(DialogInterface dialog, int which) 
        			        {       
        			        	dialog.dismiss();    
        			        }
        			    });             
        			AlertDialog alert = builder.create();
        			        alert.show();
        		 } else {
    		        if (mRecorder != null) {
    		            mRecorder.release();
    		            mRecorder = null;
    		        }
            		 setResult(RESULT_OK);
            		 finish();
        		 }
			}
		});		
        
        Button cancelButton = (Button) findViewById(getResources().getIdentifier("button_cancel", "id", getPackageName()));
		cancelButton.setOnClickListener(new View.OnClickListener() {
		    public void onClick(View v) {
		        if (mRecorder != null) {
		            mRecorder.release();
		            mRecorder = null;
		        }
				setResult(RESULT_CANCELED);
				finish();
		    }
		});
		
        progressBar = (ProgressBar) findViewById(getResources().getIdentifier("progressBar1", "id", getPackageName()));
		progressBar.setVisibility(ProgressBar.VISIBLE);
        progressBar.setProgress(0);
        progressBar.setMax(4500);
        recordingTime = 0;
    }

    private void TimerMethod()
	{
		//This method is called directly by the timer
		//and runs in the same thread as the timer.

		//We call the method that will work with the UI
		//through the runOnUiThread method.
		this.runOnUiThread(Timer_Tick);
	}


	private Runnable Timer_Tick = new Runnable() {
		public void run() {
			recordingTime++;
			progressBar.setProgress(recordingTime);
	
		}
	};
	
    @Override
    public void onPause() {
        super.onPause();
        if (mRecorder != null) {
            mRecorder.release();
            mRecorder = null;
        }

    }
}
