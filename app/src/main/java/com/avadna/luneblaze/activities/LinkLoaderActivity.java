package com.avadna.luneblaze.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.activities.article.ArticleActivity;
import com.avadna.luneblaze.activities.interest.InterestActivity;
import com.avadna.luneblaze.activities.profileInfo.ProfileInfoActivity;
import com.avadna.luneblaze.activities.question.QuestionActivity;
import com.avadna.luneblaze.activities.sessions.SessionLoaderActivity;
import com.avadna.luneblaze.activities.venue.VenueActivity;
import com.avadna.luneblaze.helperClasses.AES;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;

public class LinkLoaderActivity extends AppBaseActivity {

    public final String SESSION = "session";
    public final String QUESTION = "question";
    public final String POSTS = "posts";
    public final String VENUE = "venue";
    public final String INTEREST = "interest";
    public final String ARTICLE = "article";
    public final String USER = "user";
    public final String SESSION_PHOTO = "session_photo";

    TextView tv_link;

    PreferenceUtils preferenceUtils;


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

        setContentView(R.layout.activity_link_loader);
        preferenceUtils = new PreferenceUtils(this);

        tv_link = (TextView) findViewById(R.id.tv_link);

        if (preferenceUtils.is_user_login()) {
            Intent intent = getIntent();
            String action = intent.getAction();
            Uri data = intent.getData();
            // tv_link.setText(data.toString());
            String encodedUrl = data.toString();
            encodedUrl = encodedUrl.replace(AppKeys.WEBSITE_URL, "");
            encodedUrl = encodedUrl.replace(AppKeys.ALTERNATE_WEBSITE_URL, "");

            String decryptedUrl = AES.decrypt(encodedUrl, AppKeys.enKey);
            loadTarget(decryptedUrl);
            // ATTENTION: This was auto-generated to handle app links.
        } else {
            Intent loginIntent = new Intent(LinkLoaderActivity.this, LoginActivity.class);
            startActivity(loginIntent);
            finish();
        }
    }

    public void loadTarget(String url) {
        if (url != null) {
            Intent mainActivityIntent = new Intent(LinkLoaderActivity.this, MainActivity.class);
            Intent targetIntent;

            if (url.contains(SESSION_PHOTO)) {
                String sessionId = url.substring(url.indexOf(SESSION) + SESSION.length() + 1, url.length());
                targetIntent = new Intent(LinkLoaderActivity.this, SessionLoaderActivity.class);
                targetIntent.putExtra("id", sessionId);

            } else if (url.contains(SESSION)) {
                String sessionId = url.substring(url.indexOf(SESSION) + SESSION.length() + 1, url.length());
                targetIntent = new Intent(LinkLoaderActivity.this, SessionLoaderActivity.class);
                targetIntent.putExtra("id", sessionId);

            } else if (url.contains(QUESTION)) {
                url = url.substring(url.indexOf(QUESTION) + QUESTION.length() + 1, url.length());
                targetIntent = new Intent(LinkLoaderActivity.this, QuestionActivity.class);
                String questionId = "";
                String answerId = "";
                if (url.contains("/")) {
                    questionId = url.substring(0, url.indexOf("/"));
                    answerId = url.substring(url.indexOf("/") + 1, url.length());
                } else {
                    questionId = url;
                }
                targetIntent.putExtra("id", questionId);
                targetIntent.putExtra(AppKeys.ANSWER_ID, answerId);

            } else if (url.contains(POSTS)) {
                String postId = url.substring(url.indexOf(POSTS) + POSTS.length() + 1, url.length());
                if (postId.contains("/")) {
                    postId = postId.substring(0, postId.indexOf("/"));
                }
                targetIntent = new Intent(LinkLoaderActivity.this, PostLoaderActivity.class);
                targetIntent.putExtra(AppKeys.POST_ID, postId);

                String comment_id;
                String findStr = "/";
                int lastIndex = 0;
                int count = 0;

                while (lastIndex != -1) {
                    lastIndex = url.indexOf(findStr, lastIndex);

                    if (lastIndex != -1) {
                        count++;
                        if (count == 2) {
                            comment_id = url.substring(lastIndex + 1);
                            targetIntent.putExtra(AppKeys.COMMENT_ID, comment_id);
                        }
                        lastIndex += findStr.length();
                    }

                }


            } else if (url.contains(VENUE)) {
                String postId = url.substring(url.indexOf(VENUE) + VENUE.length() + 1, url.length());
                targetIntent = new Intent(LinkLoaderActivity.this, VenueActivity.class);
                targetIntent.putExtra(AppKeys.ID, postId);

            } else if (url.contains(INTEREST)) {
                String postId = url.substring(url.indexOf(INTEREST) + INTEREST.length() + 1, url.length());
                targetIntent = new Intent(LinkLoaderActivity.this, InterestActivity.class);
                targetIntent.putExtra(AppKeys.ID, postId);

            } else if (url.contains(ARTICLE)) {
                String postId = url.substring(url.indexOf(ARTICLE) + ARTICLE.length() + 1, url.length());
                targetIntent = new Intent(LinkLoaderActivity.this, ArticleActivity.class);
                targetIntent.putExtra(AppKeys.ID, postId);

            } else if (url.contains(USER)) {
                String postId = url.substring(url.indexOf(USER) + USER.length() + 1, url.length());
                targetIntent = new Intent(LinkLoaderActivity.this, ProfileInfoActivity.class);
                targetIntent.putExtra(AppKeys.TARGET_USER_ID, postId);

            } else {
                targetIntent = new Intent(LinkLoaderActivity.this, AppInitialiserActivity.class);
                mainActivityIntent = null;
                finish();
            }
            if (mainActivityIntent == null) {
                Intent[] intents = new Intent[]{targetIntent};
                startActivities(intents);
                finish();
            } else {
                Intent[] intents = new Intent[]{mainActivityIntent, targetIntent};
                startActivities(intents);
                finish();
            }

        } else {
            Intent splashIntent = new Intent(LinkLoaderActivity.this, AppInitialiserActivity.class);
            startActivity(splashIntent);
            finish();
        }

    }
}
