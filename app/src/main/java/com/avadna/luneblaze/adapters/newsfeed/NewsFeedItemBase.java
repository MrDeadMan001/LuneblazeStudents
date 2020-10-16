package com.avadna.luneblaze.adapters.newsfeed;

import android.app.Activity;
import android.util.DisplayMetrics;

import androidx.fragment.app.Fragment;

import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;

public class NewsFeedItemBase {
    protected NewsFeedAdapter.NewsFeedAdapterCallback newsFeedAdapterCallback;
    protected Activity activity;
    protected Fragment fragment;
    protected CommonFunctions commonFunctions;
    protected PreferenceUtils preferenceUtils;
    protected String user_id;
    protected DisplayMetrics displayMetrics;
    protected int imageFixedWidth;
    protected int imageFixedHeight;
    protected int maxSessionImageHeight;
    protected boolean hideHeader = false;
    protected boolean hideFooter = false;
    protected String userFixedname;
    protected boolean isGuestUser=false;

    public NewsFeedItemBase(Fragment fragment, String userFixedname) {
        try {
            this.newsFeedAdapterCallback = ((NewsFeedAdapter.NewsFeedAdapterCallback) fragment);
        } catch (ClassCastException e) {
            throw new ClassCastException("Fragment must implement NewsFeedAdapterCallback.");
        }
        this.fragment = fragment;
        this.activity = fragment.getActivity();
        this.userFixedname = userFixedname;
        preferenceUtils = new PreferenceUtils(activity);
        user_id = preferenceUtils.get_user_id();
        commonFunctions = new CommonFunctions(activity);
        displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        imageFixedWidth = (int) (displayMetrics.widthPixels);
        imageFixedHeight = (int) (displayMetrics.widthPixels * 0.6f);
        if(preferenceUtils.get_user_id().equals(AppKeys.GUEST_USER_ID)){
            isGuestUser=true;
        }
    }
}
