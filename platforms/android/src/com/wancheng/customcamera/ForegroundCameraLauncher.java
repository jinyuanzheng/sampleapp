
package com.wancheng.customcamera;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.cordova.ExifHelper;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.LOG;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection.MediaScannerConnectionClient;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

/**
 * This class launches the camera view, allows the user to take a picture,
 * closes the camera view, and returns the captured image. When the camera view
 * is closed, the screen displayed before the camera view was shown is
 * redisplayed.
 */
public class ForegroundCameraLauncher extends CordovaPlugin implements MediaScannerConnectionClient {

	private static final String LOG_TAG = "ForegroundCameraLauncher";
	
	public static final String ACTION_TAKE_PICTURE_ENTRY = "takePicture"; 
	public static final String ACTION_TAKE_VIDEO_ENTRY = "takeVideo"; 
	public static final String ACTION_TAKE_AUDIO_ENTRY = "takeAudio"; 


	private int mQuality;
	private int targetWidth;
	private int targetHeight;
	private String captureType;

	private Uri imageUri;
	private File photo;

	private Uri videoUri;
	private File video;

	private Uri audioUri;
	private File audio;

	public String callbackId;
	private int numPics;

	private static final String _DATA = "_data";
	
    public CallbackContext callbackContext;
    
	private boolean bVideoRecording = false;
	
	private static final int TAKE_VIDEO = 1;
	private static final int TAKE_PHOTO = 2;
	private static final int SELECT_PICTURE = 3;
	private static final int TAKE_AUDIO = 4;

	/**
	 * Constructor.
	 */
	public ForegroundCameraLauncher() {
	}

    /**
     * Executes the request and returns PluginResult.
     *
     * @param action        	The action to execute.
     * @param args          	JSONArry of arguments for the plugin.
     * @param callbackContext   The callback id used when calling back into JavaScript.
     * @return              	A PluginResult object with a status and message.
     */
	public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        this.callbackContext = callbackContext;
        Log.e("asdfas","=========================execute=================");

        try {
            this.targetHeight = 0;
            this.targetWidth = 0;
            this.mQuality = 80;

            this.mQuality = args.getInt(0);
            this.targetWidth = args.getInt(3);
            this.targetHeight = args.getInt(4);
//            this.captureType = args.getString(12);

            // If the user specifies a 0 or smaller width/height
            // make it -1 so later comparisons succeed
            if (this.targetWidth < 1) {
                this.targetWidth = -1;
            }
            if (this.targetHeight < 1) {
                this.targetHeight = -1;
            }
            
            if(ACTION_TAKE_PICTURE_ENTRY.equals(action)){
            	bVideoRecording = false;
                this.takePicture();
                PluginResult r = new PluginResult(PluginResult.Status.NO_RESULT);
                r.setKeepCallback(true);
                callbackContext.sendPluginResult(r);
                return true;
            } else if(ACTION_TAKE_VIDEO_ENTRY.equals(action)){
            	bVideoRecording = true;
            	this.takeVideo();
            	
            	PluginResult r = new PluginResult(PluginResult.Status.NO_RESULT);
                r.setKeepCallback(true);
                callbackContext.sendPluginResult(r);
                return true;
            } else if(ACTION_TAKE_AUDIO_ENTRY.equals(action)){
            	bVideoRecording = true;
            	this.takeAudio();
            	
            	PluginResult r = new PluginResult(PluginResult.Status.NO_RESULT);
                r.setKeepCallback(true);
                callbackContext.sendPluginResult(r);
                return true;
            }

            callbackContext.error("Invalid action");
            return false;
        } catch(Exception e) {
            System.err.println("Exception: " + e.getMessage());
            callbackContext.error(e.getMessage());
            return false;
        } 
    }

	// --------------------------------------------------------------------------
	// LOCAL METHODS
	// --------------------------------------------------------------------------

	/**
	 * Take a picture with the camera. When an image is captured or the camera
	 * view is cancelled, the result is returned in
	 * CordovaActivity.onActivityResult, which forwards the result to
	 * this.onActivityResult.
	 * 
	 * The image can either be returned as a base64 string or a URI that points
	 * to the file. To display base64 string in an img tag, set the source to:
	 * img.src="data:image/jpeg;base64,"+result; or to display URI in an img tag
	 * img.src=result;
	 * 
	 */    
	public void takePicture() {
        Log.e("asdfas","=========================takePicture=================");

		// Save the number of images currently on disk for later
		this.numPics = queryImgDB().getCount();

		Intent intent = new Intent(this.cordova.getActivity().getApplicationContext(), CameraActivity.class);
		this.photo = createCaptureFile();
		this.imageUri = Uri.fromFile(photo);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, this.imageUri);

		this.cordova.startActivityForResult((CordovaPlugin) this, intent, TAKE_PHOTO);
	}

	public void takeVideo() {
		Intent intent = new Intent(this.cordova.getActivity().getApplicationContext(), VideoCaptureActivity.class);
		this.video = createVideoFile();
		this.videoUri = Uri.fromFile(video);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, videoUri);
		this.cordova.startActivityForResult((CordovaPlugin) this, intent, TAKE_VIDEO);
	}
	
	public void takeAudio() {
		Intent intent = new Intent(this.cordova.getActivity().getApplicationContext(), AudioRecordActivity.class);
		this.audio = createAudioFile();
		this.audioUri = Uri.fromFile(audio);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, audioUri);
		this.cordova.startActivityForResult((CordovaPlugin) this, intent, TAKE_AUDIO);
	}
	/**
	 * Create a file in the applications temporary directory based upon the
	 * supplied encoding.
	 * 
	 * @return a File object pointing to the temporary picture
	 */
	private File createCaptureFile() {
		File photo = new File(getTempDirectoryPath(this.cordova.getActivity().getApplicationContext()), "Pic.jpg");
		return photo;
	}
	
    private File createVideoFile() {
        File dir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES), this
                        .getClass().getPackage().getName());
        File videofile ;
        if (!dir.exists() && !dir.mkdirs()) {
            //Log.wtf(TAG, "Failed to create storage directory: " + dir.getAbsolutePath());
            //Toast.makeText(VideoCaptureActivity.this, R.string.cannot_record, Toast.LENGTH_SHORT);
        	videofile = null;
        } else {
        	videofile = new File(dir.getAbsolutePath(), new SimpleDateFormat(
                    "'VIDEO_'yyyyMMddHHmmss'.m4v'").format(new Date()));
        }
        
		return videofile;
    }
    
	private File createAudioFile() {
        File dir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES), this
                        .getClass().getPackage().getName());
        File audiofile ;
        if (!dir.exists() && !dir.mkdirs()) {
            //Log.wtf(TAG, "Failed to create storage directory: " + dir.getAbsolutePath());
            //Toast.makeText(VideoCaptureActivity.this, R.string.cannot_record, Toast.LENGTH_SHORT);
        	audiofile = null;
        } else {
        	audiofile = new File(dir.getAbsolutePath(), new SimpleDateFormat(
                    "'AUDIO_'yyyyMMddHHmmss'.3gp'").format(new Date()));
        }
        
        return audiofile;
    }

	/**
	 * Called when the camera view exits.
	 * 
	 * @param requestCode
	 *            The request code originally supplied to
	 *            startActivityForResult(), allowing you to identify who this
	 *            result came from.
	 * @param resultCode
	 *            The integer result code returned by the child activity through
	 *            its setResult().
	 * @param intent
	 *            An Intent, which can return result data to the caller (various
	 *            data can be attached to Intent "extras").
	 */
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		
		// If image available
		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == SELECT_PICTURE) {
				Uri selectedImageUri = intent.getData();
				this.callbackContext.success(getPath(selectedImageUri));
			} else if(requestCode == TAKE_PHOTO) {
				try {
					// Create an ExifHelper to save the exif data that is lost
					// during compression
					ExifHelper exif = new ExifHelper();
					exif.createInFile(getTempDirectoryPath(this.cordova.getActivity().getApplicationContext())
							+ "/Pic.jpg");
					exif.readExifData();
	
					// Read in bitmap of captured image
					Bitmap bitmap;
					try {
						bitmap = android.provider.MediaStore.Images.Media
								.getBitmap(this.cordova.getActivity().getContentResolver(), imageUri);
					} catch (FileNotFoundException e) {
						Uri uri = intent.getData();
						android.content.ContentResolver resolver = this.cordova.getActivity().getContentResolver();
						bitmap = android.graphics.BitmapFactory
								.decodeStream(resolver.openInputStream(uri));
					}
	
					bitmap = scaleBitmap(bitmap);
	
					// Create entry in media store for image
					// (Don't use insertImage() because it uses default compression
					// setting of 50 - no way to change it)
					ContentValues values = new ContentValues();
					values.put(android.provider.MediaStore.Images.Media.MIME_TYPE,
							"image/jpeg");
					Uri uri = null;
					try {
						uri = this.cordova.getActivity().getContentResolver()
								.insert(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
										values);
					} catch (UnsupportedOperationException e) {
						LOG.d(LOG_TAG, "Can't write to external media storage.");
						try {
							uri = this.cordova.getActivity().getContentResolver()
									.insert(android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI,
											values);
						} catch (UnsupportedOperationException ex) {
							LOG.d(LOG_TAG, "Can't write to internal media storage.");
							this.failPicture("Error capturing image - no media storage found.");
							return;
						}
					}
	
					// Add compressed version of captured image to returned media
					// store Uri
					OutputStream os = this.cordova.getActivity().getContentResolver()
							.openOutputStream(uri);
					bitmap.compress(Bitmap.CompressFormat.JPEG, this.mQuality, os);
					os.close();
	
					// Restore exif data to file
					exif.createOutFile(getRealPathFromURI(uri, this.cordova));
					exif.writeExifData();
	
					// Send Uri back to JavaScript for viewing image
					this.callbackContext.success(getRealPathFromURI(uri, this.cordova));
	
					bitmap.recycle();
					bitmap = null;
					System.gc();
	
					checkForDuplicateImage();
				} catch (IOException e) {
					e.printStackTrace();
					this.failPicture("Error capturing image.");
				}
			} else if(requestCode == TAKE_VIDEO){
				this.callbackContext.success(this.videoUri.toString());
			} else if(requestCode == TAKE_AUDIO){
				this.callbackContext.success(this.audioUri.toString());
			}
		}

		// If cancelled
		else if (resultCode == Activity.RESULT_CANCELED) {
			this.failPicture("Camera cancelled.");
		}
		
		// If video mode
		else if (resultCode == -101) {
			this.failPicture("videomode");
		}

		// If album mode
		else if (resultCode == -102) {
			Intent intent1 = new Intent();
            intent1.setType("image/*");
            intent1.setAction(Intent.ACTION_GET_CONTENT);
            this.cordova.startActivityForResult((CordovaPlugin) this, Intent.createChooser(intent1,
                    "Select Picture"), SELECT_PICTURE);
		}
		
		// If something else
		else {
			this.failPicture("Did not complete!");
		}
	}
	
    public void failPicture(String err) {
        this.callbackContext.error(err);
    }
    
	/**
     * helper to retrieve the path of an image URI
     */
    public String getPath(Uri uri) {
            // just some safety built in 
            if( uri == null ) {
                // TODO perform some logging or show user feedback
                return null;
            }
            // try to retrieve the image from the media store first
            // this will only work for images selected from gallery
            String[] projection = { MediaStore.Images.Media.DATA };
            Cursor cursor = cordova.getActivity().managedQuery(uri, projection, null, null, null);
            if( cursor != null ){
                int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                return cursor.getString(column_index);
            }
            // this is our fallback here
            return uri.getPath();
    }
    
	/**
	 * Scales the bitmap according to the requested size.
	 * 
	 * @param bitmap
	 *            The bitmap to scale.
	 * @return Bitmap A new Bitmap object of the same bitmap after scaling.
	 */
	public Bitmap scaleBitmap(Bitmap bitmap) {
		int newWidth = this.targetWidth;
		int newHeight = this.targetHeight;
		int origWidth = bitmap.getWidth();
		int origHeight = bitmap.getHeight();

		// If no new width or height were specified return the original bitmap
		if (newWidth <= 0 && newHeight <= 0) {
			return bitmap;
		}
		// Only the width was specified
		else if (newWidth > 0 && newHeight <= 0) {
			newHeight = (newWidth * origHeight) / origWidth;
		}
		// only the height was specified
		else if (newWidth <= 0 && newHeight > 0) {
			newWidth = (newHeight * origWidth) / origHeight;
		}
		// If the user specified both a positive width and height
		// (potentially different aspect ratio) then the width or height is
		// scaled so that the image fits while maintaining aspect ratio.
		// Alternatively, the specified width and height could have been
		// kept and Bitmap.SCALE_TO_FIT specified when scaling, but this
		// would result in whitespace in the new image.
		else {
			double newRatio = newWidth / (double) newHeight;
			double origRatio = origWidth / (double) origHeight;

			if (origRatio > newRatio) {
				newHeight = (newWidth * origHeight) / origWidth;
			} else if (origRatio < newRatio) {
				newWidth = (newHeight * origWidth) / origHeight;
			}
		}

		return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
	}

	/**
	 * Creates a cursor that can be used to determine how many images we have.
	 * 
	 * @return a cursor
	 */
	private Cursor queryImgDB() {
		return this.cordova.getActivity().getContentResolver().query(
				android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
				new String[] { MediaStore.Images.Media._ID }, null, null, null);
	}

	/**
	 * Used to find out if we are in a situation where the Camera Intent adds to
	 * images to the content store. If we are using a FILE_URI and the number of
	 * images in the DB increases by 2 we have a duplicate, when using a
	 * DATA_URL the number is 1.
	 */
	private void checkForDuplicateImage() {
		int diff = 2;
		Cursor cursor = queryImgDB();
		int currentNumOfImages = cursor.getCount();

		// delete the duplicate file if the difference is 2 for file URI or 1
		// for Data URL
		if ((currentNumOfImages - numPics) == diff) {
			cursor.moveToLast();
			int id = Integer.valueOf(cursor.getString(cursor
					.getColumnIndex(MediaStore.Images.Media._ID))) - 1;
			Uri uri = Uri.parse(MediaStore.Images.Media.EXTERNAL_CONTENT_URI
					+ "/" + id);
			this.cordova.getActivity().getContentResolver().delete(uri, null, null);
		}
	}

	/**
	 * Determine if we can use the SD Card to store the temporary file. If not
	 * then use the internal cache directory.
	 * 
	 * @return the absolute path of where to store the file
	 */
	private String getTempDirectoryPath(Context ctx) {
		File cache = null;

		// SD Card Mounted
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			cache = new File(Environment.getExternalStorageDirectory()
					.getAbsolutePath()
					+ "/Android/data/"
					+ ctx.getPackageName() + "/cache/");
		}
		// Use internal storage
		else {
			cache = ctx.getCacheDir();
		}

		// Create the cache directory if it doesn't exist
		if (!cache.exists()) {
			cache.mkdirs();
		}

		return cache.getAbsolutePath();
	}

	/**
	 * Queries the media store to find out what the file path is for the Uri we
	 * supply
	 * 
	 * @param contentUri
	 *            the Uri of the audio/image/video
	 * @param ctx
	 *            the current applicaiton context
	 * @return the full path to the file
	 */
	private String getRealPathFromURI(Uri contentUri, CordovaInterface ctx) {
		String[] proj = { _DATA };
		@SuppressWarnings("deprecation")
		Cursor cursor = cordova.getActivity().managedQuery(contentUri, proj, null, null, null);
		int column_index = cursor.getColumnIndexOrThrow(_DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}

	@Override
	public void onMediaScannerConnected() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onScanCompleted(String arg0, Uri arg1) {
		// TODO Auto-generated method stub
		
	}
}
