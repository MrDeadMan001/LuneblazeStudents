package com.avadna.luneblaze.adapters;

import android.app.Activity;
import android.provider.Settings;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.pojo.PojoLoginResponseData;
import com.avadna.luneblaze.pojo.PojoLogoutResponse;
import com.avadna.luneblaze.pojo.pojoActiveSessions.PojoActiveSession;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActiveSessionsListAdapter  extends RecyclerView.Adapter<ActiveSessionsListAdapter.MyViewHolder> {
    List<PojoActiveSession> dataSet ;
    Activity activity;
    PreferenceUtils preferenceUtils;
    String user_id;
    ApiInterface apiService;
    CommonFunctions commonFunctions;
    String currentDeviceId;
    PojoLoginResponseData pojoLoginResponseData;



    public ActiveSessionsListAdapter(Activity activity,  List<PojoActiveSession> dataSet) {

        this.activity = activity;
        this.dataSet = dataSet;
        preferenceUtils=new PreferenceUtils(activity);
        user_id=preferenceUtils.get_user_id();
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
        commonFunctions=new CommonFunctions(activity);
        pojoLoginResponseData=preferenceUtils.getUserLoginData();
        currentDeviceId = Settings.Secure.getString(activity.getContentResolver(),
                Settings.Secure.ANDROID_ID);

        //  this.hierarchyList = hierarchyList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_session_id;
        TextView tv_time_stamp;
        TextView tv_browser;
        TextView tv_os;
        TextView tv_ip;
        TextView tv_logout;

        MyViewHolder(View view) {
            super(view);
            tv_session_id= (TextView) view.findViewById(R.id.tv_session_id);
            tv_time_stamp= (TextView) view.findViewById(R.id.tv_time_stamp);
            tv_browser= (TextView) view.findViewById(R.id.tv_browser);
            tv_os= (TextView) view.findViewById(R.id.tv_os);
            tv_ip= (TextView) view.findViewById(R.id.tv_ip);
            tv_logout= (TextView) view.findViewById(R.id.tv_logout);
        }
    }


    @Override
    public ActiveSessionsListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.active_session_list_item, parent, false);

        return new ActiveSessionsListAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ActiveSessionsListAdapter.MyViewHolder holder, final int position) {
        final PojoActiveSession currentItem=dataSet.get(position);

        holder.tv_session_id.setText(activity.getString(R.string.id)+" "+currentItem.sessionId);
        holder.tv_time_stamp.setText(activity.getString(R.string.time)+" "+currentItem.sessionDate);
        holder.tv_browser.setText(activity.getString(R.string.browser)+" "+currentItem.userBrowser);
        holder.tv_os.setText(activity.getString(R.string.os)+" "+currentItem.userOs);
        holder.tv_ip.setText(activity.getString(R.string.ip)+" "+currentItem.userIp);

        int sessionId=Integer.parseInt(currentItem.sessionId);
        if(currentItem.deviceId!=null&&!currentItem.deviceId.isEmpty()
        &&sessionId!=pojoLoginResponseData.sessionId){
            holder.tv_logout.setVisibility(View.VISIBLE);
            holder.tv_logout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    hitLogoutApi(user_id,currentItem.deviceId,currentItem.sessionId);
                    dataSet.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position,getItemCount());
                }
            });
        }
        else {
            holder.tv_logout.setVisibility(View.GONE);
        }
    }

    private void hitLogoutApi(String user_id,String deviceId,String sessionId) {
        Call<PojoLogoutResponse> call = apiService.logout(user_id, deviceId,sessionId);
        call.enqueue(new Callback<PojoLogoutResponse>() {
            @Override
            public void onResponse(Call<PojoLogoutResponse> call, Response<PojoLogoutResponse> response) {
                if (response != null && response.body() != null) {
                    String message = response.body().message;
                    if (response.body().data != null) {
                        commonFunctions.setToastMessage(activity, "" + message, Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                        /*confirmationDialog.dismiss();
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);

                        preferenceUtils.logOut();*/
                    }
                }
            }

            @Override
            public void onFailure(Call<PojoLogoutResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(activity, t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
            }
        });
    }



    @Override
    public int getItemCount() {
        return dataSet.size();
        //  return hierarchyList.size();
    }
}

