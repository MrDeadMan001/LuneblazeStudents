package com.avadna.luneblaze.fragments.contactsinvite;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.adapters.PhoneContactsListAdapter;
import com.avadna.luneblaze.helperClasses.AES;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.pojo.PojoPhoneContacts;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;
import com.avadna.luneblaze.rest.SMSApiClient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class PhoneContactFragment extends Fragment implements
        PhoneContactsListAdapter.PhoneContactsListAdapterCallback {

    RecyclerView rv_people_list;
    LinearLayoutManager linearLayoutManager;
    private ArrayList<PojoPhoneContacts> contactList;
    private PhoneContactsListAdapter phoneContactsListAdapter;


    private PreferenceUtils preferenceUtils;
    CommonFunctions commonFunctions;
    ApiInterface SMSapiService;
    private String user_id;
    private ApiInterface apiService;

    TextView tv_select_all;
    boolean all_selected = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferenceUtils = new PreferenceUtils(getActivity());
        commonFunctions = new CommonFunctions(getActivity());
        user_id = preferenceUtils.get_user_id();
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
        SMSapiService = SMSApiClient.getClient().create(ApiInterface.class);


        if (getArguments() != null) {
            Type type = new TypeToken<ArrayList<PojoPhoneContacts>>() {
            }.getType();
            String str = getArguments().getString("data");
            if (str.isEmpty()) {
                contactList = new ArrayList<>();
            } else {
                contactList = new Gson().fromJson(str, type);
            }
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View baseLayout = inflater.inflate(R.layout.fragment_phone_contacts, container, false);
        initViews(baseLayout);
        return baseLayout;
    }

    private void initViews(View baseLayout) {
        tv_select_all = (TextView) baseLayout.findViewById(R.id.tv_select_all);
        tv_select_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int i = 0; i < contactList.size(); i++) {
                    if (!contactList.get(i).invited) {
                        hitSendInvitationApi(contactList.get(i).getContactNumber());
                        contactList.get(i).invited = true;
                        // contactList.get(i).checked = all_selected;
                    }
                }
                // all_selected=!all_selected;
                phoneContactsListAdapter.notifyDataSetChanged();
            }
        });
        initList(baseLayout);
    }

    private void initList(View baseLayout) {

        //   contactList=new ArrayList<>();
        rv_people_list = (RecyclerView) baseLayout.findViewById(R.id.rv_people_list);
        phoneContactsListAdapter = new PhoneContactsListAdapter(this, contactList);
        linearLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        rv_people_list.setLayoutManager(linearLayoutManager);
        rv_people_list.setAdapter(phoneContactsListAdapter);
    }

    private void hitSendInvitationApi(final String phone) {
        String SMS = getString(R.string.luneblaze_phone_invite);
        SMS = SMS + " \n " + getString(R.string.app_playstore_url);
        Call<String> call = SMSapiService.sendSMS(AES.decrypt(AppKeys.SMS_UNAME, AppKeys.enKey),
                AES.decrypt(AppKeys.SMS_PASS, AppKeys.enKey), "1",
                AppKeys.SMS_COMPANY_NAME, phone, SMS);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(getActivity()!=null)commonFunctions.setToastMessage(getActivity().getApplicationContext(), "sent", Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // Log error here since request failed
                if(getActivity()!=null)commonFunctions.setToastMessage(getActivity().getApplicationContext(), "Cannot connect to Server", Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
            }
        });
    }

    @Override
    public void phoneContactsListClickCallback(int position) {
        if (!contactList.get(position).invited) {
            hitSendInvitationApi(contactList.get(position).getContactNumber());
            contactList.get(position).invited = true;
            phoneContactsListAdapter.notifyDataSetChanged();
        }
    }
}
