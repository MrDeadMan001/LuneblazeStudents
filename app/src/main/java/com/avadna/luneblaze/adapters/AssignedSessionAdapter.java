package com.avadna.luneblaze.adapters;

import android.app.Activity;
import android.app.Dialog;

import com.avadna.luneblaze.helperClasses.MyCustomThemeDialog;

import android.content.Intent;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.activities.sessions.AssignedSessionActivity;
import com.avadna.luneblaze.activities.venue.VenueActivity;
import com.avadna.luneblaze.activities.sessions.SessionLoaderActivity;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.helperClasses.ShareContentOnMediaApps;
import com.avadna.luneblaze.pojo.PojoLoginResponseData;
import com.avadna.luneblaze.pojo.PojoNoDataResponse;
import com.avadna.luneblaze.pojo.PojoUserConnectResponse;
import com.avadna.luneblaze.pojo.pojoAllSessions.PojoAssignedSessions;
import com.avadna.luneblaze.pojo.pojoAllSessions.PojoOrganisedSession;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

/**
 * Created by Sunny on 11-01-2018.
 */

//todo open dialog box when date* and venue* are clicked

public class AssignedSessionAdapter extends RecyclerView.Adapter<AssignedSessionAdapter.MyViewHolder> {
    //  pivate List<String> hierarchyList;
    Activity activity;
    List<PojoAssignedSessions> assignedSessions;
    ApiInterface apiService;
    PreferenceUtils preferenceUtils;
    CommonFunctions commonFunctions;
    String user_id;
    ProgressBar pb_loading;
    boolean fixed_venue;
    private boolean showNoMorePresent = false;
    boolean showMoreProgressBar = false;
    DisplayMetrics displayMetrics;


    public AssignedSessionAdapter(Activity activity, List<PojoAssignedSessions> assignedSessions, boolean fixed_venue) {
        this.activity = activity;
        this.assignedSessions = assignedSessions;
        this.fixed_venue = fixed_venue;
        preferenceUtils = new PreferenceUtils(activity);
        commonFunctions = new CommonFunctions(activity);
        user_id = preferenceUtils.get_user_id();
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
        displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        //  TextView tv_activity_performed;
        CardView cv_parent;
        TextView tv_session_name;
        TextView tv_session_time_stamp;
        TextView tv_sharer_name;
        // TextView tv_initiator_name;
        TextView tv_session_description;
        TextView tv_topics;
        // TextView tv_discussion_number;
        TextView tv_attending_number;
        TextView tv_discussion_number;
        TextView tv_fellow_count;
        TextView tv_share_button;
        TextView tv_attend_button;
        TextView tv_discussion_button;

        LinearLayout ll_venue_wrapper;
        RelativeLayout rl_date_wrapper;
        TextView tv_venue;
        TextView tv_date_place_holder;
        TextView tv_venue_place_holder;

        RelativeLayout rl_pb_wrapper;
        ProgressBar pb_loading;
        TextView tv_no_more;
        ImageView iv_session_live;


        // RelativeLayout rl_action_perf_desc_container;
        // ImageView iv_session_photo;

        MyViewHolder(View view) {
            super(view);
            // tv_activity_performed= (TextView) view.findViewById(R.id.tv_activity_performed);
            pb_loading = (ProgressBar) view.findViewById(R.id.pb_loading);
            cv_parent = (CardView) view.findViewById(R.id.cv_parent);
            tv_session_name = (TextView) view.findViewById(R.id.tv_session_name);
            tv_session_time_stamp = (TextView) view.findViewById(R.id.tv_session_time_stamp);
            tv_session_description = (TextView) view.findViewById(R.id.tv_session_description);
            tv_topics = (TextView) view.findViewById(R.id.tv_topics);
            tv_sharer_name = (TextView) view.findViewById(R.id.tv_sharer_name);
            tv_discussion_number = (TextView) view.findViewById(R.id.tv_discussion_number);
            tv_attending_number = (TextView) view.findViewById(R.id.tv_attending_number);
            tv_fellow_count = (TextView) view.findViewById(R.id.tv_fellow_count);
            //   tv_initiator_name=(TextView)view.findViewById(R.id.tv_initiator_name);
            tv_share_button = (TextView) view.findViewById(R.id.tv_share_button);
            tv_discussion_button = (TextView) view.findViewById(R.id.tv_discussion_button);
            tv_attend_button = (TextView) view.findViewById(R.id.tv_attend_button);

            ll_venue_wrapper = (LinearLayout) view.findViewById(R.id.ll_venue_wrapper);
            rl_date_wrapper = (RelativeLayout) view.findViewById(R.id.rl_date_wrapper);
            tv_venue = (TextView) view.findViewById(R.id.tv_venue);

            tv_date_place_holder = (TextView) view.findViewById(R.id.tv_date_place_holder);
            tv_venue_place_holder = (TextView) view.findViewById(R.id.tv_venue_place_holder);

            rl_pb_wrapper = (RelativeLayout) view.findViewById(R.id.rl_pb_wrapper);
            pb_loading = (ProgressBar) view.findViewById(R.id.pb_loading);
            tv_no_more = (TextView) view.findViewById(R.id.tv_no_more);

            iv_session_live = (ImageView) view.findViewById(R.id.iv_session_live);

            if (fixed_venue) {
                tv_venue.setVisibility(View.GONE);
                tv_venue_place_holder.setVisibility(View.GONE);
            }
        /*    rl_action_perf_desc_container= (RelativeLayout) view.findViewById(R.id.rl_action_perf_desc_container);
            fl_like_button= (FrameLayout) view.findViewById(R.id.fl_like_button);
            fl_comment_button= (FrameLayout) view.findViewById(R.id.fl_comment_button);
            fl_Share_button= (FrameLayout) view.findViewById(R.id.fl_Share_button);
            iv_session_photo = (ImageView) view.findViewById(R.id.iv_session_photo);*/

        }
    }

    public void setShowMoreProgressBar(boolean status) {
        showMoreProgressBar = status;
        notifyDataSetChanged();
    }

    public void setShowNoMorePresent(boolean status) {
        showNoMorePresent = status;
        notifyDataSetChanged();
    }


    @Override
    public AssignedSessionAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.assigned_session_list_item, parent, false);

        return new AssignedSessionAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final AssignedSessionAdapter.MyViewHolder holder, int position) {
        //   holder.tv_hierarchy_item.setText(hierarchyList.get(position));
        holder.iv_session_live.setVisibility(View.GONE);

        if (position < assignedSessions.size()) {
            holder.cv_parent.setVisibility(View.VISIBLE);
            holder.rl_pb_wrapper.setVisibility(View.GONE);
            final PojoAssignedSessions currentItem = assignedSessions.get(position);

            if (currentItem.is_session_live != null && currentItem.is_session_live.equals("1")) {
                holder.iv_session_live.setVisibility(View.VISIBLE);
            } else {
                holder.iv_session_live.setVisibility(View.GONE);
            }
            // holder.tv_activity_performed.setVisibility(View.GONE);
            // holder.rl_action_perf_desc_container.setVisibility(View.GONE);
            holder.tv_session_name.setText(Html.fromHtml(currentItem.title));
            holder.tv_session_description.setText(Html.fromHtml(currentItem.description));

            String topicString = "";
            for (int i = 0; i < currentItem.topics.size(); i++) {
                if (i == currentItem.topics.size() - 1) {
                    topicString = topicString + Html.fromHtml(currentItem.topics.get(i).value);
                } else {
                    topicString = topicString + Html.fromHtml(currentItem.topics.get(i).value) + ", ";
                }
            }
            holder.tv_topics.setText(topicString);

       /* if(activity!=null) Glide.with(activity.getApplicationContext())
                .load(currentItem.coverPhoto)
                .into(holder.iv_session_photo);*/

            if (currentItem.noofverifieddiscussion == null || currentItem.noofverifieddiscussion == 0) {
                holder.tv_discussion_number.setVisibility(View.GONE);
            } else {
                holder.tv_discussion_number.setVisibility(View.VISIBLE);
            }
            holder.tv_discussion_number.setText("" + currentItem.noofverifieddiscussion + " " + activity.getString(R.string.discussions));

            if (currentItem.totalAttends.equals("0")) {
                holder.tv_attending_number.setVisibility(View.GONE);
            } else {
                holder.tv_attending_number.setVisibility(View.VISIBLE);
            }
            holder.tv_attending_number.setText(currentItem.totalAttends + " " + activity.getString(R.string.attending));

            if (currentItem.sessionAttendFriends == null || currentItem.sessionAttendFriends.isEmpty()) {
                holder.tv_fellow_count.setVisibility(View.GONE);
            } else {
                holder.tv_fellow_count.setVisibility(View.VISIBLE);
            }
            holder.tv_fellow_count.setText(currentItem.sessionAttendFriends.size() + " " + activity.getString(R.string.fellows_attending));

            holder.rl_date_wrapper.setVisibility(View.GONE);
            if (!currentItem.eventDate.isEmpty()) {
                holder.tv_session_time_stamp.setText(commonFunctions.parseDateToName(currentItem.eventDate.get(0).value));

            }
            if (!currentItem.presentors.isEmpty()) {
                holder.tv_sharer_name.setText(currentItem.presentors.get(0).userFullname);

            }
            //holder.tv_initiator_name.setText(currentItem.createdBy.userFullname);

            if (!currentItem.venues.isEmpty()) {
                holder.tv_venue.setText(currentItem.venues.get(0).venueName);
                holder.tv_venue.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent venueIntent = new Intent(activity, VenueActivity.class);
                        venueIntent.putExtra("id", currentItem.venues.get(0).venueId);
                        activity.startActivity(venueIntent);
                    }
                });
            }

            if (currentItem.finalizedData.equals("0")) {
                holder.tv_venue_place_holder.setText(activity.getString(R.string.venue_colon));
                holder.tv_date_place_holder.setText(activity.getString(R.string.date_colon));
                holder.rl_date_wrapper.setVisibility(View.GONE);
                holder.ll_venue_wrapper.setVisibility(View.GONE);
            } else {
                holder.tv_venue_place_holder.setText(activity.getString(R.string.venue_colon));
                holder.tv_date_place_holder.setText(activity.getString(R.string.date_colon));
                holder.rl_date_wrapper.setVisibility(View.VISIBLE);
                holder.ll_venue_wrapper.setVisibility(View.VISIBLE);
            }


            holder.cv_parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent assignedIntent = new Intent(activity, SessionLoaderActivity.class);
                    assignedIntent.putExtra("id", currentItem.sessionsId);
                    activity.startActivity(assignedIntent);
                }
            });

            if (!currentItem.presentors.isEmpty() && !currentItem.sharer.isEmpty() && currentItem.sharer.get(0).userId.equals(user_id)) {
                holder.tv_attend_button.setTextColor(ContextCompat.getColor(activity, R.color.app_theme_medium));
                holder.tv_attend_button.setText(activity.getString(R.string.sharing));
                holder.tv_attend_button
                        .setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_session_sharing, 0, 0, 0);
                holder.tv_attend_button.setEnabled(false);
            } else if (currentItem.createdBy.userId.equals(user_id)) {
                holder.tv_attend_button.setTextColor(ContextCompat.getColor(activity, R.color.app_theme_medium));
                holder.tv_attend_button.setText(activity.getString(R.string.initiated));
                holder.tv_attend_button
                        .setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_attend_filled, 0, 0, 0);
                holder.tv_attend_button.setEnabled(false);
            } else {
                holder.tv_attend_button.setEnabled(true);
                if (currentItem.iAttend) {
                    holder.tv_attend_button.setTextColor(ContextCompat.getColor(activity, R.color.app_theme_medium));
                    holder.tv_attend_button.setText(activity.getString(R.string.attending));
                    holder.tv_attend_button
                            .setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_attend_filled, 0, 0, 0);
                } else {
                    holder.tv_attend_button.setTextColor(ContextCompat.getColor(activity, R.color.med_grey));
                    holder.tv_attend_button.setText(activity.getString(R.string.attend));
                    holder.tv_attend_button
                            .setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_attend_unfilled, 0, 0, 0);
                }
            }


            holder.tv_attend_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (commonFunctions.isGuestUser()) {
                        commonFunctions.showLoginRequestDialog();
                    } else {
                        if (currentItem.iAttend) {
                            Intent assignedIntent = new Intent(activity, AssignedSessionActivity.class);
                            assignedIntent.putExtra("id", currentItem.sessionsId);
                            assignedIntent.putExtra("type", AppKeys.UNATTEND);
                            activity.startActivity(assignedIntent);
                        } else {
                            Intent assignedIntent = new Intent(activity, AssignedSessionActivity.class);
                            assignedIntent.putExtra("id", currentItem.sessionsId);
                            assignedIntent.putExtra("type", AppKeys.ATTEND);
                            activity.startActivity(assignedIntent);
                        }
                    }
                }
            });


            holder.tv_discussion_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(commonFunctions.isGuestUser()){
                        commonFunctions.showLoginRequestDialog();
                    }
                    else {
                        Intent sessionIntent = new Intent(activity, SessionLoaderActivity.class);
                        sessionIntent.putExtra("id", currentItem.sessionsId);
                        sessionIntent.putExtra("type", AppKeys.DISCUSSION);
                        sessionIntent.putExtra("action", AppKeys.ADD_COMMENT);
                        activity.startActivity(sessionIntent);
                    }
                }
            });

            holder.tv_share_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final BottomSheetDialog shareDialog = new BottomSheetDialog(activity);
                    shareDialog.setContentView(R.layout.session_share_option_dialog);
                    LinearLayout ll_timeline_wrapper = (LinearLayout) shareDialog.findViewById(R.id.ll_timeline_wrapper);
                    LinearLayout ll_other_platform = (LinearLayout) shareDialog.findViewById(R.id.ll_other_platform);
                    shareDialog.setCancelable(true);
                    shareDialog.show();

                    ll_timeline_wrapper.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(commonFunctions.isGuestUser()){
                                commonFunctions.showLoginRequestDialog();
                            }
                            else {
                                openShareSessionOnProfileDialog(currentItem);
                                shareDialog.dismiss();
                            }

                        }
                    });

                    ll_other_platform.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            shareTextUrl(currentItem);
                            shareDialog.dismiss();

                        }
                    });
                }
            });

      /*      if (position == assignedSessions.size() - 1 && !showMoreProgressBar) {
                Resources r = activity.getResources();
                int DP64 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 64, r.getDisplayMetrics());
                int DP2 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, r.getDisplayMetrics());
                holder.cv_parent.setPadding(0, 0, 0, DP64);
            } else {
                holder.cv_parent.setPadding(0, 0, 0, 0);
            }*/

        } else {
            holder.cv_parent.setVisibility(View.GONE);
            holder.rl_pb_wrapper.setVisibility(View.VISIBLE);

            if (showMoreProgressBar) {
                holder.pb_loading.setVisibility(View.VISIBLE);
                holder.tv_no_more.setVisibility(View.GONE);
            } else {
                holder.pb_loading.setVisibility(View.GONE);
            }

            if (showNoMorePresent) {
                holder.pb_loading.setVisibility(View.GONE);
                holder.tv_no_more.setVisibility(View.VISIBLE);
                if (assignedSessions.isEmpty()) {
                    holder.tv_no_more.setText(R.string.no_sessions_present);
                } else {
                    holder.tv_no_more.setText(R.string.no_more_sessions);
                }
            } else {
                holder.tv_no_more.setVisibility(View.GONE);
            }

        }


    }

    private void shareTextUrl(PojoAssignedSessions currentItem) {
        Glide.with(activity.getApplicationContext())
                .asBitmap()
                .load(currentItem.coverPhoto)
                .listener(new RequestListener<Bitmap>() {
                              @Override
                              public boolean onLoadFailed(@Nullable GlideException e, Object o, Target<Bitmap> target, boolean b) {
                                  return false;
                              }

                              @Override
                              public boolean onResourceReady(Bitmap bitmap, Object o, Target<Bitmap> target, DataSource dataSource, boolean b) {

                                  ShareContentOnMediaApps.shareContent(activity,
                                          AppKeys.SHARE_SESSION, currentItem.sessionsId, currentItem.title,
                                          bitmap);

                                  return false;
                              }
                          }
                ).submit();

    }

    private void openShareSessionOnProfileDialog(final PojoAssignedSessions currentItem) {
        TextView tv_username;
        ImageView iv_user_dp;
        final EditText et_share_title;
        Button tv_share_button;
        final Dialog shareDialog;
        RelativeLayout rl_item_holder;
        shareDialog = new MyCustomThemeDialog(activity);
        shareDialog.setContentView(R.layout.share_dialog_box_layout);
        shareDialog.getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        tv_username = shareDialog.findViewById(R.id.tv_username);
        iv_user_dp = (ImageView) shareDialog.findViewById(R.id.iv_user_dp);
        et_share_title = (EditText) shareDialog.findViewById(R.id.et_share_title);
        tv_share_button = (Button) shareDialog.findViewById(R.id.tv_share_button);
        rl_item_holder = (RelativeLayout) shareDialog.findViewById(R.id.rl_item_holder);

        PojoLoginResponseData pojoLoginResponseData = preferenceUtils.getUserLoginData();
        tv_username.setText(pojoLoginResponseData.userFullname);
        if (activity != null) Glide.with(activity.getApplicationContext())
                .load(pojoLoginResponseData.userPicture)
                .apply(new RequestOptions().override(128, 128))
                .apply(bitmapTransform(new CropCircleTransformation()))
                .into(iv_user_dp);

        LayoutInflater inflater = LayoutInflater.from(activity);
        View view = inflater.inflate(R.layout.share_verified_session_item, rl_item_holder, true);
        ImageView iv_session_photo = (ImageView) view.findViewById(R.id.iv_session_photo);
        TextView tv_session_name = (TextView) view.findViewById(R.id.tv_session_name);
        TextView tv_session_time_stamp = (TextView) view.findViewById(R.id.tv_session_time_stamp);

        int targetWidth = (int) (displayMetrics.widthPixels * 0.85f);
        int targetHeight;

        if (currentItem.imgDimensions != null && !currentItem.imgDimensions.isEmpty()) {
            String imgDimen = currentItem.imgDimensions;
            int height = Integer.parseInt(imgDimen.substring(0, imgDimen.indexOf("X")));
            int width = Integer.parseInt(imgDimen.substring(imgDimen.indexOf("X") + 1, imgDimen.length()));
            targetHeight = (int) ((1f * height * targetWidth) / width);

        } else {
            targetHeight = (int) (displayMetrics.widthPixels * 0.5f);
        }

        iv_session_photo.getLayoutParams().height = targetHeight;
        iv_session_photo.getLayoutParams().width = targetWidth;


        Glide.with(activity)
                .load(currentItem.coverPhoto)
                .apply(new RequestOptions().override(targetWidth, targetHeight))
                .into(iv_session_photo);

        tv_session_name.setText(currentItem.title);

        tv_session_time_stamp.setText(commonFunctions.parseNewsFeedItemTime(currentItem.addedOn));

        tv_share_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(commonFunctions.isGuestUser()){
                    commonFunctions.showLoginRequestDialog();
                }
                else {
                    hitShareContentApi(user_id, currentItem.sessionsId,
                            "share_session", et_share_title.getText().toString().trim());
                    shareDialog.dismiss();
                }
            }
        });
        shareDialog.show();
    }

    @Override
    public int getItemCount() {

        return assignedSessions.size() + 1;

        //  return hierarchyList.size();
    }

    private void hitAttendApi(String user_id, String id, String connect_type) {
        Call<PojoUserConnectResponse> call = apiService.changeUserConnectionApi(user_id, id, connect_type);

        call.enqueue(new Callback<PojoUserConnectResponse>() {
            @Override
            public void onResponse(Call<PojoUserConnectResponse> call, Response<PojoUserConnectResponse> response) {
                String message = "";
                if (response != null) {
                    message = response.body().message;
                    commonFunctions.setToastMessage(activity, message, Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);

                }
            }

            @Override
            public void onFailure(Call<PojoUserConnectResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(activity, t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
            }
        });
    }


    private void hitShareContentApi(String user_id, String article_id, String reaction, String title) {
        Call<PojoNoDataResponse> call = apiService.shareContent(user_id, article_id, reaction, title);
        call.enqueue(new Callback<PojoNoDataResponse>() {
            @Override
            public void onResponse(Call<PojoNoDataResponse> call, Response<PojoNoDataResponse> response) {
                String message = "";
                if (response != null) {
                    //  message = response.body().message;
                    commonFunctions.setToastMessage(activity.getApplicationContext(),
                            activity.getString(R.string.shared_session_successfully),
                            Toast.LENGTH_LONG, AppKeys.TOAST_USER);

                }
            }

            @Override
            public void onFailure(Call<PojoNoDataResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(activity.getApplicationContext(), t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);

            }
        });

    }
}
