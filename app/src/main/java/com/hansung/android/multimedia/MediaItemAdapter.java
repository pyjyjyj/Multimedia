package com.hansung.android.multimedia;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by B10722 on 2017-11-15.
 */

public class MediaItemAdapter extends BaseAdapter {
    private Context mContext;
    private int mResource;
    private ArrayList<MediaItem> mItems = new ArrayList<MediaItem>();


    public MediaItemAdapter(Context context, int resource, ArrayList<MediaItem> items) {
        mContext = context;
        mItems = items;
        mResource = resource;

    }

    public void addItem(MediaItem item) {
        mItems.add(item);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Object getItem(int i) {
        return mItems.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup parent) {
        if (view == null) {
            LayoutInflater inflater =
                    (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(mResource, parent,false);
        }
        TextView name = (TextView) view.findViewById(R.id.title);
        name.setText(mItems.get(i).name);

        // Set Text 02
        TextView source = (TextView) view.findViewById(R.id.source);
        switch(mItems.get(i).source) {
            case MediaItem.RAW:
                source.setText("RAW");
                break;
            case MediaItem.SDCARD:
                source.setText("SDCARD");
                break;
            case MediaItem.WEB:
                source.setText("WEB");
                break;

        }

        ImageView image = (ImageView) view.findViewById(R.id.image);
        if (mItems.get(i).type == MediaItem.AUDIO) {
            image.setImageResource(R.drawable.music);
        }else if (mItems.get(i).type == MediaItem.VIDEO)
            image.setImageResource(R.drawable.movie);
        else if (mItems.get(i).type == MediaItem.IMAGE)
            image.setImageBitmap(mItems.get(i).bitmap);
        return view;
    }
}
