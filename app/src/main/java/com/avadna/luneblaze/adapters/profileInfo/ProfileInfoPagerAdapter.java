package com.avadna.luneblaze.adapters.profileInfo;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.avadna.luneblaze.fragments.profileinfo.ProfileInfoAnswersListFragment;
import com.avadna.luneblaze.fragments.profileinfo.ProfileInfoArticlesListFragment;
import com.avadna.luneblaze.fragments.profileinfo.ProfileInfoPollsListFragment;
import com.avadna.luneblaze.fragments.profileinfo.ProfileInfoPostsListFragment;
import com.avadna.luneblaze.fragments.profileinfo.ProfileInfoQuestionsListFragment;
import com.avadna.luneblaze.fragments.profileinfo.ProfileInfoSessionsListFragment;
import com.avadna.luneblaze.fragments.profileinfo.ProfileInfoUserActivityFragment;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.pojo.pojoProfileInfo.PojoProfileInfoResponseData;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class ProfileInfoPagerAdapter extends FragmentPagerAdapter {
    PojoProfileInfoResponseData pojoProfileInfoResponseData;
    private Context mContext;
    Gson gson = new Gson();

    Bundle bundle;
    String dataStr;
    boolean showSessionTab;

    public ProfileInfoPagerAdapter(Context context, FragmentManager fm,
                                   PojoProfileInfoResponseData pojoProfileInfoResponseData,
                                   String targetUserId, boolean showSessionTab) {
        super(fm);
        mContext = context;
        this.pojoProfileInfoResponseData = pojoProfileInfoResponseData;
        dataStr = gson.toJson(pojoProfileInfoResponseData,
                new TypeToken<PojoProfileInfoResponseData>() {
                }.getType());
        bundle = new Bundle();
        this.showSessionTab = showSessionTab;
        bundle.putString("data", dataStr);
        bundle.putString(AppKeys.TARGET_USER_ID, targetUserId);
    }

    // This determines the fragment for each tab
    @Override
    public Fragment getItem(int position) {

        if (showSessionTab) {
            switch (position) {
                case 0:
                    ProfileInfoPostsListFragment profileInfoPostsListFragment = new ProfileInfoPostsListFragment();
                    profileInfoPostsListFragment.setArguments(bundle);
                    return profileInfoPostsListFragment;

                case 1:
                    ProfileInfoSessionsListFragment profileInfoSessionsListFragment = new ProfileInfoSessionsListFragment();
                    profileInfoSessionsListFragment.setArguments(bundle);
                    return profileInfoSessionsListFragment;

                case 2:
                    ProfileInfoArticlesListFragment profileInfoArticlesListFragment = new ProfileInfoArticlesListFragment();
                    profileInfoArticlesListFragment.setArguments(bundle);
                    return profileInfoArticlesListFragment;

                case 3:
                    ProfileInfoPollsListFragment profileInfoPollsListFragment = new ProfileInfoPollsListFragment();
                    profileInfoPollsListFragment.setArguments(bundle);
                    return profileInfoPollsListFragment;

                case 4:
                    ProfileInfoQuestionsListFragment profileInfoQuestionsListFragment = new ProfileInfoQuestionsListFragment();
                    profileInfoQuestionsListFragment.setArguments(bundle);
                    return profileInfoQuestionsListFragment;

                case 5:
                    ProfileInfoAnswersListFragment profileInfoAnswersListFragment = new ProfileInfoAnswersListFragment();
                    profileInfoAnswersListFragment.setArguments(bundle);
                    return profileInfoAnswersListFragment;

                case 6:
                    ProfileInfoUserActivityFragment profileInfoUserActivityFragment = new ProfileInfoUserActivityFragment();
                    profileInfoUserActivityFragment.setArguments(bundle);
                    return profileInfoUserActivityFragment;

                default:

                    return null;
            }
        } else {
            switch (position) {
                case 0:
                    ProfileInfoPostsListFragment profileInfoPostsListFragment = new ProfileInfoPostsListFragment();
                    profileInfoPostsListFragment.setArguments(bundle);
                    return profileInfoPostsListFragment;

                case 1:
                    ProfileInfoArticlesListFragment profileInfoArticlesListFragment = new ProfileInfoArticlesListFragment();
                    profileInfoArticlesListFragment.setArguments(bundle);
                    return profileInfoArticlesListFragment;

                case 2:
                    ProfileInfoPollsListFragment profileInfoPollsListFragment = new ProfileInfoPollsListFragment();
                    profileInfoPollsListFragment.setArguments(bundle);
                    return profileInfoPollsListFragment;

                case 3:
                    ProfileInfoQuestionsListFragment profileInfoQuestionsListFragment = new ProfileInfoQuestionsListFragment();
                    profileInfoQuestionsListFragment.setArguments(bundle);
                    return profileInfoQuestionsListFragment;

                case 4:
                    ProfileInfoAnswersListFragment profileInfoAnswersListFragment = new ProfileInfoAnswersListFragment();
                    profileInfoAnswersListFragment.setArguments(bundle);
                    return profileInfoAnswersListFragment;

                case 5:
                    ProfileInfoUserActivityFragment profileInfoUserActivityFragment = new ProfileInfoUserActivityFragment();
                    profileInfoUserActivityFragment.setArguments(bundle);
                    return profileInfoUserActivityFragment;

                default:

                    return null;
            }
        }

    }

    // This determines the number of tabs
    @Override
    public int getCount() {
        if (showSessionTab) {
            return 7;
        } else {
            return 6;
        }
    }
}
