package com.avadna.luneblaze.adapters.profileInfo;

import android.content.Context;
import android.content.Intent;

import androidx.recyclerview.widget.RecyclerView;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.activities.question.QuestionActivity;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.pojo.pojoProfileInfo.PojoProfileAnswers;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;

import java.util.List;

public class ProfileInfoAnswerListAdapter extends RecyclerView.Adapter<ProfileInfoAnswerListAdapter.MyViewHolder> {
    Context context;
    List<PojoProfileAnswers> questionsList;
    ApiInterface apiService;
    PreferenceUtils preferenceUtils;
    CommonFunctions commonFunctions;
    String user_id;
    boolean showProgress = true;


    public ProfileInfoAnswerListAdapter(Context context, List<PojoProfileAnswers> questionsList) {
        this.questionsList = questionsList;
        this.context = context;
        preferenceUtils = new PreferenceUtils(context);
        commonFunctions = new CommonFunctions(context);
        user_id = preferenceUtils.get_user_id();
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
    }

    public void showProgressBar(boolean status) {
        showProgress = status;
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        LinearLayout ll_data_wrapper;
        ProgressBar pb_loading_more;
        TextView tv_question;
        TextView tv_answer;
        TextView tv_ques_num_upvotes;
        TextView tv_num_upvotes;
        TextView tv_num_answers;
        TextView tv_num_replies;
        TextView tv_time_stamp;


        MyViewHolder(View view) {
            super(view);
            tv_question = (TextView) view.findViewById(R.id.tv_question);
            tv_answer = (TextView) view.findViewById(R.id.tv_answer);
            tv_ques_num_upvotes = (TextView) view.findViewById(R.id.tv_ques_num_upvotes);
            tv_num_upvotes = (TextView) view.findViewById(R.id.tv_num_upvotes);
            tv_num_answers = (TextView) view.findViewById(R.id.tv_num_answers);
            tv_num_replies = (TextView) view.findViewById(R.id.tv_num_replies);
            tv_time_stamp = (TextView) view.findViewById(R.id.tv_time_stamp);
            ll_data_wrapper = (LinearLayout) view.findViewById(R.id.ll_data_wrapper);
            pb_loading_more = (ProgressBar) view.findViewById(R.id.pb_loading_more);
        }
    }

    @Override
    public ProfileInfoAnswerListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.profile_answer_list_item, parent, false);

        return new ProfileInfoAnswerListAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ProfileInfoAnswerListAdapter.MyViewHolder holder, int position) {
        //   holder.tv_hierarchy_item.setText(hierarchyList.get(position));

        if (position == questionsList.size()) {
            holder.ll_data_wrapper.setVisibility(View.GONE);
            if (showProgress) {
                holder.pb_loading_more.setVisibility(View.VISIBLE);
            } else {
                holder.pb_loading_more.setVisibility(View.GONE);
            }
        } else {
            holder.ll_data_wrapper.setVisibility(View.VISIBLE);
            holder.pb_loading_more.setVisibility(View.GONE);

            final PojoProfileAnswers currentItem = questionsList.get(position);

            holder.tv_question.setText(Html.fromHtml(currentItem.question));
            holder.tv_answer.setText(Html.fromHtml(currentItem.post));

            if (currentItem.qaUpvotesCnt.equals("0")) {
                holder.tv_ques_num_upvotes.setVisibility(View.GONE);
            } else {
                holder.tv_ques_num_upvotes.setVisibility(View.VISIBLE);
            }
            holder.tv_ques_num_upvotes.setText(currentItem.qaUpvotesCnt + " " + context.getString(R.string.upvotes));


            if (currentItem.ansCnt.equals("0")) {
                holder.tv_num_answers.setVisibility(View.GONE);
            } else {
                holder.tv_num_answers.setVisibility(View.VISIBLE);
            }
            holder.tv_num_answers.setText(currentItem.ansCnt + " " + context.getString(R.string.answers));

            if (currentItem.ansUpvotesCnt.equals("0")) {
                holder.tv_num_upvotes.setVisibility(View.GONE);
            } else {
                holder.tv_num_upvotes.setVisibility(View.VISIBLE);
            }
            holder.tv_num_upvotes.setText(currentItem.ansUpvotesCnt + " " + context.getString(R.string.upvotes));

            if (currentItem.replyCount==0) {
                holder.tv_num_replies.setVisibility(View.GONE);
            } else {
                holder.tv_num_replies.setVisibility(View.VISIBLE);
            }
            holder.tv_num_replies.setText(currentItem.replyCount + " " + context.getString(R.string.replies));
            holder.tv_time_stamp.setText(commonFunctions.parseDateAndTimeToName(currentItem.addedOn));

            holder.ll_data_wrapper.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent questionIntent = new Intent(context, QuestionActivity.class);
                    questionIntent.putExtra("id", currentItem.parentQuestionId);
                    questionIntent.putExtra(AppKeys.ANSWER_ID, currentItem.sessionsQaId);
                    context.startActivity(questionIntent);
                }
            });
        }


    }

    @Override
    public int getItemCount() {

        return questionsList.size() + 1;

    }


}