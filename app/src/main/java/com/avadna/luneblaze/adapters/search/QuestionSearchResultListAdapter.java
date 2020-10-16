package com.avadna.luneblaze.adapters.search;

import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.activities.question.QuestionActivity;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;

import com.avadna.luneblaze.pojo.pojoSearch.PojoSearchQa;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;

import java.util.List;


public class QuestionSearchResultListAdapter extends RecyclerView.Adapter<QuestionSearchResultListAdapter.MyViewHolder> {
    Context context;
    List<PojoSearchQa> questionsList;
    ApiInterface apiService;
    PreferenceUtils preferenceUtils;
    String user_id;
    boolean showProgress = true;


    public QuestionSearchResultListAdapter(Context context, List<PojoSearchQa> questionsList) {
        this.questionsList = questionsList;
        this.context = context;
        preferenceUtils = new PreferenceUtils(context);
        user_id = preferenceUtils.get_user_id();
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
    }

    private boolean showProgressBar = false;
    private boolean showNoMoreResults = false;

    public void setShowProgressBar(boolean status) {
        showProgressBar = status;
        notifyDataSetChanged();
    }

    public void setShowNoMoreResults(boolean status) {
        showNoMoreResults = status;
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_question;
        TextView tv_num_followers, tv_num_answers, tv_num_upvotes;
        RelativeLayout rl_parent;

        LinearLayout ll_content_wrapper;
        ProgressBar pb_loading_more;
        TextView tv_no_more_results;

        MyViewHolder(View view) {
            super(view);
            tv_question = (TextView) view.findViewById(R.id.tv_question);
            tv_num_followers = (TextView) view.findViewById(R.id.tv_num_followers);
            tv_num_answers = (TextView) view.findViewById(R.id.tv_num_answers);
            tv_num_upvotes = (TextView) view.findViewById(R.id.tv_num_upvotes);
            rl_parent = (RelativeLayout) view.findViewById(R.id.rl_parent);
            pb_loading_more = (ProgressBar) view.findViewById(R.id.pb_loading_more);

            ll_content_wrapper = (LinearLayout) view.findViewById(R.id.ll_content_wrapper);
            pb_loading_more = (ProgressBar) view.findViewById(R.id.pb_loading_more);
            tv_no_more_results = (TextView) view.findViewById(R.id.tv_no_more_results);
        }
    }

    @Override
    public QuestionSearchResultListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.question_search_result_list_item, parent, false);

        return new QuestionSearchResultListAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final QuestionSearchResultListAdapter.MyViewHolder holder, int position) {
        //   holder.tv_hierarchy_item.setText(hierarchyList.get(position));
        if (position == questionsList.size()) {
            holder.ll_content_wrapper.setVisibility(View.GONE);
            if (showProgressBar) {
                holder.pb_loading_more.setVisibility(View.VISIBLE);
                holder.tv_no_more_results.setVisibility(View.GONE);
            } else {
                holder.pb_loading_more.setVisibility(View.GONE);
            }

            if (showNoMoreResults) {
                if (questionsList.isEmpty()) {
                    holder.tv_no_more_results.setText(context.getString(R.string.no_results_found));
                } else {
                    holder.tv_no_more_results.setText(context.getString(R.string.no_more_results_found));
                }
                holder.tv_no_more_results.setVisibility(View.VISIBLE);
                holder.pb_loading_more.setVisibility(View.GONE);
            } else {
                holder.tv_no_more_results.setVisibility(View.GONE);
            }
        } else {
            holder.ll_content_wrapper.setVisibility(View.VISIBLE);
            holder.pb_loading_more.setVisibility(View.GONE);
            holder.tv_no_more_results.setVisibility(View.GONE);

            final PojoSearchQa currentItem = questionsList.get(position);
            holder.tv_question.setText(Html.fromHtml(currentItem.post));

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

           /* if (position == (questionsList.size() - 1)) {
                Resources r = context.getResources();
                int DP64 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 64, r.getDisplayMetrics());
                int DP2 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, r.getDisplayMetrics());
                holder.rl_parent.setPadding(0, 0, 0, DP64);
            } else {
                holder.rl_parent.setPadding(0, 0, 0, 0);
            } */
        }
    }


    @Override
    public int getItemCount() {

        return questionsList.size()+1;

    }


}

