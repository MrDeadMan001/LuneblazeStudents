package com.avadna.luneblaze.adapters;

import android.app.Activity;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.pojo.PojoPhoneContacts;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

public class PhoneContactsListAdapter extends RecyclerView.Adapter<PhoneContactsListAdapter.MyViewHolder> {
    ArrayList<PojoPhoneContacts> contacts;
    Activity activity;

    PhoneContactsListAdapterCallback phoneContactsListAdapterCallback;

    public PhoneContactsListAdapter(Fragment fragment, ArrayList<PojoPhoneContacts> contacts) {
        try {
            this.phoneContactsListAdapterCallback = ((PhoneContactsListAdapterCallback) fragment);
        } catch (ClassCastException e) {
            throw new ClassCastException("Fragment must implement PhoneContactsListAdapterCallback.");
        }
        this.activity = fragment.getActivity();
        this.contacts = contacts;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_user_dp;
        TextView tv_user_thumb;
        TextView tv_invite;
        TextView tv_request_sent;
        TextView tv_user_name;
        TextView tv_phone_number;
        TextView tv_email_id;
        CheckBox cb_checked;

        MyViewHolder(View view) {
            super(view);
            iv_user_dp = (ImageView) view.findViewById(R.id.iv_user_dp);
            tv_user_thumb = (TextView) view.findViewById(R.id.tv_user_thumb);
            tv_invite = (TextView) view.findViewById(R.id.tv_invite);
            tv_request_sent = (TextView) view.findViewById(R.id.tv_request_sent);
            tv_user_name = (TextView) view.findViewById(R.id.tv_user_name);
            tv_phone_number = (TextView) view.findViewById(R.id.tv_phone_number);
            tv_email_id = (TextView) view.findViewById(R.id.tv_email_id);
            cb_checked = (CheckBox) view.findViewById(R.id.cb_checked);
        }
    }

    @Override
    public PhoneContactsListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.phone_contacts_list_item, parent, false);

        return new PhoneContactsListAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PhoneContactsListAdapter.MyViewHolder holder, final int position) {
        //   holder.tv_hierarchy_item.setText(hierarchyList.get(position));


        PojoPhoneContacts currentItem = contacts.get(position);
        holder.tv_user_name.setText(currentItem.getContactName());
        holder.tv_phone_number.setText(currentItem.getContactNumber());
        holder.tv_email_id.setText(currentItem.getContactEmail());

        if (currentItem.invited) {
            holder.tv_invite.setText(activity.getString(R.string.invited));
            holder.tv_invite.setTextColor(ContextCompat.getColor(activity, R.color.med_grey));
        } else {
            holder.tv_invite.setText("+" + activity.getString(R.string.invite));
            holder.tv_invite.setTextColor(ContextCompat.getColor(activity, R.color.app_theme_dark));
        }

        holder.tv_invite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                phoneContactsListAdapterCallback.phoneContactsListClickCallback(position);
            }
        });


        if(currentItem.getContactPhoto()!=null&&!currentItem.getContactPhoto().isEmpty()){
            holder.tv_user_thumb.setVisibility(View.GONE);
            holder.iv_user_dp.setVisibility(View.VISIBLE);
            if(activity!=null) Glide.with(activity.getApplicationContext()).load(currentItem.getContactPhoto())
                    .apply(new RequestOptions().override(128,128))
                    .apply(bitmapTransform(new CropCircleTransformation()))
                    .into(holder.iv_user_dp);
        }
        else {
            holder.tv_user_thumb.setVisibility(View.VISIBLE);
            holder.iv_user_dp.setVisibility(View.GONE);
            if(!currentItem.getContactName().isEmpty()){
                holder.tv_user_thumb.setText(""+currentItem.getContactName().charAt(0));
            }
        }

       /* if(currentItem.checked){
            holder.cb_checked.setChecked(true);
        }
        else {
            holder.cb_checked.setChecked(false);
        }*/
    }

    @Override
    public int getItemCount() {
        return contacts.size();
        //  return hierarchyList.size();
    }

    public static interface PhoneContactsListAdapterCallback {
        void phoneContactsListClickCallback(int position);
    }
}


