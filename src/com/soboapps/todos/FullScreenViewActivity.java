package com.soboapps.todos;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ShareActionProvider;
import android.widget.Toast;

import com.soboapps.todos.adapter.FullScreenImageAdapter;
import com.soboapps.todos.helper.QuickEscape;
import com.soboapps.todos.helper.Utils;
import com.soboapps.todos.photoeditor.CropImage;

public class FullScreenViewActivity extends Activity {
	
 	static int random = (int)Math.ceil(Math.random()*100000000);
    private static String fname = Integer.toString(random);
    
    private static final int SELECT_PICTURE = 1;
	protected static final String TAG = null;
	
    public static final String TEMP_PHOTO_FILE_NAME = "temp_photo.jpg";
    
    public static final int REQUEST_CODE_GALLERY      = 0x1;
    public static final int REQUEST_CODE_TAKE_PICTURE = 0x2;
    public static final int REQUEST_CODE_CROP_IMAGE   = 0x3;
    
    private File mFileTemp;
	
	public static String selectedImagePath;
	public static Intent selectedImagePathIntent;
    public static String ExternalStorageDirectoryPath = Environment.getExternalStorageDirectory().getAbsolutePath();
    public static String restorePath = ExternalStorageDirectoryPath + "/DCIM/Download/";

	private Utils utils;
	private FullScreenImageAdapter adapter;
	private ViewPager viewPager;
	private ShareActionProvider mShareActionProvider;
	
	SensorManager sensorManager;
	Sensor accelerometerSensor;
	boolean accelerometerPresent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fullscreen_view);
		ActionBar actionBar = getActionBar();
		actionBar.setIcon(R.drawable.unlock);

		viewPager = (ViewPager) findViewById(R.id.pager);

		utils = new Utils(getApplicationContext());

		Intent i = getIntent();
		int position = i.getIntExtra("position", 0);

		adapter = new FullScreenImageAdapter(FullScreenViewActivity.this, utils.getFilePaths());

		viewPager.setAdapter(adapter);

		// displaying selected image first
		viewPager.setCurrentItem(position);
		
		viewPager.getCurrentItem();
		
		selectedImagePath = adapter.mImagePaths.get(viewPager.getCurrentItem());
		
		//Selected image as String
    	String file = selectedImagePath;
    	
    	//Selected image as File
    	File sFile = new File (file);
    	
    	String state = Environment.getExternalStorageState();
    	if (Environment.MEDIA_MOUNTED.equals(state)) {
    		mFileTemp = new File(Environment.getExternalStorageDirectory(), TEMP_PHOTO_FILE_NAME);
    	}
    	else {
    		mFileTemp = new File(getFilesDir(), TEMP_PHOTO_FILE_NAME);
    	}
    	
        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        List<Sensor> sensorList = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
        if(sensorList.size() > 0){
         accelerometerPresent = true;
         accelerometerSensor = sensorList.get(0);  
        }
        else{
         accelerometerPresent = false;  
         Log.d("No accelerometer detected", null);
         //face.setText("No accelerometer present!");
        }
    }

	
		// Listen for Volume Up or Down - 1
	@Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN)
        {
            switch (event.getKeyCode()) 
            {
                case KeyEvent.KEYCODE_VOLUME_UP:
	       			 imageSwitcher();
	    	    	 super.onDestroy();
	                    return true;
                case KeyEvent.KEYCODE_VOLUME_DOWN:
	       			 imageSwitcher();
	    	    	 super.onDestroy();
                    return true;
            }
        }

        return super.dispatchKeyEvent(event);
    }
	
	  //Proximity Sensor
	  @Override
	  protected void onResume() {
	   // TODO Auto-generated method stub
	   super.onResume();
	   if(accelerometerPresent){
	    sensorManager.registerListener(accelerometerListener, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);  
	   }
	  }

	  @Override
	  protected void onStop() {
	   // TODO Auto-generated method stub
	   super.onStop();
	   if(accelerometerPresent){
	    sensorManager.unregisterListener(accelerometerListener);  
	   }
	  }
	  
	  private SensorEventListener accelerometerListener = new SensorEventListener(){

	   @Override
	   public void onAccuracyChanged(Sensor arg0, int arg1) {
	    // TODO Auto-generated method stub
	    
	   }

	   @Override
	   public void onSensorChanged(SensorEvent arg0) {
	    // TODO Auto-generated method stub
	    float z_value = arg0.values[2];
	    if (z_value >= -10){
	     //face.setText("Face UP");
	    }
	    else{
  			 imageSwitcher();
	     //face.setText("Face DOWN");
	    }
	   }};
	
	//Image Switcher
	public void imageSwitcher(){
		Intent intent=new Intent(getApplicationContext(),QuickEscape.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivity(intent);
	    android.os.Process.killProcess(android.os.Process.myPid());
	    getParent().finish();
	    finish();
	    onDestroy();
	}

	// Menus
	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		super.onCreateOptionsMenu(menu);
	    getMenuInflater().inflate(R.menu.menu_item_actions, menu);
	    
	    // Locate MenuItem with ShareActionProvider
	    MenuItem item = (MenuItem) menu.findItem(R.id.menu_item_share);
	    
	    // Fetch and store ShareActionProvider
	    ShareActionProvider mshare = new ShareActionProvider (FullScreenViewActivity.this);
	    
	    Intent shareIntent = new Intent(Intent.ACTION_SEND);
	    shareIntent.setType("image/*");
		shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(selectedImagePath)));
	    mshare.setShareIntent(shareIntent);

	    return true;    
	    
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch(item.getItemId()){

		case R.id.menu_item_discard:
			deleteFile();
			return true;
			
		case R.id.menu_item_crop:
			runCropImage();
			return true;

		case R.id.menu_item_restore:
			try {
				
				restoreFile(selectedImagePath, restorePath + fname + ".jpg");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return true;	
		}
		return false;

	}

    public void restoreFile(String selectedImagePath, final String restorePath) throws IOException {
    	final String selectedImagePath1 = adapter.mImagePaths.get(viewPager.getCurrentItem());
		
		new AlertDialog.Builder(this)
		.setIcon(R.drawable.undo)
		.setTitle(R.string.restoreConfirmationTitle)
		.setMessage(R.string.restoreConfirmationString)
		.setPositiveButton(R.string.restoreConfirmationStringYes, new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				String restorePath1 = restorePath;	    
				File refreshFile = new File(restorePath1);
				
				//Restore the File back to standard Gallery 
				InputStream in = null;
				try {
					in = new FileInputStream(selectedImagePath1);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				OutputStream out = null;
				try {
					out = new FileOutputStream(restorePath1);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	  
				// Transfer bytes from in to out
				byte[] buf = new byte[1024];
				int len;
				try {
					while ((len = in.read(buf)) > 0) {
						out.write(buf, 0, len);
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					in.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					out.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					
				}
				
				// Delete File after restore
				String fileDelete = selectedImagePath1;
				File fdelete = new File(fileDelete);
				boolean deleted = fdelete.delete();
					if(deleted == false){
						Toast customToast1 = new Toast(getBaseContext());
						customToast1 = Toast.makeText(getBaseContext(), "Restore Failed", Toast.LENGTH_LONG);
						customToast1.setGravity(Gravity.CENTER|Gravity.CENTER, 0, 0);
						customToast1.show();
					}else{
						Toast customToast1 = new Toast(getBaseContext());
						customToast1 = Toast.makeText(getBaseContext(), "Image Restored", Toast.LENGTH_LONG);
						Log.d(TAG, restorePath);
						customToast1.setGravity(Gravity.CENTER|Gravity.CENTER, 0, 0);
						customToast1.show();
						viewPager.setAdapter(adapter);			    	
						refresh();
						
						// request scan
				    	Intent intent = new Intent();
				        intent.setType("image/*");
				        intent.setAction(Intent.ACTION_GET_CONTENT);
				        intent.addCategory(Intent.CATEGORY_OPENABLE);
				        startActivityForResult(intent, SELECT_PICTURE);
						Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
						scanIntent.setData(Uri.fromFile(refreshFile));
						sendBroadcast(scanIntent);
						
						refresh();
				}
					
			}
			
		})
		.setNegativeButton(R.string.deleteConfirmationStringNo, null)
		.show();
	}
    
		private void deleteFile(){
		//selectedImagePath = adapter.mImagePaths.get(viewPager.getCurrentItem());
	
		new AlertDialog.Builder(this)
		.setIcon(R.drawable.discard_button)
        .setTitle(R.string.deleteConfirmationTitle)
        .setMessage(R.string.deleteConfirmationString)
        .setPositiveButton(R.string.deleteConfirmationStringYes, new DialogInterface.OnClickListener() {        	
            @Override
            public void onClick(DialogInterface dialog, int which) {
            	String fileDelete = selectedImagePath;
				File fdelete = new File(fileDelete);
				boolean deleted = fdelete.delete();
					if(deleted == false){
				        Toast customToast = new Toast(getBaseContext());
				    	customToast = Toast.makeText(getBaseContext(), "Image NOT Removed", Toast.LENGTH_LONG);
				    	customToast.setGravity(Gravity.CENTER|Gravity.CENTER, 0, 0);
				    	customToast.show();
					}else{
				        Toast customToast = new Toast(getBaseContext());
				    	customToast = Toast.makeText(getBaseContext(), "Image Removed", Toast.LENGTH_LONG);
				    	customToast.setGravity(Gravity.CENTER|Gravity.CENTER, 0, 0);
				    	customToast.show();
				    	viewPager.setAdapter(adapter);	
				    	
						// request scan    
				    	Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
				    	scanIntent.setData(Uri.fromFile(fdelete));
				    	sendBroadcast(scanIntent);
				    	
				    	refresh();
					}
				}    

        })
        .setNegativeButton(R.string.deleteConfirmationStringNo, null)
        .show();
		}
		
		private void runCropImage() {

		    // create explicit intent
		    Intent intent = new Intent(this, CropImage.class);

		    // tell CropImage activity to look for image to crop 
		    String filePath = selectedImagePath;
		    intent.putExtra(CropImage.IMAGE_PATH, filePath);

		    // allow CropImage activity to rescale image
		    intent.putExtra(CropImage.SCALE, true);

		    // if the aspect ratio is fixed to ratio 3/2
		    intent.putExtra(CropImage.ASPECT_X, 3);
		    intent.putExtra(CropImage.ASPECT_Y, 2);

		    // start activity CropImage with certain request code and listen
		    // for result
		    startActivityForResult(intent, REQUEST_CODE_CROP_IMAGE);
		}
	    
		@Override
		protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		    if (resultCode != RESULT_OK) {

		        return;
		    }  

		    switch (requestCode) {

		        case REQUEST_CODE_CROP_IMAGE:

		            String path = data.getStringExtra(CropImage.IMAGE_PATH);

		            // if nothing received
		            if (path == null) {

		                return;
		            }

		            // cropped bitmap
		            Bitmap bitmap = BitmapFactory.decodeFile(mFileTemp.getPath());

		            break;
		    }
		    super.onActivityResult(requestCode, resultCode, data);
		    refresh();
		}
		
	    public void refresh() {
	        adapter.notifyDataSetChanged();
	        Intent intent=new Intent(getApplicationContext(),HiddenGalleryActivity.class);
	        viewPager.invalidate();
	        startActivity(intent);
	    }
	    

}
