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
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

public class AssignedSessionNewsFeedItem extends NewsFeedItemBase {

    public AssignedSessionNewsFeedItem(Fragment fragment, String userFixedname) {
        super(fragment, userFixedname);
    }


    public AssignedSessionViewHolder getViewHolder(ViewGroup parent) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.assigned_session_post, parent, false);
        return new AssignedSessionViewHolder(itemView);
    }


    public class AssignedSessionViewHolder extends RecyclerView.ViewHolder {
        LinearLayout ll_share_data_wrapper;
        RelativeLayout rl_top_bar_wrapper;
        TextView tv_share_title;
        ImageView iv_post_sharer_user_dp;
        TextView tv_post_sharer_name;
        TextView tv_post_sharer_designation;
        TextView tv_post_sharer_stamp;
        CardView cv_post_wrapper;


        LinearLayout ll_target_click_wrapper;
        ImageView iv_session_photo;
        TextView tv_session_name;
        TextView tv_activity_performed;
        TextView tv_session_time_stamp;
        TextView tv_discussion_number;
        TextView tv_attending_number;
        TextView tv_attend_button;
        TextView tv_discussion_button;
        TextView tv_share_button;

        ImageView iv_more_options;
        PopupMenu popup;

        ImageView iv_comment_dp;
        TextView tv_comment_user_name, tv_user_comment;
        RelativeLayout rl_user_comment_wrapper;
        MenuInflater inflater;

        public AssignedSessionViewHolder(final View itemView) {
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
            iv_session_photo = (ImageView) itemView.findViewById(R.id.iv_session_photo);
            tv_session_name = (TextView) itemView.findViewById(R.id.tv_session_name);
            tv_activity_performed = (TextView) itemView.findViewById(R.id.tv_activity_performed);
            tv_session_time_stamp = (TextView) itemView.findViewById(R.id.tv_session_time_stamp);
            tv_discussion_number = (TextView) itemView.findViewById(R.id.tv_discussion_number);
            tv_attending_number = (TextView) itemView.findViewById(R.id.tv_attending_number);
            tv_attend_button = (TextView) itemView.findViewById(R.id.tv_attend_button);
            tv_discussion_button = (TextView) itemView.findViewById(R.id.tv_discussion_button);
            tv_share_button = (TextView) itemView.findViewById(R.id.tv_share_button);

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


    public void configAssignedSessionViewHolder(AssignedSessionViewHolder assignedSessionViewHolder,
                                                final int position, final PojoGetNewsFeedResponseData currentItem) {
        assignedSessionViewHolder.ll_share_data_wrapper.setVisibility(View.GONE);
        if (currentItem.postType.equals("session_shared")) {
            assignedSessionViewHolder.tv_share_title.setVisibility(View.VISIBLE);
            // assignedSessionViewHolder.ll_share_data_wrapper.setVisibility(View.VISIBLE);
            //  assignedSessionViewHolder.rl_top_bar_wrapper.setVisibility(View.GONE);

            assignedSessionViewHolder.tv_post_sharer_stamp.setText(commonFunctions
                    .parseNewsFeedItemTime(currentItem.time));
            if (currentItem.postTitle.isEmpty()) {
                assignedSessionViewHolder.tv_share_title.setVisibility(View.GONE);
            } else {
                assignedSessionViewHolder.tv_share_title.setVisibility(View.VISIBLE);
                assignedSessionViewHolder.tv_share_title.setText(Html.fromHtml(currentItem.postTitle));
            }
            assignedSessionViewHolder.tv_post_sharer_name.setText(currentItem.userFullname);
            assignedSessionViewHolder.tv_post_sharer_designation.setText(currentItem.userWorkTitle);
            assignedSessionViewHolder.tv_post_sharer_name.setOnClickListener(new View.OnClickListener() {
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
                            .into(assignedSessionViewHolder.iv_post_sharer_user_dp);
            }
            assignedSessionViewHolder.iv_post_sharer_user_dp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    assignedSessionViewHolder.tv_post_sharer_name.callOnClick();
                }
            });
        } else {
            assignedSessionViewHolder.tv_share_title.setVisibility(View.GONE);
            assignedSessionViewHolder.rl_top_bar_wrapper.setVisibility(View.VISIBLE);

            assignedSessionViewHolder.ll_share_data_wrapper.setVisibility(View.GONE);
        }

        assignedSessionViewHolder.cv_post_wrapper.setOnClickListener(new View.OnClickListener() {
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
                ImageDimensions dimen = commonFunctions.getScaledDimensions(displayMetrics, currentItem.origin.imgDimensions, AppKeys.SESSION);
                height = dimen.height;
                width = dimen.width;
                assignedSessionViewHolder.iv_session_photo.getLayoutParams().height = height;
                assignedSessionViewHolder.iv_session_photo.getLayoutParams().width = width;
            }

           /* CardView.LayoutParams layoutParams=(CardView.LayoutParams)articleViewHolder.iv_cover_photo.getLayoutParams();
            layoutParams.height=height;
            layoutParams.width=width;
            articleViewHolder.iv_cover_photo.setLayoutParams(layoutParams);*/

            if (activity != null) Glide.with(activity.getApplicationContext())
                    .load(currentItem.origin.coverPhoto)
                    .apply(new RequestOptions().override(width, height).placeholder(R.drawable.placeholder))
                    .into(assignedSessionViewHolder.iv_session_photo);

        }

        assignedSessionViewHolder.tv_session_name.setText(Html.fromHtml(currentItem.origin.title));

        assignedSessionViewHolder.tv_session_time_stamp.setText(commonFunctions
                .parseNewsFeedItemTime(currentItem.origin.modifiedOn));
        if (currentItem.origin.discussionsCnt.equals("0")) {
            assignedSessionViewHolder.tv_discussion_number.setVisibility(View.GONE);
        } else {
            assignedSessionViewHolder.tv_discussion_number.setVisibility(View.VISIBLE);
        }
        assignedSessionViewHolder.tv_discussion_number.setText(currentItem.origin.discussionsCnt + " "
                + activity.getString(R.string.discussions));

        if (currentItem.origin.totalAttends.equals("0")) {
            assignedSessionViewHolder.tv_attending_number.setVisibility(View.GONE);
        } else {
            assignedSessionViewHolder.tv_attending_number.setVisibility(View.VISIBLE);
        }
        assignedSessionViewHolder.tv_attending_number.setText(currentItem.origin.totalAttends + " "
                + activity.getString(R.string.attendees));


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
            assignedSessionViewHolder.tv_activity_performed.setText(Html.fromHtml(userToShow + " "
                    + isOrAre));

        } else if (currentItem.postType.equals("session_shared")) {
            assignedSessionViewHolder.tv_activity_performed.setText(Html.fromHtml(userToShow + " "
                    + activity.getString(R.string.shared)));
        } else if (currentItem.postType.equals("session")) {
            if (currentItem.origin.presentors.equals(currentItem.origin.createdBy.userId)) {
                assignedSessionViewHolder.tv_activity_performed.setText(Html.fromHtml(userToShow + " "
                        + activity.getString(R.string.is_going_to_conduct)));
            } else {
                assignedSessionViewHolder.tv_activity_performed.setText(Html.fromHtml(userToShow + " "
                        + activity.getString(R.string.initiated_session)));
            }
        }


        if (currentItem.origin.presentors.equals(user_id)) {
            assignedSessionViewHolder.tv_attend_button.setText(activity.getString(R.string.sharing));
            assignedSessionViewHolder.tv_attend_button.setTextColor(ContextCompat.getColor(activity,
                    R.color.app_theme_medium));
            assignedSessionViewHolder.tv_attend_button
                    .setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_attend_filled, 0, 0, 0);

        } else {
            if (currentItem.origin.iAttend == 1) {
                assignedSessionViewHolder.tv_attend_button.setText(activity.getString(R.string.attending));
                assignedSessionViewHolder.tv_attend_button.setTextColor(ContextCompat.getColor(activity,
                        R.color.app_theme_medium));
                assignedSessionViewHolder.tv_attend_button
                        .setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_attend_filled, 0, 0, 0);


            } else {
                assignedSessionViewHolder.tv_attend_button.setText(activity.getString(R.string.attend));
                assignedSessionViewHolder.tv_attend_button.setTextColor(ContextCompat.getColor(activity,
                        R.color.med_grey));
                assignedSessionViewHolder.tv_attend_button
                        .setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_attend_unfilled, 0, 0, 0);

            }
        }

        assignedSessionViewHolder.tv_attend_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isGuestUser) {
                    commonFunctions.showLoginRequestDialog();
                } else {
                    newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                            AppKeys.SESSION_ACTION, currentItem.origin.sessionsId, AppKeys.ATTEND);
                }

               /* Intent sessionIntent = new Intent(activity, SessionLoaderActivity.class);
                sessionIntent.putExtra("id", currentItem.origin.sessionsId);
                sessionIntent.putExtra("orientation", AppKeys.ATTEND);
                activity.startActivity(sessionIntent);*/

            }
        });

        assignedSessionViewHolder.tv_share_button.setOnClickListener(new View.OnClickListener() {
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

        assignedSessionViewHolder.popup.getMenu().clear();

        if (user_id.equals(currentItem.origin.createdBy.userId)) {
            assignedSessionViewHolder.inflater.inflate(R.menu.news_feed_upcoming_session_creator_menu,
                    assignedSessionViewHolder.popup.getMenu());
            assignedSessionViewHolder.popup.getMenu().removeItem(R.id.delete);
        } else if (user_id.equals(currentItem.authorId)) {
            assignedSessionViewHolder.inflater.inflate(R.menu.news_feed_upcoming_session_creator_menu,
                    assignedSessionViewHolder.popup.getMenu());
        } else {
            assignedSessionViewHolder.inflater.inflate(R.menu.news_feed_upcoming_session_other_menu,
                    assignedSessionViewHolder.popup.getMenu());

            String firstName = currentItem.postAuthorName;
            if (firstName.contains(" ")) {
                firstName = firstName.substring(0, firstName.indexOf(" "));
            }
            if (userFixedname != null) {
                assignedSessionViewHolder.popup.getMenu().removeItem(R.id.unfollow);
                assignedSessionViewHolder.popup.getMenu().removeItem(R.id.hide_post);
            } else {
                assignedSessionViewHolder.popup.getMenu().getItem(2)
                        .setTitle(activity.getString(R.string.unFollow) + " " + firstName);
            }
        }


        try {
            Field[] fields = assignedSessionViewHolder.popup.getClass().getDeclaredFields();
            for (Field field : fields) {
                if ("mPopup".equals(field.getName())) {
                    field.setAccessible(true);
                    Object menuPopupHelper = field.get(assignedSessionViewHolder.popup);
                    Class<?> classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());
                    Method setForceIcons = classPopupHelper.getMethod("setForceShowIcon", boolean.class);
                    setForceIcons.invoke(menuPopupHelper, true);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        assignedSessionViewHolder.popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
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
                        .into(assignedSessionViewHolder.iv_comment_dp);

            assignedSessionViewHolder.tv_comment_user_name.setText(currentItem.postComments.get(0)
                    .userFullname);

            assignedSessionViewHolder.tv_comment_user_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                            AppKeys.OPEN_PROFILE, currentItem.postComments.get(0).userId, "");

                    /*Intent profileIntent = new Intent(activity,
                            ProfileInfoActivity.class);
                    profileIntent.putExtra("target_user_id", currentItem.postComments.get(0).userId);
                    activity.startActivity(profileIntent);*/
                }
            });
            assignedSessionViewHolder.tv_user_comment.setText(Html.fromHtml(currentItem.postComments.get(0).text));

        } else {
            assignedSessionViewHolder.rl_user_comment_wrapper.setVisibility(View.GONE);
        }

        if (hideHeader) {
            assignedSessionViewHolder.rl_top_bar_wrapper.setVisibility(View.GONE);
        }
        if (hideFooter) {
            assignedSessionViewHolder.rl_user_comment_wrapper.setVisibility(View.GONE);

        }
    }

}
