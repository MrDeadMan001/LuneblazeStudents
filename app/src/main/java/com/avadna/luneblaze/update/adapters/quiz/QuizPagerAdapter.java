package com.avadna.luneblaze.update.adapters.quiz;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import com.avadna.luneblaze.pojo.quiz.PojoQuizQuestionListItem;
import com.avadna.luneblaze.update.fragments.quiz.QuizQuestionFragment;

import java.util.List;

public class QuizPagerAdapter extends FragmentPagerAdapter {
    List<PojoQuizQuestionListItem> questionListItems;
    int count=20;

    public QuizPagerAdapter(Activity activity,FragmentManager fm,List<PojoQuizQuestionListItem> questionListItems) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        String text="Question text Question text Question text Question text Question text Question text Question text Question text Question text Question text Question text ";
        return  QuizQuestionFragment.newInstance(position,text);
    }

    @Override
    public int getCount() {
        return count;
    }
}

