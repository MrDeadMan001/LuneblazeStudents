package com.avadna.luneblaze.fragments.sessions;

import android.app.Dialog;

import com.avadna.luneblaze.helperClasses.MyCustomThemeDialog;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Point;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.avadna.luneblaze.pojo.pojoSessionDetails.PojoAddSessionDiscussionResponse;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.activities.sessions.OrganisedSessionActivity;
import com.avadna.luneblaze.adapters.InterestSearchResultAdapter;
import com.avadna.luneblaze.adapters.TaggedInterestAdapter;
import com.avadna.luneblaze.adapters.session.SessionQuestionListAdapter;
import com.avadna.luneblaze.firebasenotifications.config.Config;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.pojo.PojoGetInterestListResponseDataListItem;
import com.avadna.luneblaze.pojo.PojoLoginResponseData;
import com.avadna.luneblaze.pojo.PojoNoDataResponse;
import com.avadna.luneblaze.pojo.PojoSearchInterestWithTextResponse;
import com.avadna.luneblaze.pojo.PojoSearchInterestWithTextResponseData;
import com.avadna.luneblaze.pojo.PojoUserConnectResponse;
import com.avadna.luneblaze.pojo.pojoQuestion.PojoUserData;
import com.avadna.luneblaze.pojo.pojoSessionDetails.PojoSessionDetailsResponse;
import com.avadna.luneblaze.pojo.pojoSessionDetails.PojoSessionQuestions;
import com.avadna.luneblaze.rest.ApiClientLongDuration;
import com.avadna.luneblaze.rest.ApiInterface;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;


public class OrganisedSessionQuestionListFragment extends Fragment implements
        InterestSearchResultAdapter.InterestResultAdapterCallback,
        TaggedInterestAdapter.TaggedInterestAdapterCallback,
        SessionQuestionListAdapter.SessionQuestionListAdapterCallback {

    private LinearLayout ll_no_connection_wrapper;
    private TextView tv_retry;

    private LinearLayout ll_parent;
    private ProgressBar pb_loading_content;

    private RecyclerView rv_item_list;
    private SessionQuestionListAdapter sessionQuestionListAdapter;
    private LinearLayoutManager linearLayoutManager;
    private ApiInterface apiService;
    private PreferenceUtils preferenceUtils;
    private CommonFunctions commonFunctions;
    private String user_id;
    private String username;
    private PojoLoginResponseData pojoLoginResponseData;
    private String session_id;

    private List<PojoSessionQuestions> questionsList;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private String type;

    private TextView tv_filter;
    private Dialog filterDialog;

    private BottomSheetDialog addQuestionDialog;
    private DialogInterface.OnDismissListener onDismissListener;

    private TextView tv_done, tv_cancel, tv_relevance, tv_my_roles, tv_interest_option;

    private static final String RELEVANCE_ANY = "any";
    private static final String RELEVANCE_MOST_UPVOTED = "most_upvoted";

    private String newRelevance = RELEVANCE_ANY;
    private String oldRelevance = RELEVANCE_ANY;

    private static final String MY_ROLES_ANY = "any";
    private static final String MY_ROLES_I_ASKED = "I have asked";
    private static final String MY_ROLES_I_ANSWERED = "I have answered";
    private static final String MY_ROLES_I_FOLLOWING = "I am following";

    private String newMyRole = MY_ROLES_ANY;
    private String oldMyRole = MY_ROLES_ANY;


    private static final String INTEREST_OPTION_ANY = "Any";
    private static final String INTEREST_OPTION_MY_INTEREST = "my_interest";
    private static final String INTEREST_OPTION_SEARCH = "search_interest";
    private String newInterestOption = INTEREST_OPTION_ANY;
    private String oldInterestOption = INTEREST_OPTION_ANY;


    private String newIsMyInterestSelected = "0";
    private String oldIsMyInterestSelected = "0";

    private Dialog interestSearchDialog;
    private RecyclerView rv_interest_search_result;
    private InterestSearchResultAdapter interestSearchResultAdapter;
    private List<PojoSearchInterestWithTextResponseData> interestSearchResultList = new ArrayList<>();
    private EditText et_interest_name;

    private int offset = 0;

    private int[] location = new int[2];
    private RecyclerView rv_added_interests_in_filter, rv_added_interests_in_search;
    private TextView tv_no_questions;
    private TaggedInterestAdapter taggedInterestAdapter_in_filter, taggedInterestAdapter_in_search;
    private TextView tv_length_getter;
    private DisplayMetrics displayMetrics;
    private List<PojoGetInterestListResponseDataListItem> selectedInterestList = new ArrayList<>();
    private List<String> selectedInterestIdList = new ArrayList<>();

    private int screenHeight, screenWidth;
    private boolean isGetQuestionApiCalled = false;


    public OrganisedSessionQuestionListFragment() {
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
        apiService = ApiClientLongDuration.getClient().create(ApiInterface.class);
        preferenceUtils = new PreferenceUtils(getActivity());
        commonFunctions = new CommonFunctions(getActivity());
        user_id = preferenceUtils.get_user_id();
        username = preferenceUtils.get_user_name();
        pojoLoginResponseData = preferenceUtils.getUserLoginData();
        initDispMetrics();
        initBroadCastReceiver();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.QUESTION_CHANGED));

        initViews(baseLayout);
        initDismissListener();
        session_id = getArguments().getString("id");
        if (session_id != null) {
            offset = 0;
            hitGetSessionDetailsApi(preferenceUtils.get_user_id(), session_id, offset, "queans",
                    oldRelevance, oldMyRole, selectedInterestIdList, oldIsMyInterestSelected, false);
        }
        return baseLayout;
    }

    private void initDismissListener() {
        onDismissListener = new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                InputMethodManager imm = (InputMethodManager)
                        getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                View view = getActivity().getCurrentFocus();
                imm.hideSoftInputFromWindow(getActivity().getWindow().getDecorView()
                        .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        };
    }

    private void initDispMetrics() {
        displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenHeight = displayMetrics.heightPixels;
        screenWidth = displayMetrics.widthPixels;
    }

    private void initBroadCastReceiver() {
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Config.QUESTION_CHANGED)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    hitGetSessionDetailsApi(preferenceUtils.get_user_id(), session_id, offset,
                            "queans", oldRelevance, oldMyRole, selectedInterestIdList,
                            oldIsMyInterestSelected, false);
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
                hitGetSessionDetailsApi(preferenceUtils.get_user_id(), session_id, offset,
                        "queans", oldRelevance, oldMyRole, selectedInterestIdList,
                        oldIsMyInterestSelected, false);
            }
        });

        initQuestionList(baseLayout);
        ll_parent = (LinearLayout) baseLayout.findViewById(R.id.ll_parent);
        pb_loading_content = (ProgressBar) baseLayout.findViewById(R.id.pb_loading_content);
        pb_loading_content.setVisibility(View.GONE);
        tv_no_questions = (TextView) baseLayout.findViewById(R.id.tv_no_questions);
        tv_no_questions.setVisibility(View.GONE);

        tv_filter = (TextView) baseLayout.findViewById(R.id.tv_filter);
        tv_filter.setVisibility(View.GONE);
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
        linearLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        sessionQuestionListAdapter = new SessionQuestionListAdapter(this, questionsList);
        rv_item_list.setLayoutManager(linearLayoutManager);
        rv_item_list.setAdapter(sessionQuestionListAdapter);

        rv_item_list.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 4) {
                    if (!isGetQuestionApiCalled && linearLayoutManager.findLastVisibleItemPosition() > questionsList.size() - 3) {
                        hitGetSessionDetailsApi(preferenceUtils.get_user_id(), session_id, ++offset, "queans",
                                oldRelevance, oldMyRole, selectedInterestIdList, oldIsMyInterestSelected, false);
                    }
                    ((OrganisedSessionActivity) getActivity()).hideAddCommentFab();
                }
                if (dy < -4) {
                    ((OrganisedSessionActivity) getActivity()).showAddCommentFab();
                }
            }
        });
    }

    private void hitGetSessionDetailsApi(String user_id, String session_id, final int offset, String view,
                                         String relevance, String role, List<String> interests,
                                         String myInterests, final boolean scrollToEnd) {

        ll_no_connection_wrapper.setVisibility(View.GONE);
        HashMap<String, String> interestMap = new HashMap<>();
        for (int i = 0; i < interests.size(); i++) {
            interestMap.put("interest_search[" + i + "]", interests.get(i));
        }

        if (!isGetQuestionApiCalled) {
            //tv_no_questions.setVisibility(View.GONE);

            isGetQuestionApiCalled = true;
            sessionQuestionListAdapter.setShowProgressBar(true);
            Call<PojoSessionDetailsResponse> call = apiService.getSessionQuestions(user_id, session_id,
                    String.valueOf(offset), view, relevance, role, interestMap, myInterests);
            call.enqueue(new Callback<PojoSessionDetailsResponse>() {
                @Override
                public void onResponse(Call<PojoSessionDetailsResponse> call, Response<PojoSessionDetailsResponse> response) {
                    tv_filter.setVisibility(View.VISIBLE);

                    String message = "";
                    if (response != null && response.body() != null && response.body().data != null) {
                        message = response.body().message;
                        if (response.body().data.questions != null) {
                            if (offset == 0) {
                                questionsList.clear();
                                if (removedReportedQuestions(response.body().data.questions).isEmpty()) {
                                    tv_no_questions.setVisibility(View.VISIBLE);
                                    tv_filter.setVisibility(View.GONE);
                                }
                            }
                            questionsList.addAll(removedReportedQuestions(response.body().data.questions));
                            sessionQuestionListAdapter.notifyDataSetChanged();
                            if (scrollToEnd) {
                                rv_item_list.scrollToPosition(0);
                            }
                            if (response.body().data.questions.size() < 15) {
                                sessionQuestionListAdapter.setMoreAvailable(false);
                            }
                        }

                        //   if(getActivity()!=null)commonFunctions.setToastMessage(getActivity().getApplicationContext(), message, Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);

                    }
                    ll_parent.setVisibility(View.VISIBLE);
                    isGetQuestionApiCalled = false;
                    sessionQuestionListAdapter.setShowProgressBar(false);
                }

                @Override
                public void onFailure(Call<PojoSessionDetailsResponse> call, Throwable t) {
                    // Log error here since request failed
                    if (getActivity() != null)
                        commonFunctions.setToastMessage(getActivity().getApplicationContext(), t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                    //  ll_parent.setVisibility(View.VISIBLE);
                    isGetQuestionApiCalled = false;
                    sessionQuestionListAdapter.setShowProgressBar(false);
                    pb_loading_content.setVisibility(View.GONE);
                    if (questionsList.isEmpty()) {
                        ll_no_connection_wrapper.setVisibility(View.VISIBLE);
                    }

                }
            });
        }

    }

    private List<PojoSessionQuestions> removedReportedQuestions(List<PojoSessionQuestions> questions) {
        List<PojoSessionQuestions> moddedQuestions = new ArrayList<>();
        for (int i = 0; i < questions.size(); i++) {
            if (questions.get(i).iReported == 0) {
                moddedQuestions.add(questions.get(i));
            }
        }
        return moddedQuestions;
    }

    private void openFilterDilaog() {
        filterDialog = new MyCustomThemeDialog(getActivity());
        filterDialog.setContentView(R.layout.interest_question_list_filter_dialog);
        filterDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        filterDialog.getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);

        tv_done = (TextView) filterDialog.findViewById(R.id.tv_done);
        tv_cancel = (TextView) filterDialog.findViewById(R.id.tv_cancel);
        tv_relevance = (TextView) filterDialog.findViewById(R.id.tv_relevance);
        tv_my_roles = (TextView) filterDialog.findViewById(R.id.tv_my_roles);
        tv_interest_option = (TextView) filterDialog.findViewById(R.id.tv_interest_option);

        tv_length_getter = (TextView) filterDialog.findViewById(R.id.tv_length_getter);


        tv_length_getter = (TextView) filterDialog.findViewById(R.id.tv_length_getter);
        rv_added_interests_in_filter = (RecyclerView) filterDialog.findViewById(R.id.rv_added_interests);
        updateSelectedInterestListFilter();


        if (oldIsMyInterestSelected.equals("1")) {
            tv_interest_option.setText(R.string.my_interests);
        } else {
            switch (oldInterestOption) {
                case INTEREST_OPTION_ANY:
                    tv_interest_option.setText(R.string.any);
                    break;

                case INTEREST_OPTION_SEARCH:
                    tv_interest_option.setText(R.string.choose_interest);
                    break;

            }
        }

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

                    case R.id.interest_any:
                        newInterestOption = INTEREST_OPTION_ANY;
                        newIsMyInterestSelected = "0";
                        tv_interest_option.setText(getString(R.string.any));
                        rv_added_interests_in_filter.setVisibility(View.GONE);
                        break;

                    case R.id.my_interest:
                        newInterestOption = INTEREST_OPTION_MY_INTEREST;
                        newIsMyInterestSelected = "1";
                        tv_interest_option.setText(getString(R.string.my_interests));
                        rv_added_interests_in_filter.setVisibility(View.GONE);
                        break;

                    case R.id.choose_interest:
                        newInterestOption = INTEREST_OPTION_SEARCH;
                        newIsMyInterestSelected = "0";
                        tv_interest_option.setText(getString(R.string.choose_interest));
                        openInterestSearchDialog();
                        rv_added_interests_in_filter.setVisibility(View.VISIBLE);
                        break;

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
                        oldIsMyInterestSelected = newIsMyInterestSelected;
                        oldInterestOption = newInterestOption;
                        offset = 0;
                        questionsList.clear();
                        sessionQuestionListAdapter.notifyDataSetChanged();
                        tv_no_questions.setVisibility(View.GONE);
                        hitGetSessionDetailsApi(preferenceUtils.get_user_id(), session_id, offset, "queans",
                                oldRelevance, oldMyRole, selectedInterestIdList,
                                oldIsMyInterestSelected, false);
                        filterDialog.dismiss();
                        break;

                    case R.id.tv_cancel:
                        newMyRole = oldMyRole;
                        newRelevance = oldRelevance;
                        newInterestOption = oldInterestOption;
                        newIsMyInterestSelected = oldIsMyInterestSelected;

                        filterDialog.dismiss();
                        break;

                    case R.id.tv_interest_option:
                        PopupMenu interestOptionMenu = new PopupMenu(getActivity(), tv_interest_option);
                        MenuInflater interestOptionMenuInflater = interestOptionMenu.getMenuInflater();
                        interestOptionMenuInflater.inflate(R.menu.interest_session_filter_interest_option_menu,
                                interestOptionMenu.getMenu());
                        interestOptionMenu.setOnMenuItemClickListener(onMenuItemClickListener);
                        interestOptionMenu.show();
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
        tv_interest_option.setOnClickListener(onClickListener);

        filterDialog.show();
    }


    public void openInterestSearchDialog() {

        interestSearchDialog = new MyCustomThemeDialog(getActivity());
        interestSearchDialog.setContentView(R.layout.interest_search_dialog);
        interestSearchDialog.getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        interestSearchDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        rv_interest_search_result = (RecyclerView) interestSearchDialog.findViewById(R.id.rv_interest_search_result);
        rv_interest_search_result.setLayoutManager
                (new LinearLayoutManager(getActivity(),
                        RecyclerView.VERTICAL, false));
        rv_interest_search_result.setNestedScrollingEnabled(false);
        interestSearchResultAdapter = new InterestSearchResultAdapter(this, interestSearchResultList);
        rv_interest_search_result.setAdapter(interestSearchResultAdapter);

        tv_length_getter = interestSearchDialog.findViewById(R.id.tv_length_getter);
        rv_added_interests_in_search = (RecyclerView) interestSearchDialog.findViewById(R.id.rv_added_interests);
        updateSelectedInterestListSearch();


        et_interest_name = (EditText) interestSearchDialog.findViewById(R.id.et_interest_name);
        et_interest_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (et_interest_name.getText().toString().trim().isEmpty()) {
                    interestSearchResultList.clear();
                    interestSearchResultAdapter.notifyDataSetChanged();
                } else {
                    hitInterestSearchWithTextApi(et_interest_name.getText().toString().trim());
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        TextView tv_done_button= (TextView) interestSearchDialog.findViewById(R.id.tv_done_button);
        tv_done_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                interestSearchDialog.dismiss();
            }
        });

        interestSearchDialog.show();

    }

    private void updateSelectedInterestListSearch() {
        GridLayoutManager taggedInterestGridLayoutManager;
        taggedInterestGridLayoutManager = (new GridLayoutManager(getActivity(), 200));
        taggedInterestGridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                Display display;

                display = interestSearchDialog.getWindow().getWindowManager().getDefaultDisplay();

                Point size = new Point();
                display.getSize(size);
                String text;
                if (!selectedInterestList.isEmpty()) {
                    text = selectedInterestList.get(position).text;
                    tv_length_getter.setText(text);
                }
                tv_length_getter.measure(size.x, size.y);
                int textWidth = tv_length_getter.getMeasuredWidth();

                float adjustMarginPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, displayMetrics);

                float spanCount = (200 * (textWidth + adjustMarginPx)) / (0.8f * screenWidth);

                if (spanCount > 200) {
                    spanCount = 200;
                }
                return (int) spanCount;
            }
        });
        rv_added_interests_in_search.setLayoutManager(taggedInterestGridLayoutManager);
        taggedInterestAdapter_in_search = new TaggedInterestAdapter(selectedInterestList.size(), this, selectedInterestList, 1);
        rv_added_interests_in_search.setAdapter(taggedInterestAdapter_in_search);
    }


    private void updateSelectedInterestListFilter() {
        GridLayoutManager taggedInterestGridLayoutManager;
        taggedInterestGridLayoutManager = (new GridLayoutManager(getActivity(), 200));
        taggedInterestGridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                Display display;
                display = filterDialog.getWindow().getWindowManager().getDefaultDisplay();

                Point size = new Point();
                display.getSize(size);
                String text;
                if (!selectedInterestList.isEmpty()) {
                    text = selectedInterestList.get(position).text;
                    tv_length_getter.setText(text);
                }
                tv_length_getter.measure(size.x, size.y);
                int textWidth = tv_length_getter.getMeasuredWidth();

                float adjustMarginPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, displayMetrics);

                float spanCount = (200 * (textWidth + adjustMarginPx)) / (0.8f * screenWidth);

                if (spanCount > 200) {
                    spanCount = 200;
                }
                return (int) spanCount;
            }
        });

        rv_added_interests_in_filter.setLayoutManager(taggedInterestGridLayoutManager);
        taggedInterestAdapter_in_filter = new TaggedInterestAdapter(selectedInterestList.size(), this, selectedInterestList, 1);
        rv_added_interests_in_filter.setAdapter(taggedInterestAdapter_in_filter);
    }

    private void hitInterestSearchWithTextApi(String text) {
        Call<PojoSearchInterestWithTextResponse> call = apiService.searchInterestWithTextApi(text,
                String.valueOf(offset));
        pb_loading_content.setVisibility(View.VISIBLE);
        call.enqueue(new Callback<PojoSearchInterestWithTextResponse>() {
            @Override
            public void onResponse(Call<PojoSearchInterestWithTextResponse> call, Response<PojoSearchInterestWithTextResponse> response) {

                String message = response.body().message;
                if (response.body().data != null) {
                    interestSearchResultList.clear();
                    for (int i = 0; i < response.body().data.size() && i < 4; i++) {
                        interestSearchResultList.add(response.body().data.get(i));
                    }
                    // interestSearchResultList.addAll(response.body().data);
                    interestSearchResultAdapter.notifyDataSetChanged();
                } else {
                    if (getActivity() != null)
                        commonFunctions.setToastMessage(getActivity().getApplicationContext(), "" + message, Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                }
                pb_loading_content.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<PojoSearchInterestWithTextResponse> call, Throwable t) {
                // Log error here since request failed
                if (getActivity() != null)
                    commonFunctions.setToastMessage(getActivity().getApplicationContext(), t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                pb_loading_content.setVisibility(View.GONE);
            }
        });

    }

    public void openAddQuestionDialog(String question_post) {
        addQuestionDialog = new BottomSheetDialog(getActivity(), R.style.TransparentDialogBG);
        // dialog.setCanceledOnTouchOutside(false);
        addQuestionDialog.setContentView(R.layout.add_comment_dialog);
        addQuestionDialog.getWindow().setDimAmount(0);
        addQuestionDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        addQuestionDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        final EditText et_content = (EditText) addQuestionDialog.findViewById(R.id.et_content);
        if (!question_post.isEmpty()) {
            et_content.setText(question_post);
        }
        ImageButton ib_send = (ImageButton) addQuestionDialog.findViewById(R.id.ib_send);
        ib_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et_content.getText().toString().isEmpty()) {
                    if (getActivity() != null)
                        commonFunctions.setToastMessage(getActivity().getApplicationContext(), getString(R.string.please_enter_data),
                                Toast.LENGTH_LONG, AppKeys.TOAST_USER);
                } else {
                    tv_no_questions.setVisibility(View.GONE);

                    hitAddQuestionApi(user_id, "add_qa_question", session_id, et_content.getText().toString().trim());
                    PojoSessionQuestions tempQuestion = new PojoSessionQuestions();
                    tempQuestion.post = et_content.getText().toString().trim();
                    tempQuestion.userData = new PojoUserData();
                    tempQuestion.userData.userId = user_id;
                    tempQuestion.userData.userFullname = username;
                    tempQuestion.userData.userPicture = pojoLoginResponseData.userPicture;
                    tempQuestion.answers = new ArrayList<>();
                    tempQuestion.totalAns = 0;
                    tempQuestion.totalLikes = "0";
                    tempQuestion.sessionsQaId = "";
                    tempQuestion.sessionsId = session_id;
                    tempQuestion.addedOn = getString(R.string.just_now);
                    tempQuestion.iFollow = false;
                    tempQuestion.iLike = false;
                    questionsList.add(0, tempQuestion);

                    sessionQuestionListAdapter.notifyDataSetChanged();
                    ((OrganisedSessionActivity) getActivity()).collapseAppBar(false);
                    et_content.getText().clear();
                    InputMethodManager imm = (InputMethodManager)
                            getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(et_content.getWindowToken(), 0);
                    rv_item_list.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            rv_item_list.scrollToPosition(0);
                        }
                    }, 100);

                    addQuestionDialog.dismiss();

                }
            }
        });
        addQuestionDialog.show();

        if (et_content != null && et_content.requestFocus()) {
            InputMethodManager imm = (InputMethodManager)
                    getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(et_content, InputMethodManager.SHOW_FORCED);
        }

        addQuestionDialog.setOnDismissListener(onDismissListener);
        // if(getActivity()!=null)commonFunctions.setToastMessage(getActivity().getApplicationContext(), R.string.please_enter_data, Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);

    }


    private void hitAddQuestionApi(String user_id, String type, final String session_id,
                                   final String question_post) {
        Call<PojoAddSessionDiscussionResponse> call = apiService.sessionConnects(user_id, type,
                session_id, "", "", question_post, "");
        call.enqueue(new Callback<PojoAddSessionDiscussionResponse>() {
            @Override
            public void onResponse(Call<PojoAddSessionDiscussionResponse> call,
                                   Response<PojoAddSessionDiscussionResponse> response) {
                if (response != null && response.body() != null) {
                    for (int i = 0; i <questionsList.size(); i++) {
                        if (questionsList.get(i).sessionsQaId.isEmpty()) {
                            questionsList.get(i).modifiedOn = response.body().data.time;
                            questionsList.get(i).sessionsQaId = String.valueOf(response.body().data.id);
                            sessionQuestionListAdapter.notifyDataSetChanged();
                            break;
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<PojoAddSessionDiscussionResponse> call, Throwable t) {
                // Log error here since request failed
                if (getActivity() != null)
                    commonFunctions.setToastMessage(getActivity().getApplicationContext(),
                            getString(R.string.question_not_added),
                            Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                openAddQuestionDialog(question_post);
                for (int i = questionsList.size() - 1; i >= 0; i--) {
                    if (questionsList.get(i).sessionsQaId.isEmpty()) {
                        questionsList.remove(i);
                    }
                }
                sessionQuestionListAdapter.notifyDataSetChanged();
            }
        });
    }


    @Override
    public void interestResultItemClickCallback(int position, PojoSearchInterestWithTextResponseData data) {
        PojoGetInterestListResponseDataListItem pojoGetInterestListResponseDataListItem
                = new PojoGetInterestListResponseDataListItem(data.interestId, data.parentId, data.text, 0);
        for (int i = 0; i < selectedInterestList.size(); i++) {
            //if item already present in list then return
            if (selectedInterestList.get(i).interestId
                    .equals(data.interestId)) {
                return;
            }
        }

        //add 10 interests at max
        if (selectedInterestList.size() < 10) {
            selectedInterestList.add(pojoGetInterestListResponseDataListItem);
            selectedInterestIdList.clear();
            for (int i = 0; i < selectedInterestList.size(); i++) {
                selectedInterestIdList.add(selectedInterestList.get(i).interestId);
            }
            updateSelectedInterestListFilter();
            updateSelectedInterestListSearch();
        }
    }

    @Override
    public void taggedInterestItemClickCallback(int position, PojoGetInterestListResponseDataListItem pojoGetInterestListResponseDataListItem) {
        selectedInterestList.remove(position);
       /* selectedInterestIdList.clear();
        for(int i=0;i<selectedInterestList.size();i++){
            selectedInterestIdList.add(selectedInterestList.get(i).interestId);
        }*/
        updateSelectedInterestListFilter();
        updateSelectedInterestListSearch();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mRegistrationBroadcastReceiver);

    }

    @Override
    public void SessionQuestionListItemClickCallback(int position, PojoSessionQuestions question, String type) {
        switch (type) {
            case AppKeys.LIKE:
                int numLikes = Integer.parseInt(questionsList.get(position).totalLikes);

                if (questionsList.get(position).iLike) {
                    hitUpvoteQuestionApi(user_id, "unlike_qacomment", questionsList.get(position).sessionsQaId, questionsList.get(position).sessionsId);
                    numLikes--;
                } else {
                    hitUpvoteQuestionApi(user_id, "like_qacomment", questionsList.get(position).sessionsQaId, questionsList.get(position).sessionsId);
                    numLikes++;
                }
                questionsList.get(position).totalLikes = "" + numLikes;
                questionsList.get(position).iLike = !questionsList.get(position).iLike;
                sessionQuestionListAdapter.notifyDataSetChanged();

                break;

            case AppKeys.QUESTION_FOLLOW:
                if (questionsList.get(position).iFollow) {
                    hitQuestionNotifyApi(user_id, questionsList.get(position).sessionsQaId, "remove_question_notify");
                } else {
                    hitQuestionNotifyApi(user_id, questionsList.get(position).sessionsQaId, "question_notify");
                }
                questionsList.get(position).iFollow = !questionsList.get(position).iFollow;
                sessionQuestionListAdapter.notifyDataSetChanged();

                break;

            case AppKeys.QUESTION_SHARE:
                openShareQuestionOnProfileDialog(position);
                break;

        }
    }

    private void hitUpvoteQuestionApi(String user_id, String type, final String question_id, String session_id) {
        Call<PojoAddSessionDiscussionResponse> call = apiService.sessionConnects(user_id, type, session_id, "", "", "", question_id);
        call.enqueue(new Callback<PojoAddSessionDiscussionResponse>() {
            @Override
            public void onResponse(Call<PojoAddSessionDiscussionResponse> call,
                                   Response<PojoAddSessionDiscussionResponse> response) {
                if (response != null && response.body() != null) {
                    String message = response.body().message;
                    commonFunctions.setToastMessage(getContext(), "" + message,
                            Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                }
            }

            @Override
            public void onFailure(Call<PojoAddSessionDiscussionResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(getContext(), t.toString(),
                        Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
            }
        });
    }

    private void hitQuestionNotifyApi(String user_id, String id, final String reaction) {
        Call<PojoUserConnectResponse> call = apiService.changeUserConnectionApi(user_id, id, reaction);
        call.enqueue(new Callback<PojoUserConnectResponse>() {
            @Override
            public void onResponse(Call<PojoUserConnectResponse> call, Response<PojoUserConnectResponse> response) {
                String message = "";
                if (response != null) {
                    message = response.body().message;
                    commonFunctions.setToastMessage(getContext(), message, Toast.LENGTH_LONG,
                            AppKeys.TOAST_DEBUG);
                }
            }

            @Override
            public void onFailure(Call<PojoUserConnectResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(getContext(), getString(R.string.answer_not_added),
                        Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);

            }
        });
    }

    private void openShareQuestionOnProfileDialog(final int position) {
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

        tv_question.setText(questionsList.get(position).post);

        tv_question_time_stamp.setText(commonFunctions.parseNewsFeedItemTime(questionsList.get(position).addedOn));

        tv_share_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hitShareContentApi(user_id, questionsList.get(position).sessionsQaId, "share_question",
                        et_share_title.getText().toString().trim());
                shareDialog.dismiss();
            }
        });
        shareDialog.show();

    }

    private void hitShareContentApi(String user_id, String article_id, String reaction, String title) {
        Call<PojoNoDataResponse> call = apiService.shareContent(user_id, article_id, reaction, title);
        call.enqueue(new Callback<PojoNoDataResponse>() {
            @Override
            public void onResponse(Call<PojoNoDataResponse> call, Response<PojoNoDataResponse> response) {
                String message = "";
                if (response != null) {
                    message = response.body().message;
                    commonFunctions.setToastMessage(getContext(), message, Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);

                }
            }

            @Override
            public void onFailure(Call<PojoNoDataResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(getContext(), t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);

            }
        });

    }
}