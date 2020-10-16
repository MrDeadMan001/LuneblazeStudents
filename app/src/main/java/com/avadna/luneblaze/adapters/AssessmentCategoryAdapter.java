package com.avadna.luneblaze.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.avadna.luneblaze.R;
import com.avadna.luneblaze.pojo.assessment.PojoAssessmentTypeResponseData;
import com.avadna.luneblaze.pojo.pojoSearch.PojoSearchVenue;
import java.util.List;

public class AssessmentCategoryAdapter extends BaseAdapter {
    private List<PojoAssessmentTypeResponseData> categories;
    Activity activity;


    public AssessmentCategoryAdapter(Activity activity, List<PojoAssessmentTypeResponseData> categories) {
        this.activity = activity;
        this.categories = categories;
    }

    @Override
    public int getCount() {
        return categories.size();
    }

    @Override
    public String getItem(int i) {
        return categories.get(i).name;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.suggested_user_tag_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tv_user_name.setText(getItem(position));

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