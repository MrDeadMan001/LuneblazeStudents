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
import com.avadna.luneblaze.pojo.pojoNewsFeed.PojoGetNewsFeedResponseData;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

public class InterestNewsFeedItem extends NewsFeedItemBase{

    public InterestNewsFeedItem(Fragment fragment, String userFixedname) {
        super(fragment,userFixedname);
    }

    public InterestPostViewHolder getViewHolder(ViewGroup parent) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.interest_post, parent, false);
        return  new InterestPostViewHolder(itemView);
    }

    public class InterestPostViewHolder extends RecyclerView.ViewHolder {

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
        TextView tv_activity_performed;
        ImageView iv_cover_photo;
        TextView tv_interest_name;
        TextView tv_interest_parent;
        TextView tv_time_stamp;
        TextView tv_description;
        TextView tv_num_followers;
        TextView tv_num_sessions;
        TextView tv_num_articles;
        TextView tv_num_questions;
        TextView tv_share_button;
        TextView tv_follow_button;
        ImageView iv_comment_dp;
        TextView tv_comment_user_name, tv_user_comment;
        RelativeLayout rl_user_comment_wrapper;
        MenuInflater inflater;

        public InterestPostViewHolder(View itemView) {
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
            tv_activity_performed = (TextView) itemView.findViewById(R.id.tv_activity_performed);
            tv_interest_name = (TextView) itemView.findViewById(R.id.tv_interest_name);
            tv_interest_parent = (TextView) itemView.findViewById(R.id.tv_interest_parent);
            tv_time_stamp = (TextView) itemView.findViewById(R.id.tv_time_stamp);
            tv_description = (TextView) itemView.findViewById(R.id.tv_description);
            tv_num_followers = (TextView) itemView.findViewById(R.id.tv_num_followers);
            tv_num_sessions = (TextView) itemView.findViewById(R.id.tv_num_sessions);
            tv_num_articles = (TextView) itemView.findViewById(R.id.tv_num_articles);
            tv_num_questions = (TextView) itemView.findViewById(R.id.tv_num_questions);
            tv_share_button = (TextView) itemView.findViewById(R.id.tv_share_button);
            tv_follow_button = (TextView) itemView.findViewById(R.id.tv_follow_button);


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

    public void configInterestPostViewHolder( InterestPostViewHolder interestPostViewHolder,
                                              final int position,final PojoGetNewsFeedResponseData currentItem) {
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

        interestPostViewHolder.tv_activity_performed.setText(Html.fromHtml(userToShow + " "
                + activity.getString(R.string.shared_an_interest)));

        interestPostViewHolder.ll_share_data_wrapper.setVisibility(View.VISIBLE);
        interestPostViewHolder.tv_post_sharer_stamp.setText(commonFunctions
                .parseNewsFeedItemTime(currentItem.time));
        if (currentItem.postTitle.isEmpty()) {
            interestPostViewHolder.tv_share_title.setVisibility(View.GONE);
        } else {
            interestPostViewHolder.tv_share_title.setVisibility(View.VISIBLE);
            interestPostViewHolder.tv_share_title.setText(Html.fromHtml(currentItem.postTitle));
        }

        interestPostViewHolder.tv_post_sharer_name.setText(currentItem.userFullname);
        interestPostViewHolder.tv_post_sharer_designation.setText(currentItem.userWorkTitle);
        interestPostViewHolder.tv_post_sharer_name.setOnClickListener(new View.OnClickListener() {
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
                        .into(interestPostViewHolder.iv_post_sharer_user_dp);
        }

        interestPostViewHolder.iv_post_sharer_user_dp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                interestPostViewHolder.tv_post_sharer_name.callOnClick();
            }
        });

        if (!currentItem.origin.image.isEmpty()) {
            if (activity != null)
                Glide.with(activity.getApplicationContext()).load(currentItem.origin.image)
                        .apply(new RequestOptions().override(96, 96))
                        // .apply(bitmapTransform(new CropCircleTransformation()))
                        .into(interestPostViewHolder.iv_cover_photo);
        }

        interestPostViewHolder.tv_interest_name.setText(currentItem.origin.text);
        interestPostViewHolder.tv_interest_parent.setText(currentItem.origin.parent);
        interestPostViewHolder.tv_time_stamp.setText(commonFunctions.parseNewsFeedItemTime(currentItem.origin.time));
        interestPostViewHolder.tv_description.setText(Html.fromHtml(currentItem.origin.description));

        /*try {
            interestPostViewHolder.tv_description.post(new Runnable() {
                @Override
                public void run() {
                    int lines = interestPostViewHolder.tv_description.getLineCount();
                    int ellCount = interestPostViewHolder.tv_description.getLayout().getEllipsisCount(lines - 1);
                    if (ellCount > 0) {
                        String visibleText = interestPostViewHolder.tv_description.getText().toString();
                        visibleText = visibleText.substring(0, visibleText.length() - ellCount - 9);
                        int spanStart = visibleText.length() + 3;
                        visibleText = visibleText + "..." + activity.getString(R.string.more);
                        interestPostViewHolder.tv_description.setText(visibleText);
                        SpannableString spannableString = new SpannableString(interestPostViewHolder.tv_description.getText());
                        final ClickableSpan clickableSpan = new ClickableSpan() {
                            @Override
                            public void onClick(@NonNull View view) {
                                interestPostViewHolder.tv_description.setMaxLines(10000);
                                interestPostViewHolder.tv_description.setText(Html.fromHtml(currentItem.origin.description));
                                notifyDataSetChanged();
                            }
                        };
                        spannableString.setSpan(clickableSpan, spanStart, visibleText.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                        interestPostViewHolder.tv_description.setMovementMethod(LinkMovementMethod.getInstance());
                        interestPostViewHolder.tv_description.setText(spannableString, TextView.BufferType.SPANNABLE);
                    }
               *//* else if(lines>8){
                    String tempStr=currentItem.origin.description;

                    tempStr=tempStr+" "+activity.getString(R.string.show_less);

                    SpannableString tempSpannable=new SpannableString(tempStr);
                    ClickableSpan tempClickableSpan=new ClickableSpan() {
                        @Override
                        public void onClick(@NonNull View view) {
                            interestPostViewHolder.tv_description.setMaxLines(8);
                            interestPostViewHolder.tv_description.setText(Html.fromHtml(currentItem.origin.description));
                            notifyDataSetChanged();
                        }
                    };

                    tempSpannable.setSpan(tempClickableSpan,Html.fromHtml(currentItem.origin.description).length()+2,
                            tempSpannable.length(),Spanned.SPAN_INCLUSIVE_INCLUSIVE);

                    interestPostViewHolder.tv_description.setMovementMethod(LinkMovementMethod.getInstance());
                    interestPostViewHolder.tv_description.setText(tempSpannable, TextView.BufferType.SPANNABLE);
                }*//*

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }*/



       /* interestPostViewHolder.tv_description.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                try {
                    interestPostViewHolder.tv_description.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    int lines = interestPostViewHolder.tv_description.getLineCount();
                    Log.d("Line count:",""+lines);
                    int ellCount = interestPostViewHolder.tv_description.getLayout().getEllipsisCount(lines - 1);

                    if (ellCount > 0) {
                        String visibleText = interestPostViewHolder.tv_description.getText().toString();
                        visibleText = visibleText.substring(0, visibleText.length() - ellCount - 9);
                        int spanStart = visibleText.length() + 3;
                        visibleText = visibleText + "..." + activity.getString(R.string.more);
                        interestPostViewHolder.tv_description.setText(visibleText);
                        SpannableString spannableString = new SpannableString(interestPostViewHolder.tv_description.getText());
                        ClickableSpan clickableSpan = new ClickableSpan() {
                            @Override
                            public void onClick(@NonNull View view) {
                                interestPostViewHolder.tv_description.setMaxLines(10000);
                                interestPostViewHolder.tv_description.setText(Html.fromHtml(currentItem.origin.description));
                            }
                        };
                        spannableString.setSpan(clickableSpan, spanStart, visibleText.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                        interestPostViewHolder.tv_description.setMovementMethod(LinkMovementMethod.getInstance());
                        interestPostViewHolder.tv_description.setText(spannableString, TextView.BufferType.SPANNABLE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });*/

        if (currentItem.origin.iFollow == 1) {
            interestPostViewHolder.tv_follow_button.setTextColor(ContextCompat.getColor(activity, R.color.app_theme_medium));
            interestPostViewHolder.tv_follow_button.setText(activity.getString(R.string.following));
            interestPostViewHolder.tv_follow_button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_interest_follow_filled,
                    0, 0, 0);
        } else {
            interestPostViewHolder.tv_follow_button.setTextColor(ContextCompat.getColor(activity, R.color.med_grey));
            interestPostViewHolder.tv_follow_button.setText(activity.getString(R.string.follow));
            interestPostViewHolder.tv_follow_button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_interest_follow_unfilled,
                    0, 0, 0);
        }

        if (currentItem.origin.totalAttendes.equals("0")) {
            interestPostViewHolder.tv_num_followers.setVisibility(View.GONE);
        } else {
            interestPostViewHolder.tv_num_followers.setVisibility(View.VISIBLE);
        }
        interestPostViewHolder.tv_num_followers.setText("" + currentItem.origin.totalAttendes + " "
                + activity.getString(R.string.followers));
        interestPostViewHolder.tv_num_articles.setText("" + currentItem.origin.totalArticles);
        interestPostViewHolder.tv_num_questions.setText("" + currentItem.origin.totalQues);
        interestPostViewHolder.tv_num_sessions.setText("" + currentItem.origin.totalSessions);

        interestPostViewHolder.tv_share_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isGuestUser) {
                    commonFunctions.showLoginRequestDialog();
                } else {
                    newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                            AppKeys.INTEREST_SHARE, "", "");
                }
            }
        });

        interestPostViewHolder.tv_follow_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isGuestUser) {
                    commonFunctions.showLoginRequestDialog();
                } else {
                    newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                            AppKeys.INTEREST_FOLLOW, "", "");
                }
            }
        });

        interestPostViewHolder.popup.getMenu().clear();

        if (user_id.equals(currentItem.authorId)) {
            interestPostViewHolder.inflater.inflate(R.menu.news_feed_shared_interest_creator_menu,
                    interestPostViewHolder.popup.getMenu());
        } else {
            interestPostViewHolder.inflater.inflate(R.menu.news_feed_normal_post_other_menu,
                    interestPostViewHolder.popup.getMenu());
        }

        if (userFixedname == null) {
            String firstName = currentItem.postAuthorName;
            if (currentItem.postAuthorName.contains(" ")) {
                firstName = firstName.substring(0, firstName.indexOf(" "));
            }
            interestPostViewHolder.popup.getMenu().getItem(1)
                    .setTitle(activity.getString(R.string.unFollow) + " " + firstName);
        } else {
            interestPostViewHolder.popup.getMenu().removeItem(R.id.unfollow);
            interestPostViewHolder.popup.getMenu().removeItem(R.id.hide_post);
        }

        try {
            Field[] fields = interestPostViewHolder.popup.getClass().getDeclaredFields();
            for (Field field : fields) {
                if ("mPopup".equals(field.getName())) {
                    field.setAccessible(true);
                    Object menuPopupHelper = field.get(interestPostViewHolder.popup);
                    Class<?> classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());
                    Method setForceIcons = classPopupHelper.getMethod("setForceShowIcon", boolean.class);
                    setForceIcons.invoke(menuPopupHelper, true);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        interestPostViewHolder.popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.share_to:
                        newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                                AppKeys.SHARE_TO_INTEREST, "", "");
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
                        newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem, AppKeys.REPORT_INTEREST, "", "");
                        break;
                    case R.id.hide_post:
                        newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem, AppKeys.HIDE, "", "");
                        break;
                }
                return false;
            }
        });

        interestPostViewHolder.cv_post_wrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                        AppKeys.OPEN_INTEREST, currentItem.origin.interestId, "");

                /*Intent interestIntent = new Intent(activity, InterestActivity.class);
                interestIntent.putExtra("id", currentItem.origin.interestId);
                activity.startActivity(interestIntent);*/
            }
        });

        if (currentItem.postComments != null && currentItem.postComments.size() > 0) {
            if (activity != null)
                Glide.with(activity.getApplicationContext()).load(currentItem.postComments.get(0).userPicture)
                        .apply(new RequestOptions().override(96, 96))
                        .into(interestPostViewHolder.iv_comment_dp);

            interestPostViewHolder.tv_comment_user_name.setText(currentItem.postComments.get(0)
                    .userFullname);
            interestPostViewHolder.tv_comment_user_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                            AppKeys.OPEN_PROFILE, currentItem.postComments.get(0)
                                    .userId, "");

                    /*Intent profileIntent = new Intent(activity, ProfileInfoActivity.class);
                    profileIntent.putExtra("target_user_id", currentItem.postComments.get(0)
                            .userId);
                    activity.startActivity(profileIntent);*/
                }
            });
            interestPostViewHolder.tv_user_comment.setText(Html.fromHtml(currentItem.postComments.get(0).text));
        } else {
            interestPostViewHolder.rl_user_comment_wrapper.setVisibility(View.GONE);
        }

        if (hideHeader) {
            interestPostViewHolder.rl_top_bar_wrapper.setVisibility(View.GONE);
        }
        if (hideFooter) {
            interestPostViewHolder.rl_user_comment_wrapper.setVisibility(View.GONE);

        }

    }

}
