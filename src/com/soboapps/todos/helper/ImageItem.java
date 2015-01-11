package com.soboapps.todos.helper;

import android.content.Context;
import android.view.View;

public class ImageItem extends DataItem {

    public String contentUri;

    public void setContenturi(String str){
        this.contentUri = str;
    }

    @Override
    public int getDataType() {
       // return corresponding content type
        return DataItem.DATA_TYPE_IMAGE;
    }

    @Override
    public String getContentUri() {
        // Return URI of image or video, to fetch information related to it
        return this.contentUri;
    }

@Override
public View getChildView(Context context,View convertView) {
    // return the view you want to display for this data item in grid

    // use content uri to fetch and populate Data and then generate view and return

    // use convertView is not null

    return null;
}

    static abstract class ImageData extends DataItem{
        // have some variables here which will define attributes of this Data item

        // in case of image

        String imageName;
        long takenData;

    }

}
