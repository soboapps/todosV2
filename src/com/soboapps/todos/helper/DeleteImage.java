package com.soboapps.todos.helper;

import java.util.ArrayList;

import com.soboapps.todos.R;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;

public class DeleteImage extends NoSearchActivity {
	
    @SuppressWarnings("unused")
    private static final String TAG = "DeleteImage";
    private ProgressBar mProgressBar;
    private ArrayList<Uri> mUriList;  // a list of image uri
    private int mIndex = 0;  // next image to delete
    private final Handler mHandler = new Handler();
    private final Runnable mDeleteNextRunnable = new Runnable() {
        public void run() {
            deleteNext();
        }
    };
    private ContentResolver mContentResolver;
    private boolean mPausing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        mUriList = intent.getParcelableArrayListExtra("delete-uris");
        if (mUriList == null) {
            finish();
        }
        setContentView(R.layout.delete_image);
        mProgressBar = (ProgressBar) findViewById(R.id.delete_progress);
        mContentResolver = getContentResolver();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPausing = false;
        mHandler.post(mDeleteNextRunnable);
    }

    private void deleteNext() {
        if (mPausing) return;
        if (mIndex >= mUriList.size()) {
            finish();
            return;
        }
        Uri uri = mUriList.get(mIndex++);
        // The max progress value of the bar is set to 10000 in the xml file.
        mProgressBar.setProgress(mIndex * 10000 / mUriList.size());
        mContentResolver.delete(uri, null, null);
        mHandler.post(mDeleteNextRunnable);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPausing = true;
    }
}