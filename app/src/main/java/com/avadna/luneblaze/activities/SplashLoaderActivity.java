package com.avadna.luneblaze.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.provider.Settings;

import com.google.android.material.tabs.TabLayout;

import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.firebasenotifications.config.Config;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.pojo.PojoGetFriendRequestResponse;
import com.avadna.luneblaze.pojo.PojoGetNotificationListResponse;
import com.avadna.luneblaze.pojo.PojoGetNotificationListResponseData;
import com.avadna.luneblaze.pojo.PojoGetSuggestedFriendListResponse;
import com.avadna.luneblaze.pojo.PojoSentRequestListResponse;
import com.avadna.luneblaze.pojo.PojoUpdateTokenResponse;
import com.avadna.luneblaze.pojo.pojoChat.PojoConversationListItem;
import com.avadna.luneblaze.pojo.pojoChat.PojoGetAllMessageListResponse;
import com.avadna.luneblaze.pojo.pojoNewsFeed.PojoGetNewsFeedResponse;
import com.avadna.luneblaze.pojo.pojoNewsFeed.PojoGetNewsFeedResponseData;
import com.avadna.luneblaze.pojo.pojoProfileInfo.PojoProfileInfoResponse;
import com.avadna.luneblaze.pojo.pojoSettings.PojoUserSettingsResponse;
import com.avadna.luneblaze.pojo.pojoSettings.PojoUserSettingsResponseData;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashLoaderActivity extends AppBaseActivity {

    LinearLayout ll_splash_wrapper;
    RelativeLayout rl_images_wrapper;
    ImageView iv_logo_text;

    PreferenceUtils preferenceUtils;
    ApiInterface apiService;
    CommonFunctions commonFunctions;
    String user_id;
    List<PojoGetNewsFeedResponseData> newsFeedList;
    int API_COUNT = 9;

    int counter = 0;

    TabLayout tl_pager_dots;

    Handler radioButtonHandler;
    Runnable radioButtonRunnable;
    int onlineStatusDelay = 400;

    boolean isInForeground = false;


    @Override
    protected void onResume() {
        super.onResume();
        isInForeground = true;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash_loader);
        preferenceUtils = new PreferenceUtils(this);
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
        user_id = preferenceUtils.get_user_id();
        radioButtonHandler = new Handler();
        commonFunctions = new CommonFunctions(this);
        initViews();
        showAnimations();
        initApis();
    }

    private void initApis() {
        if (user_id.equals(AppKeys.GUEST_USER_ID)) {
            API_COUNT = 1;
            hitGetNewsFeedApi(user_id, "newsfeed", "all", 0);

        } else {
            API_COUNT = 9;
            hitGetAllMsgListApi(user_id, "", "");
            hitGetNotificationsApi(user_id, 0);
            hitGetUserProfileApi(user_id, user_id, "dashboard");
            hitGetUserPrivacySettingApi(user_id, "privacy");
            hitSuggestFriendsListApi(user_id, "0");
            hitGetReceivedFriendRequestApi(user_id);
            hitGetSentFriendRequestApi(user_id);
            hitUpdateTokenApi();
            hitGetNewsFeedApi(user_id, "newsfeed", "all", 0);
        }
    }

    private void initViews() {
        ll_splash_wrapper = (LinearLayout) findViewById(R.id.ll_splash_wrapper);
        rl_images_wrapper = (RelativeLayout) findViewById(R.id.rl_images_wrapper);
        iv_logo_text = (ImageView) findViewById(R.id.iv_logo_text);

        tl_pager_dots = (TabLayout) findViewById(R.id.tl_pager_dots);

        tl_pager_dots.addTab(tl_pager_dots.newTab());
        tl_pager_dots.addTab(tl_pager_dots.newTab());
        tl_pager_dots.addTab(tl_pager_dots.newTab());
        tl_pager_dots.addTab(tl_pager_dots.newTab());
        tl_pager_dots.addTab(tl_pager_dots.newTab());

        radioButtonRunnable = new Runnable() {
            public void run() {
                //buttonList.get(counter % 5).setChecked(true);
                TabLayout.Tab tab = tl_pager_dots.getTabAt(counter % 5);
                tab.select();
                counter++;
                radioButtonHandler.postDelayed(this, onlineStatusDelay);
            }
        };
        radioButtonHandler.postDelayed(radioButtonRunnable, 500);

    }


    private void showAnimations() {
        ll_splash_wrapper.setVisibility(View.VISIBLE);

        AnimationSet animSet = new AnimationSet(true);
        animSet.setInterpolator(new LinearInterpolator());
        final RotateAnimation animRotate = new RotateAnimation(0.0f, 360.0f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);

        animRotate.setDuration(8000);
        animRotate.setRepeatCount(Animation.INFINITE);
        animRotate.setRepeatMode(Animation.INFINITE);


        /*ObjectAnimator animation = ObjectAnimator.ofFloat(rl_images_wrapper,
                "rotationY", 0.0f, 360f);
        animation.setDuration(3000);
        animation.setRepeatMode(ValueAnimator.RESTART);
        animation.start();*/

        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.4f);
        alphaAnimation.setDuration(2000);
        alphaAnimation.setRepeatCount(Animation.INFINITE);
        alphaAnimation.setRepeatMode(Animation.REVERSE);

        // animSet.addAnimation(animRotate);
        animSet.addAnimation(alphaAnimation);
       /* rl_images_wrapper.startAnimation(animSet);
        iv_logo_text.startAnimation(alphaAnimation);*/
    }


    private void hitUpdateTokenApi() {
        String android_id = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        String regId = pref.getString("regId", null);
        if (regId == null) {
            regId = "";
        }
        Call<PojoUpdateTokenResponse> call = apiService.updateFcmToken(user_id, android_id, "A", regId);

        call.enqueue(new Callback<PojoUpdateTokenResponse>() {
            @Override
            public void onResponse(Call<PojoUpdateTokenResponse> call, Response<PojoUpdateTokenResponse> response) {
                String message = "";
                API_COUNT--;
                checkGoNext();

                if (response != null) {
                    message = response.body().message;

                    //hitGetLoginDevicesApi(user_id, email);
                }
            }

            @Override
            public void onFailure(Call<PojoUpdateTokenResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(getApplicationContext(), t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                API_COUNT--;
                checkGoNext();
            }
        });
    }


    private void hitGetAllMsgListApi(final String user_id, final String last_message_id, final String query) {
        Call<PojoGetAllMessageListResponse> call = apiService.getUserAllMsgList(user_id,
                last_message_id, query);

        call.enqueue(new Callback<PojoGetAllMessageListResponse>() {
            @Override
            public void onResponse(Call<PojoGetAllMessageListResponse> call, Response<PojoGetAllMessageListResponse> response) {
                if (response.body() != null) {
                    List<PojoConversationListItem> conversationList = new ArrayList<>();

                    String message = response.body().message;
                    for (int i = 0; i < response.body().data.messages.size(); i++) {
                        response.body().data.messages.get(i).isOnline = "0";
                    }

                    for (int i = 0; i < response.body().data.messages.size(); i++) {
                        if (!response.body().data.messages.get(i).conversationId.isEmpty()) {
                            conversationList.add(response.body().data.messages.get(i));
                        }
                    }

                    if (response.body().status == 1) {
                        preferenceUtils.saveAllMessageList(conversationList);

                    } else {
                        commonFunctions.setToastMessage(SplashLoaderActivity.this, "" + message, Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                    }
                }
                API_COUNT--;
                checkGoNext();
            }

            @Override
            public void onFailure(Call<PojoGetAllMessageListResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(getApplicationContext(), t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                API_COUNT--;
                checkGoNext();
            }
        });

    }

    private void hitGetNotificationsApi(String user_id, final int offset) {
        Call<PojoGetNotificationListResponse> call = apiService.getNotificationList(user_id, String.valueOf(offset), "0");
        call.enqueue(new Callback<PojoGetNotificationListResponse>() {
            @Override
            public void onResponse(Call<PojoGetNotificationListResponse> call, Response<PojoGetNotificationListResponse> response) {
                if (response.body() != null) {
                    String message = response.body().message;
                    if (response.body().data != null) {
                        List<PojoGetNotificationListResponseData> notifList = new ArrayList<>();
                        for (int i = 0; i < response.body().data.size(); i++) {
                            if (response.body().data.get(i).action.equals("venue_changed_confirmation")
                                    && response.body().data.get(i).seen.equals("1")) {
                            } else {
                                notifList.add(response.body().data.get(i));
                            }
                        }
                        preferenceUtils.saveNotificationList(notifList);
                    } else {
                        commonFunctions.setToastMessage(SplashLoaderActivity.this,
                                "" + message, Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                    }
                }
                API_COUNT--;
                checkGoNext();
            }

            @Override
            public void onFailure(Call<PojoGetNotificationListResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(getApplicationContext(), t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                API_COUNT--;
                checkGoNext();
            }
        });
    }


    private void hitGetNewsFeedApi(String user_id, String get, final String filter, final int offset) {
        Call<PojoGetNewsFeedResponse> call = apiService.getNewsFeed(user_id, get, filter,
                String.valueOf(offset));

        call.enqueue(new Callback<PojoGetNewsFeedResponse>() {
            @Override
            public void onResponse(Call<PojoGetNewsFeedResponse> call, Response<PojoGetNewsFeedResponse> response) {
                if (response.body() != null) {
                    String message = response.body().message;
                    if (response.body().data != null) {
                        newsFeedList = new ArrayList<>();

                        List<PojoGetNewsFeedResponseData> filteredList = new ArrayList<>();

                        for (int i = 0; i < response.body().data.size(); i++) {
                            PojoGetNewsFeedResponseData currentItem = response.body().data.get(i);


                            if (currentItem.postId != null && !currentItem.postId.isEmpty()) {
                                switch (currentItem.postType) {
                                    case "session_shared":
                                    case "session_attend":
                                    case "session":
                                        if (currentItem.origin.sessionsId == null
                                                || currentItem.origin.sessionsId.isEmpty()) {
                                            continue;
                                        }
                                        break;

                                    case "article":
                                    case "article_shared":
                                    case "article_like":
                                    case "article_comment":
                                        if (currentItem.origin.articlesId == null
                                                || currentItem.origin.articlesId.isEmpty()) {
                                            continue;
                                        }
                                        break;

                                    case "question":
                                    case "question_shared":
                                        if (currentItem.origin.sessionsQaId == null
                                                || currentItem.origin.sessionsQaId.isEmpty()) {
                                            continue;
                                        }
                                        break;

                                    case "answer_shared":
                                        if (currentItem.origin.sessionsQaId == null
                                                || currentItem.origin.sessionsQaId.isEmpty()
                                                || currentItem.answer.sessionsQaId == null) {
                                            continue;
                                        }
                                        break;

                                    case "post_like":
                                    case "post_comment":
                                    case "post_shared":
                                        //todo revert after post item is fixed
                                        if (currentItem.origin != null) {
                                            if (currentItem.origin.postId == null
                                                    || currentItem.origin.postId.isEmpty()) {
                                                if (currentItem.origin.articlesId == null) {
                                                    continue;
                                                }
                                            }
                                            if (currentItem.origin.origin != null && currentItem.origin.origin.postId == null) {
                                                continue;
                                            }
                                        }
                                        break;

                                    case "interest_shared":
                                        if (currentItem.origin.interestId == null
                                                || currentItem.origin.interestId.isEmpty()) {
                                            continue;
                                        }
                                        break;

                                    case "venue_shared":
                                        if (currentItem.origin.venueId == null
                                                || currentItem.origin.venueId.isEmpty()) {
                                            continue;
                                        }
                                        break;


                                    case "shared":
                                        if (currentItem.origin.postType.equals("poll")
                                                || currentItem.origin.postType.equals("photos")
                                                || currentItem.origin.postType.equals("")) {
                                            if (currentItem.origin.postId == null
                                                    || currentItem.origin.postId.isEmpty()) {
                                                continue;
                                            }
                                        }
                                        break;
                                }
                                filteredList.add(currentItem);
                            }
                        }
                        newsFeedList.addAll(filteredList);

                        if (newsFeedList.size() >= 9) {
                            PojoGetNewsFeedResponseData suggested = new PojoGetNewsFeedResponseData();
                            suggested.postType = "suggested_users";
                            newsFeedList.add(9, suggested);
                        }

                        preferenceUtils.saveNewsFeedList(newsFeedList);

                    } else {
                        commonFunctions.setToastMessage(SplashLoaderActivity.this,
                                "" + message, Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                    }
                }
                API_COUNT--;
                checkGoNext();
            }

            @Override
            public void onFailure(Call<PojoGetNewsFeedResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(SplashLoaderActivity.this, t.toString(),
                        Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                API_COUNT--;
                checkGoNext();
            }
        });
    }

    private void hitGetUserProfileApi(String user_id, String targetUserId, String type) {
        Call<PojoProfileInfoResponse> call = apiService.getUserProfile(user_id, targetUserId, type);
        call.enqueue(new Callback<PojoProfileInfoResponse>() {
            @Override
            public void onResponse(Call<PojoProfileInfoResponse> call, Response<PojoProfileInfoResponse> response) {
                if (response != null && response.body() != null) {
                    String message = response.body().message;
                    if (response.body().data != null) {
                        preferenceUtils.saveUserProfileInfo(response.body().data);
                        preferenceUtils.saveNewNotificationCount(response.body().data.profile.userLiveNotificationsCounter);

                        preferenceUtils.saveUnseenConvCount(response.body().data.profile.userLiveMessagesCounter);

                    } else {
                        commonFunctions.setToastMessage(getApplicationContext(), getString(R.string.you_cannot_view_this_profile),
                                Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                    }
                }
                API_COUNT--;
                checkGoNext();

            }

            @Override
            public void onFailure(Call<PojoProfileInfoResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(getApplicationContext(), t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                //  cl_parent.setVisibility(View.VISIBLE);
                //  pb_loading_content.setVisibility(View.GONE);
                API_COUNT--;
                checkGoNext();
            }
        });
    }

    public void checkGoNext() {
        if (API_COUNT <= 0) {
            Intent newsFeedIntent = new Intent(SplashLoaderActivity.this, MainActivity.class);
            startActivity(newsFeedIntent);
            finish();
        }
    }

    private void hitGetUserPrivacySettingApi(final String user_id, String type) {
        Call<PojoUserSettingsResponse> call = apiService.getUserSettings(user_id, type);
        call.enqueue(new Callback<PojoUserSettingsResponse>() {
            @Override
            public void onResponse(Call<PojoUserSettingsResponse> call, Response<PojoUserSettingsResponse> response) {
                if (response != null && response.body() != null) {
                    String message = response.body().message;
                    if (response.body().data != null) {
                        PojoUserSettingsResponseData pojoUserSettingsResponseData;
                        pojoUserSettingsResponseData = response.body().data;
                        preferenceUtils.saveUserSettings(pojoUserSettingsResponseData);

                    } else {
                        commonFunctions.setToastMessage(getApplicationContext(), "" + message, Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                    }
                }
                API_COUNT--;
                checkGoNext();
            }

            @Override
            public void onFailure(Call<PojoUserSettingsResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(getApplicationContext(), t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                API_COUNT--;
                checkGoNext();
            }
        });
    }

    private void hitGetReceivedFriendRequestApi(String user_id) {
        Call<PojoGetFriendRequestResponse> call = apiService.getFriendRequestList(user_id);
        call.enqueue(new Callback<PojoGetFriendRequestResponse>() {
            @Override
            public void onResponse(Call<PojoGetFriendRequestResponse> call, Response<PojoGetFriendRequestResponse> response) {
                if (response.body() != null) {
                    String message = response.body().message;
                    if (response.body().data != null) {
                        preferenceUtils.saveNewFriendReqCount(response.body().data.size());
                        preferenceUtils.saveReceivedRequestCount(response.body().data.size());
                    } else {
                        commonFunctions.setToastMessage(getApplicationContext(), "" + message,
                                Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                    }
                }
                API_COUNT--;
                checkGoNext();
            }

            @Override
            public void onFailure(Call<PojoGetFriendRequestResponse> call, Throwable t) {
                commonFunctions.setToastMessage(getApplicationContext(), t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                API_COUNT--;
                checkGoNext();

                // Log error here since request failed
                //   commonFunctions.setToastMessage(getActivity(), t.toString(), Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);
            }
        });
    }

    private void hitGetSentFriendRequestApi(String user_id) {
        Call<PojoSentRequestListResponse> call = apiService.getSentFriendRequestList(user_id);
        call.enqueue(new Callback<PojoSentRequestListResponse>() {
            @Override
            public void onResponse(Call<PojoSentRequestListResponse> call, Response<PojoSentRequestListResponse> response) {
                if (response.body() != null) {
                    String message = response.body().message;
                    if (response.body().data != null) {
                        preferenceUtils.saveSentRequestCount(response.body().data.size());

                    } else {
                        commonFunctions.setToastMessage(getApplicationContext(), "" + message,
                                Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                    }
                }
                API_COUNT--;
                checkGoNext();
            }

            @Override
            public void onFailure(Call<PojoSentRequestListResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(getApplicationContext(), t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                API_COUNT--;
                checkGoNext();
            }
        });
    }

    private void hitSuggestFriendsListApi(String user_id, String page) {
        Call<PojoGetSuggestedFriendListResponse> call = apiService.getSuggestedFriendListApi(user_id, page);
        call.enqueue(new Callback<PojoGetSuggestedFriendListResponse>() {
            @Override
            public void onResponse(Call<PojoGetSuggestedFriendListResponse> call, Response<PojoGetSuggestedFriendListResponse> response) {
                if (response != null) {
                    String message = response.body().message;
                    if (response.body().data != null) {
                        preferenceUtils.saveSuggestedUserList(response.body().data);
                    } else {
                        commonFunctions.setToastMessage(SplashLoaderActivity.this, ""
                                + message, Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                    }
                }
                API_COUNT--;
                checkGoNext();
            }

            @Override
            public void onFailure(Call<PojoGetSuggestedFriendListResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(getApplicationContext(), t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                API_COUNT--;
                checkGoNext();

            }
        });
    }


    @Override
    protected void onPause() {
        super.onPause();
        isInForeground = false;

        radioButtonHandler.removeCallbacks(radioButtonRunnable);
        //    unsentMsgHandler.removeCallbacks(unsentMsgRunnable);

    }

}
