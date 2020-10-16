package com.avadna.luneblaze.adapters.article;

import android.app.Activity;
import android.app.Dialog;

import com.avadna.luneblaze.adapters.UpvotersAndAttendeesListAdapter;
import com.avadna.luneblaze.helperClasses.MyCustomThemeDialog;

import android.content.Intent;
import android.graphics.Color;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
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
import com.avadna.luneblaze.activities.article.ArticleCommentReplyListActivity;
import com.avadna.luneblaze.activities.profileInfo.ProfileInfoActivity;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.helperClasses.SpannableClickItem;
import com.avadna.luneblaze.pojo.pojoArticle.PojoArticleCommentsResponseData;
import com.avadna.luneblaze.pojo.pojoQuestion.PojoUserData;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.leocardz.link.preview.library.LinkPreviewCallback;
import com.leocardz.link.preview.library.SourceContent;
import com.leocardz.link.preview.library.TextCrawler;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

public class ArticleCommentsAdapter extends RecyclerView.Adapter<ArticleCommentsAdapter.RecyclableHolder> {
    //  pivate List<String> hierarchyList;
    Activity activity;

    PreferenceUtils preferenceUtils;
    String user_id;
    String USER = "user";
    String OTHER = "other";
    String type = USER;
    List<PojoArticleCommentsResponseData> commentList;
    ApiInterface apiService;
    ArticleCommentAdapterCallback articleCommentAdapterCallback;
    CommonFunctions commonFunctions;
    String creator_id;

    boolean showProgressBar = false;
    boolean moreNextAvailable = true;
    boolean morePreviousAvailable = true;
    boolean showBottomMoreButton = true;
    boolean showTopMoreButton = true;


    @Override
    public ArticleCommentsAdapter.RecyclableHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;

        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.comment_list_item, parent, false);
        return new RecyclableHolder(itemView);


    }


    public ArticleCommentsAdapter(Activity activity, List<PojoArticleCommentsResponseData> commentList) {
        try {
            this.articleCommentAdapterCallback = ((ArticleCommentAdapterCallback) activity);
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement articleCommentListAdapterCallback.");
        }
        this.activity = activity;
        this.commentList = commentList;
        preferenceUtils = new PreferenceUtils(activity);
        user_id = preferenceUtils.get_user_id();
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
        commonFunctions = new CommonFunctions(activity);
    }


    public class RecyclableHolder extends RecyclerView.ViewHolder {
        ImageView iv_user_dp;
        TextView tv_user_name;
        TextView tv_time_stamp;
        TextView tv_comment;
        TextView tv_like_button;
        TextView tv_reply_button;

        RelativeLayout rl_web_preview_wrapper;
        ImageView iv_link_image;
        TextView tv_link_title;
        TextView tv_website_name;
        TextView tv_link_description;
        TextView tv_num_of_likes;
        TextView tv_num_of_replies;

        ImageView iv_more_options;
        PopupMenu popupMenu;
        MenuInflater inflater;

        RelativeLayout rl_data_wrapper;
        TextView tv_load_more_button;
        ProgressBar pb_loading_more;

        RecyclableHolder(View view) {
            super(view);
            iv_user_dp = (ImageView) itemView.findViewById(R.id.iv_user_dp);
            tv_user_name = (TextView) itemView.findViewById(R.id.tv_user_name);
            tv_time_stamp = (TextView) itemView.findViewById(R.id.tv_time_stamp);
            tv_comment = (TextView) itemView.findViewById(R.id.tv_comment);
            rl_web_preview_wrapper = (RelativeLayout) itemView.findViewById(R.id.rl_web_preview_wrapper);
            rl_web_preview_wrapper.setVisibility(View.GONE);
            iv_link_image = (ImageView) itemView.findViewById(R.id.iv_link_image);
            tv_link_title = (TextView) itemView.findViewById(R.id.tv_link_title);
            tv_like_button = (TextView) itemView.findViewById(R.id.tv_like_button);
            tv_reply_button = (TextView) itemView.findViewById(R.id.tv_reply_button);
            tv_website_name = (TextView) itemView.findViewById(R.id.tv_website_name);
            tv_link_description = (TextView) itemView.findViewById(R.id.tv_link_description);
            tv_num_of_likes = (TextView) itemView.findViewById(R.id.tv_num_of_likes);
            tv_num_of_replies = (TextView) itemView.findViewById(R.id.tv_num_of_replies);

            iv_more_options = (ImageView) itemView.findViewById(R.id.iv_more_options);
            popupMenu = new PopupMenu(activity, iv_more_options);
            inflater = popupMenu.getMenuInflater();

            rl_data_wrapper = (RelativeLayout) itemView.findViewById(R.id.rl_data_wrapper);
            tv_load_more_button = (TextView) itemView.findViewById(R.id.tv_load_more_button);
            pb_loading_more = (ProgressBar) itemView.findViewById(R.id.pb_loading_more);
            pb_loading_more.setVisibility(View.GONE);
        }
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


    @Override
    public void onBindViewHolder(final ArticleCommentsAdapter.RecyclableHolder holder, final int position) {
        /*if (position == 0) {
            holder.rl_data_wrapper.setVisibility(View.GONE);
            holder.tv_load_more_button.setVisibility(View.GONE);
            holder.tv_load_more_button.setText(activity.getString(R.string.load_previous));

            if (!morePreviousAvailable) {
                holder.tv_load_more_button.setText(activity.getString(R.string.no_more_available));
            }

            if (showProgressBar && showTopMoreButton) {
                holder.pb_loading_more.setVisibility(View.VISIBLE);
                holder.tv_load_more_button.setVisibility(View.GONE);
            } else {
                holder.pb_loading_more.setVisibility(View.GONE);
                if (showTopMoreButton) {
                    holder.tv_load_more_button.setVisibility(View.VISIBLE);
                } else {
                    holder.tv_load_more_button.setVisibility(View.GONE);
                }
            }

            holder.tv_load_more_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    articleCommentAdapterCallback.onCommentActionPerformed(position, AppKeys.LOAD_PREVIOUS);
                }
            });

        } else */if (position == commentList.size()) {
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
                        articleCommentAdapterCallback.onCommentActionPerformed(position,
                                AppKeys.LOAD_NEXT);
                    }
                }
            });
        } else {
            holder.tv_load_more_button.setVisibility(View.GONE);
            holder.pb_loading_more.setVisibility(View.GONE);
            holder.rl_data_wrapper.setVisibility(View.VISIBLE);
            final PojoArticleCommentsResponseData currentItem = commentList.get(position);

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
                    String title = sourceContent.getTitle();
                    String name = sourceContent.getCannonicalUrl();
                    String description = sourceContent.getDescription();

                    if (title.isEmpty() || name.isEmpty()) {
                        holder.rl_web_preview_wrapper.setVisibility(View.GONE);
                        holder.tv_comment.setVisibility(View.VISIBLE);
                        holder.tv_comment.setHighlightColor(Color.TRANSPARENT); // prevent TextView change background when highlight
                        holder.tv_comment.setMovementMethod(LinkMovementMethod.getInstance());
                        SpannableClickItem spannableClickItem = commonFunctions.setClickSpans(currentItem.post,
                                currentItem.post, false, null);
                        currentItem.clickableSpansList = spannableClickItem.clickableSpansList;
                        holder.tv_comment
                                .setText(spannableClickItem.spannableString, TextView.BufferType.SPANNABLE);
                    } else {
                        holder.rl_web_preview_wrapper.setVisibility(View.VISIBLE);
                        holder.tv_link_title.setText(title);
                        holder.tv_website_name.setText(name);
                        holder.tv_link_description.setText(description);
                        if (!sourceContent.getImages().isEmpty()) {
                            if (activity != null)
                                Glide.with(activity.getApplicationContext())
                                        .load(sourceContent.getImages().get(0))
                                        .into(holder.iv_link_image);
                        }
                    }
                }
            };

            String text = Html.fromHtml(currentItem.post).toString();
            currentItem.textCrawler = new TextCrawler();

            if (Patterns.WEB_URL.matcher(currentItem.post).matches()) {
                currentItem.textCrawler.makePreview(linkPreviewCallback, text);
                holder.tv_comment.setVisibility(View.GONE);
            } else {
                holder.rl_web_preview_wrapper.setVisibility(View.GONE);
                holder.tv_comment.setHighlightColor(
                        Color.TRANSPARENT); // prevent TextView change background when highlight
                holder.tv_comment.setMovementMethod(LinkMovementMethod.getInstance());
                SpannableClickItem spannableClickItem = commonFunctions.setClickSpans(text, text,
                        false, null);
                currentItem.clickableSpansList = spannableClickItem.clickableSpansList;
                holder.tv_comment
                        .setText(spannableClickItem.spannableString, TextView.BufferType.SPANNABLE);
                holder.tv_comment.setVisibility(View.VISIBLE);
            }

            if (!currentItem.user.userPicture.isEmpty()) {
                if (activity != null) Glide.with(activity.getApplicationContext())
                        .load(currentItem.user.userPicture)
                        .apply(new RequestOptions().override(96, 96))
                        .apply(bitmapTransform(new CropCircleTransformation()))
                        .into(holder.iv_user_dp);
            }

            holder.tv_user_name.setText(currentItem.user.userFullname);
            holder.tv_user_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent profileIntent = new Intent(activity,
                            ProfileInfoActivity.class);
                    profileIntent.putExtra("target_user_id", currentItem.userId);
                    activity.startActivity(profileIntent);
                }
            });

            holder.tv_time_stamp.setText(commonFunctions.parseNewsFeedItemTime(currentItem.modifiedOn));
            if (currentItem.totalLikes == 0) {
                holder.tv_num_of_likes.setVisibility(View.GONE);
            } else {
                holder.tv_num_of_likes.setVisibility(View.VISIBLE);
            }
            holder.tv_num_of_likes.setText(currentItem.totalLikes + " " + activity.getString(R.string.likes));


            holder.tv_num_of_likes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    articleCommentAdapterCallback.onCommentActionPerformed(position,AppKeys.LIKE_COUNT);
                }
            });

            if (currentItem.iLike != null && currentItem.iLike == 1) {
                holder.tv_like_button.setTextColor(ContextCompat.getColor(activity, R.color.app_theme_medium));
                holder.tv_like_button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_like_filled, 0, 0, 0);
            } else {
                holder.tv_like_button.setTextColor(ContextCompat.getColor(activity, R.color.med_grey));
                holder.tv_like_button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_like_unfilled, 0, 0, 0);
            }


            if (currentItem.totalReplies == 0) {
                holder.tv_num_of_replies.setVisibility(View.GONE);
            } else {
                holder.tv_num_of_replies.setVisibility(View.VISIBLE);
            }
            holder.tv_num_of_replies.setText(currentItem.totalReplies + " " + activity.getString(R.string.replies));

            if (currentItem.articlesDiscussionId == null || currentItem.articlesDiscussionId.isEmpty()) {
                holder.tv_reply_button.setOnClickListener(null);
                holder.tv_like_button.setOnClickListener(null);
                holder.rl_data_wrapper.setOnClickListener(null);
                holder.iv_more_options.setVisibility(View.GONE);
                holder.rl_data_wrapper.setAlpha(0.6f);
            } else {
                holder.iv_more_options.setVisibility(View.VISIBLE);
                holder.rl_data_wrapper.setAlpha(1f);
                holder.tv_reply_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(activity, ArticleCommentReplyListActivity.class);
                        intent.putExtra(AppKeys.COMMENT_ID, currentItem.articlesDiscussionId);
                        intent.putExtra(AppKeys.ARTICLE_ID, currentItem.articlesId);
                        intent.putExtra(AppKeys.TARGET, AppKeys.ADD_REPLY);
                        activity.startActivity(intent);
                    }
                });

                holder.tv_like_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        articleCommentAdapterCallback.onCommentActionPerformed(position, AppKeys.LIKE_COMMENT);
                    }
                });

                holder.rl_data_wrapper.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(activity, ArticleCommentReplyListActivity.class);
                        intent.putExtra(AppKeys.COMMENT_ID, currentItem.articlesDiscussionId);
                        intent.putExtra(AppKeys.ARTICLE_ID, currentItem.articlesId);
                        String dataStr = new Gson().toJson(
                                currentItem,
                                new TypeToken<PojoArticleCommentsResponseData>() {
                                }.getType());
                        intent.putExtra("data", dataStr);
                        activity.startActivity(intent);
                    }
                });
            }





     /*   holder.replyList.clear();
        holder.replyList.addAll(currentItem.replies);
        holder.articleReplyAdapter.setCommentPosition(position);
        holder.articleReplyAdapter.setCreator_id(currentItem.userId);
        holder.articleReplyAdapter.notifyDataSetChanged();*/


            holder.popupMenu.getMenu().clear();
            if (currentItem.user.userId.equals(user_id)) {
                holder.inflater.inflate(R.menu.comment_options_poster, holder.popupMenu.getMenu());
            } else if (user_id.equals(creator_id)) {
                holder.inflater.inflate(R.menu.comment_options_creator, holder.popupMenu.getMenu());
            } else {
                holder.inflater.inflate(R.menu.comment_options_other, holder.popupMenu.getMenu());
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
                        case R.id.report:
                            articleCommentAdapterCallback.onCommentActionPerformed(position,
                                    AppKeys.REPORT_COMMENT);
                            break;

                        case R.id.edit:
                            articleCommentAdapterCallback.onCommentActionPerformed(position,
                                    AppKeys.EDIT);
                            break;

                        case R.id.delete:
                            articleCommentAdapterCallback.onCommentActionPerformed(position,
                                    AppKeys.DELETE);
                            break;
                    }
                    return false;
                }
            });

            holder.iv_more_options.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.popupMenu.show();
                }
            });

        }
    }


    public void setPreviewData(RecyclableHolder holder, final PojoArticleCommentsResponseData currentItem) {
        String title = currentItem.sourceContent.getTitle();
        final String name = currentItem.sourceContent.getCannonicalUrl();
        String description = currentItem.sourceContent.getDescription();

        if (title.isEmpty() || name.isEmpty()) {
            holder.rl_web_preview_wrapper.setVisibility(View.GONE);
            holder.tv_comment.setVisibility(View.VISIBLE);
            holder.tv_comment.setHighlightColor(Color.TRANSPARENT); // prevent TextView change background when highlight
            holder.tv_comment.setMovementMethod(LinkMovementMethod.getInstance());
            SpannableClickItem spannableClickItem = commonFunctions.setClickSpans(currentItem.post,
                    currentItem.post, false, null);
            currentItem.clickableSpansList = spannableClickItem.clickableSpansList;
            holder.tv_comment
                    .setText(spannableClickItem.spannableString, TextView.BufferType.SPANNABLE);
        } else {
            holder.rl_web_preview_wrapper.setVisibility(View.VISIBLE);
            holder.rl_web_preview_wrapper.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    commonFunctions.urlLoader(Html.fromHtml(currentItem.post).toString());
                    /*Intent urlIntent = new Intent(activity, WebViewActivity.class);
                    urlIntent.putExtra("url", Html.fromHtml(currentItem.post).toString());
                    activity.startActivity(urlIntent);*/
                }
            });
            holder.tv_link_title.setText(title);
            holder.tv_website_name.setText(name);
            holder.tv_link_description.setText(description);
            holder.tv_comment.setVisibility(View.VISIBLE);
            holder.tv_comment.setText(Html.fromHtml(currentItem.post).toString());
            if (!currentItem.sourceContent.getImages().isEmpty()) {
                if (activity != null)
                    Glide.with(activity.getApplicationContext()).load(currentItem.sourceContent.getImages().get(0)).into(holder.iv_link_image);
            }
        }
    }


    @Override
    public int getItemCount() {

        return commentList.size() + 1;

    }




    public static interface ArticleCommentAdapterCallback {
        void onCommentActionPerformed(int position, String actionType);
    }


}