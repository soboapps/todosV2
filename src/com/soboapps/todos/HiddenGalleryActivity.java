package com.soboapps.todos;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ShareActionProvider;
import com.soboapps.todos.adapter.GridViewImageAdapter;
import com.soboapps.todos.helper.AppConstant;
import com.soboapps.todos.helper.QuickEscape;
import com.soboapps.todos.helper.Utils;

public class HiddenGalleryActivity extends Activity implements Shaker.Callback {
	
	File sdCardLoc = Environment.getExternalStorageDirectory();
	File intImagesDir = new File(sdCardLoc,"/DCIM/ToDo/.nomedia");
	File farDir = new File(sdCardLoc,"/DCIM/ToDo/.nomedia/far/");

	File[] imageList = intImagesDir.listFiles();

    String ExternalStorageDirectoryPath = Environment.getExternalStorageDirectory().getAbsolutePath();
    String targetPath = ExternalStorageDirectoryPath + "/DCIM/ToDo/.nomedia/";
    String farPath = ExternalStorageDirectoryPath + "/DCIM/ToDo/.nomedia/far";
    
    
	
	File targetDirector = new File(targetPath);
	
	private Shaker shaker=null;
	
	private ShareActionProvider mShareActionProvider;

	private Utils utils;
	private ArrayList<String> imagePaths = new ArrayList<String>();
	private GridViewImageAdapter adapter;
	private GridView gridView;
	private int columnWidth;
	
	private SensorManager sensorManager;
	Sensor accelerometerSensor;
	boolean accelerometerPresent;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_grid_view);
		
		shaker=new Shaker(this, 2.25F, 400, this);

		gridView = (GridView) findViewById(R.id.grid_view);

		utils = new Utils(this);

		// Initializing Grid View
		InitilizeGridLayout();

		// loading all image paths from SD card
		imagePaths = utils.getFilePaths();

		// Gridview adapter
		adapter = new GridViewImageAdapter(HiddenGalleryActivity.this, imagePaths, columnWidth);

		// setting grid view adapter
		gridView.setAdapter(adapter);	
		
        gridView.setOnItemClickListener(new OnItemClickListener() {
        	int mPosition;
        	
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            	this.mPosition = position;
            	String fName = imagePaths.get(mPosition);
            	File vName = new File(fName);
            	//Uri myUri = Uri.parse(fName);
            	
            	if(fName.contains("jpg") || fName.contains("jpeg") || fName.contains("png")) {
	                Intent i = new Intent(getApplicationContext(), FullScreenViewActivity.class);
	                //Toast.makeText(HiddenGalleryActivity.this, "" + fName, Toast.LENGTH_SHORT).show();
	                i.putExtra("position", mPosition);
	                startActivity(i);
            	}
            	
            	else if(fName.contains("mp4") || fName.contains("3gp")) {
            		Intent i = new Intent(Intent.ACTION_VIEW);
            		//Toast.makeText(HiddenGalleryActivity.this, "" + fName, Toast.LENGTH_SHORT).show();
            		i.setDataAndType(Uri.fromFile(vName), "video/*");
            		startActivity(i);
	             }
            }
                
        });
        

        // Trying to create a LongPress to delete files
    	/**
        gridView.setLongClickable(true);
    	gridView.setOnLongClickListener(new View.OnLongClickListener() {
    		int mPosition;
    		
    		public boolean onLongClick(AdapterView<?> parent, View view, int position, long id, View v) {
            	this.mPosition = position;
            	String fName = imagePaths.get(mPosition);
            	File vName = new File(fName);
                vName.delete();
                
    			startActivity(new Intent("com.soboapps.todos.PASSWORD"));
    			onDestroy();
    			return true;
    			
    		}

			@Override
			public boolean onLongClick(View v) {

			       final CharSequence[] items = {"Delete!", "Do you wish to delete the file?"};
			       AlertDialog.Builder builder = new AlertDialog.Builder(HiddenGalleryActivity.this);
			       builder.setItems(items, new DialogInterface.OnClickListener() {
			            public void onClick(DialogInterface dialog, int item) {
			                Toast.makeText(getApplicationContext(), items[item], Toast.LENGTH_SHORT).show();
			            }
			        });
			        AlertDialog alert = builder.create();
			        alert.show();

			        return true;
			}

    	});
        */
        
        
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

	private void InitilizeGridLayout() {
		//Get Device Screen Size
		DisplayMetrics metrics = this.getResources().getDisplayMetrics();
		int width = metrics.widthPixels;
		int height = metrics.heightPixels;
		 
		Resources r = getResources();
		float padding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, AppConstant.GRID_PADDING, r.getDisplayMetrics());

		if (width <= 1080) {
			columnWidth = (int) ((utils.getScreenWidth() - ((AppConstant.NUM_OF_COLUMNS + 1) * padding)) / AppConstant.NUM_OF_COLUMNS);	
			gridView.setNumColumns(AppConstant.NUM_OF_COLUMNS);
		} else {
			columnWidth = (int) ((utils.getScreenWidth() - ((AppConstant.TABLET_NUM_OF_COLUMNS + 1) * padding)) / AppConstant.TABLET_NUM_OF_COLUMNS);	
			gridView.setNumColumns(AppConstant.TABLET_NUM_OF_COLUMNS);	
		}
		gridView.setColumnWidth(columnWidth);
		gridView.setStretchMode(GridView.STRETCH_SPACING_UNIFORM);
		gridView.setPadding((int) padding, (int) padding, (int) padding, (int) padding);
		gridView.setHorizontalSpacing((int) padding);
		gridView.setVerticalSpacing((int) padding);
		gridView.setFastScrollEnabled(true);
		
	}
	
		// Listen for Volume Up or Down - 1
		@Override
	    public boolean dispatchKeyEvent(KeyEvent event) 
	    {
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
		   super.onResume();
		   if(accelerometerPresent){
		    sensorManager.registerListener(accelerometerListener, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);  
		   }
		  }

		  @Override
		  protected void onStop() {
		   super.onStop();
		   if(accelerometerPresent){
		    sensorManager.unregisterListener(accelerometerListener);  
		   }
		  }
		  
		  private SensorEventListener accelerometerListener = new SensorEventListener(){

		   @Override
		   public void onAccuracyChanged(Sensor arg0, int arg1) {
		    
		   }

		   @Override
		   public void onSensorChanged(SensorEvent arg0) {
		    float z_value = arg0.values[2];
		    if (z_value >= -10){
		     //face.setText("Face UP");
		    }
		    else{
	  			 imageSwitcher();
		     //face.setText("Face DOWN");
		    }
		   }};
	 
	 //On Shake
	public void shakingStarted() {
		imageSwitcher(); 
		super.onDestroy();
		}
	
	
	public void shakingStopped() {
		imageSwitcher();
		super.onDestroy();
		}
	
	//Image Switcher
	public void imageSwitcher(){
		Intent intent=new Intent(getApplicationContext(),QuickEscape.class);
		startActivity(intent);
        HiddenGalleryActivity.this.finish();
        android.os.Process.killProcess(android.os.Process.myPid());
        getParent().finish();
        finish();
        onDestroy();
	}
	
	
	// Menus
	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		super.onCreateOptionsMenu(menu);
		MenuInflater prefs = getMenuInflater();
		prefs.inflate(R.menu.prefs_menu, menu);
	    getMenuInflater().inflate(R.menu.menu_item_select, menu);
	
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){

		switch(item.getItemId()){
		case R.id.menuPrefs:
			startActivity(new Intent("com.soboapps.todos.PASSPREFS"));
			return true;
		case R.id.menuFake:
			startActivity(new Intent(HiddenGalleryActivity.this,FakePassPrefs.class));
			return true;
		case R.id.menuSetQE:
			startActivity(new Intent(HiddenGalleryActivity.this,SetQuickEscape.class));
			return true;
		case R.id.menuHelp:
			startActivity(new Intent(HiddenGalleryActivity.this,Help.class));
			return true;
		case R.id.menu_item_camera:
			startActivity(new Intent(HiddenGalleryActivity.this,CameraActivity.class));
			return true;	
		case R.id.menu_item_new:
			startActivity(new Intent("com.soboapps.todos.GETTODOIMAGE"));
			onDestroy();
			return true;	
		}
		return false;
	}

	
	@Override
	public void onDestroy() {
	    super.onDestroy();
	    android.os.Process.killProcess(android.os.Process.myPid());
	    shaker.close();
	}
}

