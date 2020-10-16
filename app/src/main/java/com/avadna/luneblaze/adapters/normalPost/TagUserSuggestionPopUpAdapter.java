package com.avadna.luneblaze.adapters.normalPost;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.pojo.pojoQuestion.PojoUserData;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

public class TagUserSuggestionPopUpAdapter extends BaseAdapter {
    private List<PojoUserData> users;
    Activity activity;
    String type;


    public TagUserSuggestionPopUpAdapter(Activity activity, List<PojoUserData> users,String type) {
        this.activity=activity;
        this.users = users;
        this.type=type;
    }

    @Override
    public int getCount() {
        return users.size();
    }

    @Override
    public PojoUserData getItem(int i) {
        return users.get(i);
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

        holder.tv_user_name.setText(getItem(position).userFullname);
        if (type.equals("large")){
            holder.tv_designation.setVisibility(View.VISIBLE);
            holder.tv_designation.setText(getItem(position).userWorkTitle);
        }
        else {
            holder.tv_designation.setVisibility(View.GONE);
        }

        if(activity!=null) Glide.with(activity.getApplicationContext())
                .load(getItem(position).userPicture)
                .apply(new RequestOptions().override(96, 96))
                .apply(bitmapTransform(new CropCircleTransformation()))
                .into(holder.iv_user_dp);
        return convertView;
    }

    static class ViewHolder {
        TextView tv_user_name;
        ImageView iv_user_dp;
        TextView tv_designation;

        ViewHolder(View view) {
            tv_user_name = view.findViewById(R.id.tv_user_name);
            iv_user_dp = view.findViewById(R.id.iv_user_dp);
            tv_designation = view.findViewById(R.id.tv_designation);
        }
    }


}