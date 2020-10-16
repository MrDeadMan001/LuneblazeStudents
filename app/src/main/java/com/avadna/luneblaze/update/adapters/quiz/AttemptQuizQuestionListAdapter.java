package com.avadna.luneblaze.update.adapters.quiz;

import android.app.Activity;
import android.os.CountDownTimer;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.pojo.quiz.PojoQuizQuestionListItem;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.progressindicator.ProgressIndicator;

import java.util.List;

public class AttemptQuizQuestionListAdapter extends RecyclerView.Adapter<AttemptQuizQuestionListAdapter.MyViewHolder> {
    Activity activity;
    List<PojoQuizQuestionListItem> questionsList;

    AttemptQuizQuestionListAdapterCallback attemptQuizQuestionListAdapterCallback;
    CommonFunctions commonFunctions;

    DisplayMetrics displayMetrics;
    int screenHeight;
    int screenWidth;

    int maxQuestionItemHeight;
    int imgMaxHeight;
    int answerItemHeight;


    public AttemptQuizQuestionListAdapter(Activity activity, List<PojoQuizQuestionListItem> questionsList) {
        try {
            this.attemptQuizQuestionListAdapterCallback = ((AttemptQuizQuestionListAdapterCallback) activity);
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement AttemptQuizQuestionListAdapterCallback.");
        }
        this.questionsList = questionsList;
        this.activity = activity;
        displayMetrics = new DisplayMetrics();
        commonFunctions = new CommonFunctions(activity);
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenHeight = displayMetrics.heightPixels;
        screenWidth = displayMetrics.widthPixels;

        //42% of screen height
        maxQuestionItemHeight = ((int) (screenHeight * 0.42f));
        //max image height would be 30% of the screen
        imgMaxHeight = ((int) (screenHeight * 0.28f));
        answerItemHeight = ((int) (screenHeight * 0.15f));
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout rl_question_wrapper;
        TextView tv_question_text;
        CardView cv_image_wrapper;
        ImageView iv_question_image;
        // GridView gv_answers;
        TextView tv_answer1;
        TextView tv_answer2;
        TextView tv_answer3;
        TextView tv_answer4;
        ProgressIndicator pb_time_left;
        TextView tv_time_left;
        CountDownTimer countDownTimer;


        MyViewHolder(View view) {
            super(view);
            rl_question_wrapper = (RelativeLayout) view.findViewById(R.id.rl_question_wrapper);
            tv_question_text = (TextView) view.findViewById(R.id.tv_question_text);
            cv_image_wrapper= (CardView) view.findViewById(R.id.cv_image_wrapper);
            iv_question_image = (ImageView) view.findViewById(R.id.iv_question_image);
            // gv_answers = (GridView) view.findViewById(R.id.gv_answers);
            tv_answer1 = (TextView) view.findViewById(R.id.tv_answer1);
            tv_answer2 = (TextView) view.findViewById(R.id.tv_answer2);
            tv_answer3 = (TextView) view.findViewById(R.id.tv_answer3);
            tv_answer4 = (TextView) view.findViewById(R.id.tv_answer4);

            pb_time_left = (ProgressIndicator) view.findViewById(R.id.pb_time_left);
            tv_time_left = (TextView) view.findViewById(R.id.tv_time_left);

            setIsRecyclable(false);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.attemp_quiz_question_list_item, parent, false);
        return new AttemptQuizQuestionListAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final AttemptQuizQuestionListAdapter.MyViewHolder holder, int position) {
        //   holder.tv_hierarchy_item.setText(hierarchyList.get(position));
        PojoQuizQuestionListItem currentItem = questionsList.get(position);

        if (holder.countDownTimer != null) {
            holder.countDownTimer.cancel();
        }

        holder.countDownTimer = new CountDownTimer(currentItem.time * 1000, 30) {
            public void onTick(long millisUntilFinished) {
                holder.tv_time_left.setText("" + ((millisUntilFinished + 1000) / 1000));

                float ratio = 1f * millisUntilFinished / (currentItem.time * 1000);

                int progress = (int) (ratio * 100);
                holder.pb_time_left.setProgress(progress);
            }

            public void onFinish() {
                attemptQuizQuestionListAdapterCallback.questionListItemAction(position, AppKeys.TIME_OUT, "");
            }
        }.start();

        holder.tv_question_text.setText(currentItem.text);
        holder.rl_question_wrapper.getLayoutParams().height = maxQuestionItemHeight;
        holder.iv_question_image.setVisibility(View.VISIBLE);

        //these would be fetched with question data
        int webImgHeight = 300;
        int webImgWidth = 400;
        //dp

        /*int availableWidth=screenWidth-commonFunctions.dpToPx(64);
        int scaledImgHeight=(int)(1f*webImgHeight*availableWidth/webImgWidth);

        if(scaledImgHeight>imgMaxHeight){
            scaledImgHeight=imgMaxHeight;
        }*/

        if (!currentItem.img.isEmpty()) {

            ViewTreeObserver vto = holder.iv_question_image.getViewTreeObserver();
            vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                public boolean onPreDraw() {
                    holder.iv_question_image.getViewTreeObserver().removeOnPreDrawListener(this);
                    int imgViewWidth = holder.iv_question_image.getMeasuredWidth();
                    int scaledImgHeight = (int) (1f * webImgHeight * imgViewWidth / webImgWidth);
                    if (scaledImgHeight > imgMaxHeight) {
                        scaledImgHeight = imgMaxHeight;
                    }

                    Glide.with(activity)
                            .load(currentItem.img)
                            .apply(new RequestOptions().override(imgViewWidth, scaledImgHeight))
                            .into(holder.iv_question_image);

                    holder.iv_question_image.getLayoutParams().height = scaledImgHeight;
                    holder.tv_question_text.setHeight(maxQuestionItemHeight - scaledImgHeight);
                    return true;
                }
            });
        } else {
            holder.cv_image_wrapper.setVisibility(View.GONE);
            holder.tv_question_text.setHeight(maxQuestionItemHeight);
        }

        holder.tv_time_left.invalidate();
        holder.pb_time_left.invalidate();


        View.OnClickListener onAnswerClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.tv_answer1:
                        attemptQuizQuestionListAdapterCallback.questionListItemAction(position, AppKeys.ANSWER_SELECTED, "1");
                        break;

                    case R.id.tv_answer2:
                        attemptQuizQuestionListAdapterCallback.questionListItemAction(position, AppKeys.ANSWER_SELECTED, "2");
                        break;

                    case R.id.tv_answer3:
                        attemptQuizQuestionListAdapterCallback.questionListItemAction(position, AppKeys.ANSWER_SELECTED, "3");
                        break;

                    case R.id.tv_answer4:
                        attemptQuizQuestionListAdapterCallback.questionListItemAction(position, AppKeys.ANSWER_SELECTED, "4");
                        break;
                }
            }
        };

        holder.tv_answer1.setOnClickListener(onAnswerClickListener);
        holder.tv_answer2.setOnClickListener(onAnswerClickListener);
        holder.tv_answer3.setOnClickListener(onAnswerClickListener);
        holder.tv_answer4.setOnClickListener(onAnswerClickListener);

        holder.tv_answer1.setHeight(answerItemHeight);
        holder.tv_answer2.setHeight(answerItemHeight);
        holder.tv_answer3.setHeight(answerItemHeight);
        holder.tv_answer4.setHeight(answerItemHeight);


    }

    public static interface AttemptQuizQuestionListAdapterCallback {
        void questionListItemAction(int position, String action, String data);
    }

    @Override
    public int getItemCount() {
        return questionsList.size();
    }

}