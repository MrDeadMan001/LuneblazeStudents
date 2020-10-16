package com.avadna.luneblaze.adapters.newsfeed;

import android.content.Intent;
import android.graphics.Paint;
import android.text.Html;
import android.text.method.LinkMovementMethod;
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
import com.avadna.luneblaze.activities.normalpost.NormalPostPhotoGalleryViewerActivity;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.ImageDimensions;
import com.avadna.luneblaze.pojo.pojoNewsFeed.PojoGetNewsFeedResponseData;
import com.avadna.luneblaze.pojo.pojoNewsFeed.PojoNewsFeedPhoto;
import com.avadna.luneblaze.pojo.pojoVenueDetails.PojoVenuePhoto;
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
import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

public class NormalPostNewsFeedItem extends NewsFeedItemBase {

    public NormalPostNewsFeedItem(Fragment fragment, String userFixedname) {
        super(fragment, userFixedname);
    }

    public NormalPostViewHolder getViewHolder(ViewGroup parent) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.normal_post_item, parent, false);
        return new NormalPostViewHolder(itemView);
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
        ImageView iv_comment_dp;
        TextView tv_comment_user_name, tv_user_comment;

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

        ImageView iv_more_options;
        MenuInflater inflater;
        PopupMenu popup;

        LinearLayout ll_two_images;
        LinearLayout ll_three_images;
        LinearLayout ll_content_wrapper;

        RelativeLayout rl_user_comment_wrapper;



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

    public void configNormalPostViewHolder(NormalPostViewHolder normalPostViewHolder, final int position,
                                           final PojoGetNewsFeedResponseData currentItem) {
        normalPostViewHolder.rl_user_comment_wrapper.setVisibility(View.GONE);
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
            if (currentItem.postType.equals("post_like") || currentItem.postType.equals("shared")
                    || currentItem.postType.equals("post_comment")) {
                normalPostViewHolder.rl_top_bar_wrapper.setVisibility(View.VISIBLE);
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
                    normalPostViewHolder.tv_activity_performed.setText(Html.fromHtml(userToShow + " "
                            + likeOrLikes));
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

                normalPostViewHolder.tv_poster_name.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                                AppKeys.OPEN_ORGANISATION, currentItem.organizationData.id, "");

                        /*Intent organisationIntent = new Intent(activity, OrganisationProfileInfoActivity.class);
                        organisationIntent.putExtra(AppKeys.ORGANISATION_ID, currentItem.organizationData.id);
                        activity.startActivity(organisationIntent);*/
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
                        newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                                AppKeys.OPEN_PROFILE, currentItem.authorId, "");

                        /*Intent profileIntent = new Intent(activity, ProfileInfoActivity.class);
                        profileIntent.putExtra("target_user_id", currentItem.authorId);
                        activity.startActivity(profileIntent);*/
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
            // Log.d("span info","post_id="+currentItem.postId);
            currentItem.spannableClickItem = commonFunctions.setClickSpans(currentItem.text,
                    currentItem.textPlain, tagged, currentItem.postId);

            normalPostViewHolder.tv_post_content.setLinksClickable(false);

           /* if (currentItem.spannableClickItem.clickableSpansList.isEmpty()) {
                normalPostViewHolder.tv_post_content.setMovementMethod(null);
            } else*/

            normalPostViewHolder.tv_post_content.setMovementMethod(LinkMovementMethod.getInstance());
            normalPostViewHolder.tv_post_content.setOnTouchListener(new LinkMovementMethodOverride());


            // normalPostViewHolder.tv_post_content.setText(new SpannableString(Html.fromHtml(currentItem.text)));
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
            if (lineCount > 8) {
                currentItem.canTextFit = false;
            } else {
                currentItem.canTextFit = true;
                currentItem.expanded = true;
            }

            if (currentItem.expanded) {
                normalPostViewHolder.tv_post_content.setMaxLines(10000);
                /*normalPostViewHolder.tv_post_content
                        .setText(currentItem.spannableClickItem.spannableString, TextView.BufferType.SPANNABLE);*/
            } else {
                normalPostViewHolder.tv_post_content.setMaxLines(8);

                /*normalPostViewHolder.tv_post_content
                        .setText(currentItem.spannableClickItem.spannableString, TextView.BufferType.SPANNABLE);*/
                if (!currentItem.canTextFit) {
                    normalPostViewHolder.tv_view_more.setVisibility(View.VISIBLE);
                    normalPostViewHolder.tv_view_more.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            normalPostViewHolder.tv_view_more.setVisibility(View.GONE);
                            normalPostViewHolder.tv_post_content.setMaxLines(10000);
                            currentItem.expanded = true;
                            /*normalPostViewHolder.tv_post_content
                                    .setText(currentItem.spannableClickItem.spannableString, TextView.BufferType.SPANNABLE);*/
                            //normalPostViewHolder.tv_post_content.setLinksClickable(false);
                        }
                    });
                }
            }

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

        normalPostViewHolder.tv_like_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isGuestUser) {
                    commonFunctions.showLoginRequestDialog();
                } else {
                    if (currentItem.postType.equals("shared")) {
                        newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem, AppKeys.POST_LIKE, "", "");
                    } else {
                        newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem, AppKeys.POST_LIKE, "", "");
                    }
                }
            }
        });

        normalPostViewHolder.tv_boost_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Intent adIntent = new Intent(activity, AdTargetAudienceActivity.class);
                adIntent.putExtra(AppKeys.POST_ID, currentItem.postId);
                adIntent.putExtra(AppKeys.ORGANISATION_ID, currentItem.organizationData.id);
                fragment.startActivityForResult(adIntent, AppKeys.BOOST_POST_REQUEST_CODE);*/
                if (isGuestUser) {
                    commonFunctions.showLoginRequestDialog();
                } else {
                    newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                            AppKeys.BOOST_POST, currentItem.postId, currentItem.organizationData.id);
                }
            }
        });

        normalPostViewHolder.tv_share_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isGuestUser) {
                    commonFunctions.showLoginRequestDialog();
                } else {
                    newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                            AppKeys.POST_SHARE, "", "");
                }
            }
        });

       /* normalPostViewHolder.tv_comment_number.setText("" + currentItem.comments + " "
                + activity.getString(R.string.comments));*/

        if (photos == null || photos.size() == 0) {
            normalPostViewHolder.iv_cover_photo1.setVisibility(View.GONE);
            normalPostViewHolder.ll_two_images.setVisibility(View.GONE);
            normalPostViewHolder.ll_three_images.setVisibility(View.GONE);
        } else {
            if (photos.size() != 2) {
                int height = imageFixedHeight;
                int width = imageFixedWidth;
                if (photos.get(0).imgDimensions != null && !photos.get(0).imgDimensions.isEmpty()) {
                    ImageDimensions dimen = commonFunctions.getScaledDimensions(displayMetrics, photos.get(0).imgDimensions, AppKeys.NORMAL);
                    height = dimen.height;
                    width = dimen.width;
                    normalPostViewHolder.iv_cover_photo1.getLayoutParams().height = height;
                    normalPostViewHolder.iv_cover_photo1.getLayoutParams().width = width;
                }
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

        View.OnClickListener photoClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (photos != null) {
                    int photoPos = 0;
                    switch (v.getId()) {
                        case R.id.iv_cover_photo1:
                            photoPos = 0;
                            break;

                        case R.id.iv_cover_photo2:
                            if (photos.size() == 2) {
                                photoPos = 0;
                            }
                            if (photos.size() == 3) {
                                photoPos = 1;
                            }
                            break;

                        case R.id.iv_cover_photo3:
                            if (photos.size() == 2) {
                                photoPos = 1;
                            }
                            if (photos.size() == 3) {
                                photoPos = 2;
                            }
                            break;

                        case R.id.iv_cover_photo4:
                            photoPos = 1;
                            break;

                        case R.id.iv_cover_photo5:
                            photoPos = 2;
                            break;

                        case R.id.iv_cover_photo6:
                            photoPos = 3;
                            break;
                    }

                    Type type = new TypeToken<List<PojoVenuePhoto>>() {
                    }.getType();
                    String dataStr = new Gson().toJson(photos, type);
                    Intent imgViewerIntent = new Intent(activity,
                            NormalPostPhotoGalleryViewerActivity.class);
                    imgViewerIntent.putExtra("data", dataStr);
                    imgViewerIntent.putExtra("position", photoPos);
                    activity.startActivity(imgViewerIntent);
                }
            }
        };
        normalPostViewHolder.iv_cover_photo1.setOnClickListener(photoClickListener);
        normalPostViewHolder.iv_cover_photo2.setOnClickListener(photoClickListener);
        normalPostViewHolder.iv_cover_photo3.setOnClickListener(photoClickListener);
        normalPostViewHolder.iv_cover_photo4.setOnClickListener(photoClickListener);
        normalPostViewHolder.iv_cover_photo5.setOnClickListener(photoClickListener);
        normalPostViewHolder.iv_cover_photo6.setOnClickListener(photoClickListener);

        normalPostViewHolder.cv_post_wrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // commonFunctions.setToastMessage(activity,"item clicked",Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);
                newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                        AppKeys.OPEN_POST, targetPostId, "");

                /*Intent normalPostIntent = new Intent(activity, NormalPostActivity.class);
                normalPostIntent.putExtra(AppKeys.POST_ID, targetPostId);
                activity.startActivity(normalPostIntent)*/
                ;
            }
        });

        normalPostViewHolder.tv_comment_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // commonFunctions.setToastMessage(activity,"item clicked",Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);
                if (isGuestUser) {
                    commonFunctions.showLoginRequestDialog();
                } else {
                    newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                            AppKeys.ADD_POST_COMMENT, targetPostId, AppKeys.COMMENT);
                }

                /*Intent normalPostIntent = new Intent(activity, NormalPostActivity.class);
                normalPostIntent.putExtra(AppKeys.POST_ID, targetPostId);
                normalPostIntent.putExtra(AppKeys.ACTION, AppKeys.COMMENT);
                activity.startActivity(normalPostIntent);*/
            }
        });


        if (currentItem.postType.equals("post_comment")) {
            if (currentItem.commentsArray != null && currentItem.commentsArray.size() > 0) {
                normalPostViewHolder.rl_user_comment_wrapper.setVisibility(View.VISIBLE);

                if (activity != null)
                    Glide.with(activity.getApplicationContext()).load(currentItem.commentsArray.get(0).createdBy.userPicture)
                            .apply(new RequestOptions().override(96, 96))
                            .apply(bitmapTransform(new CropCircleTransformation()))
                            .into(normalPostViewHolder.iv_comment_dp);

                normalPostViewHolder.tv_comment_user_name.setText(currentItem.commentsArray.get(0)
                        .createdBy.userFullname);
                normalPostViewHolder.tv_comment_user_name.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                                AppKeys.OPEN_PROFILE, currentItem.commentsArray.get(0).userId, "");

                        /*Intent profileIntent = new Intent(activity, ProfileInfoActivity.class);
                        profileIntent.putExtra("target_user_id", currentItem.commentsArray.get(0)
                                .userId);
                        activity.startActivity(profileIntent);*/
                    }
                });

                normalPostViewHolder.tv_user_comment.setText(Html.fromHtml(currentItem.commentsArray.get(0).text));
            }
        }

        normalPostViewHolder.popup.getMenu().clear();

        if (user_id.equals(currentItem.authorId)) {
            normalPostViewHolder.inflater.inflate(R.menu.news_feed_normal_post_creator_menu,
                    normalPostViewHolder.popup.getMenu());

        } else {
            normalPostViewHolder.inflater.inflate(R.menu.news_feed_normal_post_other_menu,
                    normalPostViewHolder.popup.getMenu());
            if (userFixedname == null) {
                String firstName = "";
                if (currentItem.postType.equals("post_like") || currentItem.postType.equals("post_comment")) {
                    firstName = currentItem.liked_by.userFullname;
                } else {
                    firstName = currentItem.postAuthorName;
                }
                if (firstName.contains(" ")) {
                    firstName = firstName.substring(0, firstName.indexOf(" "));
                }
                normalPostViewHolder.popup.getMenu().getItem(1)
                        .setTitle(activity.getString(R.string.unFollow) + " " + firstName);
            } else if (currentItem.postType.equalsIgnoreCase("ad")) {

                normalPostViewHolder.popup.getMenu().getItem(2)
                        .setTitle(activity.getBaseContext().getString(R.string.hide_ad));
                normalPostViewHolder.popup.getMenu().getItem(3)
                        .setTitle(activity.getBaseContext().getString(R.string.report_ad));
                normalPostViewHolder.popup.getMenu().removeItem(R.id.unfollow);

            } else {
                normalPostViewHolder.popup.getMenu().removeItem(R.id.unfollow);
                normalPostViewHolder.popup.getMenu().removeItem(R.id.hide_post);
            }
        }


        try {
            Field[] fields = normalPostViewHolder.popup.getClass().getDeclaredFields();
            for (Field field : fields) {
                if ("mPopup".equals(field.getName())) {
                    field.setAccessible(true);
                    Object menuPopupHelper = field.get(normalPostViewHolder.popup);
                    Class<?> classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());
                    Method setForceIcons = classPopupHelper.getMethod("setForceShowIcon", boolean.class);
                    setForceIcons.invoke(menuPopupHelper, true);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        normalPostViewHolder.popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                   /* case R.id.pin:
                        if (newsFeedList.get(position).pinned) {
                            normalPostViewHolder.popup.getMenu().getItem(0)
                                    .setTitle(activity.getString(R.string.pin));
                        } else {
                            normalPostViewHolder.popup.getMenu().getItem(0)
                                    .setTitle(activity.getString(R.string.unpin));
                        }
                        newsFeedList.get(position).pinned = !newsFeedList.get(position).pinned;
                        currentItem.pinned = !currentItem.pinned;
                        newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                                AppKeys.PIN_ARTICLE);
                        break;

                    case R.id.save:
                        if (newsFeedList.get(position).iSave) {
                            normalPostViewHolder.popup.getMenu().getItem(1)
                                    .setTitle(activity.getString(R.string.save));
                        } else {
                            normalPostViewHolder.popup.getMenu().getItem(1)
                                    .setTitle(activity.getString(R.string.unsave));
                        }
                        newsFeedList.get(position).iSave = !newsFeedList.get(position).iSave;
                        currentItem.iSave = !currentItem.iSave;
                        newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                                AppKeys.SAVE_ARTICLE);
                        break;


*/
                    case R.id.edit:
                        newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                                AppKeys.EDIT_POST, "", "");
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
                                AppKeys.UNFOLLOW_USER, "", "");
                        break;

                    case R.id.report:
                        // commonFunctions.openReportDialog(user_id, "report_post", currentItem.postId);

                        newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                                AppKeys.REPORT_POST, "", "");
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
            normalPostViewHolder.rl_top_bar_wrapper.setVisibility(View.GONE);
        }
        if (hideFooter) {
            normalPostViewHolder.rl_user_comment_wrapper.setVisibility(View.GONE);
        }

    }
}
