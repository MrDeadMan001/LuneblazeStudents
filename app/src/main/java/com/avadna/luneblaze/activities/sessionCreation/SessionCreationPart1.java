package com.avadna.luneblaze.activities.sessionCreation;

import android.app.Dialog;

import com.avadna.luneblaze.activities.LoginActivity;
import com.avadna.luneblaze.helperClasses.MyCustomThemeDialog;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;

import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.core.widget.NestedScrollView;
import androidx.appcompat.app.ActionBar;

import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.adapters.session.organised.OrganisedSessionTopicsListAdapter;
import com.avadna.luneblaze.firebasenotifications.config.Config;
import com.avadna.luneblaze.activities.AppBaseActivity;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.pojo.PojoCreateSession;
import com.avadna.luneblaze.pojo.PojoLoginResponseData;
import com.avadna.luneblaze.pojo.pojoSessionDetails.PojoTopic;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SessionCreationPart1 extends AppBaseActivity implements
        OrganisedSessionTopicsListAdapter.DiscussionTopicItemClickCallback {

    private static final int SESSION_TITLE_MAX_LENGTH = 50;
    private static final int SESSION_DISCUSSION_MAX_LENGTH = 60;
    private static final int SESSION_PREREQUISITE_MAX_LENGTH = 60;


    String type;
    ApiInterface apiService;
    CommonFunctions commonFunctions;
    PreferenceUtils preferenceUtils;
    NestedScrollView nsv_parent_wrapper;

    View.OnClickListener onClickListener;

    int statusBarHeight = 0, actionBarHeight = 0;
    EditText et_session_name;
    TextInputLayout til_session_title;


    RecyclerView rv_discussion_topics_list;
    OrganisedSessionTopicsListAdapter discussionTopicListAdapter;
    List<PojoTopic> discussionTopicList;
    EditText et_discussion_topic;
    TextView tv_add_more_discuss;
    RelativeLayout rl_add_discussion_bar;

    RecyclerView rv_prerequisites_list;
    OrganisedSessionTopicsListAdapter preRequisitesListAdapter;
    List<PojoTopic> preRequisiteList;
    EditText et_requisite_topic;
    TextView tv_add_more_requisite;
    RelativeLayout rl_add_requisite_bar;

    int dateArr[][] = new int[4][3];

    PojoCreateSession pojoCreateSession;

    TextView tv_next;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    PojoLoginResponseData pojoLoginResponseData;

    View.OnFocusChangeListener onFocusChangeListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_creation_part1);
        commonFunctions = new CommonFunctions(this);
        preferenceUtils = new PreferenceUtils(this);
        pojoLoginResponseData = preferenceUtils.getUserLoginData();

        if (pojoLoginResponseData == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
        pojoCreateSession = new PojoCreateSession(5);
        type = getIntent().getStringExtra("type");

        if (type == null) {
            type = AppKeys.SESSION_INITIATE;
        }

        setUpActionBar();
        initBarSizes();
        initApis();
        initViews();
        initLists();
        initClickListener();
        setClickListener();
        setTextWatcher();
        initBroadCastReceiver();


    }


    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.SESSION_CREATION_FINISHED));


    }

    private void initBroadCastReceiver() {
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // checking for userType intent filter
                if (intent.getAction().equals(Config.SESSION_CREATION_FINISHED)) {
                    finish();
                }
            }
        };
    }

    private void setUpActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setTitle(getString(R.string.create_session));
            actionBar.setBackgroundDrawable(new ColorDrawable(ContextCompat
                    .getColor(SessionCreationPart1.this, R.color.action_bar_color)));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(ContextCompat
                        .getColor(SessionCreationPart1.this, R.color.status_bar_color));
            }
        }
    }


    private void initBarSizes() {
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = getResources().getDimensionPixelSize(resourceId);
        }


        final TypedArray styledAttributes = getTheme().obtainStyledAttributes(
                new int[]{android.R.attr.actionBarSize}
        );
        actionBarHeight = (int) styledAttributes.getDimension(0, 0);
        styledAttributes.recycle();

      /*  // status bar height
        int statusBarHeight = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = getResources().getDimensionPixelSize(resourceId);
        }

        // action bar height
        int actionBarHeight = 0;
        final TypedArray styledAttributes = getActivity().getTheme().obtainStyledAttributes(
                new int[] { android.R.attr.actionBarSize }
        );
        actionBarHeight = (int) styledAttributes.getDimension(0, 0);
        styledAttributes.recycle();

        // navigation bar height
        int navigationBarHeight = 0;
        int resourceId = getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            navigationBarHeight = resources.getDimensionPixelSize(resourceId);
        }*/
    }


    private void initApis() {
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);

    }

    private void setTextWatcher() {
        et_session_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {


            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > SESSION_TITLE_MAX_LENGTH) {
                    commonFunctions.setToastMessage(getApplicationContext(),
                            getString(R.string.session_title_no_longer_than),
                            Toast.LENGTH_LONG, AppKeys.TOAST_USER);
                    et_session_name.setText(charSequence.subSequence(0, 40));
                    et_session_name.setSelection(et_session_name.getText().length());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        et_discussion_topic.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > SESSION_DISCUSSION_MAX_LENGTH) {
                    commonFunctions.setToastMessage(getApplicationContext(),
                            getString(R.string.session_discussion_no_longer_than),
                            Toast.LENGTH_LONG, AppKeys.TOAST_USER);
                    et_discussion_topic.setText(charSequence.subSequence(0, SESSION_DISCUSSION_MAX_LENGTH));
                    et_discussion_topic.setSelection(et_discussion_topic.getText().length());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        et_requisite_topic.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > SESSION_PREREQUISITE_MAX_LENGTH) {
                    commonFunctions.setToastMessage(getApplicationContext(),
                            getString(R.string.session_prerequisite_no_longer_than)
                                    + SESSION_TITLE_MAX_LENGTH + getString(R.string.characters),
                            Toast.LENGTH_LONG, AppKeys.TOAST_USER);
                    et_requisite_topic.setText(charSequence.subSequence(0, SESSION_PREREQUISITE_MAX_LENGTH));
                    et_requisite_topic.setSelection(et_requisite_topic.getText().length());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void initClickListener() {
        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {

                    case R.id.tv_add_more_discuss:
                        addItemToDiscussionTopicList();
                        break;

                    case R.id.tv_add_more_requisite:
                        addItemToRequisiteList();
                        break;

                    case R.id.tv_next:
                        String title = et_session_name.getText().toString().trim();

                        if (!et_requisite_topic.getText().toString().trim().isEmpty()) {
                            preRequisiteList.add(new PojoTopic(et_requisite_topic.getText().toString().trim()));
                            et_requisite_topic.getText().clear();
                            preRequisitesListAdapter.notifyDataSetChanged();
                        }

                        if (!et_discussion_topic.getText().toString().trim().isEmpty()) {
                            discussionTopicList.add(new PojoTopic(et_discussion_topic.getText().toString().trim()));
                            et_discussion_topic.getText().clear();
                            discussionTopicListAdapter.notifyDataSetChanged();
                        }

                        if (title.length() < 5) {
                            commonFunctions.setToastMessage(getApplicationContext(),
                                    getString(R.string.session_minimum_five_chars), Toast.LENGTH_LONG, AppKeys.TOAST_USER);
                        } else if (preRequisiteList.isEmpty()) {
                            commonFunctions.setToastMessage(getApplicationContext(),
                                    getString(R.string.please_enter_prerequisite_topics), Toast.LENGTH_LONG, AppKeys.TOAST_USER);
                        } else if (discussionTopicList.size() < 3) {
                            commonFunctions.setToastMessage(getApplicationContext(),
                                    getString(R.string.please_enter_discussion_topics), Toast.LENGTH_LONG, AppKeys.TOAST_USER);
                        } else {
                            pojoCreateSession.title = title;
                            pojoCreateSession.prerequisite.clear();
                            pojoCreateSession.topics.clear();

                            pojoCreateSession.prerequisite.addAll(preRequisiteList);
                            pojoCreateSession.topics.addAll(discussionTopicList);
                            Intent part2Intent = new Intent(SessionCreationPart1.this,
                                    SessionCreationPart2.class);
                            Type jsonType = new TypeToken<PojoCreateSession>() {
                            }.getType();
                            part2Intent.putExtra("data", new Gson().toJson(pojoCreateSession, jsonType));
                            part2Intent.putExtra("type", type);
                            startActivity(part2Intent);
                        }


                        break;
                }
            }
        };
    }

    private void setClickListener() {
        tv_add_more_discuss.setOnClickListener(onClickListener);
        tv_add_more_requisite.setOnClickListener(onClickListener);
        tv_next.setOnClickListener(onClickListener);
    }


    private void initViews() {
        nsv_parent_wrapper = (NestedScrollView) findViewById(R.id.nsv_parent_wrapper);
        et_discussion_topic = (EditText) findViewById(R.id.et_discussion_topic);
        tv_add_more_discuss = (TextView) findViewById(R.id.tv_add_more_discuss);
        rl_add_discussion_bar = (RelativeLayout) findViewById(R.id.rl_add_discussion_bar);
        et_session_name = (EditText) findViewById(R.id.et_session_name);
        til_session_title = (TextInputLayout) findViewById(R.id.til_session_title);
        et_requisite_topic = (EditText) findViewById(R.id.et_requisite_topic);
        tv_add_more_requisite = (TextView) findViewById(R.id.tv_add_more_requisite);
        rl_add_requisite_bar = (RelativeLayout) findViewById(R.id.rl_add_requisite_bar);
        tv_next = (TextView) findViewById(R.id.tv_next);

    }

    private void initLists() {
        initDiscussionTopicList();
        initPreRequisiteList();
    }


    private void initDiscussionTopicList() {
        discussionTopicList = new ArrayList<>();
        rv_discussion_topics_list = (RecyclerView) findViewById(R.id.rv_discussion_topics_list);
        rv_discussion_topics_list.setLayoutManager
                (new LinearLayoutManager(SessionCreationPart1.this,
                        RecyclerView.VERTICAL, false));
        rv_discussion_topics_list.setNestedScrollingEnabled(false);
        discussionTopicListAdapter = new OrganisedSessionTopicsListAdapter(SessionCreationPart1.this,
                discussionTopicList, "dis");
        rv_discussion_topics_list.setAdapter(discussionTopicListAdapter);

    }

    private void initPreRequisiteList() {
        preRequisiteList = new ArrayList<>();
        rv_prerequisites_list = (RecyclerView) findViewById(R.id.rv_prerequisites_list);
        rv_prerequisites_list.setLayoutManager
                (new LinearLayoutManager(SessionCreationPart1.this,
                        RecyclerView.VERTICAL, false));
        rv_prerequisites_list.setNestedScrollingEnabled(false);
        preRequisitesListAdapter = new OrganisedSessionTopicsListAdapter(SessionCreationPart1.this,
                preRequisiteList, "preq");
        rv_prerequisites_list.setAdapter(preRequisitesListAdapter);

    }


    private void addItemToRequisiteList() {
        //add max 8 items to list
        if (preRequisiteList.size() < 8) {
            if (!et_requisite_topic.getText().toString().trim().isEmpty()) {
                preRequisiteList.add(new PojoTopic(et_requisite_topic.getText().toString().trim()));
                preRequisitesListAdapter.notifyDataSetChanged();
                et_requisite_topic.getText().clear();
                et_requisite_topic.setHint(getString(R.string.session_prerequisite_hint));
            }
        }
        //remove ability to add more items
        if (preRequisiteList.size() == 8) {
            rl_add_requisite_bar.setVisibility(View.GONE);
        }
    }


    private void addItemToDiscussionTopicList() {
        //add max 8 items to list
        if (discussionTopicList.size() < 8) {
            if (!et_discussion_topic.getText().toString().trim().isEmpty()) {
                discussionTopicList.add(new PojoTopic(et_discussion_topic.getText().toString().trim()));
                discussionTopicListAdapter.notifyDataSetChanged();
                et_discussion_topic.getText().clear();
                et_discussion_topic.setHint(getString(R.string.session_topic_hint));
            }
        }

        if (discussionTopicList.size() == 8) {
            rl_add_discussion_bar.setVisibility(View.GONE);
            et_requisite_topic.requestFocus();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        //    unsentMsgHandler.removeCallbacks(unsentMsgRunnable);

    }

    @Override
    public void discussionTopicItemClickCallback(String action, final String type, final int position) {
        switch (action) {
            case AppKeys.DELETE:
                if (type.equals("preq")) {
                    preRequisiteList.remove(position);
                    preRequisitesListAdapter.notifyDataSetChanged();
                    rl_add_requisite_bar.setVisibility(View.VISIBLE);
                } else {
                    discussionTopicList.remove(position);
                    discussionTopicListAdapter.notifyDataSetChanged();
                    rl_add_discussion_bar.setVisibility(View.VISIBLE);
                }
                break;

            case AppKeys.EDIT:

                TextView tv_dialog_description, tv_done, tv_cancel;
                final EditText et_discussion_topic;
                final Dialog dialog = new MyCustomThemeDialog(SessionCreationPart1.this);
                dialog.setContentView(R.layout.add_discussion_topic_dialog);
                tv_dialog_description = (TextView) dialog.findViewById(R.id.tv_dialog_description);
                tv_dialog_description.setText(getString(R.string.edit));
                tv_done = (TextView) dialog.findViewById(R.id.tv_done);
                tv_cancel = (TextView) dialog.findViewById(R.id.tv_cancel);
                et_discussion_topic = (EditText) dialog.findViewById(R.id.et_discussion_topic);

                et_discussion_topic.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        if (charSequence.length() > SESSION_DISCUSSION_MAX_LENGTH) {
                            commonFunctions.setToastMessage(getApplicationContext(),
                                    getString(R.string.no_longer_than_100), Toast.LENGTH_LONG,
                                    AppKeys.TOAST_USER);
                            et_discussion_topic.setText(charSequence.subSequence(0, SESSION_DISCUSSION_MAX_LENGTH));
                            et_discussion_topic.setSelection(et_discussion_topic.getText().length());
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });

                if (type.equals("preq")) {
                    et_discussion_topic.setText(Html.fromHtml(preRequisiteList.get(position).value));
                } else {
                    et_discussion_topic.setText(Html.fromHtml(discussionTopicList.get(position).value));
                }

                tv_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                tv_done.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (type.equals("preq")) {
                            preRequisiteList.get(position).value = et_discussion_topic.getText().toString().trim();
                            preRequisitesListAdapter.notifyDataSetChanged();
                        } else {
                            discussionTopicList.get(position).value = et_discussion_topic.getText().toString().trim();
                            discussionTopicListAdapter.notifyDataSetChanged();
                        }
                        dialog.dismiss();
                    }
                });
                dialog.setCancelable(false);
                dialog.show();
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

    }
}
