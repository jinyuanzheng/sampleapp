
package com.wancheng.customcamera;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import com.wancheng.customcamera.ForegroundCameraPreview;
import com.wancheng.customcamera.VideoCaptureActivity;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

public class VideoCaptureActivity extends Activity {
    private static final String TAG = "VideoCaptureActivity";

    Camera camera;

    FrameLayout cameraPreviewFrame;

    ForegroundCameraPreview cameraPreview;

    MediaRecorder mediaRecorder;
    
    private Timer myTimer;

    private ProgressBar progressBar;
    private int recordingTime;

    private static Context mContext;

    File file;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        
        int layoutID = getResources().getIdentifier("videocapture", "layout", getPackageName());
        setContentView(layoutID);
        
		// Create a Preview and set it as the content of activity.
		camera = getCameraInstance();
		
		this.mContext = this;
		
		// Create a Preview and set it as the content of activity.
		cameraPreview = new ForegroundCameraPreview(this, camera);
		cameraPreviewFrame = (FrameLayout) findViewById(getResources().getIdentifier("camera_preview", "layout", getPackageName()));
		cameraPreviewFrame.addView(cameraPreview);
        // we'll enable this button once the camera is ready
		progressBar = (ProgressBar) findViewById(getResources().getIdentifier("progressBar1", "layout", getPackageName()));
		progressBar.setVisibility(ProgressBar.VISIBLE);
        progressBar.setProgress(0);
        progressBar.setMax(4500);
        recordingTime = 0;
        
        Button captureButton = (Button) findViewById(getResources().getIdentifier("button_capture", "layout", getPackageName()));
		captureButton.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
			    if (event.getAction() == android.view.MotionEvent.ACTION_DOWN) {
			    	startRecording(v);
			    	
			    	myTimer = new Timer();
					myTimer.schedule(new TimerTask() {			
						@Override
						public void run() {
							TimerMethod();
						}
						
					}, 0, 10);
				} else if (event.getAction() == android.view.MotionEvent.ACTION_UP) {
				    stopRecording(v);		
				    myTimer.cancel();
				}
			    return true;
			}
		});
		
        Button previewButton = (Button) findViewById(getResources().getIdentifier("button_preview", "layout", getPackageName()));
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
            		 releaseResources();
            		 setResult(RESULT_OK);
            		 finish();
        		 }
			}
		});		
        
        Button cancelButton = (Button) findViewById(getResources().getIdentifier("button_cancel", "layout", getPackageName()));
		cancelButton.setOnClickListener(new View.OnClickListener() {
		    public void onClick(View v) {
		    	releaseResources();
				setResult(RESULT_CANCELED);
				finish();
		    }
		});
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
	
    /** A safe way to get an instance of the Camera object. */
    public static Camera getCameraInstance() {
		Camera c = null;
		try {
		    c = Camera.open(); // attempt to get a Camera instance
		} catch (Exception e) {
		    // Camera is not available (in use or does not exist)
		}
		return c; // returns null if camera is unavailable
    }

    @Override
    protected void onResume() {
        if (camera == null) {
	        // initialize the camera in background, as this may take a while
	        camera = getCameraInstance();
	        this.cameraPreview = new ForegroundCameraPreview(VideoCaptureActivity.this, this.camera);
	        // add the preview to our preview frame
	        this.cameraPreviewFrame.addView(this.cameraPreview, 0);
        }
        super.onResume();
    }

    void initCamera(Camera camera) {
        // we now have the camera
        this.camera = camera;
        // create a preview for our camera
        this.cameraPreview = new ForegroundCameraPreview(VideoCaptureActivity.this, this.camera);
        // add the preview to our preview frame
        this.cameraPreviewFrame.addView(this.cameraPreview, 0);
        // enable just the record button
    }

    void releaseCamera() {
        if (this.camera != null) {
            this.camera.lock(); // unnecessary in API >= 14
            this.camera.stopPreview();
            this.camera.release();
            this.camera = null;
            this.cameraPreviewFrame.removeView(this.cameraPreview);
        }
    }

    void releaseMediaRecorder() {
        if (this.mediaRecorder != null) {
            this.mediaRecorder.reset(); // clear configuration (optional here)
            this.mediaRecorder.release();
            this.mediaRecorder = null;
        }
    }

    void releaseResources() {
        this.releaseMediaRecorder();
        this.releaseCamera();
    }

    @Override
    public void onPause() {
        this.releaseResources();
        super.onPause();
    }

    @Override
    public void onStop() {
        this.releaseResources();
        super.onStop();
    }

    // gets called by the button press
    public void startRecording(View v) {
    	recordingTime = 0;
		progressBar.setProgress(recordingTime);
		
        Log.d(TAG, "startRecording()");
        // we need to unlock the camera so that mediaRecorder can use it
        this.camera.unlock(); // unnecessary in API >= 14
        // now we can initialize the media recorder and set it up with our
        // camera
        this.mediaRecorder = new MediaRecorder();
        this.mediaRecorder.setCamera(this.camera);
        this.mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        this.mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        //mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        //mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
        //mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.DEFAULT);
        this.mediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_LOW));
        this.mediaRecorder.setOutputFile(this.initFile().getAbsolutePath());
        this.mediaRecorder.setPreviewDisplay(this.cameraPreview.getHolder().getSurface());
        try {
            this.mediaRecorder.prepare();
            // start the actual recording
            // throws IllegalStateException if not prepared
            this.mediaRecorder.start();
            //Toast.makeText(this, R.string.recording, Toast.LENGTH_SHORT).show();
            // enable the stop button by indicating that we are recording
        } catch (Exception e) {
            Log.wtf(TAG, "Failed to prepare MediaRecorder", e);
            //Toast.makeText(this, R.string.cannot_record, Toast.LENGTH_SHORT).show();
            this.releaseMediaRecorder();
        }
    }

    // gets called by the button press
    public void stopRecording(View v) {
        Log.d(TAG, "stopRecording()");
        assert this.mediaRecorder != null;
        try {
            this.mediaRecorder.stop();
            
            //Toast.makeText(this, R.string.saved, Toast.LENGTH_SHORT).show();
            // we are no longer recording
        } catch (RuntimeException e) {
            // the recording did not succeed
            Log.w(TAG, "Failed to record", e);
            if (this.file != null && this.file.exists() && this.file.delete()) {
                Log.d(TAG, "Deleted " + this.file.getAbsolutePath());
            }
            return;
        } finally {
            this.releaseMediaRecorder();
        }
        if (this.file == null || !this.file.exists()) {
            Log.w(TAG, "File does not exist after stop: " + this.file.getAbsolutePath());
        } else {
            Log.d(TAG, "Going to display the video: " + this.file.getAbsolutePath());
            //Intent intent = new Intent(this, VideoPlaybackActivity.class);
            //intent.setData(Uri.fromFile(file));
            //super.startActivity(intent);
        }

    }

    private File initFile() {
    	Uri fileUri = (Uri) getIntent().getExtras().get(
    			    MediaStore.EXTRA_OUTPUT);

    	this.file = new File(fileUri.getPath());
        return this.file;
    }
}
