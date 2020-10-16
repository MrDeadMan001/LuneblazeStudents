package com.avadna.luneblaze.fragments.sessions;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Dialog;

import com.avadna.luneblaze.activities.sessions.VenueSuggestActivity;
import com.avadna.luneblaze.adapters.UpvotersAndAttendeesListAdapter;
import com.avadna.luneblaze.helperClasses.MyCustomThemeDialog;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListPopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.activities.sessions.AssignedSessionActivity;
import com.avadna.luneblaze.activities.sessions.SessionLoaderActivity;
import com.avadna.luneblaze.activities.sessions.VerifiedSessionActivity;
import com.avadna.luneblaze.adapters.calender.DateListAdapter;
import com.avadna.luneblaze.adapters.calender.DayGroupAdapter;
import com.avadna.luneblaze.adapters.calender.HoursListAdapter;
import com.avadna.luneblaze.adapters.calender.MinutesListAdapter;
import com.avadna.luneblaze.adapters.calender.MonthListAdapter;
import com.avadna.luneblaze.adapters.calender.YearListAdapter;
import com.avadna.luneblaze.adapters.poll.SessionDatePollAdapter;
import com.avadna.luneblaze.adapters.poll.SessionVenuePollListAdapter;
import com.avadna.luneblaze.firebasenotifications.config.Config;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.pojo.PojoLoginResponseData;
import com.avadna.luneblaze.pojo.PojoNoDataResponse;
import com.avadna.luneblaze.pojo.PojoUserConnectResponse;
import com.avadna.luneblaze.pojo.pojoQuestion.PojoUserData;
import com.avadna.luneblaze.pojo.pojoSessionDetails.PojoSessionDetailsResponse;
import com.avadna.luneblaze.pojo.pojoSessionDetails.PojoSessionDetailsResponseData;
import com.avadna.luneblaze.pojo.pojoSessionDetails.PojoVoting;
import com.avadna.luneblaze.pojo.pojoVenueSlots.PojoVenueDaySlot;
import com.avadna.luneblaze.rest.ApiClientLongDuration;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SessionPollFragment extends Fragment implements
        SessionVenuePollListAdapter.SessionVenuePollListAdapterCallback,
        SessionDatePollAdapter.SessionDatePollListAdapterCallback,
        DayGroupAdapter.DayGroupItemCallback,
        MinutesListAdapter.MinutesListAdapterCallback,
        HoursListAdapter.HoursListAdapterCallback,
        UpvotersAndAttendeesListAdapter.UpvoterAndAttendeesAdapterCallback {

    /* RecyclerView rv_discussion_comments_list;
     DiscussionAdapter commentAdapter;
     List<PojoComment> commentList;
     */
    String sessionId;
    PreferenceUtils preferenceUtils;
    CommonFunctions commonFunctions;
    String user_id;
    ApiInterface apiService;
    String type;

    LinearLayout ll_no_connection_wrapper;
    TextView tv_retry;

    RecyclerView rv_poll_list;
    List<PojoVoting> votingList;
    List<PojoVoting> originalVotingList;

    ProgressBar pb_loading_content;

    SessionVenuePollListAdapter sessionVenuePollListAdapter;

    private BroadcastReceiver pollDataChangedReceiver;

    boolean exceedMsgShow = false;

    boolean finaliseInProcess = false;
    private boolean postponeInProcess = false;

    LinearLayout ll_finalising_wrapper;
    ImageView iv_outer_ring, iv_inner_ring;
    RelativeLayout rl_finalise_parent_wrapper;
    boolean timePicked;
    Dialog finaliseFormDialog;

    int currDay, currMonth, currYear, chosenDay = 0, chosenMonth = 0, chosenYear = 0;

    PojoSessionDetailsResponseData pojoSessionDetailsResponseData;

    Dialog calenderDialog;
    int yearPos;
    int monthPos;
    List<Date> selectedDates;
    List<Date> preSelectedDates;
    DateListAdapter dateListAdapter;

    boolean askConfirmVoteAttend = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initBroadCastReceiver();

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(pollDataChangedReceiver,
                new IntentFilter(Config.NEW_VENUE_ADDED));
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(pollDataChangedReceiver,
                new IntentFilter(Config.POLL_DATE_UNSELECTED));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(pollDataChangedReceiver);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View baseLayout = inflater.inflate(R.layout.fragment_session_poll, container, false);
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
        preferenceUtils = new PreferenceUtils(getActivity());
        commonFunctions = new CommonFunctions(getActivity());
        user_id = preferenceUtils.get_user_id();
        sessionId = getArguments().getString("id");
        type = getArguments().getString("type");
        initViews(baseLayout);
        initLists(baseLayout);

        if (type != null) {
            if (sessionId != null) {
                if (type.equals(AppKeys.VERIFIED)) {
                    hitGetSessionDetailsApi(user_id, sessionId, "discussion");
                } else if (type.equals(AppKeys.ORGANISED)) {
                    hitGetSessionDetailsApi(user_id, sessionId, "comments");
                }
            }
        }

        return baseLayout;
    }

    private void initBroadCastReceiver() {
        pollDataChangedReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // checking for type intent filter
                if (intent.getAction().equals(Config.NEW_VENUE_ADDED)) {
                    if (type != null) {
                        if (sessionId != null) {
                            if (type.equals(AppKeys.VERIFIED)) {
                                hitGetSessionDetailsApi(user_id, sessionId, "discussion");
                            } else if (type.equals(AppKeys.ORGANISED)) {
                                hitGetSessionDetailsApi(user_id, sessionId, "comments");
                            }

                        }
                    }

                }
              /*  if (intent.getAction().equals(Config.POLL_DATE_UNSELECTED)) {
                    venuePosition=intent.getIntExtra("position",0);
                    votingList.get(venuePosition).checked=false;
                    sessionVenuePollListAdapter.notifyDataSetChanged();
                }*/
            }


        };

    }


    private void initViews(View baseLayout) {
        pb_loading_content = (ProgressBar) baseLayout.findViewById(R.id.pb_loading_content);
    }

    private void initLists(View baseLayout) {
        //interest Tags list
     /*   commentList=new ArrayList<>();
        rv_discussion_comments_list = (RecyclerView) baseLayout.findViewById(R.id.rv_discussion_comments_list);
        rv_discussion_comments_list.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        commentAdapter = new DiscussionAdapter(getActivity(),commentList);
        rv_discussion_comments_list.setAdapter(commentAdapter);
*/

        ll_no_connection_wrapper = (LinearLayout) baseLayout.findViewById(R.id.ll_no_connection_wrapper);
        ll_no_connection_wrapper.setVisibility(View.GONE);
        tv_retry = (TextView) baseLayout.findViewById(R.id.tv_retry);
        tv_retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sessionId != null) {
                    if (type.equals(AppKeys.VERIFIED)) {
                        hitGetSessionDetailsApi(user_id, sessionId, "discussion");
                    } else if (type.equals(AppKeys.ORGANISED)) {
                        hitGetSessionDetailsApi(user_id, sessionId, "comments");
                    }

                }
            }
        });

        votingList = new ArrayList<>();
        rv_poll_list = (RecyclerView) baseLayout.findViewById(R.id.rv_poll_list);
        rv_poll_list.setNestedScrollingEnabled(false);
        sessionVenuePollListAdapter = new SessionVenuePollListAdapter(this, votingList,
                user_id, sessionId, "0", false);
        //using horizontal linearlayout as we want horizontal list
        // rv_poll_list.setLayoutManager(new GridLayoutManager(getActivity(),2));

        rv_poll_list.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
        rv_poll_list.setAdapter(sessionVenuePollListAdapter);
    }


    public void initFinaliseProcess() {
        TextView tv_dialog_description;
        final Dialog confirmationDialog;
        Button bt_ok;
        confirmationDialog = new MyCustomThemeDialog(getActivity());
        confirmationDialog.setContentView(R.layout.message_dialog);
        tv_dialog_description = (TextView) confirmationDialog.findViewById(R.id.tv_dialog_description);
        bt_ok = (Button) confirmationDialog.findViewById(R.id.bt_ok);

        bt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finaliseInProcess = true;
                confirmationDialog.dismiss();
            }
        });
        tv_dialog_description.setText(R.string.choose_date_to_finalise);
        confirmationDialog.show();
    }

    public void initPostPoneProcess() {
        TextView tv_dialog_description;
        final Dialog confirmationDialog;
        Button bt_ok;
        confirmationDialog = new MyCustomThemeDialog(getActivity());
        confirmationDialog.setContentView(R.layout.message_dialog);
        tv_dialog_description = (TextView) confirmationDialog.findViewById(R.id.tv_dialog_description);
        bt_ok = (Button) confirmationDialog.findViewById(R.id.bt_ok);

        bt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postponeInProcess = true;
                confirmationDialog.dismiss();
            }
        });
        tv_dialog_description.setText(R.string.please_choose_a_date_from_same_venue);
        confirmationDialog.show();
    }


    private void hitGetSessionDetailsApi(final String user_id, String session_id, String view) {
        ll_no_connection_wrapper.setVisibility(View.GONE);
        Call<PojoSessionDetailsResponse> call = apiService.getSessionDetails(user_id, session_id, view,
                "next", "0", "", "0");

        pb_loading_content.setVisibility(View.VISIBLE);
        rv_poll_list.setVisibility(View.GONE);

        call.enqueue(new Callback<PojoSessionDetailsResponse>() {
            @Override
            public void onResponse(Call<PojoSessionDetailsResponse> call, Response<PojoSessionDetailsResponse> response) {
                String message = "";

                if (response != null && response.body() != null) {
                    message = response.body().message;
                    pojoSessionDetailsResponseData = response.body().data;
                    if (response.body().status == 1) {
                        /*if(response.body().data.discussions!=null&&!response.body().data.discussions.isEmpty()){
                            commentList.clear();
                            commentList.addAll(response.body().data.discussions);
                        }
                        else if(response.body().data.comments!=null&&!response.body().data.comments.isEmpty()){
                            commentList.clear();
                            commentList.addAll(response.body().data.comments);

                        }
                        commentAdapter.notifyDataSetChanged();*/

                        //mod list as the venue checked status is not received with correct value

                        for (int i = 0; i < response.body().data.voting.size(); i++) {
                            boolean allDatesChecked = true;
                            for (int j = 0; j < response.body().data.voting.get(i).dates.size(); j++) {
                                if (!response.body().data.voting.get(i).dates.get(j).checked) {
                                    allDatesChecked = false;
                                    break;
                                }
                            }
                            response.body().data.voting.get(i).checked = allDatesChecked;
                        }

                        if (response.body().data.voting != null) {
                            //if list was empty previously
                            if (votingList.isEmpty()) {
                                votingList.clear();
                                votingList.addAll(response.body().data.voting);
                                for (int j = 0; j < votingList.size(); j++) {
                                    votingList.get(j).checked = false;
                                }
                            } else {
                                //copy checked status from old list
                                List<PojoVoting> oldList = new ArrayList<>();
                                oldList.addAll(votingList);
                                votingList.clear();
                                votingList.addAll(response.body().data.voting);
                                for (int j = 0; j < votingList.size(); j++) {
                                    votingList.get(j).checked = false;
                                }
                                for (int i = 0; i < oldList.size(); i++) {
                                    for (int j = 0; j < votingList.size(); j++) {
                                        if (oldList.get(i).venueName.equals(votingList.get(j).venueName)) {
                                            votingList.get(j).checked = oldList.get(i).checked;
                                        }
                                    }
                                }
                            }
                            sessionVenuePollListAdapter =
                                    new SessionVenuePollListAdapter(SessionPollFragment.this,
                                            votingList, user_id, sessionId,
                                            response.body().data.finalizedData, false);

                            //why was this condition added
                          /*  if (!pojoSessionDetailsResponseData.presentors.isEmpty()
                                    && pojoSessionDetailsResponseData.presentors.get(0).userId.equals(user_id)) {
                                sessionVenuePollListAdapter.setAllowDateSuggestion(true);
                            }*/

                            sessionVenuePollListAdapter.setAllowDateSuggestion(true);

                            if (response.body().data.finalizedData.equals("1") &&
                                    !pojoSessionDetailsResponseData.venues.isEmpty()) {
                                sessionVenuePollListAdapter
                                        .setFinalisedVenueId(pojoSessionDetailsResponseData.venues.get(0).venueId);
                                sessionVenuePollListAdapter.setFinalisedDate(pojoSessionDetailsResponseData.eventDate.get(0).value);
                            }

                            rv_poll_list.setAdapter(sessionVenuePollListAdapter);
                        }
                    } else {
                        if (getActivity() != null)
                            commonFunctions.setToastMessage(getActivity().getApplicationContext(), message, Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                    }

                    pb_loading_content.setVisibility(View.GONE);
                    rv_poll_list.setVisibility(View.VISIBLE);

                    //update attend status of activity based on i attend and vote casted

                   /* if (!response.body().data.iAttend && getTotalCheckCount() > 0
                            && getActivity() != null) {
                        if (getActivity().getClass().getSimpleName().equals("VerifiedSessionActivity")) {
                            ((VerifiedSessionActivity) getActivity()).updateAttendStatus("waiting");
                        } else if (getActivity().getClass().getSimpleName().equals("AssignedSessionActivity")) {
                            ((AssignedSessionActivity) getActivity()).updateAttendStatus("waiting");
                        }
                    }*/
                }
            }

            @Override
            public void onFailure(Call<PojoSessionDetailsResponse> call, Throwable t) {
                // Log error here since request failed
                if (getActivity() != null)
                    commonFunctions.setToastMessage(getActivity().getApplicationContext(), t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                pb_loading_content.setVisibility(View.GONE);
                ll_no_connection_wrapper.setVisibility(View.VISIBLE);
            }
        });
    }

    public void removeCheckedFromAllOptions() {
        pojoSessionDetailsResponseData.iAttend=false;
        if (votingList != null && sessionVenuePollListAdapter != null) {
            for (int i = 0; i < votingList.size(); i++) {
                if (votingList.get(i).checked) {
                    votingList.get(i).checked = false;
                    votingList.get(i).totalVotes--;
                }
                for (int j = 0; j < votingList.get(i).dates.size(); j++) {
                    if (votingList.get(i).dates.get(j).checked) {
                        votingList.get(i).dates.get(j).checked = false;
                        votingList.get(i).dates.get(j).votes--;
                    }
                }
            }
            sessionVenuePollListAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onSessionVenuePollListMethodCallback(int position, String type, Boolean isChecked) {
        //dont let sharer cast or uncast votes

        if (!(pojoSessionDetailsResponseData.status.equals("3")
                && pojoSessionDetailsResponseData.sharer.get(0).userId.equals(user_id))) {

            //if user has checked the venue checkbox
            if (isChecked) {
                if (!pojoSessionDetailsResponseData.iAttend) {
                    openConfirmVenueUpvoteDialog(position);
                } else {
                    upvoteAllDatesOfVenue(position);
                    sessionVenuePollListAdapter.notifyDataSetChanged();
                }
            } else {
                downvoteAllDatesOfVenue(position);
                sessionVenuePollListAdapter.notifyDataSetChanged();

            }
            votingList.get(position).checked = isChecked;
        }
        sessionVenuePollListAdapter.notifyDataSetChanged();
    }

    public void setAskConfirmDialog(boolean status) {
        askConfirmVoteAttend = status;
    }


    public void openConfirmVenueUpvoteDialog(int venuePosition) {

        if (askConfirmVoteAttend) {
            TextView tv_dialog_description, tv_yes, tv_no;
            final Dialog confirmationDialog;
            confirmationDialog = new MyCustomThemeDialog(getActivity());
            confirmationDialog.setContentView(R.layout.yes_no_dialog);
            tv_dialog_description = (TextView) confirmationDialog.findViewById(R.id.tv_dialog_description);
            tv_yes = (TextView) confirmationDialog.findViewById(R.id.tv_yes);
            tv_no = (TextView) confirmationDialog.findViewById(R.id.tv_no);

            tv_no.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // holder.tv_attend_button.setTextColor(ContextCompat.getColor(activity,R.color.app_theme_medium));
                    //  Intent verifiedIntent=new Intent(getActivity(),VerifiedSessionActivity.class);
                    confirmationDialog.dismiss();
                }
            });
            tv_dialog_description.setText(R.string.vote_cast_notify_message);
            tv_yes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    upvoteAllDatesOfVenue(venuePosition);
                    sessionVenuePollListAdapter.notifyDataSetChanged();
                    confirmationDialog.dismiss();

                }
            });
            confirmationDialog.show();
        } else {
            upvoteAllDatesOfVenue(venuePosition);
            sessionVenuePollListAdapter.notifyDataSetChanged();
        }

    }

    private void upvoteAllDatesOfVenue(int venuePosition) {
        votingList.get(venuePosition).totalVotes++;
        SimpleDateFormat source = new SimpleDateFormat("yyyy-MM-dd");

        for (int i = 0; i < votingList.get(venuePosition).dates.size(); i++) {
            Date date = null;
            try {
                date = source.parse(votingList.get(venuePosition).dates.get(i).date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (date != null) {
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.DATE, -1);
                Date dayBefore = calendar.getTime();
                if (dayBefore.after(date)) {
                         /*   if(getActivity()!=null)commonFunctions.setToastMessage(getActivity().getApplicationContext(), getString(R.string.this_date_has_passed),
                                    Toast.LENGTH_LONG, AppKeys.TOAST_USER);*/
                } else {
                    votingList.get(venuePosition).dates.get(i).checked = true;
                    votingList.get(venuePosition).dates.get(i).votes++;

                    upvoteDateApi(user_id, sessionId,
                            votingList.get(venuePosition).dates.get(i).optionId);
                    if (getActivity().getClass().getSimpleName().equals("VerifiedSessionActivity")) {
                        ((VerifiedSessionActivity) getActivity()).updateAttendStatus("attending");
                    } else if (getActivity().getClass().getSimpleName().equals("AssignedSessionActivity")) {
                        ((AssignedSessionActivity) getActivity()).updateAttendStatus("attending");
                    }
                }
            }
        }
    }


    public void downvoteAllDatesOfVenue(int venuePosition) {
        if (votingList.get(venuePosition).checked) {
            votingList.get(venuePosition).totalVotes--;
        }
        for (int i = 0; i < votingList.get(venuePosition).dates.size(); i++) {
            if (votingList.get(venuePosition).dates.get(i).checked) {
                votingList.get(venuePosition).dates.get(i).checked = false;
                votingList.get(venuePosition).dates.get(i).votes--;
                downvoteDateApi(user_id, sessionId,
                        votingList.get(venuePosition).dates.get(i).optionId);
            }
        }
        pojoSessionDetailsResponseData.iAttend = false;

        if (getTotalCheckCount() == 0) {
            hitAttendApi(user_id, sessionId, "unattend");
            if (getActivity().getClass().getSimpleName().equals("VerifiedSessionActivity")) {
                ((VerifiedSessionActivity) getActivity()).updateAttendStatus("attend");
            } else if (getActivity().getClass().getSimpleName().equals("AssignedSessionActivity")) {
                ((AssignedSessionActivity) getActivity()).updateAttendStatus("attend");
            }
        }
    }

    @Override
    public void onSessionDatePollListMethodCallback(final int datePosition,
                                                    final int venuePosition, String type) {

        if (type.equals("add_new_date")) {
            openCalenderDialog(venuePosition);
        } else if (type.equals("date_clicked")) {

            if (finaliseInProcess || postponeInProcess) {
                SimpleDateFormat source = new SimpleDateFormat("yyyy-MM-dd");
                Date date = null;
                try {
                    date = source.parse(votingList.get(venuePosition).dates.get(datePosition).date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (date != null) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.add(Calendar.DATE, -1);
                    Date dayBefore = calendar.getTime();
                    if (dayBefore.after(date)) {
                        if (getActivity() != null)
                            commonFunctions.setToastMessage(getActivity().getApplicationContext(), getString(R.string.this_date_has_passed),
                                    Toast.LENGTH_LONG, AppKeys.TOAST_USER);
                    } else {
                        if (postponeInProcess) {
                            if (!pojoSessionDetailsResponseData.venues.get(0).venueId
                                    .equals(votingList.get(venuePosition).venueId)) {
                                if (getActivity() != null)
                                    commonFunctions.setToastMessage(getActivity().getApplicationContext(),
                                            getString(R.string.please_choose_date_from_same_venue),
                                            Toast.LENGTH_LONG, AppKeys.TOAST_USER);
                            } else {
                                openFinaliseForm(venuePosition, datePosition, "postpone");
                            }
                        } else if (finaliseInProcess) {
                            openFinaliseForm(venuePosition, datePosition, "finalise");
                        }
                    }
                }
            }
            //dont let sharer cast or uncast votes

            else if (pojoSessionDetailsResponseData.sharer.isEmpty()
                    || !pojoSessionDetailsResponseData.sharer.get(0).userId.equals(user_id)) {

                if (votingList.get(venuePosition).dates.get(datePosition).checked) {

                    //if only one date option was selected and now it is being unchecked then remove a vote from venue

                    //if all have been unchecked then remove user attend
                    if (getTotalCheckCount() == 1) {
                        TextView tv_dialog_description, tv_yes, tv_no;
                        final Dialog confirmationDialog;
                        confirmationDialog = new MyCustomThemeDialog(getActivity());
                        confirmationDialog.setContentView(R.layout.yes_no_dialog);
                        tv_dialog_description = (TextView) confirmationDialog.findViewById(R.id.tv_dialog_description);
                        tv_yes = (TextView) confirmationDialog.findViewById(R.id.tv_yes);
                        tv_no = (TextView) confirmationDialog.findViewById(R.id.tv_no);

                        tv_no.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // holder.tv_attend_button.setTextColor(ContextCompat.getColor(activity,R.color.app_theme_medium));
                                //  Intent verifiedIntent=new Intent(getActivity(),VerifiedSessionActivity.class);
                                confirmationDialog.dismiss();
                            }
                        });

                        tv_dialog_description.setText(R.string.unattend_becuase_votes_removed);

                        tv_yes.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (getSingleVenueCheckCount(votingList.get(venuePosition)) == 1) {
                                    votingList.get(venuePosition).totalVotes--;
                                }

                                votingList.get(venuePosition).dates.get(datePosition).votes--;
                                votingList.get(venuePosition).dates.get(datePosition).checked = false;

                                //if even one date is unchecked then deselect the select all button
                                votingList.get(venuePosition).checked = false;
                                sessionVenuePollListAdapter.notifyDataSetChanged();

                                downvoteDateApi(user_id, sessionId,
                                        votingList.get(venuePosition).dates.get(datePosition).optionId);
                                hitAttendApi(user_id, sessionId, "unattend");

                                if (getActivity().getClass().getSimpleName().equals("VerifiedSessionActivity")) {
                                    ((VerifiedSessionActivity) getActivity()).updateAttendStatus("attend");
                                } else if (getActivity().getClass().getSimpleName().equals("AssignedSessionActivity")) {
                                    ((AssignedSessionActivity) getActivity()).updateAttendStatus("attend");
                                }

                                confirmationDialog.dismiss();

                            }
                        });
                        confirmationDialog.show();

                    } else {
                        if (getSingleVenueCheckCount(votingList.get(venuePosition)) == 1) {
                            votingList.get(venuePosition).totalVotes--;
                        }

                        votingList.get(venuePosition).dates.get(datePosition).votes--;
                        votingList.get(venuePosition).dates.get(datePosition).checked = false;

                        //if even one date is unchecked then deselect the select all button
                        votingList.get(venuePosition).checked = false;
                        sessionVenuePollListAdapter.notifyDataSetChanged();

                        downvoteDateApi(user_id, sessionId,
                                votingList.get(venuePosition).dates.get(datePosition).optionId);
                    }

                } else {
                    //if the no date option was selected and now its being checked then add a vote to venue

                    SimpleDateFormat source = new SimpleDateFormat("yyyy-MM-dd");

                    Date date = null;
                    try {
                        date = source.parse(votingList.get(venuePosition).dates.get(datePosition).date);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    if (date != null) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.add(Calendar.DATE, -1);

                        Date dayBefore = calendar.getTime();

                        if (dayBefore.after(date)) {
                            if (getActivity() != null)
                                commonFunctions.setToastMessage(getActivity().getApplicationContext(),
                                        getString(R.string.this_date_has_passed), Toast.LENGTH_LONG, AppKeys.TOAST_USER);
                        } else {
                            //if I am already attending session
                            if (pojoSessionDetailsResponseData.iAttend) {
                                if (getSingleVenueCheckCount(votingList.get(venuePosition)) == 0) {
                                    votingList.get(venuePosition).totalVotes++;
                                }
                                if (getSingleVenueCheckCount(votingList.get(venuePosition))
                                        == votingList.get(venuePosition).dates.size() - 1) {
                                    votingList.get(venuePosition).checked = true;
                                }

                                votingList.get(venuePosition).dates.get(datePosition).votes++;
                                votingList.get(venuePosition).dates.get(datePosition).checked = true;

                                upvoteDateApi(user_id, sessionId,
                                        votingList.get(venuePosition).dates.get(datePosition).optionId);
                                if (getActivity().getClass().getSimpleName().equals("VerifiedSessionActivity")) {
                                    ((VerifiedSessionActivity) getActivity()).updateAttendStatus("attending");
                                } else if (getActivity().getClass().getSimpleName().equals("AssignedSessionActivity")) {
                                    ((AssignedSessionActivity) getActivity()).updateAttendStatus("attending");
                                }
                                sessionVenuePollListAdapter.notifyDataSetChanged();

                            }
                            //if its my first vote
                            else if (canIAttend(venuePosition)) {
                                openVoteConfirmationDialog(venuePosition, datePosition);

                            }
                        }
                    }
                }
            }
        }
        sessionVenuePollListAdapter.notifyDataSetChanged();
    }

    public void openVoteConfirmationDialog(int venuePosition, int datePosition) {

        if (askConfirmVoteAttend) {
            TextView tv_dialog_description, tv_yes, tv_no;
            final Dialog confirmationDialog;
            confirmationDialog = new MyCustomThemeDialog(getActivity());
            confirmationDialog.setContentView(R.layout.yes_no_dialog);
            tv_dialog_description = (TextView) confirmationDialog.findViewById(R.id.tv_dialog_description);
            tv_yes = (TextView) confirmationDialog.findViewById(R.id.tv_yes);
            tv_no = (TextView) confirmationDialog.findViewById(R.id.tv_no);

            tv_no.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // holder.tv_attend_button.setTextColor(ContextCompat.getColor(activity,R.color.app_theme_medium));
                    //  Intent verifiedIntent=new Intent(getActivity(),VerifiedSessionActivity.class);
                    confirmationDialog.dismiss();
                }
            });

            tv_dialog_description.setText(R.string.vote_cast_notify_message);

            tv_yes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (getSingleVenueCheckCount(votingList.get(venuePosition)) == 0) {
                        votingList.get(venuePosition).totalVotes++;
                    }
                    if (getSingleVenueCheckCount(votingList.get(venuePosition))
                            == votingList.get(venuePosition).dates.size() - 1) {
                        votingList.get(venuePosition).checked = true;
                    }

                    votingList.get(venuePosition).dates.get(datePosition).votes++;
                    votingList.get(venuePosition).dates.get(datePosition).checked = true;

                    upvoteDateApi(user_id, sessionId,
                            votingList.get(venuePosition).dates.get(datePosition).optionId);
                    if (getActivity().getClass().getSimpleName().equals("VerifiedSessionActivity")) {
                        ((VerifiedSessionActivity) getActivity()).updateAttendStatus("attending");
                    } else if (getActivity().getClass().getSimpleName().equals("AssignedSessionActivity")) {
                        ((AssignedSessionActivity) getActivity()).updateAttendStatus("attending");
                    }
                    sessionVenuePollListAdapter.notifyDataSetChanged();
                    confirmationDialog.dismiss();
                }
            });
            confirmationDialog.show();
        } else {
            if (getSingleVenueCheckCount(votingList.get(venuePosition)) == 0) {
                votingList.get(venuePosition).totalVotes++;
            }
            if (getSingleVenueCheckCount(votingList.get(venuePosition))
                    == votingList.get(venuePosition).dates.size() - 1) {
                votingList.get(venuePosition).checked = true;
            }

            votingList.get(venuePosition).dates.get(datePosition).votes++;
            votingList.get(venuePosition).dates.get(datePosition).checked = true;

            upvoteDateApi(user_id, sessionId,
                    votingList.get(venuePosition).dates.get(datePosition).optionId);
            if (getActivity().getClass().getSimpleName().equals("VerifiedSessionActivity")) {
                ((VerifiedSessionActivity) getActivity()).updateAttendStatus("attending");
            } else if (getActivity().getClass().getSimpleName().equals("AssignedSessionActivity")) {
                ((AssignedSessionActivity) getActivity()).updateAttendStatus("attending");
            }
            sessionVenuePollListAdapter.notifyDataSetChanged();
        }
    }


    private boolean canIAttend(int venuePosition) {
        PojoLoginResponseData pojoLoginResponseData = preferenceUtils.getUserLoginData();
        String venueType = "1";
        if (pojoSessionDetailsResponseData.voting.get(venuePosition).venueType != null) {
            venueType = pojoSessionDetailsResponseData.voting.get(venuePosition).venueType;
        }

        String venueId = "0";
        if (pojoSessionDetailsResponseData.voting.get(venuePosition).venueId != null) {
            venueId = pojoSessionDetailsResponseData.voting.get(venuePosition).venueId;
        }

        if (venueType.equals("1")) {
            return true;
        } else if (venueType.equals("2") && pojoLoginResponseData.oraganisationId.equals(venueId)) {
            return true;
        } else {
            if (getActivity() != null)
                commonFunctions.setToastMessage(getActivity().getApplicationContext(), getString(R.string.you_cannot_attend_session_on_this_venue),
                        Toast.LENGTH_LONG, AppKeys.TOAST_USER);
            return false;
        }
    }

    String pickedStartTime = "";
    int pickedHour = -1;
    int pickedMinute = -1;
    // String pickedEndTime = "";
    Dialog timeDialog;
    int pickedSlotPosition = 0;
    int MIN_DURATION = 30;
    int MAX_DURATION = 90;
    int duration = 45;
    TextView tv_start_time;//, tv_end_time;

    private void openFinaliseForm(final int venuePosition, final int datePosition,
                                  String finaliseOrPostpone) {
        timePicked = false;
        final Calendar c = Calendar.getInstance();

        final TextView tv_date, tv_venue, tv_save;
        AppCompatSeekBar sb_duration_bar;
        TextView tv_duration;


        finaliseFormDialog = new MyCustomThemeDialog(getActivity(), android.R.style.Theme_Black_NoTitleBar);
        finaliseFormDialog.setContentView(R.layout.finalise_form);
        finaliseFormDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        finaliseFormDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        rl_finalise_parent_wrapper = (RelativeLayout) finaliseFormDialog.findViewById(R.id.rl_finalise_parent_wrapper);
        ll_finalising_wrapper = (LinearLayout) finaliseFormDialog.findViewById(R.id.ll_finalising_wrapper);
        ll_finalising_wrapper.setVisibility(View.GONE);

        iv_outer_ring = (ImageView) finaliseFormDialog.findViewById(R.id.iv_outer_ring);
        iv_inner_ring = (ImageView) finaliseFormDialog.findViewById(R.id.iv_inner_ring);

        ObjectAnimator clockwise = ObjectAnimator.ofFloat(iv_outer_ring, "rotation", 0f, -360f);
        clockwise.setRepeatCount(ValueAnimator.INFINITE);
        clockwise.setInterpolator(new LinearInterpolator());
        clockwise.setDuration(1000);

        ObjectAnimator countercClockwise = ObjectAnimator.ofFloat(iv_inner_ring, "rotation", 0f, 360f);
        countercClockwise.setRepeatCount(ValueAnimator.INFINITE);
        countercClockwise.setInterpolator(new LinearInterpolator());
        countercClockwise.setDuration(1000);

        countercClockwise.start();
        clockwise.start();


        tv_date = (TextView) finaliseFormDialog.findViewById(R.id.tv_date);
        tv_venue = (TextView) finaliseFormDialog.findViewById(R.id.tv_venue);
        tv_start_time = (TextView) finaliseFormDialog.findViewById(R.id.tv_start_time);
        // tv_end_time = (TextView) finaliseFormDialog.findViewById(R.id.tv_end_time);
        tv_save = (TextView) finaliseFormDialog.findViewById(R.id.tv_save);

        tv_duration = (TextView) finaliseFormDialog.findViewById(R.id.tv_duration);
        tv_duration.setText(getString(R.string.duration) + " (" + duration + "min)");

        sb_duration_bar = (AppCompatSeekBar) finaliseFormDialog.findViewById(R.id.sb_duration_bar);
        sb_duration_bar.setProgress(duration - MIN_DURATION);

        sb_duration_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                int diff = i % 5;
                if (diff <= 2) {
                    i = i - diff;
                } else {
                    diff = 5 - diff;
                    i = i + diff;
                }

                sb_duration_bar.setProgress(i);

                duration = MIN_DURATION + i;
                tv_duration.setText(getString(R.string.duration) + " (" + duration + "min)");
                tv_start_time.setText("");
                pickedStartTime = "";
                pickedHour = -1;
                pickedMinute = -1;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        tv_date.setText(commonFunctions.parseDateToName(votingList.get(venuePosition).dates.get(datePosition).date));
        tv_venue.setText(votingList.get(venuePosition).venueName);

        tv_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!pickedStartTime.isEmpty()) {
                    hitSessionFinaliseApi("session_edit", user_id, sessionId,
                            votingList.get(venuePosition).dates.get(datePosition).date,
                            updateTime(pickedHour, pickedMinute), String.valueOf(duration),
                            votingList.get(venuePosition).venueId);
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                }
            }
        });

       // final int venueId = Integer.parseInt(votingList.get(venuePosition).venueId);

        String selectedDateStr = votingList.get(venuePosition).dates.get(datePosition).date;
        SimpleDateFormat source = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = source.parse(selectedDateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int dayPos = calendar.get(Calendar.DAY_OF_WEEK);

        //Adjust daypos value with -1 as in api sunday is at index 0 while calender.day_of_week returns sunday as 1
        dayPos--;
        List<PojoVenueDaySlot> slots = pojoSessionDetailsResponseData.voting.get(venuePosition).days.get(dayPos).slots;

        tv_start_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCustomTimePickerDialog(slots, "start");
            }
        });

       /* tv_end_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pickedStartTime.isEmpty()) {
                    if(getActivity()!=null)commonFunctions.setToastMessage(getActivity().getApplicationContext(), getString(R.string.please_pick_session_start_time),
                            Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                } else {
                    openCustomDatePickerDialog(slots, "end");
                }
            }
        });*/
        finaliseFormDialog.show();
    }



   /* private boolean isValidDurationPicked() {
        if (pickedStartTime.isEmpty()) {
            if(getActivity()!=null)commonFunctions.setToastMessage(getActivity().getApplicationContext(), getString(R.string.please_pick_session_start_time),
                    Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
        }*//* else if (pickedEndTime.isEmpty()) {
            if(getActivity()!=null)commonFunctions.setToastMessage(getActivity().getApplicationContext(), getString(R.string.please_pick_session_end_time),
                    Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
        } *//*else {
            SimpleDateFormat source = new SimpleDateFormat("hh:mm");
            Date sessionStartTime = null;
            try {
                sessionStartTime = source.parse(pickedStartTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }

          *//*  Date sessionEndTime = null;
            try {
                sessionEndTime = source.parse(pickedEndTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }*//*

            float diff = sessionStartTime.getTime() - sessionEndTime.getTime();
            diff = diff / 60000;
            if (diff < 45) {
                if(getActivity()!=null)commonFunctions.setToastMessage(getActivity().getApplicationContext(), getString(R.string.session_minimum_duration),
                        Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
            } else if (diff > 90) {
                if(getActivity()!=null)commonFunctions.setToastMessage(getActivity().getApplicationContext(), getString(R.string.session_maximum_duration),
                        Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
            } else {
                duration = (int) diff;
                return true;
            }
        }
        return false;
    }*/

    private void openCustomTimePickerDialog(List<PojoVenueDaySlot> slots, String startOrEnd) {
        SimpleDateFormat source = new SimpleDateFormat("HH:mm");

        timeDialog = new MyCustomThemeDialog(getActivity());
        timeDialog.setContentView(R.layout.custom_time_picker_dialog);

        RecyclerView rv_hours = (RecyclerView) timeDialog.findViewById(R.id.rv_hours);
        LinearLayoutManager hourLayoutManager = new LinearLayoutManager(getActivity(),
                RecyclerView.VERTICAL, false);
        rv_hours.setLayoutManager(hourLayoutManager);
        List<Integer> hoursList = new ArrayList<>();

        int hourPos = -1;
        if (startOrEnd.equals("start")) {
            for (int i = 0; i < slots.size(); i++) {
                String start = slots.get(i).start;
                int startHour = Integer.parseInt(start.substring(0, start.indexOf(":")));

                String end = slots.get(i).end;
                int endHour = Integer.parseInt(end.substring(0, end.indexOf(":")));

                for (int posCounter = 0, j = startHour; j <= endHour; j++, posCounter++) {
                    hoursList.add(j);
                    if (pickedHour == j) {
                        hourPos = posCounter;
                    }
                }
            }
        }

        HoursListAdapter hoursListAdapter = new HoursListAdapter(this, hoursList);
        rv_hours.setAdapter(hoursListAdapter);
        rv_hours.scrollToPosition(hourPos + 2);

        SnapHelper hourSnapHelper = new LinearSnapHelper();
        hourSnapHelper.attachToRecyclerView(rv_hours);

        RecyclerView rv_minutes = (RecyclerView) timeDialog.findViewById(R.id.rv_minutes);
        LinearLayoutManager minuteLayoutManager = new LinearLayoutManager(getActivity(),
                RecyclerView.VERTICAL, false);
        rv_minutes.setLayoutManager(minuteLayoutManager);

        List<Integer> minutesList = new ArrayList<>();

        int minPos = -1;
        for (int posCounter = 0, i = 0; i <= 55; i = i + 5, posCounter++) {
            minutesList.add(i);
            if (pickedMinute == i) {
                minPos = posCounter;
            }
        }

        MinutesListAdapter minutesListAdapter = new MinutesListAdapter(this, minutesList);
        rv_minutes.setAdapter(minutesListAdapter);

        SnapHelper minuteSnapHelper = new LinearSnapHelper();
        minuteSnapHelper.attachToRecyclerView(rv_minutes);

        rv_minutes.scrollToPosition(minPos + 2);

        TextView tv_done_button = (TextView) timeDialog.findViewById(R.id.tv_done_button);
        tv_done_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View hourSnapView = hourSnapHelper.findSnapView(hourLayoutManager);
                int hourPos = hourLayoutManager.getPosition(hourSnapView);

                View minuteSnapView = minuteSnapHelper.findSnapView(minuteLayoutManager);
                int minutePos = minuteLayoutManager.getPosition(minuteSnapView);

                pickedHour = hoursList.get(hourPos - 1);
                pickedMinute = minutesList.get(minutePos - 1);

                String pickedTimeStr = pickedHour + ":" + pickedMinute;

                Date pickedTime = null;
                try {
                    pickedTime = source.parse(pickedTimeStr);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                boolean liesInsideSlot = false;

                if (startOrEnd.equals("start")) {
                    for (int i = 0; i < slots.size(); i++) {
                        String start = slots.get(i).start;

                        Date slotStartTime = null;
                        try {
                            slotStartTime = source.parse(start);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        String end = slots.get(i).end;
                        Date slotEndTime = null;
                        try {
                            slotEndTime = source.parse(end);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        float startDiff = pickedTime.getTime() - slotStartTime.getTime();

                        float endDiff = slotEndTime.getTime() - pickedTime.getTime();
                        float durartionInMilliSec = duration * 60000;

                        if ((startDiff >= 0 && endDiff >= durartionInMilliSec)) {
                            pickedSlotPosition = i;
                            liesInsideSlot = true;
                            break;
                        }
                    }
                    if (liesInsideSlot) {
                        if (pickedMinute < 10) {
                            pickedStartTime = pickedHour + ":0" + pickedMinute;
                        } else {
                            pickedStartTime = pickedHour + ":" + pickedMinute;
                        }
                        tv_start_time.setText(pickedStartTime);
                        timeDialog.dismiss();
                    } else {
                        if (getActivity() != null)
                            commonFunctions.setToastMessage(getActivity().getApplicationContext(),
                                    getString(R.string.session_cannot_be_completed),
                                    Toast.LENGTH_LONG, AppKeys.TOAST_USER);
                    }
                }
            }
        });
        timeDialog.show();
    }

    @Override
    public void hoursListItemClickCallback(int position, PojoUserData currentItem, String type) {

    }

    @Override
    public void minutesListItemClickCallback(int position, PojoUserData currentItem, String type) {

    }

    private String updateTime(int hours, int mins) {
      /*  String timeSet = "";
        if (hours > 12) {
            hours -= 12;
            timeSet = "PM";
        } else if (hours == 0) {
            hours += 12;
            timeSet = "AM";
        } else if (hours == 12)
            timeSet = "PM";
        else
            timeSet = "AM";
        String minutes = "";
        if (mins < 10)
            minutes = "0" + mins;
        else
            minutes = String.valueOf(mins);
        String aTime = new StringBuilder().append(hours).append(':').append(minutes).append(" ").append(timeSet).toString();*/
        String aTime = hours + ":" + mins;

        if (mins < 10) {
            aTime = hours + ":0" + mins;
        }
        return aTime;
    }

    private int getSingleVenueCheckCount(PojoVoting pojoVoting) {
        int count = 0;
        for (int i = 0; i < pojoVoting.dates.size(); i++) {
            if (pojoVoting.dates.get(i).checked) {
                count++;
            }
        }
        return count;
    }

    private int getTotalCheckCount() {
        int count = 0;
        for (int j = 0; j < votingList.size(); j++) {
            for (int i = 0; i < votingList.get(j).dates.size(); i++) {
                if (votingList.get(j).dates.get(i).checked) {
                    count++;
                }
            }
        }
        return count;
    }

    private void openCalenderDialog(final int venuePosition) {
        preSelectedDates = getVenuedates(venuePosition);
        selectedDates = new ArrayList<>();
        calenderDialog = new MyCustomThemeDialog(getActivity());
        calenderDialog.setContentView(R.layout.custom_calender_dialog);

        TextView tv_done_button = (TextView) calenderDialog.findViewById(R.id.tv_done_button);

        tv_done_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!selectedDates.isEmpty()) {
                    if (isDateUnique(selectedDates.get(0), venuePosition)) {
                        openDateConfirmationDialog(venuePosition);
                    } else {
                        if (getActivity() != null)
                            commonFunctions.setToastMessage(getActivity().getApplicationContext(), getString(R.string.this_date_already_added),
                                    Toast.LENGTH_LONG, AppKeys.TOAST_USER);
                    }
                } else {
                    calenderDialog.dismiss();
                }
            }


        });

        Calendar calendar = Calendar.getInstance();
        monthPos = calendar.get(Calendar.MONTH);

        final RecyclerView rv_date_list = (RecyclerView) calenderDialog.findViewById(R.id.rv_date_list);

        final int venueId = Integer.parseInt(votingList.get(venuePosition).venueId);

        int arraySize = 0;

        for (int i = 0; i < pojoSessionDetailsResponseData.voting.get(venuePosition).days.size(); i++) {
            if (!pojoSessionDetailsResponseData.voting.get(venuePosition).days.get(i).slots.isEmpty()) {
                arraySize++;
            }
        }

        final int[] daysOfWeek = new int[arraySize];
        int counter = 0;
        for (int i = 0; i < pojoSessionDetailsResponseData.voting.get(venuePosition).days.size(); i++) {
            if (!pojoSessionDetailsResponseData.voting.get(venuePosition).days.get(i).slots.isEmpty()) {
                //adjusted by +1 to correct mismatch in indexes of days
                daysOfWeek[counter++] = i + 1;
            }
        }

        calendar = Calendar.getInstance();
        final int years[] = new int[2];
        years[0] = calendar.get(Calendar.YEAR);
        years[1] = calendar.get(Calendar.YEAR) + 1;

        final ListPopupWindow yearPopupWindow = new ListPopupWindow(getActivity());
        YearListAdapter yearListAdapter;

        final ListPopupWindow monthPopupWindow = new ListPopupWindow(getActivity());
        final MonthListAdapter monthListAdapter;

        final TextView tv_year = (TextView) calenderDialog.findViewById(R.id.tv_year);
        final TextView tv_month = (TextView) calenderDialog.findViewById(R.id.tv_month);

        tv_year.setText("" + calendar.get(Calendar.YEAR));
        tv_year.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int heightPopUpHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 55,
                        getResources().getDisplayMetrics());
                yearPopupWindow.setHeight(heightPopUpHeight);
                yearPopupWindow.setVerticalOffset(-(heightPopUpHeight + tv_year.getHeight()) / 2);
                yearPopupWindow.show();

            }
        });

        tv_month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int monthPopUpHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 220,
                        getResources().getDisplayMetrics());
                monthPopupWindow.setHeight(monthPopUpHeight);
                monthPopupWindow.setVerticalOffset(-(monthPopUpHeight + tv_month.getHeight()) / 2);
                monthPopupWindow.show();

            }
        });

        final List<String> months = new ArrayList<>();
        months.add("January");
        months.add("February");
        months.add("March");
        months.add("April");
        months.add("May");
        months.add("June");
        months.add("July");
        months.add("August");
        months.add("September");
        months.add("October");
        months.add("November");
        months.add("December");

        tv_month.setText(months.get(calendar.get(Calendar.MONTH)));

        monthListAdapter = new MonthListAdapter(getActivity(), months);
        monthPopupWindow.setAdapter(monthListAdapter);
        monthPopupWindow.setAnchorView(tv_month);

        monthPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                tv_month.setText(months.get(position));
                monthPopupWindow.dismiss();
                monthPos = position;
                dateListAdapter.setMonthNumber(monthPos);
            }
        });

        yearListAdapter = new YearListAdapter(getActivity(), years);
        yearPopupWindow.setAdapter(yearListAdapter);
        yearPopupWindow.setAnchorView(tv_year);
        yearPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                tv_year.setText(String.valueOf(years[position]));
                yearPopupWindow.dismiss();
                yearPos = position;
                dateListAdapter.setYearNumber(years[yearPos]);
            }
        });

        LinearLayoutManager dateListLayoutManager = new LinearLayoutManager(getActivity(),
                RecyclerView.VERTICAL, false);
        rv_date_list.setLayoutManager(dateListLayoutManager);


        dateListAdapter = new DateListAdapter(SessionPollFragment.this, years[yearPos],
                monthPos, daysOfWeek, selectedDates, preSelectedDates, venueId, true,
                pojoSessionDetailsResponseData.voting.get(venuePosition).days);
        rv_date_list.setAdapter(dateListAdapter);

        if (arraySize == 0) {
            openNoSlotsDialog();
        } else {
            calenderDialog.show();
        }
    }

    private void openNoSlotsDialog() {
        TextView tv_dialog_description;
        Button bt_ok;
        final Dialog confirmationDialog;
        confirmationDialog = new MyCustomThemeDialog(getActivity());
        confirmationDialog.setContentView(R.layout.message_dialog);
        tv_dialog_description = (TextView) confirmationDialog.findViewById(R.id.tv_dialog_description);
        bt_ok = (Button) confirmationDialog.findViewById(R.id.bt_ok);

        bt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmationDialog.dismiss();
            }
        });
        tv_dialog_description.setText(R.string.no_time_slots_available);
        confirmationDialog.show();
    }

    private boolean isDateUnique(Date date1, int venuePosition) {
        for (int i = 0; i < votingList.get(venuePosition).dates.size(); i++) {
            String inputPattern = "yyyy-MM-dd";
            SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);

            Date date2 = null;
            try {
                date2 = inputFormat.parse(votingList.get(venuePosition).dates.get(i).date);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (commonFunctions.areDatesSame(date1, date2)) {
                return false;
            }
        }
        return true;
    }

    List<Date> getVenuedates(int venuePosition) {
        List<Date> dates = new ArrayList<>();
        for (int i = 0; i < votingList.get(venuePosition).dates.size(); i++) {
            String inputPattern = "yyyy-MM-dd";
            SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);

            Date date = null;
            try {
                date = inputFormat.parse(votingList.get(venuePosition).dates.get(i).date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            dates.add(date);

        }
        return dates;
    }


    public void openDateConfirmationDialog(final int venuePosition) {
        TextView tv_dialog_description, tv_yes, tv_no;
        final Dialog confirmationDialog = new MyCustomThemeDialog(getActivity());
        confirmationDialog.setContentView(R.layout.yes_no_dialog);
        tv_dialog_description = (TextView) confirmationDialog.findViewById(R.id.tv_dialog_description);
        tv_yes = (TextView) confirmationDialog.findViewById(R.id.tv_yes);
        tv_no = (TextView) confirmationDialog.findViewById(R.id.tv_no);

        String date = "";
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy");
        try {
            date = outputFormat.format(selectedDates.get(0));
        } catch (Exception e) {
            e.printStackTrace();
        }

        tv_dialog_description.setText(getString(R.string.do_you_want_to_suggest_date) + " \n" + date);
        confirmationDialog.setCancelable(false);

        tv_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmationDialog.dismiss();
            }
        });

        tv_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String date = "";
                SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    date = outputFormat.format(selectedDates.get(0));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                hitSuggestDateApi(user_id, sessionId, votingList.get(venuePosition).venueName, date);
                confirmationDialog.dismiss();
                calenderDialog.dismiss();
            }
        });
        confirmationDialog.show();
    }

    @Override
    public void onDateClicked(Date date) {
        Calendar calendar = Calendar.getInstance();
        Date curr = calendar.getTime();

        calendar.add(Calendar.DATE, 2);

        Date twoDaysAfter = calendar.getTime();

        //todo revert the commented code later if needed
        if (curr.after(date)) {
            if (getActivity() != null)
                commonFunctions.setToastMessage(getActivity().getApplicationContext(), getString(R.string.date_passed), Toast.LENGTH_LONG,
                        AppKeys.TOAST_USER);
        } /*else if (twoDaysAfter.after(date)) {
            if(getActivity()!=null)commonFunctions.setToastMessage(getActivity().getApplicationContext(), getString(R.string.date_passed), Toast.LENGTH_LONG,
                    AppKeys.TOAST_USER);
        }*/ else {
            selectedDates.clear();
            selectedDates.add(date);
            dateListAdapter.notifyDataSetChanged();
        }
    }

 /*   private void openDatePickerDialogBox(final int venuePosition) {
        final Calendar cal = Calendar.getInstance();
        currYear = cal.get(Calendar.YEAR);
        currMonth = cal.get(Calendar.MONTH);
        currDay = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                chosenDay = dayOfMonth;
                chosenMonth = month;
                chosenYear = year;
                String date = "" + chosenYear + "-" + (chosenMonth + 1) + "-" + chosenDay;
                hitSuggestDateApi(user_id, sessionId, votingList.get(venuePosition).venueName, date);
            }
        };

        DatePickerDialog mDatePickerDialog = new DatePickerDialog(getActivity(), onDateSetListener, currYear, currMonth, currDay);
        cal.add(Calendar.DAY_OF_YEAR, 1);
        mDatePickerDialog.getDatePicker().setMinDate(cal.getTimeInMillis());
        mDatePickerDialog.show();
    }*/


    private void hitSuggestDateApi(final String user_id, final String session_id, String
            venue, String date) {
        Call<PojoNoDataResponse> call = apiService.suggestDate(user_id, session_id, venue, date);
        call.enqueue(new Callback<PojoNoDataResponse>() {
            @Override
            public void onResponse(Call<PojoNoDataResponse> call, Response<PojoNoDataResponse> response) {
                String message = response.body().message;
                if (getActivity() != null)
                    commonFunctions.setToastMessage(getActivity().getApplicationContext(), "" + message, Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                if (type != null) {
                    if (sessionId != null) {
                        if (type.equals(AppKeys.VERIFIED)) {
                            hitGetSessionDetailsApi(user_id, sessionId, "discussion");
                        } else if (type.equals(AppKeys.ORGANISED)) {
                            hitGetSessionDetailsApi(user_id, sessionId, "comments");
                        }

                    }
                }
            }

            @Override
            public void onFailure(Call<PojoNoDataResponse> call, Throwable t) {
                // Log error here since request failed
                if (getActivity() != null)
                    commonFunctions.setToastMessage(getActivity().getApplicationContext(), t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);

            }
        });
    }

    private void upvoteDateApi(final String user_id, String session_id, String optionId) {
        Call<PojoNoDataResponse> call = apiService.votePoll(user_id, session_id, optionId);

        if (pojoSessionDetailsResponseData.iAttend) {
            commonFunctions.openProgressDialog(getString(R.string.please_wait_casting_vote));
        } else {
            commonFunctions.openProgressDialog(getString(R.string.marking_you_as_attending));
        }
        call.enqueue(new Callback<PojoNoDataResponse>() {
            @Override
            public void onResponse(Call<PojoNoDataResponse> call, Response<PojoNoDataResponse> response) {
                int a = 5;
                commonFunctions.closeProgressDialog();
                if (response != null && response.body() != null) {
                    sendAttendBroadcast("1");
                    pojoSessionDetailsResponseData.iAttend = true;
                    String message = response.body().message;
                    if (getActivity() != null)
                        commonFunctions.setToastMessage(getActivity().getApplicationContext(),
                                "" + message, Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                    if (message.contains("Maximum") && !exceedMsgShow) {
                        exceedMsgShow = true;
                        TextView tv_dialog_description;
                        Button bt_ok;
                        final Dialog messageDialog;
                        messageDialog = new MyCustomThemeDialog(getActivity());
                        messageDialog.setContentView(R.layout.message_dialog);
                        tv_dialog_description = (TextView) messageDialog.findViewById(R.id.tv_dialog_description);
                        bt_ok = (Button) messageDialog.findViewById(R.id.bt_ok);
                        tv_dialog_description.setText(getString(R.string.limit_exceed_message));
                        bt_ok.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                messageDialog.dismiss();
                                if (getActivity().getClass().getSimpleName().equals("VerifiedSessionActivity")) {
                                    ((VerifiedSessionActivity) getActivity()).updateAttendStatus("waiting");
                                } else if (getActivity().getClass().getSimpleName().equals("AssignedSessionActivity")) {
                                    ((AssignedSessionActivity) getActivity()).updateAttendStatus("waiting");
                                }
                            }
                        });
                        messageDialog.show();
                    } /*else {
                    hitAttendApi(user_id, sessionId, "attend");
                }*/
                }
            }

            @Override
            public void onFailure(Call<PojoNoDataResponse> call, Throwable t) {
                commonFunctions.closeProgressDialog();
                // Log error here since request failed
                if (getActivity() != null)
                    commonFunctions.setToastMessage(getActivity().getApplicationContext(),
                            t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);

            }
        });
    }

    private void sendAttendBroadcast(String status) {
        Intent registrationComplete = new Intent(Config.SESSION_ATTENDED_STATUS);
        registrationComplete.putExtra(AppKeys.ATTEND_STATUS, status);
        registrationComplete.putExtra(AppKeys.SESSION_ID, sessionId);
        if (getActivity() != null) {
            LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(registrationComplete);
        }
    }

    private void downvoteDateApi(String user_id, String session_id, String optionId) {
        Call<PojoNoDataResponse> call = apiService.uncastPollOption(user_id, session_id, optionId);
        call.enqueue(new Callback<PojoNoDataResponse>() {
            @Override
            public void onResponse(Call<PojoNoDataResponse> call, Response<PojoNoDataResponse> response) {
                String message = response.body().message;
                if (getActivity() != null)
                    commonFunctions.setToastMessage(getActivity().getApplicationContext(), "" + message, Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
            }

            @Override
            public void onFailure(Call<PojoNoDataResponse> call, Throwable t) {
                // Log error here since request failed
                if (getActivity() != null)
                    commonFunctions.setToastMessage(getActivity().getApplicationContext(), t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
            }
        });
    }

    private void hitAttendApi(String user_id, String id, String connect_type) {
        pojoSessionDetailsResponseData.iAttend = false;
        if (getActivity().getClass().getSimpleName().equals("VerifiedSessionActivity")) {
            ((VerifiedSessionActivity) getActivity()).hitAttendApi(user_id, sessionId, "unattend");
        } else if (getActivity().getClass().getSimpleName().equals("AssignedSessionActivity")) {
            ((AssignedSessionActivity) getActivity()).hitAttendApi(user_id, sessionId, "unattend");
        }
    }

    private void hitSessionFinaliseApi(String handle, String user_id, final String session_id,
                                       String event_date, String event_time, String event_duration,
                                       String venue_id) {

        ApiInterface apiServiceLocal = ApiClientLongDuration.getClient().create(ApiInterface.class);

        Call<PojoNoDataResponse> call = apiServiceLocal.finaliseSession(handle, user_id, session_id,
                event_date, event_time, event_duration, venue_id);
        ll_finalising_wrapper.setVisibility(View.VISIBLE);

        rl_finalise_parent_wrapper.setVisibility(View.GONE);
        call.enqueue(new Callback<PojoNoDataResponse>() {
            @Override
            public void onResponse(Call<PojoNoDataResponse> call, Response<PojoNoDataResponse> response) {
                String message = "";
                if (response != null && response.body() != null) {
                    if (response.body().status == 1) {
                        finaliseFormDialog.dismiss();
                        ll_finalising_wrapper.setVisibility(View.GONE);
                        Intent sessionIntent = new Intent(getActivity(), SessionLoaderActivity.class);
                        sessionIntent.putExtra("id", session_id);
                        startActivity(sessionIntent);
                        getActivity().finish();
                    } else {
                        ll_finalising_wrapper.setVisibility(View.GONE);
                        rl_finalise_parent_wrapper.setVisibility(View.VISIBLE);
                        if (getActivity() != null)
                            commonFunctions.setToastMessage(getActivity().getApplicationContext(),
                                    response.body().message, Toast.LENGTH_LONG, AppKeys.TOAST_USER);
                    }
                }
                ll_finalising_wrapper.setVisibility(View.GONE);

            }

            @Override
            public void onFailure(Call<PojoNoDataResponse> call, Throwable t) {
                // Log error here since request failed
                if (getActivity() != null)
                    commonFunctions.setToastMessage(getActivity().getApplicationContext(), t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                ll_finalising_wrapper.setVisibility(View.GONE);
                rl_finalise_parent_wrapper.setVisibility(View.VISIBLE);
            }
        });
    }


    @Override
    public void onUpvoterAndAttendeesAdapterMethod(int position) {
 /*       if (upvoterList.get(position).connection.equals("add")
                || upvoterList.get(position).connection.equals("remove")) {
            hitUpdateRequestStatusApi(user_id, upvoterList.get(position).userId, "follow", position);
            upvoterList.get(position).connection = "followed";

        } else if (upvoterList.get(position).connection.equals("followed")) {
            hitUpdateRequestStatusApi(user_id, upvoterList.get(position).userId, "unfollow", position);
            upvoterList.get(position).connection = "add";
        }
        upvotersAndAttendeesListAdapter.notifyDataSetChanged();*/
    }


   /* private void hitUpdateRequestStatusApi(String user_id, String id, final String connect_type, final int position) {
        Call<PojoUserConnectResponse> call = apiService.changeUserConnectionApi(user_id, id, connect_type);

        call.enqueue(new Callback<PojoUserConnectResponse>() {
            @Override
            public void onResponse(Call<PojoUserConnectResponse> call, Response<PojoUserConnectResponse> response) {
                String message = "";
                if (response != null&&response.body()!=null) {
                    message = response.body().message;
                    commonFunctions.setToastMessage(getApplicationContext(), message,
                            Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                    // venueList.remove(position);
                }
            }

            @Override
            public void onFailure(Call<PojoUserConnectResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(getApplicationContext(), t.toString(),
                        Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
            }
        });
    }*/

}
