package com.avadna.luneblaze.fragments.sessions;

import android.app.Activity;
import android.app.Dialog;

import com.avadna.luneblaze.activities.organisation.OrganisationProfileInfoActivity;
import com.avadna.luneblaze.adapters.UpvotersAndAttendeesListAdapter;
import com.avadna.luneblaze.helperClasses.MyCustomThemeDialog;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.activities.sessions.AssignCoordinatorsActivity;
import com.avadna.luneblaze.activities.venue.VenueActivity;
import com.avadna.luneblaze.activities.profileInfo.ProfileInfoActivity;
import com.avadna.luneblaze.adapters.TaggedInterestAdapter;
import com.avadna.luneblaze.adapters.session.organised.OrganisedSessionTopicsListAdapter;
import com.avadna.luneblaze.adapters.session.SessionAttendingFriendListAdapter;
import com.avadna.luneblaze.firebasenotifications.config.Config;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.pojo.PojoGetInterestListResponseDataListItem;
import com.avadna.luneblaze.pojo.PojoNoDataResponse;
import com.avadna.luneblaze.pojo.PojoUpvotersAndAttendeesListResponse;
import com.avadna.luneblaze.pojo.PojoUserConnectResponse;
import com.avadna.luneblaze.pojo.pojoInterest.PojoInterestArr;
import com.avadna.luneblaze.pojo.pojoOrganisation.PojoGetOrganisationInfoResponse;
import com.avadna.luneblaze.pojo.pojoOrganisation.PojoGetOrganisationInfoResponseData;
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


public class VerifiedSessionDetailsFragment extends Fragment
        implements TaggedInterestAdapter.TaggedInterestAdapterCallback,
        SessionAttendingFriendListAdapter.FriendsAttendingListAdapterCallback,
        OrganisedSessionTopicsListAdapter.DiscussionTopicItemClickCallback,
        UpvotersAndAttendeesListAdapter.UpvoterAndAttendeesAdapterCallback {

    private LinearLayout ll_parent;
    private ProgressBar pb_loading_content;

    private LinearLayout ll_no_connection_wrapper;
    private TextView tv_retry;

    private RecyclerView rv_topics_list;
    private List<PojoTopic> topicList;
    private OrganisedSessionTopicsListAdapter verifiedSessionTopicsListAdapter;

    private RecyclerView rv_prerequisites_list;
    private List<PojoTopic> preRequisitesList;
    private OrganisedSessionTopicsListAdapter verifiedSessionPrerequisitesListAdapter;

    private RecyclerView rv_interest_tag_list;
    private List<PojoInterestArr> taggedInterest;
    private TaggedInterestAdapter taggedInterestAdapter;

    private LinearLayout ll_friend_attend_wrapper;

    private RecyclerView rv_friend_attend_list;
    private SessionAttendingFriendListAdapter sessionAttendingFriendListAdapter;


    RecyclerView rv_upvoters;
    LinearLayoutManager upvoterListLayoutManager;
    UpvotersAndAttendeesListAdapter upvotersAndAttendeesListAdapter;
    List<PojoUserData> upvoterList;
    boolean isGetUpvotersApiCalled = false;
    int upvoterListOffset = 0;

    private String sessionId;
    private PreferenceUtils preferenceUtils;
    private CommonFunctions commonFunctions;
    private String user_id;
    private ApiInterface apiService;

    private TextView tv_session_name;
    private TextView tv_num_attending, tv_num_allowed;
    private TextView tv_venue, tv_time;
    private RelativeLayout rl_venue_wrapper, rl_timestamp_wrapper;


    private PojoSessionDetailsResponse pojoSessionDetailsResponse;

    private ImageView iv_add_coordinator;
    private RelativeLayout rl_coordinator_wrapper;
    private TextView tv_sharer_name, tv_initiator_name;
    private ImageView iv_sharer_dp, iv_initiator_dp;
    private RelativeLayout rl_sharer_wrapper, rl_initiator_wrapper;
    private RelativeLayout rl_coordinator2_wrapper, rl_coordinator1_wrapper;
    private TextView tv_coordinator1, tv_coordinator2;
    private ImageView iv_coordinator1_dp, iv_coordinator2_dp;
    private TextView tv_room, tv_room_placeholder;
    private RelativeLayout rl_room_wrapper;
    private ImageView iv_update_room;

    RelativeLayout rl_sponsor_wrapper;
    ImageView iv_sponsor_dp;
    TextView tv_sponsor_name, tv_sponsor_description;


    private Dialog roomDialog;

    private View.OnClickListener onClickListener;

    private BroadcastReceiver mRegistrationBroadcastReceiver;


    public VerifiedSessionDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View baseLayout = inflater.inflate(R.layout.fragment_verified_session_details, container, false);
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
        preferenceUtils = new PreferenceUtils(getActivity());
        commonFunctions = new CommonFunctions(getActivity());
        user_id = preferenceUtils.get_user_id();
        sessionId = getArguments().getString("id");
        initViews(baseLayout);
        initClickListener();
        setClickListeners();
        initBroadCastReceiver();
        if (sessionId != null) {
            hitGetSessionDetailsApi(user_id, sessionId, "home");
        }
        return baseLayout;

    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.SESSION_DETAILS_UPDATED));

    }

    private void initBroadCastReceiver() {
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // checking for userType intent filter
                if (intent.getAction().equals(Config.SESSION_DETAILS_UPDATED)) {
                    hitGetSessionDetailsApi(user_id, sessionId, "home");
                }
            }
        };
    }


    private void initViews(View baseLayout) {

        ll_no_connection_wrapper = (LinearLayout) baseLayout.findViewById(R.id.ll_no_connection_wrapper);
        ll_no_connection_wrapper.setVisibility(View.GONE);
        tv_retry = (TextView) baseLayout.findViewById(R.id.tv_retry);
        tv_retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hitGetSessionDetailsApi(user_id, sessionId, "home");

            }
        });

        ll_parent = (LinearLayout) baseLayout.findViewById(R.id.ll_parent);
        ll_parent.setVisibility(View.GONE);
        pb_loading_content = (ProgressBar) baseLayout.findViewById(R.id.pb_loading_content);
        rl_venue_wrapper = (RelativeLayout) baseLayout.findViewById(R.id.rl_venue_wrapper);
        rl_timestamp_wrapper = (RelativeLayout) baseLayout.findViewById(R.id.rl_timestamp_wrapper);
        tv_venue = (TextView) baseLayout.findViewById(R.id.tv_venue);
        tv_time = (TextView) baseLayout.findViewById(R.id.tv_time);
        tv_session_name = (TextView) baseLayout.findViewById(R.id.tv_session_name);
        tv_initiator_name = (TextView) baseLayout.findViewById(R.id.tv_initiator_name);
        tv_num_attending = (TextView) baseLayout.findViewById(R.id.tv_num_attending);
        tv_num_allowed = (TextView) baseLayout.findViewById(R.id.tv_num_allowed);

        iv_add_coordinator = (ImageView) baseLayout.findViewById(R.id.iv_add_coordinator);
        iv_add_coordinator.setVisibility(View.GONE);
        rl_coordinator_wrapper = (RelativeLayout) baseLayout.findViewById(R.id.rl_coordinator_wrapper);
        rl_coordinator_wrapper.setVisibility(View.GONE);
        tv_coordinator1 = (TextView) baseLayout.findViewById(R.id.tv_coordinator1);
        tv_coordinator2 = (TextView) baseLayout.findViewById(R.id.tv_coordinator2);
        iv_coordinator1_dp = (ImageView) baseLayout.findViewById(R.id.iv_coordinator1_dp);
        iv_coordinator2_dp = (ImageView) baseLayout.findViewById(R.id.iv_coordinator2_dp);
        rl_coordinator2_wrapper = (RelativeLayout) baseLayout.findViewById(R.id.rl_coordinator2_wrapper);
        rl_coordinator1_wrapper = (RelativeLayout) baseLayout.findViewById(R.id.rl_coordinator1_wrapper);


        ll_friend_attend_wrapper = (LinearLayout) baseLayout.findViewById(R.id.ll_friend_attend_wrapper);

        tv_sharer_name = (TextView) baseLayout.findViewById(R.id.tv_sharer_name);
        iv_sharer_dp = (ImageView) baseLayout.findViewById(R.id.iv_sharer_dp);
        iv_initiator_dp = (ImageView) baseLayout.findViewById(R.id.iv_initiator_dp);
        rl_sharer_wrapper = (RelativeLayout) baseLayout.findViewById(R.id.rl_sharer_wrapper);
        rl_initiator_wrapper = (RelativeLayout) baseLayout.findViewById(R.id.rl_initiator_wrapper);

        rl_room_wrapper = (RelativeLayout) baseLayout.findViewById(R.id.rl_room_wrapper);
        rl_room_wrapper.setVisibility(View.GONE);
        tv_room = (TextView) baseLayout.findViewById(R.id.tv_room);
        tv_room_placeholder = (TextView) baseLayout.findViewById(R.id.tv_room_placeholder);
        iv_update_room = (ImageView) baseLayout.findViewById(R.id.iv_update_room);
        iv_update_room.setVisibility(View.GONE);

        rl_sponsor_wrapper = (RelativeLayout) baseLayout.findViewById(R.id.rl_sponsor_wrapper);
        rl_sponsor_wrapper.setVisibility(View.GONE);
        iv_sponsor_dp = (ImageView) baseLayout.findViewById(R.id.iv_sponsor_dp);
        tv_sponsor_name = (TextView) baseLayout.findViewById(R.id.tv_sponsor_name);
        tv_sponsor_description = (TextView) baseLayout.findViewById(R.id.tv_sponsor_description);

        initLists(baseLayout);

    }

    private void initLists(View baseLayout) {
        //topic list
        topicList = new ArrayList<>();
        rv_topics_list = (RecyclerView) baseLayout.findViewById(R.id.rv_topics_list);
        rv_topics_list.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL,
                false));
        rv_topics_list.setNestedScrollingEnabled(false);

        verifiedSessionTopicsListAdapter = new OrganisedSessionTopicsListAdapter(VerifiedSessionDetailsFragment.this,
                modTopicList(topicList), "dis");
        rv_topics_list.setAdapter(verifiedSessionTopicsListAdapter);

        //interest Tags list
        taggedInterest = new ArrayList<>();
        rv_interest_tag_list = (RecyclerView) baseLayout.findViewById(R.id.rv_interest_tag_list);
        rv_interest_tag_list.setLayoutManager(new LinearLayoutManager(getActivity(),
                LinearLayoutManager.HORIZONTAL, false));
        taggedInterestAdapter = new TaggedInterestAdapter(this, taggedInterest, 2);
        rv_interest_tag_list.setAdapter(taggedInterestAdapter);

        //interest Tags list
        preRequisitesList = new ArrayList<>();
        rv_prerequisites_list = (RecyclerView) baseLayout.findViewById(R.id.rv_prerequisites_list);
        rv_prerequisites_list.setLayoutManager(new LinearLayoutManager(getActivity(),
                RecyclerView.VERTICAL, false));
        verifiedSessionPrerequisitesListAdapter =
                new OrganisedSessionTopicsListAdapter(VerifiedSessionDetailsFragment.this,
                        modTopicList(preRequisitesList), "preq");
        rv_prerequisites_list.setAdapter(verifiedSessionPrerequisitesListAdapter);


        rv_friend_attend_list = (RecyclerView) baseLayout.findViewById(R.id.rv_friend_attend_list);
        rv_friend_attend_list.setLayoutManager(new LinearLayoutManager(getActivity(),
                RecyclerView.HORIZONTAL, false));

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
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.iv_add_coordinator:
                        Intent coordinatorIntent = new Intent(getActivity(), AssignCoordinatorsActivity.class);
                        coordinatorIntent.putExtra("venue_id", pojoSessionDetailsResponse.data.venues.get(0).venueId);
                        coordinatorIntent.putExtra("session_id", sessionId);

                        for (int i = 0; i < pojoSessionDetailsResponse.data.coOrdinatorsData.size(); i++) {
                            if (i == 0) {
                                coordinatorIntent.putExtra("coor1",
                                        pojoSessionDetailsResponse.data.coOrdinatorsData.get(0).userId);
                            }
                            if (i == 1) {
                                coordinatorIntent.putExtra("coor2",
                                        pojoSessionDetailsResponse.data.coOrdinatorsData.get(1).userId);
                            }
                        }
                        startActivity(coordinatorIntent);
                        break;

                    case R.id.iv_update_room:
                        openUpdateRoomDialog();
                        break;

                    case R.id.tv_num_attending:
                        openAttendeeListDialog("totalattendees");
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


    private void openAttendeeListDialog(String reaction) {
        TextView tv_title;
        ImageView iv_back;

        final Dialog userListDialog = new MyCustomThemeDialog(getActivity());
        userListDialog.setContentView(R.layout.contact_list_dialog);
        userListDialog.getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        ProgressBar pb_loading_users = (ProgressBar) userListDialog.findViewById(R.id.pb_loading_users);
        pb_loading_users.setVisibility(View.GONE);
        tv_title = userListDialog.findViewById(R.id.tv_title);
        tv_title.setText(this.getString(R.string.attendees));
        iv_back = (ImageView) userListDialog.findViewById(R.id.iv_back);
        iv_back.setVisibility(View.GONE);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userListDialog.dismiss();
            }
        });

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
                    if (!isGetUpvotersApiCalled && upvoterListLayoutManager.findLastVisibleItemPosition() > upvoterList.size() - 2) {
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

    private void openUpdateRoomDialog() {
        roomDialog = new MyCustomThemeDialog(getActivity());
        roomDialog.setContentView(R.layout.edit_content_dialog);

        final EditText et_content = (EditText) roomDialog.findViewById(R.id.et_content);
        TextView tv_done = (TextView) roomDialog.findViewById(R.id.tv_done);
        TextView tv_cancel = (TextView) roomDialog.findViewById(R.id.tv_cancel);
        TextView tv_dialog_description = (TextView) roomDialog.findViewById(R.id.tv_dialog_description);

        tv_dialog_description.setText(R.string.update_room);
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                roomDialog.dismiss();
            }
        });

        tv_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et_content.getText().toString().isEmpty()) {
                    if (getActivity() != null)
                        commonFunctions.setToastMessage(getActivity().getApplicationContext(), getString(R.string.please_enter_data),
                                Toast.LENGTH_LONG, AppKeys.TOAST_USER);
                } else {
                    hitAssignRoomApi(sessionId, et_content.getText().toString());
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    et_content.requestFocus();
                    imm.hideSoftInputFromWindow(et_content.getWindowToken(), 0);
                    roomDialog.dismiss();
                }
            }
        });
        roomDialog.setCancelable(false);
        roomDialog.show();
    }

    private void setClickListeners() {
        iv_add_coordinator.setOnClickListener(onClickListener);
        iv_update_room.setOnClickListener(onClickListener);
        tv_num_attending.setOnClickListener(onClickListener);

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
        pb_loading_content.setVisibility(View.VISIBLE);
        ll_parent.setVisibility(View.GONE);

        call.enqueue(new Callback<PojoSessionDetailsResponse>() {
            @Override
            public void onResponse(Call<PojoSessionDetailsResponse> call, Response<PojoSessionDetailsResponse> response) {
                String message = "";
                if (response != null && response.body() != null) {
                    message = response.body().message;
                    pojoSessionDetailsResponse = response.body();
                    Activity activity = getActivity();
                    if (isAdded() && activity != null) {
                        setDataOnViews();
                        if (pojoSessionDetailsResponse.data.sponsorid != null
                                && !pojoSessionDetailsResponse.data.sponsorid.isEmpty()
                                && !pojoSessionDetailsResponse.data.sponsorid.equals("0")) {
                            hitGetOrganisationInfoApi(user_id, pojoSessionDetailsResponse.data.sponsorid, "home");

                        }
                    }


                    topicList.clear();
                    topicList.addAll(modTopicList(response.body().data.topics));
                    verifiedSessionTopicsListAdapter.notifyDataSetChanged();

                    taggedInterest.clear();
                    taggedInterest.addAll(response.body().data.sessionInterestArr);
                    taggedInterestAdapter.notifyDataSetChanged();

                    preRequisitesList.clear();
                    preRequisitesList.addAll(modTopicList(response.body().data.prerequisite));
                    verifiedSessionPrerequisitesListAdapter.notifyDataSetChanged();
                    ll_parent.setVisibility(View.VISIBLE);
                    pb_loading_content.setVisibility(View.GONE);
                    //   if(getActivity()!=null)commonFunctions.setToastMessage(getActivity().getApplicationContext(), message, Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);
                }
            }

            @Override
            public void onFailure(Call<PojoSessionDetailsResponse> call, Throwable t) {
                // Log error here since request failed
                if (getActivity() != null)
                    commonFunctions.setToastMessage(getActivity().getApplicationContext(), t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                // ll_parent.setVisibility(View.VISIBLE);
                pb_loading_content.setVisibility(View.GONE);
                ll_no_connection_wrapper.setVisibility(View.VISIBLE);
            }
        });
    }


    private void hitGetOrganisationInfoApi(final String user_id, final String organisation_id, String type) {
        Call<PojoGetOrganisationInfoResponse> call = apiService.getOrganisationInfo(user_id, organisation_id,
                type, "0");
        call.enqueue(new Callback<PojoGetOrganisationInfoResponse>() {
            @Override
            public void onResponse(Call<PojoGetOrganisationInfoResponse> call,
                                   Response<PojoGetOrganisationInfoResponse> response) {
                if (response != null && response.body() != null) {
                    PojoGetOrganisationInfoResponseData pojoGetOrganisationInfoResponseData = response.body().data;
                    if (pojoGetOrganisationInfoResponseData != null) {
                        rl_sponsor_wrapper.setVisibility(View.VISIBLE);
                        try {
                            if (getActivity() != null && !pojoGetOrganisationInfoResponseData.logo.isEmpty()) {
                                Glide.with(getActivity().getApplicationContext())
                                        .load(pojoGetOrganisationInfoResponseData.logo)
                                        .apply(new RequestOptions().override(192, 192))
                                        .apply(bitmapTransform(new CropCircleTransformation()))
                                        .into(iv_sponsor_dp);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        tv_sponsor_name.setText(pojoGetOrganisationInfoResponseData.name);
                        tv_sponsor_description.setText(Html.fromHtml(pojoGetOrganisationInfoResponseData.description));

                        rl_sponsor_wrapper.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getActivity(), OrganisationProfileInfoActivity.class);
                                intent.putExtra(AppKeys.ORGANISATION_ID, pojoGetOrganisationInfoResponseData.id);
                                startActivity(intent);
                            }
                        });
                    }
                }
            }

            @Override
            public void onFailure(Call<PojoGetOrganisationInfoResponse> call, Throwable t) {
                // Log error here since request failed
                //  cl_parent.setVisibility(View.VISIBLE);
                //  pb_loading_content.setVisibility(View.GONE);
            }
        });
    }

    private void setDataOnViews() {
        if (pojoSessionDetailsResponse != null) {
            try {
                if (pojoSessionDetailsResponse.data.totalAllowedMembers.equals("0")) {
                    tv_num_allowed.setVisibility(View.GONE);
                } else {
                    tv_num_allowed.setVisibility(View.VISIBLE);
                }
                tv_num_allowed.setText("(" + pojoSessionDetailsResponse.data.totalAllowedMembers + " "
                        + getString(R.string.allowed) + ")");

                if (pojoSessionDetailsResponse.data.totalAttend.equals("0")) {
                    tv_num_attending.setVisibility(View.GONE);
                } else {
                    tv_num_attending.setVisibility(View.VISIBLE);
                }
                tv_num_attending.setText("" + pojoSessionDetailsResponse.data.totalAttend + " "
                        + getString(R.string.attending));
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (pojoSessionDetailsResponse.data.sessionAttendFriends!=null
                    &&pojoSessionDetailsResponse.data.sessionAttendFriends.isEmpty()) {
                ll_friend_attend_wrapper.setVisibility(View.GONE);
            } else {
                sessionAttendingFriendListAdapter =
                        new SessionAttendingFriendListAdapter(VerifiedSessionDetailsFragment.this,
                                pojoSessionDetailsResponse.data.sessionAttendFriends);
                rv_friend_attend_list.setAdapter(sessionAttendingFriendListAdapter);
            }

            if (pojoSessionDetailsResponse.data.finalizedData.equals("1")) {
                rl_timestamp_wrapper.setVisibility(View.VISIBLE);
                rl_venue_wrapper.setVisibility(View.VISIBLE);
                if (pojoSessionDetailsResponse.data != null && !pojoSessionDetailsResponse.data.venues.isEmpty()) {
                    tv_venue.setText(pojoSessionDetailsResponse.data.venues.get(0).venueName);
                    tv_venue.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent venueIntent = new Intent(getActivity(), VenueActivity.class);
                            venueIntent.putExtra("id", pojoSessionDetailsResponse.data.venues.get(0).venueId);
                            startActivity(venueIntent);
                        }
                    });

                }
                if (!pojoSessionDetailsResponse.data.eventDate.isEmpty()) {
                    tv_time.setText("" + pojoSessionDetailsResponse.data.eventTime.get(0).value
                            + " (" + pojoSessionDetailsResponse.data.eventDuration + " min)"
                            + "     " + commonFunctions.parseDateToName(pojoSessionDetailsResponse.data.eventDate.get(0).value));

                }
            } else {
                rl_timestamp_wrapper.setVisibility(View.GONE);
                rl_venue_wrapper.setVisibility(View.GONE);
            }


            if (!pojoSessionDetailsResponse.data.venues.isEmpty()) {
                if (pojoSessionDetailsResponse.data.finalizedData.equals("1")) {
                    for (int i = 0; i < pojoSessionDetailsResponse.data.venues.get(0).venueAmbassadors.size(); i++) {
                        if (user_id.equals(pojoSessionDetailsResponse.data.venues.get(0).venueAmbassadors.get(i).userId)) {
                            iv_add_coordinator.setVisibility(View.VISIBLE);
                            rl_coordinator_wrapper.setVisibility(View.VISIBLE);
                            if (pojoSessionDetailsResponse.data.coOrdinatorsData.isEmpty()) {
                                rl_coordinator1_wrapper.setVisibility(View.GONE);
                                rl_coordinator2_wrapper.setVisibility(View.GONE);
                            }
                            break;
                        }
                    }
                }
            }

            if (pojoSessionDetailsResponse.data.finalPlace.isEmpty()) {
                rl_room_wrapper.setVisibility(View.GONE);
            } else {
                rl_room_wrapper.setVisibility(View.VISIBLE);
                tv_room.setText(pojoSessionDetailsResponse.data.finalPlace);

                boolean iAmCoordinator = false;
                if (pojoSessionDetailsResponse.data.coOrdinatorsData != null) {
                    for (int i = 0; i < pojoSessionDetailsResponse.data.coOrdinatorsData.size(); i++) {
                        if (user_id.equals(pojoSessionDetailsResponse.data.coOrdinatorsData.get(i).userId)) {
                            iAmCoordinator = true;
                        }
                    }
                }
                if (iAmCoordinator) {
                    iv_update_room.setVisibility(View.VISIBLE);
                } else {
                    iv_update_room.setVisibility(View.GONE);
                }
            }

            setCoordinatorData();
            setInitiatorData();
            setSharerData();
        }
    }

    private void setInitiatorData() {
        if (!pojoSessionDetailsResponse.data.sharer.isEmpty()
                && pojoSessionDetailsResponse.data.sharer.get(0).userId
                .equals(pojoSessionDetailsResponse.data.createdBy.userId)) {
            rl_initiator_wrapper.setVisibility(View.GONE);
        } else {
            tv_initiator_name.setText(pojoSessionDetailsResponse.data.createdBy.userFullname);
            if (pojoSessionDetailsResponse.data.createdBy.userPicture.isEmpty()) {
                if (getActivity() != null) Glide.with(getActivity().getApplicationContext())
                        .load(R.drawable.placeholder)
                        .apply(new RequestOptions().override(64, 64))
                        .apply(bitmapTransform(new CropCircleTransformation()))
                        .into(iv_initiator_dp);
            } else {
                if (getActivity() != null) Glide.with(getActivity().getApplicationContext())
                        .load(pojoSessionDetailsResponse.data.createdBy.userPicture)
                        .apply(new RequestOptions().override(64, 64))
                        .apply(bitmapTransform(new CropCircleTransformation()))
                        .into(iv_initiator_dp);
            }
        }

    }

    private void setSharerData() {
        //if session is not in assigned state yet hide sharer bar
        if (pojoSessionDetailsResponse.data.status.equals("2")) {
            rl_sharer_wrapper.setVisibility(View.GONE);
        } else if (!pojoSessionDetailsResponse.data.sharer.isEmpty()) {
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
            rl_coordinator_wrapper.setVisibility(View.VISIBLE);

            if (pojoSessionDetailsResponse.data.coOrdinatorsData.size() == 1) {
                rl_coordinator2_wrapper.setVisibility(View.GONE);
            }
            for (int i = 0; i < pojoSessionDetailsResponse.data.coOrdinatorsData.size(); i++) {
                if (user_id.equals(pojoSessionDetailsResponse.data.coOrdinatorsData.get(i).userId)) {
                    rl_room_wrapper.setVisibility(View.VISIBLE);
                    iv_update_room.setVisibility(View.VISIBLE);
                }

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
        } /*else {
            rl_coordinator_wrapper.setVisibility(View.GONE);
        }*/
    }

   /* private void hitGetVenueByIdApi(final String user_id, final String venue_id) {
        boolean allFieldsOk = !venue_id.isEmpty() && !user_id.isEmpty();
        if (allFieldsOk) {
            Call<PojoGetVenueDetailsResponse> call = apiService.getVenueDetailsFromIdApi(user_id, venue_id);
            call.enqueue(new Callback<PojoGetVenueDetailsResponse>() {
                @Override
                public void onResponse(Call<PojoGetVenueDetailsResponse> call, Response<PojoGetVenueDetailsResponse> response) {
                    String message = "";
                    if (response.body().data != null) {
                        message = response.body().message;
                        if (pojoSessionDetailsResponse.data.finalizedData.equals("1")) {
                            for (int i = 0; i < response.body().data.teams.size(); i++) {
                                if (response.body().data.teams.get(i).userDetail.userId.equals(user_id)) {
                                    if (response.body().data.teams.get(i).role.equals("Ambassadors")) {
                                        iv_add_coordinator.setVisibility(View.VISIBLE);
                                        rl_coordinator_wrapper.setVisibility(View.VISIBLE);
                                        if (pojoSessionDetailsResponse.data.coOrdinatorsData.isEmpty()) {
                                            rl_coordinator1_wrapper.setVisibility(View.GONE);
                                            rl_coordinator2_wrapper.setVisibility(View.GONE);
                                        }

                                    }

                                }

                            }
                        }


                    } else {
                        if(getActivity()!=null)commonFunctions.setToastMessage(getActivity().getApplicationContext(), "" + message, Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);
                    }
                }

                @Override
                public void onFailure(Call<PojoGetVenueDetailsResponse> call, Throwable t) {
                    // Log error here since request failed
                    if(getActivity()!=null)commonFunctions.setToastMessage(getActivity().getApplicationContext(), t.toString(), Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);

                }
            });
        }
    }*/


    private void hitAssignRoomApi(final String session_id, String place) {
        Call<PojoNoDataResponse> call = apiService.assignSessionRoom(session_id, place);
        pb_loading_content.setVisibility(View.VISIBLE);
        ll_parent.setVisibility(View.GONE);
        call.enqueue(new Callback<PojoNoDataResponse>() {
            @Override
            public void onResponse(Call<PojoNoDataResponse> call, Response<PojoNoDataResponse> response) {
                String message = "";
                if (response != null && response.body() != null) {
                    message = response.body().message;
                    hitGetSessionDetailsApi(user_id, session_id, "home");
                }
            }

            @Override
            public void onFailure(Call<PojoNoDataResponse> call, Throwable t) {
                // Log error here since request failed
                if (getActivity() != null)
                    commonFunctions.setToastMessage(getActivity().getApplicationContext(), t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                // ll_parent.setVisibility(View.VISIBLE);
                pb_loading_content.setVisibility(View.GONE);
            }
        });
    }


    @Override
    public void taggedInterestItemClickCallback(int position, PojoGetInterestListResponseDataListItem pojoGetInterestListResponseDataListItem) {

    }

    @Override
    public void onFriendsAttendingListMethodCallback(int position) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mRegistrationBroadcastReceiver);
        //    unsentMsgHandler.removeCallbacks(unsentMsgRunnable);

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
                    if (getActivity() != null) {
                        commonFunctions.setToastMessage(getActivity().getApplicationContext(), message,
                                Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                    }
                    // venueList.remove(position);
                }
            }

            @Override
            public void onFailure(Call<PojoUserConnectResponse> call, Throwable t) {
                // Log error here since request failed
                if (getActivity() != null) {
                    commonFunctions.setToastMessage(getActivity().getApplicationContext(), t.toString(),
                            Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                }
            }
        });
    }
}
