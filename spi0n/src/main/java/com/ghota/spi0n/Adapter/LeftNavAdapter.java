package com.ghota.spi0n.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ghota.spi0n.R;

/**
 * Created by Ghota on 16/02/14.
 */
public class LeftNavAdapter extends BaseAdapter {

    private String items[];
    private Context context;

    public LeftNavAdapter(Context context, String items[]) {
        this.context = context;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.length;
    }

    @Override
    public String getItem(int arg0) {
        return items[arg0];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = LayoutInflater.from(context).inflate(R.layout.left_nav_item, null);

        TextView lbl = (TextView) convertView;
        lbl.setText(getItem(position));

        if (position != items.length - 1)
            lbl.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_labels, 0, 0, 0);
        else
            lbl.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_star, 0, 0, 0);

        return lbl;
    }
}