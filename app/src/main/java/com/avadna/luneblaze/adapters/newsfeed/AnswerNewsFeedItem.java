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
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.pojo.pojoNewsFeed.PojoGetNewsFeedResponseData;
import com.bumptech.glide.Glide;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

public class AnswerNewsFeedItem extends NewsFeedItemBase {

    public AnswerNewsFeedItem(Fragment fragment, String userFixedname) {
        super(fragment, userFixedname);
    }

    public AnswerViewHolder getViewHolder(ViewGroup parent) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.answer_newsfeed_item, parent, false);
        return new AnswerViewHolder(itemView);
    }

    public class AnswerViewHolder extends RecyclerView.ViewHolder {
        CardView cv_post_wrapper;
        LinearLayout rl_top_bar_wrapper;
        LinearLayout ll_target_click_wrapper;
        TextView tv_share_title;
        TextView tv_question;

        TextView tv_num_replies;
        TextView tv_num_upvotes;
        TextView tv_share_button;
        TextView tv_reply_button;
        TextView tv_upvote_button;
        TextView tv_activity_performed;
        LinearLayout ll_answer_holder;
        TextView tv_answer_time_stamp;

        ImageView iv_user_dp;

        TextView tv_user_name;
        TextView tv_answer;

        String USER = "user";
        String OTHER = "other";
        String type = USER;
        /*      List<String> user_options_list;
              List<String> other_options_list;
              ArrayAdapter<String> spinnerAdapter;*/
        ImageView iv_more_options;
        PopupMenu popup;
        MenuInflater inflater;


        public AnswerViewHolder(View itemView) {
            super(itemView);
            cv_post_wrapper = (CardView) itemView.findViewById(R.id.cv_post_wrapper);
            tv_share_title = (TextView) itemView.findViewById(R.id.tv_share_title);
            tv_activity_performed = (TextView) itemView.findViewById(R.id.tv_activity_performed);
            tv_question = (TextView) itemView.findViewById(R.id.tv_question);

            tv_num_replies = (TextView) itemView.findViewById(R.id.tv_num_replies);
            tv_num_upvotes = (TextView) itemView.findViewById(R.id.tv_num_upvotes);
            tv_share_button = (TextView) itemView.findViewById(R.id.tv_share_button);
            tv_upvote_button = (TextView) itemView.findViewById(R.id.tv_upvote_button);
            tv_reply_button = (TextView) itemView.findViewById(R.id.tv_reply_button);
            ll_answer_holder = (LinearLayout) itemView.findViewById(R.id.ll_answer_holder);

            iv_user_dp = (ImageView) itemView.findViewById(R.id.iv_user_dp);
            tv_user_name = (TextView) itemView.findViewById(R.id.tv_user_name);
            tv_answer = (TextView) itemView.findViewById(R.id.tv_answer);
            tv_answer_time_stamp = (TextView) itemView.findViewById(R.id.tv_answer_time_stamp);

            ll_target_click_wrapper = (LinearLayout) itemView.findViewById(R.id.ll_target_click_wrapper);
            rl_top_bar_wrapper = (LinearLayout) itemView.findViewById(R.id.rl_top_bar_wrapper);
            iv_more_options = (ImageView) itemView.findViewById(R.id.iv_more_options);
            if (user_id.equals(AppKeys.GUEST_USER_ID)) {
                iv_more_options.setVisibility(View.GONE);
            }
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
            inflater.inflate(R.menu.report_news_feed_item_menu, popup.getMenu());
        }
    }

    public void configAnswerViewHolder(AnswerViewHolder answerViewHolder,final int position,
                                       final PojoGetNewsFeedResponseData currentItem) {

        answerViewHolder.tv_question.setText(Html.fromHtml(currentItem.origin.post));

        if (currentItem.answer.totalLikes.equals("0")) {
            answerViewHolder.tv_num_upvotes.setVisibility(View.GONE);
        } else {
            answerViewHolder.tv_num_upvotes.setVisibility(View.VISIBLE);
        }
        answerViewHolder.tv_num_upvotes.setText("" + currentItem.answer.totalLikes + " "
                + activity.getString(R.string.upvotes));

        if (currentItem.origin.totalReplies == 0) {
            answerViewHolder.tv_num_replies.setVisibility(View.GONE);
        } else {
            answerViewHolder.tv_num_replies.setVisibility(View.VISIBLE);
        }

        answerViewHolder.tv_num_replies.setText("" + currentItem.origin.totalReplies + " "
                + activity.getString(R.string.replies));

        String userToShow;
        if (userFixedname == null) {
            userToShow = currentItem.userFullname;

        } else {
            userToShow = userFixedname;
        }
        userToShow = "<b>" + userToShow + "</b>";

        answerViewHolder.tv_activity_performed.setText(Html.fromHtml(userToShow + " "
                + activity.getString(R.string.shared_an_answer)));

        if (!currentItem.postTitle.isEmpty()) {
            answerViewHolder.tv_share_title.setVisibility(View.VISIBLE);
            answerViewHolder.tv_share_title.setText(currentItem.postTitle);
        } else {
            answerViewHolder.tv_share_title.setVisibility(View.GONE);
        }

        answerViewHolder.cv_post_wrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isGuestUser) {
                    commonFunctions.showLoginRequestDialog();
                } else {
                    newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                            AppKeys.ADD_ANSWER, currentItem.answer.parentQuestionId, currentItem.answer.sessionsQaId);
                }

               /* Intent questionIntent = new Intent(activity, QuestionActivity.class);
                questionIntent.putExtra(AppKeys.ID, currentItem.answer.parentQuestionId);
                questionIntent.putExtra(AppKeys.ANSWER_ID, currentItem.answer.sessionsQaId);
                questionIntent.putExtra(AppKeys.ACTION, AppKeys.ADD_ANSWER);
                activity.startActivity(questionIntent);*/
            }
        });

      /*  if (currentItem.origin.iFollow == 1) {
            answerViewHolder.tv_follow_button.setTextColor(ContextCompat.getColor(activity, R.color.app_theme_medium));
        } else {
            answerViewHolder.tv_follow_button.setTextColor(ContextCompat.getColor(activity, R.color.med_grey));
        }*/

        if (currentItem.answer.iLike == 1) {
            answerViewHolder.tv_upvote_button.setTextColor(ContextCompat.getColor(activity, R.color.app_theme_medium));
            answerViewHolder.tv_upvote_button.setText(activity.getString(R.string.upvoted));
            answerViewHolder.tv_upvote_button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_upvote_filled,
                    0, 0, 0);
        } else {
            answerViewHolder.tv_upvote_button.setTextColor(ContextCompat.getColor(activity, R.color.med_grey));
            answerViewHolder.tv_upvote_button.setText(activity.getString(R.string.upvote));
            answerViewHolder.tv_upvote_button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_upvote_unfilled,
                    0, 0, 0);
        }

        answerViewHolder.tv_upvote_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isGuestUser) {
                    commonFunctions.showLoginRequestDialog();
                } else {
                    newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                            AppKeys.ANSWER_UPVOTE, "", "");
                }
            }
        });

        answerViewHolder.tv_share_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isGuestUser) {
                    commonFunctions.showLoginRequestDialog();
                } else {
                    newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                            AppKeys.ANSWER_SHARE, "", "");
                }
            }
        });
        answerViewHolder.tv_reply_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isGuestUser) {
                    commonFunctions.showLoginRequestDialog();
                } else {
                    newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                            AppKeys.ANSWER_ACTION, currentItem.answer.sessionsQaId, AppKeys.ADD_REPLY);
                }

                /*Intent questionIntent = new Intent(activity, AnswerReplyListActivity.class);
                questionIntent.putExtra(AppKeys.ID, currentItem.answer.sessionsQaId);
                questionIntent.putExtra(AppKeys.ACTION, AppKeys.ADD_REPLY);
                activity.startActivity(questionIntent);*/

            }
        });

        /*if (currentItem.pinned) {
            answerViewHolder.popup.getMenu().getItem(0).setTitle(activity.getString(R.string.unpin));
        } else {
            answerViewHolder.popup.getMenu().getItem(0).setTitle(activity.getString(R.string.pin));
        }

        if (currentItem.iSave) {
            answerViewHolder.popup.getMenu().getItem(1).setTitle(activity.getString(R.string.unsave));
        } else {
            answerViewHolder.popup.getMenu().getItem(1).setTitle(activity.getString(R.string.save));
        }*/


        if (!currentItem.answer.createdBy.userPicture.isEmpty()) {
            if (activity != null)
                Glide.with(activity.getApplicationContext()).load(currentItem.answer.createdBy.userPicture)
                        .apply(bitmapTransform(new CropCircleTransformation()))
                        .into(answerViewHolder.iv_user_dp);
        }

        answerViewHolder.tv_user_name.setText(currentItem.answer.createdBy.userFullname);

        answerViewHolder.tv_user_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                        AppKeys.OPEN_PROFILE, currentItem.userId, "");

                /*Intent profileIntent = new Intent(activity,
                        ProfileInfoActivity.class);
                profileIntent.putExtra("target_user_id", currentItem.userId);
                activity.startActivity(profileIntent);*/
            }
        });

        answerViewHolder.tv_answer.setText(Html.fromHtml(currentItem.answer.post));
        answerViewHolder.tv_answer_time_stamp.setText(commonFunctions.parseNewsFeedItemTime(currentItem.answer.addedOn));

        answerViewHolder.popup.getMenu().clear();

        if (!user_id.equals(currentItem.authorId)) {
            answerViewHolder.inflater.inflate(R.menu.news_feed_normal_post_other_menu,
                    answerViewHolder.popup.getMenu());
            if (userFixedname == null) {
                String firstName = currentItem.postAuthorName;
                if (currentItem.postAuthorName.contains(" ")) {
                    firstName = firstName.substring(0, firstName.indexOf(" "));
                }
                answerViewHolder.popup.getMenu().getItem(1)
                        .setTitle(activity.getString(R.string.unFollow) + " " + firstName);
            } else {
                answerViewHolder.popup.getMenu().removeItem(R.id.unfollow);
                answerViewHolder.popup.getMenu().removeItem(R.id.hide_post);
            }
        } else {
            answerViewHolder.inflater.inflate(R.menu.news_feed_normal_post_creator_menu,
                    answerViewHolder.popup.getMenu());
            answerViewHolder.popup.getMenu().removeItem(R.id.edit);
        }

        try {
            Field[] fields = answerViewHolder.popup.getClass().getDeclaredFields();
            for (Field field : fields) {
                if ("mPopup".equals(field.getName())) {
                    field.setAccessible(true);
                    Object menuPopupHelper = field.get(answerViewHolder.popup);
                    Class<?> classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());
                    Method setForceIcons = classPopupHelper.getMethod("setForceShowIcon", boolean.class);
                    setForceIcons.invoke(menuPopupHelper, true);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        answerViewHolder.popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.edit:
                        newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                                AppKeys.EDIT_ANSWER, "", "");
                        break;

                    case R.id.delete:
                        newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                                AppKeys.DELETE_ANSWER, "", "");
                        break;

                    case R.id.share_to:
                        newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                                AppKeys.SHARE_TO_ANSWER, "", "");
                        break;

                    case R.id.unfollow:
                        newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                                AppKeys.UNFOLLOW_USER, "", "");
                        break;

                    case R.id.report:
                        newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                                AppKeys.REPORT_ANSWER, "", "");
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
            answerViewHolder.rl_top_bar_wrapper.setVisibility(View.GONE);
            // answerViewHolder.rl_user_comment_wrapper.setVisibility(View.GONE);
        }

    }


}
