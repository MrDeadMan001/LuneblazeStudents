package com.avadna.luneblaze.adapters;

import android.content.Context;
import android.content.Intent;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.activities.question.QuestionActivity;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.pojo.pojoInterest.PojoInterestQuestions;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;

import java.util.List;

/**
 * Created by Sunny on 27-12-2017.
 */

public class InterestRelatedQuestionListAdapter extends RecyclerView.Adapter<InterestRelatedQuestionListAdapter.MyViewHolder> {
    //  pivate List<String> hierarchyList;
    Context context;
    List<PojoInterestQuestions> questionsList;
    ApiInterface apiService;
    PreferenceUtils preferenceUtils;
    String user_id;
    InterestRelatedQuestionsAdapterCallback interestRelatedQuestionsAdapterCallback;

    boolean showProgressBar = true;
    boolean moreAvailable = true;


    public InterestRelatedQuestionListAdapter(Fragment fragment, List<PojoInterestQuestions> questionsList) {
        try {
            this.interestRelatedQuestionsAdapterCallback = ((InterestRelatedQuestionsAdapterCallback) fragment);
        } catch (ClassCastException e) {
            throw new ClassCastException("Fragment must implement interestRelatedQuestionListAdapterCallback.");
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
        CardView cv_parent;

        LinearLayout ll_data_wrapper;
        ProgressBar pb_loading_more;
        TextView tv_no_more_questions;


        String USER = "user";
        String OTHER = "other";
        String type = USER;

        MyViewHolder(View view) {
            super(view);
            cv_parent = (CardView) view.findViewById(R.id.cv_parent);
            tv_question = (TextView) view.findViewById(R.id.tv_question);
            tv_num_followers = (TextView) view.findViewById(R.id.tv_num_followers);
            tv_num_answers = (TextView) view.findViewById(R.id.tv_num_answers);
            tv_num_upvotes = (TextView) view.findViewById(R.id.tv_num_upvotes);
            pb_loading_more = (ProgressBar) view.findViewById(R.id.pb_loading_more);
            tv_no_more_questions = (TextView) view.findViewById(R.id.tv_no_more_questions);
            tv_no_more_questions.setBackgroundColor(ContextCompat.getColor(context, R.color.transparent));
            ll_data_wrapper = (LinearLayout) view.findViewById(R.id.ll_data_wrapper);
            tv_share = (TextView) view.findViewById(R.id.tv_share);
            tv_answer = (TextView) view.findViewById(R.id.tv_answer);
            tv_upvote = (TextView) view.findViewById(R.id.tv_upvote);

        }
    }

    @Override
    public InterestRelatedQuestionListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.interest_question_list_item, parent, false);

        return new InterestRelatedQuestionListAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final InterestRelatedQuestionListAdapter.MyViewHolder holder, final int position) {
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
                if (questionsList.isEmpty()) {
                    holder.tv_no_more_questions.setText(context.getString(R.string.no_questions_present));
                } else {
                    holder.tv_no_more_questions.setText(context.getString(R.string.no_more_questions));
                }
            }
        } else {
            holder.tv_no_more_questions.setVisibility(View.GONE);
            holder.pb_loading_more.setVisibility(View.GONE);
            holder.ll_data_wrapper.setVisibility(View.VISIBLE);

            final PojoInterestQuestions currentItem = questionsList.get(position);
            holder.tv_num_followers.setVisibility(View.GONE);

            holder.tv_question.setText(currentItem.post);

            if (currentItem.totalLikes.equals("0")) {
                holder.tv_num_upvotes.setVisibility(View.GONE);
            } else {
                holder.tv_num_upvotes.setVisibility(View.VISIBLE);
            }
            holder.tv_num_upvotes.setText(currentItem.totalLikes + " " + context.getString(R.string.likes));

            if (currentItem.totalAns == 0) {
                holder.tv_num_answers.setVisibility(View.GONE);
            } else {
                holder.tv_num_answers.setVisibility(View.VISIBLE);
            }
            holder.tv_num_answers.setText(currentItem.totalAns + " " + context.getString(R.string.answers));

            holder.tv_question.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent questionIntent = new Intent(context, QuestionActivity.class);
                    questionIntent.putExtra("id", currentItem.sessionsQaId);
                    context.startActivity(questionIntent);
                }
            });


            holder.tv_share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    interestRelatedQuestionsAdapterCallback.interestRelatedQuestionsMethodClick(position,
                            currentItem, AppKeys.SHARE_QUESTION);
                }
            });

            holder.tv_answer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    interestRelatedQuestionsAdapterCallback.interestRelatedQuestionsMethodClick(position,
                            currentItem, AppKeys.ADD_ANSWER);
                    // openAddAnswerDialog(currentItem);
                }
            });


            if (currentItem.iLike) {
                holder.tv_upvote.setTextColor(ContextCompat.getColor(context, R.color.app_theme_medium));
                holder.tv_upvote.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_thanks_filled, 0, 0, 0);

            } else {
                holder.tv_upvote.setTextColor(ContextCompat.getColor(context, R.color.med_grey));
                holder.tv_upvote.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_thanks_unfilled, 0, 0, 0);
            }
            holder.tv_upvote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    interestRelatedQuestionsAdapterCallback.interestRelatedQuestionsMethodClick(position,
                            currentItem, AppKeys.LIKE);

                }
            });

        }

    }

    private void openShareQuestionOnProfileDialog() {


    }

    @Override
    public int getItemCount() {
        return questionsList.size() + 1;
        //  return hierarchyList.size();
    }


    public static interface InterestRelatedQuestionsAdapterCallback {
        void interestRelatedQuestionsMethodClick(int position, PojoInterestQuestions pojoInterestQuestions,
                                                 String type);
    }
}

