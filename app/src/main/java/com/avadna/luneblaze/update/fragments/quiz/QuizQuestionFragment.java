package com.avadna.luneblaze.update.fragments.quiz;

import android.os.Bundle;

import androidx.fragment.app.Fragment;


import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.update.activities.quiz.AttemptQuizActivity;
import com.google.android.material.progressindicator.ProgressIndicator;

public class QuizQuestionFragment extends Fragment {

    TextView tv_question_text;
    ImageView iv_question_image;
    // GridView gv_answers;
    TextView tv_answer1;
    TextView tv_answer2;
    TextView tv_answer3;
    TextView tv_answer4;
    ProgressIndicator pb_time_left;
    TextView tv_time_left;

    int position;
    String text;
    CountDownTimer countDownTimer;


    public QuizQuestionFragment() {
        // Required empty public constructor
    }

    // newInstance constructor for creating fragment with arguments
    public static QuizQuestionFragment newInstance(int position, String text) {
        QuizQuestionFragment quizQuestionFragment = new QuizQuestionFragment();
        Bundle args = new Bundle();
        args.putInt(AppKeys.POSITION, position);
        args.putString("text", text);
        quizQuestionFragment.setArguments(args);
        return quizQuestionFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            position=getArguments().getInt(AppKeys.POSITION);
            text=getArguments().getString("text");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View baseView= inflater.inflate(R.layout.fragment_quiz_question, container, false);
        initViews(baseView);
        return baseView;
    }


    @Override public void onResume(){
        super.onResume();
        Log.d("Fragment info",""+this.getId());
        countDownTimer.start();
    }


    void initViews(View baseView){
        tv_question_text = (TextView) baseView.findViewById(R.id.tv_question_text);
        iv_question_image = (ImageView) baseView.findViewById(R.id.iv_question_image);
        // gv_answers = (GridView) baseView.findViewById(R.id.gv_answers);
        tv_answer1 = (TextView) baseView.findViewById(R.id. tv_answer1);
        tv_answer2 = (TextView) baseView.findViewById(R.id. tv_answer2);
        tv_answer3 = (TextView) baseView.findViewById(R.id. tv_answer3);
        tv_answer4 = (TextView) baseView.findViewById(R.id. tv_answer4);

        pb_time_left = (ProgressIndicator) baseView.findViewById(R.id.pb_time_left);
        tv_time_left = (TextView) baseView.findViewById(R.id.tv_time_left);
        iv_question_image.setVisibility(View.VISIBLE);

        if (position % 2 == 0) {
           iv_question_image.setVisibility(View.GONE);
        }

        countDownTimer = new CountDownTimer(5000, 20) {
            public void onTick(long millisUntilFinished) {
                tv_time_left.setText("" + (millisUntilFinished / 1000));
                pb_time_left.setProgress((int) (millisUntilFinished) * 100 / 5000);
            }

            public void onFinish() {
                ((AttemptQuizActivity)QuizQuestionFragment.this.getActivity()).goNext();
            }
        };

    }

}