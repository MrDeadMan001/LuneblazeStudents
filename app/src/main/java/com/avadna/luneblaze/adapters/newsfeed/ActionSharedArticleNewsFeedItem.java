package com.avadna.luneblaze.adapters.newsfeed;

import android.content.Intent;
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
import com.avadna.luneblaze.activities.article.SharedArticleActivity;
import com.avadna.luneblaze.activities.profileInfo.ProfileInfoActivity;
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

public class ActionSharedArticleNewsFeedItem extends NewsFeedItemBase {

    public ActionSharedArticleNewsFeedItem(Fragment fragment, String userFixedname) {
        super(fragment, userFixedname);
    }

    public ActionSharedArticleViewHolder getViewHolder(ViewGroup parent) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.action_article_shared_item, parent, false);
        return new ActionSharedArticleViewHolder(itemView);
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
        ImageView iv_more_options;
        PopupMenu popup;
        MenuInflater inflater;

        ImageView iv_comment_dp;
        TextView tv_comment_user_name, tv_user_comment;
        RelativeLayout rl_user_comment_wrapper;
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

    public void configActionSharedArticleViewHolder(ActionSharedArticleViewHolder actionSharedArticleViewHolder,
                                                    final int position, final PojoGetNewsFeedResponseData currentItem) {

        //sharer data
        if (currentItem.postTitle.isEmpty()) {
            actionSharedArticleViewHolder.tv_share_title.setVisibility(View.GONE);
        } else {
            actionSharedArticleViewHolder.tv_share_title.setVisibility(View.VISIBLE);
            actionSharedArticleViewHolder.tv_share_title.setText(Html.fromHtml(currentItem.postTitle));
        }
        actionSharedArticleViewHolder.tv_post_sharer_stamp.setText(commonFunctions
                .parseNewsFeedItemTime(currentItem.updatedAt));
        actionSharedArticleViewHolder.tv_post_sharer_name.setText(currentItem.userFullname);
        actionSharedArticleViewHolder.tv_post_sharer_designation.setText(currentItem.userWorkTitle);
        actionSharedArticleViewHolder.tv_post_sharer_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                        AppKeys.OPEN_PROFILE, currentItem.authorId, "");


                /*Intent profileIntent = new Intent(activity, ProfileInfoActivity.class);
                profileIntent.putExtra("target_user_id", currentItem.authorId);
                activity.startActivity(profileIntent);*/
            }
        });

        if (!currentItem.postAuthorPicture.isEmpty()) {
            if (activity != null)
                Glide.with(activity.getApplicationContext()).load(currentItem.postAuthorPicture)
                        .apply(bitmapTransform(new CropCircleTransformation()))
                        .into(actionSharedArticleViewHolder.iv_post_sharer_user_dp);
        }
        actionSharedArticleViewHolder.iv_post_sharer_user_dp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actionSharedArticleViewHolder.tv_post_sharer_name.callOnClick();
            }
        });

//writer data
        actionSharedArticleViewHolder.tv_writer_name.setText(currentItem.origin.createdBy.userFullname);
        actionSharedArticleViewHolder.tv_writer_designation.setText(currentItem.origin.createdBy.userWorkTitle);
        actionSharedArticleViewHolder.tv_time_stamp.setText(commonFunctions
                .parseNewsFeedItemTime(currentItem.origin.addedOn));

        if (!currentItem.origin.createdBy.userPicture.isEmpty()) {
            if (activity != null)
                Glide.with(activity.getApplicationContext()).load(currentItem.origin.createdBy.userPicture)
                        .apply(new RequestOptions().override(96, 96))
                        .apply(bitmapTransform(new CropCircleTransformation()))
                        .into(actionSharedArticleViewHolder.iv_user_dp);
        }

        actionSharedArticleViewHolder.iv_user_dp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actionSharedArticleViewHolder.tv_writer_name.callOnClick();
            }
        });

        actionSharedArticleViewHolder.tv_writer_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                            AppKeys.OPEN_PROFILE, currentItem.authorId, "");


                /*Intent profileIntent = new Intent(activity, ProfileInfoActivity.class);
                profileIntent.putExtra("target_user_id", currentItem.authorId);
                activity.startActivity(profileIntent);*/
            }
        });

        actionSharedArticleViewHolder.cv_origin_wrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                            AppKeys.OPEN_POST, currentItem.postId, "");


                /*Intent articleIntent = new Intent(activity, SharedArticleActivity.class);
                articleIntent.putExtra(AppKeys.POST_ID, currentItem.postId);
                activity.startActivity(articleIntent);*/
            }
        });

        if (!currentItem.origin.coverPhoto.isEmpty()) {
            int height = imageFixedHeight;
            int width = imageFixedWidth;

            if (currentItem.origin.imgDimensions != null && !currentItem.origin.imgDimensions.isEmpty()) {
                ImageDimensions dimen = commonFunctions.getScaledDimensions(displayMetrics, currentItem.origin.imgDimensions, AppKeys.ARTICLE);
                height = dimen.height;
                width = dimen.width;
                actionSharedArticleViewHolder.iv_cover_photo.getLayoutParams().height = height;
                actionSharedArticleViewHolder.iv_cover_photo.getLayoutParams().width = width;
            }

           /* CardView.LayoutParams layoutParams=(CardView.LayoutParams)articleViewHolder.iv_cover_photo.getLayoutParams();
            layoutParams.height=height;
            layoutParams.width=width;
            articleViewHolder.iv_cover_photo.setLayoutParams(layoutParams);*/

            if (activity != null) Glide.with(activity.getApplicationContext())
                    .load(currentItem.origin.coverPhoto)
                    .apply(new RequestOptions().override(width, height).placeholder(R.drawable.placeholder))
                    .into(actionSharedArticleViewHolder.iv_cover_photo);

        }

        String userToShow = "";
        if (userFixedname == null) {
            userToShow = currentItem.liked_by.userFullname;
        } else {
            userToShow = userFixedname;
        }
        userToShow = "<b>" + userToShow + "</b>";

        String likeOrLikes = activity.getString(R.string.likes_small);

        if (userFixedname == null) {
            int total = Integer.parseInt(currentItem.total);
            if (total > 0) {
                userToShow = userToShow + " " + activity.getString(R.string.and) + " " + currentItem.total +
                        " " + activity.getString(R.string.others);
                likeOrLikes = activity.getString(R.string.like_small);
            }
        }


        if (currentItem.postType.equals("post_like")) {
            actionSharedArticleViewHolder.tv_activity_performed.setText(Html.fromHtml(userToShow
                    + " " + likeOrLikes));
        } else if (currentItem.postType.equals("post_comment")) {
            actionSharedArticleViewHolder.tv_activity_performed.setText(Html.fromHtml(userToShow
                    + " " + activity.getString(R.string.commented_on)));
        } else {
            actionSharedArticleViewHolder.rl_top_bar_wrapper.setVisibility(View.GONE);
        }

        actionSharedArticleViewHolder.tv_article_name.setText(Html.fromHtml(currentItem.origin.title));


        if (currentItem.totalLikes == 0) {
            actionSharedArticleViewHolder.tv_like_number.setVisibility(View.GONE);
        } else {
            actionSharedArticleViewHolder.tv_like_number.setVisibility(View.VISIBLE);
        }
        actionSharedArticleViewHolder.tv_like_number.setText("" + currentItem.totalLikes + " " + activity.getString(R.string.likes));

        if (currentItem.comments.equals("0")) {
            actionSharedArticleViewHolder.tv_comment_number.setVisibility(View.GONE);
        } else {
            actionSharedArticleViewHolder.tv_comment_number.setVisibility(View.VISIBLE);
        }
        actionSharedArticleViewHolder.tv_comment_number.setText("" + currentItem.comments + " "
                + activity.getString(R.string.comments));
        actionSharedArticleViewHolder.tv_share_number.setText("" + currentItem.shares + " " + activity.getString(R.string.shares));


        if (currentItem.iLike == 1) {
            actionSharedArticleViewHolder.tv_like_button.setTextColor(ContextCompat.getColor(activity, R.color.app_theme_medium));
            actionSharedArticleViewHolder.tv_like_button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_like_filled, 0, 0, 0);
        } else {
            actionSharedArticleViewHolder.tv_like_button.setTextColor(ContextCompat.getColor(activity, R.color.med_grey));
            actionSharedArticleViewHolder.tv_like_button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_like_unfilled, 0, 0, 0);
        }

        actionSharedArticleViewHolder.tv_like_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isGuestUser) {
                    commonFunctions.showLoginRequestDialog();
                } else {
                    newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                            AppKeys.POST_LIKE, "", "");
                }

            }
        });

        actionSharedArticleViewHolder.tv_share_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isGuestUser) {
                    commonFunctions.showLoginRequestDialog();
                } else {
                    newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                            AppKeys.ORIGIN_ARTICLE_SHARE, "", "");
                }
            }
        });

        actionSharedArticleViewHolder.tv_comment_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isGuestUser) {
                    commonFunctions.showLoginRequestDialog();
                } else {
                    newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                            AppKeys.ARTICLE_COMMENT, currentItem.postId, AppKeys.COMMENT);
                }

                /*Intent articleCommentIntent = new Intent(activity, SharedArticleActivity.class);
                articleCommentIntent.putExtra(AppKeys.POST_ID, currentItem.postId);
                articleCommentIntent.putExtra(AppKeys.ACTION, AppKeys.COMMENT);
                activity.startActivity(articleCommentIntent);*/
            }
        });


        actionSharedArticleViewHolder.popup.getMenu().clear();

        if (user_id.equals(currentItem.authorId)) {
            actionSharedArticleViewHolder.inflater.inflate(R.menu.news_feed_normal_post_creator_menu,
                    actionSharedArticleViewHolder.popup.getMenu());
        } else {
            actionSharedArticleViewHolder.inflater.inflate(R.menu.news_feed_normal_post_other_menu,
                    actionSharedArticleViewHolder.popup.getMenu());
            if (userFixedname == null) {
                String firstName = currentItem.postAuthorName;

                if (currentItem.postType.equals("post_like") || currentItem.postType.equals("post_comment")) {
                    firstName = currentItem.liked_by.userFullname;

                } else {
                    firstName = currentItem.postAuthorName;

                }
                if (currentItem.postAuthorName.contains(" ")) {
                    firstName = firstName.substring(0, firstName.indexOf(" "));
                }
                actionSharedArticleViewHolder.popup.getMenu().getItem(1)
                        .setTitle(activity.getString(R.string.unFollow) + " " + firstName);
            } else {
                actionSharedArticleViewHolder.popup.getMenu().removeItem(R.id.unfollow);
                actionSharedArticleViewHolder.popup.getMenu().removeItem(R.id.hide_post);
            }

        }

        try {
            Field[] fields = actionSharedArticleViewHolder.popup.getClass().getDeclaredFields();
            for (Field field : fields) {
                if ("mPopup".equals(field.getName())) {
                    field.setAccessible(true);
                    Object menuPopupHelper = field.get(actionSharedArticleViewHolder.popup);
                    Class<?> classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());
                    Method setForceIcons = classPopupHelper.getMethod("setForceShowIcon", boolean.class);
                    setForceIcons.invoke(menuPopupHelper, true);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        actionSharedArticleViewHolder.popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.edit:
                        newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                                AppKeys.EDIT_POST_TITLE, "", "");
                        break;

                    case R.id.delete:
                        newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                                AppKeys.POST_DELETE, "", "");
                        break;

                    case R.id.share_to:
                        newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                                AppKeys.SHARE_TO_POST, "", "");
                        break;
                    case R.id.report:
                        commonFunctions.openReportDialog(user_id, "report_post", currentItem.origin.postId);

                        /*newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                                AppKeys.REPORT_ARTICLE);*/
                        break;
                    case R.id.hide_post:
                        newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                                AppKeys.HIDE, "", "");
                        break;
                }
                return false;
            }
        });

        if (currentItem.commentsArray != null && currentItem.commentsArray.size() > 0) {
            actionSharedArticleViewHolder.rl_user_comment_wrapper.setVisibility(View.VISIBLE);

            for (int i = 0; i < currentItem.commentsArray.size(); i++) {
                final PojoNewsFeedCommentsArrayItem currentComment = currentItem.commentsArray.get(i);
                if (currentItem.liked_by.userId.equals(currentComment.userId)) {
                    if (activity != null)
                        Glide.with(activity.getApplicationContext()).load(currentComment.createdBy.userPicture)
                                .apply(new RequestOptions().override(96, 96))
                                .apply(bitmapTransform(new CropCircleTransformation()))
                                .into(actionSharedArticleViewHolder.iv_comment_dp);

                    actionSharedArticleViewHolder.tv_comment_user_name.setText(currentComment.createdBy.userFullname);
                    actionSharedArticleViewHolder.tv_comment_user_name.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                                newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                                        AppKeys.OPEN_PROFILE, currentComment.createdBy.userId, "");

                            /*Intent profileIntent = new Intent(activity,
                                    ProfileInfoActivity.class);
                            profileIntent.putExtra("target_user_id", currentComment.createdBy.userId);
                            activity.startActivity(profileIntent);*/
                        }
                    });

                    actionSharedArticleViewHolder.rl_user_comment_wrapper.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                                newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                                        AppKeys.OPEN_POST_COMMENT, currentItem.postId,
                                        currentItem.commentsArray.get(0).commentId);


                           /* Intent normalPostIntent = new Intent(activity, SharedArticleActivity.class);
                            normalPostIntent.putExtra(AppKeys.POST_ID, currentItem.postId);
                            normalPostIntent.putExtra(AppKeys.COMMENT_ID, currentItem.commentsArray.get(0).commentId);
                            activity.startActivity(normalPostIntent);*/
                        }
                    });

                    actionSharedArticleViewHolder.tv_user_comment.setText(Html.fromHtml(currentComment.text));
                    break;
                } else {
                    actionSharedArticleViewHolder.rl_user_comment_wrapper.setVisibility(View.GONE);
                }
            }
        } else {
            actionSharedArticleViewHolder.rl_user_comment_wrapper.setVisibility(View.GONE);
        }

        if (currentItem.postType.equals("post_like") || currentItem.postType.equals("post_comment")) {
            //case added because of incorrect data
            if (userFixedname != null) {
                if (!currentItem.liked_by.userFullname.equals(userFixedname)) {
                    actionSharedArticleViewHolder.rl_top_bar_wrapper.setVisibility(View.GONE);
                    actionSharedArticleViewHolder.rl_user_comment_wrapper.setVisibility(View.GONE);
                } else {
                    actionSharedArticleViewHolder.rl_top_bar_wrapper.setVisibility(View.VISIBLE);
                }
            }
        }

        if (hideHeader) {
            actionSharedArticleViewHolder.rl_top_bar_wrapper.setVisibility(View.GONE);
        }
        if (hideFooter) {
            actionSharedArticleViewHolder.rl_user_comment_wrapper.setVisibility(View.GONE);

        }
    }


    public void configSharedArticleViewHolder(ActionSharedArticleViewHolder actionSharedArticleViewHolder,
                                              final int position, final PojoGetNewsFeedResponseData currentItem) {
        actionSharedArticleViewHolder.rl_user_comment_wrapper.setVisibility(View.GONE);
        actionSharedArticleViewHolder.rl_top_bar_wrapper.setVisibility(View.GONE);

        //sharer data
        if (currentItem.postTitle.isEmpty()) {
            actionSharedArticleViewHolder.tv_share_title.setVisibility(View.GONE);
        } else {
            actionSharedArticleViewHolder.tv_share_title.setVisibility(View.VISIBLE);
            actionSharedArticleViewHolder.tv_share_title.setText(Html.fromHtml(currentItem.postTitle));
        }
        actionSharedArticleViewHolder.tv_post_sharer_stamp.setText(commonFunctions
                .parseNewsFeedItemTime(currentItem.updatedAt));
        actionSharedArticleViewHolder.tv_post_sharer_name.setText(currentItem.userFullname);
        actionSharedArticleViewHolder.tv_post_sharer_designation.setText(currentItem.userWorkTitle);
        actionSharedArticleViewHolder.tv_post_sharer_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                            AppKeys.OPEN_PROFILE, currentItem.authorId, "");

                /*Intent profileIntent = new Intent(activity, ProfileInfoActivity.class);
                profileIntent.putExtra("target_user_id", currentItem.authorId);
                activity.startActivity(profileIntent);*/
            }
        });

        if (!currentItem.postAuthorPicture.isEmpty()) {
            if (activity != null)
                Glide.with(activity.getApplicationContext()).load(currentItem.postAuthorPicture)
                        .apply(bitmapTransform(new CropCircleTransformation()))
                        .into(actionSharedArticleViewHolder.iv_post_sharer_user_dp);
        }
        actionSharedArticleViewHolder.iv_post_sharer_user_dp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actionSharedArticleViewHolder.tv_post_sharer_name.callOnClick();
            }
        });


        //article writer data
        actionSharedArticleViewHolder.tv_writer_name.setText(currentItem.origin.createdBy.userFullname);
        actionSharedArticleViewHolder.tv_writer_designation.setText(currentItem.origin.createdBy.userWorkTitle);
        actionSharedArticleViewHolder.tv_time_stamp.setText(commonFunctions
                .parseNewsFeedItemTime(currentItem.origin.addedOn));

        if (!currentItem.origin.createdBy.userPicture.isEmpty()) {
            if (activity != null)
                Glide.with(activity.getApplicationContext()).load(currentItem.origin.createdBy.userPicture)
                        .apply(new RequestOptions().override(96, 96))
                        .apply(bitmapTransform(new CropCircleTransformation()))
                        .into(actionSharedArticleViewHolder.iv_user_dp);
        }

        actionSharedArticleViewHolder.iv_user_dp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actionSharedArticleViewHolder.tv_writer_name.callOnClick();
            }
        });

        actionSharedArticleViewHolder.tv_writer_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                            AppKeys.OPEN_PROFILE, currentItem.origin.createdBy.userId, "");


                /*Intent profileIntent = new Intent(activity, ProfileInfoActivity.class);
                profileIntent.putExtra("target_user_id", currentItem.origin.createdBy.userId);
                activity.startActivity(profileIntent);*/
            }
        });

        actionSharedArticleViewHolder.cv_origin_wrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                            AppKeys.OPEN_POST, currentItem.postId, "");

                /*Intent articleIntent = new Intent(activity, SharedArticleActivity.class);
                articleIntent.putExtra(AppKeys.POST_ID, currentItem.postId);
                activity.startActivity(articleIntent);*/
            }
        });


        if (!currentItem.origin.coverPhoto.isEmpty()) {
            int height = imageFixedHeight;
            int width = imageFixedWidth;

            if (currentItem.origin.imgDimensions != null && !currentItem.origin.imgDimensions.isEmpty()) {
                ImageDimensions dimen = commonFunctions.getScaledDimensions(displayMetrics, currentItem.origin.imgDimensions, AppKeys.ARTICLE);
                height = dimen.height;
                width = dimen.width;
                actionSharedArticleViewHolder.iv_cover_photo.getLayoutParams().height = height;
                actionSharedArticleViewHolder.iv_cover_photo.getLayoutParams().width = width;
            }

           /* CardView.LayoutParams layoutParams=(CardView.LayoutParams)articleViewHolder.iv_cover_photo.getLayoutParams();
            layoutParams.height=height;
            layoutParams.width=width;
            articleViewHolder.iv_cover_photo.setLayoutParams(layoutParams);*/

            if (activity != null) Glide.with(activity.getApplicationContext())
                    .load(currentItem.origin.coverPhoto)
                    .apply(new RequestOptions().override(width, height).placeholder(R.drawable.placeholder))
                    .into(actionSharedArticleViewHolder.iv_cover_photo);

        }

        String userToShow = "";
        if (userFixedname == null) {
            userToShow = currentItem.userFullname;
        } else {
            userToShow = userFixedname;
        }
        userToShow = "<b>" + userToShow + "</b>";


        if (userFixedname == null) {
            int total = Integer.parseInt(currentItem.total);
            if (total > 0) {
                userToShow = userToShow + " " + activity.getString(R.string.and) + " " + currentItem.total +
                        " " + activity.getString(R.string.others);
            }
        }

        actionSharedArticleViewHolder.tv_activity_performed.setText(Html.fromHtml(userToShow
                + " " + activity.getString(R.string.shared)));

        actionSharedArticleViewHolder.tv_article_name.setText(Html.fromHtml(currentItem.origin.title));

        if (currentItem.totalLikes == 0) {
            actionSharedArticleViewHolder.tv_like_number.setVisibility(View.GONE);
        } else {
            actionSharedArticleViewHolder.tv_like_number.setVisibility(View.VISIBLE);
        }
        actionSharedArticleViewHolder.tv_like_number.setText("" + currentItem.totalLikes + " " + activity.getString(R.string.likes));

        if (currentItem.comments.equals("0")) {
            actionSharedArticleViewHolder.tv_comment_number.setVisibility(View.GONE);
        } else {
            actionSharedArticleViewHolder.tv_comment_number.setVisibility(View.VISIBLE);
        }
        actionSharedArticleViewHolder.tv_comment_number.setText("" + currentItem.comments + " "
                + activity.getString(R.string.comments));

        actionSharedArticleViewHolder.tv_share_number.setText("" + currentItem.shares + " " + activity.getString(R.string.shares));


        if (currentItem.iLike == 1) {
            actionSharedArticleViewHolder.tv_like_button.setTextColor(ContextCompat.getColor(activity, R.color.app_theme_medium));
            actionSharedArticleViewHolder.tv_like_button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_like_filled, 0, 0, 0);
        } else {
            actionSharedArticleViewHolder.tv_like_button.setTextColor(ContextCompat.getColor(activity, R.color.med_grey));
            actionSharedArticleViewHolder.tv_like_button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_like_unfilled, 0, 0, 0);
        }

        actionSharedArticleViewHolder.tv_like_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isGuestUser) {
                    commonFunctions.showLoginRequestDialog();
                } else {
                    newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                            AppKeys.POST_LIKE, "", "");
                }
            }
        });

        actionSharedArticleViewHolder.tv_share_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isGuestUser) {
                    commonFunctions.showLoginRequestDialog();
                } else {
                    newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                            AppKeys.ARTICLE_SHARE, "", "");
                }
            }
        });

        actionSharedArticleViewHolder.tv_comment_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isGuestUser) {
                    commonFunctions.showLoginRequestDialog();
                } else {
                    newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                            AppKeys.ADD_POST_COMMENT, currentItem.postId, AppKeys.COMMENT);
                }

                /*Intent articleCommentIntent = new Intent(activity, SharedArticleActivity.class);
                articleCommentIntent.putExtra(AppKeys.POST_ID, currentItem.postId);
                articleCommentIntent.putExtra(AppKeys.ACTION, AppKeys.COMMENT);
                activity.startActivity(articleCommentIntent);*/
            }
        });


        actionSharedArticleViewHolder.popup.getMenu().clear();

        if (user_id.equals(currentItem.authorId)) {
            actionSharedArticleViewHolder.inflater.inflate(R.menu.news_feed_normal_post_creator_menu,
                    actionSharedArticleViewHolder.popup.getMenu());
        } else {
            actionSharedArticleViewHolder.inflater.inflate(R.menu.news_feed_normal_post_other_menu,
                    actionSharedArticleViewHolder.popup.getMenu());
            if (userFixedname == null) {
                String firstName = currentItem.postAuthorName;
                if (currentItem.postAuthorName.contains(" ")) {
                    firstName = firstName.substring(0, firstName.indexOf(" "));
                }
                actionSharedArticleViewHolder.popup.getMenu().getItem(1)
                        .setTitle(activity.getString(R.string.unFollow) + " " + firstName);
            } else {
                actionSharedArticleViewHolder.popup.getMenu().removeItem(R.id.unfollow);
                actionSharedArticleViewHolder.popup.getMenu().removeItem(R.id.hide_post);
            }
        }

        try {
            Field[] fields = actionSharedArticleViewHolder.popup.getClass().getDeclaredFields();
            for (Field field : fields) {
                if ("mPopup".equals(field.getName())) {
                    field.setAccessible(true);
                    Object menuPopupHelper = field.get(actionSharedArticleViewHolder.popup);
                    Class<?> classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());
                    Method setForceIcons = classPopupHelper.getMethod("setForceShowIcon", boolean.class);
                    setForceIcons.invoke(menuPopupHelper, true);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        actionSharedArticleViewHolder.popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                  /*  case R.id.pin:
                        newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                                AppKeys.PIN_ARTICLE);
                        break;
                    case R.id.save:
                        newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                                AppKeys.SAVE_ARTICLE);
                        break;*/

                    case R.id.edit:
                        newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                                AppKeys.EDIT_POST_TITLE, "", "");
                        break;

                    case R.id.delete:
                        newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                                AppKeys.POST_DELETE, "", "");
                        break;

                    case R.id.share_to:
                        newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                                AppKeys.SHARE_TO_POST, "", "");
                        break;

                    case R.id.report:
                        commonFunctions.openReportDialog(user_id, "report_post", currentItem.postId);
                      /*  newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                                AppKeys.REPORT_ARTICLE);*/
                        break;
                    case R.id.hide_post:
                        newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                                AppKeys.HIDE, "", "");
                        break;
                }
                return false;
            }
        });


        if (hideHeader) {
            actionSharedArticleViewHolder.rl_top_bar_wrapper.setVisibility(View.GONE);
        }
        if (hideFooter) {
            actionSharedArticleViewHolder.rl_user_comment_wrapper.setVisibility(View.GONE);

        }

    }
}
