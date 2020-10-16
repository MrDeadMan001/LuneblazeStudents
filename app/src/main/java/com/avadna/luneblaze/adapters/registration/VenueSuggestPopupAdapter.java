package com.avadna.luneblaze.adapters.registration;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.avadna.luneblaze.R;
import com.avadna.luneblaze.pojo.pojoSearch.PojoSearchVenue;
import java.util.List;

public class VenueSuggestPopupAdapter extends BaseAdapter {
    private List<PojoSearchVenue> venues;
    Activity activity;


    public VenueSuggestPopupAdapter(Activity activity, List<PojoSearchVenue> venues) {
        this.activity = activity;
        this.venues = venues;
    }

    @Override
    public int getCount() {
        return venues.size();
    }

    @Override
    public PojoSearchVenue getItem(int i) {
        return venues.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        VenueSuggestPopupAdapter.ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.suggested_user_tag_item, null);
            holder = new VenueSuggestPopupAdapter.ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (VenueSuggestPopupAdapter.ViewHolder) convertView.getTag();
        }

        holder.tv_user_name.setText(getItem(position).venueName);

        return convertView;
    }

    static class ViewHolder {
        TextView tv_user_name;
        ImageView iv_user_dp;
        TextView tv_designation;

        ViewHolder(View view) {
            tv_user_name = view.findViewById(R.id.tv_user_name);
            iv_user_dp = view.findViewById(R.id.iv_user_dp);
            iv_user_dp.setVisibility(View.GONE);
            tv_designation = view.findViewById(R.id.tv_designation);
            tv_designation.setVisibility(View.GONE);
        }
    }


}