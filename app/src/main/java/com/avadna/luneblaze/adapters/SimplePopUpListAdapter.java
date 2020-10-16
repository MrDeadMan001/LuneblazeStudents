package com.avadna.luneblaze.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.avadna.luneblaze.R;

import java.util.List;

public class SimplePopUpListAdapter extends BaseAdapter {
    private List<String> items;
    Activity activity;


    public SimplePopUpListAdapter(Activity activity, List<String> items) {
        this.activity = activity;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public String getItem(int i) {
        return items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SimplePopUpListAdapter.ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.simple_list_textview, null);
            holder = new SimplePopUpListAdapter.ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (SimplePopUpListAdapter.ViewHolder) convertView.getTag();
        }

        holder.tv_item.setText(getItem(position));

        return convertView;
    }

    static class ViewHolder {
        TextView tv_item;

        ViewHolder(View view) {
            tv_item = view.findViewById(R.id.tv_item);

        }
    }
}


