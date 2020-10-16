package com.avadna.luneblaze.activities.profileInfo;

import android.app.Dialog; import com.avadna.luneblaze.helperClasses.MyCustomThemeDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.ContactsContract;
import com.google.android.material.tabs.TabLayout;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.ActionBar;

import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.adapters.FriendsPagerAdapter;
import com.avadna.luneblaze.adapters.MyFriendsListAdapter;
import com.avadna.luneblaze.adapters.SuggestedFriendsAdapter;
import com.avadna.luneblaze.activities.AppBaseActivity;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.pojo.PojoPhoneContacts;
import com.avadna.luneblaze.pojo.PojoUserConnectResponse;
import com.avadna.luneblaze.pojo.pojoProfileInfo.PojoGetRegisteredContactsListResponse;
import com.avadna.luneblaze.pojo.pojoQuestion.PojoUserData;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyFriendsActivity extends AppBaseActivity implements
        MyFriendsListAdapter.FriendListAdapterCallback {

    private PreferenceUtils preferenceUtils;
    CommonFunctions commonFunctions;
    private String user_id;
    private ApiInterface apiService;

    private ActionBar actionBar;
    private TabLayout tl_tabs;
    private ViewPager vp_pager;
    private FriendsPagerAdapter friendsPagerAdapter;

    private View.OnClickListener onClickListener;
    private static ArrayList<PojoPhoneContacts> contactList;
    private static ProgressDialog pd;
    private Dialog contactListDialog;
    private ProgressBar pb_loading_users;

    private List<PojoUserData> phoneUserList;
    private SuggestedFriendsAdapter phoneUserListAdapter;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_friend);
        preferenceUtils = new PreferenceUtils(this);
        commonFunctions=new CommonFunctions(this);
        user_id = preferenceUtils.get_user_id();
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
        setupActionBar();
        initDispMetrics();
        initApis();
        initViews();
        initLists();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void setupActionBar() {
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(getString(R.string.friends));
            actionBar.setBackgroundDrawable(new ColorDrawable(ContextCompat
                    .getColor(MyFriendsActivity.this, R.color.app_theme_dark)));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(ContextCompat
                        .getColor(MyFriendsActivity.this, R.color.app_theme_extra_dark));
            }
            actionBar.setDisplayShowTitleEnabled(true);
        }
    }

    private void initDispMetrics() {

    }

    private void initApis() {

    }

    private void initViews() {

        setUpTabLayout();
    }

    private void initLists() {

    }



    private void setClickListener() {

    }

    public void setTabCount(int tabPos,int count){
        if(tabPos==0){
            tl_tabs.getTabAt(0).setText(getString(R.string.my_fellows)+ " ("+count+")");
        }
        else if(tabPos==1){
            tl_tabs.getTabAt(1).setText(getString(R.string.requests)+ " ("+count+")");
        }
    }

    private void setUpTabLayout() {
        tl_tabs = (TabLayout) findViewById(R.id.tl_tabs);
        tl_tabs.addTab(tl_tabs.newTab().setText(getString(R.string.my_fellows)));
        tl_tabs.addTab(tl_tabs.newTab().setText(getString(R.string.requests)));
        // tl_tabs.addTab(tl_tabs.newTab().setText(getString(R.string.suggested)));
        tl_tabs.setTabGravity(TabLayout.GRAVITY_FILL);

        tl_tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (vp_pager != null) {
                    vp_pager.setCurrentItem(tab.getPosition());
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        friendsPagerAdapter = new FriendsPagerAdapter(MyFriendsActivity.this, getSupportFragmentManager());
        vp_pager = (ViewPager) findViewById(R.id.vp_pager);
        vp_pager.setAdapter(friendsPagerAdapter);

        vp_pager.setOffscreenPageLimit(2);
        vp_pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                tl_tabs.setScrollPosition(position, positionOffset, false);
            }

            @Override
            public void onPageSelected(int position) {
                TabLayout.Tab tab = tl_tabs.getTabAt(position);
                tab.select();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    private ArrayList<PojoPhoneContacts> readContacts() {
        ArrayList<PojoPhoneContacts> contactList = new ArrayList<PojoPhoneContacts>();

        Uri uri = ContactsContract.Contacts.CONTENT_URI; // Contact URI
        Cursor contactsCursor = getContentResolver().query(uri, null, null,
                null, ContactsContract.Contacts.DISPLAY_NAME + " ASC "); // Return

        // Move cursor at starting
        if (contactsCursor.moveToFirst()) {
            do {
                long contctId = contactsCursor.getLong(contactsCursor
                        .getColumnIndex("_ID")); // Get contact ID
                Uri dataUri = ContactsContract.Data.CONTENT_URI; // URI to get
                // data of
                // contacts
                Cursor dataCursor = getContentResolver().query(dataUri, null,
                        ContactsContract.Data.CONTACT_ID + " = " + contctId,
                        null, null);// Retrun data cusror represntative to
                // contact ID

                // Strings to get all details
                String displayName = "";
                String nickName = "";
                String homePhone = "";
                String mobilePhone = "";
                String workPhone = "";
                String photoPath = ""; // Photo path
                byte[] photoByte = null;// Byte to get photo since it will come
                // in BLOB
                String homeEmail = "";
                String workEmail = "";
                String companyName = "";
                String title = "";

                // This strings stores all contact numbers, email and other
                // details like nick name, company etc.
                String contactNumbers = "";
                String contactEmailAddresses = "";
                String contactOtherDetails = "";

                // Now start the cusrsor
                if (dataCursor.moveToFirst()) {
                    displayName = dataCursor
                            .getString(dataCursor
                                    .getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));// get
                    // the
                    // contact
                    // name
                    do {
                        if (dataCursor
                                .getString(
                                        dataCursor.getColumnIndex("mimetype"))
                                .equals(ContactsContract.CommonDataKinds.Nickname.CONTENT_ITEM_TYPE)) {
                            nickName = dataCursor.getString(dataCursor
                                    .getColumnIndex("data1")); // Get Nick Name
                            contactOtherDetails += "NickName : " + nickName
                                    + "n";// Add the nick name to string

                        }

                        if (dataCursor
                                .getString(
                                        dataCursor.getColumnIndex("mimetype"))
                                .equals(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)) {

                            // In this get All contact numbers like home,
                            // mobile, work, etc and add them to numbers string
                            switch (dataCursor.getInt(dataCursor
                                    .getColumnIndex("data2"))) {
                           /*     case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:
                                    homePhone = dataCursor.getString(dataCursor
                                            .getColumnIndex("data1"));
                                    contactNumbers += "Home Phone : " + homePhone
                                            + "n";
                                    break;

                                case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:
                                    workPhone = dataCursor.getString(dataCursor
                                            .getColumnIndex("data1"));
                                    contactNumbers += "Work Phone : " + workPhone
                                            + "n";
                                    break;*/

                                case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
                                    mobilePhone = dataCursor.getString(dataCursor
                                            .getColumnIndex("data1"));
                                    contactNumbers = mobilePhone;
                                    break;

                            }
                        }
                        if (dataCursor
                                .getString(
                                        dataCursor.getColumnIndex("mimetype"))
                                .equals(ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)) {

                            // In this get all Emails like home, work etc and
                            // add them to email string
                            switch (dataCursor.getInt(dataCursor
                                    .getColumnIndex("data2"))) {
                                case ContactsContract.CommonDataKinds.Email.TYPE_HOME:
                                    homeEmail = dataCursor.getString(dataCursor
                                            .getColumnIndex("data1"));
                                    contactEmailAddresses += "Home Email : "
                                            + homeEmail + "n";
                                    break;
                                case ContactsContract.CommonDataKinds.Email.TYPE_WORK:
                                    workEmail = dataCursor.getString(dataCursor
                                            .getColumnIndex("data1"));
                                    contactEmailAddresses += "Work Email : "
                                            + workEmail + "n";
                                    break;

                            }
                        }

                        if (dataCursor
                                .getString(
                                        dataCursor.getColumnIndex("mimetype"))
                                .equals(ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE)) {
                            companyName = dataCursor.getString(dataCursor
                                    .getColumnIndex("data1"));// get company
                            // name
                            contactOtherDetails += "Coompany Name : "
                                    + companyName + "n";
                            title = dataCursor.getString(dataCursor
                                    .getColumnIndex("data4"));// get Company
                            // title
                            contactOtherDetails += "Title : " + title + "n";

                        }

                        if (dataCursor
                                .getString(
                                        dataCursor.getColumnIndex("mimetype"))
                                .equals(ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE)) {
                            photoByte = dataCursor.getBlob(dataCursor
                                    .getColumnIndex("data15")); // get photo in
                            // byte

                            if (photoByte != null) {

                                // Now make a cache folder in file manager to
                                // make cache of contacts images and save them
                                // in .png
                                Bitmap bitmap = BitmapFactory.decodeByteArray(
                                        photoByte, 0, photoByte.length);
                                File cacheDirectory = getBaseContext()
                                        .getCacheDir();
                                File tmp = new File(cacheDirectory.getPath()
                                        + "/_androhub" + contctId + ".png");
                                try {
                                    FileOutputStream fileOutputStream = new FileOutputStream(tmp);
                                    bitmap.compress(Bitmap.CompressFormat.PNG,
                                            100, fileOutputStream);
                                    fileOutputStream.flush();
                                    fileOutputStream.close();
                                } catch (Exception e) {
                                    // TODO: handle exception
                                    e.printStackTrace();
                                }
                                photoPath = tmp.getPath();// finally get the
                                // saved path of
                                // image
                            }

                        }

                    } while (dataCursor.moveToNext()); // Now move to next
                    // cursor

                    contactList.add(new PojoPhoneContacts(Long.toString(contctId),
                            displayName, contactNumbers, contactEmailAddresses,
                            photoPath, contactOtherDetails));// Finally add
                    // items to
                    // array list
                }

            } while (contactsCursor.moveToNext());
        }
        return contactList;
    }

    @Override
    public void onFriendListMethodCallback(int position, PojoUserData item, String type) {

    }


    public class LoadContacts extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            contactList = readContacts();// Get contacts array list from this
            // method
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            super.onPostExecute(result);


            // Hide dialog if showing

            if (pd.isShowing())
                pd.dismiss();

            openContactListDialog();

        }

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            // Show Dialog
            pd = ProgressDialog.show(MyFriendsActivity.this, "Loading Contacts",
                    "Please Wait...");
        }

    }

    private void openContactListDialog() {
        ArrayList<String> phoneNumberList = new ArrayList<>();
        Map<String, String> phoneMap = new HashMap<>();

        for (int i = 0; i < contactList.size(); i++) {
            if (!contactList.get(i).getContactNumber().isEmpty()) {

                String phone = contactList.get(i).getContactNumber();
                phone = phone.replace(" ", "");
                phone = phone.replace("-", "");
                phone = phone.replace("+91", "");

                if (Patterns.PHONE.matcher(phone).matches()) {
                    phoneNumberList.add(phone);
                    String key = "phone_no" + "[" + i + "]";
                    phoneMap.put(key, phone);
                    // Log.d("Phone data","phone_no"+"["+i+"] ="+phone);
                }

            }
        }

        RecyclerView rv_list;
        contactListDialog = new MyCustomThemeDialog(this, android.R.style.Theme_Black_NoTitleBar);
        contactListDialog.setContentView(R.layout.contact_list_dialog);
        pb_loading_users = (ProgressBar) contactListDialog.findViewById(R.id.pb_loading_users);
        rv_list = (RecyclerView) contactListDialog.findViewById(R.id.rv_list);
        phoneUserList = new ArrayList<>();
        rv_list.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        phoneUserListAdapter = new SuggestedFriendsAdapter(MyFriendsActivity.this, phoneUserList,
                "ver");
        rv_list.setAdapter(phoneUserListAdapter);
        contactListDialog.show();
        hitGetuserFromPhoneApi(user_id, phoneMap);
    }

    private void hitGetuserFromPhoneApi(String user_id, Map<String, String> phoneMap) {

        // String[] arr= phoneList.toArray(new String[phoneList.size()]);

        /*List<MultipartBody.Part> parts = new ArrayList<>();

        for(int i=0;i<phoneList.size();i++){
            parts.add(MultipartBody.Part.createFormData("phone_num"+"["+i+"]", phoneList.get(i)));
        }*/

        Call<PojoGetRegisteredContactsListResponse> call = apiService.getUserFromPhone(user_id, phoneMap);
        call.enqueue(new Callback<PojoGetRegisteredContactsListResponse>() {
            @Override
            public void onResponse(Call<PojoGetRegisteredContactsListResponse> call, Response<PojoGetRegisteredContactsListResponse> response) {
                String message = "";
                if (response != null && response.body() != null) {
                    message = response.body().message;
                    phoneUserList.clear();
                    phoneUserList.addAll(response.body().data);
                    phoneUserListAdapter.notifyDataSetChanged();
                }
                pb_loading_users.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<PojoGetRegisteredContactsListResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(MyFriendsActivity.this, t.toString(),
                        Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);
                // rl_parent.setVisibility(View.VISIBLE);
                pb_loading_users.setVisibility(View.GONE);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_phone_contacts_menu, menu);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.add_phone_contact:
                Intent contactIntent = new Intent(MyFriendsActivity.this,
                        InviteContactsActivity.class);
                startActivity(contactIntent);
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    public void hitAddFriendApi(String user_id, String friend_id, String connection_type) {
        Call<PojoUserConnectResponse> call = apiService.addFriendApi(user_id, friend_id, connection_type);
        call.enqueue(new Callback<PojoUserConnectResponse>() {
            @Override
            public void onResponse(Call<PojoUserConnectResponse> call, Response<PojoUserConnectResponse> response) {

                if (response.body() != null) {
                    String message = response.body().message;
                    if (response.body().data != null) {
                        if (response.body().status.equals("1")) {

                        } else {
                            commonFunctions.setToastMessage(getApplicationContext(), "" + message, Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<PojoUserConnectResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(getApplicationContext(), t.toString(), Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);
            }
        });
    }

    public void hitCancelFriendApi(String user_id, String friend_id, String connection_type) {

        Call<PojoUserConnectResponse> call = apiService.changeUserConnectionApi(user_id, friend_id, connection_type);
        call.enqueue(new Callback<PojoUserConnectResponse>() {
            @Override
            public void onResponse(Call<PojoUserConnectResponse> call, Response<PojoUserConnectResponse> response) {

                if (response.body() != null) {
                    String message = response.body().message;
                    if (response.body().data != null) {
                        if (response.body().status.equals("1")) {

                        } else {
                            commonFunctions.setToastMessage(getApplicationContext(), "" + message, Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<PojoUserConnectResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(getApplicationContext(), t.toString(), Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();

    }



}
