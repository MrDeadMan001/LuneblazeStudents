package com.avadna.luneblaze.adapters.assessment;

import android.app.Activity;
import android.content.Intent;
import android.provider.Settings;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.activities.assessment.AssessmentStatusActivity;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.pojo.PojoLoginResponseData;
import com.avadna.luneblaze.pojo.assessment.PojoAssessmentListResponseData;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;

import java.util.List;


public class AssessmentTypeListAdapter extends RecyclerView.Adapter<AssessmentTypeListAdapter.MyViewHolder> {
    List<PojoAssessmentListResponseData> dataSet;
    Activity activity;
    PreferenceUtils preferenceUtils;
    String user_id;
    ApiInterface apiService;
    CommonFunctions commonFunctions;
    String currentDeviceId;
    PojoLoginResponseData pojoLoginResponseData;


    public AssessmentTypeListAdapter(Activity activity, List<PojoAssessmentListResponseData> dataSet) {

        this.activity = activity;
        this.dataSet = dataSet;
        preferenceUtils = new PreferenceUtils(activity);
        user_id = preferenceUtils.get_user_id();
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
        commonFunctions = new CommonFunctions(activity);
        pojoLoginResponseData = preferenceUtils.getUserLoginData();
        currentDeviceId = Settings.Secure.getString(activity.getContentResolver(),
                Settings.Secure.ANDROID_ID);

        //  this.hierarchyList = hierarchyList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        LinearLayout ll_content_wrapper;
        TextView tv_report_status;
        TextView tv_transaction_id;
        TextView tv_amount;
        TextView tv_download;

        MyViewHolder(View view) {
            super(view);
            ll_content_wrapper = (LinearLayout) view.findViewById(R.id.ll_content_wrapper);
            tv_report_status = (TextView) view.findViewById(R.id.tv_report_status);
            tv_transaction_id = (TextView) view.findViewById(R.id.tv_transaction_id);
            tv_amount = (TextView) view.findViewById(R.id.tv_amount);
            tv_download = (TextView) view.findViewById(R.id.tv_download);
        }
    }


    @Override
    public AssessmentTypeListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.assessment_list_item, parent, false);

        return new AssessmentTypeListAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final AssessmentTypeListAdapter.MyViewHolder holder, final int position) {
        final PojoAssessmentListResponseData currentItem = dataSet.get(position);
        if (currentItem.status == 2) {
            holder.tv_report_status.setText(Html.fromHtml(activity.getString(R.string.report_status) + " <BR><B>" + activity.getString(R.string.pending) + "<B>"));
            holder.tv_download.setVisibility(View.GONE);
        } else if (currentItem.status == 3) {
            holder.tv_report_status.setText(Html.fromHtml(activity.getString(R.string.report_status) + " <BR><B>" + activity.getString(R.string.report_complete) + "<B>"));
            holder.tv_download.setVisibility(View.GONE);
        }
        holder.tv_transaction_id.setText(Html.fromHtml(activity.getString(R.string.transaction_id) + " <BR><B>" + currentItem.transactionId + "<B>"));
        holder.tv_amount.setText(Html.fromHtml(activity.getString(R.string.amount) + " <BR><B>₹" + currentItem.amount + "<B>"));
        holder.ll_content_wrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent assessmentIntent = new Intent(activity, AssessmentStatusActivity.class);
                assessmentIntent.putExtra(AppKeys.PAYMENT_ID,currentItem.paymentId);
                activity.startActivity(assessmentIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
        //  return hierarchyList.size();
    }
}

