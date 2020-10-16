package com.avadna.luneblaze.fragments.home;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.activities.FriendRequestsActivity;
import com.avadna.luneblaze.activities.MainActivity;
import com.avadna.luneblaze.activities.profileInfo.MyFriendsActivity;
import com.avadna.luneblaze.adapters.ReceivedFriendRequestAdapter;
import com.avadna.luneblaze.adapters.SuggestedFriendsAdapter;
import com.avadna.luneblaze.firebasenotifications.config.Config;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.pojo.PojoGetFriendRequestResponse;
import com.avadna.luneblaze.pojo.PojoGetFriendRequestResponseData;
import com.avadna.luneblaze.pojo.PojoGetSuggestedFriendListResponse;
import com.avadna.luneblaze.pojo.PojoUserConnectResponse;
import com.avadna.luneblaze.pojo.pojoQuestion.PojoUserData;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;
import com.avadna.luneblaze.utils.ObservableScrollView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FriendRequestsFragment extends Fragment implements
        ObservableScrollView.ScrollViewListener,
        ReceivedFriendRequestAdapter.ReceivedRequestAdapterCallBack {
    SwipeRefreshLayout srl_refresh;
    ProgressBar pb_loading_suggested;
    ProgressBar pb_loading_friend_requests;

    RecyclerView rv_request_list;
    ReceivedFriendRequestAdapter receivedFriendRequestAdapter;
    List<PojoGetFriendRequestResponseData> receivedRequestList;

    View.OnClickListener onClickListener;
    TextView tv_manage_requests;
    PreferenceUtils preferenceUtils;
    CommonFunctions commonFunctions;
    AppKeys appKeys;
    ApiInterface apiService;
    String user_id;

    RecyclerView rv_suggested_friends_list;
    SuggestedFriendsAdapter suggestedFriendsAdapter;
    List<PojoUserData> suggestedFriendList;
    ObservableScrollView sv_wrapper;
    TextView tv_no_requests;
    int apiCounter = 1;

    BroadcastReceiver broadcastReceiver;

    boolean fetchedReceivedList = false;
    boolean fetchedSentList = false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View baseLayout = inflater.inflate(R.layout.fragment_friend_requests, container, false);
        preferenceUtils = new PreferenceUtils(getActivity());
        commonFunctions = new CommonFunctions(getActivity());
        user_id = preferenceUtils.get_user_id();
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);

        initViews(baseLayout);
        initClickListener();
        setClickListeners();
        initBroadCastReceiver();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(broadcastReceiver,
                new IntentFilter(Config.NEW_FRIEND_REQUEST));

        if (preferenceUtils.getReceivedRequestCount() <= 0) {
            tv_no_requests.setVisibility(View.VISIBLE);
            tv_no_requests.setText(getString(R.string.no_pending_requests));
        }

        return baseLayout;
    }


    private void initBroadCastReceiver() {
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // checking for userType intent filter
                if (intent.getAction().equals(Config.NEW_FRIEND_REQUEST)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    hitGetReceivedFriendRequestApi(preferenceUtils.get_user_id());

                }
            }
        };
    }

    private void initClickListener() {
        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                  /*  case R.id.tv_sent:
                        tv_sent.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.app_theme_medium));
                        tv_sent.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                        tv_received.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.white));
                        tv_received.setTextColor(ContextCompat.getColor(getContext(), R.color.med_grey));
                        rv_request_list.setAdapter(sentFriendRequestAdapter);
                        break;

                    case R.id.tv_received:
                        tv_received.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.app_theme_medium));
                        tv_received.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                        tv_sent.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.white));
                        tv_sent.setTextColor(ContextCompat.getColor(getContext(), R.color.med_grey));
                        rv_request_list.setAdapter(receivedFriendRequestAdapter);
                        break;*/

                    case R.id.tv_manage_requests:
                        Intent friendsIntent = new Intent(getActivity(), FriendRequestsActivity.class);
                        startActivity(friendsIntent);
                        break;
                }
            }
        };
    }

    private void setClickListeners() {
        tv_manage_requests.setOnClickListener(onClickListener);
    }

    private void initViews(View baseLayout) {
        pb_loading_suggested = (ProgressBar) baseLayout.findViewById(R.id.pb_loading_suggested);
        pb_loading_friend_requests = (ProgressBar) baseLayout.findViewById(R.id.pb_loading_friend_requests);
        tv_no_requests = (TextView) baseLayout.findViewById(R.id.tv_no_requests);
        tv_no_requests.setVisibility(View.GONE);
        tv_manage_requests = (TextView) baseLayout.findViewById(R.id.tv_manage_requests);
        sv_wrapper = (ObservableScrollView) baseLayout.findViewById(R.id.sv_wrapper);
        sv_wrapper.setScrollViewListener(this);

     /*   sv_wrapper.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                int dy = sv_wrapper.getScrollY(); // For ScrollView
                if (dy > 0) {
                    ((MainActivity) getActivity()).hideBars();
                } else if (dy < 0) {
                    ((MainActivity) getActivity()).showBars();
                }
            }
        });*/

        tv_manage_requests = (TextView) baseLayout.findViewById(R.id.tv_manage_requests);
        initReceivedList(baseLayout);
        initSuggestedFriendList(baseLayout);

        srl_refresh = (SwipeRefreshLayout) baseLayout.findViewById(R.id.srl_refresh);
        srl_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                hitGetReceivedFriendRequestApi(preferenceUtils.get_user_id());
                hitSuggestFriendsListApi(preferenceUtils.get_user_id(), "0");
            }
        });
    }


    private void initReceivedList(View baseLayout) {
        receivedRequestList = new ArrayList<>();
        rv_request_list = (RecyclerView) baseLayout.findViewById(R.id.rv_request_list);
        rv_request_list.setNestedScrollingEnabled(false);
        receivedFriendRequestAdapter = new ReceivedFriendRequestAdapter(FriendRequestsFragment.this,
                receivedRequestList);
        rv_request_list.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL,
                false));
        rv_request_list.setAdapter(receivedFriendRequestAdapter);
        hitGetReceivedFriendRequestApi(preferenceUtils.get_user_id());
    }


    private void initSuggestedFriendList(View baseLayout) {
        suggestedFriendList = preferenceUtils.getSuggestedUserList();
        pb_loading_suggested.setVisibility(View.GONE);
        rv_suggested_friends_list = (RecyclerView) baseLayout.findViewById(R.id.rv_suggested_friends_list);
        rv_suggested_friends_list.setNestedScrollingEnabled(false);
        suggestedFriendsAdapter = new SuggestedFriendsAdapter(getActivity(), suggestedFriendList, "ver");
        rv_suggested_friends_list.setLayoutManager(new LinearLayoutManager(getActivity(),
                RecyclerView.VERTICAL, false));
        rv_suggested_friends_list.setAdapter(suggestedFriendsAdapter);
        hitSuggestFriendsListApi(user_id, "0");
    }

    private void hitGetReceivedFriendRequestApi(String user_id) {
        Call<PojoGetFriendRequestResponse> call = apiService.getFriendRequestList(user_id);
        call.enqueue(new Callback<PojoGetFriendRequestResponse>() {
            @Override
            public void onResponse(Call<PojoGetFriendRequestResponse> call, Response<PojoGetFriendRequestResponse> response) {
                if (response.body() != null) {
                    String message = response.body().message;
                    if (response.body().data != null) {
                        receivedRequestList.clear();
                        receivedRequestList.addAll(response.body().data);
                        receivedFriendRequestAdapter.notifyDataSetChanged();
                        apiCounter--;
                        setRequestCounter(0);
                        fetchedReceivedList = true;
                        preferenceUtils.saveReceivedRequestCount(response.body().data.size());

                        try {
                            if (receivedRequestList.isEmpty()) {
                                tv_no_requests.setVisibility(View.VISIBLE);
                                tv_no_requests.setText(getString(R.string.no_pending_requests));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    } else {
                        if (getActivity() != null)
                            commonFunctions.setToastMessage(getActivity().getApplicationContext(), "" + message, Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                    }
                }
                srl_refresh.setRefreshing(false);
                pb_loading_friend_requests.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<PojoGetFriendRequestResponse> call, Throwable t) {
                // Log error here since request failed
                //   if(getActivity()!=null)commonFunctions.setToastMessage(getActivity().getApplicationContext(), t.toString(), Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);
                srl_refresh.setRefreshing(false);
                pb_loading_friend_requests.setVisibility(View.GONE);

            }
        });
    }

    private void setRequestCounter(int tabPos) {
        try {
            if (tabPos == 0) {
                //  tl_tabs.getTabAt(0).setText(getString(R.string.received) + " (" + receivedRequestList.size() + ")");
            } else {
                //  tl_tabs.getTabAt(1).setText(getString(R.string.sent) + " (" + sentRequestList.size() + ")");
            }
            if (apiCounter == 0) {
                ((HomeFriendsFragment) FriendRequestsFragment.this.getParentFragment())
                        .setTabCount(1, receivedRequestList.size());
                ((MyFriendsActivity) getActivity()).setTabCount(1, receivedRequestList.size());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void hitSuggestFriendsListApi(String user_id, String page) {
        Call<PojoGetSuggestedFriendListResponse> call = apiService.getSuggestedFriendListApi(user_id, page);
        call.enqueue(new Callback<PojoGetSuggestedFriendListResponse>() {
            @Override
            public void onResponse(Call<PojoGetSuggestedFriendListResponse> call,
                                   Response<PojoGetSuggestedFriendListResponse> response) {
                if (response.body() != null) {
                    String message = response.body().message;
                    if (response.body().data != null) {
                        for (int i = 0; i < response.body().data.size(); i++) {
                            response.body().data.get(i).connection = "add";
                        }
                        preferenceUtils.saveSuggestedUserList(response.body().data);
                        suggestedFriendList.clear();

                        suggestedFriendList.addAll(preferenceUtils.getSuggestedUserList());
                        suggestedFriendsAdapter.notifyDataSetChanged();
                    } else {
                        if (getActivity() != null)
                            commonFunctions.setToastMessage(getActivity().getApplicationContext(), "" + message, Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                    }
                }

                pb_loading_suggested.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<PojoGetSuggestedFriendListResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(getContext(), t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                pb_loading_suggested.setVisibility(View.GONE);
            }
        });
    }


    @Override
    public void onScrollChanged(ObservableScrollView scrollView, int x, int y, int oldx, int oldy) {
        if (getActivity().getClass().getSimpleName().equals("MainActivity")) {
            int dy = y - oldy;
            if (dy > 4) {
                ((MainActivity) getActivity()).hideBars();
            } else if (dy < -4) {
                ((MainActivity) getActivity()).showBars();
            }
        }
    }

    public void requestAccept(int position) {
        hitUpdateRequestStatusApi(user_id, receivedRequestList.get(position).userId, "friend-accept");
        receivedRequestList.remove(position);
        receivedFriendRequestAdapter.notifyDataSetChanged();
        // receivedFriendRequestAdapter.notifyItemRemoved(position);
        //  receivedFriendRequestAdapter.notifyItemRangeChanged(position,receivedRequestList.size());
        preferenceUtils.saveReceivedRequestCount(preferenceUtils.getReceivedRequestCount() - 1);
        setRequestCounter(0);

    }

    public void requestDecline(int position) {
        hitUpdateRequestStatusApi(user_id, receivedRequestList.get(position).userId, "friend-decline");
        receivedRequestList.remove(position);
        receivedFriendRequestAdapter.notifyDataSetChanged();
        // receivedFriendRequestAdapter.notifyItemRemoved(position);
        // receivedFriendRequestAdapter.notifyItemRangeChanged(position,receivedRequestList.size());
        preferenceUtils.saveReceivedRequestCount(preferenceUtils.getReceivedRequestCount() - 1);
        setRequestCounter(0);
    }

    private void hitUpdateRequestStatusApi(String user_id, String id, String connect_type) {
        Call<PojoUserConnectResponse> call = apiService.changeUserConnectionApi(user_id, id, connect_type);
        call.enqueue(new Callback<PojoUserConnectResponse>() {
            @Override
            public void onResponse(Call<PojoUserConnectResponse> call, Response<PojoUserConnectResponse> response) {
                String message = "";
                if (response != null) {
                    message = response.body().message;
                    if (getActivity() != null)
                        commonFunctions.setToastMessage(getActivity().getApplicationContext(), message, Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                }
            }

            @Override
            public void onFailure(Call<PojoUserConnectResponse> call, Throwable t) {
                // Log error here since request failed
                if (getActivity() != null)
                    commonFunctions.setToastMessage(getActivity().getApplicationContext(), t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void receivedRequestMethod(int position, String type) {
        if (type.equals("accept")) {
            requestAccept(position);
        }

        if (type.equals("decline")) {
            requestDecline(position);
        }
    }
}
