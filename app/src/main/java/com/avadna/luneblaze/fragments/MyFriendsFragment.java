package com.avadna.luneblaze.fragments;


import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.avadna.luneblaze.R;

import com.avadna.luneblaze.activities.MainActivity;
import com.avadna.luneblaze.activities.chat.ChatActivity;
import com.avadna.luneblaze.activities.profileInfo.MyFriendsActivity;
import com.avadna.luneblaze.activities.profileInfo.ProfileInfoActivity;
import com.avadna.luneblaze.adapters.MyFriendsListAdapter;
import com.avadna.luneblaze.fragments.home.HomeFriendsFragment;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.pojo.PojoGetFriendsListResponse;
import com.avadna.luneblaze.pojo.PojoUserConnectResponse;
import com.avadna.luneblaze.pojo.pojoChat.PojoCreateNewConversationResponse;
import com.avadna.luneblaze.pojo.pojoQuestion.PojoUserData;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MyFriendsFragment extends Fragment implements MyFriendsListAdapter.FriendListAdapterCallback {

    LinearLayout ll_no_connection_wrapper;
    TextView tv_retry;

    RecyclerView rv_my_friends_list;
    MyFriendsListAdapter myFriendsListAdapter;
    CommonFunctions commonFunctions;
    PreferenceUtils preferenceUtils;
    String user_id;
    List<PojoUserData> friendList;
    LinearLayoutManager friendListLayoutManager;
    int offset = 0;
    boolean isGetFriendApiCalled = false;
    ApiInterface apiService;

    ProgressBar pb_loading_content;

    public MyFriendsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View baseLayout = inflater.inflate(R.layout.fragment_my_friends, container, false);
        commonFunctions = new CommonFunctions(getActivity());
        preferenceUtils = new PreferenceUtils(getActivity());
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
        user_id=preferenceUtils.get_user_id();
        initViews(baseLayout);
        initClickListener();
        setClickListener();
        hitGetFriendListApi(user_id,user_id, "", offset);
        return baseLayout;
    }


    private void initViews(View baseLayout) {
        ll_no_connection_wrapper = (LinearLayout) baseLayout.findViewById(R.id.ll_no_connection_wrapper);
        ll_no_connection_wrapper.setVisibility(View.GONE);
        tv_retry = (TextView) baseLayout.findViewById(R.id.tv_retry);
        tv_retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                offset=0;
                hitGetFriendListApi(user_id,user_id, "", offset);
            }
        });

        pb_loading_content = (ProgressBar) baseLayout.findViewById(R.id.pb_loading_content);

        setUpMyFriendsList(baseLayout);

    }

    private void setUpMyFriendsList(View baseLayout) {
        friendList = new ArrayList<>();
        rv_my_friends_list = (RecyclerView) baseLayout.findViewById(R.id.rv_my_friends_list);
        myFriendsListAdapter = new MyFriendsListAdapter(this, friendList);
        friendListLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        rv_my_friends_list.setLayoutManager(friendListLayoutManager);
        rv_my_friends_list.setAdapter(myFriendsListAdapter);
        rv_my_friends_list.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                //    Log.d("Scroll info", "dx = " + dy);
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 5) {
                    if (!isGetFriendApiCalled && friendListLayoutManager.findLastVisibleItemPosition() > friendList.size() - 3) {
                        hitGetFriendListApi(user_id,user_id, "", ++offset);
                    }
                    try{
                        ((MainActivity) getActivity()).hideBars();
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }

                }
                if(dy<-5){
                    try{
                        ((MainActivity) getActivity()).showBars();
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }


            }
        });

    }

    private void initClickListener() {

    }

    private void setClickListener() {

    }

    private void hitGetFriendListApi(String user_id,String target_user_id, String query, final int offset) {
        ll_no_connection_wrapper.setVisibility(View.GONE);
        Call<PojoGetFriendsListResponse> call = apiService.getFriendListApi(user_id,target_user_id, query,
                "0", "0", String.valueOf(offset));
        if(!isGetFriendApiCalled){
            isGetFriendApiCalled=true;
            call.enqueue(new Callback<PojoGetFriendsListResponse>() {
                @Override
                public void onResponse(Call<PojoGetFriendsListResponse> call, Response<PojoGetFriendsListResponse> response) {
                    if (response.body() != null) {
                        String message = response.body().message;
                        if (response.body().data != null) {
                            if (offset == 0) {
                                friendList.clear();
                            }
                            friendList.addAll(response.body().data.friends);
                            myFriendsListAdapter.notifyDataSetChanged();
                        } else {
                            if(getActivity()!=null)commonFunctions.setToastMessage(getActivity().getApplicationContext(), "" + message, Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);
                        }
                        try{
                            ((HomeFriendsFragment)MyFriendsFragment.this.getParentFragment())
                                    .setTabCount(0,friendList.size());
                            ((MyFriendsActivity)getActivity()).setTabCount(0,friendList.size());

                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                        pb_loading_content.setVisibility(View.GONE);
                        isGetFriendApiCalled=false;
                    }
                }

                @Override
                public void onFailure(Call<PojoGetFriendsListResponse> call, Throwable t) {
                    // Log error here since request failed
                    if(getActivity()!=null)commonFunctions.setToastMessage(getActivity().getApplicationContext(), t.toString(), Toast.LENGTH_LONG,
                            AppKeys.TOAST_DEBUG);
                    pb_loading_content.setVisibility(View.GONE);
                    isGetFriendApiCalled=false;
                    if(friendList.isEmpty()){
                        ll_no_connection_wrapper.setVisibility(View.VISIBLE);
                    }
                }
            });
        }
    }

    @Override
    public void onFriendListMethodCallback(int position, PojoUserData userData, String type) {
        switch (type) {
            case AppKeys.PROFILE:
                Intent profileIntent = new Intent(getActivity(), ProfileInfoActivity.class);
                profileIntent.putExtra(AppKeys.TARGET_USER_ID, userData.userId);
                startActivity(profileIntent);
                break;

            case AppKeys.MESSAGE:
                hitCreateIndividualConversationApi(user_id,userData.userId);
                break;

            case AppKeys.BLOCK:
                hitChangeUserConnectionApi(user_id,userData.userId,"block");
                friendList.remove(position);
                myFriendsListAdapter.notifyDataSetChanged();
                break;

            case AppKeys.UNFRIEND:
                hitChangeUserConnectionApi(user_id,userData.userId,"friend-remove");
                friendList.remove(position);
                myFriendsListAdapter.notifyDataSetChanged();
                break;
        }
    }

    private void hitCreateIndividualConversationApi(String user_id, String recipient) {

        Call<PojoCreateNewConversationResponse> call = apiService.createConversation(user_id, recipient);
        commonFunctions.openProgressDialog();
        call.enqueue(new Callback<PojoCreateNewConversationResponse>() {
            @Override
            public void onResponse(Call<PojoCreateNewConversationResponse> call,
                                   Response<PojoCreateNewConversationResponse> response) {
                commonFunctions.closeProgressDialog();
                String message = response.body().message;
                if (response.body().data != null) {
                    Intent chatIntent = new Intent(getActivity(), ChatActivity.class);
                    chatIntent.putExtra("cid", response.body().data);
                    startActivity(chatIntent);
                } else {
                    if(getActivity()!=null)commonFunctions.setToastMessage(getActivity().getApplicationContext(), "" + message, Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);
                }
            }

            @Override
            public void onFailure(Call<PojoCreateNewConversationResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.closeProgressDialog();
                if(getActivity()!=null)commonFunctions.setToastMessage(getActivity().getApplicationContext(), t.toString(), Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);
            }
        });
    }

    public void hitChangeUserConnectionApi(String user_id, String friend_id, String connection_type) {
        Call<PojoUserConnectResponse> call = apiService.changeUserConnectionApi(user_id, friend_id, connection_type);
        call.enqueue(new Callback<PojoUserConnectResponse>() {
            @Override
            public void onResponse(Call<PojoUserConnectResponse> call, Response<PojoUserConnectResponse> response) {

                if (response.body() != null) {
                    String message = response.body().message;
                    if (response.body().data != null) {
                        if (response.body().status.equals("1")) {

                        } else {
                            commonFunctions.setToastMessage(getContext(), "" + message, Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<PojoUserConnectResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(getContext(), t.toString(), Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);
            }
        });

    }
}
