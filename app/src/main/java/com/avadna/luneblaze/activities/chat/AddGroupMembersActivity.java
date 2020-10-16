package com.avadna.luneblaze.activities.chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.adapters.chat.AddChatMemberListAdapter;
import com.avadna.luneblaze.activities.AppBaseActivity;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.ConnectionErrorDialog;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.pojo.PojoGetFriendsListResponse;
import com.avadna.luneblaze.pojo.PojoNoDataResponse;
import com.avadna.luneblaze.pojo.pojoQuestion.PojoUserData;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddGroupMembersActivity extends AppBaseActivity implements
        AddChatMemberListAdapter.AddChatMemberAdapterCallback {
    Toolbar tl_top_bar;
    ActionBar actionBar;

    ApiInterface apiService;
    PreferenceUtils preferenceUtils;
    CommonFunctions commonFunctions;
    String user_id;

    RecyclerView rv_list;
    LinearLayoutManager linearLayoutManager;
    List<PojoUserData> userList;
    AddChatMemberListAdapter addChatMemberListAdapter;


    EditText et_search_bar;
    ImageButton ib_back_gray, ib_close_gray;

    int offset = 0;
    boolean isGetUserListApiCalled = false;
    boolean listEndReached = false;

    private boolean isGetFriendApiCalled = false;
    private String group_id;
    String currentQuery = "";
    boolean memberAdded = false;


    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_invite);
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
        preferenceUtils = new PreferenceUtils(this);
        commonFunctions = new CommonFunctions(this);
        user_id = preferenceUtils.get_user_id();
        group_id = getIntent().getStringExtra(AppKeys.GROUP_ID);
        setUpActionBar();
        initViews();
        initList();
        offset = 0;
    }

    private void setUpActionBar() {
        tl_top_bar = findViewById(R.id.tl_top_bar);
        setSupportActionBar(tl_top_bar);
        actionBar = getSupportActionBar();
        if (tl_top_bar != null) {
            // Show the Up button in the action bar.
            //    actionBar.setDisplayHomeAsUpEnabled(true);
            tl_top_bar.setBackgroundDrawable(new ColorDrawable(ContextCompat
                    .getColor(AddGroupMembersActivity.this, R.color.action_bar_color)));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(ContextCompat
                        .getColor(AddGroupMembersActivity.this, R.color.status_bar_color));
            }
        }
    }

    private void initViews() {
        ib_back_gray = (ImageButton) findViewById(R.id.ib_back_gray);
        ib_back_gray.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        ib_close_gray = (ImageButton) findViewById(R.id.ib_close_gray);
        ib_close_gray.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_search_bar.getText().clear();
             /*   offset = 0;
                userList.clear();
                addChatMemberListAdapter.notifyDataSetChanged();
                pb_loading_content.setVisibility(View.GONE);

                if (list_type.equals(AppKeys.ANSWER_INVITATION_KEY)) {
                    hitGetAnswerInviteListApi(user_id, list_type, inviteItemId,
                            et_search_bar.getText().toString(), offset);
                } else if (list_type.equals(AppKeys.INTEREST_INVITATION_KEY) ||
                        list_type.equals(AppKeys.VENUE_INVITATION_KEY)) {
                    hitGetVenueInterestInviteListApi(user_id, list_type, inviteItemId,
                            et_search_bar.getText().toString(), offset);
                } else if (list_type.equals(AppKeys.SESSION_CONDUCT_INVITATION_KEY) ||
                        list_type.equals(AppKeys.SESSION_ATTEND_INVITATION_KEY)) {
                    hitSessionInviteListApi(user_id, list_type, inviteItemId,
                            et_search_bar.getText().toString(), offset);
                }*/
            }
        });
        //  ib_search_gray.setVisibility(View.GONE);

        et_search_bar = (EditText) findViewById(R.id.et_search_bar);
        et_search_bar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                offset = 0;
                userList.clear();
                addChatMemberListAdapter.notifyDataSetChanged();
                listEndReached = false;
                currentQuery = s.toString().trim();
                hitGetUserListApi(user_id, group_id, offset, currentQuery);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


    }

    private void initList() {
        userList = new ArrayList<>();
        rv_list = (RecyclerView) findViewById(R.id.rv_list);
        linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        rv_list.setLayoutManager(linearLayoutManager);
        addChatMemberListAdapter = new AddChatMemberListAdapter(AddGroupMembersActivity.this,
                userList, "add_group_members");
        rv_list.setAdapter(addChatMemberListAdapter);

        rv_list.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 5 && !isGetUserListApiCalled
                        && linearLayoutManager.findLastVisibleItemPosition() > userList.size() - 2
                        && !listEndReached) {
                    hitGetUserListApi(user_id, group_id, ++offset, et_search_bar.getText().toString().trim());

                }
            }
        });


        hitGetUserListApi(user_id, group_id, offset, et_search_bar.getText().toString().trim());

    }

    private void hitGetUserListApi(String user_id, String group_id, int offset, String query) {
        Call<PojoGetFriendsListResponse> call = apiService.getFriendsNotInGroup(user_id, user_id,
                group_id, query, "0", "0", String.valueOf(offset));
        if (!isGetUserListApiCalled) {
            isGetUserListApiCalled = true;
            addChatMemberListAdapter.setShowProgressBar(true);
            call.enqueue(new Callback<PojoGetFriendsListResponse>() {
                @Override
                public void onResponse(Call<PojoGetFriendsListResponse> call, Response<PojoGetFriendsListResponse> response) {
                    isGetFriendApiCalled = false;
                    if (response.body() != null) {
                        String message = response.body().message;
                        if (response.body().data != null) {
                            if (offset == 0) {
                                userList.clear();
                            }
                            if (query.equals(currentQuery)) {
                                userList.addAll(response.body().data.friends);
                                addChatMemberListAdapter.setShowProgressBar(false);
                                addChatMemberListAdapter.notifyDataSetChanged();

                            } else {
                                hitGetUserListApi(user_id, group_id, offset, currentQuery);
                            }
                        } else {
                            commonFunctions.setToastMessage(getApplicationContext(),
                                    "" + message, Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                        }
                    }
                }

                @Override
                public void onFailure(Call<PojoGetFriendsListResponse> call, Throwable t) {
                    // Log error here since request failed
                    addChatMemberListAdapter.setShowProgressBar(false);
                    commonFunctions.setToastMessage(getApplicationContext(), t.toString(),
                            Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                    isGetFriendApiCalled = false;
                    if (userList.isEmpty() && AddGroupMembersActivity.this != null) {
                        openNoConnectionDialog(getString(R.string.add_members));
                    }
                }
            });
        }
    }

    private void openNoConnectionDialog(String title) {
        try {
            connectionErrorDialog.setConnectionErrorDialogListener(new ConnectionErrorDialog.ConnectionErrorDialogListener() {
                @Override
                public void onRetry() {
                    offset = 0;
                    hitGetUserListApi(user_id, group_id, offset, et_search_bar.getText().toString().trim());
                    connectionErrorDialog.dismiss();
                }

                @Override
                public void onBack() {
                    connectionErrorDialog.dismiss();
                    finish();
                }
            });
            connectionErrorDialog.show();
            connectionErrorDialog.setTitle(title);
        } catch (Exception e) {

        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }


    @Override
    public void onAddChatMemberMethodCallback(int position, PojoUserData item, String type) {
        switch (type) {
            case "add_group_members":
                hitAddGroupMemberApi(user_id, group_id, item.userId, position);
                break;

            case "assign_admin":
                break;

            case "remove_member":
                break;
        }
    }

    private void hitAddGroupMemberApi(String user_id, final String group_id, String memberId, final int position) {
        commonFunctions.openProgressDialog();
        Call<PojoNoDataResponse> call = apiService.addMemberToGroup(user_id, group_id, memberId);
        memberAdded = true;
        call.enqueue(new Callback<PojoNoDataResponse>() {
            @Override
            public void onResponse(Call<PojoNoDataResponse> call, Response<PojoNoDataResponse> response) {
                String message = "";
                if (response != null && response.body() != null) {
                    message = response.body().message;
                    userList.remove(position);
                    addChatMemberListAdapter.notifyDataSetChanged();
                }
                commonFunctions.closeProgressDialog();
            }

            @Override
            public void onFailure(Call<PojoNoDataResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(AddGroupMembersActivity.this, t.toString(),
                        Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                commonFunctions.closeProgressDialog();

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (memberAdded) {
            setResult(RESULT_OK);
        }
    }

}