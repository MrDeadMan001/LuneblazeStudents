package com.avadna.luneblaze.adapters;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.avadna.luneblaze.fragments.home.sessionslist.AllSessionListFragmentNew;
import com.avadna.luneblaze.fragments.interest.InterestRelatedArticleListFragment;
import com.avadna.luneblaze.fragments.interest.InterestQuestionListFragment;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.pojo.pojoInterest.PojoInterestDetailsResponse;
import com.google.gson.Gson;

public class InterestPagerAdapter extends FragmentPagerAdapter {


    private Context mContext;
    private PojoInterestDetailsResponse pojoInterestDetailsResponse;
    private Bundle bundle;
    private String jsonString;
    private Gson gson;

    private SparseArray<Fragment> registeredFragments = new SparseArray<>();


    public InterestPagerAdapter(Context context, FragmentManager fm, PojoInterestDetailsResponse pojoInterestDetailsResponse) {
        super(fm);
        mContext = context;
        this.pojoInterestDetailsResponse=pojoInterestDetailsResponse;
        gson = new Gson();
        jsonString = gson.toJson(pojoInterestDetailsResponse);
        bundle = new Bundle();
        bundle.putString("params", jsonString);
    }

    // This determines the fragment for each tab
    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            AllSessionListFragmentNew allSessionListFragmentNew = new AllSessionListFragmentNew();
            Bundle sessionBundle=new Bundle();
            sessionBundle.putString(AppKeys.INTEREST_ID,pojoInterestDetailsResponse.data.interestData.interestId);
            allSessionListFragmentNew.setArguments(sessionBundle);
            return allSessionListFragmentNew;
        }
        else if (position == 1){
            InterestRelatedArticleListFragment interestRelatedArticleListFragment = new InterestRelatedArticleListFragment();
            interestRelatedArticleListFragment.setArguments(bundle);
            return interestRelatedArticleListFragment;
        }
        else {
            InterestQuestionListFragment interestQuestionListFragment = new InterestQuestionListFragment();
            interestQuestionListFragment.setArguments(bundle);
            return interestQuestionListFragment;
        }
    }

    // This determines the number of tabs
    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String title = null;
        if (position == 0)
        {
            title = "Sessions";
        }
        else if (position == 1)
        {
            title = "Articles";
        }
        else if (position == 2)
        {
            title = "Questions";
        }
        return title;
    }


    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        registeredFragments.put(position, fragment);
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        registeredFragments.remove(position);
        super.destroyItem(container, position, object);
    }

    public Fragment getRegisteredFragment(int position) {
        return registeredFragments.get(position);
    }
}
