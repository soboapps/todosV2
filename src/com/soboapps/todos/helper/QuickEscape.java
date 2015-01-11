package com.soboapps.todos.helper;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.widget.Toast;

import com.soboapps.todos.R;

public class QuickEscape extends Activity {
	
	String ExternalStorageDirectoryPath = Environment.getExternalStorageDirectory().getAbsolutePath();
    String farPath = ExternalStorageDirectoryPath + "/DCIM/ToDo/.nomedia/far";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fullscreen_view);
		
        Toast customToast = new Toast(getBaseContext());
    	customToast = Toast.makeText(getBaseContext(), "Press Back to Exit", Toast.LENGTH_SHORT);
    	customToast.setGravity(Gravity.CENTER|Gravity.CENTER, 0, 0);
    	customToast.show();
		
		Intent intent = new Intent();
	 	intent.setAction(Intent.ACTION_VIEW);
	 	intent.setDataAndType(Uri.parse("file://" + farPath + "/" + "/farside.png"), "image/*");
	 	startActivity(intent);
	 	finish();
	}	
	
	@Override
	public void onBackPressed() {
	    //onDestroy();
	    Intent intent = new Intent(Intent.ACTION_MAIN);
	    intent.addCategory(Intent.CATEGORY_HOME);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	    startActivity(intent);
	}
	
	@Override
	public void onDestroy() {
	    super.onDestroy();
	    android.os.Process.killProcess(android.os.Process.myPid());
	    getParent().finish();
	    finish();
	}
	
}
