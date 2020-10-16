package com.avadna.luneblaze.adapters;

import android.app.Activity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.pojo.pojoQuestion.PojoUserData;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

public class ConversationAddedUserListAdapter extends RecyclerView.Adapter<ConversationAddedUserListAdapter.MyViewHolder> {
    Activity activity;
    ApiInterface apiService;
    boolean enableApiCall = true;
    List<PojoUserData> friendList;
    AddedRecipientListAdapterCallback addedRecipientListAdapterCallback;

    boolean showRemoveButton = false;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_username;
        ImageView iv_dp;
        ImageView iv_remove;
        LinearLayout ll_data_wrapper;


        MyViewHolder(View view) {
            super(view);
            ll_data_wrapper = (LinearLayout) view.findViewById(R.id.ll_data_wrapper);
            tv_username = (TextView) view.findViewById(R.id.tv_username);
            iv_dp = (ImageView) view.findViewById(R.id.iv_user_dp);
            iv_remove = (ImageView) view.findViewById(R.id.iv_remove);
        }
    }

    public void showRemoveButton(boolean status) {
        showRemoveButton = status;
        notifyDataSetChanged();
    }

    public ConversationAddedUserListAdapter(Activity activity, List<PojoUserData> friendList) {
        try {
            this.addedRecipientListAdapterCallback = ((ConversationAddedUserListAdapter.AddedRecipientListAdapterCallback) activity);
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement AdapterCallback.");
        }
        this.activity = activity;
        this.friendList = friendList;
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);

    }

    public ConversationAddedUserListAdapter(Fragment fragment, List<PojoUserData> friendList) {
        try {
            this.addedRecipientListAdapterCallback = ((ConversationAddedUserListAdapter.AddedRecipientListAdapterCallback) fragment);
        } catch (ClassCastException e) {
            throw new ClassCastException("Fragment must implement AdapterCallback.");
        }
        this.activity = fragment.getActivity();
        this.friendList = friendList;
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);

    }

    @Override
    public ConversationAddedUserListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.conversation_added_user_list_item, parent, false);

        return new ConversationAddedUserListAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ConversationAddedUserListAdapter.MyViewHolder holder, final int position) {
        PojoUserData currentItem = friendList.get(position);
        holder.tv_username.setText(currentItem.userFullname);

        if (!currentItem.userPicture.isEmpty()) {
            if(activity!=null) Glide.with(activity.getApplicationContext())
                    .load(currentItem.userPicture)
                    .apply(new RequestOptions().override(96, 96))
                    .apply(bitmapTransform(new CropCircleTransformation()))
                    .placeholder(R.drawable.blank_profile_male)
                    .into(holder.iv_dp);
        }

        if (showRemoveButton) {
            holder.iv_remove.setVisibility(View.VISIBLE);
            holder.iv_remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addedRecipientListAdapterCallback.onAddedRecipientListMethodCallback(position);
                }
            });
        } else {
            holder.iv_remove.setVisibility(View.GONE);
            holder.iv_dp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addedRecipientListAdapterCallback.onAddedRecipientListMethodCallback(position);
                }
            });
        }


    }

    public static interface AddedRecipientListAdapterCallback {
        void onAddedRecipientListMethodCallback(int position);
    }

    @Override
    public int getItemCount() {
        return friendList.size();
        //  return hierarchyList.size();
    }
}
