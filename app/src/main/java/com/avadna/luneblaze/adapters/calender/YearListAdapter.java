package com.avadna.luneblaze.adapters.calender;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.avadna.luneblaze.R;

public class YearListAdapter extends BaseAdapter {
    private int[] years;
    Activity activity;

    public YearListAdapter(Activity activity, int[] years) {
        this.years=years;
        this.activity=activity;
    }

    @Override
    public int getCount() {
        return years.length;
    }

    @Override
    public String getItem(int i) {
        return String.valueOf(years[i]);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        YearListAdapter.ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.simple_text_view, null);
            holder = new YearListAdapter.ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (YearListAdapter.ViewHolder) convertView.getTag();
        }
        holder.tv_item.setText(""+years[position]);

        return convertView;
    }

    static class ViewHolder {
        TextView tv_item;

        ViewHolder(View view) {
            tv_item = view.findViewById(R.id.tv_item);
        }
    }


}