package com.avadna.luneblaze.fragments.sessions;

import android.app.Dialog;

import com.avadna.luneblaze.adapters.UpvotersAndAttendeesListAdapter;
import com.avadna.luneblaze.helperClasses.MyCustomThemeDialog;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.activities.venue.VenueActivity;
import com.avadna.luneblaze.activities.profileInfo.ProfileInfoActivity;
import com.avadna.luneblaze.activities.sessions.OrganisedSessionActivity;
import com.avadna.luneblaze.activities.sessions.SessionCommentsListActivity;
import com.avadna.luneblaze.adapters.TaggedInterestAdapter;
import com.avadna.luneblaze.adapters.session.organised.OrganisedSessionTopicsListAdapter;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.pojo.PojoGetInterestListResponseDataListItem;
import com.avadna.luneblaze.pojo.PojoUpvotersAndAttendeesListResponse;
import com.avadna.luneblaze.pojo.PojoUserConnectResponse;
import com.avadna.luneblaze.pojo.pojoInterest.PojoInterestArr;
import com.avadna.luneblaze.pojo.pojoQuestion.PojoUserData;
import com.avadna.luneblaze.pojo.pojoSessionDetails.PojoSessionDetailsResponse;
import com.avadna.luneblaze.pojo.pojoSessionDetails.PojoTopic;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;


public class OrganisedSessionDetailsFragment extends Fragment implements
        TaggedInterestAdapter.TaggedInterestAdapterCallback,
        OrganisedSessionTopicsListAdapter.DiscussionTopicItemClickCallback,
        UpvotersAndAttendeesListAdapter.UpvoterAndAttendeesAdapterCallback {


    private LinearLayout ll_no_connection_wrapper;
    private TextView tv_retry;
    private LinearLayout ll_parent;
    private ProgressBar pb_loading_content;
    private RecyclerView rv_topics_list;
    private List<PojoTopic> topicList;
    private OrganisedSessionTopicsListAdapter topicsListAdapter;
    private RecyclerView rv_interest_tag_list;
    private List<PojoInterestArr> taggedInterest;
    private TaggedInterestAdapter taggedInterestAdapter;

    private TextView tv_venue_link, tv_time_date, tv_room;
    RelativeLayout rl_room_wrapper;

    private TextView tv_session_name, tv_sharer_name, tv_initiator_name;
    private ImageView iv_sharer_dp, iv_initiator_dp;
    private RelativeLayout rl_sharer_wrapper, rl_initiator_wrapper;
    private TextView tv_num_thanks, tv_num_questions, tv_num_shares, tv_num_comments;
    private RelativeLayout rl_first_coordinator, rl_second_coordinator;
    private TextView tv_coordinator1, tv_coordinator2;
    private ImageView iv_coordinator1_dp, iv_coordinator2_dp;
    private LinearLayout ll_coordinator_wrapper;

    private TextView tv_num_joined, tv_num_of_friends_attended, tv_num_total_attending;
    private RelativeLayout rl_joined_wrapper, rl_friend_attend_wrapper, rl_total_attend_wrapper;
    private View.OnClickListener onClickListener;
    private String sessionId;
    private PreferenceUtils preferenceUtils;
    private CommonFunctions commonFunctions;
    private String user_id;
    private String user_name;
    private ApiInterface apiService;
    private PojoSessionDetailsResponse pojoSessionDetailsResponse;

    RecyclerView rv_upvoters;
    LinearLayoutManager upvoterListLayoutManager;
    UpvotersAndAttendeesListAdapter upvotersAndAttendeesListAdapter;
    List<PojoUserData> upvoterList;
    boolean isGetUpvotersApiCalled = false;
    int upvoterListOffset = 0;


    public OrganisedSessionDetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View baseLayout = inflater.inflate(R.layout.fragment_organised_session_details, container, false);
        initViews(baseLayout);
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
        preferenceUtils = new PreferenceUtils(getActivity());
        commonFunctions = new CommonFunctions(getActivity());
        user_id = preferenceUtils.get_user_id();
        user_name = preferenceUtils.get_user_name();
        sessionId = getArguments().getString("id");
        if (sessionId != null) {
            hitGetSessionDetailsApi(user_id, sessionId, "home");
        }
        initClickListener();
        setClickListener();
        return baseLayout;
    }


    private void initViews(View baseLayout) {
        ll_no_connection_wrapper = (LinearLayout) baseLayout.findViewById(R.id.ll_no_connection_wrapper);
        ll_no_connection_wrapper.setVisibility(View.GONE);
        tv_retry = (TextView) baseLayout.findViewById(R.id.tv_retry);
        tv_retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ll_parent.setVisibility(View.GONE);
                pb_loading_content.setVisibility(View.VISIBLE);
                hitGetSessionDetailsApi(user_id, sessionId, "home");
            }
        });

        ll_parent = (LinearLayout) baseLayout.findViewById(R.id.ll_parent);
        ll_parent.setVisibility(View.GONE);
        pb_loading_content = (ProgressBar) baseLayout.findViewById(R.id.pb_loading_content);
        tv_venue_link = (TextView) baseLayout.findViewById(R.id.tv_venue_link);
        tv_time_date = (TextView) baseLayout.findViewById(R.id.tv_time_date);
        tv_room = (TextView) baseLayout.findViewById(R.id.tv_room);
        rl_room_wrapper = (RelativeLayout) baseLayout.findViewById(R.id.rl_room_wrapper);

        tv_num_joined = (TextView) baseLayout.findViewById(R.id.tv_num_joined);
        tv_num_total_attending = (TextView) baseLayout.findViewById(R.id.tv_num_total_attending);
        tv_num_of_friends_attended = (TextView) baseLayout.findViewById(R.id.tv_num_of_friends_attended);
        rl_joined_wrapper = (RelativeLayout) baseLayout.findViewById(R.id.rl_joined_wrapper);
        rl_friend_attend_wrapper = (RelativeLayout) baseLayout.findViewById(R.id.rl_friend_attend_wrapper);
        rl_total_attend_wrapper = (RelativeLayout) baseLayout.findViewById(R.id.rl_total_attend_wrapper);


        tv_session_name = (TextView) baseLayout.findViewById(R.id.tv_session_name);

        iv_initiator_dp = (ImageView) baseLayout.findViewById(R.id.iv_initiator_dp);
        tv_initiator_name = (TextView) baseLayout.findViewById(R.id.tv_initiator_name);

        iv_sharer_dp = (ImageView) baseLayout.findViewById(R.id.iv_sharer_dp);
        tv_sharer_name = (TextView) baseLayout.findViewById(R.id.tv_sharer_name);

        rl_sharer_wrapper = (RelativeLayout) baseLayout.findViewById(R.id.rl_sharer_wrapper);
        rl_initiator_wrapper = (RelativeLayout) baseLayout.findViewById(R.id.rl_initiator_wrapper);
        tv_num_thanks = (TextView) baseLayout.findViewById(R.id.tv_num_thanks);
        tv_num_questions = (TextView) baseLayout.findViewById(R.id.tv_num_questions);
        tv_num_shares = (TextView) baseLayout.findViewById(R.id.tv_num_shares);
        tv_num_comments = (TextView) baseLayout.findViewById(R.id.tv_num_comments);

        tv_coordinator1 = (TextView) baseLayout.findViewById(R.id.tv_coordinator1);
        tv_coordinator2 = (TextView) baseLayout.findViewById(R.id.tv_coordinator2);
        iv_coordinator1_dp = (ImageView) baseLayout.findViewById(R.id.iv_coordinator1_dp);
        iv_coordinator2_dp = (ImageView) baseLayout.findViewById(R.id.iv_coordinator2_dp);

        rl_first_coordinator = (RelativeLayout) baseLayout.findViewById(R.id.rl_first_coordinator);
        rl_second_coordinator = (RelativeLayout) baseLayout.findViewById(R.id.rl_second_coordinator);

        ll_coordinator_wrapper = (LinearLayout) baseLayout.findViewById(R.id.ll_coordinator_wrapper);
        ll_coordinator_wrapper.setVisibility(View.GONE);
        initLists(baseLayout);

    }

    private void initLists(View baseLayout) {
        //topic list
        topicList = new ArrayList<>();
        rv_topics_list = (RecyclerView) baseLayout.findViewById(R.id.rv_topics_list);
        rv_topics_list.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
        rv_topics_list.setNestedScrollingEnabled(false);

        topicsListAdapter = new OrganisedSessionTopicsListAdapter(getActivity(), modTopicList(topicList), "dis");
        rv_topics_list.setAdapter(topicsListAdapter);

        //interest Tags list
        taggedInterest = new ArrayList<>();
        rv_interest_tag_list = (RecyclerView) baseLayout.findViewById(R.id.rv_interest_tag_list);
        rv_interest_tag_list.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        taggedInterestAdapter = new TaggedInterestAdapter(this, taggedInterest, 2);
        rv_interest_tag_list.setAdapter(taggedInterestAdapter);

    }

    private List<PojoTopic> modTopicList(List<PojoTopic> topics) {
        for (int i = 0; i < topics.size(); i++) {
            topics.get(i).editable = "0";
        }
        return topics;
    }

    private void initClickListener() {
        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.tv_venue_link:
                        Intent venueIntent = new Intent(getActivity(), VenueActivity.class);
                        if (!pojoSessionDetailsResponse.data.venues.isEmpty()) {
                            venueIntent.putExtra("id",
                                    pojoSessionDetailsResponse.data.venues.get(0).venueId);
                        }
                        startActivity(venueIntent);
                        break;
                    case R.id.tv_num_comments:
                        Intent commentIntent = new Intent(getActivity(), SessionCommentsListActivity.class);
                        commentIntent.putExtra("id", sessionId);
                        startActivity(commentIntent);
                        break;

                    case R.id.tv_num_questions:
                        ((OrganisedSessionActivity) getActivity()).switchTab(2);
                        break;

                    case R.id.tv_num_thanks:
                        openUserListDialog("thanks");
                        // openUserDialog("thanks");
                        break;

                    case R.id.rl_joined_wrapper:
                        // openUserDialog("joined");
                        openUserListDialog("sessionjoinees");
                        break;

                    case R.id.rl_friend_attend_wrapper:
                        openUserListDialog("friendattendees");
                        // openUserDialog("friends");
                        break;

                    case R.id.rl_total_attend_wrapper:
                        openUserListDialog("totalattendees");
                        //openUserDialog("total");
                        break;

                    case R.id.tv_initiator_name:
                    case R.id.iv_initiator_dp:
                        openProfile(pojoSessionDetailsResponse.data.createdBy.userId);
                        break;

                    case R.id.tv_sharer_name:
                    case R.id.iv_sharer_dp:
                        openProfile(pojoSessionDetailsResponse.data.sharer.get(0).userId);
                        break;

                    case R.id.tv_coordinator1:
                    case R.id.iv_coordinator1_dp:
                        openProfile(pojoSessionDetailsResponse.data.coOrdinatorsData.get(0).userId);
                        break;

                    case R.id.tv_coordinator2:
                    case R.id.iv_coordinator2_dp:
                        openProfile(pojoSessionDetailsResponse.data.coOrdinatorsData.get(1).userId);
                        break;

                }
            }
        };
    }

    private void openProfile(String userId) {
        Intent profileIntent = new Intent(getActivity(), ProfileInfoActivity.class);
        profileIntent.putExtra("target_user_id", userId);
        startActivity(profileIntent);
    }


    private void openUserListDialog(String reaction) {
        TextView tv_title;
        ImageView iv_back;

        final Dialog userListDialog = new MyCustomThemeDialog(getActivity());
        userListDialog.setContentView(R.layout.contact_list_dialog);
        userListDialog.getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        ProgressBar pb_loading_users = (ProgressBar) userListDialog.findViewById(R.id.pb_loading_users);
        pb_loading_users.setVisibility(View.GONE);
        tv_title = userListDialog.findViewById(R.id.tv_title);
        iv_back = (ImageView) userListDialog.findViewById(R.id.iv_back);
        iv_back.setVisibility(View.GONE);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userListDialog.dismiss();
            }
        });

        if (reaction.equals("thanks")) {
            tv_title.setText(this.getString(R.string.thanks));
        } else if (reaction.equals("sessionjoinees")) {
            tv_title.setText(this.getString(R.string.people_joined));
        } else if (reaction.equals("friendattendees")) {
            tv_title.setText(this.getString(R.string.friends_attended));
        } else if (reaction.equals("totalattendees")) {
            tv_title.setText(this.getString(R.string.attendees));
        }

        rv_upvoters = (RecyclerView) userListDialog.findViewById(R.id.rv_list);
        //rv_list.setPadding(16,32,16,32);
        upvoterListLayoutManager = new LinearLayoutManager(getActivity(),
                RecyclerView.VERTICAL, false);
        upvoterList = new ArrayList<>();
        rv_upvoters.setLayoutManager(upvoterListLayoutManager);
        upvotersAndAttendeesListAdapter = new UpvotersAndAttendeesListAdapter(this, upvoterList);

        rv_upvoters.setAdapter(upvotersAndAttendeesListAdapter);
        rv_upvoters.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 2) {
                    if (!isGetUpvotersApiCalled
                            && upvoterListLayoutManager.findLastVisibleItemPosition() > upvoterList.size() - 2) {
                        hitGetUpvotersListApi(user_id, sessionId, reaction, ++upvoterListOffset);
                    }
                }
            }
        });

        upvoterListOffset = 0;
        hitGetUpvotersListApi(user_id, sessionId, reaction, upvoterListOffset);
        userListDialog.show();
    }

    private void hitGetUpvotersListApi(String user_id, final String id, String reaction, int offset) {
        Call<PojoUpvotersAndAttendeesListResponse> call = apiService.getUpvotersAndAttendees(user_id,
                id, reaction, String.valueOf(offset));
        if (!isGetUpvotersApiCalled) {
            upvotersAndAttendeesListAdapter.setShowProgressBar(true);
            isGetUpvotersApiCalled = true;
            call.enqueue(new Callback<PojoUpvotersAndAttendeesListResponse>() {
                @Override
                public void onResponse(Call<PojoUpvotersAndAttendeesListResponse> call,
                                       Response<PojoUpvotersAndAttendeesListResponse> response) {
                    if (response != null && response.body() != null && response.body().data != null) {
                        if (offset == 0) {
                            upvoterList.clear();
                        }
                        upvoterList.addAll(response.body().data);
                        upvotersAndAttendeesListAdapter.notifyDataSetChanged();
                    }
                    isGetUpvotersApiCalled = false;
                    upvotersAndAttendeesListAdapter.setShowProgressBar(false);
                }

                @Override
                public void onFailure(Call<PojoUpvotersAndAttendeesListResponse> call, Throwable t) {
                    // Log error here since request failed
                    if (getActivity() != null)
                        commonFunctions.setToastMessage(getActivity().getApplicationContext(), t.toString(),
                                Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                    upvotersAndAttendeesListAdapter.setShowProgressBar(false);
                    isGetUpvotersApiCalled = false;

                }
            });
        }
    }


    private void setClickListener() {
        tv_venue_link.setOnClickListener(onClickListener);
        tv_num_comments.setOnClickListener(onClickListener);
        rl_joined_wrapper.setOnClickListener(onClickListener);
        rl_friend_attend_wrapper.setOnClickListener(onClickListener);
        rl_total_attend_wrapper.setOnClickListener(onClickListener);
        tv_num_thanks.setOnClickListener(onClickListener);
        tv_num_questions.setOnClickListener(onClickListener);

        tv_sharer_name.setOnClickListener(onClickListener);
        iv_sharer_dp.setOnClickListener(onClickListener);

        tv_initiator_name.setOnClickListener(onClickListener);
        iv_initiator_dp.setOnClickListener(onClickListener);

        tv_coordinator1.setOnClickListener(onClickListener);
        iv_coordinator1_dp.setOnClickListener(onClickListener);

        tv_coordinator2.setOnClickListener(onClickListener);
        iv_coordinator2_dp.setOnClickListener(onClickListener);
    }

    private void hitGetSessionDetailsApi(String user_id, String session_id, String view) {
        ll_no_connection_wrapper.setVisibility(View.GONE);
        Call<PojoSessionDetailsResponse> call = apiService.getSessionDetails(user_id, session_id,
                view, "", "", "", "0");

        call.enqueue(new Callback<PojoSessionDetailsResponse>() {
            @Override
            public void onResponse(Call<PojoSessionDetailsResponse> call, Response<PojoSessionDetailsResponse> response) {
                String message = "";
                if (response != null && response.body() != null) {
                    message = response.body().message;
                    pojoSessionDetailsResponse = response.body();
                    List<PojoUserData> tempUserList = new ArrayList<>();
                    for (int i = 0; i < pojoSessionDetailsResponse.data.sessionAttendPeoples.size(); i++) {
                        if (pojoSessionDetailsResponse.data.sessionAttendPeoples.get(i).userPicture != null) {
                            tempUserList.add(pojoSessionDetailsResponse.data.sessionAttendPeoples.get(i));
                        }
                    }
                    pojoSessionDetailsResponse.data.sessionAttendPeoples = tempUserList;
                    tempUserList = new ArrayList<>();
                    for (int i = 0; i < pojoSessionDetailsResponse.data.sessionEngagedPeoples.size(); i++) {
                        if (pojoSessionDetailsResponse.data.sessionEngagedPeoples.get(i).userPicture != null) {
                            tempUserList.add(pojoSessionDetailsResponse.data.sessionEngagedPeoples.get(i));
                        }
                    }
                    pojoSessionDetailsResponse.data.sessionEngagedPeoples = tempUserList;

                    tempUserList = new ArrayList<>();
                    for (int i = 0; i < pojoSessionDetailsResponse.data.sessionAttendFriends.size(); i++) {
                        if (pojoSessionDetailsResponse.data.sessionAttendFriends.get(i).userPicture != null) {
                            tempUserList.add(pojoSessionDetailsResponse.data.sessionAttendFriends.get(i));
                        }
                    }
                    pojoSessionDetailsResponse.data.sessionAttendFriends = tempUserList;


                    setDataOnViews();
                    topicList.clear();
                    topicList.addAll(modTopicList(response.body().data.topics));
                    topicsListAdapter.notifyDataSetChanged();


                    taggedInterest.clear();
                    taggedInterest.addAll(response.body().data.sessionInterestArr);
                    taggedInterestAdapter.notifyDataSetChanged();

                    //   if(getActivity()!=null)commonFunctions.setToastMessage(getActivity().getApplicationContext(), message, Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);
                    ll_parent.setVisibility(View.VISIBLE);
                    pb_loading_content.setVisibility(View.GONE);
                }
            }


            @Override
            public void onFailure(Call<PojoSessionDetailsResponse> call, Throwable t) {
                // Log error here since request failed
                if (getActivity() != null)
                    commonFunctions.setToastMessage(getActivity().getApplicationContext(), t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                //  ll_parent.setVisibility(View.VISIBLE);
                pb_loading_content.setVisibility(View.GONE);
                ll_no_connection_wrapper.setVisibility(View.VISIBLE);
            }
        });
    }

    private void setDataOnViews() {
        if (!pojoSessionDetailsResponse.data.venues.isEmpty()) {
            tv_venue_link.setText(pojoSessionDetailsResponse.data.venues.get(0).venueName);
        }
        String dateAndTime = "";
        if (!pojoSessionDetailsResponse.data.eventTime.isEmpty()) {
            dateAndTime = "" + dateAndTime + pojoSessionDetailsResponse.data.eventTime.get(0).value;
        }
        if (!pojoSessionDetailsResponse.data.eventDuration.isEmpty()) {
            dateAndTime = dateAndTime + " (" + pojoSessionDetailsResponse.data.eventDuration + " min) ";
        }
        if (!pojoSessionDetailsResponse.data.eventDate.isEmpty()) {
            dateAndTime = "" + dateAndTime + "  " +
                    commonFunctions.parseDateToName(pojoSessionDetailsResponse.data.eventDate.get(0).value);
        }
        tv_time_date.setText(dateAndTime);

        if (pojoSessionDetailsResponse.data.finalPlace.isEmpty()) {
            rl_room_wrapper.setVisibility(View.GONE);
        } else {
            tv_room.setText(pojoSessionDetailsResponse.data.finalPlace);
        }

        try {
            if (pojoSessionDetailsResponse.data.questionCount.equals("0")) {
                tv_num_questions.setVisibility(View.GONE);
            } else {
                tv_num_questions.setVisibility(View.VISIBLE);
            }
            tv_num_questions.setText("" + pojoSessionDetailsResponse.data.questionCount + " " + getString(R.string.questions));

            if (pojoSessionDetailsResponse.data.totalShares.equals("0")) {
                tv_num_shares.setVisibility(View.GONE);
            } else {
                tv_num_shares.setVisibility(View.VISIBLE);
            }
            tv_num_shares.setText("" + pojoSessionDetailsResponse.data.totalShares + " " + getString(R.string.shares));

            if (pojoSessionDetailsResponse.data.thanksCount.equals("0")) {
                tv_num_thanks.setVisibility(View.GONE);
            } else {
                tv_num_thanks.setVisibility(View.VISIBLE);
            }
            tv_num_thanks.setText("" + pojoSessionDetailsResponse.data.thanksCount + " " + getString(R.string.thanks));

            if (pojoSessionDetailsResponse.data.commentsCnt.equals("0")) {
                tv_num_comments.setVisibility(View.GONE);
            } else {
                tv_num_comments.setVisibility(View.VISIBLE);
            }
            tv_num_comments.setText("" + pojoSessionDetailsResponse.data.commentsCnt + " " + getString(R.string.comments));
        } catch (Exception e) {
            e.printStackTrace();
        }

        setCoordinatorData();
        setInitiatorData();
        setSharerData();


        if (pojoSessionDetailsResponse.data.totalJoins.isEmpty() ||
                pojoSessionDetailsResponse.data.totalJoins.equals("0")) {
            rl_joined_wrapper.setVisibility(View.GONE);
        } else {
            rl_joined_wrapper.setVisibility(View.VISIBLE);
            tv_num_joined.setText(pojoSessionDetailsResponse.data.totalJoins);
        }

        if (pojoSessionDetailsResponse.data.friendsAttend.isEmpty()
                || pojoSessionDetailsResponse.data.friendsAttend.equals("0")) {
            rl_friend_attend_wrapper.setVisibility(View.GONE);
        } else {
            rl_friend_attend_wrapper.setVisibility(View.VISIBLE);
            tv_num_of_friends_attended.setText(pojoSessionDetailsResponse.data.friendsAttend);
        }

        if (pojoSessionDetailsResponse.data.totalAttends.isEmpty()
                || pojoSessionDetailsResponse.data.totalAttends.equals("0")) {
            rl_total_attend_wrapper.setVisibility(View.GONE);
        } else {
            rl_total_attend_wrapper.setVisibility(View.VISIBLE);
            tv_num_total_attending.setText(pojoSessionDetailsResponse.data.totalAttends);

        }

      /*  boolean sharerPresent = false;

        for (int i = 0; i < pojoSessionDetailsResponse.data.sessionAttendPeoples.size(); i++) {
            //if sharer is present in list dont show him
            if (!pojoSessionDetailsResponse.data.sharer.isEmpty()
                    && pojoSessionDetailsResponse.data.sharer.get(0).userId
                    .equals(pojoSessionDetailsResponse.data.sessionAttendPeoples.get(i).userId)) {
                sharerPresent = true;
            }
        }*/

    }

    private void setInitiatorData() {
        if (!pojoSessionDetailsResponse.data.presentors.isEmpty()
                && pojoSessionDetailsResponse.data.presentors.get(0)
                .userId.equals(pojoSessionDetailsResponse.data.createdBy.userId)) {
            rl_initiator_wrapper.setVisibility(View.GONE);
        } else {
            tv_initiator_name.setText(pojoSessionDetailsResponse.data.createdBy.userFullname);
            if (pojoSessionDetailsResponse.data.createdBy.userPicture.isEmpty()) {
                if (getActivity() != null)
                    Glide.with(getActivity().getApplicationContext()).load(R.drawable.placeholder)
                            .apply(new RequestOptions().override(64, 64))
                            .apply(bitmapTransform(new CropCircleTransformation()))
                            .into(iv_initiator_dp);
            } else {
                if (getActivity() != null)
                    Glide.with(getActivity().getApplicationContext()).load(pojoSessionDetailsResponse.data.createdBy.userPicture)
                            .apply(new RequestOptions().override(64, 64))
                            .apply(bitmapTransform(new CropCircleTransformation()))
                            .into(iv_initiator_dp);
            }
        }
    }

    private void setSharerData() {
        if (!pojoSessionDetailsResponse.data.sharer.isEmpty()) {
            tv_sharer_name.setText(pojoSessionDetailsResponse.data.sharer.get(0).userFullname);
            if (pojoSessionDetailsResponse.data.sharer.get(0).userPicture.isEmpty()) {
                if (getActivity() != null) Glide.with(getActivity().getApplicationContext())
                        .load(R.drawable.placeholder)
                        .apply(new RequestOptions().override(64, 64))
                        .apply(bitmapTransform(new CropCircleTransformation()))
                        .into(iv_sharer_dp);
            } else {
                if (getActivity() != null) Glide.with(getActivity().getApplicationContext())
                        .load(pojoSessionDetailsResponse.data.sharer.get(0).userPicture)
                        .apply(new RequestOptions().override(64, 64))
                        .apply(bitmapTransform(new CropCircleTransformation()))
                        .into(iv_sharer_dp);
            }
        } else {
            rl_sharer_wrapper.setVisibility(View.GONE);
        }
    }

    private void setCoordinatorData() {
        if (pojoSessionDetailsResponse.data.coOrdinatorsData != null
                && !pojoSessionDetailsResponse.data.coOrdinatorsData.isEmpty()) {
            ll_coordinator_wrapper.setVisibility(View.VISIBLE);

            if (pojoSessionDetailsResponse.data.coOrdinatorsData.size() < 2) {
                rl_second_coordinator.setVisibility(View.GONE);
            }

            for (int i = 0; i < pojoSessionDetailsResponse.data.coOrdinatorsData.size(); i++) {
                if (i == 0) {
                    tv_coordinator1.setText(pojoSessionDetailsResponse.data.coOrdinatorsData.get(0).userFullname);

                    if (getActivity() != null) Glide.with(getActivity().getApplicationContext())
                            .load(pojoSessionDetailsResponse.data.coOrdinatorsData.get(0).userPicture)
                            .apply(new RequestOptions().override(64, 64))
                            .apply(bitmapTransform(new CropCircleTransformation()))
                            .into(iv_coordinator1_dp);
                } else {
                    tv_coordinator2.setText(pojoSessionDetailsResponse.data.coOrdinatorsData.get(1).userFullname);
                    if (getActivity() != null) Glide.with(getActivity().getApplicationContext())
                            .load(pojoSessionDetailsResponse.data.coOrdinatorsData.get(1).userPicture)
                            .apply(new RequestOptions().override(64, 64))
                            .apply(bitmapTransform(new CropCircleTransformation()))
                            .into(iv_coordinator2_dp);
                }
            }
        }
    }


    @Override
    public void taggedInterestItemClickCallback(int position, PojoGetInterestListResponseDataListItem pojoGetInterestListResponseDataListItem) {

    }


    @Override
    public void discussionTopicItemClickCallback(String action, String type, int position) {

    }

    @Override
    public void onUpvoterAndAttendeesAdapterMethod(int position) {
        if (upvoterList.get(position).connection.equals("add")
                || upvoterList.get(position).connection.equals("remove")) {
            hitUpdateRequestStatusApi(user_id, upvoterList.get(position).userId, "follow", position);
            upvoterList.get(position).connection = "followed";

        } else if (upvoterList.get(position).connection.equals("followed")) {
            hitUpdateRequestStatusApi(user_id, upvoterList.get(position).userId, "unfollow", position);
            upvoterList.get(position).connection = "add";
        }
        upvotersAndAttendeesListAdapter.notifyDataSetChanged();
    }

    private void hitUpdateRequestStatusApi(String user_id, String id, final String connect_type, final int position) {
        Call<PojoUserConnectResponse> call = apiService.changeUserConnectionApi(user_id, id, connect_type);

        call.enqueue(new Callback<PojoUserConnectResponse>() {
            @Override
            public void onResponse(Call<PojoUserConnectResponse> call, Response<PojoUserConnectResponse> response) {
                String message = "";
                if (response != null && response.body() != null) {
                    message = response.body().message;
                    if (getActivity() != null)
                        commonFunctions.setToastMessage(getActivity().getApplicationContext(), message,
                                Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                    // venueList.remove(position);
                }
            }

            @Override
            public void onFailure(Call<PojoUserConnectResponse> call, Throwable t) {
                // Log error here since request failed
                if (getActivity() != null)
                    commonFunctions.setToastMessage(getActivity().getApplicationContext(), t.toString(),
                            Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
            }
        });
    }
}
