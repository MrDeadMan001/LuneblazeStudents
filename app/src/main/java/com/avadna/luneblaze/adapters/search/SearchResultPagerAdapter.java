package com.avadna.luneblaze.adapters.search;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.avadna.luneblaze.fragments.search.ArticleSearchResultFragment;
import com.avadna.luneblaze.fragments.search.InterestSearchListFragment;
import com.avadna.luneblaze.fragments.search.OrganisationSearchResultFragment;
import com.avadna.luneblaze.fragments.search.PollSearchResultFragment;
import com.avadna.luneblaze.fragments.search.PostSearchResultFragment;
import com.avadna.luneblaze.fragments.search.QuestionSearchResultFragment;
import com.avadna.luneblaze.fragments.SearchAllResultsFragment;
import com.avadna.luneblaze.fragments.search.SessionSearchResultFragment;
import com.avadna.luneblaze.fragments.search.UserSearchResultListFragment;
import com.avadna.luneblaze.fragments.search.VenueSearchResultListFragment;
import com.avadna.luneblaze.pojo.pojoSearch.PojoSearchResultResponse;
import com.google.gson.Gson;

public class SearchResultPagerAdapter extends FragmentPagerAdapter {
    PojoSearchResultResponse pojoSearchResultResponse;
    private Context mContext;
    Gson gson=new Gson();
    String query="";
    int selectedPos=0;

    private SparseArray<Fragment> registeredFragments = new SparseArray<>();


    public SearchResultPagerAdapter(Context context, FragmentManager fm,String currentSearchQuery, int selectedItem) {
        super(fm);
        mContext = context;
        this.pojoSearchResultResponse=pojoSearchResultResponse;
        query=currentSearchQuery;
        selectedPos=selectedItem;
    }



    // This determines the fragment for each tab
    @Override
    public Fragment getItem(int position) {
        Bundle userBundle=new Bundle();
        userBundle.putString("query",query);
        userBundle.putInt("position",selectedPos);

        switch (position) {

            case 0:
                UserSearchResultListFragment userSearchResultListFragment =new UserSearchResultListFragment();
                userSearchResultListFragment.setArguments(userBundle);
                return userSearchResultListFragment;

            case 1:
                SessionSearchResultFragment sessionSearchResultFragment=new SessionSearchResultFragment();
                sessionSearchResultFragment.setArguments(userBundle);
                return sessionSearchResultFragment;

            case 2:
                PostSearchResultFragment postSearchResultFragment=new PostSearchResultFragment();
                     postSearchResultFragment.setArguments(userBundle);
                return postSearchResultFragment;

            case 3:
                ArticleSearchResultFragment articleSearchResultFragment=new ArticleSearchResultFragment();
                   articleSearchResultFragment.setArguments(userBundle);
                return articleSearchResultFragment;

            case 4:
                PollSearchResultFragment pollSearchResultFragment =new PollSearchResultFragment();
                pollSearchResultFragment.setArguments(userBundle);
                return pollSearchResultFragment;

            case 5:
                QuestionSearchResultFragment questionSearchResultFragment=new QuestionSearchResultFragment();
                questionSearchResultFragment.setArguments(userBundle);
                return questionSearchResultFragment;

            case 6:
                InterestSearchListFragment interestSearchListFragment =new InterestSearchListFragment();
                interestSearchListFragment.setArguments(userBundle);
                return interestSearchListFragment;

            case 7:
                VenueSearchResultListFragment venueSearchResultListFragment =new VenueSearchResultListFragment();
                venueSearchResultListFragment.setArguments(userBundle);
                return venueSearchResultListFragment;


            case 8:
                OrganisationSearchResultFragment organisationSearchResultFragment =new OrganisationSearchResultFragment();
                organisationSearchResultFragment.setArguments(userBundle);
                return organisationSearchResultFragment;


            default:
                return new SearchAllResultsFragment();
        }

    }

    // This determines the number of tabs
    @Override
    public int getCount() {
        return 9;

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
