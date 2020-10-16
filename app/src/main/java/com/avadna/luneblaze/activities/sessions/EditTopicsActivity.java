package com.avadna.luneblaze.activities.sessions;

import android.app.Activity;
import android.app.Dialog; import com.avadna.luneblaze.helperClasses.MyCustomThemeDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.appcompat.app.ActionBar;

import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.adapters.session.organised.OrganisedSessionTopicsListAdapter;
import com.avadna.luneblaze.firebasenotifications.config.Config;
import com.avadna.luneblaze.activities.AppBaseActivity;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.pojo.PojoNoDataResponse;
import com.avadna.luneblaze.pojo.pojoSessionDetails.PojoSessionDetailsResponseData;
import com.avadna.luneblaze.pojo.pojoSessionDetails.PojoTopic;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditTopicsActivity extends AppBaseActivity implements
        OrganisedSessionTopicsListAdapter.DiscussionTopicItemClickCallback {

    ActionBar actionBar;
    ApiInterface apiService;
    PreferenceUtils preferenceUtils;
    CommonFunctions commonFunctions;
    String user_id;

    PojoSessionDetailsResponseData pojoSessionDetailsResponseData;

    TextView tv_save, tv_add_more_discuss, tv_add_more_requisite;
    EditText et_discussion_topic, et_requisite_topic;

    RecyclerView rv_discussion_topics_list;
    OrganisedSessionTopicsListAdapter discussionTopicListAdapter;
    List<PojoTopic> discussionTopicList;

    RecyclerView rv_prerequisites_list;
    OrganisedSessionTopicsListAdapter preRequisiteListAdapter;
    List<PojoTopic> preRequisiteList;

    View.OnClickListener onClickListener;
    String dataStr;
    String session_id;

    ScrollView sv_contentWrapper;
    ProgressBar pb_loading;
    TextView tv_updating;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_topics);
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
        preferenceUtils = new PreferenceUtils(this);
        commonFunctions = new CommonFunctions(this);
        user_id = preferenceUtils.get_user_id();

        dataStr = getIntent().getStringExtra("data");
        if (dataStr != null && !dataStr.isEmpty()) {
            Type type = new TypeToken<PojoSessionDetailsResponseData>() {
            }.getType();
            pojoSessionDetailsResponseData = new Gson().fromJson(dataStr, type);
            session_id = pojoSessionDetailsResponseData.sessionsId;
        }

        setUpActionBar();
        initViews();
        initClickListener();
        setClickListeners();



    }


    @Override
    protected void onResume() {
        super.onResume();

    }

    private void setUpActionBar() {
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setTitle(getString(R.string.add_topics));
            actionBar.setBackgroundDrawable(new ColorDrawable(ContextCompat
                    .getColor(EditTopicsActivity.this, R.color.action_bar_color)));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(ContextCompat
                        .getColor(EditTopicsActivity.this, R.color.status_bar_color));
            }
        }
    }

    private void initViews() {
        initTopicList();
        initPreRequisiteList();

        tv_save = (TextView) findViewById(R.id.tv_save);
        tv_add_more_discuss = (TextView) findViewById(R.id.tv_add_more_discuss);
        tv_add_more_requisite = (TextView) findViewById(R.id.tv_add_more_requisite);

        et_discussion_topic = (EditText) findViewById(R.id.et_discussion_topic);
        et_requisite_topic = (EditText) findViewById(R.id.et_requisite_topic);

        sv_contentWrapper = (ScrollView) findViewById(R.id.sv_contentWrapper);
        pb_loading = (ProgressBar) findViewById(R.id.pb_loading);
        pb_loading.setVisibility(View.GONE);
        tv_updating = (TextView) findViewById(R.id.tv_updating);
        tv_updating.setVisibility(View.GONE);
    }

    private void initClickListener() {
        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.tv_add_more_discuss:
                        if(!et_discussion_topic.getText().toString().trim().isEmpty()){
                            discussionTopicList.add(new PojoTopic(et_discussion_topic.getText().toString().trim()));
                            discussionTopicListAdapter.notifyDataSetChanged();
                            et_discussion_topic.getText().clear();
                            et_discussion_topic.setHint("Enter topic here");
                        }
                        break;

                    case R.id.tv_add_more_requisite:
                        if(!et_requisite_topic.getText().toString().trim().isEmpty()){
                            preRequisiteList.add(new PojoTopic(et_requisite_topic.getText().toString().trim()));
                            preRequisiteListAdapter.notifyDataSetChanged();
                            et_requisite_topic.getText().clear();
                            et_requisite_topic.setHint("Enter topic here");
                        }
                        break;

                    case R.id.tv_save:
                        if (!discussionTopicListAdapter.getTopicList().isEmpty()) {
                            hitEditSessionTopicsApi("edit_topics", user_id, session_id,
                                    discussionTopicListAdapter.getTopicList(), preRequisiteListAdapter.getTopicList());
                        }
                        break;
                }
            }
        };
    }

    private void setClickListeners() {
        tv_add_more_discuss.setOnClickListener(onClickListener);
        tv_add_more_requisite.setOnClickListener(onClickListener);
        tv_save.setOnClickListener(onClickListener);
    }

    private void initPreRequisiteList() {
        preRequisiteList = new ArrayList<>();
        preRequisiteList.addAll(modTopicList(pojoSessionDetailsResponseData.prerequisite));
        rv_prerequisites_list = (RecyclerView) findViewById(R.id.rv_prerequisites_list);
        rv_prerequisites_list.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));
        rv_prerequisites_list.setNestedScrollingEnabled(false);
        preRequisiteListAdapter = new OrganisedSessionTopicsListAdapter(this, preRequisiteList, "preq");
        rv_prerequisites_list.setAdapter(preRequisiteListAdapter);
    }


    private void initTopicList() {
        discussionTopicList = new ArrayList<>();
        discussionTopicList.addAll(modTopicList(pojoSessionDetailsResponseData.topics));
        rv_discussion_topics_list = (RecyclerView) findViewById(R.id.rv_discussion_topics_list);
        rv_discussion_topics_list.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));
        rv_discussion_topics_list.setNestedScrollingEnabled(false);
        discussionTopicListAdapter = new OrganisedSessionTopicsListAdapter(this, discussionTopicList, "dis");
        rv_discussion_topics_list.setAdapter(discussionTopicListAdapter);
    }

    private List<PojoTopic> modTopicList(List<PojoTopic> topics) {
        for (int i = 0; i < topics.size(); i++) {
            topics.get(i).editable = "0";
        }
        return topics;
    }

    private void hitEditSessionTopicsApi(String handle, String user_id, String session_id,
                                         List<PojoTopic> topicList, List<PojoTopic> prerequisiteList) {
        HashMap<String, String> topicsMap = new HashMap<>();
        for (int i = 0; i < topicList.size(); i++) {
            if (topicList.get(i).editable.equals("1")) {
                topicsMap.put("topics[" + i + "]", topicList.get(i).value);
            }
        }

        HashMap<String, String> prerequisiteMap = new HashMap<>();
        for (int i = 0; i < prerequisiteList.size(); i++) {
            if (prerequisiteList.get(i).editable.equals("1")) {
                prerequisiteMap.put("prerequisite[" + i + "]", prerequisiteList.get(i).value);
            }
        }

        Call<PojoNoDataResponse> call = apiService.editSessionTopics(handle, user_id, session_id,
                topicsMap, prerequisiteMap);
        sv_contentWrapper.setVisibility(View.GONE);
        tv_save.setVisibility(View.GONE);
        pb_loading.setVisibility(View.VISIBLE);
        tv_updating.setVisibility(View.VISIBLE);

        call.enqueue(new Callback<PojoNoDataResponse>() {
            @Override
            public void onResponse(Call<PojoNoDataResponse> call, Response<PojoNoDataResponse> response) {
                if (response.body() != null) {
                    String message = response.body().message;
                    commonFunctions.setToastMessage(getApplicationContext(), message, Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                    Intent pushNotification = new Intent(Config.SESSION_DETAILS_UPDATED);
                    LocalBroadcastManager.getInstance(getApplicationContext())
                            .sendBroadcast(pushNotification);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<PojoNoDataResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(getApplicationContext(), t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                sv_contentWrapper.setVisibility(View.VISIBLE);
                tv_save.setVisibility(View.VISIBLE);
                pb_loading.setVisibility(View.GONE);
                tv_updating.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void discussionTopicItemClickCallback(String action, final String type, final int position) {
        switch (action) {
            case AppKeys.DELETE:
                if (type.equals("preq")) {
                    preRequisiteList.remove(position);
                    preRequisiteListAdapter.notifyDataSetChanged();
                } else {
                    discussionTopicList.remove(position);
                    discussionTopicListAdapter.notifyDataSetChanged();
                }
                break;

            case AppKeys.EDIT:

                TextView tv_dialog_description, tv_done, tv_cancel;
                final EditText et_discussion_topic;
                final Dialog dialog = new MyCustomThemeDialog(EditTopicsActivity.this);
                dialog.setContentView(R.layout.add_discussion_topic_dialog);
                tv_dialog_description = (TextView) dialog.findViewById(R.id.tv_dialog_description);
                tv_dialog_description.setText(getString(R.string.edit));
                tv_done = (TextView) dialog.findViewById(R.id.tv_done);
                tv_cancel = (TextView) dialog.findViewById(R.id.tv_cancel);
                et_discussion_topic = (EditText) dialog.findViewById(R.id.et_discussion_topic);

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
                            preRequisiteListAdapter.notifyDataSetChanged();
                        } else {
                            discussionTopicList.get(position).value = et_discussion_topic.getText().toString().trim();
                            discussionTopicListAdapter.notifyDataSetChanged();
                        }
                        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(et_discussion_topic.getWindowToken(), 0);
                        dialog.dismiss();

                    }
                });
                dialog.setCancelable(false);
                dialog.show();
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
        }
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

}
