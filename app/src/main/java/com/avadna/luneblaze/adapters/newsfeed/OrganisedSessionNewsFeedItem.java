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

public class OrganisedSessionNewsFeedItem extends NewsFeedItemBase{

    public OrganisedSessionNewsFeedItem(Fragment fragment, String userFixedname) {
        super(fragment,userFixedname);
    }

    public OrganisedSessionViewHolder getViewHolder(ViewGroup parent) {
       View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.organised_session_post, parent, false);
        return new OrganisedSessionViewHolder(itemView);
    }

    public class OrganisedSessionViewHolder extends RecyclerView.ViewHolder {
        CardView cv_post_wrapper;
        LinearLayout ll_share_data_wrapper;
        RelativeLayout rl_top_bar_wrapper;
        TextView tv_share_title;
        ImageView iv_post_sharer_user_dp;
        TextView tv_post_sharer_name;
        TextView tv_post_sharer_designation;
        TextView tv_post_sharer_stamp;

        LinearLayout ll_target_click_wrapper;
        ImageView iv_session_photo;
        TextView tv_activity_performed;
        TextView tv_session_name;
        TextView tv_session_time_stamp;
        TextView tv_thank_number;
        TextView tv_question_number;
        TextView tv_comment_number;
        TextView tv_thank_button;
        TextView tv_comment_button;
        TextView tv_question_button;
        TextView tv_share_button;

        ImageView iv_more_options;
        PopupMenu popup;

        ImageView iv_comment_dp;
        TextView tv_comment_user_name, tv_user_comment;
        RelativeLayout rl_user_comment_wrapper;
        MenuInflater inflater;

        public OrganisedSessionViewHolder(View itemView) {
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
            tv_activity_performed = (TextView) itemView.findViewById(R.id.tv_activity_performed);
            tv_session_name = (TextView) itemView.findViewById(R.id.tv_session_name);
            tv_session_time_stamp = (TextView) itemView.findViewById(R.id.tv_session_time_stamp);
            tv_thank_number = (TextView) itemView.findViewById(R.id.tv_thank_number);
            tv_question_number = (TextView) itemView.findViewById(R.id.tv_question_number);
            tv_comment_number = (TextView) itemView.findViewById(R.id.tv_comment_number);
            tv_thank_button = (TextView) itemView.findViewById(R.id.tv_thank_button);
            tv_comment_button = (TextView) itemView.findViewById(R.id.tv_comment_button);
            tv_question_button = (TextView) itemView.findViewById(R.id.tv_question_button);
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


    public void configOrganisedSessionViewHolder(OrganisedSessionViewHolder organisedSessionViewHolder,
                                                 final int position,final PojoGetNewsFeedResponseData currentItem) {
        organisedSessionViewHolder.ll_share_data_wrapper.setVisibility(View.GONE);
        if (currentItem.postType.equals("session_shared")) {
            organisedSessionViewHolder.tv_share_title.setVisibility(View.VISIBLE);

            //  organisedSessionViewHolder.ll_share_data_wrapper.setVisibility(View.VISIBLE);
            //  organisedSessionViewHolder.rl_top_bar_wrapper.setVisibility(View.GONE);

            organisedSessionViewHolder.tv_post_sharer_stamp.setText(commonFunctions
                    .parseNewsFeedItemTime(currentItem.time));
            if (currentItem.postTitle.isEmpty()) {
                organisedSessionViewHolder.tv_share_title.setVisibility(View.GONE);
            } else {
                organisedSessionViewHolder.tv_share_title.setVisibility(View.VISIBLE);
                organisedSessionViewHolder.tv_share_title.setText(Html.fromHtml(currentItem.postTitle));
            }
            organisedSessionViewHolder.tv_post_sharer_name.setText(currentItem.userFullname);
            organisedSessionViewHolder.tv_post_sharer_designation.setText(currentItem.userWorkTitle);
            organisedSessionViewHolder.tv_post_sharer_name.setOnClickListener(new View.OnClickListener() {
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
                            .into(organisedSessionViewHolder.iv_post_sharer_user_dp);
            }
            organisedSessionViewHolder.iv_post_sharer_user_dp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    organisedSessionViewHolder.tv_post_sharer_name.callOnClick();
                }
            });
        } else {
            organisedSessionViewHolder.tv_share_title.setVisibility(View.GONE);
            organisedSessionViewHolder.ll_share_data_wrapper.setVisibility(View.GONE);
            organisedSessionViewHolder.rl_top_bar_wrapper.setVisibility(View.VISIBLE);
        }

        organisedSessionViewHolder.cv_post_wrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                        AppKeys.OPEN_SESSION, currentItem.origin.sessionsId, "");

               /* Intent sessionIntent = new Intent(activity, SessionLoaderActivity.class);
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
                organisedSessionViewHolder.iv_session_photo.getLayoutParams().height = height;
                organisedSessionViewHolder.iv_session_photo.getLayoutParams().width = width;
            }

           /* CardView.LayoutParams layoutParams=(CardView.LayoutParams)articleViewHolder.iv_cover_photo.getLayoutParams();
            layoutParams.height=height;
            layoutParams.width=width;
            articleViewHolder.iv_cover_photo.setLayoutParams(layoutParams);*/

            if (activity != null) Glide.with(activity.getApplicationContext())
                    .load(currentItem.origin.coverPhoto)
                    .apply(new RequestOptions().override(width, height).placeholder(R.drawable.placeholder))
                    .into(organisedSessionViewHolder.iv_session_photo);

        }

        organisedSessionViewHolder.tv_session_name.setText(Html.fromHtml(currentItem.origin.title));

        organisedSessionViewHolder.tv_session_time_stamp.setText(commonFunctions
                .parseNewsFeedItemTime(currentItem.origin.modifiedOn));

        if (currentItem.origin.qaDataCnt.equals("0")) {
            organisedSessionViewHolder.tv_question_number.setVisibility(View.GONE);
        } else {
            organisedSessionViewHolder.tv_question_number.setVisibility(View.VISIBLE);
        }
        organisedSessionViewHolder.tv_question_number.setText(currentItem.origin.qaDataCnt + " "
                + activity.getString(R.string.questions));

        if (currentItem.origin.thanksCount.equals("0")) {
            organisedSessionViewHolder.tv_thank_number.setVisibility(View.GONE);
        } else {
            organisedSessionViewHolder.tv_thank_number.setVisibility(View.VISIBLE);
        }
        organisedSessionViewHolder.tv_thank_number.setText(currentItem.origin.thanksCount + " "
                + activity.getString(R.string.thanks));

        String userToShow;
        if (userFixedname == null) {
            userToShow = currentItem.userFullname;
        } else {
            userToShow = userFixedname;
        }

        userToShow = "<b>" + userToShow + "</b>";
        String isOrAre = activity.getString(R.string.attended);

        if (userFixedname == null) {
            int total = Integer.parseInt(currentItem.total);
            if (total > 0) {
                userToShow = userToShow + " " + activity.getString(R.string.and) + " " + currentItem.total +
                        " " + activity.getString(R.string.others);
                // isOrAre = activity.getString(R.string.are_attending);
            }
        }

        if (currentItem.postType.equals("session_attend")) {
            organisedSessionViewHolder.tv_activity_performed.setText(Html.fromHtml(userToShow + " "
                    + isOrAre));

        } else if (currentItem.postType.equals("session_shared")) {
            organisedSessionViewHolder.tv_activity_performed.setText(Html.fromHtml(userToShow + " "
                    + activity.getString(R.string.shared)));
        } else if (currentItem.postType.equals("session")) {
            organisedSessionViewHolder.tv_activity_performed.setText(Html.fromHtml(userToShow + " "
                    + activity.getString(R.string.initiated_session)));
        }

        organisedSessionViewHolder.tv_question_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isGuestUser) {
                    commonFunctions.showLoginRequestDialog();
                } else {
                    newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                            AppKeys.SESSION_ACTION, currentItem.originId, AppKeys.ASK_QUESTION);
                }

                /*Intent sessionLoaderIntent = new Intent(activity, SessionLoaderActivity.class);
                sessionLoaderIntent.putExtra("id", currentItem.originId);
                sessionLoaderIntent.putExtra("action", AppKeys.ASK_QUESTION);
                activity.startActivity(sessionLoaderIntent);*/
            }
        });


        if (currentItem.origin.iThanks == 1) {
            organisedSessionViewHolder.tv_thank_button.setTextColor(ContextCompat.getColor(activity,
                    R.color.app_theme_medium));
            organisedSessionViewHolder.tv_thank_button
                    .setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_thanks_filled, 0, 0, 0);
        } else {
            organisedSessionViewHolder.tv_thank_button.setTextColor(ContextCompat.getColor(activity,
                    R.color.med_grey));
            organisedSessionViewHolder.tv_thank_button
                    .setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_thanks_unfilled, 0, 0, 0);

        }

        organisedSessionViewHolder.tv_thank_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isGuestUser) {
                    commonFunctions.showLoginRequestDialog();
                } else {
                    newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                            AppKeys.THANK, "", "");
                }
            }
        });

        organisedSessionViewHolder.tv_share_button.setOnClickListener(new View.OnClickListener() {
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


        organisedSessionViewHolder.popup.getMenu().clear();
        if (user_id.equals(currentItem.origin.createdBy.userId)) {
            organisedSessionViewHolder.inflater.inflate(R.menu.news_feed_upcoming_session_creator_menu,
                    organisedSessionViewHolder.popup.getMenu());
            organisedSessionViewHolder.popup.getMenu().removeItem(R.id.delete);
        } else if (user_id.equals(currentItem.authorId)) {
            organisedSessionViewHolder.inflater.inflate(R.menu.news_feed_upcoming_session_creator_menu,
                    organisedSessionViewHolder.popup.getMenu());
        } else {
            organisedSessionViewHolder.inflater.inflate(R.menu.news_feed_upcoming_session_other_menu,
                    organisedSessionViewHolder.popup.getMenu());
            String firstName = currentItem.postAuthorName;
            if (firstName.contains(" ")) {
                firstName = firstName.substring(0, firstName.indexOf(" "));
            }
            if (userFixedname != null) {
                organisedSessionViewHolder.popup.getMenu().removeItem(R.id.unfollow);
                organisedSessionViewHolder.popup.getMenu().removeItem(R.id.hide_post);
            } else {
                organisedSessionViewHolder.popup.getMenu().getItem(2)
                        .setTitle(activity.getString(R.string.unFollow) + " " + firstName);
            }
        }


        try {
            Field[] fields = organisedSessionViewHolder.popup.getClass().getDeclaredFields();
            for (Field field : fields) {
                if ("mPopup".equals(field.getName())) {
                    field.setAccessible(true);
                    Object menuPopupHelper = field.get(organisedSessionViewHolder.popup);
                    Class<?> classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());
                    Method setForceIcons = classPopupHelper.getMethod("setForceShowIcon", boolean.class);
                    setForceIcons.invoke(menuPopupHelper, true);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        organisedSessionViewHolder.popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.invite:
                        newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                                AppKeys.INVITE_SESSION, "", "");
                        break;

                    case R.id.share_to:
                        newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                                AppKeys.SHARE_TO_SESSION, "", "");
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
                        .into(organisedSessionViewHolder.iv_comment_dp);

            organisedSessionViewHolder.tv_comment_user_name.setText(currentItem.postComments.get(0)
                    .userFullname);
            organisedSessionViewHolder.tv_user_comment.setText(Html.fromHtml(currentItem.postComments.get(0).text));
        } else {
            organisedSessionViewHolder.rl_user_comment_wrapper.setVisibility(View.GONE);
        }

        if (hideHeader) {
            organisedSessionViewHolder.rl_top_bar_wrapper.setVisibility(View.GONE);
        }
        if (hideFooter) {
            organisedSessionViewHolder.rl_user_comment_wrapper.setVisibility(View.GONE);

        }
    }



}
