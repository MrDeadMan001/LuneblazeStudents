package com.avadna.luneblaze.activities.profileInfo;

import android.animation.Animator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.adapters.SuggestedFriendsAdapter;
import com.avadna.luneblaze.activities.AppBaseActivity;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.pojo.PojoGetFriendsListResponse;
import com.avadna.luneblaze.pojo.PojoGetUserOnlineStatusResponse;
import com.avadna.luneblaze.pojo.pojoQuestion.PojoUserData;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FriendListActivity extends AppBaseActivity {

    PreferenceUtils preferenceUtils;
    CommonFunctions commonFunctions;
    ApiInterface apiService;
    String user_id;
    String targetUserId;


    View.OnClickListener onClickListener;
    Toolbar tl_top_bar;
    ImageButton ib_back, ib_search;
    TextView tv_title;

    EditText et_search_bar;
    boolean searchBarExpanded = false;
    int offset = 0;
    boolean isGetFriendApiCalled = false;

    RecyclerView rv_friends_list;
    SuggestedFriendsAdapter suggestedFriendsAdapter;
    LinearLayoutManager friendListLayoutManager;
    List<PojoUserData> friendList;

    ProgressBar pb_loading_users;

    LinearLayout ll_content_wrapper;

    RelativeLayout rl_search_wrapper;
    ImageButton ib_back_gray, ib_search_gray;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list);
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
        preferenceUtils = new PreferenceUtils(this);
        commonFunctions=new CommonFunctions(this);
        user_id = preferenceUtils.get_user_id();
        targetUserId = getIntent().getStringExtra("target_user_id");

        if (targetUserId == null || targetUserId.isEmpty()) {
            targetUserId = user_id;
        }
        setUpActionBar();
        initViews();
        initClickListeners();
        setClickListeners();
        pb_loading_users.setVisibility(View.VISIBLE);

        hitGetFriendListApi(user_id, targetUserId, "", offset);


    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void setUpActionBar() {
        tl_top_bar = findViewById(R.id.tl_top_bar);
        setSupportActionBar(tl_top_bar);
        if (tl_top_bar != null) {
            // Show the Up button in the action bar.
            // actionBar.setDisplayHomeAsUpEnabled(true);
            //   actionBar.setHomeButtonEnabled(true);
            tl_top_bar.setTitle(getString(R.string.fellows));
            tl_top_bar.setBackgroundDrawable(new ColorDrawable(ContextCompat
                    .getColor(FriendListActivity.this, R.color.action_bar_color)));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(ContextCompat
                        .getColor(FriendListActivity.this, R.color.status_bar_color));
            }
        }
    }

    private void initViews() {

        pb_loading_users = (ProgressBar) findViewById(R.id.pb_loading_users);

        ib_back = (ImageButton) findViewById(R.id.ib_back);
        ib_search = (ImageButton) findViewById(R.id.ib_search);
        tv_title = (TextView) findViewById(R.id.tv_title);

        // ib_back = (ImageButton) findViewById(R.id.ib_back);
        ll_content_wrapper = (LinearLayout) findViewById(R.id.ll_content_wrapper);
        et_search_bar = (EditText) findViewById(R.id.et_search_bar);

        rl_search_wrapper = (RelativeLayout) findViewById(R.id.rl_search_wrapper);
        ib_back_gray = (ImageButton) findViewById(R.id.ib_back_gray);
        ib_search_gray = (ImageButton) findViewById(R.id.ib_search_gray);

        et_search_bar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                offset = 0;
                hitGetFriendListApi(user_id, targetUserId, s.toString(), offset);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        // setUpSearchView();

        setUpMyFriendsList();
    }




    private void setUpMyFriendsList() {
        friendList = new ArrayList<>();
        rv_friends_list = (RecyclerView) findViewById(R.id.rv_friends_list);
        suggestedFriendsAdapter = new SuggestedFriendsAdapter(FriendListActivity.this, friendList, "ver");
        friendListLayoutManager = new LinearLayoutManager(FriendListActivity.this,
                RecyclerView.VERTICAL, false);
        rv_friends_list.setLayoutManager(friendListLayoutManager);
        rv_friends_list.setAdapter(suggestedFriendsAdapter);

        rv_friends_list.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                //    Log.d("Scroll info", "dx = " + dy);
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 5) {
                    if (!isGetFriendApiCalled && friendListLayoutManager.findLastVisibleItemPosition() > friendList.size() - 3) {
                        hitGetFriendListApi(user_id, targetUserId,
                                et_search_bar.getText().toString().trim(), ++offset);
                    }
                }
            }
        });
    }

    private void initClickListeners() {
        onClickListener = new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.ib_back:
                        FriendListActivity.this.onBackPressed();
                        break;

                    case R.id.ib_search:
                        revealSearchAnim();
                        break;

                    case R.id.ib_back_gray:
                        hideSearchAnim();
                        break;

                    case R.id.ib_search_gray:
                        offset = 0;
                        hitGetFriendListApi(user_id, targetUserId, et_search_bar.getText().toString(), offset);
                        break;
                }
            }
        };
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void revealSearchAnim() {
        Animator anim;
        float Xcoor=ib_search.getX()+(ib_search.getWidth()/2);
        float Ycoor=ib_search.getY()+(ib_search.getHeight()/2);


        anim = ViewAnimationUtils.createCircularReveal(rl_search_wrapper, (int) Xcoor, (int) Ycoor, 0, 1000);
        anim.setDuration((long) 250);
        anim.start();
        rl_search_wrapper.setVisibility(View.VISIBLE);
        et_search_bar.requestFocus();
        if (et_search_bar.requestFocus()) {
            InputMethodManager imm = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(et_search_bar, InputMethodManager.SHOW_FORCED);
        }
      //  sv_search.setIconified(false);

       /* ImageView icon = sv_search.findViewById(android.support.v7.appcompat.R.id.search_button);
        icon.setColorFilter(Color.TRANSPARENT);*/

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void hideSearchAnim() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(et_search_bar.getWindowToken(), 0);

        Animator anim;
        float Xcoor=ib_search.getX()+(ib_search.getWidth()/2);
        float Ycoor=ib_search.getY()+(ib_search.getHeight()/2);
        anim = ViewAnimationUtils.createCircularReveal(rl_search_wrapper, (int) Xcoor, (int) Ycoor, 1000, 0);
        anim.setDuration((long) 250);
        anim.start();

        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                rl_search_wrapper.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }


    private void setClickListeners() {
        ib_back.setOnClickListener(onClickListener);
        ib_search.setOnClickListener(onClickListener);
        ib_back_gray.setOnClickListener(onClickListener);
        ib_search_gray.setOnClickListener(onClickListener);
    }

    private void hitGetFriendListApi(String user_id, String target_user_id, String query, final int offset) {
        Call<PojoGetFriendsListResponse> call = apiService.getFriendListApi(user_id, target_user_id, query,
                "0", "1", String.valueOf(offset));
        if (!isGetFriendApiCalled) {
            isGetFriendApiCalled = true;
            call.enqueue(new Callback<PojoGetFriendsListResponse>() {
                @Override
                public void onResponse(Call<PojoGetFriendsListResponse> call, Response<PojoGetFriendsListResponse> response) {
                    String message = response.body().message;
                    if (response.body().data != null) {
                        if (offset == 0) {
                            friendList.clear();
                            tv_title.setText(response.body().data.noOfFriend+" "+getString(R.string.fellows));
                        }

                        for (int i = 0; i < response.body().data.friends.size(); i++) {
                            response.body().data.friends.get(i).selected = false;
                        }

                        friendList.addAll(response.body().data.friends);

                        List<String> userList = new ArrayList<>();
                        for (int j = 0; j < friendList.size(); j++) {
                            userList.add(friendList.get(j).userId);
                        }
                        hitGetUserOnlineStatusApi(userList);

                     /*   for (int i = 0; i < friendList.size(); i++) {
                            //set all new as false as this field is not received from api and is null
                            for (int j = 0; j < addedUserList.size(); j++) {
                                //set the previously added users as selected
                                if (addedUserList.get(j).userId.equals(friendList.get(i).userId)) {
                                    friendList.get(i).selected = addedUserList.get(j).selected;
                                }
                            }
                        }*/

                        suggestedFriendsAdapter.notifyDataSetChanged();
                    } else {
                        commonFunctions.setToastMessage(FriendListActivity.this,
                                "" + message, Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);
                    }
                    pb_loading_users.setVisibility(View.GONE);

                    isGetFriendApiCalled = false;

                }

                @Override
                public void onFailure(Call<PojoGetFriendsListResponse> call, Throwable t) {
                    // Log error here since request failed
                    commonFunctions.setToastMessage(FriendListActivity.this, t.toString(),
                            Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);
                    isGetFriendApiCalled = false;
                    pb_loading_users.setVisibility(View.GONE);

                }
            });
        }

    }

    private void hitGetUserOnlineStatusApi(List<String> user_id_list) {

        HashMap<String, String> userIdMap = new HashMap<>();
        for (int i = 0; i < user_id_list.size(); i++) {
            userIdMap.put("user_ids[" + i + "]", user_id_list.get(i));
        }

        Call<PojoGetUserOnlineStatusResponse> call = apiService.getUserOnlineStatus(userIdMap);
        call.enqueue(new Callback<PojoGetUserOnlineStatusResponse>() {
            @Override
            public void onResponse(Call<PojoGetUserOnlineStatusResponse> call,
                                   Response<PojoGetUserOnlineStatusResponse> response) {
                if (response.body() != null) {
                    String message = response.body().message;
                    if (response.body().data != null) {
                        for (int i = 0; i < friendList.size(); i++) {
                            for (int j = 0; j < response.body().data.size(); j++) {
                                if (friendList.get(i).userId.equals(response.body().data.get(j).userId)) {
                                    friendList.get(i).isOnline = response.body().data.get(j).isOnline;
                                }
                            }
                        }
                        suggestedFriendsAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onFailure(Call<PojoGetUserOnlineStatusResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(FriendListActivity.this, t.toString(),
                        Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();

    }


  /*  @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.friend_list_activity_menu, menu);
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        if (searchItem != null) {
            searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
            SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
            searchView.setSearchableInfo(manager.getSearchableInfo(getComponentName()));
            searchView.setOnCloseListener(new SearchView.OnCloseListener() {
                @Override
                public boolean onClose() {
                    //some operation

                    return true;
                }
            });
            searchView.setOnSearchClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //some operation
                }
            });

           *//* EditText searchPlate = (EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
            searchPlate.setHint("Search");
            View searchPlateView = searchView.findViewById(android.support.v7.appcompat.R.id.search_plate);
            searchPlateView.setBackgroundColor(ContextCompat.getColor(this, android.R.color.white));*//*
            // use this method for search process

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    // use this method when query submitted
                    commonFunctions.setToastMessage(FriendListActivity.this, query, Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    offset = 0;
                    hitGetFriendListApi(user_id, targetUserId, newText, offset);
                    return false;
                }
            });


        }
        return super.onCreateOptionsMenu(menu);
    }*/

  /*  @Override
    public void onBackPressed() {
        if (!searchView.isIconified()) {
            searchView.setIconified(true);
            //  findViewById(R.id.default_title).setVisibility(View.VISIBLE);
        } else {
            super.onBackPressed();
        }
    }*/

}
