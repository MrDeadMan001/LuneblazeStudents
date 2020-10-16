package com.avadna.luneblaze.adapters.poll;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.activities.venue.VenueActivity;
import com.avadna.luneblaze.activities.sessions.VenueSuggestActivity;
import com.avadna.luneblaze.fragments.sessions.SessionPollFragment;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.pojo.pojoSessionDetails.PojoVoting;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;

import java.util.List;

public class SessionVenuePollListAdapter extends RecyclerView.Adapter<SessionVenuePollListAdapter.MyViewHolder> {
    List<PojoVoting> votingList;
    Activity activity;
    String user_id;
    String session_id;
    ApiInterface apiService;
    SessionVenuePollListAdapterCallback sessionVenuePollListAdapterCallback;
    SessionPollFragment fragment;

    //todo change to boolean after the variable is changed in api from string to boolean type
    //0 means allow suggestions in api
    String allowVenueSuggestion;

    boolean allowDateSuggestion;
    String finalisedVenueId;
    String finalisedDate;
    CommonFunctions commonFunctions;

    public SessionVenuePollListAdapter(SessionPollFragment fragment, List<PojoVoting> votingList,
                                       String user_id, String sessionId, String allowVenueSuggestion,
                                       boolean allowDateSuggestion) {
        try {
            this.sessionVenuePollListAdapterCallback = ((SessionVenuePollListAdapterCallback) fragment);
        } catch (ClassCastException e) {
            throw new ClassCastException("fragment must implement sessionVenuePollListAdapterCallback.");
        }

        this.votingList = votingList;
        this.fragment = fragment;
        this.activity =fragment.getActivity();
        this.user_id = user_id;
        this.session_id = sessionId;
        this.allowVenueSuggestion = allowVenueSuggestion;
        this.allowDateSuggestion = allowDateSuggestion;
        commonFunctions = new CommonFunctions(activity);
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);

    }

    public void setAllowDateSuggestion(boolean allow) {
        allowDateSuggestion = allow;
        notifyDataSetChanged();
    }

    public void setFinalisedVenueId(String id) {
        finalisedVenueId = id;
        notifyDataSetChanged();
    }

    public void setFinalisedDate(String value) {
        finalisedDate = value;
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_is_final;
        TextView tv_black_button;
        TextView tv_venue, tv_venue_bar, tv_votes;
        RecyclerView rv_list;
        CheckBox cb_select_all;

        /* TextView tv_date1, tv_date1_bar;
         TextView tv_date2, tv_date2_bar;
         TextView tv_date3, tv_date3_bar;*/
        GridLayoutManager gridLayoutManager;
        PojoVoting currentItem;
        SessionDatePollAdapter sessionDatePollAdapter;

        MyViewHolder(View view) {
            super(view);
            iv_is_final = (ImageView) view.findViewById(R.id.iv_is_final);
            tv_black_button = (TextView) view.findViewById(R.id.tv_black_button);
            tv_venue = (TextView) view.findViewById(R.id.tv_venue);
            tv_votes = (TextView) view.findViewById(R.id.tv_votes);
            tv_venue_bar = (TextView) view.findViewById(R.id.tv_venue_bar);
            rv_list = (RecyclerView) view.findViewById(R.id.rv_list);
            cb_select_all = (CheckBox) view.findViewById(R.id.cb_select_all);
            gridLayoutManager = new GridLayoutManager(fragment.getActivity(), 2);
            rv_list.setLayoutManager(gridLayoutManager);
            rv_list.setNestedScrollingEnabled(false);

           /* tv_date1 = (TextView) view.findViewById(R.id.tv_date1);
            tv_date1_bar = (TextView) view.findViewById(R.id.tv_date1_bar);
            tv_date2 = (TextView) view.findViewById(R.id.tv_date2);
            tv_date2_bar = (TextView) view.findViewById(R.id.tv_date2_bar);
            tv_date3 = (TextView) view.findViewById(R.id.tv_date3);
            tv_date3_bar = (TextView) view.findViewById(R.id.tv_date3_bar);*/
        }
    }

    @Override
    public SessionVenuePollListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.session_poll_list_venue_item, parent, false);

        return new SessionVenuePollListAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final SessionVenuePollListAdapter.MyViewHolder holder, final int position) {
        holder.setIsRecyclable(false);
        Display display = fragment.getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        holder.tv_black_button.measure(size.x, size.y);
        ViewGroup.LayoutParams layoutParams = holder.tv_venue_bar.getLayoutParams();

        if (position < votingList.size()) {

            holder.currentItem = votingList.get(position);
            if (finalisedVenueId == null || finalisedVenueId.isEmpty()) {
                holder.cb_select_all.setVisibility(View.VISIBLE);
            } else {
                holder.cb_select_all.setVisibility(View.GONE);

            }
            holder.cb_select_all.setChecked(holder.currentItem.checked);
            holder.cb_select_all.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (commonFunctions.isGuestUser()) {
                        commonFunctions.showLoginRequestDialog();
                    } else {
                        sessionVenuePollListAdapterCallback.onSessionVenuePollListMethodCallback(position,
                                "select_all", isChecked);
                    }
                    /*if(isChecked){
                        for(int i=0;i<holder.currentItem.dates.size();i++){
                            holder.currentItem.dates.get(i).checked=true;
                            holder.currentItem.dates.get(i).votes++;
                        }
                        holder.currentItem.checked=true;
                    }
                    else {
                        for(int i=0;i<holder.currentItem.dates.size();i++){
                            holder.currentItem.dates.get(i).checked=false;
                            holder.currentItem.dates.get(i).votes--;
                        }
                        holder.currentItem.checked=false;
                    }
                    holder.sessionDatePollAdapter.notifyDataSetChanged();*/
                }
            });
            holder.tv_votes.setVisibility(View.VISIBLE);

            holder.tv_venue.setText(holder.currentItem.venueName);
            holder.tv_votes.setText("(" + holder.currentItem.totalVotes + ")");

            int maxWidth = (int) (size.x);

            int maxVoteCount = 0;
            for (int i = 0; i < votingList.size(); i++) {
                maxVoteCount += votingList.get(i).totalVotes;
            }
            if (maxVoteCount > 0) {
                layoutParams.width = (int) (1f * maxWidth * holder.currentItem.totalVotes / maxVoteCount);
            } else {
                layoutParams.width = 0;
            }
            holder.tv_venue_bar.setLayoutParams(layoutParams);

            if (holder.currentItem.venueId.equals(finalisedVenueId)) {
                holder.iv_is_final.setVisibility(View.VISIBLE);
            } else {
                holder.iv_is_final.setVisibility(View.GONE);
            }

            holder.sessionDatePollAdapter = new SessionDatePollAdapter(fragment, holder.currentItem,
                    user_id, session_id, position, allowDateSuggestion);
            holder.rv_list.setAdapter(holder.sessionDatePollAdapter);

            if (holder.currentItem.dates.size() < 7) {
                holder.sessionDatePollAdapter.setAllowSuggestion(allowDateSuggestion);
            } else {
                holder.sessionDatePollAdapter.setAllowSuggestion(false);
            }

            holder.sessionDatePollAdapter.notifyDataSetChanged();
            if (holder.currentItem.venueId.equals(finalisedVenueId)) {
                holder.sessionDatePollAdapter.setFinalDate(finalisedDate);
            } else {
                holder.sessionDatePollAdapter.setFinalDate("");
            }
            holder.tv_venue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent venueIntent = new Intent(fragment.getActivity(), VenueActivity.class);
                    venueIntent.putExtra("id", holder.currentItem.venueId);
                    fragment.getActivity().startActivity(venueIntent);
                }
            });
        } else {
            int maxWidth = size.x;
            holder.tv_venue.setText("+ " + fragment.getActivity().getString(R.string.add_new_venue));
            RelativeLayout.LayoutParams nameParams = (RelativeLayout.LayoutParams) holder.tv_venue.getLayoutParams();
            nameParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            holder.tv_venue.setGravity(Gravity.CENTER);
            nameParams.width = maxWidth;
            layoutParams.width = maxWidth;
            holder.cb_select_all.setVisibility(View.GONE);
            holder.iv_is_final.setVisibility(View.GONE);
            holder.tv_votes.setVisibility(View.GONE);

            holder.tv_venue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent venueSearchIntent = new Intent(fragment.getActivity(), VenueSuggestActivity.class);
                    venueSearchIntent.putExtra("id", session_id);
                    fragment.getActivity().startActivity(venueSearchIntent);
                }
            });
        }
    }

    public static interface SessionVenuePollListAdapterCallback {
        void onSessionVenuePollListMethodCallback(int position, String type, Boolean isChecked);

    }

    @Override
    public int getItemCount() {
        if (allowVenueSuggestion.equals("0")) {
            return votingList.size() + 1;

        } else {
            return votingList.size();
        }
    }
}
