package com.avadna.luneblaze.adapters;

import android.content.Context;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.avadna.luneblaze.fragments.search.ArticleSearchResultFragment;
import com.avadna.luneblaze.fragments.search.InterestSearchListFragment;
import com.avadna.luneblaze.fragments.search.UserSearchResultListFragment;
import com.avadna.luneblaze.fragments.search.QuestionSearchResultFragment;
import com.avadna.luneblaze.fragments.SearchAllResultsFragment;
import com.avadna.luneblaze.fragments.UserActivityPostFragment;
import com.avadna.luneblaze.fragments.search.SessionSearchResultFragment;

/**
 * Created by Sunny on 21-12-2017.
 */

public class UserActivityPagerAdapter extends FragmentPagerAdapter {

    private Context mContext;
    int type;

    public UserActivityPagerAdapter(Context context, FragmentManager fm,int type) {
        super(fm);
        mContext = context;
        this.type=type;
    }

    // This determines the fragment for each tab
    @Override
    public Fragment getItem(int position) {

        switch (type){
            //adapter for activities performed by user
            case 1:
                if (position == 0) {
                    return new UserActivityPostFragment();

                } else if (position == 1){
                    return new UserActivityPostFragment();
                }
                else if (position == 2){
                    return new UserActivityPostFragment();
                }
                else if (position == 3){
                    return new UserActivityPostFragment();
                }
                else if (position == 4){
                    return new UserActivityPostFragment();
                }

                break;
                // adapter for interest activity tabs
            case 2:


                break;
                //adapter for search results activity
            case 3:
                if (position == 0) {
                    return new SearchAllResultsFragment();

                } else if (position == 1){
                    return new SessionSearchResultFragment();
                }
                else if (position == 2){
                    return new QuestionSearchResultFragment();
                }
                else if (position == 3){
                    return new UserActivityPostFragment();
                }
                else if (position == 4){
                    return new ArticleSearchResultFragment();
                }
                else if (position == 5){
                    return new UserSearchResultListFragment();
                }
                else if (position == 6){
                    return new InterestSearchListFragment();
                }

                break;
        }

        return null;
    }

    // This determines the number of tabs
    @Override
    public int getCount() {
        if(type==1){
            return 5;
        }
        else if(type==2){
            return 3;
        }
        else if(type==3){
            return 7;
        }
        else return 0;
    }
}
