package com.avadna.luneblaze.adapters.venue;

import android.app.Activity;
import android.app.Dialog;

import com.avadna.luneblaze.activities.question.QuestionActivity;
import com.avadna.luneblaze.activities.sessions.SessionLoaderActivity;
import com.avadna.luneblaze.adapters.UpvotersAndAttendeesListAdapter;
import com.avadna.luneblaze.helperClasses.MyCustomThemeDialog;

import android.content.Intent;
import android.graphics.Color;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.activities.profileInfo.ProfileInfoActivity;
import com.avadna.luneblaze.activities.question.AnswerReplyListActivity;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.helperClasses.SpannableClickItem;
import com.avadna.luneblaze.pojo.pojoQuestion.PojoAnswer;
import com.avadna.luneblaze.pojo.pojoQuestion.PojoQuestions;
import com.avadna.luneblaze.pojo.pojoQuestion.PojoUserData;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

/**
 * Created by Sunny on 17-01-2018.
 */

public class AnswerListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    //  pivate List<String> hierarchyList;
    Activity activity;
    List<String> more_options_list;
    ArrayAdapter<String> spinnerAdapter;

    PreferenceUtils preferenceUtils;
    CommonFunctions commonFunctions;
    String user_id;
    String creator_id;
    ApiInterface apiService;

    String USER = "user";
    String OTHER = "other";
    String type = USER;
    List<PojoAnswer> answerList;
    PojoQuestions questionData;


    AnswerListAdapterCallback answerListAdapterCallback;

    boolean showProgressBar = false;
    boolean moreNextAvailable = true;
    boolean morePreviousAvailable = true;
    boolean showBottomMoreButton = true;
    boolean showTopMoreButton = true;


    public AnswerListAdapter(Activity activity, List<PojoAnswer> answerList) {
        try {
            this.answerListAdapterCallback = ((AnswerListAdapterCallback) activity);
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement AnswerListAdapterCallback.");
        }
        this.activity = activity;
        this.answerList = answerList;
        preferenceUtils = new PreferenceUtils(activity);
        commonFunctions = new CommonFunctions(activity);
        user_id = preferenceUtils.get_user_id();
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
    }

    public void setShowProgressBar(boolean status) {
        showProgressBar = status;
        // notifyDataSetChanged();
    }

    public void setQuestionData(PojoQuestions questionData) {
        this.questionData = questionData;
        notifyDataSetChanged();
    }

    public void setMoreNextAvailable(boolean available) {
        moreNextAvailable = available;
    }

    public void setMorePreviousAvailable(boolean available) {
        morePreviousAvailable = available;
    }

    public void setShowBottomMoreButton(boolean show) {
        showBottomMoreButton = show;
    }

    public void setShowTopMoreButton(boolean show) {
        showTopMoreButton = show;
    }


    public void setCreator_id(String user_id) {
        this.creator_id = user_id;
        notifyDataSetChanged();
    }


    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return 1;
        } else {
            return 2;
        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        switch (viewType) {
            case 1:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.question_activity_top_item, parent, false);
                return new QuestionViewHolder(itemView);

            case 2:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.answer_list_item, parent, false);
                return new AnswerViewHolder(itemView);

            default:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.answer_list_item, parent, false);
                return new AnswerViewHolder(itemView);
        }
    }


    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        switch (holder.getItemViewType()) {
            case 1:
                QuestionViewHolder questionViewHolder = (QuestionViewHolder) holder;
                configQuestionViewHolder(questionViewHolder, position);
                break;

            case 2:
                AnswerViewHolder replyViewHolder = (AnswerViewHolder) holder;
                configAnswerViewHolder(replyViewHolder, position - 1);
                break;
        }

    }

    private void configAnswerViewHolder(AnswerViewHolder holder, int position) {
        if (position == answerList.size()) {
            holder.ll_content_wrapper.setVisibility(View.GONE);
            holder.tv_load_more_button.setText(activity.getString(R.string.load_more));

            if (!moreNextAvailable) {
                holder.tv_load_more_button.setText(activity.getString(R.string.no_more_available));
            }

            if (showProgressBar) {
                holder.pb_loading_more.setVisibility(View.VISIBLE);
                holder.tv_load_more_button.setVisibility(View.GONE);
            } else {
                holder.pb_loading_more.setVisibility(View.GONE);
                if (showBottomMoreButton) {
                    holder.tv_load_more_button.setVisibility(View.VISIBLE);
                } else {
                    holder.tv_load_more_button.setVisibility(View.GONE);
                }
            }

            holder.tv_load_more_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (moreNextAvailable) {

                        answerListAdapterCallback.answerListItemClickMethod(position, AppKeys.LOAD_NEXT);
                    }
                }
            });
        } else {
            holder.tv_load_more_button.setVisibility(View.GONE);
            holder.pb_loading_more.setVisibility(View.GONE);
            holder.ll_content_wrapper.setVisibility(View.VISIBLE);
            final PojoAnswer currentItem = answerList.get(position);

            holder.tv_answer.setHighlightColor(Color.TRANSPARENT); // prevent TextView change background when highlight
            holder.tv_answer.setMovementMethod(LinkMovementMethod.getInstance());
            SpannableClickItem spannableClickItem = commonFunctions.setClickSpans(currentItem.post,
                    currentItem.post, false, null);
            currentItem.clickableSpansList = spannableClickItem.clickableSpansList;
            holder.tv_answer
                    .setText(spannableClickItem.spannableString, TextView.BufferType.SPANNABLE);

            holder.tv_time_stamp.setText(commonFunctions.parseNewsFeedItemTime(currentItem.addedOn));
            holder.tv_user_name.setText(currentItem.userData.userFullname);

            holder.tv_user_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent profileIntent = new Intent(activity,
                            ProfileInfoActivity.class);
                    profileIntent.putExtra("target_user_id", currentItem.userId);
                    activity.startActivity(profileIntent);
                }
            });


            if (!currentItem.userData.userPicture.isEmpty()) {
                if (activity != null) Glide.with(activity.getApplicationContext())
                        .load(currentItem.userData.userPicture)
                        .apply(new RequestOptions().override(96, 96))
                        .apply(bitmapTransform(new CropCircleTransformation()))
                        .into(holder.iv_user_dp);
            }
            if (currentItem.total_replies.equals("0")) {
                holder.tv_num_comments.setVisibility(View.GONE);
            } else {
                holder.tv_num_comments.setVisibility(View.VISIBLE);
            }
            holder.tv_num_comments.setText(currentItem.total_replies+ " " + activity.getString(R.string.replies));

            if (currentItem.totalLikes == 0) {
                holder.tv_num_upvotes.setVisibility(View.GONE);
            } else {
                holder.tv_num_upvotes.setVisibility(View.VISIBLE);
            }
            holder.tv_num_upvotes.setText(currentItem.totalLikes + " " + activity.getString(R.string.upvotes));
            holder.tv_num_upvotes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    answerListAdapterCallback.answerListItemClickMethod(position,AppKeys.LIKE_COUNT);
                }
            });

            MenuInflater inflater = holder.popupMenu.getMenuInflater();
            holder.popupMenu.getMenu().clear();

            if (user_id.equals(currentItem.userId)) {
                inflater.inflate(R.menu.answer_popup_poster_menu, holder.popupMenu.getMenu());
            } else if (user_id.equals(creator_id)) {
                inflater.inflate(R.menu.answer_popup_creator_menu, holder.popupMenu.getMenu());
            } else {
                inflater.inflate(R.menu.answer_popup_other_menu, holder.popupMenu.getMenu());
            }

            try {
                Field[] fields = holder.popupMenu.getClass().getDeclaredFields();
                for (Field field : fields) {
                    if ("mPopup".equals(field.getName())) {
                        field.setAccessible(true);
                        Object menuPopupHelper = field.get(holder.popupMenu);
                        Class<?> classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());
                        Method setForceIcons = classPopupHelper.getMethod("setForceShowIcon", boolean.class);
                        setForceIcons.invoke(menuPopupHelper, true);
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            holder.popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.edit:
                            answerListAdapterCallback.answerListItemClickMethod(position,
                                    AppKeys.EDIT);
                            break;

                        case R.id.delete:
                            answerListAdapterCallback.answerListItemClickMethod(position,
                                    AppKeys.DELETE);
                            break;

                        case R.id.share_to:
                            answerListAdapterCallback.answerListItemClickMethod(position,
                                    AppKeys.SHARE_TO_ANSWER);


                            //shareTextUrl(answerList.get(position-1).sessionsQaId);
                            break;

                        case R.id.report:
                            answerListAdapterCallback.answerListItemClickMethod(position,
                                    AppKeys.REPORT_ANSWER);

                            // commonFunctions.openReportDialog(user_id, "report_answer", currentItem.sessionsQaId);
                            break;

                    }
                    return false;
                }
            });


            if (currentItem.sessionsQaId.isEmpty()) {
                holder.tv_reply.setOnClickListener(null);
                holder.ll_content_wrapper.setOnClickListener(null);
                holder.ll_content_wrapper.setAlpha(0.6f);
                holder.iv_more_options.setVisibility(View.GONE);
                holder.tv_answer.setOnClickListener(null);
            } else {
                holder.ll_content_wrapper.setAlpha(1f);
                holder.iv_more_options.setVisibility(View.VISIBLE);
                holder.tv_reply.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent replyIntent = new Intent(activity, AnswerReplyListActivity.class);
                        replyIntent.putExtra("answer_id", answerList.get(position).sessionsQaId);
                        replyIntent.putExtra(AppKeys.TARGET, AppKeys.ADD_REPLY);
                        activity.startActivity(replyIntent);
                    }
                });

                holder.ll_content_wrapper.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent replyIntent = new Intent(activity, AnswerReplyListActivity.class);
                        replyIntent.putExtra("answer_id", answerList.get(position).sessionsQaId);
                        activity.startActivity(replyIntent);

                    }
                });
                holder.tv_answer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        holder.ll_content_wrapper.callOnClick();
                    }
                });

            }


            if (currentItem.iLike) {
                holder.tv_upvote_button.setTextColor(ContextCompat.getColor(activity, R.color.app_theme_medium));
                holder.tv_upvote_button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_upvote_filled,
                        0, 0, 0);
                holder.tv_upvote_button.setText(R.string.upvoted);
            } else {
                holder.tv_upvote_button.setTextColor(ContextCompat.getColor(activity, R.color.med_grey));
                holder.tv_upvote_button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_upvote_unfilled,
                        0, 0, 0);
                holder.tv_upvote_button.setText(R.string.upvote);
            }

            holder.tv_upvote_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    answerListAdapterCallback.answerListItemClickMethod(position,
                            AppKeys.LIKE);
                }
            });

            holder.tv_share_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    answerListAdapterCallback.answerListItemClickMethod(position,
                            AppKeys.ANSWER_SHARED);
                }
            });
        }


    /*    if (position == (answerList.size() - 1)) {
            Resources r = activity.getResources();
            int DP64 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 56, r.getDisplayMetrics());
            int DP2 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, r.getDisplayMetrics());
            holder.ll_parent.setPadding(0, 0, 0, DP64);
        } else {
            holder.ll_parent.setPadding(0, 0, 0, 0);
        }*/

    }

    private void configQuestionViewHolder(QuestionViewHolder holder, int position) {

        final PojoQuestions currentItem = questionData;
        holder.tv_sort.setVisibility(View.GONE);
        holder.tv_question.setText(Html.fromHtml(currentItem.post.trim()));
        holder.tv_asked_by.setText(currentItem.userData.userFullname);
        holder.tv_session_name.setText(currentItem.sessionName);
        holder.tv_asked_by.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profileIntent = new Intent(activity, ProfileInfoActivity.class);
                profileIntent.putExtra(AppKeys.TARGET_USER_ID, currentItem.userData.userId);
                activity.startActivity(profileIntent);
            }
        });
        holder.tv_time_stamp.setText(commonFunctions.parseDateAndTimeToName(currentItem.modifiedOn));

        if (currentItem.totalLikes.equals("0")) {
            holder.tv_num_upvotes.setVisibility(View.GONE);
        } else {
            holder.tv_num_upvotes.setVisibility(View.VISIBLE);
        }
        holder.tv_num_upvotes.setText(currentItem.totalLikes + " " + activity.getString(R.string.upvotes));

        if (currentItem.totalAns == 0) {
            holder.tv_num_answers.setVisibility(View.GONE);
        } else {
            holder.tv_num_answers.setVisibility(View.VISIBLE);
        }
        holder.tv_num_answers.setText(currentItem.totalAns + " " + activity.getString(R.string.answers));

        holder.tv_num_answers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.tv_question.callOnClick();
            }
        });

        if (currentItem.iLike) {
            holder.tv_upvote_button.setTextColor(ContextCompat.getColor(activity, R.color.app_theme_medium));
            holder.tv_upvote_button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_upvote_filled,
                    0, 0, 0);
        } else {
            holder.tv_upvote_button.setTextColor(ContextCompat.getColor(activity, R.color.med_grey));
            holder.tv_upvote_button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_upvote_unfilled,
                    0, 0, 0);
        }
        holder.tv_upvote_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                answerListAdapterCallback.questionItemClickMethod(position, AppKeys.LIKE);
            }
        });

        holder.tv_share_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                answerListAdapterCallback.questionItemClickMethod(position, AppKeys.QUESTION_SHARE);
            }
        });

        holder.tv_num_upvotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                answerListAdapterCallback.questionItemClickMethod(position,AppKeys.LIKE_COUNT);
            }
        });

        holder.tv_answer_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                answerListAdapterCallback.questionItemClickMethod(position, AppKeys.ADD_ANSWER);
                // openAddAnswerDialog(position,"");
            }
        });

        holder.tv_session_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sessionIntent = new Intent(activity, SessionLoaderActivity.class);
                sessionIntent.putExtra("id", questionData.sessionsId);
                // sessionIntent.putExtra("type","question");
                activity.startActivity(sessionIntent);
                // openAddAnswerDialog(position,"");
            }
        });




         /*   if (position == (questionsList.size() - 1)) {
                Resources r = context.getResources();
                int DP64 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 64, r.getDisplayMetrics());
                int DP2 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, r.getDisplayMetrics());
                holder.rl_parent.setPadding(0, 0, 0, DP64);
            } else {
                holder.rl_parent.setPadding(0, 0, 0, 0);
            }*/
    }

    public class AnswerViewHolder extends RecyclerView.ViewHolder {
        //   Spinner sp_more_options;
        LinearLayout ll_content_wrapper;
        ImageView iv_user_dp;
        TextView tv_user_name;
        TextView tv_time_stamp;
        TextView tv_answer;
        TextView tv_num_upvotes;
        TextView tv_num_comments;
        TextView tv_upvote_button;
        TextView tv_reply;

        TextView tv_share_button;
        PopupMenu popupMenu;
        ImageView iv_more_options;

        ProgressBar pb_loading_more;
        TextView tv_load_more_button;


        AnswerViewHolder(View view) {
            super(view);
            ll_content_wrapper = (LinearLayout) view.findViewById(R.id.ll_content_wrapper);
            iv_user_dp = (ImageView) view.findViewById(R.id.iv_user_dp);
            //    sp_more_options = (Spinner) view.findViewById(R.id.sp_more_options);
            tv_user_name = (TextView) view.findViewById(R.id.tv_user_name);
            tv_time_stamp = (TextView) view.findViewById(R.id.tv_time_stamp);
            tv_answer = (TextView) view.findViewById(R.id.tv_answer);
            tv_num_upvotes = (TextView) view.findViewById(R.id.tv_num_upvotes);
            tv_num_comments = (TextView) view.findViewById(R.id.tv_num_comments);
            tv_upvote_button = (TextView) view.findViewById(R.id.tv_upvote_button);
            tv_reply = (TextView) view.findViewById(R.id.tv_reply);
            tv_share_button = (TextView) view.findViewById(R.id.tv_share_button);

            pb_loading_more = (ProgressBar) view.findViewById(R.id.pb_loading_more);
            tv_load_more_button = (TextView) view.findViewById(R.id.tv_load_more_button);

            iv_more_options = (ImageView) view.findViewById(R.id.iv_more_options);
            iv_more_options.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupMenu.show();
                }
            });
            popupMenu = new PopupMenu(activity, iv_more_options);

        }
    }

    public class QuestionViewHolder extends RecyclerView.ViewHolder {

        TextView tv_question;
        TextView tv_asked_by;
        TextView tv_session_name;
        TextView tv_time_stamp;
        TextView tv_sort;
        TextView tv_num_answers, tv_num_upvotes;
        TextView tv_share_button, tv_answer_button, tv_upvote_button;
        RelativeLayout rl_parent;


        String USER = "user";
        String OTHER = "other";
        String type = USER;

        QuestionViewHolder(View view) {
            super(view);
            tv_question = (TextView) view.findViewById(R.id.tv_question);
            tv_asked_by = (TextView) view.findViewById(R.id.tv_asked_by);
            tv_time_stamp = (TextView) view.findViewById(R.id.tv_time_stamp);
            tv_sort= (TextView) view.findViewById(R.id.tv_sort);
            tv_session_name = (TextView) view.findViewById(R.id.tv_session_name);
            tv_num_answers = (TextView) view.findViewById(R.id.tv_num_answers);
            tv_num_upvotes = (TextView) view.findViewById(R.id.tv_num_upvotes);
            tv_share_button = (TextView) view.findViewById(R.id.tv_share_button);
            tv_answer_button = (TextView) view.findViewById(R.id.tv_answer_button);
            tv_upvote_button = (TextView) view.findViewById(R.id.tv_upvote_button);
            rl_parent = (RelativeLayout) view.findViewById(R.id.rl_parent);
        }
    }

    @Override
    public int getItemCount() {
        if (answerList.isEmpty()) {
            if (questionData == null) {
                return 0;
            } else {
                return 1;
            }
        } else {
            return answerList.size() + 2;
        }
    }


    private void shareTextUrl(String sessionsQaId) {
        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/plain");
        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

        // Add data to the intent, the receiving app will decide
        // what to do with it.
        share.putExtra(Intent.EXTRA_SUBJECT, "Title Of The Post");
        String url = AppKeys.WEBSITE_URL + "question/19/#ans" + sessionsQaId;
        share.putExtra(Intent.EXTRA_TEXT, url);
        activity.startActivity(Intent.createChooser(share, "Share link!"));
    }


    public static interface AnswerListAdapterCallback {
        void answerListItemClickMethod(int position, String type);

        void questionItemClickMethod(int poistion, String type);
    }
}
