package com.avadna.luneblaze.adapters.newsfeed;

import android.graphics.Color;
import android.graphics.Paint;
import android.text.Html;
import android.util.Log;
import android.util.Patterns;
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
import com.avadna.luneblaze.pojo.pojoNewsFeed.PojoNewsFeedPhoto;
import com.avadna.luneblaze.utils.ProportionalImageView;
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

public class ActionSharedNormalPostNewsFeedItem extends NewsFeedItemBase {
    public ActionSharedNormalPostNewsFeedItem(Fragment fragment, String userFixedname) {
        super(fragment, userFixedname);
    }

    public ActionSharedNormalPostViewHolder getViewHolder(ViewGroup parent) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.action_normal_post_shared_item, parent, false);
        return new ActionSharedNormalPostViewHolder(itemView);
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
        PopupMenu popup;

        LinearLayout ll_two_images;
        LinearLayout ll_three_images;
        LinearLayout ll_content_wrapper;

        RelativeLayout rl_user_comment_wrapper;
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

    public void configActionSharedNormalPostViewHolder(ActionSharedNormalPostViewHolder actionSharedNormalPostViewHolder,
                                                       final int position, final PojoGetNewsFeedResponseData currentItem) {
        actionSharedNormalPostViewHolder.rl_user_comment_wrapper.setVisibility(View.GONE);
        actionSharedNormalPostViewHolder.rl_top_bar_wrapper.setVisibility(View.VISIBLE);
        actionSharedNormalPostViewHolder.rl_web_preview_wrapper.setVisibility(View.GONE);

        List<PojoNewsFeedPhoto> photos = currentItem.origin.photos;

        //sharer data
        actionSharedNormalPostViewHolder.tv_post_sharer_stamp.setText(commonFunctions
                .parseNewsFeedItemTime(currentItem.updatedAt));

        if (currentItem.postTitle.isEmpty()) {
            actionSharedNormalPostViewHolder.tv_share_title.setVisibility(View.GONE);
        } else {
            actionSharedNormalPostViewHolder.tv_share_title.setVisibility(View.VISIBLE);
            actionSharedNormalPostViewHolder.tv_share_title.setText(Html.fromHtml(currentItem.postTitle));
        }
        actionSharedNormalPostViewHolder.tv_post_sharer_name.setText(currentItem.userFullname);
        actionSharedNormalPostViewHolder.tv_post_sharer_designation.setText(currentItem.userWorkTitle);
        actionSharedNormalPostViewHolder.tv_post_sharer_name.setOnClickListener(new View.OnClickListener() {
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
                        .into(actionSharedNormalPostViewHolder.iv_post_sharer_user_dp);
        }
        actionSharedNormalPostViewHolder.iv_post_sharer_user_dp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actionSharedNormalPostViewHolder.tv_post_sharer_name.callOnClick();
            }
        });

        //creator data

        if (currentItem.origin.organizationData != null
                && (currentItem.origin.postType.equals("organisation_post") || currentItem.origin.postType.equals("ad"))) {
            if (!currentItem.origin.organizationData.logo.isEmpty()) {
                if (activity != null)
                    Glide.with(activity.getApplicationContext()).load(currentItem.origin.organizationData.logo)
                            .apply(bitmapTransform(new CropCircleTransformation()))
                            .into(actionSharedNormalPostViewHolder.iv_user_dp);
            }
            actionSharedNormalPostViewHolder.tv_poster_name.setText(currentItem.origin.organizationData.name);
            actionSharedNormalPostViewHolder.tv_poster_designation.setText(currentItem.origin.organizationData.typename);
            actionSharedNormalPostViewHolder.tv_poster_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                            AppKeys.OPEN_ORGANISATION, currentItem.origin.organizationData.id, "");

                    /*Intent organisationIntent = new Intent(activity, OrganisationProfileInfoActivity.class);
                    organisationIntent.putExtra(AppKeys.ORGANISATION_ID, currentItem.origin.organizationData.id);
                    activity.startActivity(organisationIntent);*/
                }
            });

        } else {
            actionSharedNormalPostViewHolder.tv_poster_name.setText(currentItem.origin.userFullname);
            actionSharedNormalPostViewHolder.tv_poster_designation.setText(currentItem.origin.userWorkTitle);
            actionSharedNormalPostViewHolder.tv_poster_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                            AppKeys.OPEN_PROFILE, currentItem.origin.authorId, "");

                    /*Intent profileIntent = new Intent(activity, ProfileInfoActivity.class);
                    profileIntent.putExtra("target_user_id", currentItem.origin.authorId);
                    activity.startActivity(profileIntent);*/
                }
            });

            if (currentItem.origin.postAuthorPicture != null && !currentItem.origin.postAuthorPicture.isEmpty()) {
                if (activity != null)
                    Glide.with(activity.getApplicationContext()).load(currentItem.origin.postAuthorPicture)
                            .apply(bitmapTransform(new CropCircleTransformation()))
                            .into(actionSharedNormalPostViewHolder.iv_user_dp);
            }
        }

        actionSharedNormalPostViewHolder.tv_time_stamp.setText(commonFunctions
                .parseNewsFeedItemTime(currentItem.origin.time));
        actionSharedNormalPostViewHolder.iv_user_dp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actionSharedNormalPostViewHolder.tv_poster_name.callOnClick();
            }
        });

        if (currentItem.postType.equals("post_like") || currentItem.postType.equals("post_comment")) {
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
                actionSharedNormalPostViewHolder.tv_activity_performed.setText(Html.fromHtml(userToShow + " "
                        + likeOrLikes));
            } else if (currentItem.postType.equals("post_comment")) {
                actionSharedNormalPostViewHolder.tv_activity_performed.setText(Html.fromHtml(userToShow + " "
                        + activity.getString(R.string.commented_on)));
            }
        }

        boolean tagged;
        if (currentItem.origin.tagged.equals("1")) {
            tagged = true;
        } else {
            tagged = false;
        }

        actionSharedNormalPostViewHolder.tv_post_content.setHighlightColor(
                Color.TRANSPARENT); // prevent TextView change background when highlight
        //  actionSharedNormalPostViewHolder.tv_post_content.setMovementMethod(LinkMovementMethod.getInstance());
        currentItem.spannableClickItem = commonFunctions.setClickSpans(currentItem.origin.text,
                currentItem.origin.textPlain, tagged, null);
        actionSharedNormalPostViewHolder.tv_post_content
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
                actionSharedNormalPostViewHolder.rl_web_preview_wrapper.setVisibility(View.GONE);
                String title = currentItem.sourceContent.getTitle();
                String name = currentItem.sourceContent.getCannonicalUrl();
                String description = currentItem.sourceContent.getCannonicalUrl();
                if (title.isEmpty() || name.isEmpty()) {
                    actionSharedNormalPostViewHolder.rl_web_preview_wrapper.setVisibility(View.GONE);
                } else {
                    actionSharedNormalPostViewHolder.rl_web_preview_wrapper.setVisibility(View.VISIBLE);
                    actionSharedNormalPostViewHolder.tv_link_title.setText(currentItem.sourceContent.getTitle());
                    actionSharedNormalPostViewHolder.tv_website_name.setText(currentItem.sourceContent.getCannonicalUrl());
                    actionSharedNormalPostViewHolder.tv_link_description.setText(currentItem.sourceContent.getDescription());
                    for (int i = 0; i < currentItem.sourceContent.getImages().size(); i++) {
                        if (currentItem.sourceContent.getImages().get(i).contains("png")
                                || currentItem.sourceContent.getImages().get(i).contains("jpg")) {
                            if (activity != null)
                                Glide.with(activity.getApplicationContext())
                                        .load(currentItem.sourceContent.getImages().get(i))
                                        .into(actionSharedNormalPostViewHolder.iv_link_image);
                            break;
                        }
                    }

                }
            } else {
                actionSharedNormalPostViewHolder.textCrawler = new TextCrawler();
                LinkPreviewCallback linkPreviewCallback = new LinkPreviewCallback() {
                    @Override
                    public void onPre() {
                        // Any work that needs to be done before generating the preview. Usually inflate
                        // your custom preview layout here.
                        //commonFunctions.setToastMessage(activity,"link preview called",Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);
                        actionSharedNormalPostViewHolder.rl_web_preview_wrapper.setVisibility(View.GONE);
                    }

                    @Override
                    public void onPos(SourceContent sourceContent, boolean b) {
                        currentItem.sourceContent = sourceContent;
                        // Populate your preview layout with the results of sourceContent.
                        String title = sourceContent.getTitle();
                        String name = sourceContent.getCannonicalUrl();
                        String description = sourceContent.getCannonicalUrl();

                        if (title.isEmpty() || name.isEmpty()) {
                            actionSharedNormalPostViewHolder.rl_web_preview_wrapper.setVisibility(View.GONE);
                        } else {
                            actionSharedNormalPostViewHolder.rl_web_preview_wrapper.setVisibility(View.VISIBLE);
                            actionSharedNormalPostViewHolder.tv_link_title.setText(sourceContent.getTitle());
                            actionSharedNormalPostViewHolder.tv_website_name.setText(sourceContent.getCannonicalUrl());
                            actionSharedNormalPostViewHolder.tv_link_description.setText(sourceContent.getDescription());
                            for (int i = 0; i < sourceContent.getImages().size(); i++) {
                                if (sourceContent.getImages().get(i).contains("png")
                                        || sourceContent.getImages().get(i).contains("jpg")) {
                                    Glide.with(activity)
                                            .load(sourceContent.getImages().get(i))
                                            .into(actionSharedNormalPostViewHolder.iv_link_image);
                                    break;
                                }
                            }

                        }
                    }
                };
                actionSharedNormalPostViewHolder.textCrawler.makePreview(linkPreviewCallback, url);
            }


        }

        Paint paint = new Paint();
        paint.setTextSize(actionSharedNormalPostViewHolder.tv_post_content.getTextSize());
        paint.setTypeface(actionSharedNormalPostViewHolder.tv_post_content.getTypeface());

        float textViewWidthPx = displayMetrics.widthPixels * 0.85f;
        int lineCount = commonFunctions.splitWordsIntoStringsThatFit(currentItem.spannableClickItem.spannableString.toString()
                , textViewWidthPx, paint);
        currentItem.canTextFit = true;

        if (lineCount > 8) {
            currentItem.canTextFit = false;
        } else {
            currentItem.canTextFit = true;
            currentItem.expanded = true;
        }

        if (currentItem.expanded) {
            actionSharedNormalPostViewHolder.tv_post_content.setMaxLines(10000);
            actionSharedNormalPostViewHolder.tv_post_content
                    .setText(currentItem.spannableClickItem.spannableString, TextView.BufferType.SPANNABLE);
        } else {
            actionSharedNormalPostViewHolder.tv_post_content.setMaxLines(8);
            if (!currentItem.canTextFit) {
                actionSharedNormalPostViewHolder.tv_view_more.setVisibility(View.VISIBLE);
                actionSharedNormalPostViewHolder.tv_view_more.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        actionSharedNormalPostViewHolder.tv_view_more.setVisibility(View.GONE);
                        actionSharedNormalPostViewHolder.tv_post_content.setMaxLines(10000);
                        currentItem.expanded = true;
                        actionSharedNormalPostViewHolder.tv_post_content
                                .setText(currentItem.spannableClickItem.spannableString, TextView.BufferType.SPANNABLE);
                        //normalPostViewHolder.tv_post_content.setLinksClickable(false);
                    }
                });
            }
        }


        if (currentItem.totalLikes == 0) {
            actionSharedNormalPostViewHolder.tv_like_number.setVisibility(View.GONE);
        } else {
            actionSharedNormalPostViewHolder.tv_like_number.setVisibility(View.VISIBLE);
        }
        actionSharedNormalPostViewHolder.tv_like_number.setText("" + currentItem.totalLikes + " "
                + activity.getString(R.string.likes));

        if (currentItem.comments.equals("0")) {
            actionSharedNormalPostViewHolder.tv_comment_number.setVisibility(View.GONE);
        } else {
            actionSharedNormalPostViewHolder.tv_comment_number.setVisibility(View.VISIBLE);
        }
        actionSharedNormalPostViewHolder.tv_comment_number.setText("" + currentItem.comments + " "
                + activity.getString(R.string.comments));
      /*  actionSharedNormalPostViewHolder.tv_like_number.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openUserListDialog(currentItem.origin.likedUsers);
            }
        });*/

        if (currentItem.iLike == 1) {
            actionSharedNormalPostViewHolder.tv_like_button.setTextColor(ContextCompat.getColor(activity, R.color.app_theme_medium));
            actionSharedNormalPostViewHolder.tv_like_button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_like_filled,
                    0, 0, 0);
        } else {
            actionSharedNormalPostViewHolder.tv_like_button.setTextColor(ContextCompat.getColor(activity, R.color.med_grey));
            actionSharedNormalPostViewHolder.tv_like_button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_like_unfilled,
                    0, 0, 0);
        }

        if (currentItem.postType.equals("post_comment")) {
            if (currentItem.commentsArray != null && currentItem.commentsArray.size() > 0) {
                if (currentItem.commentsArray.get(0).createdBy.userFullname.equals(userFixedname)) {
                    actionSharedNormalPostViewHolder.rl_user_comment_wrapper.setVisibility(View.VISIBLE);
                } else {
                    actionSharedNormalPostViewHolder.rl_user_comment_wrapper.setVisibility(View.GONE);
                }

                if (activity != null)
                    Glide.with(activity.getApplicationContext()).load(currentItem.commentsArray.get(0).createdBy.userPicture)
                            .apply(new RequestOptions().override(96, 96))
                            .apply(bitmapTransform(new CropCircleTransformation()))
                            .into(actionSharedNormalPostViewHolder.iv_comment_dp);

                actionSharedNormalPostViewHolder.tv_comment_user_name.setText(currentItem
                        .commentsArray.get(0).createdBy.userFullname);
                actionSharedNormalPostViewHolder.tv_comment_user_name.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                                AppKeys.OPEN_PROFILE, currentItem.commentsArray
                                        .get(0).userId, "");

                        /*Intent profileIntent = new Intent(activity, ProfileInfoActivity.class);
                        profileIntent.putExtra("target_user_id", currentItem.commentsArray
                                .get(0).userId);
                        activity.startActivity(profileIntent);*/
                    }
                });

                actionSharedNormalPostViewHolder.rl_user_comment_wrapper.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                                AppKeys.OPEN_POST_COMMENT, currentItem.postId,
                                currentItem.commentsArray.get(0).commentId);

                        /*Intent normalPostIntent = new Intent(activity, SharedNormalPostActivity.class);
                        normalPostIntent.putExtra(AppKeys.POST_ID, currentItem.postId);
                        normalPostIntent.putExtra(AppKeys.COMMENT_ID, currentItem.commentsArray.get(0).commentId);
                        activity.startActivity(normalPostIntent);*/
                    }
                });
                actionSharedNormalPostViewHolder.tv_user_comment.setText(Html.fromHtml(currentItem.commentsArray.get(0).text));
            }
        }


        actionSharedNormalPostViewHolder.tv_like_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isGuestUser) {
                    commonFunctions.showLoginRequestDialog();
                } else {
                    newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem, AppKeys.POST_LIKE, "", "");
                }
            }
        });

        actionSharedNormalPostViewHolder.tv_share_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isGuestUser) {
                    commonFunctions.showLoginRequestDialog();
                } else {
                    newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem, AppKeys.POST_SHARE, "", "");
                }
            }
        });


        if (photos == null || photos.size() == 0) {
            actionSharedNormalPostViewHolder.iv_cover_photo1.setVisibility(View.GONE);
            actionSharedNormalPostViewHolder.ll_two_images.setVisibility(View.GONE);
            actionSharedNormalPostViewHolder.ll_three_images.setVisibility(View.GONE);
        } else {
            if (photos.size() != 2) {
                int height = imageFixedHeight;
                int width = imageFixedWidth;
                if (photos.get(0).imgDimensions != null && !photos.get(0).imgDimensions.isEmpty()) {
                    ImageDimensions dimen = commonFunctions.getScaledDimensions(displayMetrics, photos.get(0).imgDimensions, AppKeys.NORMAL);
                    height = dimen.height;
                    width = dimen.width;
                    actionSharedNormalPostViewHolder.iv_cover_photo1.getLayoutParams().height = height;
                    actionSharedNormalPostViewHolder.iv_cover_photo1.getLayoutParams().width = width;
                }
                actionSharedNormalPostViewHolder.iv_cover_photo1.setVisibility(View.VISIBLE);
                if (activity != null) Glide.with(activity.getApplicationContext())
                        .load(photos.get(0).source)
                        .apply(new RequestOptions().override(width, height))
                        .placeholder(R.drawable.placeholder)
                        .into(actionSharedNormalPostViewHolder.iv_cover_photo1);
            }

            if (photos.size() == 1) {
                actionSharedNormalPostViewHolder.ll_two_images.setVisibility(View.GONE);
                actionSharedNormalPostViewHolder.ll_three_images.setVisibility(View.GONE);
            } else if (photos.size() == 2) {
                actionSharedNormalPostViewHolder.iv_cover_photo1.setVisibility(View.GONE);
                actionSharedNormalPostViewHolder.ll_two_images.setVisibility(View.VISIBLE);
                if (activity != null)
                    Glide.with(activity.getApplicationContext()).load(photos.get(0).source).into(actionSharedNormalPostViewHolder.iv_cover_photo2);
                if (activity != null)
                    Glide.with(activity.getApplicationContext()).load(photos.get(1).source).into(actionSharedNormalPostViewHolder.iv_cover_photo3);
                actionSharedNormalPostViewHolder.ll_three_images.setVisibility(View.GONE);
            } else if (photos.size() == 3) {
                actionSharedNormalPostViewHolder.ll_two_images.setVisibility(View.VISIBLE);
                if (activity != null)
                    Glide.with(activity.getApplicationContext()).load(photos.get(1).source).into(actionSharedNormalPostViewHolder.iv_cover_photo2);
                if (activity != null)
                    Glide.with(activity.getApplicationContext()).load(photos.get(2).source).into(actionSharedNormalPostViewHolder.iv_cover_photo3);
                actionSharedNormalPostViewHolder.ll_three_images.setVisibility(View.GONE);
            } else if (photos.size() == 4) {
                actionSharedNormalPostViewHolder.ll_two_images.setVisibility(View.GONE);
                actionSharedNormalPostViewHolder.ll_three_images.setVisibility(View.VISIBLE);
                if (activity != null)
                    Glide.with(activity.getApplicationContext()).load(photos.get(1).source).into(actionSharedNormalPostViewHolder.iv_cover_photo4);
                if (activity != null)
                    Glide.with(activity.getApplicationContext()).load(photos.get(2).source).into(actionSharedNormalPostViewHolder.iv_cover_photo5);
                if (activity != null)
                    Glide.with(activity.getApplicationContext()).load(photos.get(3).source).into(actionSharedNormalPostViewHolder.iv_cover_photo6);
                actionSharedNormalPostViewHolder.tv_more_images.setVisibility(View.GONE);

            } else {
                actionSharedNormalPostViewHolder.ll_two_images.setVisibility(View.GONE);
                actionSharedNormalPostViewHolder.ll_three_images.setVisibility(View.VISIBLE);
                if (activity != null)
                    Glide.with(activity.getApplicationContext()).load(photos.get(1).source).into(actionSharedNormalPostViewHolder.iv_cover_photo4);
                if (activity != null)
                    Glide.with(activity.getApplicationContext()).load(photos.get(2).source).into(actionSharedNormalPostViewHolder.iv_cover_photo5);
                if (activity != null)
                    Glide.with(activity.getApplicationContext()).load(photos.get(3).source).into(actionSharedNormalPostViewHolder.iv_cover_photo6);
                actionSharedNormalPostViewHolder.tv_more_images.setVisibility(View.VISIBLE);
            }
        }

        actionSharedNormalPostViewHolder.cv_post_wrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // commonFunctions.setToastMessage(activity,"item clicked",Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);
                newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                        AppKeys.OPEN_POST, currentItem.postId, "");

                /*Intent normalPostIntent = new Intent(activity, SharedNormalPostActivity.class);
                normalPostIntent.putExtra(AppKeys.POST_ID, currentItem.postId);
                activity.startActivity(normalPostIntent);*/
            }
        });

        actionSharedNormalPostViewHolder.tv_comment_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // commonFunctions.setToastMessage(activity,"item clicked",Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);
                if (isGuestUser) {
                    commonFunctions.showLoginRequestDialog();
                } else {
                    newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                            AppKeys.ADD_POST_COMMENT, currentItem.postId, AppKeys.COMMENT);
                }

                /*Intent normalPostIntent = new Intent(activity, SharedNormalPostActivity.class);
                normalPostIntent.putExtra(AppKeys.POST_ID, currentItem.postId);
                normalPostIntent.putExtra(AppKeys.ACTION, AppKeys.COMMENT);
                activity.startActivity(normalPostIntent);*/
            }
        });

        actionSharedNormalPostViewHolder.popup.getMenu().clear();

        if (user_id.equals(currentItem.authorId)) {
            actionSharedNormalPostViewHolder.inflater.inflate(R.menu.news_feed_normal_post_creator_menu,
                    actionSharedNormalPostViewHolder.popup.getMenu());
        } else {
            actionSharedNormalPostViewHolder.inflater.inflate(R.menu.news_feed_normal_post_other_menu,
                    actionSharedNormalPostViewHolder.popup.getMenu());
            if (userFixedname == null) {
                String firstName = currentItem.postAuthorName;
                if (currentItem.postType.equals("post_like") || currentItem.postType.equals("post_comment")) {
                    firstName = currentItem.liked_by.userFullname;
                }
                if (firstName.contains(" ")) {
                    firstName = firstName.substring(0, firstName.indexOf(" "));
                }
                actionSharedNormalPostViewHolder.popup.getMenu().getItem(1)
                        .setTitle(activity.getString(R.string.unFollow) + " " + firstName);
            } else {
                actionSharedNormalPostViewHolder.popup.getMenu().removeItem(R.id.unfollow);
                actionSharedNormalPostViewHolder.popup.getMenu().removeItem(R.id.hide_post);

            }
        }

        try {
            Field[] fields = actionSharedNormalPostViewHolder.popup.getClass().getDeclaredFields();
            for (Field field : fields) {
                if ("mPopup".equals(field.getName())) {
                    field.setAccessible(true);
                    Object menuPopupHelper = field.get(actionSharedNormalPostViewHolder.popup);
                    Class<?> classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());
                    Method setForceIcons = classPopupHelper.getMethod("setForceShowIcon", boolean.class);
                    setForceIcons.invoke(menuPopupHelper, true);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        actionSharedNormalPostViewHolder.popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                 /*   case R.id.pin:
                        if (newsFeedList.get(position).pinned) {
                            actionSharedNormalPostViewHolder.popup.getMenu().getItem(0)
                                    .setTitle(activity.getString(R.string.pin));
                        } else {
                            actionSharedNormalPostViewHolder.popup.getMenu().getItem(0)
                                    .setTitle(activity.getString(R.string.unpin));
                        }
                        newsFeedList.get(position).pinned = !newsFeedList.get(position).pinned;
                        currentItem.pinned = !currentItem.pinned;
                        newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                                AppKeys.PIN_ARTICLE);
                        break;

                    case R.id.save:
                        if (newsFeedList.get(position).iSave) {
                            actionSharedNormalPostViewHolder.popup.getMenu().getItem(1)
                                    .setTitle(activity.getString(R.string.save));
                        } else {
                            actionSharedNormalPostViewHolder.popup.getMenu().getItem(1)
                                    .setTitle(activity.getString(R.string.unsave));
                        }
                        newsFeedList.get(position).iSave = !newsFeedList.get(position).iSave;
                        currentItem.iSave = !currentItem.iSave;
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

                    case R.id.unfollow:
                        newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                                AppKeys.USER_UNFOLLOW, "", "");
                        break;

                    case R.id.report:
                        commonFunctions.openReportDialog(user_id, "report_post", currentItem.origin.postId);
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
            actionSharedNormalPostViewHolder.rl_top_bar_wrapper.setVisibility(View.GONE);
        }
        if (hideFooter) {
            actionSharedNormalPostViewHolder.rl_user_comment_wrapper.setVisibility(View.GONE);

        }
    }


    //this one is used to config the shared Normal post item
    public void configSharedNormalPostViewHolder(ActionSharedNormalPostViewHolder actionSharedNormalPostViewHolder,
                                                 final int position, final PojoGetNewsFeedResponseData currentItem) {
        actionSharedNormalPostViewHolder.rl_top_bar_wrapper.setVisibility(View.GONE);
        actionSharedNormalPostViewHolder.rl_user_comment_wrapper.setVisibility(View.GONE);

        actionSharedNormalPostViewHolder.rl_web_preview_wrapper.setVisibility(View.GONE);
        List<PojoNewsFeedPhoto> photos = currentItem.origin.photos;

        //sharer data
        actionSharedNormalPostViewHolder.tv_post_sharer_stamp.setText(commonFunctions
                .parseNewsFeedItemTime(currentItem.updatedAt));

        if (currentItem.postTitle.isEmpty()) {
            actionSharedNormalPostViewHolder.tv_share_title.setVisibility(View.GONE);
        } else {
            actionSharedNormalPostViewHolder.tv_share_title.setVisibility(View.VISIBLE);
            actionSharedNormalPostViewHolder.tv_share_title.setText(Html.fromHtml(currentItem.postTitle));
        }
        actionSharedNormalPostViewHolder.tv_post_sharer_name.setText(currentItem.userFullname);
        actionSharedNormalPostViewHolder.tv_post_sharer_designation.setText(currentItem.userWorkTitle);
        actionSharedNormalPostViewHolder.tv_post_sharer_name.setOnClickListener(new View.OnClickListener() {
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
                        .into(actionSharedNormalPostViewHolder.iv_post_sharer_user_dp);
        }
        actionSharedNormalPostViewHolder.iv_post_sharer_user_dp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actionSharedNormalPostViewHolder.tv_post_sharer_name.callOnClick();
            }
        });

        actionSharedNormalPostViewHolder.tv_time_stamp.setText(commonFunctions
                .parseNewsFeedItemTime(currentItem.origin.time));

        if (currentItem.origin.organizationData != null
                && (currentItem.origin.postType.equals("organisation_post") || currentItem.origin.postType.equals("ad"))) {
            if (!currentItem.origin.organizationData.logo.isEmpty()) {
                if (activity != null)
                    Glide.with(activity.getApplicationContext()).load(currentItem.origin.organizationData.logo)
                            .apply(bitmapTransform(new CropCircleTransformation()))
                            .into(actionSharedNormalPostViewHolder.iv_user_dp);
            }
            actionSharedNormalPostViewHolder.tv_poster_name.setText(currentItem.origin.organizationData.name);
            actionSharedNormalPostViewHolder.tv_poster_designation.setText(currentItem.origin.organizationData.typename);

            actionSharedNormalPostViewHolder.tv_poster_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                            AppKeys.OPEN_ORGANISATION, currentItem.origin.organizationData.id, "");

                    /*Intent organisationIntent = new Intent(activity, OrganisationProfileInfoActivity.class);
                    organisationIntent.putExtra(AppKeys.ORGANISATION_ID, currentItem.origin.organizationData.id);
                    activity.startActivity(organisationIntent);*/
                }
            });

        } else {
            //creator data
            actionSharedNormalPostViewHolder.tv_poster_name.setText(currentItem.origin.userFullname);
            actionSharedNormalPostViewHolder.tv_poster_designation.setText(currentItem.origin.userWorkTitle);
            actionSharedNormalPostViewHolder.tv_poster_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                            AppKeys.OPEN_PROFILE, currentItem.origin.authorId, "");

                    /*Intent profileIntent = new Intent(activity, ProfileInfoActivity.class);
                    profileIntent.putExtra("target_user_id", currentItem.origin.authorId);
                    activity.startActivity(profileIntent);*/
                }
            });

            if (!currentItem.origin.postAuthorPicture.isEmpty()) {
                if (activity != null)
                    Glide.with(activity.getApplicationContext()).load(currentItem.origin.postAuthorPicture)
                            .apply(bitmapTransform(new CropCircleTransformation()))
                            .into(actionSharedNormalPostViewHolder.iv_user_dp);
            }

        }


        actionSharedNormalPostViewHolder.iv_user_dp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actionSharedNormalPostViewHolder.tv_poster_name.callOnClick();
            }
        });
        Log.d("newsfeed", currentItem.postId);

        String userToShow = "";
        if (userFixedname == null) {
            userToShow = currentItem.userFullname;
        } else {
            userToShow = userFixedname;
        }

        if (userFixedname == null) {
            int total = Integer.parseInt(currentItem.total);
            if (total > 0) {
                userToShow = userToShow + " " + activity.getString(R.string.and) + " " + currentItem.total +
                        " " + activity.getString(R.string.others);
            }
        }

        if (currentItem.origin.postType.equals("photos")) {
            actionSharedNormalPostViewHolder.tv_activity_performed.setText(Html.fromHtml(userToShow + " "
                    + activity.getString(R.string.shared) + " " + activity.getString(R.string.photos)));
            photos = currentItem.origin.photos;
        } else if (currentItem.origin.postType.equals("")) {
            actionSharedNormalPostViewHolder.tv_activity_performed.setText(Html.fromHtml(userToShow + " "
                    + activity.getString(R.string.shared)));
        }

        boolean tagged;
        if (currentItem.origin.tagged.equals("1")) {
            tagged = true;
        } else {
            tagged = false;
        }

        actionSharedNormalPostViewHolder.tv_post_content.setHighlightColor(
                Color.TRANSPARENT); // prevent TextView change background when highlight
        //  actionSharedNormalPostViewHolder.tv_post_content.setMovementMethod(LinkMovementMethod.getInstance());
        currentItem.spannableClickItem = commonFunctions.setClickSpans(currentItem.origin.text,
                currentItem.origin.textPlain, tagged, null);
        currentItem.clickableSpansList = currentItem.spannableClickItem.clickableSpansList;
        actionSharedNormalPostViewHolder.tv_post_content
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
                actionSharedNormalPostViewHolder.rl_web_preview_wrapper.setVisibility(View.GONE);
                String title = currentItem.sourceContent.getTitle();
                String name = currentItem.sourceContent.getCannonicalUrl();
                String description = currentItem.sourceContent.getCannonicalUrl();
                if (title.isEmpty() || name.isEmpty()) {
                    actionSharedNormalPostViewHolder.rl_web_preview_wrapper.setVisibility(View.GONE);
                } else {
                    actionSharedNormalPostViewHolder.rl_web_preview_wrapper.setVisibility(View.VISIBLE);
                    actionSharedNormalPostViewHolder.tv_link_title.setText(currentItem.sourceContent.getTitle());
                    actionSharedNormalPostViewHolder.tv_website_name.setText(currentItem.sourceContent.getCannonicalUrl());
                    actionSharedNormalPostViewHolder.tv_link_description.setText(currentItem.sourceContent.getDescription());
                    for (int i = 0; i < currentItem.sourceContent.getImages().size(); i++) {
                        if (currentItem.sourceContent.getImages().get(i).contains("png")
                                || currentItem.sourceContent.getImages().get(i).contains("jpg")) {
                            if (activity != null)
                                Glide.with(activity.getApplicationContext())
                                        .load(currentItem.sourceContent.getImages().get(i))
                                        .into(actionSharedNormalPostViewHolder.iv_link_image);
                            break;
                        }
                    }
                }
            } else {
                actionSharedNormalPostViewHolder.textCrawler = new TextCrawler();
                LinkPreviewCallback linkPreviewCallback = new LinkPreviewCallback() {
                    @Override
                    public void onPre() {
                        // Any work that needs to be done before generating the preview. Usually inflate
                        // your custom preview layout here.
                        //commonFunctions.setToastMessage(activity,"link preview called",Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);
                        actionSharedNormalPostViewHolder.rl_web_preview_wrapper.setVisibility(View.GONE);
                    }

                    @Override
                    public void onPos(SourceContent sourceContent, boolean b) {
                        currentItem.sourceContent = sourceContent;
                        // Populate your preview layout with the results of sourceContent.
                        String title = sourceContent.getTitle();
                        String name = sourceContent.getCannonicalUrl();
                        String description = sourceContent.getCannonicalUrl();

                        if (title.isEmpty() || name.isEmpty()) {
                            actionSharedNormalPostViewHolder.rl_web_preview_wrapper.setVisibility(View.GONE);
                        } else {
                            actionSharedNormalPostViewHolder.rl_web_preview_wrapper.setVisibility(View.VISIBLE);
                            actionSharedNormalPostViewHolder.tv_link_title.setText(sourceContent.getTitle());
                            actionSharedNormalPostViewHolder.tv_website_name.setText(sourceContent.getCannonicalUrl());
                            actionSharedNormalPostViewHolder.tv_link_description.setText(sourceContent.getDescription());
                            for (int i = 0; i < sourceContent.getImages().size(); i++) {
                                if (sourceContent.getImages().get(i).contains("png")
                                        || sourceContent.getImages().get(i).contains("jpg")) {
                                    Glide.with(activity)
                                            .load(sourceContent.getImages().get(i))
                                            .into(actionSharedNormalPostViewHolder.iv_link_image);
                                    break;
                                }
                            }

                        }
                    }
                };
                actionSharedNormalPostViewHolder.textCrawler.makePreview(linkPreviewCallback, url);
            }


        }

        Paint paint = new Paint();
        paint.setTextSize(actionSharedNormalPostViewHolder.tv_post_content.getTextSize());
        paint.setTypeface(actionSharedNormalPostViewHolder.tv_post_content.getTypeface());

        float textViewWidthPx = displayMetrics.widthPixels * 0.85f;
        int lineCount = commonFunctions.splitWordsIntoStringsThatFit(currentItem.spannableClickItem.spannableString.toString()
                , textViewWidthPx, paint);

        currentItem.canTextFit = true;

        if (lineCount > 8) {
            currentItem.canTextFit = false;
        } else {
            currentItem.canTextFit = true;
            currentItem.expanded = true;
        }

        if (currentItem.expanded) {
            actionSharedNormalPostViewHolder.tv_post_content.setMaxLines(10000);
            actionSharedNormalPostViewHolder.tv_post_content
                    .setText(currentItem.spannableClickItem.spannableString, TextView.BufferType.SPANNABLE);
        } else {
            actionSharedNormalPostViewHolder.tv_post_content.setMaxLines(8);
            if (!currentItem.canTextFit) {
                actionSharedNormalPostViewHolder.tv_view_more.setVisibility(View.VISIBLE);
                actionSharedNormalPostViewHolder.tv_view_more.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        actionSharedNormalPostViewHolder.tv_view_more.setVisibility(View.GONE);
                        actionSharedNormalPostViewHolder.tv_post_content.setMaxLines(10000);
                        currentItem.expanded = true;
                        actionSharedNormalPostViewHolder.tv_post_content
                                .setText(currentItem.spannableClickItem.spannableString, TextView.BufferType.SPANNABLE);
                        //normalPostViewHolder.tv_post_content.setLinksClickable(false);
                    }
                });
            }
        }


        if (currentItem.totalLikes == 0) {
            actionSharedNormalPostViewHolder.tv_like_number.setVisibility(View.GONE);
        } else {
            actionSharedNormalPostViewHolder.tv_like_number.setVisibility(View.VISIBLE);
        }
        actionSharedNormalPostViewHolder.tv_like_number.setText("" + currentItem.totalLikes + " "
                + activity.getString(R.string.likes));

        if (currentItem.comments.equals("0")) {
            actionSharedNormalPostViewHolder.tv_comment_number.setVisibility(View.GONE);
        } else {
            actionSharedNormalPostViewHolder.tv_comment_number.setVisibility(View.VISIBLE);
        }
        actionSharedNormalPostViewHolder.tv_comment_number.setText("" + currentItem.comments + " "
                + activity.getString(R.string.comments));
       /* actionSharedNormalPostViewHolder.tv_like_number.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openUserListDialog(currentItem.likedUsers);
            }
        });*/

        if (currentItem.iLike == 1) {
            actionSharedNormalPostViewHolder.tv_like_button.setTextColor(ContextCompat.getColor(activity, R.color.app_theme_medium));
            actionSharedNormalPostViewHolder.tv_like_button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_like_filled, 0, 0, 0);
        } else {
            actionSharedNormalPostViewHolder.tv_like_button.setTextColor(ContextCompat.getColor(activity, R.color.med_grey));
            actionSharedNormalPostViewHolder.tv_like_button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_like_unfilled, 0, 0, 0);
        }

        actionSharedNormalPostViewHolder.tv_like_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isGuestUser) {
                    commonFunctions.showLoginRequestDialog();
                } else {
                    newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem, AppKeys.POST_LIKE, "", "");
                }
            }
        });

        actionSharedNormalPostViewHolder.tv_share_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isGuestUser) {
                    commonFunctions.showLoginRequestDialog();
                } else {
                    newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem, AppKeys.POST_SHARE, "", "");
                }
            }
        });


        if (photos == null || photos.size() == 0) {
            actionSharedNormalPostViewHolder.iv_cover_photo1.setVisibility(View.GONE);
            actionSharedNormalPostViewHolder.ll_two_images.setVisibility(View.GONE);
            actionSharedNormalPostViewHolder.ll_three_images.setVisibility(View.GONE);
        } else {
            if (photos.size() != 2) {
                int height = imageFixedHeight;
                int width = imageFixedWidth;
                if (photos.get(0).imgDimensions != null && !photos.get(0).imgDimensions.isEmpty()) {
                    ImageDimensions dimen = commonFunctions.getScaledDimensions(displayMetrics, photos.get(0).imgDimensions, AppKeys.NORMAL);
                    height = dimen.height;
                    width = dimen.width;
                    actionSharedNormalPostViewHolder.iv_cover_photo1.getLayoutParams().height = height;
                    actionSharedNormalPostViewHolder.iv_cover_photo1.getLayoutParams().width = width;
                }
                actionSharedNormalPostViewHolder.iv_cover_photo1.setVisibility(View.VISIBLE);
                if (activity != null) Glide.with(activity.getApplicationContext())
                        .load(photos.get(0).source)
                        .apply(new RequestOptions().override(width, height))
                        .placeholder(R.drawable.placeholder)
                        .into(actionSharedNormalPostViewHolder.iv_cover_photo1);
            }

            if (photos.size() == 1) {
                actionSharedNormalPostViewHolder.ll_two_images.setVisibility(View.GONE);
                actionSharedNormalPostViewHolder.ll_three_images.setVisibility(View.GONE);
            } else if (photos.size() == 2) {
                actionSharedNormalPostViewHolder.iv_cover_photo1.setVisibility(View.GONE);
                actionSharedNormalPostViewHolder.ll_two_images.setVisibility(View.VISIBLE);
                if (activity != null)
                    Glide.with(activity.getApplicationContext()).load(photos.get(0).source).into(actionSharedNormalPostViewHolder.iv_cover_photo2);
                if (activity != null)
                    Glide.with(activity.getApplicationContext()).load(photos.get(1).source).into(actionSharedNormalPostViewHolder.iv_cover_photo3);
                actionSharedNormalPostViewHolder.ll_three_images.setVisibility(View.GONE);
            } else if (photos.size() == 3) {
                actionSharedNormalPostViewHolder.ll_two_images.setVisibility(View.VISIBLE);
                if (activity != null)
                    Glide.with(activity.getApplicationContext()).load(photos.get(1).source).into(actionSharedNormalPostViewHolder.iv_cover_photo2);
                if (activity != null)
                    Glide.with(activity.getApplicationContext()).load(photos.get(2).source).into(actionSharedNormalPostViewHolder.iv_cover_photo3);
                actionSharedNormalPostViewHolder.ll_three_images.setVisibility(View.GONE);
            } else if (photos.size() == 4) {
                actionSharedNormalPostViewHolder.ll_two_images.setVisibility(View.GONE);
                actionSharedNormalPostViewHolder.ll_three_images.setVisibility(View.VISIBLE);
                if (activity != null)
                    Glide.with(activity.getApplicationContext()).load(photos.get(1).source).into(actionSharedNormalPostViewHolder.iv_cover_photo4);
                if (activity != null)
                    Glide.with(activity.getApplicationContext()).load(photos.get(2).source).into(actionSharedNormalPostViewHolder.iv_cover_photo5);
                if (activity != null)
                    Glide.with(activity.getApplicationContext()).load(photos.get(3).source).into(actionSharedNormalPostViewHolder.iv_cover_photo6);
                actionSharedNormalPostViewHolder.tv_more_images.setVisibility(View.GONE);

            } else {
                actionSharedNormalPostViewHolder.ll_two_images.setVisibility(View.GONE);
                actionSharedNormalPostViewHolder.ll_three_images.setVisibility(View.VISIBLE);
                if (activity != null)
                    Glide.with(activity.getApplicationContext()).load(photos.get(1).source).into(actionSharedNormalPostViewHolder.iv_cover_photo4);
                if (activity != null)
                    Glide.with(activity.getApplicationContext()).load(photos.get(2).source).into(actionSharedNormalPostViewHolder.iv_cover_photo5);
                if (activity != null)
                    Glide.with(activity.getApplicationContext()).load(photos.get(3).source).into(actionSharedNormalPostViewHolder.iv_cover_photo6);
                actionSharedNormalPostViewHolder.tv_more_images.setVisibility(View.VISIBLE);
            }
        }

        actionSharedNormalPostViewHolder.cv_post_wrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // commonFunctions.setToastMessage(activity,"item clicked",Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);
                newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                        AppKeys.OPEN_POST, currentItem.postId, "");

               /* Intent normalPostIntent = new Intent(activity, SharedNormalPostActivity.class);
                normalPostIntent.putExtra(AppKeys.POST_ID, currentItem.postId);
                activity.startActivity(normalPostIntent);*/
            }
        });

        actionSharedNormalPostViewHolder.tv_comment_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // commonFunctions.setToastMessage(activity,"item clicked",Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);
                if (isGuestUser) {
                    commonFunctions.showLoginRequestDialog();
                } else {
                    newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                            AppKeys.ADD_POST_COMMENT, currentItem.postId, AppKeys.COMMENT);
                }

                /*Intent normalPostIntent = new Intent(activity, SharedNormalPostActivity.class);
                normalPostIntent.putExtra(AppKeys.POST_ID, currentItem.postId);
                normalPostIntent.putExtra(AppKeys.ACTION, AppKeys.COMMENT);
                activity.startActivity(normalPostIntent);*/
            }
        });

        actionSharedNormalPostViewHolder.popup.getMenu().clear();

        if (!user_id.equals(currentItem.authorId)) {
            actionSharedNormalPostViewHolder.inflater.inflate(R.menu.news_feed_normal_post_other_menu,
                    actionSharedNormalPostViewHolder.popup.getMenu());

            if (userFixedname == null) {
                String firstName = "";
                firstName = currentItem.postAuthorName;

                if (firstName.contains(" ")) {
                    firstName = firstName.substring(0, firstName.indexOf(" "));
                }
                actionSharedNormalPostViewHolder.popup.getMenu().getItem(1)
                        .setTitle(activity.getString(R.string.unFollow) + " " + firstName);
            } else {
                actionSharedNormalPostViewHolder.popup.getMenu().removeItem(R.id.unfollow);
                actionSharedNormalPostViewHolder.popup.getMenu().removeItem(R.id.hide_post);
            }
        } else {
            actionSharedNormalPostViewHolder.inflater.inflate(R.menu.news_feed_normal_post_creator_menu,
                    actionSharedNormalPostViewHolder.popup.getMenu());
        }

        try {
            Field[] fields = actionSharedNormalPostViewHolder.popup.getClass().getDeclaredFields();
            for (Field field : fields) {
                if ("mPopup".equals(field.getName())) {
                    field.setAccessible(true);
                    Object menuPopupHelper = field.get(actionSharedNormalPostViewHolder.popup);
                    Class<?> classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());
                    Method setForceIcons = classPopupHelper.getMethod("setForceShowIcon", boolean.class);
                    setForceIcons.invoke(menuPopupHelper, true);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        actionSharedNormalPostViewHolder.popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                  /*  case R.id.pin:
                        if (newsFeedList.get(position).pinned) {
                            actionSharedNormalPostViewHolder.popup.getMenu().getItem(0)
                                    .setTitle(activity.getString(R.string.pin));
                        } else {
                            actionSharedNormalPostViewHolder.popup.getMenu().getItem(0)
                                    .setTitle(activity.getString(R.string.unpin));
                        }
                        newsFeedList.get(position).pinned = !newsFeedList.get(position).pinned;
                        currentItem.pinned = !currentItem.pinned;
                        newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                                AppKeys.PIN_ARTICLE);
                        break;

                    case R.id.save:
                        if (newsFeedList.get(position).iSave) {
                            actionSharedNormalPostViewHolder.popup.getMenu().getItem(1)
                                    .setTitle(activity.getString(R.string.save));
                        } else {
                            actionSharedNormalPostViewHolder.popup.getMenu().getItem(1)
                                    .setTitle(activity.getString(R.string.unsave));
                        }
                        newsFeedList.get(position).iSave = !newsFeedList.get(position).iSave;
                        currentItem.iSave = !currentItem.iSave;
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

                    case R.id.unfollow:
                        newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                                AppKeys.USER_UNFOLLOW, "", "");
                        break;

                    case R.id.report:
                        commonFunctions.openReportDialog(user_id, "report_post", currentItem.postId);
                      /*  newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                                AppKeys.REPORT_POST);*/
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
            actionSharedNormalPostViewHolder.rl_top_bar_wrapper.setVisibility(View.GONE);
        }
        if (hideFooter) {
            actionSharedNormalPostViewHolder.rl_user_comment_wrapper.setVisibility(View.GONE);

        }


    }
}
