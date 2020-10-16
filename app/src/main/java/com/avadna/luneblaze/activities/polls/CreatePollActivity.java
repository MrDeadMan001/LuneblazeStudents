package com.avadna.luneblaze.activities.polls;

import android.app.Dialog; import com.avadna.luneblaze.helperClasses.MyCustomThemeDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.core.widget.NestedScrollView;
import androidx.appcompat.app.ActionBar;
import android.os.Bundle;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.adapters.PollOptionsListAdapter;
import com.avadna.luneblaze.firebasenotifications.config.Config;
import com.avadna.luneblaze.activities.AppBaseActivity;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.pojo.PojoCreatePostResponse;
import com.avadna.luneblaze.pojo.PojoLoginResponseData;
import com.avadna.luneblaze.pojo.PojoNoDataResponse;
import com.avadna.luneblaze.pojo.pojoNewsFeed.PojoGetNewsFeedResponseData;
import com.avadna.luneblaze.pojo.pojoNewsFeed.PojoNewsFeedPollOption;
import com.avadna.luneblaze.pojo.pojonormalpost.PojoNormalPostResponse;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreatePollActivity extends AppBaseActivity implements
        PollOptionsListAdapter.PollItemOptionClickCallback {
    ActionBar actionBar;
    PreferenceUtils preferenceUtils;
    CommonFunctions commonFunctions;
    String user_id;
    ApiInterface apiService;
    PojoLoginResponseData pojoLoginResponseData;

    TextInputLayout til_poll_title;
    EditText et_poll_title;
    GridLayoutManager gridLayoutManager;
    RecyclerView rv_poll_options_list;
    PollOptionsListAdapter pollOptionsListAdapter;
    List<PojoNewsFeedPollOption> pollOptionsList;
    List<PojoNewsFeedPollOption> deletedOptionList;
    EditText et_poll_option;
    TextView tv_add_more_options;
    RelativeLayout rl_add_option_bar;
    NestedScrollView sv_content_wrapper;
    ProgressBar pb_creating_poll;
    boolean isCreateApiCalled = false;
    TextView tv_create;
    View.OnClickListener onClickListener;
    PojoGetNewsFeedResponseData pojoGetNewsFeedResponseData;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_poll);
        preferenceUtils = new PreferenceUtils(CreatePollActivity.this);
        pojoLoginResponseData = preferenceUtils.getUserLoginData();
        commonFunctions = new CommonFunctions(this);
        user_id = preferenceUtils.get_user_id();
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
        setUpActionBar();
        initViews();
        initClickListener();
        setClickListener();

        if (getIntent() != null) {
            Type type = new TypeToken<PojoGetNewsFeedResponseData>() {
            }.getType();
            String dataStr = getIntent().getStringExtra(AppKeys.DATA);
            pojoGetNewsFeedResponseData = new Gson().fromJson(dataStr, type);
            if (pojoGetNewsFeedResponseData != null) {
                setEditableData();
            }
        }



    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void setEditableData() {
        deletedOptionList = new ArrayList<>();
        pollOptionsList.clear();
        pollOptionsList.addAll(pojoGetNewsFeedResponseData.poll.options);
        pollOptionsListAdapter.notifyDataSetChanged();
        et_poll_title.setText(pojoGetNewsFeedResponseData.textPlain);

        tv_create.setText(getString(R.string.edit));
    }

    private void setUpActionBar() {
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setTitle(getString(R.string.create_poll));
            actionBar.setBackgroundDrawable(new ColorDrawable(ContextCompat
                    .getColor(CreatePollActivity.this, R.color.action_bar_color)));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(ContextCompat
                        .getColor(CreatePollActivity.this, R.color.status_bar_color));
            }
        }
    }

    private void initViews() {
        et_poll_title = (EditText) findViewById(R.id.et_poll_title);
        til_poll_title= (TextInputLayout) findViewById(R.id.til_poll_title);
        et_poll_option = (EditText) findViewById(R.id.et_poll_option);
        tv_add_more_options = (TextView) findViewById(R.id.tv_add_more_options);
        rl_add_option_bar = (RelativeLayout) findViewById(R.id.rl_add_option_bar);
        sv_content_wrapper = (NestedScrollView) findViewById(R.id.sv_content_wrapper);
        pb_creating_poll = (ProgressBar) findViewById(R.id.pb_creating_poll);
        pb_creating_poll.setVisibility(View.GONE);
        tv_create = (TextView) findViewById(R.id.tv_create);
        initPollOptionsList();
    }

    private void initPollOptionsList() {
        pollOptionsList = new ArrayList<>();
        rv_poll_options_list = (RecyclerView) findViewById(R.id.rv_poll_options_list);
        gridLayoutManager = (new GridLayoutManager(CreatePollActivity.this, 1));
      /*  rv_poll_options_list.setLayoutManager
                (new LinearLayoutManager(CreatePollActivity.this,
                        LinearLayoutManager.VERTICAL, false));*/
        rv_poll_options_list.setLayoutManager(gridLayoutManager);
        rv_poll_options_list.setNestedScrollingEnabled(false);
        pollOptionsListAdapter = new PollOptionsListAdapter(CreatePollActivity.this, pollOptionsList);
        rv_poll_options_list.setAdapter(pollOptionsListAdapter);
    }

    private void initClickListener() {
        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.tv_create:
                        if (pollOptionsList.size() < 8){
                            addItemToPollList();
                        }
                        createPost();
                        break;

                    case R.id.iv_cover:

                        break;

                    case R.id.tv_add_more_options:
                        addItemToPollList();
                        break;

                }
            }
        };
    }

    private void setClickListener() {
        tv_add_more_options.setOnClickListener(onClickListener);
        tv_create.setOnClickListener(onClickListener);
    }


    private void addItemToPollList() {
        //add max 8 items to list
        if (pollOptionsList.size() < 8) {

            String currentOption=et_poll_option.getText().toString().trim();

            if (!currentOption.isEmpty()) {

                boolean alreadyPresent=false;
                for(int i=0;i<pollOptionsList.size();i++){
                    if(currentOption.equals(pollOptionsList.get(i).text)){
                        commonFunctions.setToastMessage(getApplicationContext(),
                                getString(R.string.this_option_already_added),Toast.LENGTH_LONG,AppKeys.TOAST_USER);
                        alreadyPresent=true;
                        break;
                    }
                }

                if(!alreadyPresent){
                    PojoNewsFeedPollOption pojoNewsFeedPollOption =
                            new PojoNewsFeedPollOption(et_poll_option.getText().toString().trim());
                    pollOptionsList.add(pojoNewsFeedPollOption);
                    pollOptionsListAdapter.notifyDataSetChanged();
                    et_poll_option.getText().clear();
                    et_poll_option.setHint(getString(R.string.enter_option_here));
                }
            }
        }
        if (pollOptionsList.size() == 8) {
            rl_add_option_bar.setVisibility(View.GONE);
        }
    }

    private void createPost() {
        if (pollOptionsList.size() < 2) {
            commonFunctions.setToastMessage(getApplicationContext(), getString(R.string.please_add_two_poll_options),
                    Toast.LENGTH_LONG, AppKeys.TOAST_USER);
        } else if (et_poll_title.getText().toString().isEmpty()) {
            //til_poll_title.setError(getString(R.string.please_enter_poll_title));
            commonFunctions.setToastMessage(getApplicationContext(), getString(R.string.please_enter_poll_title),
                    Toast.LENGTH_LONG, AppKeys.TOAST_USER);
        } else {
            if (pojoGetNewsFeedResponseData != null) {
                pb_creating_poll.setVisibility(View.VISIBLE);
                hitGetNormalPostApi(user_id, pojoGetNewsFeedResponseData.postId, "0", "prev", true);

            } else {
                hitCreatePollApi(user_id, "me", et_poll_title.getText().toString(),
                        "public", pollOptionsList);
            }

            InputMethodManager imm = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(et_poll_title.getWindowToken(), 0);

        }
    }

    private void hitGetNormalPostApi(final String user_id, final String post_id, final String comment_id,
                                     final String order, final boolean scrollToEnd) {
        Call<PojoNormalPostResponse> call = apiService.getNormalPostData(user_id, post_id,
                comment_id, order,"0");

        call.enqueue(new Callback<PojoNormalPostResponse>() {
            @Override
            public void onResponse(Call<PojoNormalPostResponse> call, Response<PojoNormalPostResponse> response) {
                String message = "";
                if (response != null && response.body() != null && response.body().data != null) {
                    message = response.body().message;
                    pojoGetNewsFeedResponseData = response.body().data.post;
                    if (!pojoGetNewsFeedResponseData.poll.votes.equals("0")) {
                        TextView tv_dialog_description, tv_no, tv_yes;
                        final Dialog confirmationDialog;
                        confirmationDialog = new MyCustomThemeDialog(CreatePollActivity.this);
                        confirmationDialog.setContentView(R.layout.yes_no_dialog);
                        tv_dialog_description = (TextView) confirmationDialog.findViewById(R.id.tv_dialog_description);
                        tv_no = (TextView) confirmationDialog.findViewById(R.id.tv_no);
                        tv_yes = (TextView) confirmationDialog.findViewById(R.id.tv_yes);
                        tv_yes.setVisibility(View.GONE);

                        tv_no.setText(getString(R.string.ok));
                        tv_no.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                confirmationDialog.dismiss();
                            }
                        });

                        tv_dialog_description.setText(R.string.you_cannot_edit_poll_after_votes_have_been_casted);
                        confirmationDialog.show();
                        pb_creating_poll.setVisibility(View.GONE);

                    } else {
                        pojoGetNewsFeedResponseData.poll.options = pollOptionsList;
                        pojoGetNewsFeedResponseData.textPlain = et_poll_title.getText().toString().trim();
                        hitEditPollApi(user_id, "poll", pojoGetNewsFeedResponseData.postId,
                                et_poll_title.getText().toString().trim(), pollOptionsList);
                    }
                }
            }

            @Override
            public void onFailure(Call<PojoNormalPostResponse> call, Throwable t) {
                // Log error here since request failed
                pb_creating_poll.setVisibility(View.GONE);

            }
        });

    }

    private void hitCreatePollApi(String user_id, String handle, String title, String privacy,
                                  List<PojoNewsFeedPollOption> optionList) {

        if (!isCreateApiCalled) {
            isCreateApiCalled = true;
            HashMap<String, String> optionMap = new HashMap<>();
            for (int i = 0; i < optionList.size(); i++) {
                optionMap.put("poll_options[" + i + "]", optionList.get(i).text);
            }

            Call<PojoCreatePostResponse> call = apiService.createPollPost(user_id, handle, title, privacy,
                    optionMap);
            sv_content_wrapper.setEnabled(false);
            pb_creating_poll.setVisibility(View.VISIBLE);
            call.enqueue(new Callback<PojoCreatePostResponse>() {
                @Override
                public void onResponse(Call<PojoCreatePostResponse> call, Response<PojoCreatePostResponse> response) {
                    if (response.body() != null) {
                        String message = response.body().message;
                        commonFunctions.setToastMessage(getApplicationContext(),
                                getString(R.string.poll_created_successfully), Toast.LENGTH_LONG,
                                AppKeys.TOAST_USER);
                        response.body().data.total = "0";
                        response.body().data.userPicture = pojoLoginResponseData.userPicture;
                        response.body().data.tagged = "0";
                        response.body().data.iLike = 0;
                        response.body().data.userFullname = pojoLoginResponseData.userFullname;

                        String dataStr = new Gson().toJson(
                                response.body().data, new TypeToken<PojoGetNewsFeedResponseData>() {
                                }.getType());

                        Intent newsFeedIntent = new Intent(Config.ADD_NEWS_FEED_ITEM);
                        newsFeedIntent.putExtra("data", dataStr);
                        LocalBroadcastManager.getInstance(CreatePollActivity.this)
                                .sendBroadcast(newsFeedIntent);
                        pb_creating_poll.setVisibility(View.GONE);

                        setResult(RESULT_OK);

                        finish();

                    }
                    isCreateApiCalled = false;
                }

                @Override
                public void onFailure(Call<PojoCreatePostResponse> call, Throwable t) {
                    // Log error here since request failed
                    commonFunctions.setToastMessage(getApplicationContext(), t.toString(),
                            Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                    pb_creating_poll.setVisibility(View.GONE);
                    sv_content_wrapper.setEnabled(true);

                    isCreateApiCalled = false;
                }
            });
        }

    }


    private void hitEditPollApi(String user_id, String type, String id, String message,
                                List<PojoNewsFeedPollOption> optionList) {
        sv_content_wrapper.setEnabled(false);

        HashMap<String, String> optionMap = new HashMap<>();
        for (int i = 0; i < optionList.size(); i++) {
            if (!optionList.get(i).optionId.isEmpty()) {
                optionMap.put("options[" + optionList.get(i).optionId + "]", optionList.get(i).text);
            } else {
                optionMap.put("options[new][]", optionList.get(i).text);
            }
        }

        for (int i = 0; i < deletedOptionList.size(); i++) {
            optionMap.put("options[delete][]", deletedOptionList.get(i).optionId);

        }

        Call<PojoNoDataResponse> call = apiService.editPollPost(user_id, type, id, message,
                optionMap);
        call.enqueue(new Callback<PojoNoDataResponse>() {
            @Override
            public void onResponse(Call<PojoNoDataResponse> call, Response<PojoNoDataResponse> response) {
                if (response.body() != null) {
                    String message = response.body().message;
                    commonFunctions.setToastMessage(getApplicationContext(), message, Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                    pb_creating_poll.setVisibility(View.GONE);
                    Intent pushNotification = new Intent(Config.POLL_EDITED);
                    LocalBroadcastManager.getInstance(CreatePollActivity.this).sendBroadcast(pushNotification);

                    String dataStr = new Gson().toJson(
                            pojoGetNewsFeedResponseData, new TypeToken<PojoGetNewsFeedResponseData>() {
                            }.getType());

                    Intent newsFeedIntent = new Intent(Config.EDIT_NEWS_FEED_ITEM);
                    newsFeedIntent.putExtra("data", dataStr);
                    LocalBroadcastManager.getInstance(CreatePollActivity.this)
                            .sendBroadcast(newsFeedIntent);

                    finish();

                }
            }

            @Override
            public void onFailure(Call<PojoNoDataResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(getApplicationContext(), t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                pb_creating_poll.setVisibility(View.GONE);
                sv_content_wrapper.setEnabled(true);


            }
        });
    }


    @Override
    public void pollOptionItemMethodCallback(int position, String type) {
        switch (type) {
            case AppKeys.EDIT:
                openEditReplyDialog(position);
                break;
            case AppKeys.DELETE:
                if (pollOptionsList.get(position).optionId != null && !pollOptionsList.get(position).optionId.isEmpty()) {
                    deletedOptionList.add(pollOptionsList.get(position));
                }
                pollOptionsList.remove(position);
                pollOptionsListAdapter.notifyDataSetChanged();
                break;
        }
    }

    private void openEditReplyDialog(final int position) {
        final Dialog dialog = new MyCustomThemeDialog(CreatePollActivity.this);
        dialog.setContentView(R.layout.edit_content_dialog);

        final EditText et_content = (EditText) dialog.findViewById(R.id.et_content);
        TextView tv_done = (TextView) dialog.findViewById(R.id.tv_done);
        TextView tv_cancel = (TextView) dialog.findViewById(R.id.tv_cancel);
        et_content.setText(Html.fromHtml(pollOptionsList.get(position).text));
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
                    commonFunctions.setToastMessage(getApplicationContext(), getString(R.string.please_enter_data),
                            Toast.LENGTH_LONG, AppKeys.TOAST_USER);
                } else {
                    pollOptionsList.get(position).text = newData;
                    dialog.dismiss();
                    pollOptionsListAdapter.notifyDataSetChanged();
                }
            }
        });
        dialog.show();
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
    protected void onPause() {
        super.onPause();

    }
}
