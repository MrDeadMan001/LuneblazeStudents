package com.avadna.luneblaze.adapters.newsfeed;

import android.app.Activity;
import android.app.Dialog;

import com.avadna.luneblaze.activities.normalpost.NormalPostPhotoGalleryViewerActivity;
import com.avadna.luneblaze.activities.organisation.AdTargetAudienceActivity;
import com.avadna.luneblaze.activities.organisation.OrganisationProfileInfoActivity;
import com.avadna.luneblaze.adapters.SuggestedFriendsAdapter;
import com.avadna.luneblaze.adapters.newsfeed.ActionSharedArticleNewsFeedItem;
import com.avadna.luneblaze.adapters.newsfeed.ActionSharedNormalPostNewsFeedItem;
import com.avadna.luneblaze.adapters.newsfeed.ActionSharedPollNewsFeedItem;
import com.avadna.luneblaze.adapters.newsfeed.AnswerNewsFeedItem;
import com.avadna.luneblaze.adapters.newsfeed.ArticleNewsFeedItem;
import com.avadna.luneblaze.adapters.newsfeed.AssignedSessionNewsFeedItem;
import com.avadna.luneblaze.adapters.newsfeed.InterestNewsFeedItem;
import com.avadna.luneblaze.adapters.newsfeed.NormalPostNewsFeedItem;
import com.avadna.luneblaze.adapters.newsfeed.OrganisedSessionNewsFeedItem;
import com.avadna.luneblaze.adapters.newsfeed.PollNewsFeedItem;
import com.avadna.luneblaze.adapters.newsfeed.QuestionNewsFeedItem;
import com.avadna.luneblaze.adapters.newsfeed.UpcomingSessionNewsFeedItem;
import com.avadna.luneblaze.adapters.newsfeed.VenueNewsFeedItem;
import com.avadna.luneblaze.helperClasses.MyCustomThemeDialog;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;

import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Patterns;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.activities.article.SharedArticleActivity;
import com.avadna.luneblaze.activities.normalpost.SharedNormalPostActivity;
import com.avadna.luneblaze.activities.polls.PollActivity;
import com.avadna.luneblaze.activities.question.AnswerReplyListActivity;
import com.avadna.luneblaze.activities.article.ArticleActivity;
import com.avadna.luneblaze.activities.profileInfo.ProfileInfoActivity;
import com.avadna.luneblaze.activities.interest.InterestActivity;
import com.avadna.luneblaze.activities.normalpost.NormalPostActivity;
import com.avadna.luneblaze.activities.question.QuestionActivity;
import com.avadna.luneblaze.activities.venue.VenueActivity;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.ImageDimensions;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.pojo.pojoNewsFeed.PojoGetNewsFeedResponseData;
import com.avadna.luneblaze.pojo.pojoNewsFeed.PojoNewsFeedCommentsArrayItem;
import com.avadna.luneblaze.pojo.pojoNewsFeed.PojoNewsFeedPhoto;
import com.avadna.luneblaze.pojo.pojoNewsFeed.PojoNewsFeedPollOption;
import com.avadna.luneblaze.pojo.pojoQuestion.PojoUserData;
import com.avadna.luneblaze.pojo.pojoVenueDetails.PojoVenuePhoto;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;
import com.avadna.luneblaze.utils.LinkMovementMethodOverride;
import com.avadna.luneblaze.utils.ProportionalImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.leocardz.link.preview.library.LinkPreviewCallback;
import com.leocardz.link.preview.library.SourceContent;
import com.leocardz.link.preview.library.TextCrawler;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

/**
 * Created by Sunny on 27-12-2017.
 */

public class NewsFeedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    //  pivate List<String> hierarchyList;
    int listSize;
    Activity activity;
    Fragment fragment;
    PreferenceUtils preferenceUtils;
    String user_id;
    List<PojoGetNewsFeedResponseData> newsFeedList;
    NewsFeedAdapterCallback newsFeedAdapterCallback;
    CommonFunctions commonFunctions;
    DisplayMetrics displayMetrics;
    int imageFixedWidth;
    int imageFixedHeight;
   /*int maxArticleImageHeight;
    int maxSessionImageHeight;
    int maxNormalPostImageHeight;*/

    String userFixedname;
    boolean hideHeader = false;
    boolean hideFooter = false;


    boolean isListEmpty = false;

    boolean showProgressBar = true;

    boolean showNoMoreResults = false;
    String noMoreResultsMessage;


    UpcomingSessionNewsFeedItem upcomingSessionNewsFeedItem;
    AssignedSessionNewsFeedItem assignedSessionNewsFeedItem;
    OrganisedSessionNewsFeedItem organisedSessionNewsFeedItem;
    ArticleNewsFeedItem articleNewsFeedItem;
    QuestionNewsFeedItem questionNewsFeedItem;
    AnswerNewsFeedItem answerNewsFeedItem;
    NormalPostNewsFeedItem normalPostNewsFeedItem;
    PollNewsFeedItem pollNewsFeedItem;
    InterestNewsFeedItem interestNewsFeedItem;
    VenueNewsFeedItem venueNewsFeedItem;
    ActionSharedNormalPostNewsFeedItem actionSharedNormalPostNewsFeedItem;
    ActionSharedPollNewsFeedItem actionSharedPollNewsFeedItem;
    ActionSharedArticleNewsFeedItem actionSharedArticleNewsFeedItem;

    public NewsFeedAdapter(Fragment fragment, List<PojoGetNewsFeedResponseData> newsFeedList,
                           String userFixedname, boolean hideHeader, boolean hideFooter) {
        try {
            this.newsFeedAdapterCallback = ((NewsFeedAdapterCallback) fragment);
        } catch (ClassCastException e) {
            throw new ClassCastException("Fragment must implement NewsFeedAdapterCallback.");
        }

        this.activity = fragment.getActivity();
        this.fragment = fragment;
        this.listSize = listSize;
        this.newsFeedList = newsFeedList;
        this.userFixedname = userFixedname;
        this.hideHeader = hideHeader;
        this.hideFooter = hideFooter;
        displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        imageFixedWidth = (int) (displayMetrics.widthPixels);
        imageFixedHeight = (int) (displayMetrics.widthPixels * 0.6f);
        /*maxArticleImageHeight = (int) (displayMetrics.widthPixels * (3f / 5));
        maxSessionImageHeight = (int) (displayMetrics.widthPixels * (2f / 3));
        maxNormalPostImageHeight = (int) (displayMetrics.heightPixels * (5f / 6));*/
        preferenceUtils = new PreferenceUtils(activity);
        user_id = preferenceUtils.get_user_id();
        commonFunctions = new CommonFunctions(activity);

        //  this.hierarchyList = hierarchyList;
    }

    @Override
    public int getItemCount() {
        return newsFeedList.size() + 1;
        //  return hierarchyList.size();
    }

    public void setIsListEmpty(boolean status) {
        isListEmpty = status;
        notifyDataSetChanged();
    }

    public void setShowProgressBar(boolean status) {
        this.showProgressBar = status;
        notifyDataSetChanged();
    }

    public void setShowNoMoreResults(boolean status, String message) {
        this.showNoMoreResults = status;
        noMoreResultsMessage = message;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (isListEmpty) {
            return 20;
        } else if (position == newsFeedList.size()) {
            return 18;
        } else if (newsFeedList.get(position).isHidden) {
            return 19;
        } else {
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
                case "article_like":
                case "article_comment":
                    return 4;

                case "article_shared":
                    return 17;

                case "question":
                case "question_shared":
                    return 5;

                case "answer_shared":
                    return 7;

                case "photos":
                case "":
                case "organisation_post":
                case "ad":
                case "post_like":
                case "post_comment":
                    if (newsFeedList.get(position).origin != null) {
                        if (newsFeedList.get(position).origin.poll != null) {
                            return 13;
                        }
                        if (newsFeedList.get(position).origin.articlesId != null) {
                            return 14;
                        }
                        if (newsFeedList.get(position).origin.postType.equals("photos") ||
                                newsFeedList.get(position).origin.postType.equals("")
                                || newsFeedList.get(position).origin.postType.equals("post_comment") ||
                                newsFeedList.get(position).origin.postType.equals("post_like")) {
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
                    }
                    if (newsFeedList.get(position).origin.postType.equals("article")) {

                    } else {
                        return 6;
                    }

                case "suggested_users":
                    return 6;

                default:
                    return 21;
            }

        }

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        switch (viewType) {
            case 1:
                if (upcomingSessionNewsFeedItem == null) {
                    upcomingSessionNewsFeedItem = new UpcomingSessionNewsFeedItem(fragment, userFixedname);
                }
                return upcomingSessionNewsFeedItem.getViewHolder(parent);

               /* itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.verified_session_post, parent, false);
                return new UpcomingSessionViewHolder(itemView);*/

            case 2:
                if (assignedSessionNewsFeedItem == null) {
                    assignedSessionNewsFeedItem = new AssignedSessionNewsFeedItem(fragment, userFixedname);
                }
                return assignedSessionNewsFeedItem.getViewHolder(parent);

                /*itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.assigned_session_post, parent, false);
                return new AssignedSessionViewHolder(itemView);*/

            case 3:
                if (organisedSessionNewsFeedItem == null) {
                    organisedSessionNewsFeedItem = new OrganisedSessionNewsFeedItem(fragment, userFixedname);
                }
                return organisedSessionNewsFeedItem.getViewHolder(parent);

                /*itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.organised_session_post, parent, false);
                return new OrganisedSessionViewHolder(itemView);*/

            case 4:
                if (articleNewsFeedItem == null) {
                    articleNewsFeedItem = new ArticleNewsFeedItem(fragment, userFixedname);
                }
                return articleNewsFeedItem.getViewHolder(parent);

                /*itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.article_post, parent, false);
                return new ArticleViewHolder(itemView);*/

            case 5:
                if (questionNewsFeedItem == null) {
                    questionNewsFeedItem = new QuestionNewsFeedItem(fragment, userFixedname);
                }
                return questionNewsFeedItem.getViewHolder(parent);

                /*itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.question_list_item, parent, false);
                return new QuestionViewHolder(itemView);*/

            case 6:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.suggested_friends_news_feed, parent, false);
                return new SuggestedFriendsViewHolder(itemView);

            case 7:
                if (answerNewsFeedItem == null) {
                    answerNewsFeedItem = new AnswerNewsFeedItem(fragment, userFixedname);
                }
                return answerNewsFeedItem.getViewHolder(parent);

                /*itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.answer_newsfeed_item, parent, false);
                return new AnswerViewHolder(itemView);*/

            case 8:
                if (normalPostNewsFeedItem == null) {
                    normalPostNewsFeedItem = new NormalPostNewsFeedItem(fragment, userFixedname);
                }
                return normalPostNewsFeedItem.getViewHolder(parent);

                /*itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.normal_post_item, parent, false);
                return new NormalPostViewHolder(itemView);*/
            case 9:
                if (pollNewsFeedItem == null) {
                    pollNewsFeedItem = new PollNewsFeedItem(fragment, userFixedname);
                }
                return pollNewsFeedItem.getViewHolder(parent);

                /*itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.poll_post_item, parent, false);
                return new PollPostViewHolder(itemView);*/
            case 10:
                if (interestNewsFeedItem == null) {
                    interestNewsFeedItem = new InterestNewsFeedItem(fragment, userFixedname);
                }
                return interestNewsFeedItem.getViewHolder(parent);

                /*itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.interest_post, parent, false);
                return new InterestPostViewHolder(itemView);*/
            case 11:
                if (venueNewsFeedItem == null) {
                    venueNewsFeedItem = new VenueNewsFeedItem(fragment, userFixedname);
                }
                return venueNewsFeedItem.getViewHolder(parent);
                /*itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.venue_post, parent, false);
                return new VenuePostViewHolder(itemView);*/

            case 12:
            case 15:
                if (actionSharedNormalPostNewsFeedItem == null) {
                    actionSharedNormalPostNewsFeedItem = new ActionSharedNormalPostNewsFeedItem(fragment, userFixedname);
                }
                return actionSharedNormalPostNewsFeedItem.getViewHolder(parent);

                /*itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.action_normal_post_shared_item, parent, false);
                return new ActionSharedNormalPostViewHolder(itemView);*/

            case 13:
            case 16:
                if (actionSharedPollNewsFeedItem == null) {
                    actionSharedPollNewsFeedItem = new ActionSharedPollNewsFeedItem(fragment, userFixedname);
                }
                return actionSharedPollNewsFeedItem.getViewHolder(parent);

                /*itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.action_poll_post_shared_item, parent, false);
                return new ActionSharedPollPostViewHolder(itemView);*/

            case 14:
            case 17:
                if (actionSharedArticleNewsFeedItem == null) {
                    actionSharedArticleNewsFeedItem = new ActionSharedArticleNewsFeedItem(fragment, userFixedname);
                }
                return actionSharedArticleNewsFeedItem.getViewHolder(parent);
                /*itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.action_article_shared_item, parent, false);
                return new ActionSharedArticleViewHolder(itemView);*/

            case 18:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.progress_bar_list_item, parent, false);
                return new ProgressBarViewHolder(itemView);

            case 19:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.hidden_news_feed_post_item, parent, false);
                return new HiddenPostViewHolder(itemView);

            case 20:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.no_post_results_item, parent, false);
                return new NoResultsViewHolder(itemView);

            case 21:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.blank_newsfeed_item, parent, false);
                return new BlankItemViewHolder(itemView);

            default:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.blank_newsfeed_item, parent, false);
                return new BlankItemViewHolder(itemView);
        }
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        //   holder.tv_hierarchy_item.setText(hierarchyList.get(position));
        switch (holder.getItemViewType()) {
            case 1:
                upcomingSessionNewsFeedItem.configUpcomingSessionViewHolder
                        ((UpcomingSessionNewsFeedItem.UpcomingSessionViewHolder) holder,position, newsFeedList.get(position));
                /*UpcomingSessionViewHolder upcomingSessionViewHolder = (UpcomingSessionViewHolder) holder;
                configUpcomingSessionViewHolder(upcomingSessionViewHolder, position);*/
                break;
            case 2:
                assignedSessionNewsFeedItem.configAssignedSessionViewHolder
                        ((AssignedSessionNewsFeedItem.AssignedSessionViewHolder) holder,position,
                                newsFeedList.get(position));
                /*AssignedSessionViewHolder assignedSessionViewHolder = (AssignedSessionViewHolder) holder;
                configAssignedSessionViewHolder(assignedSessionViewHolder, position);*/
                break;
            case 3:
                organisedSessionNewsFeedItem.configOrganisedSessionViewHolder
                        ((OrganisedSessionNewsFeedItem.OrganisedSessionViewHolder) holder,position,
                                newsFeedList.get(position));
                /*OrganisedSessionViewHolder organisedSessionViewHolder = (OrganisedSessionViewHolder) holder;
                configOrganisedSessionViewHolder(organisedSessionViewHolder, position);*/
                break;
            case 4:
                articleNewsFeedItem.configArticleViewHolder((ArticleNewsFeedItem.ArticleViewHolder) holder,
                        position, newsFeedList.get(position));
                /*ArticleViewHolder articleViewHolder = (ArticleViewHolder) holder;
                configArticleViewHolder(articleViewHolder, position);*/
                break;
            case 5:
                questionNewsFeedItem.configQuestionViewHolder
                        ((QuestionNewsFeedItem.QuestionViewHolder) holder,position,
                                newsFeedList.get(position));
                /*QuestionViewHolder questionViewHolder = (QuestionViewHolder) holder;
                configQuestionViewHolder(questionViewHolder, position);*/
                break;
            case 6:
                SuggestedFriendsViewHolder suggestedFriendsViewHolder = (SuggestedFriendsViewHolder) holder;
                configSuggestedViewHolder(suggestedFriendsViewHolder, position);
                break;
            case 7:
                answerNewsFeedItem.configAnswerViewHolder((AnswerNewsFeedItem.AnswerViewHolder) holder,
                        position, newsFeedList.get(position));
               /* AnswerViewHolder answerViewHolder = (AnswerViewHolder) holder;
                configAnswerViewHolder(answerViewHolder, position);*/
                break;
            case 8:
                normalPostNewsFeedItem.configNormalPostViewHolder
                        ((NormalPostNewsFeedItem.NormalPostViewHolder) holder,position,
                                newsFeedList.get(position));
                /*NormalPostViewHolder normalPostViewHolder = (NormalPostViewHolder) holder;
                configNormalPostViewHolder(normalPostViewHolder, position);*/
                break;
            case 9:
                pollNewsFeedItem.configPollPostViewHolder
                        ((PollNewsFeedItem.PollPostViewHolder) holder,position,newsFeedList.get(position));
                /*PollPostViewHolder pollPostViewHolder = (PollPostViewHolder) holder;
                configPollPostViewHolder(pollPostViewHolder, position);*/
                break;

            case 10:
                interestNewsFeedItem.configInterestPostViewHolder
                        ((InterestNewsFeedItem.InterestPostViewHolder) holder,position,
                                newsFeedList.get(position));
                /*InterestPostViewHolder interestPostViewHolder = (InterestPostViewHolder) holder;
                configInterestPostViewHolder(interestPostViewHolder, position);*/
                break;

            case 11:
                venueNewsFeedItem.configVenuePostViewHolder
                        ((VenueNewsFeedItem.VenuePostViewHolder) holder,position,newsFeedList.get(position));
               /*VenuePostViewHolder venuePostViewHolder = (VenuePostViewHolder) holder;
                configVenuePostViewHolder(venuePostViewHolder, position);*/
                break;

            case 12:
                actionSharedNormalPostNewsFeedItem.configActionSharedNormalPostViewHolder
                        ((ActionSharedNormalPostNewsFeedItem.ActionSharedNormalPostViewHolder) holder,
                                position,newsFeedList.get(position));
                /*ActionSharedNormalPostViewHolder actionSharedNormalPostViewHolder = (ActionSharedNormalPostViewHolder) holder;
                configActionSharedNormalPostViewHolder(actionSharedNormalPostViewHolder, position);*/
                break;

            case 13:
                actionSharedPollNewsFeedItem.configActionSharedPollViewHolder
                        ((ActionSharedPollNewsFeedItem.ActionSharedPollPostViewHolder) holder,
                                position,newsFeedList.get(position));
                /*ActionSharedPollPostViewHolder actionSharedPollViewHolder = (ActionSharedPollPostViewHolder) holder;
                configActionSharedPollViewHolder(actionSharedPollViewHolder, position);*/
                break;

            case 14:
                actionSharedArticleNewsFeedItem.configActionSharedArticleViewHolder
                        ((ActionSharedArticleNewsFeedItem.ActionSharedArticleViewHolder) holder,
                                position,newsFeedList.get(position));
                /*ActionSharedArticleViewHolder actionSharedArticlePostViewHolder = (ActionSharedArticleViewHolder) holder;
                configActionSharedArticleViewHolder(actionSharedArticlePostViewHolder, position);*/
                break;

            case 15:
                actionSharedNormalPostNewsFeedItem.configSharedNormalPostViewHolder
                        ((ActionSharedNormalPostNewsFeedItem.ActionSharedNormalPostViewHolder) holder,
                                position,newsFeedList.get(position));
                /*ActionSharedNormalPostViewHolder sharedNormalPostViewHolder = (ActionSharedNormalPostViewHolder) holder;
                configSharedNormalPostViewHolder(sharedNormalPostViewHolder, position);*/
                break;

            case 16:
                actionSharedPollNewsFeedItem.configSharedPollViewHolder
                        ((ActionSharedPollNewsFeedItem.ActionSharedPollPostViewHolder) holder,
                                position,newsFeedList.get(position));
                /*ActionSharedPollPostViewHolder sharedPollViewHolder = (ActionSharedPollPostViewHolder) holder;
                configSharedPollViewHolder(sharedPollViewHolder, position);*/
                break;

            case 17:
                actionSharedArticleNewsFeedItem.configSharedArticleViewHolder
                        ((ActionSharedArticleNewsFeedItem.ActionSharedArticleViewHolder) holder,
                                position,newsFeedList.get(position));
                /*ActionSharedArticleViewHolder sharedArticlePostViewHolder = (ActionSharedArticleViewHolder) holder;
                configSharedArticleViewHolder(sharedArticlePostViewHolder, position);*/
                break;

            case 18:
                ProgressBarViewHolder progressBarViewHolder = (ProgressBarViewHolder) holder;
                configProgressBarViewHolder(progressBarViewHolder, position);
                break;

            case 19:
                HiddenPostViewHolder hiddenPostViewHolder = (HiddenPostViewHolder) holder;
                configHiddenPostViewHolder(hiddenPostViewHolder, position);
                break;

            case 20:
                NoResultsViewHolder noResultsViewHolder = (NoResultsViewHolder) holder;
                // configSharedArticleViewHolder(progressBarViewHolder, position);
                break;

            default:
                BlankItemViewHolder blankItemViewHolder = (BlankItemViewHolder) holder;
                //     configSuggestedViewHolder(suggestedFriendsViewHolder2, position);
                break;
        }
    }

    private void configProgressBarViewHolder(ProgressBarViewHolder progressBarViewHolder, int position) {
        if (showProgressBar) {
            progressBarViewHolder.pb_loading_content.setVisibility(View.VISIBLE);
            progressBarViewHolder.tv_no_more_results.setVisibility(View.GONE);
        } else {
            progressBarViewHolder.pb_loading_content.setVisibility(View.GONE);
        }

        if (showNoMoreResults) {
            progressBarViewHolder.pb_loading_content.setVisibility(View.GONE);
            progressBarViewHolder.tv_no_more_results.setVisibility(View.VISIBLE);
            progressBarViewHolder.tv_no_more_results.setText(noMoreResultsMessage);
        } else {
            progressBarViewHolder.tv_no_more_results.setVisibility(View.GONE);
        }

    }

    private void configHiddenPostViewHolder(HiddenPostViewHolder hiddenPostViewHolder, final int position) {
        hiddenPostViewHolder.tv_undo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newsFeedAdapterCallback.newsFeedItemClickCallback(position, newsFeedList.get(position),
                        AppKeys.UNHIDE_POST, "", "");
            }
        });
    }


    private void configSuggestedViewHolder(SuggestedFriendsViewHolder suggestedFriendsViewHolder, int position) {
        SuggestedFriendsAdapter suggestedFriendsAdapter;
        List<PojoUserData> suggestedFriendList = preferenceUtils.getSuggestedUserList();
        suggestedFriendsAdapter = new SuggestedFriendsAdapter(activity, suggestedFriendList, "hor");
        suggestedFriendsViewHolder.rv_suggested_friends_list.setLayoutManager
                (new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
        suggestedFriendsViewHolder.rv_suggested_friends_list.setAdapter(suggestedFriendsAdapter);
    }



    public class ProgressBarViewHolder extends RecyclerView.ViewHolder {
        ProgressBar pb_loading_content;
        TextView tv_no_more_results;

        public ProgressBarViewHolder(View itemView) {
            super(itemView);
            pb_loading_content = (ProgressBar) itemView.findViewById(R.id.pb_loading_content);
            tv_no_more_results = (TextView) itemView.findViewById(R.id.tv_no_more_results);
        }
    }


    public class HiddenPostViewHolder extends RecyclerView.ViewHolder {
        TextView tv_undo;

        public HiddenPostViewHolder(View itemView) {
            super(itemView);
            tv_undo = (TextView) itemView.findViewById(R.id.tv_undo);
        }
    }


    public class NoResultsViewHolder extends RecyclerView.ViewHolder {
        public NoResultsViewHolder(View itemView) {
            super(itemView);
        }
    }

    public class SuggestedFriendsViewHolder extends RecyclerView.ViewHolder {
        RecyclerView rv_suggested_friends_list;

        public SuggestedFriendsViewHolder(View itemView) {
            super(itemView);
            rv_suggested_friends_list = (RecyclerView) itemView.findViewById(R.id.rv_suggested_friends_list);
        }
    }

    public class BlankItemViewHolder extends RecyclerView.ViewHolder {
        RecyclerView rv_suggested_friends_list;

        public BlankItemViewHolder(View itemView) {
            super(itemView);
        }
    }

    public static interface NewsFeedAdapterCallback {
        void newsFeedItemClickCallback(int position, PojoGetNewsFeedResponseData currentItem, String type, String data, String sub_data);
    }

}


