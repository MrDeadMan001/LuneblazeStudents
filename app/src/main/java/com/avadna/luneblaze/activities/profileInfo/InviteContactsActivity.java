package com.avadna.luneblaze.activities.profileInfo;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.ContactsContract;
import com.google.android.material.tabs.TabLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.ActionBar;

import android.os.Bundle;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.adapters.InviteContactsPagerAdapter;
import com.avadna.luneblaze.activities.AppBaseActivity;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.pojo.PojoPhoneContacts;
import com.avadna.luneblaze.pojo.pojoProfileInfo.PojoGetRegisteredContactsListResponse;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InviteContactsActivity extends AppBaseActivity {

    TabLayout tl_tabs;
    ViewPager vp_pager;
    InviteContactsPagerAdapter inviteContactsPagerAdapter;

    LinearLayout ll_data_wrapper;
    ProgressBar pb_loading;

    private PreferenceUtils preferenceUtils;
    private String user_id;
    private ApiInterface apiService;
    private ActionBar actionBar;

    private static ArrayList<PojoPhoneContacts> contactList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_contacts);
        preferenceUtils = new PreferenceUtils(this);
        user_id = preferenceUtils.get_user_id();
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
        setupActionBar();
        initViews();

    }


    private void setupActionBar() {
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(getString(R.string.contacts));
            actionBar.setBackgroundDrawable(new ColorDrawable(ContextCompat
                    .getColor(InviteContactsActivity.this, R.color.app_theme_dark)));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(ContextCompat
                        .getColor(InviteContactsActivity.this, R.color.app_theme_extra_dark));
            }
            actionBar.setDisplayShowTitleEnabled(true);
        }
    }

    private void initViews() {
        ll_data_wrapper = (LinearLayout) findViewById(R.id.ll_data_wrapper);
        ll_data_wrapper.setVisibility(View.GONE);
        pb_loading = (ProgressBar) findViewById(R.id.pb_loading);
        requestContactPermission();
        setUpTabLayout();
    }

    public void setTabCount(int tabPos, int count) {
        if (tabPos == 0) {
            tl_tabs.getTabAt(tabPos).setText(getString(R.string.your_contacts) + " (" + count + ")");
        } else if (tabPos == 1) {
            tl_tabs.getTabAt(tabPos).setText(getString(R.string.on_luneblaze) + " (" + count + ")");

        }
    }

    private void requestContactPermission() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
           /* if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_CONTACTS)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else*/
            {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_CONTACTS},
                        AppKeys.CONTACTS_READ_PERMISSION);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            new LoadContacts().execute();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case AppKeys.CONTACTS_READ_PERMISSION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    new LoadContacts().execute();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    pb_loading.setVisibility(View.GONE);
                    finish();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }


    private void setUpTabLayout() {
        tl_tabs = (TabLayout) findViewById(R.id.tl_tabs);
        tl_tabs.addTab(tl_tabs.newTab().setText(getString(R.string.your_contacts)));
        tl_tabs.addTab(tl_tabs.newTab().setText(getString(R.string.on_luneblaze)));
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


    }

    private class LoadContacts extends AsyncTask<Void, Void, Void> {

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


            //    pb_loading.setVisibility(View.GONE);
            //   ll_data_wrapper.setVisibility(View.VISIBLE);

            Map<String, String> phoneMap = new HashMap<>();
            for (int i = 0; i < contactList.size(); i++) {
                if (!contactList.get(i).getContactNumber().isEmpty()) {

                    String phone = contactList.get(i).getContactNumber();
                    phone = phone.replace(" ", "");
                    phone = phone.replace("-", "");
                    phone = phone.replace("+91", "");

                    if (Patterns.PHONE.matcher(phone).matches()) {
                        String key = "phone_no" + "[" + i + "]";
                        phoneMap.put(key, phone);
                        // Log.d("Phone data","phone_no"+"["+i+"] ="+phone);
                    }

                }
            }
            hitGetUserFromPhoneApi(user_id, phoneMap);

        }

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            // Show Dialog
            pb_loading.setVisibility(View.VISIBLE);
            ll_data_wrapper.setVisibility(View.GONE);
        }

    }

    private void hitGetUserFromPhoneApi(String user_id, Map<String, String> phoneMap) {

        Call<PojoGetRegisteredContactsListResponse> call = apiService.getUserFromPhone(user_id, phoneMap);
        call.enqueue(new Callback<PojoGetRegisteredContactsListResponse>() {
            @Override
            public void onResponse(Call<PojoGetRegisteredContactsListResponse> call, Response<PojoGetRegisteredContactsListResponse> response) {
                String message = "";
                if (response != null && response.body() != null) {
                    message = response.body().message;

                    if (response.body().data == null) {
                        response.body().data = new ArrayList<>();
                    }

                    ArrayList<PojoPhoneContacts> contactListTemp = new ArrayList<>();

                    for (int i = 0; i < contactList.size(); i++) {
                        boolean isPresent = false;
                        if (!contactList.get(i).getContactNumber().isEmpty()) {
                            String phone = contactList.get(i).getContactNumber();
                            phone = phone.replace(" ", "");
                            phone = phone.replace("-", "");
                            phone = phone.replace("+91", "");

                            if (Patterns.PHONE.matcher(phone).matches()) {
                                for (int j = 0; j < response.body().data.size(); j++) {
                                    if (phone.equals(response.body().data.get(j).userPhone)) {
                                        isPresent = true;
                                        break;
                                    }
                                }
                            }

                        }
                        if (!isPresent) {
                            contactListTemp.add(contactList.get(i));
                        }
                    }

                    tl_tabs.getTabAt(0).setText(getString(R.string.your_contacts) + " ("
                            + contactListTemp.size() + ")");
                    tl_tabs.getTabAt(1).setText(getString(R.string.on_luneblaze) + " ("
                            + response.body().data.size() + ")");

                    inviteContactsPagerAdapter = new InviteContactsPagerAdapter(
                            InviteContactsActivity.this, getSupportFragmentManager(), contactListTemp,
                            response.body().data);
                    vp_pager = (ViewPager) findViewById(R.id.vp_pager);
                    vp_pager.setAdapter(inviteContactsPagerAdapter);

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

                    pb_loading.setVisibility(View.GONE);
                    ll_data_wrapper.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<PojoGetRegisteredContactsListResponse> call, Throwable t) {
                // Log error here since request failed

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
    protected void onPause() {
        super.onPause();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
