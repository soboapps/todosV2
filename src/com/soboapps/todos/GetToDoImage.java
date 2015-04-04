package com.soboapps.todos;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Gravity;
import android.widget.Toast;

public class GetToDoImage extends Activity {
	
    static int random = (int)Math.ceil(Math.random()*100000000);
    private static String fname = Integer.toString(random);
    
    private static final int SELECT_PICTURE = 1;
    
    private static String selectedImagePath;
    private static String ExternalStorageDirectoryPath = Environment.getExternalStorageDirectory().getAbsolutePath();
    private static String targetPath = ExternalStorageDirectoryPath + "/DCIM/ToDo/.nomedia/";
 
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        	Intent getImageFromGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        	startActivityForResult(getImageFromGalleryIntent, SELECT_PICTURE);
        	//Intent intent = new Intent();
            //intent.setType("image/* video/*");
            //intent.setAction(Intent.ACTION_GET_CONTENT);
            //startActivityForResult(Intent.createChooser(intent,"Select File"), SELECT_PICTURE);
    }
 
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();
                selectedImagePath = getPath(selectedImageUri);
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
			     e.printStackTrace();
			    }
            }
        }
    }
    
    // Copy the file to the Hidden Folder 
    public void copyFile(String selectedImagePath2, String string) throws IOException {
        InputStream in = new FileInputStream(selectedImagePath2);
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
    	customToast = Toast.makeText(getBaseContext(), "Image Transferred", Toast.LENGTH_LONG);
    	customToast.setGravity(Gravity.CENTER|Gravity.CENTER, 0, 0);
    	customToast.show();
    	
    	//Restart the Hidden Gallery
        StartGallery();
        
        //Delete the File from Main Gallery
        deleteFile();
      }
 
    // Restart Hidden Gallery
    private void StartGallery() {
	    Intent intent = new Intent(this, HiddenGalleryActivity.class);
	    startActivity(intent);
	}

	public String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
    
	//Delete File from Main Gallery
    private void deleteFile() {
    	File fdelete = new File(selectedImagePath);
    	boolean fileDeleted =  fdelete.delete();
        //fdelete.delete();
        
    	// request scan    
    	Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
    	scanIntent.setData(Uri.fromFile(fdelete));
    	sendBroadcast(scanIntent);
	}
    
}