package com.avadna.luneblaze.activities;

import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;

import androidx.core.content.ContextCompat;
import androidx.appcompat.app.ActionBar;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.activities.article.SharedArticleActivity;
import com.avadna.luneblaze.activities.normalpost.NormalPostActivity;
import com.avadna.luneblaze.activities.normalpost.SharedNormalPostActivity;
import com.avadna.luneblaze.activities.polls.PollActivity;
import com.avadna.luneblaze.activities.polls.SharedPollActivity;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.ConnectionErrorDialog;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.pojo.pojonormalpost.PojoNormalPostResponse;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;
import com.avadna.luneblaze.utils.NotificationUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostLoaderActivity extends AppBaseActivity {
    ActionBar actionBar;
    ApiInterface apiService;
    PreferenceUtils preferenceUtils;
    CommonFunctions commonFunctions;
    String post_id;
    String comment_id;
    String user_id;
    int notifId;
    Intent intent;

    String notification_id;

    Response<PojoNormalPostResponse> globalResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_loader);
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
        preferenceUtils = new PreferenceUtils(this);
        commonFunctions = new CommonFunctions(this);
        user_id = preferenceUtils.get_user_id();

        post_id = getIntent().getStringExtra(AppKeys.POST_ID);
       /* if (post_id == null) {
            post_id = "0";
        }*/
        comment_id = getIntent().getStringExtra(AppKeys.COMMENT_ID);
      /*  if (comment_id == null) {
            comment_id = "0";
        }*/

        notifId = getIntent().getIntExtra("notifId", 999999);
        dismissNotification();

        notification_id = getIntent().getStringExtra(AppKeys.NOTIFICATION_ID);
        if (notification_id != null) {
            commonFunctions.hitNotificationSeenApi(user_id, notification_id);
        }

        setUpActionBar();
        if (comment_id == null || comment_id.isEmpty()) {
            hitGetNormalPostApi(user_id, post_id, "0", "prev", "0", true);
        } else {
            hitGetNormalPostApi(user_id, post_id, comment_id, "prev", "1", true);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (globalResponse != null) {
            handleApiResponse(globalResponse);
            globalResponse = null;
        }
    }

    private void dismissNotification() {
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.cancel(notifId);

    }

    private void setUpActionBar() {
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setTitle("");
            actionBar.setBackgroundDrawable(new ColorDrawable(ContextCompat
                    .getColor(PostLoaderActivity.this, R.color.action_bar_color)));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(ContextCompat
                        .getColor(PostLoaderActivity.this, R.color.status_bar_color));
            }
        }
    }


    private void hitGetNormalPostApi(String user_id, final String post_id, final String comment_id,
                                     final String order, String target, final boolean scrollToEnd) {
        Call<PojoNormalPostResponse> call = apiService.getNormalPostData(user_id, post_id,
                comment_id, order, target);
        call.enqueue(new Callback<PojoNormalPostResponse>() {
            @Override
            public void onResponse(Call<PojoNormalPostResponse> call, Response<PojoNormalPostResponse> response) {

                String message = "";
                if (response != null && response.body() != null && response.body().data != null) {
                    if (NotificationUtils.isAppIsInBackground(PostLoaderActivity.this)) {
                        globalResponse = response;
                    } else {
                        handleApiResponse(response);
                    }
                }
            }

            @Override
            public void onFailure(Call<PojoNormalPostResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(PostLoaderActivity.this, t.toString(),
                        Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                openNoConnectionDialog(getString(R.string.post), comment_id);
            }
        });
    }

    public void handleApiResponse(Response<PojoNormalPostResponse> response) {
        // if (true) {
        if (response.body().data.post.postId.isEmpty()) {
            commonFunctions.showNotFoundDialog(PostLoaderActivity.this);
        } else {
            switch (response.body().data.post.postType) {
                case "photos":
                case "":
                case "post_like":
                case "organisation_post":
                case "ad":
                case "post_comment":
                    if (response.body().data.post.origin != null) {
                        if (response.body().data.post.origin.poll != null) {
                            openSharedPoll(response.body());
                            break;
                        }
                        if (response.body().data.post.origin.articlesId != null) {
                            openSharedArticle(response.body());
                            break;
                        }
                        openSharedNormalPost(response.body());
                        break;
                    }

                    if (response.body().data.post.poll != null) {
                        openPoll(response.body());
                        break;
                    }

                    openNormalPost(response.body());
                    break;

                case "poll":
                case "poll_vote":
                    openPoll(response.body());
                    break;


                case "shared":
                    if (response.body().data.post.origin.postType.equals("poll")) {
                        openSharedPoll(response.body());
                        break;
                    }
                    if (response.body().data.post.origin.postType.equals("poll_vote")) {
                        openSharedPoll(response.body());
                        break;
                    }
                    if (response.body().data.post.origin.postType.equals("photos")
                            || response.body().data.post.origin.postType.equals("")
                            || response.body().data.post.origin.postType.equals("post_comment")
                            || response.body().data.post.origin.postType.equals("post_like")
                            || response.body().data.post.origin.postType.equals("organisation_post")
                            || response.body().data.post.origin.postType.equals("ad")) {
                        openSharedNormalPost(response.body());
                        break;
                    }
                    break;

                case "article_shared":
                    openSharedArticle(response.body());
                    break;
            }
        }
    }

    private void openNoConnectionDialog(String title, String comment_id) {
        try {
            connectionErrorDialog.setConnectionErrorDialogListener(new ConnectionErrorDialog.ConnectionErrorDialogListener() {
                @Override
                public void onRetry() {
                    if (comment_id == null || comment_id.isEmpty()) {
                        hitGetNormalPostApi(user_id, post_id, "0", "prev", "0", true);
                    } else {
                        hitGetNormalPostApi(user_id, post_id, comment_id, "prev", "1", true);
                    }
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
            e.printStackTrace();
        }

    }


    public void openNormalPost(PojoNormalPostResponse pojoNormalPostResponse) {
        intent = new Intent(PostLoaderActivity.this, NormalPostActivity.class);

        String dataStr = new Gson().toJson(pojoNormalPostResponse,
                new TypeToken<PojoNormalPostResponse>() {
                }.getType());
        intent.putExtra(AppKeys.DATA, dataStr);
        intent.putExtra(AppKeys.POST_ID, pojoNormalPostResponse.data.post.postId);
        intent.putExtra(AppKeys.COMMENT_ID, comment_id);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivityForResult(intent, 0);
        overridePendingTransition(0, 0);
        finish();
    }

    public void openSharedNormalPost(PojoNormalPostResponse pojoNormalPostResponse) {
        intent = new Intent(PostLoaderActivity.this, SharedNormalPostActivity.class);
        String dataStr = new Gson().toJson(pojoNormalPostResponse,
                new TypeToken<PojoNormalPostResponse>() {
                }.getType());
        intent.putExtra(AppKeys.DATA, dataStr);
        intent.putExtra(AppKeys.POST_ID, pojoNormalPostResponse.data.post.postId);
        intent.putExtra(AppKeys.COMMENT_ID, comment_id);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivityForResult(intent, 0);
        overridePendingTransition(0, 0);
        finish();
    }

    public void openPoll(PojoNormalPostResponse pojoNormalPostResponse) {
        intent = new Intent(PostLoaderActivity.this, PollActivity.class);
        String dataStr = new Gson().toJson(pojoNormalPostResponse,
                new TypeToken<PojoNormalPostResponse>() {
                }.getType());
        intent.putExtra(AppKeys.DATA, dataStr);
        intent.putExtra(AppKeys.POST_ID, pojoNormalPostResponse.data.post.postId);
        intent.putExtra(AppKeys.COMMENT_ID, comment_id);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivityForResult(intent, 0);
        overridePendingTransition(0, 0);
        finish();
    }


    public void openSharedPoll(PojoNormalPostResponse pojoNormalPostResponse) {
        intent = new Intent(PostLoaderActivity.this, SharedPollActivity.class);
        String dataStr = new Gson().toJson(pojoNormalPostResponse,
                new TypeToken<PojoNormalPostResponse>() {
                }.getType());
        intent.putExtra(AppKeys.DATA, dataStr);
        intent.putExtra(AppKeys.POST_ID, pojoNormalPostResponse.data.post.postId);
        intent.putExtra(AppKeys.COMMENT_ID, comment_id);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivityForResult(intent, 0);
        overridePendingTransition(0, 0);
        finish();
    }

    public void openSharedArticle(PojoNormalPostResponse pojoNormalPostResponse) {
        intent = new Intent(PostLoaderActivity.this, SharedArticleActivity.class);
        String dataStr = new Gson().toJson(pojoNormalPostResponse,
                new TypeToken<PojoNormalPostResponse>() {
                }.getType());
        intent.putExtra(AppKeys.DATA, dataStr);
        intent.putExtra(AppKeys.POST_ID, pojoNormalPostResponse.data.post.postId);
        intent.putExtra(AppKeys.COMMENT_ID, comment_id);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivityForResult(intent, 0);
        overridePendingTransition(0, 0);
        finish();
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

}
