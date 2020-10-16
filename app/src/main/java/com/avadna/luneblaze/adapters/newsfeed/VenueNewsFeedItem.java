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
import android.widget.RatingBar;
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
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

public class VenueNewsFeedItem extends NewsFeedItemBase {

    public VenueNewsFeedItem(Fragment fragment, String userFixedname) {
        super(fragment,userFixedname);
    }

    public VenuePostViewHolder getViewHolder(ViewGroup parent) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.venue_post, parent, false);
        return new VenuePostViewHolder(itemView);
    }

    public class VenuePostViewHolder extends RecyclerView.ViewHolder {
        CardView cv_post_wrapper;
        LinearLayout ll_share_data_wrapper;
        RelativeLayout rl_user_data_wrapper;
        TextView tv_share_title;
        ImageView iv_post_sharer_user_dp;
        TextView tv_post_sharer_name;
        TextView tv_post_sharer_designation;
        TextView tv_post_sharer_stamp;

        ImageView iv_more_options;
        PopupMenu popup;
        RelativeLayout rl_top_bar_wrapper;
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
        MenuInflater inflater;


        public VenuePostViewHolder(View itemView) {
            super(itemView);
            cv_post_wrapper = (CardView) itemView.findViewById(R.id.cv_post_wrapper);
            ll_share_data_wrapper = (LinearLayout) itemView.findViewById(R.id.ll_share_data_wrapper);
            rl_user_data_wrapper = (RelativeLayout) itemView.findViewById(R.id.rl_user_data_wrapper);
            rl_user_data_wrapper.setVisibility(View.GONE);
            tv_share_title = (TextView) itemView.findViewById(R.id.tv_share_title);
            iv_post_sharer_user_dp = (ImageView) itemView.findViewById(R.id.iv_post_sharer_user_dp);
            tv_post_sharer_name = (TextView) itemView.findViewById(R.id.tv_post_sharer_name);
            tv_post_sharer_designation = (TextView) itemView.findViewById(R.id.tv_post_sharer_designation);
            tv_post_sharer_stamp = (TextView) itemView.findViewById(R.id.tv_post_sharer_stamp);

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

            iv_more_options = (ImageView) itemView.findViewById(R.id.iv_more_options);
            if (isGuestUser) {
                iv_more_options.setVisibility(View.GONE);
            }
            rl_top_bar_wrapper = (RelativeLayout) itemView.findViewById(R.id.rl_top_bar_wrapper);
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


    public void configVenuePostViewHolder(VenuePostViewHolder venuePostViewHolder,final int position,
                                          final PojoGetNewsFeedResponseData currentItem) {
        if (!currentItem.origin.coverPhoto.isEmpty()) {
            int height = imageFixedHeight;
            int width = imageFixedWidth;

            if (currentItem.origin.imgDimensions != null && !currentItem.origin.imgDimensions.isEmpty()) {
                ImageDimensions dimen = commonFunctions.getScaledDimensions(displayMetrics, currentItem.origin.imgDimensions, AppKeys.VENUE);
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
                    .apply(new RequestOptions().override(width, height).placeholder(R.drawable.placeholder))
                    .into(venuePostViewHolder.iv_cover_photo);

        }

        venuePostViewHolder.ll_share_data_wrapper.setVisibility(View.VISIBLE);
        venuePostViewHolder.tv_post_sharer_stamp.setText(commonFunctions
                .parseNewsFeedItemTime(currentItem.time));
        if (currentItem.postTitle.isEmpty()) {
            venuePostViewHolder.tv_share_title.setVisibility(View.GONE);
        } else {
            venuePostViewHolder.tv_share_title.setVisibility(View.VISIBLE);
            venuePostViewHolder.tv_share_title.setText(Html.fromHtml(currentItem.postTitle));
        }
        venuePostViewHolder.tv_post_sharer_name.setText(currentItem.userFullname);
        venuePostViewHolder.tv_post_sharer_designation.setText(currentItem.userWorkTitle);
        venuePostViewHolder.tv_post_sharer_name.setOnClickListener(new View.OnClickListener() {
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
                        .into(venuePostViewHolder.iv_post_sharer_user_dp);
        }
        venuePostViewHolder.iv_post_sharer_user_dp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                venuePostViewHolder.tv_post_sharer_name.callOnClick();
            }
        });

        venuePostViewHolder.tv_venue_name.setText(currentItem.origin.venueName);
        venuePostViewHolder.rb_venue_rating.setRating(Float.parseFloat(currentItem.origin.totalRating));
        venuePostViewHolder.tv_session_count.setText("" + currentItem.origin.allSessions);
        venuePostViewHolder.tv_follower_count.setText("" + currentItem.origin.venueFollowers);
        venuePostViewHolder.tv_attendee_count.setText("" + currentItem.origin.allAttendees);

        if (currentItem.origin.iFollow == 1) {
            venuePostViewHolder.tv_follow_button.setTextColor(ContextCompat.getColor(activity, R.color.app_theme_medium));
            venuePostViewHolder.tv_follow_button.setText(activity.getString(R.string.following));
            venuePostViewHolder.tv_follow_button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_venue_followed,
                    0, 0, 0);

        } else {
            venuePostViewHolder.tv_follow_button.setTextColor(ContextCompat.getColor(activity, R.color.med_grey));
            venuePostViewHolder.tv_follow_button.setText(activity.getString(R.string.follow));
            venuePostViewHolder.tv_follow_button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_venue_unfollowed,
                    0, 0, 0);
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

        venuePostViewHolder.tv_activity_performed.setText(Html.fromHtml("" + userToShow + " "
                + activity.getString(R.string.shared) + " " + activity.getString(R.string.a_venue)));

    /*    if(currentItem.origin.iFollow==1){
            venuePostViewHolder.tv_follow_button.setTextColor(ContextCompat.getColor(activity,R.color.app_theme_medium));
        }
        else {
            venuePostViewHolder.tv_follow_button.setTextColor(ContextCompat.getColor(activity,R.color.med_grey));
        }*/

        venuePostViewHolder.tv_follow_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isGuestUser) {
                    commonFunctions.showLoginRequestDialog();
                } else {
                    newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                            AppKeys.VENUE_FOLLOW, "", "");
                }
            }
        });

        venuePostViewHolder.tv_share_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isGuestUser) {
                    commonFunctions.showLoginRequestDialog();
                } else {
                    newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                            AppKeys.VENUE_SHARE, "", "");
                }
            }
        });

        venuePostViewHolder.cv_post_wrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                        AppKeys.OPEN_VENUE, currentItem.originId, "");

                /*Intent venueIntent = new Intent(activity, VenueActivity.class);
                venueIntent.putExtra("id", currentItem.originId);
                activity.startActivity(venueIntent);*/
            }
        });

        venuePostViewHolder.popup.getMenu().clear();

        if (user_id.equals(currentItem.authorId)) {
            venuePostViewHolder.inflater.inflate(R.menu.news_feed_shared_interest_creator_menu,
                    venuePostViewHolder.popup.getMenu());
        } else {
            venuePostViewHolder.inflater.inflate(R.menu.news_feed_normal_post_other_menu,
                    venuePostViewHolder.popup.getMenu());
        }
        if (userFixedname == null) {
            String firstName = currentItem.postAuthorName;
            if (currentItem.postAuthorName.contains(" ")) {
                firstName = firstName.substring(0, firstName.indexOf(" "));
            }
            venuePostViewHolder.popup.getMenu().getItem(1)
                    .setTitle(activity.getString(R.string.unFollow) + " " + firstName);
        } else {
            venuePostViewHolder.popup.getMenu().removeItem(R.id.unfollow);
            venuePostViewHolder.popup.getMenu().removeItem(R.id.hide_post);
        }


        try {
            Field[] fields = venuePostViewHolder.popup.getClass().getDeclaredFields();
            for (Field field : fields) {
                if ("mPopup".equals(field.getName())) {
                    field.setAccessible(true);
                    Object menuPopupHelper = field.get(venuePostViewHolder.popup);
                    Class<?> classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());
                    Method setForceIcons = classPopupHelper.getMethod("setForceShowIcon", boolean.class);
                    setForceIcons.invoke(menuPopupHelper, true);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        venuePostViewHolder.popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.share_to:
                        newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                                AppKeys.SHARE_TO_VENUE, "", "");
                        break;

                    case R.id.delete:
                        newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                                AppKeys.POST_DELETE, "", "");
                        break;

                    case R.id.unfollow:
                        newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                                AppKeys.UNFOLLOW_USER, "", "");
                        break;

                    case R.id.report:
                        newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem, AppKeys.REPORT_VENUE, "", "");
                        break;
                    case R.id.hide_post:
                        newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem, AppKeys.HIDE, "", "");
                        break;
                }
                return false;
            }
        });


        if (currentItem.postComments != null && currentItem.postComments.size() > 0) {
            if (activity != null)
                Glide.with(activity.getApplicationContext()).load(currentItem.postComments.get(0).userPicture)
                        .apply(new RequestOptions().override(96, 96))
                        .into(venuePostViewHolder.iv_comment_dp);

            venuePostViewHolder.tv_comment_user_name.setText(currentItem.postComments.get(0)
                    .userFullname);

            venuePostViewHolder.tv_comment_user_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                            AppKeys.OPEN_PROFILE, currentItem.postComments.get(0).userId, "");

                    /*Intent profileIntent = new Intent(activity, ProfileInfoActivity.class);
                    profileIntent.putExtra("target_user_id", currentItem.postComments.get(0)
                            .userId);
                    activity.startActivity(profileIntent);*/
                }
            });
            venuePostViewHolder.tv_user_comment.setText(Html.fromHtml(currentItem.postComments.get(0).text));
        } else {
            venuePostViewHolder.rl_user_comment_wrapper.setVisibility(View.GONE);
        }

        if (hideHeader) {
            venuePostViewHolder.rl_top_bar_wrapper.setVisibility(View.GONE);
        }
        if (hideFooter) {
            venuePostViewHolder.rl_user_comment_wrapper.setVisibility(View.GONE);

        }
    }
}
