package com.soboapps.todos;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ShareActionProvider;

import com.soboapps.todos.adapter.FullScreenImageAdapter;
import com.soboapps.todos.helper.Utils;

/**
 * Activity displaying the taken photo and offering to share it with other apps.
 *
 * @author Sebastian Kaspari <sebastian@androidzeitgeist.com>
 */
public class PhotoActivity extends Activity {
    private static final String MIME_TYPE = "image/jpeg";

    private Uri uri;
    
    private Utils utils;
    private FullScreenImageAdapter adapter;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        uri = getIntent().getData();

        setContentView(R.layout.activity_photo);

        ImageView photoView = (ImageView) findViewById(R.id.photo);
        photoView.setImageURI(uri);
        
        
		//viewPager = (ViewPager) findViewById(R.id.pager);
		utils = new Utils(getApplicationContext());
		adapter = new FullScreenImageAdapter(this, utils.getFilePaths());
		//viewPager.setAdapter(adapter);
		
		refresh();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_photo, menu);

        initializeShareAction(menu.findItem(R.id.share));

        return super.onCreateOptionsMenu(menu);
    }

    private void initializeShareAction(MenuItem shareItem) {
        ShareActionProvider shareProvider = (ShareActionProvider) shareItem.getActionProvider();

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.setType(MIME_TYPE);

        shareProvider.setShareIntent(shareIntent);
    }
    
    public void refresh() {
        adapter.notifyDataSetChanged();
        Intent intent=new Intent(getApplicationContext(),HiddenGalleryActivity.class);
        //viewPager.invalidate();
        startActivity(intent);
    }
}
