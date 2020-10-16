package com.avadna.luneblaze.adapters.search;

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

public class UserSearchPopUpAdapter extends BaseAdapter {
    private List<PojoUserData> users;
    Activity activity;

    public UserSearchPopUpAdapter(Activity activity, List<PojoUserData> users) {

        this.activity=activity;
        this.users = users;
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
        UserSearchPopUpAdapter.ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.suggested_user_tag_item, null);
            holder = new UserSearchPopUpAdapter.ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (UserSearchPopUpAdapter.ViewHolder) convertView.getTag();
        }

        holder.tv_user_name.setText(getItem(position).userFullname);
        if(activity!=null) Glide.with(activity.getApplicationContext())
                .load(getItem(position).userPicture)
                .apply(new RequestOptions().override(128, 128))
                .apply(bitmapTransform(new CropCircleTransformation()))
                .into(holder.iv_user_dp);
        return convertView;
    }

    static class ViewHolder {
        TextView tv_user_name;
        ImageView iv_user_dp;

        ViewHolder(View view) {
            tv_user_name = view.findViewById(R.id.tv_user_name);
            iv_user_dp = view.findViewById(R.id.iv_user_dp);
        }
    }


}