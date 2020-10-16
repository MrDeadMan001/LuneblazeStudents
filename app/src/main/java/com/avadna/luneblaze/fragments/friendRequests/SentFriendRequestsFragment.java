package com.avadna.luneblaze.fragments.friendRequests;

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
import com.avadna.luneblaze.adapters.SentFriendRequestAdapter;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.pojo.PojoSentRequestListResponse;
import com.avadna.luneblaze.pojo.PojoSentRequestListResponseData;
import com.avadna.luneblaze.pojo.PojoUserConnectResponse;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SentFriendRequestsFragment extends Fragment implements
        SentFriendRequestAdapter.SentRequestAdapterCallBack {

    RecyclerView rv_request_list;
    SentFriendRequestAdapter sentFriendRequestAdapter;
    List<PojoSentRequestListResponseData> sentRequestList;

    PreferenceUtils preferenceUtils;
    CommonFunctions commonFunctions;
    ApiInterface apiService;
    String user_id;

    ProgressBar pb_loading_content;
    LinearLayout ll_no_connection_wrapper;
    TextView tv_retry;
    TextView tv_no_requests;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View baseLayout = inflater.inflate(R.layout.fragment_sent_friend_requests, container, false);
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
        preferenceUtils = new PreferenceUtils(getActivity());
        commonFunctions=new CommonFunctions(getActivity());
        user_id = preferenceUtils.get_user_id();
        initViews(baseLayout);
        initSentRequestList(baseLayout);
        return baseLayout;
    }

    private void initViews(View baseLayout) {
        pb_loading_content = (ProgressBar) baseLayout.findViewById(R.id.pb_loading_content);
        ll_no_connection_wrapper = (LinearLayout) baseLayout.findViewById(R.id.ll_no_connection_wrapper);

        tv_no_requests = (TextView) baseLayout.findViewById(R.id.tv_no_requests);
        tv_no_requests.setVisibility(View.GONE);
        tv_retry = (TextView) baseLayout.findViewById(R.id.tv_retry);
        tv_retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hitGetSentFriendRequestApi(preferenceUtils.get_user_id());
            }
        });
    }


    private void initSentRequestList(View baseLayout) {
        rv_request_list = (RecyclerView) baseLayout.findViewById(R.id.rv_request_list);
        sentRequestList = new ArrayList<>();
        sentFriendRequestAdapter = new SentFriendRequestAdapter(SentFriendRequestsFragment.this,
                sentRequestList);
        //  rv_request_list.setAdapter(sentFriendRequestAdapter);
        rv_request_list.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL,
                false));
        rv_request_list.setAdapter(sentFriendRequestAdapter);
        hitGetSentFriendRequestApi(preferenceUtils.get_user_id());
    }

    private void hitGetSentFriendRequestApi(String user_id) {
        ll_no_connection_wrapper.setVisibility(View.GONE);
        pb_loading_content.setVisibility(View.VISIBLE);

        Call<PojoSentRequestListResponse> call = apiService.getSentFriendRequestList(user_id);
        call.enqueue(new Callback<PojoSentRequestListResponse>() {
            @Override
            public void onResponse(Call<PojoSentRequestListResponse> call, Response<PojoSentRequestListResponse> response) {
                if (response.body() != null) {
                    String message = response.body().message;
                    if (response.body().data != null) {
                        sentRequestList.clear();
                        sentRequestList.addAll(response.body().data);
                        sentFriendRequestAdapter.notifyDataSetChanged();
                        preferenceUtils.saveSentRequestCount(response.body().data.size());

                        if (sentRequestList.isEmpty()) {
                            tv_no_requests.setVisibility(View.VISIBLE);
                        } else {
                            tv_no_requests.setVisibility(View.GONE);
                        }
                        pb_loading_content.setVisibility(View.GONE);

                    } else {
                        if(getActivity()!=null)commonFunctions.setToastMessage(getActivity().getApplicationContext(), "" + message, Toast.LENGTH_LONG,
                                AppKeys.TOAST_DEBUG);
                    }
                }
                pb_loading_content.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<PojoSentRequestListResponse> call, Throwable t) {
                // Log error here since request failed
//                if(getActivity()!=null)commonFunctions.setToastMessage(getActivity().getApplicationContext(), t.toString(), Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);

                ll_no_connection_wrapper.setVisibility(View.VISIBLE);
                pb_loading_content.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void sentRequestMethod(int position) {
        requestDecline(position);
    }

    public void requestDecline(int position) {
        hitUpdateRequestStatusApi(user_id, sentRequestList.get(position).userId, "friend-cancel");
        sentRequestList.remove(position);
        sentFriendRequestAdapter.notifyDataSetChanged();
        // receivedFriendRequestAdapter.notifyItemRemoved(position);
        // receivedFriendRequestAdapter.notifyItemRangeChanged(position,receivedRequestList.size());
        preferenceUtils.saveReceivedRequestCount(preferenceUtils.getReceivedRequestCount() - 1);
    }

    private void hitUpdateRequestStatusApi(String user_id, String id, String connect_type) {
        Call<PojoUserConnectResponse> call = apiService.changeUserConnectionApi(user_id, id, connect_type);
        call.enqueue(new Callback<PojoUserConnectResponse>() {
            @Override
            public void onResponse(Call<PojoUserConnectResponse> call, Response<PojoUserConnectResponse> response) {
                String message = "";
                if (response != null) {
                    message = response.body().message;
                    if(getActivity()!=null)commonFunctions.setToastMessage(getActivity().getApplicationContext(), message, Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                }
            }

            @Override
            public void onFailure(Call<PojoUserConnectResponse> call, Throwable t) {
                // Log error here since request failed
                if(getActivity()!=null)commonFunctions.setToastMessage(getActivity().getApplicationContext(), t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
            }
        });
    }


}
