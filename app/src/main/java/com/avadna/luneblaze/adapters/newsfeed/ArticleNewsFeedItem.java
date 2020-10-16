package com.avadna.luneblaze.adapters.newsfeed;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.ImageDimensions;
import com.avadna.luneblaze.pojo.pojoNewsFeed.PojoGetNewsFeedResponseData;
import com.avadna.luneblaze.pojo.pojoNewsFeed.PojoNewsFeedCommentsArrayItem;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

public class ArticleNewsFeedItem extends NewsFeedItemBase{


    public ArticleNewsFeedItem(Fragment fragment, String userFixedname) {
        super(fragment,userFixedname);
    }

    public ArticleViewHolder getViewHolder(ViewGroup parent) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.article_post, parent, false);
        return new ArticleViewHolder(itemView);
    }

    public class ArticleViewHolder extends RecyclerView.ViewHolder {
        CardView cv_post_wrapper;
        RelativeLayout rl_writer_wrapper;
        RelativeLayout rl_top_bar_wrapper;
        ImageView iv_user_dp;
        ImageView iv_cover_photo;
        CardView cv_cover_wrapper;
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
        //  View comment_item_gap;

        TextView tv_activity_performed;
        ImageView iv_more_options;
        PopupMenu popup;
        MenuInflater inflater;

        ImageView iv_comment_dp;
        TextView tv_comment_user_name, tv_user_comment;
        RelativeLayout rl_user_comment_wrapper;
        LinearLayout ll_target_click_wrapper;


        public ArticleViewHolder(View itemView) {
            super(itemView);
            cv_post_wrapper = (CardView) itemView.findViewById(R.id.cv_post_wrapper);
            rl_top_bar_wrapper = (RelativeLayout) itemView.findViewById(R.id.rl_top_bar_wrapper);
            rl_writer_wrapper = (RelativeLayout) itemView.findViewById(R.id.rl_writer_wrapper);
            ll_target_click_wrapper = (LinearLayout) itemView.findViewById(R.id.ll_target_click_wrapper);
            iv_user_dp = (ImageView) itemView.findViewById(R.id.iv_user_dp);
            iv_cover_photo = (ImageView) itemView.findViewById(R.id.iv_cover_photo);
            cv_cover_wrapper = (CardView) itemView.findViewById(R.id.cv_cover_wrapper);
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
            // comment_item_gap = (View) itemView.findViewById(R.id.comment_item_gap);
            tv_activity_performed = (TextView) itemView.findViewById(R.id.tv_activity_performed);
            iv_more_options = (ImageView) itemView.findViewById(R.id.iv_more_options);
            if (isGuestUser) {
                iv_more_options.setVisibility(View.GONE);
            }
            iv_more_options.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popup.show();
                }
            });

            popup = new PopupMenu(activity, iv_more_options);
            inflater = popup.getMenuInflater();

            rl_user_comment_wrapper = (RelativeLayout) itemView.findViewById(R.id.rl_user_comment_wrapper);
            iv_comment_dp = (ImageView) itemView.findViewById(R.id.iv_comment_dp);
            tv_comment_user_name = (TextView) itemView.findViewById(R.id.tv_comment_user_name);
            tv_user_comment = (TextView) itemView.findViewById(R.id.tv_user_comment);

        }
    }


    public void configArticleViewHolder(ArticleViewHolder articleViewHolder, final int position,
                                        final PojoGetNewsFeedResponseData currentItem) {
        //  articleViewHolder.comment_item_gap.setVisibility(View.VISIBLE);
        articleViewHolder.rl_user_comment_wrapper.setVisibility(View.GONE);

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
                newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                        AppKeys.OPEN_PROFILE, currentItem.origin.createdBy.userId, "");
                /*Intent profileIntent = new Intent(activity, ProfileInfoActivity.class);
                profileIntent.putExtra("target_user_id", currentItem.origin.createdBy.userId);
                activity.startActivity(profileIntent);*/
            }
        });

        articleViewHolder.cv_post_wrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (currentItem.postType.equals("article_shared")) {
                    newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                            AppKeys.OPEN_POST, currentItem.postId, "");
                    /*Intent articleIntent = new Intent(activity, SharedArticleActivity.class);
                    articleIntent.putExtra(AppKeys.POST_ID, currentItem.postId);
                    activity.startActivity(articleIntent);*/
                } else {
                    /*Intent articleIntent = new Intent(activity, ArticleActivity.class);*/
                    if (currentItem.postType.equals("article")) {
                        newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                                AppKeys.OPEN_ARTICLE, currentItem.origin.articlesId, "");
                        /*articleIntent.putExtra("id", currentItem.origin.articlesId);*/
                    } else {
                        newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                                AppKeys.OPEN_ARTICLE, currentItem.originId, "");
                        /*articleIntent.putExtra("id", currentItem.originId);*/
                    }


                    // activity.startActivity(articleIntent);
                }
            }
        });

        if (!currentItem.origin.coverPhoto.isEmpty()) {
            int height = imageFixedHeight;
            int width = imageFixedWidth;

            if (currentItem.origin.imgDimensions != null && !currentItem.origin.imgDimensions.isEmpty()) {
                ImageDimensions dimen = commonFunctions.getScaledDimensions(displayMetrics, currentItem.origin.imgDimensions, AppKeys.ARTICLE);
                height = dimen.height;
                width = dimen.width;
                articleViewHolder.iv_cover_photo.getLayoutParams().height = height;
                articleViewHolder.iv_cover_photo.getLayoutParams().width = width;
            }

            if (activity != null)
                Glide.with(activity.getApplicationContext())
                        .load(currentItem.origin.coverPhoto)
                        .apply(new RequestOptions().override(width, height).placeholder(R.drawable.placeholder))
                        .into(articleViewHolder.iv_cover_photo);
        }

        String userToShow;
        if (userFixedname == null) {
            userToShow = currentItem.userFullname;
            if (currentItem.postType.equals("article_like") || currentItem.postType.equals("article_comment")) {
                userToShow = currentItem.liked_by.userFullname;
            }
        } else {
            userToShow = userFixedname;
        }

        userToShow = "<b>" + userToShow + "</b>";
        String likeOrLikes = activity.getString(R.string.likes_small);

        if (userFixedname == null) {
            int total = Integer.parseInt(currentItem.total);
            if (total > 0) {
                userToShow = userToShow + " " + activity.getString(R.string.and) + " " + currentItem.total + " " + activity.getString(R.string.others);
                likeOrLikes = activity.getString(R.string.like_small);
            }
        }

        articleViewHolder.rl_top_bar_wrapper.setVisibility(View.VISIBLE);
        if (currentItem.postType.equals("article_shared")) {
            articleViewHolder.tv_activity_performed.setText(Html.fromHtml(userToShow
                    + " " + activity.getString(R.string.shared) + " " + activity.getString(R.string.article)));

        } else if (currentItem.postType.equals("article_like")) {
            articleViewHolder.tv_activity_performed.setText(Html.fromHtml(userToShow
                    + " " + likeOrLikes + " " + activity.getString(R.string.article)));
        } else if (currentItem.postType.equals("article_comment")) {
            articleViewHolder.tv_activity_performed.setText(Html.fromHtml(userToShow
                    + " " + activity.getString(R.string.commented_on) + " " + activity.getString(R.string.article)));

        } else {
            articleViewHolder.rl_top_bar_wrapper.setVisibility(View.GONE);
        }


        articleViewHolder.tv_article_name.setText(Html.fromHtml(currentItem.origin.title));


        if (currentItem.postType.equals("article_shared")) {
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

            if (currentItem.iLike == 1) {
                articleViewHolder.tv_like.setTextColor(ContextCompat.getColor(activity, R.color.app_theme_medium));
                articleViewHolder.tv_like.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_like_filled,
                        0, 0, 0);
            } else {
                articleViewHolder.tv_like.setTextColor(ContextCompat.getColor(activity, R.color.med_grey));
                articleViewHolder.tv_like.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_like_unfilled,
                        0, 0, 0);
            }
        } else {
            if (currentItem.origin.likes.equals("0")) {
                articleViewHolder.tv_like_number.setVisibility(View.GONE);
            } else {
                articleViewHolder.tv_like_number.setVisibility(View.VISIBLE);
            }
            articleViewHolder.tv_like_number.setText("" + currentItem.origin.likes + " " + activity.getString(R.string.likes));

            if (currentItem.origin.totalComments == 0) {
                articleViewHolder.tv_comment_number.setVisibility(View.GONE);
            } else {
                articleViewHolder.tv_comment_number.setVisibility(View.VISIBLE);
            }
            articleViewHolder.tv_comment_number.setText("" + currentItem.origin.totalComments + " "
                    + activity.getString(R.string.comments));
            articleViewHolder.tv_share_number.setText("" + currentItem.origin.shares + " " + activity.getString(R.string.shares));

            if (currentItem.origin.iLike == 1) {
                articleViewHolder.tv_like.setTextColor(ContextCompat.getColor(activity, R.color.app_theme_medium));
                articleViewHolder.tv_like.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_like_filled,
                        0, 0, 0);
            } else {
                articleViewHolder.tv_like.setTextColor(ContextCompat.getColor(activity, R.color.med_grey));
                articleViewHolder.tv_like.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_like_unfilled,
                        0, 0, 0);
            }
        }

        articleViewHolder.tv_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isGuestUser) {
                    commonFunctions.showLoginRequestDialog();
                } else {
                    if (currentItem.postType.equals("article_shared")) {
                        newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                                AppKeys.POST_LIKE, "", "");
                    } else {
                        newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                                AppKeys.ARTICLE_LIKE, "", "");
                    }
                }
            }
        });

        articleViewHolder.tv_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isGuestUser) {
                    commonFunctions.showLoginRequestDialog();
                } else {
                    newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem, AppKeys.ARTICLE_SHARE, "", "");
                }
            }
        });

        articleViewHolder.tv_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isGuestUser) {
                    commonFunctions.showLoginRequestDialog();
                } else {
                    newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                            AppKeys.ARTICLE_COMMENT, currentItem.origin.articlesId, AppKeys.COMMENT);
                }

                /*Intent articleCommentIntent = new Intent(activity, ArticleActivity.class);
                articleCommentIntent.putExtra("id", currentItem.origin.articlesId);
                articleCommentIntent.putExtra(AppKeys.ACTION, AppKeys.COMMENT);
                activity.startActivity(articleCommentIntent);*/
            }
        });

        articleViewHolder.popup.getMenu().clear();
        if (user_id.equals(currentItem.origin.createdBy.userId)) {
            articleViewHolder.inflater.inflate(R.menu.news_feed_normal_article_creator_menu,
                    articleViewHolder.popup.getMenu());
        } else {
            articleViewHolder.inflater.inflate(R.menu.news_feed_normal_post_other_menu,
                    articleViewHolder.popup.getMenu());
            if (userFixedname == null) {
                String firstName = "";
                if (currentItem.postType.equals("article_like") || currentItem.postType.equals("article_comment")) {
                    firstName = currentItem.liked_by.userFullname;
                } else {
                    firstName = currentItem.postAuthorName;
                }
                if (firstName.contains(" ")) {
                    firstName = firstName.substring(0, firstName.indexOf(" "));
                }
                articleViewHolder.popup.getMenu().getItem(1)
                        .setTitle(activity.getString(R.string.unFollow) + " " + firstName);
            } else {
                articleViewHolder.popup.getMenu().removeItem(R.id.unfollow);
                articleViewHolder.popup.getMenu().removeItem(R.id.hide_post);
            }
        }

        try {
            Field[] fields = articleViewHolder.popup.getClass().getDeclaredFields();
            for (Field field : fields) {
                if ("mPopup".equals(field.getName())) {
                    field.setAccessible(true);
                    Object menuPopupHelper = field.get(articleViewHolder.popup);
                    Class<?> classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());
                    Method setForceIcons = classPopupHelper.getMethod("setForceShowIcon", boolean.class);
                    setForceIcons.invoke(menuPopupHelper, true);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        articleViewHolder.popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.edit:
                        newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                                AppKeys.EDIT_ARTICLE, "", "");
                        break;

                    case R.id.delete:
                        newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                                AppKeys.DELETE_ARTICLE, "", "");
                        break;

                    case R.id.share_to:
                        newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                                AppKeys.SHARE_TO_ARTICLE, "", "");
                        break;

                    case R.id.unfollow:
                        newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                                AppKeys.UNFOLLOW_USER, "", "");
                        break;

                    case R.id.report:
                        newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                                AppKeys.REPORT_ARTICLE, "", "");
                        break;
                    case R.id.hide_post:
                        newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                                AppKeys.HIDE, "", "");
                        break;
                }
                return false;
            }
        });

        if (currentItem.postType.equals("article_comment")) {
            if (currentItem.commentsArray != null && currentItem.commentsArray.size() > 0
                    && currentItem.commentsArray.get(0).post != null) {
                //   articleViewHolder.comment_item_gap.setVisibility(View.GONE);
                for (int i = 0; i < currentItem.commentsArray.size(); i++) {
                    final PojoNewsFeedCommentsArrayItem currentComment = currentItem.commentsArray.get(i);
                    if (currentItem.liked_by.userId.equals(currentComment.userId)) {
                        articleViewHolder.rl_user_comment_wrapper.setVisibility(View.VISIBLE);
                        if (activity != null)
                            Glide.with(activity.getApplicationContext()).load(currentComment.createdBy.userPicture)
                                    .apply(new RequestOptions().override(96, 96))
                                    .apply(bitmapTransform(new CropCircleTransformation()))
                                    .into(articleViewHolder.iv_comment_dp);

                        articleViewHolder.tv_comment_user_name.setText(currentComment.createdBy.userFullname);
                        articleViewHolder.tv_comment_user_name.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                                        AppKeys.OPEN_PROFILE, currentComment.createdBy.userId, AppKeys.COMMENT);

                                /*Intent profileIntent = new Intent(activity,
                                        ProfileInfoActivity.class);
                                profileIntent.putExtra("target_user_id", currentComment.createdBy.userId);
                                activity.startActivity(profileIntent);*/
                            }
                        });
                        articleViewHolder.tv_user_comment.setText(Html.fromHtml(currentComment.post));
                        articleViewHolder.rl_user_comment_wrapper.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                                        AppKeys.ARTICLE_COMMENT_LIST, currentItem.origin.articlesId,
                                        currentComment.commentId);

                                /*Intent intent = new Intent(activity, ArticleCommentListActivity.class);
                                intent.putExtra("id", currentItem.origin.articlesId);
                                intent.putExtra(AppKeys.COMMENT_ID, currentComment.commentId);
                                activity.startActivity(intent);*/
                            }
                        });

                        break;
                    }
                }

            }


        }

        if (hideHeader) {
            articleViewHolder.rl_top_bar_wrapper.setVisibility(View.GONE);
        }
        if (hideFooter) {
            articleViewHolder.rl_user_comment_wrapper.setVisibility(View.GONE);
        }
    }

}
