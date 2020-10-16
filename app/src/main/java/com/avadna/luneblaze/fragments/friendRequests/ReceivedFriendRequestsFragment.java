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
import com.avadna.luneblaze.adapters.ReceivedFriendRequestAdapter;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.pojo.PojoGetFriendRequestResponse;
import com.avadna.luneblaze.pojo.PojoGetFriendRequestResponseData;
import com.avadna.luneblaze.pojo.PojoUserConnectResponse;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ReceivedFriendRequestsFragment extends Fragment implements
        ReceivedFriendRequestAdapter.ReceivedRequestAdapterCallBack {


    RecyclerView rv_request_list;
    ReceivedFriendRequestAdapter receivedFriendRequestAdapter;
    List<PojoGetFriendRequestResponseData> receivedRequestList;

    PreferenceUtils preferenceUtils;
    CommonFunctions commonFunctions;
    ApiInterface apiService;

    String user_id;

    ProgressBar pb_loading_content;
    LinearLayout ll_no_connection_wrapper;
    TextView tv_retry;
    TextView tv_no_requests;

    public ReceivedFriendRequestsFragment() {
        // Required empty public constructor
    }

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
        View baseLayout = inflater.inflate(R.layout.fragment_received_friend_requests, container,
                false);
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
        preferenceUtils = new PreferenceUtils(getActivity());
        commonFunctions=new CommonFunctions(getActivity());
        user_id = preferenceUtils.get_user_id();
        initViews(baseLayout);
        initReceivedRequestList(baseLayout);
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
                hitGetReceivedFriendRequestApi(user_id);
            }
        });
    }

    private void initReceivedRequestList(View baseLayout) {
        receivedRequestList = new ArrayList<>();
        rv_request_list = (RecyclerView) baseLayout.findViewById(R.id.rv_request_list);
        receivedFriendRequestAdapter = new ReceivedFriendRequestAdapter(ReceivedFriendRequestsFragment.this,
                receivedRequestList);
        rv_request_list.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL,
                false));
        rv_request_list.setAdapter(receivedFriendRequestAdapter);
        hitGetReceivedFriendRequestApi(user_id);
    }

    private void hitGetReceivedFriendRequestApi(String user_id) {
        ll_no_connection_wrapper.setVisibility(View.GONE);
        pb_loading_content.setVisibility(View.VISIBLE);

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
                        preferenceUtils.saveReceivedRequestCount(response.body().data.size());
                        pb_loading_content.setVisibility(View.GONE);

                        if (receivedRequestList.isEmpty()) {
                            tv_no_requests.setVisibility(View.VISIBLE);
                        } else {
                            tv_no_requests.setVisibility(View.GONE);
                        }

                    } else {
                        if(getActivity()!=null)commonFunctions.setToastMessage(getActivity().getApplicationContext(), "" + message, Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                    }
                }
            }

            @Override
            public void onFailure(Call<PojoGetFriendRequestResponse> call, Throwable t) {
                // Log error here since request failed
                //   if(getActivity()!=null)commonFunctions.setToastMessage(getActivity().getApplicationContext(), t.toString(), Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);
                ll_no_connection_wrapper.setVisibility(View.VISIBLE);
                pb_loading_content.setVisibility(View.GONE);


            }
        });
    }

    @Override
    public void receivedRequestMethod(int position,String type) {
        if(type.equals("accept")){
            requestAccept(position);
        }

        if(type.equals("decline")){
            requestDecline(position);
        }
    }

    public void requestAccept(int position) {
        hitUpdateRequestStatusApi(user_id, receivedRequestList.get(position).userId, "friend-accept");
        receivedRequestList.remove(position);
        receivedFriendRequestAdapter.notifyDataSetChanged();
        // receivedFriendRequestAdapter.notifyItemRemoved(position);
        //  receivedFriendRequestAdapter.notifyItemRangeChanged(position,receivedRequestList.size());
        preferenceUtils.saveReceivedRequestCount(preferenceUtils.getReceivedRequestCount() - 1);

    }

    public void requestDecline(int position) {
        hitUpdateRequestStatusApi(user_id, receivedRequestList.get(position).userId, "friend-decline");
        receivedRequestList.remove(position);
        receivedFriendRequestAdapter.notifyDataSetChanged();
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
