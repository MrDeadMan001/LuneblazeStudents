package com.avadna.luneblaze.adapters;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.avadna.luneblaze.fragments.contactsinvite.PhoneContactFragment;
import com.avadna.luneblaze.fragments.contactsinvite.RegisteredContactFragment;
import com.avadna.luneblaze.pojo.PojoPhoneContacts;
import com.avadna.luneblaze.pojo.pojoQuestion.PojoUserData;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class InviteContactsPagerAdapter extends FragmentPagerAdapter {


    private Context mContext;
    private String jsonString;
    private Gson gson;
    ArrayList<PojoPhoneContacts> contactList;
    List<PojoUserData> userList;


    public InviteContactsPagerAdapter(Context context, FragmentManager fm,
                                      ArrayList<PojoPhoneContacts> contactList, List<PojoUserData> userList) {
        super(fm);
        mContext = context;
        gson=new Gson();
        this.contactList=contactList;
        this.userList=userList;

    }

    // This determines the fragment for each tab
    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            PhoneContactFragment phoneContactFragment = new PhoneContactFragment();
            String str=gson.toJson(
                    contactList,
                    new TypeToken<ArrayList<PojoPhoneContacts>>() {}.getType());
            Bundle contactBundle=new Bundle();
            contactBundle.putString("data",str);
            phoneContactFragment.setArguments(contactBundle);
            return phoneContactFragment;
        } else {
            RegisteredContactFragment registeredContactFragment = new RegisteredContactFragment();
            String str=gson.toJson(
                    userList,
                    new TypeToken<ArrayList<PojoUserData>>() {}.getType());
            Bundle userBundle=new Bundle();
            userBundle.putString("data",str);
            registeredContactFragment.setArguments(userBundle);
            return registeredContactFragment;
        }
    }

    // This determines the number of tabs
    @Override
    public int getCount() {
        return 2;
    }
}
