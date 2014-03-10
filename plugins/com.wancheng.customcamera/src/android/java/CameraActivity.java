/*
	    Copyright 2013 Bruno Carreira - Lucas Farias - Rafael Luna - Vinícius Fonseca.

		Licensed under the Apache License, Version 2.0 (the "License");
		you may not use this file except in compliance with the License.
		You may obtain a copy of the License at

		http://www.apache.org/licenses/LICENSE-2.0

		Unless required by applicable law or agreed to in writing, software
		distributed under the License is distributed on an "AS IS" BASIS,
		WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
		See the License for the specific language governing permissions and
   		limitations under the License.   			
 */

package com.wancheng.customcamera;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.wancheng.customcamera.ForegroundCameraPreview;

import android.app.Activity;

import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PictureCallback;

import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

/**
 * Camera Activity Class. Configures Android camera to take picture and show it.
 */
public class CameraActivity extends Activity {

    private static final String TAG = "CameraActivity";

    private Camera mCamera;
    private ForegroundCameraPreview mPreview;
    private FrameLayout mFrameLayout;
    private boolean pressed = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
        int layoutID = getResources().getIdentifier("foregroundcameraplugin", "layout", getPackageName());
        setContentView(layoutID);
	
		// Create an instance of Camera
		mCamera = getCameraInstance();
	
		// Create a Preview and set it as the content of activity.
		mPreview = new ForegroundCameraPreview(this, mCamera);
		mFrameLayout = (FrameLayout) findViewById(getResources().getIdentifier("camera_view", "id", getPackageName()));
		mFrameLayout.addView(mPreview);
	
		// Add a listener to the Capture button
		Button captureButton = (Button) findViewById(getResources().getIdentifier("button_capture", "id", getPackageName()));
		captureButton.setOnClickListener(new View.OnClickListener() {
		    public void onClick(View v) {
	
				if (pressed)
				    return;
		
				// Set pressed = true to prevent freezing.
				// Issue 1 at
				// http://code.google.com/p/foreground-camera-plugin/issues/detail?id=1
				pressed = true;
		
				// get an image from the camera
				mCamera.autoFocus(new AutoFocusCallback() {
		
				    public void onAutoFocus(boolean success, Camera camera) {
					mCamera.takePicture(null, null, mPicture);
				    }
				});
		    }
		});
	
		Button cancelButton = (Button) findViewById(getResources().getIdentifier("button_cancel", "id", getPackageName()));
		cancelButton.setOnClickListener(new View.OnClickListener() {
		    public void onClick(View v) {
				pressed = false;
				setResult(RESULT_CANCELED);
				finish();
		    }
		});
		
		Button videoButton = (Button) findViewById(getResources().getIdentifier("button_video", "id", getPackageName()));
		videoButton.setOnClickListener(new View.OnClickListener() {
		    public void onClick(View v) {
				pressed = false;
				setResult(-101);
				finish();
		    }
		});
		
		Button albumButton = (Button) findViewById(getResources().getIdentifier("button_album", "id", getPackageName()));
		albumButton.setOnClickListener(new View.OnClickListener() {
		    public void onClick(View v) {
		    	pressed = false;
				setResult(-102);
				finish();
		    }
		});
    }
    
    @Override
    protected void onPause() {
	if (mCamera != null) {
	    mCamera.release(); // release the camera for other applications
	    mFrameLayout.removeView(mPreview); //Remove the preview from layout.
	    mCamera = null;
	}
	super.onPause();
    }

    @Override
    protected void onResume() {
	if (mCamera == null) {
	    mCamera = getCameraInstance(); //get the camera on resume.
	    mPreview = new ForegroundCameraPreview(this, mCamera); // Create the preview.
	    mFrameLayout.addView(mPreview); //Add the preview to layout.
	}
	super.onResume();
    }

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

    private PictureCallback mPicture = new PictureCallback() {

	public void onPictureTaken(byte[] data, Camera camera) {

	    Uri fileUri = (Uri) getIntent().getExtras().get(
		    MediaStore.EXTRA_OUTPUT);

	    File pictureFile = new File(fileUri.getPath());

	    try {
			FileOutputStream fos = new FileOutputStream(pictureFile);
			fos.write(data);
			fos.close();
	    } catch (FileNotFoundException e) {
	    	Log.d(TAG, "File not found: " + e.getMessage());
	    } catch (IOException e) {
	    	Log.d(TAG, "Error accessing file: " + e.getMessage());
	    }
	    
	    setResult(RESULT_OK);
	    pressed = false;
	    finish();
	}
    };
}