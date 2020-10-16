package com.avadna.luneblaze.fragments.interest;

import android.app.Dialog; import com.avadna.luneblaze.helperClasses.MyCustomThemeDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.activities.interest.InterestActivity;
import com.avadna.luneblaze.activities.question.QuestionActivity;
import com.avadna.luneblaze.adapters.InterestRelatedQuestionListAdapter;
import com.avadna.luneblaze.firebasenotifications.config.Config;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.pojo.PojoLoginResponseData;
import com.avadna.luneblaze.pojo.PojoNoDataResponse;
import com.avadna.luneblaze.pojo.pojoInterest.PojoInterestDetailsResponse;
import com.avadna.luneblaze.pojo.pojoInterest.PojoInterestQuestions;
import com.avadna.luneblaze.pojo.pojoSessionDetails.PojoAddSessionDiscussionResponse;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;


public class InterestQuestionListFragment extends Fragment implements
        InterestRelatedQuestionListAdapter.InterestRelatedQuestionsAdapterCallback {

    LinearLayout ll_parent;
    ProgressBar pb_loading_content;
    //ProgressBar pb_loading_more_questions;

    String user_id = "";
    ApiInterface apiService;
    PreferenceUtils preferenceUtils;
    CommonFunctions commonFunctions;

    // TextView tv_no_more_questions;

    LinearLayout ll_no_connection_wrapper;
    TextView tv_retry;

    RecyclerView rv_item_list;
    InterestRelatedQuestionListAdapter interestRelatedQuestionListAdapter;
    PojoInterestDetailsResponse pojoInterestDetailsResponse;
    List<PojoInterestQuestions> questionsList;
    boolean isQuestionListApiCalled = false;
    LinearLayoutManager questionListLayoutManager;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    String type;
    int offset = 0;


    TextView tv_filter;
    Dialog filterDialog;
    TextView tv_no_questions;

    static final String RELEVANCE_ANY = "any";
    static final String RELEVANCE_MOST_UPVOTED = "most_upvoted";

    String newRelevance = RELEVANCE_ANY;
    String oldRelevance = RELEVANCE_ANY;

    static final String MY_ROLES_ANY = "any";
    static final String MY_ROLES_I_ASKED = "I have asked";
    static final String MY_ROLES_I_ANSWERED = "I have answered";
    static final String MY_ROLES_I_FOLLOWING = "I am following";

    String newMyRole = MY_ROLES_ANY;
    String oldMyRole = MY_ROLES_ANY;

    RelativeLayout rl_comment_bar;
    EditText et_comment;
    TextView bt_send;


    public InterestQuestionListFragment() {
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
        View baseLayout = inflater.inflate(R.layout.fragment_user_activity_question, container, false);
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
        preferenceUtils = new PreferenceUtils(getContext());
        commonFunctions = new CommonFunctions(getActivity());
        user_id = preferenceUtils.get_user_id();
        initBroadCastReceiver();

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.QUESTION_CHANGED));

        pojoInterestDetailsResponse = new Gson().fromJson(getArguments().getString("params"), PojoInterestDetailsResponse.class);

        initViews(baseLayout);
        offset = 0;
        questionsList.clear();


        hitGetInterestDetailsApi(preferenceUtils.get_user_id(),
                pojoInterestDetailsResponse.data.interestData.interestId,
                "question", newRelevance, newMyRole, offset);
        return baseLayout;
    }


    private void initBroadCastReceiver() {
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // checking for type intent filter
                if (intent.getAction().equals(Config.QUESTION_CHANGED)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    offset = 0;
                    hitGetInterestDetailsApi(preferenceUtils.get_user_id(),
                            pojoInterestDetailsResponse.data.interestData.interestId,
                            "question", newRelevance, newMyRole, offset);
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
                offset = 0;
                hitGetInterestDetailsApi(preferenceUtils.get_user_id(),
                        pojoInterestDetailsResponse.data.interestData.interestId,
                        "question", newRelevance, newMyRole, offset);
            }
        });

        initQuestionList(baseLayout);
        ll_parent = (LinearLayout) baseLayout.findViewById(R.id.ll_parent);
        ll_parent.setVisibility(View.GONE);
        //  pb_loading_more_questions= (ProgressBar) baseLayout.findViewById(R.id.pb_loading_more_questions);
        pb_loading_content = (ProgressBar) baseLayout.findViewById(R.id.pb_loading_content);
        pb_loading_content.setVisibility(View.GONE);
        rl_comment_bar = (RelativeLayout) baseLayout.findViewById(R.id.rl_comment_bar);
        et_comment = (EditText) baseLayout.findViewById(R.id.et_comment);
        bt_send = (TextView) baseLayout.findViewById(R.id.bt_send);
        // tv_no_more_questions= (TextView) baseLayout.findViewById(R.id.tv_no_more_questions);
        tv_filter = (TextView) baseLayout.findViewById(R.id.tv_filter);
        tv_filter.setVisibility(View.GONE);
        tv_no_questions = (TextView) baseLayout.findViewById(R.id.tv_no_questions);
        tv_no_questions.setVisibility(View.GONE);

        tv_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFilterDilaog();
            }
        });
    }


    private void initQuestionList(View baseLayout) {
        questionsList = new ArrayList<>();
        rv_item_list = (RecyclerView) baseLayout.findViewById(R.id.rv_item_list);
        interestRelatedQuestionListAdapter = new InterestRelatedQuestionListAdapter(this, questionsList);
        questionListLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL,
                false);
        rv_item_list.setLayoutManager(questionListLayoutManager);
        rv_item_list.setAdapter(interestRelatedQuestionListAdapter);
        interestRelatedQuestionListAdapter.setMoreAvailable(true);

        rv_item_list.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                /*if(Math.abs(dy)>1){
                    tv_no_more_questions.setVisibility(View.GONE);
                }
*/
                if (dy > 5) {
                    ((InterestActivity) getActivity()).hideFilter();
                    rl_comment_bar.setVisibility(View.GONE);
                    InputMethodManager imm = (InputMethodManager)
                            getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(rv_item_list.getWindowToken(), 0);

                    if (!isQuestionListApiCalled && questionListLayoutManager
                            .findLastVisibleItemPosition() > questionsList.size() - 3) {
                        hitGetInterestDetailsApi(preferenceUtils.get_user_id(),
                                pojoInterestDetailsResponse.data.interestData.interestId,
                                "question", newRelevance, newMyRole, ++offset);
                    }
                }
                if (dy < -5) {
                    ((InterestActivity) getActivity()).showFilter();
                }
            }
        });
    }


    private void hitGetInterestDetailsApi(String user_id, String interest_id, String type, String relevance,
                                          String my_roles, final int offset) {
        Call<PojoInterestDetailsResponse> call = apiService.getInterestQuestionList(user_id, interest_id,
                type, relevance, my_roles, String.valueOf(offset));
        ll_no_connection_wrapper.setVisibility(View.GONE);

        if (!isQuestionListApiCalled) {
            isQuestionListApiCalled = true;
            interestRelatedQuestionListAdapter.setShowProgressBar(true);
            interestRelatedQuestionListAdapter.notifyDataSetChanged();
            call.enqueue(new Callback<PojoInterestDetailsResponse>() {
                @Override
                public void onResponse(Call<PojoInterestDetailsResponse> call, Response<PojoInterestDetailsResponse> response) {
                    String message = "";
                    if (response.body() != null && response.body().data != null) {
                        message = response.body().message;
                        if (offset == 0) {
                            questionsList.clear();
                       /*     if(response.body().data.questions.isEmpty()){
                                tv_no_questions.setVisibility(View.VISIBLE);
                            }
                            else {
                                tv_no_questions.setVisibility(View.GONE);
                            }*/
                        }
                        if (response.body().data.questions.isEmpty()) {
                            interestRelatedQuestionListAdapter.setMoreAvailable(false);
                        }
                        questionsList.addAll(response.body().data.questions);
                        interestRelatedQuestionListAdapter.notifyDataSetChanged();
                        ll_parent.setVisibility(View.VISIBLE);
                    }
                    isQuestionListApiCalled = false;
                    interestRelatedQuestionListAdapter.setShowProgressBar(false);

                }

                @Override
                public void onFailure(Call<PojoInterestDetailsResponse> call, Throwable t) {
                    // Log error here since request failed
                    commonFunctions.setToastMessage(getContext(), t.toString(), Toast.LENGTH_LONG,
                            AppKeys.TOAST_DEBUG);
                    isQuestionListApiCalled = false;
                    interestRelatedQuestionListAdapter.setShowProgressBar(false);
                    if(questionsList.isEmpty()){
                        ll_no_connection_wrapper.setVisibility(View.VISIBLE);
                    }
                }
            });
        }

    }

    public void openFilterDilaog() {
        filterDialog = new MyCustomThemeDialog(getActivity());
        filterDialog.setContentView(R.layout.interest_question_list_filter_dialog);
        filterDialog.getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        filterDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        final TextView tv_done, tv_cancel, tv_relevance, tv_my_roles;

        LinearLayout ll_interest_data_wrapper = (LinearLayout) filterDialog.findViewById(R.id.ll_interest_data_wrapper);
        ll_interest_data_wrapper.setVisibility(View.GONE);

        tv_done = (TextView) filterDialog.findViewById(R.id.tv_done);
        tv_cancel = (TextView) filterDialog.findViewById(R.id.tv_cancel);
        tv_relevance = (TextView) filterDialog.findViewById(R.id.tv_relevance);
        tv_my_roles = (TextView) filterDialog.findViewById(R.id.tv_my_roles);

        switch (oldRelevance) {
            case RELEVANCE_ANY:
                tv_relevance.setText(R.string.any);
                break;

            case RELEVANCE_MOST_UPVOTED:
                tv_relevance.setText(R.string.most_upvoted);
                break;
        }

        switch (oldMyRole) {
            case MY_ROLES_ANY:
                tv_my_roles.setText(R.string.any);
                break;

            case MY_ROLES_I_ASKED:
                tv_my_roles.setText(R.string.i_have_asked);
                break;

            case MY_ROLES_I_ANSWERED:
                tv_my_roles.setText(R.string.i_have_answered);
                break;

            case MY_ROLES_I_FOLLOWING:
                tv_my_roles.setText(R.string.i_am_following);
                break;
        }


        final PopupMenu.OnMenuItemClickListener onMenuItemClickListener = new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.relevance_any:
                        newRelevance = RELEVANCE_ANY;
                        tv_relevance.setText(R.string.any);
                        break;

                    case R.id.relevance_most_upvoted:
                        newRelevance = RELEVANCE_MOST_UPVOTED;
                        tv_relevance.setText(R.string.most_upvoted);

                        break;

                    case R.id.role_any:
                        newMyRole = MY_ROLES_ANY;
                        tv_my_roles.setText(R.string.any);
                        break;

                    case R.id.role_i_asked:
                        newMyRole = MY_ROLES_I_ASKED;
                        tv_my_roles.setText(R.string.i_have_asked);
                        break;

                    case R.id.role_i_answered:
                        newMyRole = MY_ROLES_I_ANSWERED;
                        tv_my_roles.setText(R.string.i_have_answered);
                        break;

                    case R.id.role_i_following:
                        newMyRole = MY_ROLES_I_FOLLOWING;
                        tv_my_roles.setText(R.string.i_am_following);
                        break;
                }
                return false;
            }
        };


        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.tv_done:
                        oldMyRole = newMyRole;
                        oldRelevance = newRelevance;
                        offset = 0;
                        questionsList.clear();
                        interestRelatedQuestionListAdapter.setShowProgressBar(true);
                        interestRelatedQuestionListAdapter.setMoreAvailable(true);
                        hitGetInterestDetailsApi(preferenceUtils.get_user_id(),
                                pojoInterestDetailsResponse.data.interestData.interestId,
                                "question", newRelevance, newMyRole, offset);
                        filterDialog.dismiss();
                        break;

                    case R.id.tv_cancel:
                        newMyRole = oldMyRole;
                        newRelevance = oldRelevance;
                        filterDialog.dismiss();
                        break;

                    case R.id.tv_relevance:
                        PopupMenu relevanceMenu = new PopupMenu(getActivity(), tv_relevance);
                        MenuInflater inflater = relevanceMenu.getMenuInflater();
                        inflater.inflate(R.menu.interest_question_filter_relevance_menu, relevanceMenu.getMenu());
                        relevanceMenu.setOnMenuItemClickListener(onMenuItemClickListener);
                        relevanceMenu.show();
                        break;

                    case R.id.tv_my_roles:
                        PopupMenu roleMenu = new PopupMenu(getActivity(), tv_my_roles);
                        MenuInflater rolesInflater = roleMenu.getMenuInflater();
                        rolesInflater.inflate(R.menu.interest_question_filter_role_menu, roleMenu.getMenu());
                        roleMenu.setOnMenuItemClickListener(onMenuItemClickListener);
                        roleMenu.show();
                        break;
                }
            }
        };

        tv_done.setOnClickListener(onClickListener);
        tv_cancel.setOnClickListener(onClickListener);
        tv_relevance.setOnClickListener(onClickListener);
        tv_my_roles.setOnClickListener(onClickListener);
        filterDialog.show();
    }


    private void openAddAnswerDialog(final PojoInterestQuestions currentItem) {
        bt_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!et_comment.getText().toString().trim().isEmpty()) {
                    hitAddAnswerApi(user_id, "add_qa_answer", currentItem.sessionsQaId, currentItem.sessionsId, et_comment.getText().toString());
                    et_comment.getText().clear();
                }

            }
        });
        rl_comment_bar.setVisibility(View.VISIBLE);
        if (et_comment.requestFocus()) {
            InputMethodManager imm = (InputMethodManager)
                    getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(et_comment, InputMethodManager.SHOW_FORCED);
        }
    }

    private void hitAddAnswerApi(String user_id, String type, final String question_id, String session_id, String comment_post) {
        Call<PojoAddSessionDiscussionResponse> call = apiService.sessionConnects(user_id, type, session_id, "", comment_post, "", question_id);
        call.enqueue(new Callback<PojoAddSessionDiscussionResponse>() {
            @Override
            public void onResponse(Call<PojoAddSessionDiscussionResponse> call,
                                   Response<PojoAddSessionDiscussionResponse> response) {
                if (response != null && response.body() != null) {
                    String message = response.body().message;
                    commonFunctions.setToastMessage(getContext(), "" + message, Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                    Intent questionIntent = new Intent(getContext(), QuestionActivity.class);
                    questionIntent.putExtra("id", question_id);
                    questionIntent.putExtra("type", "answer_added");
                    startActivity(questionIntent);
                }
            }

            @Override
            public void onFailure(Call<PojoAddSessionDiscussionResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(getContext(), t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
            }
        });
    }

    @Override
    public void interestRelatedQuestionsMethodClick(int position, PojoInterestQuestions currentItem,
                                                    String type) {
        switch (type) {
            case AppKeys.ADD_ANSWER:
                if (currentItem.sessionsQaId != null && !currentItem.sessionsQaId.isEmpty()) {
                    Intent questionIntent = new Intent(getActivity(), QuestionActivity.class);
                    questionIntent.putExtra(AppKeys.ID, currentItem.sessionsQaId);
                    questionIntent.putExtra(AppKeys.TARGET, AppKeys.ADD_ANSWER);
                    getActivity().startActivity(questionIntent);
                }
                break;
            case AppKeys.SHARE_QUESTION:
                openShareQuestionOnProfileDialog(currentItem);
                break;
            case AppKeys.LIKE:
                if (questionsList.get(position).iLike) {
                    hitUpvoteQuestionApi(user_id, "unlike_qacomment", currentItem.sessionsQaId, currentItem.sessionsId);
                } else {
                    hitUpvoteQuestionApi(user_id, "like_qacomment", currentItem.sessionsQaId, currentItem.sessionsId);
                }
                questionsList.get(position).iLike = !questionsList.get(position).iLike;
                interestRelatedQuestionListAdapter.notifyDataSetChanged();
                break;
        }


        //  openAddAnswerDialog(currentItem);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mRegistrationBroadcastReceiver);
    }

    private void openShareQuestionOnProfileDialog(final PojoInterestQuestions currentItem) {
        TextView tv_username;
        ImageView iv_user_dp;
        final EditText et_share_title;
        Button tv_share_button;
        final Dialog shareDialog;
        RelativeLayout rl_item_holder;
        shareDialog = new MyCustomThemeDialog(getActivity());
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
        if (getActivity() != null) Glide.with(getActivity().getApplicationContext())
                .load(pojoLoginResponseData.userPicture)
                .apply(new RequestOptions().override(128, 128))
                .apply(bitmapTransform(new CropCircleTransformation()))
                .into(iv_user_dp);

        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.share_question_item, rl_item_holder, true);

        TextView tv_question = (TextView) view.findViewById(R.id.tv_question);
        TextView tv_question_time_stamp = (TextView) view.findViewById(R.id.tv_question_time_stamp);

        tv_question.setText(Html.fromHtml(currentItem.post));
        tv_question_time_stamp.setText(commonFunctions.parseNewsFeedItemTime(currentItem.addedOn));

        tv_share_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hitShareContentApi(user_id, currentItem.sessionsQaId, "share_question",
                        et_share_title.getText().toString().trim());
                shareDialog.dismiss();
            }
        });
        shareDialog.show();

    }


    private void hitUpvoteQuestionApi(String user_id, String type, final String question_id, String session_id) {
        Call<PojoAddSessionDiscussionResponse> call = apiService.sessionConnects(user_id, type, session_id, "", "", "", question_id);
        call.enqueue(new Callback<PojoAddSessionDiscussionResponse>() {
            @Override
            public void onResponse(Call<PojoAddSessionDiscussionResponse> call,
                                   Response<PojoAddSessionDiscussionResponse> response) {
                if (response != null && response.body() != null) {
                    String message = response.body().message;
                    if(getActivity()!=null)commonFunctions.setToastMessage(getActivity().getApplicationContext(), "" + message, Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                    // hitGetQuestionDetailsApi(preferenceUtils.get_user_id(), question_id, "question");
                }
            }

            @Override
            public void onFailure(Call<PojoAddSessionDiscussionResponse> call, Throwable t) {
                // Log error here since request failed
                if(getActivity()!=null)commonFunctions.setToastMessage(getActivity().getApplicationContext(),
                        t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
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
                    message = response.body().message;
                    if(getActivity()!=null)commonFunctions.setToastMessage(getActivity().getApplicationContext(), message, Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                }
            }

            @Override
            public void onFailure(Call<PojoNoDataResponse> call, Throwable t) {
                // Log error here since request failed
                if(getActivity()!=null)commonFunctions.setToastMessage(getActivity().getApplicationContext(), t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);

            }
        });

    }


}
