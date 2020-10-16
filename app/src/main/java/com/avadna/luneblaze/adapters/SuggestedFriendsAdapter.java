package com.avadna.luneblaze.adapters;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Point;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Html;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avadna.luneblaze.activities.profileInfo.ProfileInfoActivity;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.pojo.PojoUserConnectResponse;
import com.avadna.luneblaze.pojo.pojoQuestion.PojoUserData;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.bumptech.glide.Glide;
import com.avadna.luneblaze.R;
import com.avadna.luneblaze.rest.ApiInterface;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

/**
 * Created by Sunny on 23-01-2018.
 */

public class SuggestedFriendsAdapter extends RecyclerView.Adapter<SuggestedFriendsAdapter.MyViewHolder> {
    //  pivate List<String> hierarchyList;
    Activity activity;
    ApiInterface apiService;
    boolean enableApiCall = true;
    private List<PojoUserData> dataSet = new ArrayList<>();
    String orientation;
    String listType;
    DisplayMetrics displayMetrics;
    String user_id;
    PreferenceUtils preferenceUtils;
    CommonFunctions commonFunctions;

    //SuggestedFriendAdapterCallback suggestedFriendAdapterCallback;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_username;
        TextView tv_place_and_designation;
        TextView tv_mutual_friends;
        TextView tv_add_friend;
        ImageView iv_user_dp;
        RelativeLayout rl_parent;

        MyViewHolder(View view) {
            super(view);
            tv_username = (TextView) view.findViewById(R.id.tv_username);
            tv_place_and_designation = (TextView) view.findViewById(R.id.tv_place_and_designation);
            tv_mutual_friends = (TextView) view.findViewById(R.id.tv_num_followers);
            tv_add_friend = (TextView) view.findViewById(R.id.tv_add_friend);
            iv_user_dp = (ImageView) view.findViewById(R.id.iv_user_dp);
            rl_parent = (RelativeLayout) view.findViewById(R.id.rl_parent);
        }
    }

    public SuggestedFriendsAdapter(Fragment fragment, List<PojoUserData> dataSet,
                                   String orientation) {
        /*try {
            this.suggestedFriendAdapterCallback = ((SuggestedFriendAdapterCallback) fragment);
        } catch (ClassCastException e) {
            throw new ClassCastException("Fragment must implement DiscussionListAdapterCallback.");
        }*/
        this.activity = fragment.getActivity();
        this.dataSet = dataSet;
        this.apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
        this.orientation = orientation;
        this.listType = listType;
        displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        preferenceUtils = new PreferenceUtils(activity);
        commonFunctions = new CommonFunctions(activity);
        user_id = preferenceUtils.get_user_id();
    }

    public SuggestedFriendsAdapter(Activity activity, List<PojoUserData> dataSet,
                                   String orientation) {
       /* try {
            this.suggestedFriendAdapterCallback = ((SuggestedFriendAdapterCallback) activity);
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement DiscussionListAdapterCallback.");
        }*/
        this.activity = activity;
        this.dataSet = dataSet;
        this.apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
        this.orientation = orientation;
        this.listType = listType;
        displayMetrics = new DisplayMetrics();
        commonFunctions = new CommonFunctions(activity);

        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        preferenceUtils = new PreferenceUtils(activity);
        user_id = preferenceUtils.get_user_id();
    }


    @Override
    public int getItemViewType(int position) {
        // return position%2;
        if (orientation.equals("hor")) {
            return 0;
        } else {
            return 1;
        }
    }

    @Override
    public SuggestedFriendsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        switch (viewType) {
            case 0:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.suggested_friend_horizontal, parent, false);
                return new SuggestedFriendsAdapter.MyViewHolder(itemView);
            case 1:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.suggested_friends_list_item, parent, false);
                return new SuggestedFriendsAdapter.MyViewHolder(itemView);

            default:

                return null;
        }
    }

    @Override
    public void onBindViewHolder(final SuggestedFriendsAdapter.MyViewHolder holder, final int position) {
        final PojoUserData currentItem = dataSet.get(position);
        holder.tv_username.setText(currentItem.userFullname);
        if (activity != null) Glide.with(activity.getApplicationContext())
                .load(currentItem.userPicture)
                .apply(new RequestOptions().override(96, 96))
                .apply(bitmapTransform(new CropCircleTransformation()))
                .placeholder(R.drawable.blank_profile_male)
                .into(holder.iv_user_dp);

        holder.tv_username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent profileIntent = new Intent(activity,
                        ProfileInfoActivity.class);
                profileIntent.putExtra("target_user_id", currentItem.userId);
                activity.startActivity(profileIntent);
            }
        });

        holder.iv_user_dp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.tv_username.callOnClick();
            }
        });


        String placeAndDesignation = "";
        //if both fields are in data list then show both
        if (currentItem.userWorkPlace != null) {

            if ((!currentItem.userWorkTitle.isEmpty()) && (!currentItem.userWorkPlace.isEmpty())) {
                placeAndDesignation = currentItem.userWorkTitle + " (" + currentItem.userWorkPlace + ")";
            }
            //if work title not available show only work place
            else if (currentItem.userWorkTitle.isEmpty()) {
                placeAndDesignation = currentItem.userWorkPlace;
            }
            //if work place not available show only work title
            else if (currentItem.userWorkPlace.isEmpty()) {
                placeAndDesignation = currentItem.userWorkTitle;
            }
            //if none is present then remove visibility of the field
            else if (currentItem.userWorkTitle.isEmpty() && currentItem.userWorkPlace.isEmpty()) {
                holder.tv_place_and_designation.setVisibility(View.GONE);
            }
            holder.tv_place_and_designation.setText(Html.fromHtml(placeAndDesignation));
        }

        Display display = activity.getWindowManager().getDefaultDisplay();
        Point nameSize = new Point();
        display.getSize(nameSize);
        holder.tv_username.measure(nameSize.x, nameSize.y);

        Point desigSize = new Point();
        display.getSize(desigSize);
        holder.tv_place_and_designation.measure(desigSize.x, desigSize.y);

        Paint paint = new Paint();
        paint.setTextSize(holder.tv_username.getTextSize());
        paint.setTypeface(holder.tv_username.getTypeface());

        float textViewWidthPx = holder.tv_username.getMeasuredWidth();
        int userLineCount = commonFunctions.splitWordsIntoStringsThatFit(holder.tv_username.getText().toString(),
                textViewWidthPx, paint);

        paint.setTextSize(holder.tv_place_and_designation.getTextSize());
        paint.setTypeface(holder.tv_place_and_designation.getTypeface());

        textViewWidthPx = holder.tv_place_and_designation.getMeasuredWidth();
        int desigLineCount = commonFunctions.splitWordsIntoStringsThatFit(holder.tv_place_and_designation.getText().toString(),
                textViewWidthPx, paint);

        if (userLineCount+desigLineCount >3) {
            holder.tv_username.setMaxLines(2);
            holder.tv_username.setEllipsize(TextUtils.TruncateAt.END);
            holder.tv_place_and_designation.setMaxLines(1);
            holder.tv_place_and_designation.setEllipsize(TextUtils.TruncateAt.END);
        } else {
            holder.tv_username.setMaxLines(2);
            holder.tv_username.setEllipsize(TextUtils.TruncateAt.END);
            holder.tv_place_and_designation.setMaxLines(2);
            holder.tv_place_and_designation.setEllipsize(TextUtils.TruncateAt.END);
        }

        if (currentItem.mutualFriendsCount == null || currentItem.mutualFriendsCount.equals("0")) {
            holder.tv_mutual_friends.setVisibility(View.GONE);
        } else {
            holder.tv_mutual_friends.setVisibility(View.VISIBLE);
            holder.tv_mutual_friends.setText(currentItem.mutualFriendsCount + " Mutual Friends");
        }


       /* Display display = activity.getWindowManager().getDefaultDisplay();
        Point nameSize = new Point();
        display.getSize(nameSize);
        holder.tv_username.measure(nameSize.x, nameSize.y);
        int nameHeight = holder.tv_username.getMeasuredHeight();

        Point desigSize = new Point();
        display.getSize(desigSize);
        holder.tv_place_and_designation.measure(desigSize.x, desigSize.y);
        int desigHeight = holder.tv_place_and_designation.getMeasuredHeight();

        int heightThreshold = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 52, displayMetrics);

        if (nameHeight + desigHeight > heightThreshold) {
            holder.tv_username.setMaxLines(2);
            holder.tv_username.setEllipsize(TextUtils.TruncateAt.END);
            holder.tv_place_and_designation.setMaxLines(1);
            holder.tv_place_and_designation.setEllipsize(TextUtils.TruncateAt.END);
        } else {
            holder.tv_username.setMaxLines(2);
            holder.tv_username.setEllipsize(TextUtils.TruncateAt.END);
            holder.tv_place_and_designation.setMaxLines(2);
            holder.tv_place_and_designation.setEllipsize(TextUtils.TruncateAt.END);
        }

        if (currentItem.mutualFriendsCount == null || currentItem.mutualFriendsCount.equals("0")) {
            holder.tv_mutual_friends.setVisibility(View.GONE);
        } else {
            holder.tv_mutual_friends.setVisibility(View.VISIBLE);
            holder.tv_mutual_friends.setText(currentItem.mutualFriendsCount + " Mutual Friends");
        }*/

        if (currentItem.connection != null) {
            if (currentItem.connection.equals("add")) {
                holder.tv_add_friend.setVisibility(View.VISIBLE);
                holder.tv_add_friend.setText(activity.getString(R.string.add_fellow));
                holder.tv_add_friend
                        .setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_add_fellow, 0, 0, 0);
            } else if (currentItem.connection.equals("me") || currentItem.userId.equals(user_id)) {
                holder.tv_add_friend.setText(activity.getString(R.string.me));
                holder.tv_add_friend.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            } else if (currentItem.connection.equals("remove")) {
                holder.tv_add_friend.setVisibility(View.VISIBLE);
                holder.tv_add_friend
                        .setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_attend_filled, 0, 0, 0);
                holder.tv_add_friend.setText(activity.getString(R.string.fellow));
            } else if (currentItem.connection.equals("followed")) {
                holder.tv_add_friend.setVisibility(View.VISIBLE);
                holder.tv_add_friend
                        .setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_attend_filled, 0, 0, 0);
                holder.tv_add_friend.setText(activity.getString(R.string.following));
            } else if (currentItem.connection.equals("request")) {
                holder.tv_add_friend.setVisibility(View.VISIBLE);
                holder.tv_add_friend
                        .setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_yes_tick, 0, 0, 0);
                holder.tv_add_friend.setText(activity.getString(R.string.accept));
            } else if (currentItem.connection.equals("cancel")) {
                holder.tv_add_friend.setVisibility(View.VISIBLE);
                holder.tv_add_friend
                        .setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_thanks_filled, 0, 0, 0);
                holder.tv_add_friend.setText(activity.getString(R.string.requested));
            }
        } else {
            holder.tv_add_friend.setVisibility(View.VISIBLE);
            holder.tv_add_friend
                    .setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_add_fellow, 0, 0, 0);
            holder.tv_add_friend.setText(activity.getString(R.string.add_fellow));
        }

        holder.tv_add_friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  suggestedFriendAdapterCallback.onFriendButtonMethod(position,currentItem,listType,"add_clicked");
                if (dataSet.get(position).connection != null) {
                    if (dataSet.get(position).connection.equals("add")) {
                        hitAddFriendApi(user_id, currentItem.userId, "friend-add");
                        dataSet.get(position).connection = "cancel";
                    } else if (dataSet.get(position).connection.equals("remove")) {
                        hitCancelFriendApi(user_id, currentItem.userId, "friend-remove");
                        dataSet.get(position).connection = "add";
                    } else if (currentItem.connection.equals("followed")) {
                        hitCancelFriendApi(user_id, currentItem.userId, "unfollow");
                        dataSet.get(position).connection = "add";
                    } else if (dataSet.get(position).connection.equals("request")) {
                        hitCancelFriendApi(user_id, currentItem.userId, "friend-accept");
                        dataSet.get(position).connection = "remove";
                    } else if (dataSet.get(position).connection.equals("cancel")) {
                        hitCancelFriendApi(user_id, currentItem.userId, "friend-cancel");
                        dataSet.get(position).connection = "add";
                    }
                } else {
                    hitAddFriendApi(user_id, currentItem.userId, "friend-add");
                    dataSet.get(position).connection = "cancel";
                }
                notifyDataSetChanged();
            }
        });

       /* if (!orientation.equals("hor")) {
            if (position == (dataSet.size() - 1)) {
                Resources r = activity.getResources();
                int DP64 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 64, r.getDisplayMetrics());
                int DP2 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, r.getDisplayMetrics());
                holder.rl_parent.setPadding(0, 0, 0, DP64);
            } else {
                holder.rl_parent.setPadding(0, 0, 0, 0);
            }
        }*/
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
        //  return hierarchyList.size();
    }


    /*public static interface SuggestedFriendAdapterCallback {
        void onFriendButtonMethod(int position, PojoUserData userData, String listType , String type);
    }*/

    public void hitAddFriendApi(String user_id, String friend_id, String connection_type) {


        Call<PojoUserConnectResponse> call = apiService.addFriendApi(user_id, friend_id, connection_type);
        call.enqueue(new Callback<PojoUserConnectResponse>() {
            @Override
            public void onResponse(Call<PojoUserConnectResponse> call, Response<PojoUserConnectResponse> response) {

                if (response.body() != null) {
                    String message = response.body().message;
                    if (response.body().status == 1) {
                        removeUserFromSuggestedInPref(friend_id);
                    }
                }
            }

            @Override
            public void onFailure(Call<PojoUserConnectResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(activity, t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
            }
        });
    }

    private void removeUserFromSuggestedInPref(String user_id) {
        List<PojoUserData> tempList = preferenceUtils.getSuggestedUserList();
        for (int i = 0; i < tempList.size(); i++) {
            if (user_id.equals(tempList.get(i).userId)) {
                tempList.remove(i);
                preferenceUtils.saveSuggestedUserList(tempList);
                break;
            }
        }
    }

    public void hitCancelFriendApi(String user_id, String friend_id, String connection_type) {

        Call<PojoUserConnectResponse> call = apiService.changeUserConnectionApi(user_id, friend_id, connection_type);
        call.enqueue(new Callback<PojoUserConnectResponse>() {
            @Override
            public void onResponse(Call<PojoUserConnectResponse> call, Response<PojoUserConnectResponse> response) {

                if (response.body() != null) {
                    String message = response.body().message;
                    if (response.body().data != null) {
                        if (response.body().status.equals("1")) {

                        } else {
                            commonFunctions.setToastMessage(activity, "" + message, Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<PojoUserConnectResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(activity, t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
            }
        });

    }
}

