package com.avadna.luneblaze.adapters.session;

import android.app.Activity;
import android.content.Intent;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.activities.profileInfo.ProfileInfoActivity;
import com.avadna.luneblaze.pojo.pojoQuestion.PojoUserData;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

public class SessionAttendingFriendListAdapter extends RecyclerView.Adapter<SessionAttendingFriendListAdapter.MyViewHolder> {
    Activity activity;
    ApiInterface apiService;
    boolean enableApiCall = true;
    List<PojoUserData> friendList;
    FriendsAttendingListAdapterCallback friendsAttendingListAdapterCallback;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_dp;



        MyViewHolder(View view) {
            super(view);
            iv_dp = (ImageView) view.findViewById(R.id.iv_user_dp);
        }
    }

    public SessionAttendingFriendListAdapter(Activity activity,List<PojoUserData> friendList) {
        try {
            this.friendsAttendingListAdapterCallback = ((FriendsAttendingListAdapterCallback) activity);
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement AdapterCallback.");
        }
        this.activity = activity;
        this.friendList = friendList;
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);

    }

    public SessionAttendingFriendListAdapter(Fragment fragment, List<PojoUserData> friendList) {
        try {
            this.friendsAttendingListAdapterCallback = ((FriendsAttendingListAdapterCallback) fragment);
        } catch (ClassCastException e) {
            throw new ClassCastException("Fragment must implement AdapterCallback.");
        }
        this.activity = fragment.getActivity();
        this.friendList = friendList;
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);

    }

    @Override
    public SessionAttendingFriendListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.friends_attending_list_item, parent, false);

        return new SessionAttendingFriendListAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final SessionAttendingFriendListAdapter.MyViewHolder holder, final int position) {
        final PojoUserData currentItem=friendList.get(position);

        if(!currentItem.userPicture.isEmpty()){
            if(activity!=null) Glide.with(activity.getApplicationContext())
                    .load(currentItem.userPicture)
                    .apply(new RequestOptions().override(64,64))
                    .apply(bitmapTransform(new CropCircleTransformation()))
                    .into(holder.iv_dp);
        }

        holder.iv_dp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // friendsAttendingListAdapterCallback.onFriendsAttendingListMethodCallback(position);
                Intent profileIntent = new Intent(activity, ProfileInfoActivity.class);
                profileIntent.putExtra("target_user_id", currentItem.userId);
                activity.startActivity(profileIntent);
            }
        });
    }

    public static interface FriendsAttendingListAdapterCallback {
        void onFriendsAttendingListMethodCallback(int position);
    }

    @Override
    public int getItemCount() {
        return friendList.size();
        //  return hierarchyList.size();
    }
}

