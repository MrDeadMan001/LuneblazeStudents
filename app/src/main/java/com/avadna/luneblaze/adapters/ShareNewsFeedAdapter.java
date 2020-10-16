package com.avadna.luneblaze.adapters;


import android.app.Activity;
import android.app.Dialog;

import com.avadna.luneblaze.helperClasses.MyCustomThemeDialog;

import android.content.Intent;
import android.graphics.Color;

import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.activities.sessions.SessionLoaderActivity;
import com.avadna.luneblaze.activities.article.ArticleActivity;
import com.avadna.luneblaze.activities.profileInfo.ProfileInfoActivity;
import com.avadna.luneblaze.activities.sessions.AssignedSessionActivity;
import com.avadna.luneblaze.activities.interest.InterestActivity;
import com.avadna.luneblaze.activities.question.QuestionActivity;
import com.avadna.luneblaze.activities.SessionAcceptFormActivity;
import com.avadna.luneblaze.activities.venue.VenueActivity;
import com.avadna.luneblaze.activities.VenuePollActivity;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.ImageDimensions;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.pojo.PojoGetSuggestedFriendListResponse;
import com.avadna.luneblaze.pojo.pojoNewsFeed.PojoGetNewsFeedResponseData;
import com.avadna.luneblaze.pojo.pojoNewsFeed.PojoNewsFeedPhoto;
import com.avadna.luneblaze.pojo.pojoNewsFeed.PojoNewsFeedPollOption;
import com.avadna.luneblaze.pojo.pojoQuestion.PojoUserData;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;
import com.avadna.luneblaze.utils.ProportionalImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.leocardz.link.preview.library.LinkPreviewCallback;
import com.leocardz.link.preview.library.SourceContent;
import com.leocardz.link.preview.library.TextCrawler;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

/**
 * Created by Sunny on 27-12-2017.
 */

public class ShareNewsFeedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    //  pivate List<String> hierarchyList;
    int listSize;
    Activity activity;
    Fragment fragment;
    ApiInterface apiService;
    PreferenceUtils preferenceUtils;
    String user_id;
    List<PojoUserData> suggestedFriendList;
    List<PojoGetNewsFeedResponseData> newsFeedList;
    ShareNewsFeedAdapterCallback shareNewsFeedAdapterCallback;
    CommonFunctions commonFunctions;

    DisplayMetrics displayMetrics;
    int imageFixedWidth;
    int imageFixedHeight;

    int maxArticleImageHeight;
    int maxSessionImageHeight;


    public ShareNewsFeedAdapter(Fragment fragment, List<PojoGetNewsFeedResponseData> newsFeedList) {

        try {
            this.shareNewsFeedAdapterCallback = ((ShareNewsFeedAdapterCallback) fragment);
        } catch (ClassCastException e) {
            throw new ClassCastException("Fragment must implement ShareNewsFeedAdapterCallback.");
        }
        this.fragment = fragment;
        this.activity = fragment.getActivity();
        this.listSize = listSize;
        this.newsFeedList = newsFeedList;
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
        preferenceUtils = new PreferenceUtils(activity);
        user_id = preferenceUtils.get_user_id();
        commonFunctions = new CommonFunctions(activity);

        displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        imageFixedWidth = (int) (displayMetrics.widthPixels);
        imageFixedHeight = (int) (displayMetrics.widthPixels * 0.6f);
        maxArticleImageHeight = (int) (displayMetrics.widthPixels * (3f / 5));
        maxSessionImageHeight = (int) (displayMetrics.widthPixels * (2f / 3));

        //  this.hierarchyList = hierarchyList;
    }


    public ShareNewsFeedAdapter(Activity activity, List<PojoGetNewsFeedResponseData> newsFeedList) {

        try {
            this.shareNewsFeedAdapterCallback = ((ShareNewsFeedAdapterCallback) fragment);
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement ShareNewsFeedAdapterCallback.");
        }
        this.activity = activity;
        this.listSize = listSize;
        this.newsFeedList = newsFeedList;
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
        preferenceUtils = new PreferenceUtils(activity);
        user_id = preferenceUtils.get_user_id();
        commonFunctions = new CommonFunctions(activity);

        displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        imageFixedWidth = (int) (displayMetrics.widthPixels);
        imageFixedHeight = (int) (displayMetrics.widthPixels * 0.6f);
        maxArticleImageHeight = (int) (displayMetrics.widthPixels * (3f / 5));
        maxSessionImageHeight = (int) (displayMetrics.widthPixels * (2f / 3));

        //  this.hierarchyList = hierarchyList;
    }

    @Override
    public int getItemViewType(int position) {
        switch (newsFeedList.get(position).postType) {
            case "session_shared":
            case "session_attend":
            case "session":
                if (newsFeedList.get(position).origin != null) {
                    if (newsFeedList.get(position).origin.status.equals("1")) {
                        return 1;
                    } else if (newsFeedList.get(position).origin.status.equals("2")) {
                        return 1;
                    } else if (newsFeedList.get(position).origin.status.equals("3")) {
                        return 2;
                    } else {
                        return 3;
                    }
                } else {
                    return 6;
                }


            case "article":
            case "article_shared":
            case "article_like":
            case "article_comment":
                return 4;


            case "question":
            case "question_shared":
                return 5;

            case "answer_shared":
                return 7;

            case "photos":
            case "":
            case "post_like":
            case "post_comment":
            case "organisation_post":
            case "ad":
                if (newsFeedList.get(position).origin != null) {
                    if (newsFeedList.get(position).origin.poll != null) {
                        return 13;
                    }
                    if (newsFeedList.get(position).origin.articlesId != null) {
                        return 14;
                    }
                    if (newsFeedList.get(position).origin.postType.equals("photos")
                            || newsFeedList.get(position).origin.postType.equals("")
                            || newsFeedList.get(position).origin.postType.equals("post_comment")
                            || newsFeedList.get(position).origin.postType.equals("post_like")) {
                        return 12;
                    }

                } else {
                    if (newsFeedList.get(position).poll != null) {
                        return 9;
                    }
                }
                return 8;


            case "poll":
            case "poll_vote":

                return 9;

            case "interest_shared":
                return 10;

            case "venue_shared":
                return 11;

            case "shared":
                if (newsFeedList.get(position).origin.postType.equals("poll")) {
                    return 16;
                }
                if (newsFeedList.get(position).origin.postType.equals("poll_vote")) {
                    return 16;
                }
                if (newsFeedList.get(position).origin.postType.equals("photos")
                        || newsFeedList.get(position).origin.postType.equals("")
                        || newsFeedList.get(position).origin.postType.equals("post_comment")
                        || newsFeedList.get(position).origin.postType.equals("post_like")
                        || newsFeedList.get(position).origin.postType.equals("organisation_post")
                        || newsFeedList.get(position).origin.postType.equals("ad")) {
                    if (newsFeedList.get(position).origin.poll != null) {
                        return 16;
                    } else {
                        return 15;
                    }
                } else {
                    return 6;
                }


            default:
                return 6;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        switch (viewType) {
            case 1:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.verified_session_post, parent, false);
                return new UpcomingSessionViewHolder(itemView);

            case 2:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.assigned_session_post, parent, false);
                return new AssignedSessionViewHolder(itemView);

            case 3:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.organised_session_post, parent, false);
                return new OrganisedSessionViewHolder(itemView);

            case 4:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.article_post, parent, false);
                return new ArticleViewHolder(itemView);

            case 5:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.question_list_item, parent, false);
                return new QuestionViewHolder(itemView);

            case 6:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.suggested_friends_news_feed, parent, false);
                return new SuggestedFriendsViewHolder(itemView);

            case 7:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.share_answer_list_item, parent, false);
                return new AnswerViewHolder(itemView);

            case 8:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.normal_post_item, parent, false);
                return new NormalPostViewHolder(itemView);
            case 9:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.poll_post_item, parent, false);
                return new PollPostViewHolder(itemView);
            case 10:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.interest_post, parent, false);
                return new InterestPostViewHolder(itemView);
            case 11:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.venue_post, parent, false);
                return new VenuePostViewHolder(itemView);

            case 12:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.normal_post_item, parent, false);
                return new NormalPostViewHolder(itemView);

            case 13:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.poll_post_item, parent, false);
                return new PollPostViewHolder(itemView);

            case 14:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.article_post, parent, false);
                return new ArticleViewHolder(itemView);

            case 15:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.normal_post_item, parent, false);
                return new NormalPostViewHolder(itemView);

            case 16:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.poll_post_item, parent, false);
                return new PollPostViewHolder(itemView);

            case 17:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.article_post, parent, false);
                return new ArticleViewHolder(itemView);

            default:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.suggested_friends_news_feed, parent, false);
                return new SuggestedFriendsViewHolder(itemView);
        }
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        //   holder.tv_hierarchy_item.setText(hierarchyList.get(position));
        switch (holder.getItemViewType()) {
            case 1:
                UpcomingSessionViewHolder upcomingSessionViewHolder = (UpcomingSessionViewHolder) holder;
                configUpcomingSessionViewHolder(upcomingSessionViewHolder, position);
                break;
            case 2:
                AssignedSessionViewHolder assignedSessionViewHolder = (AssignedSessionViewHolder) holder;
                configAssignedSessionViewHolder(assignedSessionViewHolder, position);
                break;
            case 3:
                OrganisedSessionViewHolder organisedSessionViewHolder = (OrganisedSessionViewHolder) holder;
                configOrganisedSessionViewHolder(organisedSessionViewHolder, position);
                break;
            case 4:
                ArticleViewHolder articleViewHolder = (ArticleViewHolder) holder;
                configArticleViewHolder(articleViewHolder, position);
                break;
            case 5:
                QuestionViewHolder questionViewHolder = (QuestionViewHolder) holder;
                configQuestionViewHolder(questionViewHolder, position);
                break;
            case 6:
                SuggestedFriendsViewHolder suggestedFriendsViewHolder = (SuggestedFriendsViewHolder) holder;
                configSuggestedViewHolder(suggestedFriendsViewHolder, position);
                break;
            case 7:
                AnswerViewHolder answerViewHolder = (AnswerViewHolder) holder;
                configAnswerViewHolder(answerViewHolder, position);
                break;
            case 8:
                NormalPostViewHolder normalPostViewHolder = (NormalPostViewHolder) holder;
                configNormalPostViewHolder(normalPostViewHolder, position);
                break;
            case 9:
                PollPostViewHolder pollPostViewHolder = (PollPostViewHolder) holder;
                configPollPostViewHolder(pollPostViewHolder, position);
                break;
            case 10:
                InterestPostViewHolder interestPostViewHolder = (InterestPostViewHolder) holder;
                configInterestPostViewHolder(interestPostViewHolder, position);
                break;

            case 11:
                VenuePostViewHolder venuePostViewHolder = (VenuePostViewHolder) holder;
                configVenuePostViewHolder(venuePostViewHolder, position);
                break;

            case 12:
                NormalPostViewHolder normalPostViewHolder1 = (NormalPostViewHolder) holder;
                configActionNormalPostViewHolder(normalPostViewHolder1, position);
                break;

            case 13:
                PollPostViewHolder PollViewHolder = (PollPostViewHolder) holder;
                configActionSharedPollViewHolder(PollViewHolder, position);
                break;

            case 14:
                ArticleViewHolder articlePostViewHolder = (ArticleViewHolder) holder;
                configActionSharedArticleViewHolder(articlePostViewHolder, position);
                break;

            case 15:
                NormalPostViewHolder normalPostViewHolder2 = (NormalPostViewHolder) holder;
                configSharedNormalPostViewHolder(normalPostViewHolder2, position);
                break;

            case 16:
                PollPostViewHolder sharedPollViewHolder = (PollPostViewHolder) holder;
                configSharedPollViewHolder(sharedPollViewHolder, position);
                break;

            case 17:
                ArticleViewHolder sharedArticlePostViewHolder = (ArticleViewHolder) holder;
                configSharedArticleViewHolder(sharedArticlePostViewHolder, position);
                break;

            default:
                SuggestedFriendsViewHolder suggestedFriendsViewHolder2 = (SuggestedFriendsViewHolder) holder;
                configSuggestedViewHolder(suggestedFriendsViewHolder2, position);
                break;
        }
    }


    private void configSuggestedViewHolder(SuggestedFriendsViewHolder suggestedFriendsViewHolder, int position) {

    }

    /*private void configPostViewHolder(PostViewHolder postViewHolder, int position) {
    }
*/
    private void configAnswerViewHolder(AnswerViewHolder answerViewHolder, final int position) {
        final PojoGetNewsFeedResponseData currentItem = newsFeedList.get(position);

        answerViewHolder.tv_question.setText(Html.fromHtml(currentItem.origin.post));

        if (currentItem.answer.likes.equals("0")) {
            answerViewHolder.tv_num_upvotes.setVisibility(View.GONE);
        } else {
            answerViewHolder.tv_num_upvotes.setVisibility(View.VISIBLE);
        }
        answerViewHolder.tv_num_upvotes.setText("" + currentItem.answer.likes + " "
                + activity.getString(R.string.upvotes));


        if (currentItem.origin.totalReplies == 0) {
            answerViewHolder.tv_num_replies.setVisibility(View.GONE);
        } else {
            answerViewHolder.tv_num_replies.setVisibility(View.VISIBLE);
        }

        answerViewHolder.tv_num_replies.setText("" + currentItem.origin.totalReplies + " "
                + activity.getString(R.string.replies));

        if (!currentItem.answer.createdBy.userPicture.isEmpty()) {
            if (activity != null)
                Glide.with(activity.getApplicationContext()).load(currentItem.answer.createdBy.userPicture)
                        .apply(bitmapTransform(new CropCircleTransformation()))
                        .into(answerViewHolder.iv_user_dp);
        }

        answerViewHolder.tv_user_name.setText(currentItem.answer.createdBy.userFullname);
        answerViewHolder.tv_user_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent profileIntent = new Intent(activity,
                        ProfileInfoActivity.class);
                profileIntent.putExtra("target_user_id", currentItem.userId);
                activity.startActivity(profileIntent);
            }
        });

        answerViewHolder.tv_answer.setText(Html.fromHtml(currentItem.answer.post));
        answerViewHolder.tv_answer_time_stamp.setText(commonFunctions.parseNewsFeedItemTime(currentItem.answer.addedOn));

    }


    private void configPollPostViewHolder(final PollPostViewHolder pollPostViewHolder, final int position) {
        final PojoGetNewsFeedResponseData currentItem = newsFeedList.get(position);

        String userToShow = currentItem.userFullname;

        if (!currentItem.total.equals("0")) {
            userToShow = userToShow + " " + activity.getString(R.string.and) + " " + currentItem.total +
                    " " + activity.getString(R.string.others);
        }

        if (currentItem.origin != null && currentItem.origin.poll != null) {
            pollPostViewHolder.tv_time_stamp.setText(commonFunctions.parseNewsFeedItemTime(currentItem.origin.updatedAt));
            if (currentItem.origin.iLike == 1) {
                pollPostViewHolder.tv_like_button.setTextColor(ContextCompat.getColor(activity, R.color.app_theme_medium));
            } else {
                pollPostViewHolder.tv_like_button.setTextColor(ContextCompat.getColor(activity, R.color.med_grey));
            }
            pollPostViewHolder.tv_poll_title.setText(Html.fromHtml(currentItem.origin.text));
            pollPostViewHolder.optionList.clear();
            pollPostViewHolder.optionList.addAll(currentItem.origin.poll.options);
            pollPostViewHolder.pollPostAdapter.setTotalVotes(currentItem.origin.poll.votes);
            pollPostViewHolder.tv_activity_performed.setText(userToShow + " "
                    + activity.getString(R.string.shared));
            pollPostViewHolder.tv_like_number.setText(currentItem.origin.likes + " " + activity.getString(R.string.likes));
            pollPostViewHolder.tv_comment_number.setText(currentItem.origin.comments + " " + activity.getString(R.string.comments));

        } else {
            pollPostViewHolder.tv_time_stamp.setText(commonFunctions.parseNewsFeedItemTime(currentItem.updatedAt));
            if (currentItem.iLike == 1) {
                pollPostViewHolder.tv_like_button.setTextColor(ContextCompat.getColor(activity, R.color.app_theme_medium));
            } else {
                pollPostViewHolder.tv_like_button.setTextColor(ContextCompat.getColor(activity, R.color.med_grey));
            }
            pollPostViewHolder.tv_like_number.setText(currentItem.totalLikes + " " + activity.getString(R.string.likes));
            pollPostViewHolder.tv_comment_number.setText(currentItem.comments + " " + activity.getString(R.string.comments));
            pollPostViewHolder.tv_poll_title.setText(currentItem.textPlain);
            pollPostViewHolder.optionList.clear();
            pollPostViewHolder.optionList.addAll(currentItem.poll.options);
            pollPostViewHolder.pollPostAdapter.setTotalVotes(currentItem.poll.votes);
            pollPostViewHolder.tv_activity_performed.setText(userToShow + " "
                    + activity.getString(R.string.created_poll));
        }

        for (int i = 0; i < pollPostViewHolder.optionList.size(); i++) {
            if (pollPostViewHolder.optionList.get(i).checked) {
                pollPostViewHolder.pollPostAdapter.setVoteCasted(true);
                pollPostViewHolder.pollPostAdapter.setCanSeeVotes(true);

                break;
            }
        }

        pollPostViewHolder.pollPostAdapter.notifyDataSetChanged();

    }


    private void configActionSharedPollViewHolder(final PollPostViewHolder pollViewHolder, final int position) {
        final PojoGetNewsFeedResponseData currentItem = newsFeedList.get(position);
        pollViewHolder.rl_user_comment_wrapper.setVisibility(View.GONE);

        pollViewHolder.pollPostAdapter.setVoteCasted(false);

        String userToShow = currentItem.userFullname;
        if (!currentItem.total.equals("0")) {
            userToShow = userToShow + " " + activity.getString(R.string.and) + " " + currentItem.total +
                    " " + activity.getString(R.string.others);
        }
        pollViewHolder.tv_time_stamp.setText(commonFunctions.parseNewsFeedItemTime(currentItem.origin.updatedAt));

        pollViewHolder.tv_like_number.setText(currentItem.origin.poll.votes + " "
                + activity.getString(R.string.votes));
        pollViewHolder.tv_comment_number.setText(currentItem.comments + " " + activity.getString(R.string.comments));
        pollViewHolder.tv_poll_title.setText(Html.fromHtml(currentItem.origin.textPlain));
        pollViewHolder.optionList.clear();
        pollViewHolder.optionList.addAll(currentItem.origin.poll.options);
        pollViewHolder.pollPostAdapter.setTotalVotes(currentItem.origin.poll.votes);
        for (int i = 0; i < pollViewHolder.optionList.size(); i++) {
            if (pollViewHolder.optionList.get(i).checked) {
                pollViewHolder.pollPostAdapter.setVoteCasted(true);
                break;
            }
        }

        if (currentItem.postType.equals("post_comment")) {
            pollViewHolder.tv_activity_performed.setText(userToShow + " "
                    + activity.getString(R.string.commented_on));
        } else if (currentItem.postType.equals("post_like")) {
            pollViewHolder.tv_activity_performed.setText(userToShow + " "
                    + activity.getString(R.string.liked));
        }

        pollViewHolder.pollPostAdapter.notifyDataSetChanged();

    }

    private void configInterestPostViewHolder(InterestPostViewHolder interestPostViewHolder, final int position) {
        final PojoGetNewsFeedResponseData currentItem = newsFeedList.get(position);

        String userToShow = currentItem.userFullname;

        if (!currentItem.total.equals("0")) {
            userToShow = userToShow + " " + activity.getString(R.string.and) + " " + currentItem.total +
                    " " + activity.getString(R.string.others);
        }

        interestPostViewHolder.tv_activity_performed.setText(userToShow + " " + activity.getString(R.string.shared));

        if (!currentItem.origin.image.isEmpty()) {
            if (activity != null)
                Glide.with(activity.getApplicationContext()).load(currentItem.origin.image)
                        .apply(new RequestOptions().override(96, 96))
                        // .apply(bitmapTransform(new CropCircleTransformation()))
                        .into(interestPostViewHolder.iv_cover_photo);
        }

        interestPostViewHolder.tv_interest_name.setText(Html.fromHtml(currentItem.origin.text));
        interestPostViewHolder.tv_interest_parent.setText(currentItem.origin.parent);
        interestPostViewHolder.tv_time_stamp.setText(commonFunctions.parseNewsFeedItemTime(currentItem.origin.modifiedOn));
        interestPostViewHolder.tv_description.setText(Html.fromHtml(currentItem.origin.description));

        if (currentItem.origin.iFollow == 1) {
            interestPostViewHolder.tv_follow_button.setTextColor(ContextCompat.getColor(activity, R.color.app_theme_medium));
        } else {
            interestPostViewHolder.tv_follow_button.setTextColor(ContextCompat.getColor(activity, R.color.med_grey));
        }

        interestPostViewHolder.tv_num_followers.setText("" + currentItem.origin.totalAttendes + " "
                + activity.getString(R.string.followers));
        interestPostViewHolder.tv_num_articles.setText("" + currentItem.origin.totalArticles);
        interestPostViewHolder.tv_num_questions.setText("" + currentItem.origin.totalQues);
        interestPostViewHolder.tv_num_sessions.setText("" + currentItem.origin.totalSessions);

        interestPostViewHolder.tv_share_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareNewsFeedAdapterCallback.shareNewsFeedItemClickCallback(position, currentItem, AppKeys.INTEREST_SHARE);
            }
        });

        interestPostViewHolder.tv_follow_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareNewsFeedAdapterCallback.shareNewsFeedItemClickCallback(position, currentItem, AppKeys.INTEREST_FOLLOW);
            }
        });


        interestPostViewHolder.rl_top_bar_wrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent interestIntent = new Intent(activity, InterestActivity.class);
                interestIntent.putExtra("id", "3");
                activity.startActivity(interestIntent);
            }
        });


    }

    private void configSharedPollViewHolder(final PollPostViewHolder pollViewHolder, final int position) {
        final PojoGetNewsFeedResponseData currentItem = newsFeedList.get(position);
        pollViewHolder.rl_top_bar_wrapper.setVisibility(View.GONE);
        pollViewHolder.pollPostAdapter.setVoteCasted(false);
        pollViewHolder.pollPostAdapter.setNewsFeedItemPosition(position);

        String userToShow = currentItem.userFullname;

        if (!currentItem.total.equals("0")) {
            userToShow = userToShow + " " + activity.getString(R.string.and) + " " + currentItem.total +
                    " " + activity.getString(R.string.others);
        }

        pollViewHolder.tv_time_stamp.setText(commonFunctions.parseNewsFeedItemTime(currentItem.updatedAt));

        pollViewHolder.tv_like_number.setText(currentItem.origin.poll.votes + " " + activity.getString(R.string.votes));
        pollViewHolder.tv_comment_number.setText(currentItem.comments + " " + activity.getString(R.string.comments));
        pollViewHolder.tv_poll_title.setText(Html.fromHtml(currentItem.origin.textPlain));
        pollViewHolder.optionList.clear();
        pollViewHolder.optionList.addAll(currentItem.origin.poll.options);
        pollViewHolder.pollPostAdapter.setTotalVotes(currentItem.origin.poll.votes);
        for (int i = 0; i < pollViewHolder.optionList.size(); i++) {
            if (pollViewHolder.optionList.get(i).checked) {
                pollViewHolder.pollPostAdapter.setVoteCasted(true);
                pollViewHolder.pollPostAdapter.setCanSeeVotes(true);

                break;
            }
        }
        pollViewHolder.rl_top_bar_wrapper.setVisibility(View.GONE);
        pollViewHolder.rl_user_comment_wrapper.setVisibility(View.GONE);
        pollViewHolder.pollPostAdapter.notifyDataSetChanged();

    }

    private void configSharedArticleViewHolder(final ArticleViewHolder articleViewHolder, final int position) {
        final PojoGetNewsFeedResponseData currentItem = newsFeedList.get(position);

        articleViewHolder.tv_writer_name.setText(currentItem.origin.createdBy.userFullname);
        articleViewHolder.tv_writer_designation.setText(currentItem.origin.createdBy.userWorkTitle);
        articleViewHolder.tv_time_stamp.setText(commonFunctions
                .parseNewsFeedItemTime(currentItem.origin.addedOn));

        if (!currentItem.origin.createdBy.userPicture.isEmpty()) {
            if (activity != null)
                Glide.with(activity.getApplicationContext()).load(currentItem.origin.createdBy.userPicture)
                        .apply(new RequestOptions().override(96, 96))
                        .apply(bitmapTransform(new CropCircleTransformation()))
                        .into(articleViewHolder.iv_user_dp);
        }

        articleViewHolder.iv_user_dp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                articleViewHolder.tv_writer_name.callOnClick();
            }
        });

        articleViewHolder.tv_writer_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent profileIntent = new Intent(activity, ProfileInfoActivity.class);
                profileIntent.putExtra("target_user_id", currentItem.authorId);
                activity.startActivity(profileIntent);
            }
        });


        if (!currentItem.origin.coverPhoto.isEmpty()) {
            int height = imageFixedHeight;
            int width = imageFixedWidth;

            if (currentItem.origin.imgDimensions != null && !currentItem.origin.imgDimensions.isEmpty()) {
                ImageDimensions dimen = getScaledDimensions(currentItem.origin.imgDimensions, AppKeys.ARTICLE);
                height = dimen.height;
                width = dimen.width;
                articleViewHolder.iv_cover_photo.getLayoutParams().height = height;
                articleViewHolder.iv_cover_photo.getLayoutParams().width = width;
            }

           /* CardView.LayoutParams layoutParams=(CardView.LayoutParams)articleViewHolder.iv_cover_photo.getLayoutParams();
            layoutParams.height=height;
            layoutParams.width=width;
            articleViewHolder.iv_cover_photo.setLayoutParams(layoutParams);*/

            if (activity != null) Glide.with(activity.getApplicationContext())
                    .load(currentItem.origin.coverPhoto)
                    .apply(new RequestOptions().override(width, height))
                    .into(articleViewHolder.iv_cover_photo);
        }

        String userToShow = currentItem.userFullname;

        if (!currentItem.total.equals("0")) {
            userToShow = userToShow + " " + activity.getString(R.string.and) + " " + currentItem.total +
                    " " + activity.getString(R.string.others);
        }
        articleViewHolder.tv_article_name.setText(Html.fromHtml(currentItem.origin.title));
        articleViewHolder.tv_like_number.setText("" + currentItem.origin.likes + " " + activity.getString(R.string.likes));
        articleViewHolder.tv_comment_number.setText("" + currentItem.origin.totalComments + " "
                + activity.getString(R.string.comments));
        articleViewHolder.tv_share_number.setText("" + currentItem.origin.shares + " " + activity.getString(R.string.shares));

    }

    private void configSharedNormalPostViewHolder(final NormalPostViewHolder normalPostViewHolder, final int position) {
        final PojoGetNewsFeedResponseData currentItem = newsFeedList.get(position);
        normalPostViewHolder.rl_top_bar_wrapper.setVisibility(View.GONE);
        normalPostViewHolder.rl_user_comment_wrapper.setVisibility(View.GONE);
        normalPostViewHolder.rl_web_preview_wrapper.setVisibility(View.GONE);
        List<PojoNewsFeedPhoto> photos = currentItem.origin.photos;

        //creator data
        normalPostViewHolder.tv_time_stamp.setText(commonFunctions
                .parseNewsFeedItemTime(currentItem.origin.updatedAt));

        if (currentItem.origin.organizationData != null
                && (currentItem.origin.postType.equals("organisation_post") || currentItem.origin.postType.equals("ad"))) {
            if (!currentItem.origin.organizationData.logo.isEmpty()) {
                if (activity != null)
                    Glide.with(activity.getApplicationContext()).load(currentItem.origin.organizationData.logo)
                            .apply(bitmapTransform(new CropCircleTransformation()))
                            .into(normalPostViewHolder.iv_user_dp);
            }
            normalPostViewHolder.tv_poster_name.setText(currentItem.origin.organizationData.name);
            normalPostViewHolder.tv_poster_designation.setText(currentItem.origin.organizationData.typename);
        } else {
            normalPostViewHolder.tv_poster_name.setText(currentItem.origin.userFullname);
            normalPostViewHolder.tv_poster_designation.setText(currentItem.origin.userWorkTitle);
            if (!currentItem.origin.postAuthorPicture.isEmpty()) {
                if (activity != null)
                    Glide.with(activity.getApplicationContext()).load(currentItem.origin.postAuthorPicture)
                            .apply(bitmapTransform(new CropCircleTransformation()))
                            .into(normalPostViewHolder.iv_user_dp);
            }
        }


        normalPostViewHolder.iv_user_dp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                normalPostViewHolder.tv_poster_name.callOnClick();
            }
        });
        Log.d("newsfeed", currentItem.postId);

        String userToShow = currentItem.userFullname;
        if (!currentItem.total.equals("0")) {
            userToShow = userToShow + " " + activity.getString(R.string.and) + " " + currentItem.total +
                    " " + activity.getString(R.string.others);
        }


        if (currentItem.origin.postType.equals("photos")) {
            normalPostViewHolder.tv_activity_performed.setText(userToShow + " "
                    + activity.getString(R.string.shared) + " "
                    + activity.getString(R.string.photos));
            photos = currentItem.origin.photos;
        } else if (currentItem.origin.postType.equals("")) {
            normalPostViewHolder.tv_activity_performed.setText(userToShow + " "
                    + activity.getString(R.string.shared));
        }

        boolean tagged;
        if (currentItem.tagged.equals("1")) {
            tagged = true;
        } else {
            tagged = false;
        }
        normalPostViewHolder.tv_post_content.setHighlightColor(
                Color.TRANSPARENT); // prevent TextView change background when highlight
        normalPostViewHolder.tv_post_content.setMovementMethod(LinkMovementMethod.getInstance());
        currentItem.spannableClickItem = commonFunctions.setClickSpans(currentItem.origin.text,
                currentItem.origin.textPlain, tagged, null);
        currentItem.clickableSpansList = currentItem.spannableClickItem.clickableSpansList;
        normalPostViewHolder.tv_post_content
                .setText(currentItem.spannableClickItem.spannableString, TextView.BufferType.SPANNABLE);

        String url = "";

        for (int i = 0; i < currentItem.spannableClickItem.clickableUrls.size(); i++) {
            String currentUrl = currentItem.spannableClickItem.clickableUrls.get(i);
            if (!currentUrl.contains(AppKeys.WEBSITE_URL)
                    && Patterns.WEB_URL.matcher(currentUrl).matches()) {
                url = currentItem.spannableClickItem.clickableUrls.get(i);
                break;
            }
        }

        if (!url.isEmpty()) {
            if (currentItem.sourceContent != null) {
                normalPostViewHolder.rl_web_preview_wrapper.setVisibility(View.GONE);
                String title = currentItem.sourceContent.getTitle();
                String name = currentItem.sourceContent.getCannonicalUrl();
                String description = currentItem.sourceContent.getCannonicalUrl();
                if (title.isEmpty() || name.isEmpty()) {
                    normalPostViewHolder.rl_web_preview_wrapper.setVisibility(View.GONE);
                } else {
                    normalPostViewHolder.rl_web_preview_wrapper.setVisibility(View.VISIBLE);
                    normalPostViewHolder.tv_link_title.setText(currentItem.sourceContent.getTitle());
                    normalPostViewHolder.tv_website_name.setText(currentItem.sourceContent.getCannonicalUrl());
                    normalPostViewHolder.tv_link_description.setText(currentItem.sourceContent.getDescription());
                    for (int i = 0; i < currentItem.sourceContent.getImages().size(); i++) {
                        if (currentItem.sourceContent.getImages().get(i).contains("png")
                                || currentItem.sourceContent.getImages().get(i).contains("jpg")) {
                            Glide.with(activity)
                                    .load(currentItem.sourceContent.getImages().get(i))
                                    .into(normalPostViewHolder.iv_link_image);
                            break;
                        }
                    }

                }
            } else {
                normalPostViewHolder.textCrawler = new TextCrawler();
                LinkPreviewCallback linkPreviewCallback = new LinkPreviewCallback() {
                    @Override
                    public void onPre() {
                        // Any work that needs to be done before generating the preview. Usually inflate
                        // your custom preview layout here.
                        //commonFunctions.setToastMessage(activity,"link preview called",Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);
                        normalPostViewHolder.rl_web_preview_wrapper.setVisibility(View.GONE);
                    }

                    @Override
                    public void onPos(SourceContent sourceContent, boolean b) {
                        currentItem.sourceContent = sourceContent;
                        // Populate your preview layout with the results of sourceContent.
                        String title = sourceContent.getTitle();
                        String name = sourceContent.getCannonicalUrl();
                        String description = sourceContent.getCannonicalUrl();

                        if (title.isEmpty() || name.isEmpty()) {
                            normalPostViewHolder.rl_web_preview_wrapper.setVisibility(View.GONE);
                        } else {
                            normalPostViewHolder.rl_web_preview_wrapper.setVisibility(View.VISIBLE);
                            normalPostViewHolder.tv_link_title.setText(sourceContent.getTitle());
                            normalPostViewHolder.tv_website_name.setText(sourceContent.getCannonicalUrl());
                            normalPostViewHolder.tv_link_description.setText(sourceContent.getDescription());
                            for (int i = 0; i < sourceContent.getImages().size(); i++) {
                                if (sourceContent.getImages().get(i).contains("png")
                                        || sourceContent.getImages().get(i).contains("jpg")) {
                                    Glide.with(activity)
                                            .load(sourceContent.getImages().get(i))
                                            .into(normalPostViewHolder.iv_link_image);
                                    break;
                                }
                            }

                        }
                    }
                };
                normalPostViewHolder.textCrawler.makePreview(linkPreviewCallback, url);
            }
        }


        if (photos == null || photos.size() == 0) {
            normalPostViewHolder.iv_cover_photo1.setVisibility(View.GONE);
            normalPostViewHolder.ll_two_images.setVisibility(View.GONE);
            normalPostViewHolder.ll_three_images.setVisibility(View.GONE);
        } else if (photos.size() == 1) {
            normalPostViewHolder.iv_cover_photo1.setVisibility(View.VISIBLE);
            int height = imageFixedHeight;
            int width = imageFixedWidth;
            if (photos.get(0).imgDimensions != null && !photos.get(0).imgDimensions.isEmpty()) {
                ImageDimensions dimen = getScaledDimensions(photos.get(0).imgDimensions, AppKeys.NORMAL);
                height = dimen.height;
                width = dimen.width;
                normalPostViewHolder.iv_cover_photo1.getLayoutParams().height = height;
                normalPostViewHolder.iv_cover_photo1.getLayoutParams().width = width;
            }
            if (activity != null) Glide.with(activity.getApplicationContext())
                    .load(photos.get(0).source)
                    .apply(new RequestOptions().override(width, height))
                    .into(normalPostViewHolder.iv_cover_photo1);
            normalPostViewHolder.ll_two_images.setVisibility(View.GONE);
            normalPostViewHolder.ll_three_images.setVisibility(View.GONE);
        } else if (photos.size() == 2) {
            normalPostViewHolder.iv_cover_photo1.setVisibility(View.GONE);
            normalPostViewHolder.ll_two_images.setVisibility(View.VISIBLE);
            if (activity != null)
                Glide.with(activity.getApplicationContext()).load(photos.get(0).source).into(normalPostViewHolder.iv_cover_photo2);
            if (activity != null)
                Glide.with(activity.getApplicationContext()).load(photos.get(1).source).into(normalPostViewHolder.iv_cover_photo3);
            normalPostViewHolder.ll_three_images.setVisibility(View.GONE);
        } else if (photos.size() == 3) {
            normalPostViewHolder.iv_cover_photo1.setVisibility(View.VISIBLE);
            if (activity != null)
                Glide.with(activity.getApplicationContext()).load(photos.get(0).source).into(normalPostViewHolder.iv_cover_photo1);
            normalPostViewHolder.ll_two_images.setVisibility(View.VISIBLE);
            if (activity != null)
                Glide.with(activity.getApplicationContext()).load(photos.get(1).source).into(normalPostViewHolder.iv_cover_photo2);
            if (activity != null)
                Glide.with(activity.getApplicationContext()).load(photos.get(2).source).into(normalPostViewHolder.iv_cover_photo3);
            normalPostViewHolder.ll_three_images.setVisibility(View.GONE);
        } else if (photos.size() == 4) {
            normalPostViewHolder.iv_cover_photo1.setVisibility(View.VISIBLE);
            if (activity != null)
                Glide.with(activity.getApplicationContext()).load(photos.get(0).source).into(normalPostViewHolder.iv_cover_photo1);
            normalPostViewHolder.ll_two_images.setVisibility(View.GONE);
            normalPostViewHolder.ll_three_images.setVisibility(View.VISIBLE);
            if (activity != null)
                Glide.with(activity.getApplicationContext()).load(photos.get(1).source).into(normalPostViewHolder.iv_cover_photo4);
            if (activity != null)
                Glide.with(activity.getApplicationContext()).load(photos.get(2).source).into(normalPostViewHolder.iv_cover_photo5);
            if (activity != null)
                Glide.with(activity.getApplicationContext()).load(photos.get(3).source).into(normalPostViewHolder.iv_cover_photo6);
            normalPostViewHolder.tv_more_images.setVisibility(View.GONE);

        } else {
            normalPostViewHolder.iv_cover_photo1.setVisibility(View.VISIBLE);
            if (activity != null)
                Glide.with(activity.getApplicationContext()).load(photos.get(0).source).into(normalPostViewHolder.iv_cover_photo1);
            normalPostViewHolder.ll_two_images.setVisibility(View.GONE);
            normalPostViewHolder.ll_three_images.setVisibility(View.VISIBLE);
            if (activity != null)
                Glide.with(activity.getApplicationContext()).load(photos.get(1).source).into(normalPostViewHolder.iv_cover_photo4);
            if (activity != null)
                Glide.with(activity.getApplicationContext()).load(photos.get(2).source).into(normalPostViewHolder.iv_cover_photo5);
            if (activity != null)
                Glide.with(activity.getApplicationContext()).load(photos.get(3).source).into(normalPostViewHolder.iv_cover_photo6);
            normalPostViewHolder.tv_more_images.setVisibility(View.VISIBLE);
        }
    }

    private void configNormalPostViewHolder(final NormalPostViewHolder normalPostViewHolder, final int position) {
        final PojoGetNewsFeedResponseData currentItem = newsFeedList.get(position);
        normalPostViewHolder.rl_user_comment_wrapper.setVisibility(View.GONE);

        List<PojoNewsFeedPhoto> photos = currentItem.photos;

        String userToShow = "";
        normalPostViewHolder.rl_web_preview_wrapper.setVisibility(View.GONE);
        {
            if (currentItem.postType.equals("post_like") || currentItem.postType.equals("shared")
                    || currentItem.postType.equals("post_comment")) {
                normalPostViewHolder.rl_top_bar_wrapper.setVisibility(View.VISIBLE);
                // userToShow=currentItem.liked_by.userFullname;
                userToShow = "<b>" + userToShow + "</b>";
                if (!currentItem.total.equals("0")) {
                    userToShow = userToShow + " " + activity.getString(R.string.and) + " " + currentItem.total +
                            " " + activity.getString(R.string.others);
                }
                if (currentItem.postType.equals("post_like")) {
                    normalPostViewHolder.tv_activity_performed.setText(Html.fromHtml(userToShow + " "
                            + activity.getString(R.string.liked)));
                } else if (currentItem.postType.equals("post_comment")) {
                    normalPostViewHolder.tv_activity_performed.setText(Html.fromHtml(userToShow + " "
                            + activity.getString(R.string.commented_on)));
                }
            }

            normalPostViewHolder.tv_time_stamp.setText(commonFunctions
                    .parseNewsFeedItemTime(currentItem.updatedAt));


            if (currentItem.organizationData != null
                    && (currentItem.postType.equals("organisation_post") || currentItem.postType.equals("ad"))) {
                if (!currentItem.organizationData.logo.isEmpty()) {
                    if (activity != null)
                        Glide.with(activity.getApplicationContext()).load(currentItem.organizationData.logo)
                                .apply(bitmapTransform(new CropCircleTransformation()))
                                .into(normalPostViewHolder.iv_user_dp);
                }
                normalPostViewHolder.tv_poster_name.setText(currentItem.organizationData.name);
                normalPostViewHolder.tv_poster_designation.setText(currentItem.organizationData.typename);
            } else {
                if (!currentItem.postAuthorPicture.isEmpty()) {
                    if (activity != null)
                        Glide.with(activity.getApplicationContext()).load(currentItem.postAuthorPicture)
                                .apply(bitmapTransform(new CropCircleTransformation()))
                                .into(normalPostViewHolder.iv_user_dp);
                }
                normalPostViewHolder.tv_poster_name.setText(currentItem.userFullname);
                normalPostViewHolder.tv_poster_designation.setText(currentItem.userWorkTitle);
            }

            normalPostViewHolder.rl_top_bar_wrapper.setVisibility(View.GONE);
            boolean tagged;
            if (currentItem.tagged.equals("1")) {
                tagged = true;
            } else {
                tagged = false;
            }
            normalPostViewHolder.tv_post_content.setHighlightColor(
                    Color.TRANSPARENT); // prevent TextView change background when highlight
            normalPostViewHolder.tv_post_content.setMovementMethod(LinkMovementMethod.getInstance());
            currentItem.spannableClickItem = commonFunctions.setClickSpans(currentItem.text,
                    currentItem.textPlain, tagged, null);
            currentItem.clickableSpansList = currentItem.spannableClickItem.clickableSpansList;
            normalPostViewHolder.tv_post_content
                    .setText(currentItem.spannableClickItem.spannableString, TextView.BufferType.SPANNABLE);


            String url = "";

            for (int i = 0; i < currentItem.spannableClickItem.clickableUrls.size(); i++) {
                if (Patterns.WEB_URL.matcher(currentItem.spannableClickItem.clickableUrls.get(i)).matches()) {
                    url = currentItem.spannableClickItem.clickableUrls.get(i);
                    break;
                }
            }

            if (!url.isEmpty()) {
                if (currentItem.sourceContent != null) {
                    normalPostViewHolder.rl_web_preview_wrapper.setVisibility(View.GONE);
                    String title = currentItem.sourceContent.getTitle();
                    String name = currentItem.sourceContent.getCannonicalUrl();
                    String description = currentItem.sourceContent.getCannonicalUrl();
                    if (title.isEmpty() || name.isEmpty()) {
                        normalPostViewHolder.rl_web_preview_wrapper.setVisibility(View.GONE);
                    } else {
                        normalPostViewHolder.rl_web_preview_wrapper.setVisibility(View.VISIBLE);
                        normalPostViewHolder.tv_link_title.setText(currentItem.sourceContent.getTitle());
                        normalPostViewHolder.tv_website_name.setText(currentItem.sourceContent.getCannonicalUrl());
                        normalPostViewHolder.tv_link_description.setText(currentItem.sourceContent.getDescription());
                        for (int i = 0; i < currentItem.sourceContent.getImages().size(); i++) {
                            if (currentItem.sourceContent.getImages().get(i).contains("png")
                                    || currentItem.sourceContent.getImages().get(i).contains("jpg")) {
                                if (activity != null)
                                    Glide.with(activity.getApplicationContext())
                                            .load(currentItem.sourceContent.getImages().get(i))
                                            .into(normalPostViewHolder.iv_link_image);
                                break;
                            }
                        }
                    }
                } else {
                    normalPostViewHolder.textCrawler = new TextCrawler();
                    LinkPreviewCallback linkPreviewCallback = new LinkPreviewCallback() {
                        @Override
                        public void onPre() {
                            // Any work that needs to be done before generating the preview. Usually inflate
                            // your custom preview layout here.
                            //commonFunctions.setToastMessage(activity,"link preview called",Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);
                            normalPostViewHolder.rl_web_preview_wrapper.setVisibility(View.GONE);
                        }

                        @Override
                        public void onPos(SourceContent sourceContent, boolean b) {
                            currentItem.sourceContent = sourceContent;
                            // Populate your preview layout with the results of sourceContent.
                            String title = sourceContent.getTitle();
                            String name = sourceContent.getCannonicalUrl();
                            String description = sourceContent.getCannonicalUrl();

                            if (title.isEmpty() || name.isEmpty()) {
                                normalPostViewHolder.rl_web_preview_wrapper.setVisibility(View.GONE);
                            } else {
                                normalPostViewHolder.rl_web_preview_wrapper.setVisibility(View.VISIBLE);
                                normalPostViewHolder.tv_link_title.setText(sourceContent.getTitle());
                                normalPostViewHolder.tv_website_name.setText(sourceContent.getCannonicalUrl());
                                normalPostViewHolder.tv_link_description.setText(sourceContent.getDescription());
                                for (int i = 0; i < sourceContent.getImages().size(); i++) {
                                    if (sourceContent.getImages().get(i).contains("png")
                                            || sourceContent.getImages().get(i).contains("jpg")) {
                                        Glide.with(activity)
                                                .load(sourceContent.getImages().get(i))
                                                .into(normalPostViewHolder.iv_link_image);
                                        break;
                                    }
                                }
                            }
                        }
                    };
                    normalPostViewHolder.textCrawler.makePreview(linkPreviewCallback, url);
                }
            }
        }


        if (photos == null || photos.size() == 0) {
            normalPostViewHolder.iv_cover_photo1.setVisibility(View.GONE);
            normalPostViewHolder.ll_two_images.setVisibility(View.GONE);
            normalPostViewHolder.ll_three_images.setVisibility(View.GONE);
        } else if (photos.size() == 1) {
            normalPostViewHolder.iv_cover_photo1.setVisibility(View.VISIBLE);
            int height = imageFixedHeight;
            int width = imageFixedWidth;
            if (photos.get(0).imgDimensions != null && !photos.get(0).imgDimensions.isEmpty()) {
                ImageDimensions dimen = getScaledDimensions(photos.get(0).imgDimensions, AppKeys.NORMAL);
                height = dimen.height;
                width = dimen.width;
                normalPostViewHolder.iv_cover_photo1.getLayoutParams().height = height;
                normalPostViewHolder.iv_cover_photo1.getLayoutParams().width = width;
            }
            if (activity != null) Glide.with(activity.getApplicationContext())
                    .load(photos.get(0).source)
                    .apply(new RequestOptions().override(width, height))
                    .into(normalPostViewHolder.iv_cover_photo1);
            normalPostViewHolder.ll_two_images.setVisibility(View.GONE);
            normalPostViewHolder.ll_three_images.setVisibility(View.GONE);
        } else if (photos.size() == 2) {
            normalPostViewHolder.iv_cover_photo1.setVisibility(View.GONE);
            normalPostViewHolder.ll_two_images.setVisibility(View.VISIBLE);
            if (activity != null)
                Glide.with(activity.getApplicationContext()).load(photos.get(0).source).into(normalPostViewHolder.iv_cover_photo2);
            if (activity != null)
                Glide.with(activity.getApplicationContext()).load(photos.get(1).source).into(normalPostViewHolder.iv_cover_photo3);
            normalPostViewHolder.ll_three_images.setVisibility(View.GONE);
        } else if (photos.size() == 3) {
            normalPostViewHolder.iv_cover_photo1.setVisibility(View.VISIBLE);
            if (activity != null)
                Glide.with(activity.getApplicationContext()).load(photos.get(0).source).into(normalPostViewHolder.iv_cover_photo1);
            normalPostViewHolder.ll_two_images.setVisibility(View.VISIBLE);
            if (activity != null)
                Glide.with(activity.getApplicationContext()).load(photos.get(1).source).into(normalPostViewHolder.iv_cover_photo2);
            if (activity != null)
                Glide.with(activity.getApplicationContext()).load(photos.get(2).source).into(normalPostViewHolder.iv_cover_photo3);
            normalPostViewHolder.ll_three_images.setVisibility(View.GONE);
        } else if (photos.size() == 4) {
            normalPostViewHolder.iv_cover_photo1.setVisibility(View.VISIBLE);
            if (activity != null)
                Glide.with(activity.getApplicationContext()).load(photos.get(0).source).into(normalPostViewHolder.iv_cover_photo1);
            normalPostViewHolder.ll_two_images.setVisibility(View.GONE);
            normalPostViewHolder.ll_three_images.setVisibility(View.VISIBLE);
            if (activity != null)
                Glide.with(activity.getApplicationContext()).load(photos.get(1).source).into(normalPostViewHolder.iv_cover_photo4);
            if (activity != null)
                Glide.with(activity.getApplicationContext()).load(photos.get(2).source).into(normalPostViewHolder.iv_cover_photo5);
            if (activity != null)
                Glide.with(activity.getApplicationContext()).load(photos.get(3).source).into(normalPostViewHolder.iv_cover_photo6);
            normalPostViewHolder.tv_more_images.setVisibility(View.GONE);

        } else {
            normalPostViewHolder.iv_cover_photo1.setVisibility(View.VISIBLE);
            if (activity != null)
                Glide.with(activity.getApplicationContext()).load(photos.get(0).source).into(normalPostViewHolder.iv_cover_photo1);
            normalPostViewHolder.ll_two_images.setVisibility(View.GONE);
            normalPostViewHolder.ll_three_images.setVisibility(View.VISIBLE);
            if (activity != null)
                Glide.with(activity.getApplicationContext()).load(photos.get(1).source).into(normalPostViewHolder.iv_cover_photo4);
            if (activity != null)
                Glide.with(activity.getApplicationContext()).load(photos.get(2).source).into(normalPostViewHolder.iv_cover_photo5);
            if (activity != null)
                Glide.with(activity.getApplicationContext()).load(photos.get(3).source).into(normalPostViewHolder.iv_cover_photo6);
            normalPostViewHolder.tv_more_images.setVisibility(View.VISIBLE);
        }

    }

    private void configActionNormalPostViewHolder(final NormalPostViewHolder normalPostViewHolder, final int position) {
        final PojoGetNewsFeedResponseData currentItem = newsFeedList.get(position);
        normalPostViewHolder.rl_user_comment_wrapper.setVisibility(View.GONE);
        List<PojoNewsFeedPhoto> photos = currentItem.origin.photos;
        normalPostViewHolder.rl_web_preview_wrapper.setVisibility(View.GONE);

        //creator data
        normalPostViewHolder.tv_time_stamp.setText(commonFunctions
                .parseNewsFeedItemTime(currentItem.origin.updatedAt));

        if (currentItem.origin.organizationData != null
                && (currentItem.origin.postType.equals("organisation_post") || currentItem.origin.postType.equals("ad"))) {
            if (!currentItem.origin.organizationData.logo.isEmpty()) {
                if (activity != null)
                    Glide.with(activity.getApplicationContext()).load(currentItem.origin.organizationData.logo)
                            .apply(bitmapTransform(new CropCircleTransformation()))
                            .into(normalPostViewHolder.iv_user_dp);
            }
            normalPostViewHolder.tv_poster_name.setText(currentItem.origin.organizationData.name);
            normalPostViewHolder.tv_poster_designation.setText(currentItem.origin.organizationData.typename);
        } else {
            normalPostViewHolder.tv_poster_name.setText(currentItem.origin.userFullname);
            normalPostViewHolder.tv_poster_designation.setText(currentItem.origin.userWorkTitle);
            if (!currentItem.origin.postAuthorPicture.isEmpty()) {
                if (activity != null)
                    Glide.with(activity.getApplicationContext()).load(currentItem.origin.postAuthorPicture)
                            .apply(bitmapTransform(new CropCircleTransformation()))
                            .into(normalPostViewHolder.iv_user_dp);
            }
        }

        Log.d("newsfeed", currentItem.postId);
        boolean tagged;
        if (currentItem.origin.tagged.equals("1")) {
            tagged = true;
        } else {
            tagged = false;
        }

        normalPostViewHolder.tv_post_content.setHighlightColor(
                Color.TRANSPARENT); // prevent TextView change background when highlight
        normalPostViewHolder.tv_post_content.setMovementMethod(LinkMovementMethod.getInstance());
        currentItem.spannableClickItem = commonFunctions.setClickSpans(currentItem.origin.text,
                currentItem.origin.textPlain, tagged, null);
        currentItem.clickableSpansList = currentItem.spannableClickItem.clickableSpansList;
        normalPostViewHolder.tv_post_content
                .setText(currentItem.spannableClickItem.spannableString, TextView.BufferType.SPANNABLE);
        String url = "";

        for (int i = 0; i < currentItem.spannableClickItem.clickableUrls.size(); i++) {
            String currentUrl = currentItem.spannableClickItem.clickableUrls.get(i);
            if (!currentUrl.contains(AppKeys.WEBSITE_URL)
                    && Patterns.WEB_URL.matcher(currentUrl).matches()) {
                url = currentItem.spannableClickItem.clickableUrls.get(i);
                break;
            }
        }

        if (!url.isEmpty()) {
            if (currentItem.sourceContent != null) {
                normalPostViewHolder.rl_web_preview_wrapper.setVisibility(View.GONE);
                String title = currentItem.sourceContent.getTitle();
                String name = currentItem.sourceContent.getCannonicalUrl();
                String description = currentItem.sourceContent.getCannonicalUrl();
                if (title.isEmpty() || name.isEmpty()) {
                    normalPostViewHolder.rl_web_preview_wrapper.setVisibility(View.GONE);
                } else {
                    normalPostViewHolder.rl_web_preview_wrapper.setVisibility(View.VISIBLE);
                    normalPostViewHolder.tv_link_title.setText(currentItem.sourceContent.getTitle());
                    normalPostViewHolder.tv_website_name.setText(currentItem.sourceContent.getCannonicalUrl());
                    normalPostViewHolder.tv_link_description.setText(currentItem.sourceContent.getDescription());
                    for (int i = 0; i < currentItem.sourceContent.getImages().size(); i++) {
                        if (currentItem.sourceContent.getImages().get(i).contains("png")
                                || currentItem.sourceContent.getImages().get(i).contains("jpg")) {
                            Glide.with(activity)
                                    .load(currentItem.sourceContent.getImages().get(i))
                                    .into(normalPostViewHolder.iv_link_image);
                            break;
                        }
                    }

                }
            } else {
                normalPostViewHolder.textCrawler = new TextCrawler();
                LinkPreviewCallback linkPreviewCallback = new LinkPreviewCallback() {
                    @Override
                    public void onPre() {
                        // Any work that needs to be done before generating the preview. Usually inflate
                        // your custom preview layout here.
                        //commonFunctions.setToastMessage(activity,"link preview called",Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);
                        normalPostViewHolder.rl_web_preview_wrapper.setVisibility(View.GONE);
                    }

                    @Override
                    public void onPos(SourceContent sourceContent, boolean b) {
                        currentItem.sourceContent = sourceContent;
                        // Populate your preview layout with the results of sourceContent.
                        String title = sourceContent.getTitle();
                        String name = sourceContent.getCannonicalUrl();
                        String description = sourceContent.getCannonicalUrl();

                        if (title.isEmpty() || name.isEmpty()) {
                            normalPostViewHolder.rl_web_preview_wrapper.setVisibility(View.GONE);
                        } else {
                            normalPostViewHolder.rl_web_preview_wrapper.setVisibility(View.VISIBLE);
                            normalPostViewHolder.tv_link_title.setText(sourceContent.getTitle());
                            normalPostViewHolder.tv_website_name.setText(sourceContent.getCannonicalUrl());
                            normalPostViewHolder.tv_link_description.setText(sourceContent.getDescription());
                            for (int i = 0; i < sourceContent.getImages().size(); i++) {
                                if (sourceContent.getImages().get(i).contains("png")
                                        || sourceContent.getImages().get(i).contains("jpg")) {
                                    if (activity != null)
                                        Glide.with(activity.getApplicationContext())
                                                .load(sourceContent.getImages().get(i))
                                                .into(normalPostViewHolder.iv_link_image);
                                    break;
                                }
                            }

                        }
                    }
                };
                normalPostViewHolder.textCrawler.makePreview(linkPreviewCallback, url);
            }


        }


        if (currentItem.origin.iLike == 1) {
            normalPostViewHolder.tv_like_button.setTextColor(ContextCompat.getColor(activity, R.color.app_theme_medium));
        } else {
            normalPostViewHolder.tv_like_button.setTextColor(ContextCompat.getColor(activity, R.color.med_grey));
        }

        if (photos == null || photos.size() == 0) {
            normalPostViewHolder.iv_cover_photo1.setVisibility(View.GONE);
            normalPostViewHolder.ll_two_images.setVisibility(View.GONE);
            normalPostViewHolder.ll_three_images.setVisibility(View.GONE);
        } else if (photos.size() == 1) {
            normalPostViewHolder.iv_cover_photo1.setVisibility(View.VISIBLE);
            normalPostViewHolder.iv_cover_photo1.setVisibility(View.VISIBLE);
            int height = imageFixedHeight;
            int width = imageFixedWidth;
            if (photos.get(0).imgDimensions != null && !photos.get(0).imgDimensions.isEmpty()) {
                ImageDimensions dimen = getScaledDimensions(photos.get(0).imgDimensions, AppKeys.NORMAL);
                height = dimen.height;
                width = dimen.width;
                normalPostViewHolder.iv_cover_photo1.getLayoutParams().height = height;
                normalPostViewHolder.iv_cover_photo1.getLayoutParams().width = width;
            }
            if (activity != null) Glide.with(activity.getApplicationContext())
                    .load(photos.get(0).source)
                    .apply(new RequestOptions().override(width, height))
                    .into(normalPostViewHolder.iv_cover_photo1);

            normalPostViewHolder.ll_two_images.setVisibility(View.GONE);
            normalPostViewHolder.ll_three_images.setVisibility(View.GONE);
        } else if (photos.size() == 2) {
            normalPostViewHolder.iv_cover_photo1.setVisibility(View.GONE);
            normalPostViewHolder.ll_two_images.setVisibility(View.VISIBLE);
            if (activity != null)
                Glide.with(activity.getApplicationContext()).load(photos.get(0).source).into(normalPostViewHolder.iv_cover_photo2);
            if (activity != null)
                Glide.with(activity.getApplicationContext()).load(photos.get(1).source).into(normalPostViewHolder.iv_cover_photo3);
            normalPostViewHolder.ll_three_images.setVisibility(View.GONE);
        } else if (photos.size() == 3) {
            normalPostViewHolder.iv_cover_photo1.setVisibility(View.VISIBLE);
            if (activity != null)
                Glide.with(activity.getApplicationContext()).load(photos.get(0).source).into(normalPostViewHolder.iv_cover_photo1);
            normalPostViewHolder.ll_two_images.setVisibility(View.VISIBLE);
            if (activity != null)
                Glide.with(activity.getApplicationContext()).load(photos.get(1).source).into(normalPostViewHolder.iv_cover_photo2);
            if (activity != null)
                Glide.with(activity.getApplicationContext()).load(photos.get(2).source).into(normalPostViewHolder.iv_cover_photo3);
            normalPostViewHolder.ll_three_images.setVisibility(View.GONE);
        } else if (photos.size() == 4) {
            normalPostViewHolder.iv_cover_photo1.setVisibility(View.VISIBLE);
            if (activity != null)
                Glide.with(activity.getApplicationContext()).load(photos.get(0).source).into(normalPostViewHolder.iv_cover_photo1);
            normalPostViewHolder.ll_two_images.setVisibility(View.GONE);
            normalPostViewHolder.ll_three_images.setVisibility(View.VISIBLE);
            if (activity != null)
                Glide.with(activity.getApplicationContext()).load(photos.get(1).source).into(normalPostViewHolder.iv_cover_photo4);
            if (activity != null)
                Glide.with(activity.getApplicationContext()).load(photos.get(2).source).into(normalPostViewHolder.iv_cover_photo5);
            if (activity != null)
                Glide.with(activity.getApplicationContext()).load(photos.get(3).source).into(normalPostViewHolder.iv_cover_photo6);
            normalPostViewHolder.tv_more_images.setVisibility(View.GONE);

        } else {
            normalPostViewHolder.iv_cover_photo1.setVisibility(View.VISIBLE);
            if (activity != null)
                Glide.with(activity.getApplicationContext()).load(photos.get(0).source).into(normalPostViewHolder.iv_cover_photo1);
            normalPostViewHolder.ll_two_images.setVisibility(View.GONE);
            normalPostViewHolder.ll_three_images.setVisibility(View.VISIBLE);
            if (activity != null)
                Glide.with(activity.getApplicationContext()).load(photos.get(1).source).into(normalPostViewHolder.iv_cover_photo4);
            if (activity != null)
                Glide.with(activity.getApplicationContext()).load(photos.get(2).source).into(normalPostViewHolder.iv_cover_photo5);
            if (activity != null)
                Glide.with(activity.getApplicationContext()).load(photos.get(3).source).into(normalPostViewHolder.iv_cover_photo6);
            normalPostViewHolder.tv_more_images.setVisibility(View.VISIBLE);
        }

    }


    private void configVenuePostViewHolder(VenuePostViewHolder venuePostViewHolder, final int position) {
        final PojoGetNewsFeedResponseData currentItem = newsFeedList.get(position);

        if (!currentItem.origin.coverPhoto.isEmpty()) {
            int height = imageFixedHeight;
            int width = imageFixedWidth;

            if (currentItem.origin.imgDimensions != null && !currentItem.origin.imgDimensions.isEmpty()) {
                ImageDimensions dimen = getScaledDimensions(currentItem.origin.imgDimensions, AppKeys.VENUE);
                height = dimen.height;
                width = dimen.width;
                venuePostViewHolder.iv_cover_photo.getLayoutParams().height = height;
                venuePostViewHolder.iv_cover_photo.getLayoutParams().width = width;
            }

           /* CardView.LayoutParams layoutParams=(CardView.LayoutParams)articleViewHolder.iv_cover_photo.getLayoutParams();
            layoutParams.height=height;
            layoutParams.width=width;
            articleViewHolder.iv_cover_photo.setLayoutParams(layoutParams);*/

            if (activity != null) Glide.with(activity.getApplicationContext())
                    .load(currentItem.origin.coverPhoto)
                    .apply(new RequestOptions().override(width, height))
                    .into(venuePostViewHolder.iv_cover_photo);
        }

        venuePostViewHolder.tv_venue_name.setText(currentItem.origin.venueName);
        venuePostViewHolder.rb_venue_rating.setRating(Float.parseFloat(currentItem.origin.totalRating));
        venuePostViewHolder.tv_session_count.setText("" + currentItem.origin.allSessions);
        venuePostViewHolder.tv_follower_count.setText("" + currentItem.origin.venueFollowers);
        venuePostViewHolder.tv_attendee_count.setText("" + currentItem.origin.allAttendees);

        if (currentItem.origin.iFollow == 1) {
            venuePostViewHolder.tv_follow_button.setTextColor(ContextCompat.getColor(activity, R.color.app_theme_medium));
        } else {
            venuePostViewHolder.tv_follow_button.setTextColor(ContextCompat.getColor(activity, R.color.med_grey));
        }

        String userToShow = currentItem.userFullname;

        if (!currentItem.total.equals("0")) {
            userToShow = userToShow + " " + activity.getString(R.string.and) + " " + currentItem.total +
                    " " + activity.getString(R.string.others);
        }

        venuePostViewHolder.tv_activity_performed.setText("" + userToShow + " " + activity.getString(R.string.shared));




    /*    if(currentItem.origin.iFollow==1){
            venuePostViewHolder.tv_follow_button.setTextColor(ContextCompat.getColor(activity,R.color.app_theme_medium));
        }
        else {
            venuePostViewHolder.tv_follow_button.setTextColor(ContextCompat.getColor(activity,R.color.med_grey));
        }*/

        venuePostViewHolder.tv_follow_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareNewsFeedAdapterCallback.shareNewsFeedItemClickCallback(position, currentItem, AppKeys.VENUE_FOLLOW);
            }
        });

        venuePostViewHolder.tv_share_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareNewsFeedAdapterCallback.shareNewsFeedItemClickCallback(position, currentItem, AppKeys.VENUE_SHARE);
            }
        });

        venuePostViewHolder.iv_cover_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent venueIntent = new Intent(activity, VenueActivity.class);
                venueIntent.putExtra("id", currentItem.originId);
                activity.startActivity(venueIntent);
            }
        });

    }

    private void configQuestionViewHolder(final QuestionViewHolder questionViewHolder, final int position) {
        final PojoGetNewsFeedResponseData currentItem = newsFeedList.get(position);

        questionViewHolder.tv_question.setText(Html.fromHtml(currentItem.origin.post));

        if (currentItem.origin.likes.equals("0")) {
            questionViewHolder.tv_num_upvotes.setVisibility(View.GONE);
        } else {
            questionViewHolder.tv_num_upvotes.setVisibility(View.VISIBLE);
        }
        questionViewHolder.tv_num_upvotes.setText("" + currentItem.origin.likes + " "
                + activity.getString(R.string.upvotes));

        if (currentItem.origin.totalAnswers == 0) {
            questionViewHolder.tv_num_answers.setVisibility(View.GONE);
        } else {
            questionViewHolder.tv_num_answers.setVisibility(View.VISIBLE);
        }
        questionViewHolder.tv_num_answers.setText("" + currentItem.origin.totalAnswers + " "
                + activity.getString(R.string.answers));

       /* if (currentItem.origin.followers == 0) {
            questionViewHolder.tv_num_followers.setVisibility(View.GONE);
        } else {
            questionViewHolder.tv_num_followers.setVisibility(View.VISIBLE);
        }
        questionViewHolder.tv_num_followers.setText("" + currentItem.origin.followers + " "
                + activity.getString(R.string.followers));*/

        questionViewHolder.ll_answer_holder.setVisibility(View.GONE);
        questionViewHolder.tv_question_time_stamp.setText(commonFunctions
                .parseNewsFeedItemTime(currentItem.origin.modifiedOn));

        if (currentItem.postType.equals("question_shared")) {
            questionViewHolder.tv_activity_performed.setText(currentItem.userFullname
                    + " " + activity.getString(R.string.shared_a_question));
        } else {
            questionViewHolder.tv_activity_performed.setText(currentItem.userFullname
                    + " " + activity.getString(R.string.asked_question_on) + " " + currentItem.sessionName);
        }

        questionViewHolder.ll_target_click_wrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, QuestionActivity.class);
                intent.putExtra("id", currentItem.originId);
                activity.startActivity(intent);
            }
        });

        if (currentItem.origin.iLike == 1) {
            questionViewHolder.tv_follow_button.setTextColor(ContextCompat.getColor(activity, R.color.app_theme_medium));
        } else {
            questionViewHolder.tv_follow_button.setTextColor(ContextCompat.getColor(activity, R.color.med_grey));
        }

        questionViewHolder.tv_follow_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareNewsFeedAdapterCallback.shareNewsFeedItemClickCallback(position, currentItem, AppKeys.QUESTION_UPVOTE);
            }
        });

        questionViewHolder.tv_share_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareNewsFeedAdapterCallback.shareNewsFeedItemClickCallback(position, currentItem, AppKeys.QUESTION_SHARE);
            }
        });


    }

    private void configArticleViewHolder(final ArticleViewHolder articleViewHolder, final int position) {
        final PojoGetNewsFeedResponseData currentItem = newsFeedList.get(position);


        articleViewHolder.tv_writer_name.setText(currentItem.origin.createdBy.userFullname);
        articleViewHolder.tv_writer_designation.setText(currentItem.origin.createdBy.userWorkTitle);
        articleViewHolder.tv_time_stamp.setText(commonFunctions.parseNewsFeedItemTime(currentItem.origin.addedOn));

        if (!currentItem.origin.createdBy.userPicture.isEmpty()) {
            if (activity != null)
                Glide.with(activity.getApplicationContext()).load(currentItem.origin.createdBy.userPicture)
                        .apply(new RequestOptions().override(96, 96))
                        .apply(bitmapTransform(new CropCircleTransformation()))
                        .into(articleViewHolder.iv_user_dp);
        }

        articleViewHolder.iv_user_dp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                articleViewHolder.tv_writer_name.callOnClick();
            }
        });

        articleViewHolder.tv_writer_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent profileIntent = new Intent(activity, ProfileInfoActivity.class);
                profileIntent.putExtra("target_user_id", currentItem.authorId);
                activity.startActivity(profileIntent);
            }
        });

        articleViewHolder.ll_target_click_wrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent articleIntent = new Intent(activity, ArticleActivity.class);
                if (currentItem.postType.equals("article")) {
                    articleIntent.putExtra("id", currentItem.origin.articlesId);
                } else {
                    articleIntent.putExtra("id", currentItem.originId);
                }
                activity.startActivity(articleIntent);
            }
        });

        if (!currentItem.origin.coverPhoto.isEmpty()) {
            int height = imageFixedHeight;
            int width = imageFixedWidth;

            if (currentItem.origin.imgDimensions != null && !currentItem.origin.imgDimensions.isEmpty()) {
                ImageDimensions dimen = getScaledDimensions(currentItem.origin.imgDimensions, AppKeys.ARTICLE);
                height = dimen.height;
                width = dimen.width;
                articleViewHolder.iv_cover_photo.getLayoutParams().height = height;
                articleViewHolder.iv_cover_photo.getLayoutParams().width = width;
            }

            if (activity != null)
                Glide.with(activity.getApplicationContext())
                        .load(currentItem.origin.coverPhoto)
                        .apply(new RequestOptions().override(width, height))
                        .into(articleViewHolder.iv_cover_photo);
        }


        String userToShow = currentItem.userFullname;

        if (!currentItem.total.equals("0")) {
            userToShow = userToShow + " " + activity.getString(R.string.and) + " " + currentItem.total +
                    " " + activity.getString(R.string.others);
        }

        if (currentItem.postType.equals("article_shared")) {
            articleViewHolder.tv_activity_performed.setText(userToShow
                    + " " + activity.getString(R.string.shared) + " " + activity.getString(R.string.article));
        } else if (currentItem.postType.equals("article_like")) {
            articleViewHolder.tv_activity_performed.setText(userToShow
                    + " " + activity.getString(R.string.liked) + " " + activity.getString(R.string.article));
        } else if (currentItem.postType.equals("article_comment")) {
            articleViewHolder.tv_activity_performed.setText(userToShow
                    + " " + activity.getString(R.string.commented_on) + " " + activity.getString(R.string.article));
        } else {
            articleViewHolder.rl_top_bar_wrapper.setVisibility(View.GONE);
        }


        articleViewHolder.tv_article_name.setText(Html.fromHtml(currentItem.origin.title));
        articleViewHolder.tv_like_number.setText("" + currentItem.origin.likes + " " + activity.getString(R.string.likes));
        articleViewHolder.tv_comment_number.setText("" + currentItem.origin.totalComments + " "
                + activity.getString(R.string.comments));
        articleViewHolder.tv_share_number.setText("" + currentItem.origin.shares + " " + activity.getString(R.string.shares));


        //used newsfeedlist.get as we are modifying this item
        if (currentItem.origin.iLike == 1) {
            articleViewHolder.tv_like.setTextColor(ContextCompat.getColor(activity, R.color.app_theme_medium));
        } else {
            articleViewHolder.tv_like.setTextColor(ContextCompat.getColor(activity, R.color.med_grey));
        }

        articleViewHolder.tv_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareNewsFeedAdapterCallback.shareNewsFeedItemClickCallback(position, currentItem,
                        AppKeys.ARTICLE_LIKE);
            }
        });

        articleViewHolder.tv_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareNewsFeedAdapterCallback.shareNewsFeedItemClickCallback(position, currentItem, AppKeys.ARTICLE_SHARE);
            }
        });

        articleViewHolder.tv_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent articleCommentIntent = new Intent(activity, ArticleActivity.class);
                articleCommentIntent.putExtra("id", currentItem.originId);
                articleCommentIntent.putExtra(AppKeys.ACTION, AppKeys.COMMENT);
                activity.startActivity(articleCommentIntent);
            }
        });

    }

    private void configActionSharedArticleViewHolder(final ArticleViewHolder articleViewHolder, final int position) {
        final PojoGetNewsFeedResponseData currentItem = newsFeedList.get(position);


        articleViewHolder.tv_writer_name.setText(currentItem.origin.createdBy.userFullname);
        articleViewHolder.tv_writer_designation.setText(currentItem.origin.createdBy.userWorkTitle);
        articleViewHolder.tv_time_stamp.setText(commonFunctions
                .parseNewsFeedItemTime(currentItem.origin.addedOn));

        if (!currentItem.origin.createdBy.userPicture.isEmpty()) {
            if (activity != null)
                Glide.with(activity.getApplicationContext()).load(currentItem.origin.createdBy.userPicture)
                        .apply(new RequestOptions().override(96, 96))
                        .apply(bitmapTransform(new CropCircleTransformation()))
                        .into(articleViewHolder.iv_user_dp);
        }

        articleViewHolder.iv_user_dp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                articleViewHolder.tv_writer_name.callOnClick();
            }
        });

        articleViewHolder.tv_writer_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent profileIntent = new Intent(activity, ProfileInfoActivity.class);
                profileIntent.putExtra("target_user_id", currentItem.authorId);
                activity.startActivity(profileIntent);
            }
        });


        articleViewHolder.tv_article_name.setText(currentItem.origin.title);

        if (!currentItem.origin.coverPhoto.isEmpty()) {
            int height = imageFixedHeight;
            int width = imageFixedWidth;

            if (currentItem.origin.imgDimensions != null && !currentItem.origin.imgDimensions.isEmpty()) {
                ImageDimensions dimen = getScaledDimensions(currentItem.origin.imgDimensions, AppKeys.ARTICLE);
                height = dimen.height;
                width = dimen.width;
                articleViewHolder.iv_cover_photo.getLayoutParams().height = height;
                articleViewHolder.iv_cover_photo.getLayoutParams().width = width;
            }

           /* CardView.LayoutParams layoutParams=(CardView.LayoutParams)articleViewHolder.iv_cover_photo.getLayoutParams();
            layoutParams.height=height;
            layoutParams.width=width;
            articleViewHolder.iv_cover_photo.setLayoutParams(layoutParams);*/

            if (activity != null) Glide.with(activity.getApplicationContext())
                    .load(currentItem.origin.coverPhoto)
                    .apply(new RequestOptions().override(width, height))
                    .into(articleViewHolder.iv_cover_photo);
        }

        String userToShow = currentItem.userFullname;

        if (currentItem.totalLikes == 0) {
            articleViewHolder.tv_like_number.setVisibility(View.GONE);
        } else {
            articleViewHolder.tv_like_number.setVisibility(View.VISIBLE);
        }
        articleViewHolder.tv_like_number.setText("" + currentItem.totalLikes + " " + activity.getString(R.string.likes));

        if (currentItem.comments.equals("0")) {
            articleViewHolder.tv_comment_number.setVisibility(View.GONE);
        } else {
            articleViewHolder.tv_comment_number.setVisibility(View.VISIBLE);
        }
        articleViewHolder.tv_comment_number.setText("" + currentItem.comments + " "
                + activity.getString(R.string.comments));
        articleViewHolder.tv_share_number.setText("" + currentItem.shares + " " + activity.getString(R.string.shares));

        articleViewHolder.rl_user_comment_wrapper.setVisibility(View.GONE);

    }

    private void configUpcomingSessionViewHolder(final UpcomingSessionViewHolder upcomingSessionViewHolder, final int position) {
        final PojoGetNewsFeedResponseData currentItem = newsFeedList.get(position);

        upcomingSessionViewHolder.ll_target_click_wrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sessionIntent = new Intent(activity, SessionLoaderActivity.class);
                sessionIntent.putExtra("id", currentItem.originId);
                activity.startActivity(sessionIntent);
            }
        });

        if (!currentItem.origin.coverPhoto.isEmpty()) {
            int height = imageFixedHeight;
            int width = imageFixedWidth;

            if (currentItem.origin.imgDimensions != null && !currentItem.origin.imgDimensions.isEmpty()) {
                ImageDimensions dimen = getScaledDimensions(currentItem.origin.imgDimensions, AppKeys.SESSION);
                height = dimen.height;
                width = dimen.width;
                upcomingSessionViewHolder.iv_session_photo.getLayoutParams().height = height;
                upcomingSessionViewHolder.iv_session_photo.getLayoutParams().width = width;
            }

           /* CardView.LayoutParams layoutParams=(CardView.LayoutParams)articleViewHolder.iv_cover_photo.getLayoutParams();
            layoutParams.height=height;
            layoutParams.width=width;
            articleViewHolder.iv_cover_photo.setLayoutParams(layoutParams);*/

            if (activity != null) Glide.with(activity.getApplicationContext())
                    .load(currentItem.origin.coverPhoto)
                    .apply(new RequestOptions().override(width, height))
                    .into(upcomingSessionViewHolder.iv_session_photo);

        }

        upcomingSessionViewHolder.tv_session_name.setText(Html.fromHtml(currentItem.origin.title));
        upcomingSessionViewHolder.tv_session_time_stamp.setText(commonFunctions
                .parseNewsFeedItemTime(currentItem.origin.modifiedOn));
        upcomingSessionViewHolder.tv_discussion_number.setText(currentItem.origin.discussionsCnt + " "
                + activity.getString(R.string.discussions));
        upcomingSessionViewHolder.tv_attending_number.setText(currentItem.origin.totalAttends + " "
                + activity.getString(R.string.attendees));
        upcomingSessionViewHolder.tv_shares_number.setText(currentItem.origin.totalShares + " "
                + activity.getString(R.string.shares));

        String userToShow = currentItem.userFullname;

        if (!currentItem.total.equals("0")) {
            userToShow = userToShow + " " + activity.getString(R.string.and) + " " + currentItem.total +
                    " " + activity.getString(R.string.others);
        }

        if (currentItem.postType.equals("session_attend")) {
            upcomingSessionViewHolder.tv_activity_performed.setText(userToShow + " "
                    + activity.getString(R.string.is_attending));

        } else if (currentItem.postType.equals("session_shared")) {
            upcomingSessionViewHolder.tv_activity_performed.setText(userToShow + " "
                    + activity.getString(R.string.shared));
        } else if (currentItem.postType.equals("session")) {
            upcomingSessionViewHolder.tv_activity_performed.setText(userToShow + " "
                    + activity.getString(R.string.initiated));
        }

        upcomingSessionViewHolder.tv_discuss_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sessionLoaderIntent = new Intent(activity, SessionLoaderActivity.class);
                sessionLoaderIntent.putExtra("id", currentItem.originId);
                sessionLoaderIntent.putExtra("orientation", AppKeys.DISCUSSION);
                activity.startActivity(sessionLoaderIntent);
            }
        });

        upcomingSessionViewHolder.tv_accept_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, SessionAcceptFormActivity.class);
                activity.startActivity(intent);
            }
        });

        upcomingSessionViewHolder.tv_share_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareNewsFeedAdapterCallback.shareNewsFeedItemClickCallback(position, currentItem, AppKeys.SESSION_SHARE);
            }
        });

        if (currentItem.origin.iAttend == 1) {
            upcomingSessionViewHolder.tv_attend_button.setTextColor(ContextCompat.getColor(activity,
                    R.color.app_theme_medium));
        } else {
            upcomingSessionViewHolder.tv_attend_button.setTextColor(ContextCompat.getColor(activity,
                    R.color.med_grey));
        }

        upcomingSessionViewHolder.tv_attend_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sessionIntent = new Intent(activity, SessionLoaderActivity.class);
                sessionIntent.putExtra("id", currentItem.originId);
                sessionIntent.putExtra("orientation", AppKeys.ATTEND);
                activity.startActivity(sessionIntent);

            }
        });

    }

    private void configAssignedSessionViewHolder(final AssignedSessionViewHolder assignedSessionViewHolder, final int position) {
        final PojoGetNewsFeedResponseData currentItem = newsFeedList.get(position);

        assignedSessionViewHolder.ll_target_click_wrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sessionIntent = new Intent(activity, SessionLoaderActivity.class);
                sessionIntent.putExtra("id", currentItem.originId);
                activity.startActivity(sessionIntent);
            }
        });

        if (!currentItem.origin.coverPhoto.isEmpty()) {
            int height = imageFixedHeight;
            int width = imageFixedWidth;

            if (currentItem.origin.imgDimensions != null && !currentItem.origin.imgDimensions.isEmpty()) {
                ImageDimensions dimen = getScaledDimensions(currentItem.origin.imgDimensions, AppKeys.SESSION);
                height = dimen.height;
                width = dimen.width;
                assignedSessionViewHolder.iv_session_photo.getLayoutParams().height = height;
                assignedSessionViewHolder.iv_session_photo.getLayoutParams().width = width;
            }

           /* CardView.LayoutParams layoutParams=(CardView.LayoutParams)articleViewHolder.iv_cover_photo.getLayoutParams();
            layoutParams.height=height;
            layoutParams.width=width;
            articleViewHolder.iv_cover_photo.setLayoutParams(layoutParams);*/

            if (activity != null) Glide.with(activity.getApplicationContext())
                    .load(currentItem.origin.coverPhoto)
                    .apply(new RequestOptions().override(width, height))
                    .into(assignedSessionViewHolder.iv_session_photo);
        }

        assignedSessionViewHolder.tv_session_name.setText(Html.fromHtml(currentItem.origin.title));

        assignedSessionViewHolder.tv_session_time_stamp.setText(commonFunctions
                .parseNewsFeedItemTime(currentItem.origin.modifiedOn));
        assignedSessionViewHolder.tv_discussion_number.setText(currentItem.origin.discussionsCnt + " "
                + activity.getString(R.string.discussions));
        assignedSessionViewHolder.tv_attending_number.setText(currentItem.origin.totalAttends + " "
                + activity.getString(R.string.attendees));


        String userToShow = currentItem.userFullname;

        if (!currentItem.total.equals("0")) {
            userToShow = userToShow + " " + activity.getString(R.string.and) + " " + currentItem.total +
                    " " + activity.getString(R.string.others);
        }

        if (currentItem.postType.equals("session_attend")) {
            assignedSessionViewHolder.tv_activity_performed.setText(userToShow + " "
                    + activity.getString(R.string.is_attending));

        } else if (currentItem.postType.equals("session_shared")) {
            assignedSessionViewHolder.tv_activity_performed.setText(userToShow + " "
                    + activity.getString(R.string.shared));
        } else if (currentItem.postType.equals("session")) {
            assignedSessionViewHolder.tv_activity_performed.setText(userToShow + " "
                    + activity.getString(R.string.initiated));
        }
        assignedSessionViewHolder.tv_activity_performed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, AssignedSessionActivity.class);
                activity.startActivity(intent);
            }
        });

        assignedSessionViewHolder.tv_attend_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                assignedSessionViewHolder.fl_attend_button.performClick();
            }
        });

        assignedSessionViewHolder.fl_attend_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent venuePollActivity = new Intent(activity, VenuePollActivity.class);
                activity.startActivity(venuePollActivity);
            }
        });


        if (currentItem.origin.iAttend == 1) {
            assignedSessionViewHolder.tv_attend_button.setTextColor(ContextCompat.getColor(activity,
                    R.color.app_theme_medium));
        } else {
            assignedSessionViewHolder.tv_attend_button.setTextColor(ContextCompat.getColor(activity,
                    R.color.med_grey));
        }

        assignedSessionViewHolder.tv_share_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareNewsFeedAdapterCallback.shareNewsFeedItemClickCallback(position, currentItem, AppKeys.SESSION_SHARE);
            }
        });

    }

    private void configOrganisedSessionViewHolder(OrganisedSessionViewHolder organisedSessionViewHolder, final int position) {
        final PojoGetNewsFeedResponseData currentItem = newsFeedList.get(position);

        organisedSessionViewHolder.ll_target_click_wrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sessionIntent = new Intent(activity, SessionLoaderActivity.class);
                sessionIntent.putExtra("id", currentItem.originId);
                activity.startActivity(sessionIntent);
            }
        });

        if (!currentItem.origin.coverPhoto.isEmpty()) {
            int height = imageFixedHeight;
            int width = imageFixedWidth;

            if (currentItem.origin.imgDimensions != null && !currentItem.origin.imgDimensions.isEmpty()) {
                ImageDimensions dimen = getScaledDimensions(currentItem.origin.imgDimensions, AppKeys.SESSION);
                height = dimen.height;
                width = dimen.width;
                organisedSessionViewHolder.iv_session_photo.getLayoutParams().height = height;
                organisedSessionViewHolder.iv_session_photo.getLayoutParams().width = width;
            }

           /* CardView.LayoutParams layoutParams=(CardView.LayoutParams)articleViewHolder.iv_cover_photo.getLayoutParams();
            layoutParams.height=height;
            layoutParams.width=width;
            articleViewHolder.iv_cover_photo.setLayoutParams(layoutParams);*/

            if (activity != null) Glide.with(activity.getApplicationContext())
                    .load(currentItem.origin.coverPhoto)
                    .apply(new RequestOptions().override(width, height))
                    .into(organisedSessionViewHolder.iv_session_photo);
        }

        organisedSessionViewHolder.tv_session_name.setText(Html.fromHtml(currentItem.origin.title));

        organisedSessionViewHolder.tv_session_time_stamp.setText(commonFunctions
                .parseNewsFeedItemTime(currentItem.origin.modifiedOn));
        organisedSessionViewHolder.tv_question_number.setText(currentItem.origin.qaDataCnt + " "
                + activity.getString(R.string.questions));
        organisedSessionViewHolder.tv_thank_number.setText(currentItem.origin.thanksCount + " "
                + activity.getString(R.string.thanks));

        String userToShow = currentItem.userFullname;

        if (!currentItem.total.equals("0")) {
            userToShow = userToShow + " " + activity.getString(R.string.and) + " " + currentItem.total +
                    " " + activity.getString(R.string.others);
        }

        if (currentItem.postType.equals("session_attend")) {
            organisedSessionViewHolder.tv_activity_performed.setText(userToShow + " "
                    + activity.getString(R.string.is_attending));

        } else if (currentItem.postType.equals("session_shared")) {
            organisedSessionViewHolder.tv_activity_performed.setText(userToShow + " "
                    + activity.getString(R.string.shared));
        } else if (currentItem.postType.equals("session")) {
            organisedSessionViewHolder.tv_activity_performed.setText(userToShow + " "
                    + activity.getString(R.string.initiated));
        }

        organisedSessionViewHolder.tv_question_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sessionLoaderIntent = new Intent(activity, SessionLoaderActivity.class);
                sessionLoaderIntent.putExtra("id", currentItem.originId);
                sessionLoaderIntent.putExtra("orientation", AppKeys.ASK_QUESTION);
                activity.startActivity(sessionLoaderIntent);
            }
        });


        if (currentItem.origin.iThanks == 1) {
            organisedSessionViewHolder.tv_thank_button.setTextColor(ContextCompat.getColor(activity,
                    R.color.app_theme_medium));
        } else {
            organisedSessionViewHolder.tv_thank_button.setTextColor(ContextCompat.getColor(activity,
                    R.color.med_grey));

        }

        organisedSessionViewHolder.tv_thank_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareNewsFeedAdapterCallback.shareNewsFeedItemClickCallback(position, currentItem, AppKeys.THANK);
            }
        });

        organisedSessionViewHolder.tv_share_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareNewsFeedAdapterCallback.shareNewsFeedItemClickCallback(position, currentItem, AppKeys.SESSION_SHARE);
            }
        });

    }


    @Override
    public int getItemCount() {
        return newsFeedList.size();
        //  return hierarchyList.size();
    }


    public class UpcomingSessionViewHolder extends RecyclerView.ViewHolder {
        TextView tv_activity_performed, tv_accept_button, tv_attend_button;
        LinearLayout ll_target_click_wrapper;
        LinearLayout ll_share_data_wrapper;
        LinearLayout ll_post_action_button_wrapper;
        FrameLayout fl_attend_button;
        ImageView iv_session_photo;
        TextView tv_session_name;
        TextView tv_session_time_stamp;
        TextView tv_discussion_number;
        TextView tv_shares_number;
        TextView tv_attending_number;
        TextView tv_discuss_button;
        TextView tv_share_button;

        RelativeLayout rl_user_comment_wrapper;
        RelativeLayout rl_top_bar_wrapper;
        TextView tv_share_title;

        ImageView iv_comment_dp;
        TextView tv_comment_user_name, tv_user_comment;


        String USER = "user";
        String OTHER = "other";
        String type = USER;
        boolean finalised = true;
        ImageView iv_more_options;


        // ArrayAdapter<String> spinnerAdapter;
        /*List<String> finalised_user = new ArrayList<>();
        List<String> finalised_other = new ArrayList<>();
        List<String> non_finalised_user = new ArrayList<>();
        List<String> non_finalsed_other = new ArrayList<>();*/


        public UpcomingSessionViewHolder(View itemView) {
            super(itemView);
            ll_target_click_wrapper = (LinearLayout) itemView.findViewById(R.id.ll_target_click_wrapper);
            ll_share_data_wrapper = (LinearLayout) itemView.findViewById(R.id.ll_share_data_wrapper);
            ll_share_data_wrapper.setVisibility(View.GONE);

            tv_activity_performed = (TextView) itemView.findViewById(R.id.tv_activity_performed);
            tv_accept_button = (TextView) itemView.findViewById(R.id.tv_accept_button);
            tv_accept_button.setVisibility(View.GONE);
            tv_attend_button = (TextView) itemView.findViewById(R.id.tv_attend_button);
            rl_top_bar_wrapper = (RelativeLayout) itemView.findViewById(R.id.rl_top_bar_wrapper);
            rl_top_bar_wrapper.setVisibility(View.GONE);
            tv_share_title = (TextView) itemView.findViewById(R.id.tv_share_title);
            tv_share_title.setVisibility(View.GONE);

            iv_session_photo = (ImageView) itemView.findViewById(R.id.iv_session_photo);
            tv_session_name = (TextView) itemView.findViewById(R.id.tv_session_name);
            tv_session_time_stamp = (TextView) itemView.findViewById(R.id.tv_session_time_stamp);
            tv_discussion_number = (TextView) itemView.findViewById(R.id.tv_discussion_number);
            tv_discussion_number.setVisibility(View.GONE);
            tv_shares_number = (TextView) itemView.findViewById(R.id.tv_shares_number);
            tv_attending_number = (TextView) itemView.findViewById(R.id.tv_attending_number);
            tv_attending_number.setVisibility(View.GONE);
            tv_discuss_button = (TextView) itemView.findViewById(R.id.tv_discuss_button);
            tv_share_button = (TextView) itemView.findViewById(R.id.tv_share_button);

            ll_post_action_button_wrapper = (LinearLayout) itemView.findViewById(R.id.ll_post_action_button_wrapper);
            ll_post_action_button_wrapper.setVisibility(View.GONE);

            iv_more_options = (ImageView) itemView.findViewById(R.id.iv_more_options);
            fl_attend_button = (FrameLayout) itemView.findViewById(R.id.fl_attend_button);
            iv_more_options.setVisibility(View.GONE);

            rl_user_comment_wrapper = (RelativeLayout) itemView.findViewById(R.id.rl_user_comment_wrapper);
            rl_user_comment_wrapper.setVisibility(View.GONE);
            iv_comment_dp = (ImageView) itemView.findViewById(R.id.iv_comment_dp);
            tv_comment_user_name = (TextView) itemView.findViewById(R.id.tv_comment_user_name);
            tv_user_comment = (TextView) itemView.findViewById(R.id.tv_user_comment);
        }

    }

    public class AssignedSessionViewHolder extends RecyclerView.ViewHolder {
        LinearLayout ll_share_data_wrapper;
        TextView tv_share_title;
        LinearLayout ll_target_click_wrapper, ll_post_action_button_wrapper;
        ImageView iv_session_photo;
        TextView tv_session_name;
        TextView tv_activity_performed;
        TextView tv_session_time_stamp;
        TextView tv_discussion_number;
        TextView tv_attending_number;
        TextView tv_attend_button;
        TextView tv_discussion_button;
        TextView tv_share_button;

        FrameLayout fl_attend_button;
        ImageView iv_more_options;

        ImageView iv_comment_dp;
        TextView tv_comment_user_name, tv_user_comment;
        RelativeLayout rl_user_comment_wrapper, rl_top_bar_wrapper;

        public AssignedSessionViewHolder(final View itemView) {
            super(itemView);
            ll_target_click_wrapper = (LinearLayout) itemView.findViewById(R.id.ll_target_click_wrapper);
            ll_share_data_wrapper = (LinearLayout) itemView.findViewById(R.id.ll_share_data_wrapper);
            ll_share_data_wrapper.setVisibility(View.GONE);
            tv_share_title = (TextView) itemView.findViewById(R.id.tv_share_title);
            tv_share_title.setVisibility(View.GONE);

            ll_post_action_button_wrapper = (LinearLayout) itemView.findViewById(R.id.ll_post_action_button_wrapper);
            ll_post_action_button_wrapper.setVisibility(View.GONE);
            iv_session_photo = (ImageView) itemView.findViewById(R.id.iv_session_photo);
            tv_session_name = (TextView) itemView.findViewById(R.id.tv_session_name);
            tv_activity_performed = (TextView) itemView.findViewById(R.id.tv_activity_performed);
            tv_session_time_stamp = (TextView) itemView.findViewById(R.id.tv_session_time_stamp);
            tv_discussion_number = (TextView) itemView.findViewById(R.id.tv_discussion_number);
            tv_discussion_number.setVisibility(View.GONE);
            tv_attending_number = (TextView) itemView.findViewById(R.id.tv_attending_number);
            tv_attending_number.setVisibility(View.GONE);
            tv_attend_button = (TextView) itemView.findViewById(R.id.tv_attend_button);
            tv_discussion_button = (TextView) itemView.findViewById(R.id.tv_discussion_button);
            tv_share_button = (TextView) itemView.findViewById(R.id.tv_share_button);

            fl_attend_button = (FrameLayout) itemView.findViewById(R.id.fl_attend_button);
            iv_more_options = (ImageView) itemView.findViewById(R.id.iv_more_options);
            iv_more_options.setVisibility(View.GONE);

            rl_top_bar_wrapper = (RelativeLayout) itemView.findViewById(R.id.rl_top_bar_wrapper);
            rl_top_bar_wrapper.setVisibility(View.GONE);
            rl_user_comment_wrapper = (RelativeLayout) itemView.findViewById(R.id.rl_user_comment_wrapper);
            rl_user_comment_wrapper.setVisibility(View.GONE);
            iv_comment_dp = (ImageView) itemView.findViewById(R.id.iv_comment_dp);
            tv_comment_user_name = (TextView) itemView.findViewById(R.id.tv_comment_user_name);
            tv_user_comment = (TextView) itemView.findViewById(R.id.tv_user_comment);

        }
    }

    public class OrganisedSessionViewHolder extends RecyclerView.ViewHolder {
        LinearLayout ll_target_click_wrapper, ll_post_action_button_wrapper;
        LinearLayout ll_share_data_wrapper;
        TextView tv_share_title;
        ImageView iv_session_photo;
        TextView tv_activity_performed;
        TextView tv_session_name;
        TextView tv_session_time_stamp;
        TextView tv_thank_number;
        TextView tv_question_number;
        TextView tv_comment_number;
        TextView tv_thank_button;
        TextView tv_comment_button;
        TextView tv_question_button;
        TextView tv_share_button;

        ImageView iv_more_options;
        ImageView iv_comment_dp;
        TextView tv_comment_user_name, tv_user_comment;
        RelativeLayout rl_user_comment_wrapper;


        public OrganisedSessionViewHolder(View itemView) {
            super(itemView);
            ll_target_click_wrapper = (LinearLayout) itemView.findViewById(R.id.ll_target_click_wrapper);
            ll_post_action_button_wrapper = (LinearLayout) itemView.findViewById(R.id.ll_post_action_button_wrapper);
            ll_share_data_wrapper = (LinearLayout) itemView.findViewById(R.id.ll_share_data_wrapper);
            ll_share_data_wrapper.setVisibility(View.GONE);
            tv_share_title = (TextView) itemView.findViewById(R.id.tv_share_title);
            tv_share_title.setVisibility(View.GONE);

            ll_post_action_button_wrapper.setVisibility(View.GONE);
            iv_session_photo = (ImageView) itemView.findViewById(R.id.iv_session_photo);
            tv_activity_performed = (TextView) itemView.findViewById(R.id.tv_activity_performed);
            tv_session_name = (TextView) itemView.findViewById(R.id.tv_session_name);
            tv_session_time_stamp = (TextView) itemView.findViewById(R.id.tv_session_time_stamp);
            tv_thank_number = (TextView) itemView.findViewById(R.id.tv_thank_number);
            tv_thank_number.setVisibility(View.GONE);
            tv_question_number = (TextView) itemView.findViewById(R.id.tv_question_number);
            tv_question_number.setVisibility(View.GONE);
            tv_comment_number = (TextView) itemView.findViewById(R.id.tv_comment_number);
            tv_thank_button = (TextView) itemView.findViewById(R.id.tv_thank_button);
            tv_comment_button = (TextView) itemView.findViewById(R.id.tv_comment_button);
            tv_question_button = (TextView) itemView.findViewById(R.id.tv_question_button);
            tv_share_button = (TextView) itemView.findViewById(R.id.tv_share_button);

            iv_more_options = (ImageView) itemView.findViewById(R.id.iv_more_options);
            iv_more_options.setVisibility(View.GONE);

            rl_user_comment_wrapper = (RelativeLayout) itemView.findViewById(R.id.rl_user_comment_wrapper);
            rl_user_comment_wrapper.setVisibility(View.GONE);
            iv_comment_dp = (ImageView) itemView.findViewById(R.id.iv_comment_dp);
            tv_comment_user_name = (TextView) itemView.findViewById(R.id.tv_comment_user_name);
            tv_user_comment = (TextView) itemView.findViewById(R.id.tv_user_comment);
        }
    }

    public class ArticleViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout rl_writer_wrapper;
        RelativeLayout rl_top_bar_wrapper;
        ImageView iv_user_dp;
        ImageView iv_cover_photo;
        TextView tv_writer_name;
        TextView tv_writer_designation;
        TextView tv_time_stamp;
        TextView tv_article_name;
        TextView tv_like_number;
        TextView tv_comment_number;
        TextView tv_share_number;
        TextView tv_like;
        TextView tv_comment;
        TextView tv_share;
        View comment_item_gap;

        TextView tv_activity_performed;
        ImageView iv_more_options;


        ImageView iv_comment_dp;
        TextView tv_comment_user_name, tv_user_comment;
        RelativeLayout rl_user_comment_wrapper;
        LinearLayout ll_target_click_wrapper, ll_likes_comments_number_wrapper,
                ll_post_action_button_wrapper;


        public ArticleViewHolder(View itemView) {
            super(itemView);
            rl_top_bar_wrapper = (RelativeLayout) itemView.findViewById(R.id.rl_top_bar_wrapper);
            rl_top_bar_wrapper.setVisibility(View.GONE);

            rl_writer_wrapper = (RelativeLayout) itemView.findViewById(R.id.rl_writer_wrapper);
            ll_target_click_wrapper = (LinearLayout) itemView.findViewById(R.id.ll_target_click_wrapper);
            ll_likes_comments_number_wrapper = (LinearLayout) itemView.findViewById(R.id.ll_likes_comments_number_wrapper);
            ll_likes_comments_number_wrapper.setVisibility(View.GONE);

            ll_post_action_button_wrapper = (LinearLayout) itemView.findViewById(R.id.ll_post_action_button_wrapper);
            ll_post_action_button_wrapper.setVisibility(View.GONE);
            iv_user_dp = (ImageView) itemView.findViewById(R.id.iv_user_dp);
            iv_cover_photo = (ImageView) itemView.findViewById(R.id.iv_cover_photo);
            tv_writer_name = (TextView) itemView.findViewById(R.id.tv_writer_name);
            tv_writer_designation = (TextView) itemView.findViewById(R.id.tv_writer_designation);
            tv_time_stamp = (TextView) itemView.findViewById(R.id.tv_time_stamp);
            tv_article_name = (TextView) itemView.findViewById(R.id.tv_article_name);
            tv_like_number = (TextView) itemView.findViewById(R.id.tv_like_number);
            tv_comment_number = (TextView) itemView.findViewById(R.id.tv_comment_number);
            tv_share_number = (TextView) itemView.findViewById(R.id.tv_share_number);
            tv_like = (TextView) itemView.findViewById(R.id.tv_like);
            tv_comment = (TextView) itemView.findViewById(R.id.tv_comment);
            tv_share = (TextView) itemView.findViewById(R.id.tv_share);
            comment_item_gap = (View) itemView.findViewById(R.id.comment_item_gap);
            tv_activity_performed = (TextView) itemView.findViewById(R.id.tv_activity_performed);
            iv_more_options = (ImageView) itemView.findViewById(R.id.iv_more_options);
            iv_more_options.setVisibility(View.GONE);
            rl_user_comment_wrapper = (RelativeLayout) itemView.findViewById(R.id.rl_user_comment_wrapper);
            rl_user_comment_wrapper.setVisibility(View.GONE);
            iv_comment_dp = (ImageView) itemView.findViewById(R.id.iv_comment_dp);
            tv_comment_user_name = (TextView) itemView.findViewById(R.id.tv_comment_user_name);
            tv_user_comment = (TextView) itemView.findViewById(R.id.tv_user_comment);

        }
    }

    public class QuestionViewHolder extends RecyclerView.ViewHolder {
        LinearLayout rl_top_bar_wrapper;
        LinearLayout ll_target_click_wrapper, ll_post_action_button_wrapper;
        TextView tv_question;
        TextView tv_question_time_stamp;
        TextView tv_num_followers;
        TextView tv_num_answers;
        TextView tv_num_upvotes;
        TextView tv_share_button;
        TextView tv_answer_button;
        TextView tv_follow_button;
        TextView tv_activity_performed;
        LinearLayout ll_answer_holder;
        String USER = "user";
        String OTHER = "other";
        String type = USER;
        ImageView iv_more_options;


        public QuestionViewHolder(View itemView) {
            super(itemView);
            tv_activity_performed = (TextView) itemView.findViewById(R.id.tv_activity_performed);
            tv_question = (TextView) itemView.findViewById(R.id.tv_question);
            tv_question_time_stamp = (TextView) itemView.findViewById(R.id.tv_question_time_stamp);
            tv_num_followers = (TextView) itemView.findViewById(R.id.tv_num_followers);
            tv_num_followers.setVisibility(View.GONE);
            tv_num_answers = (TextView) itemView.findViewById(R.id.tv_num_answers);
            tv_num_answers.setVisibility(View.GONE);
            tv_num_upvotes = (TextView) itemView.findViewById(R.id.tv_num_upvotes);
            tv_num_upvotes.setVisibility(View.GONE);
            tv_share_button = (TextView) itemView.findViewById(R.id.tv_share_button);
            tv_answer_button = (TextView) itemView.findViewById(R.id.tv_answer_button);
            tv_follow_button = (TextView) itemView.findViewById(R.id.tv_follow_button);
            ll_answer_holder = (LinearLayout) itemView.findViewById(R.id.ll_answer_holder);
            ll_target_click_wrapper = (LinearLayout) itemView.findViewById(R.id.ll_target_click_wrapper);
            ll_post_action_button_wrapper = (LinearLayout) itemView.findViewById(R.id.ll_post_action_button_wrapper);
            ll_post_action_button_wrapper.setVisibility(View.GONE);
            rl_top_bar_wrapper = (LinearLayout) itemView.findViewById(R.id.rl_top_bar_wrapper);
            rl_top_bar_wrapper.setVisibility(View.GONE);
            iv_more_options = (ImageView) itemView.findViewById(R.id.iv_more_options);
            iv_more_options = (ImageView) itemView.findViewById(R.id.iv_more_options);
            iv_more_options.setVisibility(View.GONE);

        }
    }

    public class NormalPostViewHolder extends RecyclerView.ViewHolder {
        TextView tv_activity_performed;
        TextView tv_poster_name;
        TextView tv_poster_designation;
        TextView tv_time_stamp;
        TextView tv_post_content;
        TextView tv_like_number;
        TextView tv_comment_number;
        TextView tv_like_button;
        TextView tv_comment_button;
        TextView tv_share_button;
        ImageView iv_comment_dp;
        TextView tv_comment_user_name, tv_user_comment;


        RelativeLayout rl_web_preview_wrapper;
        ImageView iv_link_image;
        TextView tv_link_title;
        TextView tv_website_name;
        TextView tv_link_description;
        TextCrawler textCrawler;

        RelativeLayout rl_top_bar_wrapper;

        ImageView iv_cover_photo1;
        ProportionalImageView iv_cover_photo2;
        ProportionalImageView iv_cover_photo3;
        ProportionalImageView iv_cover_photo4;
        ProportionalImageView iv_cover_photo5;
        ProportionalImageView iv_cover_photo6;
        TextView tv_more_images;
        ImageView iv_more_options;
        ImageView iv_user_dp;

        LinearLayout ll_two_images;
        LinearLayout ll_three_images;
        LinearLayout ll_content_wrapper, ll_post_action_button_wrapper;

        RelativeLayout rl_user_comment_wrapper;

        public NormalPostViewHolder(View itemView) {
            super(itemView);
            rl_top_bar_wrapper = (RelativeLayout) itemView.findViewById(R.id.rl_top_bar_wrapper);
            rl_top_bar_wrapper.setVisibility(View.GONE);

            tv_activity_performed = (TextView) itemView.findViewById(R.id.tv_activity_performed);
            tv_poster_name = (TextView) itemView.findViewById(R.id.tv_poster_name);
            tv_poster_designation = (TextView) itemView.findViewById(R.id.tv_poster_designation);
            iv_user_dp = (ImageView) itemView.findViewById(R.id.iv_user_dp);

            tv_time_stamp = (TextView) itemView.findViewById(R.id.tv_time_stamp);
            tv_post_content = (TextView) itemView.findViewById(R.id.tv_post_content);

            tv_like_number = (TextView) itemView.findViewById(R.id.tv_like_number);
            tv_like_number.setVisibility(View.GONE);
            tv_comment_number = (TextView) itemView.findViewById(R.id.tv_comment_number);
            tv_comment_number.setVisibility(View.GONE);
            tv_like_button = (TextView) itemView.findViewById(R.id.tv_like_button);
            tv_comment_button = (TextView) itemView.findViewById(R.id.tv_comment_button);
            tv_share_button = (TextView) itemView.findViewById(R.id.tv_share_button);

            rl_web_preview_wrapper = (RelativeLayout) itemView.findViewById(R.id.rl_web_preview_wrapper);
            rl_web_preview_wrapper.setVisibility(View.GONE);
            iv_link_image = (ImageView) itemView.findViewById(R.id.iv_link_image);
            tv_link_title = (TextView) itemView.findViewById(R.id.tv_link_title);
            tv_website_name = (TextView) itemView.findViewById(R.id.tv_website_name);
            tv_link_description = (TextView) itemView.findViewById(R.id.tv_link_description);

            iv_cover_photo1 = (ImageView) itemView.findViewById(R.id.iv_cover_photo1);
            iv_cover_photo2 = (ProportionalImageView) itemView.findViewById(R.id.iv_cover_photo2);
            iv_cover_photo3 = (ProportionalImageView) itemView.findViewById(R.id.iv_cover_photo3);
            iv_cover_photo4 = (ProportionalImageView) itemView.findViewById(R.id.iv_cover_photo4);
            iv_cover_photo5 = (ProportionalImageView) itemView.findViewById(R.id.iv_cover_photo5);
            iv_cover_photo6 = (ProportionalImageView) itemView.findViewById(R.id.iv_cover_photo6);
            tv_more_images = (TextView) itemView.findViewById(R.id.tv_more_images);
            ll_two_images = (LinearLayout) itemView.findViewById(R.id.ll_two_images);
            ll_three_images = (LinearLayout) itemView.findViewById(R.id.ll_three_images);
            ll_content_wrapper = (LinearLayout) itemView.findViewById(R.id.ll_content_wrapper);
            ll_post_action_button_wrapper = (LinearLayout) itemView.findViewById(R.id.ll_post_action_button_wrapper);
            ll_post_action_button_wrapper.setVisibility(View.GONE);
            iv_more_options = (ImageView) itemView.findViewById(R.id.iv_more_options);
            iv_more_options.setVisibility(View.GONE);

            rl_user_comment_wrapper = (RelativeLayout) itemView.findViewById(R.id.rl_user_comment_wrapper);
            rl_user_comment_wrapper.setVisibility(View.GONE);
            iv_comment_dp = (ImageView) itemView.findViewById(R.id.iv_comment_dp);
            tv_comment_user_name = (TextView) itemView.findViewById(R.id.tv_comment_user_name);
            tv_user_comment = (TextView) itemView.findViewById(R.id.tv_user_comment);
        }
    }

    public class InterestPostViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_more_options;
        RelativeLayout rl_top_bar_wrapper;

        LinearLayout ll_share_data_wrapper;
        TextView tv_activity_performed;
        ImageView iv_cover_photo;
        TextView tv_interest_name;
        TextView tv_interest_parent;
        TextView tv_time_stamp;
        TextView tv_description;
        TextView tv_num_followers;
        TextView tv_num_sessions;
        TextView tv_num_articles;
        TextView tv_num_questions;
        TextView tv_share_button;
        TextView tv_follow_button;
        ImageView iv_comment_dp;
        TextView tv_comment_user_name, tv_user_comment;
        RelativeLayout rl_user_comment_wrapper;
        LinearLayout ll_post_action_button_wrapper;

        public InterestPostViewHolder(View itemView) {
            super(itemView);
            iv_cover_photo = (ImageView) itemView.findViewById(R.id.iv_cover_photo);
            tv_activity_performed = (TextView) itemView.findViewById(R.id.tv_activity_performed);
            tv_interest_name = (TextView) itemView.findViewById(R.id.tv_interest_name);
            tv_interest_parent = (TextView) itemView.findViewById(R.id.tv_interest_parent);
            tv_time_stamp = (TextView) itemView.findViewById(R.id.tv_time_stamp);
            tv_description = (TextView) itemView.findViewById(R.id.tv_description);
            tv_num_followers = (TextView) itemView.findViewById(R.id.tv_num_followers);
            tv_num_sessions = (TextView) itemView.findViewById(R.id.tv_num_sessions);
            tv_num_articles = (TextView) itemView.findViewById(R.id.tv_num_articles);
            tv_num_questions = (TextView) itemView.findViewById(R.id.tv_num_questions);
            tv_share_button = (TextView) itemView.findViewById(R.id.tv_share_button);
            tv_follow_button = (TextView) itemView.findViewById(R.id.tv_follow_button);

            ll_post_action_button_wrapper = (LinearLayout) itemView.findViewById(R.id.ll_post_action_button_wrapper);
            ll_post_action_button_wrapper.setVisibility(View.GONE);
            iv_more_options = (ImageView) itemView.findViewById(R.id.iv_more_options);
            rl_top_bar_wrapper = (RelativeLayout) itemView.findViewById(R.id.rl_top_bar_wrapper);
            rl_top_bar_wrapper.setVisibility(View.GONE);
            ll_share_data_wrapper = (LinearLayout) itemView.findViewById(R.id.ll_share_data_wrapper);
            ll_share_data_wrapper.setVisibility(View.GONE);

            iv_more_options.setVisibility(View.GONE);

            rl_user_comment_wrapper = (RelativeLayout) itemView.findViewById(R.id.rl_user_comment_wrapper);
            rl_user_comment_wrapper.setVisibility(View.GONE);
            iv_comment_dp = (ImageView) itemView.findViewById(R.id.iv_comment_dp);
            tv_comment_user_name = (TextView) itemView.findViewById(R.id.tv_comment_user_name);
            tv_user_comment = (TextView) itemView.findViewById(R.id.tv_user_comment);

        }
    }

    public class VenuePostViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_more_options;
        RelativeLayout rl_top_bar_wrapper;
        LinearLayout ll_share_data_wrapper;
        ImageView iv_cover_photo;
        TextView tv_venue_name;
        RatingBar rb_venue_rating;
        TextView tv_session_count;
        TextView tv_follower_count;
        TextView tv_attendee_count;
        TextView tv_like_button;
        TextView tv_comment_button;
        TextView tv_follow_button;
        TextView tv_share_button;
        TextView tv_activity_performed;
        ImageView iv_comment_dp;
        TextView tv_comment_user_name, tv_user_comment;
        RelativeLayout rl_user_comment_wrapper;
        LinearLayout ll_post_action_button_wrapper;


        public VenuePostViewHolder(View itemView) {
            super(itemView);

            iv_cover_photo = (ImageView) itemView.findViewById(R.id.iv_cover_photo);
            tv_venue_name = (TextView) itemView.findViewById(R.id.tv_venue_name);
            rb_venue_rating = (RatingBar) itemView.findViewById(R.id.rb_venue_rating);
            tv_session_count = (TextView) itemView.findViewById(R.id.tv_session_count);
            tv_follower_count = (TextView) itemView.findViewById(R.id.tv_follower_count);
            tv_attendee_count = (TextView) itemView.findViewById(R.id.tv_attendee_count);
            tv_like_button = (TextView) itemView.findViewById(R.id.tv_like_button);
            tv_comment_button = (TextView) itemView.findViewById(R.id.tv_comment_button);
            tv_follow_button = (TextView) itemView.findViewById(R.id.tv_follow_button);
            tv_share_button = (TextView) itemView.findViewById(R.id.tv_share_button);
            tv_activity_performed = (TextView) itemView.findViewById(R.id.tv_activity_performed);
            ll_share_data_wrapper = (LinearLayout) itemView.findViewById(R.id.ll_share_data_wrapper);
            ll_share_data_wrapper.setVisibility(View.GONE);
            ll_post_action_button_wrapper = (LinearLayout) itemView.findViewById(R.id.ll_post_action_button_wrapper);
            ll_post_action_button_wrapper.setVisibility(View.GONE);
            iv_more_options = (ImageView) itemView.findViewById(R.id.iv_more_options);
            rl_top_bar_wrapper = (RelativeLayout) itemView.findViewById(R.id.rl_top_bar_wrapper);
            rl_top_bar_wrapper.setVisibility(View.GONE);

            iv_more_options.setVisibility(View.GONE);

            rl_user_comment_wrapper = (RelativeLayout) itemView.findViewById(R.id.rl_user_comment_wrapper);
            rl_user_comment_wrapper.setVisibility(View.GONE);
            iv_comment_dp = (ImageView) itemView.findViewById(R.id.iv_comment_dp);
            tv_comment_user_name = (TextView) itemView.findViewById(R.id.tv_comment_user_name);
            tv_user_comment = (TextView) itemView.findViewById(R.id.tv_user_comment);
        }
    }


    public class PollPostViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout rl_top_bar_wrapper;
        RecyclerView rv_poll_list;
        PollPostAdapter pollPostAdapter;
        ImageView iv_more_options;
        TextView tv_activity_performed;
        TextView tv_poll_title;
        TextView tv_time_stamp;
        TextView tv_like_number;
        TextView tv_comment_number;
        TextView tv_share_number;
        TextView tv_like_button;
        TextView tv_comment_button;
        TextView tv_share_button;
        ImageView iv_comment_dp;
        TextView tv_comment_user_name, tv_user_comment;
        RelativeLayout rl_user_comment_wrapper;
        List<PojoNewsFeedPollOption> optionList;
        LinearLayout ll_post_action_button_wrapper;


        public PollPostViewHolder(View itemView) {
            super(itemView);
            rl_top_bar_wrapper = (RelativeLayout) itemView.findViewById(R.id.rl_top_bar_wrapper);
            rl_top_bar_wrapper.setVisibility(View.GONE);
            tv_activity_performed = (TextView) itemView.findViewById(R.id.tv_activity_performed);
            tv_poll_title = (TextView) itemView.findViewById(R.id.tv_poll_title);
            tv_time_stamp = (TextView) itemView.findViewById(R.id.tv_time_stamp);
            tv_like_number = (TextView) itemView.findViewById(R.id.tv_like_number);
            tv_like_number.setVisibility(View.GONE);
            tv_comment_number = (TextView) itemView.findViewById(R.id.tv_comment_number);
            tv_comment_number.setVisibility(View.GONE);
            tv_share_number = (TextView) itemView.findViewById(R.id.tv_share_number);
            tv_like_button = (TextView) itemView.findViewById(R.id.tv_like_button);
            tv_comment_button = (TextView) itemView.findViewById(R.id.tv_comment_button);
            tv_share_button = (TextView) itemView.findViewById(R.id.tv_share_button);
            ll_post_action_button_wrapper = (LinearLayout) itemView.findViewById(R.id.ll_post_action_button_wrapper);
            ll_post_action_button_wrapper.setVisibility(View.GONE);

            optionList = new ArrayList<>();
            rv_poll_list = (RecyclerView) itemView.findViewById(R.id.rv_poll_list);
            if (fragment != null) {
                pollPostAdapter = new PollPostAdapter(fragment, optionList, "normal");

            } else if (activity != null) {
                pollPostAdapter = new PollPostAdapter(activity, optionList, "normal");

            }
            //using horizontal linearlayout as we want horizontal list
            rv_poll_list.setNestedScrollingEnabled(false);
            rv_poll_list.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
            rv_poll_list.setAdapter(pollPostAdapter);

            iv_more_options = (ImageView) itemView.findViewById(R.id.iv_more_options);
            iv_more_options = (ImageView) itemView.findViewById(R.id.iv_more_options);
            iv_more_options.setVisibility(View.GONE);

            rl_user_comment_wrapper = (RelativeLayout) itemView.findViewById(R.id.rl_user_comment_wrapper);
            rl_user_comment_wrapper.setVisibility(View.GONE);
            iv_comment_dp = (ImageView) itemView.findViewById(R.id.iv_comment_dp);
            tv_comment_user_name = (TextView) itemView.findViewById(R.id.tv_comment_user_name);
            tv_user_comment = (TextView) itemView.findViewById(R.id.tv_user_comment);
        }
    }

    public class AnswerViewHolder extends RecyclerView.ViewHolder {
        TextView tv_question;

        TextView tv_num_upvotes;
        TextView tv_num_replies;

        LinearLayout ll_answer_holder;
        TextView tv_answer_time_stamp;
        ImageView iv_user_dp;

        TextView tv_user_name;
        TextView tv_answer;


        public AnswerViewHolder(View itemView) {
            super(itemView);
            tv_question = (TextView) itemView.findViewById(R.id.tv_question);
            tv_num_upvotes = (TextView) itemView.findViewById(R.id.tv_num_upvotes);
            tv_num_upvotes.setVisibility(View.GONE);
            tv_num_replies = (TextView) itemView.findViewById(R.id.tv_num_replies);
            ll_answer_holder = (LinearLayout) itemView.findViewById(R.id.ll_answer_holder);
            iv_user_dp = (ImageView) itemView.findViewById(R.id.iv_user_dp);
            tv_user_name = (TextView) itemView.findViewById(R.id.tv_user_name);
            tv_answer = (TextView) itemView.findViewById(R.id.tv_answer);
            tv_answer_time_stamp = (TextView) itemView.findViewById(R.id.tv_answer_time_stamp);
        }
    }

  /*  public class PostViewHolder extends RecyclerView.ViewHolder {

        public PostViewHolder(View itemView) {
            super(itemView);
        }
    }*/

    public class SuggestedFriendsViewHolder extends RecyclerView.ViewHolder {
        RecyclerView rv_suggested_friends_list;
        SuggestedFriendsAdapter suggestedFriendsAdapter;

        public SuggestedFriendsViewHolder(View itemView) {
            super(itemView);
            suggestedFriendList = new ArrayList<>();
            hitSuggestFriendsListApi("0");
            rv_suggested_friends_list = (RecyclerView) itemView.findViewById(R.id.rv_suggested_friends_list);
            suggestedFriendsAdapter = new SuggestedFriendsAdapter(activity, suggestedFriendList, "hor");
            rv_suggested_friends_list.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
            rv_suggested_friends_list.setAdapter(suggestedFriendsAdapter);
        }

        private void hitSuggestFriendsListApi(String page) {
            String user_id = "51";
            Call<PojoGetSuggestedFriendListResponse> call = apiService.getSuggestedFriendListApi(user_id, page);
            call.enqueue(new Callback<PojoGetSuggestedFriendListResponse>() {
                @Override
                public void onResponse(Call<PojoGetSuggestedFriendListResponse> call, Response<PojoGetSuggestedFriendListResponse> response) {
                    String message = response.body().message;
                    if (response.body().data != null) {
                        suggestedFriendList.addAll(response.body().data);
                        suggestedFriendsAdapter.notifyDataSetChanged();
                    } else {
                        commonFunctions.setToastMessage(activity, "" + message, Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                    }
                }

                @Override
                public void onFailure(Call<PojoGetSuggestedFriendListResponse> call, Throwable t) {
                    // Log error here since request failed
                    commonFunctions.setToastMessage(activity, t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                }
            });
        }
    }

    public static interface ShareNewsFeedAdapterCallback {
        void shareNewsFeedItemClickCallback(int position, PojoGetNewsFeedResponseData currentItem, String type);
    }


    private void openUserListDialog(List<PojoUserData> userList) {
        TextView tv_title;
        ImageView iv_back;

        final Dialog userListDialog = new MyCustomThemeDialog(activity);
        userListDialog.setContentView(R.layout.contact_list_dialog);
        userListDialog.getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        ProgressBar pb_loading_users = (ProgressBar) userListDialog.findViewById(R.id.pb_loading_users);
        pb_loading_users.setVisibility(View.GONE);
        tv_title = userListDialog.findViewById(R.id.tv_title);
        tv_title.setText(activity.getString(R.string.upvotes));
        iv_back = (ImageView) userListDialog.findViewById(R.id.iv_back);
        iv_back.setVisibility(View.GONE);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userListDialog.dismiss();
            }
        });
        userListDialog.setTitle(activity.getString(R.string.upvotes));
        RecyclerView rv_list;

        rv_list = (RecyclerView) userListDialog.findViewById(R.id.rv_list);
        //rv_list.setPadding(16,32,16,32);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity,
                LinearLayoutManager.VERTICAL, false);
        rv_list.setLayoutManager(linearLayoutManager);
        SuggestedFriendsAdapter suggestedFriendsAdapter =
                new SuggestedFriendsAdapter(activity, userList, "ver");
        rv_list.setAdapter(suggestedFriendsAdapter);
        userListDialog.show();
    }

    public ImageDimensions getScaledDimensions(String imgDimen, String type) {
        ImageDimensions dimen = new ImageDimensions();
        int height = Integer.parseInt(imgDimen.substring(0, imgDimen.indexOf("X")));
        int width = Integer.parseInt(imgDimen.substring(imgDimen.indexOf("X") + 1, imgDimen.length()));
        float imgRatio = 1f * width / height;
        int targetWidth = (int) (displayMetrics.widthPixels * 0.9f);
        int targetHeight = (int) (1f * targetWidth / imgRatio);
        switch (type) {
            case AppKeys.ARTICLE:
                if (targetHeight > maxArticleImageHeight) {
                    targetHeight = maxArticleImageHeight;
                }
                break;

            case AppKeys.SESSION:
                if (targetHeight > maxSessionImageHeight) {
                    targetHeight = maxSessionImageHeight;
                }
                break;

            case AppKeys.NORMAL:
                if (targetHeight > maxSessionImageHeight) {
                    targetHeight = maxSessionImageHeight;
                }
                break;

        }

        dimen.width = targetWidth;
        dimen.height = targetHeight;
        return dimen;
    }
}
