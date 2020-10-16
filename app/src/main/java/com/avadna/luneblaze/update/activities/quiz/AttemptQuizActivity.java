package com.avadna.luneblaze.update.activities.quiz;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.RelativeLayout;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.pojo.quiz.PojoQuizQuestionListItem;
import com.avadna.luneblaze.update.adapters.quiz.AttemptQuizQuestionListAdapter;
import com.avadna.luneblaze.update.adapters.quiz.QuizPagerAdapter;
import com.avadna.luneblaze.utils.NonSwipingViewPager;
import com.google.android.material.progressindicator.ProgressIndicator;

import java.util.ArrayList;
import java.util.List;

public class AttemptQuizActivity extends AppCompatActivity implements
        AttemptQuizQuestionListAdapter.AttemptQuizQuestionListAdapterCallback {

    RecyclerView rv_question_list;
    List<PojoQuizQuestionListItem> questionListItems;
    AttemptQuizQuestionListAdapter attemptQuizQuestionListAdapter;
    LinearLayoutManager linearLayoutManager;
    RelativeLayout rl_finish_wrapper;
    int counter;

    private NonSwipingViewPager vp_questions_pager;
    private QuizPagerAdapter quizPagerAdapter;

    final int TIME_LIM = 500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attempt_quiz);
        initQuizList();
        counter = 0;
        //setUpPager();
        //initGrifView();
    }


    /*private void setUpPager() {
        vp_questions_pager = (NonSwipingViewPager) findViewById(R.id.vp_questions_pager);
        quizPagerAdapter = new QuizPagerAdapter(AttemptQuizActivity.this,
                getSupportFragmentManager(),new ArrayList<>());
        vp_questions_pager.setAdapter(quizPagerAdapter);
        vp_questions_pager.setOffscreenPageLimit(1);
    }*/
   /* private void initGrifView() {
        GridView gv_answers = (GridView) findViewById(R.id.gv_answers);
        AnswerAdapter customAdapter = new AnswerAdapter(this,new String[4]);
        gv_answers.setAdapter(customAdapter);
        // implement setOnItemClickListener event on GridView
        gv_answers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int answerPosition, long id) {
                TextView tv_answer = (TextView) view.findViewById(R.id.tv_answer);
                tv_answer.setBackgroundResource(R.drawable.app_theme_medium_rounded_fill);
            }
        });

        gv_answers.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return event.getAction() == MotionEvent.ACTION_MOVE;
            }

        });
    }*/


    private void initQuizList() {
        rl_finish_wrapper= (RelativeLayout) findViewById(R.id.rl_finish_wrapper);
        rl_finish_wrapper.setVisibility(View.GONE);

        rv_question_list = (RecyclerView) findViewById(R.id.rv_question_list);
        //rv_list.setPadding(16,32,16,32);
        linearLayoutManager = new LinearLayoutManager(this,
                RecyclerView.HORIZONTAL, false);

        questionListItems = new ArrayList<>();
        String text = "Question List Question List Question List  Question List Question List  Question List  Question List  Question List  Question List  Question List  vQuestion List  Question List  Question List Question List Question List Question List Question List Question List Question List Question List ";

        for (int i = 0; i < 10; i++) {
            String question = text;
            String image = "";
            if (i % 2 == 0) {
                question = text.substring(80);
            }
            if (i % 3 == 0) {
                image = "https://www.ford.com/cmslibs/content/dam/brand_ford/en_us/brand/legacy/nameplate/cars/20_FRD_MST_42360_32.jpg/_jcr_content/renditions/cq5dam.web.1280.1280.jpeg";
            }
            questionListItems.add(new PojoQuizQuestionListItem("#"+(i+1)+" "+question, image, TIME_LIM));
        }

        rv_question_list.setLayoutManager(linearLayoutManager);
        attemptQuizQuestionListAdapter = new AttemptQuizQuestionListAdapter(this, questionListItems);
        rv_question_list.setNestedScrollingEnabled(false);
        rv_question_list.setAdapter(attemptQuizQuestionListAdapter);

    }

    public void goNext() {
        vp_questions_pager.setCurrentItem(counter++);
    }

    @Override
    public void questionListItemAction(int position, String action, String data) {

        switch (action) {
            case AppKeys.TIME_OUT:
            case AppKeys.ANSWER_SELECTED:
                if (!questionListItems.get(position).shown) {
                    counter++;
                    questionListItems.get(counter).shown = true;
                    rv_question_list.smoothScrollToPosition(counter);
                    if(counter==questionListItems.size()-1){
                        rl_finish_wrapper.setVisibility(View.VISIBLE);
                        rv_question_list.setVisibility(View.GONE);

                        ValueAnimator nextAnimation = ValueAnimator.ofFloat(0f, 75f);
                        nextAnimation.setDuration(1800);
                        nextAnimation.setStartDelay(400);
                        nextAnimation.setInterpolator(new DecelerateInterpolator());
                        nextAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator updatedAnimation) {
                                // You can use the animated value in a property that uses the
                                // same type as the animation. In this case, you can use the
                                // float value in the translationX property.
                                float animatedValue = (float)updatedAnimation.getAnimatedValue();
                                ProgressIndicator pb_score=findViewById(R.id.pb_score);
                                pb_score.setProgress((int)animatedValue);
                            }
                        });
                        nextAnimation.start();
                    }
                }
                break;
        }
    }


   /* class AnswerAdapter extends BaseAdapter {
        Context context;
        String[] answers;
        LayoutInflater inflter;

        public AnswerAdapter(Context applicationContext, String[] answers) {
            this.context = applicationContext;
            this.answers = answers;
            inflter = (LayoutInflater.from(applicationContext));
        }

        @Override
        public int getCount() {
            return answers.length;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            view = inflter.inflate(R.layout.quiz_answer_item, null); // inflate the layout
            TextView tv_answer = (TextView) view.findViewById(R.id.tv_answer); // get the reference of ImageView
            String text = "answers Text answers Text answers Text answers Text answers Text answers Text answers Text answers Text ";
            if (position % 2 == 0) {
                text = text.substring(20);
            }
            tv_answer.setText(text);
            return view;
        }
    }*/

}