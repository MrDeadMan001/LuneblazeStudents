package com.avadna.luneblaze.activities.sessions;

import androidx.appcompat.app.ActionBar;

import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.adapters.session.organised.OrganisedSessionTopicsListAdapter;
import com.avadna.luneblaze.activities.AppBaseActivity;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.pojo.pojoSessionDetails.PojoSessionDetailsResponseData;
import com.avadna.luneblaze.pojo.pojoSessionDetails.PojoTopic;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class EditSessionTopicsActivity extends AppBaseActivity implements
        OrganisedSessionTopicsListAdapter.DiscussionTopicItemClickCallback {
    ActionBar actionBar;
    ApiInterface apiService;
    PreferenceUtils preferenceUtils;
    String user_id;
    String session_id = "";

    PojoSessionDetailsResponseData pojoSessionDetailsResponseData = null;

    TextView tv_save;
    ProgressBar pb_edit_topic_api_status;
    RelativeLayout rl_edit_topic_parent_wrapper;

    RecyclerView rv_discussion_topics_list;
    OrganisedSessionTopicsListAdapter discussionTopicListAdapter;
    List<PojoTopic> discussionTopicList;

    RecyclerView rv_prerequisites_list;
    OrganisedSessionTopicsListAdapter preRequisiteListAdapter;
    List<PojoTopic> preRequisiteList;

    View.OnClickListener onClickListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_session_topics);

        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
        preferenceUtils = new PreferenceUtils(this);
        user_id = preferenceUtils.get_user_id();
        session_id = getIntent().getStringExtra("id");
        if (session_id == null) {
            session_id = "134";
        }
        String dataStr = getIntent().getStringExtra("data");
        if (dataStr != null && !dataStr.isEmpty()) {
            Type type = new TypeToken<PojoSessionDetailsResponseData>() {
            }.getType();
            pojoSessionDetailsResponseData = new Gson().fromJson(dataStr, type);

            initViews();
            initDiscussionList();
            initPreRequisiteList();
            initClickListener();
            setClickListener();
        }

    }


    private void initClickListener() {
        onClickListener=new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()){

                }
            }
        };
    }

    private void setClickListener() {
    }

    private void initViews() {
        tv_save = (TextView) findViewById(R.id.tv_save);
        rl_edit_topic_parent_wrapper=(RelativeLayout) findViewById(R.id.rl_edit_topic_parent_wrapper);
        pb_edit_topic_api_status=(ProgressBar)findViewById(R.id.pb_edit_topic_api_status);
        pb_edit_topic_api_status.setVisibility(View.GONE);
    }

    private void initDiscussionList() {
        discussionTopicList = new ArrayList<>();
        discussionTopicList.addAll(modTopicList(pojoSessionDetailsResponseData.topics));
        rv_discussion_topics_list = (RecyclerView) findViewById(R.id.rv_discussion_topics_list);
        rv_discussion_topics_list.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));
        rv_discussion_topics_list.setNestedScrollingEnabled(false);
        discussionTopicListAdapter = new OrganisedSessionTopicsListAdapter(this, discussionTopicList,"dis");
        rv_discussion_topics_list.setAdapter(discussionTopicListAdapter);
        final EditText et_discussion_topic = (EditText) findViewById(R.id.et_discussion_topic);
        TextView tv_add_more_discuss = (TextView) findViewById(R.id.tv_add_more_discuss);
        tv_add_more_discuss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                discussionTopicList.add(new PojoTopic(et_discussion_topic.getText().toString().trim()));
                discussionTopicListAdapter.notifyDataSetChanged();
                et_discussion_topic.getText().clear();
                et_discussion_topic.setHint("Enter topic here");
            }
        });
    }

    private void initPreRequisiteList() {
        preRequisiteList = new ArrayList<>();
        preRequisiteList.addAll(modTopicList(pojoSessionDetailsResponseData.topics));
        rv_prerequisites_list = (RecyclerView) findViewById(R.id.rv_prerequisites_list);
        rv_prerequisites_list.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));
        rv_prerequisites_list.setNestedScrollingEnabled(false);
        preRequisiteListAdapter = new OrganisedSessionTopicsListAdapter(this, preRequisiteList,"preq");
        rv_prerequisites_list.setAdapter(preRequisiteListAdapter);
        TextView tv_add_more_requisite= (TextView) findViewById(R.id.tv_add_more_requisite);
        final EditText et_requisite_topic= (EditText) findViewById(R.id.et_requisite_topic);
        tv_add_more_requisite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                preRequisiteList.add(new PojoTopic(et_requisite_topic.getText().toString().trim()));
                preRequisiteListAdapter.notifyDataSetChanged();
                et_requisite_topic.getText().clear();
                et_requisite_topic.setHint("Enter topic here");
            }
        });

    }


    private List<PojoTopic> modTopicList(List<PojoTopic> topics) {
        for (int i = 0; i < topics.size(); i++) {
            topics.get(i).editable = "0";
        }
        return topics;
    }

    @Override
    public void discussionTopicItemClickCallback(String action, String type, int position) {

    }


}
