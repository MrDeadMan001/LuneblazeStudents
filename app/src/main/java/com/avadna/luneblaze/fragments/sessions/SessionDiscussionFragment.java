package com.avadna.luneblaze.fragments.sessions;

import android.app.Activity;
import android.app.Dialog;

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

import android.se.omapi.Session;
import android.text.Html;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.activities.sessions.AssignedSessionActivity;
import com.avadna.luneblaze.activities.sessions.VerifiedSessionActivity;
import com.avadna.luneblaze.adapters.session.DiscussionAdapter;
import com.avadna.luneblaze.firebasenotifications.config.Config;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.pojo.PojoLoginResponseData;
import com.avadna.luneblaze.pojo.PojoNoDataResponse;
import com.avadna.luneblaze.pojo.PojoUpvotersAndAttendeesListResponse;
import com.avadna.luneblaze.pojo.PojoUserConnectResponse;
import com.avadna.luneblaze.pojo.pojoQuestion.PojoUserData;
import com.avadna.luneblaze.pojo.pojoSessionDetails.PojoAddSessionDiscussionResponse;
import com.avadna.luneblaze.pojo.pojoSessionDetails.PojoComment;
import com.avadna.luneblaze.pojo.pojoSessionDetails.PojoSessionDetailsResponse;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SessionDiscussionFragment extends Fragment implements
        DiscussionAdapter.DiscussionListAdapterCallback,
        UpvotersAndAttendeesListAdapter.UpvoterAndAttendeesAdapterCallback {

    private LinearLayout ll_no_connection_wrapper;
    private TextView tv_retry;

    private RecyclerView rv_discussion_comments_list;
    private DiscussionAdapter discussionAdapter;
    private boolean isGetDiscussionApiCalled = false;
    private LinearLayoutManager discussionListLayoutManager;
    private List<PojoComment> discussionList;

    private String sessionId;
    private PreferenceUtils preferenceUtils;
    private CommonFunctions commonFunctions;
    private String user_id;
    private String user_name;
    private PojoLoginResponseData pojoLoginResponseData;
    private ApiInterface apiService;
    private String type;
    private String target_discussion_id = "0";
    String top_discussion_id;

    private RelativeLayout rl_parent;
    private ProgressBar pb_loading_content;

    private EditText et_new_comment;
    private ImageButton ib_send;

    /* TextView tv_load_previous;
  private   ProgressBar pb_loading_previous;*/
    private boolean listBottomEndReached = false;
    private boolean listTopEndReached = false;
    private BroadcastReceiver broadcastReceiver;
    private final int API_COMMMENT_COUNT = 15;


    RecyclerView rv_upvoters;
    LinearLayoutManager upvoterListLayoutManager;
    UpvotersAndAttendeesListAdapter upvotersAndAttendeesListAdapter;
    List<PojoUserData> upvoterList;
    boolean isGetUpvotersApiCalled = false;
    int upvoterListOffset = 0;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initBroadCastReceiver();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(broadcastReceiver,
                new IntentFilter(Config.SESSION_DISCUSSION_DELETED));
    }

    private void initBroadCastReceiver() {
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Config.SESSION_DISCUSSION_DELETED)) {
                    String comment_id = intent.getStringExtra(AppKeys.DISCUSSION_ID);
                    if (discussionList != null) {
                        for (int i = 0; i < discussionList.size(); i++) {
                            if (discussionList.get(i).sessionsDiscussionId.equals(comment_id)) {
                                discussionList.remove(i);
                                discussionAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                }
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View baseLayout = inflater.inflate(R.layout.fragment_session_discussion_comment, container,
                false);
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
        preferenceUtils = new PreferenceUtils(getActivity());
        commonFunctions = new CommonFunctions(getActivity());
        user_id = preferenceUtils.get_user_id();
        pojoLoginResponseData = preferenceUtils.getUserLoginData();
        user_name = preferenceUtils.get_user_name();
        sessionId = getArguments().getString("id");
        type = getArguments().getString("type");
        target_discussion_id = getArguments().getString(AppKeys.DISCUSSION_ID);
        top_discussion_id = target_discussion_id;
        initViews(baseLayout);
        initLists(baseLayout);

        if (type != null) {
            if (sessionId != null) {
                if (target_discussion_id == null || target_discussion_id.isEmpty()) {
                    top_discussion_id = "0";
                    hitGetSessionDetailsApi(user_id, sessionId, "discussion", "prev",
                            "0", "0", false);
                } else {
                    hitGetSessionDetailsApi(user_id, sessionId, "discussion", "prev",
                            target_discussion_id, "1", false);
                }
            }
        }
        return baseLayout;
    }


    private void initViews(View baseLayout) {
        ll_no_connection_wrapper = (LinearLayout) baseLayout.findViewById(R.id.ll_no_connection_wrapper);
        ll_no_connection_wrapper.setVisibility(View.GONE);
        tv_retry = (TextView) baseLayout.findViewById(R.id.tv_retry);
        tv_retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                discussionAdapter.setShowProgressBar(true);
                hitGetSessionDetailsApi(user_id, sessionId, "discussion", "prev",
                        "0", "1", false);
            }
        });

        et_new_comment = (EditText) baseLayout.findViewById(R.id.et_new_comment);
        et_new_comment.setFilters(new InputFilter[]{new InputFilter.LengthFilter(AppKeys.COMMENT_AND_REPLY_MAX_LENGTH)});

        ib_send = (ImageButton) baseLayout.findViewById(R.id.ib_send);
        rl_parent = (RelativeLayout) baseLayout.findViewById(R.id.rl_parent);
        rl_parent.setVisibility(View.GONE);
        pb_loading_content = (ProgressBar) baseLayout.findViewById(R.id.pb_loading_content);

        ib_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDiscussion(et_new_comment.getText().toString().trim());
                et_new_comment.getText().clear();
            }
        });
/*
        tv_load_previous = (TextView) baseLayout.findViewById(R.id.tv_load_previous);
        tv_load_previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hitGetSessionDetailsApi(user_id, sessionId, "discussion", "prev",
                        getOldestDiscussionId(), false);
                pb_loading_previous.setVisibility(View.VISIBLE);
                tv_load_previous.setVisibility(View.INVISIBLE);
            }
        });
        pb_loading_previous = (ProgressBar) baseLayout.findViewById(R.id.pb_loading_previous);
        pb_loading_previous.setVisibility(View.INVISIBLE);*/
    }

    public void addDiscussion(String comment) {
        if (!comment.isEmpty()) {
            if (type != null) {
                if (sessionId != null) {
                    if (type.equals(AppKeys.VERIFIED)) {
                        hitAddDiscussionApi(user_id, "add_discussion", sessionId, comment);
                    } else if (type.equals(AppKeys.ORGANISED)) {
                        hitAddDiscussionApi(user_id, "add_comment", sessionId, comment);
                    }
                    PojoComment tempComment = new PojoComment();
                    tempComment.post = comment;
                    tempComment.addedOn = getString(R.string.just_now);
                    tempComment.userData = new PojoUserData();
                    tempComment.userData.userFullname = user_name;
                    tempComment.userData.userId = user_id;
                    tempComment.sessionsDiscussionId = "";
                    tempComment.sessionsCommentId = "";
                    tempComment.userData.userPicture = pojoLoginResponseData.userPicture;
                    tempComment.sessionsId = sessionId;
                    tempComment.totalReplies = 0;
                    tempComment.totalLikes = 0;

                    discussionList.add(0, tempComment);
                    if (getActivity().getClass().getSimpleName().equals("VerifiedSessionActivity")) {
                        ((VerifiedSessionActivity) getActivity()).setAppBarExpanded(false);
                    } else if (getActivity().getClass().getSimpleName().equals("AssignedSessionActivity")) {
                        ((AssignedSessionActivity) getActivity()).setAppBarExpanded(false);
                    }
                    discussionAdapter.notifyDataSetChanged();
                    rv_discussion_comments_list.scrollToPosition(0);

                    // et_new_comment.getText().clear();

                }
            }
        }
    }


    private void initLists(View baseLayout) {
        //interest Tags list
        discussionList = new ArrayList<>();
        rv_discussion_comments_list = (RecyclerView) baseLayout.findViewById(R.id.rv_discussion_comments_list);
        discussionListLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        rv_discussion_comments_list.setLayoutManager(discussionListLayoutManager);
        discussionAdapter = new DiscussionAdapter(this, discussionList, "discussion");
        rv_discussion_comments_list.setAdapter(discussionAdapter);

        rv_discussion_comments_list.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!listBottomEndReached && dy > 4) {
                    if (discussionListLayoutManager.findLastVisibleItemPosition() > discussionList.size() - 3) {
                        hitGetSessionDetailsApi(user_id, sessionId, "discussion", "prev",
                                getLatestDiscussionId(), "0", false);
                    }
                } /*else if (dy < -4) {
                    if (discussionListLayoutManager.findFirstVisibleItemPosition() < 2) {
                        hitGetSessionDetailsApi(user_id, sessionId, "discussion", "prev",
                                getOldestDiscussionId(), false);
                    }
                }*/
            }
        });
    }

    private void hitGetSessionDetailsApi(String user_id, String session_id, String view, final String order
            , final String sessions_discussion_id, String target, final boolean scrollToEnd) {
        ll_no_connection_wrapper.setVisibility(View.GONE);

        Call<PojoSessionDetailsResponse> call = apiService.getSessionDetails(user_id, session_id,
                view, order, sessions_discussion_id, "", target);

        if (!isGetDiscussionApiCalled) {
            discussionAdapter.setShowProgressBar(true);
            isGetDiscussionApiCalled = true;
            call.enqueue(new Callback<PojoSessionDetailsResponse>() {
                @Override
                public void onResponse(Call<PojoSessionDetailsResponse> call, Response<PojoSessionDetailsResponse> response) {
                    String message = "";

                    if (response != null && response.body() != null) {
                        message = response.body().message;
                        if (response.body().status == 1 && !response.body().data.discussions.isEmpty()) {

                            if (target_discussion_id != null) {
                                boolean present = false;
                                for (int i = 0; i < response.body().data.discussions.size(); i++) {
                                    if (target_discussion_id.equals(response.body()
                                            .data.discussions.get(i).sessionsDiscussionId)) {
                                        present = true;
                                        target_discussion_id = null;
                                        break;
                                    }
                                }
                                if (!present) {
                                    target_discussion_id = null;
                                    if (getActivity() != null)
                                        showMessageDialog(getString(R.string.the_comment_has_been_deleted));
                                }
                            }

                            if (sessions_discussion_id.equals("0")) {
                                discussionList.clear();
                            }
                            if (order.equals("next")) {
                                if (response.body().data.discussions.size() < API_COMMMENT_COUNT) {
                                    discussionAdapter.setMoreNextAvailable(false);
                                    listBottomEndReached = true;
                                }
                                if (!sessions_discussion_id.equals("0") && !discussionList.isEmpty()) {
                                    response.body().data.discussions.remove(0);
                                }
                                /*if (!discussionList.isEmpty()) {
                                    discussionList.remove(discussionList.size() - 1);
                                }*/
                                discussionList.addAll(removeReportedPostComments(response.body().data.discussions));
                            } else if (order.equals("prev")) {
                                // Collections.reverse(response.body().data.discussions);
                                if (response.body().data.morePresent == 0) {
                                    discussionAdapter.setMoreNextAvailable(false);
                                    discussionAdapter.setShowBottomMoreButton(false);
                                }

                                if (sessions_discussion_id.equals("0")) {
                                    if (response.body().data.discussions.size() < API_COMMMENT_COUNT) {
                                        discussionAdapter.setMorePreviousAvailable(false);
                                        discussionAdapter.setShowTopMoreButton(false);
                                        discussionAdapter.setShowBottomMoreButton(false);
                                        listTopEndReached = true;
                                    } else {
                                        discussionAdapter.setMoreNextAvailable(false);
                                        discussionAdapter.setShowBottomMoreButton(false);
                                    }
                                } else {
                                    if (response.body().data.discussions.size() < API_COMMMENT_COUNT) {
                                        discussionAdapter.setMorePreviousAvailable(false);
                                        discussionAdapter.setShowTopMoreButton(false);
                                        listTopEndReached = true;
                                    }

                                    if (!discussionList.isEmpty() && !response.body().data.discussions.isEmpty()) {
                                        response.body().data.discussions.remove(0);
                                    }

                                    if (target != null && target.equals("0")) {
                                        for (int i = 0; i < response.body().data.discussions.size(); i++) {
                                            if (response.body().data.discussions.get(i).sessionsDiscussionId.equals(top_discussion_id)) {
                                                response.body().data.discussions.remove(i);
                                                break;
                                            }
                                        }
                                    }
                                }

                                discussionList.addAll(removeReportedPostComments(response.body().data.discussions));
                            }

                            if (getActivity() != null) {
                                if (getActivity().getClass().getSimpleName().equals("VerifiedSessionActivity")) {
                                    discussionAdapter.setCreator_id(response.body().data.createdBy.userId);
                                } else if (getActivity().getClass().getSimpleName().equals("AssignedSessionActivity")) {
                                    discussionAdapter.setCreator_id(response.body().data.presentors.get(0).userId);
                                }
                            }

                            discussionAdapter.setShowProgressBar(false);

                            discussionAdapter.notifyDataSetChanged();
                            if (scrollToEnd) {
                                /*  if (*//*reply_id.equals("0") && *//*order.equals("prev")) {
                                    rv_discussion_comments_list.scrollToPosition(discussionList.size());
                                } else*/
                                {
                                    rv_discussion_comments_list.scrollToPosition(discussionList.size() + 1);
                                }
                            }
                        } else {
                            if (getActivity() != null)
                                commonFunctions.setToastMessage(getActivity().getApplicationContext(), message, Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                            discussionAdapter.setShowProgressBar(false);
                            discussionAdapter.setShowBottomMoreButton(false);
                            discussionAdapter.setShowTopMoreButton(false);

                        }

                        rl_parent.setVisibility(View.VISIBLE);
                        pb_loading_content.setVisibility(View.GONE);
                    }
                    if (target_discussion_id != null && !target_discussion_id.isEmpty() && !target_discussion_id.equals("0")) {
                        if (response.body().data.status.equals("2")) {
                            ((VerifiedSessionActivity) getActivity()).setAppBarExpanded(false);
                        } else if (response.body().data.status.equals("3")) {
                            ((AssignedSessionActivity) getActivity()).setAppBarExpanded(false);
                        }
                    }
                    isGetDiscussionApiCalled = false;
                }

                @Override
                public void onFailure(Call<PojoSessionDetailsResponse> call, Throwable t) {
                    // Log error here since request failed
                  /*  if (getActivity() != null)
                        commonFunctions.setToastMessage(getActivity().getApplicationContext(),
                                t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);*/
                    // rl_parent.setVisibility(View.VISIBLE);

                    if (SessionDiscussionFragment.this != null) {
                        pb_loading_content.setVisibility(View.GONE);
                        isGetDiscussionApiCalled = false;
                        discussionAdapter.setShowProgressBar(false);
                        if (discussionList.isEmpty()) {
                            ll_no_connection_wrapper.setVisibility(View.VISIBLE);
                        }
                    }
                }
            });
        }
    }

    public void showMessageDialog(String message) {
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
        tv_dialog_description.setText(message);
        confirmationDialog.show();
    }

    public List<PojoComment> removeReportedPostComments(List<PojoComment> comments) {
        List<PojoComment> moddedComments = new ArrayList<>();
        for (int i = 0; i < comments.size(); i++) {
            if (comments.get(i).iReported == 0) {
                moddedComments.add(comments.get(i));
            }
        }
        return moddedComments;
    }

    private void hitAddDiscussionApi(final String user_id, final String type, String session_id, final String discussion) {
        Call<PojoAddSessionDiscussionResponse> call = null;
        if (SessionDiscussionFragment.this.type != null) {
            if (sessionId != null) {
                call = apiService.sessionConnects(user_id, type, session_id, discussion, "", "", "");
            }
        }

        call.enqueue(new Callback<PojoAddSessionDiscussionResponse>() {
            @Override
            public void onResponse(Call<PojoAddSessionDiscussionResponse> call, Response<PojoAddSessionDiscussionResponse> response) {
                if (response != null && response.body() != null) {
                    String message = response.body().message;
                    commonFunctions.setToastMessage(getContext(), "" + message, Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                    if (SessionDiscussionFragment.this.type != null) {
                        if (sessionId != null) {
                            if (response.body().data.id != null && response.body().data.time != null) {
                                for (int i = 0; i < discussionList.size(); i++) {
                                    if (discussionList.get(i).sessionsDiscussionId.isEmpty()) {
                                        discussionList.get(i).sessionsDiscussionId = String.valueOf(response.body().data.id);
                                        discussionList.get(i).addedOn = response.body().data.time;
                                        discussionAdapter.notifyDataSetChanged();
                                        break;
                                    }
                                }
                            } /*else {
                                hitGetSessionDetailsApi(user_id, sessionId, "discussion",
                                        "prev", "0", true);
                            }*/
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<PojoAddSessionDiscussionResponse> call, Throwable t) {
                // Log error here since request failed
                if (getActivity() != null) {
                    commonFunctions.setToastMessage(getActivity().getApplicationContext(),
                            getString(R.string.comment_not_added),
                            Toast.LENGTH_LONG, AppKeys.TOAST_USER);
                    et_new_comment.setText(discussion);
                    for (int i = discussionList.size() - 1; i >= 0; i--) {
                        if (discussionList.get(i).sessionsDiscussionId.isEmpty()) {
                            discussionList.remove(i);
                        }
                    }
                    discussionAdapter.notifyDataSetChanged();
                    if (getActivity().getClass().getSimpleName().equals("VerifiedSessionActivity")) {
                        ((VerifiedSessionActivity) getActivity()).setCommentText(discussion);
                    } else if (getActivity().getClass().getSimpleName().equals("AssignedSessionActivity")) {
                        ((AssignedSessionActivity) getActivity()).setCommentText(discussion);
                    }
                }

            }
        });
    }

    private String getLatestDiscussionId() {
        if (!discussionList.isEmpty()) {
            return discussionList.get(discussionList.size() - 1).sessionsDiscussionId;
        } else {
            return "0";
        }
    }

    private String getOldestDiscussionId() {
        if (!discussionList.isEmpty()) {
            return discussionList.get(0).sessionsDiscussionId;
        } else {
            return "0";
        }
    }


    @Override
    public void discussionListItemClickCallback(final int position, String type) {
        switch (type) {
            case AppKeys.LIKE:

                if (discussionList.get(position).iLike) {
                    hitDiscussionLikeApi(user_id, discussionList.get(position).sessionsDiscussionId,
                            "unlike_session_discussion");
                    discussionList.get(position).totalLikes--;

                } else {
                    hitDiscussionLikeApi(user_id, discussionList.get(position).sessionsDiscussionId,
                            "like_session_discussion");
                    discussionList.get(position).totalLikes++;
                }
                discussionList.get(position).iLike = !discussionList.get(position).iLike;

                discussionAdapter.notifyDataSetChanged();
                break;

            case AppKeys.EDIT:
                openEditDialog(position);
                break;

            case AppKeys.DELETE:
                TextView tv_dialog_description, tv_yes, tv_no;
                final Dialog confirmationDialog = new MyCustomThemeDialog(getActivity());
                confirmationDialog.setContentView(R.layout.yes_no_dialog);
                tv_dialog_description = (TextView) confirmationDialog.findViewById(R.id.tv_dialog_description);
                tv_yes = (TextView) confirmationDialog.findViewById(R.id.tv_yes);
                tv_no = (TextView) confirmationDialog.findViewById(R.id.tv_no);
                tv_dialog_description.setText(getString(R.string.delete_confirm));

                tv_no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        confirmationDialog.dismiss();
                    }
                });

                tv_yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        hitDeleteDiscussionApi(user_id, discussionList.get(position).sessionsDiscussionId, position);
                        discussionList.remove(position);
                        discussionAdapter.notifyDataSetChanged();
                        confirmationDialog.dismiss();
                    }
                });
                confirmationDialog.show();

                break;

            case AppKeys.REPORT_COMMENT:
                openReportDialog(position, user_id, "report_session_discussion", discussionList.get(position).sessionsDiscussionId);
                break;

            case AppKeys.LIKE_COUNT:
                openUserListDialog(discussionList.get(position).sessionsDiscussionId, "sessiondiscussionreply");
                break;


            case AppKeys.LOAD_NEXT:
                if (!listBottomEndReached) {
                    hitGetSessionDetailsApi(user_id, sessionId, "discussion", "next",
                            getLatestDiscussionId(), "0", false);
                }
                break;

            case AppKeys.LOAD_PREVIOUS:
                if (!listTopEndReached) {
                    hitGetSessionDetailsApi(user_id, sessionId, "discussion", "prev",
                            getOldestDiscussionId(), "0", false);
                }
                break;
        }
    }

    private void openReportDialog(final int position, final String user_id, final String type, final String id) {
        final Dialog dialog = new MyCustomThemeDialog(getActivity());
        dialog.setContentView(R.layout.edit_content_dialog);
        final EditText et_content = (EditText) dialog.findViewById(R.id.et_content);
        TextView tv_done = (TextView) dialog.findViewById(R.id.tv_done);
        TextView tv_cancel = (TextView) dialog.findViewById(R.id.tv_cancel);
        TextView tv_dialog_description = (TextView) dialog.findViewById(R.id.tv_dialog_description);
        tv_dialog_description.setText(getString(R.string.report_reason_description));
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        tv_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commonFunctions.hitReportContentApi(user_id, type, id, et_content.getText().toString().trim());
                discussionList.remove(position);
                discussionAdapter.notifyDataSetChanged();
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(et_content.getWindowToken(), 0);
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    private void hitDiscussionLikeApi(String user_id, String sessionsDiscussionId, String reaction) {
        Call<PojoNoDataResponse> call = apiService.likeUnlikeSessionDiscussion(user_id, sessionsDiscussionId, reaction);
        call.enqueue(new Callback<PojoNoDataResponse>() {
            @Override
            public void onResponse(Call<PojoNoDataResponse> call, Response<PojoNoDataResponse> response) {
                if (response != null && response.body() != null) {
                    String message = response.body().message;
//                    if(getActivity()!=null)commonFunctions.setToastMessage(getActivity().getApplicationContext(), "" + message, Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);
                }
            }

            @Override
            public void onFailure(Call<PojoNoDataResponse> call, Throwable t) {
                // Log error here since request failed
                if (getActivity() != null)
                    commonFunctions.setToastMessage(getActivity().getApplicationContext(), t.toString(),
                            Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
            }
        });
    }

    private void openEditDialog(final int position) {
        final Dialog dialog = new MyCustomThemeDialog(getActivity());
        dialog.setContentView(R.layout.edit_content_dialog);
        final EditText et_content = (EditText) dialog.findViewById(R.id.et_content);
        TextView tv_done = (TextView) dialog.findViewById(R.id.tv_done);
        TextView tv_cancel = (TextView) dialog.findViewById(R.id.tv_cancel);
        et_content.setText(Html.fromHtml(discussionList.get(position).post));
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        tv_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String newData = et_content.getText().toString().trim();
                if (newData.isEmpty()) {
                    if (getActivity() != null)
                        commonFunctions.setToastMessage(getActivity().getApplicationContext(),
                                getString(R.string.please_enter_data),
                                Toast.LENGTH_LONG, AppKeys.TOAST_USER);
                } else {
                    if (discussionList.get(position).sessionsDiscussionId != null) {
                        hitEditDiscussionApi(user_id, discussionList.get(position).sessionsDiscussionId,
                                newData, position);
                        discussionList.get(position).post = newData;
                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(et_content.getWindowToken(), 0);
                        //tv_question.setText(response.body().data.post);
                        discussionAdapter.notifyDataSetChanged();
                    }

                    dialog.dismiss();
                }
            }
        });
        dialog.show();
    }


    private void hitEditDiscussionApi(String user_id, String reply_id, final String newData, final int position) {
        Call<PojoNoDataResponse> call = apiService.editSessionDiscussionAndReply(user_id, reply_id, newData);
        call.enqueue(new Callback<PojoNoDataResponse>() {
            @Override
            public void onResponse(Call<PojoNoDataResponse> call, Response<PojoNoDataResponse> response) {
                if (response != null && response.body() != null) {
                    String message = response.body().message;
                    if (getActivity() != null)
                        commonFunctions.setToastMessage(getActivity().getApplicationContext(), "" + message, Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                }

            }

            @Override
            public void onFailure(Call<PojoNoDataResponse> call, Throwable t) {
                // Log error here since request failed
                if (getActivity() != null)
                    commonFunctions.setToastMessage(getActivity().getApplicationContext(), t.toString(),
                            Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
            }
        });
    }


    private void hitDeleteDiscussionApi(String user_id, String discussion_id, final int position) {
        Call<PojoNoDataResponse> call = apiService.deleteSessionDiscussionAndReply(user_id, discussion_id);
        call.enqueue(new Callback<PojoNoDataResponse>() {
            @Override
            public void onResponse(Call<PojoNoDataResponse> call, Response<PojoNoDataResponse> response) {
                if (response != null && response.body() != null) {
                    String message = response.body().message;
                    if (getActivity() != null)
                        commonFunctions.setToastMessage(getActivity().getApplicationContext(),
                                "" + message, Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);

                }
            }

            @Override
            public void onFailure(Call<PojoNoDataResponse> call, Throwable t) {
                // Log error here since request failed
                if (getActivity() != null)
                    commonFunctions.setToastMessage(getActivity().getApplicationContext(), t.toString(),
                            Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
            }
        });
    }

    private void openUserListDialog(String id, String type) {
        TextView tv_title;
        ImageView iv_back;

        final Dialog userListDialog = new MyCustomThemeDialog(getActivity());
        userListDialog.setContentView(R.layout.contact_list_dialog);
        userListDialog.getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        ProgressBar pb_loading_users = (ProgressBar) userListDialog.findViewById(R.id.pb_loading_users);
        pb_loading_users.setVisibility(View.GONE);
        tv_title = userListDialog.findViewById(R.id.tv_title);
        tv_title.setText(this.getString(R.string.likes));
        iv_back = (ImageView) userListDialog.findViewById(R.id.iv_back);
        iv_back.setVisibility(View.GONE);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userListDialog.dismiss();
            }
        });
        userListDialog.setTitle(this.getString(R.string.likes));

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
                        hitGetUpvotersListApi(user_id, id, type, ++upvoterListOffset);
                    }
                }
            }
        });

        upvoterListOffset = 0;
        hitGetUpvotersListApi(user_id, id, type, upvoterListOffset);
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
                    commonFunctions.setToastMessage(getActivity().getApplicationContext(), t.toString(),
                            Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                    upvotersAndAttendeesListAdapter.setShowProgressBar(false);
                    isGetUpvotersApiCalled = false;
                }
            });
        }
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
