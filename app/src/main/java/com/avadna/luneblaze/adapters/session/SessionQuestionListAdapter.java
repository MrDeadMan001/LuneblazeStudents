package com.avadna.luneblaze.adapters.session;

import android.content.Context;
import android.content.Intent;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.activities.question.QuestionActivity;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.pojo.pojoSessionDetails.PojoSessionQuestions;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;

import java.util.List;

public class SessionQuestionListAdapter extends RecyclerView.Adapter<SessionQuestionListAdapter.MyViewHolder> {
    //  pivate List<String> hierarchyList;
    int listSize;
    Context context;
    List<PojoSessionQuestions> questionsList;
    ApiInterface apiService;
    PreferenceUtils preferenceUtils;
    String user_id;


    EditText et_content;
    BottomSheetDialog addAnswerdialog;
    boolean showProgressBar = false;
    boolean moreAvailable = true;
    SessionQuestionListAdapterCallback sessionQuestionListAdapterCallback;

    public SessionQuestionListAdapter(Fragment fragment, List<PojoSessionQuestions> questionsList) {
        try {
            this.sessionQuestionListAdapterCallback = ((SessionQuestionListAdapterCallback) fragment);
        } catch (ClassCastException e) {
            throw new ClassCastException("Fragment must implement SessionQuestionListAdapterCallback.");
        }
        this.questionsList = questionsList;
        this.context = fragment.getContext();
        preferenceUtils = new PreferenceUtils(context);
        user_id = preferenceUtils.get_user_id();
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
    }

    public void setShowProgressBar(boolean status) {
        showProgressBar = status;
        notifyDataSetChanged();
    }

    public void setMoreAvailable(boolean more) {
        moreAvailable = more;
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tv_question;
        TextView tv_num_followers, tv_num_answers, tv_num_upvotes;
        TextView tv_share, tv_answer, tv_upvote;
        RelativeLayout rl_parent;
        LinearLayout ll_data_wrapper,ll_action_button_wrapper;

        ProgressBar pb_loading_more;
        TextView tv_no_more_questions;

        String USER = "user";
        String OTHER = "other";
        String type = USER;

        MyViewHolder(View view) {
            super(view);
            tv_question = (TextView) view.findViewById(R.id.tv_question);
            tv_num_followers = (TextView) view.findViewById(R.id.tv_num_followers);
            tv_num_answers = (TextView) view.findViewById(R.id.tv_num_answers);
            tv_num_upvotes = (TextView) view.findViewById(R.id.tv_num_upvotes);
            tv_share = (TextView) view.findViewById(R.id.tv_share);
            tv_answer = (TextView) view.findViewById(R.id.tv_answer);
            tv_upvote = (TextView) view.findViewById(R.id.tv_upvote);
            rl_parent = (RelativeLayout) view.findViewById(R.id.rl_parent);
            ll_data_wrapper = (LinearLayout) view.findViewById(R.id.ll_data_wrapper);
            ll_action_button_wrapper= (LinearLayout) view.findViewById(R.id.ll_action_button_wrapper);
            pb_loading_more = (ProgressBar) view.findViewById(R.id.pb_loading_more);
            tv_no_more_questions = (TextView) view.findViewById(R.id.tv_no_more_questions);
            pb_loading_more.setVisibility(View.GONE);
        }
    }

    @Override
    public SessionQuestionListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.interest_question_list_item, parent, false);

        return new SessionQuestionListAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final SessionQuestionListAdapter.MyViewHolder holder, final int position) {
        //   holder.tv_hierarchy_item.setText(hierarchyList.get(position));
        if (position == questionsList.size()) {
            if (showProgressBar) {
                holder.pb_loading_more.setVisibility(View.VISIBLE);
                holder.ll_data_wrapper.setVisibility(View.GONE);
                holder.tv_no_more_questions.setVisibility(View.GONE);

            } else if (!moreAvailable) {
                holder.ll_data_wrapper.setVisibility(View.GONE);
                holder.pb_loading_more.setVisibility(View.GONE);
                holder.tv_no_more_questions.setVisibility(View.VISIBLE);
            }
        } else {

            holder.tv_no_more_questions.setVisibility(View.GONE);
            holder.pb_loading_more.setVisibility(View.GONE);
            holder.ll_data_wrapper.setVisibility(View.VISIBLE);
            final PojoSessionQuestions currentItem = questionsList.get(position);

            if(currentItem.sessionsQaId.equals("")){
                holder.ll_action_button_wrapper.setVisibility(View.GONE);
            }
            else {
                holder.ll_action_button_wrapper.setVisibility(View.VISIBLE);
            }
            holder.tv_num_followers.setVisibility(View.GONE);

            holder.tv_question.setText(Html.fromHtml(currentItem.post.trim()));

            if (currentItem.totalLikes.equals("0")) {
                holder.tv_num_upvotes.setVisibility(View.GONE);
            } else {
                holder.tv_num_upvotes.setVisibility(View.VISIBLE);
            }
            holder.tv_num_upvotes.setText(currentItem.totalLikes + " " + context.getString(R.string.upvotes));

            if (currentItem.totalAns == 0) {
                holder.tv_num_answers.setVisibility(View.GONE);
            } else {
                holder.tv_num_answers.setVisibility(View.VISIBLE);
            }
            holder.tv_num_answers.setText(currentItem.totalAns + " " + context.getString(R.string.answers));

            holder.tv_question.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (currentItem.sessionsQaId != null && !currentItem.sessionsQaId.isEmpty()) {
                        Intent questionIntent = new Intent(context, QuestionActivity.class);
                        questionIntent.putExtra("id", currentItem.sessionsQaId);
                        context.startActivity(questionIntent);
                    }
                }
            });


            holder.tv_answer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (currentItem.sessionsQaId != null && !currentItem.sessionsQaId.isEmpty()) {
                        Intent questionIntent = new Intent(context, QuestionActivity.class);
                        questionIntent.putExtra(AppKeys.ID, currentItem.sessionsQaId);
                        questionIntent.putExtra(AppKeys.TARGET, AppKeys.ADD_ANSWER);
                        context.startActivity(questionIntent);
                    }
                    // openAddAnswerDialog(position,"");
                }
            });

            holder.tv_num_answers.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.tv_question.callOnClick();
                }
            });

            if (currentItem.iLike) {
                holder.tv_upvote.setTextColor(ContextCompat.getColor(context, R.color.app_theme_medium));
                holder.tv_upvote.setText(R.string.upvoted);
                holder.tv_upvote.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_upvote_filled,
                        0,0,0);
            } else {
                holder.tv_upvote.setTextColor(ContextCompat.getColor(context, R.color.med_grey));
                holder.tv_upvote.setText(R.string.upvote);
                holder.tv_upvote.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_upvote_unfilled,
                        0,0,0);
            }
            holder.tv_upvote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (currentItem.sessionsQaId != null && !currentItem.sessionsQaId.isEmpty()) {
                        sessionQuestionListAdapterCallback.SessionQuestionListItemClickCallback(position,
                                currentItem, AppKeys.LIKE);
                    }
                }
            });

            holder.tv_share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (currentItem.sessionsQaId != null && !currentItem.sessionsQaId.isEmpty()) {
                        sessionQuestionListAdapterCallback.SessionQuestionListItemClickCallback(position,
                                currentItem, AppKeys.QUESTION_SHARE);
                    }
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

    }

    @Override
    public int getItemCount() {
        if (showProgressBar) {
            return questionsList.size() + 1;
        } else {
            return questionsList.size();
        }
        //  return hierarchyList.size();
    }


    public static interface SessionQuestionListAdapterCallback {
        void SessionQuestionListItemClickCallback(int position, PojoSessionQuestions question, String type);
    }
}

