package com.avadna.luneblaze.adapters.organisation;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.pojo.pojoOrganisation.PojoGetCitiesResponseData;

import java.util.List;

public class CitySuggestPopUpAdapter extends BaseAdapter {
    private List<PojoGetCitiesResponseData> cities;
    Activity activity;


    public CitySuggestPopUpAdapter(Activity activity, List<PojoGetCitiesResponseData> cities) {
        this.activity = activity;
        this.cities = cities;
    }

    @Override
    public int getCount() {
        return cities.size();
    }

    @Override
    public PojoGetCitiesResponseData getItem(int i) {
        return cities.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CitySuggestPopUpAdapter.ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.suggested_user_tag_item, null);
            holder = new CitySuggestPopUpAdapter.ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (CitySuggestPopUpAdapter.ViewHolder) convertView.getTag();
        }

        holder.tv_user_name.setText(getItem(position).cityname);

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