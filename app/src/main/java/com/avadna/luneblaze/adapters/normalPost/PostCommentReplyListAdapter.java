package com.avadna.luneblaze.adapters.normalPost;

import android.app.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;

import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import com.avadna.luneblaze.pojo.pojonormalpost.PojoNormalPostComment;
import com.avadna.luneblaze.pojo.pojonormalpost.pojoreplylist.PojoNormalPostCommentReplyListItem;
import com.avadna.luneblaze.pojo.pojonormalpost.pojoreplylist.PojoReplyListParentComment;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.leocardz.link.preview.library.LinkPreviewCallback;
import com.leocardz.link.preview.library.SourceContent;
import com.leocardz.link.preview.library.TextCrawler;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

public class PostCommentReplyListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    int listSize;
    Activity activity;
    Fragment fragment;
    List<PojoNormalPostCommentReplyListItem> replyList;

    PreferenceUtils preferenceUtils;
    String user_id;
    String creator_id;
    ApiInterface apiService;
    String type;

    PostCommentReplyListAdapterCallback replyListAdapterCallback;
    PojoReplyListParentComment commentItem;
    CommonFunctions commonFunctions;

    boolean showProgressBar = false;
    boolean moreNextAvailable = true;
    boolean morePreviousAvailable = true;
    boolean showBottomMoreButton = true;
    boolean showTopMoreButton = true;

    public PostCommentReplyListAdapter(Fragment fragment, List<PojoNormalPostCommentReplyListItem> replyList,
                                       String type) {
        try {
            this.replyListAdapterCallback = ((PostCommentReplyListAdapterCallback) fragment);
        } catch (ClassCastException e) {
            throw new ClassCastException("Fragment must implement replyAdapterCallback.");
        }
        this.activity = fragment.getActivity();
        this.replyList = replyList;
        this.type = type;
        preferenceUtils = new PreferenceUtils(activity);
        user_id = preferenceUtils.get_user_id();
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
        commonFunctions = new CommonFunctions(activity);

    }

    public PostCommentReplyListAdapter(Activity activity, List<PojoNormalPostCommentReplyListItem> replyList,
                                       String type) {
        try {
            this.replyListAdapterCallback = ((PostCommentReplyListAdapterCallback) activity);
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement replyAdapterCallback.");
        }
        this.activity = activity;
        this.replyList = replyList;
        this.type = type;
        preferenceUtils = new PreferenceUtils(this.activity);
        user_id = preferenceUtils.get_user_id();
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
        commonFunctions = new CommonFunctions(activity);

    }

    public void setCommentItem(PojoReplyListParentComment commentItem) {
        this.commentItem = commentItem;
        notifyDataSetChanged();
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
    }


    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return 1;
        } else {
            return 2;
        }
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
                        .inflate(R.layout.reply_list_item, parent, false);
                return new ReplyViewHolder(itemView);

            default:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.reply_list_item, parent, false);
                return new ReplyViewHolder(itemView);
        }
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        switch (holder.getItemViewType()) {
            case 1:
                CommentViewHolder commentViewHolder = (CommentViewHolder) holder;
                configCommentViewHolder(commentViewHolder, position);
                break;

            case 2:
                ReplyViewHolder replyViewHolder = (ReplyViewHolder) holder;
                configReplyViewHolder(replyViewHolder, position - 1);
                break;
        }

    }

    private void configCommentViewHolder(CommentViewHolder commentViewHolder, int position) {
        {
            commentViewHolder.tv_load_more_button.setVisibility(View.GONE);
            commentViewHolder.pb_loading_more.setVisibility(View.GONE);
            commentViewHolder.rl_data_wrapper.setVisibility(View.VISIBLE);
            final PojoReplyListParentComment currentItem = commentItem;

            LinkPreviewCallback linkPreviewCallback = new LinkPreviewCallback() {
                @Override
                public void onPre() {
                    // Any work that needs to be done before generating the preview. Usually inflate
                    // your custom preview layout here.
                    //commonFunctions.setToastMessage(activity,"link preview called",Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);
                    commentViewHolder.rl_web_preview_wrapper.setVisibility(View.GONE);
                }

                @Override
                public void onPos(SourceContent sourceContent, boolean b) {
                    // Populate your preview layout with the results of sourceContent.
                    currentItem.sourceContent = sourceContent;
                    setPreviewData(commentViewHolder, currentItem);
                }
            };

            currentItem.textCrawler = new TextCrawler();
            String text = Html.fromHtml(currentItem.text).toString();
            if (Patterns.WEB_URL.matcher(text).matches()) {
                commentViewHolder.tv_comment.setText(text);
                if (currentItem.sourceContent == null) {
                    currentItem.textCrawler.makePreview(linkPreviewCallback, text);
                } else {
                    setPreviewData(commentViewHolder, currentItem);
                }
                commentViewHolder.rl_web_preview_wrapper.setVisibility(View.GONE);
            } else {
                commentViewHolder.rl_web_preview_wrapper.setVisibility(View.GONE);
                commentViewHolder.tv_comment.setVisibility(View.VISIBLE);
                commentViewHolder.tv_comment.setHighlightColor(Color.TRANSPARENT); // prevent TextView change background when highlight
                SpannableClickItem spannableClickItem = commonFunctions.setClickSpans(currentItem.text,
                        currentItem.text, false, null);
                currentItem.clickableSpansList = spannableClickItem.clickableSpansList;
                commentViewHolder.tv_comment
                        .setText(spannableClickItem.spannableString, TextView.BufferType.SPANNABLE);

                if (currentItem.clickableSpansList.isEmpty()) {
                    commentViewHolder.tv_comment.setMovementMethod(null);
                } else {
                    commentViewHolder.tv_comment.setMovementMethod(LinkMovementMethod.getInstance());
                }

            }

            if (!currentItem.userPicture.isEmpty()) {
                if (activity != null) Glide.with(activity.getApplicationContext())
                        .load(currentItem.userPicture)
                        .apply(new RequestOptions().override(96, 96))
                        .apply(bitmapTransform(new CropCircleTransformation()))
                        .into(commentViewHolder.iv_user_dp);
            }

            MenuInflater inflater = commentViewHolder.popupMenu.getMenuInflater();

            commentViewHolder.tv_user_name.setText(currentItem.userFullname);
            commentViewHolder.rl_user_data_wrapper.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent profileIntent = new Intent(activity,
                            ProfileInfoActivity.class);
                    profileIntent.putExtra("target_user_id", currentItem.userId);
                    activity.startActivity(profileIntent);
                }
            });

            commentViewHolder.tv_time_stamp.setText(commonFunctions.parseNewsFeedItemTime(currentItem.time));

            if (currentItem.replies.equals("0")) {
                commentViewHolder.tv_num_of_replies.setVisibility(View.GONE);
            } else {
                commentViewHolder.tv_num_of_replies.setVisibility(View.VISIBLE);
            }
            commentViewHolder.tv_num_of_replies.setText("" + currentItem.replies + " " + activity.getString(R.string.replies));

            if (currentItem.likes.equals("0")) {
                commentViewHolder.tv_num_of_likes.setVisibility(View.GONE);
            } else {
                commentViewHolder.tv_num_of_likes.setVisibility(View.VISIBLE);
            }

            if (currentItem.likes == 0) {
                commentViewHolder.tv_num_of_likes.setVisibility(View.GONE);

            } else {
                commentViewHolder.tv_num_of_likes.setText("" + currentItem.likes + " " + activity.getString(R.string.likes));
            }
            // holder.tv_num_of_replies.setText("" + currentItem.totalReplies + " " + activity.getString(R.string.replies));


            if (currentItem.iLike == 1) {
                commentViewHolder.tv_like_button.setCompoundDrawablesWithIntrinsicBounds
                        (R.drawable.ic_like_filled, 0, 0, 0);
                commentViewHolder.tv_like_button.setTextColor(ContextCompat.getColor(activity, R.color.app_theme_medium));
            } else {
                commentViewHolder.tv_like_button.setTextColor(ContextCompat.getColor(activity, R.color.med_grey));
                commentViewHolder.tv_like_button.setCompoundDrawablesWithIntrinsicBounds
                        (R.drawable.ic_like_unfilled, 0, 0, 0);
            }

            if (currentItem.commentId == null || currentItem.commentId.isEmpty()) {
                commentViewHolder.iv_more_options.setVisibility(View.GONE);
                commentViewHolder.tv_like_button.setOnClickListener(null);
                commentViewHolder.rl_data_wrapper.setOnClickListener(null);
                commentViewHolder.tv_reply_button.setOnClickListener(null);
                commentViewHolder.rl_parent.setAlpha(0.6f);
            } else {
                commentViewHolder.rl_parent.setAlpha(1f);
                commentViewHolder.iv_more_options.setVisibility(View.VISIBLE);

                commentViewHolder.rl_data_wrapper.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent replyIntent = new Intent(activity, PostCommentRepliesListActivity.class);
                        replyIntent.putExtra(AppKeys.COMMENT_ID, currentItem.commentId);
                        replyIntent.putExtra(AppKeys.POST_ID, currentItem.nodeId);
                        activity.startActivity(replyIntent);
                    }
                });


                if (user_id.equals(currentItem.userId)) {
                    commentViewHolder.popupMenu.getMenu().clear();
                    inflater.inflate(R.menu.comment_options_poster, commentViewHolder.popupMenu.getMenu());
                } else if (user_id.equals(creator_id)) {
                    commentViewHolder.popupMenu.getMenu().clear();
                    inflater.inflate(R.menu.comment_options_creator, commentViewHolder.popupMenu.getMenu());
                } else {
                    commentViewHolder.popupMenu.getMenu().clear();
                    inflater.inflate(R.menu.comment_options_other, commentViewHolder.popupMenu.getMenu());
                }

                try {
                    Field[] fields = commentViewHolder.popupMenu.getClass().getDeclaredFields();
                    for (Field field : fields) {
                        if ("mPopup".equals(field.getName())) {
                            field.setAccessible(true);
                            Object menuPopupHelper = field.get(commentViewHolder.popupMenu);
                            Class<?> classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());
                            Method setForceIcons = classPopupHelper.getMethod("setForceShowIcon", boolean.class);
                            setForceIcons.invoke(menuPopupHelper, true);
                            break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                commentViewHolder.popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.edit:
                                replyListAdapterCallback.commentItemClickCallback(position,
                                        AppKeys.EDIT);
                                break;

                            case R.id.delete:
                                replyListAdapterCallback.commentItemClickCallback(position,
                                        AppKeys.DELETE);

                                break;

                            case R.id.share_to:
                                // shareTextUrl(answerList.get(position).sessionsQaId);
                                break;

                            case R.id.report:
                                replyListAdapterCallback.commentItemClickCallback(position,
                                        AppKeys.REPORT_COMMENT);
                                // commonFunctions.openReportDialog(user_id, "report_comment", currentItem.replyId);
                                break;

                        }
                        return false;
                    }
                });

                commentViewHolder.tv_like_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        replyListAdapterCallback
                                .commentItemClickCallback(position, AppKeys.LIKE);
                    }
                });

                commentViewHolder.tv_reply_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        replyListAdapterCallback.commentItemClickCallback(position, AppKeys.REPLY);
                    }
                });
            }

            commentViewHolder.tv_num_of_likes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    replyListAdapterCallback.commentItemClickCallback(position, AppKeys.LIKE_COUNT);
                }
            });

            commentViewHolder.tv_num_of_replies.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    replyListAdapterCallback.commentItemClickCallback(position, AppKeys.REPLY_COUNT);
                }
            });

        }
    }

    private void setPreviewData(CommentViewHolder holder, final PojoReplyListParentComment currentItem) {
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
                            Glide.with(activity.getApplicationContext()).load(currentItem.sourceContent.getImages().get(0))
                                    .into(holder.iv_link_image);
                    }
                }
            }
        }
    }

    private void configReplyViewHolder(ReplyViewHolder replyViewHolder, int position) {
        if (position == replyList.size()) {
            replyViewHolder.rl_data_wrapper.setVisibility(View.GONE);
            replyViewHolder.tv_load_more_button.setText(activity.getString(R.string.load_more));


            if (!moreNextAvailable) {
                replyViewHolder.tv_load_more_button.setText(activity.getString(R.string.no_more_available));
            }

            if (showProgressBar && showBottomMoreButton) {
                replyViewHolder.pb_loading_more.setVisibility(View.VISIBLE);
                replyViewHolder.tv_load_more_button.setVisibility(View.GONE);
            } else {
                replyViewHolder.pb_loading_more.setVisibility(View.GONE);
                if (showBottomMoreButton) {
                    replyViewHolder.tv_load_more_button.setVisibility(View.VISIBLE);
                } else {
                    replyViewHolder.tv_load_more_button.setVisibility(View.GONE);
                }
            }

            replyViewHolder.tv_load_more_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (moreNextAvailable) {
                        replyListAdapterCallback.replyListItemClickCallback(position,
                                AppKeys.LOAD_NEXT);
                    }
                }
            });
        } else {
            replyViewHolder.tv_load_more_button.setVisibility(View.GONE);
            replyViewHolder.pb_loading_more.setVisibility(View.GONE);
            replyViewHolder.rl_data_wrapper.setVisibility(View.VISIBLE);
            final PojoNormalPostCommentReplyListItem currentItem = replyList.get(position);

            LinkPreviewCallback linkPreviewCallback = new LinkPreviewCallback() {
                @Override
                public void onPre() {
                    // Any work that needs to be done before generating the preview. Usually inflate
                    // your custom preview layout here.
                    //commonFunctions.setToastMessage(activity,"link preview called",Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);
                    replyViewHolder.rl_web_preview_wrapper.setVisibility(View.GONE);

                }

                @Override
                public void onPos(SourceContent sourceContent, boolean b) {
                    // Populate your preview layout with the results of sourceContent.
                    String title = sourceContent.getTitle();
                    String name = sourceContent.getCannonicalUrl();
                    String description = sourceContent.getDescription();
                    if (title.isEmpty() || name.isEmpty()) {
                        replyViewHolder.rl_web_preview_wrapper.setVisibility(View.GONE);
                        replyViewHolder.tv_comment.setVisibility(View.VISIBLE);
                        replyViewHolder.tv_comment.setHighlightColor(Color.TRANSPARENT); // prevent TextView change background when highlight
                        replyViewHolder.tv_comment.setMovementMethod(LinkMovementMethod.getInstance());
                        SpannableClickItem spannableClickItem = commonFunctions.setClickSpans(currentItem.text,
                                currentItem.text, false, null);
                        currentItem.clickableSpansList = spannableClickItem.clickableSpansList;
                        replyViewHolder.tv_comment
                                .setText(spannableClickItem.spannableString, TextView.BufferType.SPANNABLE);
                    } else {
                        replyViewHolder.rl_web_preview_wrapper.setVisibility(View.VISIBLE);
                        replyViewHolder.tv_link_title.setText(title);
                        replyViewHolder.tv_website_name.setText(name);
                        replyViewHolder.tv_link_description.setText(description);
                        if (!sourceContent.getImages().isEmpty()) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                                if (!activity.isDestroyed()) {
                                    if (activity != null)
                                        Glide.with(activity.getApplicationContext())
                                                .load(sourceContent.getImages().get(0))
                                                .into(replyViewHolder.iv_link_image);
                                }
                            }
                        }
                    }
                }
            };

            currentItem.textCrawler = new TextCrawler();
            String text = Html.fromHtml(currentItem.text).toString();

           /* if (Patterns.WEB_URL.matcher(text).matches()) {
                currentItem.textCrawler.makePreview(linkPreviewCallback, text);
                holder.rl_web_preview_wrapper.setVisibility(View.GONE);
                holder.tv_comment.setVisibility(View.GONE);
            } else*/
            {
                replyViewHolder.rl_web_preview_wrapper.setVisibility(View.GONE);
                replyViewHolder.tv_comment.setVisibility(View.VISIBLE);
                replyViewHolder.tv_comment.setHighlightColor(Color.TRANSPARENT); // prevent TextView change background when highlight
                replyViewHolder.tv_comment.setMovementMethod(LinkMovementMethod.getInstance());
                SpannableClickItem spannableClickItem = commonFunctions.setClickSpans(currentItem.text,
                        currentItem.text, false, null);
                currentItem.clickableSpansList = spannableClickItem.clickableSpansList;
                replyViewHolder.tv_comment
                        .setText(spannableClickItem.spannableString, TextView.BufferType.SPANNABLE);
            }

            if (!currentItem.userPicture.isEmpty()) {
                if (activity != null) Glide.with(activity.getApplicationContext())
                        .load(currentItem.userPicture)
                        .apply(new RequestOptions().override(96, 96))
                        .apply(bitmapTransform(new CropCircleTransformation()))
                        .into(replyViewHolder.iv_user_dp);
            }

            MenuInflater inflater = replyViewHolder.popupMenu.getMenuInflater();

            replyViewHolder.tv_user_name.setText(currentItem.userFullname);

            replyViewHolder.tv_user_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent profileIntent = new Intent(activity,
                            ProfileInfoActivity.class);
                    profileIntent.putExtra("target_user_id", currentItem.userId);
                    activity.startActivity(profileIntent);
                }
            });

            replyViewHolder.iv_user_dp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    replyViewHolder.tv_user_name.callOnClick();
                }
            });

            replyViewHolder.tv_time_stamp.setText(commonFunctions.parseNewsFeedItemTime(currentItem.time));

            if (currentItem.likes.equals("0")) {
                replyViewHolder.tv_num_likes.setVisibility(View.GONE);
            } else {
                replyViewHolder.tv_num_likes.setVisibility(View.VISIBLE);
            }
            replyViewHolder.tv_num_likes.setText(currentItem.likes + " " + activity.getString(R.string.likes));

            replyViewHolder.tv_num_likes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    replyListAdapterCallback.replyListItemClickCallback(position,
                            AppKeys.LIKE_COUNT);
                }
            });

            if (currentItem.iLike != null && currentItem.iLike == 1) {
                replyViewHolder.tv_like_button.setTextColor(ContextCompat.getColor(activity, R.color.app_theme_medium));
                replyViewHolder.tv_like_button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_like_filled, 0, 0, 0);
            } else {
                replyViewHolder.tv_like_button.setTextColor(ContextCompat.getColor(activity, R.color.med_grey));
                replyViewHolder.tv_like_button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_like_unfilled, 0, 0, 0);
            }

            if (currentItem.commentId == null || currentItem.commentId.isEmpty()) {
                replyViewHolder.tv_like_button.setOnClickListener(null);
                replyViewHolder.rl_parent.setAlpha(0.6f);
                replyViewHolder.iv_more_options.setVisibility(View.GONE);
            } else {
                replyViewHolder.rl_parent.setAlpha(1f);
                replyViewHolder.iv_more_options.setVisibility(View.VISIBLE);
                replyViewHolder.tv_like_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        replyListAdapterCallback.replyListItemClickCallback(position,
                                AppKeys.LIKE_REPLY);
                    }
                });
            }

            if (user_id.equals(currentItem.userId)) {
                replyViewHolder.popupMenu.getMenu().clear();
                inflater.inflate(R.menu.comment_options_poster, replyViewHolder.popupMenu.getMenu());
            } else if (user_id.equals(creator_id)) {
                replyViewHolder.popupMenu.getMenu().clear();
                inflater.inflate(R.menu.comment_options_creator, replyViewHolder.popupMenu.getMenu());
            } else {
                replyViewHolder.popupMenu.getMenu().clear();
                inflater.inflate(R.menu.comment_options_other, replyViewHolder.popupMenu.getMenu());
            }

            try {
                Field[] fields = replyViewHolder.popupMenu.getClass().getDeclaredFields();
                for (Field field : fields) {
                    if ("mPopup".equals(field.getName())) {
                        field.setAccessible(true);
                        Object menuPopupHelper = field.get(replyViewHolder.popupMenu);
                        Class<?> classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());
                        Method setForceIcons = classPopupHelper.getMethod("setForceShowIcon", boolean.class);
                        setForceIcons.invoke(menuPopupHelper, true);
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            replyViewHolder.popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.edit:
                            replyListAdapterCallback.replyListItemClickCallback(position,
                                    AppKeys.EDIT);
                            // openEditDialog(position);
                            break;

                        case R.id.delete:
                            replyListAdapterCallback.replyListItemClickCallback(position,
                                    AppKeys.DELETE);
                            //   hitDeleteAnswerApi(user_id, currentItem.sessionsQaId, "delete_question", position);
                            break;

                        case R.id.share_to:
                            //shareTextUrl(answerList.get(position).sessionsQaId);
                            break;

                        case R.id.report:
                            replyListAdapterCallback.replyListItemClickCallback(position,
                                    AppKeys.REPORT);

                            // commonFunctions.openReportDialog(user_id, "report_post_comment_reply", currentItem.replyCommentId);
                            break;

                    }
                    return false;
                }
            });


        }


    }

    public class ReplyViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_user_dp;
        TextView tv_user_name;
        TextView tv_time_stamp;
        TextView tv_comment;
        TextView tv_like_button;
        TextView tv_num_of_replies;
        TextView tv_num_likes;

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
        //   TextView tv_load_previous;
        ProgressBar pb_loading_more;


        ReplyViewHolder(View view) {
            super(view);
            iv_user_dp = (ImageView) itemView.findViewById(R.id.iv_user_dp);
            tv_user_name = (TextView) itemView.findViewById(R.id.tv_user_name);
            tv_time_stamp = (TextView) itemView.findViewById(R.id.tv_time_stamp);
            tv_like_button = (TextView) itemView.findViewById(R.id.tv_like_button);
            tv_num_of_replies = (TextView) itemView.findViewById(R.id.tv_num_of_replies);
            tv_num_likes = (TextView) itemView.findViewById(R.id.tv_num_likes);
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
            //   tv_load_previous = (TextView) itemView.findViewById(R.id.tv_load_previous);
            pb_loading_more = (ProgressBar) itemView.findViewById(R.id.pb_loading_more);
            pb_loading_more.setVisibility(View.GONE);

        }
    }


    public class CommentViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout rl_user_data_wrapper;
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
            rl_user_data_wrapper = (RelativeLayout) itemView.findViewById(R.id.rl_user_data_wrapper);
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

        if (replyList.isEmpty()) {
            if (commentItem == null) {
                return 0;
            } else {
                return 1;
            }
        } else {
            return replyList.size() + 2;
        }
    }

    public static interface PostCommentReplyListAdapterCallback {
        void replyListItemClickCallback(int position, String type);

        void commentItemClickCallback(int position, String type);
    }

}
