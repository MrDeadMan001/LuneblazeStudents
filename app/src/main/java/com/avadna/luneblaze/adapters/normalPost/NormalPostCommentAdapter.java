package com.avadna.luneblaze.adapters.normalPost;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;

import com.avadna.luneblaze.activities.article.ArticleActivity;
import com.avadna.luneblaze.activities.article.SharedArticleActivity;
import com.avadna.luneblaze.activities.normalpost.NormalPostActivity;
import com.avadna.luneblaze.activities.normalpost.SharedNormalPostActivity;
import com.avadna.luneblaze.activities.organisation.AdTargetAudienceActivity;
import com.avadna.luneblaze.activities.organisation.OrganisationProfileInfoActivity;
import com.avadna.luneblaze.activities.polls.PollActivity;
import com.avadna.luneblaze.activities.polls.SharedPollActivity;
import com.avadna.luneblaze.adapters.PollPostAdapter;
import com.avadna.luneblaze.adapters.UpvotersAndAttendeesListAdapter;
import com.avadna.luneblaze.helperClasses.ImageDimensions;
import com.avadna.luneblaze.helperClasses.MyCustomThemeDialog;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.activities.normalpost.PostCommentRepliesListActivity;
import com.avadna.luneblaze.activities.profileInfo.ProfileInfoActivity;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.helperClasses.SpannableClickItem;
import com.avadna.luneblaze.pojo.pojoNewsFeed.PojoGetNewsFeedResponseData;
import com.avadna.luneblaze.pojo.pojoNewsFeed.PojoNewsFeedPhoto;
import com.avadna.luneblaze.pojo.pojoNewsFeed.PojoNewsFeedPollOption;
import com.avadna.luneblaze.pojo.pojoQuestion.PojoUserData;
import com.avadna.luneblaze.pojo.pojonormalpost.PojoNormalPostComment;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;
import com.avadna.luneblaze.utils.ProportionalImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.leocardz.link.preview.library.LinkPreviewCallback;
import com.leocardz.link.preview.library.SourceContent;
import com.leocardz.link.preview.library.TextCrawler;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

public class NormalPostCommentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    int listSize;
    Activity activity;
    List<PojoNormalPostComment> commentList;

    PreferenceUtils preferenceUtils;
    String user_id;
    String userName;
    String creator_id;
    ApiInterface apiService;
    String type;
    NormalPostCommentListAdapterCallback normalPostCommentListAdapterCallback;
    PojoGetNewsFeedResponseData postItem;
    CommonFunctions commonFunctions;


    DisplayMetrics displayMetrics;
    int imageFixedWidth;
    int imageFixedHeight;
    int maxArticleImageHeight;
    int maxSessionImageHeight;
    int maxNormalPostImageHeight;


    boolean showProgressBar = false;
    boolean moreNextAvailable = true;
    boolean morePreviousAvailable = true;
    boolean showBottomMoreButton = true;
    boolean showTopMoreButton = true;


    public NormalPostCommentAdapter(Activity activity, List<PojoNormalPostComment> commentList, String type) {
        try {
            this.normalPostCommentListAdapterCallback = ((NormalPostCommentListAdapterCallback) activity);
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NormalPostCommentListAdapterCallback.");
        }
        this.activity = activity;
        this.commentList = commentList;
        this.type = type;
        this.creator_id = creator_id;
        this.postItem = postItem;
        displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        imageFixedWidth = (int) (displayMetrics.widthPixels);
        imageFixedHeight = (int) (displayMetrics.widthPixels * 0.6f);
        maxArticleImageHeight = (int) (displayMetrics.widthPixels * (3f / 5));
        maxSessionImageHeight = (int) (displayMetrics.widthPixels * (2f / 3));
        maxNormalPostImageHeight = (int) (displayMetrics.heightPixels * (5f / 6));
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
        preferenceUtils = new PreferenceUtils(activity);
        user_id = preferenceUtils.get_user_id();
        userName = preferenceUtils.get_user_name();
        commonFunctions = new CommonFunctions(activity);
    }

    public void setShowProgressBar(boolean status) {
        showProgressBar = status;
        // notifyDataSetChanged();
    }

    public void setMoreNextAvailable(boolean available) {
        moreNextAvailable = available;
    }

    public void setMorePreviousAvailable(boolean available) {
        morePreviousAvailable = available;
    }

    public void setShowBottomMoreButton(boolean show) {
        showBottomMoreButton = show;
    }

    public void setShowTopMoreButton(boolean show) {
        showTopMoreButton = show;
    }


    public void setCreator_id(String user_id) {
        this.creator_id = user_id;
        notifyDataSetChanged();
    }

    public void setPostData(PojoGetNewsFeedResponseData postItem) {
        this.postItem = postItem;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            switch (postItem.postType) {
                case "photos":
                case "":
                case "post_like":
                case "post_comment":
                case "organisation_post":
                case "ad":
                    if (postItem.origin != null) {
                        if (postItem.origin.poll != null) {
                            return 5; //shared poll
                        }
                        if (postItem.origin.articlesId != null) {
                            return 6; //shared article
                        }
                        return 3; //shared normal post
                    }

                    if (postItem.poll != null) {
                        return 4; //poll
                    }

                    return 2; //normal post

                case "poll":
                case "poll_vote":
                    return 4; //poll


                case "shared":
                    if (postItem.origin.postType.equals("poll")) {
                        return 5; //shared poll
                    }
                    if (postItem.origin.postType.equals("poll_vote")) {
                        return 5; //shared poll
                    }
                    if (postItem.origin.postType.equals("photos")
                            || postItem.origin.postType.equals("")
                            || postItem.origin.postType.equals("post_comment")
                            || postItem.origin.postType.equals("post_like")
                            || postItem.origin.postType.equals("organisation_post")
                            || postItem.origin.postType.equals("ad")) {
                        if (postItem.origin.poll != null) {
                            return 5;
                        } else {
                            return 3;
                        }
                    }

                case "article_shared":
                    return 6; //shared article
            }
        } else {
            return 1;
        }
        return 1;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        switch (viewType) {
            case 1:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.comment_list_item, parent, false);
                return new CommentViewHolder(itemView);

            case 2:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.normal_post_activity_top_item, parent, false);
                return new NormalPostViewHolder(itemView);

            case 3:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.shared_normal_post_activity_top_item, parent, false);
                return new ActionSharedNormalPostViewHolder(itemView);

            case 4:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.poll_activity_top_item, parent, false);
                return new PollPostViewHolder(itemView);

            case 5:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.shared_poll_activity_top_item, parent, false);
                return new ActionSharedPollPostViewHolder(itemView);

            case 6:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.shared_article_activity_top_item, parent, false);
                return new ActionSharedArticleViewHolder(itemView);

            default:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.comment_list_item, parent, false);
                return new CommentViewHolder(itemView);
        }
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        switch (holder.getItemViewType()) {

            case 1:
                CommentViewHolder commentViewHolder = (CommentViewHolder) holder;
                configCommentViewHolder(commentViewHolder, position - 1);
                break;

            case 2:
                NormalPostViewHolder normalPostViewHolder = (NormalPostViewHolder) holder;
                configNormalPostViewHolder(normalPostViewHolder, position);
                break;

            case 3:
                ActionSharedNormalPostViewHolder actionSharedNormalPostViewHolder = (ActionSharedNormalPostViewHolder) holder;
                configSharedNormalPostViewHolder(actionSharedNormalPostViewHolder, position);
                break;

            case 4:
                PollPostViewHolder pollPostViewHolder = (PollPostViewHolder) holder;
                configPollPostViewHolder(pollPostViewHolder, position);
                break;

            case 5:
                ActionSharedPollPostViewHolder actionSharedPollPostViewHolder = (ActionSharedPollPostViewHolder) holder;
                configSharedPollViewHolder(actionSharedPollPostViewHolder, position);
                break;

            case 6:
                ActionSharedArticleViewHolder actionSharedArticleViewHolder = (ActionSharedArticleViewHolder) holder;
                configSharedArticleViewHolder(actionSharedArticleViewHolder, position);
                break;
        }

    }

    private void configCommentViewHolder(CommentViewHolder holder, int position) {

        if (position == commentList.size()) {
            holder.rl_data_wrapper.setVisibility(View.GONE);
            holder.tv_load_more_button.setText(activity.getString(R.string.load_more));

            if (!moreNextAvailable) {
                holder.tv_load_more_button.setText(activity.getString(R.string.no_more_available));
            }

            if (showProgressBar) {
                holder.pb_loading_more.setVisibility(View.VISIBLE);
                holder.tv_load_more_button.setVisibility(View.GONE);
            } else {
                holder.pb_loading_more.setVisibility(View.GONE);
                if (showBottomMoreButton) {
                    holder.tv_load_more_button.setVisibility(View.VISIBLE);
                } else {
                    holder.tv_load_more_button.setVisibility(View.GONE);
                }
            }

            holder.tv_load_more_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (moreNextAvailable) {
                        normalPostCommentListAdapterCallback.normalPostCommentListItemClickCallback(position, AppKeys.LOAD_NEXT);
                    }
                }
            });

        } else {
            holder.tv_load_more_button.setVisibility(View.GONE);
            holder.pb_loading_more.setVisibility(View.GONE);
            holder.rl_data_wrapper.setVisibility(View.VISIBLE);
            final PojoNormalPostComment currentItem = commentList.get(position);

            LinkPreviewCallback linkPreviewCallback = new LinkPreviewCallback() {
                @Override
                public void onPre() {
                    // Any work that needs to be done before generating the preview. Usually inflate
                    // your custom preview layout here.
                    //commonFunctions.setToastMessage(activity,"link preview called",Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);
                    holder.rl_web_preview_wrapper.setVisibility(View.GONE);
                }

                @Override
                public void onPos(SourceContent sourceContent, boolean b) {
                    // Populate your preview layout with the results of sourceContent.
                    currentItem.sourceContent = sourceContent;
                    setPreviewData(holder, currentItem);
                }
            };

            currentItem.textCrawler = new TextCrawler();
            String text = Html.fromHtml(currentItem.text).toString();
            if (Patterns.WEB_URL.matcher(text).matches()) {
                holder.tv_comment.setText(text);
                if (currentItem.sourceContent == null) {
                    currentItem.textCrawler.makePreview(linkPreviewCallback, text);
                } else {
                    setPreviewData(holder, currentItem);
                }
                holder.rl_web_preview_wrapper.setVisibility(View.GONE);
            } else {
                holder.rl_web_preview_wrapper.setVisibility(View.GONE);
                holder.tv_comment.setVisibility(View.VISIBLE);
                holder.tv_comment.setHighlightColor(Color.TRANSPARENT); // prevent TextView change background when highlight
                SpannableClickItem spannableClickItem = commonFunctions.setClickSpans(currentItem.text,
                        currentItem.text, false, null);
                currentItem.clickableSpansList = spannableClickItem.clickableSpansList;
                holder.tv_comment
                        .setText(spannableClickItem.spannableString, TextView.BufferType.SPANNABLE);

                if (currentItem.clickableSpansList.isEmpty()) {
                    holder.tv_comment.setMovementMethod(null);
                } else {
                    holder.tv_comment.setMovementMethod(LinkMovementMethod.getInstance());
                }

            }

            if (!currentItem.authorPicture.isEmpty()) {
                if (activity != null) Glide.with(activity.getApplicationContext())
                        .load(currentItem.authorPicture)
                        .apply(new RequestOptions().override(96, 96))
                        .apply(bitmapTransform(new CropCircleTransformation()))
                        .into(holder.iv_user_dp);
            }

            MenuInflater inflater = holder.popupMenu.getMenuInflater();

            holder.tv_user_name.setText(currentItem.userFullname);
            holder.tv_user_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent profileIntent = new Intent(activity,
                            ProfileInfoActivity.class);
                    profileIntent.putExtra("target_user_id", currentItem.userId);
                    activity.startActivity(profileIntent);
                }
            });

            holder.tv_time_stamp.setText(commonFunctions.parseNewsFeedItemTime(currentItem.time));

            if (currentItem.replies.equals("0")) {
                holder.tv_num_of_replies.setVisibility(View.GONE);
            } else {
                holder.tv_num_of_replies.setVisibility(View.VISIBLE);
            }
            holder.tv_num_of_replies.setText("" + currentItem.replies + " " + activity.getString(R.string.replies));

            if (currentItem.likes.equals("0")) {
                holder.tv_num_of_likes.setVisibility(View.GONE);
            } else {
                holder.tv_num_of_likes.setVisibility(View.VISIBLE);
            }
            holder.tv_num_of_likes.setText("" + currentItem.likes + " " + activity.getString(R.string.likes));
            // holder.tv_num_of_replies.setText("" + currentItem.totalReplies + " " + activity.getString(R.string.replies));


            if (currentItem.iLike) {
                holder.tv_like_button.setCompoundDrawablesWithIntrinsicBounds
                        (R.drawable.ic_like_filled, 0, 0, 0);
                holder.tv_like_button.setTextColor(ContextCompat.getColor(activity, R.color.app_theme_medium));
            } else {
                holder.tv_like_button.setTextColor(ContextCompat.getColor(activity, R.color.med_grey));
                holder.tv_like_button.setCompoundDrawablesWithIntrinsicBounds
                        (R.drawable.ic_like_unfilled, 0, 0, 0);
            }

            holder.tv_num_of_replies.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent replyIntent = new Intent(activity, PostCommentRepliesListActivity.class);
                    replyIntent.putExtra(AppKeys.COMMENT_ID, currentItem.commentId);
                    replyIntent.putExtra(AppKeys.POST_ID, currentItem.nodeId);
                    activity.startActivity(replyIntent);
                }
            });

            if (currentItem.commentId == null || currentItem.commentId.isEmpty()) {
                holder.iv_more_options.setVisibility(View.GONE);
                holder.tv_like_button.setOnClickListener(null);
                holder.rl_data_wrapper.setOnClickListener(null);
                holder.tv_reply_button.setOnClickListener(null);
                holder.rl_parent.setAlpha(0.6f);
            } else {
                holder.rl_parent.setAlpha(1f);
                if (!user_id.equals(AppKeys.GUEST_USER_ID)) {
                    holder.iv_more_options.setVisibility(View.VISIBLE);
                } else {
                    holder.iv_more_options.setVisibility(View.GONE);
                }
                holder.rl_data_wrapper.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent replyIntent = new Intent(activity, PostCommentRepliesListActivity.class);
                        replyIntent.putExtra(AppKeys.COMMENT_ID, currentItem.commentId);
                        replyIntent.putExtra(AppKeys.POST_ID, currentItem.nodeId);
                        activity.startActivity(replyIntent);
                    }
                });

                holder.tv_like_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (user_id.equals(AppKeys.GUEST_USER_ID)) {
                            commonFunctions.showLoginRequestDialog();
                        } else {
                            normalPostCommentListAdapterCallback
                                    .normalPostCommentListItemClickCallback(position, AppKeys.LIKE);
                        }
                    }
                });

                holder.tv_reply_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (user_id.equals(AppKeys.GUEST_USER_ID)) {
                            commonFunctions.showLoginRequestDialog();
                        } else {
                            Intent replyIntent = new Intent(activity, PostCommentRepliesListActivity.class);
                            replyIntent.putExtra(AppKeys.COMMENT_ID, currentItem.commentId);
                            replyIntent.putExtra(AppKeys.POST_ID, currentItem.nodeId);
                            replyIntent.putExtra(AppKeys.TARGET, AppKeys.ADD_REPLY);
                            activity.startActivity(replyIntent);
                        }
                    }
                });
            }

            holder.tv_num_of_likes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (user_id.equals(AppKeys.GUEST_USER_ID)) {
                        commonFunctions.showLoginRequestDialog();
                    } else {
                        normalPostCommentListAdapterCallback.normalPostCommentListItemClickCallback(position, AppKeys.LIKE_COUNT);
                        // openUserListDialog(currentItem.likedUsers);
                    }
                }
            });


            if (user_id.equals(currentItem.userId)) {
                holder.popupMenu.getMenu().clear();
                inflater.inflate(R.menu.comment_options_poster, holder.popupMenu.getMenu());
            } else if (user_id.equals(creator_id)) {
                holder.popupMenu.getMenu().clear();
                inflater.inflate(R.menu.comment_options_creator, holder.popupMenu.getMenu());
            } else {
                holder.popupMenu.getMenu().clear();
                inflater.inflate(R.menu.comment_options_other, holder.popupMenu.getMenu());
            }

            try {
                Field[] fields = holder.popupMenu.getClass().getDeclaredFields();
                for (Field field : fields) {
                    if ("mPopup".equals(field.getName())) {
                        field.setAccessible(true);
                        Object menuPopupHelper = field.get(holder.popupMenu);
                        Class<?> classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());
                        Method setForceIcons = classPopupHelper.getMethod("setForceShowIcon", boolean.class);
                        setForceIcons.invoke(menuPopupHelper, true);
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            holder.popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.edit:
                            normalPostCommentListAdapterCallback
                                    .normalPostCommentListItemClickCallback(position,
                                            AppKeys.EDIT);
                            break;

                        case R.id.delete:
                            normalPostCommentListAdapterCallback
                                    .normalPostCommentListItemClickCallback(position,
                                            AppKeys.DELETE);

                            break;

                        case R.id.share_to:
                            // shareTextUrl(answerList.get(position).sessionsQaId);
                            break;

                        case R.id.report:
                            normalPostCommentListAdapterCallback
                                    .normalPostCommentListItemClickCallback(position,
                                            AppKeys.REPORT_COMMENT);
                            // commonFunctions.openReportDialog(user_id, "report_comment", currentItem.replyId);
                            break;

                    }
                    return false;
                }
            });

        }
    }

    private void configNormalPostViewHolder(final NormalPostViewHolder normalPostViewHolder, final int position) {
        final PojoGetNewsFeedResponseData currentItem = postItem;

        normalPostViewHolder.rl_top_bar_wrapper.setVisibility(View.GONE);

        final String targetPostId = currentItem.postId;
        List<PojoNewsFeedPhoto> photos = currentItem.photos;
        normalPostViewHolder.tv_boost_post.setVisibility(View.GONE);

        normalPostViewHolder.iv_user_dp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                normalPostViewHolder.tv_poster_name.callOnClick();
            }
        });

        String userToShow;
        {
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

                normalPostViewHolder.tv_poster_name.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent organisationIntent = new Intent(activity, OrganisationProfileInfoActivity.class);
                        organisationIntent.putExtra(AppKeys.ORGANISATION_ID, currentItem.organizationData.id);
                        organisationIntent.putExtra(AppKeys.OWNER_ID, currentItem.postAuthorName);
                        activity.startActivity(organisationIntent);
                    }
                });

                if (currentItem.postType.equals("organisation_post") && currentItem.authorId.equals(user_id)) {
                    normalPostViewHolder.tv_boost_post.setVisibility(View.VISIBLE);
                }

            } else {
                if (!currentItem.postAuthorPicture.isEmpty()) {
                    if (activity != null)
                        Glide.with(activity.getApplicationContext()).load(currentItem.postAuthorPicture)
                                .apply(bitmapTransform(new CropCircleTransformation()))
                                .into(normalPostViewHolder.iv_user_dp);
                }
                normalPostViewHolder.tv_poster_name.setText(currentItem.postAuthorName);
                normalPostViewHolder.tv_poster_designation.setText(currentItem.userWorkTitle);

                normalPostViewHolder.tv_poster_name.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent profileIntent = new Intent(activity, ProfileInfoActivity.class);
                        profileIntent.putExtra("target_user_id", currentItem.authorId);
                        activity.startActivity(profileIntent);
                    }
                });
            }


            final boolean tagged;
            if (currentItem.tagged.equals("1")) {
                tagged = true;
            } else {
                tagged = false;
            }

            //  normalPostViewHolder.tv_post_content.setHighlightColor(Color.TRANSPARENT); // prevent TextView change background when highlight

            currentItem.spannableClickItem = commonFunctions.setClickSpans(currentItem.text,
                    currentItem.textPlain, tagged, currentItem.postId);

            normalPostViewHolder.tv_post_content.setLinksClickable(false);

           /* if (currentItem.spannableClickItem.clickableSpansList.isEmpty()) {
                normalPostViewHolder.tv_post_content.setMovementMethod(null);
            } else {
                normalPostViewHolder.tv_post_content.setMovementMethod(LinkMovementMethod.getInstance());
            }*/
            // normalPostViewHolder.tv_post_content.setText(new SpannableString(Html.fromHtml(currentItem.text)));
            normalPostViewHolder.tv_post_content.setMovementMethod(LinkMovementMethod.getInstance());
            normalPostViewHolder.tv_view_more.setVisibility(View.GONE);

            normalPostViewHolder.tv_post_content.setText(currentItem.spannableClickItem.spannableString,
                    TextView.BufferType.SPANNABLE);

            currentItem.url = "";

            for (int i = 0; i < currentItem.spannableClickItem.clickableUrls.size(); i++) {
                String currentUrl = currentItem.spannableClickItem.clickableUrls.get(i);
                if (!currentUrl.contains(AppKeys.WEBSITE_URL)
                        && Patterns.WEB_URL.matcher(currentUrl).matches()) {
                    currentItem.url = currentItem.spannableClickItem.clickableUrls.get(i);
                    break;
                }
            }

            normalPostViewHolder.rl_web_preview_wrapper.setVisibility(View.GONE);

            if (!currentItem.url.isEmpty()) {
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

                            if (title.isEmpty() || name.isEmpty() || currentItem.url.isEmpty()) {
                                normalPostViewHolder.rl_web_preview_wrapper.setVisibility(View.GONE);
                            } else {
                                normalPostViewHolder.rl_web_preview_wrapper.setVisibility(View.VISIBLE);
                                normalPostViewHolder.tv_link_title.setText(sourceContent.getTitle());
                                normalPostViewHolder.tv_website_name.setText(sourceContent.getCannonicalUrl());
                                normalPostViewHolder.tv_link_description.setText(sourceContent.getDescription());
                                for (int i = 0; i < sourceContent.getImages().size(); i++) {
                                    if (sourceContent.getImages().get(i).contains("png")
                                            || sourceContent.getImages().get(i).contains("jpg")) {
                                        Glide.with(activity.getApplicationContext())
                                                .load(sourceContent.getImages().get(i))
                                                .into(normalPostViewHolder.iv_link_image);
                                        break;
                                    }
                                }

                            }
                        }
                    };
                    normalPostViewHolder.textCrawler.makePreview(linkPreviewCallback, currentItem.url);
                }

            } else {
                normalPostViewHolder.rl_web_preview_wrapper.setVisibility(View.GONE);
            }

            Paint paint = new Paint();
            paint.setTextSize(normalPostViewHolder.tv_post_content.getTextSize());
            paint.setTypeface(normalPostViewHolder.tv_post_content.getTypeface());

            float textViewWidthPx = displayMetrics.widthPixels * 0.85f;
            int lineCount = commonFunctions.splitWordsIntoStringsThatFit(currentItem.spannableClickItem.spannableString.toString()
                    , textViewWidthPx, paint);
            currentItem.canTextFit = true;

            normalPostViewHolder.tv_post_content.setMaxLines(10000);
            normalPostViewHolder.tv_post_content
                    .setText(currentItem.spannableClickItem.spannableString, TextView.BufferType.SPANNABLE);

            if (currentItem.totalLikes == 0) {
                normalPostViewHolder.tv_like_number.setVisibility(View.GONE);
            } else {
                normalPostViewHolder.tv_like_number.setVisibility(View.VISIBLE);
            }
            normalPostViewHolder.tv_like_number.setText("" + currentItem.totalLikes + " "
                    + activity.getString(R.string.likes));

            if (currentItem.comments.equals("0")) {
                normalPostViewHolder.tv_comment_number.setVisibility(View.GONE);
            } else {
                normalPostViewHolder.tv_comment_number.setVisibility(View.VISIBLE);
            }
            normalPostViewHolder.tv_comment_number.setText("" + currentItem.comments + " "
                    + activity.getString(R.string.comments));

            if (currentItem.iLike == 1) {
                normalPostViewHolder.tv_like_button.setTextColor(ContextCompat.getColor(activity, R.color.app_theme_medium));
                normalPostViewHolder.tv_like_button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_like_filled, 0, 0, 0);
            } else {
                normalPostViewHolder.tv_like_button.setTextColor(ContextCompat.getColor(activity, R.color.med_grey));
                normalPostViewHolder.tv_like_button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_like_unfilled, 0, 0, 0);
            }
        }


       /* normalPostViewHolder.tv_comment_number.setText("" + currentItem.comments + " "
                + activity.getString(R.string.comments));*/

        if (photos == null || photos.size() == 0) {
            normalPostViewHolder.iv_cover_photo1.setVisibility(View.GONE);
            normalPostViewHolder.ll_two_images.setVisibility(View.GONE);
            normalPostViewHolder.ll_three_images.setVisibility(View.GONE);
        } else {
            int height = imageFixedHeight;
            int width = imageFixedWidth;
            if (photos.get(0).imgDimensions != null && !photos.get(0).imgDimensions.isEmpty()) {
                ImageDimensions dimen = getScaledDimensions(photos.get(0).imgDimensions, AppKeys.NORMAL);
                height = dimen.height;
                width = dimen.width;
               /* normalPostViewHolder.iv_cover_photo1.getLayoutParams().height = height;
                normalPostViewHolder.iv_cover_photo1.getLayoutParams().width = width;*/
            }

            if (photos.size() != 2) {
                normalPostViewHolder.iv_cover_photo1.setVisibility(View.VISIBLE);
                if (activity != null) Glide.with(activity.getApplicationContext())
                        .load(photos.get(0).source)
                        .apply(new RequestOptions().override(width, height))
                        .placeholder(R.drawable.placeholder)
                        .into(normalPostViewHolder.iv_cover_photo1);
            }

            if (photos.size() == 1) {
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
                normalPostViewHolder.ll_two_images.setVisibility(View.VISIBLE);
                if (activity != null)
                    Glide.with(activity.getApplicationContext()).load(photos.get(1).source).into(normalPostViewHolder.iv_cover_photo2);
                if (activity != null)
                    Glide.with(activity.getApplicationContext()).load(photos.get(2).source).into(normalPostViewHolder.iv_cover_photo3);
                normalPostViewHolder.ll_three_images.setVisibility(View.GONE);
            } else if (photos.size() == 4) {
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


        normalPostViewHolder.tv_like_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (user_id.equals(AppKeys.GUEST_USER_ID)) {
                    commonFunctions.showLoginRequestDialog();
                } else {
                    normalPostCommentListAdapterCallback.newsFeedItemClickCallback(position, currentItem, AppKeys.POST_LIKE);
                }
            }
        });

        normalPostViewHolder.tv_boost_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent adIntent = new Intent(activity, AdTargetAudienceActivity.class);
                adIntent.putExtra(AppKeys.POST_ID, currentItem.postId);
                adIntent.putExtra(AppKeys.ORGANISATION_ID, currentItem.organizationData.id);
                activity.startActivityForResult(adIntent, AppKeys.BOOST_POST_REQUEST_CODE);
                normalPostCommentListAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                        AppKeys.BOOST_POST);
            }
        });

        normalPostViewHolder.tv_share_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (user_id.equals(AppKeys.GUEST_USER_ID)) {
                    commonFunctions.showLoginRequestDialog();
                } else {
                    normalPostCommentListAdapterCallback.newsFeedItemClickCallback(position, currentItem, AppKeys.POST_SHARE);
                }
            }
        });


        normalPostViewHolder.tv_like_number.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user_id.equals(AppKeys.GUEST_USER_ID)) {
                    commonFunctions.showLoginRequestDialog();
                } else {
                    // commonFunctions.setToastMessage(activity,"item clicked",Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);
                    normalPostCommentListAdapterCallback.newsFeedItemClickCallback(position, postItem, AppKeys.LIKE_COUNT);
                }
            }
        });


        normalPostViewHolder.tv_comment_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user_id.equals(AppKeys.GUEST_USER_ID)) {
                    commonFunctions.showLoginRequestDialog();
                } else {
                    // commonFunctions.setToastMessage(activity,"item clicked",Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);
                    normalPostCommentListAdapterCallback.newsFeedItemClickCallback(position, postItem, AppKeys.COMMENT);
                }
            }
        });

        //if there are 2 photos we show second row which  has photoview number 2 and 3
        //if there are 3 photos we show 1st and second row with 1 and 2 photos in the row respectively
        //if there are 4 photos we show 1st and 3rd row with 1 and 3 photos respectively

        normalPostViewHolder.iv_cover_photo1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                normalPostCommentListAdapterCallback.newsFeedItemClickCallback(0, postItem,
                        AppKeys.PHOTO_CLICK);
            }
        });

        normalPostViewHolder.iv_cover_photo2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (photos != null && photos.size() == 2) {
                    normalPostCommentListAdapterCallback.newsFeedItemClickCallback(0, postItem,
                            AppKeys.PHOTO_CLICK);
                } else {
                    normalPostCommentListAdapterCallback.newsFeedItemClickCallback(1, postItem,
                            AppKeys.PHOTO_CLICK);
                }
            }
        });

        normalPostViewHolder.iv_cover_photo3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (photos != null && photos.size() == 2) {
                    normalPostCommentListAdapterCallback.newsFeedItemClickCallback(1, postItem,
                            AppKeys.PHOTO_CLICK);
                } else {
                    normalPostCommentListAdapterCallback.newsFeedItemClickCallback(2, postItem,
                            AppKeys.PHOTO_CLICK);
                }
            }
        });

        normalPostViewHolder.iv_cover_photo4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (photos != null && photos.size() == 4) {
                    normalPostCommentListAdapterCallback.newsFeedItemClickCallback(1, postItem,
                            AppKeys.PHOTO_CLICK);
                } else {
                    normalPostCommentListAdapterCallback.newsFeedItemClickCallback(3, postItem,
                            AppKeys.PHOTO_CLICK);
                }
            }
        });

        normalPostViewHolder.iv_cover_photo5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (photos != null && photos.size() == 4) {
                    normalPostCommentListAdapterCallback.newsFeedItemClickCallback(2, postItem,
                            AppKeys.PHOTO_CLICK);
                } else {
                    normalPostCommentListAdapterCallback.newsFeedItemClickCallback(4, postItem,
                            AppKeys.PHOTO_CLICK);
                }
            }
        });


        normalPostViewHolder.iv_cover_photo6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (photos != null && photos.size() == 4) {
                    normalPostCommentListAdapterCallback.newsFeedItemClickCallback(3, postItem,
                            AppKeys.PHOTO_CLICK);
                } else {
                    normalPostCommentListAdapterCallback.newsFeedItemClickCallback(5, postItem,
                            AppKeys.PHOTO_CLICK);

                }
            }
        });

        normalPostViewHolder.rl_top_bar_wrapper.setVisibility(View.GONE);
    }

    private void configSharedNormalPostViewHolder(final ActionSharedNormalPostViewHolder sharedNormalPostViewHolder,
                                                  final int position) {
        final PojoGetNewsFeedResponseData currentItem = postItem;
        sharedNormalPostViewHolder.rl_top_bar_wrapper.setVisibility(View.GONE);

        sharedNormalPostViewHolder.rl_web_preview_wrapper.setVisibility(View.GONE);
        List<PojoNewsFeedPhoto> photos = currentItem.origin.photos;

        //sharer data
        sharedNormalPostViewHolder.tv_post_sharer_stamp.setText(commonFunctions
                .parseNewsFeedItemTime(currentItem.updatedAt));

        if (currentItem.postTitle.isEmpty()) {
            sharedNormalPostViewHolder.tv_share_title.setVisibility(View.GONE);
        } else {
            sharedNormalPostViewHolder.tv_share_title.setVisibility(View.VISIBLE);
            sharedNormalPostViewHolder.tv_share_title.setText(Html.fromHtml(currentItem.postTitle));
        }
        sharedNormalPostViewHolder.tv_post_sharer_name.setText(currentItem.userFullname);
        sharedNormalPostViewHolder.tv_post_sharer_designation.setText(currentItem.userWorkTitle);
        sharedNormalPostViewHolder.tv_post_sharer_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent profileIntent = new Intent(activity, ProfileInfoActivity.class);
                profileIntent.putExtra("target_user_id", currentItem.authorId);
                activity.startActivity(profileIntent);
            }
        });

        if (!currentItem.postAuthorPicture.isEmpty()) {
            if (activity != null)
                Glide.with(activity.getApplicationContext()).load(currentItem.postAuthorPicture)
                        .apply(bitmapTransform(new CropCircleTransformation()))
                        .into(sharedNormalPostViewHolder.iv_post_sharer_user_dp);
        }
        sharedNormalPostViewHolder.iv_post_sharer_user_dp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharedNormalPostViewHolder.tv_post_sharer_name.callOnClick();
            }
        });


        //creator data
        sharedNormalPostViewHolder.tv_time_stamp.setText(commonFunctions
                .parseNewsFeedItemTime(currentItem.origin.time));

        if (currentItem.origin.organizationData != null
                && (currentItem.origin.postType.equals("organisation_post") || currentItem.origin.postType.equals("ad"))) {
            if (!currentItem.origin.organizationData.logo.isEmpty()) {
                if (activity != null)
                    Glide.with(activity.getApplicationContext()).load(currentItem.origin.organizationData.logo)
                            .apply(bitmapTransform(new CropCircleTransformation()))
                            .into(sharedNormalPostViewHolder.iv_user_dp);
            }
            sharedNormalPostViewHolder.tv_poster_name.setText(currentItem.origin.organizationData.name);
            sharedNormalPostViewHolder.tv_poster_designation.setText(currentItem.origin.organizationData.typename);

            sharedNormalPostViewHolder.tv_poster_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent organisationIntent = new Intent(activity, OrganisationProfileInfoActivity.class);
                    organisationIntent.putExtra(AppKeys.ORGANISATION_ID, currentItem.origin.organizationData.id);
                    activity.startActivity(organisationIntent);
                }
            });

        } else {
            sharedNormalPostViewHolder.tv_poster_name.setText(currentItem.origin.userFullname);
            sharedNormalPostViewHolder.tv_poster_designation.setText(currentItem.origin.userWorkTitle);


            sharedNormalPostViewHolder.tv_poster_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent profileIntent = new Intent(activity, ProfileInfoActivity.class);
                    profileIntent.putExtra("target_user_id", currentItem.origin.authorId);
                    activity.startActivity(profileIntent);
                }
            });
            if (!currentItem.origin.postAuthorPicture.isEmpty()) {
                if (activity != null)
                    Glide.with(activity.getApplicationContext()).load(currentItem.origin.postAuthorPicture)
                            .apply(bitmapTransform(new CropCircleTransformation()))
                            .into(sharedNormalPostViewHolder.iv_user_dp);
            }
        }


        sharedNormalPostViewHolder.cv_origin_wrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent postIntent = new Intent(activity, NormalPostActivity.class);
                postIntent.putExtra(AppKeys.POST_ID, currentItem.origin.postId);
                activity.startActivity(postIntent);
            }
        });


        sharedNormalPostViewHolder.iv_user_dp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharedNormalPostViewHolder.tv_poster_name.callOnClick();
            }
        });
        Log.d("newsfeed", currentItem.postId);


        boolean tagged;
        if (currentItem.origin.tagged.equals("1")) {
            tagged = true;
        } else {
            tagged = false;
        }

        sharedNormalPostViewHolder.tv_post_content.setHighlightColor(
                Color.TRANSPARENT); // prevent TextView change background when highlight
        sharedNormalPostViewHolder.tv_post_content.setMovementMethod(LinkMovementMethod.getInstance());
        currentItem.spannableClickItem = commonFunctions.setClickSpans(currentItem.origin.text,
                currentItem.origin.textPlain, tagged, null);
        currentItem.clickableSpansList = currentItem.spannableClickItem.clickableSpansList;
        sharedNormalPostViewHolder.tv_post_content
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
                sharedNormalPostViewHolder.rl_web_preview_wrapper.setVisibility(View.GONE);
                String title = currentItem.sourceContent.getTitle();
                String name = currentItem.sourceContent.getCannonicalUrl();
                String description = currentItem.sourceContent.getCannonicalUrl();
                if (title.isEmpty() || name.isEmpty()) {
                    sharedNormalPostViewHolder.rl_web_preview_wrapper.setVisibility(View.GONE);
                } else {
                    sharedNormalPostViewHolder.rl_web_preview_wrapper.setVisibility(View.VISIBLE);
                    sharedNormalPostViewHolder.tv_link_title.setText(currentItem.sourceContent.getTitle());
                    sharedNormalPostViewHolder.tv_website_name.setText(currentItem.sourceContent.getCannonicalUrl());
                    sharedNormalPostViewHolder.tv_link_description.setText(currentItem.sourceContent.getDescription());
                    for (int i = 0; i < currentItem.sourceContent.getImages().size(); i++) {
                        if (currentItem.sourceContent.getImages().get(i).contains("png")
                                || currentItem.sourceContent.getImages().get(i).contains("jpg")) {
                            if (activity != null)
                                Glide.with(activity.getApplicationContext())
                                        .load(currentItem.sourceContent.getImages().get(i))
                                        .into(sharedNormalPostViewHolder.iv_link_image);
                            break;
                        }
                    }
                }
            } else {
                sharedNormalPostViewHolder.textCrawler = new TextCrawler();
                LinkPreviewCallback linkPreviewCallback = new LinkPreviewCallback() {
                    @Override
                    public void onPre() {
                        // Any work that needs to be done before generating the preview. Usually inflate
                        // your custom preview layout here.
                        //commonFunctions.setToastMessage(activity,"link preview called",Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);
                        sharedNormalPostViewHolder.rl_web_preview_wrapper.setVisibility(View.GONE);
                    }

                    @Override
                    public void onPos(SourceContent sourceContent, boolean b) {
                        currentItem.sourceContent = sourceContent;
                        // Populate your preview layout with the results of sourceContent.
                        String title = sourceContent.getTitle();
                        String name = sourceContent.getCannonicalUrl();
                        String description = sourceContent.getCannonicalUrl();

                        if (title.isEmpty() || name.isEmpty()) {
                            sharedNormalPostViewHolder.rl_web_preview_wrapper.setVisibility(View.GONE);
                        } else {
                            sharedNormalPostViewHolder.rl_web_preview_wrapper.setVisibility(View.VISIBLE);
                            sharedNormalPostViewHolder.tv_link_title.setText(sourceContent.getTitle());
                            sharedNormalPostViewHolder.tv_website_name.setText(sourceContent.getCannonicalUrl());
                            sharedNormalPostViewHolder.tv_link_description.setText(sourceContent.getDescription());
                            for (int i = 0; i < sourceContent.getImages().size(); i++) {
                                if (sourceContent.getImages().get(i).contains("png")
                                        || sourceContent.getImages().get(i).contains("jpg")) {
                                    Glide.with(activity)
                                            .load(sourceContent.getImages().get(i))
                                            .into(sharedNormalPostViewHolder.iv_link_image);
                                    break;
                                }
                            }

                        }
                    }
                };
                sharedNormalPostViewHolder.textCrawler.makePreview(linkPreviewCallback, url);
            }


        }

        Paint paint = new Paint();
        paint.setTextSize(sharedNormalPostViewHolder.tv_post_content.getTextSize());
        paint.setTypeface(sharedNormalPostViewHolder.tv_post_content.getTypeface());

        float textViewWidthPx = displayMetrics.widthPixels * 0.85f;
        int lineCount = commonFunctions.splitWordsIntoStringsThatFit(currentItem.spannableClickItem.spannableString.toString()
                , textViewWidthPx, paint);


        sharedNormalPostViewHolder.tv_post_content.setMaxLines(10000);
        sharedNormalPostViewHolder.tv_post_content
                .setText(currentItem.spannableClickItem.spannableString, TextView.BufferType.SPANNABLE);


        if (currentItem.totalLikes == 0) {
            sharedNormalPostViewHolder.tv_like_number.setVisibility(View.GONE);
        } else {
            sharedNormalPostViewHolder.tv_like_number.setVisibility(View.VISIBLE);
        }
        sharedNormalPostViewHolder.tv_like_number.setText("" + currentItem.totalLikes + " "
                + activity.getString(R.string.likes));

        if (currentItem.comments.equals("0")) {
            sharedNormalPostViewHolder.tv_comment_number.setVisibility(View.GONE);
        } else {
            sharedNormalPostViewHolder.tv_comment_number.setVisibility(View.VISIBLE);
        }
        sharedNormalPostViewHolder.tv_comment_number.setText("" + currentItem.comments + " "
                + activity.getString(R.string.comments));


        if (currentItem.iLike == 1) {
            sharedNormalPostViewHolder.tv_like_button.setTextColor(ContextCompat.getColor(activity, R.color.app_theme_medium));
            sharedNormalPostViewHolder.tv_like_button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_like_filled, 0, 0, 0);
        } else {
            sharedNormalPostViewHolder.tv_like_button.setTextColor(ContextCompat.getColor(activity, R.color.med_grey));
            sharedNormalPostViewHolder.tv_like_button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_like_unfilled, 0, 0, 0);
        }


        if (photos == null || photos.size() == 0) {
            sharedNormalPostViewHolder.iv_cover_photo1.setVisibility(View.GONE);
            sharedNormalPostViewHolder.ll_two_images.setVisibility(View.GONE);
            sharedNormalPostViewHolder.ll_three_images.setVisibility(View.GONE);
        } else {
            int height = imageFixedHeight;
            int width = imageFixedWidth;
            if (photos.get(0).imgDimensions != null && !photos.get(0).imgDimensions.isEmpty()) {
                ImageDimensions dimen = getScaledDimensions(photos.get(0).imgDimensions, AppKeys.NORMAL);
                height = dimen.height;
                width = dimen.width;
               /* sharedNormalPostViewHolder.iv_cover_photo1.getLayoutParams().height = height;
                sharedNormalPostViewHolder.iv_cover_photo1.getLayoutParams().width = width;*/
            }

            if (photos.size() != 2) {
                sharedNormalPostViewHolder.iv_cover_photo1.setVisibility(View.VISIBLE);
                if (activity != null) Glide.with(activity.getApplicationContext())
                        .load(photos.get(0).source)
                        .apply(new RequestOptions().override(width, height))
                        .placeholder(R.drawable.placeholder)
                        .into(sharedNormalPostViewHolder.iv_cover_photo1);
            }

            if (photos.size() == 1) {
                sharedNormalPostViewHolder.ll_two_images.setVisibility(View.GONE);
                sharedNormalPostViewHolder.ll_three_images.setVisibility(View.GONE);
            } else if (photos.size() == 2) {
                sharedNormalPostViewHolder.iv_cover_photo1.setVisibility(View.GONE);
                sharedNormalPostViewHolder.ll_two_images.setVisibility(View.VISIBLE);
                if (activity != null)
                    Glide.with(activity.getApplicationContext()).load(photos.get(0).source).into(sharedNormalPostViewHolder.iv_cover_photo2);
                if (activity != null)
                    Glide.with(activity.getApplicationContext()).load(photos.get(1).source).into(sharedNormalPostViewHolder.iv_cover_photo3);
                sharedNormalPostViewHolder.ll_three_images.setVisibility(View.GONE);
            } else if (photos.size() == 3) {
                sharedNormalPostViewHolder.ll_two_images.setVisibility(View.VISIBLE);
                if (activity != null)
                    Glide.with(activity.getApplicationContext()).load(photos.get(1).source).into(sharedNormalPostViewHolder.iv_cover_photo2);
                if (activity != null)
                    Glide.with(activity.getApplicationContext()).load(photos.get(2).source).into(sharedNormalPostViewHolder.iv_cover_photo3);
                sharedNormalPostViewHolder.ll_three_images.setVisibility(View.GONE);
            } else if (photos.size() == 4) {
                sharedNormalPostViewHolder.ll_two_images.setVisibility(View.GONE);
                sharedNormalPostViewHolder.ll_three_images.setVisibility(View.VISIBLE);
                if (activity != null)
                    Glide.with(activity.getApplicationContext()).load(photos.get(1).source).into(sharedNormalPostViewHolder.iv_cover_photo4);
                if (activity != null)
                    Glide.with(activity.getApplicationContext()).load(photos.get(2).source).into(sharedNormalPostViewHolder.iv_cover_photo5);
                if (activity != null)
                    Glide.with(activity.getApplicationContext()).load(photos.get(3).source).into(sharedNormalPostViewHolder.iv_cover_photo6);
                sharedNormalPostViewHolder.tv_more_images.setVisibility(View.GONE);

            } else {
                sharedNormalPostViewHolder.ll_two_images.setVisibility(View.GONE);
                sharedNormalPostViewHolder.ll_three_images.setVisibility(View.VISIBLE);
                if (activity != null)
                    Glide.with(activity.getApplicationContext()).load(photos.get(1).source).into(sharedNormalPostViewHolder.iv_cover_photo4);
                if (activity != null)
                    Glide.with(activity.getApplicationContext()).load(photos.get(2).source).into(sharedNormalPostViewHolder.iv_cover_photo5);
                if (activity != null)
                    Glide.with(activity.getApplicationContext()).load(photos.get(3).source).into(sharedNormalPostViewHolder.iv_cover_photo6);
                sharedNormalPostViewHolder.tv_more_images.setVisibility(View.VISIBLE);
            }
        }

        sharedNormalPostViewHolder.cv_post_wrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // commonFunctions.setToastMessage(activity,"item clicked",Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);
                Intent normalPostIntent = new Intent(activity, SharedNormalPostActivity.class);
                normalPostIntent.putExtra(AppKeys.POST_ID, currentItem.postId);
                activity.startActivity(normalPostIntent);
            }
        });

        sharedNormalPostViewHolder.tv_like_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (user_id.equals(AppKeys.GUEST_USER_ID)) {
                    commonFunctions.showLoginRequestDialog();
                } else {
                    normalPostCommentListAdapterCallback.newsFeedItemClickCallback(position,
                            currentItem, AppKeys.POST_LIKE);
                }
            }
        });

        sharedNormalPostViewHolder.tv_share_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (user_id.equals(AppKeys.GUEST_USER_ID)) {
                    commonFunctions.showLoginRequestDialog();
                } else {
                    normalPostCommentListAdapterCallback.newsFeedItemClickCallback(position,
                            currentItem, AppKeys.POST_SHARE);
                }
            }
        });

        sharedNormalPostViewHolder.tv_like_number.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (user_id.equals(AppKeys.GUEST_USER_ID)) {
                    commonFunctions.showLoginRequestDialog();
                } else {
                    normalPostCommentListAdapterCallback.newsFeedItemClickCallback(position,
                            postItem, AppKeys.LIKE_COUNT);
                }
            }
        });

        sharedNormalPostViewHolder.tv_comment_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user_id.equals(AppKeys.GUEST_USER_ID)) {
                    commonFunctions.showLoginRequestDialog();
                } else {
                    // commonFunctions.setToastMessage(activity,"item clicked",Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);
                    normalPostCommentListAdapterCallback.newsFeedItemClickCallback(position,
                            postItem, AppKeys.COMMENT);
                }
            }
        });

        //if there are 2 photos we show second row which  has photoview number 2 and 3
        //if there are 3 photos we show 1st and second row with 1 and 2 photos in the row respectively
        //if there are 4 photos we show 1st and 3rd row with 1 and 3 photos respectively

        sharedNormalPostViewHolder.iv_cover_photo1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                normalPostCommentListAdapterCallback.newsFeedItemClickCallback(0, postItem, AppKeys.PHOTO_CLICK);
            }
        });

        sharedNormalPostViewHolder.iv_cover_photo2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (photos != null && photos.size() == 2) {
                    normalPostCommentListAdapterCallback.newsFeedItemClickCallback(0, postItem,
                            AppKeys.PHOTO_CLICK);
                } else {
                    normalPostCommentListAdapterCallback.newsFeedItemClickCallback(1, postItem,
                            AppKeys.PHOTO_CLICK);
                }
            }
        });

        sharedNormalPostViewHolder.iv_cover_photo3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (photos != null && photos.size() == 2) {
                    normalPostCommentListAdapterCallback.newsFeedItemClickCallback(1, postItem,
                            AppKeys.PHOTO_CLICK);
                } else {
                    normalPostCommentListAdapterCallback.newsFeedItemClickCallback(2, postItem,
                            AppKeys.PHOTO_CLICK);
                }
            }
        });

        sharedNormalPostViewHolder.iv_cover_photo4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (photos != null && photos.size() == 4) {
                    normalPostCommentListAdapterCallback.newsFeedItemClickCallback(1, postItem,
                            AppKeys.PHOTO_CLICK);
                } else {
                    normalPostCommentListAdapterCallback.newsFeedItemClickCallback(3, postItem,
                            AppKeys.PHOTO_CLICK);
                }
            }
        });

        sharedNormalPostViewHolder.iv_cover_photo5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (photos != null && photos.size() == 4) {
                    normalPostCommentListAdapterCallback.newsFeedItemClickCallback(2, postItem,
                            AppKeys.PHOTO_CLICK);
                } else {
                    normalPostCommentListAdapterCallback.newsFeedItemClickCallback(4, postItem,
                            AppKeys.PHOTO_CLICK);
                }
            }
        });


        sharedNormalPostViewHolder.iv_cover_photo6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (photos != null && photos.size() == 4) {
                    normalPostCommentListAdapterCallback.newsFeedItemClickCallback(3, postItem,
                            AppKeys.PHOTO_CLICK);
                } else {
                    normalPostCommentListAdapterCallback.newsFeedItemClickCallback(5, postItem,
                            AppKeys.PHOTO_CLICK);

                }
            }
        });


        sharedNormalPostViewHolder.rl_top_bar_wrapper.setVisibility(View.GONE);
    }


    private void configPollPostViewHolder(final PollPostViewHolder pollPostViewHolder, final int position) {
        final PojoGetNewsFeedResponseData currentItem = postItem;

        pollPostViewHolder.pollPostAdapter.setVoteCasted(false);
        pollPostViewHolder.pollPostAdapter.setNewsFeedItemPosition(position);
        pollPostViewHolder.rl_user_data_wrapper.setVisibility(View.VISIBLE);

        pollPostViewHolder.tv_time_stamp.setText(commonFunctions.parseNewsFeedItemTime(currentItem.updatedAt));
        if (currentItem.iLike == 1) {
            pollPostViewHolder.tv_like_button.setTextColor(ContextCompat.getColor(activity, R.color.app_theme_medium));
            pollPostViewHolder.tv_like_button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_like_filled,
                    0, 0, 0);
        } else {
            pollPostViewHolder.tv_like_button.setTextColor(ContextCompat.getColor(activity, R.color.med_grey));
            pollPostViewHolder.tv_like_button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_like_unfilled,
                    0, 0, 0);
        }

        pollPostViewHolder.tv_poll_title.setText(Html.fromHtml(currentItem.textPlain));

        if (currentItem.postType.equals("shared")) {
            pollPostViewHolder.tv_poster_name.setText(postItem.origin.userFullname);
            try {
                Glide.with(activity.getApplicationContext())
                        .load(postItem.origin.postAuthorPicture)
                        .apply(new RequestOptions().override(96, 96))
                        .apply(bitmapTransform(new CropCircleTransformation()))
                        .into(pollPostViewHolder.iv_poster_dp);
            } catch (Exception e) {
                e.printStackTrace();
            }
            pollPostViewHolder.tv_poster_designation.setText(postItem.origin.userWorkTitle);

            if (currentItem.origin.poll.votes.equals("0")) {
                pollPostViewHolder.tv_like_number.setVisibility(View.GONE);
            } else {
                pollPostViewHolder.tv_like_number.setVisibility(View.VISIBLE);
            }
            pollPostViewHolder.tv_like_number.setText(currentItem.origin.poll.votes + " " + activity.getString(R.string.votes));

            if (currentItem.origin.comments.equals("0")) {
                pollPostViewHolder.tv_comment_number.setVisibility(View.GONE);
            } else {
                pollPostViewHolder.tv_comment_number.setVisibility(View.VISIBLE);
            }
            pollPostViewHolder.tv_comment_number.setText(currentItem.origin.comments + " " + activity.getString(R.string.comments));
            pollPostViewHolder.optionList.clear();
            pollPostViewHolder.optionList.addAll(currentItem.origin.poll.options);
            pollPostViewHolder.pollPostAdapter.setTotalVotes(currentItem.origin.poll.votes);

            pollPostViewHolder.tv_comment_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (user_id.equals(AppKeys.GUEST_USER_ID)) {
                        commonFunctions.showLoginRequestDialog();
                    } else {
                        normalPostCommentListAdapterCallback.newsFeedItemClickCallback(position,
                                postItem, AppKeys.COMMENT);
                    }
                }
            });

            if (currentItem.origin != null && currentItem.origin.authorId.equals(user_id)) {
                pollPostViewHolder.pollPostAdapter.setCanSeeVotes(true);
            } else {
                pollPostViewHolder.pollPostAdapter.setCanSeeVotes(false);
            }
        } else {
            pollPostViewHolder.tv_poster_name.setText(postItem.userFullname);
            try {
                Glide.with(activity.getApplicationContext())
                        .load(postItem.postAuthorPicture)
                        .apply(new RequestOptions().override(96, 96))
                        .apply(bitmapTransform(new CropCircleTransformation()))
                        .into(pollPostViewHolder.iv_poster_dp);
            } catch (Exception e) {
                e.printStackTrace();
            }
            pollPostViewHolder.tv_poster_designation.setText(postItem.userWorkTitle);

            if (currentItem.authorId.equals(user_id)) {
                pollPostViewHolder.pollPostAdapter.setCanSeeVotes(true);
            } else {
                pollPostViewHolder.pollPostAdapter.setCanSeeVotes(false);
            }
            if (currentItem.poll.votes.equals("0")) {
                pollPostViewHolder.tv_like_number.setVisibility(View.GONE);
            } else {
                pollPostViewHolder.tv_like_number.setVisibility(View.VISIBLE);
            }
            pollPostViewHolder.tv_like_number.setText(currentItem.poll.votes + " " + activity.getString(R.string.votes));

            if (currentItem.comments.equals("0")) {
                pollPostViewHolder.tv_comment_number.setVisibility(View.GONE);
            } else {
                pollPostViewHolder.tv_comment_number.setVisibility(View.VISIBLE);
            }
            pollPostViewHolder.tv_comment_number.setText(currentItem.comments + " " + activity.getString(R.string.comments));
            pollPostViewHolder.optionList.clear();
            pollPostViewHolder.optionList.addAll(currentItem.poll.options);
            pollPostViewHolder.pollPostAdapter.setTotalVotes(currentItem.poll.votes);

            pollPostViewHolder.tv_comment_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (user_id.equals(AppKeys.GUEST_USER_ID)) {
                        commonFunctions.showLoginRequestDialog();
                    } else {
                        normalPostCommentListAdapterCallback.newsFeedItemClickCallback(position,
                                postItem, AppKeys.COMMENT);
                    }
                }
            });
        }

        for (int i = 0; i < pollPostViewHolder.optionList.size(); i++) {
            if (pollPostViewHolder.optionList.get(i).checked) {
                pollPostViewHolder.pollPostAdapter.setVoteCasted(true);
                pollPostViewHolder.pollPostAdapter.setCanSeeVotes(true);

                break;
            }
        }

        pollPostViewHolder.tv_like_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (user_id.equals(AppKeys.GUEST_USER_ID)) {
                    commonFunctions.showLoginRequestDialog();
                } else {
                    normalPostCommentListAdapterCallback.newsFeedItemClickCallback(position,
                            currentItem, AppKeys.POST_LIKE);
                }
            }
        });

        pollPostViewHolder.tv_share_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (user_id.equals(AppKeys.GUEST_USER_ID)) {
                    commonFunctions.showLoginRequestDialog();
                } else {
                    normalPostCommentListAdapterCallback.newsFeedItemClickCallback(position,
                            currentItem, AppKeys.POST_SHARE);
                }
            }
        });

        pollPostViewHolder.tv_like_number.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user_id.equals(AppKeys.GUEST_USER_ID)) {
                    commonFunctions.showLoginRequestDialog();
                } else {
                    normalPostCommentListAdapterCallback.newsFeedItemClickCallback(position,
                            currentItem, AppKeys.NUM_VOTES);
                }
            }
        });
        pollPostViewHolder.pollPostAdapter.notifyDataSetChanged();
        pollPostViewHolder.rl_top_bar_wrapper.setVisibility(View.GONE);

    }


    private void configSharedPollViewHolder(final ActionSharedPollPostViewHolder sharedPollViewHolder, final int position) {
        final PojoGetNewsFeedResponseData currentItem = postItem;
        sharedPollViewHolder.rl_top_bar_wrapper.setVisibility(View.GONE);
        sharedPollViewHolder.pollPostAdapter.setVoteCasted(false);
        sharedPollViewHolder.pollPostAdapter.setNewsFeedItemPosition(position);

        sharedPollViewHolder.rl_user_data_wrapper.setVisibility(View.VISIBLE);
        sharedPollViewHolder.tv_post_sharer_name.setText(postItem.userFullname);
        Glide.with(activity.getApplicationContext())
                .load(postItem.postAuthorPicture)
                .apply(new RequestOptions().override(96, 96))
                .apply(bitmapTransform(new CropCircleTransformation()))
                .into(sharedPollViewHolder.iv_post_sharer_user_dp);


        sharedPollViewHolder.tv_post_sharer_designation.setText(postItem.userWorkTitle);

        //sharer data
        if (currentItem.postTitle.isEmpty()) {
            sharedPollViewHolder.tv_share_title.setVisibility(View.GONE);
        } else {
            sharedPollViewHolder.tv_share_title.setVisibility(View.VISIBLE);
            sharedPollViewHolder.tv_share_title.setText(Html.fromHtml(currentItem.postTitle));
        }
        sharedPollViewHolder.tv_post_sharer_stamp.setText(commonFunctions
                .parseNewsFeedItemTime(currentItem.updatedAt));
        sharedPollViewHolder.tv_post_sharer_name.setText(currentItem.userFullname);
        sharedPollViewHolder.tv_post_sharer_designation.setText(currentItem.userWorkTitle);
        sharedPollViewHolder.tv_post_sharer_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent profileIntent = new Intent(activity, ProfileInfoActivity.class);
                profileIntent.putExtra("target_user_id", currentItem.authorId);
                activity.startActivity(profileIntent);
            }
        });

        if (!currentItem.postAuthorPicture.isEmpty()) {
            if (activity != null)
                Glide.with(activity.getApplicationContext()).load(currentItem.postAuthorPicture)
                        .apply(bitmapTransform(new CropCircleTransformation()))
                        .into(sharedPollViewHolder.iv_post_sharer_user_dp);
        }
        sharedPollViewHolder.iv_post_sharer_user_dp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharedPollViewHolder.tv_post_sharer_name.callOnClick();
            }
        });


        //poster data
        sharedPollViewHolder.tv_poster_name.setText(currentItem.origin.userFullname);
        sharedPollViewHolder.tv_poster_designation.setText(currentItem.origin.userWorkTitle);

        sharedPollViewHolder.tv_poster_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent profileIntent = new Intent(activity, ProfileInfoActivity.class);
                profileIntent.putExtra("target_user_id", currentItem.origin.authorId);
                activity.startActivity(profileIntent);
            }
        });

        if (!currentItem.origin.postAuthorPicture.isEmpty()) {
            if (activity != null)
                Glide.with(activity.getApplicationContext()).load(currentItem.origin.postAuthorPicture)
                        .apply(bitmapTransform(new CropCircleTransformation()))
                        .into(sharedPollViewHolder.iv_poster_dp);
        }

        sharedPollViewHolder.iv_poster_dp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharedPollViewHolder.tv_poster_name.callOnClick();
            }
        });


        sharedPollViewHolder.tv_time_stamp.setText(commonFunctions.parseNewsFeedItemTime(currentItem.origin.time));
        if (currentItem.iLike == 1) {
            sharedPollViewHolder.tv_like_button.setTextColor(ContextCompat.getColor(activity, R.color.app_theme_medium));
            sharedPollViewHolder.tv_like_button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_like_filled, 0, 0, 0);
        } else {
            sharedPollViewHolder.tv_like_button.setTextColor(ContextCompat.getColor(activity, R.color.med_grey));
            sharedPollViewHolder.tv_like_button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_like_unfilled, 0, 0, 0);
        }

        sharedPollViewHolder.tv_like_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (user_id.equals(AppKeys.GUEST_USER_ID)) {
                    commonFunctions.showLoginRequestDialog();
                } else {
                    normalPostCommentListAdapterCallback.newsFeedItemClickCallback(position,
                            currentItem, AppKeys.POST_LIKE);
                }
            }
        });


        sharedPollViewHolder.tv_like_number.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user_id.equals(AppKeys.GUEST_USER_ID)) {
                    commonFunctions.showLoginRequestDialog();
                } else {
                    normalPostCommentListAdapterCallback.newsFeedItemClickCallback(position,
                            currentItem, AppKeys.NUM_VOTES);
                }
            }
        });

        sharedPollViewHolder.tv_share_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (user_id.equals(AppKeys.GUEST_USER_ID)) {
                    commonFunctions.showLoginRequestDialog();
                } else {
                    normalPostCommentListAdapterCallback.newsFeedItemClickCallback(position,
                            currentItem, AppKeys.POST_SHARE);
                }
            }
        });


        sharedPollViewHolder.cv_origin_wrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent pollIntent = new Intent(activity, PollActivity.class);
                pollIntent.putExtra(AppKeys.POST_ID, currentItem.origin.postId);
                activity.startActivity(pollIntent);
            }
        });

        sharedPollViewHolder.tv_comment_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (user_id.equals(AppKeys.GUEST_USER_ID)) {
                    commonFunctions.showLoginRequestDialog();
                } else {
                    normalPostCommentListAdapterCallback.newsFeedItemClickCallback(position,
                            postItem, AppKeys.COMMENT);
                }
            }
        });


        if (currentItem.origin.poll.votes.equals("0")) {
            sharedPollViewHolder.tv_like_number.setVisibility(View.GONE);
        } else {
            sharedPollViewHolder.tv_like_number.setVisibility(View.VISIBLE);
        }
        sharedPollViewHolder.tv_like_number.setText(currentItem.origin.poll.votes + " " + activity.getString(R.string.votes));

        if (currentItem.comments.equals("0")) {
            sharedPollViewHolder.tv_comment_number.setVisibility(View.GONE);
        } else {
            sharedPollViewHolder.tv_comment_number.setVisibility(View.VISIBLE);
        }
        sharedPollViewHolder.tv_comment_number.setText(currentItem.comments + " " + activity.getString(R.string.comments));
        sharedPollViewHolder.tv_poll_title.setText(Html.fromHtml(currentItem.origin.textPlain));
        sharedPollViewHolder.optionList.clear();
        sharedPollViewHolder.optionList.addAll(currentItem.origin.poll.options);
        sharedPollViewHolder.pollPostAdapter.setTotalVotes(currentItem.origin.poll.votes);
        if (currentItem.origin != null && currentItem.origin.authorId.equals(user_id)) {
            sharedPollViewHolder.pollPostAdapter.setCanSeeVotes(true);
        }

        for (int i = 0; i < sharedPollViewHolder.optionList.size(); i++) {
            if (sharedPollViewHolder.optionList.get(i).checked) {
                sharedPollViewHolder.pollPostAdapter.setVoteCasted(true);
                sharedPollViewHolder.pollPostAdapter.setCanSeeVotes(true);
                break;
            }
        }

        sharedPollViewHolder.pollPostAdapter.notifyDataSetChanged();
        sharedPollViewHolder.rl_top_bar_wrapper.setVisibility(View.GONE);

    }

    private void configSharedArticleViewHolder(final ActionSharedArticleViewHolder sharedArticleViewHolder, final int position) {
        final PojoGetNewsFeedResponseData currentItem = postItem;
        sharedArticleViewHolder.rl_top_bar_wrapper.setVisibility(View.GONE);

        //sharer data
        if (currentItem.postTitle.isEmpty()) {
            sharedArticleViewHolder.tv_share_title.setVisibility(View.GONE);
        } else {
            sharedArticleViewHolder.tv_share_title.setVisibility(View.VISIBLE);
            sharedArticleViewHolder.tv_share_title.setText(Html.fromHtml(currentItem.postTitle));
        }
        sharedArticleViewHolder.tv_post_sharer_stamp.setText(commonFunctions
                .parseNewsFeedItemTime(currentItem.updatedAt));
        sharedArticleViewHolder.tv_post_sharer_name.setText(currentItem.userFullname);
        sharedArticleViewHolder.tv_post_sharer_designation.setText(currentItem.userWorkTitle);
        sharedArticleViewHolder.tv_post_sharer_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent profileIntent = new Intent(activity, ProfileInfoActivity.class);
                profileIntent.putExtra("target_user_id", currentItem.authorId);
                activity.startActivity(profileIntent);
            }
        });

        if (!currentItem.postAuthorPicture.isEmpty()) {
            if (activity != null)
                Glide.with(activity.getApplicationContext()).load(currentItem.postAuthorPicture)
                        .apply(bitmapTransform(new CropCircleTransformation()))
                        .into(sharedArticleViewHolder.iv_post_sharer_user_dp);
        }
        sharedArticleViewHolder.iv_post_sharer_user_dp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharedArticleViewHolder.tv_post_sharer_name.callOnClick();
            }
        });


        //article writer data
        sharedArticleViewHolder.tv_writer_name.setText(currentItem.origin.createdBy.userFullname);
        sharedArticleViewHolder.tv_writer_designation.setText(currentItem.origin.createdBy.userWorkTitle);
        sharedArticleViewHolder.tv_time_stamp.setText(commonFunctions
                .parseNewsFeedItemTime(currentItem.origin.addedOn));

        if (!currentItem.origin.createdBy.userPicture.isEmpty()) {
            if (activity != null)
                Glide.with(activity.getApplicationContext()).load(currentItem.origin.createdBy.userPicture)
                        .apply(new RequestOptions().override(96, 96))
                        .apply(bitmapTransform(new CropCircleTransformation()))
                        .into(sharedArticleViewHolder.iv_user_dp);
        }

        sharedArticleViewHolder.iv_user_dp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharedArticleViewHolder.tv_writer_name.callOnClick();
            }
        });

        sharedArticleViewHolder.tv_writer_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent profileIntent = new Intent(activity, ProfileInfoActivity.class);
                profileIntent.putExtra("target_user_id", currentItem.origin.createdBy.userId);
                activity.startActivity(profileIntent);
            }
        });

        sharedArticleViewHolder.cv_origin_wrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent articleIntent = new Intent(activity, ArticleActivity.class);
                articleIntent.putExtra(AppKeys.POST_ID, currentItem.origin.articlesId);
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
                sharedArticleViewHolder.iv_cover_photo.getLayoutParams().height = height;
                sharedArticleViewHolder.iv_cover_photo.getLayoutParams().width = width;
            }

           /* CardView.LayoutParams layoutParams=(CardView.LayoutParams)articleViewHolder.iv_cover_photo.getLayoutParams();
            layoutParams.height=height;
            layoutParams.width=width;
            articleViewHolder.iv_cover_photo.setLayoutParams(layoutParams);*/

            if (activity != null) Glide.with(activity.getApplicationContext())
                    .load(currentItem.origin.coverPhoto)
                    .apply(new RequestOptions().override(width, height).placeholder(R.drawable.placeholder))
                    .into(sharedArticleViewHolder.iv_cover_photo);

        }


        sharedArticleViewHolder.tv_article_name.setText(Html.fromHtml(currentItem.origin.title));

        if (currentItem.totalLikes == 0) {
            sharedArticleViewHolder.tv_like_number.setVisibility(View.GONE);
        } else {
            sharedArticleViewHolder.tv_like_number.setVisibility(View.VISIBLE);
        }
        sharedArticleViewHolder.tv_like_number.setText("" + currentItem.totalLikes + " " + activity.getString(R.string.likes));

        if (currentItem.comments.equals("0")) {
            sharedArticleViewHolder.tv_comment_number.setVisibility(View.GONE);
        } else {
            sharedArticleViewHolder.tv_comment_number.setVisibility(View.VISIBLE);
        }
        sharedArticleViewHolder.tv_comment_number.setText("" + currentItem.comments + " "
                + activity.getString(R.string.comments));

        sharedArticleViewHolder.tv_share_number.setText("" + currentItem.shares + " " + activity.getString(R.string.shares));


        if (currentItem.iLike == 1) {
            sharedArticleViewHolder.tv_like_button.setTextColor(ContextCompat.getColor(activity, R.color.app_theme_medium));
            sharedArticleViewHolder.tv_like_button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_like_filled, 0, 0, 0);
        } else {
            sharedArticleViewHolder.tv_like_button.setTextColor(ContextCompat.getColor(activity, R.color.med_grey));
            sharedArticleViewHolder.tv_like_button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_like_unfilled, 0, 0, 0);
        }

        sharedArticleViewHolder.tv_like_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (user_id.equals(AppKeys.GUEST_USER_ID)) {
                    commonFunctions.showLoginRequestDialog();
                } else {
                    normalPostCommentListAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                            AppKeys.POST_LIKE);
                }
            }
        });

        sharedArticleViewHolder.tv_share_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (user_id.equals(AppKeys.GUEST_USER_ID)) {
                    commonFunctions.showLoginRequestDialog();
                } else {
                    normalPostCommentListAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                            AppKeys.ARTICLE_SHARE);
                }
            }
        });


        sharedArticleViewHolder.tv_comment_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // commonFunctions.setToastMessage(activity,"item clicked",Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);
                if (user_id.equals(AppKeys.GUEST_USER_ID)) {
                    commonFunctions.showLoginRequestDialog();
                } else {
                    normalPostCommentListAdapterCallback.newsFeedItemClickCallback(position,
                            postItem, AppKeys.COMMENT);
                }
            }
        });
        sharedArticleViewHolder.rl_top_bar_wrapper.setVisibility(View.GONE);

    }


    private void setPreviewData(CommentViewHolder holder, final PojoNormalPostComment currentItem) {
        String title = currentItem.sourceContent.getTitle();
        String name = currentItem.sourceContent.getCannonicalUrl();
        String description = currentItem.sourceContent.getDescription();

        if (title.isEmpty() || name.isEmpty()) {
            holder.rl_web_preview_wrapper.setVisibility(View.GONE);
            holder.tv_comment.setVisibility(View.VISIBLE);
            holder.tv_comment.setHighlightColor(Color.TRANSPARENT); // prevent TextView change background when highlight
            holder.tv_comment.setMovementMethod(LinkMovementMethod.getInstance());
            SpannableClickItem spannableClickItem = commonFunctions.setClickSpans(currentItem.text,
                    currentItem.text, false, null);
            currentItem.clickableSpansList = spannableClickItem.clickableSpansList;
            holder.tv_comment
                    .setText(spannableClickItem.spannableString, TextView.BufferType.SPANNABLE);
        } else {
            holder.rl_web_preview_wrapper.setVisibility(View.VISIBLE);
            holder.rl_web_preview_wrapper.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    commonFunctions.urlLoader(Html.fromHtml(currentItem.text).toString());
                    /*Intent urlIntent = new Intent(activity, WebViewActivity.class);
                    urlIntent.putExtra("url", Html.fromHtml(currentItem.text).toString());
                    activity.startActivity(urlIntent);*/
                }
            });
            holder.tv_link_title.setText(title);
            holder.tv_website_name.setText(name);
            holder.tv_link_description.setText(description);
            holder.tv_comment.setText(Html.fromHtml(currentItem.text).toString());
            holder.tv_comment.setVisibility(View.VISIBLE);
            if (!currentItem.sourceContent.getImages().isEmpty()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    if (!activity.isDestroyed()) {
                        if (activity != null)
                            Glide.with(activity.getApplicationContext()).load(currentItem.sourceContent.getImages().get(0)).into(holder.iv_link_image);
                    }
                }
            }
        }
    }


    public class NormalPostViewHolder extends RecyclerView.ViewHolder {
        CardView cv_post_wrapper;
        TextView tv_activity_performed;
        TextView tv_poster_name;
        TextView tv_poster_designation;
        TextView tv_time_stamp;
        TextView tv_post_content;
        TextView tv_view_more;
        TextView tv_like_number;
        TextView tv_comment_number;
        TextView tv_like_button;
        TextView tv_comment_button;
        TextView tv_share_button;
        TextView tv_boost_post;

        RelativeLayout rl_top_bar_wrapper;

        RelativeLayout rl_web_preview_wrapper;
        ImageView iv_link_image;
        TextView tv_link_title;
        TextView tv_website_name;
        TextView tv_link_description;
        TextCrawler textCrawler;

        ImageView iv_cover_photo1;
        ProportionalImageView iv_cover_photo2;
        ProportionalImageView iv_cover_photo3;
        ProportionalImageView iv_cover_photo4;
        ProportionalImageView iv_cover_photo5;
        ProportionalImageView iv_cover_photo6;
        TextView tv_more_images;
        ImageView iv_user_dp;

        LinearLayout ll_two_images;
        LinearLayout ll_three_images;
        LinearLayout ll_content_wrapper;


        public NormalPostViewHolder(View itemView) {
            super(itemView);
            cv_post_wrapper = (CardView) itemView.findViewById(R.id.cv_post_wrapper);
            rl_top_bar_wrapper = (RelativeLayout) itemView.findViewById(R.id.rl_top_bar_wrapper);
            tv_activity_performed = (TextView) itemView.findViewById(R.id.tv_activity_performed);
            tv_poster_name = (TextView) itemView.findViewById(R.id.tv_poster_name);
            tv_poster_designation = (TextView) itemView.findViewById(R.id.tv_poster_designation);
            iv_user_dp = (ImageView) itemView.findViewById(R.id.iv_user_dp);

            tv_time_stamp = (TextView) itemView.findViewById(R.id.tv_time_stamp);
            tv_post_content = (TextView) itemView.findViewById(R.id.tv_post_content);
            tv_view_more = (TextView) itemView.findViewById(R.id.tv_view_more);

            tv_like_number = (TextView) itemView.findViewById(R.id.tv_like_number);
            tv_comment_number = (TextView) itemView.findViewById(R.id.tv_comment_number);
            tv_like_button = (TextView) itemView.findViewById(R.id.tv_like_button);
            tv_comment_button = (TextView) itemView.findViewById(R.id.tv_comment_button);
            tv_share_button = (TextView) itemView.findViewById(R.id.tv_share_button);
            tv_boost_post = (TextView) itemView.findViewById(R.id.tv_boost_post);

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

        }
    }


    public class PollPostViewHolder extends RecyclerView.ViewHolder {

        CardView cv_post_wrapper;
        RecyclerView rv_poll_list;
        PollPostAdapter pollPostAdapter;
        List<PojoNewsFeedPollOption> optionList;

        RelativeLayout rl_top_bar_wrapper;
        TextView tv_activity_performed;
        TextView tv_poll_title;
        TextView tv_time_stamp;
        TextView tv_like_number;
        TextView tv_comment_number;
        TextView tv_share_number;
        TextView tv_like_button;
        TextView tv_comment_button;
        TextView tv_share_button;


        RelativeLayout rl_user_data_wrapper;
        ImageView iv_poster_dp;
        TextView tv_poster_name;
        TextView tv_poster_designation;


        public PollPostViewHolder(View itemView) {
            super(itemView);
            cv_post_wrapper = (CardView) itemView.findViewById(R.id.cv_post_wrapper);
            rl_top_bar_wrapper = (RelativeLayout) itemView.findViewById(R.id.rl_top_bar_wrapper);
            tv_activity_performed = (TextView) itemView.findViewById(R.id.tv_activity_performed);
            tv_poll_title = (TextView) itemView.findViewById(R.id.tv_poll_title);
            tv_time_stamp = (TextView) itemView.findViewById(R.id.tv_time_stamp);
            tv_like_number = (TextView) itemView.findViewById(R.id.tv_like_number);
            tv_comment_number = (TextView) itemView.findViewById(R.id.tv_comment_number);
            tv_share_number = (TextView) itemView.findViewById(R.id.tv_share_number);
            tv_like_button = (TextView) itemView.findViewById(R.id.tv_like_button);
            tv_comment_button = (TextView) itemView.findViewById(R.id.tv_comment_button);
            tv_share_button = (TextView) itemView.findViewById(R.id.tv_share_button);


            optionList = new ArrayList<>();
            rv_poll_list = (RecyclerView) itemView.findViewById(R.id.rv_poll_list);
            pollPostAdapter = new PollPostAdapter(activity, optionList, "normal");
            //using horizontal linearlayout as we want horizontal list
            rv_poll_list.setNestedScrollingEnabled(false);
            rv_poll_list.setLayoutManager(new LinearLayoutManager(activity, RecyclerView.VERTICAL, false));
            rv_poll_list.setAdapter(pollPostAdapter);

            rl_user_data_wrapper = (RelativeLayout) itemView.findViewById(R.id.rl_user_data_wrapper);
            iv_poster_dp = (ImageView) itemView.findViewById(R.id.iv_poster_dp);
            tv_poster_name = (TextView) itemView.findViewById(R.id.tv_poster_name);
            tv_poster_designation = (TextView) itemView.findViewById(R.id.tv_poster_designation);
        }
    }

    public class ActionSharedNormalPostViewHolder extends RecyclerView.ViewHolder {
        CardView cv_post_wrapper;
        TextView tv_activity_performed;

        CardView cv_origin_wrapper;
        ImageView iv_user_dp;
        TextView tv_poster_name;
        TextView tv_poster_designation;
        TextView tv_time_stamp;

        TextView tv_share_title;
        ImageView iv_post_sharer_user_dp;
        TextView tv_post_sharer_name;
        TextView tv_post_sharer_designation;
        TextView tv_post_sharer_stamp;

        TextView tv_view_more;
        TextView tv_post_content;
        TextView tv_like_number;
        TextView tv_comment_number;
        TextView tv_like_button;
        TextView tv_comment_button;
        TextView tv_share_button;

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

        LinearLayout ll_two_images;
        LinearLayout ll_three_images;
        LinearLayout ll_content_wrapper;

        MenuInflater inflater;

        public ActionSharedNormalPostViewHolder(View itemView) {
            super(itemView);
            cv_post_wrapper = (CardView) itemView.findViewById(R.id.cv_post_wrapper);
            rl_top_bar_wrapper = (RelativeLayout) itemView.findViewById(R.id.rl_top_bar_wrapper);
            tv_activity_performed = (TextView) itemView.findViewById(R.id.tv_activity_performed);
            tv_poster_name = (TextView) itemView.findViewById(R.id.tv_poster_name);
            tv_poster_designation = (TextView) itemView.findViewById(R.id.tv_poster_designation);
            iv_user_dp = (ImageView) itemView.findViewById(R.id.iv_user_dp);

            cv_origin_wrapper = (CardView) itemView.findViewById(R.id.cv_origin_wrapper);

            tv_share_title = (TextView) itemView.findViewById(R.id.tv_share_title);
            iv_post_sharer_user_dp = (ImageView) itemView.findViewById(R.id.iv_post_sharer_user_dp);
            tv_post_sharer_name = (TextView) itemView.findViewById(R.id.tv_post_sharer_name);
            tv_post_sharer_designation = (TextView) itemView.findViewById(R.id.tv_post_sharer_designation);
            tv_post_sharer_stamp = (TextView) itemView.findViewById(R.id.tv_post_sharer_stamp);

            tv_time_stamp = (TextView) itemView.findViewById(R.id.tv_time_stamp);
            tv_post_content = (TextView) itemView.findViewById(R.id.tv_post_content);
            tv_view_more = (TextView) itemView.findViewById(R.id.tv_view_more);

            tv_like_number = (TextView) itemView.findViewById(R.id.tv_like_number);
            tv_comment_number = (TextView) itemView.findViewById(R.id.tv_comment_number);
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

        }
    }


    public class ActionSharedPollPostViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout rl_top_bar_wrapper;
        LinearLayout ll_poll_wrapper;
        TextView tv_share_title;
        ImageView iv_post_sharer_user_dp;
        TextView tv_post_sharer_name;
        TextView tv_post_sharer_designation;
        TextView tv_post_sharer_stamp;
        CardView cv_origin_wrapper;

        RecyclerView rv_poll_list;
        PollPostAdapter pollPostAdapter;
        TextView tv_activity_performed;
        TextView tv_poll_title;
        TextView tv_time_stamp;
        TextView tv_like_number;
        TextView tv_comment_number;
        TextView tv_share_number;
        TextView tv_like_button;
        TextView tv_comment_button;
        TextView tv_share_button;
        List<PojoNewsFeedPollOption> optionList;

        RelativeLayout rl_user_data_wrapper;
        ImageView iv_poster_dp;
        TextView tv_poster_name;
        TextView tv_poster_designation;


        public ActionSharedPollPostViewHolder(View itemView) {
            super(itemView);
            rl_top_bar_wrapper = (RelativeLayout) itemView.findViewById(R.id.rl_top_bar_wrapper);
            ll_poll_wrapper = (LinearLayout) itemView.findViewById(R.id.ll_poll_wrapper);
            tv_share_title = (TextView) itemView.findViewById(R.id.tv_share_title);
            iv_post_sharer_user_dp = (ImageView) itemView.findViewById(R.id.iv_post_sharer_user_dp);
            tv_post_sharer_name = (TextView) itemView.findViewById(R.id.tv_post_sharer_name);
            tv_post_sharer_designation = (TextView) itemView.findViewById(R.id.tv_post_sharer_designation);
            tv_post_sharer_stamp = (TextView) itemView.findViewById(R.id.tv_post_sharer_stamp);
            cv_origin_wrapper = (CardView) itemView.findViewById(R.id.cv_origin_wrapper);

            tv_activity_performed = (TextView) itemView.findViewById(R.id.tv_activity_performed);
            tv_poll_title = (TextView) itemView.findViewById(R.id.tv_poll_title);
            tv_time_stamp = (TextView) itemView.findViewById(R.id.tv_time_stamp);
            tv_like_number = (TextView) itemView.findViewById(R.id.tv_like_number);
            tv_comment_number = (TextView) itemView.findViewById(R.id.tv_comment_number);
            tv_share_number = (TextView) itemView.findViewById(R.id.tv_share_number);
            tv_like_button = (TextView) itemView.findViewById(R.id.tv_like_button);
            tv_comment_button = (TextView) itemView.findViewById(R.id.tv_comment_button);
            tv_share_button = (TextView) itemView.findViewById(R.id.tv_share_button);


            optionList = new ArrayList<>();
            rv_poll_list = (RecyclerView) itemView.findViewById(R.id.rv_poll_list);
            pollPostAdapter = new PollPostAdapter(activity, optionList, "shared");
            //using horizontal linearlayout as we want horizontal list
            rv_poll_list.setNestedScrollingEnabled(false);
            rv_poll_list.setLayoutManager(new LinearLayoutManager(activity, RecyclerView.VERTICAL, false));
            rv_poll_list.setAdapter(pollPostAdapter);

            rl_user_data_wrapper = (RelativeLayout) itemView.findViewById(R.id.rl_user_data_wrapper);
            iv_poster_dp = (ImageView) itemView.findViewById(R.id.iv_poster_dp);
            tv_poster_name = (TextView) itemView.findViewById(R.id.tv_poster_name);
            tv_poster_designation = (TextView) itemView.findViewById(R.id.tv_poster_designation);
        }
    }

    public class ActionSharedArticleViewHolder extends RecyclerView.ViewHolder {
        TextView tv_share_title;
        ImageView iv_post_sharer_user_dp;
        TextView tv_post_sharer_name;
        TextView tv_post_sharer_designation;
        TextView tv_post_sharer_stamp;
        CardView cv_origin_wrapper;

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
        TextView tv_like_button;
        TextView tv_comment_button;
        TextView tv_share_button;

        TextView tv_activity_performed;

        LinearLayout ll_target_click_wrapper;


        public ActionSharedArticleViewHolder(View itemView) {
            super(itemView);
            tv_share_title = (TextView) itemView.findViewById(R.id.tv_share_title);
            iv_post_sharer_user_dp = (ImageView) itemView.findViewById(R.id.iv_post_sharer_user_dp);
            tv_post_sharer_name = (TextView) itemView.findViewById(R.id.tv_post_sharer_name);
            tv_post_sharer_designation = (TextView) itemView.findViewById(R.id.tv_post_sharer_designation);
            tv_post_sharer_stamp = (TextView) itemView.findViewById(R.id.tv_post_sharer_stamp);

            cv_origin_wrapper = (CardView) itemView.findViewById(R.id.cv_origin_wrapper);
            rl_top_bar_wrapper = (RelativeLayout) itemView.findViewById(R.id.rl_top_bar_wrapper);
            rl_writer_wrapper = (RelativeLayout) itemView.findViewById(R.id.rl_writer_wrapper);
            ll_target_click_wrapper = (LinearLayout) itemView.findViewById(R.id.ll_target_click_wrapper);
            iv_user_dp = (ImageView) itemView.findViewById(R.id.iv_user_dp);
            iv_cover_photo = (ImageView) itemView.findViewById(R.id.iv_cover_photo);
            tv_writer_name = (TextView) itemView.findViewById(R.id.tv_writer_name);
            tv_writer_designation = (TextView) itemView.findViewById(R.id.tv_writer_designation);
            tv_time_stamp = (TextView) itemView.findViewById(R.id.tv_time_stamp);
            tv_article_name = (TextView) itemView.findViewById(R.id.tv_article_name);
            tv_like_number = (TextView) itemView.findViewById(R.id.tv_like_number);
            tv_comment_number = (TextView) itemView.findViewById(R.id.tv_comment_number);
            tv_share_number = (TextView) itemView.findViewById(R.id.tv_share_number);
            tv_like_button = (TextView) itemView.findViewById(R.id.tv_like_button);
            tv_comment_button = (TextView) itemView.findViewById(R.id.tv_comment_button);
            tv_share_button = (TextView) itemView.findViewById(R.id.tv_share_button);
            tv_activity_performed = (TextView) itemView.findViewById(R.id.tv_activity_performed);


        }
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_user_dp;
        TextView tv_user_name;
        TextView tv_time_stamp;
        TextView tv_comment;
        TextView tv_like_button;
        TextView tv_reply_button;
        TextView tv_num_of_likes;
        TextView tv_num_of_replies;

        RelativeLayout rl_web_preview_wrapper;
        ImageView iv_link_image;
        TextView tv_link_title;
        TextView tv_website_name;
        TextView tv_link_description;
        RelativeLayout rl_parent;
        PopupMenu popupMenu;
        ImageView iv_more_options;

        RelativeLayout rl_data_wrapper;
        TextView tv_load_more_button;
        ProgressBar pb_loading_more;


        CommentViewHolder(View view) {
            super(view);
            iv_user_dp = (ImageView) itemView.findViewById(R.id.iv_user_dp);
            tv_user_name = (TextView) itemView.findViewById(R.id.tv_user_name);
            tv_time_stamp = (TextView) itemView.findViewById(R.id.tv_time_stamp);
            tv_like_button = (TextView) itemView.findViewById(R.id.tv_like_button);
            tv_num_of_replies = (TextView) itemView.findViewById(R.id.tv_num_of_replies);
            tv_num_of_likes = (TextView) itemView.findViewById(R.id.tv_num_of_likes);
            tv_reply_button = (TextView) itemView.findViewById(R.id.tv_reply_button);
            tv_comment = (TextView) itemView.findViewById(R.id.tv_comment);
            rl_web_preview_wrapper = (RelativeLayout) itemView.findViewById(R.id.rl_web_preview_wrapper);
            rl_web_preview_wrapper.setVisibility(View.GONE);
            iv_link_image = (ImageView) itemView.findViewById(R.id.iv_link_image);
            tv_link_title = (TextView) itemView.findViewById(R.id.tv_link_title);
            tv_website_name = (TextView) itemView.findViewById(R.id.tv_website_name);
            tv_link_description = (TextView) itemView.findViewById(R.id.tv_link_description);
            rl_parent = (RelativeLayout) itemView.findViewById(R.id.rl_parent);
            iv_more_options = (ImageView) view.findViewById(R.id.iv_more_options);
            iv_more_options.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupMenu.show();
                }
            });

            popupMenu = new PopupMenu(activity, iv_more_options);
            rl_data_wrapper = (RelativeLayout) itemView.findViewById(R.id.rl_data_wrapper);
            tv_load_more_button = (TextView) itemView.findViewById(R.id.tv_load_more_button);
            pb_loading_more = (ProgressBar) itemView.findViewById(R.id.pb_loading_more);
            pb_loading_more.setVisibility(View.GONE);
        }
    }


    @Override
    public int getItemCount() {
        if (commentList.isEmpty()) {
            if (postItem == null) {
                return 0;
            } else {
                return 1;
            }
        } else {
            return commentList.size() + 2;
        }

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
                RecyclerView.VERTICAL, false);
        rv_list.setLayoutManager(linearLayoutManager);
        UpvotersAndAttendeesListAdapter upvotersAndAttendeesListAdapter =
                new UpvotersAndAttendeesListAdapter(activity, userList);
        rv_list.setAdapter(upvotersAndAttendeesListAdapter);
        userListDialog.show();
    }


    public static interface NormalPostCommentListAdapterCallback {
        void normalPostCommentListItemClickCallback(int position, String type);

        void newsFeedItemClickCallback(int position, PojoGetNewsFeedResponseData currentItem, String type);

    }


    public ImageDimensions getScaledDimensions(String imgDimen, String type) {
        ImageDimensions dimen = new ImageDimensions();


        int height = Integer.parseInt(imgDimen.substring(0, imgDimen.indexOf("X")));
        int width = Integer.parseInt(imgDimen.substring(imgDimen.indexOf("X") + 1, imgDimen.length()));
        if (type.equals(AppKeys.NORMAL)) {
            width = Integer.parseInt(imgDimen.substring(0, imgDimen.indexOf("X")));
            height = Integer.parseInt(imgDimen.substring(imgDimen.indexOf("X") + 1, imgDimen.length()));
        }

        float imgRatio = 1f * width / height;
        int targetWidth = (int) (displayMetrics.widthPixels);
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

            case AppKeys.VERIFIED_SESSION:
                float px = TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        16,
                        activity.getResources().getDisplayMetrics()
                );
                targetWidth = (int) ((displayMetrics.widthPixels) - px);
                targetHeight = (int) (1f * targetWidth / imgRatio);
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
