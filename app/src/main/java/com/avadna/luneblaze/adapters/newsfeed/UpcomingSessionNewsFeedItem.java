package com.avadna.luneblaze.adapters.newsfeed;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
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
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

public class UpcomingSessionNewsFeedItem extends NewsFeedItemBase {

    public UpcomingSessionNewsFeedItem(Fragment fragment, String userFixedname) {
        super(fragment, userFixedname);
    }

    public UpcomingSessionViewHolder getViewHolder(ViewGroup parent) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.verified_session_post, parent, false);
        return new UpcomingSessionViewHolder(itemView);
    }

    public class UpcomingSessionViewHolder extends RecyclerView.ViewHolder {
        CardView cv_post_wrapper;
        LinearLayout ll_share_data_wrapper;
        RelativeLayout rl_top_bar_wrapper;
        TextView tv_share_title;
        ImageView iv_post_sharer_user_dp;
        TextView tv_post_sharer_name;
        TextView tv_post_sharer_designation;
        TextView tv_post_sharer_stamp;

        TextView tv_activity_performed, tv_accept_button, tv_attend_button;

        LinearLayout ll_target_click_wrapper;
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

        ImageView iv_comment_dp;
        TextView tv_comment_user_name, tv_user_comment;


        String USER = "user";
        String OTHER = "other";
        String type = USER;
        boolean finalised = true;
        //  Spinner sp_more_options;
        ImageView iv_more_options;
        MenuInflater inflater;
        PopupMenu popup;


        public UpcomingSessionViewHolder(View itemView) {
            super(itemView);
            cv_post_wrapper = (CardView) itemView.findViewById(R.id.cv_post_wrapper);
            ll_share_data_wrapper = (LinearLayout) itemView.findViewById(R.id.ll_share_data_wrapper);
            rl_top_bar_wrapper = (RelativeLayout) itemView.findViewById(R.id.rl_top_bar_wrapper);
            tv_share_title = (TextView) itemView.findViewById(R.id.tv_share_title);
            iv_post_sharer_user_dp = (ImageView) itemView.findViewById(R.id.iv_post_sharer_user_dp);
            tv_post_sharer_name = (TextView) itemView.findViewById(R.id.tv_post_sharer_name);
            tv_post_sharer_designation = (TextView) itemView.findViewById(R.id.tv_post_sharer_designation);
            tv_post_sharer_stamp = (TextView) itemView.findViewById(R.id.tv_post_sharer_stamp);

            ll_target_click_wrapper = (LinearLayout) itemView.findViewById(R.id.ll_target_click_wrapper);
            tv_activity_performed = (TextView) itemView.findViewById(R.id.tv_activity_performed);
            tv_accept_button = (TextView) itemView.findViewById(R.id.tv_accept_button);
            tv_attend_button = (TextView) itemView.findViewById(R.id.tv_attend_button);

            iv_session_photo = (ImageView) itemView.findViewById(R.id.iv_session_photo);
            tv_session_name = (TextView) itemView.findViewById(R.id.tv_session_name);
            tv_session_time_stamp = (TextView) itemView.findViewById(R.id.tv_session_time_stamp);
            tv_discussion_number = (TextView) itemView.findViewById(R.id.tv_discussion_number);
            tv_shares_number = (TextView) itemView.findViewById(R.id.tv_shares_number);
            tv_attending_number = (TextView) itemView.findViewById(R.id.tv_attending_number);
            tv_discuss_button = (TextView) itemView.findViewById(R.id.tv_discuss_button);
            tv_share_button = (TextView) itemView.findViewById(R.id.tv_share_button);

            //  sp_more_options = (Spinner) itemView.findViewById(R.id.sp_more_options);
            iv_more_options = (ImageView) itemView.findViewById(R.id.iv_more_options);
            if (isGuestUser) {
                iv_more_options.setVisibility(View.GONE);
            }
            fl_attend_button = (FrameLayout) itemView.findViewById(R.id.fl_attend_button);
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


    public void configUpcomingSessionViewHolder(UpcomingSessionViewHolder upcomingSessionViewHolder,
                                                final int position, PojoGetNewsFeedResponseData currentItem) {
        upcomingSessionViewHolder.ll_share_data_wrapper.setVisibility(View.GONE);
        if (currentItem.postType.equals("session_shared")) {
            //   upcomingSessionViewHolder.ll_share_data_wrapper.setVisibility(View.VISIBLE);
            //   upcomingSessionViewHolder.rl_top_bar_wrapper.setVisibility(View.GONE);
            upcomingSessionViewHolder.tv_share_title.setVisibility(View.VISIBLE);
            upcomingSessionViewHolder.tv_post_sharer_stamp.setText(commonFunctions
                    .parseNewsFeedItemTime(currentItem.time));
            if (currentItem.postTitle.isEmpty()) {
                upcomingSessionViewHolder.tv_share_title.setVisibility(View.GONE);
            } else {
                upcomingSessionViewHolder.tv_share_title.setVisibility(View.VISIBLE);
                upcomingSessionViewHolder.tv_share_title.setText(Html.fromHtml(currentItem.postTitle));
            }
            upcomingSessionViewHolder.tv_post_sharer_name.setText(currentItem.userFullname);
            upcomingSessionViewHolder.tv_post_sharer_designation.setText(currentItem.userWorkTitle);
            upcomingSessionViewHolder.tv_post_sharer_name.setOnClickListener(new View.OnClickListener() {
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
                            .into(upcomingSessionViewHolder.iv_post_sharer_user_dp);
            }
            upcomingSessionViewHolder.iv_post_sharer_user_dp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    upcomingSessionViewHolder.tv_post_sharer_name.callOnClick();
                }
            });
        } else {
            upcomingSessionViewHolder.tv_share_title.setVisibility(View.GONE);
            upcomingSessionViewHolder.rl_top_bar_wrapper.setVisibility(View.VISIBLE);
            upcomingSessionViewHolder.ll_share_data_wrapper.setVisibility(View.GONE);
        }

        upcomingSessionViewHolder.cv_post_wrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                        AppKeys.OPEN_SESSION, currentItem.origin.sessionsId, "");

                /*Intent sessionIntent = new Intent(activity, SessionLoaderActivity.class);
                sessionIntent.putExtra("id", currentItem.origin.sessionsId);
                activity.startActivity(sessionIntent);*/
            }
        });

        if (!currentItem.origin.coverPhoto.isEmpty()) {
            int height = imageFixedHeight;
            int width = imageFixedWidth;

            if (currentItem.origin.imgDimensions != null && !currentItem.origin.imgDimensions.isEmpty()) {
                ImageDimensions dimen = commonFunctions.getScaledDimensions(displayMetrics, currentItem.origin.imgDimensions, AppKeys.VERIFIED_SESSION);
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
                    .apply(new RequestOptions().override(width, height).placeholder(R.drawable.placeholder))
                    .into(upcomingSessionViewHolder.iv_session_photo);

        }

        upcomingSessionViewHolder.tv_session_name.setText(Html.fromHtml(currentItem.origin.title));
        upcomingSessionViewHolder.tv_session_time_stamp.setText(commonFunctions
                .parseNewsFeedItemTime(currentItem.origin.modifiedOn));
        if (currentItem.origin.discussionsCnt.equals("0")) {
            upcomingSessionViewHolder.tv_discussion_number.setVisibility(View.GONE);
        } else {
            upcomingSessionViewHolder.tv_discussion_number.setVisibility(View.VISIBLE);
        }
        upcomingSessionViewHolder.tv_discussion_number.setText(currentItem.origin.discussionsCnt + " "
                + activity.getString(R.string.discussions));

        if (currentItem.origin.totalAttends.equals("0")) {
            upcomingSessionViewHolder.tv_attending_number.setVisibility(View.GONE);
        } else {
            upcomingSessionViewHolder.tv_attending_number.setVisibility(View.VISIBLE);
        }
        upcomingSessionViewHolder.tv_attending_number.setText(currentItem.origin.totalAttends + " "
                + activity.getString(R.string.attendees));

        upcomingSessionViewHolder.tv_shares_number.setText(currentItem.origin.totalShares + " "
                + activity.getString(R.string.shares));

        String userToShow;
        if (userFixedname == null) {
            userToShow = currentItem.userFullname;
        } else {
            userToShow = userFixedname;
        }

        userToShow = "<b>" + userToShow + "</b>";
        String isOrAre = activity.getString(R.string.is_attending);
        if (userFixedname == null) {
            int total = Integer.parseInt(currentItem.total);
            if (total > 0) {
                userToShow = userToShow + " " + activity.getString(R.string.and) + " " + currentItem.total +
                        " " + activity.getString(R.string.others);
                isOrAre = activity.getString(R.string.are_attending);

            }
        }
        if (currentItem.postType.equals("session_attend")) {
            upcomingSessionViewHolder.tv_activity_performed.setText(Html.fromHtml(userToShow + " "
                    + isOrAre));
        } else if (currentItem.postType.equals("session_shared")) {
            upcomingSessionViewHolder.tv_activity_performed.setText(Html.fromHtml(userToShow + " "
                    + activity.getString(R.string.shared)));
        } else if (currentItem.postType.equals("session")) {
            if (currentItem.origin.presentors.equals(currentItem.origin.createdBy.userId)) {
                upcomingSessionViewHolder.tv_activity_performed.setText(Html.fromHtml(userToShow + " "
                        + activity.getString(R.string.is_going_to_conduct)));
            } else {
                upcomingSessionViewHolder.tv_activity_performed.setText(Html.fromHtml(userToShow + " "
                        + activity.getString(R.string.initiated_session)));
            }
        }

        upcomingSessionViewHolder.tv_discuss_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isGuestUser) {
                    commonFunctions.showLoginRequestDialog();
                } else {
                    newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                            AppKeys.DISCUSSION_SESSION, currentItem.originId, AppKeys.DISCUSSION);
                }

               /* Intent sessionLoaderIntent = new Intent(activity, SessionLoaderActivity.class);
                sessionLoaderIntent.putExtra("id", currentItem.originId);
                sessionLoaderIntent.putExtra("type", AppKeys.DISCUSSION);
                activity.startActivity(sessionLoaderIntent);*/
            }
        });

        upcomingSessionViewHolder.tv_accept_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isGuestUser) {
                    commonFunctions.showLoginRequestDialog();
                } else {
                    newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                            AppKeys.ACCEPT_SESSION, currentItem.originId, "");
                }

                /*Intent intent = new Intent(activity, SessionAcceptFormActivity.class);
                intent.putExtra("id",currentItem.originId);
                activity.startActivity(intent);*/
            }
        });

        upcomingSessionViewHolder.tv_share_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isGuestUser) {
                    commonFunctions.showLoginRequestDialog();
                } else {
                    newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                            AppKeys.SESSION_SHARE, "", "");
                }
            }
        });

        if (currentItem.origin.presentors.equals(user_id)) {
            upcomingSessionViewHolder.tv_attend_button.setText(activity.getString(R.string.sharing));
            upcomingSessionViewHolder.tv_attend_button.setTextColor(ContextCompat.getColor(activity,
                    R.color.app_theme_medium));
            upcomingSessionViewHolder.tv_attend_button
                    .setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_attend_filled, 0, 0, 0);

        } else {
            if (currentItem.origin.iAttend == 1) {
                upcomingSessionViewHolder.tv_attend_button.setText(activity.getString(R.string.attending));
                upcomingSessionViewHolder.tv_attend_button.setTextColor(ContextCompat.getColor(activity,
                        R.color.app_theme_medium));
                upcomingSessionViewHolder.tv_attend_button
                        .setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_attend_filled, 0, 0, 0);


            } else {
                upcomingSessionViewHolder.tv_attend_button.setText(activity.getString(R.string.attend));
                upcomingSessionViewHolder.tv_attend_button.setTextColor(ContextCompat.getColor(activity,
                        R.color.med_grey));
                upcomingSessionViewHolder.tv_attend_button
                        .setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_attend_unfilled, 0, 0, 0);

            }
        }

        upcomingSessionViewHolder.tv_attend_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isGuestUser) {
                    commonFunctions.showLoginRequestDialog();
                } else {
                    newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                            AppKeys.ACCEPT_SESSION, currentItem.origin.sessionsId, AppKeys.ATTEND);
                }

                /*Intent sessionIntent = new Intent(activity, SessionLoaderActivity.class);
                sessionIntent.putExtra("id", currentItem.origin.sessionsId);
                sessionIntent.putExtra("action", AppKeys.ATTEND);
                activity.startActivity(sessionIntent);*/

            }
        });


        upcomingSessionViewHolder.popup.getMenu().clear();

        if (user_id.equals(currentItem.origin.createdBy.userId)) {
            upcomingSessionViewHolder.inflater.inflate(R.menu.news_feed_upcoming_session_creator_menu,
                    upcomingSessionViewHolder.popup.getMenu());
            upcomingSessionViewHolder.popup.getMenu().removeItem(R.id.delete);
        } else if (user_id.equals(currentItem.authorId)) {
            upcomingSessionViewHolder.inflater.inflate(R.menu.news_feed_upcoming_session_creator_menu,
                    upcomingSessionViewHolder.popup.getMenu());
        } else {
            upcomingSessionViewHolder.inflater.inflate(R.menu.news_feed_upcoming_session_other_menu,
                    upcomingSessionViewHolder.popup.getMenu());

            String firstName = currentItem.postAuthorName;
            if (firstName.contains(" ")) {
                firstName = firstName.substring(0, firstName.indexOf(" "));
            }
            if (userFixedname != null) {
                upcomingSessionViewHolder.popup.getMenu().removeItem(R.id.unfollow);
                upcomingSessionViewHolder.popup.getMenu().removeItem(R.id.hide_post);
            } else {
                upcomingSessionViewHolder.popup.getMenu().getItem(2)
                        .setTitle(activity.getString(R.string.unFollow) + " " + firstName);
            }

        }

        try {
            Field[] fields = upcomingSessionViewHolder.popup.getClass().getDeclaredFields();
            for (Field field : fields) {
                if ("mPopup".equals(field.getName())) {
                    field.setAccessible(true);
                    Object menuPopupHelper = field.get(upcomingSessionViewHolder.popup);
                    Class<?> classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());
                    Method setForceIcons = classPopupHelper.getMethod("setForceShowIcon", boolean.class);
                    setForceIcons.invoke(menuPopupHelper, true);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        upcomingSessionViewHolder.popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.invite:
                        newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                                AppKeys.INVITE_SESSION, "", "");
                        break;

                    case R.id.delete:
                        newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                                AppKeys.POST_DELETE, "", "");
                        break;

                    case R.id.share_to:
                        newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                                AppKeys.SHARE_TO_SESSION, "", "");
                        break;

                    case R.id.unfollow:
                        newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                                AppKeys.UNFOLLOW_USER, "", "");
                        break;

                    case R.id.report:
                        newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem, AppKeys.REPORT_SESSION, "", "");
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
                        .into(upcomingSessionViewHolder.iv_comment_dp);

            upcomingSessionViewHolder.tv_comment_user_name.setText(currentItem.postComments.get(0)
                    .userFullname);

            upcomingSessionViewHolder.tv_comment_user_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                            AppKeys.OPEN_PROFILE, currentItem.postComments.get(0).userId, "");


                    /*Intent profileIntent = new Intent(activity, ProfileInfoActivity.class);
                    profileIntent.putExtra("target_user_id", currentItem.postComments.get(0).userId);
                    activity.startActivity(profileIntent);*/
                }
            });
            upcomingSessionViewHolder.tv_user_comment.setText(Html.fromHtml(currentItem.postComments.get(0).text));
        } else {
            upcomingSessionViewHolder.rl_user_comment_wrapper.setVisibility(View.GONE);
        }

        if (hideHeader) {
            upcomingSessionViewHolder.rl_top_bar_wrapper.setVisibility(View.GONE);
        }
        if (hideFooter) {
            upcomingSessionViewHolder.rl_user_comment_wrapper.setVisibility(View.GONE);

        }

    }


}
