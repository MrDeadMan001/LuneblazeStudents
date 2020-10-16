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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.activities.question.QuestionActivity;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.pojo.pojoProfileInfo.tabsData.PojoProfileQuestions;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;

import java.util.List;

public class ProfileInfoQuestionListAdapter extends RecyclerView.Adapter<ProfileInfoQuestionListAdapter.MyViewHolder> {
    Context context;
    List<PojoProfileQuestions> questionsList;
    ApiInterface apiService;
    PreferenceUtils preferenceUtils;
    CommonFunctions commonFunctions;
    String user_id;
    boolean showProgress=true;

    public ProfileInfoQuestionListAdapter(Context context, List<PojoProfileQuestions> questionsList) {
        this.questionsList = questionsList;
        this.context = context;
        preferenceUtils = new PreferenceUtils(context);
        commonFunctions=new CommonFunctions(context);
        user_id = preferenceUtils.get_user_id();
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
    }

    public void showProgress(boolean status) {
        showProgress=status;
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tv_question;
        TextView tv_ques_num_upvotes;
        TextView tv_num_answers;
        TextView tv_num_followers;
        RelativeLayout rl_parent;
        LinearLayout ll_border_wrapper;
        ProgressBar pb_loading_more;
        TextView tv_session_name;
        TextView tv_time_stamp;


        MyViewHolder(View view) {
            super(view);
            ll_border_wrapper= (LinearLayout) view.findViewById(R.id.ll_border_wrapper);
            pb_loading_more= (ProgressBar) view.findViewById(R.id.pb_loading_more);
            tv_question = (TextView) view.findViewById(R.id.tv_question);
            tv_ques_num_upvotes = (TextView) view.findViewById(R.id.tv_ques_num_upvotes);
            tv_num_answers = (TextView) view.findViewById(R.id.tv_num_answers);
            tv_num_followers= (TextView) view.findViewById(R.id.tv_num_followers);
            rl_parent = (RelativeLayout) view.findViewById(R.id.rl_parent);

            tv_session_name= (TextView) view.findViewById(R.id.tv_session_name);
            tv_time_stamp= (TextView) view.findViewById(R.id.tv_time_stamp);
        }
    }

    @Override
    public ProfileInfoQuestionListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.profile_question_list_item, parent, false);

        return new ProfileInfoQuestionListAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ProfileInfoQuestionListAdapter.MyViewHolder holder, int position) {
        //   holder.tv_hierarchy_item.setText(hierarchyList.get(position));
        if(position==questionsList.size()){
            holder.ll_border_wrapper.setVisibility(View.GONE);
            if(showProgress){
                holder.pb_loading_more.setVisibility(View.VISIBLE);
            }
            else {
                holder.pb_loading_more.setVisibility(View.GONE);
            }
        }

        else {
            holder.pb_loading_more.setVisibility(View.GONE);
            holder.ll_border_wrapper.setVisibility(View.VISIBLE);
            final PojoProfileQuestions currentItem = questionsList.get(position);
            holder.tv_question.setText(Html.fromHtml(currentItem.post));

            if(currentItem.likes.equals("0")){
                holder.tv_ques_num_upvotes.setVisibility(View.GONE);
            }else {
                holder.tv_ques_num_upvotes.setVisibility(View.VISIBLE);
            }
            holder.tv_ques_num_upvotes.setText(currentItem.likes + " " + context.getString(R.string.upvotes));

            if(currentItem.totalAns==0){
                holder.tv_num_answers.setVisibility(View.GONE);
            }else {
                holder.tv_num_answers.setVisibility(View.VISIBLE);
            }
            holder.tv_num_answers.setText(currentItem.totalAns + " " + context.getString(R.string.answers));

            if(currentItem.totalFollowers==0){
                holder.tv_num_followers.setVisibility(View.GONE);
            }else {
                holder.tv_num_followers.setVisibility(View.VISIBLE);
            }
            holder.tv_num_followers.setText(currentItem.totalFollowers + " " + context.getString(R.string.followers));
            holder.tv_session_name.setText(currentItem.sessionName);
            holder.tv_time_stamp.setText(commonFunctions.parseDateAndTimeToName(currentItem.addedOn));


            holder.rl_parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent questionIntent = new Intent(context, QuestionActivity.class);
                    questionIntent.putExtra("id", currentItem.sessionsQaId);
                    context.startActivity(questionIntent);
                }
            });
        }

    }

    @Override
    public int getItemCount() {

        return questionsList.size()+1;

    }


}

