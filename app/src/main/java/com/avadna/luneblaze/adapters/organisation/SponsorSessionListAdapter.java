package com.avadna.luneblaze.adapters.organisation;

import android.app.Activity;
import android.content.Intent;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.activities.sessions.SessionLoaderActivity;
import com.avadna.luneblaze.activities.venue.VenueActivity;
import com.avadna.luneblaze.adapters.normalPost.NormalPostCommentAdapter;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;

import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.pojo.pojoAllSessions.PojoAssignedSessions;
import com.avadna.luneblaze.pojo.pojoOrganisation.PojoSponsorSessionListItem;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;

import java.util.List;

public class SponsorSessionListAdapter extends RecyclerView.Adapter<SponsorSessionListAdapter.MyViewHolder> {
    //  pivate List<String> hierarchyList;
    Activity activity;
    List<PojoSponsorSessionListItem> assignedSessions;
    ApiInterface apiService;
    PreferenceUtils preferenceUtils;
    CommonFunctions commonFunctions;
    String user_id;
    ProgressBar pb_loading;
    boolean fixed_venue;
    private boolean showNoMorePresent = false;
    boolean showMoreProgressBar = false;
    DisplayMetrics displayMetrics;

    SessionSponsorAdapterCallback sessionSponsorAdapterCallback;


    public SponsorSessionListAdapter(Activity activity, List<PojoSponsorSessionListItem> assignedSessions) {
        try {
            this.sessionSponsorAdapterCallback = ((SessionSponsorAdapterCallback) activity);
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement SessionSponsorAdapterCallback.");
        }
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
        TextView tv_sponsor_button;

        LinearLayout ll_venue_wrapper;
        RelativeLayout rl_date_wrapper;
        TextView tv_venue;
        TextView tv_date_place_holder;
        TextView tv_venue_place_holder;

        RelativeLayout rl_pb_wrapper;
        ProgressBar pb_loading;
        TextView tv_no_more;


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
            //   tv_initiator_name=(TextView)view.findViewById(R.id.tv_initiator_name);
            tv_sponsor_button = (TextView) view.findViewById(R.id.tv_sponsor_button);

            ll_venue_wrapper = (LinearLayout) view.findViewById(R.id.ll_venue_wrapper);
            rl_date_wrapper = (RelativeLayout) view.findViewById(R.id.rl_date_wrapper);
            tv_venue = (TextView) view.findViewById(R.id.tv_venue);

            tv_date_place_holder = (TextView) view.findViewById(R.id.tv_date_place_holder);
            tv_venue_place_holder = (TextView) view.findViewById(R.id.tv_venue_place_holder);

            rl_pb_wrapper = (RelativeLayout) view.findViewById(R.id.rl_pb_wrapper);
            pb_loading = (ProgressBar) view.findViewById(R.id.pb_loading);
            tv_no_more = (TextView) view.findViewById(R.id.tv_no_more);

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
    public SponsorSessionListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.session_sponsor_list_item, parent, false);

        return new SponsorSessionListAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final SponsorSessionListAdapter.MyViewHolder holder, int position) {
        //   holder.tv_hierarchy_item.setText(hierarchyList.get(position));

        if (position < assignedSessions.size()) {
            holder.cv_parent.setVisibility(View.VISIBLE);
            holder.rl_pb_wrapper.setVisibility(View.GONE);
            final PojoSponsorSessionListItem currentItem = assignedSessions.get(position);
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
                holder.tv_venue_place_holder.setText("Venue* : ");
                holder.tv_date_place_holder.setText("Date* : ");
                holder.rl_date_wrapper.setVisibility(View.GONE);
                holder.ll_venue_wrapper.setVisibility(View.GONE);
            } else {
                holder.tv_venue_place_holder.setText("Venue : ");
                holder.tv_date_place_holder.setText("Date : ");
                holder.rl_date_wrapper.setVisibility(View.VISIBLE);
                holder.ll_venue_wrapper.setVisibility(View.VISIBLE);
            }

            if (currentItem.sponsored) {
                holder.tv_sponsor_button.setTextColor(ContextCompat.getColor(activity, R.color.app_theme_medium));
                holder.tv_sponsor_button.setText(activity.getString(R.string.sponsored));
            } else {
                holder.tv_sponsor_button.setTextColor(ContextCompat.getColor(activity, R.color.med_grey));
                holder.tv_sponsor_button.setText(activity.getString(R.string.sponsor));
            }

            holder.cv_parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent assignedIntent = new Intent(activity, SessionLoaderActivity.class);
                    assignedIntent.putExtra("id", currentItem.sessionsId);
                    activity.startActivity(assignedIntent);
                }
            });

            holder.tv_sponsor_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sessionSponsorAdapterCallback.onSessionSponsorAdapterMethod(position, AppKeys.SPONSOR);
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


    @Override
    public int getItemCount() {

        return assignedSessions.size() + 1;

        //  return hierarchyList.size();
    }

    public static interface SessionSponsorAdapterCallback {
        void onSessionSponsorAdapterMethod(int position, String type);
    }


}