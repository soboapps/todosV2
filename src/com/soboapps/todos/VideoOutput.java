package com.soboapps.todos;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

public class VideoOutput extends Activity {
	
	
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.videoview);   


        MediaController mediaController = new MediaController(this);
        VideoView videoView = null;


        Intent launchingIntent = getIntent();
        String content = launchingIntent.getData().toString();

	    mediaController.setMediaPlayer(videoView);
	
	    videoView.setVideoPath(content);
	
	    videoView.setMediaController(mediaController);
	
	    videoView.requestFocus();
	
	    videoView.start();
	
	    mediaController.show();


    }

}
