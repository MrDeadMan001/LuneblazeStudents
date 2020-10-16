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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.activities.LoginActivity;
import com.avadna.luneblaze.activities.MainActivity;
import com.avadna.luneblaze.activities.registration.RegisterActivity1;
import com.avadna.luneblaze.activities.sessions.SessionLoaderActivity;
import com.avadna.luneblaze.adapters.NotificationListAdapter;
import com.avadna.luneblaze.firebasenotifications.config.Config;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.NotificationLoader;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.pojo.PojoGetNotificationListResponse;
import com.avadna.luneblaze.pojo.PojoGetNotificationListResponseData;
import com.avadna.luneblaze.pojo.PojoNoDataResponse;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class NotificationsListFragment extends Fragment implements NotificationListAdapter.NotificationListAdapterCallback {

    private SwipeRefreshLayout srl_refresh;


    private LinearLayout ll_no_connection_wrapper;
    private TextView tv_retry;

    private RecyclerView rv_notification_list;
    private LinearLayoutManager linearLayoutManager;
    private NotificationListAdapter notificationListAdapter;
    private CommonFunctions commonFunctions;
    private PreferenceUtils preferenceUtils;
    private String user_id;
    private List<PojoGetNotificationListResponseData> notifList;
    private ApiInterface apiService;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private int latestNotificationId = 0;
    private boolean isApiCalled = false;
    private int offset = 0;
    private ProgressBar pb_loading_content;

    private NotificationLoader notificationLoader;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View baseLayout = inflater.inflate(R.layout.fragment_notifications_list, container, false);
        commonFunctions = new CommonFunctions(getActivity());
        preferenceUtils = new PreferenceUtils(getActivity());
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);

        user_id = preferenceUtils.get_user_id();
        if (user_id.equals(AppKeys.GUEST_USER_ID)) {
            showGuestLogin(baseLayout);
        } else {
            notificationLoader = new NotificationLoader(getActivity());
            initViews(baseLayout);
            initBroadCastReceiver();
            registerReceivers();
        }
        return baseLayout;
    }

    private void showGuestLogin(View baseLayout) {
        RelativeLayout rl_guest_login_wrapper = (RelativeLayout) baseLayout.findViewById(R.id.rl_guest_login_wrapper);
        rl_guest_login_wrapper.setVisibility(View.VISIBLE);
        TextView tv_login_button = (TextView) baseLayout.findViewById(R.id.tv_login_button);
        tv_login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginActivityIntent = new Intent(getActivity(), LoginActivity.class);
                startActivity(loginActivityIntent);
            }
        });
    }

    private void registerReceivers() {
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.NEW_NOTIFICATION));
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.NOTIFICATION_SEEN));
    }


    public void markAllNotificationsSeen() {
        hitSeeAllNotifications(user_id);
        for (int i = 0; i < notifList.size(); i++) {
            notifList.get(i).seen = "1";
        }
        notificationListAdapter.notifyDataSetChanged();

    }

    private void initBroadCastReceiver() {
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Config.NEW_NOTIFICATION)) {
                    // new push notification is received
                 /*   String message = intent.getStringExtra("message");
                    commonFunctions.setToastMessage(getApplicationContext(), "Push notification: " + message, Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);*/
                    offset = 0;
                    hitGetNotificationsApi(preferenceUtils.get_user_id(), offset);
                } else if (intent.getAction().equals(Config.NOTIFICATION_SEEN)) {
                    String id = intent.getStringExtra(AppKeys.NOTIFICATION_ID);
                    for (int i = 0; i < notifList.size(); i++) {
                        if (notifList.get(i).notificationId.equals(id)) {
                            notifList.get(i).seen = "1";
                            notificationListAdapter.notifyDataSetChanged();
                            break;
                        }
                    }
                }
            }
        };

    }

    private void initViews(View baseLayout) {

        ll_no_connection_wrapper = (LinearLayout) baseLayout.findViewById(R.id.ll_no_connection_wrapper);
        ll_no_connection_wrapper.setVisibility(View.GONE);
        tv_retry = (TextView) baseLayout.findViewById(R.id.tv_retry);
        tv_retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                offset = 0;
                hitGetNotificationsApi(preferenceUtils.get_user_id(), offset);
            }
        });

        pb_loading_content = (ProgressBar) baseLayout.findViewById(R.id.pb_loading_content);
        pb_loading_content.setVisibility(View.GONE);
        initNotificationList(baseLayout);
    }

    private void initNotificationList(View baseLayout) {
        // notifList = preferenceUtils.getNotificationList();

        notifList = filterList(preferenceUtils.getNotificationList());
        srl_refresh = (SwipeRefreshLayout) baseLayout.findViewById(R.id.srl_refresh);

        srl_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                offset = 0;
                hitGetNotificationsApi(preferenceUtils.get_user_id(), offset);
            }
        });

        rv_notification_list = (RecyclerView) baseLayout.findViewById(R.id.rv_notification_list);
        notificationListAdapter = new NotificationListAdapter(NotificationsListFragment.this, notifList);
        linearLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        rv_notification_list.setLayoutManager(linearLayoutManager);
        rv_notification_list.setAdapter(notificationListAdapter);
        rv_notification_list.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                //    Log.d("Scroll info", "dx = " + dy);
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 5) {
                    ((MainActivity) getActivity()).hideBars();
                    if (!isApiCalled && linearLayoutManager.findLastVisibleItemPosition() > notifList.size() - 3) {
                        hitGetNotificationsApi(preferenceUtils.get_user_id(), ++offset);
                    }
                } else if (dy < -5) {
                    ((MainActivity) getActivity()).showBars();
                }


            }
        });
        if (!notifList.isEmpty()) {
            pb_loading_content.setVisibility(View.GONE);
        }
        offset = 0;
        hitGetNotificationsApi(preferenceUtils.get_user_id(), offset);
    }

    //todo remove this method after the full message field issue is resolved
    public List<PojoGetNotificationListResponseData> filterList(List<PojoGetNotificationListResponseData> dirtyList) {
        List<PojoGetNotificationListResponseData> tempList = new ArrayList<>();
        for (int i = 0; i < dirtyList.size(); i++) {
            boolean valid = true;
            PojoGetNotificationListResponseData currentItem = dirtyList.get(i);
            if (currentItem.action.equals("venue_changed_confirmation")
                    && currentItem.seen.equals("1")) {
                valid = false;
            }
            if (dirtyList.get(i).fullMessage == null) {
                valid = false;
            }
            if (dirtyList.get(i).action.equals("campusschedule")) {
                valid = false;
            }

            if (valid) {
                tempList.add(dirtyList.get(i));
            }
        }
        return tempList;

    }

    private void hitGetNotificationsApi(String user_id, final int offset) {
        Call<PojoGetNotificationListResponse> call = apiService.getNotificationList(user_id, String.valueOf(offset), "0");

        ll_no_connection_wrapper.setVisibility(View.GONE);
        if (offset == 0) {
            notificationListAdapter.setShowNoMoreResults(false);
            //   notifList.clear();
            notificationListAdapter.notifyDataSetChanged();
        }

        if (!isApiCalled) {
            isApiCalled = true;
            notificationListAdapter.setShowProgressBar(true);
            call.enqueue(new Callback<PojoGetNotificationListResponse>() {
                @Override
                public void onResponse(Call<PojoGetNotificationListResponse> call, Response<PojoGetNotificationListResponse> response) {
                    srl_refresh.setRefreshing(false);
                    pb_loading_content.setVisibility(View.GONE);
                    if (response.body() != null) {
                        String message = response.body().message;
                        if (response.body().data != null) {
                            response.body().data = filterList(response.body().data);
                            if (offset == 0) {
                                notifList.clear();
                            }

                            if (response.body().data.isEmpty()) {
                                notificationListAdapter.setShowNoMoreResults(true);
                            }
                            notifList.addAll(response.body().data);
                            notificationListAdapter.notifyDataSetChanged();
                            preferenceUtils.saveNotificationList(notifList);
                        } else {
                            if (getActivity() != null)
                                commonFunctions.setToastMessage(getActivity().getApplicationContext(), "" + message, Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                        }
                    }
                    notificationListAdapter.setShowProgressBar(false);

                    isApiCalled = false;
                }

                @Override
                public void onFailure(Call<PojoGetNotificationListResponse> call, Throwable t) {
                    srl_refresh.setRefreshing(false);
                    // Log error here since request failed
                    // if(getActivity()!=null)commonFunctions.setToastMessage(getActivity().getApplicationContext(), t.toString(), Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);
                    isApiCalled = false;
                    if (notifList.isEmpty()) {
                        ll_no_connection_wrapper.setVisibility(View.VISIBLE);
                        notificationListAdapter.setShowProgressBar(false);
                    }
                    notificationListAdapter.setShowProgressBar(false);

                }
            });
        }
    }

  /*  private void hitGetNewNotificationsApi(String user_id, int lastPostId, int notification_id) {
        Call<PojoGetNotificationListResponse> call = apiService.getNotificationList(user_id, String.valueOf(lastPostId), String.valueOf(notification_id));
        call.enqueue(new Callback<PojoGetNotificationListResponse>() {
            @Override
            public void onResponse(Call<PojoGetNotificationListResponse> call, Response<PojoGetNotificationListResponse> response) {
                srl_refresh.setRefreshing(false);
                if (response.body() != null) {
                    String message = response.body().message;
                    if (response.body().data != null) {
                        //append all new notifications to front of the list
                        for(int i=response.body().data.size()-1;i>=0;i--){
                            notifList.add(0,response.body().data.get(i));
                        }
                        latestNotificationId=Integer.parseInt(notifList.get(0).notificationId);
                        preferenceUtils.saveNotificationList(notifList);
                        notificationListAdapter.notifyDataSetChanged();
                    } else {
                        if(getActivity()!=null)commonFunctions.setToastMessage(getActivity().getApplicationContext(), "" + message, Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);
                    }
                }
            }

            @Override
            public void onFailure(Call<PojoGetNotificationListResponse> call, Throwable t) {
                srl_refresh.setRefreshing(false);
                // Log error here since request failed
                if(getActivity()!=null)commonFunctions.setToastMessage(getActivity().getApplicationContext(), t.toString(), Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);
            }
        });
    }*/

    @Override
    public void notificationListMethodCallback(int position, String actionType) {
        switch (actionType) {
            case AppKeys.NOTIFICATION_SEEN:
                notifList.get(position).seen = "1";
                notificationListAdapter.notifyDataSetChanged();
                notificationLoader.openTarget(notifList.get(position), 0);
                break;

            case AppKeys.FINALISE_YES:
                Intent yesIntent = new Intent(getActivity(), SessionLoaderActivity.class);
                yesIntent.putExtra("id", notifList.get(position).nodeUrl);
                // yesIntent.putExtra("type","poll");
                yesIntent.putExtra("action", AppKeys.FINALISE_YES);
                hitNotificationSeenApi(notifList.get(position).notificationId);
                notifList.remove(position);
                notificationListAdapter.notifyDataSetChanged();
                startActivity(yesIntent);
                break;

            case AppKeys.FINALISE_NO:
                Intent noIntent = new Intent(getActivity(), SessionLoaderActivity.class);
                noIntent.putExtra("id", notifList.get(position).nodeUrl);
                //  noIntent.putExtra("type","poll");
                noIntent.putExtra("action", AppKeys.FINALISE_NO);
                hitNotificationSeenApi(notifList.get(position).notificationId);
                notifList.remove(position);
                notificationListAdapter.notifyDataSetChanged();
                startActivity(noIntent);
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mRegistrationBroadcastReceiver);
    }


    private void hitNotificationSeenApi(String notificationId) {
        Call<PojoNoDataResponse> call = apiService.seenNotification(user_id, notificationId);
        call.enqueue(new Callback<PojoNoDataResponse>() {
            @Override
            public void onResponse(Call<PojoNoDataResponse> call, Response<PojoNoDataResponse> response) {
                String message = "";
                if (response != null) {

                }
            }

            @Override
            public void onFailure(Call<PojoNoDataResponse> call, Throwable t) {
                // Log error here since request failed
                //  if(getActivity()!=null)commonFunctions.setToastMessage(getActivity().getApplicationContext(), t.toString(), Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);
            }
        });
    }

    private void hitSeeAllNotifications(String user_id) {
        Call<PojoNoDataResponse> call = apiService.seeAllNotification(user_id);
        call.enqueue(new Callback<PojoNoDataResponse>() {
            @Override
            public void onResponse(Call<PojoNoDataResponse> call, Response<PojoNoDataResponse> response) {
                String message = "";
                if (response != null) {

                }
            }

            @Override
            public void onFailure(Call<PojoNoDataResponse> call, Throwable t) {
                // Log error here since request failed
                //  if(getActivity()!=null)commonFunctions.setToastMessage(getActivity().getApplicationContext(), t.toString(), Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);
            }
        });
    }


}
