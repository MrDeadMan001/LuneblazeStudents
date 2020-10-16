package com.avadna.luneblaze.activities;

import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.ActionBar;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.adapters.InviteListAdapter;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.ConnectionErrorDialog;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.pojo.PojoInvitesListResponse;
import com.avadna.luneblaze.pojo.PojoInvitesListResponseData;
import com.avadna.luneblaze.pojo.pojoInvite.PojoInviteFriendResponse;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InviteActivity extends AppBaseActivity implements InviteListAdapter.InviteListAdapterCallback {
    Toolbar tl_top_bar;
    ActionBar actionBar;

    ApiInterface apiService;
    PreferenceUtils preferenceUtils;
    CommonFunctions commonFunctions;
    String user_id;

    RecyclerView rv_list;
    LinearLayoutManager linearLayoutManager;
    List<PojoInvitesListResponseData> userList;
    InviteListAdapter inviteListAdapter;

    String list_type = AppKeys.INTEREST_INVITATION_KEY;
    String inviteItemId = "3";
    String inviteType = "invite_interest";

    EditText et_search_bar;
    ImageButton ib_back_gray, ib_close_gray;
    ProgressBar pb_loading_content;

    int offset = 0;
    boolean isGetUserListApiCalled = false;
    boolean listEndReached = false;




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
        setUpActionBar();
        list_type = getIntent().getStringExtra("userType");
        inviteItemId = getIntent().getStringExtra("id");
        initViews();
        initList();
        offset = 0;
        if (list_type.equals(AppKeys.ANSWER_INVITATION_KEY)) {
            hitGetAnswerInviteListApi(user_id, list_type, inviteItemId, "", offset);
        } else if (list_type.equals(AppKeys.INTEREST_INVITATION_KEY) ||
                list_type.equals(AppKeys.VENUE_INVITATION_KEY)) {
            hitGetVenueInterestInviteListApi(user_id, list_type, inviteItemId, "", offset);
        } else if (list_type.equals(AppKeys.SESSION_CONDUCT_INVITATION_KEY) ||
                list_type.equals(AppKeys.SESSION_ATTEND_INVITATION_KEY)) {
            hitSessionInviteListApi(user_id, list_type, inviteItemId, "", offset);
        }
    }

    private void setUpActionBar() {
        tl_top_bar = findViewById(R.id.tl_top_bar);
        setSupportActionBar(tl_top_bar);
        actionBar = getSupportActionBar();
        if (tl_top_bar != null) {
            // Show the Up button in the action bar.
            //    actionBar.setDisplayHomeAsUpEnabled(true);
            tl_top_bar.setBackgroundDrawable(new ColorDrawable(ContextCompat
                    .getColor(InviteActivity.this, R.color.action_bar_color)));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(ContextCompat
                        .getColor(InviteActivity.this, R.color.status_bar_color));
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
        pb_loading_content = (ProgressBar) findViewById(R.id.pb_loading_content);
        pb_loading_content.setVisibility(View.GONE);
        ib_close_gray = (ImageButton) findViewById(R.id.ib_close_gray);
        ib_close_gray.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_search_bar.getText().clear();
             /*   offset = 0;
                userList.clear();
                inviteListAdapter.notifyDataSetChanged();
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
                inviteListAdapter.notifyDataSetChanged();
                pb_loading_content.setVisibility(View.GONE);
                listEndReached = false;
                inviteListAdapter.setShowNoMore(false);


                if (list_type.equals(AppKeys.ANSWER_INVITATION_KEY)) {
                    hitGetAnswerInviteListApi(user_id, list_type, inviteItemId, s.toString(), offset);
                } else if (list_type.equals(AppKeys.INTEREST_INVITATION_KEY) ||
                        list_type.equals(AppKeys.VENUE_INVITATION_KEY)) {
                    hitGetVenueInterestInviteListApi(user_id, list_type, inviteItemId, s.toString(), offset);
                } else if (list_type.equals(AppKeys.SESSION_CONDUCT_INVITATION_KEY) ||
                        list_type.equals(AppKeys.SESSION_ATTEND_INVITATION_KEY)) {
                    hitSessionInviteListApi(user_id, list_type, inviteItemId, s.toString(), offset);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


    }

    private void initList() {
        userList = new ArrayList<>();
        rv_list = (RecyclerView) findViewById(R.id.rv_list);
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rv_list.setLayoutManager(linearLayoutManager);
        inviteListAdapter = new InviteListAdapter(userList, InviteActivity.this);
        rv_list.setAdapter(inviteListAdapter);

        rv_list.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 5 && !isGetUserListApiCalled
                        && linearLayoutManager.findLastVisibleItemPosition() > userList.size() - 2
                        && !listEndReached) {
                    if (list_type.equals(AppKeys.ANSWER_INVITATION_KEY)) {
                        hitGetAnswerInviteListApi(user_id, list_type, inviteItemId,
                                et_search_bar.getText().toString(), ++offset);
                    } else if (list_type.equals(AppKeys.INTEREST_INVITATION_KEY) ||
                            list_type.equals(AppKeys.VENUE_INVITATION_KEY)) {
                        hitGetVenueInterestInviteListApi(user_id, list_type, inviteItemId,
                                et_search_bar.getText().toString(), ++offset);
                    } else if (list_type.equals(AppKeys.SESSION_CONDUCT_INVITATION_KEY) ||
                            list_type.equals(AppKeys.SESSION_ATTEND_INVITATION_KEY)) {
                        hitSessionInviteListApi(user_id, list_type, inviteItemId,
                                et_search_bar.getText().toString(), ++offset);
                    }
                }
            }
        });

      /*  if (list_type.equals(AppKeys.INTEREST_INVITATION_KEY) || list_type.equals(AppKeys.VENUE_INVITATION_KEY)) {
            hitGetVenueInterestInviteListApi(user_id, list_type, inviteItemId,"");
        }
        else if(list_type.equals(AppKeys.SESSION_CONDUCT_INVITATION_KEY) || list_type.equals(AppKeys.SESSION_ATTEND_INVITATION_KEY)){
            hitSessionInviteListApi(user_id,list_type,inviteItemId,"0");
        }*/
    }

    private void hitGetVenueInterestInviteListApi(final String user_id, String type, final String inviteItemId,
                                                  String query, final int offset) {
        Call<PojoInvitesListResponse> call = apiService.getVenueInterestInvitesList(user_id, type,
                inviteItemId, query, String.valueOf(offset));
        if (!isGetUserListApiCalled) {
            isGetUserListApiCalled = true;
            inviteListAdapter.setShowProgressBar(true);
            inviteListAdapter.setShowNoMore(false);

            call.enqueue(new Callback<PojoInvitesListResponse>() {
                @Override
                public void onResponse(Call<PojoInvitesListResponse> call, Response<PojoInvitesListResponse> response) {
                    pb_loading_content.setVisibility(View.GONE);

                    String message = "";
                    if (response != null && response.body() != null) {
                        message = response.body().message;
                        if (response.body().data != null) {
                            if (offset == 0) {
                                userList.clear();
                            }
                            if (response.body().data.size() == 0) {
                                listEndReached = true;
                                inviteListAdapter.setShowNoMore(true);
                            }
                            userList.addAll(response.body().data);
                            if (list_type.equals(AppKeys.INTEREST_INVITATION_KEY)) {
                                inviteListAdapter.setInviteType("invite_interest");
                            } else if (list_type.equals(AppKeys.VENUE_INVITATION_KEY)) {
                                inviteListAdapter.setInviteType("invite_venue");
                            }

                            inviteListAdapter.notifyDataSetChanged();
                        }
                        commonFunctions.setToastMessage(getApplicationContext(), message,
                                Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                    }
                    inviteListAdapter.setShowProgressBar(false);
                    isGetUserListApiCalled = false;
                }

                @Override
                public void onFailure(Call<PojoInvitesListResponse> call, Throwable t) {
                    // Log error here since request failed
                    inviteListAdapter.setShowProgressBar(false);

                    commonFunctions.setToastMessage(getApplicationContext(), t.toString(),
                            Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                    if (userList.isEmpty()) {
                        openNoConnectionDialog(getString(R.string.invite), "venue_interest", query);
                    }
                    isGetUserListApiCalled = false;

                }
            });
        }

    }

    private void hitGetAnswerInviteListApi(final String user_id, String type, final String inviteItemId,
                                           String query, final int offset) {
        Call<PojoInvitesListResponse> call = apiService.getAnswerInvitesList(user_id, type, inviteItemId,
                query, String.valueOf(offset));
        inviteListAdapter.setInviteType("invite_question");

        if (!isGetUserListApiCalled) {
            isGetUserListApiCalled = true;
            inviteListAdapter.setShowProgressBar(true);
            inviteListAdapter.setShowNoMore(false);

            call.enqueue(new Callback<PojoInvitesListResponse>() {
                @Override
                public void onResponse(Call<PojoInvitesListResponse> call, Response<PojoInvitesListResponse> response) {
                    pb_loading_content.setVisibility(View.GONE);
                    String message = "";
                    if (response != null && response.body() != null) {
                        message = response.body().message;
                        if (response.body().data != null) {
                            if (offset == 0) {
                                userList.clear();
                            }
                            if (response.body().data.size() == 0) {
                                listEndReached = true;
                                inviteListAdapter.setShowNoMore(true);
                            }
                            userList.addAll(response.body().data);
                            inviteListAdapter.notifyDataSetChanged();
                        }
                        commonFunctions.setToastMessage(getApplicationContext(), message,
                                Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                    }
                    isGetUserListApiCalled = false;
                    inviteListAdapter.setShowProgressBar(false);

                }

                @Override
                public void onFailure(Call<PojoInvitesListResponse> call, Throwable t) {
                    // Log error here since request failed
                    inviteListAdapter.setShowProgressBar(false);
                    commonFunctions.setToastMessage(getApplicationContext(), t.toString(),
                            Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                    if (userList.isEmpty()) {
                        openNoConnectionDialog(getString(R.string.invite), "answer", query);
                    }
                    isGetUserListApiCalled = false;

                }
            });
        }
    }


    private void hitSessionInviteListApi(final String user_id, String type, final String inviteItemId,
                                         String query, final int offset) {
        Call<PojoInvitesListResponse> call = apiService.getSessionInviteList(user_id, type, inviteItemId,
                query, String.valueOf(offset));
        if (!isGetUserListApiCalled) {
            isGetUserListApiCalled = true;
            inviteListAdapter.setShowProgressBar(true);
            inviteListAdapter.setShowNoMore(false);

            call.enqueue(new Callback<PojoInvitesListResponse>() {
                @Override
                public void onResponse(Call<PojoInvitesListResponse> call, Response<PojoInvitesListResponse> response) {
                    pb_loading_content.setVisibility(View.GONE);
                    String message = "";
                    if (response != null && response.body() != null) {
                        message = response.body().message;
                        if (response.body().data != null) {
                            if (offset == 0) {
                                userList.clear();
                                if (list_type.equals(AppKeys.SESSION_CONDUCT_INVITATION_KEY)) {
                                    inviteListAdapter.setInviteType("session_conduct_request");
                                } else if (list_type.equals(AppKeys.SESSION_ATTEND_INVITATION_KEY)) {
                                    inviteListAdapter.setInviteType("invite_session");
                                }
                            }
                            if (response.body().data.size() == 0) {
                                listEndReached = true;
                                inviteListAdapter.setShowNoMore(true);
                            }
                            userList.addAll(response.body().data);
                            inviteListAdapter.notifyDataSetChanged();
                        }

                        commonFunctions.setToastMessage(getApplicationContext(), message,
                                Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                    }
                    isGetUserListApiCalled = false;
                    inviteListAdapter.setShowProgressBar(false);


                }

                @Override
                public void onFailure(Call<PojoInvitesListResponse> call, Throwable t) {
                    // Log error here since request failed
                    inviteListAdapter.setShowProgressBar(false);

                    if (userList.isEmpty()) {
                        openNoConnectionDialog(getString(R.string.invite), "session", query);

                    }
                    commonFunctions.setToastMessage(getApplicationContext(), t.toString(),
                            Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                    isGetUserListApiCalled = false;

                }
            });
        }

    }

    private void openNoConnectionDialog(String title, String type, String query) {

        try {
            connectionErrorDialog.setConnectionErrorDialogListener(new ConnectionErrorDialog.ConnectionErrorDialogListener() {
                @Override
                public void onRetry() {
                    if (type.equals("venue_interest")) {
                        hitGetVenueInterestInviteListApi(user_id, list_type, inviteItemId, "", offset);
                    } else if (type.equals("session")) {
                        hitSessionInviteListApi(user_id, list_type, inviteItemId, "", offset);
                    } else if (type.equals("answer")) {
                        hitGetAnswerInviteListApi(user_id, list_type, inviteItemId, query, offset);

                    }
                    connectionErrorDialog.dismiss();
                }

                @Override
                public void onBack() {
                    connectionErrorDialog.dismiss();
                    finish();
                }
            });
            connectionErrorDialog.show();connectionErrorDialog.setTitle(title);
        }
        catch (Exception e){

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
    public void inviteListClickMethod(int position, PojoInvitesListResponseData pojoInvitesListResponseData, String inviteType) {
        if (userList.get(position).invitedStatus == 0) {
            userList.get(position).invitedStatus = 1;
            hitInviteFriendApi(user_id, inviteType, pojoInvitesListResponseData.userId, inviteItemId);
        }
        inviteListAdapter.notifyDataSetChanged();

    }

    private void hitInviteFriendApi(String user_id, String type, final String friend_id, String Id) {
        Call<PojoInviteFriendResponse> call = apiService.inviteFriends(user_id, type, friend_id, Id);
        call.enqueue(new Callback<PojoInviteFriendResponse>() {
            @Override
            public void onResponse(Call<PojoInviteFriendResponse> call, Response<PojoInviteFriendResponse> response) {
                String message = "";
                if (response != null) {
                    message = response.body().message;
                    commonFunctions.setToastMessage(InviteActivity.this, message, Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                }
            }

            @Override
            public void onFailure(Call<PojoInviteFriendResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(InviteActivity.this, t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
            }
        });
    }
}
