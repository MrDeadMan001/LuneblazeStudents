package com.avadna.luneblaze.adapters.newsfeed;

import android.os.Build;
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

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class QuestionNewsFeedItem extends NewsFeedItemBase {


    public QuestionNewsFeedItem(Fragment fragment, String userFixedname) {
        super(fragment, userFixedname);
    }

    public QuestionViewHolder getViewHolder(ViewGroup parent) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.question_list_item, parent, false);
        return new QuestionViewHolder(itemView);
    }

    public class QuestionViewHolder extends RecyclerView.ViewHolder {
        CardView cv_post_wrapper;
        // CardView cv_question_wrapper;
        View view_top_divider;
        LinearLayout ll_border_wrapper;

        LinearLayout rl_top_bar_wrapper;
        LinearLayout ll_target_click_wrapper;
        TextView tv_share_title;
        TextView tv_question;
        TextView tv_question_time_stamp;
        TextView tv_num_followers;
        TextView tv_num_answers;
        TextView tv_num_upvotes;
        TextView tv_share_button;
        TextView tv_answer_button;
        TextView tv_follow_button;
        TextView tv_activity_performed;
        LinearLayout ll_answer_holder;


        String USER = "user";
        String OTHER = "other";
        String type = USER;
        /*      List<String> user_options_list;
              List<String> other_options_list;
              ArrayAdapter<String> spinnerAdapter;*/
        ImageView iv_more_options;
        PopupMenu popup;
        MenuInflater inflater;

        //  Spinner sp_more_options;

        public QuestionViewHolder(View itemView) {
            super(itemView);

            cv_post_wrapper = (CardView) itemView.findViewById(R.id.cv_post_wrapper);
            // cv_question_wrapper = (CardView) itemView.findViewById(R.id.cv_question_wrapper);
            tv_share_title = (TextView) itemView.findViewById(R.id.tv_share_title);
            view_top_divider = (View) itemView.findViewById(R.id.view_top_divider);
            ll_border_wrapper = (LinearLayout) itemView.findViewById(R.id.ll_border_wrapper);
            tv_activity_performed = (TextView) itemView.findViewById(R.id.tv_activity_performed);
            tv_question = (TextView) itemView.findViewById(R.id.tv_question);
            tv_question_time_stamp = (TextView) itemView.findViewById(R.id.tv_question_time_stamp);
            tv_num_followers = (TextView) itemView.findViewById(R.id.tv_num_followers);
            tv_num_followers.setVisibility(View.GONE);
            tv_num_answers = (TextView) itemView.findViewById(R.id.tv_num_answers);
            tv_num_upvotes = (TextView) itemView.findViewById(R.id.tv_num_upvotes);
            tv_share_button = (TextView) itemView.findViewById(R.id.tv_share_button);
            tv_answer_button = (TextView) itemView.findViewById(R.id.tv_answer_button);
            tv_follow_button = (TextView) itemView.findViewById(R.id.tv_follow_button);
            ll_answer_holder = (LinearLayout) itemView.findViewById(R.id.ll_answer_holder);
            ll_target_click_wrapper = (LinearLayout) itemView.findViewById(R.id.ll_target_click_wrapper);
            rl_top_bar_wrapper = (LinearLayout) itemView.findViewById(R.id.rl_top_bar_wrapper);
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

    public void configQuestionViewHolder(QuestionViewHolder questionViewHolder,final int position,
                                         final PojoGetNewsFeedResponseData currentItem) {

        questionViewHolder.tv_share_title.setVisibility(View.GONE);
        questionViewHolder.tv_question.setText(Html.fromHtml(currentItem.origin.post));

        if (currentItem.origin.likes.equals("0")) {
            questionViewHolder.tv_num_upvotes.setVisibility(View.GONE);
        } else {
            questionViewHolder.tv_num_upvotes.setVisibility(View.VISIBLE);
        }
        questionViewHolder.tv_num_upvotes.setText("" + currentItem.origin.likes + " "
                + activity.getString(R.string.upvotes));

        if (currentItem.origin.totalAnswers == 0) {
            questionViewHolder.tv_num_answers.setVisibility(View.GONE);
        } else {
            questionViewHolder.tv_num_answers.setVisibility(View.VISIBLE);
        }
        questionViewHolder.tv_num_answers.setText("" + currentItem.origin.totalAnswers + " "
                + activity.getString(R.string.answers));

       /* if (currentItem.origin.followers == 0) {
            questionViewHolder.tv_num_followers.setVisibility(View.GONE);
        } else {
            questionViewHolder.tv_num_followers.setVisibility(View.VISIBLE);
        }
        questionViewHolder.tv_num_followers.setText("" + currentItem.origin.followers + " "
                + activity.getString(R.string.followers));*/

        questionViewHolder.ll_answer_holder.setVisibility(View.GONE);
        questionViewHolder.tv_question_time_stamp.setText(commonFunctions
                .parseNewsFeedItemTime(currentItem.origin.modifiedOn));

        String userToShow;
        if (userFixedname == null) {
            userToShow = currentItem.userFullname;
        } else {
            userToShow = userFixedname;
        }
        userToShow = "<b>" + userToShow + "</b>";
        if (currentItem.postType.equals("question_shared")) {
            questionViewHolder.tv_activity_performed.setText(Html.fromHtml(userToShow
                    + " " + activity.getString(R.string.shared_a_question)));
            if (!currentItem.postTitle.isEmpty()) {
                questionViewHolder.tv_share_title.setVisibility(View.VISIBLE);
                questionViewHolder.tv_share_title.setText(currentItem.postTitle);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                questionViewHolder.ll_target_click_wrapper.setElevation(4);
            }
            questionViewHolder.view_top_divider.setVisibility(View.GONE);

        } else {
            questionViewHolder.tv_activity_performed.setText(Html.fromHtml(userToShow
                    + " " + activity.getString(R.string.asked_question)));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                questionViewHolder.ll_target_click_wrapper.setElevation(0);
            }
            questionViewHolder.view_top_divider.setVisibility(View.VISIBLE);

        }

        questionViewHolder.cv_post_wrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                        AppKeys.OPEN_QUESTION, currentItem.origin.sessionsQaId, "");

                /*Intent intent = new Intent(activity, QuestionActivity.class);
                intent.putExtra("id", currentItem.origin.sessionsQaId);
                activity.startActivity(intent);*/
            }
        });

        if (currentItem.origin.iFollow == 1) {
            questionViewHolder.tv_follow_button.setTextColor(ContextCompat.getColor(activity, R.color.app_theme_medium));
        } else {
            questionViewHolder.tv_follow_button.setTextColor(ContextCompat.getColor(activity, R.color.med_grey));
        }

        questionViewHolder.tv_follow_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isGuestUser) {
                    commonFunctions.showLoginRequestDialog();
                } else {
                    newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                            AppKeys.QUESTION_FOLLOW, "", "");
                }
            }
        });

        questionViewHolder.tv_share_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isGuestUser) {
                    commonFunctions.showLoginRequestDialog();
                } else {
                    newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                            AppKeys.QUESTION_SHARE, "", "");
                }
            }
        });

        questionViewHolder.tv_answer_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isGuestUser) {
                    commonFunctions.showLoginRequestDialog();
                } else {
                    newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                            AppKeys.ADD_ANSWER, currentItem.origin.sessionsQaId, AppKeys.ADD_ANSWER);
                }

               /* Intent questionIntent = new Intent(activity, QuestionActivity.class);
                questionIntent.putExtra(AppKeys.ID, currentItem.origin.sessionsQaId);
                questionIntent.putExtra(AppKeys.TARGET, AppKeys.ADD_ANSWER);
                activity.startActivity(questionIntent);*/

            }
        });

        questionViewHolder.popup.getMenu().clear();

        if (user_id.equals(currentItem.authorId)) {
            questionViewHolder.inflater.inflate(R.menu.news_feed_normal_post_creator_menu,
                    questionViewHolder.popup.getMenu());
            if (!currentItem.postType.equals("question_shared")) {
                questionViewHolder.popup.getMenu().removeItem(R.id.edit);
            }

        } else {
            questionViewHolder.inflater.inflate(R.menu.news_feed_normal_post_other_menu,
                    questionViewHolder.popup.getMenu());
            if (userFixedname == null) {
                String firstName = currentItem.postAuthorName;
                if (currentItem.postAuthorName.contains(" ")) {
                    firstName = firstName.substring(0, firstName.indexOf(" "));
                }
                questionViewHolder.popup.getMenu().getItem(1)
                        .setTitle(activity.getString(R.string.unFollow) + " " + firstName);
            } else {
                questionViewHolder.popup.getMenu().removeItem(R.id.unfollow);
                questionViewHolder.popup.getMenu().removeItem(R.id.hide_post);
            }
        }


        try {
            Field[] fields = questionViewHolder.popup.getClass().getDeclaredFields();
            for (Field field : fields) {
                if ("mPopup".equals(field.getName())) {
                    field.setAccessible(true);
                    Object menuPopupHelper = field.get(questionViewHolder.popup);
                    Class<?> classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());
                    Method setForceIcons = classPopupHelper.getMethod("setForceShowIcon", boolean.class);
                    setForceIcons.invoke(menuPopupHelper, true);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        questionViewHolder.popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.edit:
                        newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                                AppKeys.EDIT_POST_TITLE, "", "");
                        break;

                    case R.id.delete:
                        newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                                AppKeys.DELETE_QUESTION, "", "");
                        break;

                    case R.id.share_to:
                        newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                                AppKeys.SHARE_TO_QUESTION, "", "");
                        break;

                    case R.id.unfollow:
                        newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                                AppKeys.UNFOLLOW_USER, "", "");
                        break;

                    case R.id.report:
                        newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                                AppKeys.REPORT_QUESTION, "", "");
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
            questionViewHolder.rl_top_bar_wrapper.setVisibility(View.GONE);
        }

    }
}
