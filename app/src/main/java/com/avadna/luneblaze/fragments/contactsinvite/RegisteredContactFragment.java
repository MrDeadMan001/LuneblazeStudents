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
import com.avadna.luneblaze.adapters.UpvotersAndAttendeesListAdapter;
import com.avadna.luneblaze.adapters.chat.RecipientSearchResultListAdapter;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.pojo.PojoNoDataResponse;
import com.avadna.luneblaze.pojo.PojoUserConnectResponse;
import com.avadna.luneblaze.pojo.pojoQuestion.PojoUserData;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class RegisteredContactFragment extends Fragment implements
        RecipientSearchResultListAdapter.RecipientListAdapterCallback,
        UpvotersAndAttendeesListAdapter.UpvoterAndAttendeesAdapterCallback {

    private PreferenceUtils preferenceUtils;
    CommonFunctions commonFunctions;
    private String user_id;
    private ApiInterface apiService;

    private List<PojoUserData> phoneUserList;
    private UpvotersAndAttendeesListAdapter userSearchResultListAdapter;

    TextView tv_select_all;
    boolean all_selected = false;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferenceUtils = new PreferenceUtils(getActivity());
        commonFunctions = new CommonFunctions(getActivity());
        user_id = preferenceUtils.get_user_id();
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
        if (getArguments() != null) {
            Type type = new TypeToken<ArrayList<PojoUserData>>() {
            }.getType();
            String str = getArguments().getString("data");
            if (str.isEmpty()) {
                phoneUserList = new ArrayList<>();
            } else {
                phoneUserList = new Gson().fromJson(str, type);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View baseLayout = inflater.inflate(R.layout.fragment_registered_contacts, container, false);
        initViews(baseLayout);
        return baseLayout;
    }

    private void initViews(View baseLayout) {
        RecyclerView rv_list;
        rv_list = (RecyclerView) baseLayout.findViewById(R.id.rv_list);
        userSearchResultListAdapter = new UpvotersAndAttendeesListAdapter(RegisteredContactFragment.this,
                phoneUserList);
        rv_list.setLayoutManager(new LinearLayoutManager(getActivity(),
                RecyclerView.VERTICAL, false));
        rv_list.setAdapter(userSearchResultListAdapter);


        tv_select_all = (TextView) baseLayout.findViewById(R.id.tv_select_all);
        tv_select_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hitAddAllUserApi(user_id, phoneUserList);
            }
        });
    }

    private void hitAddAllUserApi(String user_id, final List<PojoUserData> phoneUserList) {
        HashMap<String, String> userMap = new HashMap<>();
        for (int i = 0; i < phoneUserList.size(); i++) {
            userMap.put("id[" + i + "]", phoneUserList.get(i).userId);
        }
        Call<PojoNoDataResponse> call = apiService.addAllUsers(user_id,
                "friend-add", userMap);
        call.enqueue(new Callback<PojoNoDataResponse>() {
            @Override
            public void onResponse(Call<PojoNoDataResponse> call, Response<PojoNoDataResponse> response) {
                String message = "";
                if (response != null && response.body() != null) {
                    phoneUserList.clear();
                    userSearchResultListAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<PojoNoDataResponse> call, Throwable t) {
                // Log error here since request failed
                if (getActivity() != null)
                    commonFunctions.setToastMessage(getActivity().getApplicationContext(), t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                // rl_parent.setVisibility(View.VISIBLE);
            }
        });
    }


    @Override
    public void onRecipientListMethodCallback(int position, PojoUserData item) {

        phoneUserList.get(position).selected = !phoneUserList.get(position).selected;
        userSearchResultListAdapter.notifyDataSetChanged();
    }


    @Override
    public void onUpvoterAndAttendeesAdapterMethod(int position) {
        if (phoneUserList.get(position).connection.equals("add")
                || phoneUserList.get(position).connection.equals("remove")) {
            hitUpdateRequestStatusApi(user_id, phoneUserList.get(position).userId, "follow", position);
            phoneUserList.get(position).connection = "followed";

        } else if (phoneUserList.get(position).connection.equals("followed")) {
            hitUpdateRequestStatusApi(user_id, phoneUserList.get(position).userId, "unfollow", position);
            phoneUserList.get(position).connection = "add";
        }
        userSearchResultListAdapter.notifyDataSetChanged();
    }

    private void hitUpdateRequestStatusApi(String user_id, String id, final String connect_type, final int position) {
        Call<PojoUserConnectResponse> call = apiService.changeUserConnectionApi(user_id, id, connect_type);

        call.enqueue(new Callback<PojoUserConnectResponse>() {
            @Override
            public void onResponse(Call<PojoUserConnectResponse> call, Response<PojoUserConnectResponse> response) {
                String message = "";
                if (response != null && response.body() != null) {
                    message = response.body().message;
                    if (getActivity() != null)
                        commonFunctions.setToastMessage(getActivity().getApplicationContext(), message,
                                Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                    // venueList.remove(position);
                }
            }

            @Override
            public void onFailure(Call<PojoUserConnectResponse> call, Throwable t) {
                // Log error here since request failed
                if (getActivity() != null)
                    commonFunctions.setToastMessage(getActivity().getApplicationContext(), t.toString(),
                            Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
            }
        });
    }
}
