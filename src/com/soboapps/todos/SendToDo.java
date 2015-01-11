package com.soboapps.todos;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Gravity;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import com.soboapps.todos.helper.DeleteImage;

 public class SendToDo extends Activity {
	 
	 	private static final int SELECT_PICTURE = 0;
		//Generating Random Number to use as a unique file name
	 	static int random = (int)Math.ceil(Math.random()*100000000);
	    private static String fname = Integer.toString(random);
	    
	    private static String selectedImagePath;
	    //Getting the external Path to the Storage
	    private static String ExternalStorageDirectoryPath = Environment.getExternalStorageDirectory().getAbsolutePath();
	    //Setting a directory that is already created on the Storage to copy the file to.
	    private static String targetPath = ExternalStorageDirectoryPath + "/DCIM/ToDo/.nomedia/";
	 
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        
			//get the received intent
			Intent receivedIntent = getIntent();
			
			//get the action
			String receivedType = receivedIntent.getType();

			//make sure it's an action and type we can handle
				if(receivedType.startsWith("image/") || receivedType.startsWith("video/")){
					//get the uri of the received image
					Uri receivedUri = (Uri)receivedIntent.getParcelableExtra(Intent.EXTRA_STREAM);
					selectedImagePath = getPath(receivedUri);
					System.out.println("Image Path : " + selectedImagePath);
					//check we have a uri
					if (receivedUri != null) {
						//Copy the picture
							try {
								if(selectedImagePath.contains("jpg") || selectedImagePath.contains("jpeg") || selectedImagePath.contains("png")) {
								copyFile(selectedImagePath, targetPath + fname + ".jpg");
								}
								else if(selectedImagePath.contains("mp4")) {
								copyFile(selectedImagePath, targetPath + fname + ".mp4");
								}
								else if(selectedImagePath.contains("3gp")) {
								copyFile(selectedImagePath, targetPath + fname + ".3gp");
								}
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							startGallery();
							Intent intent = new Intent(SendToDo.this,DeleteImage.class);
							startActivity(intent);
							
							//deleteFile();
				        	onDestroy();
					}
				}	
		}
    
	    public void copyFile(String selectedImagePath, String string) throws IOException {
	        InputStream in = new FileInputStream(selectedImagePath);
	        OutputStream out = new FileOutputStream(string);
	  
	        // Transfer bytes from in to out
	        byte[] buf = new byte[1024];
	        int len;
	        while ((len = in.read(buf)) > 0) {
	            out.write(buf, 0, len);
	        }
	        in.close();
	        out.close();
            Toast customToast = new Toast(getBaseContext());
        	customToast = Toast.makeText(getBaseContext(), "File Transferred", Toast.LENGTH_LONG);
        	customToast.setGravity(Gravity.CENTER|Gravity.CENTER, 0, 0);
        	customToast.show();
	      }
	 
	    private void startGallery() {
	    	File fileCopied =  new File(targetPath + fname + ".jpg");
	    	Intent intent = new Intent();
	        intent.setType("image/* video/*");
	        intent.setAction(Intent.ACTION_GET_CONTENT);
	        intent.addCategory(Intent.CATEGORY_OPENABLE);
	        startActivityForResult(intent, SELECT_PICTURE);
	        
	    	// request scan    
	    	Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
	    	scanIntent.setData(Uri.fromFile(fileCopied));
	    	sendBroadcast(scanIntent);
		}
	    
	    // Delete the file that was copied over
	    private void deleteFile() {
	    	File fileToDelete =  new File(selectedImagePath);
	    	File fileCopied =  new File(targetPath + fname + ".jpg");
	    	boolean fileDeleted =  fileToDelete.delete();

	    	// request scan	    	
	    	Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
	    	scanIntent.setData(Uri.fromFile(fileToDelete));
	    	sendBroadcast(scanIntent);
			
	    	finish();
		}

		public String getPath(Uri uri) {
	        String[] projection = { MediaStore.Images.Media.DATA };
	        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
	        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
	        cursor.moveToFirst();
	        return cursor.getString(column_index);
	    }
		
		@Override
		public void onDestroy() {
		    super.onDestroy();
		    android.os.Process.killProcess(android.os.Process.myPid());
		    finish();
		    
		}

	}