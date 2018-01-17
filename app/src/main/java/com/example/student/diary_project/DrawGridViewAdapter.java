package com.example.student.diary_project;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.util.List;


/**
 * Created by student on 2018-01-16.
 */

public class DrawGridViewAdapter extends BaseAdapter {
    private Context context;
    private int itemLayout;
    private List<Integer> colorList;

    public DrawGridViewAdapter(Context context, int itemLayout, List<Integer> colorList) {
        this.context = context;
        this.itemLayout = itemLayout;
        this.colorList = colorList;
    }

    @Override
    public int getCount() {
        return colorList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ImageView viewDrawColor;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(itemLayout, parent, false);

            viewDrawColor = (ImageView) convertView.findViewById(R.id.view_item_color);

            convertView.setTag(viewDrawColor);
        } else {
            viewDrawColor = (ImageView) convertView.getTag();
        }

        switch (colorList.get(position)) {
            case 0:
                viewDrawColor.setBackgroundResource(R.drawable.circlebuttoncolor1);
                break;
            case 1:
                viewDrawColor.setBackgroundResource(R.drawable.circlebuttoncolor2);
                break;
            case 2:
                viewDrawColor.setBackgroundResource(R.drawable.circlebuttoncolor3);
                break;
            case 3:
                viewDrawColor.setBackgroundResource(R.drawable.circlebuttoncolor4);
                break;
            case 4:
                viewDrawColor.setBackgroundResource(R.drawable.circlebuttoncolor5);
                break;
        }

        return convertView;
    }


}
