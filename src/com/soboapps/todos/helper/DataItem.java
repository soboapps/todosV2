package com.soboapps.todos.helper;

import android.content.Context;
import android.view.View;

// this is your main abstract class
public abstract class DataItem {

    // You can define your data types here
    public static final int DATA_TYPE_IMAGE = 0;
    public static final int DATA_TYPE_VIDEO = 1;

    // have all data item implement this
    public abstract int getDataType();
    public abstract String getContentUri();
    public abstract View getChildView(Context context,View convertView);

}
