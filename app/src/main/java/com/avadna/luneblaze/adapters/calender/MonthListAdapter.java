package com.avadna.luneblaze.adapters.calender;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.avadna.luneblaze.R;

import java.util.List;



public class MonthListAdapter extends BaseAdapter {
    private List<String> months;
    Activity activity;
    
    public MonthListAdapter(Activity activity, List<String> months) {
        this.months=months;
        this.activity=activity;
    }

    @Override
    public int getCount() {
        return months.size();
    }

    @Override
    public String getItem(int i) {
        return months.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MonthListAdapter.ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.simple_text_view, null);
            holder = new MonthListAdapter.ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (MonthListAdapter.ViewHolder) convertView.getTag();
        }
        holder.tv_item.setText(months.get(position));


        return convertView;
    }

    static class ViewHolder {
        TextView tv_item;

        ViewHolder(View view) {
            tv_item = view.findViewById(R.id.tv_item);
        }
    }


}